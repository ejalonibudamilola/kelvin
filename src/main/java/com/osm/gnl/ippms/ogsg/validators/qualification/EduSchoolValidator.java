package com.osm.gnl.ippms.ogsg.validators.qualification;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.qualification.EducationSchool;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class EduSchoolValidator extends BaseValidator {


	public EduSchoolValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return EducationSchool.class.isAssignableFrom(clazz);
	}

	 
	public void validate(Object pTarget, Errors pErrors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "School Name is required");

		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "codeName", "Required Field", "Code Name is required");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "address", "Required Field", "Address is required");

		EducationSchool wObj = (EducationSchool) pTarget;
		
		if(IppmsUtils.isNullOrLessThanOne(wObj.getEducationSchoolType().getId())) {
			pErrors.rejectValue("educationSchoolType.id", "Invalid.Value", "Please select a value for 'School Type'");
			return;
		}
		if(IppmsUtils.isNullOrLessThanOne(wObj.getStateInfo().getId())) {
			pErrors.rejectValue("stateInfo.id", "Invalid.Value", "Please select a value for 'School State'");
			return;
		}
		if(wObj.getLocalInd() == -1) {
			pErrors.rejectValue("localInd", "Invalid.Value", "Please select a value for 'Internation/Local'");
		}
		 
		 
	}

	

}
