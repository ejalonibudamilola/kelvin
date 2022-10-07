/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.payment;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.engine.SimulatePayrollWithLtg;
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
@RequestMapping({"/displayLtgStatus.do"})
public class SimulationLtgPayrollStatus extends BaseController {

    private final String VIEW = "progress/progressBarForm";

    public SimulationLtgPayrollStatus() {
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        ProgressBean wPB = new ProgressBean();


        Object o = getSessionAttribute(request, "simCalcPay");
        if (o == null) {
            return REDIRECT_TO_DASHBOARD;
        }
            SimulatePayrollWithLtg wCP = (SimulatePayrollWithLtg)o;
            wPB.setDisplayMessage("Simulating Payroll With Leave Transport Grant!");
            wPB.setPageTitle("L.T.G for "+wCP.getPayPeriodStr());

            if (wCP.isFinished()) {
                removeSessionAttribute(request, "simCalcPay");
                Object p = getSessionAttribute(request, "currLtgId");
                if (p == null)
                {
                    return REDIRECT_TO_DASHBOARD;
                }
                removeSessionAttribute(request, "currLtgId");
                return "redirect:viewSimulatedPayroll.do?lid=" + p;
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

        Object o = getSessionAttribute(request, "simCalcPay");
        if (o != null) {
            removeSessionAttribute(request,"simCalcPay");
        }


        removeSessionAttribute(request, LTG_ATTR_NAME);
        return REDIRECT_TO_DASHBOARD;



    }


}