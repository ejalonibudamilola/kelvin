package com.osm.gnl.ippms.ogsg.domain.beans;

import java.time.LocalDate;
import java.util.List;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.BaseEntity;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import lombok.Getter;
import lombok.Setter;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

@Getter
@Setter
public class PaginatedPromotionTrackerBean extends BaseEntity
  implements PaginatedList
{
  private static final long serialVersionUID = -9894988342884736L;
  private boolean showingInactive;
  private int fromYear;
  private int toYear;
  private String displayErrors;
  private String showingInactiveStr;
  private String hidden;
  private LocalDate fromDate;
  private String showRow;
  private LocalDate toDate;
  private String fromDateStr;
  private String toDateStr;
  private List<BaseEntity> yearsList;
  private List<PromotionTracker> promotionTrackList;
  private int pageNumber;
  private int pageLength;
  private int listSize;
  private String sortCriterion;
  private String sortOrder;

  public PaginatedPromotionTrackerBean()
  {
  }

  public PaginatedPromotionTrackerBean(List<PromotionTracker> pList, int pPageNumber, int pPageLength, int pListSize, String pSortCriterion, String pSortOrder)
  {
    this.listSize = pListSize;
    this.promotionTrackList = pList;
    this.pageLength = pPageLength;
    this.pageNumber = pPageNumber;
    this.sortCriterion = pSortCriterion;
    this.sortOrder = pSortOrder;
  }


  public String getDisplayErrors() {
    if (this.displayErrors == null)
      this.displayErrors = "none";
    return this.displayErrors;
  }

  public List getList()  {
    return this.promotionTrackList;
  }

  public int getObjectsPerPage() {
    return this.pageLength;
  }

  public int getFullListSize(){
    return this.listSize;
  }

  public String getSearchId()
  {
    return null;
  }

  public SortOrderEnum getSortDirection()
  {
    return this.sortOrder.equals("asc") ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
  }


}