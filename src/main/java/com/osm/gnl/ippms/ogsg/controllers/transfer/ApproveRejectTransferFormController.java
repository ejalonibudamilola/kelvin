/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.transfer;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.TransferApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.apache.commons.lang.StringUtils;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;


@Controller
@RequestMapping({"/approveTransfer.do"})
@SessionAttributes(types = {TransferApproval.class})
public class ApproveRejectTransferFormController extends BaseController {

     private final PaycheckService paycheckService;
     private final String VIEW = "transfer/transferApprovalForm";

    @Autowired
    public ApproveRejectTransferFormController(PaycheckService paycheckService) {
        this.paycheckService = paycheckService;

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"tid"})
    public String setupForm(@RequestParam("tid") Long pTid, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        //Load this TransferApproval...
        TransferApproval wTA = this.genericService.loadObjectById(TransferApproval.class, pTid);

        wTA.setOldMda(wTA.getEmployee().getParentObjectName());

        if (wTA.getSchoolInfo() != null && !wTA.getSchoolInfo().isNewEntity())
            wTA.setNewMda(wTA.getSchoolInfo().getName());
        else
            wTA.setNewMda(wTA.getMdaDeptMap().getMdaInfo().getName());

        //Now check if there are pending Paychecks found...
        LocalDate _wCal = paycheckService.getPendingPaycheckRunMonthAndYear(bc);
        if (_wCal != null) {
            wTA.setShowOverride(true);
        }

        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);
        return VIEW;

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"tid", "s"})
    public String setupForm(@RequestParam("tid") Long pTid,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        //Load this TransferApproval...
        TransferApproval wTA = this.genericService.loadObjectById(TransferApproval.class, pTid);

        wTA.setOldMda(wTA.getEmployee().getParentObjectName());

        if (wTA.getSchoolInfo() != null && !wTA.getSchoolInfo().isNewEntity()) {
            wTA.setNewMda(wTA.getSchoolInfo().getCodeName());
        } else {
            wTA.setNewMda(wTA.getMdaDeptMap().getMdaInfo().getCodeName());
        }


            if (pSaved == 2) {
                model.addAttribute(IConstants.SAVED_MSG, "Transfer Request Rejected Successfully.");
            } else {
                model.addAttribute(IConstants.SAVED_MSG, "Transfer Completed and Approved Successfully");
            }
        NotificationService.storeNotification(bc, genericService, wTA, "requestNotification.do?arid="+wTA.getId()+"&s="+pSaved+"&oc="+TRANSFER_APPROVAL_URL_IND, bc.getStaffTypeName() + " Transfer ",TRANSFER_APPROVAL_CODE);

        model.addAttribute("saved", true);
        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);

        return VIEW;

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"tid", "tlid", "s"})
    public String setupForm(@RequestParam("tid") Long pTaid, @RequestParam("tlid") Long pTlid,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        TransferLog transferLog = null;

        //Load this TransferApproval...
        TransferApproval wTA = this.genericService.loadObjectById(TransferApproval.class, pTaid);
        if (pTlid != null) {
            transferLog = this.genericService.loadObjectById(TransferLog.class, pTlid);
        }
        wTA.setOldMda(transferLog.getOldMda());

        wTA.setNewMda(transferLog.getNewMda());
        if (pSaved == 2) {
            model.addAttribute(IConstants.SAVED_MSG, "Transfer Request Rejected Successfully.");
        } else {
            model.addAttribute(IConstants.SAVED_MSG, "Transfer Completed and Approved Successfully");
        }
        NotificationService.storeNotification(bc, genericService, wTA, "requestNotification.do?arid="+wTA.getId()+"&s="+pSaved+"&oc="+TRANSFER_APPROVAL_URL_IND, bc.getStaffTypeName() + " Transfer ",TRANSFER_APPROVAL_CODE);

        model.addAttribute("saved", true);
        model.addAttribute("miniBean", wTA);
        model.addAttribute("roleBean", bc);

        return VIEW;

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_approve", required = false) String approve,
                                @RequestParam(value = "_reject", required = false) String reject,
                                @ModelAttribute("miniBean") TransferApproval pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            //if(this.payrollService.getTotalNoOfActiveTransferApprovals() == 0)
            return Navigator.getInstance(getSessionId(request)).getFromForm();
            //return "redirect:viewPendingTransfers.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {


            if (StringUtils.isEmpty(pEHB.getApprovalMemo())) {
                pEHB.setApproval(true);
                pEHB.setApprovalStatusInd(2);
                result.rejectValue("", "warning", "Please enter an Approval Memo.");
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", bc);
                return VIEW;
            }

            //Here we need to Approve this Transfer...
            Long pEmpId = pEHB.getParentId();

            HiringInfo hiringInfo = this.loadHiringInfoByEmpId(request, bc, pEHB.getParentId());
            this.transferSingleEmployee(hiringInfo, pEHB.getMdaDeptMap(), bc);
            TransferLog wPL = new TransferLog();
            wPL.setNewMda(pEHB.getNewMda());
            wPL.setOldMda(pEHB.getOldMda());
            if (bc.isPensioner())
                wPL.setPensioner(new Pensioner(pEmpId));
            else
                wPL.setEmployee(new Employee(pEmpId));
            wPL.setName(pEHB.getEmployee().getDisplayName());
            wPL.setTransferDate(pEHB.getInitiatedDate());
            wPL.setUser(pEHB.getInitiator());
            wPL.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, bc));
            wPL.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
            wPL.setMdaInfo(pEHB.getMdaDeptMap().getMdaInfo());
            wPL.setBusinessClientId(bc.getBusinessClientInstId());

            if (pEHB.getSchoolInfo() != null && !pEHB.getSchoolInfo().isNewEntity())
                wPL.setSchoolInfo(pEHB.getEmployee().getSchoolInfo());
            wPL.setSalaryInfo(pEHB.getEmployee().getSalaryInfo());
            this.genericService.saveObject(wPL);
            pEHB.setApprovalStatusInd(1);
            pEHB.setApprovedDate(LocalDate.now());
            pEHB.setApprover(new User(bc.getLoginId()));
            pEHB.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
            pEHB.setLastModTs(LocalDate.now());
            this.genericService.saveObject(pEHB);

            //now check if this dude is overriding..
            if (pEHB.getOverrideInd() == 1) {
                LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
                if (_wCal != null) {
                    AbstractPaycheckEntity abstractPaycheckEntity = (AbstractPaycheckEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(getBusinessClientIdPredicate(request),
                            CustomPredicate.procurePredicate("employee.id", pEHB.getParentId()), CustomPredicate.procurePredicate("status", "P")));
                    abstractPaycheckEntity.setMdaDeptMap(pEHB.getMdaDeptMap());
                    if (IppmsUtils.isNotNull(pEHB.getSchoolInfo()) && !pEHB.getSchoolInfo().isNewEntity())
                        abstractPaycheckEntity.setSchoolInfo(new SchoolInfo(pEHB.getSchoolInfo().getId()));
                    genericService.saveObject(abstractPaycheckEntity);
                }
            }

             return "redirect:approveTransfer.do?tid=" + pEHB.getId() + "&tlid=" + wPL.getId() + "&s=1";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_REJECT)) {

            //if we get here...check if Rejection Reason is set..
            if (StringUtils.isEmpty(pEHB.getRejectionReason())) {
                pEHB.setRejection(true);
                pEHB.setApprovalStatusInd(2);
                result.rejectValue("", "warning", "Please enter a Rejection Reason.");
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", bc);
                return VIEW;
            }
            //Otherwise Reject it....
            pEHB.setApprovalStatusInd(2);
            pEHB.setApprovedDate(LocalDate.now());
            pEHB.setApprover(new User(bc.getLoginId()));
            pEHB.setApprovalMemo(pEHB.getRejectionReason());
            pEHB.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
            pEHB.setLastModTs(LocalDate.now());
            this.genericService.saveObject(pEHB);
              return "redirect:approveTransfer.do?tid=" + pEHB.getId() + "&s=2";
        }


        return "redirect:approveTransfer.do?tid=" + pEHB.getId();
    }

    private Long transferSingleEmployee(HiringInfo pEHB, MdaDeptMap m, BusinessCertificate bc) {


        if (!bc.isPensioner()) {
            if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getSchoolId()))
                pEHB.getEmployee().setSchoolInfo(new SchoolInfo(pEHB.getSchoolId()));
            else
                pEHB.getEmployee().setSchoolInfo(null);

        }
        pEHB.getAbstractEmployeeEntity().setMdaDeptMap(m);
        pEHB.getAbstractEmployeeEntity().setLastModBy(new User(bc.getLoginId()));
        pEHB.getAbstractEmployeeEntity().setLastModTs(Timestamp.from(Instant.now()));
        return this.genericService.storeObject(pEHB.getAbstractEmployeeEntity());

    }
}
