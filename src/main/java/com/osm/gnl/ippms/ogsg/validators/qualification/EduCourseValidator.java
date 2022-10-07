package com.osm.gnl.ippms.ogsg.validators.qualification;

import java.util.List;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.qualification.EducationalCourses;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class EduCourseValidator extends BaseValidator {


	public EduCourseValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return EducationalCourses.class.isAssignableFrom(clazz);
	}

	
	public void validate(Object pTarget, Errors pErrors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Course Name is required");



		EducationalCourses wObj = (EducationalCourses) pTarget;
		
		if(IppmsUtils.isNullOrLessThanOne(wObj.getEducationSchoolType().getId())) {
			pErrors.rejectValue("schoolType.id", "Invalid.Value", "Please select a value for 'School Type'");
			return;
		}
		List<EducationalCourses> wDBObj = null;
		if(wObj.isEditMode()) {
			wDBObj = this.genericService.loadAllObjectsWithSingleCondition(EducationalCourses.class, CustomPredicate.procurePredicate("name", wObj.getName()), "name");
			if(!wDBObj.isEmpty()) {
				for(EducationalCourses e : wDBObj) {
					if(e.getName().equalsIgnoreCase(wObj.getName())
							&& !e.getId().equals(wObj.getId())) {
						pErrors.rejectValue("name", "name.duplicate", "This Course has already been defined");

						return;
					}
				}
			
			} 
			
		}else {
			wDBObj = this.genericService.loadAllObjectsWithSingleCondition(EducationalCourses.class, CustomPredicate.procurePredicate("name", wObj.getName()), "name");
			if(!wDBObj.isEmpty()) {
				pErrors.rejectValue("name", "name.duplicate", "This Course has already been defined");

			} 
		}
		 
	}

	

}
