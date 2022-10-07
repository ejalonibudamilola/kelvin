/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payroll;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_payroll_master")
@SequenceGenerator(name = "payrollMasterSeq", sequenceName = "ippms_payroll_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayrollRunMasterBean implements Serializable {


    private static final long serialVersionUID = 7357596653160425975L;
    @Id
    @GeneratedValue(generator = "payrollMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "payroll_master_inst_id")
    private Long id;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "global_per_master_inst_id")
    private GlobalPercentConfig globalPercentConfig;

    @ManyToOne
    @JoinColumn(name = "initiator_inst_id", nullable = false)
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "approver_inst_id")
    private User approver;
    @Column(name = "payroll_status", columnDefinition = "integer default 0")
    private int payrollStatus;

    @Column(name = "gratuity_status", columnDefinition = "integer default '0'")
    private int gratuityStatus;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "start_time", length = 20, nullable = false)
    private String startTime;
    @Column(name = "end_time", length = 20)
    private String endTime;

    @Column(name = "global_percent_ind", columnDefinition = "integer default 0")
    private int globalPercentInd;

    @Column(name = "approved_date")
    private LocalDate approvedDate;
    @Column(name = "approved_time", length = 20)
    private String approvedTime;
    @Column(name = "rba_percentage", columnDefinition = "numeric(12,0) default '0.00'")
    private double rbaPercentage;
    @Column(name = "run_month", columnDefinition = "numeric(2,0) default '0'")
    private int runMonth;
    @Column(name = "run_year", columnDefinition = "numeric(4,0) default '0'")
    private int runYear;
    @Column(name = "ignore_pending_ind", columnDefinition = "integer default 0")
    private int ignorePendingInd;



    @Transient
    private String payrollRunTime;
    @Transient
    private String approvalLapseTime;
    @Transient
    private String approvalTime;
    @Transient
    private boolean running;
    @Transient
    private boolean gratuityRunning;
    @Transient
    private boolean payrollError;
    @Transient
    private boolean payrollCancelled;
    @Transient
    private String rbaPercentageStr;
    @Transient
    private boolean approved;
    @Transient
    private String payrollRunStartDateStr;
    @Transient
    private String payrollRunEndDateStr;


    public PayrollRunMasterBean(Long id) {
        this.setId(id);
    }


    public Long getId() {
        return id;
    }


    public boolean isRunning() {
        running = this.payrollStatus == 1;
        return running;
    }

    public boolean isGratuityRunning() {
        this.gratuityRunning = this.gratuityStatus == 1;
        return gratuityRunning;
    }

    public boolean isPayrollError() {
        payrollError = this.payrollStatus == IConstants.ERROR_PAYROLL_IND;
        return payrollError;
    }


    public String getRbaPercentageStr() {
        rbaPercentageStr = this.rbaPercentage + "%";
        return rbaPercentageStr;
    }


    public boolean isPayrollCancelled() {
        payrollCancelled = this.payrollStatus == IConstants.CANCEL_PAYROLL_IND;
        return payrollCancelled;
    }


    public String getApprovalTime() {
        if (this.approvedDate != null)
            approvalTime = PayrollHRUtils.getDisplayDateFormat().format(this.approvedDate) + " " + this.approvedTime;

        return approvalTime;
    }

    public boolean isApproved() {
        approved = approver != null && !approver.isNewEntity();
        return approved;
    }


    public String getPayrollRunStartDateStr() {
        if (this.startDate != null)
            payrollRunStartDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.startDate) + " " + this.startTime;
        return payrollRunStartDateStr;
    }


    public String getPayrollRunEndDateStr() {
        if (this.endDate != null)
            payrollRunEndDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.endDate) + " " + this.endTime;

        return payrollRunEndDateStr;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }


}
