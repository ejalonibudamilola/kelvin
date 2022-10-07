/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.negativepay.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_neg_pay_info")
@SequenceGenerator(name = "negPaycheckSeq", sequenceName = "ippms_neg_pay_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class NegativePayBean   implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5866817751900022820L;
	@Id
	@GeneratedValue(generator = "negPaycheckSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "neg_pay_info_inst_id", nullable = false, unique = true)
	private Long id;


	@Column(name = "paycheck_inst_id", nullable = false)
	private Long paycheckId;

	@Column(name = "employee_inst_id", nullable = false)
	private Long employeeInstId;

	@Column(name = "pensioner_inst_id", nullable = false)
	private Long pensionerInstId;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;
	
	@Column(name = "total_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalPay;

	@Column(name = "net_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double netPay;
	@Column(name = "total_deductions", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalDeductions;

	@Column(name = "run_month", nullable = false)
	private int runMonth;

	@Column(name = "run_year", nullable = false)
	private int runYear;
	
	@Column(name = "reduction_amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double reductionAmount;

	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;
	
	@Column(name = "last_mod_by", nullable = false, length = 32)
	private String lastModBy;
	@Transient
	private String currDiff;
	
	@Transient
	private AbstractPaycheckEntity employeePayBean;
	
	public NegativePayBean(Long pId) {
		this.id = pId;
	}


	public String getCurrDiff() {
		this.currDiff = PayrollHRUtils.getDecimalFormat()
				.format(Math.abs(this.reductionAmount + this.netPay));
		return currDiff;
	}

	public boolean isNewEntity() {
		 
		return this.id == null;
	}


}
