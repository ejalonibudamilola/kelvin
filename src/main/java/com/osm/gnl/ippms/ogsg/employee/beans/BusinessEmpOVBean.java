package com.osm.gnl.ippms.ogsg.employee.beans;

import com.osm.gnl.ippms.ogsg.domain.beans.EmpContMiniBean;
import com.osm.gnl.ippms.ogsg.report.beans.HRReportBean;
import lombok.Getter;
import lombok.Setter;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.util.List;

@Getter
@Setter
public class BusinessEmpOVBean extends HRReportBean
  implements PaginatedList
{
  private static final long serialVersionUID = 4714834289715855016L;
  private boolean showingInactive;
  private String showingInactiveStr;
  private String salaryStructureName;
  private String hidden;
  private String showRow;
  private boolean reabsorbEmployee;
  private boolean readOnly;
  private int fromLevel;
  private int toLevel;
  private List<?> employeeList;
  private int pageNumber;
  private int pageLength;
  private int listSize;
  private String sortCriterion;
  private String sortOrder;
  private double totalGrossPay;
  private double totalTaxesPaid;
  private int currPayInd;
  private int ytdInd;
  private int specAllowInd;
  private int dedInd;
  private int bankTypeInd;
  private int bvnStatusInd;
  private int statusInd;
  private EmpContMiniBean statBean;
  private String mdaInd;

  public BusinessEmpOVBean()
  {
  }
  public BusinessEmpOVBean(List<?> pList)
  {
    this.employeeList = pList;
  }

  public BusinessEmpOVBean(List<?> pList, int pPageNumber, int pPageLength, int pListSize, String pSortCriterion, String pSortOrder)
  {
    this.listSize = pListSize;
    this.employeeList = pList;
    this.pageLength = pPageLength;
    this.pageNumber = pPageNumber;
    this.sortCriterion = pSortCriterion;
    this.sortOrder = pSortOrder;
  }

  public String getShowingInactiveStr()
  {
    if (!this.isShowingInactive())
      this.showingInactiveStr = "n";
    else {
      this.showingInactiveStr = "y";
    }
    return this.showingInactiveStr;
  }

  @Override
  public List getList() {
    return this.employeeList;
  }

  @Override
  public int getObjectsPerPage() {
    return this.pageLength;
  }

  @Override
  public int getFullListSize() {
    return this.listSize;
  }

  public SortOrderEnum getSortDirection()
  {
    if(this.sortOrder == null)
      return SortOrderEnum.ASCENDING;
    return this.sortOrder.equals("asc") ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
  }

  @Override
  public String getSearchId() {
    return null;
  }


}