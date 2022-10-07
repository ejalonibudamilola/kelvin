package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.search.SearchValidator;
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
import java.util.List;



@Controller
@RequestMapping({"/searchEmpForSuspension.do"})
@SessionAttributes(types={HrMiniBean.class})
public class SearchForSuspensionController extends BaseSearchController
{

  @Autowired
  private SearchValidator searchValidator;

  private final String VIEW = "search/searchEmpForPromotionForm";

  public SearchForSuspensionController()
  {}
  
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    HrMiniBean empHrBean = new HrMiniBean();

    empHrBean.setDisplayTitle("Search for "+bc.getStaffTypeName()+" to Suspend or stop salary");
    empHrBean.setActiveInd(ON);

    model.addAttribute("roleBean", bc);
    model.addAttribute("empMiniBean", empHrBean);
    return VIEW;

  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"reab"})
  public String setupForm(@RequestParam("reab") String pReab, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);


    HrMiniBean empHrBean = new HrMiniBean();

    empHrBean.setDisplayTitle("Search for a Suspended "+bc.getStaffTypeName()+" to Reabsorb into the service");
    empHrBean.setActiveInd(ON);
    empHrBean.setReabsorbEmployee(true);

    model.addAttribute("roleBean", bc);
    model.addAttribute("empMiniBean", empHrBean);
    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);
 

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return REDIRECT_TO_DASHBOARD;
    }

    searchValidator.validate(pEHB, result);
    if (result.hasErrors()) {
      addDisplayErrorsToModel(model, request);
      model.addAttribute("roleBean", bc);
      model.addAttribute("status", result);
      model.addAttribute("empMiniBean", pEHB);
      return VIEW;
    }


    List<AbstractEmployeeEntity> emp = doSearch(pEHB,bc);

    if (emp.size() < 1)
    {
      result.rejectValue("", "search.no_values", "No "+bc.getStaffTypeName()+"(s) found! Please retry.");

      addDisplayErrorsToModel(model, request);
      model.addAttribute("roleBean", bc);
      model.addAttribute("status", result);
      model.addAttribute("empMiniBean", pEHB);
      return VIEW;
    }

    if (emp.size() == 1)
    {


      HiringInfo h = this.genericService.loadObjectWithSingleCondition(HiringInfo.class,CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),emp.get(0).getId()));
      if(h.isNewEntity()){
        result.rejectValue("", "search.no_values", "No "+bc.getStaffTypeName()+" Has no Hiring Information. Please Add Hiring Information.");

        addDisplayErrorsToModel(model, request);
        model.addAttribute("roleBean", bc);
        model.addAttribute("status", result);
        model.addAttribute("empMiniBean", pEHB);
        return VIEW;
      }
      if (h.isTerminatedEmployee()) {
        String msg = "";
        if (pEHB.isReabsorbEmployee())
          msg = bc.getStaffTypeName()+" " + h.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed() + " was Terminated on " + PayrollHRUtils.getDisplayDateFormat().format(h.getTerminateDate()) + ". You can not Re-absorb a terminated "+bc.getStaffTypeName();
        else {
          msg = bc.getStaffTypeName()+" " + h.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed() + " was Terminated on " + PayrollHRUtils.getDisplayDateFormat().format(h.getTerminateDate()) + ". You can not Suspend a terminated "+bc.getStaffTypeName();
        }
        result.rejectValue("", "Invalid.Command", msg);
        addDisplayErrorsToModel(model, request);
        model.addAttribute("roleBean", bc);
        model.addAttribute("status", result);
        model.addAttribute("empMiniBean", pEHB);
        return VIEW;
      }
      if (h.isSuspendedEmployee() && !pEHB.isReabsorbEmployee()) {
        return "redirect:reabsorbEmployee.do?eid=" + h.getAbstractEmployeeEntity().getId();
      }

      if (pEHB.isReabsorbEmployee() && !h.isSuspendedEmployee()) {
        result.rejectValue("", "Invalid.Command", bc.getStaffTypeName()+" " + h.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed() + " is not Suspended. You can not Re-absorb an active "+bc.getStaffTypeName());
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("roleBean", bc);
        model.addAttribute("empMiniBean", pEHB);
        return VIEW;
      }if (!pEHB.isReabsorbEmployee() && h.isSuspendedEmployee()) {
        result.rejectValue("", "Invalid.Command", bc.getStaffTypeName()+" " + h.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed() + " is currently suspended. Suspension date was " + h.getSuspensionDateStr());
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("roleBean", bc);
        model.addAttribute("empMiniBean", pEHB);
        return VIEW;
      }

      if (h.isSuspendedEmployee()) {
        return "redirect:reabsorbEmployee.do?eid=" + h.getAbstractEmployeeEntity().getId() ;
      }
      if(IppmsUtilsExt.pendingPaychecksExistsForEntity(genericService,bc,h.getAbstractEmployeeEntity().getId())){
    	  result.rejectValue("", "Invalid.Command", bc.getStaffTypeName()+" " + h.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed() + " has a Pending Paycheck. Delete the Paycheck before Suspension can be allowed.");
          addDisplayErrorsToModel(model, request);
          addRoleBeanToModel(model, request);
          model.addAttribute("status", result);
          model.addAttribute("empMiniBean", pEHB);
          return VIEW;
      }
      return "redirect:suspendEmployee.do?eid=" + h.getAbstractEmployeeEntity().getId();
    }

    if(!pEHB.isReabsorbEmployee())
      return "redirect:viewMultiEmployeeResults.do?eid="+ StringUtils.trimToEmpty(pEHB.getEmployeeId())+"&fn="+ IppmsUtils.treatNull(pEHB.getFirstName())+"&ln="+IppmsUtils.treatNull(pEHB.getLastName())+"&noe="+emp.size()+"&cn=14";
    else
      return "redirect:viewMultiEmployeeResults.do?eid="+ StringUtils.trimToEmpty(pEHB.getEmployeeId())+"&fn="+IppmsUtils.treatNull(pEHB.getFirstName())+"&ln="+IppmsUtils.treatNull(pEHB.getLastName())+"&noe="+emp.size()+"&cn=15";

  }
}