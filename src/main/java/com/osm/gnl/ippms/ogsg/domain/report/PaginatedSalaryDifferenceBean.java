package com.osm.gnl.ippms.ogsg.domain.report;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedSalaryDifferenceBean extends NamedEntity
{
  private static final long serialVersionUID = 5284406370171231659L;
  private List<SalaryDifferenceBean> salaryDiffBeanList;
  private int pageNumber;
  private int pageLength;
  private int listSize;
  private String sortCriterion;
  private String sortOrder;
  private String showRow;
  private LocalDate fromDate;
  private LocalDate toDate;
  private String fromDateStr;
  private String toDateStr;
  private String fromDatePrintStr;
  private String toDatePrintStr;
  private int userId;
  private double startAmount;
  private double endAmount;
  private String startAmountStr;
  private String endAmountStr;
  private SalaryDifferenceBean salaryBean;

  public PaginatedSalaryDifferenceBean(List<SalaryDifferenceBean> pList, int pPageNumber, int pPageLength, int pListSize, String pSortCriterion, String pSortOrder)
  {
    this.listSize = pListSize;
    this.salaryDiffBeanList = pList;
    this.pageLength = pPageLength;
    this.pageNumber = pPageNumber;
    this.sortCriterion = pSortCriterion;
    this.sortOrder = pSortOrder;
  }


}