/*
 * Copyright (c) 2020.
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
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.validators.hr.TransferValidator;
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
import java.util.List;

@Controller
@RequestMapping({"/transferEmployee.do"})
@SessionAttributes(types={HiringInfo.class})

public class TransferController extends BaseController {

    private final PaycheckService paycheckService;
    private final TransferValidator validator;
    private final String VIEW_NAME = "transfer/transferEmployeeForm";

    @ModelAttribute("mdaList")
    protected List<MdaInfo> loadMdaList(HttpServletRequest request) {
        return this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), "name");
    }
    @Autowired
    public TransferController(PaycheckService paycheckService, TransferValidator validator) {
        this.paycheckService = paycheckService;
        this.validator = validator;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {


        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (IppmsUtils.isNullOrLessThanOne(pEmpId)) {
            return REDIRECT_TO_DASHBOARD;
        }


        HiringInfo empHrBean = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmpId),
                getBusinessClientIdPredicate(request)));

        if (empHrBean.isNewEntity() || empHrBean.isTerminated()) {

            empHrBean.setDisplayPayrollMsg("This " + bc.getStaffTypeName() + " was terminated on " + PayrollHRUtils.getDisplayDateFormat().format(empHrBean.getTerminateDate()) + ". Transfer denied!");

            model.addAttribute("miniBean", empHrBean);
            return "promotion/promoteEmployeeErrorForm";
        }

        PayrollFlag wPF = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));

        AbstractPaycheckEntity wEmpPayBean = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("employee.id", pEmpId), CustomPredicate.procurePredicate("runMonth", wPF.getApprovedMonthInd()), CustomPredicate.procurePredicate("runYear", wPF.getApprovedYearInd())));

        empHrBean.setShowForConfirm(HIDE_ROW);
        empHrBean.setOldLevelAndStep(empHrBean.getAbstractEmployeeEntity().getSalaryInfo().getLevelStepStr());

        if (wEmpPayBean.isNewEntity())
            empHrBean.setOldSalary(0.0D);
        else {
            empHrBean.setOldSalary(wEmpPayBean.getTotalPay());
        }

        empHrBean.setOldSalaryInfoInstId(empHrBean.getAbstractEmployeeEntity().getSalaryInfo().getId());
        LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
        if (_wCal != null) {
            if (bc.isSuperAdmin()) {
                empHrBean.setShowOverride(true);
            }
        }


        empHrBean.setRowVisibility(HIDE_ROW);
        empHrBean.setTerminationWarningIssued(false);
        model.addAttribute("roleBean", bc);
        model.addAttribute("miniBean", empHrBean);
        return VIEW_NAME;

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"tlid", "s"})
    public String setupForm(@RequestParam("tlid") Long pTlid, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (pTlid <= 0) {
            return REDIRECT_TO_DASHBOARD;
        }

        TransferLog wTL = this.genericService.loadObjectById(TransferLog.class, pTlid);
        Long pId;
        if (bc.isPensioner())
            pId = wTL.getPensioner().getId();
        else
            pId = wTL.getEmployee().getId();

        HiringInfo empHrBean = this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pId));

        PayrollFlag wPF = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));
        AbstractPaycheckEntity wEmpPayBean = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("employee.id", pId), CustomPredicate.procurePredicate("runMonth", wPF.getApprovedMonthInd()), CustomPredicate.procurePredicate("runYear", wPF.getApprovedYearInd())));

        empHrBean.setOldLevelAndStep(empHrBean.getAbstractEmployeeEntity().getSalaryInfo().getLevelStepStr());

        if (wEmpPayBean.isNewEntity())
            empHrBean.setOldSalary(0.0D);
        else {
            empHrBean.setOldSalary(wEmpPayBean.getTotalPay());
        }


        if (!bc.isPensioner() && wTL.getEmployee().isSchoolEnabled()) {
            if (wTL.getEmployee().isSchoolStaff()) {

                empHrBean.setProposedSchool(wTL.getOldMda());
            }

        } else {
            empHrBean.setProposedMda(wTL.getOldMda());
        }

        if (null != wTL.getSchoolInfo() && !wTL.getSchoolInfo().isNewEntity())
            empHrBean.setRowVisibility(SHOW_ROW);
        else
            empHrBean.setRowVisibility(HIDE_ROW);
        String actionCompleted = bc.getStaffTypeName()+" " + empHrBean.getAbstractEmployeeEntity().getDisplayName() + " Transferred to " + empHrBean.getAbstractEmployeeEntity().getAssignedToObject() + " Successfully";


        empHrBean.setTerminationWarningIssued(false);
        model.addAttribute(SAVED_MSG, actionCompleted);
        model.addAttribute("saved", true);
        model.addAttribute("needsApproval", false);
        model.addAttribute("roleBean", bc);
        model.addAttribute("miniBean", empHrBean);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid", "s", "tid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("s") int pSaved,
                            @RequestParam("tid") Long pTid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (pEmpId <= 0) {
            return REDIRECT_TO_DASHBOARD;
        }


        HiringInfo empHrBean = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmpId),
                getBusinessClientIdPredicate(request)));

        PayrollFlag wPF = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));

        AbstractPaycheckEntity wEmpPayBean = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("employee.id", pEmpId), CustomPredicate.procurePredicate("runMonth", wPF.getApprovedMonthInd()), CustomPredicate.procurePredicate("runYear", wPF.getApprovedYearInd())));

        empHrBean.setOldLevelAndStep(empHrBean.getAbstractEmployeeEntity().getSalaryInfo().getLevelStepStr());

        if (wEmpPayBean.isNewEntity())
            empHrBean.setOldSalary(0.0D);
        else {
            empHrBean.setOldSalary(wEmpPayBean.getTotalPay());
        }


        TransferApproval wTA = this.genericService.loadObjectById(TransferApproval.class, pTid);


        String actionCompleted;

        if (wTA.getSchoolInfo() != null && !wTA.getSchoolInfo().isNewEntity()) {
               actionCompleted = bc.getStaffTypeName() + " " + empHrBean.getAbstractEmployeeEntity().getDisplayName() + " Scheduled for Transfer to " + wTA.getSchoolInfo().getName() + " Successfully";

            empHrBean.setProposedSchool(wTA.getSchoolInfo().getName());
        } else {
           actionCompleted = bc.getStaffTypeName() + " " + empHrBean.getAbstractEmployeeEntity().getDisplayName() + " Scheduled for Transfer to " + wTA.getMdaDeptMap().getMdaInfo().getName() + " Successfully";

            empHrBean.setProposedMda(wTA.getMdaDeptMap().getMdaInfo().getName());
        }

            model.addAttribute(SAVED_MSG, actionCompleted);

        model.addAttribute("saved", true);
        model.addAttribute("needsApproval", true);
        model.addAttribute("warningIssued", false);
        model.addAttribute("roleBean", bc);
        model.addAttribute("miniBean", empHrBean);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"tlid", "s", "sn"})
    public String setupForm(@RequestParam("tlid") Long pTlid, @RequestParam("s") int pSaved, @RequestParam("sn") String pSchoolInd, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (pTlid <= 0) {
            return REDIRECT_TO_DASHBOARD;
        }

        TransferLog wTL = this.genericService.loadObjectById(TransferLog.class, pTlid);
        Long pId;
        if (bc.isPensioner())
            pId = wTL.getPensioner().getId();
        else
            pId = wTL.getEmployee().getId();
        HiringInfo empHrBean = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pId),
                getBusinessClientIdPredicate(request)));

        PayrollFlag wPF = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));

        AbstractPaycheckEntity wEmpPayBean = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("employee.id", pId), CustomPredicate.procurePredicate("runMonth", wPF.getApprovedMonthInd()), CustomPredicate.procurePredicate("runYear", wPF.getApprovedYearInd())));

        empHrBean.setOldLevelAndStep(empHrBean.getAbstractEmployeeEntity().getSalaryInfo().getLevelStepStr());

        if (wEmpPayBean.isNewEntity())
            empHrBean.setOldSalary(0.0D);
        else {
            empHrBean.setOldSalary(wEmpPayBean.getTotalPay());
        }


        empHrBean.setProposedSchool(wTL.getOldMda());


        String actionCompleted = "Employee " + empHrBean.getAbstractEmployeeEntity().getDisplayName() + " Transferred to '" + empHrBean.getAbstractEmployeeEntity().getSchoolInfo().getName() + "' Successfully";


        model.addAttribute("warningIssued", false);
        model.addAttribute(SAVED_MSG, actionCompleted);
        model.addAttribute("saved", Boolean.valueOf(pSaved == 1));
        model.addAttribute("roleBean", bc);
        model.addAttribute("miniBean", empHrBean);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("miniBean") HiringInfo pEHB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:searchEmpForTransfer.do";
        }

        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", ON)));
        if (!wPRB.isNewEntity() && wPRB.isRunning()) {

            result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". Transfers can not be effected during a Payroll Run");
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("roleBean", bc);
            model.addAttribute("miniBean", pEHB);
            if (!IppmsUtils.isNullOrLessThanOne(pEHB.getMdaId())) {

                model.addAttribute("departmentList", this.genericService.loadAllObjectsWithSingleCondition(Department.class, getBusinessClientIdPredicate(request), "name"));
                MdaInfo wMdaInfo = this.genericService.loadObjectById(MdaInfo.class, pEHB.getMdaId());
                if (wMdaInfo.isSchoolAttached()) {
                    //Try and Load the Schools....
                    model.addAttribute("schoolList", this.genericService.loadAllObjectsWithSingleCondition(SchoolInfo.class, CustomPredicate.procurePredicate("mdaInfo.id", wMdaInfo.getId()), "name"));
                    pEHB.setRowVisibility(SHOW_ROW);
                }   else {
                    pEHB.setRowVisibility(HIDE_ROW);
                }

            }

            return VIEW_NAME;
        }

        validator.validate(pEHB, result, bc);

        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            if (!IppmsUtils.isNullOrLessThanOne(pEHB.getMdaId())) {

                model.addAttribute("departmentList", this.genericService.loadAllObjectsWithSingleCondition(Department.class, getBusinessClientIdPredicate(request), "name"));
                MdaInfo wMdaInfo = this.genericService.loadObjectById(MdaInfo.class, pEHB.getMdaId());
                if (wMdaInfo.isSchoolAttached()) {
                    //Try and Load the Schools....
                    model.addAttribute("schoolList", this.genericService.loadAllObjectsWithSingleCondition(SchoolInfo.class, CustomPredicate.procurePredicate("mdaInfo.id", wMdaInfo.getId()), "name"));
                    pEHB.setRowVisibility(SHOW_ROW);
                }else{
                    pEHB.setRowVisibility(HIDE_ROW);
                }
            }
            model.addAttribute("status", result);
            model.addAttribute("roleBean", bc);
            model.addAttribute("miniBean", pEHB);
            return VIEW_NAME;

        }
        TransferLog wPL = new TransferLog();
        if(pEHB.getSchoolTransfer() == 1){
         if(pEHB.getEmployee().getSchoolInfo() != null && !pEHB.getEmployee().getSchoolInfo().isNewEntity())
                wPL.setOldMda(pEHB.getEmployee().getSchoolInfo().getName());
         else
             wPL.setOldMda("No School");
        }else{
            if (bc.isPensioner())
                wPL.setOldMda(pEHB.getPensioner().getMdaDeptMap().getMdaInfo().getName());
            else
                wPL.setOldMda(pEHB.getEmployee().getMdaDeptMap().getMdaInfo().getName());

        }

        MdaInfo wMdaInfo = this.genericService.loadObjectById(MdaInfo.class, pEHB.getMdaId());
        SchoolInfo wSI = new SchoolInfo();
        if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getSchoolId()))
            wSI = this.genericService.loadObjectById(SchoolInfo.class, pEHB.getSchoolId());

        String wNewMdaName = wMdaInfo.getName();
        String wNewSchoolName = wSI.getName();


        //Here check if this guy can approve it....
        Long pEmpId;
        if (!bc.isSuperAdmin()) {
            //Place in approval bucket...but log it.

            TransferApproval wTA = new TransferApproval();
            if (bc.isPensioner()) {
                wTA.setPensioner(pEHB.getPensioner());
                pEmpId = pEHB.getPensioner().getId();
            } else {
                wTA.setEmployee(pEHB.getEmployee());
                pEmpId = pEHB.getEmployee().getId();
            }
            wTA.setInitiator(new User(bc.getLoginId()));
            wTA.setInitiatedDate(LocalDate.now());
            wTA.setTransferDate(pEHB.getTransferDate());
            wTA.setLastModTs(LocalDate.now());
            wTA.setMdaDeptMap(this.genericService.loadObjectUsingRestriction(MdaDeptMap.class, Arrays.asList(CustomPredicate.procurePredicate("mdaInfo.id", pEHB.getMdaId()), CustomPredicate.procurePredicate("department.id", pEHB.getDepartmentId()),
                    getBusinessClientIdPredicate(request))));
            if(wSI != null && !wSI.isNewEntity())
                 wTA.setSchoolInfo(wSI);
            else
                wTA.setSchoolInfo(null);
            wTA.setAuditTime(PayrollBeanUtils.getCurrentTime());
            wTA.setBusinessClientId(bc.getBusinessClientInstId());
            if(!wSI.isNewEntity())
                wTA.setObjectInd(1);
            wTA.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(bc.getLoginId()));
            wTA.setEntityName(pEHB.getAbstractEmployeeEntity().getDisplayName());
            wTA.setEntityId(pEHB.getParentId());
            wTA.setEmployeeId(pEHB.getAbstractEmployeeEntity().getEmployeeId());
            this.genericService.saveObject(wTA);
            //create Notification
            NotificationService.storeNotification(bc,genericService,wTA,"requestNotification.do?arid="+wTA.getId()+"&s=1&oc="+IConstants.TRANSFER_REQUEST_URL_IND,"Transfer Request", IConstants.TRANSFER_REQUEST_URL_IND);
            return "redirect:transferEmployee.do?eid=" + pEmpId + "&s=1&tid=" + wTA.getId();
        } else {
            //--
            boolean updatePendingPaychecks = false;

            if (pEHB.isShowOverride()) {
                if (pEHB.getOverrideInd() == 0) {
                    result.rejectValue("", "Override.Unset", "Please select a value for 'Effect on Pending Payslip*'.");
                    addDisplayErrorsToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("roleBean", bc);
                    model.addAttribute("miniBean", pEHB);
                    if (!IppmsUtils.isNullOrLessThanOne(pEHB.getMdaId())) {

                        model.addAttribute("departmentList", this.genericService.loadAllObjectsWithSingleCondition(Department.class, getBusinessClientIdPredicate(request), "name"));
                        wMdaInfo = this.genericService.loadObjectById(MdaInfo.class, pEHB.getMdaId());
                        if (wMdaInfo.isSchoolAttached()) {
                            //Try and Load the Schools....
                            model.addAttribute("schoolList", this.genericService.loadAllObjectsWithSingleCondition(SchoolInfo.class, CustomPredicate.procurePredicate("mdaInfo.id", wMdaInfo.getId()), "name"));

                        }
                    }

                    return VIEW_NAME;
                } else {
                    if (pEHB.getOverrideInd() == 1)
                        updatePendingPaychecks = true;

                }
            }
            MdaDeptMap m = this.genericService.loadObjectUsingRestriction(MdaDeptMap.class, Arrays.asList(CustomPredicate.procurePredicate("mdaInfo.id", pEHB.getMdaId()), CustomPredicate.procurePredicate("department.id", pEHB.getDepartmentId()),
                    getBusinessClientIdPredicate(request)));

            pEmpId = transferSingleEmployee(pEHB, m, bc);
            AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService,pEmpId,bc);
            if (updatePendingPaychecks) {
                LocalDate wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);

                Long wSchoolInstId = null;

                if (wEmp.getSchoolInfo() != null && !wEmp.getSchoolInfo().isNewEntity()) {
                    wSchoolInstId = wEmp.getSchoolInfo().getId();
                    wPL.setSchoolInfo(new SchoolInfo(wSchoolInstId));
                }
                this.paycheckService.updateMdaForPendingPaycheck(wEmp , wCal , wSchoolInstId, bc);
            }
            if(pEHB.getSchoolTransfer() == 1){
                wPL.setNewMda(wNewSchoolName);
            }else{
                wPL.setNewMda(wNewMdaName);
            }

            if(bc.isPensioner())
                wPL.setPensioner(new Pensioner(wEmp.getId()));
            else
                wPL.setEmployee(new Employee(wEmp.getId()));

            //wPL.setEmployeeInstId(pEHB.getEmployeeInstId());
            wPL.setName(wEmp.getDisplayNameWivTitlePrefixed());
            wPL.setTransferDate(pEHB.getTransferDate());
            wPL.setBusinessClientId(bc.getBusinessClientInstId());
            wPL.setUser(new User(bc.getLoginId()));
            wPL.setMdaInfo(m.getMdaInfo());
            wPL.setObjectInd(m.getMdaInfo().getMdaType().getMdaTypeCode());
            wPL.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
            wPL.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService,bc));
            wPL.setSalaryInfo(pEHB.getAbstractEmployeeEntity().getSalaryInfo());
            Long wLid = this.genericService.storeObject(wPL);



            if (wNewSchoolName == null)
                return "redirect:transferEmployee.do?tlid=" + wLid + "&s=1";
            else
                return "redirect:transferEmployee.do?tlid=" + wLid + "&s=1&sn=t";
        }


    }

    private Long transferSingleEmployee(HiringInfo pEHB, MdaDeptMap m, BusinessCertificate bc) {

        if(bc.isPensioner()){
            pEHB.getPensioner().setMdaDeptMap(m);
            pEHB.getPensioner().setLastModBy(new User(bc.getLoginId()));
            pEHB.getPensioner().setLastModTs(Timestamp.from(Instant.now()));
            return this.genericService.storeObject(pEHB.getPensioner());
        }else{
            pEHB.getEmployee().setMdaDeptMap(m);
            if(IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getSchoolId()))
                pEHB.getEmployee().setSchoolInfo(new SchoolInfo(pEHB.getSchoolId()));
            else
                pEHB.getEmployee().setSchoolInfo(null);

            pEHB.getEmployee().setLastModBy(new User(bc.getLoginId()));
            pEHB.getEmployee().setLastModTs(Timestamp.from(Instant.now()));
            return  this.genericService.storeObject(pEHB.getEmployee());
        }

    }
}

