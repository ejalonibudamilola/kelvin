package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.promotion.ManualPromotionBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncreaseBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepJobBean;
import com.osm.gnl.ippms.ogsg.engine.IncreaseEmployeeStep;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping({"/doManualStepPromotion.do"})
@SessionAttributes(types={ManualPromotionBean.class})
public class ManualStepPromotionFormController extends BaseController
{

  private int totalRecords;
  private int filteredRecords;

  private final String VIEW = "promotion/preStepIncreaseForm";
  @Autowired
  private PromotionService promotionService;

  @Autowired
  private EmployeeService employeeService;


  public ManualStepPromotionFormController()
  { }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model pModel, HttpServletRequest pRequest, HttpServletResponse pResponse)
    throws Exception
  {
	  SessionManagerService.manageSession(pRequest, pModel);
      BusinessCertificate bc = super.getBusinessCertificate(pRequest);


    HashMap wSalaryTypeToLevelAndStepMap = this.promotionService.makeSalaryTypeToLevelAndStepMap(bc.getBusinessClientInstId());

    List wSIBList1 = this.promotionService.getStepIncreasableEmployees(bc,null);

    List wSIBList = findAllEmployeesToBePromoted(wSalaryTypeToLevelAndStepMap, wSIBList1);

    ManualPromotionBean wMPB = new ManualPromotionBean();
    LocalDate toDate = LocalDate.now();
    LocalDate fromDate = LocalDate.of(toDate.getYear(),1,1);
    int noOfSuspendedEmployees = this.employeeService.getTotalNumberOfSuspendedEmployees(bc, fromDate, toDate, null, false);

    if(noOfSuspendedEmployees > 0){
      wMPB.setSuspendedEmployee(true);
      if(noOfSuspendedEmployees == 1)
         wMPB.setDisplayPayrollMsg("1 "+bc.getStaffTypeName()+" is on Suspension.");
      else
        wMPB.setDisplayPayrollMsg(noOfSuspendedEmployees+" "+bc.getStaffTypeName()+"'s are on Suspension.");
    }

    wMPB.setFilteredNo(this.filteredRecords);
    wMPB.setTotalEmpNo(this.totalRecords);
    wMPB.setEmployeesAtBar(this.totalRecords - this.filteredRecords);
    wMPB.setStepBeanList(wSIBList);
    wMPB.setSalaryTypeLevelStepMap(wSalaryTypeToLevelAndStepMap);
    addRoleBeanToModel(pModel,pRequest);
    pModel.addAttribute("manualBean", wMPB);

    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("manualBean") ManualPromotionBean pBPMB,
                              BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws HttpSessionRequiredException, InstantiationException, IllegalAccessException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

     

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {
      return REDIRECT_TO_DASHBOARD;
    }

    ManualPromotionBean wMPBean = this.genericService.loadObjectUsingRestriction(ManualPromotionBean.class,
            Arrays.asList(CustomPredicate.procurePredicate("lastPromoYear", PayrollBeanUtils.getCurrentYearAsString()),
                    super.getBusinessClientIdPredicate(request)));

    if (!wMPBean.isNewEntity()) {
      result.rejectValue("", "InvalidValue", "Manual Step Increment has been applied already this year.");
      addDisplayErrorsToModel(model, request);
      model.addAttribute("status", result);
      model.addAttribute("miniBean", pBPMB);
      addRoleBeanToModel(model,request);
      return VIEW;
    }
    PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

    if (this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, StepIncreaseBean.class) > 0)
    {

      return "redirect:resolvePendingStepIncrement.do";
    }

    StepJobBean wSJB = this.genericService.loadObjectUsingRestriction(StepJobBean.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));

    if ((!wSJB.isNewEntity()) && (wSJB.isBeingRun())) {
      result.rejectValue("", "InvalidValue", "Manual Step Increment is currently running. This process can only be done once a year!");
      addDisplayErrorsToModel(model, request);
      model.addAttribute("status", result);
      model.addAttribute("miniBean", pBPMB);
      addRoleBeanToModel(model, request);
      return VIEW;
    }
    wSJB.setRunning(1);
    wSJB.setBusinessClientId(bc.getBusinessClientInstId());
    wSJB.setLastRunBy(bc.getUserName());
    wSJB.setStartDate(LocalDate.now());
    wSJB.setStartTime(PayrollBeanUtils.getCurrentTime(false));
    this.genericService.storeObject(wSJB);


    HashMap wSLSTSMap = this.promotionService.makeSalaryTypeLevelStepToSalaryInfoMap(bc.getBusinessClientInstId());

    IncreaseEmployeeStep wIES = new IncreaseEmployeeStep(this.genericService, bc.getUserName(), wSJB, pBPMB.getSalaryTypeLevelStepMap(), pBPMB.getStepBeanList(), wSLSTSMap);

    addSessionAttribute(request, "incEmpStep", wIES);

    Thread t = new Thread(wIES);
    t.start();

    return "redirect:runStepIncreaseStatus.do";
  }

  private List<StepIncreaseBean> findAllEmployeesToBePromoted(HashMap<Long, HashMap<Integer, Integer>> pSalaryTypeToLevelAndStepMap, List<StepIncreaseBean> pSIBList1)
  {
    List wRetVal = new ArrayList();

    this.totalRecords = pSIBList1.size();

    for (StepIncreaseBean s : pSIBList1)
    {
      if (pSalaryTypeToLevelAndStepMap.containsKey(s.getSalaryTypeInstId()))
      {
        HashMap sMap = pSalaryTypeToLevelAndStepMap.get(s.getSalaryTypeInstId());
        if ((sMap != null) && (!sMap.isEmpty())) {
          Integer wBarValue = (Integer)sMap.get(Integer.valueOf(s.getLevel()));
          if (wBarValue == null)
          {
            wRetVal.add(s);
          }
          else if (s.getStep() < wBarValue.intValue()) {
            wRetVal.add(s);
          }

        }

      }

    }

    this.filteredRecords = wRetVal.size();

    return wRetVal;
  }
}