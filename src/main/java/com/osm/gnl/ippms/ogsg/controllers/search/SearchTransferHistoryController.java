package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

 
@Controller
@RequestMapping({"/searchEmpForTransferHistory.do"})
@SessionAttributes(types={HrMiniBean.class})
public class SearchTransferHistoryController extends BaseSearchController {


	@Autowired
	private SearchValidator searchValidator;

	private final String VIEW_NAME = "search/searchEmpForPromotionForm";

	  public SearchTransferHistoryController(){}

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {  
		  
		  SessionManagerService.manageSession(request, model);

			BusinessCertificate bc = this.getBusinessCertificate(request);


	    HrMiniBean empHrBean = new HrMiniBean();

	    empHrBean.setDisplayTitle("Search for "+bc.getStaffTypeName()+" to view Transfer History");

	   
	    model.addAttribute("empMiniBean", empHrBean);
	    model.addAttribute("roleBean", bc);
	    return VIEW_NAME;
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
	      addDisplayErrorsToModel(model, request);
	      model.addAttribute("status", result);
	      model.addAttribute("empMiniBean", pEHB);
	      model.addAttribute("roleBean", bc);
	      return VIEW_NAME;
	    }
		  List<AbstractEmployeeEntity> emp = doSearch(pEHB,bc);

	    if (emp.size() < 1)
	    {
	      result.rejectValue("", "search.no_values", "No "+bc.getStaffTypeName()+"(s) found! Please retry.");
	      addDisplayErrorsToModel(model, request);
	      model.addAttribute("status", result);
	      model.addAttribute("empMiniBean", pEHB);
	      return VIEW_NAME;
	    }

	    if (emp.size() == 1)
	    {

	    HiringInfo wHI = loadHiringInfoByEmpId(request,bc,emp.get(0).getId());

	    if(wHI == null || wHI.isNewEntity()){
	  	  result.rejectValue("", "search.no_values", emp.get(0).getDisplayName()+ " has no Hiring Information.");

			addDisplayErrorsToModel(model, request);

	  	    model.addAttribute("status", result);
	  	    model.addAttribute("empMiniBean", pEHB);
	  	   
	  	    model.addAttribute("roleBean", bc);
			return VIEW_NAME;
	    }
	      return "redirect:viewEmpTransferHistory.do?eid=" + emp.get(0).getId();
	    }else {
	    	 return "redirect:viewMultiEmployeeResults.do?eid="+ StringUtils.trimToEmpty(pEHB.getEmployeeId())+"&fn=" + StringUtils.trimToEmpty(pEHB.getFirstName()) + "&ln=" + StringUtils.trimToEmpty(pEHB.getLastName()) + "&noe=" + emp.size() + "&cn=9";
	  }
	  }
}
