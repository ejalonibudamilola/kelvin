/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_client_pay_sched")
@SequenceGenerator(name = "clientPaySchedSeq", sequenceName = "ippms_client_pay_sched_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class BusinessPaySchedule extends AbstractControlEntity {

    @Id
    @GeneratedValue(generator = "clientPaySchedSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "client_pay_sched_inst_id")
    private Long id;


    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "pay_period_inst_id")
    private PayPeriod payPeriod;

    @ManyToOne
    @JoinColumn(name = "period_day_inst_id")
    private PayPeriodDays payPeriodDays;

    @Column(name = "work_period_inst_id")
    private int workPeriodInstId;
    @Column(name = "rel_period_inst_id")
    private int workPeriodEffective;
    @Column(name = "work_period_days")
    private int workPeriodDays;
    @Column(name = "period_day_inst_id_2")
    private Long periodDayInstId2;
    @Column(name = "work_period_inst_id_2")
    private int workPeriodInstId2;
    @Column(name = "rel_period_inst_id_2")
    private int workPeriodEffective2;
    @Column(name = "work_period_days_2")
    private int workPeriodDays2;


    @Column(name = "pay_start_date")
    private LocalDate payStartDate;

    @Column(name = "last_pay_date")
    private LocalDate lastPayDate;

    @Column(name = "use_as_default", nullable = false, columnDefinition = "VARCHAR(1)  default 'N'")
    private String useAsDefault;


    @Transient private String refType1;
    @Transient private String refType2;
    @Transient private String payPeriodName;
    @Transient private String descr;
    @Transient private String frequency;
    @Transient private String mode;


    public boolean isDefault() {
        return getUseAsDefault().equalsIgnoreCase("y");
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }


}