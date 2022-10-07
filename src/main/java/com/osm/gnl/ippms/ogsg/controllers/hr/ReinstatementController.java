package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.hr.ReinstateFlag;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.suspension.PayrollReinstatementTracker;
import com.osm.gnl.ippms.ogsg.domain.suspension.ReinstatementLog;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.validators.hr.ReinstatementValidator;
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
import java.util.*;


@Controller
@RequestMapping({"/reinstateEmployee.do"})
@SessionAttributes(types = {HrMiniBean.class})
public class ReinstatementController extends BaseController {


    private static final String VIEW_NAME = "hr/reinstateEmployeeForm";
    private final PromotionService promotionService;
    private final PaycheckService paycheckService;
    private final ReinstatementValidator reinstatementValidator;

    @Autowired
    public ReinstatementController(PromotionService promotionService, PaycheckService paycheckService, ReinstatementValidator reinstatementValidator) {
        this.promotionService = promotionService;
        this.paycheckService = paycheckService;
        this.reinstatementValidator = reinstatementValidator;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception  {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (pEmpId <= 0) {
            return REDIRECT_TO_DASHBOARD;
        }
        HrMiniBean empHrBean = new HrMiniBean();

        empHrBean.setHideRow1(HIDE_ROW);
        empHrBean.setHideRow2(HIDE_ROW);
        empHrBean.setHideRow3(HIDE_ROW);

        HiringInfo wHI = loadHiringInfoByEmpId(request, bc, pEmpId);

        List wSalaryInfo;
        if (bc.isLocalGovt()) {
            wSalaryInfo = promotionService.loadSalaryInfoBySalaryScaleAndFilterLGA(wHI.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getId(), wHI.getAbstractEmployeeEntity().getSalaryInfo().getStep(), wHI.getAbstractEmployeeEntity().getRank(), bc.getBusinessClientInstId());

        } else {
            PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(getBusinessClientIdPredicate(request));
           predicateBuilder.addPredicate(CustomPredicate.procurePredicate("level", wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevel(), Operation.GREATER_OR_EQUAL));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("salaryType.id", wHI.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getId()));


            wSalaryInfo = this.genericService.getObjectsFromBuilder(predicateBuilder, SalaryInfo.class);

        }
        Long wSalaryInfoId;

            wSalaryInfo.add(wHI.getAbstractEmployeeEntity().getSalaryInfo());
            wSalaryInfoId = wHI.getAbstractEmployeeEntity().getSalaryInfo().getId();

        Collections.sort(wSalaryInfo, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
        empHrBean = setRequiredValues(wHI, empHrBean);

        empHrBean.setTerminate(true);
        empHrBean.setWarningIssued(false);
        empHrBean.setSuperAdmin(bc.isSuperAdmin());
        empHrBean.setParentId(bc.getLoginId());
        empHrBean.setLevelStepList(wSalaryInfo);
        empHrBean.setSalaryInfoInstId(wSalaryInfoId);
        empHrBean.setEmployeeInstId(pEmpId);

        empHrBean.setShowForConfirm(HIDE_ROW);

        model.addAttribute("reinstateBean", empHrBean);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid", "s"})
    public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception  {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (IppmsUtils.isNullOrLessThanOne(pEmpId)) {
            return REDIRECT_TO_DASHBOARD;
        }
        HrMiniBean empHrBean = new HrMiniBean();

        empHrBean.setHideRow1(HIDE_ROW);
        empHrBean.setHideRow2(HIDE_ROW);
        empHrBean.setHideRow3(HIDE_ROW);

        HiringInfo wHI = loadHiringInfoById(request, bc, pEmpId);


        List wSalaryInfo;
        if (bc.isLocalGovt()) {
            wSalaryInfo = promotionService.loadSalaryInfoBySalaryScaleAndFilterLGA(wHI.getEmployee().getSalaryInfo().getSalaryType().getId(), wHI.getEmployee().getSalaryInfo().getStep(), wHI.getEmployee().getRank(), bc.getBusinessClientInstId());

        } else {
                PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(getBusinessClientIdPredicate(request));

                predicateBuilder.addPredicate(CustomPredicate.procurePredicate("level", wHI.getAbstractEmployeeEntity().getSalaryInfo().getLevel(), Operation.GREATER_OR_EQUAL));
                predicateBuilder.addPredicate(CustomPredicate.procurePredicate("salaryType.id", wHI.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getId()));




            wSalaryInfo = this.genericService.getObjectsFromBuilder(predicateBuilder, SalaryInfo.class);

        }
        empHrBean = setRequiredValues(wHI, empHrBean);
        Collections.sort(wSalaryInfo, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
        empHrBean.setTerminate(true);
        empHrBean.setWarningIssued(false);
        empHrBean.setSuperAdmin(bc.isSuperAdmin());
        empHrBean.setParentId(bc.getLoginId());
        empHrBean.setLevelStepList(wSalaryInfo);
        empHrBean.setSalaryInfoInstId(wHI.getEmployee().getSalaryInfo().getId());

        empHrBean.setShowForConfirm(HIDE_ROW);
        String wName;
        if (bc.isPensioner())
            wName = wHI.getPensioner().getDisplayNameWivTitlePrefixed();
        else
            wName = wHI.getEmployee().getDisplayNameWivTitlePrefixed();

        String actionCompleted = bc.getStaffTypeName() + " [ " + wName + " ] - Reinstated Successfully.";
        model.addAttribute("roleBean", bc);
        model.addAttribute(SAVED_MSG, actionCompleted);
        model.addAttribute("saved", Boolean.valueOf(pSaved == 1));
        model.addAttribute("reinstateBean", empHrBean);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("reinstateBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (super.isCancelRequest(request, cancel)) {
            return "redirect:hrEmployeeFunctionalities.do";
        }
        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", ON)));

        if (!wPRB.isNewEntity() && wPRB.isRunning()) {
            result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". Reinstatement can not be effected during a Payroll Run");
            pEHB = determineButtonClicked(pEHB);
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("employee", pEHB);
            return VIEW_NAME;
        }
        reinstatementValidator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            pEHB = determineButtonClicked(pEHB);
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("employee", pEHB);
            return VIEW_NAME;
        }

        if (!pEHB.isWarningIssued()) {
            pEHB = determineButtonClicked(pEHB);
            pEHB.setRefNumber(bc.getUserName());
            pEHB.setRefDate(LocalDate.now());
            pEHB.setShowForConfirm(SHOW_ROW);
            result.rejectValue("", "warning", "Click 'Reinstate' Button to Reinstate "+bc.getStaffTypeName()+", 'Cancel' to abort.");
            pEHB.setWarningIssued(true);
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);

            model.addAttribute("status", result);
            model.addAttribute("employee", pEHB);
            return VIEW_NAME;
        }

        HiringInfo wHireInfo = loadHiringInfoById(request, bc, pEHB.getId());

        int payArrears;
        try {
            payArrears = Integer.parseInt(pEHB.getPayArrearsInd());
        } catch (Exception wEx) {
            payArrears = 0;
        }

        ReinstatementLog s = new ReinstatementLog();

        s.setMdaInfo(wHireInfo.getAbstractEmployeeEntity().getMdaDeptMap().getMdaInfo());
        s.setSalaryInfo(wHireInfo.getAbstractEmployeeEntity().getSalaryInfo());
        if (bc.isPensioner()) {
            s.setPensioner(new Pensioner(wHireInfo.getParentId()));
        }else {
            s.setEmployee(new Employee(wHireInfo.getParentId()));

        }
        s.setBusinessClientId(bc.getBusinessClientInstId());
        s.setAuditTime(PayrollBeanUtils.getCurrentTime());
        s.setTerminationDate(wHireInfo.getTerminateDate());

        s.setPayArrearsInd(payArrears);
        s.setReinstatementDate(pEHB.getRefDate());
        s.setArrearsStartDate(pEHB.getArrearsStartDate());
        s.setArrearsEndDate(pEHB.getArrearsEndDate());
        s.setReferenceNumber(pEHB.getRefNumber());
        s.setApprover(bc.getUserName());
        s.setLastModTs(LocalDate.now());
        if(!bc.isPensioner()){
            if (wHireInfo.getEmployee().isSchoolStaff()) {
                s.setSchoolInfo(wHireInfo.getEmployee().getSchoolInfo());
            }else{
                s.setSchoolInfo(null);
            }
        }
        else {
            s.setSchoolInfo(null);

        }
        s.setUser(new User(bc.getLoginId()));
        s.setEmployeeName(wHireInfo.getAbstractEmployeeEntity().getDisplayName());
        s.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService,bc));

        this.genericService.saveObject(s);

        if (s.isPayArrears()) {
            Calendar _startDate = new GregorianCalendar();
            Calendar _endDate = new GregorianCalendar();
            _startDate.set(pEHB.getArrearsStartDate().getYear(), pEHB.getArrearsStartDate().getMonthValue() - 1, pEHB.getArrearsStartDate().getMonthValue());
            _endDate.set(pEHB.getArrearsEndDate().getYear(), pEHB.getArrearsEndDate().getMonthValue() - 1, pEHB.getArrearsEndDate().getMonthValue());
            PayrollFlag p = genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));
            int _monthInd = Calendar.getInstance().get(2);
            boolean goToNextYear = false;
            if (!p.isNewEntity()) {
                if (p.getApprovedMonthInd() == 11) {
                    _monthInd = 0;
                    goToNextYear = true;
                } else {
                    _monthInd = p.getApprovedMonthInd() + 1;
                }
            }

            int yearToUse;

            if (!goToNextYear) {
                yearToUse = p.getApprovedYearInd();
            } else {
                yearToUse = p.getApprovedYearInd() + 1;
            }
            PayrollPayUtils _payUtilsClass = new PayrollPayUtils();

            PayrollReinstatementTracker wPPT = new PayrollReinstatementTracker();

            if(bc.isPensioner())
                wPPT.setPensioner(wHireInfo.getPensioner());
            else
                 wPPT.setEmployee(wHireInfo.getEmployee());

            wPPT.setNoOfDays(Integer.parseInt(Long.toString(_payUtilsClass.getWorkingDays(_endDate, _startDate))));

            wPPT.setMonthInd(_monthInd);

            wPPT.setSalaryInfo(wHireInfo.getEmployee().getSalaryInfo());

            wPPT.setYearInd(yearToUse);

            wPPT.setReinstatementLog(s);
            if (s.getPayArrearsInd() == 2)
                wPPT.setPercentage(Double.parseDouble(parseIt(pEHB.getArrearsPercentageStr())));
            else {
                wPPT.setPercentage(0.0D);
            }

            wPPT.setLastModBy(bc.getUserName());

            this.genericService.saveObject(wPPT);
        }

        wHireInfo.setLastModBy(new User(bc.getLoginId()));
        wHireInfo.setLastModTs(Timestamp.from(Instant.now()));
        wHireInfo.setTerminateDate(null);
        wHireInfo.setTerminateInactive("N");
        wHireInfo.setTerminateReason(null);

        this.genericService.saveObject(wHireInfo);
        if(bc.isPensioner()){
            wHireInfo.getPensioner().setSalaryInfo(new SalaryInfo(pEHB.getSalaryInfoInstId()));
            wHireInfo.getPensioner().setLastModBy(new User(bc.getLoginId()));
            wHireInfo.getPensioner().setLastModTs(Timestamp.from(Instant.now()));
            wHireInfo.getPensioner().setStatusIndicator(0);
            this.genericService.saveObject(wHireInfo.getPensioner());
        }else{
            wHireInfo.getEmployee().setSalaryInfo(new SalaryInfo(pEHB.getSalaryInfoInstId()));
            wHireInfo.getEmployee().setLastModBy(new User(bc.getLoginId()));
            wHireInfo.getEmployee().setLastModTs(Timestamp.from(Instant.now()));
            wHireInfo.getEmployee().setStatusIndicator(0);
            this.genericService.saveObject(wHireInfo.getEmployee());
        }


        ReinstateFlag reinstateFlag = new ReinstateFlag();
        reinstateFlag.setBusinessClientId(bc.getBusinessClientInstId());
        if(wHireInfo.isPensionerType())
            reinstateFlag.setPensioner(wHireInfo.getPensioner());
        else
            reinstateFlag.setEmployee(wHireInfo.getEmployee());

        LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
        if (_wCal != null) {
            //This means we have a Pending Paycheck. Set for strictly RERUN
            RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(
                    CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()),CustomPredicate.procurePredicate("runYear", _wCal.getYear()),
                    getBusinessClientIdPredicate(request)));
            wRPB.setNoOfReinstatements(wRPB.getNoOfReinstatements() + 1);
            if (wRPB.isNewEntity()) {
                wRPB.setRunMonth(_wCal.getMonthValue());
                wRPB.setRunYear(_wCal.getYear());
                wRPB.setRerunInd(ON);
                wRPB.setBusinessClientId(bc.getBusinessClientInstId());
            }
            this.genericService.saveObject(wRPB);
            reinstateFlag.setRunMonth(_wCal.getMonthValue());
            reinstateFlag.setRunYear(_wCal.getYear());
        }else{
            PayrollFlag payrollFlag = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);
            if(payrollFlag.getApprovedMonthInd() == 12) {
                reinstateFlag.setRunMonth(1);
                reinstateFlag.setRunYear(payrollFlag.getApprovedYearInd() + 1);
            }else{
                reinstateFlag.setRunMonth(payrollFlag.getApprovedMonthInd() + 1);
                reinstateFlag.setRunYear(payrollFlag.getApprovedYearInd());
            }

        }

        this.genericService.storeObject(reinstateFlag);

        return "redirect:reinstateEmployee.do?eid=" + pEHB.getId() + "&s=1";
    }

    private HrMiniBean determineButtonClicked(HrMiniBean pEHB) {
        if (pEHB.getPayArrearsInd().equalsIgnoreCase("0")) {
            pEHB.setHideRow1(HIDE_ROW);
            pEHB.setHideRow2(HIDE_ROW);
            pEHB.setHideRow3(HIDE_ROW);
        } else if (pEHB.getPayArrearsInd().equalsIgnoreCase("1")) {
            pEHB.setHideRow1(HIDE_ROW);
            pEHB.setHideRow2(SHOW_ROW);
            pEHB.setHideRow3(SHOW_ROW);
        } else {
            pEHB.setHideRow1(SHOW_ROW);
            pEHB.setHideRow2(SHOW_ROW);
            pEHB.setHideRow3(SHOW_ROW);
        }
        return pEHB;
    }

    private String parseIt(String pArrearsPercentageStr) {
        StringBuffer wStrBuff = new StringBuffer();
        char[] wChar = pArrearsPercentageStr.toCharArray();
        int dotCount = 0;
        for (char c : wChar) {
            if (Character.isDigit(c)) {
                wStrBuff.append(c);
            } else {
                if ((c != '.') ||
                        (dotCount != 0)) continue;
                dotCount++;
                wStrBuff.append(c);
            }
        }

        return wStrBuff.toString();
    }

    private HrMiniBean setRequiredValues(HiringInfo pWhi, HrMiniBean pEmpHrBean ) {


        pEmpHrBean.setHireDate(PayrollHRUtils.getFullDateFormat().format(pWhi.getHireDate()));
        if(pWhi.isTerminatedEmployee()) {
            if (pWhi.isPensionerType()) {
                pEmpHrBean.setYearsOfService(String.valueOf(pWhi.getYearsOnPension()));
                pEmpHrBean.setTerminateDate(PayrollHRUtils.getFullDateFormat().format(pWhi.getPensionEndDate()));
            } else {
                pEmpHrBean.setYearsOfService(String.valueOf(pWhi.getNoOfYearsInService()));
                pEmpHrBean.setTerminateDate(PayrollHRUtils.getFullDateFormat().format(pWhi.getTerminateDate()));
            }
        }


            pEmpHrBean.setName(pWhi.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed());
            pEmpHrBean.setAssignedToObject(pWhi.getAbstractEmployeeEntity().getAssignedToObject());
            pEmpHrBean.setEmployeeId(pWhi.getAbstractEmployeeEntity().getEmployeeId());
            pEmpHrBean.setGradeLevelAndStep(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getLevelAndStepAsStr());
            pEmpHrBean.setSalaryScale(pWhi.getAbstractEmployeeEntity().getSalaryTypeName());



        pEmpHrBean.setId(pWhi.getId());
        pEmpHrBean.setPayArrearsInd("0");

        return pEmpHrBean;
    }
}