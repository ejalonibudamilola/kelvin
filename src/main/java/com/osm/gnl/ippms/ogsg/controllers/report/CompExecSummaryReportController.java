package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/wageCompExecSummary.do"})
@SessionAttributes(types={WageBeanContainer.class})
public class CompExecSummaryReportController extends BaseController{

  @Autowired
  PaycheckService paycheckService;

  private HashMap<Long, MdaInfo> mapAgencyMap;
  private HashMap<Long, SalaryInfo> salaryInfoMap;


  private void init()  {
    this.mapAgencyMap = new HashMap<>();
    

    this.salaryInfoMap = new HashMap<>();
  }

  private void setControlMaps(HttpServletRequest request) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

    BusinessCertificate bc = super.getBusinessCertificate(request);
    
    
//	List<MdaDeptMap> wAgencyList = (List<MdaDeptMap>) this.payrollService.loadAllByHqlStr("from MdaDeptMap");
    List<MdaDeptMap> wAgencyList = this.genericService.loadAllObjectsWithSingleCondition(MdaDeptMap.class,
            CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),null);

     
    makeAgencyHashMapFromList(wAgencyList);
    
//    this.salaryInfoMap = this.payrollServiceExt.loadSalaryInfoAsMap();
    this.salaryInfoMap = this.genericService.loadObjectAsMapWithConditions(SalaryInfo.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId())), "id");
  }

  private void makeAgencyHashMapFromList(List<MdaDeptMap> pList){
    for (MdaDeptMap a : pList)
      this.mapAgencyMap.put(a.getId(), a.getMdaInfo());
  }

  

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);
    
    WageBeanContainer wBEOB = new WageBeanContainer();

    PayrollFlag pf = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class,getBusinessClientIdPredicate(request));

    LocalDate startDate;
    LocalDate endDate;

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

    int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);


    if (noOfEmpWivNegPay > 0)
    {
      return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + fDate.getMonthValue() + "&ry=" + fDate.getYear();
    }

    wBEOB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(startDate));
    wBEOB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(endDate));
    wBEOB.setFromDate(fDate);
    wBEOB.setToDate(tDate);
    init();
    setControlMaps(request);

    List<EmployeePayBean> wEPBList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), fDate.getMonthValue(), fDate.getYear(), bc,null,false);


    List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();

    wBEOB.setMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));

    WageSummaryBean wMinistryBean = new WageSummaryBean();
    wMinistryBean.setAssignedToObject("Ministries");
    WageSummaryBean wAgencyBean = new WageSummaryBean();
    wAgencyBean.setAssignedToObject("Agencies");
    WageSummaryBean wParastatals = new WageSummaryBean();
    wParastatals.setAssignedToObject("Parastatals");
    WageSummaryBean wBoardBean = new WageSummaryBean();
    wBoardBean.setAssignedToObject("Boards");
    wBEOB.setTotalNoOfEmp(wEPBList.size());
    HashMap<Long,Long> wAgencyMap = new HashMap<Long,Long>();
    

    for (EmployeePayBean e : wEPBList)
    {
     
        MdaInfo wAgency =this.mapAgencyMap.get(e.getMdaDeptMap().getId());
        
        switch(wAgency.getMdaType().getMdaTypeCode()) {
        case 1: //Agency
        	wAgencyBean = this.processWageBean(wAgencyBean,wAgency.getId(),wAgencyMap, e);
        	break;
        case 2://Board
        	wBoardBean = this.processWageBean(wBoardBean,wAgency.getId(),wAgencyMap, e);
        	break;
        	
        case 3: // Ministry
        	wMinistryBean = this.processWageBean(wMinistryBean,wAgency.getId(),wAgencyMap, e);
        	break;
        case 4:
        	wParastatals = this.processWageBean(wParastatals,wAgency.getId(),wAgencyMap, e);
        break;
        }
      
    

    }

    wRetList.add(wAgencyBean);
    wRetList.add(wBoardBean);
    wRetList.add(wParastatals);
    wRetList.add(wMinistryBean);
    Collections.sort(wRetList);

    wBEOB = makeTotals(wRetList, wBEOB);

    model.addAttribute("miniBean", wBEOB);
    addRoleBeanToModel(model, request);

    return "report/compExecSummaryForm";
  }

  @RequestMapping(method={RequestMethod.GET}, params={"fd", "td"})
  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
	  SessionManagerService.manageSession(request, model);
	     
	  BusinessCertificate bc = this.getBusinessCertificate(request);
    
    init();

    LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
    LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

    int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

    if (noOfEmpWivNegPay > 0)
    {
//      return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + fDate.get(2) + "&ry=" + fDate.get(1);
      return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + fDate.getMonthValue() + "&ry=" + fDate.getYear();
    }

//    setControlMaps();
    setControlMaps(request);

    List<EmployeePayBean> wEPBList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), fDate.getMonthValue(), fDate.getYear(), bc,null,false);

    List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();

    WageBeanContainer wBEOB = new WageBeanContainer();

    wBEOB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(fDate));
    wBEOB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(tDate));

    wBEOB.setFromDate(fDate);
    wBEOB.setToDate(tDate);

    wBEOB.setMonthAndYearStr(PayrollHRUtils.getMonthYearDateFormat().format(fDate));

    WageSummaryBean wMinistryBean = new WageSummaryBean();
    wMinistryBean.setAssignedToObject("Ministries");
    WageSummaryBean wAgencyBean = new WageSummaryBean();
    wAgencyBean.setAssignedToObject("Agencies");
    WageSummaryBean wParastatals = new WageSummaryBean();
    wParastatals.setAssignedToObject("Parastatals");
    WageSummaryBean wBoardBean = new WageSummaryBean();
    wBoardBean.setAssignedToObject("Boards");
    wBEOB.setTotalNoOfEmp(wEPBList.size());
    HashMap<Long,Long> wAgencyMap = new HashMap<Long,Long>();
   

    for (EmployeePayBean e : wEPBList)
    {
     
        MdaInfo wAgency =this.mapAgencyMap.get(e.getMdaDeptMap().getId());
        
        switch(wAgency.getMdaType().getMdaTypeCode()) {
        case 1: //Agency
        	wAgencyBean = this.processWageBean(wAgencyBean,wAgency.getId(),wAgencyMap, e);
        	break;
        case 2://Board
        	wBoardBean = this.processWageBean(wBoardBean,wAgency.getId(),wAgencyMap, e);
        	break;
        	
        case 3: // Ministry
        	wMinistryBean = this.processWageBean(wMinistryBean,wAgency.getId(),wAgencyMap, e);
        	break;
        case 4:
        	wParastatals = this.processWageBean(wParastatals,wAgency.getId(),wAgencyMap, e);
        break;
        }
      
    
    }
   

    wRetList.add(wAgencyBean);
    wRetList.add(wBoardBean);
    wRetList.add(wParastatals);
    wRetList.add(wMinistryBean);

    Collections.sort(wRetList);
    wBEOB = makeTotals(wRetList, wBEOB);
    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wBEOB);

    return "report/compExecSummaryForm";
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") WageBeanContainer pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
    
    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:reportsOverview.do";
    }
     
    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
      if ((pLPB.getFromDate() == null) || (pLPB.getToDate() == null))
      {
        result.rejectValue("", "InvalidValue", "Please select valid Dates");

        addDisplayErrorsToModel(model, request);
        addRoleBeanToModel(model, request);

        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);

        return "report/compExecSummaryForm";
      }


      LocalDate date1 = (pLPB.getFromDate());
      LocalDate date2 = (pLPB.getToDate());

      if (date1.isAfter(date2)) {
        result.rejectValue("", "InvalidValue", "'From' Date can not be greater than 'To' Date ");

        addDisplayErrorsToModel(model, request);
        addRoleBeanToModel(model, request);

        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);

        return "report/compExecSummaryForm";
      }
//      if ((date1.isAfter(Calendar.getInstance())) || (date1.equals(Calendar.getInstance()))) {
      if ((date1.isAfter(LocalDate.now())) || (date1.equals(LocalDate.now()))) {
        result.rejectValue("", "InvalidValue", "'From' Date must be in the Past");

        addDisplayErrorsToModel(model, request);
        addRoleBeanToModel(model, request);

        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);

        return "report/compExecSummaryForm";
      }
//      if (date1.get(1) != date2.get(1)) {
      if (date1.getYear()!= date2.getYear()) {
        result.rejectValue("", "InvalidValue", "'From' and 'To' Dates must be in the same year.");

        addDisplayErrorsToModel(model, request);
        addRoleBeanToModel(model, request);

        model.addAttribute("status", result);
        model.addAttribute("miniBean", pLPB);

        return "report/compExecSummaryForm";
      }

      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
      return "redirect:wageCompExecSummary.do?fd=" + sDate + "&td=" + eDate;
    }

    return "redirect:wageCompExecSummary.do";
  }

  private WageBeanContainer makeTotals(List<WageSummaryBean> pRetList, WageBeanContainer pBEOB)
  {
    for (WageSummaryBean w : pRetList) {
      w.setDisplayStyle("reportOdd");
      pBEOB.setTotalNoOfItems(pBEOB.getTotalNoOfItems() + w.getNoOfItems());
      pBEOB.setTotalNoOfStaffs(pBEOB.getTotalNoOfStaffs() + w.getNoOfEmp());
      pBEOB.setTotalBasicSalary(pBEOB.getTotalBasicSalary() + w.getBasicSalary());
      pBEOB.setTotalAllowances(pBEOB.getTotalAllowances() + w.getTotalAllowance());
      pBEOB.setTotalGrossAmount(pBEOB.getTotalGrossAmount() + w.getGrossAmount());
      pBEOB.setTotalPaye(pBEOB.getTotalPaye() + w.getPaye());
      pBEOB.setTotalOtherDeductions(pBEOB.getTotalOtherDeductions() + w.getOtherDeductions());
      pBEOB.setTotalDeductions(pBEOB.getTotalDeductions() + w.getTotalDeductions());
      pBEOB.setTotalNetPay(pBEOB.getTotalNetPay() + w.getNetPay());
    }
    pBEOB.setWageSummaryBeanList(pRetList);
    return pBEOB;
  }
  
  private WageSummaryBean processWageBean(WageSummaryBean pSummaryBean, Long wAgencyId, HashMap<Long,Long> wAgencyMap, EmployeePayBean e ) {
	      
	  if (!wAgencyMap.containsKey(wAgencyId))
      {
        
		  pSummaryBean.setNoOfItems(pSummaryBean.getNoOfItems() + 1);  
          wAgencyMap.put(wAgencyId, wAgencyId);
      }
	  pSummaryBean.setNoOfEmp(pSummaryBean.getNoOfEmp() + 1);
      SalaryInfo s = this.salaryInfoMap.get(e.getSalaryInfo().getId());
      pSummaryBean.setBasicSalary(pSummaryBean.getBasicSalary() + EntityUtils.convertDoubleToEpmStandard(s.getMonthlyBasicSalary() / 12.0D));

      double otherAllowance = e.getPrincipalAllowance() + e.getOtherArrears() + e.getArrears() + e.getSpecialAllowance();

      double wSalaryDiffAsDeduction = 0.0D;
      if (e.getSalaryDifference() > 0.0D)
        otherAllowance += e.getSalaryDifference();
      else {
        wSalaryDiffAsDeduction = e.getSalaryDifference() * -1.0D;
      }
      pSummaryBean.setTotalAllowance(pSummaryBean.getTotalAllowance() + otherAllowance + e.getLeaveTransportGrant() + EntityUtils.convertDoubleToEpmStandard(s.getConsolidatedAllowance() / 12.0D));
      pSummaryBean.setGrossAmount(pSummaryBean.getGrossAmount() + (s.getMonthlyBasicSalary() / 12.0D + otherAllowance + e.getLeaveTransportGrant() + EntityUtils.convertDoubleToEpmStandard(s.getConsolidatedAllowance() / 12.0D)));
      pSummaryBean.setPaye(pSummaryBean.getPaye() + e.getTaxesPaid());
      double otherDeductions = e.getNhf() + e.getUnionDues() + e.getTws() + e.getTotalDeductions() + e.getTotalGarnishments() + wSalaryDiffAsDeduction + e.getDevelopmentLevy();
      pSummaryBean.setOtherDeductions(pSummaryBean.getOtherDeductions() + otherDeductions);
      pSummaryBean.setTotalDeductions(pSummaryBean.getTotalDeductions() + (e.getTaxesPaid() + otherDeductions));
      pSummaryBean.setNetPay(pSummaryBean.getNetPay() + e.getNetPay());
	  
	  
	  return pSummaryBean;
  }
}