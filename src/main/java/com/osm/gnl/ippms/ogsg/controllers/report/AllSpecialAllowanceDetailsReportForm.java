package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.SpecAllowService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/allOtherSpecialAllowanceReport.do"})
@SessionAttributes(types={DeductionDetailsBean.class})
public class AllSpecialAllowanceDetailsReportForm extends BaseController
{

 private final PaycheckService paycheckService;

 private final SpecAllowService specAllowService;

 private final String VIEW_NAME =  "allowance/allOtherSpecialAllowanceReportForm";

 @Autowired
  public AllSpecialAllowanceDetailsReportForm(PaycheckService paycheckService, SpecAllowService specAllowService)
  {
	  this.paycheckService = paycheckService;
	  this.specAllowService = specAllowService;
  }
  
  @ModelAttribute("monthList")
  protected List<NamedEntity> getMonthsList(){
	  return PayrollBeanUtils.makeAllMonthList();
  }

  @ModelAttribute("yearList")
  protected List<NamedEntity> getYearList(HttpServletRequest request){
	  return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

    DeductionDetailsBean deductDetails = new DeductionDetailsBean();
    HashMap <Long, DeductGarnMiniBean>deductionBean = new HashMap<>();

    List<AbstractPaycheckSpecAllowEntity> paycheckSpecialAllowance = new ArrayList<>();

	  Long pfId = this.genericService.loadMaxValueByClassClientIdAndColumn(PayrollRun.class, "id",bc.getBusinessClientInstId(),"businessClientId");

	  if(IppmsUtils.isNotNullAndGreaterThanZero(pfId)){
		  PayrollRun pf = this.genericService.loadObjectById(PayrollRun.class,pfId);
		  deductDetails.setFromDate(pf.getPayPeriodStart());
		  deductDetails.setToDate(pf.getPayPeriodEnd());
		  deductDetails.setCurrentDeduction("All Special Allowances");
		  deductDetails.setId(bc.getBusinessClientInstId());
//      deductDetails.setId(null);
		  deductDetails.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodStart()));
		  deductDetails.setToDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodEnd()));
		  deductDetails.setRunMonth(pf.getPayPeriodStart().getMonthValue());
		  deductDetails.setRunYear(pf.getPayPeriodStart().getYear());
	  }else{
		  PayrollFlag wPf = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);
		  if(wPf.isNewEntity()){
		  	//do some kind of notification here....
		  }else{
			  deductDetails.setFromDate(wPf.getPayPeriodStart());
			  deductDetails.setToDate(wPf.getPayPeriodEnd());
			  deductDetails.setCurrentDeduction("All Special Allowances");
			  deductDetails.setId(bc.getBusinessClientInstId());
//      deductDetails.setId(null);
			  deductDetails.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(wPf.getPayPeriodStart()));
			  deductDetails.setToDateStr(PayrollBeanUtils.getJavaDateAsString(wPf.getPayPeriodEnd()));
 			  deductDetails.setRunMonth(wPf.getPayPeriodStart().getMonthValue());
			  deductDetails.setRunYear(wPf.getPayPeriodStart().getYear());
		  }
	  }



      return this.createModel(deductionBean, deductDetails,0L, model, request);
  }
  @RequestMapping(method={RequestMethod.GET}, params={"did", "rm", "ry", "pid"})
  public String setupForm(@RequestParam("did") Long dedTypeId, @RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, 
		  @RequestParam("pid") Long pid, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

		 

    DeductionDetailsBean deductDetails = new DeductionDetailsBean();
    HashMap <Long,DeductGarnMiniBean> deductionBean = new HashMap<Long,DeductGarnMiniBean> ();

    List <AbstractPaycheckSpecAllowEntity>paycheckDeductions = new ArrayList<>();

    LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
    LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
	String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
	String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);
	 deductDetails.setRunMonth(pRunMonth);
	    deductDetails.setRunYear(pRunYear);   
    deductDetails.setFromDate(sDate);
    deductDetails.setToDate(eDate);
     
    
    deductDetails.setId(pid);
    deductDetails.setFromDateStr(pSDate);
    deductDetails.setToDateStr(pEDate);
    

   return this.createModel(deductionBean, deductDetails,dedTypeId, model, request);

    
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, 
		  @RequestParam(value="_go", required=false) String go,
		  @RequestParam(value="_cancel", required=false) String cancel,
		  @ModelAttribute("allowanceDetails") DeductionDetailsBean pDDB, 
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
   if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
	    {
	      return "redirect:reportsOverview.do";
	    }
 
    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
     /* String sDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getFromDate());
      String eDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getToDate());*/
    	int rm = pDDB.getRunMonth();
    	int ry = pDDB.getRunYear();
      if (pDDB.getDeductionId() > 0) {
        return "redirect:otherSpecialAllowanceDetailsReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry  + "&pid=" + pDDB.getId();
      }
      return "redirect:allOtherSpecialAllowanceReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry  + "&pid=" + pDDB.getId();
    }

    return "redirect:" + pDDB.getSubLinkSelect() + ".do";
  }
  private String createModel(HashMap<Long, DeductGarnMiniBean> pDeductionBean,
							 DeductionDetailsBean pDeductDetails, Long pDedTypeId, Model model, HttpServletRequest request) throws Exception{


  	BusinessCertificate bc = getBusinessCertificate(request);

	    boolean usingDedType = false;
	    if (pDedTypeId == 0)
	      pDeductDetails.setCurrentDeduction("All Special Allowances");
	    else {
	      usingDedType = true;
	    }

	  List<AbstractPaycheckSpecAllowEntity> pPaycheckDeductions = this.specAllowService.loadPaycheckSpecialAllowanceByParentIdAndPayPeriod(pDedTypeId, pDeductDetails.getRunMonth(), pDeductDetails.getRunYear(), bc);
	    boolean dedTypeSet = false;
	    DeductGarnMiniBean d;
	    for (AbstractPaycheckSpecAllowEntity p : pPaycheckDeductions) {
	      
	      if (pDeductionBean.containsKey(p.getSpecialAllowanceInfo().getSpecialAllowanceType().getId())) {
	          d = pDeductionBean.get(p.getSpecialAllowanceInfo().getSpecialAllowanceType().getId());

	       
	      }
	      else {
	          d = new DeductGarnMiniBean();
	         
	        if ((usingDedType) && (!dedTypeSet)) {
	          dedTypeSet = true;
	          pDeductDetails.setCurrentDeduction(d.getName());
	        }
	        d.setDeductionId(p.getSpecialAllowanceInfo().getSpecialAllowanceType().getId());
	        
	      }

	        d.setAmount(d.getAmount() + p.getAmount());
	        d.setDescription(p.getSpecialAllowanceInfo().getDescription());
	        d.setName(p.getSpecialAllowanceInfo().getName());
	        pDeductionBean.put(p.getSpecialAllowanceInfo().getSpecialAllowanceType().getId(), d);
	        pDeductDetails.setTotal(pDeductDetails.getTotal() + p.getAmount());
	    }
	    
	  //Give a little overview...
	    int wNoOfEmployees = this.specAllowService.getNoOfEmployeesWithSpecialAllowance(pDedTypeId,pDeductDetails.getRunMonth(), pDeductDetails.getRunYear(), bc);
	    double wTotalSpecAllow = this.specAllowService.getTotalSpecialAllowancePaid(pDedTypeId,pDeductDetails.getRunMonth(), pDeductDetails.getRunYear(), bc);
	    
	    pDeductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(wTotalSpecAllow));
	    pDeductDetails.setNoOfEmployees(wNoOfEmployees);
	    pDeductDetails.setTotalCurrentDeduction(wTotalSpecAllow);
	    pDeductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pDeductDetails.getRunMonth(), pDeductDetails.getRunYear()));
	    List<NamedEntity> garnishmentListFiltered = this.specAllowService.loadSpecAllowTypeByPeriodAndFilter(pDeductDetails.getRunMonth(), pDeductDetails.getRunYear(), bc);
	    Collections.sort(garnishmentListFiltered);
	    pDeductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(pDeductionBean));
//	    pDeductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyle(pDeductDetails.getDeductionMiniBean()));
	    model.addAttribute("allowanceTypes", garnishmentListFiltered);
	    model.addAttribute("allowanceDetails", pDeductDetails);
	    addRoleBeanToModel(model, request);
	    return VIEW_NAME;
  }
  
}