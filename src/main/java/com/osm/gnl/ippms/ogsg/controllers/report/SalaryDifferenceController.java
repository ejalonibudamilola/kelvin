package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.SalaryService;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.report.PaginatedSalaryDifferenceBean;
import com.osm.gnl.ippms.ogsg.domain.report.SalaryDifferenceBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping({"/employeeSalaryDiff.do"})
@SessionAttributes(types={PaginatedSalaryDifferenceBean.class})
public class SalaryDifferenceController extends BaseController
{

  @Autowired
  private SalaryService salaryService;
  
 
  private final int pageLength = 20;

  private static final String VIEW_NAME = "employee/viewEmpSalaryDiffForm";
  
  public SalaryDifferenceController()
  {}

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    PaginationBean paginationBean = getPaginationInfo(request);

    PayrollFlag pf = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class,getBusinessClientIdPredicate(request));

    LocalDate startDate = null;
    LocalDate endDate = null;

    if (!pf.isNewEntity()) {
      startDate = pf.getPayPeriodStart();
      endDate = pf.getPayPeriodEnd();
    } else {
      ArrayList<LocalDate> list = PayrollBeanUtils.getDefaultCheckRegisterDates();
      startDate = list.get(0);
      endDate = list.get(1);
    }
    LocalDate fDate = PayrollBeanUtils.setDateFromString(PayrollBeanUtils.getJavaDateAsString(startDate));
    LocalDate tDate = PayrollBeanUtils.setDateFromString(PayrollBeanUtils.getJavaDateAsString(endDate));

    LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(fDate, false);
    LocalDate wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(tDate, true);


    List<SalaryDifferenceBean> empList = this.salaryService.getEmployeesWithSalaryDiffByDates((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), fDate, tDate, wPrevMonthStart, wPrevMonthEnd, bc);
    

    int wGLNoOfElements = this.salaryService.getTotalNoOfWithSalaryDiffByDate(fDate, tDate, wPrevMonthStart, wPrevMonthEnd, bc);

    PaginatedSalaryDifferenceBean pCList = new PaginatedSalaryDifferenceBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    pCList.setShowRow(SHOW_ROW);

    pCList.setFromDate(fDate);
    pCList.setToDate(tDate);

    pCList.setFromDateStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));
    pCList.setToDateStr(PayrollHRUtils.getMonthYearDateFormat().format(wPrevMonthEnd));

    pCList.setFromDatePrintStr(PayrollHRUtils.getFullDateFormat().format(fDate));
    pCList.setToDatePrintStr(PayrollHRUtils.getFullDateFormat().format(tDate));

    pCList.setStartAmount(0.0D);
    pCList.setEndAmount(0.0D);
    model.addAttribute("miniBean", pCList);
    addDisplayErrorsToModel(model, request);
    addRoleBeanToModel(model, request);

    return VIEW_NAME;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"fd", "td", "sa", "ea"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, @RequestParam("sa") String pStartAmountStr, @RequestParam("ea") String pEndAmountStr, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    PaginationBean paginationBean = getPaginationInfo(request);

    LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
    LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);
    double startAmount = 0.0D;
    double endAmount = 0.0D;

    if ((pStartAmountStr != null) && (StringUtils.trim(pStartAmountStr).length() > 0)) {
      try {
        startAmount = Double.parseDouble(PayrollHRUtils.removeCommas(pStartAmountStr));
      } catch (Exception wEx) {
        startAmount = 0.0D;
      }
    }

    if ((pEndAmountStr != null) && (StringUtils.trim(pEndAmountStr).length() > 0)) {
      try {
        endAmount = Double.parseDouble(PayrollHRUtils.removeCommas(pEndAmountStr));
      } catch (Exception wEx) {
        endAmount = 0.0D;
      }

    }

    LocalDate wPrevMonthStart = PayrollBeanUtils.getPreviousMonthDate(fDate, false);
    LocalDate wPrevMonthEnd = PayrollBeanUtils.getPreviousMonthDate(tDate, true);

    List<SalaryDifferenceBean> empList = null;
    int wGLNoOfElements = 0;
    if ((startAmount == 0.0D) && (endAmount == 0.0D)) {
      empList = this.salaryService.getEmployeesWithSalaryDiffByDates((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), fDate, tDate, wPrevMonthStart, wPrevMonthEnd, bc);

      wGLNoOfElements = this.salaryService.getTotalNoOfWithSalaryDiffByDate(fDate, tDate, wPrevMonthStart, wPrevMonthEnd, bc);
    }
    else {
      empList = this.salaryService.getEmployeesWithSalaryDiffByDates((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), fDate, tDate, wPrevMonthStart, wPrevMonthEnd, startAmount, endAmount, bc);

      wGLNoOfElements = this.salaryService.getTotalNoOfWithSalaryDiffByDate(fDate, tDate, wPrevMonthStart, wPrevMonthEnd, startAmount, endAmount, bc);
    }

    PaginatedSalaryDifferenceBean pCList = new PaginatedSalaryDifferenceBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    pCList.setShowRow(SHOW_ROW);

    pCList.setFromDate(fDate);
    pCList.setToDate(tDate);

    pCList.setFromDateStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));
    pCList.setToDateStr(PayrollHRUtils.getMonthYearDateFormat().format(wPrevMonthEnd));

    pCList.setFromDatePrintStr(PayrollHRUtils.getFullDateFormat().format(fDate));
    pCList.setToDatePrintStr(PayrollHRUtils.getFullDateFormat().format(tDate));

    pCList.setStartAmount(startAmount);
    pCList.setEndAmount(endAmount);

    pCList.setStartAmountStr(pStartAmountStr);
    pCList.setEndAmountStr(pEndAmountStr);
    addDisplayErrorsToModel(model, request);
    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", pCList);

    return VIEW_NAME;
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") PaginatedSalaryDifferenceBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:reportsOverview.do";
    }
     

    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
    	boolean wDateSet = false;
      if ((pLPB.getFromDate() == null) || (pLPB.getToDate() == null))
      {
        result.rejectValue("", "InvalidValue", "Please select valid Dates");
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;
      }else {
    	  wDateSet = true;
      }
      LocalDate date1;
      LocalDate date2;
      date1 = pLPB.getFromDate();
      date2 = pLPB.getToDate();
      double startAmount = 0.0D;
      double endAmount = 0.0D;

      if (date1.isAfter(date2)) {
        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;
      }if (date1.isAfter(LocalDate.now()) || (date1.equals(LocalDate.now()))) {
        result.rejectValue("", "InvalidValue", "'Between' Date must be in the Past");
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;
      }if (date1.getMonthValue() != date2.getMonthValue()) {
        result.rejectValue("", "InvalidValue", "Pay Period Dates must be within the same month");
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;
      }if (date1.getYear() != date2.getYear()) {
        result.rejectValue("", "InvalidValue", "Pay Period Dates must be within the same year");
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;
      }
      try
      {
        startAmount = Double.parseDouble(PayrollHRUtils.removeCommas(pLPB.getStartAmountStr()));
      } catch (Exception wEx) {
    	  if(!wDateSet) {
	        result.rejectValue("", "InvalidValue", "From Amount is not a valid number.");
	        addDisplayErrorsToModel(model, request);
	        model.addAttribute("status", result);
	        model.addAttribute("miniBean", pLPB);
            addRoleBeanToModel(model, request);
            return VIEW_NAME;
    	  }


      }
      try
      {
        endAmount = Double.parseDouble(PayrollHRUtils.removeCommas(pLPB.getEndAmountStr()));
      } catch (Exception wEx) {
    	  if(!wDateSet) {
	        result.rejectValue("", "InvalidValue", "To Amount is not a valid number.");
	        addDisplayErrorsToModel(model, request);
	        model.addAttribute("status", result);
	        model.addAttribute("miniBean", pLPB);
            addRoleBeanToModel(model, request);

	        return VIEW_NAME;
    	  }
      }

      if (startAmount > endAmount) {
    	  
        result.rejectValue("", "InvalidValue", "Amount Range is invalid. 'From' amount can not be greater than the 'To' amount");
        addDisplayErrorsToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;
      }

      LocalDate wStartDate;
      LocalDate wEndDate;

      wStartDate = date1;
      wEndDate = date2;

      LocalDate.of(date1.getYear(), date1.getMonthValue(), 1);
      LocalDate.of(date2.getYear(), date2.getMonthValue(), date2.lengthOfMonth());

      String sDate = PayrollBeanUtils.getJavaDateAsString(date1);
      String eDate = PayrollBeanUtils.getJavaDateAsString(date2);
      
      if(IppmsUtils.isNullOrEmpty(pLPB.getStartAmountStr()) || IppmsUtils.isNullOrEmpty(pLPB.getEndAmountStr())) {
    	  return "redirect:employeeSalaryDiff.do?fd=" + sDate + "&td=" + eDate + "&sa=0" + pLPB.getStartAmountStr() + "&ea=0";
      }

      return "redirect:employeeSalaryDiff.do?fd=" + sDate + "&td=" + eDate + "&sa=" + pLPB.getStartAmountStr() + "&ea=" + pLPB.getEndAmountStr();
    }

    return "redirect:employeeSalaryDiff.do";
  }
}