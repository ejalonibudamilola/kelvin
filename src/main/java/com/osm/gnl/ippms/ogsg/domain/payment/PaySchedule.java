/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ippms_payment_schedule")
@SequenceGenerator(name = "paySchedSeq", sequenceName = "ippms_payment_schedule_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaySchedule {

	@Id
	@GeneratedValue(generator = "paySchedSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "pay_schedule_inst_id")
	private Long id;

	@OneToOne()
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@OneToOne()
	@JoinColumn(name = "pension_inst_id")
	private Pensioner pensioner;

	@ManyToOne
	@JoinColumn(name = "pay_period_inst_id")
	private PayPeriod payPeriod;

	@ManyToOne
	@JoinColumn(name = "period_day_inst_id")
	private PayPeriodDays payPeriodDays;


	@Transient private String refType1;
	@Transient private String refType2;
	@Transient private String displayErrors;


	public PaySchedule(Long pId) {
		this.id = pId;
	}


	public boolean isNewEntity() {
		 
		return this.id == null;
	}


}