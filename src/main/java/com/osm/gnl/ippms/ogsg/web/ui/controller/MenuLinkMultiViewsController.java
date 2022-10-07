package com.osm.gnl.ippms.ogsg.web.ui.controller;

import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLink;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLinkCategory;
import com.osm.gnl.ippms.ogsg.menu.domain.RoleMenuMaster;
import com.osm.gnl.ippms.ogsg.web.ui.model.DisplayTagTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class MenuLinkMultiViewsController extends BaseController {
	
	@Autowired
	private IMenuService menuService;
 
	@RequestMapping(value = "/viewMenuLinkCategories.do", method = RequestMethod.GET)
	public String displayMenuLinkCategoriesPage(HttpServletRequest request, Model model) {
		BusinessCertificate bc = getBusinessCertificate(request);

		PaginationBean paginationBean = this.getPaginationInfo(request);
		

	    List<MenuLinkCategory> list = this.menuService.getMenuLinkCategories(bc, (paginationBean.getPageNumber() - 1) * 20, 20, paginationBean.getSortCriterion(),
				paginationBean.getSortOrder().equalsIgnoreCase("asc"));
	    long totalCount = this.menuService.getTotalNumberOfMenuLinkCategories(bc);
	    
	    DisplayTagTableModel tableModel = new DisplayTagTableModel(list, paginationBean.getPageNumber(), 20, (int) totalCount, paginationBean.getSortCriterion(),
				paginationBean.getSortOrder(), true);
	    
	    model.addAttribute("tableModel", tableModel);
		addRoleBeanToModel(model, request);
	    addPageTitle(model, getText("menuLinkCat.view.pageTitle"));
	    addMainHeader(model, getText("menuLinkCat.view.mainHeader"));
		
		return "menu/viewMenuLinkCategories";
	}
	
	@RequestMapping(value = "/viewMenuLinks.do", method = RequestMethod.GET)
	public String displayMenuLinksPage(HttpServletRequest request, Model model) {
		BusinessCertificate bc = getBusinessCertificate(request);

		PaginationBean paginationBean = this.getPaginationInfo(request);
	    List<MenuLink> list = this.menuService.getMenuLinks(bc, (paginationBean.getPageNumber() - 1) * 500, 500, paginationBean.getSortCriterion(),
				paginationBean.getSortOrder().equalsIgnoreCase("asc"));
	    
	    long totalCount = this.menuService.getTotalNumberOfMenuLinks(bc);
	    
	    DisplayTagTableModel tableModel = new DisplayTagTableModel(list, paginationBean.getPageNumber(), 500, (int) totalCount,
				paginationBean.getSortCriterion(),
				paginationBean.getSortOrder(), true);
	    
	    model.addAttribute("tableModel", tableModel);
	    addRoleBeanToModel(model, request);
	    addPageTitle(model, getText("menuLink.view.pageTitle"));
	    addMainHeader(model, getText("menuLink.view.mainHeader"));
		
		return "menu/viewMenuLinks";
	}
	
	@RequestMapping(value = "/viewRoleMenuLinks.do", method = RequestMethod.GET)
	public String displayRoleMenuLinksPage(HttpServletRequest request, Model model) {
		BusinessCertificate bc = getBusinessCertificate(request);

		PaginationBean paginationBean = this.getPaginationInfo(request);
	    
	    List<RoleMenuMaster> list = this.menuService.getRoleMenuMasters(bc, (paginationBean.getPageNumber() - 1) * 20, 20, paginationBean.getSortCriterion(),
				paginationBean.getSortOrder().equalsIgnoreCase("asc"));
	    
	    long totalCount = this.menuService.getTotalNumberOfRoleMenuMasters(bc);
	    
	    DisplayTagTableModel tableModel = new DisplayTagTableModel(list, paginationBean.getPageNumber(), 20, (int) totalCount,
				paginationBean.getSortCriterion(), paginationBean.getSortOrder(), true);

	    tableModel.setCanViewMenuLinks(menuService.canUserAccessURL(bc,"/viewMenuLinks.do","/viewMenuLinks.do"));
	    tableModel.setCanViewMenuLinkCategories(menuService.canUserAccessURL(bc,"/viewMenuLinkCategories.do","/viewMenuLinkCategories.do"));
	    
	    model.addAttribute("tableModel", tableModel);
		addRoleBeanToModel(model, request);
	    addPageTitle(model, getText("roleMenuLink.pageTitle"));
	    addMainHeader(model, getText("roleMenuLink.mainHeader"));
		
		return "menu/viewRoleMenuLinks";
	}
	
}
