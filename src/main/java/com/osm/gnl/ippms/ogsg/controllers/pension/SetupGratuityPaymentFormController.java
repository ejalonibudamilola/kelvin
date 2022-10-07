package com.osm.gnl.ippms.ogsg.controllers.pension;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.*;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.PayrollConfigImpact;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityDetails;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityMasterBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayPerPensioner;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayrollPensioner;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.pension.GratuityPaymentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/setupGratuity.do")
@SessionAttributes(types = GratuityMasterBean.class)
public class SetupGratuityPaymentFormController extends BaseController {

	@Autowired
	private PensionService pensionService;

	@Autowired
	private PayrollService payrollService;
	@Autowired
	private DeductionService deductionService;
	@Autowired
	private LoanService loanService;
	@Autowired
	private SpecAllowService specAllowService;

	@Autowired
	private GratuityPaymentValidator gratuityPaymentValidator;
	
	public final String VIEW_NAME = "pension/setupGratuityForm";

	public SetupGratuityPaymentFormController( ){
	}
	@RequestMapping(method = RequestMethod.GET, params={"pd","pp","noe"})
	public String setupForm(@RequestParam("pd") String pPayDate,
			@RequestParam("pp") String pPayPeriod,
			@RequestParam("noe") int pFullListSize,
			Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		
		SessionManagerService.manageSession(request);
		GratuityMasterBean wGMB = new GratuityMasterBean();
		wGMB.setNoOfEmployees(pFullListSize);
		wGMB.setCodeName(pPayPeriod);
		wGMB.setCreationDate(Timestamp.valueOf(pPayDate));
		wGMB.setName("No Selection");
		
		List<NamedEntity> wGratuityList = pensionService.loadUniqueOutstandingGratuities();
		
		wGMB.setGratuityList(wGratuityList);
		addRoleBeanToModel(model, request);
		model.addAttribute("gratBean", wGMB);
		
		return VIEW_NAME;
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit( @RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,  @ModelAttribute ("gratBean") GratuityMasterBean pHMB,
			BindingResult result, SessionStatus status, Model model,HttpServletRequest request) throws Exception {
		
		SessionManagerService.manageSession(request);
		BusinessCertificate bc = super.getBusinessCertificate(request);
		HttpSession session = getSession(request);

		if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
			//Go to the home Page.
			//Here Return to run Payroll.....
			return "redirect:paydayForm.do";
			
		}
		//Check for Errors....
		gratuityPaymentValidator.validate(pHMB, result);
		if(result.hasErrors()){
			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model, request);
			model.addAttribute("pageErrors", result);
			model.addAttribute("gratBean", pHMB);
			return VIEW_NAME;
		}
		//Other wise......
		//we need to create the Gratuity Master Bean and details for each one...
		boolean atLeastOneChecked = false;
		for(NamedEntity n : pHMB.getGratuityList()){
			if(Boolean.valueOf(n.getPayGratuity())){
				atLeastOneChecked = true;
				break;
			}
				
		}
		pHMB.setConfirmation(atLeastOneChecked);
		
		runPayroll(pHMB,bc,session,request);
		
	  return "redirect:displayStatus.do";
		
	}
	private void runPayroll(GratuityMasterBean pHMB, BusinessCertificate pBc, HttpSession pSession, HttpServletRequest request) throws Exception
	{
		String fCurrentUser = pBc.getUserName();
		LocalDate fPayDate = pHMB.getCreationDate().toLocalDateTime().toLocalDate();
		String fPayPeriod = pHMB.getCodeName();


		LocalDate fPayPeriodStart = PayrollBeanUtils.getDateFromPayPeriod(fPayPeriod,true);
		LocalDate fPayPeriodEnd = PayrollBeanUtils.getDateFromPayPeriod(fPayPeriod,false);
		
		//First fill all the Maps and set them.
		List<AbstractDeductionEntity> wEmpListToSplit = this.deductionService.loadToBePaidEmployeeDeductions(pBc,fPayPeriodStart,fPayPeriodEnd,false);
 		List<AbstractSpecialAllowanceEntity> wAllowListToSplit = this.specAllowService.loadToBePaidEmployeeSpecialAllowances(pBc,fPayPeriodStart,fPayPeriodEnd,false);

		HashMap<Long,List<AbstractDeductionEntity>> fEmpDedMap = EntityUtils.breakUpDeductionList(wEmpListToSplit, fPayPeriodStart, fPayPeriodEnd) ;
 		HashMap<Long,List<AbstractSpecialAllowanceEntity>> fAllowanceMap =EntityUtils.breakUpAllowanceList(wAllowListToSplit, fPayPeriodStart, fPayPeriodEnd);

		List<SalaryInfo> wList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, CustomPredicate.procurePredicate("businessClientId", pBc.getBusinessClientInstId()), null);
		Map<Long, SalaryInfo> wMap = EntityUtils.breakSalaryInfo(wList);


		CalculatePayPerPensioner fCalcPayPerEmployee = new CalculatePayPerPensioner();
		//We need to know if we have Payments for this dude....
		if(pHMB.isConfirmation()){
			//Then Create Gratuity Master Bean...
			GratuityMasterBean wGMB = new GratuityMasterBean();
			assert fPayPeriodEnd != null;
			wGMB.setRunMonth( fPayPeriodEnd.getMonthValue());
			wGMB.setRunYear(fPayPeriodEnd.getYear());
			wGMB.setLastModBy(new User(pBc.getLoginId()));
			wGMB.setLastModTs(Timestamp.from(Instant.now()));
			wGMB.setApplicationIndicator(OFF);
			this.genericService.saveObject(wGMB);
		    HashMap<String,Double> wGDMap = new HashMap<String,Double>();
			//Now create the Details and add to CalculatePayPerEmployee...
		    HashMap<Long,NamedEntity> wEmpGratuityMap = new HashMap<>();
			for(NamedEntity n : pHMB.getGratuityList()){
				if(Boolean.valueOf(n.getPayGratuity())){
					GratuityDetails wGD = new GratuityDetails();
					wGD.setGratuityMasterBean(wGMB);
					wGD.setApplyMonth(n.getNoOfEmployees());
					wGD.setApplyYear(n.getPageSize());
					wGD.setApplicationPercentage(Double.parseDouble(PayrollHRUtils.removeCommas(n.getPayPercentageStr())));
				    this.genericService.saveObject(wGD);
				    wGDMap.put(wGD.getApplyMonth() +":"+ wGD.getApplyYear(), wGD.getApplicationPercentage());
				    wEmpGratuityMap = this.pensionService.loadEmployeeGratuityByMonthAndYear(wEmpGratuityMap,wGD.getApplyMonth(),wGD.getApplyYear());
				}
			}
			fCalcPayPerEmployee.setPayGratuity(pHMB.isConfirmation());
			fCalcPayPerEmployee.setGratuityMasterBean(wGMB);
			fCalcPayPerEmployee.setGratuityPaymentMethodMap(wGDMap);
			fCalcPayPerEmployee.setEmployeeGratuityPaymentMap(wEmpGratuityMap);
			//Now lets load G
		}
		 fCalcPayPerEmployee.setSixtyYearsAgo(this.calculate60yrsAgo(fPayPeriodEnd));
		fCalcPayPerEmployee.setThirtyFiveYearsAgo(this.calculate35YearsAgo(fPayPeriodEnd));
		fCalcPayPerEmployee.setPayPeriodEnd(fPayPeriodEnd);
		fCalcPayPerEmployee.setEmployeeDeductions(fEmpDedMap);
 		fCalcPayPerEmployee.setSpecialAllowances(fAllowanceMap);
		

		

		PayrollRunMasterBean wPMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(CustomPredicate.procurePredicate("runMonth", fPayPeriodEnd.getMonthValue()),
				CustomPredicate.procurePredicate("runYear", fPayPeriodEnd.getYear()), CustomPredicate.procurePredicate("businessClientId", pBc.getBusinessClientInstId())));
		   if(wPMB.isNewEntity()){
		    	wPMB.setRunMonth( fPayPeriodEnd.getMonthValue());
		    	wPMB.setRunYear(fPayPeriodEnd.getYear());
		   }
		wPMB.setInitiator(new User(pBc.getLoginId()));
		wPMB.setPayrollStatus(IConstants.ON);
		wPMB.setStartDate(LocalDate.now());
		wPMB.setStartTime(PayrollBeanUtils.getCurrentTime(false));
		this.genericService.storeObject(wPMB);

		PayrollConfigImpact payrollConfigImpact = IppmsUtilsExt.configurePayrollConfigImpact(genericService,loadConfigurationBean(request),wPMB,pBc);
		fCalcPayPerEmployee.setSalaryInfoMap(wMap);


		//Use to test for the bug that the Run Month and Year is not being set for Agencies.....
		fCalcPayPerEmployee.setRunMonth(fPayPeriodEnd.getMonthValue());
		fCalcPayPerEmployee.setRunYear(fPayPeriodEnd.getYear());
        List<MdaInfo> mdaInfoList = genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,CustomPredicate.procurePredicate("businessClientId", pBc.getBusinessClientInstId()), null);
		PfaInfo defPfa = genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("defaultInd", 1));
		CalculatePayrollPensioner wCalcPay = new CalculatePayrollPensioner(genericService, payrollService,pensionService, pBc,fPayDate, fPayPeriodStart, fPayPeriodEnd, fPayPeriod,
				fCurrentUser,fCalcPayPerEmployee,pHMB.getNoOfEmployees(),wPMB, mdaInfoList,defPfa, loadConfigurationBean(request),payrollConfigImpact);
        	
       pSession.setAttribute("myCalcPay", wCalcPay);
       pSession.setAttribute("ppStartDate", fPayPeriodStart);
       pSession.setAttribute("ppEndDate", fPayPeriodEnd);
       pSession.setAttribute("payDayDate", fPayDate);
        Thread t = new Thread(wCalcPay);
        t.start();
       
   
	}
	

	private LocalDate calculate35YearsAgo(LocalDate fPayPeriodEnd)
	{
		return LocalDate.of(fPayPeriodEnd.getYear() - 35, fPayPeriodEnd.getMonthValue(),fPayPeriodEnd.getDayOfMonth());
	}


	private LocalDate calculate60yrsAgo(LocalDate fPayPeriodEnd)
	{
		return LocalDate.of(fPayPeriodEnd.getYear() - 90, fPayPeriodEnd.getMonthValue(),fPayPeriodEnd.getDayOfMonth());
	}

}
