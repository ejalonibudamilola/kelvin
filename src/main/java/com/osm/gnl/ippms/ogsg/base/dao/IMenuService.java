/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.dao;



import com.osm.gnl.ippms.ogsg.auth.domain.Role;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.menu.domain.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface IMenuService {

	/**
	 * Gets the menu categories for a user that are
	 * not tied to specific dashboards
	 * 
	 * @param bc
	 * @param long1
	 * @return
	 * @see MenuLinkCategory#()
	 * @see MenuLink#isDashboardMenuLink()
	 */
    List<MenuLinkCategory> getMenuLinkCategoriesForUser(BusinessCertificate bc, Long long1);

    List<MenuLink> getAllMenuLinks(BusinessCertificate bc, boolean restrictToUser);

    Map<Long, MenuLink> getAllMenuLinksAsMap(BusinessCertificate bc, boolean restrictToUser);

    List<MenuLinkCategory> getMenuLinkCategories(BusinessCertificate bc, int startIndex, int resultSize, String sortField, boolean ascending);

    long getTotalNumberOfMenuLinkCategories(BusinessCertificate bc);

    MenuLinkCategory getMenuLinkCategoryById(BusinessCertificate bc, Long id);

    boolean canMenuLinkCategoryUseName(BusinessCertificate bc, String name, Long categoryId);

    List<MenuLink> getMenuLinks(BusinessCertificate bc, int startIndex, int resultSize, String sortCriterion, boolean ascending);

    long getTotalNumberOfMenuLinks(BusinessCertificate bc);

    boolean canMenuLinkUseName(BusinessCertificate bc, String name, Long id);

    Long getMenuCategoryIdForMenu(BusinessCertificate bc, Long menuId);

    void saveMenuLink(BusinessCertificate bc, MenuLink menuLink);

    RoleMenuMaster getRoleMenuMasterForRole(BusinessCertificate bc, Long roleId);

    List<Long> getMenuLinkIdsForRole(BusinessCertificate bc, Long roleId);

    boolean doesRoleHaveMenuMaster(BusinessCertificate bc, Long roleId);

    Long getRoleMenuMasterIdForRole(BusinessCertificate bc, Long roleId);

    List<RoleMenuMaster> getRoleMenuMasters(BusinessCertificate bc, int startIndex, int resultSize, String sortCriterion, boolean ascending);

    long getTotalNumberOfRoleMenuMasters(BusinessCertificate bc);

    List<Long> getMenuLinkIdsForUser(BusinessClient bc, Long pLoginId);

    UserMenu getUserMenuForUser(BusinessClient bc, Long pLoginId);

    List<MenuLink> getRootMenuLinksForMenuCategory(BusinessCertificate bc, Long menuCatId, Long userId);

    List<MenuLink> getChildMenuLinksForMenu(BusinessCertificate bc, Long menuId, Long userId);

    List<MenuLink> getMenuLinksDisplayableOnDashboardForCategory(BusinessCertificate bc, Long categoryId, Long userId);

    List<Role> getRolesWithoutMenuLinks(BusinessCertificate businessCertificate);

    List<MenuLinkCategory> getMenuLinkCategoryForDashboardTabs(BusinessCertificate bc);

    Map<Long, MenuLinkCategory> getMenuLinkCategoriesForDashboardTabsAsMap(BusinessCertificate bc);
    
    /**
     * This method gets a {@link MenuLink} by it's id without populating
     * it's {@link MenuLink#getTabMenuCategories() TabMenuCategories}.
     * 
     * @param bc the user
     * @param id the id of the {@link MenuLink MenuLink} to get.
     * @return the {@link MenuLink MenuLink} or a new one if none exists.
     * Never {@code null}.
     */
    MenuLink getMenuLinkById(BusinessCertificate bc, Long id);

    List<Long> getTabMenuLinkCategoriesForMenu(BusinessCertificate bc, Long id);

    /**
     * This will check the URL to see if it's for a dashboard page and then
     * load menu info e.g menu categories, etc for the URL
     * 
     * @param bc
     * @param requestURL
     * @return
     */
	MenuLinksWrapper getMenuInfoForDashboard(BusinessCertificate bc, Long userId, String requestURL);
	
	List<Long> getMenuLinkIdsForRoleAndUser(BusinessCertificate businessCertificate, Long roleId, Long userId);
	
	boolean canUserAccessURL(BusinessCertificate bc, String urlWithoutParams, String fullURL);

	/**
	 * @reason Gets all the hidden menu links for a particular parent menu link
	 * @param bc
	 * @param wParentMenuId
	 * @param wHidden
	 * @return
	 */
	List<MenuLink> getAllHiddenMenuLinksForParent(BusinessCertificate bc,
			Long wParentMenuId, Boolean wHidden);

	void persist(Serializable entity);

    List<MenuLink> treatMenuLinks(List<MenuLink> pMenuToTreat, BusinessCertificate bc);

    boolean isExcludedUrl(BusinessCertificate bc,String urlWithoutParams);
}
