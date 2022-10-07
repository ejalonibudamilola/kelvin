package com.osm.gnl.ippms.ogsg.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaymentService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping({"/viewPayGroupEmployees.do"})
@SessionAttributes(types={BusinessEmpOVBean.class})
public class PayGroupReportController extends BaseController {

	  @Autowired
	  private PayrollService payrollService;

	  @Autowired
	  private PaymentService paymentService;
	  
	  private final int pageLength = 20;
	  private final String VIEW = "payment/payGroupReportForm";
	  
	  public PayGroupReportController()
	  {}
	  @ModelAttribute("salaryTypeList")
	  public List<SalaryType> populateSalaryScaleList(HttpServletRequest request) {

	  		BusinessCertificate bc = getBusinessCertificate(request);

		  List<SalaryType> wRetList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(
		  		CustomPredicate.procurePredicate("selectableInd", IConstants.ON), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "name");

		  Collections.sort(wRetList);
	    return wRetList;
	  }
	  @ModelAttribute("fromLevelList")
	  public Collection<NamedEntityBean> populateLevelList() {
		  ArrayList<NamedEntityBean> wRetList = new ArrayList<NamedEntityBean>();
	    for (long i = 1; i < 18; i++) {
	      NamedEntityBean n = new NamedEntityBean();
	      n.setId(i);
	      n.setName(String.valueOf(i));
	      wRetList.add(n);
	    }
	    Collections.sort(wRetList);
	    return wRetList;
	    
	  }

	  @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);
	    

	    BusinessEmpOVBean wBEOB = new BusinessEmpOVBean(new ArrayList<Employee>(), 1, this.pageLength, 0, null, "asc");

	    wBEOB.setShowRow(HIDE_ROW);
	    model.addAttribute("miniBean", wBEOB);
	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }

	  @RequestMapping(method={RequestMethod.GET}, params={"ssc"})
	  public String setupForm(@RequestParam("ssc") Long pSsc, Model model, HttpServletRequest request) throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);
	    
	    int pageNumber = ServletRequestUtils.getIntParameter(request, "page", 1);
	    String sortOrder = ServletRequestUtils.getStringParameter(request, "dir", "asc");
	    String sortCriterion = ServletRequestUtils.getStringParameter(request, "sort", null);

	    List<Employee> empList = this.paymentService.getActiveEmployeesByPayGroup((pageNumber - 1) * this.pageLength, this.pageLength, sortOrder, sortCriterion, pSsc,0,0, bc);

	    
	    SalaryType wSS = this.genericService.loadObjectById(SalaryType.class, pSsc);

	    int wGLNoOfElements = this.paymentService.getTotalNoOfEmployeesOnPayGroup(pSsc,0,0, bc);

	    BusinessEmpOVBean wBEOB = new BusinessEmpOVBean(empList, pageNumber, this.pageLength, wGLNoOfElements, sortCriterion, sortOrder);
	    wBEOB.setId(pSsc);
	    wBEOB.setShowRow(SHOW_ROW);
	    wBEOB.setSalaryStructureName(wSS.getName());
	    model.addAttribute("miniBean", wBEOB);

	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }

	  @RequestMapping(method={RequestMethod.GET}, params={"ssc", "fl", "tl"})
	  public String setupForm(@RequestParam("ssc") Long pSsc, @RequestParam("fl") int pFromLevel, @RequestParam("tl") int pToLevel, Model model, HttpServletRequest request) throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);
	    

	    int pageNumber = ServletRequestUtils.getIntParameter(request, "page", 1);
	    String sortOrder = ServletRequestUtils.getStringParameter(request, "dir", "asc");
	    String sortCriterion = ServletRequestUtils.getStringParameter(request, "sort", null);

	    List<Employee> empList = new ArrayList<Employee>();
	    int wGLNoOfElements = 0;
	    
	      empList = this.paymentService.getActiveEmployeesByPayGroup((pageNumber - 1) * this.pageLength, this.pageLength, sortOrder, sortCriterion, pSsc, pFromLevel, pToLevel, bc);
	      wGLNoOfElements = this.paymentService.getTotalNoOfEmployeesOnPayGroup(pSsc, pFromLevel, pToLevel, bc);


	    SalaryType wSS = this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(CustomPredicate.procurePredicate("id", pSsc),
				CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));

	    BusinessEmpOVBean wBEOB = new BusinessEmpOVBean(empList, pageNumber, this.pageLength, wGLNoOfElements, sortCriterion, sortOrder);
	    wBEOB.setId(pSsc);
	    wBEOB.setFromLevel(pFromLevel);
	    wBEOB.setToLevel(pToLevel);
	    wBEOB.setShowRow(SHOW_ROW);
	    wBEOB.setSalaryStructureName(wSS.getName());
	    model.addAttribute("miniBean", wBEOB);

	    model.addAttribute("roleBean", bc);
	    return VIEW;
	  }

	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") BusinessEmpOVBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
		     
		  BusinessCertificate bc = this.getBusinessCertificate(request);
	    
	     
	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
	      return "redirect:hrEmployeeRelatedReports.do";
	    }
	    
	    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
	      if (IppmsUtils.isNullOrLessThanOne(pLPB.getId()))
	      {
	        result.rejectValue("id", "InvalidValue", "Please select a Pay Group");
	        ((BusinessEmpOVBean)result.getTarget()).setDisplayErrors("block");

	        model.addAttribute("status", result);
	        model.addAttribute("miniBean", pLPB);

	        model.addAttribute("roleBean", bc);
	        return VIEW;
	      }
	      if ((pLPB.getFromLevel() > 0) && (pLPB.getToLevel() > 0)) {
	        if (pLPB.getFromLevel() > pLPB.getToLevel()) {
	          result.rejectValue("id", "InvalidValue", "'From' Level can not be greater than 'To' Level");
	          ((BusinessEmpOVBean)result.getTarget()).setDisplayErrors("block");

	          model.addAttribute("status", result);
	          model.addAttribute("miniBean", pLPB);

	          model.addAttribute("roleBean", bc);
	          return VIEW;
	        }
	        return "redirect:viewPayGroupEmployees.do?ssc=" + pLPB.getId() + "&fl=" + pLPB.getFromLevel() + "&tl=" + pLPB.getToLevel();
	      }
	      

	      return "redirect:viewPayGroupEmployees.do?ssc=" + pLPB.getId();
	    }

	    return "redirect:viewPayGroupEmployees.do";
	  }

	 

}
