package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.NamedEntityLong;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.search.SearchValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping("/enterVariableForCashBook.do")
@SessionAttributes(types = HrMiniBean.class)
public class EnterVariablesForCashbookController extends BaseController {

	private static final String VIEW_NAME = "report/preCashbookForm";

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private SearchValidator validator;

	@ModelAttribute("monthList")
	protected List<NamedEntity> makeMonthList(){
		return PayrollBeanUtils.makeMonthList();

	}
	
	@ModelAttribute("yearList")
	protected Collection<NamedEntityLong> makeYearList(HttpServletRequest request){
		List<NamedEntityLong> wRetList = new ArrayList<NamedEntityLong>();
		//First we need to get the years payroll has been run for..
		List<EmployeePayBean> wEmpPayBean = employeeService.getPayrollRunYears(getBusinessCertificate(request));
		
		for(EmployeePayBean e :wEmpPayBean ){
			NamedEntityLong n = new NamedEntityLong();
			n.setId(Long.valueOf(e.getRunYear()));
			
			n.setName(String.valueOf(e.getRunYear()));
			wRetList.add(n);
		}
		Collections.sort(wRetList);
		return wRetList;
	}
	

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);
 
		HrMiniBean empHrBean = new HrMiniBean();
		empHrBean.setAmountStr("0.0");
		empHrBean.setPayeStr("0.0");
		addRoleBeanToModel(model, request);
		model.addAttribute("empMiniBean", empHrBean);
		return VIEW_NAME;
		
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,
			@RequestParam(value = REQUEST_PARAM_CONFIRM, required = false) String pConfirm,
			@RequestParam(value = REQUEST_PARAM_OK, required = false) String pOk,
			@ModelAttribute ("empMiniBean") HrMiniBean pEHB, BindingResult result,
			SessionStatus status, Model model,HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
 
		
		if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
			return "redirect:reportsOverview.do";
		}

		validator.validateForCashbook(pEHB, result,getBusinessCertificate(request));
		if (result.hasErrors()) {
			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model, request);
			model.addAttribute("status", result);
			model.addAttribute("empMiniBean", pEHB);
			return VIEW_NAME;
		}
		//First thing is to Check for OK.
		 
		if(isButtonTypeClick(request, REQUEST_PARAM_OK)){
			pEHB.setShowConfirmationRow(true);
			result.rejectValue("", "Pending.Paychecks", "Please Enter the Generated Code to continue.");
			pEHB.setName(PassPhrase.generateCapcha(8));
			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model, request);
			model.addAttribute("status", result);
			model.addAttribute("empMiniBean", pEHB);
			return VIEW_NAME;
		}
		
		 
		if(isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)){
			//All we need do is check capcha...
				if(!pEHB.getName().equalsIgnoreCase(pEHB.getMode())){
					pEHB.setShowConfirmationRow(true);
					result.rejectValue("", "Pending.Paychecks", "Entered Code "+pEHB.getMode()+" Did not match Generated Code "+pEHB.getName());
					pEHB.setName(PassPhrase.generateCapcha(8));
					addDisplayErrorsToModel(model, request);
					addRoleBeanToModel(model, request);
					model.addAttribute("status", result);
					model.addAttribute("empMiniBean", pEHB);
					return VIEW_NAME;
				}
			
		}
		
		 return "redirect:cashBookReportSummary.do?phe="+pEHB.getAmountStr()+"&pht="+pEHB.getPayeStr()+"&month="+pEHB.getMonthInd()+"&year="+pEHB.getYearInd();
			
		
	}
	
	


}
