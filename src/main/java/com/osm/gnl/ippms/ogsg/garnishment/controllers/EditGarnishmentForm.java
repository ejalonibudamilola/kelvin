/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.garnishment.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.engine.ApprovePendingPayroll;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.negativepay.domain.NegativePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.garnishment.GarnishmentValidator;
import com.osm.gnl.ippms.ogsg.validators.garnishment.GarnishmentValidatorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@Controller
@RequestMapping({"/empGarnishForm.do"})
@SessionAttributes("empGarnishInfo")
public class EditGarnishmentForm extends BaseController {

     private final GarnishmentValidator garnishmentValidator;
     private final PaycheckService paycheckService;

    private final GarnishmentValidatorHelper garnishmentValidatorHelper;
    private final String VIEW_NAME = "employee/empGarnishForm";

    @Autowired
    public EditGarnishmentForm(GarnishmentValidator garnishmentValidator, PaycheckService paycheckService, GarnishmentValidatorHelper garnishmentValidatorHelper) {
        this.garnishmentValidator = garnishmentValidator;
        this.paycheckService = paycheckService;
        this.garnishmentValidatorHelper = garnishmentValidatorHelper;
    }

    @ModelAttribute("garnishType")
    public List<EmpGarnishmentType> getGarnishType(HttpServletRequest request) {

        List<EmpGarnishmentType> wRetList = this.genericService.loadAllObjectsWithSingleCondition(EmpGarnishmentType.class, super.getBusinessClientIdPredicate(request), "description");
        return wRetList;


    }

    @ModelAttribute("loanTermList")
    public Collection<NamedEntity> getLoanTermList() {

        ArrayList<NamedEntity> wList = new ArrayList<NamedEntity>();

        for (int i = 1; i < 97; i++) {
            NamedEntity n = new NamedEntity(i);

            if (i == 1)
                n.setName(i + " Month");
            else {
                n.setName(i + " Months");
            }
            wList.add(n);
        }

        return wList;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
        if ((ne.isNewEntity()) || (ne.getName() == null)) {
            Employee emp = (Employee) IppmsUtils.loadEmployee(genericService, pEmpId, bc);
            ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ] ");
            ne.setId(emp.getId());
        }

        model.addAttribute("namedEntity", ne);
        AbstractGarnishmentEntity empGarnishInfo = IppmsUtils.makeGarnishmentInfoObject(bc);
        empGarnishInfo.setEditDenied(false);
        empGarnishInfo.setDisplayName("Create Loan");
        empGarnishInfo.setEmployee(new Employee(ne.getId()));
        model.addAttribute("empGarnishInfo", empGarnishInfo);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid", "cid", "atn"})
    public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("cid") Long pGarnishId, @RequestParam("atn") String pAction, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        //Object userId = this.getSessionId(request);
        AbstractGarnishmentEntity empGarnishInfo = (AbstractGarnishmentEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getGarnishmentInfoClass(bc), Arrays.asList(
                CustomPredicate.procurePredicate("employee.id", pEmpId), CustomPredicate.procurePredicate("id", pGarnishId),
                super.getBusinessClientIdPredicate(request)));

        if (pAction.equalsIgnoreCase("d")) {
            empGarnishInfo.setDelete(true);
            empGarnishInfo.setDisplayName("Delete Loan");
        } else if (pAction.equalsIgnoreCase("v")) {
            empGarnishInfo.setViewOnlyMode(true);
            empGarnishInfo.setEdit(false);
            empGarnishInfo.setDisplayName("View Loan");
        } else {
            empGarnishInfo.setEdit(true);
            empGarnishInfo.setDisplayName("Edit Loan");
        }
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);

        AbstractEmployeeEntity emp = (AbstractEmployeeEntity) this.genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), ne.getId());
        ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ] ");
        ne.setId(emp.getId());

        if (empGarnishInfo.getEmpGarnishmentType().isEditRestricted()) {
            empGarnishInfo.setEditDenied(!bc.isSuperAdmin());
        }
        model.addAttribute("namedEntity", ne);
        empGarnishInfo.setEmpGarnishTypeInstId(empGarnishInfo.getEmpGarnishmentType().getId());

        empGarnishInfo.setOwedAmountStr(PayrollHRUtils.getDecimalFormat().format(empGarnishInfo.getOwedAmount()));
        empGarnishInfo.setOldOwedAmount(empGarnishInfo.getOwedAmount());

        empGarnishInfo.setOldLoanTerm(empGarnishInfo.getLoanTerm());
        empGarnishInfo.setOldCurrentLoanTerm(empGarnishInfo.getCurrentLoanTerm());
        empGarnishInfo.setGarnishAmountStr(PayrollHRUtils.getDecimalFormat().format(empGarnishInfo.getAmount()));

        model.addAttribute("empGarnishInfo", empGarnishInfo);

        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }

    /**
     * @param pEmpId
     * @param pGarnishId
     * @param model
     * @param request
     * @return
     * @throws Exception
     * @Note - This Handles Loan/Garnishment edit when there is negative pay
     */
    @RequestMapping(method = {RequestMethod.GET}, params = {"eid", "cid", "npv"})
    public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("cid") Long pGarnishId, @RequestParam("npv") Long pNegPayId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        //Object userId = super.getSessionId(request);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        AbstractGarnishmentEntity empGarnishInfo = (AbstractGarnishmentEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getGarnishmentInfoClass(bc), Arrays.asList(
                CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmpId), CustomPredicate.procurePredicate("id", pGarnishId),
                super.getBusinessClientIdPredicate(request)));


        empGarnishInfo.setEdit(true);
        NegativePayBean wNPB = this.genericService.loadObjectById(NegativePayBean.class, pNegPayId);
        empGarnishInfo.setDisplayName("Edit Loan ");
        empGarnishInfo.setDisplayTitle("Target Reduction : " + wNPB.getCurrDiff());

        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
        if ((ne.isNewEntity()) || (ne.getName() == null)) {
            AbstractEmployeeEntity emp =   IppmsUtils.loadEmployee(genericService, pEmpId, bc);
            ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ] ");
            ne.setId(emp.getId());
        }

        model.addAttribute("namedEntity", ne);
        empGarnishInfo.setEmpGarnishTypeInstId(empGarnishInfo.getEmpGarnishmentType().getId());

        empGarnishInfo.setOwedAmountStr(PayrollHRUtils.getDecimalFormat().format(empGarnishInfo.getOwedAmount()));
        empGarnishInfo.setOldOwedAmount(empGarnishInfo.getOwedAmount());

        empGarnishInfo.setOldLoanTerm(empGarnishInfo.getLoanTerm());
        empGarnishInfo.setOldCurrentLoanTerm(empGarnishInfo.getCurrentLoanTerm());
        empGarnishInfo.setGarnishAmountStr(PayrollHRUtils.getDecimalFormat().format(empGarnishInfo.getAmount()));
        empGarnishInfo.setOldGarnishAmount(empGarnishInfo.getAmount());
        empGarnishInfo.setNegativePayId(wNPB.getId());
        //Now check if this loan can be deleted.
        //This is for new Loans that do not exist...
        //PayrollFlag wPf = this.payrollService.getPayrollFlagByParentId(bc.getBusinessClientInstId());

        //empGarnishInfo.setDeletable(PayrollBeanUtils.isLoanDeletable(this.payrollServiceExt,ne.getId(),empGarnishInfo.getId(),wPf.getApprovedMonthInd(),wPf.getApprovedYearInd() ));

        model.addAttribute("empGarnishInfo", empGarnishInfo);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @RequestParam(value = "_delete", required = false) String delete,
                                @ModelAttribute("empGarnishInfo") AbstractGarnishmentEntity empGarnishInfo, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        //Object userId = this.getSessionId(request);

        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(super.getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", 1)));
        if (!wPRB.isNewEntity() && wPRB.isRunning()) {
            result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". Employee Loans can not be edited/added or deleted during a Payroll Run");
            addDisplayErrorsToModel(model, request);
            NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
            if ((ne.isNewEntity()) || (ne.getName() == null)) {
                AbstractEmployeeEntity emp = (AbstractEmployeeEntity) this.genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), empGarnishInfo.getParentId());
                ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ] ");
                ne.setId(emp.getId());
            }

            model.addAttribute("pageErrors", result);
            model.addAttribute("empGarnishInfo", empGarnishInfo);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;

        }
        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            if (empGarnishInfo.isNewEntity())
                return "redirect:dedGarnForm.do?eid=" + empGarnishInfo.getParentId()
                        + "&pid=" + bc.getBusinessClientInstId() + "&oid=0" + "&tid=" + LOAN;
            else
                return "redirect:dedGarnForm.do?eid=" + empGarnishInfo.getParentId()
                        + "&pid=" + bc.getBusinessClientInstId() + "&oid=" + empGarnishInfo.getId() + "&tid=" + LOAN;

        }


        if (isButtonTypeClick(request, REQUEST_PARAM_DELETE)) {
            garnishmentValidator.validateForDelete(empGarnishInfo, result, bc);
            if (result.hasErrors()) {
                addDisplayErrorsToModel(model, request);
                NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
                if ((ne.isNewEntity()) || (ne.getName() == null)) {
                    AbstractEmployeeEntity emp = (AbstractEmployeeEntity) this.genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), empGarnishInfo.getParentId());
                    ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ] ");
                    ne.setId(emp.getId());
                }

                model.addAttribute("pageErrors", result);
                model.addAttribute("empGarnishInfo", empGarnishInfo);
                model.addAttribute("roleBean", bc);
                return VIEW_NAME;
            }
            Long empId = empGarnishInfo.getParentId();
            this.genericService.deleteObject(empGarnishInfo);

            return "redirect:dedGarnForm.do?eid=" + empId + "&pid=" + bc.getBusinessClientInstId() + "&oid=0&tid=" + LOAN;
        }
        garnishmentValidator.validate(empGarnishInfo, result, bc);
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
            if ((ne.isNewEntity()) || (ne.getName() == null)) {
                AbstractEmployeeEntity emp = (AbstractEmployeeEntity) this.genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), empGarnishInfo.getParentId());
                ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ] ");
                ne.setId(emp.getId());
            }

            model.addAttribute("namedEntity", ne);
            model.addAttribute("pageErrors", result);
            model.addAttribute("empGarnishInfo", empGarnishInfo);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }
         List<String> wStr = this.garnishmentValidatorHelper.validateGarnishmentFileUpload(genericService,empGarnishInfo,bc, IppmsUtilsExt.loadConfigurationBean(genericService,bc));
        if(IppmsUtils.isNotNullOrEmpty(wStr)){
            addDisplayErrorsToModel(model, request);
            for(String str : wStr)
                 result.rejectValue("owedAmountStr","Invalid.Value",str);
            NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
            if ((ne.isNewEntity()) || (ne.getName() == null)) {
                AbstractEmployeeEntity emp = (AbstractEmployeeEntity) this.genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), empGarnishInfo.getParentId());
                ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ] ");
                ne.setId(emp.getId());
            }

            model.addAttribute("namedEntity", ne);
            model.addAttribute("pageErrors", result);
            model.addAttribute("empGarnishInfo", empGarnishInfo);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }

        if (empGarnishInfo != null) {
            //Now check if we are performing a Payroll Approval...
            ApprovePendingPayroll wAPP = (ApprovePendingPayroll) getSessionAttribute(request, "aprvPay");
            if (wAPP != null) {
                result.rejectValue("", "PayrollApproval", "Payroll is currently being approved. Loans can not be altered.");
                addDisplayErrorsToModel(model, request);
                NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
                if ((ne.isNewEntity()) || (ne.getName() == null)) {
                    AbstractEmployeeEntity emp = (AbstractEmployeeEntity) this.genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), empGarnishInfo.getParentId());
                    ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ] ");
                    ne.setId(emp.getId());
                }

                model.addAttribute("namedEntity", ne);
                model.addAttribute("pageErrors", result);
                model.addAttribute("empGarnishInfo", empGarnishInfo);
                model.addAttribute("roleBean", bc);
                return VIEW_NAME;
            }
            /**
             * Always allow the Triggers to do the final update
             * if not it could screw up loan payments by setting
             * monthly amount to 0.
             * Ola how????
             */
            if (empGarnishInfo.getOldOwedAmount() == 0 && empGarnishInfo.isEdit()) {
                if (IppmsUtils.isNotNullOrEmpty(empGarnishInfo.getOwedAmountStr()))
                    return "redirect:dedGarnForm.do?eid=" + empGarnishInfo.getParentId() + "&pid=" + bc.getBusinessClientInstId()
                            + "&oid=" + empGarnishInfo.getId() + "&tid=" + LOAN;
            }
            empGarnishInfo.setOwedAmount(Double.parseDouble(PayrollHRUtils.removeCommas(empGarnishInfo.getOwedAmountStr())));

            if ((empGarnishInfo.getOldOwedAmount() != empGarnishInfo.getOwedAmount())
                    || empGarnishInfo.getNewLoanTerm() > 0) {

                if (!empGarnishInfo.isNewEntity() && !empGarnishInfo.isConfirmation()
                        && empGarnishInfo.getNewLoanTerm() > 0
                        && empGarnishInfo.getOwedAmount() > 0.0D) {
                    empGarnishInfo.setConfirmation(true);
                    BigDecimal wNewMonthlyPaymentBD = new BigDecimal(empGarnishInfo.getOwedAmount() / new Double(empGarnishInfo.getNewLoanTerm()).doubleValue()).setScale(2, RoundingMode.FLOOR);
                    double wNewMonthlyPayment = wNewMonthlyPaymentBD.doubleValue();

                    // wNewMonthlyPayment = Double.parseDouble(PayrollHRUtils.removeCommas(PayrollHRUtils.getDecimalFormat(true).format(wNewMonthlyPayment)));

                    //We need to warn that the Values will change....
                    result.rejectValue("", "PayrollApproval", "You have chosen to restructure this loan as follows....");
                    result.rejectValue("", "PayrollApproval", "New Owed Amount : " + empGarnishInfo.getOwedAmountStr());
                    result.rejectValue("", "PayrollApproval", "New Loan Term  : " + empGarnishInfo.getNewLoanTerm());
                    result.rejectValue("", "PayrollApproval", "New Monthly Payment : " + PayrollHRUtils.getDecimalFormat().format(wNewMonthlyPayment));
                    result.rejectValue("", "PayrollApproval", "Click 'Confirm' button to confirm loan restructuring or 'Cancel' to abort ");


                    addDisplayErrorsToModel(model, request);
                    NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
                    if ((ne.isNewEntity()) || (ne.getName() == null)) {
                        AbstractEmployeeEntity emp = (AbstractEmployeeEntity) this.genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), empGarnishInfo.getParentId());
                        ne.setName(emp.getDisplayNameWivTitlePrefixed() + " [ " + emp.getEmployeeId() + " ] ");
                        ne.setId(emp.getId());
                    }

                    model.addAttribute("namedEntity", ne);
                    model.addAttribute("pageErrors", result);
                    model.addAttribute("empGarnishInfo", empGarnishInfo);
                    model.addAttribute("roleBean", bc);
                    return VIEW_NAME;
                }


                empGarnishInfo.setInterestAmount(0.0D);

                if (Boolean.valueOf(empGarnishInfo.getGovtLoanIndStr()).booleanValue())
                    empGarnishInfo.setGovtLoan(1);
                else {
                    empGarnishInfo.setGovtLoan(0);
                }

                empGarnishInfo.setLastModBy(new User(bc.getLoginId()));
                empGarnishInfo.setLastModTs(Timestamp.from(Instant.now()));
                empGarnishInfo.setCurrentLoanTerm(0);
                if (empGarnishInfo.isNewEntity()) {
                    empGarnishInfo.setCreatedBy(new User(bc.getLoginId()));
                    empGarnishInfo.setStartDate(LocalDate.now());
                    empGarnishInfo.setOriginalLoanAmount(empGarnishInfo.getOwedAmount());
                    empGarnishInfo.setAmount(setGarnishAmount(empGarnishInfo.getOwedAmount(), empGarnishInfo.getLoanTerm()));

                } else {
                    empGarnishInfo.setAmount(setGarnishAmount(empGarnishInfo.getOwedAmount(), empGarnishInfo.getNewLoanTerm()));
                    empGarnishInfo.setLoanTerm(empGarnishInfo.getNewLoanTerm());

                    empGarnishInfo.setOriginalLoanAmount(empGarnishInfo.getOwedAmount());

                }

                EmpGarnishmentType empGarnishmentType = genericService.loadObjectById(EmpGarnishmentType.class,empGarnishInfo.getEmpGarnishmentType().getId());
                empGarnishInfo.setDescription(empGarnishmentType.getDescription());
                //Now check if Garnishment Amount > OwedAmount..
                if (empGarnishInfo.getAmount() > empGarnishInfo.getOwedAmount()) {
                    result.rejectValue("", "Invalid.Value", "Monthly Garnish Amount can not be greater than Owed Amount.");
                    addDisplayErrorsToModel(model, request);
                    model.addAttribute("pageErrors", result);
                    model.addAttribute("empGarnishInfo", empGarnishInfo);
                    model.addAttribute("roleBean", bc);
                    return VIEW_NAME;
                }
                //--Now Set Loan Estimated End Date...
                empGarnishInfo.setEndDate(PayrollUtils.makeEndDate(empGarnishInfo.getStartDate(),empGarnishInfo.getLoanTerm()));
                LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
                if (_wCal != null) {
                    //This means we have a Pending Paycheck. Set for strictly RERUN
                    RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(
                            CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()),
                            CustomPredicate.procurePredicate("runYear", _wCal.getYear()),
                            this.getBusinessClientIdPredicate(request)));
                    wRPB.setNoOfLoans(wRPB.getNoOfLoans() + 1);
                    if (wRPB.isNewEntity()) {
                        wRPB.setRunMonth(_wCal.getMonthValue());
                        wRPB.setRunYear(_wCal.getYear());
                        wRPB.setRerunInd(1);
                        wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                    }
                    this.genericService.storeObject(wRPB);
                }
                if (empGarnishInfo.getNegativePayId() != null
                        && empGarnishInfo.getNegativePayId() > 0) {

                    NegativePayBean wNPB = genericService.loadObjectById(NegativePayBean.class, empGarnishInfo.getNegativePayId());
                    wNPB.setReductionAmount(wNPB.getReductionAmount() + (empGarnishInfo.getOldGarnishAmount() - empGarnishInfo.getAmount()));
                    this.genericService.storeObject(wNPB);
                }
                //-- Before we store. Check if this Employee has ever had this loan before...
                //-- If he/she has, then get the hold id and revive the loan....
                AbstractGarnishmentEntity wEGI = (AbstractGarnishmentEntity) this.genericService.loadObjectUsingRestriction(
                        IppmsUtils.getGarnishmentInfoClass(bc), Arrays.asList(CustomPredicate.procurePredicate("employee.id", empGarnishInfo.getParentId()),
                                CustomPredicate.procurePredicate("empGarnishmentType.id", empGarnishInfo.getEmpGarnishmentType().getId()),
                                this.getBusinessClientIdPredicate(request)));

                if (!wEGI.isNewEntity()) {
                    empGarnishInfo.setId(wEGI.getId());
                }
                empGarnishInfo.setBusinessClientId(bc.getBusinessClientInstId());
                this.genericService.saveObject(empGarnishInfo);
            }

        }

        return "redirect:dedGarnForm.do?eid=" + empGarnishInfo.getParentId() + "&pid=" + bc.getBusinessClientInstId()
                + "&oid=" + empGarnishInfo.getId() + "&tid=" + LOAN;
    }


    private double setGarnishAmount(double pLoanAmount, int pLoanTerm) {
        double wRetVal = 0.0D;

        if ((pLoanAmount == 0.0D) || (pLoanTerm == 0)) {
            return wRetVal;
        }
        wRetVal = pLoanAmount / new Double(pLoanTerm).doubleValue();

        wRetVal = Double.parseDouble(PayrollHRUtils.removeCommas(PayrollHRUtils.getDecimalFormat(true).format(wRetVal)));

        return wRetVal;
    }

}