/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.AllowanceRuleApproval;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;



@Controller
@RequestMapping({"/approveAllowanceRule.do"})
@SessionAttributes(types={AllowanceRuleApproval.class})
public class ApproveRejectAllowanceRuleController extends BaseController {

    private final PaycheckService paycheckService;
    private final String VIEW = "rules/allowanceRuleApprovalForm";

    @Autowired
    public ApproveRejectAllowanceRuleController(PaycheckService paycheckService){
        this.paycheckService = paycheckService;

    }

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(@RequestParam(value = "arid", required = false) Long pArid, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);

        if(pArid == null)
            return "redirect:viewPendingAllowanceRule.do";
        //Load this AllowanceRuleApproval...
        AllowanceRuleApproval wARA = this.genericService.loadObjectById(AllowanceRuleApproval.class, pArid);

        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wARA);

        return VIEW;

    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"arid","s"})
    public String setupForm(@RequestParam("arid") Long pArid,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);

        if(pSaved == 2){
            model.addAttribute(IConstants.SAVED_MSG, "Pay Group Allowance Rule Request Rejected Successfully.");
        }else{
            model.addAttribute(IConstants.SAVED_MSG, "Pay Group Allowance Rule Approved Successfully");
        }
        addRoleBeanToModel(model, request);
        if(Navigator.getInstance(getSessionId(request)).getFromClass() != null &&
                Navigator.getInstance(getSessionId(request)).getFromClass().isAssignableFrom(this.getClass())) {
            model.addAttribute("saved", true);
            Navigator.getInstance(getSessionId(request)).setFromClass(null);
        }
        model.addAttribute("miniBean", this.genericService.loadObjectById(AllowanceRuleApproval.class, pArid));

        return VIEW;

    }

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_approve", required=false) String approve,
                                @RequestParam(value="_reject", required=false) String reject,
                                @ModelAttribute("miniBean") AllowanceRuleApproval pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        String redirectUrl;

        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            //if(this.payrollService.getTotalNoOfActiveTransferApprovals() == 0)
            return Navigator.getInstance(getSessionId(request)).getFromForm();
            //return "redirect:viewPendingTransfers.do";
        }

        Navigator.getInstance(getSessionId(request)).setFromClass(this.getClass());

        if (isButtonTypeClick(request,REQUEST_PARAM_APPROVE)) {


            if(StringUtils.isEmpty(pEHB.getApprovalMemo())){
                pEHB.setApproval(true);
                pEHB.setApprovalStatusInd(2);
                result.rejectValue("", "warning", "Please enter an Approval Memo.");
                return makeModelAndReturnView(model,request,bc,pEHB,result);
            }

            //Here we need to Approve this Allowance Rule...
           pEHB.setApprovalStatusInd(ON);
           pEHB.setApprover(new User(bc.getLoginId()));
           pEHB.setApprovedDate(LocalDate.now());
           pEHB.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
            genericService.saveObject(pEHB);
           //Approve the Allowance Rule.
            pEHB.getAllowanceRuleMaster().setActiveInd(ON);
            genericService.saveObject(pEHB.getAllowanceRuleMaster());

            redirectUrl = "requestNotification.do?arid=" + genericService.storeObject(pEHB) + "&s=1&oc="+IConstants.ALLOWANCE_RULE_APPROVAL_URL_IND;
            NotificationService.storeNotification(bc, genericService, pEHB,redirectUrl, bc.getStaffTypeName()+" PayGroup Allowance Rule",ALLOWANCE_RULE_CODE);

            return "redirect:approveAllowanceRule.do?arid="+pEHB.getId()+ "&s=1";
        }

        if (isButtonTypeClick(request,REQUEST_PARAM_REJECT)) {

            //if we get here...check if Rejection Reason is set..
            if(StringUtils.isEmpty(pEHB.getRejectionReason())){
                pEHB.setRejection(true);
                pEHB.setApprovalStatusInd(2);
                result.rejectValue("", "warning", "Please enter a Rejection Reason.");
                return makeModelAndReturnView(model,request,bc,pEHB,result);
            }
            //Otherwise Reject it....
            pEHB.setApprovalStatusInd(2);
            pEHB.setApprovedDate(LocalDate.now());
            pEHB.setApprover(new User(bc.getLoginId()));
            pEHB.setApprovalMemo(pEHB.getRejectionReason());
            pEHB.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));

            this.genericService.saveObject(pEHB);
            pEHB.getAllowanceRuleMaster().setActiveInd(2);
            genericService.saveObject(pEHB.getAllowanceRuleMaster());
            redirectUrl = "requestNotification.do?arid=" + pEHB.getId()+ "&s=2&oc="+IConstants.ALLOWANCE_RULE_APPROVAL_URL_IND;

            NotificationService.storeNotification(bc, genericService, pEHB, redirectUrl, bc.getStaffTypeName()+" PayGroup Allowance Rule ", ALLOWANCE_RULE_CODE);

            return "redirect:approveAllowanceRule.do?arid="+pEHB.getId()+ "&s=2";
        }

        return "redirect:approveAllowanceRule.do?arid=" + pEHB.getId();
    }

     private String makeModelAndReturnView(Model model, HttpServletRequest request, BusinessCertificate bc, AllowanceRuleApproval pEHB, BindingResult result) {

        if (result != null) {
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
        }
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", pEHB);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }
}
