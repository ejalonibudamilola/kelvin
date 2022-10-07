package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.employee.SuspendedStaffReportController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.hr.ReabsorbFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.suspension.AbsorptionLog;
import com.osm.gnl.ippms.ogsg.domain.suspension.AbsorptionReason;
import com.osm.gnl.ippms.ogsg.domain.suspension.PayrollReabsorptionTracker;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.hr.ReabsorbingValidator;
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
import java.time.Period;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping({"/reabsorbEmployee.do"})
@SessionAttributes(types={HrMiniBean.class})
public class AbsorptionController extends BaseController
{
  
	 

    private final PaycheckService paycheckService;

    private final ReabsorbingValidator validator;
	private final String VIEW_NAME = "suspension/absorptionForm";
  
  @ModelAttribute("absorptionReason")
  public List<AbsorptionReason> makeAbsorptionReasonList(HttpServletRequest request) {
    return genericService.loadAllObjectsWithSingleCondition(AbsorptionReason.class,getBusinessClientIdPredicate(request), "name");
  }
  @Autowired
  public AbsorptionController(PaycheckService paycheckService, ReabsorbingValidator validator)
  {
    this.paycheckService = paycheckService;
    this.validator = validator;
  }

  

  
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid"})
  public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    if (IppmsUtils.isNullOrLessThanOne(pEmpId))
    {
      return REDIRECT_TO_DASHBOARD;
    }

    HrMiniBean empHrBean = new HrMiniBean();

    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,pEmpId);

    if ((wHI.isNewEntity()) || (wHI.isTerminated()))
    {
      return REDIRECT_TO_DASHBOARD;
    }

    empHrBean = setRequiredValues(wHI, empHrBean,bc);
    empHrBean.setPayArrearsInd("0");
    empHrBean.setHideRow1(HIDE_ROW);
    empHrBean.setHideRow2(HIDE_ROW);
    empHrBean.setHideRow3(HIDE_ROW);

    empHrBean.setTerminate(true);
    empHrBean.setWarningIssued(false);
    empHrBean.setSuperAdmin(bc.isSuperAdmin());
    return this.makeModelAndReturnView(model,request,bc,empHrBean,null);
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid", "s"})
  public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    HrMiniBean empHrBean = new HrMiniBean();

    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,pEmpId);

    empHrBean = setRequiredValues(wHI, empHrBean,bc);
    empHrBean.setPayArrearsInd("0");
    empHrBean.setHideRow1(HIDE_ROW);
    empHrBean.setHideRow2(HIDE_ROW);
    empHrBean.setHideRow3(HIDE_ROW);

    empHrBean.setTerminate(true);
    empHrBean.setWarningIssued(false);
    empHrBean.setSuperAdmin(bc.isSuperAdmin());
    ArrayList<CustomPredicate> customPredicates = new ArrayList<>();
    customPredicates.add(CustomPredicate.procurePredicate("auditPayPeriod",PayrollUtils.makePayPeriodForAuditLogs(genericService,bc)));
    customPredicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),pEmpId));
    customPredicates.add(getBusinessClientIdPredicate(request));
    AbsorptionLog s =  this.genericService.loadObjectUsingRestriction(AbsorptionLog.class, customPredicates);
    empHrBean.setTerminateReasonId(s.getAbsorptionReason().getId());

    String actionCompleted = bc.getStaffTypeName()+" " + wHI.getAbstractEmployeeEntity().getDisplayName() + " Absorbed Successfully.";

    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
    model.addAttribute("saved", true);
    return this.makeModelAndReturnView(model,request,bc,empHrBean,null);

  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("employee") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {
      if(Navigator.getInstance(bc.getSessionId()).getFromClass().isAssignableFrom(SuspendedStaffReportController.class)){
        return Navigator.getInstance(bc.getSessionId()).getFromForm();
      }
      return "redirect:hrEmployeeFunctionalities.do";
    }
    PayrollRunMasterBean wPRB = IppmsUtilsExt.loadCurrentlyRunningPayroll(genericService,bc);
    if(!wPRB.isNewEntity() && wPRB.isRunning()){
  	  result.rejectValue("", "No.Employees", "Payroll is currently being run by "+wPRB.getInitiator().getActualUserName()+". Re-absorption can not be effected during a Payroll Run");
      return this.makeModelAndReturnView(model,request,bc,pEHB,result);
    }
    validator.validate(pEHB, result, bc,paycheckService);
    if (result.hasErrors()) {

      if (!IppmsUtils.treatNull(pEHB.getPayArrearsInd()).equals("")) {
        switch (Integer.valueOf(pEHB.getPayArrearsInd()).intValue()) {
          case 1:
          pEHB.setHideRow1(HIDE_ROW);
          pEHB.setHideRow2(SHOW_ROW);
          pEHB.setHideRow3(SHOW_ROW);
          break;
        case 2:
          pEHB.setHideRow1(SHOW_ROW);
          pEHB.setHideRow2(SHOW_ROW);
          pEHB.setHideRow3(SHOW_ROW);
          break;
        default:
          pEHB.setHideRow1(HIDE_ROW);
          pEHB.setHideRow2(HIDE_ROW);
          pEHB.setHideRow3(HIDE_ROW);
        }
      }
      return this.makeModelAndReturnView(model,request,bc,pEHB,result);

    }
    if (!pEHB.isWarningIssued()) {
      result.rejectValue("", "warning", "Click 'Absorb '"+bc.getStaffTypeName()+"' to Reabsorb "+bc.getStaffTypeName()+", 'Cancel' to abort.");
      if (!IppmsUtils.treatNull(pEHB.getPayArrearsInd()).equals("")) {
        switch (Integer.valueOf(pEHB.getPayArrearsInd()).intValue()) {
          case 1:
          pEHB.setHideRow1(HIDE_ROW);
          pEHB.setHideRow2(SHOW_ROW);
          pEHB.setHideRow3(SHOW_ROW);
          break;
        case 2:
          pEHB.setHideRow1(SHOW_ROW);
          pEHB.setHideRow2(SHOW_ROW);
          pEHB.setHideRow3(SHOW_ROW);
          break;
        default:
          pEHB.setHideRow1(HIDE_ROW);
          pEHB.setHideRow2(HIDE_ROW);
          pEHB.setHideRow3(HIDE_ROW);
        }
      }

      pEHB.setWarningIssued(true);
      pEHB.setRefDate(LocalDate.now());
      pEHB.setRefNumber("REF_"+bc.getUserName()+"_"+PayrollBeanUtils.getDateAsStringWidoutSeparators(pEHB.getRefDate()));
      return this.makeModelAndReturnView(model,request,bc,pEHB,result);

    }else{
      if(pEHB.getRefDate() != null){
        PayrollFlag payrollFlag = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);
        LocalDate localDate = PayrollBeanUtils.getDateFromMonthAndYear(payrollFlag.getApprovedMonthInd(),payrollFlag.getApprovedYearInd(),true);
        LocalDate nextPayPeriod = PayrollBeanUtils.makeNextPayPeriod(localDate,localDate.getMonthValue(),localDate.getYear());
        pEHB.setArrearsEndDate(nextPayPeriod);
        if(!(pEHB.getRefDate().getYear() == nextPayPeriod.getYear() && pEHB.getRefDate().getMonth().equals(nextPayPeriod.getMonth()))){
          result.rejectValue("", "warning", "Reference Date must be in the Month of "+nextPayPeriod.getMonth()+" of Year "+nextPayPeriod.getYear());
          return makeModelAndReturnView(model,request,bc,pEHB,result);
        }
      }else{
        result.rejectValue("", "warning", "Please enter a value for Reference Date ");
        return makeModelAndReturnView(model,request,bc,pEHB,result);
      }
    }

    HiringInfo wHireInfo = loadHiringInfoById(request,bc,pEHB.getId());

    int payArrears;
    try {
      payArrears = Integer.parseInt(pEHB.getPayArrearsInd());
    } catch (Exception wEx) {
      payArrears = 0;
    }

    ArrayList<CustomPredicate> customPredicates = new ArrayList<>();
    customPredicates.add(CustomPredicate.procurePredicate("auditPayPeriod",PayrollUtils.makePayPeriodForAuditLogs(genericService,bc)));
    customPredicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),pEHB.getEmployeeInstId()));
    customPredicates.add(getBusinessClientIdPredicate(request));
    AbsorptionLog s =  this.genericService.loadObjectUsingRestriction(AbsorptionLog.class, customPredicates);

    boolean increment = s.isNewEntity();
    if(increment) {
      if (bc.isPensioner())
        s.setPensioner(new Pensioner(wHireInfo.getAbstractEmployeeEntity().getId()));
      else
        s.setEmployee(new Employee(wHireInfo.getAbstractEmployeeEntity().getId()));

        s.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService,bc));
        s.setBusinessClientId(bc.getBusinessClientInstId());
    }
    s.setAbsorptionReason(new AbsorptionReason(pEHB.getTerminateReasonId()));
    s.setSuspensionDate(wHireInfo.getSuspensionDate());
    s.setPayArrearsInd(payArrears);
    s.setAbsorptionDate(pEHB.getRefDate());
    s.setArrearsStartDate(pEHB.getArrearsStartDate());
    s.setArrearsEndDate(pEHB.getArrearsEndDate());
    s.setReferenceNumber(pEHB.getRefNumber());
    s.setLastModTs(LocalDate.now());
    s.setUser(new User(bc.getLoginId()));
    s.setSalaryInfo(wHireInfo.getAbstractEmployeeEntity().getSalaryInfo());
    s.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
    s.setMdaInfo(wHireInfo.getAbstractEmployeeEntity().getMdaDeptMap().getMdaInfo());
    if(wHireInfo.getAbstractEmployeeEntity().isSchoolStaff()){
    	s.setSchoolInfo(wHireInfo.getEmployee().getSchoolInfo());
    }

    this.genericService.saveObject(s);

    if (s.isPayArrears())
    {

      PayrollFlag p = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);
      int _monthInd = LocalDate.now().getMonthValue();
      boolean goToNextYear = false;
      if (!p.isNewEntity()) {
        if (p.getApprovedMonthInd() == 12) {
          _monthInd = 1;
          goToNextYear = true;
        } else {
          _monthInd = p.getApprovedMonthInd() + 1;
        }
      }

      int yearToUse;

      if (!goToNextYear)
      {
        yearToUse = p.getApprovedYearInd();
      }
      else
      {
        yearToUse = p.getApprovedYearInd() + 1;
      }

      customPredicates = new ArrayList<>();
      customPredicates.add(CustomPredicate.procurePredicate("monthInd",_monthInd));
      customPredicates.add(CustomPredicate.procurePredicate("yearInd",yearToUse));
      customPredicates.add(CustomPredicate.procurePredicate("absorptionLog.id",s.getId()));
      customPredicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),pEHB.getEmployeeInstId()));
     // customPredicates.add(getBusinessClientIdPredicate(request));
      PayrollReabsorptionTracker wPPT = this.genericService.loadObjectUsingRestriction(PayrollReabsorptionTracker.class, customPredicates);
      increment = wPPT.isNewEntity();
      if(increment){
        if(wHireInfo.isPensionerType())
          wPPT.setPensioner(new Pensioner(wHireInfo.getAbstractEmployeeEntity().getId()));
        else
          wPPT.setEmployee(new Employee(wHireInfo.getAbstractEmployeeEntity().getId()));

        wPPT.setMonthInd(_monthInd);
        wPPT.setYearInd(yearToUse);
        wPPT.setAbsorptionLog(s);
      }
      wPPT.setNoOfDays(Integer.parseInt(Period.between(pEHB.getArrearsStartDate(),pEHB.getArrearsEndDate()).toString()));
      if (s.getPayArrearsInd() == 2)
        wPPT.setPercentage(Double.parseDouble(parseIt(pEHB.getArrearsPercentageStr())));
      else {
        wPPT.setPercentage(0.0D);
      }
      wPPT.setUser(new User(bc.getLoginId()));

      this.genericService.saveObject(wPPT);
    }

    wHireInfo.setLastModBy(new User(bc.getLoginId()));
    wHireInfo.setLastModTs(Timestamp.from(Instant.now()));
    wHireInfo.setSuspended(0);
    wHireInfo.setSuspensionDate(null);

    this.genericService.saveObject(wHireInfo);
    if(increment) {
      ReabsorbFlag reabsorbFlag = new ReabsorbFlag();
      reabsorbFlag.setBusinessClientId(bc.getBusinessClientInstId());
      if (wHireInfo.isPensionerType())
        reabsorbFlag.setPensioner(wHireInfo.getPensioner());
      else
        reabsorbFlag.setEmployee(wHireInfo.getEmployee());
      LocalDate _wCal = paycheckService.getPendingPaycheckRunMonthAndYear(bc);
      if (_wCal != null) {
        RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService, bc.getBusinessClientInstId(), _wCal);
        wRPB.setNoOfReabsorptions(wRPB.getNoOfReabsorptions() + 1);
        if (wRPB.isNewEntity()) {
          wRPB.setRunMonth(_wCal.getMonthValue());
          wRPB.setRunYear(_wCal.getYear());
          wRPB.setRerunInd(IConstants.ON);
          wRPB.setBusinessClientId(bc.getBusinessClientInstId());
        }
        this.genericService.storeObject(wRPB);
        reabsorbFlag.setRunMonth(_wCal.getMonthValue());
        reabsorbFlag.setRunYear(_wCal.getYear());
      } else {
        PayrollFlag payrollFlag = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);
        if (payrollFlag.getApprovedMonthInd() == 12) {
          reabsorbFlag.setRunMonth(1);
          reabsorbFlag.setRunYear(payrollFlag.getApprovedYearInd() + 1);
        } else {
          reabsorbFlag.setRunMonth(payrollFlag.getApprovedMonthInd() + 1);
          reabsorbFlag.setRunYear(payrollFlag.getApprovedYearInd());
        }
      }

      this.genericService.storeObject(reabsorbFlag);
    }
    return "redirect:reabsorbEmployee.do?eid="+wHireInfo.getAbstractEmployeeEntity().getId()+"&s=1";
  }

  private String parseIt(String pArrearsPercentageStr)
  {
    StringBuffer wStrBuff = new StringBuffer();
    char[] wChar = pArrearsPercentageStr.toCharArray();
    int dotCount = 0;
    for (char c : wChar) {
      if (!Character.isDigit(c)) {
        if ((c != '.') ||
                (dotCount != 0)) continue;
        dotCount++;
      }
      wStrBuff.append(c);
    }

    return wStrBuff.toString();
  }

  private HrMiniBean setRequiredValues(HiringInfo pWhi, HrMiniBean pEmpHrBean, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {

    if(pEmpHrBean.getSuspensionLog() == null) {
      ArrayList<CustomPredicate> customPredicates = new ArrayList<>();
      customPredicates.add(CustomPredicate.procurePredicate("suspensionDate", pWhi.getSuspensionDate()));
      customPredicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pWhi.getParentId()));
      customPredicates.add(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
      SuspensionLog suspensionLog = this.genericService.loadObjectUsingRestriction(SuspensionLog.class, customPredicates);
      if (!suspensionLog.isNewEntity()) {
        pEmpHrBean.setSuspensionLog(suspensionLog);
        pEmpHrBean.setLogPresent(true);
      }
    }
	 pEmpHrBean.setName(pWhi.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed());
	 pEmpHrBean.setGradeLevelAndStep(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getSalaryScaleLevelAndStepStr());
     
    pEmpHrBean.setHireDate(PayrollHRUtils.getFullDateFormat().format(pWhi.getHireDate()));

    int noOfYears  = LocalDate.now().getYear() - pWhi.getHireDate().getYear();

    pEmpHrBean.setYearsOfService(String.valueOf(noOfYears));
    
    pEmpHrBean.setAssignedToObject(pWhi.getAbstractEmployeeEntity().getCurrentMdaName());
    pEmpHrBean.setEmployeeId(pWhi.getAbstractEmployeeEntity().getEmployeeId());
    pEmpHrBean.setLevelAndStepStr(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getLevelAndStepAsStr());
    pEmpHrBean.setSalaryScale(pWhi.getAbstractEmployeeEntity().getSalaryTypeName());
    pEmpHrBean.setId(pWhi.getId());

    if (!IppmsUtils.treatNull(pEmpHrBean.getPayArrearsInd()).equals("")) {
      switch (Integer.valueOf(pEmpHrBean.getPayArrearsInd()).intValue()) {
        case 1:
        pEmpHrBean.setHideRow1(HIDE_ROW);
        pEmpHrBean.setHideRow2(SHOW_ROW);
        pEmpHrBean.setHideRow3(SHOW_ROW);
        break;
      case 2:
        pEmpHrBean.setHideRow1(SHOW_ROW);
        pEmpHrBean.setHideRow2(SHOW_ROW);
        pEmpHrBean.setHideRow3(SHOW_ROW);
        break;
      default:
        pEmpHrBean.setHideRow1(HIDE_ROW);
        pEmpHrBean.setHideRow2(HIDE_ROW);
        pEmpHrBean.setHideRow3(HIDE_ROW);
      }

    }

    return pEmpHrBean;
  }
  private String makeModelAndReturnView(Model model, HttpServletRequest request, BusinessCertificate bc, HrMiniBean pEHB, BindingResult result){
    if(result != null){
      addDisplayErrorsToModel(model, request);
      model.addAttribute("status", result);
    }
    model.addAttribute("employee", pEHB);
    model.addAttribute("roleBean", bc);
    return VIEW_NAME;
  }
}