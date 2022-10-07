package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.PaycheckDeductionService;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping({"/otherDeductionDetailsReport.do"})
@SessionAttributes(types={DeductionDetailsBean.class})
public class OtherDedDetailsReportForm extends BaseController
{

  @Autowired
  PaycheckService paycheckService;

  @Autowired
  PaycheckDeductionService paycheckDeductionService;

  private final String VIEW = "report/otherDeductionDetailsReport";

  @ModelAttribute("monthList")
  protected List<NamedEntity> getMonthsList(){
	  return PayrollBeanUtils.makeAllMonthList();
  }
  @ModelAttribute("yearList")
  protected Collection<NamedEntity> getYearList(HttpServletRequest request){
	  return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
  }
  
  public OtherDedDetailsReportForm()
  {}

  
@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"did", "rm", "ry", "pid"})
  public String setupForm(@RequestParam("did") Long dedTypeId, @RequestParam("rm") int pRunMonth, 
		  @RequestParam("ry") int pRunYear, @RequestParam("pid") Long pid, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
    BusinessCertificate bc = getBusinessCertificate(request);
		 
    boolean filter = false;
    if (IppmsUtils.isNotNullAndGreaterThanZero(dedTypeId)) {
      filter = true;
    }
    DeductionDetailsBean deductDetails = new DeductionDetailsBean();
    HashMap <Long, DeductGarnMiniBean>deductionBean = new HashMap<Long,DeductGarnMiniBean>();

    List<AbstractPaycheckDeductionEntity> paycheckDeductions;

    LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
    LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
    String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
    String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);
   
    deductDetails.setFromDate(sDate);
    deductDetails.setToDate(eDate);
    deductDetails.setRunMonth(pRunMonth);
    deductDetails.setRunYear(pRunYear);
    if (filter)
    {
      EmpDeductionType wDt = this.genericService.loadObjectUsingRestriction(EmpDeductionType.class,
              Arrays.asList(CustomPredicate.procurePredicate("id", dedTypeId), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
      deductDetails.setCurrentDeduction(wDt.getName() + " - " + wDt.getDescription());
    }
    deductDetails.setId(pid);
    deductDetails.setFromDateStr(pSDate);
    deductDetails.setToDateStr(pEDate);

    paycheckDeductions = this.paycheckDeductionService.loadEmpDeductionsByParentIdAndPayPeriod(dedTypeId, sDate.getMonthValue(), sDate.getYear(), bc);
    DeductGarnMiniBean d = null;
    for (AbstractPaycheckDeductionEntity p : paycheckDeductions) {
       

      if (deductionBean.containsKey(p.getAbstractEmployeeEntity().getId())) {
          d = deductionBean.get(p.getAbstractEmployeeEntity().getId());

        d.setAmount(d.getAmount() + p.getAmount());

        deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());

      }
      else
      {
          d = new DeductGarnMiniBean();
        d.setName(p.getAbstractEmployeeEntity().getDisplayName());

        d.setId(p.getAbstractEmployeeEntity().getId());
        d.setParentInstId(p.getEmpDedInfo().getId());
        d.setEmployeeId(p.getAbstractEmployeeEntity().getEmployeeId());
        d.setAmount(d.getAmount() + p.getAmount());
        d.setMdaName(p.getName());

        deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
        deductDetails.setTotalBalance(deductDetails.getTotalBalance() + d.getBalanceAmount());
      }
      deductionBean.put(p.getAbstractEmployeeEntity().getId(), d);

    }

    deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
    deductDetails.setTotalCurrentDeduction(this.paycheckDeductionService.getTotalDeductions(dedTypeId,pRunMonth, pRunYear, bc));
    deductDetails.setNoOfEmployees(this.paycheckDeductionService.getNoOfEmployeeWithDeductions(dedTypeId,pRunMonth, pRunYear, bc));
   
    deductDetails.setDeductionId(dedTypeId);
    List <EmpDeductionType>garnishmentListFiltered = this.genericService.loadAllObjectsUsingRestrictions(EmpDeductionType.class,
            Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "description");

    deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
    Collections.sort(deductDetails.getDeductionMiniBean());
    deductDetails.setPageSize(deductDetails.getDeductionMiniBean().size());
    deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(deductDetails.getTotalCurrentDeduction()));
    deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));

    model.addAttribute("deductionList", garnishmentListFiltered);
    model.addAttribute("deductionDetails", deductDetails);
    model.addAttribute("rm", pRunMonth);
    model.addAttribute("ry", pRunYear);
    model.addAttribute("did", dedTypeId);
    addRoleBeanToModel(model, request);
    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
		  @RequestParam(value="_cancel", required=false) String cancel, 
		  @RequestParam(value="_go", required=false) String go,
		  @ModelAttribute("deductionDetails") DeductionDetailsBean pDDB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
 
	  if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
	    {
	    	
	      return "redirect:reportsOverview.do";
	    }
	  
    

    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
    	int rm = pDDB.getRunMonth();
    	int ry = pDDB.getRunYear();
    	
    	//Calendar wCal = PayrollBeanUtils.getDateFromMonthAndYear(rm, ry);
    //  String sDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getFromDate());
    //  String eDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getToDate());
      if (pDDB.getDeductionId() > 0) {
        return "redirect:otherDeductionDetailsReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry + "&pid=" + pDDB.getId();
      }
      return "redirect:allOtherDeductionsReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry + "&pid=" + pDDB.getId();
    }

    if (isButtonTypeClick(request, REQUEST_PARAM_GO))
    {
      return "redirect:" + pDDB.getSubLinkSelect() + ".do";
    }

    return "redirect:allOtherDeductionsReport.do";
  }

  

  
}