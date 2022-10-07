package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping({"/viewContractEmployees.do"})
@SessionAttributes(types={PaginatedBean.class})
public class StaffOnContractController extends BaseController
{

    /**
     * Kasumu Taiwo
     * 12-2020
     */

  @Autowired
  PayrollService payrollService;
   
  private final int pageLength = 20;
	private final String VIEW_NAME = "employee/viewEmployeesOnContractForm";
  

 
  @ModelAttribute("yearList")
  protected List<NamedEntityBean> getYearList(HttpServletRequest request){
	  return this.payrollService.makeFutureYearsList(getBusinessCertificate(request).getBusinessClientInstId());
  }
  
  @ModelAttribute("monthList")
  protected List<NamedEntity> getMonthList() {
    return PayrollBeanUtils.makeAllMonthList();
  }


  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LocalDate wToday = LocalDate.now();

    PaginationBean paginationBean = this.getPaginationInfo(request);


    List<ContractHistory> empList = this.payrollService.getActiveContracts((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(),
            wToday.getMonthValue(), wToday.getYear(),null,null,0 , bc);

    int wGLNoOfElements = this.payrollService.getTotalNumberOfActiveContracts(wToday.getMonthValue(), wToday.getYear(),null,null,0, bc);

    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

      if(wGLNoOfElements > 0){
          wPELB.setObjectsExist(true);
      }

    wPELB.setShowRow(SHOW_ROW);
    wPELB.setRunMonth(wToday.getMonthValue());
    wPELB.setRunYear(wToday.getYear());
    wPELB.setLastName("");
    wPELB.setOgNumber("");
    wPELB.setEmpInstId(0L);
    wPELB.setFromDate(LocalDate.now());
    wPELB.setId(-1L);
    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wPELB);

    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"rm","ry","og","ln","toc"})
  public String setupForm(@RequestParam("rm") int pRunMonth,
		  @RequestParam("ry") int pRunYear,
		  @RequestParam("og") Long pEmpInstId,
		  @RequestParam("ln") String pLastName,
		  @RequestParam("toc") int pTypeOfContract,
		  Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    PaginationBean paginationBean = this.getPaginationInfo(request);


    List<ContractHistory> empList = this.payrollService.getActiveContracts((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(),pRunMonth, pRunYear, pEmpInstId,pLastName, pTypeOfContract, bc);

    int wGLNoOfElements = this.payrollService.getTotalNumberOfActiveContracts(pRunMonth, pRunYear, pEmpInstId,pLastName, pTypeOfContract, bc);

    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    if(wGLNoOfElements > 0){
        wPELB.setObjectsExist(true);
    }

    wPELB.setShowRow(SHOW_ROW);

    wPELB.setRunMonth(pRunMonth);
    wPELB.setRunYear(pRunYear);
    wPELB.setLastName(pLastName);
    wPELB.setEmpInstId(pEmpInstId);
    if(pEmpInstId > 0){
        AbstractEmployeeEntity abstractEmployeeEntity = IppmsUtils.loadEmployee(genericService,pEmpInstId,bc);
        wPELB.setOgNumber(abstractEmployeeEntity.getEmployeeId());
    }

    wPELB.setNoOfYearsInService(pTypeOfContract);
    wPELB.setId(0L);
    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wPELB);

    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, 
		  @RequestParam(value="_close", required=false) String close, 
		  @ModelAttribute("miniBean") PaginatedBean pLPB, BindingResult result, 
		  SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

     
    if (isButtonTypeClick(request, REQUEST_PARAM_CLOSE)) {
      return "redirect:reportsOverview.do";
    }
     

    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
     /* if (pLPB.getRunMonth() < 0 && pLPB.getRunYear() == 0 && StringUtils.isBlank(pLPB.getOgNumber()) && StringUtils.isBlank(pLPB.getLastName()))
      {
        result.rejectValue("", "InvalidValue", "Please select a valid values Month and Year or Enter a value for Last Name or "+bc.getStaffTitle());
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", pLPB);

        return VIEW_NAME;
      }*/
       
      Long pEmpInstId = 0L;
      if(pLPB.getOgNumber() != null && StringUtils.isNotEmpty(pLPB.getOgNumber())) {

    	  Employee emp = this.genericService.loadObjectWithSingleCondition(Employee.class,
                  CustomPredicate.procurePredicate("employeeId", super.treatOgNumber(pLPB.getOgNumber().toUpperCase(),bc)));
    	  if(!emp.isNewEntity())
    		  pEmpInstId = emp.getId();
      }
      if(pLPB.getLastName() == null)
    	  pLPB.setLastName("");
      if(pLPB.getOgNumber() == null)
    	  pLPB.setOgNumber("");
      return "redirect:viewContractEmployees.do?rm=" + pLPB.getRunMonth()+"&ry="+pLPB.getRunYear()+"&og="+pEmpInstId+"&ln="+pLPB.getLastName()+"&toc="+pLPB.getNoOfYearsInService();
    }

    return "redirect:viewContractEmployees.do";
  }
}