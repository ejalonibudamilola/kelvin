/*
 * Copyright (c) 2022. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping({"/SchoolPayrollAnalysisByMdaExcel.do"})
public class DetailAnalysisSchoolReportController extends BaseController {


   
    private final PayrollService payrollService;

    @Autowired
    public DetailAnalysisSchoolReportController(PayrollService payrollService) {
       
        this.payrollService = payrollService;
    }
    @RequestMapping(method={RequestMethod.GET}, params={"rm","ry","mid","rt"})
    public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,@RequestParam("mid") Long pMdaId, @RequestParam("rt") int reportType, Model model, HttpServletRequest request,
                         HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
       SessionManagerService.manageSession(request, model);

       SchoolReportHelper.prepareReport(payrollService, getBusinessCertificate(request), pRunMonth, pRunYear, reportType, request, 0, 0, null, response);
   }

    @RequestMapping(method={RequestMethod.GET}, params={"rm","ry","rt","sGL","eGL","bank"})
    public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("rt") int reportType, @RequestParam(value = "sGL") int fromLevel,
                          @RequestParam(value = "eGL") int toLevel, @RequestParam(value = "bank") String bank, Model model, HttpServletRequest request,
                          HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);

        SchoolReportHelper.prepareReport(payrollService, getBusinessCertificate(request), pRunMonth, pRunYear, reportType, request, fromLevel, toLevel, bank, response);


    }

}
