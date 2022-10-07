/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_paycheck_gratuity")
@SequenceGenerator(name = "paycheckGratuitySeq", sequenceName = "ippms_paycheck_gratuity_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaycheckGratuity implements Comparable<PaycheckGratuity>{

    @Id
    @GeneratedValue(generator = "paycheckGratuitySeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_gratuity_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id", nullable = false)
    private Pensioner pensioner;

    @ManyToOne
    @JoinColumn(name = "gratuity_inst_id", nullable = false)
    private GratuityInfo gratuityInfo;

    @Column(name = "paycheck_inst_id", nullable = false)
    private Long parentInstId;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "run_month", nullable = false)
    private int runMonth;

    @Column(name = "run_year", nullable = false)
    private int runYear;

    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;

    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;

    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @Column(name = "amount_paid", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double amount;
    @Column(name = "balance_amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double currBalAmt;

    @Column(name = "gratuity_percentage", columnDefinition = "numeric(5,2) default '0.00'", nullable = false)
    private double gratuityPercentage;

    @Transient
    private String currBalAmtStr;
    @Transient
    private String amountStr;


    public String getAmountStr()
    {
        this.amountStr = PayrollHRUtils.getDecimalFormat().format(this.amount);
        return amountStr;
    }

    public String getCurrBalAmtStr()
    {
        this.currBalAmtStr = PayrollHRUtils.getDecimalFormat().format(this.currBalAmt);
        return currBalAmtStr;
    }
    @Override
    public int compareTo(PaycheckGratuity paycheckGratuity) {
        return 0;
    }
}
