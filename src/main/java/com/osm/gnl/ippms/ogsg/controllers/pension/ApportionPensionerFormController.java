/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.pension;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.NewPensionerBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollGratuityUtil;
import com.osm.gnl.ippms.ogsg.validators.pension.CreateGratuityAndPensionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;


@Controller
@RequestMapping("/apportionPensioner.do")
@SessionAttributes(types = NewPensionerBean.class)
public class ApportionPensionerFormController extends BaseController {


	@Autowired
	private PensionService pensionService;

	@Autowired
	private CreateGratuityAndPensionValidator validator;
	
	private final String VIEW = "pension/createApportionmentForm";

	public ApportionPensionerFormController(){}

	/**
	 * This Method serves as a redirect to the actual method for Apportionment.
	 * Note : This is from the Multi Result Search Controller
	 * 		  This should be replicated where there are multi results for a search.
	 * @param pHid
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, params={"eid"})
	public String setupForm(@RequestParam("eid") Long pHid, HttpServletRequest request) throws Exception {

		SessionManagerService.manageSession(request);

		HiringInfo wHI = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(
				CustomPredicate.procurePredicate("pensioner.id", pHid), getBusinessClientIdPredicate(request)));

		if(wHI.getYearlyPensionAmount() > 0 || wHI.getGratuityAmount() > 0)
			return "redirect:apportionPensioner.do?hid = "+wHI.getId()+"&rind=1";

		return "redirect:apportionPensioner.do?hid = "+wHI.getId();
	}
 
	@RequestMapping(method = RequestMethod.GET, params={"hid"})
	public String setupForm(@RequestParam("hid") Long pHid,
			Model model, HttpServletRequest request) throws Exception {
		
		SessionManagerService.manageSession(request);
		BusinessCertificate businessCertificate = getBusinessCertificate(request);
		NewPensionerBean wNPB = new NewPensionerBean();

		HiringInfo wHI = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(
				CustomPredicate.procurePredicate("id", pHid), getBusinessClientIdPredicate(request)));

		wNPB.setCalculateGratuityInd(ON);
		wNPB.setHiringInfo(wHI);
		wNPB.setApportionment(true);

		addRoleBeanToModel(model, request);
		model.addAttribute("apportionmentBean", wNPB);
		
		return VIEW;
	}
	
	
	 
	@RequestMapping(method = RequestMethod.GET, params={"hid","rind"})
	public String setupForm(@RequestParam("hid") Long pHid,@RequestParam("rind") int pRec,
			Model model, HttpServletRequest request) throws Exception {
		
		SessionManagerService.manageSession(request);
		NewPensionerBean wNPB = new NewPensionerBean();
		//First we need to get the HiringInfo object...
		HiringInfo wHI = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(
				CustomPredicate.procurePredicate("id", pHid), getBusinessClientIdPredicate(request)));
		
		//We have to load the Salary Scale Level and Step....
		
		wNPB.setApportionment(true); 
		wNPB.setCalculateGratuityInd(OFF);
		wNPB.setCalculatePensionInd(OFF);
		wNPB.setRecalculation(true);
		wNPB.setHiringInfo(wHI);
		//wNPB.getHiringInfo().setLengthOfService(PayrollHRUtils.getLengthOfService(wNPB.getHiringInfo() ));
		
		model.addAttribute("apportionmentBean", wNPB);
		
		return VIEW;
	}
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(
			@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,
			
			@ModelAttribute ("apportionmentBean") NewPensionerBean pHMB, 
			BindingResult result, SessionStatus status,
			Model model,HttpServletRequest request) throws Exception {
		
		SessionManagerService.manageSession(request);
		BusinessCertificate bc = super.getBusinessCertificate(request);

		if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
			//Go to the home Page.
			   return "redirect:pensionerOverviewForm.do?eid="+pHMB.getHiringInfo().getPensioner().getId();
			
		}
		PredicateBuilder predicateBuilder = new PredicateBuilder();
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("status", "P"));
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId(), Operation.EQUALS));

		if(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(bc)) > 0){
			 
			result.rejectValue("", "Pending.Paychecks", "Pending Paychecks found. Pension and Gratuity Apportionment can only be effected after a Payroll Run has been approved.");
		 
			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model,request);
		model.addAttribute("pageErrors", result);
		model.addAttribute("apportionmentBean", pHMB);
		return VIEW;
	}
		//Check for Errors....
		validator.validateForApportionment(pHMB, result);
		if(result.hasErrors()){
			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model, request);
			model.addAttribute("pageErrors", result);
			model.addAttribute("apportionmentBean", pHMB);
			return VIEW;
		}
		//Other wise...check if we have confirmation...
		if(!pHMB.isConfirmation()){
			pHMB.setConfirmation(!pHMB.isConfirmation());

			pHMB.setTotalLengthOfService(Integer.parseInt(pHMB.getTotalLengthOfServiceStr()));
			pHMB.setFinalLengthOfService(pHMB.getHiringInfo().getLengthOfService());
			pHMB = PayrollGratuityUtil.generateGatuityAndPensionByApportionment(pHMB,this.pensionService, bc);
			if(pHMB.isCalculateGratuity() && pHMB.isCalculatePensions())
			   result.rejectValue("", "Confirmation.Required", "Please Confirm this Gratuity and Pension Apportionment.");
			else if(pHMB.isCalculateGratuity() && !pHMB.isCalculatePensions())
					  result.rejectValue("", "Confirmation.Required", "Please Confirm this Gratuity Apportionment.");
			else if	(!pHMB.isCalculateGratuity() && pHMB.isCalculatePensions())
			  result.rejectValue("", "Confirmation.Required", "Please Confirm this Pension Apportionment.");

			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model, request);
			model.addAttribute("pageErrors", result);
			model.addAttribute("apportionmentBean", pHMB);
			return VIEW;
		}
		//If Confirmed. Now Save the Values....
		if(pHMB.isCalculateGratuity() && pHMB.getHiringInfo().getGratuityAmount() > 0.0D){
			//Make sure a date is put in there...
			if(pHMB.getGratuityEffectiveDate() == null){
				result.rejectValue("", "Confirmation.Required", "Please Enter a value for Gratuity Effective Date.");
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model.addAttribute("pageErrors", result);
				model.addAttribute("apportionmentBean", pHMB);
				return VIEW;
			}else{
				//TODO Add - Logic here to test Recalculation Logic.
			}
		}
	    HiringInfo wHireInfo = pHMB.getHiringInfo();
		
	    wHireInfo.setLastModBy(new User(bc.getLoginId()));
	    wHireInfo.setApportionmentInd(ON);
	    BigDecimal wNoOfYears = new BigDecimal(new Double(pHMB.getTotalLengthOfService()/12.0D)).setScale(1, RoundingMode.HALF_UP);
	    wHireInfo.setNoOfYearsInOgun(wNoOfYears.doubleValue());
	    wHireInfo.setLastModTs(Timestamp.from(Instant.now()));
	    this.genericService.saveObject(wHireInfo);
	    //Now Create the Gratuity Object if Gratuity is greater than 0.
	    if(pHMB.isCalculateGratuity() && wHireInfo.getGratuityAmount() > 0){
	    	//Now See if this dude has an existing Gratuity Info....
	    	GratuityInfo wGI = this.genericService.loadObjectUsingRestriction(GratuityInfo.class, Arrays.asList(CustomPredicate.procurePredicate("pensioner.id", wHireInfo.getPensioner().getId()),
					getBusinessClientIdPredicate(request)));
	    	if(wGI.isNewEntity()){
	    		wGI.setPensioner(wHireInfo.getPensioner());
	    		wGI.setMonth(pHMB.getGratuityEffectiveDate().getMonthValue());
	    		wGI.setYear(pHMB.getGratuityEffectiveDate().getYear());
	    		wGI.setCreationDate(Timestamp.from(Instant.now()));
	    		wGI.setCreatedBy(new User(bc.getLoginId()));
	    	} 
	    		wGI.setGratuityAmount(wHireInfo.getGratuityAmount());
	    		wGI.setOutstandingAmount(wHireInfo.getGratuityAmount());
	    		wGI.setLastModBy(new User(bc.getLoginId()));
	    		wGI.setLastModTs(Timestamp.from(Instant.now()));
	    		this.genericService.storeObject(wGI);
	    	 
	    }
		
	    
			   return "redirect:employeeOverviewForm.do?eid="+pHMB.getHiringInfo().getPensioner().getId();
		
	}
	
 


}
