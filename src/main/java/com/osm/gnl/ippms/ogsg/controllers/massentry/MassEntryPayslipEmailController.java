/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.massentry;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.Mailer;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.services.MailerService;
import com.osm.gnl.ippms.ogsg.base.services.MassEntryService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GeneratePayslipFile;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.massentry.PromotionHistory;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping({"/massEmailPayslips.do"})
@SessionAttributes(types = {PaginatedPaycheckGarnDedBeanHolder.class})
public class MassEntryPayslipEmailController extends BaseController{

    private final int pageLength = 10;
    private final String VIEW = "massentry/massEntryPayslipEmailForm";

    @Autowired
    private MassEntryService massEntryService;
    @Autowired
    private PaycheckService paycheckService;
    @Autowired
    private MailerService mailerService;
    @Autowired
    private GeneratePayslipFile generatePayslipFile;


    public MassEntryPayslipEmailController() {
    }

    @ModelAttribute("monthList")
    public Collection<NamedEntity> getMonthList() {
        return PayrollBeanUtils.makeAllMonthList();
    }

    @ModelAttribute("yearList")
    public Collection<NamedEntity> makeYearList(HttpServletRequest request) {

        return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME);

        if ((wPGBDH != null) && (!wPGBDH.isNewEntity())) {
            removeSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME);
        }

        List<PromotionHistory> wDeductInfoList = new ArrayList<>();
        List<Long> wEmpIdList = new ArrayList<Long>();
        BaseController.PaginationBean paginationBean = getPaginationInfo(request);
        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wDeductInfoList, paginationBean.getPageNumber(), pageLength, 0, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setEmployeeIdList(wEmpIdList);
        wPGBDH.setPaginationListHolder(wDeductInfoList);
        wPGBDH.setRunMonth(-1);
        addSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME, wPGBDH);
        wPGBDH.setDedGarnOrSpecAllow(false);
        wPGBDH.setActiveInd(ON);
        addRoleBeanToModel(model, request);
        model.addAttribute("massEmailPayslipBean", wPGBDH);
        return VIEW;

    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        BaseController.PaginationBean paginationBean = this.getPaginationInfo(request);


        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME);


        List<PromotionHistory> wNewList;
        List<Long> wEmpIdList = new ArrayList<Long>();


        List<PromotionHistory> wAllList = new ArrayList<>();

        for (PromotionHistory p : (List<PromotionHistory>) wPGBDH.getPaginationListHolder()) {
            if (p.getEmployee().getId().equals(pEmpId)) {
                continue;
            }
            wAllList.add(p);
            wEmpIdList.add(p.getEmployee().getId());
        }


        int pageNumber = paginationBean.getPageNumber();

        if (wAllList.size() > this.pageLength) {
            Double wDouble = new Double(wAllList.size()) / new Double(this.pageLength);
            pageNumber = Integer.parseInt(String.valueOf(wDouble).substring(0, String.valueOf(wDouble).indexOf(".")));
        }
        //Now Paginate.
        wNewList = (List<PromotionHistory>) PayrollUtils.paginateList(pageNumber, this.pageLength, wAllList);

        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, pageNumber, pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        wPGBDH.setEmployeeIdList(wEmpIdList);
        wPGBDH.setBeanList(wNewList);
        wPGBDH.setEmptyList(wNewList.size() > 0);
        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setRunMonth(-1);
        wPGBDH.setPaginationListHolder(wAllList);
        addSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME, wPGBDH);
        addRoleBeanToModel(model, request);
        model.addAttribute("massEmailPayslipBean", wPGBDH);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"lid", "act"})
    public String setupForm(@RequestParam("lid") Long pLoginId, @RequestParam("act") String pSaved, Model model, HttpServletRequest request) throws Exception {


        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME);

        List<PromotionHistory> wAllList = (List<PromotionHistory>) wPGBDH.getPaginationListHolder();
        if (wAllList == null)
            wAllList = new ArrayList<>();

        PaginationBean paginationBean = getPaginationInfo(request);
        List<PromotionHistory> wNewList = (List<PromotionHistory>) PayrollUtils.paginateList(paginationBean.getPageNumber(), this.pageLength, wAllList);
        List<Long> wEmpIdList = wPGBDH.getEmployeeIdList();


        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, paginationBean.getPageNumber(), pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPGBDH.setEmptyList(wNewList.size() > 0);
        wPGBDH.setId(pLoginId);
        wPGBDH.setEmployeeIdList(wEmpIdList);
        wPGBDH.setPaginationListHolder(wAllList);
        addSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME, wPGBDH);
        addRoleBeanToModel(model, request);
        model.addAttribute("massEmailPayslipBean", wPGBDH);
        return VIEW;

    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_search", required = false) String search, @RequestParam(value = "_sendEmail", required = false) String addDeduction,
                                @ModelAttribute("massEmailPayslipBean") PaginatedPaycheckGarnDedBeanHolder pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            removeSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME);
            return "redirect:massEntryMainDashboard.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_SEARCH)) {
            String staffId = pEHB.getStaffId().toUpperCase();

            AbstractEmployeeEntity wEmp = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc),
                    Arrays.asList(getBusinessClientIdPredicate(request),
                            CustomPredicate.procurePredicate("employeeId", staffId)));
            if (wEmp.isNewEntity()) {
                result.rejectValue("", "Global.Change", "No "+bc.getStaffTypeName()+" Found with "+bc.getStaffTitle()+" '" + staffId + "'");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massEmailPayslipBean", pEHB);
                return VIEW;
            }else{
                //Check if this Staff has Email Address
                if(IppmsUtils.isNullOrEmpty(wEmp.getEmail())){
                    result.rejectValue("", "Global.Change", " "+wEmp.getDisplayNameWivTitlePrefixed()+" Has no valid email address.");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massEmailPayslipBean", pEHB);
                    return VIEW;
                }else if(!BaseValidator.isEmailValid(wEmp.getEmail())){
                    result.rejectValue("", "Global.Change", ""+wEmp.getDisplayNameWivTitlePrefixed()+"'s Has no valid email address"+wEmp.getEmail()+" is invalid by IPPMS Standards.");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massEmailPayslipBean", pEHB);
                    return VIEW;
                }

            }

            for (Object e : pEHB.getPaginationListHolder()) {
                if (((PromotionHistory) e).getEmployee().getId().equals(wEmp.getId())) {
                    result.rejectValue("", "Global.Change", bc.getStaffTypeName()+" '" + wEmp.getDisplayName() + "' is already added");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massEmailPayslipBean", pEHB);
                    return VIEW;
                }
            }

            pEHB = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME);

            List<PromotionHistory> wPromoHist = (List<PromotionHistory>) pEHB.getPaginationListHolder();


            List<Long> wEmpIds = pEHB.getEmployeeIdList();

            if (wPromoHist == null) {
                wPromoHist = new ArrayList<>();
            }
            if (wEmpIds == null) {
                wEmpIds = new ArrayList<>();
            }

            PromotionHistory wPromHistBean = new PromotionHistory();
            wPromHistBean.setEntryIndex(wPromoHist.size() + 1);
            wPromHistBean.setEmployee(wEmp);
            wPromHistBean.setId(wEmp.getId());
            wPromoHist.add(wPromHistBean);
            wEmpIds.add(wEmp.getId());

            pEHB.setPaginationListHolder(wPromoHist);
            pEHB.setEmployeeIdList(wEmpIds);


            addSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME, pEHB);

            return "redirect:massEmailPayslips.do?lid=" + bc.getLoginId() + "&act=y";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_SEND_EMAIL)) {
            if (pEHB.getRunMonth() == -1 || pEHB.getRunYear() == 0) {
                result.rejectValue("", "Global.Change", "Please select Month and Year");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massEmailPayslipBean", pEHB);

                return VIEW;
            }



            PaginatedPaycheckGarnDedBeanHolder wPEHB = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME);

            if ((wPEHB == null) || (wPEHB.getList().isEmpty())) {
                result.rejectValue("", "Global.Change", "Please add "+bc.getStaffTypeName()+" to Email Payslips");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massEmailPayslipBean", pEHB);

                return VIEW;
            }

            List<PromotionHistory> wSuccessList = new ArrayList<>();

            wPEHB.setName("Email "+bc.getStaffTypeName()+" Payslips");
            wPEHB.setTypeInd(7);
            addSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME, wPEHB);

            List<PromotionHistory> wPromoHist = (List<PromotionHistory>) wPEHB.getPaginationListHolder();

            List<PromotionHistory> wIntegerList = new ArrayList<>();

            int wAddendum = 0;
            Mailer mailer = null;
            for (PromotionHistory p : wPromoHist) {

                mailer = new Mailer();
                mailer.setId(p.getEmployee().getId());
                mailer.setRecipient(p.getEmployee().getEmail());
                mailer.setRunMonth(wPEHB.getRunMonth());
                mailer.setRunYear(wPEHB.getRunYear());
                mailer.setBusinessCertificate(bc);
                mailer.setHttpServletRequest(request);
                if(!mailerService.sendMailWithAttachments(mailer))
                   wIntegerList.add(p);
                else
                    wSuccessList.add(p);


            }

            pEHB.setSuccessList(wSuccessList);
            addSessionAttribute(request, MASS_PAYSLIP_EMAIL_NAME, pEHB);

        }

        return "redirect:displayMassEntryResult.do?lid=" + bc.getLoginId() + "&tn=" + MASS_PAYSLIP_EMAIL_NAME;
    }


}
