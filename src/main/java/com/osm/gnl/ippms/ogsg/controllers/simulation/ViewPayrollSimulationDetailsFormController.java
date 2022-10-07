package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationMasterBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


@Controller
@RequestMapping({"/viewPayrollDetails.do"})
@SessionAttributes(types={SimulationBeanHolder.class})
public class ViewPayrollSimulationDetailsFormController extends BaseController
{
 
  @Autowired
  private HRService hrService;

  private final String VIEW = "simulation/simulatePayrollDetailsForm";
  
  public ViewPayrollSimulationDetailsFormController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"pid"})
  public String setupForm(@RequestParam("pid") Long pPid, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);


    PayrollSimulationMasterBean p = this.genericService.loadObjectById(PayrollSimulationMasterBean.class, pPid);

    PayrollSimulationDetailsBean d = this.genericService.loadObjectById(PayrollSimulationDetailsBean.class,pPid);

    SimulationBeanHolder wLTGH = makeSimulationBeanHolderUsingMasterAndDetail(p, d, bc.getBusinessClientInstId());

    //wLTGH.setId(bc.getBusinessClientInstId());
    addRoleBeanToModel(model, request);
    model.addAttribute("simulationBean", wLTGH);

    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"pid", "pop"})
  public String setupForm(@RequestParam("pid") Long pPid, @RequestParam("pop") String pPop, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);


	  PayrollSimulationMasterBean p = this.genericService.loadObjectById(PayrollSimulationMasterBean.class, pPid);

	  PayrollSimulationDetailsBean d = this.genericService.loadObjectById(PayrollSimulationDetailsBean.class,pPid);

    SimulationBeanHolder wLTGH = makeSimulationBeanHolderUsingMasterAndDetail(p, d, bc.getBusinessClientInstId());

    //wLTGH.setId(bc.getBusinessClientInstId());
    addRoleBeanToModel(model, request);
    model.addAttribute("simulationBean", wLTGH);

    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_delete", required=false) String pDelete, 
		  @RequestParam(value="_cancel", required=false) String pCancel,
		  @RequestParam(value="_confirm", required=false) String pConfirm, @ModelAttribute("simulationBean") SimulationBeanHolder pHMB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
 
    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
    {
      return "redirect:prePayrollSimReportForm.do";
    }
    

    if (isButtonTypeClick(request, REQUEST_PARAM_DELETE))
    {
      if (!pHMB.isCancelWarningIssued()) {
        pHMB.setCancelWarningIssued(true);
        result.rejectValue("", "Sim.Canc.Warn", "You are about to delete all records associated with this Payroll Simulation! Press 'Confirm' button to delete or 'Cancel' button to cancel deletion.");
        addDisplayErrorsToModel(model, request);
        addRoleBeanToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("simulationBean", pHMB);

        return VIEW;

      }

    }
 
 
      PayrollSimulationMasterBean p = this.genericService.loadObjectById(PayrollSimulationMasterBean.class,pHMB.getId());

      this.genericService.deleteObject(p);
    

    return "redirect:prePayrollSimReportForm.do";
  }

  private SimulationBeanHolder makeSimulationBeanHolderUsingMasterAndDetail(PayrollSimulationMasterBean pHMB, PayrollSimulationDetailsBean pPSDB, Long bizId)
  {
    SimulationBeanHolder wSBH = new SimulationBeanHolder();

    wSBH.setId(pHMB.getId());
    wSBH.setName(pHMB.getName());
    wSBH.setStartMonthName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pHMB.getSimulationStartMonth(),  pHMB.getSimulationStartYear()));
    int wNoOfMonths = pHMB.getSimulationPeriodInd() + 1;
    String wAddendum = "Months";
    if (wNoOfMonths == 1)
      wAddendum = "Month";
    wSBH.setSimulationLength(wNoOfMonths + " " + wAddendum);
    wSBH.setEffectiveYear(pHMB.getSimulationStartYear());

    if (pPSDB.getApplyLtgInd1() == 1) {
      wSBH.setApplyLtgInd("true");
      wSBH = createAbmpAssignedList(wSBH, pPSDB,bizId);
      wSBH.setHasDataForDisplay(true);
    }
    if (pPSDB.isApplyPromotions()) {
      wSBH.setIncludePromotionInd("true");
      String includePromotionInd = "Include Promotion";
      wSBH.setIncludePromotionStr(includePromotionInd);
      wSBH.setIncludePromotionInd("true");
    }
    if (pPSDB.isDeductDevLevyForYear1()) {
      wSBH.setDeductBaseYearDevLevy("true");
    }

    if (pPSDB.isDeductDevLevyForYear2()) {
      wSBH.setDeductSpillOverYearDevLevy("true");
      LocalDate wCal = LocalDate.now().plusYears(1L);

      wSBH.setDeductDevLevyForSpillOverYearStr("Deduct Development Levy for " + PayrollBeanUtils.getYearAsString(wCal) + " in " + PayrollBeanUtils.getMonthNameFromInteger(pHMB.getSpillYearDevLevyInd()));
    }

    if (pPSDB.isDoStepIncrement()) {
      wSBH.setStepIncrementInd("true");
    }

    return wSBH;
  }

  private SimulationBeanHolder createAbmpAssignedList(SimulationBeanHolder pSBH, PayrollSimulationDetailsBean pPSDB, Long bizId)
  {
    String wLtgStr = pPSDB.getApplyLtgDetails1();
    StringTokenizer wStr = new StringTokenizer(wLtgStr, "|");
    List<AbmpBean> wAssignedList = new ArrayList<AbmpBean>();
    while (wStr.hasMoreTokens())
    {
      String wSubStr = wStr.nextToken();
      String wInstId = wSubStr.substring(0, wSubStr.indexOf(":"));
      //String wInd = wSubStr.substring(wSubStr.indexOf(":") + 1, wSubStr.lastIndexOf(":"));
      String wMonth = wSubStr.substring(wSubStr.lastIndexOf(":") + 1);

      AbmpBean a = createMBAPBeanForDisplay(Long.parseLong(wInstId),bizId);

      a.setMonthToApply(Integer.parseInt(wMonth));
      a.setMonthYearDisplay(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(a.getMonthToApply(), pSBH.getEffectiveYear()));

      pSBH.setTotalBasicSalary(pSBH.getTotalBasicSalary() + a.getBasicSalary());
      pSBH.setTotalLtgCost(pSBH.getTotalLtgCost() + a.getLtgCost());
      pSBH.setTotalNetIncrease(pSBH.getTotalNetIncrease() + a.getNetIncrease());
      pSBH.setTotalNoOfEmp(pSBH.getTotalNoOfEmp() + a.getNoOfEmp());
      wAssignedList.add(a);
    }

    pSBH.setAssignedMBAPList(wAssignedList);

    return pSBH;
  }

  private AbmpBean createMBAPBeanForDisplay(Long pPid, Long bizId)
  {
    AbmpBean wRetVal = this.hrService.createMBAPMiniBeanForLTGAllowance(pPid, bizId,false);
    return wRetVal;
  }
}