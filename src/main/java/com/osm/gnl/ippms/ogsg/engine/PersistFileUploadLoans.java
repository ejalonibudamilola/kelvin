/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseStatusClass;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.garnishment.GarnishmentValidatorHelper;
import lombok.SneakyThrows;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class PersistFileUploadLoans extends BaseStatusClass implements Runnable {


    private final GenericService genericService;
    private final FileParseBean fileParseBean;
    private final BusinessCertificate businessCertificate;
    private final PayrollFlag payrollFlag;
    private final LocalDate pendingPaycheckDate;
    private GarnishmentValidatorHelper garnishmentValidatorHelper;
    private final ConfigurationBean configurationBean;


    public PersistFileUploadLoans(GenericService genericService, FileParseBean fileParseBean, BusinessCertificate businessCertificate,
                                  PayrollFlag payrollFlag, LocalDate pendingPaycheckDate, ConfigurationBean configurationBean) {
        this.genericService = genericService;
        this.fileParseBean = fileParseBean;
        this.businessCertificate = businessCertificate;
        this.payrollFlag = payrollFlag;
        this.pendingPaycheckDate = pendingPaycheckDate;
        this.configurationBean = configurationBean;
        this.garnishmentValidatorHelper = new GarnishmentValidatorHelper();
    }

    @SneakyThrows
    @Override
    public void run() {
        persistLoans();
    }

    private void persistLoans() throws IllegalAccessException, InstantiationException {
        startTime = System.currentTimeMillis();
        Vector<Object> wActualSaveList = new Vector<>();
        fileParseBean.setErrorList(new Vector<>());
        this.listSize = fileParseBean.getListToSave().size();
        this.fileParseBean.setTotalNumberOfRecords(this.listSize);
        if(this.listSize > 50)
            this.batchSize = 20;
        else
            this.batchSize = 5;


        Iterator<NamedEntity> it = fileParseBean.getListToSave().iterator();
        CustomPredicate bizIdPredicate = CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId());
        AbstractGarnishmentEntity wEmpGarnInfo;
        while (it.hasNext()) {
            NamedEntity p = it.next();

            StringBuilder wErrorMsgBuilder = new StringBuilder();

            boolean errorRecord = false;

            EmpGarnishmentType wEGT = (EmpGarnishmentType) fileParseBean.getObjectMap().get(p.getObjectCode().trim().toUpperCase());

            //get a garnishment object by the employee and garnishment type ids'
            List<AbstractGarnishmentEntity> wEmpGarnInfoList = ( List<AbstractGarnishmentEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getGarnishmentInfoClass(businessCertificate),
                    Arrays.asList(bizIdPredicate, CustomPredicate.procurePredicate("empGarnishmentType.id", wEGT.getId()),
                            CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), p.getId())), null);
            wEmpGarnInfo = IppmsUtils.makeGarnishmentInfoObject(businessCertificate);

            boolean foundSubstitute = false;
            if (!IppmsUtils.isNullOrEmpty(wEmpGarnInfoList)) {
                if (wEmpGarnInfoList.size() > 1) {
                    for (AbstractGarnishmentEntity empGarnInfo : wEmpGarnInfoList) {
                        if (empGarnInfo.getOwedAmount() > 0) {
                            wEmpGarnInfo = empGarnInfo;
                            foundSubstitute = true;
                            break;
                        }
                    }
                    if (!foundSubstitute) {
                        wEmpGarnInfo = wEmpGarnInfoList.get(0);
                    }
                } else {
                    wEmpGarnInfo = wEmpGarnInfoList.get(0);
                }
            }

            //if the employee doesn't already have a garnishment/loan of the type
            //then the upload can be performed, else an is an invalid record.
            if (wEmpGarnInfo.isNewEntity()) {
                wEmpGarnInfo.setEmpGarnishmentType(wEGT);
                wEmpGarnInfo.setDescription(wEGT.getDescription());
                if (businessCertificate.isPensioner())
                    wEmpGarnInfo.setPensioner(new Pensioner(p.getId()));
                else
                    wEmpGarnInfo.setEmployee(new Employee(p.getId()));
                wEmpGarnInfo.setCreatedBy(new User(businessCertificate.getLoginId()));
                wEmpGarnInfo.setBusinessClientId(businessCertificate.getBusinessClientInstId());
            }
            wEmpGarnInfo.setOwedAmount(p.getLoanBalance());
            wEmpGarnInfo.setInterestAmount(0.0D);
            wEmpGarnInfo.setDeductInterestSeparatelyInd(0);
            wEmpGarnInfo.setGovtLoan(0);
            wEmpGarnInfo.setLastModBy(new User(businessCertificate.getLoginId()));
            wEmpGarnInfo.setLastModTs(Timestamp.from(Instant.now()));
            wEmpGarnInfo.setGarnishCap(0.0D);


            if ((p.getLoanBalance() == 0.0D) || (p.getTenor() == 0.0D)) {
                wEmpGarnInfo.setAmount(0.0D);
                wEmpGarnInfo.setLoanTerm(0);
                wEmpGarnInfo.setEndDate(LocalDate.now());
                if (wEmpGarnInfo.isNewEntity())
                    continue;
            } else {
                wEmpGarnInfo.setAmount(EntityUtils.convertDoubleToEpmStandard(p.getLoanBalance() / p.getTenor()));
                wEmpGarnInfo.setTenor(p.getTenor());
                wEmpGarnInfo.setCurrentLoanTerm(Double.valueOf(p.getTenor()).intValue());
                wEmpGarnInfo.setOriginalLoanAmount(p.getLoanBalance());
                wEmpGarnInfo.setLoanTerm(Double.valueOf(p.getTenor()).intValue());
                wEmpGarnInfo.setStartDate(LocalDate.now());
                wEmpGarnInfo.setEndDate(PayrollUtils.makeEndDate(LocalDate.now(),wEmpGarnInfo.getLoanTerm()));
            }

            //check if the employees pay will be negative after applying this loan
           List<String> wErrMsg = garnishmentValidatorHelper.validateGarnishmentFileUpload(genericService,wEmpGarnInfo, businessCertificate,configurationBean);

            //if it has an error
            if (IppmsUtils.isNotNullOrEmpty(wErrMsg )) {
                for(String str : wErrMsg)
                    wErrorMsgBuilder.append(str);
                errorRecord = true;

            }


            //if it's not a record with errors then add to list
            if (!errorRecord) {
                wActualSaveList.add(wEmpGarnInfo);
            }
            //create the error object for this record.
            else {
                NamedEntity wNE = new NamedEntity();
                wNE.setDisplayErrors(wErrorMsgBuilder.toString());
                wNE.setObjectCode(p.getObjectCode());
                wNE.setLoanBalance(p.getLoanBalance());
                wNE.setStaffId(p.getStaffId());
                wNE.setTenor(p.getTenor());
                fileParseBean.getErrorList().add(wNE);//add to the list of errors
                this.currentPercentage += 1;
                continue;
                //remove error records from the list to be saved
                // it.remove();
                //if there are any errors then the upload is rejected in totality.
                //wActualSaveList = null;
            }

            //
            if (wActualSaveList.size() == 50) {
                this.genericService.storeObjectBatch(wActualSaveList);
                this.fileParseBean.setSuccessfulRecords(this.fileParseBean.getSuccessfulRecords() + wActualSaveList.size());
                wActualSaveList = new Vector<>();
            }
            this.currentPercentage += 1;
        }
        if ((wActualSaveList != null) && !(wActualSaveList.isEmpty())) {
            this.genericService.storeObjectBatch(wActualSaveList);
            this.fileParseBean.setSuccessfulRecords(this.fileParseBean.getSuccessfulRecords() + wActualSaveList.size());

        }

        //Set Pending Payroll Run to be rerun or deleted
        if (pendingPaycheckDate != null) {
            //This means we have a Pending Paycheck. Set for strictly RERUN
            RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService, businessCertificate.getBusinessClientInstId(), pendingPaycheckDate);
            wRPB.setNoOfLoans(wRPB.getNoOfLoans() + fileParseBean.getListToSave().size());
            if (wRPB.isNewEntity()) {
                wRPB.setRunMonth(pendingPaycheckDate.getMonthValue());
                wRPB.setRunYear(pendingPaycheckDate.getYear());
                wRPB.setBusinessClientId(businessCertificate.getBusinessClientInstId());

            }
            wRPB.setRerunInd(IConstants.ON);
            this.genericService.storeObject(wRPB);
        }
        this.currentPercentage = this.listSize;
    }



    public FileParseBean getFileParseBean() {
        return fileParseBean;
    }




}
