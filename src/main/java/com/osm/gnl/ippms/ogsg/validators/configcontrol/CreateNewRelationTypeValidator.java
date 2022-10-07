package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.RelationshipType;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class CreateNewRelationTypeValidator extends BaseValidator {

	public CreateNewRelationTypeValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return aClass.isAssignableFrom(RelationshipType.class);
	}

	@SneakyThrows
	public void validate(Object pTarget, Errors pErrors)
	  {
	    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Relationship Type Name is a required field");

	   
	    RelationshipType p = (RelationshipType)pTarget;
	   
	    RelationshipType _p = this.genericService.loadObjectWithSingleCondition(RelationshipType.class, CustomPredicate.procurePredicate("name", p.getName()));
	    
	 
	      if (!_p.isNewEntity()) {
	        
	          pErrors.rejectValue("", "Reason.Invalid", "A Relationship Type with this name already exists. Please change the 'Relationship Name'");

	        
	      }
	    

	    
	  }
	  public void validateForEdit(Object pTarget, Errors pErrors) throws InstantiationException, IllegalAccessException {
	    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Relationship Type Name is a required field");

	   
	    RelationshipType p = (RelationshipType)pTarget;
	   
	    RelationshipType _p = this.genericService.loadObjectWithSingleCondition(RelationshipType.class, CustomPredicate.procurePredicate( "name", p.getName()));
	    
	 
	      if (!_p.isNewEntity()) {
	        if (!p.getId().equals( _p.getId()))
	        {
	          pErrors.rejectValue("", "Reason.Invalid", "A Relationship Type with this name already exists. Please change the 'Relationship Name'");

	        }
	      }
	    

	    
	  }

}
