package com.osm.gnl.ippms.ogsg.validators.allowance;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.domain.allowance.LeaveTransportGrantHolder;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class LtgValidator extends BaseValidator {


	private final HRService hrService;

	@Autowired
	public LtgValidator(GenericService genericService, HRService hrService) {
		super(genericService);
		this.hrService = hrService;
	}

	@Override
	public boolean supports(Class<?> pClass) {
	 
		return LeaveTransportGrantHolder.class.isAssignableFrom(pClass);
	}

	@Override
	public void validate(Object pTarget, Errors pErrors) {
		
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "ltgInstructionName", "Required Field", "LTG Instruction Name is required");
		
		LeaveTransportGrantHolder pHMB = (LeaveTransportGrantHolder)pTarget;
		
		if(pHMB.getMonthId() < 0) {
			pErrors.rejectValue("", "Invalid.Value", "Please select Month of the year to appy LTG.");
			return;
		}
		 if(pErrors.getErrorCount() == 0) {
			 char[] wCharArray = pHMB.getLtgInstructionName().toCharArray();
			 for (char c : wCharArray) {
			        if ((!Character.isLetterOrDigit(c)) || (!Character.isWhitespace(c))) {
			        	pErrors.rejectValue("", "Invalid.Value", "'LTG Instruction Name' Can ONLY be made up of Alphabets[Aa-Zz] and/or Numbers[0-9] with or without spaces. ");
			            return;
			        }
			 }
		 }
		

	}
	 
	public void validateForAdd(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws Exception {
		LeaveTransportGrantHolder pHMB = (LeaveTransportGrantHolder)pTarget;
		
		if (IppmsUtils.isNullOrLessThanOne(pHMB.getMdaInfo().getId())) {
			
			 
			pErrors.rejectValue("", "Invalid.Value", "Please select an Agency to Pay/Simulate Leave Transport Grant for");
	       
	      }else {
	    	  //Check if this MDA has Mapped Employees that are active...
	    	  if(!this.hrService.mdaMappingHasActiveEmployees(pHMB.getMdaInfo().getId(),businessCertificate)) {
	    		  MdaInfo wMdaInfo = this.genericService.loadObjectById(MdaInfo.class, pHMB.getMdaInfo().getId());
	    		  pHMB.setMdaInfo(wMdaInfo);
	    		  pErrors.rejectValue("", "Invalid.Value", "No Active "+ businessCertificate.getStaffTypeName()+" currently in "+pHMB.getMdaInfo().getName());
	    		  
	    	  }
	      }
	      
 
	}
	public void validateForConfirm(Object pTarget, Errors pErrors) throws Exception {
		
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "ltgInstructionName", "Required Field", "LTG Instruction Name is required");
		
		LeaveTransportGrantHolder pHMB = (LeaveTransportGrantHolder)pTarget;
		
		if(pHMB.getMonthId() < 0) {
			pErrors.rejectValue("", "Invalid.Value", "Please select Month of the year to appy LTG.");
			 
		}
		
		 if (!PayrollHRUtils.canBeXlsSheetName(pHMB.getLtgInstructionName())) {
			 pErrors.rejectValue("", "Invalid.Value", "'LTG Instruction Name' Contains invalid characters ");
			 return;
		 }
		 if(this.genericService.isObjectExisting(LtgMasterBean.class, Arrays.asList(CustomPredicate.procurePredicate("name", pHMB.getLtgInstructionName())))){
			 pErrors.rejectValue("", "Invalid.Value", pHMB.getLtgInstructionName() + " Exists for a different simulation.");
			 return;
		 }
 
	}

}
