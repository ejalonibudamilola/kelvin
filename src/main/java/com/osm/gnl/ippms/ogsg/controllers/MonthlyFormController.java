package com.osm.gnl.ippms.ogsg.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriod;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriodDays;
import com.osm.gnl.ippms.ogsg.domain.payment.PaySchedule;
import com.osm.gnl.ippms.ogsg.domain.payment.RelevantPeriod;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.payment.MonthlyFormValidator;
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
import java.util.Collection;

@Controller
@RequestMapping({"/monthlyForm.do"})
@SessionAttributes(types={PaySchedule.class})
public class MonthlyFormController extends BaseController
{


  private MonthlyFormValidator validator;
  @Autowired
  public MonthlyFormController(MonthlyFormValidator validator)
  {
    this.validator = validator;
  }
  
@ModelAttribute("daysList")
  public Collection<PayPeriodDays> populatePayPeriodDays() {
    return  this.genericService.loadControlEntity(PayPeriodDays.class);
  }
  
@ModelAttribute("relPeriod")
  public Collection<RelevantPeriod> populateRelevantPeriod() {
	  return this.genericService.loadControlEntity(RelevantPeriod.class);
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"ppid", "eid"})
  public String setupForm(@RequestParam("ppid") Long ppId, @RequestParam("eid") Long empId, Model model, HttpServletRequest request)
    throws Exception
  {
      SessionManagerService.manageSession(request, model);
		//Object userId = super.getSessionId(request);
		BusinessCertificate bc = this.getBusinessCertificate(request);
		 NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
    PayPeriod pp = this.genericService.loadObjectById(PayPeriod.class,ppId);
    if (pp == null) {
      throw new Exception("Access Denied! Invalid PPID");
    }

    PaySchedule ps = this.genericService.loadObjectWithSingleCondition(PaySchedule.class,CustomPredicate.procurePredicate("employee.id",empId));
    if (ps.isNewEntity()) {
      ps.setPayPeriod(pp);
      ps.setEmployee(new Employee(empId));
      ps.setRefType1("0");
    }
    model.addAttribute("namedEntity", ne);
    model.addAttribute("paySchedule", ps);
    model.addAttribute("payPeriod", pp);
    model.addAttribute("roleBean", bc);
    return "monthlyForm";
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("paySchedule") PaySchedule pPaySchedule, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
      SessionManagerService.manageSession(request, model);
		 
		BusinessCertificate bc = this.getBusinessCertificate(request);
    
    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {
      return "redirect:payInfoForm.do";
    }
    validator.validate(pPaySchedule, result);
    if (result.hasErrors()) {
      ((PaySchedule)result.getTarget()).setDisplayErrors("block");
      model.addAttribute("status", result);
      model.addAttribute("roleBean", bc);
      return "monthlyForm";
    }
    pPaySchedule = resolveInternalIssues(pPaySchedule);
    this.genericService.storeObject(pPaySchedule);

    return "redirect:payInfoForm.do";
  }

  private PaySchedule resolveInternalIssues(PaySchedule pPaySchedule)
    throws Exception
  {
    /*String ref1 = pPaySchedule.getRefType1();
    switch (Integer.parseInt(ref1)) {
    case 0:
      pPaySchedule.setWorkPeriodDays(0);
      break;
    case 1:
      pPaySchedule.setWorkPeriodInstId(this.genericService.loadObjectWithSingleCondition(PayPeriodDays.class, CustomPredicate.procurePredicate("name","internal")).getId().intValue());


      pPaySchedule.setWorkPeriodEffective(this.genericService.loadObjectWithSingleCondition(RelevantPeriod.class, CustomPredicate.procurePredicate("name","internal")).getId().intValue());
    }*/

    return pPaySchedule;
  }
}