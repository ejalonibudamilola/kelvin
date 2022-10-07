/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.customreports;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class CustomReportResultReportController extends BaseController {

    public CustomReportResultReportController() {
    }
    @RequestMapping(value = {"/genCustomRepXlsOrPdf.do"},params = {"rt"})
    public void generateTransferLogExcel(@RequestParam("rt") int  pReportTypeInd, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        ReportGeneratorBean rt = (ReportGeneratorBean)getSessionAttribute(request,"_repGenBean");

        if(pReportTypeInd == 1){

            new PdfSimpleGeneratorClass().getPdf(response,request,rt);

        }else{
          //  CustomExcelReportGenerator wCP = new CustomExcelReportGenerator(rt,response,request,rt.getTableData().size());
           // wCP.getExcelDoc();
            SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();
            simpleExcelReportGenerator.getExcel(response,request,rt);
        }



    }
}
