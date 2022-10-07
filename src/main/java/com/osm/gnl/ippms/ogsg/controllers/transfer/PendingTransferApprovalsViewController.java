package com.osm.gnl.ippms.ogsg.controllers.transfer;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.TransferService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.TransferApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/viewPendingTransfers.do"})
@SessionAttributes(types = {PaginatedBean.class})
public class PendingTransferApprovalsViewController extends BaseController {


    private final TransferService transferService;
    private final IMenuService menuService;
    private final PaycheckService paycheckService;

    private final int pageLength = 20;
    private final String VIEW = "transfer/viewPendingTransfersForm";

    @Autowired
    public PendingTransferApprovalsViewController(TransferService transferService, IMenuService menuService, PaycheckService paycheckService) {
        this.transferService = transferService;
        this.menuService = menuService;
        this.paycheckService = paycheckService;
    }


    @ModelAttribute("userList")
    public List<User> populateUsersList(HttpServletRequest request) {
        return this.transferService.loadCurrentViewActiveLogin(super.getBusinessCertificate(request));
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);


        List<TransferApproval> empList = this.genericService.loadPaginatedObjects(TransferApproval.class,
                Arrays.asList(CustomPredicate.procurePredicate("approvedDate", null, Operation.IS_NULL),
                        CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())),
                (paginationBean.getPageNumber() - 1) * this.pageLength,
                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        for (TransferApproval t : empList) {

            t.setOldMda(t.getParentObject().getCurrentMdaName());

            if (t.getSchoolInfo() != null && !t.getSchoolInfo().isNewEntity()) {
                t.setNewMda(t.getSchoolInfo().getName());
            } else
                t.setNewMda(t.getMdaDeptMap().getMdaInfo().getName());
        }
        int wNoOfElements = this.transferService.getTotalNoOfActiveTransferApprovals(bc.getBusinessClientInstId());

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength,
                wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        if (bc.isSuperAdmin())
            wPHDB.setEditMode(true);

        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewPendingTransfers.do");
        model.addAttribute("miniBean", wPHDB);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"s", "us"})
    public String setupForm(@RequestParam("s") Integer pSaved,
                            @RequestParam("us") Integer pUnSaved, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);


        List<TransferApproval> empList = this.genericService.loadPaginatedObjects(TransferApproval.class,
                Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("approvedDate", null, Operation.IS_NULL)),
                (paginationBean.getPageNumber() - 1) * this.pageLength,
                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        for (TransferApproval t : empList) {

            t.setOldMda(t.getParentObject().getCurrentMdaName());

            if (t.getSchoolInfo() != null && !t.getSchoolInfo().isNewEntity()) {
                t.setNewMda(t.getSchoolInfo().getName());
            } else
                t.setNewMda(t.getMdaDeptMap().getMdaInfo().getName());
        }
        int wNoOfElements = this.transferService.getTotalNoOfActiveTransferApprovals(bc.getBusinessClientInstId());

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength,
                wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        String action = "Approved";
        if (model.getAttribute("rejection") != null) {
            action = "Rejected";
        }

        if (IppmsUtils.isNotNullAndGreaterThanZero(pSaved)) {
            String approvalMessage = "" + pSaved + " Pending Transfers " + action + " Successfully.";
            if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved))
                approvalMessage += " " + pUnSaved + " Transfer Approvals not processed.";

            addSaveMsgToModel(request, model, approvalMessage);

        } else if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved)) {
            addSaveMsgToModel(request, model, " " + pUnSaved + " Transfer Approvals not processed.");
        }
        wPHDB.setShowLink(true);
        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewPendingTransfers.do");
        model.addAttribute("miniBean", wPHDB);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid", "ln", "uid"})
    public String setupForm(@RequestParam("eid") Long pEmpId,
                            @RequestParam("ln") String pLastName,
                            @RequestParam("uid") Long pUid, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);


        if (pEmpId.equals(0L))
            pEmpId = null;

        if (pUid.equals(0L))
            pUid = null;

        //Now Get the list of Id's with the Last Name...
        List<Long> wEmpIds = null;
        if (!StringUtils.trimToEmpty(pLastName).equals(EMPTY_STR)) {
            wEmpIds = this.transferService.loadEmployeeInstIdsByStringValue(bc, pLastName);
            if (wEmpIds.isEmpty())
                wEmpIds = null;
        }

        List<CustomPredicate> customPredicates = new ArrayList<>();
        customPredicates.add(getBusinessClientIdPredicate(request));
        if (IppmsUtils.isNotNullAndGreaterThanZero(pEmpId))
            customPredicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmpId));
        if (IppmsUtils.isNotNullOrEmpty(wEmpIds))
            customPredicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), (Comparable) wEmpIds));
        if (IppmsUtils.isNotNullAndGreaterThanZero(pUid))
            customPredicates.add(CustomPredicate.procurePredicate("initiator.id", pUid));
        if (IppmsUtils.isNotNullOrEmpty(pLastName))
            customPredicates.add(CustomPredicate.procurePredicate("lastName", pLastName, Operation.LIKE));

        List<TransferApproval> empList = this.genericService.loadPaginatedObjects(TransferApproval.class, customPredicates,
                (paginationBean.getPageNumber() - 1) * this.pageLength,
                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        for (TransferApproval t : empList) {

            t.setOldMda(t.getParentObject().getCurrentMdaName());

            if (t.getSchoolInfo() != null && !t.getSchoolInfo().isNewEntity()) {
                t.setNewMda(t.getSchoolInfo().getName());
            } else
                t.setNewMda(t.getMdaDeptMap().getMdaInfo().getName());
        }
        int wNoOfElements = this.transferService.getTotalNoOfActiveTransferApprovals(bc,pEmpId, wEmpIds, pUid);

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength,
                wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        if (IppmsUtils.isNotNullAndGreaterThanZero(pUid))
            wPHDB.setId(pUid);
        else
            wPHDB.setId(0L);
        if (!StringUtils.trimToEmpty(pLastName).equals(EMPTY_STR)) {
            wPHDB.setLastName(pLastName);
        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)) {
            wPHDB.setOgNumber(IppmsUtils.loadEmployee(genericService, pEmpId, bc).getEmployeeId());
        }
        if (bc.isSuperAdmin())
            wPHDB.setEditMode(true);

        model.addAttribute("miniBean", wPHDB);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_update", required = false) String pUpd,
                                @ModelAttribute("miniBean") PaginatedBean pLPB, BindingResult result, SessionStatus
                                        status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE)) {
            Long empId = 0L;
            String wLastName = "";
            Long pUid = 0L;
            if (!StringUtils.trimToEmpty(pLPB.getOgNumber()).equals(EMPTY_STR)) {

                AbstractEmployeeEntity abstractEmployeeEntity = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(
                        CustomPredicate.procurePredicate("employeeId", pLPB.getOgNumber()), getBusinessClientIdPredicate(request)));

                if (abstractEmployeeEntity.isNewEntity()) {
                    result.rejectValue("", "InvalidValue", "No " + bc.getStaffTypeName() + " found with " + bc.getStaffTitle() + " " + pLPB.getOgNumber());
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);

                    model.addAttribute("status", result);
                    model.addAttribute("miniBean", pLPB);
                    model.addAttribute("roleBean", bc);

                    return VIEW;
                }
                empId = abstractEmployeeEntity.getId();
            }
            if (!StringUtils.trimToEmpty(pLPB.getLastName()).equals(EMPTY_STR)) {
                wLastName = pLPB.getLastName();
            }
            if (pLPB.getId() > 0)
                pUid = pLPB.getId();

            Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewPendingTransfers.do?eid=" + empId + "&ln=" + wLastName + "&uid=" + pUid);
            return "redirect:viewPendingTransfers.do?eid=" + empId + "&ln=" + wLastName + "&uid=" + pUid;

        }
        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {
            //--Check if there are pending paychecks
            if (IppmsUtilsExt.pendingPaychecksExists(genericService, bc))
                if (!pLPB.isShowOverride())
                    pLPB.setShowOverride(true);
            pLPB.setMemoType("Approval");

            if (!pLPB.isAddWarningIssued()) {
                pLPB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Approve Selected " + bc.getStaffTypeName() + " Transfers. Please enter a value for 'Approval Memo' and Confirm.");
                model.addAttribute("miniBean", pLPB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW;
            }

        }
        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {
            boolean updatePendingPaychecks = false;


            if (pLPB.getOverrideInd() == 0 && IppmsUtilsExt.pendingPaychecksExists(genericService, bc)) {
                result.rejectValue("", "Override.Unset", "Please select a value for 'Effect on Pending Payslip*'.");
                addDisplayErrorsToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("roleBean", bc);
                model.addAttribute("miniBean", pLPB);
                return VIEW;
            } else {
                if (pLPB.getOverrideInd() == 1)
                    updatePendingPaychecks = true;

            }

            if (IppmsUtils.isNullOrEmpty(pLPB.getApprovalMemo()) || StringUtils.trimToEmpty(pLPB.getApprovalMemo()).trim().length() < 8) {
                pLPB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Approval Memo'");
                model.addAttribute("miniBean", pLPB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW;
            } else {
                Integer unprocessed = 0;
                Integer processed = 0;
                for (TransferApproval transferApproval : (List<TransferApproval>) pLPB.getObjectList()) {

                    if (!Boolean.valueOf(transferApproval.getRowSelected())) {
                        ++unprocessed;
                        continue;
                    }
                    processed += transferStaff(transferApproval, bc, pLPB.getApprovalMemo(), updatePendingPaychecks);
                    NotificationService.storeNotification(bc, genericService, transferApproval, "requestNotification.do?arid="+transferApproval.getId()+"&s=1&oc="+TRANSFER_APPROVAL_URL_IND, bc.getStaffTypeName() + " Transfer ",TRANSFER_APPROVAL_CODE);

                }
                return "redirect:viewPendingTransfers.do?s=" + processed + "&us=" + unprocessed;

            }
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_REJECT)) {

            //if we get here...check if Rejection Reason is set..
            if (!pLPB.isAddWarningIssued()) {
                pLPB.setAddWarningIssued(true);
                pLPB.setMemoType("Rejection");
                pLPB.setRejection(true);

                result.rejectValue("", "warning", "Please enter a Rejection Reason.");
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);
                model.addAttribute("roleBean", bc);
                return VIEW;
            }
            if (IppmsUtils.isNullOrEmpty(pLPB.getApprovalMemo()) || StringUtils.trimToEmpty(pLPB.getApprovalMemo()).trim().length() < 8) {
                pLPB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Rejection Memo'");
                model.addAttribute("miniBean", pLPB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW;
            } else {
                Integer unprocessed = 0;
                Integer processed = 0;
                for (TransferApproval transferApproval : (List<TransferApproval>) pLPB.getObjectList()) {

                    if (!Boolean.valueOf(transferApproval.getRowSelected())) {
                        ++unprocessed;
                        continue;
                    }
                    processed += rejectTransfer(transferApproval, bc, pLPB.getApprovalMemo());
                    NotificationService.storeNotification(bc, genericService, transferApproval, "requestNotification.do?arid="+transferApproval.getId()+"&s=2&oc="+TRANSFER_APPROVAL_URL_IND, bc.getStaffTypeName() + " Transfer ",TRANSFER_APPROVAL_CODE);

                }
                 model.addAttribute("rejection", true);
                return "redirect:viewPendingTransfers.do?s=" + unprocessed + "&us=" + processed;

            }
        }


        return REDIRECT_TO_DASHBOARD;
    }

    private Integer rejectTransfer(TransferApproval transferApproval, BusinessCertificate bc, String approvalMemo) throws Exception {
        transferApproval.setApprovalStatusInd(2);
        transferApproval.setApprovedDate(LocalDate.now());
        transferApproval.setApprover(new User(bc.getLoginId()));
        transferApproval.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        transferApproval.setApprovalMemo(approvalMemo);
        transferApproval.setLastModTs(LocalDate.now());
        this.genericService.saveObject(transferApproval);

        return 1;
    }

    private Integer transferStaff(TransferApproval transferApproval, BusinessCertificate bc, String approvalMemo, boolean updatePendingPaychecks) throws Exception {

        Long pEmpId = transferApproval.getParentId();

        String oldMda = transferApproval.getParentObject().getCurrentMdaName();
        String newMda = transferApproval.getMdaDeptMap().getMdaInfo().getName();

        this.transferSingleEmployee(transferApproval, bc);
        TransferLog wPL = new TransferLog();
        wPL.setNewMda(newMda);
        wPL.setOldMda(oldMda);
        if (bc.isPensioner())
            wPL.setPensioner(new Pensioner(pEmpId));
        else
            wPL.setEmployee(new Employee(pEmpId));
        wPL.setName(transferApproval.getParentObject().getDisplayName());
        wPL.setTransferDate(transferApproval.getInitiatedDate());
        wPL.setUser(transferApproval.getInitiator());
        wPL.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, bc));
        wPL.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
        wPL.setMdaInfo(transferApproval.getMdaDeptMap().getMdaInfo());
        wPL.setBusinessClientId(bc.getBusinessClientInstId());

        if (transferApproval.getSchoolInfo() != null && !transferApproval.getSchoolInfo().isNewEntity())
            wPL.setSchoolInfo(transferApproval.getSchoolInfo());
        wPL.setSalaryInfo(transferApproval.getEmployee().getSalaryInfo());
        this.genericService.saveObject(wPL);
        transferApproval.setApprovalStatusInd(1);
        transferApproval.setApprovedDate(LocalDate.now());
        transferApproval.setApprover(new User(bc.getLoginId()));
        transferApproval.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        transferApproval.setApprovalMemo(approvalMemo);
        transferApproval.setLastModTs(LocalDate.now());
        this.genericService.saveObject(transferApproval);
         //now check if this dude is overriding..
        if (updatePendingPaychecks) {
            LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            if (_wCal != null) {
                AbstractPaycheckEntity abstractPaycheckEntity = (AbstractPaycheckEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc),
                        Arrays.asList(CustomPredicate.procurePredicate("employee.id", transferApproval.getParentId()), CustomPredicate.procurePredicate("status", "P")));
                abstractPaycheckEntity.setMdaDeptMap(transferApproval.getMdaDeptMap());
                if (transferApproval.getSchoolInfo() != null && !transferApproval.getSchoolInfo().isNewEntity())
                    abstractPaycheckEntity.setSchoolInfo(new SchoolInfo(transferApproval.getSchoolInfo().getId()));
                genericService.saveObject(abstractPaycheckEntity);
            }
        }

        return 1;
    }

    private Long transferSingleEmployee(TransferApproval transferApproval, BusinessCertificate bc) {
            transferApproval.getParentObject().setMdaDeptMap(transferApproval.getMdaDeptMap());
            transferApproval.getParentObject().setLastModBy(new User(bc.getLoginId()));
            transferApproval.getParentObject().setLastModTs(Timestamp.from(Instant.now()));
            if (transferApproval.getSchoolInfo() != null && !transferApproval.getSchoolInfo().isNewEntity())
                transferApproval.getEmployee().setSchoolInfo(transferApproval.getSchoolInfo());
            else
                transferApproval.getEmployee().setSchoolInfo(null);


        return this.genericService.storeObject(transferApproval.getParentObject());
    }

}
