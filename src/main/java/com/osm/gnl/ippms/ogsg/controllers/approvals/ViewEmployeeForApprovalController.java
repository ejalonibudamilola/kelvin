/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.HistoryService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.EmployeeApproval;
import com.osm.gnl.ippms.ogsg.domain.beans.PaginatedEpmBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping({"/viewEmpForPayApproval.do"})
@SessionAttributes(types = {PaginatedEpmBean.class})
public class ViewEmployeeForApprovalController extends BaseController {

    private final String VIEW_NAME = "employee/empForPayrollApprovalForm";
    private final int pageLength = 50;

    private final PaycheckService paycheckService;
    private final HistoryService historyService;
    private final IMenuService menuService;

    @Autowired
    public ViewEmployeeForApprovalController(PaycheckService paycheckService, HistoryService historyService, IMenuService menuService) {
        this.paycheckService = paycheckService;
        this.historyService = historyService;
        this.menuService = menuService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(@RequestParam(name = "s", required = false) Integer pSaved,
                            @RequestParam(name = "us", required = false) Integer pUnSaved,
                            @RequestParam(name = "r", required = false) Integer pRejected, Model model, HttpServletRequest pRequest) throws Exception {
        SessionManagerService.manageSession(pRequest, model);

        PaginationBean paginationBean = this.getPaginationInfo(pRequest);
        String url = "approveEmployeeForPayroll.do?aid=";
        BusinessCertificate bc = this.getBusinessCertificate(pRequest);
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
        predicateBuilder.addPredicate(getBusinessClientIdPredicate(pRequest));
        int wNoOfElem = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmployeeApproval.class);
        if (wNoOfElem == 0)
            return REDIRECT_TO_DASHBOARD;
        PaginatedEpmBean wPB;
        List<EmployeeApproval> wEmpApprList = this.historyService.loadPendingEmpApprovals(bc,  (paginationBean.getPageNumber() - 1) * pageLength,
                pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());


        if (wEmpApprList.size() == 0 && wNoOfElem > 0){
            url = "/paymentInfoForm.do?oid=";
            if(bc.isPensioner()){
                url = "/penPaymentInfoForm.do?oid=";
            }

            wEmpApprList = this.historyService.loadSkeletalPendingEmpApprovals(bc,(paginationBean.getPageNumber() - 1) * pageLength,
                    pageLength, url);
            wPB = new PaginatedEpmBean(wEmpApprList, paginationBean.getPageNumber(), pageLength, wNoOfElem, paginationBean.getSortCriterion(),
                    paginationBean.getSortOrder());
            wPB.setCanCreatePaymentMethod(this.menuService.canUserAccessURL(bc,url,url));

             wPB.setCaptchaError(true);
            if(wPB.isCanCreatePaymentMethod())
                bc.setTargetUrl(url);

        }else{
            wPB = new PaginatedEpmBean(wEmpApprList, paginationBean.getPageNumber(), pageLength, wNoOfElem, paginationBean.getSortCriterion(),
                    paginationBean.getSortOrder());
            bc.setTargetUrl(url);
        }

        if (IppmsUtils.isNotNullAndGreaterThanZero(pSaved)) {
            String approvalMessage = "Approvals Done for " + pSaved + " Pending Approvals Successfully.";
            if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved))
                approvalMessage += " " + pUnSaved + " Approvals not processed. You created the " + bc.getStaffTypeName() + "(s)";

            addSaveMsgToModel(pRequest, model, approvalMessage);

        } else if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved)) {
            addSaveMsgToModel(pRequest, model, " " + pUnSaved + " Approvals not processed. You created the " + bc.getStaffTypeName() + "(s)");
        }
        else if (IppmsUtils.isNotNullAndGreaterThanZero(pRejected)){
            addSaveMsgToModel(pRequest, model, " " + pUnSaved + " Approvals not processed. You Rejected the " + bc.getStaffTypeName() + "(s)");
        }
        if (requestsAreSelected(pRequest)) {
            setSelectedRequests(getSelectedRequests(pRequest), wEmpApprList);
        }
        model.addAttribute("miniBean", wPB);
        model.addAttribute("roleBean", bc);

        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @RequestParam(value = "_submit", required = false) String ok, @RequestParam(value = "_confirm", required = false) String confirm,
                                @ModelAttribute("miniBean") PaginatedEpmBean pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws InstantiationException, IllegalAccessException, HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            super.removeSessionAttribute(request, SELECTED_IDS_KEY); //per adventure it has been selected....
        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
            return REDIRECT_TO_DASHBOARD;
        }

        ConfigurationBean configurationBean = loadConfigurationBean(request);
        if (!configurationBean.isEmpCreatorCanApprove()) {
            int found = 0;
            for (EmployeeApproval eA : (List<EmployeeApproval>) pEHB.getList()) {

                if (eA.getInitiatorId().equals(bc.getLoginId()) && Boolean.valueOf(eA.getRowSelected())) {
                    eA.setRowSelected("false");
                    found++;
                }
            }
            if(found > 0){
                result.rejectValue("approvalMemo", "Warning", found+" "+bc.getStaffTypeName()+" Were created by you. Creators of "+bc.getStaffTypeName()+" Are restricted from Approving them.");
                result.rejectValue("approvalMemo", "Warning", found+" "+bc.getStaffTypeName()+" Have been deselected automatically for you.");
                model.addAttribute("status",result);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {

            if(pEHB.isCaptchaError()){
                result.rejectValue("approvalMemo", "Warning",  bc.getStaffTypeName() + " Can not be Approved. Payment Information is missing");
                if(pEHB.isCanCreatePaymentMethod())
                    result.rejectValue("approvalMemo", "Warning",  " Click on the "+bc.getStaffTitle()+" To Add Payment Information.");

                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }

            if (!pEHB.isAddWarningIssued()) {

                pEHB.setAddWarningIssued(true);
                pEHB.setApprovalInd(ON);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Approve Selected " + bc.getStaffTypeName() + ". Please enter a value for 'Approval Memo' and Confirm.");
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }

        }

        if (isButtonTypeClick(request, REQUEST_PARAM_REJECT)) {

            if (!pEHB.isAddWarningIssued()) {
                pEHB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Reject Selected " + bc.getStaffTypeName() + ". Please enter a value for 'Rejection Memo' and Confirm.");
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }

        }


        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {
            if (IppmsUtils.isNullOrEmpty(pEHB.getApprovalMemo()) || StringUtils.trimToEmpty(pEHB.getApprovalMemo()).trim().length() < 8) {
                pEHB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Approval Memo'");
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            } else {
                if (pEHB.getApprovalInd() == IConstants.ON) {
                    Integer rejected = 0;
                    Integer unprocessed = 0;
                    Integer processed = 0;
                    for (EmployeeApproval employeeApproval : (List<EmployeeApproval>) pEHB.getObjectList()) {

                        if (!Boolean.valueOf(employeeApproval.getRowSelected())) {
                            ++unprocessed;
                            continue;
                        }
                        processed += approveStaff(employeeApproval, bc, pEHB.getApprovalMemo());
                    }
                    if (processed > 0) {
                        LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
                        if (_wCal != null) {
                            RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService, bc.getBusinessClientInstId(), _wCal);

                            if (wRPB.isNewEntity()) {
                                wRPB.setRunMonth(_wCal.getMonthValue());
                                wRPB.setRunYear(_wCal.getYear());
                                wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                            }
                            wRPB.setNoOfApprovedEmployees(wRPB.getNoOfApprovedEmployees() + processed);
                            wRPB.setRerunInd(IConstants.ON);
                            this.genericService.saveObject(wRPB);
                        }

                    }
                    super.removeSessionAttribute(request, SELECTED_IDS_KEY);
                    return "redirect:viewEmpForPayApproval.do?s=" + processed + "&us=" + unprocessed+"&r="+rejected;
                }
                else {
                    Integer unprocessed = 0;
                    Integer processed = 0;
                    Integer rejected =0;
                    for (EmployeeApproval employeeApproval : (List<EmployeeApproval>) pEHB.getObjectList()) {

                        if (!Boolean.valueOf(employeeApproval.getRowSelected())) {
                            ++unprocessed;
                            continue;
                        }
                        rejected += rejectStaff(employeeApproval, bc, pEHB.getApprovalMemo());
                    }
                    return "redirect:viewEmpForPayApproval.do?s=" + processed + "&us=" + unprocessed + "&r="+rejected;
                }
            }
        }

        super.removeSessionAttribute(request, SELECTED_IDS_KEY);
        return REDIRECT_TO_DASHBOARD;
    }

    private boolean requestsAreSelected(HttpServletRequest request) {
        return !getSelectedRequests(request).isEmpty();
    }

    @RequestMapping(method = RequestMethod.POST, params = {"checkboxState", "reqList"})
    public void handleCheckboxSelectionState(
            @RequestParam("checkboxState") boolean isChecked,
            @RequestParam("reqList") List<Long> reqIdList,
            HttpServletRequest request) {
        if (reqIdList != null && reqIdList.size() > 0) {
            Set<Long> selectedIds = getSelectedRequests(request);

            if (isChecked) {
                selectedIds.addAll(reqIdList);
            } else {
                selectedIds.removeAll(reqIdList);
            }
        }
    }

    private Set<Long> getSelectedRequests(HttpServletRequest request) {
        //Check for the collection of selected ID's
        Set<Long> selectedIds = (Set<Long>) request.getSession().getAttribute(SELECTED_IDS_KEY);
        if (selectedIds == null) {

            selectedIds = new HashSet<>();

            //store it in the session
            request.getSession().setAttribute(SELECTED_IDS_KEY, selectedIds);

        }
        return selectedIds;
    }

    private void setSelectedRequests(Set<Long> selectedReqIds, List<EmployeeApproval> currReqApprs) {
        if (selectedReqIds != null && selectedReqIds.size() > 0
                && currReqApprs != null && currReqApprs.size() > 0) {
            for (EmployeeApproval reqAppr : currReqApprs) {
                if (selectedReqIds.contains(reqAppr.getId())) {
                    reqAppr.setRowSelected(String.valueOf(true));
                }
            }
        }
    }

    private Integer rejectStaff(EmployeeApproval employeeApproval, BusinessCertificate bc, String approvalMemo) throws InstantiationException, IllegalAccessException {
        //employeeApproval.setEntityId(employeeApproval.getChildObject().getId());
        employeeApproval.setEntityName(employeeApproval.getDisplayName());
        employeeApproval.setEmployeeId(employeeApproval.getEmployeeId());
        employeeApproval.setApprovalStatusInd(2);
        employeeApproval.setApprover(new User(bc.getLoginId()));
        employeeApproval.setApprovedDate(LocalDate.now());
        employeeApproval.setApprovalMemo(approvalMemo);
        employeeApproval.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        employeeApproval.setLastModTs(LocalDate.now());
        employeeApproval.setBusinessClientId(bc.getBusinessClientInstId());
        this.genericService.saveObject(employeeApproval);

        NotificationService.storeNotification(bc,genericService,employeeApproval,"approveEmployeeForPayroll.do?aid=" + employeeApproval.getId() + "&s=1"," Approval For Payroll", IConstants.STAFF_APPROVAL_CODE);

        return 1;
    }


    private Integer approveStaff(EmployeeApproval employeeApproval, BusinessCertificate bc, String typeName) throws IllegalAccessException, InstantiationException {
        //employeeApproval.setEntityId(employeeApproval.getChildObject().getId());
        employeeApproval.setEntityName(employeeApproval.getDisplayName());
        employeeApproval.setEmployeeId(employeeApproval.getEmployeeId());

        employeeApproval.setApprovalStatusInd(ON);
        employeeApproval.setApprover(new User(bc.getLoginId()));
        employeeApproval.setApprovedDate(LocalDate.now());
        employeeApproval.setApprovalMemo(typeName);
        employeeApproval.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        employeeApproval.setLastModTs(LocalDate.now());
        this.historyService.saveApproval(employeeApproval, bc);


        NotificationService.storeNotification(bc,genericService,employeeApproval,"approveEmployeeForPayroll.do?aid=" + employeeApproval.getId() + "&s=1"," Approval For Payroll", STAFF_APPROVAL_CODE);


        return 1;
    }


}
