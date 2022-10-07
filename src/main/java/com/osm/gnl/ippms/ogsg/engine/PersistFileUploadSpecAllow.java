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
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

@Slf4j
public class PersistFileUploadSpecAllow extends BaseStatusClass implements Runnable{

    private final GenericService genericService;
    private final FileParseBean fileParseBean;
    private final BusinessCertificate businessCertificate;
    private final PayrollFlag payrollFlag;
    private final LocalDate pendingPaycheckDate;


    public PersistFileUploadSpecAllow(GenericService genericService, FileParseBean fileParseBean, BusinessCertificate businessCertificate, PayrollFlag payrollFlag, LocalDate pendingPaycheckDate) {
        this.genericService = genericService;
        this.fileParseBean = fileParseBean;
        this.businessCertificate = businessCertificate;
        this.payrollFlag = payrollFlag;
        this.pendingPaycheckDate = pendingPaycheckDate;

    }

    @Override
    public void run() {
        try {
            persistSpecialAllowance();
        }catch (Exception wEx){
                log.error(wEx.getMessage());
                wEx.printStackTrace();
        }
    }

    private void persistSpecialAllowance() throws IllegalAccessException, InstantiationException {
        startTime = System.currentTimeMillis();
        this.listSize = fileParseBean.getListToSave().size();
        int recordCount = 0;
        if(this.listSize > 50)
            this.batchSize = 20;
        else
            this.batchSize = 5;
        this.fileParseBean.setTotalNumberOfRecords(this.listSize);
        Vector<Object> wActualSaveList = new Vector<>();
        CustomPredicate bizIdPredicate = CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId());
        SpecialAllowanceType wSAT;
        AbstractSpecialAllowanceEntity wSpecAllowInfo;
        for (NamedEntity p : fileParseBean.getListToSave()) {
              wSAT = (SpecialAllowanceType)fileParseBean.getObjectMap().get(p.getObjectCode().trim().toUpperCase());
              wSpecAllowInfo = (AbstractSpecialAllowanceEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getSpecialAllowanceInfoClass(businessCertificate), Arrays.asList(CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(),p.getId()),
                    CustomPredicate.procurePredicate("specialAllowanceType.id",wSAT.getId()), bizIdPredicate));
            if (wSpecAllowInfo.isNewEntity()) {

                wSpecAllowInfo.setSpecialAllowanceType(wSAT);
                wSpecAllowInfo.setDescription(wSAT.getDescription());
                wSpecAllowInfo.setName(wSAT.getName());
                wSpecAllowInfo.makeParentObject(p.getId());
                wSpecAllowInfo.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                wSpecAllowInfo.setCreatedBy(new User(businessCertificate.getLoginId()));
            }

            wSpecAllowInfo.setPayTypes(new PayTypes(p.getPaycheckTypeInstId()));

            wSpecAllowInfo.setAmount(p.getAllowanceAmount());

            wSpecAllowInfo.setStartDate(p.getAllowanceStartDate());
            if (p.getAllowanceEndDate() != null) {
                wSpecAllowInfo.setEndDate(p.getAllowanceEndDate());
            } else if (wSpecAllowInfo.getSpecialAllowanceType().isEndDateRequired() && (p.getAllowanceEndDate() == null)) {

                wSpecAllowInfo.setEndDate(LocalDate.of(p.getAllowanceStartDate().getYear(),p.getAllowanceStartDate().getMonthValue(),p.getAllowanceStartDate().lengthOfMonth()));
            }
            else{
                wSpecAllowInfo.setEndDate(null);
            }

            if (p.getAllowanceAmount() > 0.0D)
            {
                wSpecAllowInfo.setExpire(0);
            }
            else {
                wSpecAllowInfo.setExpire(1);
            }

            wSpecAllowInfo.setLastModBy(new User(businessCertificate.getLoginId()));
            wSpecAllowInfo.setLastModTs(Timestamp.from(Instant.now()));


            wSpecAllowInfo.setReferenceDate(LocalDate.now());
            wSpecAllowInfo.setReferenceNumber("/REF/SPA/UPLOAD/"+businessCertificate.getUserName()+ PayrollHRUtils.dateWidoutDelimeters());

            wActualSaveList.add(wSpecAllowInfo);
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
        if(pendingPaycheckDate != null){
            //This means we have a Pending Paycheck. Set for strictly RERUN
            RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService,businessCertificate.getBusinessClientInstId(),pendingPaycheckDate);
            wRPB.setNoOfSpecialAllowances(wRPB.getNoOfSpecialAllowances() + fileParseBean.getListToSave().size());
            if(wRPB.isNewEntity()){
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
