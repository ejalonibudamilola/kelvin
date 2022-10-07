/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.paygroup;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignMasterBean;
import com.osm.gnl.ippms.ogsg.engine.MassReassignStaffs;
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
@Controller
@RequestMapping({"/statusForMassReassign.do"})
public class MassReassignStatusController extends BaseController {

    private final String VIEW = "progress/progressBarForm";

    public MassReassignStatusController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        ProgressBean wPB = new ProgressBean();


        Object o = getSessionAttribute(request, "massReassign");
        if (o == null) {
            return CONFIG_HOME_URL;
        }

        MassReassignStaffs wCP = (MassReassignStaffs) o;
        wPB.setDisplayMessage("Mass Reassigning "+bc.getStaffTypeName());
        wPB.setPageTitle("Mass Reassigning  " + bc.getStaffTypeName());

        if (wCP.isFinished()) {
            removeSessionAttribute(request, "massReassign");
            MassReassignMasterBean pSBH = wCP.getMassReassignMasterBean();

            if (pSBH == null) {
                return CONFIG_HOME_URL;
            }

            return "redirect:massReassignResult.do?mid=" + pSBH.getId();
        }
        wPB.setCurrentCount(wCP.getCurrentRecord());
        wPB.setTotalElements(wCP.getTotalRecords());
        wPB.setPercentage(wCP.getPercentage());
        wPB.setTimeRemaining(wCP.getTimeToElapse());
        addRoleBeanToModel(model, request);
        model.addAttribute("progressBean", wPB);
        return VIEW;
    }


    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        Object o = getSessionAttribute(request, "massReassign");
        if (o != null) {
            removeSessionAttribute(request, "massReassign");
        }

        return "redirect:massReassign.do";


    }


}
