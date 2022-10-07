package com.osm.gnl.ippms.ogsg.employee.beans;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.BaseEntity;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.HRReportBean;
import lombok.Getter;
import lombok.Setter;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BusinessEmpOVBeanInactive extends HRReportBean
  implements PaginatedList
{
	private static final long serialVersionUID = 6817806592507523331L;
	private boolean showingInactive;
	private int fromYear;
	private int toYear;
	private String salaryFromLevel;
	private String salaryToLevel;
	private String displayErrors;
	private String showingInactiveStr;
	private String hidden;
	private LocalDate fromDate;
	private String showRow;
	private LocalDate toDate;
	private String fromDateStr;
	private String toDateStr;
	private List<BaseEntity> yearsList;
	private List<?> employeeList;
	private List<TerminateReason> termReasonList;
	private Long terminateReasonId;
	private int pageNumber;
	private int pageLength;
	private int listSize;
	private String sortCriterion;
	private String sortOrder;
	private boolean noDates;
	private boolean noReason;
	private boolean usingTermReason;
	private boolean usingDates;
	private String terminationReasonStr;
	private boolean hasData;
	private String employeeName;
	private String employeeId;
	private String currentMda;
	private List<?> someObjectList;
	private Map<Long, MdaInfo> mdaMap;
	private Map<Integer, SalaryInfo> salaryInfoMap;
	private double totalGrossPay;
	private double totalTaxesPaid;
	private boolean singleEmployee;
	private boolean filteredByUserId;
	private String typeName;
	private boolean filteredByType;
	private double totalNetPay;
	private double totalMonthlyBasic;
 	private String totalMonthlyBasicStr;
	private String currentLevelAndStep;
//	private String hireDate;
	private boolean terminatedEmployee;
	private int noOfYearsInService;
	private String terminationReason;
	private String terminationDate;
	private String confirmationDate;
	private boolean filteredByMda;
	private boolean filteredBySchool;
	private boolean usingPayPeriod;
	private BusinessEmpOVBean busEmpOvBean;
	private BusinessEmpOVBean configBean;
	private boolean forContracts;
	private boolean forPromotion;
	private Long parentId;
	private Long schoolInstId;
	private boolean addWarningIssued;
	private int approvalInd;
	private String approvalMemo;

	public BusinessEmpOVBeanInactive()
	{
	}

  public BusinessEmpOVBeanInactive(List<HiringInfo> pList, int pPageNumber, int pPageLength, int pListSize, String pSortCriterion, String pSortOrder)
  {
    this.listSize = pListSize;
    this.employeeList = pList;
    this.pageLength = pPageLength;
    this.pageNumber = pPageNumber;
    this.sortCriterion = pSortCriterion;
    this.sortOrder = pSortOrder;
  }

	public BusinessEmpOVBeanInactive(List<?> pEmpList) {
		this.employeeList = pEmpList;
	}

    @Override
    public List<?> getList(){
        return this.employeeList;
    }
    @Override
    public int getObjectsPerPage(){
        return this.pageLength;
    }
    @Override
    public int getFullListSize(){
        return this.listSize;
    }

	public String getTotalMonthlyBasicStr() {
		totalMonthlyBasicStr = naira + PayrollHRUtils.getDecimalFormat().format(this.totalMonthlyBasic);
		return totalMonthlyBasicStr;
	}

	public SortOrderEnum getSortDirection(){
    return this.sortOrder.equals("asc") ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
  }

    @Override
    public String getSearchId(){
        return null;
    }


    public boolean isAddWarningIssued() {
        return addWarningIssued;
    }


	public int getApprovalInd() {
		return approvalInd;
	}
}