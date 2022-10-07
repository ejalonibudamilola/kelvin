package com.osm.gnl.ippms.ogsg.web.ui.validators;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.Role;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.menu.domain.RoleMenuMaster;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class RoleMenuLinkValidator extends I18nMessageProvider implements Validator {
	
	//@Autowired
	//private IMenuService menuService;
	
	@Autowired
	private GenericService genericService;

	@Override
	public boolean supports(Class<?> clazz) {
		return RoleMenuMaster.class.isAssignableFrom(clazz);
	}

	@SneakyThrows
	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role.description", "roleMenuLink.form.error.descRequired", 
				"A description is required for the Role Menus");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role.name", "roleMenuLink.form.error.nameRequired", 
		"A name is required for the Role Menus");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role.displayName", "roleMenuLink.form.error.codeNameRequired", 
		"Display Name is Required");
		
		RoleMenuMaster roleMenuMaster = (RoleMenuMaster) target;


		Role wRole = genericService.loadObjectWithSingleCondition(Role.class, CustomPredicate.procurePredicate("name", roleMenuMaster.getRole().getName()));

		Role wRole1 = genericService.loadObjectWithSingleCondition(Role.class, CustomPredicate.procurePredicate("displayName", roleMenuMaster.getRole().getDisplayName()));

		//NEW ENTRY
		//Validate for Existing Name or display name
		if(roleMenuMaster.getRole().isNewEntity()){
			
			//Check for name
			if(!wRole.isNewEntity()){
				errors.rejectValue("role.id", "The Role Name Entered Already Exists", "The Role Name Entered Already Exists");
			}
			
			//Check for display name
			if(!wRole1.isNewEntity()){
				errors.rejectValue("role.id", "The Display Name Entered Already Exists", "The Display Name Entered Already Exists");
			}
		}
		
		//EDIT EXISTING ENTRY
		else{
			//Check for name
			if(!wRole.isNewEntity() && wRole.getId().intValue() != roleMenuMaster.getRole().getId().intValue()){
				errors.rejectValue("role.id", "The Role Name Entered Already Exists", "The Role Name Entered Already Exists");
			}
			
			//Check for display name
			if(!wRole1.isNewEntity() && wRole1.getId().intValue() != roleMenuMaster.getRole().getId().intValue()){
				errors.rejectValue("role.id", "The Display Name Entered Already Exists", "The Display Name Entered Already Exists");
			}
		}
		

	}

}
