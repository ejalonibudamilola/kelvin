package com.osm.gnl.ippms.ogsg.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.report.service.PaycheckDeductionService;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.tax.TaxDeductions;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;


@Controller
@RequestMapping({"/taxDeductionHistory.do"})
@SessionAttributes(types={PaginatedPaycheckGarnDedBeanHolder.class})
public class TaxDeductionHistoryFormController extends BaseController
{
  
  public TaxDeductionHistoryFormController()
  {}

  @Autowired
    PaycheckDeductionService paycheckDeductionService;

  private static final String VIEW_NAME = "deduction/taxDeductionHistory";

  @RequestMapping(method={RequestMethod.GET}, params={"eid"})
  public String setupForm(@RequestParam("eid") String pEmp, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

    PaginationBean paginationBean = getPaginationInfo(request);

      BusinessCertificate bc = getBusinessCertificate(request);
    
    String[] wStr = StringUtils.split(pEmp, ".",2);
    int pRunMonth = 0;
    int pRunYear = 0;
    int pToMonth = 0;
    int pToYear = 0;
    Long pEmpId = 0L;
    
    if(wStr[0].trim().length() == 6){
    	String _pRunMonth = wStr[0].substring(0, 2);
    	pRunMonth = Integer.parseInt(_pRunMonth);
    	_pRunMonth = wStr[0].substring(2);
    	pRunYear = Integer.parseInt(_pRunMonth);
    	
    }else if(wStr[0].trim().length() == 5){
    	String _pRunMonth = wStr[0].substring(0, 1);
    	pRunMonth = Integer.parseInt(_pRunMonth);
    	_pRunMonth = wStr[0].substring(1);
    	pRunYear = Integer.parseInt(_pRunMonth);
    }
    String toDate = StringUtils.substringBefore(wStr[1],".");
    if(toDate.length() == 6){
    	String _pRunMonth = toDate.substring(0, 2);
    	pToMonth = Integer.parseInt(_pRunMonth);
    	_pRunMonth = toDate.substring(2);
    	pToYear = Integer.parseInt(_pRunMonth);
    	
    }else if(toDate.length() == 5){
    	String _pRunMonth = toDate.substring(0, 1);
    	pToMonth = Integer.parseInt(_pRunMonth);
    	_pRunMonth = toDate.substring(1);
    	pToYear = Integer.parseInt(_pRunMonth);
    }
    pEmpId = Long.parseLong(StringUtils.substringAfter(wStr[1],"."));
    //int wRunYear = PayrollBeanUtils.getYearIntFromJavaDate(PayrollBeanUtils.setDateFromString(pPayPeriod).getTime());
    
   // Calendar sDate = PayrollBeanUtils.setDateFromString(pPayPeriod);
     
    double taxPaid = 0.0D;
     

    int wNoOfElements = this.paycheckDeductionService.getTotalNoOfEmpTaxDeductionByDates(pRunMonth, pRunYear, pToMonth, pToYear, 0L, pEmpId, bc);

    List<TaxDeductions> taxDeductions = null;
    if (wNoOfElements > 0) {
    	taxDeductions = this.paycheckDeductionService.loadEmployeeTaxDeductionByRunMonthAndYear((paginationBean.getPageNumber() - 1) * 24, 24, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), pRunMonth, pRunYear, pToMonth, pToYear,  0L, pEmpId, bc);

      for (TaxDeductions p : taxDeductions) {
        taxPaid += p.getAmount();
      }
      Collections.sort(taxDeductions);
    } else {
    	taxDeductions = new ArrayList<TaxDeductions>();
    }
      AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService, pEmpId, bc);
    PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(taxDeductions, paginationBean.getPageNumber(), 24, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
    wPGBDH.setDisplayTitle(wEmp.getEmployeeId());
    wPGBDH.setEmployeeId(wEmp.getEmployeeId());
    wPGBDH.setId(wEmp.getId());
    wPGBDH.setFromDateStr(PayrollBeanUtils.createPayPeriodFromInt(pRunMonth,pRunYear,true));
    wPGBDH.setToDateStr(PayrollBeanUtils.createPayPeriodFromInt(pToMonth,pToYear,false));
    wPGBDH.setName(wEmp.getTitle().getName() + " " + PayrollHRUtils.createDisplayName(wEmp.getLastName(), wEmp.getFirstName(), wEmp.getInitials()));
    wPGBDH.setMode(wEmp.getParentObjectName());
    wPGBDH.setDeductionName(" Tax Deduction ");
 //   wPGBDH.setFromDate(sDate.getTime());

    /*SpecialAllowanceInfo wSAI = (SpecialAllowanceInfo)this.payrollServiceExt.getObjectByClassAndId(SpecialAllowanceInfo.class, Integer.valueOf(pGarnId));
    wPGBDH.setObjectId(wSAI.getId().intValue());
    wPGBDH.setObjectInd(2);
    wPGBDH.setGarnishmentName(wSAI.getSpecialAllowanceType().getName());*/

    wPGBDH.setTaxPaid(taxPaid);
    model.addAttribute("taxDedHist", wPGBDH);
    addRoleBeanToModel(model, request);

    return VIEW_NAME;
  }
  
  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
		  @RequestParam(value="_go", required=false) String go, 
		  @ModelAttribute("taxDedHist") PaginatedPaycheckGarnDedBeanHolder pDDB, 
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
     

     
    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      
      return "redirect:taxReport.do?sDate="+pDDB.getFromDateStr()+"&eDate="+pDDB.getToDateStr();
    }

    return REDIRECT_TO_DASHBOARD;
  }
}