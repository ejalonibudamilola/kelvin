/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractDeductionAuditEntity implements Comparable<Object> {

    @ManyToOne
    @JoinColumn(name = "deduction_type_inst_id")
    protected EmpDeductionType deductionType;

    @Column(name = "deduction_info_inst_id")
    protected Long deductionInfoId;

    @Column(name = "audit_action_type", nullable = false, length = 6)
    private String auditActionType;

    @Column(name = "old_value", nullable = false, length = 50)
    private String oldValue;

    @Column(name = "new_value", nullable = false, length = 50)
    private String newValue;


    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs;

    @Column(name = "pay_period", nullable = false, length = 6)
    private String auditPayPeriod;

    @Column(name = "column_changed", nullable = false, length = 30)
    private String columnChanged;

    @ManyToOne
    @JoinColumn(name = "mda_inst_id", nullable = false)
    private MdaInfo mdaInfo;



    @ManyToOne
    @JoinColumn(name = "school_inst_id")
    private SchoolInfo schoolInfo;

    @ManyToOne
    @JoinColumn(name = "salary_info_inst_id", nullable = false)
    private SalaryInfo salaryInfo;

    @ManyToOne
    @JoinColumn(name = "user_inst_id")
    private User user;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "audit_time", nullable = false, length = 12)
    protected String auditTime;

    @Transient
    private String employeeName;
    @Transient
    private String userName;
    @Transient
    private String mdaName;
    @Transient
    private String mdaCode;
    @Transient
    protected String reinstatedDateStr;
    @Transient
    private String payGroup;
    @Transient
    private String payGroupLevelAndStep;
    @Transient
    protected int payArrearsInd;
    @Transient
    private double arrearsPercentage;
    @Transient
    private String arrearsPercentageStr;
    @Transient
    private String auditTimeStamp;
    @Transient
    private String placeHolder;
    @Transient
    private String changedBy;
    @Transient
    private LocalDate transferDate;
    @Transient
    private String transferDateStr;

    @Transient
    protected final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public String getAuditTimeStamp() {
        if (this.lastModTs != null)
            this.auditTimeStamp = (this.lastModTs.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + " " + this.auditTime);
        return this.auditTimeStamp;
    }

    public String getTransferDate() {
        this.transferDateStr = this.transferDate.format(DateTimeFormatter.ofPattern("MMM dd, YYYY"));
        return  this.transferDateStr;
    }


    public abstract boolean isNewEntity();
    @Transient
    protected String deductType;

    public String getChangedBy() {
        if(this.user != null && !this.user.isNewEntity())
            changedBy = this.user.getActualUserName();
        return changedBy;
    }

    public String getDeductType() {
        if ((deductionType != null) && (!deductionType.isNewEntity())) {
            this.deductType = deductionType.getDescription();
        }

        return this.deductType;
    }


}
