/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractPaymentInfoEntity {

    @ManyToOne
    @JoinColumn(name = "pay_info_type_inst_id", nullable = false)
    private PayInfoType payInfoType;

    @ManyToOne
    @JoinColumn(name = "frequency_inst_id", nullable = false)
    private PayFrequency payFrequency;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "rate", columnDefinition = "numeric(10,2) default '0.00'" ,nullable = false)
    private double rate = 0.0D;
    @Column(name = "salary", columnDefinition = "numeric(10,2) default '0.00'" ,nullable = false)
    private double salary = 0.0D;

    @Column(name = "hrs_worked_per_day", columnDefinition = "numeric(4,2) default '0.00'" ,nullable = false)
    private double hoursWorkedPerDay = 0.0D;
    @Column(name = "days_worked_per_week", columnDefinition = "numeric(2,1) default '0.0'" ,nullable = false)
    private double daysWorkedPerWeek = 0.0D;
    @Column(name = "overtime", columnDefinition = "VARCHAR(1) default 'N'" ,nullable = false)
    private String overtime;
    @Column(name = "double_overtime", columnDefinition = "VARCHAR(1) default 'N'" ,nullable = false)
    private String doubleOvertime;
    @Column(name = "sick_pay", columnDefinition = "VARCHAR(1) default 'N'" ,nullable = false)
    private String sickPay;
    @Column(name = "vacation_pay", columnDefinition = "VARCHAR(1) default 'N'" ,nullable = false)
    private String vacationPay;
    @Column(name = "holiday_pay", columnDefinition = "VARCHAR(1) default 'N'" ,nullable = false)
    private String holidayPay;
    @Column(name = "bonus", columnDefinition = "VARCHAR(1) default 'N'" ,nullable = false)
    private String bonus;
    @Column(name = "commission", columnDefinition = "VARCHAR(1) default 'N'" ,nullable = false)
    private String commission;
    @Transient
    private String salaryRef;
    @Transient
    private int hourlyPayType;
    @Transient
    private int salaryPayType;
    @Transient
    private int commissionPayType;
    @Column(name = "allowance", columnDefinition = "numeric(8,2) default '0.00'" ,nullable = false)
    private double allowance = 0.0D;
    @Column(name = "reimbursement", columnDefinition = "numeric(8,2) default '0.00'" ,nullable = false)
    private double reimbursement = 0.0D;
    @Column(name = "per_diem", columnDefinition = "numeric(8,2) default '0.00'" ,nullable = false)
    private double perDiem = 0.0D;
    @Column(name = "group_term_life_insurance", columnDefinition = "numeric(8,2) default '0.00'" ,nullable = false)
    private double groupTermLifeInsurance = 0.0D;
    @Column(name = "other_earnings", columnDefinition = "numeric(8,2) default '0.00'" ,nullable = false)
    private double otherEarnings = 0.0D;
    @Column(name = "other_earnings_2", columnDefinition = "numeric(8,2) default '0.00'" ,nullable = false)
    private double otherEarnings2 = 0.0D;
    @Transient
    private boolean allowanceRef;
    @Transient
    private boolean reimbursementRef;
    @Transient
    private boolean perDiemRef;
    @Transient
    private boolean groupTermLifeInsuranceRef;
    @Transient
    private boolean otherEarningsRef;
    @Transient
    private boolean otherEarnings2Ref;
    @Transient
    private Long parentInstId;
    @Transient
    private Long parentId;
    @Transient
    private String displayErrors;
    @Transient
    private AbstractEmployeeEntity abstractEmployee;

    public abstract Long getParentInstId();
    public abstract boolean isNewEntity();
    public abstract AbstractEmployeeEntity getAbstractEmployee();
}
