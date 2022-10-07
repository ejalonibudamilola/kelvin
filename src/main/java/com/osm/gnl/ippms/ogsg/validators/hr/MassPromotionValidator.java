/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.MassEntryService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.massentry.PromotionHistory;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class MassPromotionValidator extends BaseValidator {


	public MassPromotionValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> clazz) {

		return PaginatedPaycheckGarnDedBeanHolder.class.isAssignableFrom(clazz)
				|| PayPeriodDaysMiniBean.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {

	}

	public void validateForPromotion(MassEntryService massEntryService, Object pTarget, Errors pErrors, BusinessCertificate bc, SalaryInfo oldSalaryInfo, SalaryInfo newSalaryInfo, boolean allChecked) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		PayPeriodDaysMiniBean pEHB = (PayPeriodDaysMiniBean)pTarget;
		if (pEHB.getToSalaryStructureId() == 0) {

			pErrors.rejectValue("", "Global.Change", "Please select 'To Level/Step'");
			return;

		}
		//Make sure it is not a step increment...

		if(oldSalaryInfo.getLevel() == newSalaryInfo.getLevel()){
			if(newSalaryInfo.getStep() - oldSalaryInfo.getStep() == 1){
				//This is a Step Increment...reject.
				pErrors.rejectValue("", "Global.Change", "This Module is for Mass Promotions ONLY. Step Increments are not allowed here.");
				return;
			}
		}else{
			if (newSalaryInfo.getLevel() < oldSalaryInfo.getLevel()
					|| (newSalaryInfo.getLevel() == oldSalaryInfo.getLevel()
					&& newSalaryInfo.getStep() <= oldSalaryInfo.getStep())) {
				pErrors.rejectValue("", "Global.Change", "This Module is strictly for Promotions ONLY.");
				pErrors.rejectValue("", "Global.Change", "If you wish to Demote, please use the Demotions Module.");
				return;

			}
		}

		if (!allChecked) {

			boolean atLeastOneChecked = false;

			for (Employee e : pEHB.getEmployeeList()) {
				if (Boolean.valueOf(e.getPayEmployee()).booleanValue()) {
					atLeastOneChecked = true;
					break;
				}
			}

			if (!atLeastOneChecked) {
				pErrors.rejectValue("", "Global.Change", "Please select at least 1 "+bc.getStaffTypeName()+" to be promoted.");
				return;
			}
		}
		//if we get here...check if the Employees are flagged for Promotion or Step Increment Approval...
		HashMap<Long,String> flaggedPromoMap = massEntryService.loadFlaggedPromotionMapForMassPromotion(bc);
		HashMap<Long,String> stepIncrementMap = massEntryService.loadStepIncrementMapForMassPromotion(bc);

		 boolean flaggedError = false, stepError = false;
	for (Employee e : pEHB.getEmployeeList()) {

			 if(flaggedPromoMap.containsKey(e.getId())){
			 	  e.setBankName(flaggedPromoMap.get(e.getId()));
			 	  if(!e.isAdd())
			 	  	e.setAdd(true);
			 	  if(!flaggedError) flaggedError = true;
			 	  e.setPayEmployee(null);
			 }
			 if(stepIncrementMap.containsKey(e.getId())){
			 	if(e.getBankName() != null){
			 		e.setBankName(e.getBankName()+" "+stepIncrementMap.get(e.getId()));
				}else{
					e.setBankName(stepIncrementMap.get(e.getId()));
				}
				 if(!e.isAdd())
					 e.setAdd(true);
				 if(!stepError) stepError = true;
				 e.setPayEmployee(null);
			 }
		}

	  if(flaggedError){
		  pErrors.rejectValue("", "Global.Change", "There are "+bc.getStaffTypeName()+"s Having Pending Flagged Promotions ");

	  }
	  if(stepError){
		  pErrors.rejectValue("", "Global.Change", "There are "+bc.getStaffTypeName()+"s Having Pending Step Increment Approvals ");
	  }
      if(stepError || flaggedError){
		  pErrors.rejectValue("", "Global.Change", "Affected Rows have been highlighted in Red. Please choose to ");
		  ((PayPeriodDaysMiniBean)pTarget).setErrorRecord(true);
	  }
	}
	public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate)
	  {
		 PaginatedPaycheckGarnDedBeanHolder wEHB = (PaginatedPaycheckGarnDedBeanHolder)pTarget;


	    if ((wEHB.getPayArrearsInd() != null) &&
	      (Boolean.valueOf(wEHB.getPayArrearsInd()).booleanValue())) {

	      Object wRetVal = PayrollHRUtils.getNumberFromString(wEHB.getAmountStr());

	      if(wRetVal == null){
	    	  pErrors.rejectValue("amountStr", "Required.Value", "Arrears amount '"+wEHB.getAmountStr()+"' is not a valid Monetary Value");
	    	  return;
	      }

	      if(wEHB.getStartDate() == null){
	    	  pErrors.rejectValue("", "Required.Value", "Arrears Start Date is required");
	    	  return;
	      }
	      if(wEHB.getEndDate() == null){
	    	  pErrors.rejectValue("", "Required.Value", "Arrears End Date is required");
	    	  return;
	      }
	      //-- Now check if the start date is after the last payroll approved date...
	      PayrollFlag p = new PayrollFlag();
		try {
 		    p = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class,
					CustomPredicate.procurePredicate("businessClientId",businessCertificate.getBusinessClientInstId()));
		} catch (InstantiationException | IllegalAccessException e) {

			e.printStackTrace();
		}

	      try{

	    	  if(wEHB.getStartDate().compareTo(p.getPayPeriodEnd()) <= 0){
	    		  pErrors.rejectValue("", "Required.Value", "Arrears Start Date must be after "+PayrollHRUtils.getDisplayDateFormat().format(p.getPayPeriodEnd()));
	        	  return;
	    	  }
	      }catch(Exception wEx){
	    	  pErrors.rejectValue("", "Required.Value", "Arrears Start Date is not a valid Date.");
	    	  return;
	      }
	      try{

	    	  if(wEHB.getStartDate().compareTo(wEHB.getEndDate()) >= 0){
	    		  pErrors.rejectValue("", "Required.Value", "Arrears Start Date MUST BE before Arrears End Date.");
	        	  return;
	    	  }
	      }catch(Exception wEx){
	    	  pErrors.rejectValue("", "Required.Value", "Arrears End Date is not a valid Date.");
	    	  return;
	      }
	      //Now check ALL Employees having Currently Active SALAR...
	      List<PromotionHistory> wPromoHist = (List<PromotionHistory>) wEHB.getPaginationListHolder();
			AbstractSpecialAllowanceEntity wSAI = null;
	      for(PromotionHistory wPH : wPromoHist){
	    	  wSAI = IppmsUtils.makeSpecialAllowanceInfoObject(businessCertificate);
			try {
 				wSAI = (AbstractSpecialAllowanceEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getSpecialAllowanceInfoClass(businessCertificate), Arrays.asList(CustomPredicate.procurePredicate("employee.id",wPH.getEmployee().getId()),
						CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()), CustomPredicate.procurePredicate("specialAllowanceType.arrearsInd", IConstants.ON)));

			} catch ( Exception e) {

				e.printStackTrace();
			}

	    	  if(!wSAI.isNewEntity()){
		    	  //Now check if the SALAR is still active....
		    	  if(wEHB.getStartDate().compareTo(wSAI.getEndDate()) <= 0 && !wSAI.isExpired() && wSAI.getAmount() > 0){
		    		  pErrors.rejectValue("", "Required.Value", "Employee "+wPH.getEmployee().getEmployeeId()+" currently has an active Salary Arrears. Amount : "+wSAI.getAmount()+" | Dates : "+PayrollHRUtils.getDateFormat().format(wSAI.getStartDate())+" - "+PayrollHRUtils.getDateFormat().format(wSAI.getEndDate()));
		    	  }
		      }
	      }




	    }


	  }

}
