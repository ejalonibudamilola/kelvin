/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_pay_sim_details")
@Getter
@Setter
@NoArgsConstructor
public class PayrollSimulationDetailsBean {

    @Id
    @Column(name = "pay_sim_master_inst_id")
    private Long id;

    @Column(name = "apply_to_base_year", columnDefinition = "numeric")
    private int applyToBaseYear;
    @Column(name = "apply_to_spillover", columnDefinition = "numeric")
    private int applyToSpillOverYear;
    @Column(name = "dev_levy_ind1", columnDefinition = "numeric")
    private int devLevyInd1;
    @Column(name = "dev_levy_ind2", columnDefinition = "numeric")
    private int devLevyInd2;
    @Column(name = "apply_ltg_ind1", columnDefinition = "numeric")
    private int applyLtgInd1;
    @Column(name = "apply_ltg_ind2", columnDefinition = "numeric")
    private int applyLtgInd2;
    @Column(name = "deduct_dev_levy_month1", columnDefinition = "numeric")
    private int deductDevLevyMonth1;
    @Column(name = "deduct_dev_levy_month2", columnDefinition = "numeric")
    private int deductDevLevyMonth2;
    @Column(name = "step_increment", columnDefinition = "numeric")
    private int stepIncrement;
    @Column(name = "apply_ltg_details1", length = 500)
    private String applyLtgDetails1;
    @Column(name = "apply_ltg_details2", length = 500)
    private String applyLtgDetails2;
    @Column(name = "promotion_ind", columnDefinition = "numeric")
    private int promotionsInd;

    @Transient
    private boolean deductDevLevyForYear1;
    @Transient
    private boolean deductDevLevyForYear2;
    @Transient
    private boolean applyLtgForYear1;
    @Transient
    private boolean applyLtgForYear2;
    @Transient
    private boolean applyPromotions;
    @Transient
    private boolean doStepIncrement;


    public PayrollSimulationDetailsBean(Long pId) {
        this.id = pId;
    }

    public boolean isDeductDevLevyForYear1() {
        return this.devLevyInd1 == 1;
    }

    public boolean isDeductDevLevyForYear2() {

        return this.devLevyInd2 == 1;
    }

    public boolean isApplyLtgForYear1() {
        return this.applyLtgInd1 == 1;
    }

    public boolean isApplyLtgForYear2() {
        return this.applyLtgInd2 == 1;
    }

    public boolean isApplyPromotions() {
        return this.promotionsInd == 1;
    }

    public boolean isDoStepIncrement() {
        return this.stepIncrement == 1;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }
}