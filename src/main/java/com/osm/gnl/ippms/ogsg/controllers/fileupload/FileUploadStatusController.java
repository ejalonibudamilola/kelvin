/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.fileupload;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.engine.ParseExcelAndSchedulePayment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping({"/uploadStatus.do"})
public class FileUploadStatusController extends BaseController {


    private final String VIEW = "fileupload/fileUploadProgressForm";

    public FileUploadStatusController() {}

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request);
        ProgressBean wPB = new ProgressBean();

        Object o = getSessionAttribute(request, "fileUpload");

        if (o == null) {
            return "redirect:determineDashBoard.do";
        }
        ParseExcelAndSchedulePayment wCP = (ParseExcelAndSchedulePayment) o;

        if (wCP.isFinished()) {

            FileParseBean wPFB = wCP.getFileParseBean();
            if (wPFB == null ||  wPFB.getUniqueUploadId() == null )
                return "redirect:fileUploadFailed.do";

            addSessionAttribute(request, wPFB.getUniqueUploadId(), wPFB);
            removeSessionAttribute(request, "fileUpload");
            return "redirect:fileUploadReport.do?uid=" + wPFB.getUniqueUploadId();
        }
        addRoleBeanToModel(model, request);
        model.addAttribute("progressBean", wPB);
        return VIEW;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request);


        Object o = getSessionAttribute(request, "fileUpload");
        if (o == null) {
            return "redirect:determineDashBoard.do";
        }

        ParseExcelAndSchedulePayment wCP;

        try {
            if (o.getClass().isAssignableFrom(ParseExcelAndSchedulePayment.class)) {
                wCP = (ParseExcelAndSchedulePayment) o;
                wCP.setStop(true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        removeSessionAttribute(request, "fileUpload");


        Thread.sleep(200L);

        return "redirect:determineDashBoard.do";
    }
}