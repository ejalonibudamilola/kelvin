package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class ClientDeptValidator extends BaseValidator{


  public ClientDeptValidator(GenericService genericService) {
    super(genericService);
  }


  @Override
	public boolean supports(Class<?> clazz) {
		return Department.class.isAssignableFrom(clazz);
	}

  @Override
  public void validate(Object target, Errors errors) {

  }

public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws IllegalAccessException, InstantiationException {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Department Name is required");

    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", "Description is required");

     
    Department wHCDD  = (Department)pTarget;
    if (pErrors.getErrorCount() < 1) {
    	 
      List<Department> clientDefinedDept = this.genericService.loadAllObjectsWithSingleCondition(Department.class,CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()), null);
      if (!wHCDD.isEditMode())
      {
        for (Department h : clientDefinedDept) {
          if (h.getName().trim().equalsIgnoreCase(wHCDD.getName().trim())) {
            String wAddendum = "";
            if (h.isDeactivated())
              wAddendum = ", but is deactivated. You may choose to reactivate it.";
            else
              wAddendum = ".";
            pErrors.rejectValue("name", "name.duplicate", "This Department has already been defined" + wAddendum);

            break;
          }
        }
      }
      else
      {
        for (Department h : clientDefinedDept) {
          if ((h.getName().trim().equalsIgnoreCase(wHCDD.getName().trim())) && (!h.getId().equals(wHCDD.getId())))
          {
            String wAddendum = "";
            if (h.isDeactivated())
              wAddendum = ", but is deactivated. You may choose to reactivate it.";
            else
              wAddendum = ".";
            pErrors.rejectValue("name", "name.duplicate", "This Department has already been defined" + wAddendum);

            break;
          }

        }

       
      }
      if(wHCDD.isDefaultIndBind()){
        //Find out if any other Department is default.
        Department dept = genericService.loadObjectUsingRestriction(Department.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("defaultInd",1)));
        if(wHCDD.isEditMode()){
          if(!dept.isNewEntity() && !wHCDD.getId().equals(dept.getId()) ) {
            pErrors.rejectValue("defaultInd", "Invalid.Value", "Another Department " + dept.getName() + " is already designated as a 'Default Department'");
            return;
          }
        }else{
          if(!dept.isNewEntity() ) {
            pErrors.rejectValue("defaultInd", "Invalid.Value", "Another Department " + dept.getName() + " is already designated as a 'Default Department'");
            return;
          }
        }

      }
    }
  }
}