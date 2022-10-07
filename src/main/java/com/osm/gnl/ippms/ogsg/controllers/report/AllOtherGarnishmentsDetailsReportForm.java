package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.GarnishmentService;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGarnishment;
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
import java.util.*;


@Controller
@RequestMapping({"/allOtherGarnishmentReport.do"})
@SessionAttributes(types={DeductionDetailsBean.class})
public class AllOtherGarnishmentsDetailsReportForm extends BaseController
{

  /**
   * Kasumu Taiwo
   * 12-2020
   */

  private final PaycheckService paycheckService;

  private final  GarnishmentService garnishmentService;

   
  
  @ModelAttribute("monthList")
  protected List<NamedEntity> getMonthsList(){
	  return PayrollBeanUtils.makeAllMonthList();
  }

  @ModelAttribute("yearList")
  protected Collection<NamedEntity> getYearList(HttpServletRequest request){
	  return this.paycheckService.makePaycheckYearList(super.getBusinessCertificate(request));
  }

  @Autowired
  public AllOtherGarnishmentsDetailsReportForm(PaycheckService paycheckService, GarnishmentService garnishmentService)
  {
    this.paycheckService = paycheckService;
    this.garnishmentService = garnishmentService;
  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

     

    DeductionDetailsBean deductDetails = new DeductionDetailsBean();
    HashMap <Long, DeductGarnMiniBean>deductionBean = new HashMap<>();

    List <AbstractPaycheckGarnishmentEntity>paycheckDeductions;

     List<PayrollRun> listPf = this.genericService.loadAllObjectsUsingRestrictions(PayrollRun.class, Arrays.asList(
            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "id");
    Comparator<PayrollRun> c = Comparator.comparing(PayrollRun::getId);
    Collections.sort(listPf,c.reversed());


    if(IppmsUtils.isNotNullAndGreaterThanZero(listPf.size())){
      PayrollRun pf = listPf.get(0);
      if (!pf.isNewEntity()) {
        int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

        if (noOfEmpWivNegPay > 0)
        {
          return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + pf.getPayPeriodEnd().getMonthValue() + "&ry=" + pf.getPayPeriodEnd().getYear();
        }
        deductDetails.setFromDate(pf.getPayPeriodStart());
        deductDetails.setToDate(pf.getPayPeriodEnd());
        deductDetails.setCurrentDeduction("All Deductions");
        deductDetails.setId(bc.getBusinessClientInstId());
        deductDetails.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodStart()));
        deductDetails.setToDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodEnd()));


        LocalDate wCal = pf.getPayPeriodEnd();
        deductDetails.setRunMonth(wCal.getMonthValue());
        deductDetails.setRunYear(wCal.getYear());



        paycheckDeductions = this.garnishmentService.loadEmpGarnishmentsByParentIdAndPayPeriod(null, wCal.getMonthValue(),wCal.getYear(), bc);
        DeductGarnMiniBean d;
        for (AbstractPaycheckGarnishmentEntity p : paycheckDeductions) {
          if (deductionBean.containsKey(p.getEmpGarnInfo().getEmpGarnishmentType().getId())) {
            d = deductionBean.get(p.getEmpGarnInfo().getEmpGarnishmentType().getId());


          } else {
            d = new DeductGarnMiniBean();
            d.setDeductionId(p.getEmpGarnInfo().getEmpGarnishmentType().getId());

          }
          d.setAmount(d.getAmount() + p.getAmount());
          d.setDescription(p.getEmpGarnInfo().getDescription());
          d.setName(p.getEmpGarnInfo().getEmpGarnishmentType().getDescription());
          deductionBean.put(p.getEmpGarnInfo().getEmpGarnishmentType().getId(), d);
          deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
        }
        //Give a little overview...
        int wNoOfEmployees = this.garnishmentService.getNoOfEmployeesWithLoanDeductions(0L,wCal.getMonthValue(), wCal.getYear(), bc);
        double wTotalLoanDeducted = this.garnishmentService.getTotalGarnishments(0L,wCal.getMonthValue(), wCal.getYear(), bc);

        //String totalInWords = CurrencyWordGenerator.getInstance().convertToWords(wTotalLoanDeducted);
        deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(wTotalLoanDeducted));
        deductDetails.setNoOfEmployees(wNoOfEmployees);
        deductDetails.setTotalCurrentDeduction(wTotalLoanDeducted);
        deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wCal.getMonthValue(), wCal.getYear()));
      }
    }

    else {
      PayrollFlag wPf = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);
      if (wPf.isNewEntity()) {
        //do some kind of notification here....
      } else {
        deductDetails.setFromDate(wPf.getPayPeriodStart());
        deductDetails.setToDate(wPf.getPayPeriodEnd());
        deductDetails.setCurrentDeduction("All Garnishments");
        deductDetails.setId(bc.getBusinessClientInstId());
//      deductDetails.setId(null);
        deductDetails.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(wPf.getPayPeriodStart()));
        deductDetails.setToDateStr(PayrollBeanUtils.getJavaDateAsString(wPf.getPayPeriodEnd()));
        deductDetails.setRunMonth(wPf.getPayPeriodStart().getMonthValue());
        deductDetails.setRunYear(wPf.getPayPeriodStart().getYear());
      }
    }


    List <EmpGarnishmentType>garnishmentListFiltered = this.garnishmentService.findEmpGarnishmentsByBusinessClient(bc);
    Collections.sort(garnishmentListFiltered,Comparator.comparing(EmpGarnishmentType::getName));


    deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
    deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
    model.addAttribute("garnishmentList", garnishmentListFiltered);
    addRoleBeanToModel(model, request);
    addDisplayErrorsToModel(model, request);
    model.addAttribute("garnishmentDetails", deductDetails);
    return "allOtherGarnishmentReport";
  }
  @RequestMapping(method={RequestMethod.GET}, params={"did", "rm", "ry", "pid"})
  public String setupForm(@RequestParam("did") Long dedTypeId, @RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
		  @RequestParam("pid") Long pid, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

    if (noOfEmpWivNegPay > 0)
    {
      return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + pRunMonth + "&ry=" + pRunYear;
    }
    DeductionDetailsBean deductDetails = new DeductionDetailsBean();
    HashMap <Long,DeductGarnMiniBean>deductionBean = new HashMap<Long,DeductGarnMiniBean>();

    List <AbstractPaycheckGarnishmentEntity>paycheckDeductions;
    LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
    LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
	String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
	String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);

     deductDetails.setFromDate(sDate);
    deductDetails.setToDate(eDate);
    deductDetails.setRunMonth(pRunMonth);
    deductDetails.setRunYear(pRunYear); 
    boolean usingDedType = false;
    if (dedTypeId == 0)
      deductDetails.setCurrentDeduction("All Garnishments");
    else {
      usingDedType = true;
    }

    deductDetails.setId(pid);
    deductDetails.setFromDateStr(pSDate);
    deductDetails.setToDateStr(pEDate);

    paycheckDeductions = this.garnishmentService.loadEmpGarnishmentsByParentIdAndPayPeriod(dedTypeId, pRunMonth,pRunYear, bc);
    DeductGarnMiniBean d;
    
    boolean dedTypeSet = false;
    for (AbstractPaycheckGarnishmentEntity p : paycheckDeductions) {
      if ((usingDedType) && (p.getEmpGarnInfo().getEmpGarnishmentType().getId().intValue() != dedTypeId)) {
        continue;
      }
      if (deductionBean.containsKey(p.getEmpGarnInfo().getEmpGarnishmentType().getId())) {
          d = deductionBean.get(p.getEmpGarnInfo().getEmpGarnishmentType().getId());

      }
      else {
          d = new DeductGarnMiniBean();
       
        if ((usingDedType) && (!dedTypeSet)) {
          dedTypeSet = true;
          deductDetails.setCurrentDeduction(d.getType());
        }
        d.setDeductionId(p.getEmpGarnInfo().getEmpGarnishmentType().getId());
       
      } 
      d.setAmount(d.getAmount() + p.getAmount());
      d.setDescription(p.getEmpGarnInfo().getDescription());
      d.setName(p.getEmpGarnInfo().getEmpGarnishmentType().getDescription());
      deductionBean.put(p.getEmpGarnInfo().getEmpGarnishmentType().getId(), d);
      deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
      
    }

    int wNoOfEmployees = this.garnishmentService.getNoOfEmployeesWithLoanDeductions(dedTypeId,pRunMonth, pRunYear, bc);
    double wTotalLoanDeducted = this.garnishmentService.getTotalGarnishments(dedTypeId,pRunMonth, pRunYear, bc);
    deductDetails.setNoOfEmployees(wNoOfEmployees);
    deductDetails.setTotalCurrentDeduction(wTotalLoanDeducted);
    deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
    deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(wTotalLoanDeducted));

    deductDetails.setDeductionId(dedTypeId);
    List <EmpGarnishmentType>garnishmentListFiltered = this.garnishmentService.findEmpGarnishmentsByBusinessClient(bc);

    Collections.sort(garnishmentListFiltered);
    deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
    deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
    model.addAttribute("garnishmentList", garnishmentListFiltered);
    addRoleBeanToModel(model, request);
    addDisplayErrorsToModel(model, request);
    model.addAttribute("garnishmentDetails", deductDetails);
    return "allOtherGarnishmentReport";
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
		  @RequestParam(value="_go", required=false) String go, 
		  @RequestParam(value="_cancel", required=false) String cancel, 
		  @ModelAttribute("garnishmentDetails") DeductionDetailsBean pDDB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
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
        return "redirect:otherGarnishmentDetailsReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry + "&pid=" + pDDB.getId();
      }
      return "redirect:allOtherGarnishmentReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry + "&pid=" + pDDB.getId();
    }

    return "redirect:" + pDDB.getSubLinkSelect() + ".do";
  }

  
}