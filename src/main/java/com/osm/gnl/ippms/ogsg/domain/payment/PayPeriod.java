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
@Table(name = "ippms_pay_period")
@SequenceGenerator(name = "payPeriodSeq", sequenceName = "ippms_pay_period_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class PayPeriod extends AbstractControlEntity {
    @Id
    @GeneratedValue(generator = "payPeriodSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "pay_period_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "pay_period_name", length = 12, nullable = false)
    private String name;

    @Column(name = "pay_period_value", nullable = false)
    private int payPeriodValue;

    @Column(name = "displayable", columnDefinition = "numeric(1) default '0'")
    private int defaultInd;

    public PayPeriod(Long id) {
        this.id = id;
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}