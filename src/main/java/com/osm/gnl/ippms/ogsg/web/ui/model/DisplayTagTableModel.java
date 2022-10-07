/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.web.ui.model;

import com.osm.gnl.ippms.ogsg.menu.domain.AbstractEntity;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.util.List;



@SuppressWarnings("serial")
public class DisplayTagTableModel extends WebModel implements PaginatedList {
	
	private final int fullListSize;
	private final List<?> list;
	private final int pageLength;
	private final int pageNumber;
	private final String sortCriterion;
	private final String sortOrder;
	private boolean canViewMenuLinks;
	private boolean canViewMenuLinkCategories;
	/**
	 * 
	 * @param pList
	 * @param pageNumber
	 * @param pageLength
	 * @param listSize
	 * @param sortCriterion
	 * @param sortOrder
	 * @param genSerials should we generate serials?
	 */
	public DisplayTagTableModel(List<?> pList, int pageNumber, int pageLength, int listSize, String sortCriterion, String sortOrder, boolean genSerials) {
		this.fullListSize = listSize;
	    this.list = pList;
	    this.pageLength = pageLength;
	    this.pageNumber = pageNumber;
	    this.sortCriterion = sortCriterion;
	    this.sortOrder = sortOrder;
	    
	    if (genSerials) {
	    	 generateSerials();
	    }
	}

	@Override
	public int getFullListSize() {
		return this.fullListSize;
	}

	@Override
	public List<?> getList() {
		return this.list;
	}

	@Override
	public int getObjectsPerPage() {
		return this.pageLength;
	}

	@Override
	public int getPageNumber() {
		return this.pageNumber;
	}

	@Override
	public String getSearchId() {
		return null;
	}

	@Override
	public String getSortCriterion() {
		return this.sortCriterion;
	}

	@Override
	public SortOrderEnum getSortDirection() {
		return (IppmsUtils.isNullOrEmpty(this.sortOrder) || this.sortOrder.equals("asc")) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
	}

	public boolean isCanViewMenuLinks() {
		return canViewMenuLinks;
	}

	public void setCanViewMenuLinks(boolean canViewMenuLinks) {
		this.canViewMenuLinks = canViewMenuLinks;
	}

	public boolean isCanViewMenuLinkCategories() {
		return canViewMenuLinkCategories;
	}

	public void setCanViewMenuLinkCategories(boolean canViewMenuLinkCategories) {
		this.canViewMenuLinkCategories = canViewMenuLinkCategories;
	}

	/**
	 * utility method to generate serials for BaseEntity objects in list
	 */
	private void generateSerials() {
		
		if( IppmsUtils.isNotNullOrEmpty( this.list ) ) {
			Object obj = this.list.get( 0 );
			
			//we expect AbstractEntity and WebModel instances here
			boolean isWebModel = false;
			
			if( obj instanceof AbstractEntity || obj instanceof WebModel ) {
				isWebModel = (obj instanceof WebModel);
				
				int start = (this.pageNumber -1) * this.pageLength;
				int end = start + this.pageLength;
				int listIndex = 0;
				
				for(int count = start; count < end; count++){
					if(this.list.size() > listIndex){
						if (!isWebModel) {
							((AbstractEntity) this.list.get(listIndex++)).setSerialNo(count + 1);
						}
						else {
							((WebModel) this.list.get(listIndex++)).setSerialNo(count + 1);
						}
					}
					else
						break;
				}
			}
		}
	}
}
