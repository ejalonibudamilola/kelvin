package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
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
@RequestMapping({"/searchEmpForPromoHistory.do"})
@SessionAttributes(types={HrMiniBean.class})
public class SearchForPromoHistoryController extends BaseSearchController {

	@Autowired
	private SearchValidator searchValidator;
	private final String VIEW_NAME = "search/searchEmpForPromotionForm";
	 
	  public SearchForPromoHistoryController()
	  {}
	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {   
		  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		  HrMiniBean empHrBean = new HrMiniBean();

	    empHrBean.setDisplayTitle("Search for "+bc.getStaffTypeName()+" to view Promotion History");
	    model.addAttribute("roleBean", bc);
	    model.addAttribute("empMiniBean", empHrBean);
	    return VIEW_NAME;
	  }

	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("empMiniBean") HrMiniBean pEHB,
			  BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);

	   
	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
	      return REDIRECT_TO_DASHBOARD;
	    }

		  searchValidator.validate(pEHB, result, bc);
		  if (result.hasErrors()) {
			  addDisplayErrorsToModel(model, request);

			  model.addAttribute("status", result);
			  model.addAttribute("empMiniBean", pEHB);
			  model.addAttribute("roleBean", bc);
			  return VIEW_NAME;
		  }

		  List<AbstractEmployeeEntity> emp = doSearch(pEHB,bc);

	    if (emp.size() == 1)
	    {
			//lets do something interesting here...
			//lets see if this employee has Hiring Information
			HiringInfo wHireInfo = genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), emp.get(0).getId()));

			if (wHireInfo.isNewEntity()) {
				result.rejectValue("", "search.no_values", bc.getStaffTypeName() + " does not have Hiring Information. Please create hiring information.");
				pEHB.setEmployeeInstId(emp.get(0).getId());
				addDisplayErrorsToModel(model, request);
				model.addAttribute("status", result);
				model.addAttribute("empMiniBean", pEHB);
				model.addAttribute("roleBean", bc);
				model.addAttribute("no_hire_info", true);

				return VIEW_NAME;
			}
	      return "redirect:viewEmpPromoHistory.do?eid=" + emp.get(0).getId();
	    }
	    return "redirect:viewMultiEmployeeResults.do?eid="+ StringUtils.trimToEmpty(pEHB.getEmployeeId())+"&fn=" + StringUtils.trimToEmpty(pEHB.getFirstName()) + "&ln=" + StringUtils.trimToEmpty(pEHB.getLastName()) + "&noe=" + emp.size() + "&cn=3";
	  }

}
