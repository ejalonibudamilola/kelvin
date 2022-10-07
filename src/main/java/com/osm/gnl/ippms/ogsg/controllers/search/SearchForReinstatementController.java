package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.validators.search.SearchValidator;
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
import java.util.Arrays;
import java.util.List;
 
@Controller
@RequestMapping({"/searchForEmpToReinstate.do"})
@SessionAttributes(types={HrMiniBean.class})
public class SearchForReinstatementController extends BaseSearchController
{
  @Autowired
  private SearchValidator searchValidator;

  private final String VIEW_NAME = "search/searchEmpForPromotionForm";
  public SearchForReinstatementController()
  {}
  
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);


    HrMiniBean empHrBean = new HrMiniBean();
    empHrBean.setDisplayTitle("Search for "+bc.getStaffTypeName()+" to Reinstate");
    empHrBean.setActiveInd(2);
    return this.makeAndReturnView(null,empHrBean,model,request);
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

    

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return REDIRECT_TO_DASHBOARD;
    }

    searchValidator.validate(pEHB, result);
    if (result.hasErrors()) {
      return this.makeAndReturnView(result,pEHB,model,request);
    }

    if (IppmsUtilsExt.pendingPaychecksExists(genericService,bc)) {
      result.rejectValue("", "Pending.Paychecks", "Pending Paychecks found. Reinstatements can only be effected after a Payroll Run has been approved.");
      return this.makeAndReturnView(result,pEHB,model,request);
    }


    List<AbstractEmployeeEntity> emp = doSearch(pEHB,bc);

    if (emp.size() < 1)
    {
      result.rejectValue("", "search.no_values", "No "+bc.getStaffTypeName()+"(s) found! Please retry.");
      return this.makeAndReturnView(result,pEHB,model,request);
    }

    if (emp.size() == 1)
    {

      HiringInfo wH = genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList( getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),emp.get(0).getId())));

      if (!wH.isTerminatedEmployee()) {
        //--check if contract employee...
        if(!bc.isPensioner() && wH.getTerminateDate() != null && wH.isOnContract()){
          result.rejectValue("", "Invalid.Value", emp.get(0).getDisplayNameWivTitlePrefixed() + " Though Terminated, is a Contract Staff. Contract Should be renewed. Reinstatement Denied.");

        }else{
          result.rejectValue("", "Invalid.Value", emp.get(0).getDisplayNameWivTitlePrefixed() + " is currently Active. Reinstatement Denied.");

        }
        return this.makeAndReturnView(result,pEHB,model,request);
      }
      if (wH.getTerminateReason().isNotReinstateable()) {
        result.rejectValue("", "Invalid.Value", emp.get(0).getDisplayNameWivTitlePrefixed() + " was terminated due to Death. Reinstatement Denied.");
        return this.makeAndReturnView(result,pEHB,model,request);
      }

      return "redirect:reinstateEmployee.do?eid=" + emp.get(0).getId();
    }
    result.rejectValue("", "search.multi_values", emp.size() + " "+bc.getStaffTypeName()+ "  found! Please narrow search criteria.");

    return this.makeAndReturnView(result,pEHB,model,request);
  }

  private String makeAndReturnView(BindingResult result, HrMiniBean pEHB, Model model, HttpServletRequest request){

    if(result != null){
      addDisplayErrorsToModel(model, request);
      model.addAttribute("status", result);
    }
    addRoleBeanToModel(model, request);
    model.addAttribute("empMiniBean", pEHB);
    return VIEW_NAME;
  }
}