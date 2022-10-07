package com.osm.gnl.ippms.ogsg.web.ui.controller;

import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLinkCategory;
import com.osm.gnl.ippms.ogsg.web.ui.validators.MenuLinkCategoryValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;


@Controller
@RequestMapping("/editMenuLinkCategory.do")
@SessionAttributes("menuLinkCategoryBean")
public class EditMenuLinkCategoryController extends BaseController {
	
	@Autowired
	private IMenuService menuService;
	
	@Autowired
	private MenuLinkCategoryValidator validator;
	
	public EditMenuLinkCategoryController() {
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(HttpServletRequest request, 
			@RequestParam(value = "catId", required = false) Long id, Model model) {
		
		BusinessCertificate bc = getBusinessCertificate(request);
		
		MenuLinkCategory category = this.menuService.getMenuLinkCategoryById(bc, id);
		
		if (category.isNotNewEntity()) {
			category.setEditMode(true);
			category.setOldName(category.getName());
		}else{
			category.setDisplayOnlyOnDbTabsBind(category.getDisplayOnlyOnDbTabs() == 1);
		}
		
		model.addAttribute("menuLinkCategoryBean", category);

		return prepareAndDisplayForm(model, category,bc);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(
			@ModelAttribute("menuLinkCategoryBean") MenuLinkCategory category,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			@RequestParam(value = "_cancel", required = false) String cancel,
			SessionStatus status,
			RedirectAttributes redirectAttr) throws Exception {
		
		if (StringUtils.isBlank(cancel)) {
			this.validator.validate(category, result);
			
			if (result.hasErrors()) {
				return prepareAndDisplayForm(model, category,getBusinessCertificate(request));
			}
			
			BusinessCertificate bc = getBusinessCertificate(request);

			if (category.isNewEntity()) {
				category.setCreatedBy(bc.getUserName());
				category.setTimeCreated(LocalDate.now());
			}
			if(category.isDisplayOnlyOnDbTabsBind()){
				category.setDisplayOnlyOnDbTabs(ON);
			}else{
				category.setDisplayOnlyOnDbTabs(OFF);
			}
			category.setLastModBy(bc.getUserName());
			category.setLastModTs(LocalDate.now());
			
			this.menuService.persist(category);
			
			addSaveMsgToFlashMap(redirectAttr, getText(category.isEditMode() ? "menuLinkCat.form.success.edit" : "menuLinkCat.form.success.create"));
		}
		
		status.setComplete();
		return "redirect:/viewMenuLinkCategories.do";
	}

	private String prepareAndDisplayForm(Model model, MenuLinkCategory category, BusinessCertificate bc) {
		addPageTitle(model, getText(category.isEditMode() ? "menuLinkCat.form.edit.pageTitle" : "menuLinkCat.form.create.pageTitle"));
		
		addMainHeader(model, category.isEditMode() ? getText("menuLinkCat.form.edit.mainHeader", new Object[] { category.getOldName() }) :
			getText("menuLinkCat.form.create.mainHeader"));
		model.addAttribute("roleBean", bc);
		return "menu/editMenuLinkCategory";
	}
}
