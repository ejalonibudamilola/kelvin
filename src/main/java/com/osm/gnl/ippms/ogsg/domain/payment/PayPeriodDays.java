/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "ippms_pay_period_days")
@SequenceGenerator(name = "payPeriodDaysSeq", sequenceName = "ippms_pay_period_days_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayPeriodDays  extends AbstractControlEntity
{
    @Id
    @GeneratedValue(generator = "payPeriodDaysSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "pay_period_days_inst_id", nullable = false, unique = true)
	private Long id;
    
    @Column(name = "pay_period_day", length = 100, nullable = false)
    private String name;
	
    @Column(name = "default_ind", columnDefinition = "integer default '0'")
	private int defaultInd;

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public boolean isNewEntity() {
		return this.id == null;
	}
}