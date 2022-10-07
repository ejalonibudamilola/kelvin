package com.osm.gnl.ippms.ogsg.web.ui.validators;

import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLink;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;



@Component
public class MenuLinkValidator implements Validator {
	
	@Autowired
	private IMenuService menuService;

	@Override
	public boolean supports(Class<?> clazz) {
		return MenuLink.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "menuLink.form.error.nameRequired", "Menu Name is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "menuLink.form.error.descRequired", "Menu Description is required");
		
		MenuLink menuLink = (MenuLink) target;
		
		//If Menu does not have a parent then it must explicitly specify a category
		if (!menuLink.isHasParentLink()) {
			if (menuLink.getMenuLinkCategory() == null || IppmsUtils.isNullOrLessThanOne(menuLink.getMenuLinkCategory().getId())) {
				errors.rejectValue("menuLinkCategory.id", "menuLink.form.error.selectCategory", "Select a Category for the Menu");
			}
		}
		else {
			//A menu cannot be a parent to itself
			if (menuLink.isEditMode() && menuLink.getParentMenuLink().getId().equals(menuLink.getId())) {
				errors.rejectValue("parentMenuLink.id", "menuLink.form.error.menuCannotBeParentToItself", "A Menu cannot be a parent to itself");
			}
		}
		
		if (!errors.hasFieldErrors("name") && (!this.menuService.canMenuLinkUseName(null, menuLink.getName(), menuLink.getId()))) {
			errors.rejectValue("name", "menuLink.form.error.nameinUse", "Menu Name entered is already in use");
		}
	}

}
