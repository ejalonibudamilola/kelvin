package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */


@Controller
@RequestMapping({"/paystubsReportForm.do"})
@SessionAttributes(types={PayrollSummaryBean.class})
public class AddPayStubsReportForm extends BaseController{
  

  private final PaycheckService paycheckService;
  private final PayrollService payrollServiceExt;

  private final String VIEW_NAME = "payment/paystubsReportForm";
  @Autowired
  public AddPayStubsReportForm(PaycheckService paycheckService, PayrollService payrollServiceExt)
  {
    this.paycheckService = paycheckService;
    this.payrollServiceExt = payrollServiceExt;
  }

  @ModelAttribute("monthList")
  public List<NamedEntity> getMonthList() {
    return PayrollBeanUtils.makeAllMonthList();
  }
  
  @ModelAttribute("yearList")
  public List<NamedEntity> makeYearList(HttpServletRequest request) {
	
    return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
  }

  @ModelAttribute("bankList")
  public List<BankInfo> makeBankList() {

    return this.genericService.loadAllObjectsWithoutRestrictions(BankInfo.class, "name");
  }



  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    List<?> empBeanList;
    Long pfId = this.genericService.loadMaxValueByClassClientIdAndColumn(PayrollRunMasterBean.class, "id", bc.getBusinessClientInstId(), "businessClientId");

    PayrollRunMasterBean pf = this.genericService.loadObjectById(PayrollRunMasterBean.class, pfId);

     PayrollSummaryBean pBSB;
    if (pf.isNewEntity()) {
      pBSB = new PayrollSummaryBean();
      pBSB.setShowRow(HIDE_ROW);
    }
    else
    {

      int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

      if (noOfEmpWivNegPay > 0)
      {
        LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
        return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + _wCal.getMonthValue() + "&ry=" + _wCal.getYear();
      }

      PaginationBean paginationBean = getPaginationInfo(request);

      empBeanList = this.payrollServiceExt.loadEmployeePayBeanByParentIdGlAndBank( pf.getRunMonth(), pf.getRunYear(), -1, -1, IConstants.EMPTY_STR, bc,false);


       int wNoOfElements = empBeanList.size();
      pBSB = new PayrollSummaryBean((List<AbstractPaycheckEntity>) empBeanList, paginationBean.getPageNumber(), pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

      pBSB.setShowUnionDues(false);
      pBSB.setShowRow(SHOW_ROW);
      pBSB.setEmployeeId(0L);
      pBSB.setId(  bc.getBusinessClientInstId());
      pBSB.setCompanyName(bc.getBusinessName());

      pBSB.setRunMonth(pf.getRunMonth());
      pBSB.setRunYear(pf.getRunYear());
      pBSB.setEmpPayBean((List<AbstractPaycheckEntity>) empBeanList);
    }

    model.addAttribute("paystubSummary", pBSB);
    addRoleBeanToModel(model, request);
    return VIEW_NAME ;
  }
  @RequestMapping(method={RequestMethod.GET}, params={"rm", "ry"})
  public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
    SessionManagerService.manageSession(request, model);
     return "redirect:paystubsReportForm.do?rm="+pRunMonth+"&ry="+pRunYear+"&sGL=0&eGL=0&bN=";
  }


  @RequestMapping(method={RequestMethod.GET}, params={"rm", "ry", "sGL", "eGL", "bN"})
  public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("sGL") int fromLevel,
                          @RequestParam("eGL") int toLevel, @RequestParam("bN") String bank, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);


    int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

    if (noOfEmpWivNegPay > 0)
    {
      return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + pRunMonth + "&ry=" + pRunYear;
    }

    PaginationBean paginationBean = getPaginationInfo(request);

    List<?> empBeanList = this.payrollServiceExt.loadEmployeePayBeanByParentIdGlAndBank( pRunMonth, pRunYear, fromLevel, toLevel, bank, bc,false);


    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth",pRunMonth));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear",pRunYear));


    int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,
            IppmsUtils.getPaycheckClass(bc));

    PayrollSummaryBean pBSB = new PayrollSummaryBean((List<AbstractPaycheckEntity>) empBeanList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

       pBSB.setShowUnionDues(true);

    pBSB.setShowRow(SHOW_ROW);
    pBSB.setEmployeeId(0L);
    pBSB.setId( bc.getBusinessClientInstId());
    pBSB.setCompanyName(bc.getBusinessName());
    pBSB.setRunMonth(pRunMonth);
    pBSB.setRunYear(pRunYear);
    pBSB.setBankName(bank);
    pBSB.setFromLevel(fromLevel);
    pBSB.setToLevel(toLevel);

    addRoleBeanToModel(model, request);
    
    model.addAttribute("paystubSummary", pBSB);

    return VIEW_NAME;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, 
		  @RequestParam(value="_close", required=false) String close,
		  @ModelAttribute("paystubSummary") PayrollSummaryBean ppDMB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
 
	  if (isButtonTypeClick(request, REQUEST_PARAM_CLOSE))
	    {
	      return "redirect:reportsOverview.do";
	    }
 
      return "redirect:paystubsReportForm.do?rm=" + ppDMB.getRunMonth() + "&ry=" + ppDMB.getRunYear()+"&sGL="
              +ppDMB.getFromLevel()+"&eGL="+ppDMB.getToLevel()+"&bN="+ppDMB.getBankName();
    
  }
}