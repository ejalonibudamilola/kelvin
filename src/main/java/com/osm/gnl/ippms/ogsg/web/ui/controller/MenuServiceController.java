package com.osm.gnl.ippms.ogsg.web.ui.controller;

import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class MenuServiceController extends BaseController {
	
	@Autowired
	private IMenuService menuService;

	@RequestMapping(value = "/menu/getMenuCategoryByMenuId.do", method = RequestMethod.GET)
	public @ResponseBody Long getMenuLinkCategoryIdByMenuId(HttpServletRequest request, @RequestParam("menuId") Long id) {
		return this.menuService.getMenuCategoryIdForMenu(getBusinessCertificate(request), id);
	}
	
	
	@RequestMapping(value = "/menu/getMenuLinkIdsForRole.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<Long, Boolean> getMenuLinkIdsForRole(HttpServletRequest request, @RequestParam("roleId") Long roleId) {
		List<Long> ids = this.menuService.getMenuLinkIdsForRole(getBusinessCertificate(request), roleId);
		if (ids != null && ids.size() > 0) {
			Map<Long, Boolean> map = new HashMap<Long, Boolean>(ids.size());
			for (Long id : ids) {
				map.put(id, Boolean.TRUE);
			}
			
			return map;
		}
		
		return Collections.EMPTY_MAP;
	}
	
	@RequestMapping(value = "/menu/getMenuLinkIdsForRoleAndOrUser.do", method = RequestMethod.GET)
	@ResponseBody
	public List<Long> getMenuLinkIdsForRoleAndUser(HttpServletRequest request, 
			@RequestParam("roleId") Long roleId, 
			@RequestParam(value = "userId", required = false) Long userId) {
		

		return this.menuService.getMenuLinkIdsForRoleAndUser(getBusinessCertificate(request), roleId, null);

	}
	
	@RequestMapping(value = "/menu/getRootMenuLinksForMenuCategory.do", method = RequestMethod.GET)
	@ResponseBody
	public List<MenuLink> getRootMenuLinkForCategory(HttpServletRequest request, @RequestParam("catId") Long categoryId) {
		BusinessCertificate bc = getBusinessCertificate(request);
		return this.menuService.getRootMenuLinksForMenuCategory(bc, categoryId, bc.getLoginId());
	}
	
	@RequestMapping(value = "/menu/getChildMenuLinksForMenu.do", method = RequestMethod.GET)
	@ResponseBody
	public List<MenuLink> getRootMenuLinkForMenu(HttpServletRequest request, @RequestParam("menuId") Long id) {
		BusinessCertificate bc = getBusinessCertificate(request);
		return this.menuService.getChildMenuLinksForMenu(bc, id, bc.getLoginId());
	}
	
	@RequestMapping(value = "/menu/getMenuLinksForCategoryForDashboard.do", method = RequestMethod.GET)
	@ResponseBody
	public List<MenuLink> getMenusLinksForCategoryTabForDb(HttpServletRequest request, @RequestParam("catId") Long id) {
		BusinessCertificate bc = getBusinessCertificate(request);
		return this.menuService.getMenuLinksDisplayableOnDashboardForCategory(bc, id, bc.getLoginId());
	}
}
