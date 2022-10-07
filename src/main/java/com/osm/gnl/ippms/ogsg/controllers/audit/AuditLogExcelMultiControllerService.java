/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.audit.domain.AbstractDeductionAuditEntity;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractSpecAllowAuditEntity;
import com.osm.gnl.ippms.ogsg.audit.domain.GarnishmentAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.AuditService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public abstract class AuditLogExcelMultiControllerService {


    public static PaginatedBean prepareDeductionAudit(GenericService genericService, AuditService auditService, BusinessCertificate businessCertificate, String pFromDate, String pToDate, Long pUserId, Long pTypeId, Long pEmpId) throws InstantiationException, IllegalAccessException {

        LocalDate fDate = null;
        LocalDate tDate = null;
        boolean usingDates = false;
        if (!IppmsUtils.isNotNullOrEmpty(pFromDate) && !IppmsUtils.isNotNullOrEmpty(pToDate)) {
            fDate = PayrollBeanUtils.setDateFromString(pFromDate);
            tDate = PayrollBeanUtils.setDateFromString(pToDate);
            usingDates = !usingDates;

        }

        List<AbstractDeductionAuditEntity> wDeductionLogList = auditService.loadDeductionAuditLogsForExport(businessCertificate, fDate, tDate, pUserId, pTypeId, pEmpId);

        Comparator<AbstractDeductionAuditEntity> comparator = Comparator.comparing(AbstractDeductionAuditEntity::getAuditTimeStamp);
        Collections.sort(wDeductionLogList, comparator.reversed());

        PaginatedBean pList = new PaginatedBean(wDeductionLogList);

        if (!usingDates) {
            pList.setFromDateStr(pFromDate);
            pList.setToDateStr(pToDate);
        } else {
            pList.setFromDateStr("");
            pList.setToDateStr("All Dates");
        }

        if (IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)) {
            pList.setTypeName((genericService.loadObjectById(EmpDeductionType.class, pTypeId)).getDescription());

        }
        pList.setFilteredByType(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId));
        pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
        if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
            pList.setEmployeeName(genericService.loadObjectById(User.class, pUserId).getActualUserName());

        return pList;
    }

    public static PaginatedBean prepareSpecAllowAudit(GenericService genericService, AuditService auditService, BusinessCertificate businessCertificate, String pFromDate, String pToDate, Long pUserId, Long pTypeId, Long pEmpId) throws InstantiationException, IllegalAccessException {

        LocalDate fDate = null;
        LocalDate tDate = null;
        boolean usingDates = false;
        if (!IppmsUtils.isNotNullOrEmpty(pFromDate) && !IppmsUtils.isNotNullOrEmpty(pToDate)) {
            fDate = PayrollBeanUtils.setDateFromString(pFromDate);
            tDate = PayrollBeanUtils.setDateFromString(pToDate);
            usingDates = !usingDates;

        }

        List<AbstractSpecAllowAuditEntity> wDeductionLogList = auditService.loadSpecAllowAuditLogsForExport(businessCertificate, fDate, tDate, pUserId, pTypeId, pEmpId);

        Comparator<AbstractSpecAllowAuditEntity> comparator = Comparator.comparing(AbstractSpecAllowAuditEntity::getAuditTimeStamp);
        Collections.sort(wDeductionLogList, comparator.reversed());

        PaginatedBean pList = new PaginatedBean(wDeductionLogList);

        if (!usingDates) {
            pList.setFromDateStr(pFromDate);
            pList.setToDateStr(pToDate);
        } else {
            pList.setFromDateStr("");
            pList.setToDateStr("All Dates");
        }

        if (IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)) {
            pList.setTypeName((genericService.loadObjectById(SpecialAllowanceType.class, pTypeId)).getDescription());

        }
        pList.setFilteredByType(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId));
        pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
        if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
            pList.setEmployeeName(genericService.loadObjectById(User.class, pUserId).getActualUserName());
        pList.setUsingDates(usingDates);
        return pList;
    }

    public static PaginatedBean prepareGarnishmentAudit(GenericService genericService, AuditService auditService, BusinessCertificate businessCertificate, String pFromDate, String pToDate, Long pUserId, Long pTypeId, Long pEmpId) throws InstantiationException, IllegalAccessException {
        LocalDate fDate = null;
        LocalDate tDate = null;
        List<GarnishmentAudit> wGarnLogList;
        boolean useDates = false;
        if(!pFromDate.equals("") && !pToDate.equals("")){
            fDate = PayrollBeanUtils.setDateFromString(pFromDate);
            tDate = PayrollBeanUtils.setDateFromString(pToDate);
            useDates = true;
        }
        if(useDates){
            wGarnLogList = auditService.loadGarnishmentLogsForExport(businessCertificate,PayrollBeanUtils.getNextORPreviousDay(fDate, false)
                    ,PayrollBeanUtils.getNextORPreviousDay(tDate, true),pUserId,pTypeId,pEmpId);
        }else{
            wGarnLogList = auditService.loadGarnishmentLogsForExport(businessCertificate,null,null,pUserId,pTypeId,pEmpId);
        }

        Comparator<GarnishmentAudit> comparator = Comparator.comparing(GarnishmentAudit::getAuditTime);
        Collections.sort(wGarnLogList,comparator.reversed());

        PaginatedBean pList = new PaginatedBean(wGarnLogList);

        if(useDates){
            pList.setFromDateStr(pFromDate);
            pList.setToDateStr(pToDate);
        }else{
            pList.setFromDateStr("");
            pList.setToDateStr("All Dates");
        }

        if(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)){
            String wName = genericService.loadObjectById(EmpGarnishmentType.class,  pTypeId).getName();
            pList.setTypeName(wName);

        }
        pList.setFilteredByType(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId));
        pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
        if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
            pList.setEmployeeName(genericService.loadObjectById(User.class, pUserId).getActualUserName());


        pList.setUsingDates(true);
        return pList;
    }
}
