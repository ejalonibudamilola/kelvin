package com.osm.gnl.ippms.ogsg.report.beans;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Transient;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReportViewAttributes {
	
	@Transient private String rowVisibility;
	@Transient private String displayStyle;
	@Transient private int schoolTransfer;
	@Transient private Long departmentId;
	@Transient private Long schoolId;
	@Transient private Long mdaId;
	@Transient private String showForConfirm;
	@Transient private String proposedSchool;
	@Transient private String proposedMda;
	@Transient private boolean showOverride;
	@Transient private String oldLevelAndStep;
	@Transient private Double oldSalary;
	@Transient private LocalDate dateOfHire;
	@Transient private boolean editMode;
	@Transient private boolean firstTimePay;
	@Transient private boolean normalPay;
	@Transient private boolean previousPay;
	@Transient private boolean add;
	@Transient private boolean calcNumWorkingDays;
	@Transient private boolean useDefaultPaySchedule;
	@Transient private Long termId;
	@Transient private String payPeriodName;
	@Transient private String bankName;
	@Transient private String auditTime;
	@Transient private String branchName;
	@Transient private LocalDate transferDate;
	@Transient private boolean terminationWarningIssued;
	@Transient private boolean terminated;
	@Transient private int overrideInd;
	@Transient private String displayStatus;
	@Transient private String showTerminated;
	@Transient private LocalDate oldHireDate;
	@Transient private LocalDate oldBirthDate;
	@Transient private String confirmDateStr;
	@Transient private String lastPromotionDateStr;
	@Transient private boolean confirmEmployeeWarning;
	@Transient protected boolean nonContractStaff;
	@Transient private boolean lastPromotionDateChanged;
	@Transient private String showContractDateRows;
	@Transient private String showRespAllowRow;
	@Transient protected boolean onContract;
	@Transient private String suspensionDateStr;
	@Transient private boolean suspendedEmployee;
	@Transient private String suspendedStr;
	@Transient private int employmentStatusInd;
	@Transient private boolean terminatedEmployee;
	@Transient protected String confirmationDateAsStr;
	@Transient protected int ageAtTermination;
	@Transient protected int noOfYearsAtRetirement;
	@Transient protected int ageAtRetirement;
	//@Transient protected boolean pensionableEmployee;
	@Transient private boolean politicalOfficeHolderType;
	@Transient private String accountNumber;
	@Transient private Long branchInstId;
	@Transient private double terminalGrossPay;
	@Transient private String terminalGrossPayStr;
	@Transient private double taxesPaid;
	@Transient private String taxesPaidStr;
	@Transient private String religionStr;
	@Transient private String lgaName;
	@Transient private String maritalStatusStr;
	@Transient private int rerunInd;
	@Transient private double netPay;
	@Transient private double monthlyBasic;
	@Transient private String monthlyBasicStr;
	@Transient private String netPayStr;
	@Transient private LocalDate oldContractStartDate;
	@Transient private LocalDate oldContractEndDate;
	@Transient private boolean canEdit;
	/**
	 * for batch remove
	 */
	@Transient private String displayErrors;
	@Transient private boolean payrollRunning;
	@Transient private String displayPayrollMsg;
	@Transient private String displayTitle;
	@Transient private String payrollRunningMessage;
	@Transient private String errorMsg;
	/**
	 *
	 */
	@Transient private String mode;
	@Transient private String name;
	@Transient private LocalDate dateOfBirth;
	@Transient private LocalDate dateTerminated;
	@Transient private LocalDate lastLtgPaid;
	@Transient private int suspendedInd;
	@Transient private boolean approvedForPayroll;
	@Transient private LocalDate expRetireDate;
	@Transient private Long oldSalaryInfoInstId;





	public String getShowTerminated() {
		if ((this.showTerminated == null) || (this.showTerminated == ""))
			this.showTerminated = IConstants.HIDE_ROW;
		return this.showTerminated;

	}


	public String getTaxesPaidStr() {
		this.taxesPaidStr = PayrollHRUtils.getDecimalFormat().format(this.getTaxesPaid());
		return taxesPaidStr;
	}
	public String getTerminalGrossPayStr() {
		terminalGrossPayStr = PayrollHRUtils.getDecimalFormat().format(this.terminalGrossPay);
		return terminalGrossPayStr;
	}

	public String getNetPayStr() {
		netPayStr = PayrollHRUtils.getDecimalFormat().format(this.getNetPay());
		return netPayStr;
	}

}
