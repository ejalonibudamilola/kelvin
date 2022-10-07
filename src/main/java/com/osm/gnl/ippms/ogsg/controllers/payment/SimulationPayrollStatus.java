/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.payment;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.engine.SimulatePayroll;
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
@RequestMapping({"/displaySimStatus.do"})
public class SimulationPayrollStatus extends BaseController {

    private final String VIEW = "progress/progressBarForm";

    public SimulationPayrollStatus() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        ProgressBean wPB = new ProgressBean();


        Object o = getSessionAttribute(request, "SimulatePayroll");
        if (o == null) {
            return REDIRECT_TO_DASHBOARD;
        }

        SimulatePayroll wCP = (SimulatePayroll) o;
        wPB.setDisplayMessage("Simulating Payroll!");
        wPB.setPageTitle("Payroll Simulation for " + wCP.getPayPeriodStr());

        if (wCP.isFinished()) {
            removeSessionAttribute(request, "SimulatePayroll");
            SimulationBeanHolder pSBH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);

            if (pSBH == null) {
                return REDIRECT_TO_DASHBOARD;
            }
            Long wObjId = pSBH.getPayrollSimulationMasterBean().getId();
            return "redirect:viewSimulatedPayrollResult.do?pid=" + wObjId;
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

        Object o = getSessionAttribute(request, "SimulatePayroll");
        if (o != null) {
            removeSessionAttribute(request, "SimulatePayroll");
        }


        return "redirect:prePayrollSimulator.do";


    }


}