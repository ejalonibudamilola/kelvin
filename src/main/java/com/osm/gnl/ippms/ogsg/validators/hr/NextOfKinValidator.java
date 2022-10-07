package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.NextOfKin;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class NextOfKinValidator extends BaseValidator {

	protected NextOfKinValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return NextOfKin.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) { }

	public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate)
	  {
	    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "firstName", "Required Field", "First Name is a required field");
	    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "lastName", "Required Field", "Last Name is a required field");

	    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "address", "Required Field", "Address is required");
 	    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "gsmNumber", "Required Field", "'Primary Phone Number' is required.");

	    if (pErrors.getErrorCount() < 1) {
	      NextOfKin wNok = (NextOfKin)pTarget;
	      
	      if(wNok.getCity().isNewEntity() || !IppmsUtils.isNotNullAndGreaterThanZero(wNok.getCity().getId())){
	    	  pErrors.rejectValue("", "Value.Required", "Please select the 'City' for Next Of Kin");
	      }

	     
	      if(wNok.getRelationshipType().isNewEntity() ||!IppmsUtils.isNotNullAndGreaterThanZero( wNok.getRelationshipType().getId())){
	    	  pErrors.rejectValue("", "Value.Required", "Please select the 'Relationship Type' for Next Of Kin");
	      }
	      
	      //Now check if this next of kin has 
	      if(!allNumeric(wNok.getGsmNumber())){
	    	  pErrors.rejectValue("", "Value.Required", "'Primary Phone Number' should be all numeric");
	      }else{
	    	  //-- Now Check if some other Guy has this Number...
	    	  List<NextOfKin> wNextOfKinList = genericService.loadAllObjectsUsingRestrictions(NextOfKin.class, Arrays.asList(
	    	  		CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()), CustomPredicate.procurePredicate("gsmNumber", wNok.getGsmNumber())),null);
	      
	    	  if(!wNextOfKinList.isEmpty()){
	    		  for(NextOfKin nok : wNextOfKinList){
	    			  if(!nok.getParentObject().getId().equals(wNok.getAbstractEmployeeEntity().getId())){
	    				  pErrors.rejectValue("", "Value.Required", businessCertificate.getStaffTypeName()+" "+nok.getParentObject().getDisplayName()+" [ "+nok.getParentObject().getEmployeeId()+" ] has a next of kin with the same 'Primary Phone Number'. Please changed.");
	    			  }
	    		  }
	    	  }
	      }
	      if(!StringUtils.isBlank(wNok.getGsmNumber2())){
	    	  if(!allNumeric(wNok.getGsmNumber())){
		    	  pErrors.rejectValue("", "Value.Required", "'Alternate Phone Number' should be all numeric");
		      }else{
		    	  //-- Now Check if some other Guy has this Number...

				  List<NextOfKin> wNextOfKinList = genericService.loadAllObjectsUsingRestrictions(NextOfKin.class, Arrays.asList(
						  CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()), CustomPredicate.procurePredicate("gsmNumber2", wNok.getGsmNumber2())),null);

				  if(!wNextOfKinList.isEmpty()){
		    		  for(NextOfKin nok : wNextOfKinList){
		    			  if(!nok.getParentObject().getId().equals(wNok.getParentObject().getId())){
		    				  pErrors.rejectValue("", "Value.Required", businessCertificate.getStaffTypeName()+" "+nok.getParentObject().getDisplayName()+" [ "+nok.getParentObject().getEmployeeId()+" ] has a next of kin with the same 'Alternate Phone Number'. Please changed.");
		    			  }
		    		  }
		    	  }
		      }
	      }
	      
	      if(!StringUtils.isBlank(wNok.getEmailAddress())){
	    	  
		    	  //-- Now Check if some other Guy has this Number...

			      List<NextOfKin> wNextOfKinList = genericService.loadAllObjectsUsingRestrictions(NextOfKin.class, Arrays.asList(
					  CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()), CustomPredicate.procurePredicate("emailAddress", wNok.getEmailAddress())),null);

			  if(!wNextOfKinList.isEmpty()){
		    		  for(NextOfKin nok : wNextOfKinList){
		    			  if(!nok.getParentObject().getId().equals(wNok.getAbstractEmployeeEntity().getId())){
 		    				  pErrors.rejectValue("", "Value.Required", businessCertificate.getStaffTypeName()+" "+nok.getParentObject().getDisplayName()+" [ "+nok.getParentObject().getEmployeeId()+" ] has a next of kin with the same 'Email Address'. Please changed.");
		    			  }
		    		  }
		    	  }
		      
	      }

	      
	  }
	}
}
