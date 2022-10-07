/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.StepIncrementApproval;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

@Controller
@RequestMapping({"/approveStepIncrement.do"})
@SessionAttributes(types={StepIncrementApproval.class})
public class ApproveRejectStepIncrementController extends BaseController {

    private final PaycheckService paycheckService;
    private final String VIEW_NAME = "approval/stepIncrementApprovalForm";

    @Autowired
    public ApproveRejectStepIncrementController(PaycheckService paycheckService) {
        this.paycheckService = paycheckService;
    }
    @RequestMapping(method={RequestMethod.GET}, params={"aid"})
    public String setupForm(@RequestParam("aid") Long pAid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        //Load this EmployeeApproval...
        StepIncrementApproval wTA = this.genericService.loadObjectUsingRestriction(StepIncrementApproval.class, Arrays.asList(
                CustomPredicate.procurePredicate("id", pAid), getBusinessClientIdPredicate(request)));


        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("payrollStatus", IConstants.ON)));
        if(!wPRB.isNewEntity()){
            String msg = "Payroll is currently being run by "+wPRB.getInitiator().getActualUserName()+ ". "+bc.getStaffTypeName()+" Approvals or Rejections can not be effected during a Payroll Run.";

            model.addAttribute("payrollRunning", true);

            model.addAttribute("payrollRunningMessage",msg);
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
        }
        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;

    }



    @RequestMapping(method={RequestMethod.GET}, params={"aid","s"})
    public String setupForm(@RequestParam("aid") Long pTaid,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        //Load this EmployeeApproval...
        StepIncrementApproval wTA = this.genericService.loadObjectUsingRestriction(StepIncrementApproval.class,
                Arrays.asList(getBusinessClientIdPredicate(request),
                        CustomPredicate.procurePredicate("id",  pTaid)));



            if(pSaved == 2){
                model.addAttribute(IConstants.SAVED_MSG, bc.getStaffTypeName()+" Step Increment Request Rejected Successfully.");
            }else {
                model.addAttribute(IConstants.SAVED_MSG, bc.getStaffTypeName() + " Step Increment Request Completed and Approved Successfully");
            }
        model.addAttribute("saved", true);
        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);

        return VIEW_NAME;

    }


    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_approve", required=false) String approve,
                                @RequestParam(value="_reject", required=false) String reject,
                                @ModelAttribute("miniBean") StepIncrementApproval pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        String redirectUrl;

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            PredicateBuilder predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
            predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));

            if(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, StepIncrementApproval.class) < 1)
                return REDIRECT_TO_DASHBOARD;
            return "redirect:viewStepIncrementApproval.do";
        }

         result = this.doValidation(result, bc);
         if(result.hasErrors()){
             return makeAndReturnModel(model,result,bc,pEHB);
         }


        if (isButtonTypeClick(request,REQUEST_PARAM_APPROVE)) {

            ConfigurationBean configurationBean = loadConfigurationBean(request);

            if(!configurationBean.isStepIncrementerCanApprove()){
                if(pEHB.getInitiator().getId().equals(bc.getLoginId())){
                    result.rejectValue("", "Invalid.True","You can not approve Step Increment you created.");
                    return makeAndReturnModel(model,result,bc,pEHB);
                }
            }
            pEHB.setEntityId(pEHB.getStepIncrementTracker().getEmployee().getId());
            pEHB.setEntityName(pEHB.getStepIncrementTracker().getEmployee().getDisplayName());
            pEHB.setEmployeeId(pEHB.getStepIncrementTracker().getEmployee().getEmployeeId());
            pEHB.getStepIncrementTracker().getEmployee().setSalaryInfo(pEHB.getSalaryInfo());
            pEHB.getStepIncrementTracker().getEmployee().setLastModBy(new User(bc.getLoginId()));
            pEHB.getStepIncrementTracker().getEmployee().setLastModTs(Timestamp.from(Instant.now()));
            genericService.storeObject(pEHB.getStepIncrementTracker().getEmployee());
            pEHB.getStepIncrementTracker().setYear(LocalDate.now().getYear());
            pEHB.getStepIncrementTracker().setNoOfTimes(pEHB.getStepIncrementTracker().getNoOfTimes() + 1);
            genericService.storeObject(pEHB.getStepIncrementTracker());

            pEHB.setApprovalStatusInd(ON);
            pEHB.setApprover(new User(bc.getLoginId()));
            pEHB.setApprovedDate(LocalDate.now());
            this.genericService.saveObject(pEHB);

            LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            if(_wCal != null){
                RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class,
                        Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                                CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", _wCal.getYear())));

                wRPB.setNoOfApprovedEmployees(wRPB.getNoOfApprovedEmployees() + 1 );
                if(wRPB.isNewEntity()){
                    wRPB.setRunMonth(_wCal.getMonthValue());
                    wRPB.setRunYear(_wCal.getYear());
                    wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                }
                wRPB.setRerunInd(IConstants.ON);
                this.genericService.saveObject(wRPB);
            }

            redirectUrl = "approveStepIncrement.do?aid=" + pEHB.getId() + "&s=1";

            //save object for notification
            NotificationService.storeNotification(bc, genericService, pEHB, "requestNotification.do?arid=" + pEHB.getId() + "&s=1&oc="+IConstants.STEP_INC_APPROVAL_URL_IND, "Step Increment",STEP_INC_APPROVAL_CODE);

            return "redirect:" + redirectUrl;
        }


        if (isButtonTypeClick(request,REQUEST_PARAM_REJECT)) {
            if(IppmsUtilsExt.pendingPaychecksExistsForEntity(genericService,bc,pEHB.getParentId())){
                result.rejectValue("", "Invalid.True","Pending Paychecks found for "+pEHB.getStepIncrementTracker().getEmployee().getDisplayName()+". Approve or Delete paycheck before Rejecting this "+bc.getStaffTypeName()+"'s Step Increment");
               return makeAndReturnModel(model,result,bc,pEHB);
            }
            pEHB.setEntityId(pEHB.getStepIncrementTracker().getEmployee().getId());
            pEHB.setEntityName(pEHB.getStepIncrementTracker().getEmployee().getDisplayName());
            pEHB.setEmployeeId(pEHB.getStepIncrementTracker().getEmployee().getEmployeeId());
            pEHB.setApprovalStatusInd(2);
            pEHB.setApprover(new User(bc.getLoginId()));
            pEHB.setApprovedDate(LocalDate.now());
            this.genericService.saveObject(pEHB);
            LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            if(_wCal != null){
                RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class,
                        Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                                CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", _wCal.getYear())));

                wRPB.setNoOfRejectedEmployees(wRPB.getNoOfRejectedEmployees() + 1);
                if(wRPB.isNewEntity()){
                    wRPB.setRunMonth(_wCal.getMonthValue());
                    wRPB.setRunYear(_wCal.getYear());
                    wRPB.setRerunInd(IConstants.ON);
                    wRPB.setBusinessClientId(bc.getBusinessClientInstId());

                }
                this.genericService.saveObject(wRPB);
            }
            redirectUrl = "approveStepIncrement.do?aid=" + pEHB.getId() + "&s=2";
             //save object for notification
            NotificationService.storeNotification(bc, genericService, pEHB, "requestNotification.do?arid=" + pEHB.getId() + "&s=2&oc="+IConstants.STEP_INC_APPROVAL_URL_IND, "Step Increment",STEP_INC_APPROVAL_CODE);


            return "redirect:"+redirectUrl;
        }


        return "redirect:approveStepIncrement.do?aid=" + pEHB.getId() + "&s=1";
    }

    private BindingResult doValidation(BindingResult result, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("payrollStatus", IConstants.OFF)));
        if(!wPRB.isNewEntity() && wPRB.isRunning())
            result.rejectValue("", "No.Employees", "Payroll is currently being run by "+wPRB.getInitiator().getActualUserName()+". Step Increment Approvals or Rejections can not be effected during a Payroll Run");


        return  result;
    }

    private String makeAndReturnModel(Model model,BindingResult result,BusinessCertificate bc,StepIncrementApproval pEHB){
        model.addAttribute(DISPLAY_ERRORS, BLOCK);
        model.addAttribute("status", result);
        model.addAttribute("roleBean", bc);
        model.addAttribute("miniBean",pEHB);
        return VIEW_NAME;
    }

}
