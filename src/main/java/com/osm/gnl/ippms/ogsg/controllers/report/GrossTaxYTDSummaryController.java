package com.osm.gnl.ippms.ogsg.controllers.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping({"/taxSummaryYTD.do"})
@SessionAttributes(types={PayrollSummaryBean.class})
public class GrossTaxYTDSummaryController extends BaseController
{
 
  private final int pageLength = 20;
  
  @Autowired 
  private PaycheckService paycheckService;
   

  
  private final String VIEW = "grossTaxSummary";
   
  public GrossTaxYTDSummaryController()
  {}
  
  
  
  @ModelAttribute("yearList")
  public Collection<NamedEntity> makeYearList(HttpServletRequest request) {
      BusinessCertificate bc = getBusinessCertificate(request);
    return  this.paycheckService.makePaycheckYearList(bc);
  }
  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);
 
    Long wInt =  this.genericService.loadMaxValueByClassAndLongColName(PayrollRunMasterBean.class, "id");
	
	PayrollRunMasterBean wPMB = genericService.loadObjectById(PayrollRunMasterBean.class, wInt);

    
    PayrollSummaryBean pBSB = null;
    if ((wPMB == null) || (wPMB.isNewEntity())) {
      pBSB = new PayrollSummaryBean();
      pBSB.setShowRow(HIDE_ROW);
      model.addAttribute("paystubSummary", pBSB);
      addRoleBeanToModel(model, request);
      return VIEW;
    }
    else
    { 
    	return this.generateModel(wPMB.getRunYear(), bc, request, model);
    }

    
  }

  @RequestMapping(method={RequestMethod.GET}, params={"ry"})
  public String setupForm(@RequestParam("ry") int pRunYear,  Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

     return this.generateModel(pRunYear, bc, request, model);
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(
		  @RequestParam(value="_cancel", required=false) String cancel,
		  @ModelAttribute("paystubSummary") PayrollSummaryBean ppDMB, 
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);
 
	  
	  

	    if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
	    	return "redirect:reportsOverview.do";
	    }
	    
    
    
       if(ppDMB.getRunYear() == -1){
    	   result.rejectValue("", "Invalid.Value", "Please select a value for Payroll Run Year.");
    	      model.addAttribute("status", result);
    	      model.addAttribute("paystubSummary", ppDMB);
    	      addRoleBeanToModel(model, request);
    	      addDisplayErrorsToModel(model, request);
    	      return "grossTaxSummary";
       }

      return "redirect:taxSummaryYTD.do?ry=" + ppDMB.getRunYear();
    

  }
  
  private String generateModel(int pRunYear,BusinessCertificate pBc,HttpServletRequest pRequest, Model pModel){
	  
	  
	    List<AbstractPaycheckEntity> empBeanList = new ArrayList<>();

	    BusinessCertificate bc = getBusinessCertificate(pRequest);
	    
	    PaginationBean paginationBean = getPaginationInfo(pRequest);

	    empBeanList = this.paycheckService.loadEmployeePayBeanSummaryByParentIdAndLastPayPeriod(pBc.getBusinessClientInstId(), pRunYear, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, bc);

	    int wNoOfElements = this.paycheckService.countEmployeePayBeanSummaryByParentIdAndLastPayPeriod(pBc.getBusinessClientInstId(), pRunYear, bc);
	    
	     
	    PayrollSummaryBean pBSB = new PayrollSummaryBean(empBeanList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
	    pBSB.setShowRow(SHOW_ROW);
	    pBSB.setEmployeeId(0L);
	    pBSB.setId(pBc.getBusinessClientInstId());
	    pBSB.setCompanyName(pBc.getBusinessName());
	    pBSB.setRunYear(pRunYear);
	    pBSB.setFromDateAsString(PayrollBeanUtils.getJavaDateAsString(pBSB.getFromDate()));
	    pBSB.setToDateAsString(PayrollBeanUtils.getJavaDateAsString(pBSB.getToDate()));

	    pModel.addAttribute("paystubSummary", pBSB);
	    addRoleBeanToModel(pModel, pRequest);
	    return VIEW;  
  }
}