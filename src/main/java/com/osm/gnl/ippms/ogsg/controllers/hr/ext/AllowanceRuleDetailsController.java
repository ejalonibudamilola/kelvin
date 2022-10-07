/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.AllowanceRuleApproval;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;


@Controller
@RequestMapping({"/allowanceRuleDetails.do"})
@SessionAttributes(types={AllowanceRuleApproval.class})
public class AllowanceRuleDetailsController extends BaseController {

    private final String VIEW = "rules/allowanceRuleDetailsForm";

    public AllowanceRuleDetailsController( )
    {

    }
    @RequestMapping(method={RequestMethod.GET}, params={"arid"})
    public String setupForm(@RequestParam("arid") Long pTaid, Model model, HttpServletRequest request) throws Exception {


        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        //Load this TransferApproval...
        AllowanceRuleApproval wTA = genericService.loadObjectById(AllowanceRuleApproval.class, pTaid);


        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);
        return VIEW;

    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"arid","s"})
    public String setupForm(@RequestParam("arid") Long pTaid,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        //Load this TransferApproval...
        AllowanceRuleApproval wTA = genericService.loadObjectById(AllowanceRuleApproval.class, pTaid);


        model.addAttribute(IConstants.SAVED_MSG, "PayGroup Allowance Rule Request Deleted Successfully.");


        model.addAttribute("saved", true);
        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);

        return VIEW;

    }

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_delete", required=false) String delete, @ModelAttribute("miniBean") AllowanceRuleApproval pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);



        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(getBusinessClientIdPredicate(request));
            predicateBuilder.addPredicate(Arrays.asList(CustomPredicate.procurePredicate("approvalStatusInd",2), CustomPredicate.procurePredicate("initiator.id",bc.getLoginId())));
            if(genericService.countObjectsUsingPredicateBuilder(predicateBuilder, AllowanceRuleApproval.class) == 0)
                return Navigator.getInstance(getSessionId(request)).getFromForm();

            return "redirect:viewRejectedAllowanceRules.do";
        }
        //Otherwise Delete it....
        pEHB.setApprovalStatusInd(3);
        pEHB.setDeleteDate(LocalDate.now());
        pEHB.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));

        this.genericService.saveObject(pEHB);

        return "redirect:allowanceRuleDetails.do?arid=" + pEHB.getId() + "&s=2";


    }



}
