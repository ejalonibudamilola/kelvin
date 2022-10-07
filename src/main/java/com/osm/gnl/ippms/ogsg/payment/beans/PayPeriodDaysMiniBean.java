package com.osm.gnl.ippms.ogsg.payment.beans;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payslip.beans.MDAPPaySlipSummaryBean;
import com.osm.gnl.ippms.ogsg.report.beans.HRReportBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class PayPeriodDaysMiniBean extends HRReportBean
  implements PaginatedList
{
  private static final long serialVersionUID = 5355386092054543316L;
  private String payPeriod;
  private String payPeriodCode;
  private LocalDate payDate;
  private String payPeriodName;
  private int payPeriodValue;
  private int objectId;
  private boolean reRun;
  private LocalDate startLocalDate;
  private LocalDate endLocalDate;
  private LocalDate currentPayPeriodStart;
  private LocalDate currentPayPeriodEnd;
  private PayrollFlag payrollFlag;
  private List<String> payPeriodDaysListInternal;
  private List<PayPeriodDaysBean> payPeriodDaysList;
  private PayPeriodDaysBean payPeriodDaysBean;
  private List<?> objectList;
  private String displayPaycheckInfo;
  private String displayStatusMessage;
  private boolean hasData;
  private String deductDevelopmentLevy;
  private boolean deductLevy;
  private boolean hasErrors;
  private String showDeductDevLevy;
  private List<MDAPPaySlipSummaryBean> mdapPaySlips;
  private HashMap<Object, List<EmployeePayBean>> employeePayBeanMap;
  private List<NamedEntityBean> namedListBean;
   
  private List<Subvention> subventionList;
  private List<AbstractPaycheckEntity> employeePayBean;
  private List<Employee> employeeList;
  private int pageNumber;
  private int pageLength;
  private int listSize;
  private String sortCriterion;
  private String sortOrder;
  private int totalNoOfElements;
  private String showCaptchaRow;
  private boolean payGroup;
  private String ogNumber;
  private String lastName;
  private Long fromSalaryTypeId;
  private Long fromSalaryStructureId;
  private Long toSalaryTypeId;
  private Long toSalaryStructureId;
  private boolean hasRecords;

  //additions
  private int toBeProcessedStaff;
  private int toBeProcessedMda;
  private int toBeProcessedSpecAllow;
  private int toBeProcessedDeduction;
  private int toBeProcessedLoan;
  private String lastMonthGrossPayStr;
  private String projectedGrossPayStr;
  private String projectedNetPayStr;
  private boolean wholeEntity;


  public PayPeriodDaysMiniBean(List<AbstractPaycheckEntity> pList, int pPageNumber, int pPageLength, int pListSize, String pSortCriterion, String pSortOrder)
  {
    this.employeePayBean = pList;
    this.pageNumber = pPageNumber;
    this.pageLength = pPageLength;
    this.listSize = pListSize;
    this.sortCriterion = pSortCriterion;
    this.sortOrder = pSortOrder;
  }

   
  public int getFullListSize()
  {
    return this.listSize;
  }


  public List getList()
  {
    return this.employeePayBean;
  }

  public int getObjectsPerPage()
  {
    return this.pageLength;
  }

  public int getPageNumber()
  {
    return this.pageNumber;
  }

  public String getSearchId()
  {
    return null;
  }

  public String getSortCriterion()
  {
    return this.sortCriterion;
  }

  public SortOrderEnum getSortDirection()
  {
    return this.sortOrder.equals("asc") ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
  }
  public boolean isHasRecords() {
    hasRecords = this.getEmployeeList().size() > 0;
    return hasRecords;
  }

  
}