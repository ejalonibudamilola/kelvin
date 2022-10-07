package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/viewSalaryStructure.do"})
@SessionAttributes(types={BusinessEmpOVBean.class})
public class SalaryStructureReportController extends BaseController{
  @Autowired
  private EmployeeService employeeService;

  private final String VIEW = "report/salaryScaleReportForm";
 
  private final int pageLength = 20;
  

  
  public SalaryStructureReportController()
  {}
  
  
@ModelAttribute("salaryTypeList")
  public Collection<SalaryType> populateSalaryScaleList(HttpServletRequest request) {

    return this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(getBusinessClientIdPredicate(request),
            CustomPredicate.procurePredicate("selectableInd",ON) ), "name");
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


    BusinessEmpOVBean wBEOB = new BusinessEmpOVBean(new ArrayList<AbstractEmployeeEntity>(), 1, this.pageLength, 0, null, "asc");

    wBEOB.setShowRow(HIDE_ROW);
    wBEOB.setDisplayTitle(""+bc.getStaffTypeName()+" Pay Group List");
    model.addAttribute("miniBean", wBEOB);
    model.addAttribute("roleBean", bc);
    return VIEW;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"ssc"})
  public String setupForm(@RequestParam("ssc") Long pSsc, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

	  PaginationBean paginationBean = getPaginationInfo(request);


    List<AbstractEmployeeEntity> empList = this.employeeService.getActiveEmployeesByPayGroup((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength,
            paginationBean.getSortOrder(), paginationBean.getSortCriterion(), pSsc, 0, 0, bc);
    
    SalaryType wSS = this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
            CustomPredicate.procurePredicate("id",pSsc)));

    int wGLNoOfElements = this.employeeService.getTotalNoOfEmployeesOnPayGroup(bc,pSsc,0,0);

    BusinessEmpOVBean wBEOB = new BusinessEmpOVBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
    wBEOB.setId(pSsc);
    wBEOB.setShowRow(SHOW_ROW);
    wBEOB.setSalaryStructureName(wSS.getName());
    wBEOB.setDisplayTitle(""+bc.getStaffTypeName()+" Pay Group List");
    model.addAttribute("miniBean", wBEOB);
    addRoleBeanToModel(model, request);
		return VIEW;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"ssc", "fl", "tl"})
  public String setupForm(@RequestParam("ssc") Long pSsc, @RequestParam("fl") int pFromLevel, @RequestParam("tl") int pToLevel, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    PaginationBean paginationBean = getPaginationInfo(request);

    List<AbstractEmployeeEntity> empList = new ArrayList<>();
    int wGLNoOfElements = 0;
     
      empList = this.employeeService.getActiveEmployeesByPayGroup((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), pSsc, pFromLevel, pToLevel,bc);
      wGLNoOfElements = this.employeeService.getTotalNoOfEmployeesOnPayGroup(bc, pSsc, pFromLevel, pToLevel);


    SalaryType wSS = this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
            CustomPredicate.procurePredicate("id",pSsc)));

    BusinessEmpOVBean wBEOB = new BusinessEmpOVBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
    wBEOB.setId( pSsc);
    wBEOB.setFromLevel(pFromLevel);
    wBEOB.setToLevel(pToLevel);
    wBEOB.setShowRow(SHOW_ROW);
    wBEOB.setSalaryStructureName(wSS.getName());
    model.addAttribute("miniBean", wBEOB);
    addRoleBeanToModel(model, request);
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
        result.rejectValue("id", "InvalidValue", "Please select a Salary Scale Type");
        addDisplayErrorsToModel(model, request);

        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);
        return VIEW;
      }
      if ((pLPB.getFromLevel() > 0) || (pLPB.getToLevel() > 0)) {
        if (pLPB.getFromLevel() > pLPB.getToLevel()) {
          result.rejectValue("id", "InvalidValue", "'From' Level can not be greater than 'To' Level");
          addDisplayErrorsToModel(model, request);

          model.addAttribute("status", result);
          model.addAttribute("miniBean", pLPB);

          addRoleBeanToModel(model, request);
          return VIEW;
        }
        return "redirect:viewSalaryStructure.do?ssc=" + pLPB.getId() + "&fl=" + pLPB.getFromLevel() + "&tl=" + pLPB.getToLevel();
      }
       

      return "redirect:viewSalaryStructure.do?ssc=" + pLPB.getId();
    }

    return "redirect:viewSalaryStructure.do";
  }

  

  
}