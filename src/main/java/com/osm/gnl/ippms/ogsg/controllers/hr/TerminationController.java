/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IPaycheckDao;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/terminateEmployee.do"})
@SessionAttributes(types = {HiringInfo.class})
public class TerminationController extends BaseController {


    private final IPaycheckDao paycheckService;

    private static final String VIEW_NAME = "hr/terminateEmpForm";


    @ModelAttribute("termReason")
    protected List<TerminateReason> loadTermReason() {
        return this.genericService.loadAllObjectsWithoutRestrictions(TerminateReason.class, "name");
    }

    @Autowired
    public TerminationController(IPaycheckDao paycheckService) {
        this.paycheckService = paycheckService;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (IppmsUtils.isNullOrLessThanOne(pEmpId)) {
            return REDIRECT_TO_DASHBOARD;
        }


        HiringInfo wHI = loadHiringInfoByEmpId(request, bc, pEmpId);

        if ((wHI.isNewEntity()) || (wHI.isTerminated())) {
            return REDIRECT_TO_DASHBOARD;
        }

        if(wHI.isPensionerType()){
            wHI.setActiveTermDate(wHI.getTerminateDate());
            wHI.setTerminateDate(wHI.getPensionEndDate());
        }

        model.addAttribute("miniBean", wHI);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;

    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"oid", "s"})
    public String setupForm(@RequestParam("oid") Long pOid, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);
        HiringInfo wHI = loadHiringInfoById(request, bc, pOid);

        String actionCompleted;
        if (wHI.isPensionerType())
            actionCompleted = bc.getStaffTypeName() + " " + wHI.getPensioner().getDisplayNameWivTitlePrefixed() + " Terminated Successfully.";
        else
            actionCompleted = bc.getStaffTypeName() + " " + wHI.getEmployee().getDisplayNameWivTitlePrefixed() + " Terminated Successfully.";

        model.addAttribute(SAVED_MSG, actionCompleted);
        model.addAttribute("saved", true);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wHI);

        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("miniBean") HiringInfo pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:hrEmployeeFunctionalities.do";
        }
        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", ON)));

        if (!wPRB.isNewEntity() && wPRB.isRunning()) {
            result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". Terminations can not be effected during a Payroll Run");
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);

            return VIEW_NAME;
        }
        Long pEmpId;
        if (bc.isPensioner())
            pEmpId = pEHB.getPensioner().getId();
        else
            pEmpId = pEHB.getEmployee().getId();


        if (IppmsUtils.isNullOrLessThanOne(pEHB.getTerminateReason().getId())) {
            result.rejectValue("", "Error", "Please select a reason for Termination");
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);

            return VIEW_NAME;
        } else {
            if (bc.isPensioner()) {
                TerminateReason terminateReason = this.genericService.loadObjectById(TerminateReason.class, pEHB.getTerminateReason().getId());
                if (!terminateReason.isNotReinstateable()) {
                    result.rejectValue("", "Error", "Termination Reason for Pensioners must be 'Death'");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("miniBean", pEHB);

                    return VIEW_NAME;
                }
            }
        }
        if (pEHB.getTerminateDate() == null) {
            result.rejectValue("", "Error", "Please select a value for Termination Effective Date");

            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            return VIEW_NAME;
        }


        LocalDate wStartMonth = LocalDate.now();

        if (pEHB.getTerminateDate().compareTo(wStartMonth) < 0) {
            if (IppmsUtilsExt.paycheckExistsForEmployee(genericService, bc, pEHB.getTerminateDate().getYear(), pEHB.getTerminateDate().getMonthValue(), pEmpId)) {
                LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
                if (_wCal != null) {
                    if (pEHB.getTerminateDate().getYear() == _wCal.getYear()
                            && (pEHB.getTerminateDate().getMonthValue() == _wCal.getMonthValue())) {
                        //FORCE A RERUN.
                        RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(
                                CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", _wCal.getYear()),
                                getBusinessClientIdPredicate(request)));
                        wRPB.setNoOfTerminations(wRPB.getNoOfTerminations() + 1);
                        if (wRPB.isNewEntity()) {
                            wRPB.setRunMonth(_wCal.getMonthValue());
                            wRPB.setRunYear(_wCal.getYear());
                            wRPB.setRerunInd(ON);
                            wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                        }
                        this.genericService.storeObject(wRPB);
                    }

                } else {
                    result.rejectValue("", "Error", bc.getStaffTypeName() + " has Paycheck Information for this period. "
                            + bc.getStaffTypeName() + " can not be terminated on this date " + PayrollHRUtils.getDisplayDateFormat().format(pEHB.getTerminateDate()));
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("miniBean", pEHB);
                    return VIEW_NAME;
                }

            }

        }

        if (!pEHB.isTerminationWarningIssued()) {
            if (bc.isPensioner()) {
                result.rejectValue("", "warning", "You are about to terminate this " + bc.getStaffTypeName() + "! Terminating a Pensioner is IRREVERSIBLE. Click 'Terminate' to continue, 'Cancel' to abort.");

            } else {
                if(pEHB.isContractStaff()){
                    if(!pEHB.isContractExpired()){
                        result.rejectValue("", "warning", bc.getStaffTypeName() + " Has an Active Contract. If you proceed with Termination, Contract will be Auto-Terminated.");

                    }
                }
                result.rejectValue("", "warning", "You are about to terminate this " + bc.getStaffTypeName() + "! Click 'Terminate' to continue, 'Cancel' to abort.");

            }
            pEHB.setTerminationWarningIssued(true);
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            return VIEW_NAME;
        }

        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        pEHB.setTerminateInactive("Y");
        if(bc.isPensioner()){
            pEHB.setPensionEndDate(pEHB.getTerminateDate());
            pEHB.setTerminateDate(pEHB.getActiveTermDate());
        }

        if (pEHB.isContractStaff()) {
            PayrollUtils.expireContract(pEHB,this.genericService, bc.getLoginId() );
        }else{
            this.genericService.saveObject(pEHB);
        }

        return "redirect:terminateEmployee.do?oid=" + pEHB.getId() + "&s=1";
    }


}