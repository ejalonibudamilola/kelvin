/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payroll;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ippms_global_percent_master")
@SequenceGenerator(name = "globalMasterSeq", sequenceName = "ippms_global_percent_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class GlobalPercentConfig implements Comparable<GlobalPercentConfig> {

    @Id
    @GeneratedValue(generator = "globalMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "global_per_master_inst_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "approver_inst_id")
    private User approver;

    @ManyToOne
    @JoinColumn(name = "creator_inst_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "deactivator_inst_id")
    private User deactivatedBy;

    @Column(name = "creation_date", nullable = false)
    private Timestamp creationDate = Timestamp.from(Instant.now());

    @Column(name = "approval_date")
    private Timestamp approvalDate;

    @Column(name = "deactivation_date")
    private Timestamp deactivationDate;

    @Column(name = "payroll_status", columnDefinition = "integer default 0")
    private int payrollStatus;

    @Column(name = "start_date", nullable = false)
    protected LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    protected LocalDate endDate;

    @Column(name = "global_percentage", columnDefinition = "numeric(5,2) default '0.00'")
    private double globalPercentage;

    @Column(name = "deactivate", columnDefinition = "integer default 0")
    private int deactivateInd;

    @Column(name = "suspension_effect", columnDefinition = "integer default 0")
    private int effectOnSuspensionInd;

    @Column(name = "pay_per_days_effect", columnDefinition = "integer default 0")
    private int payPerDaysEffectInd;

    @Column(name = "loan_effect", columnDefinition = "integer default 0")
    private int loanEffectInd;

    @Column(name = "deduction_effect", columnDefinition = "integer default 0")
    private int deductionEffectInd;

    @Column(name = "spec_allow_effect", columnDefinition = "integer default 0")
    private int specAllowEffectInd;

    @Column(name = "least_no_of_days", columnDefinition = "numeric(2,0) default 0")
    private int leastNoOfDays;

    @Column(name = "apply_mode", columnDefinition = "integer default 0")
    private int applicationMode;


    @Transient
    private String startDateStr;

    @Transient
    private String currentStatus;

    @Transient
    private String createdDateStr;
    @Transient
    private boolean showErrorRow;

    @Transient
    private String applicationModeStr;

    @Transient
    private boolean effectOnSuspensionBind;
    @Transient
    private boolean effectOnSuspension;

    @Transient
    private boolean loanEffectBind;
    @Transient
    private boolean effectOnLoan;

    @Transient
    private boolean deductionEffectBind;
    @Transient
    private boolean effectOnDeduction;

    @Transient
    private boolean specAllowEffectBind;
    @Transient
    private boolean effectOnSpecAllow;

    @Transient
    private boolean confirmation;

    @Transient
    private boolean effectOnTermBind;
    @Transient
    private boolean effectOnTerm;
    @Transient
    private boolean globalApply;


    @Transient
    private String endDateStr;

    @Transient
    private String percentageStr;

    @Transient
    private String globalPercentStr;

    @Transient
    private String hideRow;
    @Transient
    private String hideRow1;
    @Transient
    private String hideRow2;

    @Transient
    private String idStr;
    @Transient
    private String mode;

    @Transient
    private boolean active;

    @Transient
    private boolean fiftyPercentExists;

    @Transient
    private Long salaryTypeId;

    @Transient
    private int mapId;

    @Transient
    private int fromLevel;

    @Transient
    private int toLevel;

    @Transient
    private List<GlobalPercentConfigDetails> configDetailsList;

    @Transient
    private List<SalaryInfo> fromLevelList;

    @Transient
    private List<SalaryInfo> toLevelList;
    @Transient
    private String generatedCaptcha;
    @Transient
    private String enteredCaptcha;
    @Transient
    private boolean captchaError;

    @Transient
    private boolean forApproval;
    @Transient
    private boolean deactivation;
    @Transient
    private boolean forRejection;

    @Transient
    private boolean approved;

    @Transient
    private boolean rejected;

    @Transient
    private boolean deactivated;

    @Transient
    private String approvalDateStr;

    @Transient
    private String deactivationDateStr;


    public GlobalPercentConfig(Long id) {
        this.id = id;
    }


    public boolean isApproved() {
        approved = this.payrollStatus == 1;
        return approved;
    }

    public boolean isRejected() {
        rejected = this.payrollStatus == 2;
        return rejected;
    }

    public boolean isDeactivated() {
        deactivated = this.deactivateInd == 1;
        return deactivated;
    }

    public String getApplicationModeStr() {
        if (this.isGlobalApply())
            applicationModeStr = "Global Value";
        else
            applicationModeStr = "Pay Group Specific";
        return applicationModeStr;
    }

    public List<GlobalPercentConfigDetails> getConfigDetailsList() {
        if (configDetailsList == null) configDetailsList = new ArrayList<>();
        return configDetailsList;
    }

    public boolean isGlobalApply() {
        this.globalApply = this.applicationMode == 2;
        return globalApply;
    }

    public boolean isEffectOnLoan() {
        effectOnLoan = this.loanEffectInd == 1;
        return effectOnLoan;
    }

    public boolean isEffectOnSpecAllow() {
        effectOnSpecAllow = this.specAllowEffectInd == 1;
        return effectOnSpecAllow;
    }

    public boolean isEffectOnSuspension() {
        this.effectOnSuspension = this.effectOnSuspensionInd == 1;
        return effectOnSuspension;
    }

    public boolean isEffectOnTerm() {
        this.effectOnTerm = this.payPerDaysEffectInd == 1;
        return effectOnTerm;
    }

    public boolean isEffectOnDeduction() {
        this.effectOnDeduction = this.deductionEffectInd == 1;
        return effectOnDeduction;
    }

    public String getCurrentStatus() {
        if(this.isActive() && this.payrollStatus == 1)
            currentStatus = "Active";
        else if(this.deactivateInd == 1)
            currentStatus = "Deactivated";
        else if(this.payrollStatus == 0)
            currentStatus = "Awaiting Approval";

        return currentStatus;
    }

    public String getCreatedDateStr() {
        if(this.creationDate != null)
            createdDateStr = PayrollUtils.formatTimeStamp(this.creationDate);
        return createdDateStr;
    }

    public String getApprovalDateStr() {
        if(this.approvalDate != null)
            approvalDateStr  = PayrollHRUtils.getTimeStampDisplayDateFormat().format(this.approvalDate.toInstant());
        return approvalDateStr;
    }

    public String getDeactivationDateStr() {
        if(this.deactivationDate != null)
            deactivationDateStr  = PayrollHRUtils.getTimeStampDisplayDateFormat().format(this.deactivationDate.toInstant());

        return deactivationDateStr;
    }

    public String getStartDateStr() {
        if(this.startDate != null)
            this.startDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.startDate);
        return startDateStr;
    }

    public String getEndDateStr() {
        if(this.endDate != null)
            this.endDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.endDate);
        return endDateStr;
    }

    public boolean isActive() {
        active = this.deactivateInd == 0;
        return active;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public int compareTo(GlobalPercentConfig globalPercentConfig) {
        return 0;
    }
}
