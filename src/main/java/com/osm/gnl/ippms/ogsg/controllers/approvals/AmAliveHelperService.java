/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.approval.AmAliveApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

public abstract class AmAliveHelperService {

    public static int noOfPendingAmAliveApprovals(BusinessCertificate bc, GenericService genericService) {
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd",IConstants.OFF));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder, AmAliveApproval.class);
    }
    public static int noOfPendingAmAliveApprovals(BusinessCertificate bc, GenericService genericService, Long pTicketId) {
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("ticketId",pTicketId));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder, AmAliveApproval.class);
    }
    public static boolean treatHiringInfo(AmAliveApproval pEHB, BusinessCertificate bc, GenericService genericService) throws Exception {
        HiringInfo hiringInfo = pEHB.getHiringInfo();
        if(pEHB.isGroupTicket())
        if(pEHB.isSuspendStaff()){
            PayrollFlag payrollFlag = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);
            LocalDate localDate = PayrollBeanUtils.getDateFromMonthAndYear(payrollFlag.getApprovedMonthInd(),payrollFlag.getApprovedYearInd(),true);
            LocalDate endOfMonth = PayrollBeanUtils.makeNextPayPeriod(localDate,localDate.getMonthValue(),localDate.getYear());
            LocalDate startOfMonth = LocalDate.of(endOfMonth.getYear(),endOfMonth.getMonthValue(),1);
            ArrayList<CustomPredicate> customPredicates = new ArrayList<>();
            customPredicates.add(CustomPredicate.procurePredicate("suspensionDate", startOfMonth, Operation.GREATER_OR_EQUAL));
            customPredicates.add(CustomPredicate.procurePredicate("suspensionDate", endOfMonth, Operation.LESS_OR_EQUAL));
            customPredicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),hiringInfo.getParentId()));


            SuspensionLog s = genericService.loadObjectUsingRestriction(SuspensionLog.class, customPredicates);

            boolean increment = s.isNewEntity();
            if(increment){
                if(hiringInfo.isPensionerType()){
                    s.setPensioner(new Pensioner(hiringInfo.getParentId()));
                }else{
                    s.setEmployee(new Employee(hiringInfo.getParentId()));
                }
                s.setName(hiringInfo.getAbstractEmployeeEntity().getDisplayName());
                s.setBusinessClientId(bc.getBusinessClientInstId());
            }
            s.setSuspensionType(IppmsUtilsExt.loadAmAliveSuspensionType(bc,genericService));
            s.setUser(new User(bc.getLoginId()));
            s.setPayPercentage(0.00D);
            s.setReferenceNumber(bc.getUserName()+"_AA_SUS_"+hiringInfo.getAbstractEmployeeEntity().getEmployeeId());
            s.setMdaInfo(hiringInfo.getAbstractEmployeeEntity().getMdaDeptMap().getMdaInfo());
            s.setSalaryInfo(hiringInfo.getAbstractEmployeeEntity().getSalaryInfo());
            s.setSuspensionDate(LocalDate.now());
            s.setUser(new User(bc.getLoginId()));
            s.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService,bc));
            s.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
            s.setLastModTs(LocalDate.now());

            if(hiringInfo.getAbstractEmployeeEntity().isSchoolStaff()){
                s.setSchoolInfo(hiringInfo.getAbstractEmployeeEntity().getSchoolInfo());
            }

            genericService.saveObject(s);

            hiringInfo.setLastModBy(new User(bc.getLoginId()));
            hiringInfo.setLastModTs(Timestamp.from(Instant.now()));
            hiringInfo.setSuspended(1);
            hiringInfo.setSuspensionDate(LocalDate.now());


        }else{
            hiringInfo.setLastModBy(new User(bc.getLoginId()));
            hiringInfo.setLastModTs(Timestamp.from(Instant.now()));
            hiringInfo.setAmAliveDate(hiringInfo.getResetIAmAliveDate());

        }
        try{
            genericService.saveObject(hiringInfo);
        }catch (Exception exception){
            return false;
        }
        return true;
    }

}
