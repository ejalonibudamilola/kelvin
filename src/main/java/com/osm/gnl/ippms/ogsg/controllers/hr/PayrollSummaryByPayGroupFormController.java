package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/payrollSummaryByPaygroup.do"})
@SessionAttributes(types={WageBeanContainer.class})
public class PayrollSummaryByPayGroupFormController extends BaseController {

	  
	  protected int OTHER_ID;

	  @Autowired
	  private PaycheckService paycheckService;

	  @Autowired
	  PayrollService payrollService;

 	  private final String VIEW_NAME = "payment/paySumByGLPayGroupForm";
	  
	  //private List<NamedEntityBean> modelBean;

	 
	  @ModelAttribute("yearList")
	  protected List<NamedEntity> getYearList(HttpServletRequest request){
		  return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
	  }
	  
	  @ModelAttribute("monthList")
	  protected List<NamedEntity> getMonthList() {
	    return PayrollBeanUtils.makeAllMonthList();
	  }
	  
	  public PayrollSummaryByPayGroupFormController()
	  {}

	  
	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) 
	  throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
		  SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);


		  OTHER_ID = 20;
		PayrollFlag pf = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));
	    LocalDate startDate = null;
	    

	    if (!pf.isNewEntity()) {
	      startDate = pf.getPayPeriodStart();
	       
	    } else {
	      ArrayList<LocalDate> list = PayrollBeanUtils.getDefaultCheckRegisterDates();
	      startDate = list.get(0);
	      
	    }
	    LocalDate fDate = PayrollBeanUtils.setDateFromString(PayrollBeanUtils.getJavaDateAsString(startDate));

	    Object wObj = createViewForm(request, fDate, model, bc);
	    if(wObj.getClass().isInstance(String.class)){
	    	return (String)wObj;
	    }
		  addRoleBeanToModel(model, request);
	    return VIEW_NAME;
	  }

	 
	

	@RequestMapping(method={RequestMethod.GET}, params={"rm", "ry"})
	  public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int  pRunYear, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
		  SessionManagerService.manageSession(request, model);

		 BusinessCertificate bc = this.getBusinessCertificate(request);
		 OTHER_ID = 20;
 
	    LocalDate fDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
	     
 
	    Object wObj = createViewForm(request, fDate, model, bc);
	    if(wObj.getClass().isInstance(String.class)){
	    	return (String)wObj;
	    }
		addRoleBeanToModel(model, request);
 	    return VIEW_NAME; 
	  }

	  private Object createViewForm(HttpServletRequest request, LocalDate pDate, Model pModel, BusinessCertificate pBizCert) throws InstantiationException, IllegalAccessException {
		  
		  
		  WageBeanContainer wBEOB = new WageBeanContainer();
		  wBEOB.setRunMonth(pDate.getMonthValue());
		    wBEOB.setRunYear(pDate.getYear());

		  int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(pBizCert),genericService,pBizCert);


		  if (noOfEmpWivNegPay > 0)
		    {
				LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(pBizCert);
		      return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + _wCal.getMonthValue() + "&ry=" + _wCal.getYear();
		    }

		   
		  List<NamedEntityBean> wEPBList = this.payrollService.loadPayrollSummaryByRunMonthAndYear(pBizCert,pDate.getMonthValue(), pDate.getYear());

		    
		    wBEOB.setTotalNoOfEmp(wEPBList.size());
		    HashMap<String,NamedEntityBean> wModelBean = new HashMap<>();
		    

		    for (NamedEntityBean e : wEPBList)
		    {
		    	
		    	e = PayrollUtils.makeCode(e,OTHER_ID);
		    	if(e.getCurrentOtherId() != null && e.getCurrentOtherId() > 0){
		    		OTHER_ID = e.getCurrentOtherId();
		    	}
		    	NamedEntityBean wNEB = wModelBean.get(e.getName());
		    	if(wNEB == null){
		    		wNEB = new  NamedEntityBean();
		    		wNEB.setTypeOfEmpType(e.getTypeOfEmpType());
		    	} 
		    	wNEB.setTotalDeductions(wNEB.getTotalDeductions() + e.getTotalDeductions());
		    	wNEB.setTotalPay(wNEB.getTotalPay() + e.getTotalPay());
		    	wNEB.setNetPay(wNEB.getNetPay() + e.getNetPay());
		    	wNEB.setName(e.getName());
		    	wNEB.setId(e.getId());
		    	wNEB.setNoOfActiveEmployees(wNEB.getNoOfActiveEmployees() + e.getNoOfActiveEmployees());
		    	wModelBean.put(e.getName(), wNEB);
		    	wBEOB.setTotalDeductions(wBEOB.getTotalDeductions() + e.getTotalDeductions());
		    	wBEOB.setTotalGrossSalary(wBEOB.getTotalGrossSalary() + e.getTotalPay());
		    	wBEOB.setTotalNetPay(wBEOB.getTotalNetPay() + e.getNetPay());
		    	wBEOB.setTotalNoOfEmp(wBEOB.getTotalNoOfEmp() + e.getNoOfActiveEmployees());
		    	
		    }
		    wBEOB.setPayPeriodStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pDate.getMonthValue(), pDate.getYear()));
		     
		    wBEOB.setNamedEntityBeanList(PayrollBeanUtils.getListFromMap(wModelBean,true));
		    pModel.addAttribute("miniBean", wBEOB);
		    pModel.addAttribute("roleBean", pBizCert);
		  addRoleBeanToModel(pModel, request);
		    
	    return pModel;
	  }
	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") WageBeanContainer pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);

	   
	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
	      return "redirect:reportsOverview.do";
	    }
	    
	    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
	    	
	    	if (pLPB.getRunYear() == -1) 
		      {
		        result.rejectValue("", "InvalidValue", "Please select a Payroll Year");
		        ((WageBeanContainer)result.getTarget()).setDisplayErrors("block");

				  addRoleBeanToModel(model, request);
		        model.addAttribute("status", result);
		        model.addAttribute("miniBean", pLPB);

		        return VIEW_NAME;
		      }
	      if (pLPB.getRunMonth() == -1) 
	      {
	        result.rejectValue("", "InvalidValue", "Please select a Payroll Month");
			  addDisplayErrorsToModel(model, request);

	        if(pLPB.getRunYear() > 0)
	        	model.addAttribute("monthList", this.payrollService.loadPayrollMonthsByYear(bc, pLPB.getRunYear()));
	        
	        model.addAttribute("roleBean", bc);
	        model.addAttribute("status", result);
	        model.addAttribute("miniBean", pLPB);
			  addRoleBeanToModel(model, request);
	        return VIEW_NAME;
	      }
	      
	      return "redirect:payrollSummaryByPaygroup.do?rm=" + pLPB.getRunMonth()+"&ry="+pLPB.getRunYear();
	    }

	    return "redirect:payrollSummaryByPaygroup.do";
	  }
	  
	 /* private NamedEntityBean makeCode(NamedEntityBean pNamedEntityBean)
		{
			 
		    if(pNamedEntityBean.getObjectInd() == 1){
		    	pNamedEntityBean.setName("GL"+String.valueOf(pNamedEntityBean.getParentId())); 
				pNamedEntityBean.setId(pNamedEntityBean.getParentId());
		    }else if(pNamedEntityBean.getObjectInd() == 2){
		    	pNamedEntityBean.setName("Political Appointees");
				pNamedEntityBean.setId(POL_APP_ID);
		    }else if(pNamedEntityBean.getObjectInd() == 3){
		    	pNamedEntityBean.setName("HOS/PS/GMs/AGs");
				pNamedEntityBean.setId(HOPS_ID);
				 
		    }else{
		    	OTHER_ID++;
		    	pNamedEntityBean.setId(OTHER_ID);
		    }
			 
			
			return pNamedEntityBean;
		}*/


}
