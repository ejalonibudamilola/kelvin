package com.osm.gnl.ippms.ogsg.validators.hr;

import java.util.List;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class SchoolValidator extends BaseValidator {

	public SchoolValidator(GenericService genericService) {
		super(genericService);
	}

	
	public void validate(Object pTarget, Errors pErrors) {
		
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name",
				"Required Field", "School Name is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description",
				"Required Field", "Description is required");
		/*ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "codeName",
				"Required Field", "Parastatal Code is required");*/
	

		if (pErrors.getErrorCount() < 1) {
			SchoolInfo wHCDD = (SchoolInfo) pTarget;
			List<SchoolInfo> wPList = this.genericService.loadControlEntity(SchoolInfo.class);
			// Now see if there is an existing school with this name.
			if (!wHCDD.isEditMode()) {

				for (SchoolInfo p : wPList) {
					if (p.getName().trim().equalsIgnoreCase(wHCDD.getName().trim())) {
						pErrors.rejectValue("name", "name.duplicate",
								"This School Name has already been defined");
					
						break;
					}else if(p.getCodeName().trim().equalsIgnoreCase(wHCDD.getCodeName().trim())) {
						pErrors.rejectValue("codeName", "name.duplicate",
								"This School Code Name has already been defined");
					
						break;
					}
				}
				
			} else {
				
				
				for (SchoolInfo p : wPList) {
					if (p.getName().trim().equalsIgnoreCase(wHCDD.getName().trim())
							&&!p.getId().equals(wHCDD.getId())) {
						
						pErrors.rejectValue("name", "name.duplicate",
								"This School has already been defined"
										);
					
						break;
					}
					else if(p.getCodeName().trim().equalsIgnoreCase(wHCDD.getCodeName().trim())
							&&!p.getId().equals(wHCDD.getId())) {
						pErrors.rejectValue("codeName", "name.duplicate",
								"This School Code Name has already been defined");
					
						break;
					}
				}
				
			}
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return false;
	}

}
