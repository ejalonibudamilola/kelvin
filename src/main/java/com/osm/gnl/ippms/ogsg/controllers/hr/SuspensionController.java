package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionType;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/suspendEmployee.do"})
@SessionAttributes(types={HrMiniBean.class})
public class SuspensionController extends BaseController
{



  private final PaycheckService paycheckService;

  private final String VIEW_NAME = "suspension/suspendEmpForm";

  @Autowired
  public SuspensionController(PaycheckService paycheckService)
  {
    this.paycheckService = paycheckService;
  }

  @ModelAttribute("suspensionReason")
  public List<SuspensionType> makeSuspensionReasonList(HttpServletRequest request) {
     return genericService.loadAllObjectsWithSingleCondition(SuspensionType.class,getBusinessClientIdPredicate(request),"name");
  }

  
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid"})
  public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    if (pEmpId <= 0) {
      return REDIRECT_TO_DASHBOARD;
    }
    HrMiniBean empHrBean = new HrMiniBean();

    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,pEmpId);

    if ((wHI.isNewEntity()) || (wHI.isTerminated())) {
      return REDIRECT_TO_DASHBOARD;
    }

    empHrBean = setRequiredValues(wHI, empHrBean);
    empHrBean.setTerminate(true);
    empHrBean.setWarningIssued(false);
    empHrBean.setShowForConfirm(HIDE_ROW);

    return makeModelAndReturnView(model,request,bc,empHrBean,null);
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"slid","eid", "s"})
  public String setupForm(@RequestParam("slid") Long pSuspensionLogId,
                          @RequestParam("eid") Long pEmpId, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);


    if (IppmsUtils.isNullOrLessThanOne(pSuspensionLogId)) {
      return REDIRECT_TO_DASHBOARD;
    }
    HrMiniBean empHrBean = new HrMiniBean();

    SuspensionLog s = genericService.loadObjectUsingRestriction(SuspensionLog.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id",pSuspensionLogId)));

    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,pEmpId);

    if ((wHI.isNewEntity()) || (wHI.isTerminated())) {
      return REDIRECT_TO_DASHBOARD;
    }

    empHrBean = setRequiredValues(wHI, empHrBean);
    if (s.getPayPercentage() > 0.0D) {
      empHrBean.setPartPayable(true);
      empHrBean.setPartPayment(s.getPayPercentage());

    }
    empHrBean.setRefNumber(s.getReferenceNumber());
    empHrBean.setTerminateReasonId(s.getSuspensionType().getId());
    empHrBean.setSuspensionDate(s.getSuspensionDateStr());
    empHrBean.setTerminate(true);
    empHrBean.setWarningIssued(false);
    empHrBean.setShowForConfirm(SHOW_ROW);

    String actionCompleted = bc.getStaffTypeName()+" " + wHI.getAbstractEmployeeEntity().getDisplayName() + " Suspended successfully.";

    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
    model.addAttribute("saved", true);
    return makeModelAndReturnView(model,request,bc,empHrBean,null);

  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
		  @ModelAttribute("employee") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    
    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:searchEmpForSuspension.do";
    }
    result = doValidation(pEHB,result,bc);
    if(result.hasErrors()){
       return makeModelAndReturnView(model,request,bc,pEHB,result);
    }
    if(!pEHB.isShowConfirmationRow()){
      result.rejectValue("","warning","Please confirm "+bc.getStaffTypeName()+" Suspension");
      pEHB.setShowConfirmationRow(true);
      pEHB.setShowForConfirm(SHOW_ROW);
      pEHB.setRefDate(LocalDate.now());
      pEHB.setRefNumber("REF_"+bc.getUserName()+"_"+PayrollBeanUtils.getDateAsStringWidoutSeparators(pEHB.getRefDate()));
      return makeModelAndReturnView(model,request,bc,pEHB,result);
    }else{
      //Check if Ref Date and time is set.
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
    HiringInfo wHireInfo =  loadHiringInfoByEmpId(request,bc,pEHB.getEmployeeInstId());

    /**
     * Mustola 24th Sept 2021
     * A little bug that might affect Reconciliation Report...
     * Always make sure there is only one entry for Suspension
     * of a particular Employee with a Pay Period.
     */

    //LocalDate monthStart = LocalDate.of(pEHB.getArrearsEndDate().getYear(), pEHB.getArrearsEndDate().getMonthValue(),1);
    ArrayList<CustomPredicate> customPredicates = new ArrayList<>();
    String payPeriod = PayrollUtils.makePayPeriodForAuditLogs(genericService,bc);
    //customPredicates.add(CustomPredicate.procurePredicate("suspensionDate", monthStart, Operation.GREATER_OR_EQUAL));
    customPredicates.add(CustomPredicate.procurePredicate("auditPayPeriod", payPeriod));
    customPredicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),pEHB.getEmployeeInstId()));
    customPredicates.add(getBusinessClientIdPredicate(request));


    SuspensionLog s = this.genericService.loadObjectUsingRestriction(SuspensionLog.class, customPredicates);
    boolean increment = s.isNewEntity();
    if(increment){
      if(wHireInfo.isPensionerType()){
        s.setPensioner(new Pensioner(pEHB.getEmployeeInstId()));
      }else{
        s.setEmployee(new Employee(pEHB.getEmployeeInstId()));
      }
       s.setName(pEHB.getName());
      s.setBusinessClientId(bc.getBusinessClientInstId());
    }
      s.setSuspensionType(new SuspensionType(pEHB.getTerminateReasonId()));
      s.setUser(new User(bc.getLoginId()));
      s.setPayPercentage(pEHB.getPartPayment());
      s.setReferenceNumber(bc.getUserName()+"_SUS_"+wHireInfo.getAbstractEmployeeEntity().getEmployeeId());
      s.setMdaInfo(wHireInfo.getAbstractEmployeeEntity().getMdaDeptMap().getMdaInfo());
      s.setSalaryInfo(wHireInfo.getAbstractEmployeeEntity().getSalaryInfo());
      s.setSuspensionDate(pEHB.getRefDate());
      s.setUser(new User(bc.getLoginId()));
      s.setAuditPayPeriod(payPeriod);
      s.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
      s.setLastModTs(LocalDate.now());

    if(wHireInfo.getAbstractEmployeeEntity().isSchoolStaff()){
    	s.setSchoolInfo(wHireInfo.getAbstractEmployeeEntity().getSchoolInfo());
    }
  
     this.genericService.saveObject(s);

    wHireInfo.setLastModBy(new User(bc.getLoginId()));
    wHireInfo.setLastModTs(Timestamp.from(Instant.now()));
    wHireInfo.setSuspended(1);
    wHireInfo.setSuspensionDate(pEHB.getRefDate());

    this.genericService.saveObject(wHireInfo);
    if(increment) {
      LocalDate _wCal = paycheckService.getPendingPaycheckRunMonthAndYear(bc);
      if (_wCal != null) {
        RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(
                CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", _wCal.getYear()),
                getBusinessClientIdPredicate(request)));

        wRPB.setNoOfSuspensions(wRPB.getNoOfSuspensions() + 1);
        if (wRPB.isNewEntity()) {
          wRPB.setRunMonth(_wCal.getMonthValue());
          wRPB.setRunYear(_wCal.getYear());
          wRPB.setRerunInd(IConstants.ON);
          wRPB.setBusinessClientId(bc.getBusinessClientInstId());
        }
        this.genericService.storeObject(wRPB);
      }
    }
    return "redirect:suspendEmployee.do?slid=" + s.getId() + "&eid="+wHireInfo.getAbstractEmployeeEntity().getId()+"&s=1";
  }

  private HrMiniBean setRequiredValues(HiringInfo pWhi, HrMiniBean pEmpHrBean)
  {
    
    pEmpHrBean.setName(pWhi.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed());
    
    pEmpHrBean.setGradeLevelAndStep(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getSalaryScaleLevelAndStepStr());
    

    pEmpHrBean.setHireDate(PayrollHRUtils.getFullDateFormat().format(pWhi.getHireDate()));
    pEmpHrBean.setEmployeeInstId(pWhi.getAbstractEmployeeEntity().getId());
    pEmpHrBean.setAbstractEmployeeEntity(pWhi.getAbstractEmployeeEntity());
    
    pEmpHrBean.setYearsOfService(String.valueOf(pWhi.getNoOfYearsInService()));
    pEmpHrBean.setAssignedToObject(pWhi.getAbstractEmployeeEntity().getAssignedToObject());
    pEmpHrBean.setEmployeeId(pWhi.getAbstractEmployeeEntity().getEmployeeId());
    pEmpHrBean.setLevelAndStepStr(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getLevelAndStepAsStr());
    pEmpHrBean.setSalaryScale(pWhi.getAbstractEmployeeEntity().getSalaryTypeName());
    pEmpHrBean.setId(pWhi.getId());

    return pEmpHrBean;
  }
  private BindingResult doValidation(HrMiniBean pEHB, BindingResult result, BusinessCertificate businessCertificate) throws IllegalAccessException, InstantiationException {
    PayrollRunMasterBean payrollRunMasterBean = IppmsUtilsExt.loadCurrentlyRunningPayroll(genericService, businessCertificate);
    if(payrollRunMasterBean != null && !payrollRunMasterBean.isNewEntity()){
      result.rejectValue("", "No.Employees", "Payroll is currently being run by "+payrollRunMasterBean.getInitiator().getActualUserName()+". Suspensions can not be effected during a Payroll Run");
      return result;
    }
    if(IppmsUtilsExt.pendingPaychecksExistsForEntity(genericService,businessCertificate,pEHB.getAbstractEmployeeEntity().getId())){
      result.rejectValue("", "Invalid.Command", businessCertificate.getStaffTypeName()+" " + pEHB.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed() + " has a Pending Paycheck. Delete the Paycheck before Suspension can be allowed.");
      return result;
    }
    if(IppmsUtils.isNullOrLessThanOne(pEHB.getTerminateReasonId())){
      result.rejectValue("partPayment", "Invalid.Value", "Please select Suspension Reason.");
    }else{
      SuspensionType suspensionType = genericService.loadObjectById(SuspensionType.class,pEHB.getTerminateReasonId());
      if(suspensionType.isPayPercentage() ){
        if(pEHB.getPartPayment() >= 100.0D){
          result.rejectValue("partPayment", "Invalid.Value", "Percentage Payment can not be greater than or equal to 100%.");
        }else{
          if(!pEHB.isWarningIssued()){
            pEHB.setPartPayable(true);
            result.rejectValue("", "warning", "Enter Part Payment Percentage.");
            return  result;
          }else{
            pEHB.setWarningIssued(true);
          }
        }
      }

    }


    return  result;
  }

  private String makeModelAndReturnView(Model model, HttpServletRequest request, BusinessCertificate bc, HrMiniBean pEHB, BindingResult result){
    if(result != null){
      addDisplayErrorsToModel(model, request);
      model.addAttribute("status", result);
    }

    model.addAttribute("roleBean", bc);
    model.addAttribute("employee", pEHB);
    return VIEW_NAME;
  }
}