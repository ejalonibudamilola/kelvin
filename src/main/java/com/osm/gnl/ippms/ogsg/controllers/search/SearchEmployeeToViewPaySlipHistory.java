package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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
import java.util.Arrays;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping("/searchPaySlipHistory.do")
@SessionAttributes(types = HrMiniBean.class)
public class SearchEmployeeToViewPaySlipHistory extends BaseController {

	@Autowired
	private SearchValidator searchValidator;

	public SearchEmployeeToViewPaySlipHistory(){}

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		HrMiniBean empHrBean = new HrMiniBean();

		empHrBean.setDisplayTitle("Search for"+ getBusinessCertificate(request).getStaffTypeName()+ "to view Paycheck History");

		model.addAttribute("empMiniBean", empHrBean);
		addRoleBeanToModel(model, request);
		return "search/searchEmployeeForPaycheckHistoryForm";

	}


	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,@ModelAttribute ("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model,HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

		if(super.isCancelRequest(request, cancel)){
			return REDIRECT_TO_DASHBOARD;
		}

		searchValidator.validate(pEHB, result);
		if (result.hasErrors()) {
			addDisplayErrorsToModel(model, request);

			model.addAttribute("status", result);
			model.addAttribute("empMiniBean", pEHB);
			addRoleBeanToModel(model, request);
			return "search/searchEmployeeForPaycheckHistoryForm";
		}


		//Now do a search with the supplied information.
		boolean useFirstName = false;
	    boolean useLastName = false;
	    boolean useEmployeeId = false;
	    if (!PayrollHRUtils.treatNull(pEHB.getEmployeeId()).equals("")) {
	      pEHB.setEmployeeId(PayrollHRUtils.treatSqlStringParam(pEHB.getEmployeeId(), false));
	      pEHB.setEmployeeId(IppmsUtils.treatOgNumber(bc,pEHB.getEmployeeId()));
	      useEmployeeId = true;
	    } else {
	      if (!PayrollHRUtils.treatNull(pEHB.getFirstName()).equals("")) {
	        pEHB.setFirstName(PayrollHRUtils.treatSqlStringParam(pEHB.getFirstName(), false));
	        useFirstName = true;
	      }
	      if (!PayrollHRUtils.treatNull(pEHB.getLastName()).equals("")) {
	        pEHB.setLastName(PayrollHRUtils.treatSqlStringParam(pEHB.getLastName(), false));
	        useLastName = true;
	      }

	    }

        CustomPredicate cp = null;

        if(useEmployeeId){
            cp = CustomPredicate.procurePredicate("employeeId", pEHB.getEmployeeId(), Operation.LIKE);
        }
        else{
            if (useFirstName){
                cp = CustomPredicate.procurePredicate("firstName", pEHB.getFirstName(), Operation.LIKE);
            }
            if(useLastName){
                cp = CustomPredicate.procurePredicate("firstName", pEHB.getLastName(), Operation.LIKE);
            }
        }
        List<Employee> emp = this.genericService.loadAllObjectsUsingRestrictions(Employee.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),cp), null);

//		List<Employee> emp = this.hrService.findHrEmployee(bc.getBusinessClientInstId(),pEHB,useFirstName,useLastName,useEmployeeId);

		if(emp.size() < 1){

			result.rejectValue("", "search.no_values", "No "+bc.getStaffTypeName()+" found! Please retry.");

			addDisplayErrorsToModel(model, request);

			model.addAttribute("status", result);
			model.addAttribute("empMiniBean", pEHB);
			addRoleBeanToModel(model, request);
			return "search/searchEmployeeForPaycheckHistoryForm";

		}

		if(emp.size() == 1){
			//first get the employee.
			Employee e = emp.get(0);

		    return "redirect:viewPayslipHistory.do?eid="+e.getId();

		}else{
			return "redirect:viewMultiEmployeeResults.do?eid="+ StringUtils.trimToEmpty(pEHB.getEmployeeId())+"&fn="+IppmsUtils.treatNull(pEHB.getFirstName())+"&ln="+IppmsUtils.treatNull(pEHB.getLastName())+"&noe="+emp.size()+"&cn=11";
		}



	}





}
