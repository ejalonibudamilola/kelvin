package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.ExcelReportService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/selectYOBForm.do"})
@SessionAttributes(types={BusinessEmpOVBeanInactive.class})
public class ViewEmployeeByYearOfBirthFormController extends BaseController
{
  
   
  private final int pageLength = 40;

  private static final String VIEW_NAME = "employee/viewEmpByYearOfBirthForm";

  @Autowired
  private ExcelReportService excelReportService;
  
  public ViewEmployeeByYearOfBirthFormController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);


    BusinessEmpOVBeanInactive wBEOB = new BusinessEmpOVBeanInactive(new ArrayList<>(), 1, this.pageLength, 0, null, "asc");

    wBEOB.setShowRow(HIDE_ROW);
    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wBEOB);

    return VIEW_NAME;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"fd", "td"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
    LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

    //LocalDate wToday = LocalDate.now();
    fDate = LocalDate.of(fDate.getYear(), 1, 1);
    tDate = LocalDate.of(tDate.getYear(), 12, 31);

    PaginationBean paginationBean = getPaginationInfo(request);

    List<HiringInfo> empList = excelReportService.loadYOBDataForExport(fDate,tDate,bc);

    int wGLNoOfElements = empList.size();//this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, HiringInfo.class);

    BusinessEmpOVBeanInactive pCList = new BusinessEmpOVBeanInactive(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

    pCList.setShowRow(SHOW_ROW);

    pCList.setFromDate(fDate);
    pCList.setToDate(tDate);

    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", pCList);

    return VIEW_NAME;
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") BusinessEmpOVBeanInactive pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

     

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:hrRelatedReports.do";
    }
     
    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
      if ((pLPB.getFromDate() == null) || (pLPB.getToDate() == null))
      {
        result.rejectValue("", "InvalidValue", "Please select valid Dates");
        ((BusinessEmpOVBeanInactive)result.getTarget()).setDisplayErrors("block");

        model.addAttribute("status", result);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", pLPB);

        return VIEW_NAME;
      }
      LocalDate date1;
      LocalDate date2;
      date1 = pLPB.getFromDate();
      date2 = pLPB.getToDate();

      if (date1.isAfter(date2)) {
        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
        ((BusinessEmpOVBeanInactive)result.getTarget()).setDisplayErrors("block");

        model.addAttribute("status", result);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", pLPB);

        return VIEW_NAME;
      }if ((date1.isAfter(LocalDate.now())) || (date1.equals(LocalDate.now()))) {
        result.rejectValue("", "InvalidValue", "'Between' Date must be in the Past");
        ((BusinessEmpOVBeanInactive)result.getTarget()).setDisplayErrors("block");

        model.addAttribute("status", result);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", pLPB);

        return VIEW_NAME;
      }

      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
      return "redirect:selectYOBForm.do?fd=" + sDate + "&td=" + eDate;
    }

    return "redirect:selectYOBForm.do";
  }
}