/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.paygroup;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.SalaryService;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignMasterBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.hr.MassReassignValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Controller
@RequestMapping({"/massReassignResult.do"})
//@SessionAttributes(types={MassReassignMasterBean.class})
public class AutoReassignResultController extends BaseController {

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private MassReassignValidator massReassignValidator;

    private final String VIEW_NAME = "configcontrol/massReassignResultForm";


    public AutoReassignResultController() {
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"mid"})
    public String setupForm(@RequestParam("mid") Long pMid, Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        MassReassignMasterBean massReassignMasterBeans = this.genericService.loadObjectUsingRestriction(MassReassignMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("approvalStatus",OFF), CustomPredicate.procurePredicate("id", pMid)));
        double sumFrom = 0.00D;
        double sumTo = 0.00D;
        for(MassReassignDetailsBean mr: massReassignMasterBeans.getMassReassignDetailsBeans()){
            sumFrom+=mr.getFromSalaryInfo().getMonthlySalary();
            sumTo+=mr.getToSalaryInfo().getMonthlySalary();
        }

        double netDiff = sumTo-sumFrom;

        massReassignMasterBeans.setSumFrom(PayrollHRUtils.getDecimalFormat().format(sumFrom));
        massReassignMasterBeans.setSumTo(PayrollHRUtils.getDecimalFormat().format(sumTo));
        massReassignMasterBeans.setNetDiff(PayrollHRUtils.getDecimalFormat().format(netDiff));

        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", massReassignMasterBeans);
        model.addAttribute("approve", IConstants.OFF);

        return VIEW_NAME;
    }


    @RequestMapping(method = {RequestMethod.POST })
    public String processSubmit(@RequestParam(value = "_close", required = false) String close,
                                @RequestParam(value = "_cancel", required = false) String cancel,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);


        if (isButtonTypeClick(request, REQUEST_PARAM_CLOSE)) {
            return "redirect:massReassign.do";
        }
        isButtonTypeClick(request, REQUEST_PARAM_CANCEL);

        return "redirect:approveMassReassign.do";
    }


}
