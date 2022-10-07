package com.osm.gnl.ippms.ogsg.web.ui.validators;

import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLinkCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class MenuLinkCategoryValidator extends I18nMessageProvider implements Validator {
	
	@Autowired
	private IMenuService menuService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return MenuLinkCategory.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "menuLinkCat.form.error.nameRequired", "Category Name is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "menuLinkCat.form.error.descRequired", "Category Description is required");
		
		if (!errors.hasErrors() || !errors.hasFieldErrors("name")) {
			//check for uniqueness of name
			MenuLinkCategory category = (MenuLinkCategory) target;
			
			if (!this.menuService.canMenuLinkCategoryUseName(null, category.getName(), category.getId())) {
				errors.rejectValue("name", "menuLinkCat.form.error.nameInUse", "The Category Name entered is already in use");
			}
		}
	}
}
