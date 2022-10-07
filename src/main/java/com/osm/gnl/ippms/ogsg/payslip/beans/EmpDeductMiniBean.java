package com.osm.gnl.ippms.ogsg.payslip.beans;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class EmpDeductMiniBean extends NamedEntity implements Comparable<Object> {

	private static final long serialVersionUID = 31023184813442059L;
	private DateTimeFormatter dateFormat = PayrollHRUtils.getDisplayDateFormat();
	private double yearToDate;
	private double currentDeduction;
	//Will remove Later...
	private double currentGarnishment;
	private String currentGarnishmentStr;
	private String yearToDateStr;
	private String currentDeductionStr;
	private DecimalFormat df;
	private Employee employee;
	private String empInstId;
	private String salaryInfoDesc;
	private String employeeId;
	private MdaDeptMap mdaDeptMap;
	private int integerId;
	private String mdaName;
	private Long pfaId;
	private String details;
	private String fromDateStr;
	private int yearInt;
	private String accountNumber;
	private String branchName;
	private LocalDate expDateOfRetirement;
	private String expDateOfRetirementStr;
	private LocalDate hireDate;
	private String hireDateStr;
	private LocalDate birthDate;
	private String birthDateStr;
	private int level;
	private int step;
	private String gradeLevelAndStep;
	private boolean hasTerminatedEmployee;
	private HashMap<String, NamedEntity> deductionMap;
    private String deductionCode;
	private String deductionName;
	private double deductionAmount;
	private String bankName;
	private String bankBranchName;
	private LocalDate period;
	private int totalBanBranchkCount;
	private LocalDate terminateDate;
	private String terminatDateStr;
	private int totalDeduction;
	private List<String> listBankName;
	private List<Integer> listBranchCount;
	private List<Integer> listDeduction;
	private String bankTotal;
	private String totalAllBanksAount;
	private transient String yearsMonthsAndIdStr;
	private int reportInd;
	private double amountWitheld;
	private String amountWitheldStr;
	private String payPeriodStr;

	

	public EmpDeductMiniBean() {
		this.df = new DecimalFormat("#,##0.00");
	}


	public String getYearToDateStr() {
		this.yearToDateStr = this.df.format(this.yearToDate);
		return this.yearToDateStr;
	}

	public String getCurrentDeductionStr() {
		this.currentDeductionStr = this.df.format(this.currentDeduction);
		return this.currentDeductionStr;
	}


	public int compareTo(EmpDeductMiniBean pIncoming) {
		if ((getName() != null) && (pIncoming != null) && (pIncoming.getName() != null))
			return getName().compareToIgnoreCase(pIncoming.getName());
		return 0;
	}


	public String getDetails() {
		if (IppmsUtils.isNotNullOrEmpty(this.getFromDateStr())) {
			details = "" + this.getId() + "&sDate=" + this.getFromDateStr();
		}
		return details;
	}


	public int getYearAndId() {
		String yearId = this.getYearInt() + "" + this.getId();
		return Integer.parseInt(yearId);
	}

	public String getExpDateOfRetirementStr() {
		if (this.expDateOfRetirement != null)
			expDateOfRetirementStr = dateFormat.format(this.expDateOfRetirement);
		return expDateOfRetirementStr;
	}



	public String getHireDateStr() {
		if (this.hireDate != null)
			hireDateStr = dateFormat.format(this.hireDate);
		return hireDateStr;
	}


	public String getGradeLevelAndStep() {
		gradeLevelAndStep = (this.level + " /" + (this.step));
		return gradeLevelAndStep;
	}


	public String getBirthDateStr() {
		if (this.birthDate != null)
			birthDateStr = dateFormat.format(this.birthDate);
		return birthDateStr;
	}


	public HashMap<String, NamedEntity> getDeductionMap() {
		if (null == this.deductionMap)
			deductionMap = new HashMap<>();
		return deductionMap;
	}

	public String getTerminatDateStr() {
		terminatDateStr = dateFormat.format(this.terminateDate);
		return terminatDateStr;
	}
}