/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.ConfigurationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@Controller
@RequestMapping({"/editIppmsConfig.do"})
@SessionAttributes(types = {ConfigurationBean.class})
public class EditConfigBeanController extends BaseController {


    private final ConfigurationValidator configurationValidator;

    private final String VIEW_NAME = "configcontrol/configForm";
    @Autowired
    public EditConfigBeanController(ConfigurationValidator configurationValidator) {
        this.configurationValidator = configurationValidator;
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(@RequestParam(value = "bid", required = false) Long pDtid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        ConfigurationBean wP;
        if (pDtid == null) {
            wP = this.genericService.loadObjectWithSingleCondition(ConfigurationBean.class, CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

        } else {
            wP = this.genericService.loadObjectUsingRestriction(ConfigurationBean.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("id", pDtid)));
            model.addAttribute(IConstants.SAVED_MSG, " Configuration updated successfully.");
            model.addAttribute("saved", true);
        }

        wP.setEmpCreatorCanApproveBind(wP.isEmpCreatorCanApprove());
        wP.setStepIncrementerCanApproveBind(wP.isStepIncrementerCanApprove());
        wP.setPayGroupCreatorCanApproveBind(wP.isPayGroupCreatorCanApprove());
        wP.setReqNinBind(wP.isNinRequired());
        wP.setReqResIdBind(wP.isResidenceIdRequired());
        wP.setIamAliveBind(wP.isUseIAmAlive());
        wP.setShowBankInfoOnPaySlipBind(wP.isShowBankInfo());
        wP.setShowPiksoOnPaySlipBind(wP.isShowPikso());
        wP.setShowRetirementDatePaySlipBind(wP.isShowRetirementDate());
        wP.setUseOgsEmailBind(wP.isUseOgsEmail());
        wP.setReqBioBind(wP.isBioRequired());
        wP.setGlobalPercentModOpenBind(wP.isGlobalPercentModOpen());
        wP.setAmAliveCreatorCanApproveBind(wP.isAmAliveCreatorCanApprove());
        wP.setReqBvnBind((wP.isBvnRequired()));
        wP.setReqStaffEmailBind(wP.isStaffEmailRequired());

        model.addAttribute("roleBean", bc);
        model.addAttribute("configBean", wP);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("configBean") ConfigurationBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }

        configurationValidator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            model.addAttribute("roleBean", bc);
            model.addAttribute("configBean", pEHB);
            return VIEW_NAME;
        }
        pEHB.setEmpCreatorCanApproveInd(pEHB.isEmpCreatorCanApproveBind() ? ON : OFF);
        pEHB.setStepIncrementerCanApproveInd(pEHB.isStepIncrementerCanApproveBind() ? ON : OFF);
        pEHB.setRequireResidenceId(pEHB.isReqResIdBind() ? ON : OFF);
        pEHB.setRequireNin(pEHB.isReqNinBind() ? ON : OFF);
        pEHB.setIgnoreAmAliveInd(pEHB.isIamAliveBind() ? ON : OFF);
        pEHB.setShowBankInfoOnPaySlip(pEHB.isShowBankInfoOnPaySlipBind() ? ON : OFF);
        pEHB.setShowPiksoOnPaySlip(pEHB.isShowPiksoOnPaySlipBind() ? ON : OFF);
        pEHB.setShowRetirementDatePaySlip(pEHB.isShowRetirementDatePaySlipBind() ? ON : OFF);
        pEHB.setUseOgsEmailInd(pEHB.isUseOgsEmailBind() ? ON : OFF);
        pEHB.setReqBiometricInfoInd(pEHB.isReqBioBind() ? ON : OFF);
        pEHB.setGlobalPercentModInd(pEHB.isGlobalPercentModOpenBind() ? ON : OFF);
        pEHB.setAmAliveCreatorCanApproveInd(pEHB.isAmAliveCreatorCanApproveBind() ? ON : OFF);
        pEHB.setPayGroupCreatorCanApproveInd(pEHB.isPayGroupCreatorCanApproveBind() ? ON : OFF);
        pEHB.setRequireStaffEmailInd(pEHB.isReqStaffEmailBind() ? ON : OFF);

        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));

        Long pId = this.genericService.storeObject(pEHB);

        return "redirect:editIppmsConfig.do?bid=" + pId;
    }
}
