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
@Table(name = "ippms_payroll_run")
@SequenceGenerator(name = "payrollRunSeq", sequenceName = "ippms_payroll_run_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayrollRun implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "payrollRunSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "payroll_run_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;


    @Column(name = "last_pay_date", nullable = false)
    private LocalDate lastPayDate;


    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;


    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;


    @Column(name = "payroll_run_date", insertable = false)
    private LocalDate payrollRunDate;

    @Column(name = "payroll_approved", length = 1)
    private String payrollApproved;

    @Column(name = "payroll_approved_by", length = 32)
    private String payrollApprovedBy;


    @Column(name = "payroll_approval_date", nullable = true)
    private LocalDate payrollApprovalDate;


    public PayrollRun(Long pId) {
        this.id = pId;
    }


    public boolean isNewEntity() {
        return this.id == null;
    }
}