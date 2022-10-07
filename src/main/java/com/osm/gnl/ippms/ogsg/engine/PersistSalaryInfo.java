/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseStatusClass;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.MasterSalaryTemp;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryTemp;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Vector;
@Slf4j
public class PersistSalaryInfo extends BaseStatusClass implements Runnable{

    private final GenericService genericService;
    private final FileParseBean fileParseBean;
    private final BusinessCertificate businessCertificate;
    private final PayrollFlag payrollFlag;
    private final LocalDate pendingPaycheckDate;

    public PersistSalaryInfo(GenericService genericService, FileParseBean fileParseBean, BusinessCertificate businessCertificate,
                             PayrollFlag payrollFlag, LocalDate pendingPaycheckDate) {
        this.genericService = genericService;
        this.fileParseBean = fileParseBean;
        this.businessCertificate = businessCertificate;
        this.payrollFlag = payrollFlag;
        this.pendingPaycheckDate = pendingPaycheckDate;

    }

    @Override
    public void run() {
        try {
            persistSalaryInformation();
        }catch (Exception wEx){
            log.error(wEx.getMessage());
            wEx.printStackTrace();
        }
    }

    private void persistSalaryInformation() throws Exception {
        startTime = System.currentTimeMillis();
        this.listSize = fileParseBean.getPayGroupList().size();
        this.fileParseBean.setTotalNumberOfRecords(this.listSize);
        if(this.listSize > 50)
            this.batchSize = 20;
        else
            this.batchSize = 5;
        Vector<Object> wActualSaveList = new Vector<>();
        SalaryType salaryType = genericService.loadObjectById(SalaryType.class,fileParseBean.getSalaryTypeId());

        MasterSalaryTemp masterSalaryTemp = new MasterSalaryTemp();
        masterSalaryTemp.setBusinessClientId(businessCertificate.getBusinessClientInstId());
        masterSalaryTemp.setSalaryType(salaryType);
        masterSalaryTemp.setInitiator(new User(businessCertificate.getLoginId()));
        masterSalaryTemp.setEntityId(salaryType.getId());
        masterSalaryTemp.setEntityName(salaryType.getName());
        masterSalaryTemp.setEmployeeId(salaryType.getPayGroupCode());
        masterSalaryTemp.setLastModTs(LocalDate.now());
        masterSalaryTemp.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(this.businessCertificate.getLoginId()));
        this.genericService.saveObject(masterSalaryTemp);


        for (SalaryTemp p : fileParseBean.getPayGroupList()) {
            p.setParentId(masterSalaryTemp.getId());
            p.setPayGroupCode(salaryType.getPayGroupCode()+""+p.getLevel());
            p.setLastModBy(businessCertificate.getLoginId());
            p.setLastModTs(Timestamp.from(Instant.now()));
            p.setCreatedDate(p.getLastModTs());
            p.setSalaryTypeId(salaryType.getId());

            wActualSaveList.add(p);
            if (wActualSaveList.size() == 50) {
                try{
                    this.genericService.storeObjectBatch(wActualSaveList);
                    this.fileParseBean.setSuccessfulRecords(this.fileParseBean.getSuccessfulRecords() + wActualSaveList.size());
                }catch (Exception ex){
                    //Do we eat it? How do we propagate?
                    throw new Exception(ex);
                }

                wActualSaveList = new Vector<>();
            }
            this.currentPercentage += 1;
        }
        if (!wActualSaveList.isEmpty()) {
            this.genericService.storeObjectBatch(wActualSaveList);
            this.fileParseBean.setSuccessfulRecords(this.fileParseBean.getSuccessfulRecords() + wActualSaveList.size());
        }
        //Make Notification....
        String redirectUrl = "requestNotification.do?arid=" + masterSalaryTemp.getId() + "&s=1&oc="+IConstants.PAY_GROUP_INIT_URL_IND;
        NotificationService.storeNotification(this.businessCertificate,genericService,masterSalaryTemp,redirectUrl,"Pay Group Approval Request", IConstants.PAY_GROUP_APPROVAL_CODE);

        //Set Pending Payroll Run to be rerun or deleted

        this.currentPercentage = this.listSize;
    }

    public FileParseBean getFileParseBean() {
        return fileParseBean;
    }
}
