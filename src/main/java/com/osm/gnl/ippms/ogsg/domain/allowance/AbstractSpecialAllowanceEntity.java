/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.allowance;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractSpecialAllowanceEntity extends AbstractDescControlEntity {




    @ManyToOne
    @JoinColumn(name = "pay_types_inst_id", nullable = false)
    private PayTypes payTypes;

    @ManyToOne
    @JoinColumn(name = "spec_allow_type_inst_id", nullable = false)
    private SpecialAllowanceType specialAllowanceType;

    @Column(name = "amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double amount;


    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;


    @Column(name = "end_date")
    private LocalDate endDate;


    @Column(name = "reference_date")
    private LocalDate referenceDate;

    @ManyToOne
    @JoinColumn(name = "expired_by")
    private User expiredBy;

    @Column(name = "expired", columnDefinition = "integer default '0'")
    private int expire;

    @Column(name = "reference_number", nullable = false, length = 50 )
    private String referenceNumber;

    @Column(name = "business_client_inst_id",  nullable = false)
    private Long businessClientId;

    @Transient protected Long parentId;
    @Transient private String amountStr;
    @Transient private String name;
    @Transient private String amountAsStr;
    @Transient private double actAllowAmt;
    @Transient private String actAllowAmtStr;
    @Transient private String endDateStr;
    @Transient private boolean expired;
    @Transient private boolean warningIssued;
    @Transient private String showForConfirm;
    @Transient private boolean deleteMode;
    @Transient private String startDateStr;
    @Transient private boolean hasPaycheckInfo;
    @Transient private String oldSalaryLevelAndStep;
    @Transient private int entryIndex;
    @Transient protected AbstractEmployeeEntity parentObject;
    @Transient private Employee employee;
    @Transient protected Long payTypeInstId;
    @Transient protected Long typeInstId;

    public abstract Long getId();

    public abstract void setId(Long pId);

    public abstract Long getParentId();

    public abstract AbstractEmployeeEntity getParentObject();

    public boolean isExpired()
    {
        this.expired = (this.expire == 1);
        return this.expired;
    }


    public String getStartDateStr()
    {
        this.startDateStr = PayrollHRUtils.getDateFormat().format(this.startDate);
        return this.startDateStr;
    }

    public String getEndDateStr()
    {
        if(this.endDate != null){
            this.endDateStr = PayrollHRUtils.getDateFormat().format(this.endDate);
        }
        return endDateStr;
    }


    public String getAmountAsStr()
    {
        amountAsStr = PayrollHRUtils.getDecimalFormat().format(this.amount);
        return amountAsStr;
    }

    public abstract void setParentObject(AbstractEmployeeEntity abstractEmployeeEntity);

    @Override
    public int compareTo(Object o) {
        return 0;
    }


    public abstract void makeParentObject(Long parentId);
}
