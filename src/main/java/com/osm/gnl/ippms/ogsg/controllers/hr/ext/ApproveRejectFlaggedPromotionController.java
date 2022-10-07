/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.promotion.FlaggedPromotions;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.engine.PromotionHelperService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@Controller
@RequestMapping({"/approveFlaggedPromotion.do"})
@SessionAttributes(types={FlaggedPromotions.class})
public class ApproveRejectFlaggedPromotionController extends BaseController {

    private final PaycheckService paycheckService;
    private final PromotionService promotionService;
    private final String VIEW = "promotion/approveFlaggedPromotionForm";

    @Autowired
    public ApproveRejectFlaggedPromotionController(PaycheckService paycheckService, PromotionService promotionService){
        this.paycheckService = paycheckService;
        this.promotionService = promotionService;
    }

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"aid"})
    public String setupForm(@RequestParam("aid") Long pTaid, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        //Load this TransferApproval...
        FlaggedPromotions wTA = this.genericService.loadObjectById(FlaggedPromotions.class, pTaid);

        if(bc.isLocalGovt())
            if(!wTA.getNewRank().getId().equals(wTA.getEmployee().getRank().getId()))
                wTA.setShowRankRow(true);



        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);
        return VIEW;

    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"fpid","s"})
    public String setupForm(@RequestParam("fpid") Long pTaid,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        //Load this TransferApproval...
        FlaggedPromotions wTA = this.genericService.loadObjectById(FlaggedPromotions.class, pTaid);
        if(bc.isLocalGovt())
            if(!wTA.getNewRank().getId().equals(wTA.getEmployee().getRank().getId()))
                wTA.setShowRankRow(true);


        if(pSaved == 2){
            model.addAttribute(IConstants.SAVED_MSG, "Promotion Request Rejected Successfully.");
        }else{
            model.addAttribute(IConstants.SAVED_MSG, "Promotion Completed and Approved Successfully");
        }

        model.addAttribute("saved", true);
        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);

        return VIEW;

    }


    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_approve", required=false) String approve,
                                @RequestParam(value="_reject", required=false) String reject,
                                @ModelAttribute("miniBean") FlaggedPromotions pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);



        if (super.isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            //if(this.payrollService.getTotalNoOfActiveTransferApprovals() == 0)
            return Navigator.getInstance(super.getSessionId(request)).getFromForm();
            //return "redirect:viewPendingTransfers.do";
        }



        if (super.isButtonTypeClick(request,REQUEST_PARAM_APPROVE)) {

            //Here we need to Approve this Transfer...
            if(pEHB.getInitiator().getId().equals(bc.getLoginId())){
                result.rejectValue("", "warning", "You can not Approve a Flagged Promotion you initiated.");
                addDisplayErrorsToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", bc);
                return VIEW;
            }
            Long schoolId = pEHB.getEmployee().isSchoolStaff() ? pEHB.getEmployee().getSchoolInfo().getId() : null;
            PromotionTracker wPT = this.genericService.loadObjectUsingRestriction(PromotionTracker.class,Arrays.asList(CustomPredicate.procurePredicate("employee.id", pEHB.getEmployee().getId()),
                    getBusinessClientIdPredicate(request)));
            PromotionHelperService.applyPromotion(bc,pEHB.getEmployee().getId(),pEHB.getHireInfoId(),pEHB.getToSalaryInfo().getLevel(),promotionService,paycheckService,pEHB.getNewRank().getId(),genericService,pEHB.getFromSalaryInfo().getId()
            ,pEHB.getToSalaryInfo().getId(),pEHB.getMdaInfo().getId(),schoolId,wPT,pEHB.getArrears(),pEHB.getRefNumber(),pEHB.getRefDate());

            pEHB.setApprover(new User(bc.getLoginId()));
            pEHB.setTreatedDate(Timestamp.from(Instant.now()));
            pEHB.setStatusInd(ON);
            pEHB.setLastModTs(Timestamp.from(Instant.now()));
            this.genericService.storeObject(pEHB);

            return "redirect:approveFlaggedPromotion.do?fpid=" + pEHB.getId() + "&s=1";
        }

        if (super.isButtonTypeClick(request,REQUEST_PARAM_REJECT)) {

            //if we get here...check if Rejection Reason is set..
            if(StringUtils.isEmpty(pEHB.getRejectionReason())){
                pEHB.setRejection(true);
                pEHB.setStatusInd(2);
                result.rejectValue("", "warning", "Please enter a Rejection Reason.");
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", bc);
                return VIEW;
            }
            //Otherwise Reject it....
            pEHB.setStatusInd(2);
            pEHB.setTreatedDate(Timestamp.from(Instant.now()));
            pEHB.setLastModTs(Timestamp.from(Instant.now()));
            pEHB.setApprover(new User(bc.getLoginId()));
            pEHB.setRejectionReason("Rejected.");
            this.genericService.storeObject(pEHB);

            return "redirect:approveFlaggedPromotion.do?fpid=" + pEHB.getId() + "&s=2";
        }
        return "redirect:approveFlaggedPromotion.do?fpid=" + pEHB.getId() + "&s=1";
    }


}
