package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HrServiceHelper;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/selectYOR.do"})
@SessionAttributes(types={BusinessEmpOVBeanInactive.class})
public class StaffByRetirementYearController extends BaseController
{
 

  private final HrServiceHelper hrServiceHelper;

  private final String VIEW_NAME = "employee/viewEmpByExpYrOfRetire";
  
  @Autowired
  public StaffByRetirementYearController(final HrServiceHelper hrServiceHelper)
  {
    this.hrServiceHelper = hrServiceHelper;
  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
		
	  

    BusinessEmpOVBeanInactive wBEOB = new BusinessEmpOVBeanInactive(new ArrayList<>(), 1, this.pageLength, 0, null, "asc");

    wBEOB.setShowRow(HIDE_ROW);
    model.addAttribute("miniBean", wBEOB);
    addRoleBeanToModel(model, request);
    
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);
		
	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
    LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);


    List<HiringInfo> empList =  this.hrServiceHelper.getEmpByExpRetireDate(bc,fDate,tDate,false);


    BusinessEmpOVBeanInactive pCList = new BusinessEmpOVBeanInactive(empList);

    pCList.setFromDate(fDate);
    pCList.setToDate(tDate);
    pCList.setShowRow(SHOW_ROW);

    model.addAttribute("miniBean", pCList);
    addRoleBeanToModel(model, request);
    return VIEW_NAME;
  }
  @RequestMapping(method={RequestMethod.POST})
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
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);

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
        model.addAttribute("miniBean", pLPB);
        addRoleBeanToModel(model, request);

        return VIEW_NAME;
      }

      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
      return "redirect:selectYOR.do?fd=" + sDate + "&td=" + eDate;
    }

    return "redirect:selectYOR.do";
  }
}