package com.osm.gnl.ippms.ogsg.validators.qualification;

import java.util.List;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.qualification.EducationQualificationType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class EduQualTypeValidator extends BaseValidator {

	public EduQualTypeValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> arg0) {
		 
		return EducationQualificationType.class.isAssignableFrom(arg0);
	}

	
	@Override
	public void validate(Object pTarget, Errors pErrors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Qualification Type Name is required");

		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "codeName", "Required Field", "Code Name is required");
		 
		EducationQualificationType wObj = (EducationQualificationType) pTarget;
		
		if(IppmsUtils.isNullOrLessThanOne(wObj.getEducationSchoolType().getId())) {
			pErrors.rejectValue("educationSchoolType.id", "Invalid.Value", "Please select a value for 'School Type'");

			return;
		}
		 
		List<EducationQualificationType> wDBObj = null;
		if(wObj.isEditMode()) {
			wDBObj = this.genericService.loadAllObjectsWithSingleCondition(EducationQualificationType.class, CustomPredicate.procurePredicate("name", wObj.getName()), "name");
			if(!wDBObj.isEmpty()) {
				for(EducationQualificationType e : wDBObj) {
					if(e.getName().equalsIgnoreCase(wObj.getName())
							&& !e.getId().equals(wObj.getId())) {
						pErrors.rejectValue("name", "name.duplicate", "This Qualification Type Name has already been defined");

						return;
					}
				}
			
			}else {
				wDBObj = this.genericService.loadAllObjectsWithSingleCondition(EducationQualificationType.class, CustomPredicate.procurePredicate("codeName", wObj.getCodeName()), "name");
				if(!wDBObj.isEmpty()) {
					for(EducationQualificationType e : wDBObj) {
						if(e.getCodeName().equalsIgnoreCase(wObj.getCodeName())
								&& !e.getId().equals(wObj.getId())) {
							pErrors.rejectValue("codeName", "name.duplicate", "This Qualification Type 'Code Name' exists for another Qualification Type");

							return;
						}
					}
			  }
			}
			 
		}else {
			wDBObj = this.genericService.loadAllObjectsWithSingleCondition(EducationQualificationType.class, CustomPredicate.procurePredicate("name", wObj.getName()), "name");
			if(!wDBObj.isEmpty()) {
				pErrors.rejectValue("name", "name.duplicate", "This Qualification Type Name has already been defined");

				return;
			
			}else {
				wDBObj =  this.genericService.loadAllObjectsWithSingleCondition(EducationQualificationType.class, CustomPredicate.procurePredicate("codeName", wObj.getCodeName()), "name");
				if(!wDBObj.isEmpty()) {
					pErrors.rejectValue("name", "name.duplicate", "This Qualification Type Code Name has already been defined");

					return;
			  }
			}
		 
		}
		 
	}

}
