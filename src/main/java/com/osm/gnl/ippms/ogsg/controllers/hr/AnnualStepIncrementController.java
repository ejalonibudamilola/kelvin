/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.base.services.TransferService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.StepIncrementApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncrementTracker;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.hr.PromotionValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;

@Controller
@RequestMapping({"/stepIncrement.do"})
@SessionAttributes(types = {HrMiniBean.class})
public class AnnualStepIncrementController extends BaseController {

    private final String VIEW_NAME = "promotion/stepIncrementForm";
    private final String ERROR_VIEW_NAME = "promotion/promoteEmployeeErrorForm";


    private final PromotionValidator promotionValidator;
    private final PromotionService promotionService;
    private final PaycheckService paycheckService;
    private final TransferService transferService;

    @Autowired
    public AnnualStepIncrementController( PromotionValidator promotionValidator,  PromotionService promotionService, PaycheckService paycheckService, TransferService transferService) {
        this.promotionValidator = promotionValidator;
        this.promotionService = promotionService;
        this.paycheckService = paycheckService;
        this.transferService = transferService;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {


        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (pEmpId <= 0) {
            return REDIRECT_TO_DASHBOARD;
        }
        HrMiniBean empHrBean = new HrMiniBean();
        empHrBean.setEmployeeInstId(pEmpId);
        HiringInfo wHI = loadHiringInfoByEmpId(request, bc, pEmpId);


        if ((wHI == null) || (wHI.isNewEntity())) {

            return makeModelAndReturnErrorView(empHrBean,wHI,bc,model,request);
        }
        if ((wHI.isNewEntity()) || (wHI.isTerminated())) {
             empHrBean.setDisplayTitle("This " + bc.getStaffTypeName() + " was terminated on " + PayrollHRUtils.getDisplayDateFormat().format(wHI.getTerminateDate()) + ". Step Increment denied!");
            return makeModelAndReturnErrorView(empHrBean,wHI,bc,model,request);
        }
        if (!wHI.getAbstractEmployeeEntity().isApprovedForPayrolling()) {
             empHrBean.setDisplayTitle("This " + bc.getStaffTypeName() + " is not Approved For Payroll . Step Increment denied!");
            return makeModelAndReturnErrorView(empHrBean,wHI,bc,model,request);
        }
        if (wHI.isSuspendedEmployee()) {
             empHrBean.setDisplayTitle("This " + bc.getStaffTypeName() + " is on Suspension . Step Increment denied!");
            return makeModelAndReturnErrorView(empHrBean,wHI,bc,model,request);
        }
        //--Check if this guy has been step incremented twice this year...
        StepIncrementTracker stepIncrementTracker = genericService.loadObjectUsingRestriction(StepIncrementTracker.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), wHI.getParentId())
                , CustomPredicate.procurePredicate("year", LocalDate.now().getYear())));

        if (!stepIncrementTracker.isNewEntity()) {
            //Means dude has been Step Incremented this year...
            if (stepIncrementTracker.getNoOfTimes() == 2) {
                 empHrBean.setDisplayTitle("Step Increment denied! " + bc.getStaffTypeName() + "'s Step has been incremented twice (2 Times) for " + stepIncrementTracker.getYear());
                return makeModelAndReturnErrorView(empHrBean,wHI,bc,model,request);
            }else if (stepIncrementTracker.getNoOfTimes() == 1){
                //Make sure the step increment is logged for Approval.
                empHrBean.setRunYear(stepIncrementTracker.getYear());
                empHrBean.setApprovalNeeded(true);
            }
        }
        //Also make sure we don't have pending Step Increment Approvals for this dude.

        SalaryInfo wSalaryInfo = genericService.loadObjectUsingRestriction(SalaryInfo.class, Arrays.asList(CustomPredicate.procurePredicate("salaryType.id", wHI.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getId()),
                CustomPredicate.procurePredicate("level", wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevel()), CustomPredicate.procurePredicate("step", wHI.getAbstractEmployeeEntity().getSalaryInfo().getStep() + 1)));

        if (wSalaryInfo.isNewEntity()) {
             empHrBean.setDisplayTitle("Step Increment denied! " + bc.getStaffTypeName() + " is currently At Bar.");
            return makeModelAndReturnErrorView(empHrBean,wHI,bc,model,request);
        }
        empHrBean.setShowForConfirm(HIDE_ROW);
        empHrBean.setShowArrearsRow(HIDE_ROW);
        empHrBean.setOldLevelAndStep(wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevelStepStr());
        empHrBean.setNewLevelAndStep(wSalaryInfo.getLevelAndStepAsStr());
        empHrBean.setOldSalaryStr(wHI.getAbstractEmployeeEntity().getSalaryInfo().getAnnualSalaryStr());
        empHrBean.setNewSalaryStr(wSalaryInfo.getAnnualSalaryStr());
        empHrBean.setEmployeeInstId(wHI.getAbstractEmployeeEntity().getId());
        empHrBean.setHiringInfoId(wHI.getId());
        empHrBean.setSalaryInfoInstId(wSalaryInfo.getId());
        empHrBean = setRequiredValues(wHI, empHrBean);

        empHrBean.setWarningIssued(false);
         return makeModelAndReturnView(empHrBean,model,null,bc,request);

    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid", "sid", "s"})
    public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("sid") Long pSid, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (pEmpId <= 0) {
            return REDIRECT_TO_DASHBOARD;
        }
        HrMiniBean empHrBean = new HrMiniBean();


        HiringInfo wHI = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmpId),
                getBusinessClientIdPredicate(request)));

        SalaryInfo wSalaryInfo = genericService.loadObjectUsingRestriction(SalaryInfo.class, Arrays.asList(CustomPredicate.procurePredicate("id", pSid), getBusinessClientIdPredicate(request)));
        String actionCompleted;
        if (pSaved == 1) {
            empHrBean.setName(wHI.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed());
            empHrBean.setOldLevelAndStep(wSalaryInfo.getLevelStepStr());
            empHrBean.setNewLevelAndStep(wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevelAndStepAsStr());
            empHrBean.setOldSalaryStr(wSalaryInfo.getAnnualSalaryStr());
            empHrBean.setNewSalaryStr(wHI.getAbstractEmployeeEntity().getSalaryInfo().getAnnualSalaryStr());
            actionCompleted = bc.getStaffTypeName() + " " + empHrBean.getName() + " Step Incremented Successfully.";
        } else {

            empHrBean.setOldLevelAndStep(wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevelStepStr());
            empHrBean.setNewLevelAndStep(wSalaryInfo.getLevelAndStepAsStr());
            empHrBean.setOldSalaryStr(wHI.getAbstractEmployeeEntity().getSalaryInfo().getAnnualSalaryStr());
            empHrBean.setNewSalaryStr(wSalaryInfo.getAnnualSalaryStr());
            actionCompleted = bc.getStaffTypeName() + " " + wHI.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed() + "'s Step Increment Scheduled for Approval Successfully.";
        }

        empHrBean.setShowForConfirm(HIDE_ROW);

        empHrBean.setEmployeeInstId(wHI.getAbstractEmployeeEntity().getId());
        empHrBean.setHiringInfoId(wHI.getId());
        empHrBean.setSalaryInfoInstId(wSalaryInfo.getId());
        empHrBean = setRequiredValues(wHI, empHrBean);

        empHrBean.setWarningIssued(false);
        empHrBean.setShowArrearsRow(HIDE_ROW);

        empHrBean.setShowForConfirm(HIDE_ROW);

        model.addAttribute(SAVED_MSG, actionCompleted);

        model.addAttribute("saved", Boolean.valueOf(true));
        return  this.makeModelAndReturnView(empHrBean,model,null,bc,request);
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("miniBean") HrMiniBean pEHB,
                                BindingResult result, SessionStatus status, org.springframework.ui.Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }
        //Check if Payroll is running...
        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", ON)));

        if (!wPRB.isNewEntity() && wPRB.isRunning()) {
            result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". Step Increment can not be effected during a Payroll Run");
            return makeModelAndReturnView(pEHB,model,result,bc,request);
        }
        //check for Pending paychecks...
        //First get appropriate Pay Period
        PayrollFlag payrollFlag = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);
        LocalDate localDate = PayrollBeanUtils.makeNextPayPeriodStart(payrollFlag.getApprovedMonthInd(), payrollFlag.getApprovedYearInd());
        if (IppmsUtilsExt.paycheckExistsForEmployee(genericService, bc, localDate.getMonthValue(), localDate.getYear(), pEHB.getEmployeeInstId())) {
            result.rejectValue("", "No.Employees", "Pending Paychecks exists for  " + pEHB.getName() + ". Step Increment can not be effected.");
            return makeModelAndReturnView(pEHB,model,result,bc,request);

        }
        promotionValidator.validateForStepIncrement(pEHB, result, bc);
        if (result.hasErrors()) {
            return makeModelAndReturnView(pEHB,model,result,bc,request);

        }
         if(pEHB.isApprovalNeeded() && !pEHB.isStepWarningIssued()){
             if(!pEHB.isStepWarningIssued()) {
                 pEHB.setStepWarningIssued(true);
                 result.rejectValue("", "warning", bc.getStaffTypeName() + " " + pEHB.getName() + ", has had Step Increment done in " + pEHB.getRunYear());
                 result.rejectValue("", "warning", "This Step Increment needs Approval. It will be logged for Approval.");
                 return makeModelAndReturnView(pEHB, model, result, bc, request);
             }

         }
        if (!pEHB.isWarningIssued()) {
            result.rejectValue("", "warning", "Please confirm this Step Increment");
            pEHB.setShowForConfirm(IConstants.SHOW_ROW);
            pEHB.setWarningIssued(true);

            pEHB.setRefDate(LocalDate.now());
            pEHB.setRefNumber(bc.getUserName());
            return makeModelAndReturnView(pEHB,model,result,bc,request);

        }
        Navigator.getInstance(getSessionId(request)).setFromClass(this.getClass());
        StepIncrementTracker stepIncrementTracker = genericService.loadObjectUsingRestriction(StepIncrementTracker.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),
                pEHB.getEmployeeInstId()), CustomPredicate.procurePredicate("year", LocalDate.now().getYear())));

        if (stepIncrementTracker.getNoOfTimes() == 1) {
            //--Needs Approval...
            StepIncrementApproval stepIncrementApproval = new StepIncrementApproval();
            stepIncrementApproval.setBusinessClientId(bc.getBusinessClientInstId());
            stepIncrementApproval.setParentId(pEHB.getEmployeeInstId());
            stepIncrementApproval.setEntityId(pEHB.getEmployeeInstId());
            stepIncrementApproval.setEntityName(pEHB.getName());
            stepIncrementApproval.setEmployeeId(pEHB.getEmployeeId());
            stepIncrementApproval.setLastModTs(LocalDate.now());
            stepIncrementApproval.setInitiatedDate(LocalDate.now());
            stepIncrementApproval.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(bc.getLoginId()));
            stepIncrementApproval.setInitiator(new User(bc.getLoginId()));
            stepIncrementApproval.setStepIncrementTracker(stepIncrementTracker);
            //stepIncrementApproval.setApprovedDate(LocalDate.now());
            stepIncrementApproval.setSalaryInfo(new SalaryInfo(pEHB.getSalaryInfoInstId()));
            stepIncrementApproval.setOldSalaryInfo(new SalaryInfo(pEHB.getOldSalaryInfoInstId()));
            genericService.saveObject(stepIncrementApproval);
            NotificationService.storeNotification(bc,genericService,stepIncrementApproval,"requestNotification.do?arid=" + stepIncrementApproval.getId() + "&s=1&oc="+IConstants.STEP_INC_INIT_URL_IND,"Step Increment Request",STEP_INC_APPROVAL_CODE);
            return "redirect:stepIncrement.do?eid=" + pEHB.getEmployeeInstId() + "&sid=" + pEHB.getSalaryInfoInstId() + "&s=2";

        } else {
            stepIncrementTracker = new StepIncrementTracker();
            stepIncrementTracker.setEmployee(new Employee(pEHB.getEmployeeInstId()));

            stepIncrementTracker.setNoOfTimes(stepIncrementTracker.getNoOfTimes() + 1);
            stepIncrementTracker.setBusinessClientId(bc.getBusinessClientInstId());
            stepIncrementTracker.setYear(LocalDate.now().getYear());
            genericService.storeObject(stepIncrementTracker);
        }
        Employee employee = genericService.loadObjectById(Employee.class, pEHB.getEmployeeInstId());
        employee.setSalaryInfo(new SalaryInfo(pEHB.getSalaryInfoInstId()));

        this.genericService.storeObject(employee);

        AbstractPromotionAuditEntity wPA = IppmsUtils.makePromotionAuditObject(bc);
        wPA.setEmployee(new Employee(pEHB.getEmployeeInstId()));
        wPA.setOldSalaryInfo(new SalaryInfo(pEHB.getOldSalaryInfoInstId()));
        wPA.setUser(new User(bc.getLoginId()));
        wPA.setLastModTs(LocalDate.now());
        wPA.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
        wPA.setSalaryInfo(new SalaryInfo(pEHB.getSalaryInfoInstId()));
        wPA.setMdaInfo(new MdaInfo(pEHB.getMdaInstId()));//This is actually the MDA_INST_ID now. Please note..
        wPA.setPromotionDate(LocalDate.now());
        wPA.setUser(new User(bc.getLoginId()));
        wPA.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, bc));
        wPA.setBusinessClientId(bc.getBusinessClientInstId());
        if (pEHB.isSchoolAssigned()) {
            wPA.setSchoolInfo(new SchoolInfo(pEHB.getSchoolInstId()));
        }
        wPA.setStepIncrementInd(ON);
        this.genericService.storeObject(wPA);


        return "redirect:stepIncrement.do?eid=" + pEHB.getEmployeeInstId() + "&sid=" + pEHB.getSalaryInfoInstId() + "&s=1";
    }

    private HrMiniBean setRequiredValues(HiringInfo pWhi, HrMiniBean pEmpHrBean) {
        pEmpHrBean.setName(pWhi.getAbstractEmployeeEntity().getDisplayName());

        pEmpHrBean.setGradeLevelAndStep(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getName());

        pEmpHrBean.setFirstName(pWhi.getAbstractEmployeeEntity().getFirstName());
        pEmpHrBean.setLastName(pWhi.getAbstractEmployeeEntity().getLastName());
        pEmpHrBean.setMiddleName(StringUtils.trimToEmpty(pWhi.getAbstractEmployeeEntity().getInitials()));

        pEmpHrBean.setHireDate(PayrollHRUtils.getFullDateFormat().format(pWhi.getHireDate()));

        int noOfYears = LocalDate.now().getYear() - pWhi.getHireDate().getYear();
        pEmpHrBean.setYearsOfService(String.valueOf(noOfYears));
        pEmpHrBean.setAssignedToObject(pWhi.getAbstractEmployeeEntity().getCurrentMdaName());
        pEmpHrBean.setEmployeeId(pWhi.getAbstractEmployeeEntity().getEmployeeId());
        pEmpHrBean.setSalaryScale(pWhi.getAbstractEmployeeEntity().getSalaryScale());
        pEmpHrBean.setId(pWhi.getId());
        pEmpHrBean.setMdaInstId(pWhi.getAbstractEmployeeEntity().getMdaDeptMap().getMdaInfo().getId());

        pEmpHrBean.setOldSalaryInfoInstId(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getId());

        return pEmpHrBean;
    }

    private String makeModelAndReturnView(HrMiniBean pEHB,Model model,BindingResult result, BusinessCertificate bc, HttpServletRequest request){
       if(result != null) {
           if (result.hasErrors()) {
               addDisplayErrorsToModel(model, request);
               model.addAttribute("status", result);
           }
       }
        addRoleBeanToModel(model,request);
        model.addAttribute("miniBean", pEHB);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }
    private String makeModelAndReturnErrorView(HrMiniBean empHrBean, HiringInfo hiringInfo,BusinessCertificate bc,Model model,HttpServletRequest request) throws InstantiationException, IllegalAccessException {
        if(hiringInfo == null || hiringInfo.isNewEntity()){
            AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService, empHrBean.getEmployeeInstId(), bc);
            empHrBean.setName(wEmp.getDisplayName());
            empHrBean.setDisplayTitle("This " + bc.getStaffTypeName() + " has no Hiring Information. " + bc.getStaffTypeName() + ". Step Increment Denied");
         }else{
            empHrBean.setName(hiringInfo.getAbstractEmployeeEntity().getDisplayName());
         }

         addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", empHrBean);
        return ERROR_VIEW_NAME;
    }
}
