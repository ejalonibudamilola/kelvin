/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.CompContEmployeeMap;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "ippms_simulation_info")
@SequenceGenerator(name = "simInfoSeq", sequenceName = "ippms_simulation_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SimulationInfo implements Comparable<SimulationInfo>, Serializable {

	@Id
	@GeneratedValue(generator = "simInfoSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "simulation_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pay_sim_master_inst_id")
	private PayrollSimulationMasterBean payrollSimulationMasterBean;

	@ManyToOne
	@JoinColumn(name = "mda_dept_map_inst_id", nullable = false)
	private MdaDeptMap mdaDeptMap;

	@Column(name = "total_pay", columnDefinition = "numeric(15,2) default '0.00'")
	private double totalPay;

	@Column(name = "net_pay", columnDefinition = "numeric(15,2) default '0.00'")
	private double netPay;

	@Column(name = "taxes_paid", columnDefinition = "numeric(15,2) default '0.00'")
	private double taxesPaid;

	@Column(name = "total_contributions", columnDefinition = "numeric(15,2) default '0.00'")
	private double totalContributions;

	@Column(name = "total_deductions", columnDefinition = "numeric(15,2) default '0.00'")
	private double totalDeductions;

	@Column(name = "total_garnishments", columnDefinition = "numeric(15,2) default '0.00'")
	private double totalGarnishments;

	@Column(name = "nhf", columnDefinition = "numeric(15,2) default '0.00'")
	private double nhf;

	@Column(name = "paye", columnDefinition = "numeric(15,2) default '0.00'")
	private double monthlyTax;

	@Column(name = "total_allowance", columnDefinition = "numeric(15,2) default '0.00'")
	private double totalAllowance;

	@Column(name = "development_levy", columnDefinition = "numeric(15,2) default '0.00'")
	private double developmentLevy;

	@Column(name = "tws", columnDefinition = "numeric(15,2) default '0.00'")
	private double tws;

	@Column(name = "union_dues", columnDefinition = "numeric(15,2) default '0.00'")
	private double unionDues;

	@Column(name = "leave_transport_grant", columnDefinition = "numeric(15,2) default '0.00'")
	private double leaveTransportGrant;

	@Column(name = "run_month", columnDefinition = "numeric(2,0) default '0'")
	private int runMonth;
	@Column(name = "run_year", columnDefinition = "numeric(4,0) default '0'")
	private int runYear;

	@Column(name = "promoted", columnDefinition = "integer default '0'")
	private int promotedInd;

	@Column(name = "step_increment", columnDefinition = "integer default '0'")
	private int stepIncrementInd;

	@Column(name = "retire_flag", columnDefinition = "integer default '0'")
	private int retireFlag;

	@Column(name = "object_id", columnDefinition = "integer default '0'")
	private int objectId;
	
	@Transient
	private double basicSalary;
	
	@Transient private String name;

	@Transient
	private String totalAllowanceStr;

	@Transient
	private double allDedTotal;
	@Transient
	private String allDedTotalStr;
	@Transient
	private String allDedTotalStrSansNaira;

	@Transient
	private boolean promoted;
	@Transient
	private boolean stepIncreased;
	@Transient
	private boolean retired;
	@Transient
	private boolean stepIncrementDone;

	@Transient
	private String departmentName;
	@Transient
	private DecimalFormat df = new DecimalFormat("#,##0.00");
    @Transient
	private boolean doNotPay;
	@Transient
	private List<CompContEmployeeMap> empContributions;
	@Transient
	private List<AbstractDeductionEntity> employeeDeductions;
	@Transient
	private List<AbstractGarnishmentEntity> employeeGarnishments;
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
	private String netPayStrSansNaira;
	@Transient
	private String nhfStr;
	@Transient
	private String nhfStrSansNaira;
	
	@Transient
	private Date payDate;
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
	private SalaryType salaryType;
	@Transient
	private int salaryTypeCode;

	@Transient
	private LocalDate birthDate;
	@Transient
	private LocalDate hireDate;
	@Transient
	private boolean ltgEnabled;
	@Transient
	private boolean deductGarnishment;
	@Transient
	private int promotionMonth;
	@Transient
	private boolean reRunPayRoll;
	@Transient
	private Long salaryInfoId;
 
	public int compareTo(SimulationInfo pEmpPayBean) {
		if ((getEmployee() != null) && (pEmpPayBean.getEmployee() != null))
			return getEmployee().getLastName().compareToIgnoreCase(pEmpPayBean.getEmployee().getLastName());
		return 0;
	}

	public double getAllDedTotal() {
		this.allDedTotal = (getTaxesPaid() + getUnionDues() + getNhf() + getTotalDeductions() + getTotalGarnishments());
		return this.allDedTotal;
	}

	public String getAllDedTotalStr() {
		this.allDedTotalStr = (IConstants.naira + this.df.format(getAllDedTotal()));
		return this.allDedTotalStr;
	}

	public String getAllDedTotalStrSansNaira() {
		this.allDedTotalStrSansNaira = this.df.format(getAllDedTotal());
		return this.allDedTotalStrSansNaira;
	}


	public String getGrossPayStr() {
		this.grossPayStr = (IConstants.naira + this.df.format(getTotalPay()));
		return this.grossPayStr;
	}

	public String getGrossPayStrSansNaira() {
		this.grossPayStrSansNaira = this.df.format(getTotalPay());
		return this.grossPayStrSansNaira;
	}


	public String getName() {
		if ((getEmployee() != null) && (!getEmployee().isNewEntity()))
			return getEmployee().getDisplayName();
		return "";
	}


	public String getNetPayStr() {
		this.netPayStr = (IConstants.naira + this.df.format(this.netPay));
		return this.netPayStr;
	}

	public String getNetPayStrSansNaira() {
		this.netPayStrSansNaira = this.df.format(getNetPay());
		return this.netPayStrSansNaira;
	}

	public double getNhf() {
		return this.nhf;
	}

	public String getNhfStr() {
		this.nhfStr = (IConstants.naira + this.df.format(getNhf()));
		return this.nhfStr;
	}

	public String getNhfStrSansNaira() {
		this.nhfStrSansNaira = this.df.format(getNhf());
		return this.nhfStrSansNaira;
	}


	public String getSalaryScale() {
		this.salaryScale = getEmployee().getSalaryInfo().getSalaryType().getName();
		return this.salaryScale;
	}


	public String getTaxesPaidStr() {
		this.taxesPaidStr = (IConstants.naira + this.df.format(getTaxesPaid()));
		return this.taxesPaidStr;
	}

	public String getTaxesPaidStrSansNaira() {
		this.taxesPaidStrSansNaira = this.df.format(getTaxesPaid());
		return this.taxesPaidStrSansNaira;
	}


	public String getTotalContributionsStr() {
		this.totalContributionsStr = (IConstants.naira + this.df.format(this.totalContributions));
		return this.totalContributionsStr;
	}

	public String getTotalContributionsStrSansNaira() {
		this.totalContributionsStrSansNaira = this.df.format(getTotalContributions());
		return this.totalContributionsStrSansNaira;
	}


	public String getTotalDeductionsStr() {
		this.totalDeductionsStr = (IConstants.naira + this.df.format(this.totalDeductions));
		return this.totalDeductionsStr;
	}

	public String getTotalDeductionsStrSansNaira() {
		this.totalDeductionsStrSansNaira = this.df.format(getTotalDeductions());
		return this.totalDeductionsStrSansNaira;
	}


	public String getTotalGarnishmentsStr() {
		this.totalGarnishmentsStr = (IConstants.naira + this.df.format(this.totalGarnishments));
		return this.totalGarnishmentsStr;
	}

	public String getTotalGarnishmentsStrSansNaira() {
		this.totalGarnishmentsStrSansNaira = this.df.format(this.totalGarnishments);
		return this.totalGarnishmentsStrSansNaira;
	}


	public String getTotalPayStr() {
		this.totalPayStr = (IConstants.naira + this.df.format(this.totalPay));
		return this.totalPayStr;
	}

	public String getTotalPayStrSansNaira() {
		this.totalPayStrSansNaira = this.df.format(getTotalPay());
		return this.totalPayStrSansNaira;
	}


	public String getUnionDuesStr() {
		this.unionDuesStr = (IConstants.naira + this.df.format(getUnionDues()));
		return this.unionDuesStr;
	}

	public String getUnionDuesStrSansNaira() {
		this.unionDuesStrSansNaira = this.df.format(getUnionDues());
		return this.unionDuesStrSansNaira;
	}


	public String getTotalAllowanceStr() {
		if (getTotalAllowance() > 0.0D)
			this.totalAllowanceStr = this.df.format(getTotalAllowance());
		return this.totalAllowanceStr;
	}


	public boolean isPromoted() {
		if (getPromotedInd() == 1)
			this.promoted = true;
		return this.promoted;
	}


	public boolean isStepIncreased() {
		if (getStepIncrementInd() == 1)
			this.stepIncreased = true;
		return this.stepIncreased;
	}


	public boolean isRetired() {
		if (getRetireFlag() == 1)
			this.retired = true;
		return this.retired;
	}

}