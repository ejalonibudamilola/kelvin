package com.osm.gnl.ippms.ogsg.web.ui.controller;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.SelectedEntityPredicate;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLink;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLinkCategory;
import com.osm.gnl.ippms.ogsg.web.ui.validators.MenuLinkValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Controller
@RequestMapping({ "/editMenuLink.do", "/addMenuLinksContent.do" })
@SessionAttributes("menuLinkBean")
public class EditMenuLinkController extends BaseController {

	@Autowired
	private IMenuService menuService;

	@Autowired
	private MenuLinkValidator validator;

	public EditMenuLinkController() {
	}

	@ModelAttribute("menuList")
	public List<MenuLink> getMenuLinkList(HttpServletRequest request) {
		return this.menuService.getAllMenuLinks(getBusinessCertificate(request), false);
	}

 	@ModelAttribute("categoryList")
	public List<MenuLinkCategory> getMenuLinkCategoryList() {
		return  this.genericService.loadAllObjectsWithoutRestrictions(MenuLinkCategory.class,"name");
 	}
	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(HttpServletRequest request, Model model) {

		BusinessCertificate bc = getBusinessCertificate(request);

		 MenuLink menuLink = new MenuLink();


		Map<Long, MenuLinkCategory> dbCategoriesMap = this.menuService.getMenuLinkCategoriesForDashboardTabsAsMap(bc);

		Collection<MenuLinkCategory> menuLinksCategoryCol = dbCategoriesMap.values();

		SortedSet<MenuLinkCategory> menuLinksCategorySet = new TreeSet<>(
				Comparator.comparing(MenuLinkCategory::getName));

		menuLinksCategorySet.addAll(menuLinksCategoryCol);

		menuLink.setTabMenuCategories(menuLinksCategorySet);



		return prepareAndDisplayForm(model, menuLink, bc);
	}

	@RequestMapping(method = RequestMethod.GET, params={"linkId"})
	public String setupForm(HttpServletRequest request, Model model,
			@RequestParam(value = "linkId") Long id) {

		BusinessCertificate bc = getBusinessCertificate(request);

		MenuLink menuLink = this.menuService.getMenuLinkById(bc, id);

		if (menuLink == null) {
			return "redirect:editMenuLink.do";
		}else{
			menuLink.setSystemUserBind(menuLink.isSysUser());
			menuLink.setInnerLinkBind(menuLink.isInnerLink());
		}

		Map<Long, MenuLinkCategory> dbCategoriesMap = this.menuService.getMenuLinkCategoriesForDashboardTabsAsMap(bc);


			menuLink.setEditMode(true);
			menuLink.setOldName(menuLink.getName());

			// If it is a dashboard link then it might have tab categories
			if (menuLink.isDashboardMenuLink()) {
				List<Long> tabCategoryIds = this.menuService.getTabMenuLinkCategoriesForMenu(bc, menuLink.getId());

				if (CollectionUtils.isNotEmpty(tabCategoryIds)) {
					for (Long catId : tabCategoryIds) {
						MenuLinkCategory category = dbCategoriesMap.get(catId);

						assert category != null : "[MenuLinkCategory] not found for id --> " + catId;

						category.setSelected(true);
					}
				}
			}


		Collection<MenuLinkCategory> menuLinksCategoryCol = dbCategoriesMap.values();

		SortedSet<MenuLinkCategory> menuLinksCategorySet = new TreeSet<>(
				Comparator.comparing(MenuLinkCategory::getName));

		menuLinksCategorySet.addAll(menuLinksCategoryCol);
		menuLink.setDisplayOnDashboardBind(menuLink.isDisplayOnDashboard());
		menuLink.setTabMenuCategories(menuLinksCategorySet);

		return prepareAndDisplayForm(model, menuLink, bc);
	}

 	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute("menuLinkBean") MenuLink menuLink, BindingResult result,
			@RequestParam(value = "_cancel", required = false) String cancel, HttpServletRequest request, Model model,
			SessionStatus status, RedirectAttributes redirectAttr) throws Exception {

		if (StringUtils.isBlank(cancel)) {
			this.validator.validate(menuLink, result);

			if (result.hasErrors()) {
				return prepareAndDisplayForm(model, menuLink, getBusinessCertificate(request));
			}

			if (!menuLink.isHasParentLink()) {
				menuLink.setParentMenuLink(null);
			}
            if(menuLink.isSystemUserBind()){
            	menuLink.setSystemUser(ON);
			}
            if(menuLink.isInnerLinkBind()){
            	menuLink.setInnerLinkInd(ON);
			}
            if(menuLink.isDisplayOnDashboardBind()){
            	menuLink.setDisplayOnDb(ON);
			}else{
            	menuLink.setDisplayOnDb(OFF);
			}
			if (menuLink.isDashboardMenuLink()) {
				menuLink.setDashboardMenuLinkInd(ON);
				menuLink.setTabMenuCategories(new HashSet<MenuLinkCategory>(
						CollectionUtils.select(menuLink.getTabMenuCategories(), new SelectedEntityPredicate())));
			} else {
				menuLink.setDashboardMenuLinkInd(OFF);
				menuLink.setTabMenuCategories(Collections.EMPTY_SET);
			}

			this.menuService.saveMenuLink(getBusinessCertificate(request), menuLink);

			addSaveMsgToFlashMap(redirectAttr,
					getText(menuLink.isEditMode() ? "menuLink.form.success.edit" : "menuLink.form.success.create"));
		}

		//status.setComplete();
		return "redirect:/viewMenuLinks.do";
	}

	private String prepareAndDisplayForm(Model model, MenuLink menuLink, BusinessCertificate bc) {
		addPageTitle(model,
				getText(menuLink.isEditMode() ? "menuLink.form.edit.pageTitle" : "menuLink.form.create.pageTitle"));

		addMainHeader(model,
				menuLink.isEditMode() ? getText("menuLink.form.edit.mainHeader", new Object[] { menuLink.getOldName() })
						: getText("menuLink.form.create.mainHeader"));

		model.addAttribute("roleBean",bc);
		model.addAttribute("menuLinkBean", menuLink);

		return "menu/editMenuLink";
	}
}
