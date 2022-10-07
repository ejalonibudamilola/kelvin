package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClientParentMap;
import com.osm.gnl.ippms.ogsg.validators.search.SearchValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/searchPensionerToCreate.do")
@SessionAttributes(types = HrMiniBean.class)
public class SearchForEmpToAddToPension extends BaseController {

	@Autowired
	private SearchValidator searchValidator;

	private final String VIEW = "search/searchEmpForPension";

	@ModelAttribute("parentClientList")
	private List<BusinessClientParentMap> makeParentMap(HttpServletRequest request){
		List<BusinessClientParentMap> wList = this.genericService.loadAllObjectsWithSingleCondition(BusinessClientParentMap.class, CustomPredicate.procurePredicate("childBusinessClient.id", getBusinessCertificate(request).getBusinessClientInstId()),null);
	    return wList;
	}
   
	public SearchForEmpToAddToPension(){ }
	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request);
		HrMiniBean empHrBean = new HrMiniBean();
		 addRoleBeanToModel(model, request);
		model.addAttribute("empMiniBean", empHrBean);
		return VIEW;
		
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,@ModelAttribute ("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model,HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request);
        BusinessCertificate businessCertificate = getBusinessCertificate(request);
		if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
			return "redirect:determineDashBoard.do";
		}
		BusinessClient businessClient = null;
        if(businessCertificate.isStatePension()){
        	BusinessClientParentMap businessClientParentMap = this.genericService.loadObjectWithSingleCondition(BusinessClientParentMap.class,CustomPredicate.procurePredicate("childBusinessClient.id", businessCertificate.getBusinessClientInstId()));
			if(businessClientParentMap.isNewEntity()){
				result.rejectValue("parentId","Required Value"," Service Organization is Undefined. Create this Pensioner Manually.");
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model.addAttribute("status", result);
				model.addAttribute("empMiniBean", pEHB);
				return VIEW;
			}else{
				pEHB.setParentId(businessClientParentMap.getParentBusinessClient().getId());
				businessClient = businessClientParentMap.getParentBusinessClient();
			}
        }
		searchValidator.validateForPensionSearch(pEHB, result,businessCertificate);
		if (result.hasErrors()) {
			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model, request);
			model.addAttribute("status", result);
			model.addAttribute("empMiniBean", pEHB);
			return VIEW;
		}

		if(businessCertificate.isLocalGovtPension()){

			businessClient = genericService.loadObjectById(BusinessClient.class, pEHB.getParentId());
		}

	    Employee retiringEmployee =  this.genericService.loadObjectUsingRestriction(Employee.class, Arrays.asList(CustomPredicate.procurePredicate("employeeId", pEHB.getEmployeeId()),
				CustomPredicate.procurePredicate("businessClientId", businessClient.getId())));

		searchValidator.validateRetiringEmployee(result,retiringEmployee, businessCertificate);
		if (result.hasErrors()) {
			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model, request);
			model.addAttribute("status", result);
			model.addAttribute("empMiniBean", pEHB);
			return VIEW;
		}

		return "redirect:createPensioner.do?eid="+retiringEmployee.getId()+"&bid="+businessClient.getId();

	}

}
