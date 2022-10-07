/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.customreports;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.CustomReportService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.customreports.utils.CustomReportFinalizer;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepGenBean;
import com.osm.gnl.ippms.ogsg.validators.customreport.CustomReportValidator;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping({"/finalizeCustomReport.do"})
@SessionAttributes({"custRepBean"})
public class CustomReportFinalizeController extends BaseController {


    private final CustomReportValidator validator;
    private final CustomReportService customReportService;
    private final String VIEW = "custom_report/customReport";


    @Autowired
    public CustomReportFinalizeController(CustomReportValidator validator, CustomReportService customReportService) {
        this.validator = validator;
        this.customReportService = customReportService;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

       CustomRepGenBean customRepGenBean = (CustomRepGenBean) getSessionAttribute(request,"_custRepGenBean");


        addRoleBeanToModel(model, request);
        model.addAttribute("custRepBean", customRepGenBean);

        return VIEW;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("custRepBean") CustomRepGenBean customRepGenBean,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
           if(!customRepGenBean.isCancelWarningIssued()){
               customRepGenBean.setCancelWarningIssued(true);
               addRoleBeanToModel(model, request);
               addDisplayErrorsToModel(model, request);
               result.rejectValue("fileName","Warning", "Choosing to cancel will erase all data associated with this Custom Report.");
               model.addAttribute("status", result);
               model.addAttribute("custRepBean", customRepGenBean);
               return  VIEW;
           }

            return "redirect:customReportGenerator.do";
        }
        validator.validate(customRepGenBean,result);
        if(result.hasErrors()){
            addRoleBeanToModel(model, request);
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("custRepBean", customRepGenBean);
            return  VIEW;
        }
        //Finalize Report.
        customRepGenBean = CustomReportFinalizer.processReport(customRepGenBean,customReportService, getBusinessCertificate(request),genericService);
        addSessionAttribute(request,"_custRepGenBean", customRepGenBean);
        return "redirect:viewCustomReportResults.do";

    }




}
