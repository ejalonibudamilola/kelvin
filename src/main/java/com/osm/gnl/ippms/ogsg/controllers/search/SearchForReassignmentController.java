package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.search.SearchValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping({"/searchForEmpForm.do"})
@SessionAttributes(types={HrMiniBean.class})
public class SearchForReassignmentController extends BaseSearchController
{

  @Autowired
  private SearchValidator validator;
  private final String VIEW_NAME = "search/searchEmployeeForm";

  public SearchForReassignmentController()
  {}
  
  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);


    HrMiniBean empHrBean = new HrMiniBean();

    empHrBean.setDisplayTitle("Search for "+bc.getStaffTypeName()+" to Demote or Change Pay Group");
    empHrBean.setActiveInd(ON);
    model.addAttribute("empMiniBean", empHrBean);
    addRoleBeanToModel(model, request);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);
 

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:determineDashBoard.do";
    }

    validator.validate(pEHB, result);
    if (result.hasErrors()) {
       addDisplayErrorsToModel(model, request);
      model.addAttribute("status", result);
      model.addAttribute("empMiniBean", pEHB);
      return VIEW_NAME;
    }

    List<AbstractEmployeeEntity> emp = doSearch(pEHB,bc);

     if(emp.size() == 1) {
    	Employee e = (Employee)emp.get(0);

        if (e.isTerminated()) {
          result.rejectValue("", "Terminated.Employee", e.getDisplayNameWivTitlePrefixed() + " has been terminated. Reassignment Denied!");

         addDisplayErrorsToModel(model, request);
          model.addAttribute("status", result);
          model.addAttribute("empMiniBean", pEHB);
          model.addAttribute("roleBean", bc);
          return VIEW_NAME;
        }else {
        	 return "redirect:reassignEmpDeptForm.do?eid=" + e.getId();
        }
    }else {
     
    	return "redirect:viewMultiEmployeeResults.do?eid="+ StringUtils.trimToEmpty(pEHB.getEmployeeId())+"&fn=" +  pEHB.getFirstName() + "&ln=" + pEHB.getLastName()+ "&noe=" + emp.size() + "&cn=4";
     
    }

   
  }
}