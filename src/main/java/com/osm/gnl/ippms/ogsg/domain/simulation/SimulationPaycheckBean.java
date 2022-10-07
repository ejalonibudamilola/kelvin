/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.CompContEmployeeMap;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.tax.domain.EmployeeTaxInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ippms_paycheck_sim_info")
@SequenceGenerator(name = "paycheckSimSeq", sequenceName = "paycheck_sim_seq", allocationSize = 1)
public class SimulationPaycheckBean implements Comparable<SimulationPaycheckBean>, Serializable {
	private static final long serialVersionUID = 4858234877013782839L;

	@Id
	@GeneratedValue(generator = "paycheckSimSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "paycheck_sim_inst_id", nullable = false, unique = true)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pensioner_inst_id")
	private Pensioner pensioner;

	@ManyToOne
	@JoinColumn(name = "ltg_master_inst_id", nullable = false)
	private LtgMasterBean ltgMasterBean;

	@ManyToOne
	@JoinColumn(name = "map_id", nullable = false)
	private MdaInfo mdaInfo;

	@Column(name = "business_client_inst_id", nullable = false, unique = true)
	private Long businessClientId;

	@Column(name = "total_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalPay;

	@Column(name = "total_pay_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalPaySim;

	@Column(name = "net_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double netPay;

	@Column(name = "net_pay_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double netPaySim;

	@Column(name = "taxes_paid", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double taxesPaid;

	@Column(name = "total_contributions", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalContributions;

	@Column(name = "total_contributions_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalContributionsSim;

	@Column(name = "total_deductions", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalDeductions;

	@Column(name = "total_deductions_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalDeductionsSim;

	@Column(name = "total_garnishments", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalGarnishments;

	@Column(name = "total_garnishments_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalGarnishmentsSim;

	@Column(name = "nhf", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double nhf;

	@Column(name = "nhf_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double nhfSim;

	@Column(name = "paye", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double monthlyTax;

	@Column(name = "paye_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double taxesSimPaid;

	@Column(name = "total_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalAllowance;

	@Column(name = "total_allowance_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalAllowanceSim;

	@Column(name = "development_levy", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double developmentLevy;

	@Column(name = "tws", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double tws;

	@Column(name = "tws_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double twsSim;

	@Column(name = "union_dues", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double unionDues;

	@Column(name = "union_dues_sim", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double unionDuesSim;

	@Column(name = "leave_transport_grant", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double leaveTransportGrant;

	@Column(name = "object_id", columnDefinition = "numeric default '1'", nullable = false)
	private int objectInd;


	@Transient private boolean pensionerType;

	@Transient private double monthlyPensionAmount;

	@Transient
	private double monthlyTaxSim; // TODO Removal Candidate

	@Transient
	private boolean approvedForPayroll;

	@Transient
	private boolean suspended;

	@Transient
	private int employeeInstId;// TODO Removal Candidate
	@Transient
	private String totalAllowanceStr;
	@Transient
	private String totalAllowanceSimStr;
	@Transient
	private double allDedTotal;
	@Transient
	private double allDedTotalSim;
	@Transient
	private String allDedTotalStr;
	@Transient
	private String allDedTotalSimStr;
	@Transient
	private String allDedTotalStrSansNaira;
	@Transient
	private String allDedTotalSimStrSansNaira;

	@Transient
	private String departmentName;
	@Transient
	private String displayStyle;
	@Transient
	private boolean doNotPay;
	@Transient
	private List<CompContEmployeeMap> empContributions;
	@Transient
	private List<AbstractDeductionEntity> employeeDeductions;
	@Transient
	private List<AbstractGarnishmentEntity> employeeGarnishments;
	@Transient
	private EmployeeTaxInfo employeeTaxInfo;
	@Transient
	private double grossPay;
	@Transient
	private String grossPayStr;
	@Transient
	private String grossPayStrSansNaira;
	@Transient
	private boolean hasPaymentMethod;
	@Transient
	private HiringInfo hiringInfo;
	@Transient
	private boolean hourlyPayInd;
	@Transient
	private double hoursWorked;
	@Transient
	private String lastPayDate;
	@Transient
	private String ministryName;
	@Transient
	private String netPayStr;
	@Transient
	private String netPaySimStr;
	@Transient
	private String netPayStrSansNaira;
	@Transient
	private String netPaySimStrSansNaira;
	@Transient
	private String nhfStr;
	@Transient
	private String nhfStrSansNaira;
	@Transient
	private String nhfSimStr;
	@Transient
	private String nhfStrSimSansNaira;
	@Transient
	private LocalDate payDate;
	@Transient
	private String payEmployee;
	@Transient
	private boolean payEmployeeRef;
	@Transient
	private PaymentInfo paymentInfo;
	@Transient
	private PaymentMethodInfo paymentMethodInfo;
	@Transient
	private String paymentType;
	@Transient
	private String payPeriodAsString;
	@Transient
	private String paySched;
	@Transient
	private boolean PFADefined;
	@Transient
	private String salaryScale;
	@Transient
	private String status;
	@Transient
	private String taxesPaidStr;
	@Transient
	private String taxesPaidStrSansNaira;
	@Transient
	private String totalContributionsStr;
	@Transient
	private String totalContributionsStrSansNaira;
	@Transient
	private String taxesSimPaidStr;
	@Transient
	private String taxesPaidSimStrSansNaira;
	@Transient
	private String totalContributionsSimStr;
	@Transient
	private String totalContributionsSimStrSansNaira;
	@Transient
	private String totalDeductionsStr;
	@Transient
	private String totalDeductionsStrSansNaira;
	@Transient
	private String totalGarnishmentsStr;
	@Transient
	private String totalGarnishmentsStrSansNaira;
	@Transient
	private String totalPayStr;
	@Transient
	private String totalPayStrSansNaira;
	@Transient
	private String totalDeductionsSimStr;
	@Transient
	private String totalDeductionsSimStrSansNaira;
	@Transient
	private String totalGarnishmentsSimStr;
	@Transient
	private String totalGarnishmentsSimStrSansNaira;
	@Transient
	private String totalPaySimStr;
	@Transient
	private String totalPaySimStrSansNaira;
	@Transient
	private String unionDuesStr;
	@Transient
	private String unionDuesStrSansNaira;
	@Transient
	private String twsStr;
	@Transient
	private String twsStrSansNaira;
	@Transient
	private String leaveTransportGrantStr;
	@Transient
	private String leaveTransportGrantStrSansNaira;
	@Transient
	private String unionDuesSimStr;
	@Transient
	private String unionDuesSimStrSansNaira;
	@Transient
	private String twsSimStr;
	@Transient
	private String twsStrSimSansNaira;
	@Transient
	private double leaveTransportGrantSim;
	@Transient
	private String leaveTransportGrantSimStr;
	@Transient
	private String leaveTransportGrantSimStrSansNaira;
	@Transient
	private SalaryType salaryType;
	@Transient
	private int salaryTypeCode;
	@Transient
	private Long ltgMasterInstId;
	@Transient
	private Long salaryInfoId;
	@Transient
	private Integer designationInstId;
	@Transient
	private int designationInd;
	@Transient
	private LocalDate birthDate;
	@Transient
	private LocalDate hireDate;
	@Transient
	private boolean ltgEnabled;
	@Transient
	private double payPercentage;
	@Transient
	private LocalDate terminateDate;
	@Transient
	private boolean politicalOfficeHolder;
	@Transient
	private int noOfDays;
	@Transient
	private boolean contractStaff;
	@Transient
	private LocalDate contractEndDate;
	@Transient
	private LocalDate ltgLastPaid;
	@Transient
	private boolean payPerDays;
	@Transient
	private boolean doNotDeductContributoryPension;
	@Transient
	private LocalDate expectedDateOfRetirement;
	@Transient
	private Integer pensionableInd;
	@Transient
	private boolean pensionableEmployee;


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SimulationPaycheckBean)) return false;
		SimulationPaycheckBean that = (SimulationPaycheckBean) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(ltgMasterBean, that.ltgMasterBean) &&
				Objects.equals(mdaInfo, that.mdaInfo) &&
				Objects.equals(businessClientId, that.businessClientId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, ltgMasterBean.getId(), mdaInfo.getId(), businessClientId);
	}

	public int compareTo(SimulationPaycheckBean pEmpPayBean) {
		if ((getEmployee() != null) && (pEmpPayBean.getEmployee() != null))
			return getEmployee().getLastName().compareToIgnoreCase(pEmpPayBean.getEmployee().getLastName());
		return 0;
	}

	public double getAllDedTotal() {
		this.allDedTotal = (getTaxesPaid() + getUnionDues() + getNhf() + getTotalDeductions() + getTotalGarnishments());
		return this.allDedTotal;
	}

	public String getAllDedTotalStr() {
		this.allDedTotalStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getAllDedTotal()));
		return this.allDedTotalStr;
	}

	public String getAllDedTotalStrSansNaira() {
		this.allDedTotalStrSansNaira = IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getAllDedTotal());
		return this.allDedTotalStrSansNaira;
	}

	public double getGrossPay() {
		return this.grossPay;
	}

	public String getGrossPayStr() {
		this.grossPayStr = IConstants.naira + PayrollHRUtils.getDecimalFormat().format((getTotalPay()));
		return this.grossPayStr;
	}

	public String getGrossPayStrSansNaira() {
		this.grossPayStrSansNaira = IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getTotalPay());
		return this.grossPayStrSansNaira;
	}

	public String getName() {
		if ((getEmployee() != null) && (!getEmployee().isNewEntity()))
			return getEmployee().getLastName().toUpperCase() + ", " + getEmployee().getFirstName() + " "
					+ StringUtils.trimToEmpty(getEmployee().getInitials());
		return "";
	}

	public double getNetPay() {
		return this.netPay;
	}

	public String getNetPayStr() {
		this.netPayStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(this.netPay));
		return this.netPayStr;
	}

	public String getNetPayStrSansNaira() {
		this.netPayStrSansNaira = IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getNetPay());
		return this.netPayStrSansNaira;
	}

	public double getNhf() {
		return this.nhf;
	}

	public String getNhfStr() {
		this.nhfStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getNhf()));
		return this.nhfStr;
	}

	public String getNhfStrSansNaira() {
		this.nhfStrSansNaira = IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getNhf());
		return this.nhfStrSansNaira;
	}


	public String getSalaryScale() {
		this.salaryScale = getEmployee().getSalaryInfo().getSalaryType().getName();
		return this.salaryScale;
	}

	public String getTaxesPaidStr() {
		this.taxesPaidStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getTaxesPaid()));
		return this.taxesPaidStr;
	}

	public String getTaxesPaidStrSansNaira() {
		this.taxesPaidStrSansNaira = IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getTaxesPaid());
		return this.taxesPaidStrSansNaira;
	}

	public double getTotalContributions() {
		return this.totalContributions;
	}

	public String getTotalContributionsStr() {
		this.totalContributionsStr = (IConstants.naira
				+ PayrollHRUtils.getDecimalFormat().format(this.totalContributions));
		return this.totalContributionsStr;
	}

	public String getTotalContributionsStrSansNaira() {
		this.totalContributionsStrSansNaira = PayrollHRUtils.getDecimalFormat().format(getTotalContributions());
		return this.totalContributionsStrSansNaira;
	}

	public double getTotalDeductions() {
		return this.totalDeductions;
	}

	public String getTotalDeductionsStr() {
		this.totalDeductionsStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(this.totalDeductions));
		return this.totalDeductionsStr;
	}

	public String getTotalDeductionsStrSansNaira() {
		this.totalDeductionsStrSansNaira = PayrollHRUtils.getDecimalFormat().format(getTotalDeductions());
		return this.totalDeductionsStrSansNaira;
	}

	public double getTotalGarnishments() {
		return this.totalGarnishments;
	}

	public String getTotalGarnishmentsStr() {
		this.totalGarnishmentsStr = (IConstants.naira
				+ PayrollHRUtils.getDecimalFormat().format(this.totalGarnishments));
		return this.totalGarnishmentsStr;
	}

	public String getTotalGarnishmentsStrSansNaira() {
		this.totalGarnishmentsStrSansNaira = PayrollHRUtils.getDecimalFormat().format(this.totalGarnishments);
		return this.totalGarnishmentsStrSansNaira;
	}

	public double getTotalPay() {
		return this.totalPay;
	}

	public String getTotalPayStr() {
		this.totalPayStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(this.totalPay));
		return this.totalPayStr;
	}

	public String getTotalPayStrSansNaira() {
		this.totalPayStrSansNaira = PayrollHRUtils.getDecimalFormat().format(getTotalPay());
		return this.totalPayStrSansNaira;
	}

	public double getUnionDues() {
		return this.unionDues;
	}

	public String getUnionDuesStr() {
		this.unionDuesStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getUnionDues()));
		return this.unionDuesStr;
	}

	public String getUnionDuesStrSansNaira() {
		this.unionDuesStrSansNaira = PayrollHRUtils.getDecimalFormat().format(getUnionDues());
		return this.unionDuesStrSansNaira;
	}

	public void setTotalAllowance(double pTotalAllowance) {
		this.totalAllowance = pTotalAllowance;
	}

	public String getTotalAllowanceStr() {
		if (getTotalAllowance() > 0.0D)
			this.totalAllowanceStr = PayrollHRUtils.getDecimalFormat().format(getTotalAllowance());
		return this.totalAllowanceStr;
	}

	public boolean isPensionableEmployee() {
		return this.pensionableInd == 0;
	}

	public void setPensionableEmployee(boolean pensionableEmployee) {
		this.pensionableEmployee = pensionableEmployee;
	}


}