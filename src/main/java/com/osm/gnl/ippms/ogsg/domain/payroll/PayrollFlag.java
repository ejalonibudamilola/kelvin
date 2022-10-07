/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payroll;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_payroll_flag")
@SequenceGenerator(name = "payrollFlagSeq", sequenceName = "ippms_payroll_flag_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class PayrollFlag implements Serializable {
    private static final long serialVersionUID = -9218771430722689131L;

    @Id
    @GeneratedValue(generator = "payrollFlagSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "payroll_flag_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "payroll_master_inst_id")
    private PayrollRunMasterBean payrollRunMasterBean;


    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;


    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @Column(name = "approved_month", columnDefinition = "numeric(2,0)", nullable = false)
    private int approvedMonthInd;

    @Column(name = "approved_year", columnDefinition = "numeric(4,0)", nullable = false)
    private int approvedYearInd;


    public boolean isNewEntity() {
        return this.id == null;
    }


}