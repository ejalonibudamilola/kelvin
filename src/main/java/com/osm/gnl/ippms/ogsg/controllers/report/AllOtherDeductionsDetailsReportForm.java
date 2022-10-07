package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.PaycheckDeductionService;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping({"/allOtherDeductionsReport.do"})
@SessionAttributes(types={DeductionDetailsBean.class})
public class AllOtherDeductionsDetailsReportForm extends BaseController
{

  private final PaycheckService paycheckService;
  private final PaycheckDeductionService paycheckDeductionService;

  @Autowired
  public AllOtherDeductionsDetailsReportForm(PaycheckService paycheckService, PaycheckDeductionService paycheckDeductionService) {
    this.paycheckService = paycheckService;
    this.paycheckDeductionService = paycheckDeductionService;
  }

  @ModelAttribute("monthList")
  public List<NamedEntity> getMonthList() {
    return PayrollBeanUtils.makeAllMonthList();
  }

  private static final String VIEW_NAME = "deduction/allOtherDeductionsReport";

  @ModelAttribute("yearList")
  public List<NamedEntity> makeYearList(HttpServletRequest request) {

    return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
  }


  
@RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

    DeductionDetailsBean deductDetails = new DeductionDetailsBean();

  int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

  if (noOfEmpWivNegPay > 0)
  {
    LocalDate localDate = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
     return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + localDate.getMonthValue() + "&ry=" + localDate.getYear();
  }
       
     Long pfId = this.genericService.loadMaxValueByClassClientIdAndColumn(PayrollRun.class, "id",bc.getBusinessClientInstId(),"businessClientId");

    PayrollRun pf = this.genericService.loadObjectById(PayrollRun.class,pfId);

    LocalDate wSDate = LocalDate.now();
    HashMap <Long, DeductGarnMiniBean> deductionBean = new HashMap<Long,DeductGarnMiniBean> ();
    if (!pf.isNewEntity()) {
      deductDetails.setFromDate(pf.getPayPeriodStart());
      deductDetails.setToDate(pf.getPayPeriodEnd());
      deductDetails.setCurrentDeduction("All Deductions");
      deductDetails.setId(bc.getBusinessClientInstId());
      deductDetails.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodStart()));
      deductDetails.setToDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodEnd()));

      wSDate = pf.getPayPeriodStart();


      List<AbstractPaycheckDeductionEntity> paycheckDeductions = this.paycheckDeductionService.loadEmpDeductionsByParentIdAndPayPeriod(null, wSDate.getMonthValue(),wSDate.getYear(), bc);
      
      DeductGarnMiniBean d;
      for (AbstractPaycheckDeductionEntity p : paycheckDeductions) {
        if (deductionBean.containsKey(p.getEmpDedInfo().getEmpDeductionType().getId())) {
            d = deductionBean.get(p.getEmpDedInfo().getEmpDeductionType().getId());
        } else {
            d = new DeductGarnMiniBean();
          d.setDeductionId(p.getEmpDedInfo().getEmpDeductionType().getId());
         
        }
        if(p.getEmpDeductionCategory().isApportionedDeduction()){
          d.setApportionedType(true);
        }
        
        d.setAmount(d.getAmount() + p.getAmount());
        d.setDescription(p.getEmpDedInfo().getDescription());
        d.setName(p.getEmpDedInfo().getEmpDeductionType().getDescription() + " [ " + p.getEmpDedInfo().getEmpDeductionType().getName() + " ]");
        deductionBean.put(p.getEmpDedInfo().getEmpDeductionType().getId(), d);
        deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
      }
      deductDetails.setRunMonth(wSDate.getMonthValue());
      deductDetails.setRunYear(wSDate.getYear());
    }else{
    	deductDetails.setRunMonth(-1);
        deductDetails.setRunYear(0);
    }
    deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wSDate.getMonthValue(), wSDate.getYear()));
    deductDetails.setTotalCurrentDeduction(this.paycheckDeductionService.getTotalDeductions(null, wSDate.getMonthValue(), wSDate.getYear(), bc));
    deductDetails.setNoOfEmployees(this.paycheckDeductionService.getNoOfEmployeeWithDeductions(null,wSDate.getMonthValue(), wSDate.getYear(), bc));
    deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(deductDetails.getTotalCurrentDeduction()));

    List <EmpDeductionType>deductionListFiltered = this.genericService.loadAllObjectsWithSingleCondition(EmpDeductionType.class,getBusinessClientIdPredicate(request), "description");
    Collections.sort(deductionListFiltered, Comparator.comparing(EmpDeductionType::getName));
    deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
    deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
    model.addAttribute("deductionList", deductionListFiltered);
    model.addAttribute("deductionDetails", deductDetails);
    addRoleBeanToModel(model, request);
    return VIEW_NAME;
  }
  
@RequestMapping(method={RequestMethod.GET}, params={"did", "rm", "ry", "pid"})
  public String setupForm(@RequestParam("did") Long dedTypeId, @RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, 
		  @RequestParam("pid") Long pid, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	DeductionDetailsBean deductDetails = new DeductionDetailsBean();
    HashMap <Long,DeductGarnMiniBean>deductionBean = new HashMap<>();

    BusinessCertificate bc = getBusinessCertificate(request);

    List<AbstractPaycheckDeductionEntity> paycheckDeductions;
    int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

    if (noOfEmpWivNegPay > 0)
    {
      LocalDate localDate = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
      return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + localDate.getMonthValue() + "&ry=" + localDate.getYear();
    }
     
    deductDetails.setRunMonth(pRunMonth);
    deductDetails.setRunYear(pRunYear);
    boolean usingDedType = false;
    if (dedTypeId == 0)
      deductDetails.setCurrentDeduction("All Deductions");
    else {
      usingDedType = true;
    }

    deductDetails.setId( pid);
     

    paycheckDeductions = this.paycheckDeductionService.loadEmpDeductionsByParentIdAndPayPeriod(dedTypeId, pRunMonth,pRunYear, bc);
    DeductGarnMiniBean d;
    boolean dedTypeSet = false;
    for (AbstractPaycheckDeductionEntity p : paycheckDeductions) {
      
      if (deductionBean.containsKey(p.getEmpDedInfo().getEmpDeductionType().getId())) {
          d = deductionBean.get(p.getEmpDedInfo().getEmpDeductionType().getId());
      }
      else {
          d = new DeductGarnMiniBean();
       
        if ((usingDedType) && (!dedTypeSet)) {
          dedTypeSet = true;
          deductDetails.setCurrentDeduction(d.getType());
        }
        d.setDeductionId(p.getEmpDedInfo().getEmpDeductionType().getId());
        
      }
      d.setAmount(d.getAmount() + p.getAmount());
      d.setDescription(p.getEmpDedInfo().getDescription());
      d.setName(p.getEmpDedInfo().getEmpDeductionType().getDescription() + " [ " + p.getEmpDedInfo().getEmpDeductionType().getName() + " ]");
      deductionBean.put(p.getEmpDedInfo().getEmpDeductionType().getId(), d);
      deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
    }
    deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
    deductDetails.setTotalCurrentDeduction(this.paycheckDeductionService.getTotalDeductions(dedTypeId,pRunMonth, pRunYear, bc));
    deductDetails.setNoOfEmployees(this.paycheckDeductionService.getNoOfEmployeeWithDeductions(dedTypeId,pRunMonth, pRunYear, bc));
   
    deductDetails.setDeductionId(dedTypeId);
    List <EmpDeductionType>deductionListFiltered = this.genericService.loadAllObjectsWithSingleCondition(EmpDeductionType.class,getBusinessClientIdPredicate(request), "description");
    //Collections.sort(deductionListFiltered, Comparator.comparing(EmpDeductionType::getDescription));

    Collections.sort(deductionListFiltered);
    deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
    deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(deductDetails.getTotalCurrentDeduction()));
    deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
    model.addAttribute("deductionList", deductionListFiltered);
    model.addAttribute("deductionDetails", deductDetails);
    addRoleBeanToModel(model, request);
    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
		  @RequestParam(value="_cancel", required=false) String cancel,
		  @RequestParam(value="_go", required=false) String go, @ModelAttribute("deductionDetails") DeductionDetailsBean pDDB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	  
    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
	    {
	    	
	      return "redirect:reportsOverview.do";
	    }
	  
    
    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
    	int rm = pDDB.getRunMonth();
    	int ry = pDDB.getRunYear();
     
      if (pDDB.getDeductionId() > 0) {
        return "redirect:otherDeductionDetailsReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry + "&pid=" + pDDB.getId()+"&mid=0";
      }
      return "redirect:allOtherDeductionsReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry + "&pid=" + pDDB.getId();
    }

    return "redirect:" + pDDB.getSubLinkSelect() + ".do";
  }

  
}