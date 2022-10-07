/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.configcontrol.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_config_info")
@SequenceGenerator(name = "configBeanSeq", sequenceName = "ippms_config_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ConfigurationBean extends AbstractConfigBean {

    @Id
    @GeneratedValue(generator = "configBeanSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "config_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "max_spec_allow_value", nullable = false, columnDefinition = "numeric (5,2) default '20.00'")
    private double maxSpecAllowValue;

    @Column(name = "est_tax_rate", nullable = false, columnDefinition = "numeric (5,2) default '15.00'")
    private double taxRate;

    @Column(name = "max_loan_rate", nullable = false, columnDefinition = "numeric (5,2) default '65.00'")
    private double maxLoanPercentage;

    @Column(name = "max_ded_value", nullable = false, columnDefinition = "numeric (5,2) default '10.00'")
    private double maxDeductionValue;

    @Column(name = "emp_creator_can_approve", nullable = false, columnDefinition = "integer default '0'")
    private int empCreatorCanApproveInd;

    @Column(name = "am_alive_creator_can_approve", nullable = false, columnDefinition = "integer default '0'")
    private int amAliveCreatorCanApproveInd;

    @Column(name = "step_incrementer_can_approve", nullable = false, columnDefinition = "integer default '0'")
    private int stepIncrementerCanApproveInd;

    @Column(name = "pay_group_creator_can_approve", nullable = false, columnDefinition = "integer default '0'")
    private int payGroupCreatorCanApproveInd;

    @Column(name = "open_global_percent_module", nullable = false, columnDefinition = "integer default '0'")
    private int globalPercentModInd;

    @Column(name = "show_bank_info", nullable = false, columnDefinition = "integer default '0'")
    private int showBankInfoOnPaySlip;

    @Column(name = "show_pikso", nullable = false, columnDefinition = "integer default '1'")
    private int showPiksoOnPaySlip;

    @Column(name = "show_retirement_date", nullable = false, columnDefinition = "integer default '0'")
    private int showRetirementDatePaySlip;

    @Column(name = "must_use_ogs_email", nullable = false, columnDefinition = "integer default '1'")
    private int useOgsEmailInd;

    @Column(name = "cut_off_start_date")
    private LocalDate cutOffStartDate;

    @Column(name = "cut_off_end_date")
    private LocalDate cutOffEndDate;

    @Column(name = "cut_off_start_time", length = 20)
    private String cutOffStartTime;

    @Column(name = "cut_off_end_time", length = 20)
    private String cutOffEndTime;


    @Transient private boolean useOgsEmail;
    @Transient private boolean useOgsEmailBind;


    @Transient private boolean emailRequired;
    @Transient private boolean emailRequiredBind;

    @Transient private boolean empCreatorCanApprove;
    @Transient private boolean amAliveCreatorCanApprove;
    @Transient private boolean stepIncrementerCanApprove;
    @Transient private boolean payGroupCreatorCanApprove;

    @Transient private boolean iamAliveExtBind;
    @Transient private boolean empCreatorCanApproveBind;
    @Transient private boolean amAliveCreatorCanApproveBind;
    @Transient private boolean stepIncrementerCanApproveBind;
    @Transient private boolean payGroupCreatorCanApproveBind;

    @Transient private boolean reqResIdBind;

    @Transient private boolean reqNinBind;
    @Transient private boolean reqBvnBind;
    @Transient private boolean reqStaffEmailBind;

    @Transient private boolean reqBioBind;

    @Transient private boolean globalPercentModOpen;
    @Transient private boolean globalPercentModOpenBind;
    @Transient private boolean showBankInfoOnPaySlipBind;
    @Transient private boolean showBankInfo;
    @Transient private boolean showPikso;
    @Transient private boolean showRetirementDate;
    @Transient private boolean showPiksoOnPaySlipBind;
    @Transient private boolean showRetirementDatePaySlipBind;


    public boolean isUseOgsEmail() {
        useOgsEmail = this.useOgsEmailInd == 1;
        return useOgsEmail;
    }


    public boolean isGlobalPercentModOpen() {
        this.globalPercentModOpen = this.globalPercentModInd == 1;
        return globalPercentModOpen;
    }

    public boolean isEmpCreatorCanApprove() {
        empCreatorCanApprove = this.empCreatorCanApproveInd == 1;
        return empCreatorCanApprove;
    }
    public boolean isAmAliveCreatorCanApprove() {
        amAliveCreatorCanApprove = this.amAliveCreatorCanApproveInd == 1;
        return amAliveCreatorCanApprove;
    }
    public boolean isStepIncrementerCanApprove() {
        stepIncrementerCanApprove = this.stepIncrementerCanApproveInd == 1;
        return stepIncrementerCanApprove;
    }

    public boolean isPayGroupCreatorCanApprove() {
        payGroupCreatorCanApprove = this.payGroupCreatorCanApproveInd == 1;
        return payGroupCreatorCanApprove;
    }

    public int getAgeAtRetirement() {
        return ageAtRetirement;
    }

    public boolean isShowBankInfo() {
        this.showBankInfo = this.showBankInfoOnPaySlip == 1;
        return showBankInfo;
    }

    public boolean isShowPikso() {
        showPikso = this.showPiksoOnPaySlip == 1;
        return showPikso;
    }

    public boolean isShowRetirementDate() {
        showRetirementDate = this.showRetirementDatePaySlip == 1;
        return showRetirementDate;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
