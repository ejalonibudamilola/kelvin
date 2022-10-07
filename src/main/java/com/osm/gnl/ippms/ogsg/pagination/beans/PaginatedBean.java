package com.osm.gnl.ippms.ogsg.pagination.beans;

import com.osm.gnl.ippms.ogsg.abstractentities.NamedEntityLong;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.beans.MPBAMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Data;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@Data
public class PaginatedBean extends NamedEntityLong implements PaginatedList {
	private static final long serialVersionUID = 328745804930470553L;

	private List<MPBAMiniBean> mpbaMiniBeanList;
	private List<?> objectList;
	private int pageNumber;
	private int pageLength;
	private int listSize;
	private String sortCriterion;
	private String sortOrder;
	private String objectName;
	private String createdBy;
	private String createdDateStr;
	private int noOfEmployees;
	private boolean objectsExist;
	private String showRow;
	private String yearStr;
	private String monthAndYearStr;
	private int monthInd;
	private Long mdaInd;
	private Long schoolId;
	private String employeeName;
	private String employeeId;
	private String empId;
	private double totPensionCont;
	private LocalDate fromDate;
	private LocalDate toDate;
	private LocalDate fromHireDate;
	private LocalDate toHireDate;
	private LocalDate fromBirthDate;
	private LocalDate toBirthDate;
	private LocalDate fromRetireDate;
	private LocalDate toRetireDate;
	private String fromDateStr;
	private String toDateStr;
	private String fromHireDateStr;
	private String toHireDateStr;
	private String fromBirthDateStr;
	private String toBirthDateStr;
	private String fromRetireDateStr;
	private String toRetireDateStr;
	private boolean addWarningIssued;
	private boolean showOverride;
	private boolean replaceWarningIssued;
	private boolean showForConfirm;
	private boolean individual;
	private int objectId;
	private String details;
	private Long empInstId;
	private String mapObj;
	private int pfaId;
	private Long idLong;
	private String ogNumber;
	private String lastName;
	private String useTpsCpsRule;
	private String includeTerminated;
	private int tpsOrCps;
	private String tpsOrCpsName;
	private boolean notUsingDates;
	private int userId;
	// -- PaginatedAbmpBean
	private double totalBasicSalary;
	private String totalBasicSalaryStr;
	private double totBasicSalaryPlusLtg;
	private String totBasicSalaryPlusLtgStr;
	private double netIncrease;
	private String netIncreaseStr;
	// -- PaginatedBankInfoBean
	private String sortCode;
	private Long bankId;
	private String typeCode;
	private boolean canSendToExcel;
	private String organization;
	private String birthDateStr;
	private String hireDateStr;
	private String salaryScaleLevelAndStep;
	private String salaryTypeName;
	private int noOfYearsInService;
	private Long deductionTypeId;
	private boolean filteredBySchool;
	private String payPeriodStr;
	private boolean showLink;
	private String actionCompleted;
	private boolean captchaError;
	private boolean errorRecord;
	private String typeName;
	private boolean filteredByType;
	private boolean usingDates;
	private String schoolName;
	private boolean filteredByMda;
	private String payPeriod;
	private boolean usingPayPeriod;
	private String departmentName;
	private String mbapName;
	private String ministryName;
	private String approvalMemo;
	private int overrideInd;
	private String memoType;
	private boolean rejection;
	private int noOfFemales;
	private int noOfMales;
	private String femalePercentageStr;
	private String malePercentageStr;
	private boolean hasErrors;
	private Long salaryTypeId;

	public PaginatedBean(List<?> pList){
		this.objectList = pList;
	}

	public PaginatedBean(List<?> pList, int pPageNumber, int pPageLength, int pListSize, String pSortCriterion,
                         String pSortOrder) {
		this.listSize = pListSize;
		this.objectList = pList;
		this.pageLength = pPageLength;
		this.pageNumber = pPageNumber;
		this.sortCriterion = pSortCriterion;
		this.sortOrder = pSortOrder;
	}

	public PaginatedBean(List<?> pEmpList, int pSize) {
		this.listSize = pSize;
		this.pageLength = pSize;
		this.objectList = pEmpList;
		this.sortOrder = "asc";
	}
	public List<?> getList() {
		return this.objectList;
	}
	public String getSearchId() {
		return null;
	}
	public int getObjectsPerPage() {
		return this.pageLength;
	}

	public int getFullListSize() {
		return this.listSize;
	}

	public SortOrderEnum getSortDirection() {
		if (this.sortOrder == null)
			return SortOrderEnum.ASCENDING;
		return this.sortOrder.equals("asc") ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
	}

	public boolean isObjectsExist() {
		objectsExist = IppmsUtils.isNotNullOrEmpty(this.getObjectList());
		return objectsExist;
	}

	public String getCreatedDateStr() {
		if (getCreatedDate() != null)
			this.createdDateStr = PayrollHRUtils.getDisplayDateFormat().format(getCreatedDate());
		return this.createdDateStr;
	}

	public String getDetails() {
		if (this.getFromDateStr() != null && !treatNull(getFromDateStr()).equals("")) {
			details = "" + this.getEmpInstId() + "&sDate=" + this.getFromDateStr();
		}
		return details;
	}

	public String getMapObj() {
		mapObj = this.getId() + "" + this.getObjectId();
		return mapObj;
	}

	public String getFromHireDateStr() {
		if (this.fromHireDate != null)
			fromHireDateStr = PayrollHRUtils.getFullDateFormat().format(this.fromHireDate);
		return fromHireDateStr;
	}

	public String getFromDateStr() {
		if (this.fromDate != null)
			fromDateStr = PayrollHRUtils.getFullDateFormat().format(this.fromDate);
		return fromDateStr;
	}

	public String getToDateStr() {
		if (this.toDate != null)
			toDateStr = PayrollHRUtils.getFullDateFormat().format(this.toDate);
		return toDateStr;
	}


	public void setFromHireDateStr(String fromHireDateStr) {
		this.fromHireDateStr = fromHireDateStr;
	}

	public String getToHireDateStr() {
		if (this.toHireDate != null)
			toHireDateStr = PayrollHRUtils.getFullDateFormat().format(this.toHireDate);
		return toHireDateStr;
	}

	public void setToHireDateStr(String toHireDateStr) {
		this.toHireDateStr = toHireDateStr;
	}

	public String getFromBirthDateStr() {
		if (this.fromBirthDate != null)
			fromBirthDateStr = PayrollHRUtils.getFullDateFormat().format(this.fromBirthDate);
		return fromBirthDateStr;
	}

	public void setFromBirthDateStr(String fromBirthDateStr) {
		this.fromBirthDateStr = fromBirthDateStr;
	}

	public String getToBirthDateStr() {
		if (this.toBirthDate != null)
			toBirthDateStr = PayrollHRUtils.getFullDateFormat().format(this.toBirthDate);
		return toBirthDateStr;
	}

	public String getFromRetireDateStr() {
		if (this.fromRetireDate != null)
			fromRetireDateStr = PayrollHRUtils.getFullDateFormat().format(this.fromRetireDate);
		return fromRetireDateStr;
	}

	public String getToRetireDateStr() {
		if (this.toRetireDate != null)
			toRetireDateStr = PayrollHRUtils.getFullDateFormat().format(this.toRetireDate);
		return toRetireDateStr;
	}

	public String getTpsOrCpsName() {
		if (this.tpsOrCps == IConstants.TPS_IND)
			this.tpsOrCpsName = "TPS";
		else
			this.tpsOrCpsName = "CPS";
		return tpsOrCpsName;
	}

	public String getTotalBasicSalaryStr() {
		if (getTotalBasicSalary() > 0.0D)
			this.totalBasicSalaryStr = PayrollHRUtils.getDecimalFormat().format(getTotalBasicSalary());
		return this.totalBasicSalaryStr;
	}

	public String getTotBasicSalaryPlusLtgStr() {
		if (this.totBasicSalaryPlusLtg > 0.0D)
			this.totBasicSalaryPlusLtgStr = PayrollHRUtils.getDecimalFormat().format(getTotalBasicSalary());
		return this.totBasicSalaryPlusLtgStr;
	}

	public void setTotBasicSalaryPlusLtgStr(String pTotBasicSalaryPlusLtgStr) {
		this.totBasicSalaryPlusLtgStr = pTotBasicSalaryPlusLtgStr;
	}


	public String getNetIncreaseStr() {
		if (getNetIncrease() > 0.0D)
			this.netIncreaseStr = PayrollHRUtils.getDecimalFormat().format(getNetIncrease());
		return this.netIncreaseStr;
	}
	public String getMalePercentageStr()
	{
		if ( this.noOfEmployees  > 0  && (noOfMales > 0)) {
			String wFormatRes = new DecimalFormat("#.#").format(new Double(noOfMales).doubleValue() / new Double(noOfEmployees).doubleValue() * 100.0D);
			if ((wFormatRes.equals("0")) || (wFormatRes.length() == 1)) {
				DecimalFormat df = new DecimalFormat("#.##");
				wFormatRes = df.format(new Double(noOfMales).doubleValue() / new Double(noOfEmployees).doubleValue() * 100.0D);
			}
			this.malePercentageStr = (wFormatRes + "%");
		} else {
			this.malePercentageStr = "0.0%";
		}

		return this.malePercentageStr;
	}
	public String getFemalePercentageStr()
	{
		if ((noOfEmployees > 0) && (noOfFemales > 0)) {
			String wFormatRes = new DecimalFormat("#.#").format(new Double(noOfFemales).doubleValue() / new Double(noOfEmployees).doubleValue() * 100.0D);
			if ((wFormatRes.equals("0")) || (wFormatRes.length() == 1)) {
				DecimalFormat df = new DecimalFormat("#.##");
				wFormatRes = df.format(new Double(noOfFemales).doubleValue() / new Double(noOfEmployees).doubleValue() * 100.0D);
			}
			this.femalePercentageStr = (wFormatRes + "%");
		} else {
			this.femalePercentageStr = "0.0%";
		}

		return this.femalePercentageStr;
	}
}