/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.abstractentities;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractPaycheckGarnishmentEntity implements Serializable, Comparable<AbstractPaycheckGarnishmentEntity> {


    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;


    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;


    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @Column(name = "interest_paid", columnDefinition = "numeric(15,2) default '0.00'")
    private double interestPaid;

    @Column(name = "sort_code", length = 20, nullable = false)
    private String sortCode;

    @Column(name = "account_number", length = 20)
    private String accountNumber;

    @Column(name = "pre_garn_amt", columnDefinition = "numeric(15,2) default '0.00'")
    private double startingLoanBalance;

    @Column(name = "aft_garn_amt", columnDefinition = "numeric(15,2) default '0.00'")
    private double aftGarnBal;

    @Column(name = "run_month", columnDefinition = "numeric", nullable = false)
    private int runMonth;

    @Column(name = "run_year", columnDefinition = "numeric", nullable = false)
    private int runYear;

    @Column(name = "amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double amount;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;


    @Transient private String amountStr;
    @Transient private String payDateStr;
    @Transient private String displayStyle;
    @Transient private Long employeeInstId;
    @Transient private String titleField;
    @Transient private String payPeriodStr;
    @Transient private double deductionAmount;
    @Transient private String deductionAmountStr;
    @Transient private String startingLoanBalanceStr;
    @Transient private String name;
    @Transient protected final DecimalFormat df = new DecimalFormat("#,##0.00##");
    @Transient private String schoolName;
    @Transient private String mdaName;


    @Transient private double loanBalance;
    @Transient private String loanBalanceStr;
    @Transient private String loanBalanceStrSansNaira;
    @Transient private String interestPaidStr;
    @Transient private String interestPaidStrSansNaira;

    public String getPayDateStr() {
        if(this.payDate != null)
            this.payDateStr = PayrollHRUtils.getFullDateFormat().format(this.payDate);
        return payDateStr;
    }

    public String getStartingLoanBalanceStr() {
       this.startingLoanBalanceStr = (IConstants.naira + df.format(this.startingLoanBalance));
        return startingLoanBalanceStr;
    }

    public String getAmountStr() {
        this.amountStr = (IConstants.naira + df.format(this.amount));
        return amountStr;
    }

    public String getPayPeriodStr() {
        if(this.payPeriodStart != null && this.payPeriodEnd != null)
            this.payPeriodStr = PayrollHRUtils.getFullDateFormat().format(this.payPeriodStart)+" - "+PayrollHRUtils.getFullDateFormat().format(this.payPeriodEnd);
        return payPeriodStr;
    }

    public String getLoanBalanceStr() {
        this.loanBalanceStr = (IConstants.naira + df.format(this.loanBalance));
        return this.loanBalanceStr;
    }

    public String getLoanBalanceStrSansNaira() {
        return df.format(this.loanBalance);
    }

    public String getInterestPaidStr() {
        this.interestPaidStr = (IConstants.naira + df.format(this.interestPaid));
        return this.interestPaidStr;
    }

    public String getInterestPaidStrSansNaira() {
        this.interestPaidStrSansNaira = df.format(this.interestPaid);
        return this.interestPaidStrSansNaira;
    }


    public abstract boolean isNewEntity();
    public abstract void setId(Long pId);

    @Override
    public int compareTo(AbstractPaycheckGarnishmentEntity abstractGarnishmentEntity) {
        return 0;
    }

    public abstract void setEmpGarnInfo(AbstractGarnishmentEntity abstractGarnishmentEntity);
    public abstract AbstractGarnishmentEntity getEmpGarnInfo();
    public abstract void setEmployee(AbstractEmployeeEntity abstractEmployeeEntity);
    public abstract AbstractEmployeeEntity getEmployee();
    public abstract String getEmployeeName();
}
