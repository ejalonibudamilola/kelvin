/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.employee.service;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.employee.beans.EmployeeBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.NextOfKin;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public abstract class EmpGenOverviewService {


    public static Object makeModeAndReturnView(EmployeeBean pEB, NamedEntity ne,
                                               BusinessCertificate bc, GenericService genericService, PaycheckService paycheckService,
                                               PromotionService promotionService, ConfigurationBean configurationBean) throws Exception {


        if (pEB.getEmployee().isSchoolEnabled()) {
            if ((pEB.getEmployee().getSchoolInfo() != null) &&
                    (!pEB.getEmployee().getSchoolInfo().isNewEntity())) {
                pEB.setHasSchoolInformation(true);
            }

        }
        HiringInfo wHireInfo = genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), ne.getId()));
        if (wHireInfo.isNewEntity()) {
            if(bc.isPensioner())
                return "redirect:penHireInfo.do?oid=" + ne.getId();
            return "redirect:hiringForm.do?oid=" + ne.getId();
        }
        pEB.setHiringInfo(wHireInfo);
        //For Pension
        if (bc.isPensioner()) {
            GratuityInfo wGI = genericService.loadObjectUsingRestriction(GratuityInfo.class, Arrays.asList(CustomPredicate.procurePredicate("pensioner.id", pEB.getHiringInfo().getParentId()),
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
            if (!wGI.isNewEntity()) {
                pEB.setHasGratuityPayments(true);
                pEB.setGratuityInfo(wGI);
            }
            //Also set the Monthly and Yearly Pension Amount

            pEB.getHiringInfo().setMonthlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(pEB.getHiringInfo().getMonthlyPensionAmount()));
            pEB.getHiringInfo().setYearlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(pEB.getHiringInfo().getMonthlyPensionAmount() * 12.0D));
        }
        pEB.setId(bc.getBusinessClientInstId());
        //Check if Hiring Info Exists...

        //Load Next Of Kin information...
        List<NextOfKin> wNextOfKinList = genericService.loadAllObjectsWithSingleCondition(NextOfKin.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), ne.getId()), null);

        if (wNextOfKinList == null || wNextOfKinList.isEmpty()) {
            pEB.setNextOfKinMessage("No Next Of Kin");
            pEB.setFloatMessage("Click to Add Next Of Kin");
            pEB.setHasNextOfKin(false);
        } else {
            pEB.setHasNextOfKin(true);
            if (wNextOfKinList.size() == 1) {
                pEB.setNextOfKinMessage("1 Next Of Kin");
            } else {
                pEB.setNextOfKinMessage("2 Next Of Kins");
            }
            pEB.setFloatMessage("Click to Edit/Delete Next Of Kin");

        }

        pEB.getEmployee().getSalaryInfo().setMonthlySalaryStr(PayrollHRUtils.getDecimalFormat().format(pEB.getEmployee().getSalaryInfo().getMonthlyBasicSalary() / 12.0D));


        PaymentMethodInfo pMI = genericService.loadObjectWithSingleCondition(PaymentMethodInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), ne.getId()));
        if (pMI.isNewEntity()) {
            pEB.setPayMethodType("None");
            pEB.setBankName("None");
            pEB.setBankBranchName("None");
            pEB.setAccountNumber("");
        } else {
            pEB.setPayMethodType(pMI.getPaymentMethodTypes().getName());

            pEB.setBankName(pMI.getBankBranches().getBankInfo().getName());
            pEB.setBankBranchName(pMI.getBankBranches().getName());
            pEB.setAccountNumber(pMI.getAccountNumber());
            pEB.setBvnNo(pMI.getBvnNo());
        }

        pEB.setCityStateZip(pEB.getEmployee().getCity().getName() + ", " + pEB.getEmployee().getCity().getState().getName() + " " + IppmsUtils.treatNull(pEB.getEmployee().getZipCode()));

        if ((pEB.getCityStateZip() != null) && (pEB.getCityStateZip().endsWith(","))) {
            pEB.setCityStateZip(pEB.getCityStateZip().substring(0, pEB.getCityStateZip().lastIndexOf(",")));
        }

        pEB.setEmployeeType(pEB.getEmployee().getEmployeeType().getName());
        if (pEB.getHiringInfo().isSuspendedEmployee()) {
            pEB.setShowSuspensionRow(IConstants.SHOW_ROW);
            PredicateBuilder predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("suspensionDate", PayrollBeanUtils.getNextORPreviousDay(pEB.getHiringInfo().getSuspensionDate(), false), Operation.GREATER_OR_EQUAL));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("suspensionDate", PayrollBeanUtils.getNextORPreviousDay(pEB.getHiringInfo().getSuspensionDate(), false), Operation.LESS));

            predicateBuilder.addPredicate(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEB.getEmployee().getId()));


            SuspensionLog wSL = new SuspensionLog();

            List<SuspensionLog> wList = genericService.getObjectsFromBuilder(predicateBuilder, SuspensionLog.class);

            if(!IppmsUtils.isNullOrEmpty(wList)) {
                Collections.sort(wList, Comparator.comparing(SuspensionLog::getId).reversed());
                wSL = wList.get(0);
            }

            if (!wSL.isNewEntity()) {
                pEB.setSuspendedBy(wSL.getUser().getActualUserName());
                pEB.setSuspensionReasonStr(wSL.getSuspensionType().getName());
            } else {
                pEB.setSuspendedBy("Unknown");
                pEB.setSuspensionReasonStr("Unknown");
            }

        } else {
            //First Get the Suspension Details...

            pEB.setShowSuspensionRow(IConstants.HIDE_ROW);
        }

        if (pEB.getHiringInfo().isOnContract()) {
            pEB.setShowContractRow(IConstants.SHOW_ROW);
            pEB.setContractType(pEB.getEmployee().getEmployeeType().getName());
        }

        if (ne.getMode().equalsIgnoreCase("create")) {
            pEB.setButtonRowInd("table-row");
        } else pEB.setButtonRowInd("none");
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("owedAmount", 0, Operation.GREATER));

        predicateBuilder.addPredicate(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEB.getEmployee().getId(), Operation.EQUALS));

        int i = genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getGarnishmentInfoClass(bc));
        if (i == 0) {
            pEB.setGarnishment("None");
        } else if (i == 1)
            pEB.setGarnishment(i + " Garnishment/Loan");
        else {
            pEB.setGarnishment(i + " Garnishments/Loans");
        }

        predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("amount", 0, Operation.GREATER));

        predicateBuilder.addPredicate(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEB.getEmployee().getId(), Operation.EQUALS));
        int wTotNum = genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getDeductionInfoClass(bc));


        int wAddendum = 0;
        if(!bc.isPensioner() && !pEB.getHiringInfo().getEmployee().getSalaryInfo().getSalaryType().isExemptFromPension()) {

            LocalDate wTpsHireDate = PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_HIRE_DATE_STR);
            LocalDate wExpectedRetirementDate = PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR);

            if (pEB.getHiringInfo().isPensionableEmployee()) {
                if (!pEB.isNewEntity() && !pEB.getHiringInfo().getAbstractEmployeeEntity().getEmployeeType().isPoliticalOfficeHolder()
                        && !PayrollBeanUtils.isTPSEmployee(pEB.getHiringInfo().getBirthDate(), pEB.getHiringInfo().getHireDate()
                        , pEB.getHiringInfo().getExpectedDateOfRetirement(), wTpsHireDate, wExpectedRetirementDate, configurationBean, bc)) {
                    wAddendum++;
                }
            }
        }
        wTotNum += wAddendum;


        if (wTotNum == 0) {
            pEB.setEmpDed("None");
        } else if (wTotNum == 1)
            pEB.setEmpDed(wTotNum + " Deduction");
        else {
            pEB.setEmpDed(wTotNum + " Deductions");
        }

        predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("expire", 0, Operation.EQUALS));

        predicateBuilder.addPredicate(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEB.getEmployee().getId(), Operation.EQUALS));
        int wTotSpecAllow = genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getSpecialAllowanceInfoClass(bc));

        if (wTotSpecAllow == 0) {
            pEB.setSpecAllow("None");
        } else if (wTotNum == 1)
            pEB.setSpecAllow(wTotSpecAllow + " Special Allowance");
        else {
            pEB.setSpecAllow(wTotSpecAllow + " Special Allowances");
        }

        Long pid = paycheckService.getMaxPaycheckIdForEmployee(bc, pEB.getEmployee().getId());
        if (!pid.equals(0L)) {

            pEB.setLastPayCheck((AbstractPaycheckEntity) genericService.loadObjectById(IppmsUtils.getPaycheckClass(bc), pid));
            pEB.setHasPayInformation(true);

        } else {
            pEB.setHasPayInformation(false);
        }


        pEB = formatDates(pEB, genericService, bc,configurationBean);
        pEB = setLastAndNextPromotionDates(pEB, bc, genericService, promotionService);

        return pEB;


    }

    private static EmployeeBean formatDates(EmployeeBean pEB, GenericService genericService, BusinessCertificate businessCertificate,ConfigurationBean configurationBean) throws IllegalAccessException, InstantiationException {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        if ((pEB.getHiringInfo() == null) || (pEB.getHiringInfo().isNewEntity())) {
            pEB.setBirthDate("");
            pEB.setHireDate("");
        } else {
            pEB.setBirthDate(sdf.format(pEB.getHiringInfo().getBirthDate()));
            pEB.setHireDate(sdf.format(pEB.getHiringInfo().getHireDate()));
            if (pEB.getHiringInfo().getConfirmDate() == null)
                pEB.getHiringInfo().setConfirmDateStr("");
            else {
                pEB.getHiringInfo().setConfirmDateStr(sdf.format(pEB.getHiringInfo().getConfirmDate()));
            }
            if (businessCertificate.isPensioner()) {
                if(pEB.getHiringInfo().getPensionStartDate() == null){
                    pEB.getHiringInfo().setPensionStartDateStr("");
                }else{
                    pEB.getHiringInfo().setPensionStartDateStr(sdf.format(pEB.getHiringInfo().getPensionStartDate()));
                }
                if(pEB.getHiringInfo().getPensionEndDate() == null){
                    pEB.getHiringInfo().setPensionEndDateStr("");
                }else{
                    pEB.getHiringInfo().setPensionEndDateStr(sdf.format(pEB.getHiringInfo().getPensionEndDate()));
                }
            } else {

                if (pEB.getHiringInfo().getExpectedDateOfRetirement() == null) {
                    if (!pEB.getEmployee().isTerminated() && pEB.getHiringInfo().getBirthDate() != null && pEB.getHiringInfo().getHireDate() != null) {
                        pEB.getHiringInfo().setExpectedDateOfRetirement(PayrollBeanUtils.calculateExpDateOfRetirement(pEB.getHiringInfo().getBirthDate(), pEB.getHiringInfo().getHireDate(),configurationBean,businessCertificate));
                        genericService.saveObject(pEB.getHiringInfo());
                        pEB.getHiringInfo().setExpDateOfRetireStr(sdf.format(pEB.getHiringInfo().getExpectedDateOfRetirement()));
                    } else {
                        pEB.getHiringInfo().setExpDateOfRetireStr("");
                    }

                } else {
                    pEB.getHiringInfo().setExpDateOfRetireStr(sdf.format(pEB.getHiringInfo().getExpectedDateOfRetirement()));
                }
            }
            if ((pEB.getHiringInfo().isSuspendedEmployee()) &&
                    (pEB.getHiringInfo().getSuspensionDate() != null)) {
                pEB.getHiringInfo().setSuspensionDateStr(sdf.format(pEB.getHiringInfo().getSuspensionDate()));
                pEB.getHiringInfo().setSuspendedStr("On Suspension");
            }

        }
        if (pEB.getEmployee().isTerminated()) {
            pEB.setTerminatedBy(  pEB.getHiringInfo().getLastModBy().getActualUserName());
        }
        return pEB;
    }

    private static EmployeeBean setLastAndNextPromotionDates(EmployeeBean pPeb, BusinessCertificate bc, final GenericService genericService
            , final PromotionService promotionService) throws InstantiationException, IllegalAccessException {
        if(bc.isPensioner())
            return pPeb;
        PromotionTracker wPT = genericService.loadObjectWithSingleCondition(PromotionTracker.class, CustomPredicate.procurePredicate("employee.id", pPeb.getEmployee().getId()));
        boolean valueNotSet = true;

        if ((wPT != null) && (!wPT.isNewEntity())) {
            valueNotSet = false;
            pPeb.getHiringInfo().setLastPromotionDateStr(PayrollHRUtils.getDisplayDateFormat().format(wPT.getLastPromotionDate()));
            pPeb.setNextPromotionDateStr(PayrollHRUtils.getDisplayDateFormat().format(wPT.getNextPromotionDate()));
        } else if ((pPeb.getHiringInfo() != null) && (!pPeb.getHiringInfo().isNewEntity())) {
            if (pPeb.getHiringInfo().getLastPromotionDate() != null) {
                valueNotSet = false;
                PromotionTracker p = new PromotionTracker();
                p.setEmployee(new Employee(pPeb.getEmployee().getId()));
                p.setLastPromotionDate(pPeb.getHiringInfo().getLastPromotionDate());
                p.setNextPromotionDate(PayrollHRUtils.determineNextPromotionDate(p.getLastPromotionDate(), pPeb.getEmployee().getSalaryInfo().getLevel()));
                p.setUser(new User(bc.getLoginId()));
                p.setBusinessClientId(bc.getBusinessClientInstId());
                genericService.saveObject(p);

                pPeb.getHiringInfo().setLastPromotionDateStr(PayrollHRUtils.getDisplayDateFormat().format(p.getLastPromotionDate()));
                pPeb.setNextPromotionDateStr(PayrollHRUtils.getDisplayDateFormat().format(p.getNextPromotionDate()));
            }
        }

        if (valueNotSet) {
            pPeb.getHiringInfo().setLastPromotionDateStr("Value Not Set");
            pPeb.setNextPromotionDateStr("Value Not Set");
        }

        //Check if Employee Has Promotion History...
        pPeb.setHasPromotionHistory(promotionService.employeeHasPromotionHistory(bc,pPeb.getEmployee().getId()));
        return pPeb;
    }
}
