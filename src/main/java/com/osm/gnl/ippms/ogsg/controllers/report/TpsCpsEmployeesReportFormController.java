package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.ContributoryPensionService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
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
@RequestMapping({"/tpsReport.do"})
@SessionAttributes(types={PaginatedBean.class})
public class TpsCpsEmployeesReportFormController extends BaseController {

	@Autowired
	PaycheckService paycheckService;

	@Autowired
	ContributoryPensionService contributoryPensionService;

	  private final int pageLength = 20;
	  private final String VIEW = "report/tpsReportPage";


	public TpsCpsEmployeesReportFormController()
	{}
	  
	  @ModelAttribute("monthList")
	  public List<NamedEntity> getMonthList() {
	    return PayrollBeanUtils.makeAllMonthList();
	  }
	  
	  @ModelAttribute("yearList")
	  public List<NamedEntity> makeYearList(HttpServletRequest request) {
		
	    return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
	  }
	  
	  @ModelAttribute("mdaList")
	  public List<MdaInfo> populateMDAList(HttpServletRequest request){
		 return this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request),"name");
	  }
	  
	  
	  @ModelAttribute("pfaList")
	  public List<PfaInfo> populatePFAList(){
		  return this.genericService.loadControlEntity(PfaInfo.class);
	  }


	 @RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	    
	    PaginationBean paginationBean = getPaginationInfo(request);
	    List<EmpDeductMiniBean> tpsCpsList;
	    int wCount = 0;

//	    PayrollRun pf = this.payrollService.getMostRecentPayrollRunByParentId(bc.getBusinessClientInstId());
		 Long pfId = this.genericService.loadMaxValueByClassClientIdAndColumn(PayrollRun.class, "id",bc.getBusinessClientInstId(),"businessClientId");

		 PayrollRun pf = this.genericService.loadObjectById(PayrollRun.class,pfId);

		 int runMonth = 0;
		 int runYear = 0;

	    if (!pf.isNewEntity()) {
	    	runMonth = pf.getPayPeriodStart().getMonthValue();
	    	runYear = pf.getPayPeriodStart().getYear();

			int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

			if (noOfEmpWivNegPay > 0)
	      {
			  LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
	        return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + _wCal.getMonthValue() + "&ry=" + _wCal.getYear();
	      }
	     
	    }

	    else{
			PayrollFlag wPf = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);
			runMonth = wPf.getPayPeriodStart().getMonthValue();
			runYear = wPf.getPayPeriodStart().getYear();
		}
	     
	    Long wMdaInstId = 0L;
	    
	    
	     
//	    tpsCpsList = this.contributoryPensionService.loadTPSEmployeeByRunMonthAndYear(false, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength,
//	    		paginationBean.getSortOrder(), paginationBean.getSortCriterion(),pf.getPayPeriodStart().getMonthValue(), pf.getPayPeriodStart().getYear(),0L,wMdaInstId,true,true,true , bc);

		 tpsCpsList = this.contributoryPensionService.loadTPSEmployeeByRunMonthAndYear(false, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength,
				 paginationBean.getSortOrder(), paginationBean.getSortCriterion(),runMonth, runYear,0L,wMdaInstId,true,true,true , bc);

	    wCount = this.contributoryPensionService.getTotalNoOfTPSEmployeesByRunMonthAndRunYear(runMonth, runYear,0L,wMdaInstId,true,true,true);

	    boolean wErrorRecord = false;
	     
	    PaginatedBean pCList = new PaginatedBean(tpsCpsList, paginationBean.getPageNumber(), this.pageLength, wCount, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

	     pCList.setId(0L);
	    
	     pCList.setMdaInd(0L);
	     
	     pCList.setTpsOrCps(IConstants.TPS_IND);
	     
	     pCList.setRunMonth(runMonth);
	     pCList.setRunYear(runYear);
	     
	      
	     pCList.setUseTpsCpsRule("true");
	    
	     pCList.setIncludeTerminated("true");

	    pCList.setErrorRecord(wErrorRecord);
	    pCList.setShowRow(SHOW_ROW);

	    model.addAttribute("miniBean", pCList);
		 addRoleBeanToModel(model, request);
	    return VIEW;
	  }

	@RequestMapping(method={RequestMethod.GET}, params={"rm","ry","pid","mdaid","utcr","intemp","tpscps"})
	  public String setupForm(@RequestParam(value="rm", required= false) int pRunMonth,
			  @RequestParam(value="ry", required= false) int pRunYear,
			  @RequestParam(value="pid", required= false) Long pPfaInstId,
			  @RequestParam(value="mdaid", required= false) Long pMdaId,
			  @RequestParam(value="utcr", required= false) boolean  pUseRule,
			  @RequestParam(value="intemp", required= false) boolean pIncludeTerminated,
			  @RequestParam(value="tpscps", required= false) int pTpsCps,
			  Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	    PaginationBean paginationBean = getPaginationInfo(request);

	    List<EmpDeductMiniBean> tpsCpsList;
	    int wCount = 0;


		Long pfId = this.genericService.loadMaxValueByClassClientIdAndColumn(PayrollRun.class, "id",bc.getBusinessClientInstId(),"businessClientId");

		PayrollRun pf = this.genericService.loadObjectById(PayrollRun.class,pfId);


	    if (!pf.isNewEntity()) {

			int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

	      if (noOfEmpWivNegPay > 0)
	      {
			  LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
	        return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + _wCal.getMonthValue() + "&ry=" + _wCal.getYear();
	      }
	     
	    }
	    
	    boolean wTps = pTpsCps == IConstants.TPS_IND;
	    
	   
	    tpsCpsList = this.contributoryPensionService.loadTPSEmployeeByRunMonthAndYear(false, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength,
	    		paginationBean.getSortOrder(), paginationBean.getSortCriterion(),pRunMonth, pRunYear,pPfaInstId,pMdaId,pUseRule,pIncludeTerminated,wTps, bc);

	    wCount = this.contributoryPensionService.getTotalNoOfTPSEmployeesByRunMonthAndRunYear(pRunMonth, pRunYear,pPfaInstId,pMdaId,pUseRule,pIncludeTerminated,wTps);
	     
	    
	   
	    PaginatedBean pCList = new PaginatedBean(tpsCpsList, paginationBean.getPageNumber(), this.pageLength, wCount, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
	    
	    if(pPfaInstId > 0)
	      pCList.setId(pPfaInstId);
	    
	     pCList.setMdaInd(pMdaId);
	     
	     pCList.setRunMonth(pRunMonth);
	     pCList.setRunYear(pRunYear);
	     
	     if(pUseRule){
	    	 pCList.setUseTpsCpsRule("true");
	     }else{
	    	 pCList.setUseTpsCpsRule("false");
	     }
	     if(pIncludeTerminated)
	    	 pCList.setIncludeTerminated("true");
	     else
	    	 pCList.setIncludeTerminated("false");
	    
	    if(wTps)
	    	pCList.setTpsOrCps(IConstants.TPS_IND);
	    else
	    	pCList.setTpsOrCps(IConstants.CPS_IND);
	    
	    pCList.setShowRow(SHOW_ROW);

	    model.addAttribute("pfaList",
//				this.payrollServiceExt.loadObjectsByHqlStr("from PfaInfo order by name")
		this.genericService.loadControlEntity(PfaInfo.class));
	    model.addAttribute("miniBean", pCList);
		addRoleBeanToModel(model, request);
	    return VIEW;
	  }

	@RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, 
			  @RequestParam(value="_go", required=false) String go, 
			  @ModelAttribute("miniBean") PaginatedBean pDDB, 
			  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception	  {
		BusinessCertificate bc = super.getBusinessCertificate(request);

		SessionManagerService.manageSession(request, model);
 
	    
	    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
	    	
	    	if(pDDB.getRunMonth() == -1){
	    		result.rejectValue("fromDate", "InvalidValue", "Please select a value for 'Month'");
				addDisplayErrorsToModel(model, request);

		        model.addAttribute("status", result);

		        model.addAttribute("pfaList",
//						this.payrollServiceExt.loadObjectsByHqlStr("from PfaInfo order by name")
				this.genericService.loadControlObjectByHqlStr(bc, "from PfaInfo order by name",false));
	  	    	model.addAttribute("miniBean", pDDB);
				addRoleBeanToModel(model, request);
	    	}
	    	if(pDDB.getRunYear() == 0){
	    		result.rejectValue("fromDate", "InvalidValue", "Please select a value for 'Year'");
				addDisplayErrorsToModel(model, request);

		        model.addAttribute("status", result);

		        model.addAttribute("pfaList",
//						this.payrollServiceExt.loadObjectsByHqlStr("from PfaInfo order by name")
						this.genericService.loadControlObjectByHqlStr(bc, "from PfaInfo order by name",false));
	  	    	model.addAttribute("miniBean", pDDB);
				addRoleBeanToModel(model, request);
	    	}
	    	if(pDDB.getTpsOrCps() == 0 ){
	    		result.rejectValue("fromDate", "InvalidValue", "Please indicate whether TPS or CPS Employee Report should be generated");
				addDisplayErrorsToModel(model, request);

		        model.addAttribute("status", result);

		        model.addAttribute("pfaList",
//						this.payrollServiceExt.loadObjectsByHqlStr("from PfaInfo order by name"));
						this.genericService.loadControlObjectByHqlStr(bc, "from PfaInfo order by name",false));
	  	    	model.addAttribute("miniBean", pDDB);
				addRoleBeanToModel(model, request);
	    	}
	    	boolean wUseRule = Boolean.valueOf(pDDB.getUseTpsCpsRule()).booleanValue();
	    	boolean wIncludeTerminated = Boolean.valueOf(pDDB.getIncludeTerminated()).booleanValue();
	    	
	        return "redirect:tpsReport.do?rm="+pDDB.getRunMonth()+"&ry="+pDDB.getRunYear()+"&pid="+pDDB.getId()+"&mdaid="+pDDB.getMdaInd()+"&utcr="+wUseRule+"&intemp="+wIncludeTerminated+"&tpscps="+pDDB.getTpsOrCps() ;
	      
	    }

	    return REDIRECT_TO_DASHBOARD;
	  }

	

}
