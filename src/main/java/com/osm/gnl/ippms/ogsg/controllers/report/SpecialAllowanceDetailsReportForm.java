package com.osm.gnl.ippms.ogsg.controllers.report;

import java.time.LocalDate;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.SpecAllowService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckSpecialAllowance;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping({"/otherSpecialAllowanceDetailsReport.do"})
@SessionAttributes(types={DeductionDetailsBean.class})
public class SpecialAllowanceDetailsReportForm extends BaseController
{


  private final PaycheckService paycheckService;
  private final SpecAllowService specAllowService;

  @Autowired
  public SpecialAllowanceDetailsReportForm(PaycheckService paycheckService, SpecAllowService specAllowService)
  {
    this.paycheckService = paycheckService;
    this.specAllowService = specAllowService;
  }
  
  @ModelAttribute("monthList")
  protected List<NamedEntity> getMonthsList(){
	  return PayrollBeanUtils.makeAllMonthList();
  }
  @ModelAttribute("yearList")
  protected Collection<NamedEntity> getYearList(HttpServletRequest request){
	  return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
  }

  @RequestMapping(method={RequestMethod.GET}, params={"did", "rm", "ry", "pid"})
  public String setupForm(@RequestParam("did") Long dedTypeId,  @RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("pid") Long pid, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

    boolean filter = false;
    if (IppmsUtils.isNotNullAndGreaterThanZero(dedTypeId)) {
      filter = true;
    }
    DeductionDetailsBean deductDetails = new DeductionDetailsBean();
    HashMap<Long, DeductGarnMiniBean> deductionBean = new HashMap<>();

    List <AbstractPaycheckSpecAllowEntity>paycheckDeductions;

    LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
    LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
	String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
	String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);
	 deductDetails.setRunMonth(pRunMonth);
	    deductDetails.setRunYear(pRunYear);   
	    
    deductDetails.setFromDate(sDate);
    deductDetails.setToDate(eDate);
    if (filter) {
      SpecialAllowanceType wSAT = this.genericService.loadObjectUsingRestriction(SpecialAllowanceType.class,
              Arrays.asList(CustomPredicate.procurePredicate("id", dedTypeId), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
      deductDetails.setCurrentDeduction(wSAT.getName());
    }

    deductDetails.setId(pid);
    deductDetails.setFromDateStr(pSDate);
    deductDetails.setToDateStr(pEDate);

    paycheckDeductions = this.specAllowService.loadPaycheckSpecialAllowanceByParentIdAndPayPeriod(dedTypeId,pRunMonth, pRunYear, bc);
    
   
    for (AbstractPaycheckSpecAllowEntity p : paycheckDeductions) {
    	DeductGarnMiniBean d;
      if (deductionBean.containsKey(p.getEmployeeInstId())) {
          d = deductionBean.get(p.getEmployeeInstId());

        
      }
      else
      {
          d = new DeductGarnMiniBean();
          d.setName(p.getEmployeeName());
          d.setDeductionId(p.getSpecialAllowanceInfo().getId());
          d.setId(p.getEmployeeInstId());
          d.setEmployeeId(p.getEmployeeId());
      }
      
      
      d.setAmount(d.getAmount() + p.getAmount());

      d.setDeductionId(p.getSpecialAllowanceInfo().getId());
      deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());

      //deductDetails.setTotalBalance(deductDetails.getTotalBalance() + d.getBalanceAmount());
      deductionBean.put(p.getEmployeeInstId(), d);
    
       
    }

    deductDetails.setDeductionId(dedTypeId);
    List <NamedEntity>allowanceListFiltered = this.specAllowService.loadSpecAllowTypeByPeriodAndFilter(pRunMonth, pRunYear, bc);
    Collections.sort(allowanceListFiltered);
    deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
    deductDetails.setPageSize(deductDetails.getDeductionMiniBean().size());
    deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
    model.addAttribute("allowanceTypeList", allowanceListFiltered);
    model.addAttribute("allowanceDetails", deductDetails);
    addRoleBeanToModel(model, request);
    return "specialAllowanceDetailsForm";
  }


  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
		  @ModelAttribute("allowanceDetails") DeductionDetailsBean pDDB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
 
	  int rm = pDDB.getRunMonth();
  	  int ry = pDDB.getRunYear();

    

    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
    
      if (pDDB.getDeductionId() > 0) {
        return "redirect:otherSpecialAllowanceDetailsReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry + "&pid=" + pDDB.getId();
      }
      return "redirect:allOtherSpecialAllowanceReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry + "&pid=" + pDDB.getId();
    }

   
    return "redirect:allOtherSpecialAllowanceReport.do";
  }

   
}