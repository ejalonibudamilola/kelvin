/*
 * Copyright (c) 2020. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.pension;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.employee.beans.NewPensionerBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class CreateGratuityAndPensionValidator extends BaseValidator {

	public CreateGratuityAndPensionValidator(GenericService genericService) {
		super(genericService);
	}

	public void validate(Object pObject, Errors pErrors)
	{

		NewPensionerBean wNPB = (NewPensionerBean)pObject;
		//First check if SalaryType can be selected....
		if(IppmsUtils.isNullOrEmpty(wNPB.getTotalPayStr())){

			 if(IppmsUtils.isNullOrLessThanOne(wNPB.getSalaryTypeId())){
				
				 pErrors.rejectValue("", "Invalid.Value", "Please Select Pay Group to use in calculating Gratuity/Pension.");
					return;
				
			 }
			 
			 if(IppmsUtils.isNullOrLessThanOne(wNPB.getSalaryInfoId())){
				 pErrors.rejectValue("", "Invalid.Value", "Please Select Level and Step to use in generating Pension and Gratuity ."); 
				 return;
			 }

		}else{
			try{
				  Double.parseDouble(PayrollHRUtils.removeCommas(wNPB.getTotalPayStr()));
			 
			 }catch(Exception wEx){
				pErrors.rejectValue("", "Invalid.Value", "Please Enter a value for Total Emoluments."); 
				return;
			 }
		}
		if(wNPB.getCalculateGratuityInd() == 0 && wNPB.getCalculatePensionInd() == 0 && wNPB.isRecalculation()){
			pErrors.rejectValue("", "Invalid.Value", "For Apportionment Recalculations, please indicate either Pension and/or Gratuity."); 
			return;
		}
		
		
	}

	public void validateForApportionment(Object pObject,
			Errors pErrors)
	{
		NewPensionerBean wNPB = (NewPensionerBean)pObject;
		try{
			  Double.parseDouble(PayrollHRUtils.removeCommas(wNPB.getTotalPayStr()));
		 
		 }catch(Exception wEx){
			pErrors.rejectValue("", "Invalid.Value", "Please Enter a value for Total Emoluments."); 
			return;
		 }
		 if(wNPB.getCalculateGratuityInd() == 0 && wNPB.getCalculatePensionInd() == 0 && wNPB.isRecalculation()){
				pErrors.rejectValue("", "Invalid.Value", "For Apportionment Recalculations, please indicate either Pension and/or Gratuity."); 
				return;
			}
		  
				try{
					  Integer.parseInt(wNPB.getTotalLengthOfServiceStr());
				 
				 }catch(Exception wEx){
					pErrors.rejectValue("", "Invalid.Value", "Please Enter a value for 'Length Of Service In Ogun (In Months)'."); 
					return;
				 }
				
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(NewPensionerBean.class);
	}


}
