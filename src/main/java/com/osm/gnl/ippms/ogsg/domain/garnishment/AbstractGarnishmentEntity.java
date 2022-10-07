/*
 * Copyright (c) 2020. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.garnishment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractGarnishmentEntity extends AbstractControlEntity {


    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner pensioner;


    @ManyToOne
    @JoinColumn(name = "garn_type_inst_id", nullable = false)
    protected EmpGarnishmentType empGarnishmentType;

    @Column(name = "garnish_cap", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double garnishCap;

    @Column(name = "business_client_inst_id",  nullable = false)
    protected Long businessClientId;

    @Column(name = "amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double amount;

    @Column(name = "owed_amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double owedAmount;

    @Column(name = "monthly_interest", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double interestAmount;

    @Column(name = "original_amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double originalLoanAmount;

    @Column(name = "start_date", nullable = false)
    protected LocalDate startDate;

    @Column(name = "end_date")
    protected LocalDate endDate;

    @Column(name = "run_trig_ind", columnDefinition = "integer default '0'", nullable = false)
    protected int runTrigInd;

    @Column(name = "loan_term", columnDefinition = "numeric(3,0) default '0'", nullable = false)
    protected int loanTerm;
    @Column(name = "government_loan", columnDefinition = "integer default '0'", nullable = false)
    protected int govtLoan;

    @Column(name = "deduct_separately", columnDefinition = "integer default '0'", nullable = false)
    protected int deductInterestSeparatelyInd;


    @Column(name = "no_of_payments", columnDefinition = "numeric(3,0) default '0'", nullable = false)
    protected int currentLoanTerm;

    @Column(name = "description", columnDefinition = "varchar(120)", nullable = false)
    protected String description;

    @Transient
    protected double currentOwedAmount;
    @Transient protected double actGarnAmt;
    @Transient protected double actGarnAmtStr;
    @Transient protected String owedAmountStr;
    @Transient protected String name;
    @Transient protected String owedAmountAsStr;
    @Transient protected String garnishAmountStr;
    @Transient protected String garnishAmountAsStr;
    @Transient protected Long empGarnishTypeInstId;
    @Transient protected int empPayTypeInstId;
    @Transient protected boolean edit;
    @Transient protected boolean delete;
    @Transient protected double currentGarnishment;
    @Transient protected String originalLoanAmountStr;
    @Transient protected String accountNumber;
    @Transient protected String sortCode;
    @Transient protected String interestAmountStr;
    @Transient protected String interestAmountAsStr;
    @Transient protected String govtLoanIndStr;
    @Transient protected boolean govermentLoan;
    @Transient protected String deductSeparatelyRef;
    @Transient protected boolean deductSeparately;
    @Transient protected int oldLoanTerm;
    @Transient protected double oldOwedAmount;
    @Transient protected boolean editDenied;
    @Transient protected boolean viewOnlyMode;
    @Transient protected int oldCurrentLoanTerm;
    @Transient protected int newLoanTerm;
    @Transient protected Long negativePayId;
    @Transient protected double oldGarnishAmount;
    @Transient protected boolean hasNegPay;
    @Transient protected boolean sortByAmount;
    @Transient protected double tenor;
    @Transient private Long parentId;


    public abstract Long getId();
    public abstract void setId(Long pId);
    public boolean isPensioner(){
        return this.pensioner != null && !this.pensioner.isNewEntity();
    }

    public Long getParentId() {
        if(this.pensioner != null && !this.pensioner.isNewEntity())
            parentId = this.pensioner.getId();
        else if(this.employee != null && !this.employee.isNewEntity())
            parentId =this.employee.getId();
        return parentId;
    }

    public String getOriginalLoanAmountStr() {
        this.originalLoanAmountStr = PayrollHRUtils.getDecimalFormat().format(this.originalLoanAmount);
        return originalLoanAmountStr;
    }

    public String getGarnishAmountAsStr() {
        this.garnishAmountAsStr = PayrollHRUtils.getDecimalFormat().format(this.amount);
        return garnishAmountAsStr;
    }


    public String getOwedAmountAsStr() {
        this.owedAmountAsStr = PayrollHRUtils.getDecimalFormat().format(this.owedAmount);
        return owedAmountAsStr;
    }

    public boolean isHasNegPay() {
        return this.negativePayId != null && this.negativePayId > 0L;
    }

    @Override
    public int compareTo(Object pIncoming) {

        if(pIncoming != null && pIncoming.getClass().isAssignableFrom(this.getClass())) {

            if (this.sortByAmount) {
                if (this.amount > ((AbstractGarnishmentEntity) pIncoming).amount)
                    return 1;
                else if (this.amount < ((AbstractGarnishmentEntity) pIncoming).amount)
                    return -1;
                /*
                 * else return 0;
                 */
            } else {
                if ((getEmpGarnishmentType() != null) && (((AbstractGarnishmentEntity) pIncoming).getEmpGarnishmentType() != null)) {
                    return getEmpGarnishmentType().getName()
                            .compareToIgnoreCase(((AbstractGarnishmentEntity) pIncoming).getEmpGarnishmentType().getName());
                }
            }
        }
        return 0;
    }

}
