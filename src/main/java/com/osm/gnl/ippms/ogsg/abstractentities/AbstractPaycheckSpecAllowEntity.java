/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.abstractentities;

import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractPaycheckSpecAllowEntity implements Serializable, Comparable<AbstractPaycheckSpecAllowEntity>{


    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;


    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;


    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @Column(name = "amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double amount;

    @Column(name = "run_month", columnDefinition = "numeric", nullable = false)
    private int runMonth;

    @Column(name = "run_year", columnDefinition = "numeric", nullable = false)
    private int runYear;


    @Transient private String amountStr;
    @Transient private String payDateStr;
    @Transient private String displayStyle;
    @Transient private Long employeeInstId;
    @Transient private String titleField;
    @Transient private String payPeriodStr;
    @Transient private double deductionAmount;
    @Transient private String deductionAmountStr;
    @Transient private String name;
    @Transient private Object parentObject;
    @Transient private Long parentObjectInstId;
    @Transient protected final DecimalFormat df = new DecimalFormat("#,##0.00##");
    @Transient private String employeeId;
    @Transient private String employeeName;
    @Transient private String mdaName;

    public String getPayDateStr() {
        if (this.payDate != null)
            this.payDateStr = PayrollHRUtils.getFullDateFormat().format(this.payDate);
        return this.payDateStr;
    }

    public String getPayPeriodStr() {
        return PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(this.runMonth, this.runYear);
    }

    public abstract boolean isNewEntity();
    public abstract Long getId();
    public abstract void setId(Long pId);
    public abstract Object getParentObject();

    public abstract void setSpecialAllowanceInfo(AbstractSpecialAllowanceEntity abstractPaycheckSpecAllowEntity);
    public abstract AbstractSpecialAllowanceEntity getSpecialAllowanceInfo();

}
