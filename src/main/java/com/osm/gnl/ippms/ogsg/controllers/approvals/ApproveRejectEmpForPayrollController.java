/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.employee.EmployeeController;
import com.osm.gnl.ippms.ogsg.controllers.employee.service.EmployeeControllerService;
import com.osm.gnl.ippms.ogsg.domain.approval.EmployeeApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
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
import java.util.Arrays;

@Controller
@RequestMapping({"/approveEmployeeForPayroll.do"})
@SessionAttributes(types = {EmployeeApproval.class})
public class ApproveRejectEmpForPayrollController extends BaseController {


    private final PaycheckService paycheckService;

    private final String VIEW_NAME = "approval/employeeApprovalForm";

    @Autowired
    public ApproveRejectEmpForPayrollController(PaycheckService paycheckService) {
        this.paycheckService = paycheckService;

    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"aid"})
    public String setupForm(@RequestParam("aid") Long pAid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        //Load this EmployeeApproval...
        EmployeeApproval wTA = this.genericService.loadObjectUsingRestriction(EmployeeApproval.class, Arrays.asList(
                CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pAid), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));


        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("payrollStatus", IConstants.ON)));
        if (!wPRB.isNewEntity()) {
            String msg = "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". " + bc.getStaffTypeName() + " Payroll Approvals or Rejections can not be effected during a Payroll Run.";
            if (bc.isPensioner()) {
                if (wPRB.isGratuityRunning())
                    msg = "Gratuity Information is currently being run by " + wPRB.getInitiator().getActualUserName() + ". " + bc.getStaffTypeName() + " Payroll Approvals or Rejections can not be effected during a Payroll Run.";
            }
            model.addAttribute("payrollRunning", true);

            model.addAttribute("payrollRunningMessage", msg);
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
        }

        return prepareAndReturView(model, bc, wTA, null);

    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid", "upid", "upidi"})
    public String setupForm(@RequestParam("eid") Long pEid,
                            @RequestParam("upid") int pUpid, @RequestParam("upidi") int pUpidi,
                            Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        EmployeeApproval wTA = this.genericService.loadObjectUsingRestriction(EmployeeApproval.class, Arrays.asList(
                CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEid), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
        if(wTA.isNewEntity()){
            AbstractEmployeeEntity  employeeEntity = (AbstractEmployeeEntity)genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc),pEid);
            wTA = new EmployeeApproval(EmployeeControllerService.createEmployeeApproval(employeeEntity,bc,genericService));
        }
        return "redirect:approveEmployeeForPayroll.do?aid=" + wTA.getId();

    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"aid", "s"})
    public String setupForm(@RequestParam("aid") Long pTid,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        //Load this EmployeeApproval...
        EmployeeApproval wTA = this.genericService.loadObjectUsingRestriction(EmployeeApproval.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("id", pTid)));
        if(wTA.getChildObject() == null){
            //This means we have some issue with this guy...
            AbstractEmployeeEntity abstractEmployeeEntity = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc),Arrays.asList(CustomPredicate.procurePredicate("id",wTA.getEntityId()), getBusinessClientIdPredicate(request)));
            if(abstractEmployeeEntity.isPensioner())
                wTA.setPensioner((Pensioner)abstractEmployeeEntity);
            else
                wTA.setEmployee((Employee)abstractEmployeeEntity);
        }

        if (this.isShowNotificationMsg(request, this.getClass())) {
            if (pSaved == 2) {
                model.addAttribute(IConstants.SAVED_MSG, bc.getStaffTypeName() + " Payroll Request Rejected Successfully.");
            } else {
                model.addAttribute(IConstants.SAVED_MSG, bc.getStaffTypeName() + " Payroll Request Completed and Approved Successfully");
            }
            this.resetNavigatorFromClass(request);
        }

        model.addAttribute("saved", true);


        return prepareAndReturView(model, bc, wTA, null);

    }


    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_approve", required = false) String approve,
                                @RequestParam(value = "_reject", required = false) String reject,
                                @ModelAttribute("miniBean") EmployeeApproval pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        String redirectUrl;

        //Long ticketId  = PayrollBeanUtils.getCurrentDateTimeAsLong();

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            //int empApp = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmployeeApproval.class);
            if (this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmployeeApproval.class) < 1)
                return REDIRECT_TO_DASHBOARD;
            return "redirect:viewEmpForPayApproval.do";
        }

        //Check if Payroll is running...
        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("payrollStatus", IConstants.OFF)));
        if (!wPRB.isNewEntity() && wPRB.isRunning()) {
            result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". " + bc.getStaffTypeName() + " Payroll Approvals or Rejections can not be effected during a Payroll Run");
            return prepareAndReturView(model, bc, pEHB, result);
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {
            if (IppmsUtils.isNullOrEmpty(pEHB.getApprovalMemo()) || StringUtils.trimToEmpty(pEHB.getApprovalMemo()).trim().length() < 8) {

                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Approval Memo' - Minimum 8 Characters");
                return prepareAndReturView(model, bc, pEHB, result);
            }
            ConfigurationBean configurationBean = loadConfigurationBean(request);

            if (!bc.isSuperAdmin() || !configurationBean.isEmpCreatorCanApprove()) {
                if (pEHB.getInitiator().getId().equals(bc.getLoginId())) {
                    result.rejectValue("", "Invalid.True", "You can not approve " + bc.getStaffTypeName() + " you created.");
                    return prepareAndReturView(model, bc, pEHB, result);
                }
            }
            pEHB.setEntityId(pEHB.getChildObject().getId());
            pEHB.setEntityName(pEHB.getChildObject().getDisplayName());
            pEHB.setEmployeeId(pEHB.getChildObject().getEmployeeId());
            pEHB.setApprovalStatusInd(ON);
            pEHB.setApprover(new User(bc.getLoginId()));
            pEHB.setApprovedDate(LocalDate.now());
            pEHB.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
            this.genericService.saveObject(pEHB);

            LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            if (_wCal != null) {
                RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class,
                        Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                                CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", _wCal.getYear())));

                wRPB.setNoOfApprovedEmployees(wRPB.getNoOfApprovedEmployees() + 1);
                if (wRPB.isNewEntity()) {
                    wRPB.setRunMonth(_wCal.getMonthValue());
                    wRPB.setRunYear(_wCal.getYear());
                    wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                }
                wRPB.setRerunInd(IConstants.ON);
                this.genericService.saveObject(wRPB);
            }

            redirectUrl = "approveEmployeeForPayroll.do?aid=" + pEHB.getId() + "&s=1";

            //save object for notification
            NotificationService.storeNotification(bc, genericService, pEHB, redirectUrl, bc.getStaffTypeName() + " Approval", STAFF_APPROVAL_CODE);
            return "redirect:" + redirectUrl;
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_REJECT)) {
            if (IppmsUtilsExt.pendingPaychecksExistsForEntity(genericService, bc, pEHB.getChildObject().getId())) {
                result.rejectValue("", "Invalid.True", "Pending Paychecks found for " + pEHB.getChildObject().getDisplayName() + ". Approve or Delete paycheck before Rejecting this " + bc.getStaffTypeName());
                return prepareAndReturView(model, bc, pEHB, result);
            }
            if (IppmsUtils.isNullOrEmpty(pEHB.getApprovalMemo()) || StringUtils.trimToEmpty(pEHB.getApprovalMemo()).trim().length() < 8) {

                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Rejection Memo' - Minimum 8 Characters");
                return prepareAndReturView(model, bc, pEHB, result);
            }
            pEHB.setEntityId(pEHB.getChildObject().getId());
            pEHB.setEntityName(pEHB.getChildObject().getDisplayName());
            pEHB.setEmployeeId(pEHB.getChildObject().getEmployeeId());
            pEHB.setApprovalStatusInd(2);
            pEHB.setApprover(new User(bc.getLoginId()));
            pEHB.setApprovedDate(LocalDate.now());
            this.genericService.saveObject(pEHB);

            LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            if (_wCal != null) {
                RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class,
                        Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                                CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", _wCal.getYear())));

                wRPB.setNoOfRejectedEmployees(wRPB.getNoOfRejectedEmployees() + 1);
                if (wRPB.isNewEntity()) {
                    wRPB.setRunMonth(_wCal.getMonthValue());
                    wRPB.setRunYear(_wCal.getYear());
                    wRPB.setRerunInd(IConstants.ON);
                    wRPB.setBusinessClientId(bc.getBusinessClientInstId());

                }
                this.genericService.saveObject(wRPB);
            }

            redirectUrl = "approveEmployeeForPayroll.do?aid=" + pEHB.getId() + "&s=2";

            //save object for notification
            NotificationService.storeNotification(bc, genericService, pEHB, redirectUrl, bc.getStaffTypeName() + " Approval", STAFF_APPROVAL_CODE);
            return "redirect:" + redirectUrl;
        }

        return "redirect:approveEmployeeForPayroll.do?aid=" + pEHB.getId() + "&s=1";
    }

    private String prepareAndReturView(Model model, BusinessCertificate bc, EmployeeApproval wTA, BindingResult result) {

        addPageTitle(model, bc.getStaffTypeName() + " Payroll Approval Form");
        addMainHeader(model, "Payroll Approval for " + bc.getStaffTypeName() + " " + wTA.getChildObject().getDisplayName() + " [ " + wTA.getChildObject().getEmployeeId() + " ]");
        addTableHeader(model, wTA.getChildObject().getDisplayName() + "'s Information");
        model.addAttribute("hasChildObject", true);
        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);
        if (result != null) {
            model.addAttribute("status", result);
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
        }
        return VIEW_NAME;
    }

}
