/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Table(name = "ippms_future_paychecks_info")
@SequenceGenerator(name = "futurePaySeq", sequenceName = "ippms_future_paychecks_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class FuturePaycheckBean implements Comparable<FuturePaycheckBean> {

	@Id
	@GeneratedValue(generator = "futurePaySeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "paycheck_sim_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "future_sim_inst_id")
	private FuturePaycheckMaster futurePaycheckMaster;

	@ManyToOne
	@JoinColumn(name = "mda_inst_id")
	private MdaInfo mdaInfo;

	@Column(name = "total_pay", columnDefinition = "numeric(15,2) default '0.00'")
	private double totalPay;
	@Transient
	private String totalPayStr;
	@Column(name = "net_pay", columnDefinition = "numeric(15,2) default '0.00'")
	private double netPay;
	@Transient
	private String netPayStr;
	@Column(name = "taxes_paid", columnDefinition = "numeric(15,2) default '0.00'")
	private double taxesPaid;
	@Transient
	private String taxesPaidStr;
	@Column(name = "nhf", columnDefinition = "numeric(15,2) default '0.00'")
	private double nhf;
	@Transient
	private String nhfStr;
	@Column(name = "paye", columnDefinition = "numeric(15,2) default '0.00'")
	private double paye;
	@Transient
	private String payeStr;
	@Column(name = "total_allowance", columnDefinition = "numeric(15,2) default '0.00'")
	private double totalAllowance;
	@Transient
	private String totalAllowanceStr;
	@Column(name = "union_dues", columnDefinition = "numeric(15,2) default '0.00'")
	private double unionDues;
	@Column(name = "total_deductions", columnDefinition = "numeric(15,2) default '0.00'")
	private double totalDeductions;
	@Column(name = "total_garnishments", columnDefinition = "numeric(15,2) default '0.00'")
	private double totalGarnishments;
	@Column(name = "special_allowance", columnDefinition = "numeric(15,2) default '0.00'")
	private double specialAllowance;
	@Column(name = "contributory_pension", columnDefinition = "numeric(15,2) default '0.00'")
	private double contributoryPension;
	@Column(name = "neg_pay_ind", columnDefinition = "integer default '0'")
	private int negativePayInd;
	
	@Transient
	private String unionDuesStr;
	@Transient
	private HiringInfo hiringInfo;
	@Transient
	private boolean payEmployee;
	@Transient
	private boolean doNotPay;
	@Transient
	private String name;
	@Transient
	private String bvnNo;
	@Transient
	private String accountNumber;
	@Transient
	private int rejectedForPayrollingInd;
	@Transient
	private double payPercentage;
	@Transient
	private int payPercentageInd;
	@Transient
	private int payByDaysInd;
	@Transient
	private SalaryInfo salaryInfo;
	@Transient
	private int terminatedInd;
	@Transient
	private int noOfDays;
	@Transient
	private int contractIndicator;
	@Transient
	private boolean doNotDeductContributoryPension;
	@Transient
	private boolean payByDays;
	@Transient
	private boolean percentagePayment;
	@Transient
	private double totCurrDed;

	
	@Transient
	private double totalNonTaxableDeductions;


	public FuturePaycheckBean(Long pId) {
		this.id = pId;
	}


	public int compareTo(FuturePaycheckBean pIncoming) {
		if ((pIncoming != null) && (pIncoming.getName() != null))
			return getName().compareToIgnoreCase(pIncoming.getName());
		return 0;
	}


	public boolean isPayByDays() {
		return this.payByDaysInd == 1;
	}
	public boolean isPercentagePayment() { return this.payPercentageInd == 1;
	}

}