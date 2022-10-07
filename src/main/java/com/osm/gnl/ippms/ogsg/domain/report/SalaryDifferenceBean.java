package com.osm.gnl.ippms.ogsg.domain.report;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
@Entity
@Table(name = "ippms_ops_ups_info")
@SequenceGenerator(name = "opsUpsSeq", sequenceName = "ippms_ops_ups_info_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class SalaryDifferenceBean implements Comparable<SalaryDifferenceBean>, Serializable {
	private static final long serialVersionUID = 8931548335214379101L;

	@Id
	@GeneratedValue(generator = "opsUpsSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "ops_ups_inst_id")
	private Long id;

	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;

	@Column(name = "last_mod_by", nullable = false, length = 50)
	private User lastModBy;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id", nullable = false)
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "curr_paycheck_inst_id", nullable = false)
	private EmployeePayBean currPaycheck;

	@ManyToOne
	@JoinColumn(name = "prev_paycheck_inst_id", nullable = false)
	private EmployeePayBean prevPaycheck;

	@Column(name = "ops_ups_type", nullable = false, columnDefinition = "integer default '0'")
	private int opsUpsType;

	@Column(name = "amount", nullable = false,  columnDefinition = "numeric(15,2) default '0.00'")
	private double amount;

	@Column(name = "applied", nullable = false, columnDefinition = "integer default '0'")
	private int paid;

	@Column(name = "created_time", nullable = false, length = 20)
	private String time;

	@Column(name = "apply_month", nullable = false, columnDefinition = "numeric(2,0) default '0'")
	private int applicationMonth;
	@Column(name = "apply_year", nullable = false, columnDefinition = "numeric(4,0) default '0'")
	private int applicationYear;

	@Transient
	private String employeeId;
	@Transient
	private String employeeName;
	@Transient
	private String organization;
	@Transient
	private String salaryScale;
	@Transient
	private String levelAndStep;
	@Transient
	private String lastSalaryStr;
	@Transient
	private double lastSalary;
	@Transient
	private String currentSalaryStr;
	@Transient
	private double currentSalary;
	@Transient
	private double salaryDiff;
	@Transient
	private String salaryDiffStr;
	@Transient
	private double lastTaxPaid;
	@Transient
	private double currentTaxPaid;
	@Transient
	private String lastTaxPaidStr;

	@Transient
	private double taxDiff;
	@Transient
	private String taxDiffStr;

	@Transient
	private boolean applied;
	@Transient
	private String status;
	@Transient
	private String opsUpsTypeStr;
	@Transient
	private int totalSalaryWarning;
	@Transient
	private boolean underPayment;
	@Transient
	private boolean overPayment;
	@Transient
	private String opupStr;
	@Transient
	private String currentMonthStr;

	@Transient
	private String previousMonthStr;

	@Transient
	private int currentMonthInd;
	@Transient
	private int previousMonthInd;
	@Transient
	private int currentYearInd;
	@Transient
	private int previousYearInd;

	@Transient
	private boolean superAdmin;
	@Transient
	private boolean deleteMode;
	@Transient
	private boolean exists;
	@Transient
	private boolean viewOnly;
	@Transient
	private boolean pendingMode;
	@Transient
	private String applicationMonthYearStr;
	@Transient
	private String freshOpsUpsStr;
	@Transient
	private boolean hasInitials;
	@Transient
	private String displayErrors;
	@Transient
	private MdaInfo mdaInfo;

	@Transient
	private Long currentPaycheckId;

	public double getSalaryDiff() {
		this.salaryDiff = (this.currentSalary - this.lastSalary);
		return this.salaryDiff;
	}

	public String getSalaryDiffStr() {
		this.salaryDiffStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getSalaryDiff()));
		return this.salaryDiffStr;
	}

	public String getLastSalaryStr() {
		this.lastSalaryStr = PayrollHRUtils.getDecimalFormat().format(getLastSalary());
		return this.lastSalaryStr;
	}

	public double getLastSalary() {
		return this.lastSalary;
	}

	public String getCurrentSalaryStr() {
		this.currentSalaryStr = PayrollHRUtils.getDecimalFormat().format(getCurrentSalary());
		return this.currentSalaryStr;
	}


	public String getLastTaxPaidStr() {
		this.lastTaxPaidStr = PayrollHRUtils.getDecimalFormat().format(getLastTaxPaid());
		return this.lastTaxPaidStr;
	}

	public double getTaxDiff() {
		this.taxDiff = (getCurrentTaxPaid() - getLastTaxPaid());
		return this.taxDiff;
	}

	public String getTaxDiffStr() {
		this.taxDiffStr = PayrollHRUtils.getDecimalFormat().format(getTaxDiff());
		return this.taxDiffStr;
	}


	public boolean isApplied() {
		this.applied = (getPaid() > 0);
		return this.applied;
	}

	public String getStatus() throws Exception {
		if (isNewEntity())
			this.status = "Open";
		else if (isApplied())
			this.status = "Applied";
		else if (!isApplied())
			this.status = "Pending";
		else {
			throw new Exception("Uknown Status for Salary Difference Bean [" + getEmployeeName() + "] ");
		}
		return this.status;
	}

	public boolean isNewEntity() {

		return this.id == null;
	}


	public boolean isUnderPayment() {
		this.underPayment = (getOpsUpsType() == 1);
		return this.underPayment;
	}

	public boolean isOverPayment() {
		this.overPayment = (getOpsUpsType() == 0);
		return this.overPayment;
	}

	public String getOpupStr() throws Exception {
		if (isOverPayment())
			this.opupStr = "Over Payment";
		else if (isUnderPayment())
			this.opupStr = "Under Payment";
		else
			throw new Exception("Unknown Payment Type!");
		return this.opupStr;
	}



	public boolean isViewOnly() {
		this.viewOnly = isApplied();
		return this.viewOnly;
	}

	public String getApplicationMonthYearStr() {
		this.applicationMonthYearStr = (PayrollBeanUtils.getMonthNameFromInteger(getApplicationMonth()) + ", "
				+ getApplicationYear() + ".");
		return this.applicationMonthYearStr;
	}

	public void setApplicationMonthYearStr(String pApplicationMonthYearStr) {
		this.applicationMonthYearStr = pApplicationMonthYearStr;
	}

	public String getOpsUpsTypeStr() {
		if (getOpsUpsType() == 1)
			this.opsUpsTypeStr = "Under Payment";
		else {
			this.opsUpsTypeStr = "Over Payment";
		}
		return this.opsUpsTypeStr;
	}

	public int compareTo(SalaryDifferenceBean pIncoming) {
		if (pIncoming != null)
			return getEmployeeName().compareToIgnoreCase(pIncoming.getEmployeeName());
		return 0;
	}

	public String getFreshOpsUpsStr() {
		if (getCurrentSalary() - getLastSalary() > 0.0D)
			this.freshOpsUpsStr = "Under Payment";
		else {
			this.freshOpsUpsStr = "Over Payment";
		}
		return this.freshOpsUpsStr;
	}

	public boolean isPendingMode() throws Exception {
		this.pendingMode = getStatus().equalsIgnoreCase("Pending");
		return this.pendingMode;
	}

}