package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncreaseBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepJobBean;
import com.osm.gnl.ippms.ogsg.engine.IncreaseEmployeeStep;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;


@Controller
@RequestMapping({"/runStepIncreaseStatus.do"})
public class StepIncreaseStatusFormController extends BaseController
{

  private final String VIEW = "promotion/stepIncreaseProgressBarForm";

  public StepIncreaseStatusFormController()
  {}

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

    ProgressBean wPB = new ProgressBean();
    Object o = getSessionAttribute(request, "incEmpStep");
    if (o == null) {
      return "redirect:approveStepIncrease.do";
    }
    IncreaseEmployeeStep wCP = (IncreaseEmployeeStep)o;
    if (wCP.isFinished()) {
      removeSessionAttribute(request, "incEmpStep");
      return "redirect:approveStepIncrease.do";
    }
    wPB.setCurrentCount(wCP.getCurrentRecord());
    wPB.setTotalElements(wCP.getTotalRecords());
    addRoleBeanToModel(model, request);
    model.addAttribute("progressBean", wPB);
    return VIEW;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

     
    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      StepJobBean wSJB = this.genericService.loadObjectUsingRestriction(StepJobBean.class, Arrays.asList(
              CustomPredicate.procurePredicate("running", IConstants.ON),
              CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));

      if(!wSJB.isNewEntity()){
	      wSJB.setEndDate(LocalDate.now());
	      wSJB.setLastRunBy(bc.getUserName());
	      wSJB.setRunning(0);
	      this.genericService.storeObject(wSJB);
      }

      this.genericService.deleteObjectsWithConditions(StepIncreaseBean.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId())));
      return REDIRECT_TO_DASHBOARD;
    }
    Object o = getSessionAttribute(request, "incEmpStep");
    if (o == null) {
      return REDIRECT_TO_DASHBOARD;
    }
    IncreaseEmployeeStep wCP = (IncreaseEmployeeStep)o;
    wCP.stop(true);
    removeSessionAttribute(request, "incEmpStep");

    Thread.sleep(200L);

    StepJobBean wSJB = this.genericService.loadObjectUsingRestriction(StepJobBean.class, Arrays.asList(
            CustomPredicate.procurePredicate("running", IConstants.ON),
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
    wSJB.setEndDate(LocalDate.now());
    wSJB.setLastRunBy(bc.getUserName());
    wSJB.setRunning(0);
    this.genericService.storeObject(wSJB);

    return REDIRECT_TO_DASHBOARD;
  }
}