/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.HistoryService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.PaginatedEpmBean;
import com.osm.gnl.ippms.ogsg.domain.approval.AmAliveApproval;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping({"/viewApproveRejectAmAlive.do"})
@SessionAttributes(types = {PaginatedEpmBean.class})
public class ViewAmAliveApprovalController extends BaseController {

    private final String VIEW_NAME = "approval/penForAmAliveApprovalForm";
    private final String READONLY_VIEW = "approval/penAmAliveResultForm";
    private final int pageLength = 50;

    private final HistoryService historyService;
    @Autowired
    public ViewAmAliveApprovalController( final HistoryService historyService) {
         this.historyService = historyService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(@RequestParam(name = "s", required = false) Integer pSaved,
                            @RequestParam(name = "us", required = false) Integer pUnSaved,
                            @RequestParam(name = "r", required = false) Integer pRejected, Model model, HttpServletRequest pRequest) throws Exception {
        SessionManagerService.manageSession(pRequest, model);

        PaginationBean paginationBean = this.getPaginationInfo(pRequest);

        BusinessCertificate bc = this.getBusinessCertificate(pRequest);

        int wNoOfElem = AmAliveHelperService.noOfPendingAmAliveApprovals(bc,genericService);
        if (wNoOfElem == 0)
            return REDIRECT_TO_DASHBOARD;
        List<AmAliveApproval> wEmpApprList = this.historyService.loadPendingAmAliveApprovals(bc,  (paginationBean.getPageNumber() - 1) * pageLength,
                pageLength, null);

        PaginatedEpmBean wPB = new PaginatedEpmBean(wEmpApprList, paginationBean.getPageNumber(), pageLength, wNoOfElem, paginationBean.getSortCriterion(),
                paginationBean.getSortOrder());
        if (wEmpApprList.size() > 0)
            wPB.setShowLink(true);
        if (IppmsUtils.isNotNullAndGreaterThanZero(pSaved)) {
            String approvalMessage = "Approvals Done for " + pSaved + " Pending 'I Am Alive' Approvals Successfully.";
            if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved))
                approvalMessage += " " + pUnSaved + " 'I Am Alive' Approvals not processed. You initiated the 'I Am Alive' requests(s)";

            addSaveMsgToModel(pRequest, model, approvalMessage);

        }
        else if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved)) {
            addSaveMsgToModel(pRequest, model, " " + pUnSaved + " 'I Am Alive' Approvals not processed. You initiated the 'I Am Alive' request(s)");
        }
        else if (IppmsUtils.isNotNullAndGreaterThanZero(pRejected)){
            addSaveMsgToModel(pRequest, model, " " + pUnSaved + " 'I Am Alive' Approvals not processed. You Rejected the " + bc.getStaffTypeName() + "(s)");
        }
        if (requestsAreSelected(pRequest)) {
            setSelectedRequests(getSelectedRequests(pRequest), wEmpApprList);
        }
        model.addAttribute("miniBean", wPB);
        model.addAttribute("roleBean", bc);

        return READONLY_VIEW;
    }
    @RequestMapping(method = {RequestMethod.GET}, params = {"tid"})
    public String setupForm(@RequestParam(name = "tid") Long pTicketId, Model model, HttpServletRequest pRequest) throws Exception {
        SessionManagerService.manageSession(pRequest, model);

        PaginationBean paginationBean = this.getPaginationInfo(pRequest);

        BusinessCertificate bc = this.getBusinessCertificate(pRequest);

        int wNoOfElem = AmAliveHelperService.noOfPendingAmAliveApprovals(bc,genericService,pTicketId);
        if (wNoOfElem == 0)
            return REDIRECT_TO_DASHBOARD;
        List<AmAliveApproval> wEmpApprList = this.historyService.loadPendingAmAliveApprovals(bc,  (paginationBean.getPageNumber() - 1) * pageLength,
                pageLength, pTicketId);

        PaginatedEpmBean wPB = new PaginatedEpmBean(wEmpApprList, paginationBean.getPageNumber(), pageLength, wNoOfElem, paginationBean.getSortCriterion(),
                paginationBean.getSortOrder());
        if (wEmpApprList.size() > 0)
            wPB.setShowLink(true);

        model.addAttribute("miniBean", wPB);
        model.addAttribute("roleBean", bc);

        return VIEW_NAME;
    }
    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @RequestParam(value = "_submit", required = false) String ok, @RequestParam(value = "_confirm", required = false) String confirm,
                                @ModelAttribute("miniBean") PaginatedEpmBean pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            super.removeSessionAttribute(request, SELECTED_IDS_KEY); //per adventure it has been selected....
            return REDIRECT_TO_DASHBOARD;
        }

        ConfigurationBean configurationBean = loadConfigurationBean(request);
        if (!configurationBean.isAmAliveCreatorCanApprove()) {
            int found = 0;
            for (AmAliveApproval eA : (List<AmAliveApproval>) pEHB.getList()) {

                if (eA.getInitiator().getId().equals(bc.getLoginId()) && Boolean.valueOf(eA.getRowSelected())) {
                    eA.setRowSelected("false");
                    found++;
                }
            }
            if(found > 0){
                result.rejectValue("approvalMemo", "Warning", found+" 'I Am Alive' Approvals found that were initiated by you. Initiators Are restricted from approving their own requests.");
                result.rejectValue("approvalMemo", "Warning", found+" "+bc.getStaffTypeName()+" Have been deselected automatically for you.");

                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {

            if (!pEHB.isAddWarningIssued()) {
                pEHB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Approve Selected Am Alive Request. Please enter a value for 'Approval Memo' and Confirm.");
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }

        }

        if (isButtonTypeClick(request, REQUEST_PARAM_REJECT)) {

            if (!pEHB.isAddWarningIssued()) {
                pEHB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Reject Selected Am Alive Request  Please enter a value for 'Rejection Memo' and Confirm.");
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
                result.rejectValue("approvalMemo", "Warning", " 'Approval Memos' must be at least 8 characters in length.");
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            } else {
                Integer rejected = 0;
                Integer unprocessed = 0;
                Integer processed = 0;

                Long ticketId = ((AmAliveApproval)pEHB.getObjectList().get(0)).getTicketId();

                if (pEHB.getApprovalInd() == IConstants.ON) {
                    for (AmAliveApproval amAliveApproval : (List<AmAliveApproval>) pEHB.getObjectList()) {

                        if (!Boolean.valueOf(amAliveApproval.getRowSelected())) {
                            ++unprocessed;
                            continue;
                        }
                        processed += approveStaff(amAliveApproval, bc, pEHB.getApprovalMemo(),ticketId);
                    }

                }
                else {
                    for (AmAliveApproval amAliveApproval  : (List<AmAliveApproval>) pEHB.getObjectList()) {

                        if (!Boolean.valueOf(amAliveApproval.getRowSelected())) {
                            ++unprocessed;
                            continue;
                        }
                        rejected += rejectStaff(amAliveApproval, bc, pEHB.getApprovalMemo(),ticketId);
                    }

                }
                super.removeSessionAttribute(request, SELECTED_IDS_KEY);

                return "redirect:viewApproveRejectAmAlive.do?s=" + processed + "&us=" + unprocessed;

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

    private void setSelectedRequests(Set<Long> selectedReqIds, List<AmAliveApproval> currReqApprs) {
        if (selectedReqIds != null && selectedReqIds.size() > 0
                && currReqApprs != null && currReqApprs.size() > 0) {
            for (AmAliveApproval reqAppr : currReqApprs) {
                if (selectedReqIds.contains(reqAppr.getId())) {
                    reqAppr.setRowSelected(String.valueOf(true));
                }
            }
        }
    }

    private Integer approveStaff(AmAliveApproval approveAmAlive, BusinessCertificate bc, String approvalMemo, Long ticketId) throws InstantiationException, IllegalAccessException {
        approveAmAlive.setEntityId(approveAmAlive.getHiringInfo().getPensioner().getId());
        approveAmAlive.setEntityName(approveAmAlive.getHiringInfo().getPensioner().getDisplayName());
        approveAmAlive.setEmployeeId(approveAmAlive.getHiringInfo().getPensioner().getEmployeeId());
        approveAmAlive.setApprovalStatusInd(ON);
        approveAmAlive.setApprover(new User(bc.getLoginId()));
        approveAmAlive.setApprovedDate(LocalDate.now());
        approveAmAlive.setApprovalMemo(approvalMemo);
        approveAmAlive.setLastModTs(LocalDate.now());
        approveAmAlive.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        this.historyService.saveApproval(approveAmAlive, bc);
        NotificationService.storeNotification(bc,genericService,approveAmAlive,"viewApproveRejectAmAlive.do?tid="+ticketId,"Am Alive Approved",AM_ALIVE_APPROVAL_CODE);
        return 1;
    }

    private Integer rejectStaff(AmAliveApproval approveAmAlive, BusinessCertificate bc, String approvalMemo, Long ticketId) throws InstantiationException, IllegalAccessException {
        approveAmAlive.setEntityId(approveAmAlive.getHiringInfo().getPensioner().getId());
        approveAmAlive.setEntityName(approveAmAlive.getHiringInfo().getPensioner().getDisplayName());
        approveAmAlive.setEmployeeId(approveAmAlive.getHiringInfo().getPensioner().getEmployeeId());
        approveAmAlive.setApprovalStatusInd(2);
        approveAmAlive.setApprover(new User(bc.getLoginId()));
        approveAmAlive.setApprovedDate(LocalDate.now());
        approveAmAlive.setApprovalMemo(approvalMemo);
        approveAmAlive.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        approveAmAlive.setLastModTs(LocalDate.now());
        this.historyService.saveApproval(approveAmAlive, bc);
        NotificationService.storeNotification(bc,genericService,approveAmAlive,"viewApproveRejectAmAlive.do?tid="+ticketId,"Am Alive Rejected",AM_ALIVE_APPROVAL_CODE);

        //-- Save Approval Info For Notification

        return 1;
    }


}
