/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseStatusClass;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.SneakyThrows;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class PersistFileUploadDeductions extends BaseStatusClass implements Runnable {

    private final GenericService genericService;
    private final HashMap<?, ?> payTypesList;
    private final FileParseBean fileParseBean;
    private final BusinessCertificate businessCertificate;
    private final PayrollFlag payrollFlag;
    private final LocalDate pendingPaycheckDate;
    private Vector<Object> wActualSaveList;

    public PersistFileUploadDeductions(GenericService genericService, HashMap<?, ?> payTypesList, FileParseBean fileParseBean, BusinessCertificate businessCertificate,
                                       PayrollFlag payrollFlag, LocalDate pendingPaycheckDate) {
        this.genericService = genericService;
        this.payTypesList = payTypesList;
        this.fileParseBean = fileParseBean;
        this.businessCertificate = businessCertificate;
        this.payrollFlag = payrollFlag;
        this.pendingPaycheckDate = pendingPaycheckDate;
    }

    @SneakyThrows
    @Override
    public void run() {

        try {
            persistDeductions();
        } catch (Exception wEx) {
            throw wEx;
        }
    }

    private synchronized void persistDeductions() throws IllegalAccessException, InstantiationException {
        startTime = System.currentTimeMillis();
        this.listSize = fileParseBean.getListToSave().size();
        this.fileParseBean.setTotalNumberOfRecords(this.listSize);
        wActualSaveList = new Vector<>();
        if(this.listSize > 50)
            this.batchSize = 20;
        else
            this.batchSize = 5;
        int noOfObject = 0;
        for (NamedEntity p : this.fileParseBean.getListToSave()) {

            EmpDeductionType wEDT = (EmpDeductionType) fileParseBean.getObjectMap().get(p.getObjectCode().trim().toUpperCase());
            AbstractDeductionEntity wDeductInfo = (AbstractDeductionEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getDeductionInfoClass(businessCertificate), Arrays.asList(CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), p.getId()),
                    CustomPredicate.procurePredicate("empDeductionType.id", wEDT.getId()), CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())));

            if (wDeductInfo.isNewEntity()) {
                wDeductInfo.setEmpDeductionType(wEDT);

                if (payTypesList.get(p.getObjectCode().trim().toUpperCase()) != null) {
                    wDeductInfo.setPayTypes((PayTypes) payTypesList.get(p.getObjectCode().trim().toUpperCase()));
                } else {
                    wDeductInfo.setPayTypes(wEDT.getPayTypes());
                }
                wDeductInfo.makeParentObject(p.getId());

                wDeductInfo.setCreatedBy(new User(businessCertificate.getLoginId()));
                wDeductInfo.setBusinessClientId(businessCertificate.getBusinessClientInstId());
            }

            if (wDeductInfo.getEmpDeductionType().isMustEnterDate()) {
                if (p.getStartDate() != null) {
                    wDeductInfo.setStartDate(p.getStartDate());
                } else {

                    LocalDate localDate = PayrollBeanUtils.makeNextPayPeriodStart(payrollFlag.getApprovedMonthInd(), payrollFlag.getApprovedYearInd());
                    wDeductInfo.setStartDate(localDate);
                }

                if (p.getEndDate() != null) {
                    wDeductInfo.setEndDate(p.getEndDate());
                } else {

                    LocalDate wEndDate = LocalDate.of(wDeductInfo.getStartDate().getYear(), wDeductInfo.getStartDate().getMonthValue(), wDeductInfo.getStartDate().lengthOfMonth());

                    wDeductInfo.setEndDate(wEndDate);
                }
            }

            wDeductInfo.setDescription(wEDT.getDescription());
            wDeductInfo.setName(wEDT.getName());
            wDeductInfo.setAmount(p.getDeductionAmount());
            wDeductInfo.setAnnualMax(0.0D);

            wDeductInfo.setLastModBy(new User(businessCertificate.getLoginId()));
            wDeductInfo.setLastModTs(Timestamp.from(Instant.now()));
            noOfObject++;
            wActualSaveList.add(wDeductInfo);
            if (wActualSaveList.size() == 50) {
                this.genericService.storeObjectBatch(wActualSaveList);
                this.fileParseBean.setSuccessfulRecords(this.fileParseBean.getSuccessfulRecords() + wActualSaveList.size());
                wActualSaveList = new Vector<>();
            }
            this.currentPercentage += 1;
        }

        if (!wActualSaveList.isEmpty()) {
            this.genericService.storeObjectBatch(wActualSaveList);
            this.fileParseBean.setSuccessfulRecords(this.fileParseBean.getSuccessfulRecords() + wActualSaveList.size());
        }

        //Set Pending Payroll Run to be rerun or deleted
        if (pendingPaycheckDate != null) {
            //This means we have a Pending Paycheck. Set for strictly RERUN
            RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService, businessCertificate.getBusinessClientInstId(), pendingPaycheckDate);
            wRPB.setNoOfDeductions(wRPB.getNoOfDeductions() + noOfObject);
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
