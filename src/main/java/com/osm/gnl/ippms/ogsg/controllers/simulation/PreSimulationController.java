package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationMasterBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.simulation.PreSimulationValidator;
import org.apache.commons.lang.StringUtils;
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
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;


@Controller
@RequestMapping({"/prePayrollSimulator.do"})
@SessionAttributes(types={SimulationBeanHolder.class})
public class PreSimulationController extends BaseController
{

  @Autowired
  private PreSimulationValidator validator;
  private final String VIEW = "simulation/simulatePayrollPreForm";
  public PreSimulationController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
	  SessionManagerService.manageSession(request, model);

	BusinessCertificate bc = this.getBusinessCertificate(request);

    SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);

    if (wLTGH == null)
    {
      wLTGH = new SimulationBeanHolder();
      wLTGH = setMonthList(wLTGH, bc);
      
    }
    else {
      wLTGH.setEditMode(true);

      PayrollSimulationMasterBean p = wLTGH.getPayrollSimulationMasterBean();
      if (p == null)
      {
        wLTGH = new SimulationBeanHolder();
        wLTGH = setMonthList(wLTGH, bc);
       } else if (p.isIncludePromotionsChecked()) {
        wLTGH.setIncludePromotionInd("true");
      }
    }
    addRoleBeanToModel(model, request);
    addSessionAttribute(request, SIM_ATTR, wLTGH);
    model.addAttribute("simulationBean", wLTGH);

    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"del"})
  public String setupForm(@RequestParam("del") String pDel, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

	  //BusinessCertificate bc = this.getBusinessCertificate(request);

    SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);

    if (wLTGH == null)
    {
      return REDIRECT_TO_DASHBOARD;
    }
    wLTGH.setWarningIssued(true);

    result.rejectValue("", "Sim.Del.Warn", "Simulator Data will be lost!. Press 'Delete' Button to delete or 'Save' Button to keep data.");
    addDisplayErrorsToModel(model, request);
    addRoleBeanToModel(model, request);
    model.addAttribute("status", result);
    model.addAttribute("simulationBean", wLTGH);

    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
		  @RequestParam(value="_next", required=false) String pContinue,
		  @RequestParam(value="_delete", required=false) String delete, 
		  @ModelAttribute("simulationBean") SimulationBeanHolder pHMB, 
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    if (pHMB.isWarningIssued()) {
      
      if (isButtonTypeClick(request,REQUEST_PARAM_DELETE))
      {
        if(pHMB.getPayrollSimulationMasterBean() == null || pHMB.getPayrollSimulationMasterBean().isNewEntity())
          return REDIRECT_TO_DASHBOARD;
        this.genericService.deleteObject(pHMB.getPayrollSimulationMasterBean());
      }

      removeSessionAttribute(request, SIM_ATTR);
      return REDIRECT_TO_DASHBOARD;
    }
    
    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {
      if (pHMB.isWarningIssued()) {
        if(pHMB.getPayrollSimulationMasterBean() != null && !pHMB.getPayrollSimulationMasterBean().isNewEntity()) {
          this.genericService.deleteObject(pHMB.getPayrollSimulationMasterBean());
          removeSessionAttribute(request, SIM_ATTR);
        }
        return REDIRECT_TO_DASHBOARD;
      }

      pHMB.setWarningIssued(true);

      result.rejectValue("", "Sim.Del.Warn", "Simulator Data will be lost!. Press 'Delete' Button to delete or 'Save' Button to keep data.");
      addDisplayErrorsToModel(model, request);
      addRoleBeanToModel(model, request);
      model.addAttribute("status", result);
      model.addAttribute("simulationBean", pHMB);

      return VIEW;
    }
    if (isButtonTypeClick(request, REQUEST_PARAM_NEXT))
    {
      validator.validate(pHMB, result, bc);
      if (result.hasErrors()) {
        addDisplayErrorsToModel(model, request);
        addRoleBeanToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("simulationBean", pHMB);

        return VIEW;
      }

      createAndStorePayrollSimulationMasterBean(pHMB, bc,request);
      pHMB.setDisplayErrors("none");
      return "redirect:stepTwoPayrollSimulator.do?uid=" + bc.getSessionId().toString();
    }

    return REDIRECT_TO_DASHBOARD;
  }

  private void createAndStorePayrollSimulationMasterBean(SimulationBeanHolder pHMB, BusinessCertificate pBc, HttpServletRequest pRequest)
  {
    PayrollSimulationMasterBean p = new PayrollSimulationMasterBean();

    if (pHMB.isEditMode()) {
      p = pHMB.getPayrollSimulationMasterBean();
     
    }

    if ((!StringUtils.trimToEmpty(pHMB.getIncludePromotionInd()).equalsIgnoreCase("")) && 
    		(Boolean.valueOf(pHMB.getIncludePromotionInd()).booleanValue())) {
      p.setIncludePromotions(1);
    }
    p.setLastModTs(LocalDate.now());
    p.setLastModBy(pBc.getUserName());
    p.setName(pHMB.getName());
    p.setSimulationStartMonth(pHMB.getStartMonthInd());
    if (LocalDate.now().getYear() < pHMB.getEffectiveYear()) {
      LocalDate wCal = LocalDate.now().plusYears(1L);

      p.setSimulationStartYear( wCal.getYear());
    } else {
      p.setSimulationStartYear(LocalDate.now().getYear() );
    }

    p.setSimulationPeriodInd(pHMB.getNoOfMonthsInd());
    if (pHMB.getStartMonthInd() + pHMB.getNoOfMonthsInd() > 11) {
      p.setSimulationCrossOverInd(1);
    }
    p.setBusinessClientId(pBc.getBusinessClientInstId());
    this.genericService.saveObject(p);

    pHMB.setPayrollSimulationMasterBean(p);
    addSessionAttribute(pRequest, SIM_ATTR, pHMB);
  }

  private SimulationBeanHolder setMonthList(SimulationBeanHolder pLTGH, BusinessCertificate pBc) throws InstantiationException, IllegalAccessException
  {
    LocalDate  wCal = LocalDate.now();
    ArrayList<NamedEntity> wMonthList = new ArrayList<NamedEntity>();

    int wYear = wCal.getYear();
    PayrollFlag wPf = IppmsUtilsExt.getPayrollFlagForClient(genericService,pBc);

    int wStartMonth = 0;
    if ((wPf.getApprovedMonthInd() == wCal.getMonthValue()) && (wYear == wPf.getApprovedYearInd())) {
      if (wCal.getMonthValue() + 1 > 12) {
        wStartMonth = 1;
        wCal = wCal.plusYears(1L);
        wYear++;
      } else {
        wStartMonth = wCal.getMonthValue() + 1;
      }
    }
    else wStartMonth = wCal.getMonthValue();

    for (int wStart = wStartMonth; (wStart <= 12) && (wCal.getYear() == wYear); wStart++) {
      NamedEntity n = new NamedEntity();
      n.setId(Long.valueOf(wStart));
      n.setName(Month.of(wStart).getDisplayName(TextStyle.FULL,Locale.UK));
     // wCal = wCal.plusMonths(1L);
      wMonthList.add(n);
    }
    pLTGH.setBaseMonthList(wMonthList);
    pLTGH.setEffectiveYear(wCal.getYear());
    return pLTGH;
  }
}