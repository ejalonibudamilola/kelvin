/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.customreports;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.customreports.utils.CustomReportGenHelper;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepGenBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping({"/viewCustomReportResults.do"})
@SessionAttributes({"miniBean"})
public class CustomReportResultViewController extends BaseController {

    private final String VIEW = "custom_report/customReportResultView";

    public CustomReportResultViewController() {}

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        CustomRepGenBean customRepGenBean = (CustomRepGenBean) getSessionAttribute(request,"_custRepGenBean");

        ReportGeneratorBean reportGeneratorBean = CustomReportGenHelper.makeReportGeneratorBean(customRepGenBean,getBusinessCertificate(request));

        addSessionAttribute(request, "_repGenBean",reportGeneratorBean);
        if(reportGeneratorBean.getTableData().size() > 0)
           reportGeneratorBean.setShowLink(true);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", reportGeneratorBean);


        return VIEW;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") ReportGeneratorBean reportGeneratorBean,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        if(!reportGeneratorBean.isCancelWarningIssued()){
            reportGeneratorBean.setCancelWarningIssued(true);
            addRoleBeanToModel(model, request);
            addDisplayErrorsToModel(model, request);
            result.rejectValue("","Warning", "Choosing to cancel will erase all data associated with this Custom Report.");
            model.addAttribute("status", result);
            model.addAttribute("miniBean", reportGeneratorBean);
            return  VIEW;
        }
        removeSessionAttribute(request,"_custRepGenBean");
        removeSessionAttribute(request,"_repGenBean");

        return "redirect:customReportGenerator.do";

    }

}
