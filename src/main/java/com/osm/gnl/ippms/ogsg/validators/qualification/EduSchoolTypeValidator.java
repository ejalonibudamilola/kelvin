package com.osm.gnl.ippms.ogsg.validators.qualification;

import java.util.List;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.qualification.EducationSchoolType;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class EduSchoolTypeValidator extends BaseValidator {


	public EduSchoolTypeValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return EducationSchoolType.class.isAssignableFrom(clazz);
	}

	
	public void validate(Object pTarget, Errors pErrors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "School Type Name is required");

		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", "Description is required");

		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "codeName", "Required Field", "Code Name is required");
		 
		EducationSchoolType wObj = (EducationSchoolType) pTarget;
		
		if(wObj.getHigherInstitutionInd() == -1) {
			pErrors.rejectValue("higherInstitutionInd", "Invalid.Value", "Please select a value for 'Higher Institution?'");
			return;
		}
		List<EducationSchoolType> wDBObj;
		if(wObj.isEditMode()) {
			wDBObj = (this.genericService.loadAllObjectsWithSingleCondition(EducationSchoolType.class, CustomPredicate.procurePredicate("name", wObj.getName()), "name"));
			if(!wDBObj.isEmpty()) {
				for(EducationSchoolType e : wDBObj) {
					if(e.getName().equalsIgnoreCase(wObj.getName())
							&& !e.getId().equals(wObj.getId())) {
						pErrors.rejectValue("name", "name.duplicate", "This School Type Name has already been defined");

						return;
					}
				}
			
			}else {
				wDBObj = this.genericService.loadAllObjectsWithSingleCondition(EducationSchoolType.class, CustomPredicate.procurePredicate("codeName", wObj.getCodeName()), "name");
				if(!wDBObj.isEmpty()) {
					for(EducationSchoolType e : wDBObj) {
						if(e.getCodeName().equalsIgnoreCase(wObj.getCodeName())
								&& !e.getId().equals(wObj.getId())) {
							pErrors.rejectValue("codeName", "name.duplicate", "This School Type 'Code Name' exists for another School Type");

							return;
						}
					}
			  }
			}
			if (pErrors.getErrorCount() < 1) {
				wDBObj = this.genericService.loadAllObjectsWithSingleCondition(EducationSchoolType.class,  CustomPredicate.procurePredicate("description", wObj.getDescription()), "name");
				if(!wDBObj.isEmpty()) {
					for(EducationSchoolType e : wDBObj) {
						if(e.getDescription().equalsIgnoreCase(wObj.getDescription())
								&& !e.getId().equals(wObj.getId())) {
							pErrors.rejectValue("description", "name.duplicate", "This School Type 'Description' exists for another School Type ");

							return;
						}
					}
			  }
			}
		}else {
			wDBObj =  this.genericService.loadAllObjectsWithSingleCondition(EducationSchoolType.class, CustomPredicate.procurePredicate( "name", wObj.getName()), "name");
			if(!wDBObj.isEmpty()) {
				pErrors.rejectValue("name", "name.duplicate", "This School Type Name has already been defined");

				return;
			
			}else {
				wDBObj = this.genericService.loadAllObjectsWithSingleCondition(EducationSchoolType.class, CustomPredicate.procurePredicate("codeName", wObj.getCodeName()), "name");
				if(!wDBObj.isEmpty()) {
					pErrors.rejectValue("name", "name.duplicate", "This School Type Code Name exists for another School Type");

					return;
			  }
			}
			if (pErrors.getErrorCount() < 1) {
				wDBObj =this.genericService.loadAllObjectsWithSingleCondition(EducationSchoolType.class, CustomPredicate.procurePredicate("description", wObj.getDescription()), "name");
				if(!wDBObj.isEmpty()) {
					pErrors.rejectValue("name", "name.duplicate", "This School Type Description exists for another School Type");

			  }
			}
		}
		 
	}

	
}