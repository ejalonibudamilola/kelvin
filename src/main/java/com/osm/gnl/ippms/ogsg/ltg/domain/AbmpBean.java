/*
 * Copyright (c) 2020. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.ltg.domain;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "ippms_ltg_details")
@SequenceGenerator(name = "ltgDetailsSeq", sequenceName = "ippms_ltg_details_seq", allocationSize = 1)
public class AbmpBean 
  implements Comparable<AbmpBean>, Serializable
{
	private static final long serialVersionUID = -1981635737740435312L;

	@Id
	@GeneratedValue(generator = "ltgDetailsSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "ltg_details_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "ltg_master_inst_id")
	private LtgMasterBean ltgMasterBean;

	@ManyToOne
	@JoinColumn(name = "mda_inst_id")
	private MdaInfo mdaInfo;


	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;

	@Column(name = "last_mod_by", nullable = false, length = 50)
	private String lastModBy;
	@Transient
	private int noOfEmp;
	@Transient
	private String name;
	@Transient
	private double basicSalary;
	@Transient
	private String basicSalaryStr;
	@Transient
	private String basicSalaryStrSansNaira;
	@Transient
	private double ltgCost;
	@Transient
	private String ltgCostStr;
	@Transient
	private String ltgCostStrSansNaira;
	@Transient
	private double netIncrease;
	@Transient
	private String netIncreaseStr;
	@Transient
	private String netIncreaseStrSansNaira;
	@Transient
	private String displayStyle;
	@Transient
	private String salaryScaleName;
	@Transient
	private int salaryLevel;
	@Transient
	private int salaryStep;
	@Transient
	private String employeeId;
	@Transient
	private String salarylevelAndStepStr;

	@Transient
	private int totalNoOfEmployees;
	
	@Transient
	private String objectCode;
	@Transient
	private int monthToApply;
	@Transient
	private String monthYearDisplay;

	public AbmpBean() {

	}

	public AbmpBean(Long pLong) {
		this.id = pLong;
	}

	public String getBasicSalaryStr() {
		if (getBasicSalary() > 0.0D)
			this.basicSalaryStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getBasicSalary()));
		return this.basicSalaryStr;
	}

	public String getLtgCostStr() {
		if (getLtgCost() > 0.0D)
			this.ltgCostStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getLtgCost()));
		return this.ltgCostStr;
	}

	public double getNetIncrease() {
		this.netIncrease = (this.ltgCost + this.basicSalary);
		return this.netIncrease;
	}

	public String getNetIncreaseStr() {
		if (getNetIncrease() > 0.0D)
			this.netIncreaseStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getNetIncrease()));
		return this.netIncreaseStr;
	}

	@Override
	public int compareTo(AbmpBean pIncoming) {

		return 0;
	}

	public boolean isNewEntity() {
		return this.id == null;
	}

	public String getSalarylevelAndStepStr() {
		String stepStr = String.valueOf(getSalaryStep());
		String levelStr = String.valueOf(getSalaryLevel());

		if (stepStr.length() == 1)
			stepStr = ".0" + stepStr;
		else
			stepStr = "." + stepStr;
		this.salarylevelAndStepStr = (levelStr + stepStr);
		return this.salarylevelAndStepStr;
	}

	public String getBasicSalaryStrSansNaira() {
		if (getBasicSalary() > 0.0D)
			this.basicSalaryStrSansNaira = IConstants.naira
					+ PayrollHRUtils.getDecimalFormat().format(getBasicSalary());
		return this.basicSalaryStrSansNaira;
	}

	public String getLtgCostStrSansNaira() {
		if (getLtgCost() > 0.0D)
			this.ltgCostStrSansNaira = IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getLtgCost());
		return this.ltgCostStrSansNaira;
	}

	public String getNetIncreaseStrSansNaira() {
		if (getNetIncrease() > 0.0D)
			this.netIncreaseStrSansNaira = IConstants.naira
					+ PayrollHRUtils.getDecimalFormat().format(getNetIncrease());
		return this.netIncreaseStrSansNaira;
	}
}