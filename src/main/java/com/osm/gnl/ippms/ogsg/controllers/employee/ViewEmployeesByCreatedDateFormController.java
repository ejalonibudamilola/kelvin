package com.osm.gnl.ippms.ogsg.controllers.employee;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractEmployeeAuditEntity;
import com.osm.gnl.ippms.ogsg.audit.domain.EmployeeAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;



@Controller
@RequestMapping({"/viewCreatedEmpForm.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewEmployeesByCreatedDateFormController extends BaseController
{
  
  
  private final int pageLength = 80;
  
 
  public ViewEmployeesByCreatedDateFormController()
  {}

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);


    LocalDate wToday = LocalDate.now();
    LocalDate fDate = LocalDate.of(wToday.getYear(), wToday.getMonthValue(), 1);
    LocalDate tDate = LocalDate.of(wToday.getYear(), wToday.getMonthValue(), wToday.lengthOfMonth());

    PaginationBean paginationBean = getPaginationInfo(request);

//    List<EmployeeAudit> empList = this.payrollService.getEmployeeAuditLogByDateAndCode((pageNumber - 1) * this.pageLength, this.pageLength, sortOrder, sortCriterion, fDate.getTime(), tDate.getTime(), "I");

//    List<AbstractEmployeeAuditEntity> empList = (List<AbstractEmployeeAuditEntity>)this.genericService.loadPaginatedObjects(IppmsUtils.getEmployeeAuditEntityClass(bc), Arrays.asList(
//            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
//            CustomPredicate.procurePredicate("auditActionType", "I"),
//            CustomPredicate.procurePredicate("lastModTs", fDate, Operation.GREATER_OR_EQUAL),
//            CustomPredicate.procurePredicate("lastModTs", tDate, Operation.LESS_OR_EQUAL)),
//            (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

    List<AbstractEmployeeAuditEntity> empList = (List<AbstractEmployeeAuditEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getEmployeeAuditEntityClass(bc), Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
            CustomPredicate.procurePredicate("auditActionType", "I"),
            CustomPredicate.procurePredicate("lastModTs", fDate, Operation.GREATER_OR_EQUAL),
            CustomPredicate.procurePredicate("lastModTs", tDate, Operation.LESS_OR_EQUAL)), null);

    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("auditActionType","I"));

//    int wGLNoOfElements = this.payrollService.getTotalNoOfEmployeeAuditLogByDateAndCode(bc.getBusinessClientInstId(), fDate.getTime(), tDate.getTime(), "I");

    int wGLNoOfElements = this. genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmployeeAudit.class);

    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wPELB.setShowRow(SHOW_ROW);

    wPELB.setFromDate(fDate);
    wPELB.setToDate(tDate);

    wPELB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(fDate));
    wPELB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(tDate));

    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wPELB);
    return "employee/viewEmpByCreatedDateForm";
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
    LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

    PaginationBean paginationBean = getPaginationInfo(request);

//    List<EmployeeAudit> empList = this.payrollService.getEmployeeAuditLogByDateAndCode((pageNumber - 1) * this.pageLength, this.pageLength, sortOrder, sortCriterion, fDate.getTime(), tDate.getTime(), "I");

//    List<AbstractEmployeeAuditEntity> empList = (List<AbstractEmployeeAuditEntity>)this.genericService.loadPaginatedObjects(IppmsUtils.getEmployeeAuditEntityClass(bc), Arrays.asList(
//            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
//            CustomPredicate.procurePredicate("auditActionType", "I"),
//            CustomPredicate.procurePredicate("lastModTs", fDate, Operation.GREATER_OR_EQUAL),
//            CustomPredicate.procurePredicate("lastModTs", tDate, Operation.LESS_OR_EQUAL)),
//            (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

    List<AbstractEmployeeAuditEntity> empList = (List<AbstractEmployeeAuditEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getEmployeeAuditEntityClass(bc), Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
            CustomPredicate.procurePredicate("auditActionType", "I"),
            CustomPredicate.procurePredicate("lastModTs", fDate, Operation.GREATER_OR_EQUAL),
            CustomPredicate.procurePredicate("lastModTs", tDate, Operation.LESS_OR_EQUAL)),null);


    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("auditActionType","I"));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("lastModTs",fDate, Operation.GREATER_OR_EQUAL));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("lastModTs",tDate, Operation.LESS_OR_EQUAL));
//    int wGLNoOfElements = this.payrollService.getTotalNoOfEmployeeAuditLogByDateAndCode(bc.getBusinessClientInstId(), fDate.getTime(), tDate.getTime(), "I");

    int wGLNoOfElements = this. genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmployeeAudit.class);

    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wPELB.setShowRow(SHOW_ROW);

    wPELB.setFromDate(fDate);
    wPELB.setToDate(tDate);

    wPELB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(fDate));
    wPELB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(tDate));

    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wPELB);

    return "employee/viewEmpByCreatedDateForm";
  }
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
		  @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") PaginatedBean pLPB,
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return REDIRECT_TO_DASHBOARD;
    }
    
    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
      if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null))
      {
        result.rejectValue("", "InvalidValue", "Please select valid Dates");
        addDisplayErrorsToModel(model, request);

        addRoleBeanToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);

        return "viewEmpByCreatedDateForm";
      }
      LocalDate date1 = pLPB.getFromDate();
      LocalDate date2 = pLPB.getToDate();

      if (date1.isAfter(date2)) {
        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
        addDisplayErrorsToModel(model, request);

        addRoleBeanToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);

        return "employee/viewEmpByCreatedDateForm";
      }

      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
      return "redirect:viewCreatedEmpForm.do?fd=" + sDate + "&td=" + eDate;
    }

    return "redirect:viewCreatedEmpForm.do";
  }
}