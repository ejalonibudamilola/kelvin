package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.EmpContMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping({"/viewEmployeeBVNInfo.do"})
@SessionAttributes(types={BusinessEmpOVBean.class, EmpContMiniBean.class})
public class EmployeeBVNReportFormController extends BaseController {

	  @Autowired
	  private PaycheckService paycheckService;

	  @Autowired
	  private HRService hrService;

	  private final int pageLength = 100;
	  
	  private final String VIEW = "employee/empBvnInfoForm";

	  
	  public EmployeeBVNReportFormController()
	  {}
	 
	  @ModelAttribute("monthList")
	  public List<NamedEntity> getMonthList() {
		return PayrollBeanUtils.makeAllMonthList();
	}
	  @ModelAttribute("mdaList")
	  protected List<MdaInfo> loadAllMdas(HttpServletRequest request){
		  return this.genericService.loadAllObjectsUsingRestrictions(MdaInfo.class,
						  Arrays.asList(CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()),
								  CustomPredicate.procurePredicate("mappableInd", 0)), "name");
	  }
	  @ModelAttribute("yearList")
	  public Collection<NamedEntity> makeYearList(HttpServletRequest request) {
		BusinessCertificate bc = getBusinessCertificate(request);
	    return this.paycheckService.makePaycheckYearList(bc);
	  }
	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);


	    PayrollFlag wPf =  this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));

	    PaginationBean paginationBean = getPaginationInfo(request);

	    List<Employee> wRetList = this.hrService.loadEmpBVNInformation((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder()
				, paginationBean.getSortCriterion(), wPf.getApprovedMonthInd(), wPf.getApprovedYearInd(),
				new BusinessEmpOVBean(),false, bc);
	   
	    EmpContMiniBean wNoOfElements =  this.hrService.getTotalNoOfEmpBVNInformation(wPf.getApprovedMonthInd(), wPf.getApprovedYearInd(),new BusinessEmpOVBean(), bc);
	    BusinessEmpOVBean wPELB = new BusinessEmpOVBean(wRetList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements.getObjectInd(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());

	    wPELB.setRunMonth(wPf.getApprovedMonthInd());
	    wPELB.setRunYear(wPf.getApprovedYearInd());
	   
	    wPELB.setName(PayrollBeanUtils.getMonthNameFromInteger(wPf.getApprovedMonthInd())+", "+ wPf.getApprovedYearInd());
	   /* GregorianCalendar wCal = new GregorianCalendar();
	    wCal.set(wPf.getApprovedYearInd(), wPf.getApprovedMonthInd(), 1);
	    wPELB.setAllowanceStartDate(wCal.getTime());*/
	    addRoleBeanToModel(model, request);
	    model.addAttribute("statBean", wNoOfElements);
	    model.addAttribute("miniBean", wPELB);

	    return VIEW;
	  }

	  @RequestMapping(method={RequestMethod.GET},params={"rm","ry"})
	  public String setupForm(
			  @RequestParam("rm") int pRunMonth, 
			  @RequestParam("ry") int pRunYear,
			  Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

      Object wParam = model.getAttribute("miniBean");
      BusinessEmpOVBean wConfigBean =  new BusinessEmpOVBean();
      if(wParam != null ){
      	wConfigBean = (BusinessEmpOVBean) wParam;
      }

      PaginationBean paginationBean = getPaginationInfo(request);

	    List<Employee> wRetList = this.hrService.loadEmpBVNInformation((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), pRunMonth, pRunYear, wConfigBean,false, bc);
		   
	    EmpContMiniBean wNoOfElements =  this.hrService.getTotalNoOfEmpBVNInformation(pRunMonth, pRunYear,wConfigBean, bc);
	    BusinessEmpOVBean wPELB = new BusinessEmpOVBean(wRetList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements.getObjectInd(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
	    wPELB.setName(PayrollBeanUtils.getMonthNameFromInteger(pRunMonth)+", "+ pRunYear);
	    wPELB.setStatBean(wNoOfElements);
	    wPELB.setRunMonth(pRunMonth);
	    wPELB.setRunYear(pRunYear);
	    wPELB.setMdaInd(wConfigBean.getMdaInd());
	    wPELB.setBankTypeInd(wConfigBean.getBankTypeInd());
	    wPELB.setBvnStatusInd(wConfigBean.getBvnStatusInd());
	    wPELB.setStatusInd(wConfigBean.getStatusInd());
	    addRoleBeanToModel(model, request);
	    model.addAttribute("statBean", wNoOfElements);
	    model.addAttribute("miniBean", wPELB);

	    return VIEW;
	  }

	 
	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
			  @RequestParam(value="_updateReport", required=false) String updRep, 
			  @ModelAttribute("miniBean") BusinessEmpOVBean pLPB,@ModelAttribute("statBean") EmpContMiniBean pEmpContMiniBean, BindingResult result, 
			  SessionStatus status, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);

	    
	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
	    	removeSessionAttribute(request,  BVN_CONFIG_BEAN);
	    	return "redirect:hrRelatedReports.do";
	    }
	     
	    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
	      if ((pLPB.getRunMonth() == -1 ))
	      {
	        result.rejectValue("", "InvalidValue", "Please select Payroll Month");
	        ((BusinessEmpOVBean)result.getTarget()).setDisplayErrors("block");

	        model.addAttribute("status", result);
	        model.addAttribute("statBean", pEmpContMiniBean);
	        model.addAttribute("miniBean", pLPB);
	        addRoleBeanToModel(model, request);

	        return VIEW;
	      }
	      if ((pLPB.getRunYear() == -1 ))
	      {
	        result.rejectValue("", "InvalidValue", "Please select Year");
	        ((BusinessEmpOVBean)result.getTarget()).setDisplayErrors("block");

	        model.addAttribute("status", result);
	        model.addAttribute("statBean", pEmpContMiniBean);
	        model.addAttribute("miniBean", pLPB);addRoleBeanToModel(model, request);

	        return VIEW;
	      }
	      
	      result = this.filterCriteriaCheck(pLPB,result);
	      if(result.hasErrors()){
	    	  
		        ((BusinessEmpOVBean)result.getTarget()).setDisplayErrors("block");

		        model.addAttribute("status", result);
		        model.addAttribute("statBean", pEmpContMiniBean);
		        model.addAttribute("miniBean", pLPB);
		        addRoleBeanToModel(model, request);

		        return VIEW;  
	      }
	      //--DO NOT REMOVE. For Printing to Excel.
	      addSessionAttribute(request, BVN_CONFIG_BEAN, pLPB);
	      return "redirect:viewEmployeeBVNInfo.do?rm=" + pLPB.getRunMonth() + "&ry=" + pLPB.getRunYear();
	    
	  }
		return "redirect:viewEmployeeBVNInfo.do";
	  }

	private BindingResult filterCriteriaCheck(BusinessEmpOVBean pLPB,
			BindingResult pResult)
	{
		if(pLPB.getAllowanceStartDate() != null){
			
			if(pLPB.getAllowanceEndDate() != null){

				if(pLPB.getAllowanceStartDate().compareTo(pLPB.getAllowanceEndDate()) > 0){
					pResult.rejectValue("", "", "Service Entry Dates are not valid.");
				}
			}
			
		}
		
		return pResult;
	}

}
