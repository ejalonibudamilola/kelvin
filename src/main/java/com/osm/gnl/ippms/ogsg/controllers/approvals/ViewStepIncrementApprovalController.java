/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.StepIncrementApproval;
import com.osm.gnl.ippms.ogsg.domain.beans.PaginatedEpmBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping({"/viewStepIncrementApproval.do"})
@SessionAttributes(types = {PaginatedEpmBean.class})
public class ViewStepIncrementApprovalController extends BaseController {

    private final String VIEW_NAME = "approval/stepIncrementApprovalView";
    private final PaycheckService paycheckService;

    @Autowired
    public ViewStepIncrementApprovalController(PaycheckService paycheckService) {
        this.paycheckService = paycheckService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(@RequestParam(name = "s", required = false) Integer pSaved,
                            @RequestParam(name = "us", required = false) Integer pUnSaved,
                            @RequestParam(name = "r", required = false) Integer pRejected, Model model, HttpServletRequest pRequest) throws Exception {
        SessionManagerService.manageSession(pRequest, model);

        PaginationBean paginationBean = this.getPaginationInfo(pRequest);

        BusinessCertificate bc = this.getBusinessCertificate(pRequest);
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
        predicateBuilder.addPredicate(getBusinessClientIdPredicate(pRequest));
        int wNoOfElem = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, StepIncrementApproval.class);
        if (wNoOfElem == 0)
            return REDIRECT_TO_DASHBOARD;
        List<StepIncrementApproval> wEmpApprList = this.genericService.loadPaginatedObjects(StepIncrementApproval.class, predicateBuilder.getPredicates(), (paginationBean.getPageNumber() - 1) * pageLength,
                pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        PaginatedEpmBean wPB = new PaginatedEpmBean(wEmpApprList, paginationBean.getPageNumber(), pageLength, wNoOfElem, paginationBean.getSortCriterion(),
                paginationBean.getSortOrder());
        if (wEmpApprList.size() > 0)
            wPB.setShowLink(true);
        if (IppmsUtils.isNotNullAndGreaterThanZero(pSaved)) {
            String approvalMessage = "Approvals Done for " + pSaved + " Pending Step Increment Approvals Successfully.";
            if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved))
                approvalMessage += " " + pUnSaved + " Approvals not processed. You created the " + bc.getStaffTypeName() + " Step Increments";

            addSaveMsgToModel(pRequest, model, approvalMessage);

        } else if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved)) {
            addSaveMsgToModel(pRequest, model, " " + pUnSaved + " Approvals not processed. You created the " + bc.getStaffTypeName() + " Step Increments");
        } else if (IppmsUtils.isNotNullAndGreaterThanZero(pRejected)) {
            addSaveMsgToModel(pRequest, model, " " + pUnSaved + " Approvals not processed. You Rejected the " + bc.getStaffTypeName() + " Step Increments");

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


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
            return REDIRECT_TO_DASHBOARD;

        ConfigurationBean configurationBean = loadConfigurationBean(request);
        if (!configurationBean.isStepIncrementerCanApprove()) {
            int found = 0;
            for (StepIncrementApproval eA : (List<StepIncrementApproval>) pEHB.getList()) {

                if (eA.getInitiator().getId().equals(bc.getLoginId()) && Boolean.valueOf(eA.getRowSelected())) {
                    eA.setRowSelected("false");
                    found++;
                }
            }
            if (found > 0) {
                result.rejectValue("approvalMemo", "Warning", found + " " + bc.getStaffTypeName() + " Step Increments created by you. Creators of " + bc.getStaffTypeName() + " Step Increments are restricted from Approving them.");
                result.rejectValue("approvalMemo", "Warning", found + " " + bc.getStaffTypeName() + " Have been deselected automatically for you.");

                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {

            if (!pEHB.isAddWarningIssued()) {
                pEHB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Approve Selected " + bc.getStaffTypeName() + " Step Increments. Please enter a value for 'Approval Memo' and Confirm.");
                pEHB.setApprovalInd(IConstants.ON);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }

        }

        if (isButtonTypeClick(request, REQUEST_PARAM_REJECT)) {

            if (!pEHB.isAddWarningIssued()) {
                pEHB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Reject Selected " + bc.getStaffTypeName() + " Step Increments. Please enter a value for 'Rejection Memo' and Confirm.");
                pEHB.setApprovalInd(IConstants.OFF);
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
                Integer unprocessed = 0;
                Integer processed = 0;
                Integer rejected = 0;
                if (pEHB.getApprovalInd() == IConstants.ON) {

                    for (StepIncrementApproval employeeApproval : (List<StepIncrementApproval>) pEHB.getObjectList()) {

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

                            wRPB.setNoOfPromotions(wRPB.getNoOfPromotions() + processed);
                            if (wRPB.isNewEntity()) {
                                wRPB.setRunMonth(_wCal.getMonthValue());
                                wRPB.setRunYear(_wCal.getYear());
                                wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                            }
                            wRPB.setRerunInd(IConstants.ON);
                            this.genericService.saveObject(wRPB);
                        }

                    }

                } else {
                    for (StepIncrementApproval employeeApproval : (List<StepIncrementApproval>) pEHB.getObjectList()) {

                        if (!Boolean.valueOf(employeeApproval.getRowSelected())) {
                            ++unprocessed;
                            continue;
                        }
                        rejected += rejectStaff(employeeApproval, bc, pEHB.getApprovalMemo());

                    }

                }
                return "redirect:viewStepIncrementApproval.do?s=" + processed + "&us=" + unprocessed + "&r=" + rejected;
            }
        }


        return REDIRECT_TO_DASHBOARD;
    }

    private Integer rejectStaff(StepIncrementApproval employeeApproval, BusinessCertificate bc, String approvalMemo) throws InstantiationException, IllegalAccessException {
        employeeApproval.setEntityId(employeeApproval.getStepIncrementTracker().getEmployee().getId());
        employeeApproval.setEntityName(employeeApproval.getStepIncrementTracker().getEmployee().getDisplayName());
        employeeApproval.setEmployeeId(employeeApproval.getStepIncrementTracker().getEmployee().getEmployeeId());
        employeeApproval.setApprovalStatusInd(2);
        employeeApproval.setApprover(new User(bc.getLoginId()));
        employeeApproval.setApprovedDate(LocalDate.now());
        employeeApproval.setApprovalMemo(approvalMemo);
        employeeApproval.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        employeeApproval.setLastModTs(LocalDate.now());
        this.genericService.saveObject(employeeApproval);

        NotificationService.storeNotification(bc, genericService, employeeApproval, "requestNotification.do?arid=" + employeeApproval.getId() + "&s=2&oc="+IConstants.STEP_INC_APPROVAL_URL_IND, approvalMemo, STEP_INC_APPROVAL_CODE);
        return 1;

    }

    private Integer approveStaff(StepIncrementApproval employeeApproval, BusinessCertificate bc, String typeName) throws InstantiationException, IllegalAccessException {

        //First we want to do the Step Increment....
        Employee employee = employeeApproval.getStepIncrementTracker().getEmployee();
        employee.setSalaryInfo(employeeApproval.getSalaryInfo());
        employee.setLastModBy(new User(bc.getLoginId()));
        employee.setLastModTs(Timestamp.from(Instant.now()));
        this.genericService.storeObject(employee);
        employeeApproval.setEntityId(employee.getId());
        employeeApproval.setEntityName(employee.getDisplayName());
        employeeApproval.setEmployeeId(employee.getEmployeeId());
        employeeApproval.setApprovalStatusInd(ON);
        employeeApproval.setApprover(new User(bc.getLoginId()));
        employeeApproval.setApprovedDate(LocalDate.now());
        employeeApproval.setApprovalMemo(typeName);
        employeeApproval.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        employeeApproval.setLastModTs(LocalDate.now());
        this.genericService.saveObject(employeeApproval);
        NotificationService.storeNotification(bc, genericService, employeeApproval, "requestNotification.do?arid=" + employeeApproval.getId() + "&s=1&oc="+IConstants.STEP_INC_APPROVAL_URL_IND, typeName, STEP_INC_APPROVAL_CODE);
        return 1;
    }


}
