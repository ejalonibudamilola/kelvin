package com.osm.gnl.ippms.ogsg.domain.beans;
/**
 * This software code is the proprietary and intellectual property of GNL Systems Nigeria Limited.
 * ALL RIGHTS Reserved (C)2008-2011
 */

import com.osm.gnl.ippms.ogsg.abstractentities.DependentEntity;
import lombok.Getter;
import lombok.Setter;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.util.Date;
import java.util.List;


@Getter
@Setter
public class PaginatedEpmBean extends DependentEntity implements PaginatedList {

	
	private List<?> objectList;
	
	private int pageNumber;
	
	private int pageLength;
	
	private int listSize;
	private String displayErrors;
	
	private String sortCriterion;
	private String sortOrder;
	private String showRow;
	private Date fromDate;
	private Date toDate;
	private int userId;
	
	private boolean canSendToExcel;
	private boolean hasRecords;
	
	//--
	private String employeeName;
	private String employeeId;
	private String organization;
	private String birthDateStr;
	private String hireDateStr;
	private String salaryScaleLevelAndStep;
	private int noOfYearsInService;
	private String approvalMemo;
	private boolean showLink;
	private boolean addWarningIssued;
	private int approvalInd;
	private boolean canCreatePaymentMethod;
	private String url;
	
	
	
	public PaginatedEpmBean(List<?> pList,int pPageNumber,int pPageLength,
			int pListSize,String pSortCriterion,String pSortOrder){
		this.listSize = pListSize;
		this.objectList = pList;
		this.pageLength = pPageLength;
		this.pageNumber = pPageNumber;
		this.sortCriterion = pSortCriterion;
		this.sortOrder = pSortOrder;
		
	}

	
	@Override
	public int getFullListSize()
	{
		
		return this.listSize;
	}

	
	@Override
	public List<?> getList()
	{
		return objectList;
	}

	
	@Override
	public int getObjectsPerPage()
	{
		
		return this.pageLength;
	}

	
	@Override
	public int getPageNumber()
	{
		
		return this.pageNumber;
	}

	
	@Override
	public String getSearchId()
	{
		
		return null;
	}

	
	@Override
	public String getSortCriterion()
	{
		
		return this.sortCriterion;
	}

	
	@Override
	public SortOrderEnum getSortDirection()
	{
		if(sortOrder == null)
			return SortOrderEnum.ASCENDING;
		return sortOrder.equals("asc") ? SortOrderEnum.ASCENDING
				: SortOrderEnum.DESCENDING;
	}

	public boolean isHasRecords() {
		hasRecords = this.getObjectList() != null && this.getObjectList().size() > 0 ? true : false;
		return hasRecords;
	}
}
