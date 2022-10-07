package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.IppmsMultiFormController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionType;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
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


/**
 * @author Damilola Ejalonibu
 */


@Controller
@RequestMapping({"/viewSuspendedEmployees.do"})
@SessionAttributes(types={PaginatedBean.class})
public class SuspendedStaffReportController extends BaseController
{
  
  private final int pageLength = 20;
  
  private final String VIEW = "suspension/viewEmployeesOnSuspensionForm";

  @Autowired
  EmployeeService employeeService;

  
  public SuspendedStaffReportController()
  {}
  
  @ModelAttribute("userList")
  public List<User> populateUsersList() {

    return this.genericService.loadAllObjectsWithSingleCondition(User.class,
            CustomPredicate.procurePredicate("accountLocked", IConstants.OFF), "firstName");
  }
  
  @ModelAttribute("userList")
  public List<SuspensionType> populateSuspensionList(HttpServletRequest request) {

    BusinessCertificate bc = this.getBusinessCertificate(request);

    return this.genericService.loadAllObjectsWithSingleCondition(SuspensionType.class,
            CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),"name");
  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LocalDate wToday = LocalDate.now();

    LocalDate fDate = LocalDate.of(wToday.getYear(), 1, 1);

    PaginationBean paginationBean = getPaginationInfo(request);

    List<SuspensionLog> empList = this.employeeService.getSuspendedEmployees((paginationBean.getPageNumber() - 1) * this.pageLength,
            this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), fDate, wToday, 0L, false, bc);

    int wGLNoOfElements = this.employeeService.getTotalNumberOfSuspendedEmployees(bc, fDate, wToday, 0L, false);

    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wPELB.setShowRow(SHOW_ROW);

    wPELB.setFromDate(fDate);
    wPELB.setToDate(wToday);
    wPELB.setFromDateStr(PayrollBeanUtils.getDateAsString(fDate));
    wPELB.setToDateStr(PayrollBeanUtils.getDateAsString(wToday));
    wPELB.setId(0L);
    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wPELB);
    Navigator.getInstance(bc.getSessionId()).setFromForm("redirect:viewSuspendedEmployees.do");
    Navigator.getInstance(bc.getSessionId()).setFromClass(this.getClass());
     return VIEW;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td", "uid"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, @RequestParam("uid") Long pUid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
    LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

    PaginationBean paginationBean = getPaginationInfo(request);

    boolean useUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUid);

    List<SuspensionLog> empList = this.employeeService.getSuspendedEmployees((paginationBean.getPageNumber() - 1) * this.pageLength,
            this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), fDate, tDate, pUid, useUserId, bc);

    int wGLNoOfElements = this.employeeService.getTotalNumberOfSuspendedEmployees(bc, fDate, tDate, pUid, useUserId);

    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    wPELB.setShowRow(SHOW_ROW);

    wPELB.setFromDate(fDate);
    wPELB.setToDate(tDate);
    wPELB.setFromDateStr(PayrollBeanUtils.getDateAsString(fDate));
    wPELB.setToDateStr(PayrollBeanUtils.getDateAsString(tDate));
    wPELB.setId(pUid);
    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wPELB);
    if(useUserId)
      Navigator.getInstance(bc.getSessionId()).setFromForm("redirect:viewSuspendedEmployees.do?fd="+pFd+"&td="+pTd+"&uid="+pUid);
    else
      Navigator.getInstance(bc.getSessionId()).setFromForm("redirect:viewSuspendedEmployees.do?fd="+pFd+"&td="+pTd+"&uid="+0L);
    Navigator.getInstance(bc.getSessionId()).setFromClass(this.getClass());
    return VIEW;
  }
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") PaginatedBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    
    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      Navigator.getInstance(bc.getSessionId()).setFromClass(IppmsMultiFormController.class);
      Navigator.getInstance(bc.getSessionId()).setFromForm("determineDashBoard.do");
      return REDIRECT_TO_DASHBOARD;
    }
   

    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
      if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null))
      {
        result.rejectValue("", "InvalidValue", "Please select valid Dates");
        addDisplayErrorsToModel(model, request);

        model.addAttribute("roleBean",bc);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);

        return VIEW;
      }
      LocalDate date1 = pLPB.getFromDate();
      LocalDate date2 = pLPB.getToDate();

      if (date1.isAfter(date2)) {
        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
        addDisplayErrorsToModel(model, request);

        model.addAttribute("roleBean",bc);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);

        return VIEW;
      }

      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
      return "redirect:viewSuspendedEmployees.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId();
    }

    return "redirect:viewSuspendedEmployees.do";
  }
}