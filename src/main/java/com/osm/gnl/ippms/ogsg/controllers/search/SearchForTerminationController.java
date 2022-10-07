package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
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
import java.util.List;

 
@Controller
@RequestMapping({ "/searchEmpForTermination.do" })
@SessionAttributes(types = { HrMiniBean.class })
public class SearchForTerminationController extends BaseSearchController {


	@Autowired
	private SearchValidator searchValidator;

	private final String VIEW_NAME = "search/searchEmpForPromotionForm";

	public SearchForTerminationController() {}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String setupForm(Model model, HttpServletRequest request)
			throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		HrMiniBean empHrBean = new HrMiniBean();

		empHrBean.setDisplayTitle("Search for "+bc.getStaffTypeName()+" to Terminate");
		empHrBean.setActiveInd(ON);

		return makeAndReturnView(model,null,empHrBean,bc,request);
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@ModelAttribute("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status,
			Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
			return REDIRECT_TO_DASHBOARD;
		}

		searchValidator.validate(pEHB, result);
		if (result.hasErrors()) {
			return makeAndReturnView(model,result,pEHB,bc,request);
		}


		List<AbstractEmployeeEntity> emp =  doSearch(pEHB,bc);

		if (emp.size() < 1) {
			result.rejectValue("", "search.no_values", "No "+bc.getStaffTypeName()+"(s) found! Please retry.");
			return makeAndReturnView(model,result,pEHB,bc,request);
		}

		if (emp.size() == 1) {

			if (emp.get(0).isTerminated()) {
				result.rejectValue("", "search.multi_values", bc.getStaffTypeName()+" " + emp.get(0).getDisplayNameWivTitlePrefixed()
						+ " [ " + emp.get(0).getEmployeeId() + " ] is already terminated.");
				return makeAndReturnView(model,result,pEHB,bc,request);
			}

			return "redirect:terminateEmployee.do?eid=" + emp.get(0).getId();
		}
		result.rejectValue("", "search.multi_values", emp.size() +" "+bc.getStaffTypeName()+ "  found! Please narrow search criteria.");

		return makeAndReturnView(model,result,pEHB,bc,request);
	}


	private String makeAndReturnView(Model model, BindingResult result, HrMiniBean pEHB,BusinessCertificate bc, HttpServletRequest request){
		if(result != null){
			addDisplayErrorsToModel(model, request);
			model.addAttribute("status", result);
		}

		model.addAttribute("empMiniBean", pEHB);
		model.addAttribute("roleBean", bc);
		return VIEW_NAME;
	}
}