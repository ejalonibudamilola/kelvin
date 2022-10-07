/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.biometrics;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.engine.VerifyAllBiometrics;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping({"/displayBioVerificationStatus.do"})
public class VerifyBiometricsStatusController extends BaseController {

    private final String VIEW = "progress/progressBarForm";
    private Long mdaId;

    public VerifyBiometricsStatusController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        ProgressBean wPB = new ProgressBean();


        Object o = getSessionAttribute(request, "myVerifier");
        if (o == null) {
            return "redirect:biometricVerified.do?pid=" + bc.getBusinessClientInstId();
        }
        if (o.getClass().isAssignableFrom(VerifyAllBiometrics.class)) {
            VerifyAllBiometrics wCP = (VerifyAllBiometrics) o;

                wPB.setDisplayMessage("Verifying Biometrics.....");
                wPB.setPageTitle(bc.getStaffTypeName()+" Biometrics Verification");

            if (wCP.isFinished()) {
                    mdaId = (Long) getSessionAttribute(request,"bv_mda");

                     removeSessionAttribute(request, "myVerifier");
                     return "redirect:biometricVerified.do?pid=" + bc.getBusinessClientInstId()+"&mid="+mdaId;

             }
            wPB.setCurrentCount(wCP.getCurrentRecord());
            wPB.setTotalElements(wCP.getTotalRecords());
            wPB.setPercentage(wCP.getPercentage());
            wPB.setTimeRemaining(wCP.getTimeToElapse());

        }

        addRoleBeanToModel(model, request);
        model.addAttribute("progressBean", wPB);
        return VIEW;
    }


    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        Object o = getSessionAttribute(request, "myVerifier");
        if (o != null) {
            removeSessionAttribute(request,"myVerifier");
        }

        Thread.sleep(50L);
        return REDIRECT_TO_DASHBOARD;


    }


}
