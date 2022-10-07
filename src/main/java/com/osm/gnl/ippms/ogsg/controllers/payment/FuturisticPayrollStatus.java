/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.payment;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.engine.CalculateFuturePayroll;
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
@RequestMapping({"/displayFtrStatus.do"})
public class FuturisticPayrollStatus extends BaseController {

    private final String VIEW = "progress/progressBarForm";

    public FuturisticPayrollStatus() {
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        ProgressBean wPB = new ProgressBean();


        Object o = getSessionAttribute(request, "myFutureCalcPay");
        if (o == null) {
            return REDIRECT_TO_DASHBOARD;
        }

        CalculateFuturePayroll wCP = (CalculateFuturePayroll) o;

        wPB.setDisplayMessage("Futuristic Payroll Run.....please wait!");
        wPB.setPageTitle("Futuristic PayrollRun for " + wCP.getPayPeriodStr());

        if (wCP.isFinished()) {
            Long wKey = wCP.getParentId();
            removeSessionAttribute(request, "myFutureCalcPay");
            return "redirect:showFuturePayrollResult.do?pid=" + wKey;

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

        Object o = getSessionAttribute(request, "myFutureCalcPay");
        if (o != null) {
            removeSessionAttribute(request, "myFutureCalcPay");
        }

        return "redirect:preFutureSimReportForm.do";


    }


}