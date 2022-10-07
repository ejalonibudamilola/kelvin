/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.fileupload;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.engine.*;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
//@RequestMapping({"/fileUploadSaveStatus.do"})
public class FileUploadSaveStatusController extends BaseController {

    private final String VIEW = "progress/progressBarForm";

    public FileUploadSaveStatusController() {
    }

    @RequestMapping({"/fileUploadSaveStatus.do"})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        Object objectType = getSessionAttribute(request, FILE_UPLOAD_OBJ_IND);
        ProgressBean wPB = new ProgressBean();

        Object objectClass = getSessionAttribute(request, FU_OBJ_KEY);
        if (objectType == null || objectClass == null) {
            if (getSessionAttribute(request, FILE_UPLOAD_UUID) == null)
                return "redirect:fileUploadFailed.do";
            return "redirect:fileUploadReport.do?uid=" + (getSessionAttribute(request, FILE_UPLOAD_UUID)) + "&s=1";

        }


        switch ((int) objectType) {
            case 2:
                wPB.setPageTitle("Loan File Upload");
                wPB.setDisplayMessage("Uploading Loans.....please wait!");
                PersistFileUploadLoans pful = (PersistFileUploadLoans) objectClass;
                if (pful.isFinished()) {
                    removeSessionAttribute(request, FU_OBJ_KEY);
                    return "redirect:fileUploadReport.do?uid=" + (getSessionAttribute(request, FILE_UPLOAD_UUID)) + "&s=" + objectType;
                } else {
                    wPB.setCurrentCount(pful.getCurrentRecord());
                    wPB.setTotalElements(pful.getTotalRecords());
                    wPB.setPercentage(pful.getPercentage());
                    wPB.setTimeRemaining(pful.getTimeToElapse());
                }
                break;
            case 3:
                wPB.setPageTitle("Deduction File Upload");
                wPB.setDisplayMessage("Uploading Deductions.....please wait!");
                PersistFileUploadDeductions pfud = (PersistFileUploadDeductions) objectClass;
                if (pfud.isFinished()) {
                    removeSessionAttribute(request, FU_OBJ_KEY);
                    return "redirect:fileUploadReport.do?uid=" + (getSessionAttribute(request, FILE_UPLOAD_UUID)) + "&s=" + objectType;
                } else {
                    wPB.setCurrentCount(pfud.getCurrentRecord());
                    wPB.setTotalElements(pfud.getTotalRecords());
                    wPB.setPercentage(pfud.getPercentage());
                    wPB.setTimeRemaining(pfud.getTimeToElapse());
                }
                break;
            case 5:
                wPB.setPageTitle("Special Allowance File Upload");
                wPB.setDisplayMessage("Uploading Special Allowances.....please wait!");
                PersistFileUploadSpecAllow pfusa = (PersistFileUploadSpecAllow) objectClass;
                if (pfusa.isFinished()) {
                    removeSessionAttribute(request, FU_OBJ_KEY);
                    return "redirect:fileUploadReport.do?uid=" + (getSessionAttribute(request, FILE_UPLOAD_UUID)) + "&s=" + objectType;
                } else {
                    wPB.setCurrentCount(pfusa.getCurrentRecord());
                    wPB.setTotalElements(pfusa.getTotalRecords());
                    wPB.setPercentage(pfusa.getPercentage());
                    wPB.setTimeRemaining(pfusa.getTimeToElapse());
                }
                break;
            case 6:
                wPB.setPageTitle("Salary Structure File Upload");
                wPB.setDisplayMessage("Uploading Salary Structure.......please wait!");
                PersistSalaryInfo pFUSS = (PersistSalaryInfo) objectClass;
                if (pFUSS.isFinished()){
                    removeSessionAttribute(request, FU_OBJ_KEY);
                    return "redirect:fileUploadReport.do?uid=" + (getSessionAttribute(request, FILE_UPLOAD_UUID)) + "&s=" +objectType;
                }
                else {
                    wPB.setCurrentCount(pFUSS.getCurrentRecord());
                    wPB.setTotalElements(pFUSS.getTotalRecords());
                    wPB.setPercentage(pFUSS.getPercentage());
                    wPB.setTimeRemaining(pFUSS.getTimeToElapse());
                }
                break;
            case 7:
                wPB.setPageTitle("Step Increment File Upload");
                wPB.setDisplayMessage("Performing Step Increments.......please wait!");
                PerformStepIncrement pSI = (PerformStepIncrement) objectClass;
                if (pSI.isFinished()){
                    removeSessionAttribute(request, FU_OBJ_KEY);
                    return "redirect:fileUploadReport.do?uid=" + (getSessionAttribute(request, FILE_UPLOAD_UUID)) + "&s=" +objectType;
                }
                else {
                    wPB.setCurrentCount(pSI.getCurrentRecord());
                    wPB.setTotalElements(pSI.getTotalRecords());
                    wPB.setPercentage(pSI.getPercentage());
                    wPB.setTimeRemaining(pSI.getTimeToElapse());
                }
                break;
        }

        addRoleBeanToModel(model, request);
        model.addAttribute("progressBean", wPB);
        return VIEW;
    }

    @RequestMapping({"/miniSalaryInfoStatus.do"})
    public String performMiniSalaryInfoStatus(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        ProgressBean wPB = new ProgressBean();

        Object o = getSessionAttribute(request, IConstants.MINI_SAL_KEY);
        if (o == null) {
            return CONFIG_HOME_URL;
        }

        PersistMiniSalaryInfo wCP = (PersistMiniSalaryInfo) o;

                wPB.setDisplayMessage("Creating Mini Salary Information's.....please wait!");
                wPB.setPageTitle("Mini Salary Info Processing");

            if (wCP.isFinished()) {

                return "redirect:createEditMiniSalary.do?maid=1" ;
            }
            wPB.setCurrentCount(wCP.getCurrentRecord());
            wPB.setTotalElements(wCP.getTotalRecords());
            wPB.setPercentage(wCP.getPercentage());
            wPB.setTimeRemaining(wCP.getTimeToElapse());


        addRoleBeanToModel(model, request);
        model.addAttribute("progressBean", wPB);
        return VIEW;
    }

}
