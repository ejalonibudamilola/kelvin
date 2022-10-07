package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.*;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationMasterBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.engine.SimulatePayroll;
import com.osm.gnl.ippms.ogsg.engine.SimulatePayrollEngine;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping({"/simulationPreviewForm.do"})
@SessionAttributes(types={SimulationBeanHolder.class})
public class SimulationFinalViewController extends BaseController
{
 
  @Autowired
  private SimulationService simulationService;
  @Autowired
  private DeductionService deductionService;
  @Autowired
  private LoanService loanService;
  @Autowired
  private SpecAllowService specAllowService;
  @Autowired
  private PayrollService payrollService;
  private HashMap<Long, List<AbstractDeductionEntity>> fEmpDedMap;
  private HashMap<Long, List<AbstractGarnishmentEntity>> fGarnMap;
  private HashMap<Long, List<AbstractSpecialAllowanceEntity>> fAllowanceMap;
  private LocalDate fPayPeriodStart;
  private LocalDate fPayPeriodEnd;

  private final String VIEW = "simulation/simulatePayrollPreviewForm";
  public SimulationFinalViewController()
  { }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"uid"})
  public String setupForm(@RequestParam("uid") String pUid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);


    if (!bc.getUserName().equalsIgnoreCase(pUid)) {
      return "redirect:step3PayrollSimulator.do?uid=" + bc.getUserName();
    }
    
    SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);
    
    if(wLTGH == null)
       return "redirect:prePayrollSimulator.do";
    wLTGH = setDisplayRelevantData(wLTGH);

    wLTGH.setId(bc.getBusinessClientInstId());
    addRoleBeanToModel(model, request);
    model.addAttribute("simulationBean4", wLTGH);

    return VIEW;
  }


@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_simulate", required=false) String pAdd, 
		  @RequestParam(value="_cancel", required=false) String pCancel, @ModelAttribute("simulationBean") SimulationBeanHolder pHMB, 
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    
    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
      if (pHMB.getPayrollSimulationMasterBean().isCanApplyLtg()) {
        return "redirect:step3PayrollSimulator.do?uid=" + bc.getUserName();
      }
      return "redirect:stepTwoPayrollSimulator.do?uid=" + bc.getSessionId().toString() + "&mode=e";
    }
 
    if (isButtonTypeClick(request, REQUEST_PARAM_SIMULATE))
    {

      SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);

      this.fPayPeriodStart = LocalDate.of(wLTGH.getPayrollSimulationMasterBean().getSimulationStartYear(), wLTGH.getPayrollSimulationMasterBean().getSimulationStartMonth(),1);
      this.fPayPeriodEnd = LocalDate.of(fPayPeriodStart.getYear(),fPayPeriodStart.getMonthValue(),fPayPeriodStart.lengthOfMonth());

      List<AbstractDeductionEntity> wEmpListToSplit = this.deductionService.loadToBePaidEmployeeDeductions(bc, this.fPayPeriodStart, this.fPayPeriodEnd,false);
      List<AbstractGarnishmentEntity> wGarnListToSplit = this.loanService.loadToBePaidEmployeeGarnishments(bc, fPayPeriodStart,false);
      List<AbstractSpecialAllowanceEntity> wAllowListToSplit = this.specAllowService.loadToBePaidEmployeeSpecialAllowances(bc, this.fPayPeriodStart, this.fPayPeriodEnd,false);

      HashMap<Long, SuspensionLog> wPartPayments = this.payrollService.loadToBePaidSuspendedEmployees(bc);
      this.fEmpDedMap = EntityUtils.breakUpDeductionList(wEmpListToSplit,this.fPayPeriodStart, this.fPayPeriodEnd);
      this.fGarnMap = EntityUtils.breakUpGarnishmentList(wGarnListToSplit);
      this.fAllowanceMap = EntityUtils.breakUpAllowanceList(wAllowListToSplit, fPayPeriodStart, fPayPeriodEnd);
      List<SalaryInfo> wList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, getBusinessClientIdPredicate(request), null);
      Map<Long, SalaryInfo> wMap = EntityUtils.breakSalaryInfo(wList);

      SimulatePayrollEngine wSimulateEngine = new SimulatePayrollEngine();
      wSimulateEngine.setSalaryInfoMap(wMap);
      
      //--Here we need to populate RetMainBean with its MiniBeans...

      PayrollFlag wPF = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);

      SimulatePayroll wCalcPay = new SimulatePayroll(this.genericService,this.simulationService, bc, wSimulateEngine, pHMB,genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,
              CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId() ),null),wPF, loadConfigurationBean(request),fEmpDedMap,fGarnMap,fAllowanceMap,wPartPayments);

      addSessionAttribute(request, "SimulatePayroll", wCalcPay);

      Thread t = new Thread(wCalcPay);
      t.start();

//      return "redirect:displayStatus.do?ftr=sim";
      return "redirect:displaySimStatus.do";
    }

    return "redirect:step3PayrollSimulator.do?uid=" + bc.getUserName();
  }

  private SimulationBeanHolder setDisplayRelevantData(SimulationBeanHolder pHMB)
  {
	
    PayrollSimulationMasterBean wPSMB = pHMB.getPayrollSimulationMasterBean();
    if (wPSMB.getApplyLtgInd() == 1) {
      pHMB.setApplyLtgInd("true");
    }
    if (wPSMB.isIncludePromotionsChecked()) {
      pHMB.setIncludePromotionInd("true");
    }
    if (wPSMB.getDeductBaseYearDevLevyInd() == 1) {
      pHMB.setDeductBaseYearDevLevy("true");
    }
    if (wPSMB.getDeductSpillOverYearDevLevy() == 1) {
      pHMB.setDeductSpillOverYearDevLevy("true");
    }
    if (wPSMB.getStepIncrementInd() == 1) {
      pHMB.setStepIncrementInd("true");
    }
    if (wPSMB.isIncludePromotionsChecked()) {
      String includePromotionInd = "Include Promotion";
      pHMB.setIncludePromotionStr(includePromotionInd);
      pHMB.setIncludePromotionInd("true");
    }
    if (pHMB.isDeductDevLevyForBaseYear()) {
      pHMB.setDeductBaseYearDevLevyStr("Deduct Development Levy for " + PayrollBeanUtils.getCurrentYearAsString() + " in " + PayrollBeanUtils.getMonthNameFromInteger(pHMB.getStartMonthInd()));
    }

    if (pHMB.isDeductDevLevyForSpillOverYear()) {
      LocalDate wCal = LocalDate.now();
      wCal = wCal.plusYears(1L);
      pHMB.setDeductDevLevyForSpillOverYearStr("Deduct Development Levy for " + PayrollBeanUtils.getYearAsString(wCal) + " in " + PayrollBeanUtils.getMonthNameFromInteger(pHMB.getSpillYearDevLevyInd()));
    }
    return pHMB;
  }
}