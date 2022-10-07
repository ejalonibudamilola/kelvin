/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.AmAliveApproval;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
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
@RequestMapping({"/approveAmAliveForPayroll.do"})
@SessionAttributes(types={AmAliveApproval.class})
public class ApproveRejectAmAliveController extends BaseController {


    private final PaycheckService paycheckService;

    private final String VIEW_NAME = "approval/employeeApprovalForm";
    @Autowired
    public ApproveRejectAmAliveController(PaycheckService paycheckService) {
        this.paycheckService = paycheckService;
    }

    @RequestMapping(method={RequestMethod.GET}, params={"aid"})
    public String setupForm(@RequestParam("aid") Long pAid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        //Load this EmployeeApproval...
        AmAliveApproval wTA = this.genericService.loadObjectUsingRestriction(AmAliveApproval.class, Arrays.asList(
                CustomPredicate.procurePredicate("id", pAid), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));


        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("payrollStatus", IConstants.ON)));
        if(!wPRB.isNewEntity()){
            String msg = "Payroll is currently being run by "+wPRB.getInitiator().getActualUserName()+ ". "+bc.getStaffTypeName()+" Am Alive Approvals or Rejections can not be effected during a Payroll Run.";
            if(bc.isPensioner()){
                if(wPRB.isGratuityRunning())
                    msg = "Gratuity Information is currently being run by "+wPRB.getInitiator().getActualUserName()+ ". "+bc.getStaffTypeName()+" Am Alive Approvals or Rejections can not be effected during a Payroll Run.";
            }
            model.addAttribute("payrollRunning", true);

            model.addAttribute("payrollRunningMessage",msg);
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
        }
       return prepareAndReturView(model,bc,wTA, null,false);
     }



    @RequestMapping(method={RequestMethod.GET}, params={"aid","s"})
    public String setupForm(@RequestParam("aid") Long pTaid,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        //Load this EmployeeApproval...
        AmAliveApproval wTA = this.genericService.loadObjectUsingRestriction(AmAliveApproval.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("id",  pTaid)));

        if(pSaved == 2){
            model.addAttribute(IConstants.SAVED_MSG, bc.getStaffTypeName()+" Am Alive Request Rejected Successfully.");
        }else{
            model.addAttribute(IConstants.SAVED_MSG, bc.getStaffTypeName()+" Am Alive Request Completed and Approved Successfully");
        }

        model.addAttribute("saved", true);

        return prepareAndReturView(model,bc,wTA, null,false);
    }


    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_approve", required=false) String approve,
                                @RequestParam(value="_reject", required=false) String reject,
                                @ModelAttribute("miniBean") AmAliveApproval pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
             if(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, AmAliveApproval.class) < 1)
                return REDIRECT_TO_DASHBOARD;
            return "redirect:viewApproveRejectAmAlive.do";
        }

        if (isButtonTypeClick(request,REQUEST_PARAM_CONFIRM)) {
             //Check if the Captchas are correct...
            if(!pEHB.getEnteredCaptcha().equalsIgnoreCase(pEHB.getGeneratedCaptcha())){
                result.rejectValue("", "confirmation", "Entered Captcha does not match Generated Captcha");
                return prepareAndReturView(model,bc,pEHB, result, true);
            }
            return this.approveRejectAmAlive(pEHB,bc);

        }
        if (isButtonTypeClick(request,REQUEST_PARAM_APPROVE)) {

            ConfigurationBean configurationBean = loadConfigurationBean(request);

            if(!bc.isSuperAdmin() || !configurationBean.isAmAliveCreatorCanApprove()){
                if(pEHB.getInitiator().getId().equals(bc.getLoginId())){
                    result.rejectValue("", "Invalid.True","You can not approve 'Am Alive' Requests for approval you initiated.");

                    return prepareAndReturView(model,bc,pEHB, result,false);
                }
            }
            //before Approving, treatHiringInfo....
            if(!AmAliveHelperService.treatHiringInfo(pEHB,bc,genericService)){
                result.rejectValue("", "Invalid.True","'Am Alive' Approval Failed. Please contact your IPPMS Systems Administrator.");

                return prepareAndReturView(model,bc,pEHB, result, false);
            }

                result.rejectValue("", "confirmation", "Please confirm this 'Am Alive' Approval");
                pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
                pEHB.setConfirmed(true);
                pEHB.setApprovalStatusInd(ON);
                return prepareAndReturView(model,bc,pEHB, result, true);


        }


        if (isButtonTypeClick(request,REQUEST_PARAM_REJECT)) {

            result.rejectValue("", "confirmation", "Please confirm this 'Am Alive' Rejection");
            pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
            pEHB.setConfirmed(true);
            pEHB.setApprovalStatusInd(2);
            return prepareAndReturView(model,bc,pEHB, result, true);
        }
        return "redirect:approveAmAliveForPayroll.do?aid=" + pEHB.getId() + "&s="+pEHB.getApprovalStatusInd();
    }

    private String approveRejectAmAlive(AmAliveApproval pEHB, BusinessCertificate bc){

        pEHB.setApprover(new User(bc.getLoginId()));
        pEHB.setApprovedDate(LocalDate.now());
        this.genericService.saveObject(pEHB);

        return "redirect:approveAmAliveForPayroll.do?aid=" + pEHB.getId() + "&s="+pEHB.getApprovalStatusInd();
    }

    private String prepareAndReturView(Model model, BusinessCertificate bc, AmAliveApproval wTA, BindingResult result, boolean approveOrReject) {
        addPageTitle(model,bc.getStaffTypeName()+" Am Alive Approval Form");
        addMainHeader(model,"Am Alive Approval for "+bc.getStaffTypeName()+ " "+wTA.getHiringInfo().getPensioner().getDisplayName()+" [ "+wTA.getHiringInfo().getPensioner().getEmployeeId()+" ]");
        addTableHeader(model,wTA.getHiringInfo().getPensioner().getDisplayName()+"'s Information");
        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);
        if(result != null) {
            if(!approveOrReject){
                model.addAttribute(DISPLAY_ERRORS, BLOCK);

            }else{
                ((AmAliveApproval)result).setDisplayErrors(BLOCK);
            }
            model.addAttribute("status", result);

        }



        return VIEW_NAME;
    }

}
