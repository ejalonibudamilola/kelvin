/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.repository;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.Role;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.menu.domain.*;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.*;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;



@Repository
@Transactional
public class MenuServiceImpl implements IMenuService {
	private final GenericService genericService;
	@Autowired
	public MenuServiceImpl(GenericService genericService) {
		this.genericService = genericService;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void persist(Serializable entity) {
		this.genericService.saveObject(entity);
	}
	
	public NativeQuery makeSQLQuery(String query)  {
		return this.genericService.getCurrentSession().createNativeQuery(query);
	}
	
	public Criteria makeCriteria( Class<?> pClazz )  {
		Criteria wCrit = null;
		if( pClazz != null ) {
			wCrit = genericService.getCurrentSession().createCriteria( pClazz );
		}
		
		return wCrit;
	}
	
	 
	public Query makeQuery( String pHqlQuery ) {
		Query wQuery = null;
		if( StringUtils.isNotEmpty( pHqlQuery ) ) {
			wQuery = genericService
							.getCurrentSession().createQuery(pHqlQuery);
		}
		
		return wQuery;
	}

	
	@Override
	public List<MenuLinkCategory> getMenuLinkCategoriesForUser(BusinessCertificate bc, Long userId) {
		
		final String queryStr = "SELECT DISTINCT c.menu_link_cat_inst_id AS catId, c.menu_link_cat_name AS catName, c.menu_link_cat_desc AS catDesc"
				+ " FROM ippms_menu_link_cat c WHERE c.disp_only_on_db_tabs = :dbTabsOnly AND c.menu_link_cat_inst_id IN "
				+ "(SELECT DISTINCT ml.menu_link_cat_inst_id FROM ippms_menu_link ml WHERE ml.menu_link_inst_id IN "
				+ "(SELECT DISTINCT uml.menu_link_inst_id FROM ippms_user_menu_links uml WHERE uml.user_menu_inst_id = "
				+ "(SELECT um.user_menu_inst_id FROM ippms_user_menu um WHERE um.user_inst_id = :userId)) and ml.privileged_ind <= :pPrivilegeInd)"
				+ "ORDER BY c.menu_link_cat_name ASC";
		
		List<Object[]> rows = makeSQLQuery(queryStr)
								.addScalar("catId", StandardBasicTypes.LONG)
								.addScalar("catName", StandardBasicTypes.STRING)
								.addScalar("catDesc", StandardBasicTypes.STRING)
								.setParameter("userId", userId)
								.setParameter("dbTabsOnly", IConstants.OFF)
								.setParameter("pPrivilegeInd", bc.getPrivilegedInd())
								.list();
		
		if (IppmsUtils.isNotNullOrEmpty(rows)) {
			List<MenuLinkCategory> list = new ArrayList<MenuLinkCategory>(rows.size());
			for (Object[] row : rows) {
				list.add(new MenuLinkCategory((Long) row[0], (String) row[1], (String) row[2]));
			}
			
			return list;
		}
		
		return Collections.EMPTY_LIST;
	}
	
	@Override
	
	public List<MenuLink> getRootMenuLinksForMenuCategory(BusinessCertificate bc, Long menuCatId, Long userId) {

		
		final String query = "SELECT * FROM  "
								 //select links with child links. User would not have direct access to some of the links
								 //returned here. This is because some of those links have children that the user has
								 //direct access to.
						         + "(SELECT DISTINCT ml.menu_link_inst_id AS menuId, ml.menu_link_name AS menuName, ml.menu_link_desc AS menuDesc, "
						         + "ml.menu_link_url AS menuUrl, ml.menu_link_cat_inst_id AS menuCatId, pmlc.child_count AS childCount, "
						         + "pmlc.direct_access_ind AS directAccessInd, ml.IS_DB_LINK AS dashboardLink "
						         + "FROM ippms_menu_link ml, ippms_menu_link_user_child_cnt pmlc, ippms_business_client_menu_link bcml "
						         + "WHERE ml.menu_link_inst_id = pmlc.menu_link_inst_id AND ml.menu_link_cat_inst_id = :categoryId "
						         + "AND pmlc.user_inst_id = :userId AND ml.parent_menu_link_inst_id IS NULL and ml.is_inner_link = 0  "
								+ "AND bcml.business_client_inst_id = :pBizId AND bcml.menu_link_inst_id = ml.menu_link_inst_id and ml.privileged_ind <= :pVar "
						         + "UNION "
						         //select those without child links but that user has direct access to
						         + "SELECT DISTINCT ml.menu_link_inst_id AS menuId, ml.menu_link_name AS menuName, ml.menu_link_desc AS menuDesc, "
						         + "ml.menu_link_url AS menuUrl, ml.menu_link_cat_inst_id AS menuCatId, 0 AS childCount, 1 AS directAccessInd, "
						         + "ml.IS_DB_LINK AS dashboardLink "
						         + "FROM ippms_menu_link ml, ippms_user_menu_links uml, ippms_user_menu um  "
						         + "WHERE ml.privileged_ind <= :pVar and "
						         + "ml.menu_link_inst_id NOT IN ( "
						         	+ "SELECT DISTINCT pmlc.menu_link_inst_id FROM ippms_menu_link_user_child_cnt pmlc WHERE pmlc.user_inst_id = :userId"
						         + ")"
						         + "AND ml.menu_link_inst_id IN ( "
						         	+ " SELECT DISTINCT uml.menu_link_inst_id FROM ippms_user_menu_links uml, ippms_user_menu um "
						         	+ " WHERE uml.user_menu_inst_id = um.user_menu_inst_id AND um.user_inst_id = :userId "
						         + ") "
								+ "AND ml.menu_link_inst_id IN ( "
								+ " SELECT DISTINCT ibcml.menu_link_inst_id FROM ippms_business_client_menu_link ibcml  "
								+ " WHERE ibcml.business_client_inst_id = :pBizId "
								+ ") "
						         + "AND ml.menu_link_inst_id NOT IN ( SELECT DISTINCT rml.menu_link_inst_id FROM "+getViewName(bc)+" rml ) "
						         + "AND ml.menu_link_cat_inst_id = :categoryId AND ml.parent_menu_link_inst_id IS NULL and ml.is_inner_link = 0) AS foo "
				          + "ORDER BY menuName ASC ";
		
		List<Object[]> rows = makeSQLQuery(query)
								.addScalar("menuId", StandardBasicTypes.LONG)
								.addScalar("menuName", StandardBasicTypes.STRING)
								.addScalar("menuDesc", StandardBasicTypes.STRING)
								.addScalar("menuUrl", StandardBasicTypes.STRING)
								.addScalar("menuCatId", StandardBasicTypes.LONG)
								.addScalar("childCount", StandardBasicTypes.INTEGER)
								.addScalar("directAccessInd", StandardBasicTypes.INTEGER)
								.addScalar("dashboardLink", StandardBasicTypes.INTEGER)
								.setParameter("userId", userId)
								.setParameter("categoryId", menuCatId)
								.setParameter("pBizId", bc.getBusinessClientInstId())
								.setParameter("pVar",bc.getPrivilegedInd())
								.list();
		
		if (IppmsUtils.isNotNullOrEmpty(rows)) {
			List<MenuLink> retVal = new ArrayList<MenuLink>(rows.size());
			for (Object[] row : rows) {
				MenuLink menuLink = new MenuLink((Long) row[0]);
				menuLink.setName((String) row[1]);
				menuLink.setDescription((String) row[2]);
				menuLink.setLinkUrl((String) row[3]);
				menuLink.setMenuLinkCategory(new MenuLinkCategory((Long) row[4]));

				if (row[5] != null) {
					menuLink.setNumOfChildren((Integer) row[5]);
				}
				
				//Does the user have direct has to this link? If user doesn't then it because the
				//link has a child that the user has access to

				menuLink.setDirectAccess(((Integer) row[6]).intValue() == IConstants.ON);
				menuLink.setDashboardMenuLinkInd((Integer) row[7]);

				retVal.add(menuLink);
			}
			//Change Name and Description
			return treatMenuLinks(retVal,bc);
		}
		
		return Collections.EMPTY_LIST;
	}
	
	@Override
	
	public List<MenuLink> getChildMenuLinksForMenu(BusinessCertificate bc, Long menuId, Long userId) {

		
		final String query = "SELECT * FROM ( "
				 //select links with child links. User would not have direct access to some of the links
				 //returned here. This is because some of those links have children that the user has
				 //direct access to.
		         + "SELECT DISTINCT ml.menu_link_inst_id AS menuId, ml.menu_link_name AS menuName, ml.menu_link_desc AS menuDesc, "
		         + "ml.menu_link_url AS menuUrl, ml.menu_link_cat_inst_id AS menuCatId, pmlc.child_count AS childCount, "
		         + "pmlc.direct_access_ind AS directAccessInd, ml.IS_DB_LINK AS dashboardLink  "
		         + "FROM ippms_menu_link ml, ippms_menu_link_user_child_cnt pmlc,ippms_business_client_menu_link ibcml   "
		         + "WHERE ml.menu_link_inst_id = pmlc.menu_link_inst_id and ml.menu_link_inst_id = ibcml.menu_link_inst_id "
		         + "AND pmlc.user_inst_id = :userId AND ml.parent_menu_link_inst_id = :parentId and ml.is_inner_link = :innerLinkInd" +
				" and ibcml.business_client_inst_id = :pBizId and ml.privileged_ind <= :pPrivilegedVar "
		         + "UNION "
		         //select those without child links but that user has direct access to
		         + "SELECT DISTINCT ml.menu_link_inst_id AS menuId, ml.menu_link_name AS menuName, ml.menu_link_desc AS menuDesc, "
		         + "ml.menu_link_url AS menuUrl, ml.menu_link_cat_inst_id AS menuCatId, 0 AS childCount, 1 AS directAccessInd, ml.IS_DB_LINK AS dashboardLink "
		         + "FROM ippms_menu_link ml "
		         + "WHERE ml.menu_link_inst_id NOT IN ("
		         	+ "SELECT DISTINCT pmlc.menu_link_inst_id FROM ippms_menu_link_user_child_cnt pmlc WHERE pmlc.user_inst_id = :userId "
		         	+ "AND pmlc.parent_menu_link_inst_id = :parentId "
		         + ") "
		         + "AND ml.menu_link_inst_id IN ("
		         	+ "SELECT DISTINCT uml.menu_link_inst_id FROM ippms_user_menu_links uml, ippms_user_menu um "
		         	+ "WHERE uml.user_menu_inst_id = um.user_menu_inst_id AND um.user_inst_id = :userId "
		         + ") "
				+ "AND ml.menu_link_inst_id IN ( "
				+ "SELECT DISTINCT ibcml.menu_link_inst_id FROM ippms_business_client_menu_link ibcml  "
				+ "WHERE ibcml.business_client_inst_id = :pBizId "
				+ ") "
		         + "AND ml.menu_link_inst_id NOT IN ( SELECT DISTINCT rml.menu_link_inst_id FROM "+getViewName(bc)+" rml ) "
		         + "AND ml.parent_menu_link_inst_id = :parentId and ml.is_inner_link = :innerLinkInd "
		         //Select Menu Links That are categories tied to the dashboard link
		         + "UNION "
		         + "SELECT DISTINCT 0 AS menuId, mlc.menu_link_cat_name AS menuName, mlc.menu_link_cat_desc AS menuDesc, "
		         + "null AS menuUrl, mlc.menu_link_cat_inst_id AS menuCatId, 1 AS childCount, 1 AS directAccessInd, 0 as dashboardLink "
		         + "FROM ippms_menu_link ml, ippms_menu_link_cat mlc, ippms_menu_link_db_cats mldc, ippms_menu_link mll "
		         + "WHERE mldc.menu_link_inst_id = ml.menu_link_inst_id AND mldc.menu_link_cat_inst_id = mlc.menu_link_cat_inst_id "
		         + "AND mldc.menu_link_inst_id = :parentId AND mll.menu_link_cat_inst_id = mlc.menu_link_cat_inst_id) AS foo"
          + " "
          + "ORDER BY menuName ASC ";
		
		
		List<Object[]> rows = makeSQLQuery(query)
								.addScalar("menuId", StandardBasicTypes.LONG)
								.addScalar("menuName", StandardBasicTypes.STRING)
								.addScalar("menuDesc", StandardBasicTypes.STRING)
								.addScalar("menuUrl", StandardBasicTypes.STRING)
								.addScalar("menuCatId", StandardBasicTypes.LONG)
								.addScalar("childCount", StandardBasicTypes.INTEGER)
								.addScalar("directAccessInd", StandardBasicTypes.INTEGER)
								.addScalar("dashboardLink", StandardBasicTypes.INTEGER)
								.setParameter("userId", userId)
								.setParameter("innerLinkInd", IConstants.OFF)
								.setParameter("parentId", menuId)
				                .setParameter("pBizId", bc.getBusinessClientInstId())
								.setParameter("pPrivilegedVar",bc.getPrivilegedInd())
								.list();
		
		if (IppmsUtils.isNotNullOrEmpty(rows)) {
			List<MenuLink> retVal = new ArrayList<MenuLink>(rows.size());
			for (Object[] row : rows) {
				MenuLink menuLink = new MenuLink((Long) row[0]);
				menuLink.setName((String) row[1]);
				menuLink.setDescription((String) row[2]);
				menuLink.setLinkUrl((String) row[3]);
				menuLink.setMenuLinkCategory(new MenuLinkCategory((Long) row[4]));
				
				if (row[5] != null) {
					menuLink.setNumOfChildren((Integer) row[5]);
				}
				
				//Does the user have direct has to this link? If user doesn't then it because the
				//link has a child that the user has access to
				menuLink.setDirectAccess(((Integer) row[6]).intValue() == IConstants.ON);
				menuLink.setDashboardMenuLinkInd((Integer) row[7]);

				retVal.add(menuLink);
			}
			//Leave
			return treatMenuLinks(retVal,bc);
		}
		
		return Collections.EMPTY_LIST;
	}
	
	@Override
	
	public List<MenuLink> getMenuLinksDisplayableOnDashboardForCategory(BusinessCertificate bc, Long categoryId, Long userId) {

		final String query = "SELECT DISTINCT ml.menu_link_inst_id AS menuId, ml.menu_link_name AS menuName, ml.menu_link_desc AS menuDesc, "
				+ "ml.menu_link_url AS menuUrl, ml.menu_link_cat_inst_id AS menuCatId "
				+ "FROM ippms_menu_link ml, ippms_business_client_menu_link ibcml WHERE ibcml.menu_link_inst_id = ml.menu_link_inst_id " +
				"AND ibcml.business_client_inst_id = :pBizId  AND (ml.menu_link_inst_id IN "
				+ "(SELECT DISTINCT uml.menu_link_inst_id FROM ippms_user_menu_links uml WHERE uml.user_menu_inst_id = "
				+ "(SELECT um.user_menu_inst_id FROM ippms_user_menu um WHERE um.user_inst_id = :userId))) "
				+ "AND ml.menu_link_inst_id NOT IN (SELECT DISTINCT rml.menu_link_inst_id from "+getViewName(bc)+" rml) "
				+ "AND ml.menu_link_cat_inst_id = :categoryId AND ml.menu_link_url IS NOT NULL AND ml.display_on_db = :displayOnDbInd and ml.IS_INNER_LINK = :hiddenLinkInd "
				+ "ORDER BY ml.menu_link_name ASC";
		
		List<Object[]> rows = makeSQLQuery(query)
								.addScalar("menuId", StandardBasicTypes.LONG)
								.addScalar("menuName", StandardBasicTypes.STRING)
								.addScalar("menuDesc", StandardBasicTypes.STRING)
								.addScalar("menuUrl", StandardBasicTypes.STRING)
								.addScalar("menuCatId", StandardBasicTypes.LONG)
								.setParameter("userId", userId)
								.setParameter("categoryId", categoryId)
								.setParameter("displayOnDbInd", IConstants.ON)
								.setParameter("hiddenLinkInd", IConstants.OFF)
								.setParameter("pBizId", bc.getBusinessClientInstId())
								.list();

		if (IppmsUtils.isNotNullOrEmpty(rows)) {
			List<MenuLink> retVal = new ArrayList<MenuLink>(rows.size());
			for (Object[] row : rows) {
				MenuLink menuLink = new MenuLink((Long) row[0]);
				menuLink.setName((String) row[1]);
				menuLink.setDescription((String) row[2]);
				menuLink.setLinkUrl((String) row[3]);
				
				retVal.add(menuLink);
			}
			//Change Menu Name and Description if needed
			return treatMenuLinks(retVal,bc);
		}

		return Collections.EMPTY_LIST;
	}



	@Override
	public List<MenuLink> getAllMenuLinks(BusinessCertificate bc, boolean restrictToUser) {
		Criteria crit = makeCriteria(MenuLink.class);
		crit.add(Restrictions.eq("innerLinkInd", IConstants.OFF));
		if(!bc.isPrivilegedUser())
			crit.add(Restrictions.eq("privilegedInd",0));

		crit.addOrder(Order.asc("name"));
		
		if (!bc.isSysUser()) {
			crit.add(Restrictions.eq("systemUser", IConstants.OFF));
		}
		
		if(restrictToUser){
			crit.add(Restrictions.in("id", this.getMenuLinkIdsForUser(null, bc.getLoginId())));
		}else{
			crit.add(Restrictions.in("id",this.getMenuLinkIdsForBusinessClient(bc)));
		}

		
		return  (crit.list() );
	}
	
	
	@Override
	public Map<Long, MenuLink> getAllMenuLinksAsMap(BusinessCertificate bc, boolean restrictToUser) {
		List<MenuLink> links = getAllMenuLinks(bc, restrictToUser);
		
		if (IppmsUtils.isNotNullOrEmpty(links)) {
			Map<Long, MenuLink> map = new HashMap<Long, MenuLink>(links.size());
			for (MenuLink link : links) {
				map.put(link.getId(), link);
			}
			
			return map;
		}
		
		return Collections.EMPTY_MAP;
	}

	
	@Override
	public List<MenuLinkCategory> getMenuLinkCategories(BusinessCertificate bc, int startIndex, int resultSize, String sortField, boolean ascending) {
		Criteria crit = makeCriteria(MenuLinkCategory.class).setFirstResult(startIndex);
		
		if (resultSize > 0) {
			crit.setMaxResults(resultSize);
		}
		
		if (IppmsUtils.isNotNullOrEmpty(sortField)) {
			crit.addOrder(ascending ? Order.asc(sortField) : Order.desc(sortField));
		}
		
		return crit.list();
	}

	@Override
	public long getTotalNumberOfMenuLinkCategories(BusinessCertificate bc) {
		return (Long) makeCriteria(MenuLinkCategory.class)
					.setProjection(Projections.rowCount())
					.uniqueResult();
	}

	@Override
	public MenuLinkCategory getMenuLinkCategoryById(BusinessCertificate bc, Long id) {
		MenuLinkCategory category = null;
		
		if (id != null) {
			category = (MenuLinkCategory) makeCriteria(MenuLinkCategory.class).add(Restrictions.idEq(id)).uniqueResult();
		}
		
		if (category == null) {
			category = new MenuLinkCategory();
		}
		
		return category;
	}
	
	@Override
	public boolean canMenuLinkCategoryUseName(BusinessCertificate bc, String name, Long categoryId) {
		Long ownerId = (Long) makeCriteria(MenuLinkCategory.class)
								.add(Restrictions.ilike("name", name, MatchMode.EXACT))
								.setProjection(Projections.distinct(Projections.id()))
								.uniqueResult();
		
		//new entity
		if (categoryId == null) {
			return ownerId == null;
		} 
		
		if (ownerId == null) {
			return true;
		}
		
		return ownerId.equals(categoryId);
	}

	
	@Override
	public List<MenuLink> getMenuLinks(BusinessCertificate bc, int startIndex, int resultSize, String sortField, boolean ascending) {
		Criteria crit = makeCriteria(MenuLink.class)
						.createAlias("parentMenuLink", "pm", CriteriaSpecification.LEFT_JOIN)
						.createAlias("menuLinkCategory", "mc", CriteriaSpecification.LEFT_JOIN)
						.setFirstResult(startIndex);
		
		if (resultSize > 0) {
			crit.setMaxResults(resultSize);
		}
		crit.add(Restrictions.in("id",this.getMenuLinkIdsForBusinessClient(bc)));
		if (IppmsUtils.isNotNullOrEmpty(sortField)) {
			if (sortField.startsWith("parentMenuLink")) {
				sortField = StringUtils.replace("parentMenuLink", sortField, "pm");
			}
			else if (sortField.startsWith("menuLinkCategory")) {
				sortField = StringUtils.replace("menuLinkCategory", sortField, "mc");
			}
			
			crit.addOrder(ascending ? Order.asc(sortField) : Order.desc(sortField));
		}
		
		return  crit.list() ;
	}

	@Override
	public long getTotalNumberOfMenuLinks(BusinessCertificate bc) {
		return (Long) makeCriteria(MenuLink.class)
					.setProjection(Projections.rowCount())
		            .add(Restrictions.in("id",this.getMenuLinkIdsForBusinessClient(bc)))
					.uniqueResult();
	}

	@Override
	public boolean canMenuLinkUseName(BusinessCertificate bc, String name, Long menuId) {
		Long ownerId = (Long) makeCriteria(MenuLink.class)
				.add(Restrictions.ilike("name", name, MatchMode.EXACT))
				.setProjection(Projections.distinct(Projections.id())).uniqueResult();

		// new entity
		if (menuId == null) {
			return ownerId == null;
		}

		if (ownerId == null) {
			return true;
		}

		return ownerId.equals(menuId);
	}
	
	@Override
	@Transactional()
	public void saveMenuLink(BusinessCertificate bc, MenuLink menuLink) {
		assert bc != null : "BusinessCertificate must not be null";
		boolean createBusinessMenuLink = false;
		//Child Menus always inherit category from parents
		if (menuLink.isHasParentLink()) {
			Long parentCategoryId = this.getMenuCategoryIdForMenu(bc, menuLink.getParentMenuLink().getId()); 
			menuLink.setMenuLinkCategory(new MenuLinkCategory(parentCategoryId));
		}
		
		//All menu URLs must be prefixed with a forward slash (/)
		if (menuLink.isHasLinkUrl() && (!menuLink.getLinkUrl().startsWith("/"))) {
			menuLink.setLinkUrl("/" + menuLink.getLinkUrl());
		}
		
		LocalDate now = LocalDate.now();
		
		if (menuLink.isNewEntity()) {
			createBusinessMenuLink = true;
			menuLink.setCreatedBy(bc.getUserName());
			menuLink.setTimeCreated(now);
		}
		
		menuLink.setLastModBy(bc.getUserName());
		menuLink.setLastModTs(now);
		
		this.persist(menuLink);
		if(createBusinessMenuLink){
			//This has been Altered.
			List<BusinessClientMenuLink> saveList = new ArrayList<>();
			try {
				 if (menuLink.isCaspBind()) {
					BusinessClient wBusinessClient = genericService.loadObjectWithSingleCondition(BusinessClient.class, CustomPredicate.procurePredicate("civilServiceInd", 1));
					 BusinessClientMenuLink businessClientMenuLink = new BusinessClientMenuLink(wBusinessClient, menuLink);
					 saveList.add(businessClientMenuLink);
				 }
				if(menuLink.isLgBind()){
					BusinessClient wBusinessClient = genericService.loadObjectWithSingleCondition(BusinessClient.class, CustomPredicate.procurePredicate("lgInd", 1));
					BusinessClientMenuLink businessClientMenuLink = new BusinessClientMenuLink(wBusinessClient, menuLink);
					saveList.add(businessClientMenuLink);
				}
				if(menuLink.isSubebBind()){
					BusinessClient wBusinessClient = genericService.loadObjectWithSingleCondition(BusinessClient.class, CustomPredicate.procurePredicate("subebInd", 1));
					BusinessClientMenuLink businessClientMenuLink = new BusinessClientMenuLink(wBusinessClient, menuLink);
					saveList.add(businessClientMenuLink);
				}if(menuLink.isLgPensionBind()){
					BusinessClient wBusinessClient = genericService.loadObjectWithSingleCondition(BusinessClient.class, CustomPredicate.procurePredicate("blpgInd", 1));
					BusinessClientMenuLink businessClientMenuLink = new BusinessClientMenuLink(wBusinessClient, menuLink);
					saveList.add(businessClientMenuLink);
				}if(menuLink.isStatePensionBind()){
					BusinessClient wBusinessClient = genericService.loadObjectWithSingleCondition(BusinessClient.class, CustomPredicate.procurePredicate("statePensionInd", 1));
					BusinessClientMenuLink businessClientMenuLink = new BusinessClientMenuLink(wBusinessClient, menuLink);
					saveList.add(businessClientMenuLink);
				}
				if(menuLink.isExecutiveBind()){
					BusinessClient wBusinessClient = genericService.loadObjectWithSingleCondition(BusinessClient.class, CustomPredicate.procurePredicate("execInd", 1));
					BusinessClientMenuLink businessClientMenuLink = new BusinessClientMenuLink(wBusinessClient, menuLink);
					saveList.add(businessClientMenuLink);
				}
				for(BusinessClientMenuLink b : saveList)
				    this.persist(b);
			}catch (Exception exception){
				exception.printStackTrace();
			}
		}

	}
	
	@Override
	public Long getMenuCategoryIdForMenu(BusinessCertificate bc, Long menuId) {
		return (Long) makeCriteria(MenuLink.class)
						.add(Restrictions.idEq(menuId))
						.add(Restrictions.le("privilegedInd", bc.getPrivilegedInd()))
						.setProjection(Projections.property("menuLinkCategory.id"))
						.uniqueResult();
	}

	@Override
	public RoleMenuMaster getRoleMenuMasterForRole(BusinessCertificate bc, Long roleId) {
		RoleMenuMaster roleMenuMaster = null;
		if (roleId != null) {
			Object[] values = (Object[]) makeCriteria(RoleMenuMaster.class)
												.add(Restrictions.eq("role.id", roleId))
												.setProjection(
														Projections.projectionList()
															.add(Projections.distinct(Projections.id()))
															.add(Projections.property("role"))
															.add(Projections.property("description"))
													)
												.uniqueResult();
			
			if (!ArrayUtils.isEmpty(values)) {
				roleMenuMaster = new RoleMenuMaster((Long) values[0]);
				roleMenuMaster.setRole((Role) values[1]);
				roleMenuMaster.setDescription((String) values[2]);
			}
		}
		
		if (roleMenuMaster == null) {
			roleMenuMaster = new RoleMenuMaster();
		}
		
		return roleMenuMaster;
	}
	
	@Override
	public boolean doesRoleHaveMenuMaster(BusinessCertificate bc, Long roleId) {
		return makeCriteria(RoleMenuMaster.class)
					.add(Restrictions.eq("role.id", roleId))
					.setProjection(Projections.distinct(Projections.id()))
					.uniqueResult() != null;
	}
	
	@Override
	public Long getRoleMenuMasterIdForRole(BusinessCertificate bc, Long roleId) {
		return (Long) makeCriteria(RoleMenuMaster.class)
				.add(Restrictions.eq("role.id", roleId))
				.setProjection(Projections.distinct(Projections.id()))
				.uniqueResult();
	}

	
	@Override
	public List<RoleMenuMaster> getRoleMenuMasters(BusinessCertificate bc, int startIndex, int resultSize,
			String sortField, boolean ascending) {

		Criteria crit = makeCriteria(RoleMenuMaster.class)
				.createAlias("role", "r", CriteriaSpecification.LEFT_JOIN)
				.add(Restrictions.eq("r.businessClient.id", bc.getBusinessClientInstId()))
				.setFirstResult(startIndex);

		if (resultSize > 0) {
			crit.setMaxResults(resultSize);
		}

		if (IppmsUtils.isNotNullOrEmpty(sortField)) {
			if (sortField.startsWith("role")) {
				sortField = StringUtils.replace("role", sortField, "r");
			}

			crit.addOrder(ascending ? Order.asc(sortField) : Order.desc(sortField));
		}

		return crit.list();
	}

	@Override
	public long getTotalNumberOfRoleMenuMasters(BusinessCertificate bc) {
		return (Long) makeCriteria(RoleMenuMaster.class)
				      .createAlias("role","r",CriteriaSpecification.LEFT_JOIN)
				     .add(Restrictions.eq("r.businessClient.id", bc.getBusinessClientInstId()))
					.setProjection(Projections.rowCount())
					.uniqueResult();
	}

	
	@Override
	public List<Long> getMenuLinkIdsForUser(BusinessClient bc, Long pLoginId) {
		Long userMenuId = (Long) makeCriteria(UserMenu.class)
		.add(Restrictions.eq("user.id", pLoginId))
		.setProjection(Projections.distinct(Projections.id()))
		.uniqueResult();

		if (userMenuId != null) {
		return makeSQLQuery("SELECT DISTINCT um.menu_link_inst_id AS menu_link_id FROM ippms_user_menu_links um, ippms_menu_link ml "
		+ "WHERE um.user_menu_inst_id = :id AND ml.menu_link_inst_id = um.menu_link_inst_id and ml.is_inner_link = 0")
		.addScalar("menu_link_id", StandardBasicTypes.LONG)
		.setParameter("id", userMenuId)
		.list();
		}
		
		return Collections.EMPTY_LIST;
}
	
	private List<Long> getMenuLinkIdsForBusinessClient(BusinessCertificate bc) {


			return makeSQLQuery("SELECT DISTINCT ibcml.menu_link_inst_id AS menu_link_id FROM ippms_business_client_menu_link ibcml, ippms_menu_link ml "
					+ "WHERE ibcml.business_client_inst_id = :id AND ml.menu_link_inst_id = ibcml.menu_link_inst_id and ml.privileged_ind <= :pVar")
					.addScalar("menu_link_id", StandardBasicTypes.LONG)
					.setParameter("id", bc.getBusinessClientInstId())
					.setParameter("pVar",bc.getPrivilegedInd())
					.list();

	}
	@Override
	
	public List<Long> getMenuLinkIdsForRole(BusinessCertificate bc, Long roleId) {
		Long roleMasterId = (Long) makeCriteria(RoleMenuMaster.class)
									.add(Restrictions.eq("role.id", roleId))
									.setProjection(Projections.distinct(Projections.id()))
									.uniqueResult();
		
		if (roleMasterId != null) {

			StringBuilder query = new StringBuilder("SELECT DISTINCT rmm.menu_link_inst_id AS menu_link_id ")
										.append("FROM ippms_role_menu_master_links rmm, ippms_menu_link ml ")
										.append("WHERE rmm.role_menu_master_inst_id = :id AND ml.menu_link_inst_id = rmm.menu_link_inst_id ");

			if (!bc.isSysUser()) {
				query.append(" AND ml.is_sys_link = 0 ");
			}
			if(!bc.isPrivilegedUser())
				query.append(" AND ml.privileged_ind = 0");
			return makeSQLQuery(query.toString())
					    .addScalar("menu_link_id", StandardBasicTypes.LONG)
						.setParameter("id", roleMasterId)
						.list();
		}
		
		return Collections.EMPTY_LIST;
	}

	
	@Override
	public List<Long> getMenuLinkIdsForRoleAndUser(BusinessCertificate bc, Long roleId, Long userId) {
		/*
		 * We do not need to use 'DISTINCT' keyword in our queries because UNION
		 * returns only distinct records.
		 */
		StringBuilder queryBuilder = new StringBuilder(75);
		boolean hasQuery = false;
		
		if (IppmsUtils.isNotNullAndGreaterThanZero(roleId)) {
			queryBuilder.append("SELECT rmm.menu_link_inst_id AS menu_link_id ")
						.append("FROM ippms_role_menu_master_links rmm, ippms_menu_link ml ")
						.append("WHERE ml.menu_link_inst_id = rmm.menu_link_inst_id ")
						.append("AND rmm.role_menu_master_inst_id = ")
						.append("(SELECT DISTINCT m.role_menu_master_inst_id FROM ippms_role_menu_master m WHERE m.role_inst_id = :roleId) ");

			if(!bc.isPrivilegedUser())
				queryBuilder.append(" AND ml.privileged_ind = 0");
			if (!bc.isSysUser()) {
				queryBuilder.append(" AND ml.is_sys_link = 0 ");
			}
			
			hasQuery = true;
		}
		
		if (IppmsUtils.isNotNullAndGreaterThanZero(userId)) {
			
			if (IppmsUtils.isNotNullAndGreaterThanZero(roleId)) {
				queryBuilder.append(" UNION ");
			}
			
			queryBuilder.append("SELECT um.menu_link_inst_id AS menu_link_id FROM ippms_user_menu_links um ")
			.append("WHERE um.user_menu_inst_id = (SELECT DISTINCT p.user_menu_inst_id FROM ippms_user_menu p WHERE p.user_inst_id = :userId) ");
			
			hasQuery = true;
		}
		
		NativeQuery sqlQuery;
		
		if (hasQuery) {
			sqlQuery = makeSQLQuery(queryBuilder.toString())
					.addScalar("menu_link_id", StandardBasicTypes.LONG);
			
			if (IppmsUtils.isNotNullAndGreaterThanZero(roleId)) {
				sqlQuery.setParameter("roleId", roleId);
			}
			
			if (IppmsUtils.isNotNullAndGreaterThanZero(userId)) {
				sqlQuery.setParameter("userId", userId);
			}
			
			return sqlQuery.list();
		}
		
		return Collections.EMPTY_LIST;
	}

	@Override
	public UserMenu getUserMenuForUser(BusinessClient bc, Long pLoginId) {
		UserMenu userMenu = null;
		if (pLoginId != null) {
			Object[] values = (Object[]) makeCriteria(UserMenu.class)
												.add(Restrictions.eq("user.id", pLoginId))
												.setProjection(
														Projections.projectionList()
															.add(Projections.distinct(Projections.id()))
															.add(Projections.property("user"))
													)
												.uniqueResult();
			
			if (!ArrayUtils.isEmpty(values)) {
				userMenu = new UserMenu((Long) values[0]);
				userMenu.setUser((User) values[1]);
			}
		}
		
		if (userMenu == null) {
			userMenu = new UserMenu();
		}
		
		return userMenu;
	}

	@Override
	
	public List<Role> getRolesWithoutMenuLinks(BusinessCertificate businessCertificate) {

		return makeQuery("SELECT r FROM Role r WHERE r.businessClient.id = "+businessCertificate.getBusinessClientInstId()+"" +
				" and r.id NOT IN (SELECT DISTINCT rmm.role.id FROM RoleMenuMaster rmm  )").list();
	}

    @Override
    
    public List<MenuLinkCategory> getMenuLinkCategoryForDashboardTabs(BusinessCertificate bc) {
       return makeQuery("SELECT c FROM MenuLinkCategory c WHERE c.displayOnlyOnDbTabs = :dbTabInd")
                .setParameter("dbTabInd", IConstants.ON)
               .list();
    }

    @Override
    
    public Map<Long, MenuLinkCategory> getMenuLinkCategoriesForDashboardTabsAsMap(BusinessCertificate bc) {
        List<MenuLinkCategory> categories = getMenuLinkCategoryForDashboardTabs(bc);
        
        if (IppmsUtils.isNotNullOrEmpty(categories)) {
            Map<Long, MenuLinkCategory> map = new HashMap<Long, MenuLinkCategory>();
            for (MenuLinkCategory cat : categories) {
                map.put(cat.getId(), cat);
            }
            
            return map;
        }
        
        return Collections.EMPTY_MAP;
    }

    @Override
    public MenuLink getMenuLinkById(BusinessCertificate bc, Long id) {
        
        if (id != null) {
        	Object[] values = (Object[]) makeCriteria(MenuLink.class)
        						.add(Restrictions.idEq(id))
        						.setProjection(
        								Projections.projectionList()
        									.add(Projections.distinct(Projections.id()))
        									.add(Projections.property("name"))
        									.add(Projections.property("description"))
        									.add(Projections.property("parentMenuLink"))
        									.add(Projections.property("linkUrl"))
        									.add(Projections.property("menuLinkCategory"))
        									.add(Projections.property("displayOnDb"))
        									.add(Projections.property("dashboardMenuLinkInd"))
        									.add(Projections.property("innerLinkInd"))
        									.add(Projections.property("systemUser"))
        						)
        						.uniqueResult();
        						
            //Object[] values = (Object[]) makeQuery(query).setLong("menuId", id).uniqueResult();

            if (values != null && values.length > 0) {
               MenuLink link = new MenuLink((Long) values[0]);
               link.setName((String) values[1]);
               link.setDescription((String) values[2]);
               link.setParentMenuLink((MenuLink) values[3]);
               link.setLinkUrl((String) values[4]);
               link.setMenuLinkCategory((MenuLinkCategory) values[5]);
               link.setDisplayOnDb((Integer) values[6]);
               link.setDashboardMenuLinkInd((Integer) values[7]);
               link.setInnerLinkInd((Integer) values[8]);
               link.setSystemUser((Integer) values[9]);


               return link;
            }
        }
        
        return new MenuLink();
    }

    @Override
    
    public List<Long> getTabMenuLinkCategoriesForMenu(BusinessCertificate bc, Long id) {
        return makeSQLQuery("SELECT DISTINCT menu_link_cat_inst_id AS menu_link_cat_id FROM ippms_menu_link_db_cats "
					+ "WHERE menu_link_inst_id = :id")
					.addScalar("menu_link_cat_id", StandardBasicTypes.LONG)
					.setParameter("id", id)
					.list();
    }

	
	@Override
	public MenuLinksWrapper getMenuInfoForDashboard(BusinessCertificate bc, Long userId, String requestURL) {
		if (StringUtils.isNotBlank(requestURL)) {
			//TODO Optimize the query that checks the URL
			//The query can check in one go whether the menu link represented by the
			//url dashboard urls

			List<Object[]> list = (List<Object[]>) makeCriteria(MenuLink.class)
											.add(Restrictions.ilike("linkUrl", requestURL, MatchMode.EXACT))
											.add(Restrictions.le("privilegedInd",bc.getPrivilegedInd()))
											.setProjection(
													Projections.projectionList()
														.add(Projections.distinct(Projections.id()))
														.add(Projections.property("name"))
														.add(Projections.property("description"))
														.add(Projections.property("linkUrl"))
											)
											.list();
			
			if (IppmsUtils.isNotNullOrEmpty(list)) {
				Object[] values = list.get(0);
				
				MenuLink link = new MenuLink((Long) values[0]);
	            link.setName((String) values[1]);
	            link.setDescription((String) values[2]);
	            link.setLinkUrl((String) values[3]);
	            
	            final String queryStr = "SELECT DISTINCT c.menu_link_cat_inst_id AS catId, c.menu_link_cat_name AS catName, c.menu_link_cat_desc AS catDesc"
	    				+ " FROM ippms_menu_link_cat c WHERE c.menu_link_cat_inst_id IN "
	    				+ "(SELECT DISTINCT ml.menu_link_cat_inst_id FROM ippms_menu_link ml WHERE ml.menu_link_inst_id IN "
	    				+ "(SELECT DISTINCT uml.menu_link_inst_id FROM ippms_user_menu_links uml, ippms_business_client_menu_link ibcml "
	    				+ "WHERE ibcml.menu_link_inst_id = uml.menu_link_inst_id and ibcml.business_client_inst_id = :pBizId AND uml.user_menu_inst_id = "
	    				+ "(SELECT um.user_menu_inst_id FROM ippms_user_menu um WHERE um.user_inst_id = :userId)) "
	    				+ "AND ml.menu_link_cat_inst_id IN (SELECT DISTINCT mdc.menu_link_cat_inst_id FROM ippms_menu_link_db_cats mdc "
	    				+ "WHERE mdc.menu_link_inst_id = :menuLinkInstId)) "
	    				+ "ORDER BY c.menu_link_cat_name ASC ";
	    		
	    		List<Object[]> rows = makeSQLQuery(queryStr)
	    								.addScalar("catId", StandardBasicTypes.LONG)
	    								.addScalar("catName", StandardBasicTypes.STRING)
	    								.addScalar("catDesc", StandardBasicTypes.STRING)
	    								.setParameter("userId", userId)
	    								.setParameter("menuLinkInstId", link.getId())
										.setParameter("pBizId", bc.getBusinessClientInstId())
	    								.list();
	    		
	    		if (IppmsUtils.isNotNullOrEmpty(rows)) {
	    			MenuLinksWrapper wrapper = new MenuLinksWrapper();
	    			
	    			List<MenuLinkCategory> cats = new ArrayList<MenuLinkCategory>(rows.size());
	    			for (Object[] row : rows) {
	    				cats.add(new MenuLinkCategory((Long) row[0], (String) row[1], (String) row[2]));
	    			}
	    			
	    			wrapper.setMenuLinkCategories(cats);
	    			
	    			//get the first tabs
	    			List<MenuLink> firstTabLinks = getMenuLinksDisplayableOnDashboardForCategory(bc, cats.get(0).getId(), userId);
	    			wrapper.setFirstTabs(firstTabLinks);
	    			
	    			wrapper.setMenuLink(link);
	    			
	    			return wrapper;
	    		}
			}
		}
	
		return null;
	}

	
	@Override
	public boolean canUserAccessURL(BusinessCertificate bc, String urlWithoutParams, String fullURL) {
		/*
		 * If the URL has a menu link, then we check if user has
		 */

		List<Long> menuId = (List<Long>) makeCriteria(MenuLink.class)
						.add(Restrictions.isNotNull("linkUrl"))
						.add(Restrictions.le("privilegedInd", bc.getPrivilegedInd()))
						.add(Restrictions.or(
								Restrictions.ilike("linkUrl", urlWithoutParams, MatchMode.EXACT), 
								Restrictions.ilike("linkUrl", fullURL, MatchMode.EXACT))
						)
						.setProjection(Projections.distinct(Projections.id()))
						.list();
		
		if (IppmsUtils.isNotNullOrEmpty(menuId)) {
			final String queryStr = "SELECT COUNT(um.menu_link_inst_id) AS menuCount "
					+ "FROM ippms_user_menu_links um, ippms_business_client_menu_link ibcml "
					+ "WHERE ibcml.menu_link_inst_id = um.menu_link_inst_id AND ibcml.business_client_inst_id = :pBizId  AND um.menu_link_inst_id in (:menuId) AND "
					+ "um.user_menu_inst_id = (SELECT DISTINCT p.user_menu_inst_id FROM ippms_user_menu p WHERE p.user_inst_id = :userId) ";
			
			Number count = (Number) makeSQLQuery(queryStr)
									.addScalar("menuCount", StandardBasicTypes.LONG)
									.setParameter("userId", bc.getLoginId())
								    .setParameter("pBizId", bc.getBusinessClientInstId())
									.setParameterList("menuId", menuId)
									.uniqueResult();

			long value = count.longValue();
			if(value == 0L){
				 if(userHasAccessToParent(bc,urlWithoutParams))
				 	return true;

                 return this.isExcludedUrl(bc,urlWithoutParams);
            }
			return value > 0L;
		}else{
			return this.isExcludedUrl(bc,urlWithoutParams);
		}
		

	}

	private boolean userHasAccessToParent(BusinessCertificate bc,String urlWithoutParams)  {
		try{
			MenuLink menuLink = this.genericService.loadObjectUsingRestriction(MenuLink.class,Arrays.asList(CustomPredicate.procurePredicate("privilegedInd", bc.getPrivilegedInd(), Operation.LESS_OR_EQUAL),
					CustomPredicate.procurePredicate("linkUrl",urlWithoutParams)));
			if(menuLink.isNewEntity() || !menuLink.isInnerLink()) return false;

			final String queryStr = "SELECT u.user_inst_id AS userId "
					+ "FROM ippms_user_menu_links um, ippms_business_client_menu_link ibcml, ippms_user_menu u "
					+ "WHERE ibcml.menu_link_inst_id = um.menu_link_inst_id AND ibcml.business_client_inst_id = :pBizId  AND um.menu_link_inst_id = :menuId AND "
					+ "um.user_menu_inst_id = u.user_menu_inst_id and u.user_inst_id = :pLoginId";

			Long count = (Long) makeSQLQuery(queryStr)
					.addScalar("userId", StandardBasicTypes.LONG)
					.setParameter("pBizId", bc.getBusinessClientInstId())
					.setParameter("menuId", menuLink.getParentMenuLink())
					.setParameter("pLoginId", bc.getLoginId())
					.uniqueResult();

			if(count != null && count.equals(bc.getLoginId())) {
				//before doing that...insert into User Menu Links....
				makeUserMenuLink(menuLink.getId(),this.genericService.loadObjectWithSingleCondition(UserMenu.class,CustomPredicate.procurePredicate("user.id", count)));

				return true;
			}


		}catch (Exception e){
			System.out.println("Exception occurred "+e.getMessage());
			return false;
		}
		return  true;
	}
	@Transactional()
	private void makeUserMenuLink(Long menuId, UserMenu userMenu) {
		String sql = "INSERT INTO ippms_user_menu_links(user_menu_inst_id, menu_link_inst_id) VALUES (:pUserMenuId, :pMenuId) ";

		NativeQuery nativeQuery = this.makeSQLQuery(sql);

		nativeQuery.setParameter("pUserMenuId",userMenu.getId());
		nativeQuery.setParameter("pMenuId", menuId);

        nativeQuery.executeUpdate();


	}


	@Override
	public List<MenuLink> getAllHiddenMenuLinksForParent(BusinessCertificate bc, Long wParentMenuId, Boolean wHidden) {


		Criteria crit = makeCriteria(MenuLink.class);


		crit.add(Restrictions.eq("innerLinkInd", 1));
		crit.add(Restrictions.eq("parentMenuLink.id", wParentMenuId));
		if(!bc.isPrivilegedUser())
			crit.add(Restrictions.eq("privilegedInd",0));
		if (!bc.isSysUser()) {
			crit.add(Restrictions.eq("systemUser", 0));
		}

		return crit.list();
	}


	public List<MenuLink> treatMenuLinks(List<MenuLink> pMenuToTreat, BusinessCertificate bc){

		for(MenuLink menuLink : pMenuToTreat) {

			if (menuLink.getName().indexOf("MDA") != -1) {
				if (!bc.isCivilService()) {
					menuLink.setName(menuLink.getName().replaceAll("MDA", bc.getMdaTitle()));
					menuLink.setDescription(menuLink.getDescription().replaceAll("MDA", bc.getMdaTitle()));
				}
			}
			if(menuLink.getName().indexOf("/Schools") != -1){
				if(bc.isPensioner()){
					menuLink.setName(menuLink.getName().replaceAll("/Schools", ""));
					menuLink.setDescription(menuLink.getDescription().replaceAll("/Schools", ""));
				}
			}
			if(menuLink.getName().indexOf("/School") != -1){
				if(bc.isPensioner()){
					menuLink.setName(menuLink.getName().replaceAll("/School", ""));
					menuLink.setDescription(menuLink.getDescription().replaceAll("/School", ""));
				}
			}
            if(menuLink.getName().indexOf("Employees") != -1)
            	if(!bc.isCivilService()){
					menuLink.setName(menuLink.getName().replaceAll("Employees", bc.getStaffTypeName()+"s"));
					menuLink.setDescription(menuLink.getDescription().replaceAll("Employees", bc.getStaffTypeName()+"s"));
				}

			if(menuLink.getName().indexOf("Employee") != -1)
				if(!bc.isCivilService()) {
					menuLink.setName(menuLink.getName().replaceAll("Employee", bc.getStaffTypeName()));
					menuLink.setDescription(menuLink.getDescription().replaceAll("Employee", bc.getStaffTypeName()));
				}
			if(menuLink.getName().equalsIgnoreCase("Expected Year of Retirement")){
				if(bc.isPensioner()){
					menuLink.setName("'I am Alive' Check Date");
					menuLink.setDescription("Expected 'I am Alive' Check Date");
				}
			}
			if(menuLink.getName().equalsIgnoreCase("State Pensioners Due For Retirement"))
			   {
				menuLink.setName("Pensioners By 'I am Alive' Due Date");
				menuLink.setDescription("Pensioners By 'I am Alive' Due Date");
			}

		}
		return pMenuToTreat;
	}

	@Override
	public boolean isExcludedUrl(BusinessCertificate bc, String urlWithoutParams) {

		if(bc == null)
			return false;
		String queryStr;
		List<String> excludedUrlsList = bc.getExcludedUrls();
		if(IppmsUtils.isNullOrEmpty(excludedUrlsList)){
			excludedUrlsList = new ArrayList<>();
			 if(!bc.isPrivilegedUser())
			    queryStr = "SELECT DISTINCT url_name  AS urlName FROM ippms_excluded_urls where privileged_ind = 0";
			 else
				 queryStr = "SELECT DISTINCT url_name  AS urlName FROM ippms_excluded_urls ";
			 
			List<Object> rows = makeSQLQuery(queryStr) .addScalar("urlName", StandardBasicTypes.STRING) .list();
			for (Object row : rows) {
				excludedUrlsList.add( (String) row );
			}
			bc.setExcludedUrls(excludedUrlsList);

		}
		return excludedUrlsList.contains(urlWithoutParams);
	}

	private String getViewName(BusinessCertificate bc) {
		String VIEW_NAME = "";
		if(bc.isLocalGovt()){
			VIEW_NAME = "ippms_lg_restricted_menu_links";
		}else if(bc.isCivilService()){
			VIEW_NAME = "ippms_restricted_menu_links";
		}else if(bc.isLocalGovtPension()){
			VIEW_NAME = "ippms_blgp_restricted_menu_links";
		}else if(bc.isSubeb()){
			VIEW_NAME = "ippms_subeb_restricted_menu_links";
		}else if(bc.isStatePension()){
			VIEW_NAME = "ippms_pension_restricted_menu_links";
		}else if(bc.isExecutive()) {
			VIEW_NAME = "ippms_executive_restricted_menu_links";
		}
		return VIEW_NAME;
	}

}
