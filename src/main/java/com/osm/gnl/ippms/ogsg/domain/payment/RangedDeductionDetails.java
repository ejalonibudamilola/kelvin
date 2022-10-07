/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ippms_deduction_range_details" )
@SequenceGenerator(name = "rangedDedDetSeq", sequenceName = "ippms_deduction_range_details_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class RangedDeductionDetails {

    @Id
    @GeneratedValue(generator = "rangedDedDetSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ranged_ded_det_inst_id", nullable = false,unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ranged_ded_inst_id", nullable = false)
    private RangedDeduction rangedDeduction;

    @Column(name = "lower_bound", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double lowerBound;

    @Column(name = "upper_bound", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double upperBound;

    @Column(name = "amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double amount;

    @Transient
    private String lowerBoundAsStr;

    @Transient
    private String upperBoundAsStr;

    @Transient
    private String displayStyle;

    @Transient
    private String amountAsStr;


    @Transient
    private String lowerBoundStr;

    @Transient
    private String upperBoundStr;

    @Transient
    private String amountStr;

    @Transient
    private String entryIndex;

    @Transient
    private String remove = "Remove...";

    public String getLowerBoundStr() {
        lowerBoundStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.lowerBound);
        return lowerBoundStr;
    }

    public String getUpperBoundStr() {
        upperBoundStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.upperBound);
        return upperBoundStr;
    }

    public String getAmountStr() {
        this.amountStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.amount);
        return amountStr;
    }

    public String getEntryIndex() {
        entryIndex = this.lowerBound+":"+this.upperBound;
        return entryIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RangedDeductionDetails)) return false;
        RangedDeductionDetails that = (RangedDeductionDetails) o;
        return Double.compare(that.getLowerBound(), getLowerBound()) == 0 &&
                Double.compare(that.getUpperBound(), getUpperBound()) == 0 &&
                Double.compare(that.getAmount(), getAmount()) == 0 &&
                getId().equals(that.getId()) &&
                getRangedDeduction().equals(that.getRangedDeduction());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRangedDeduction(), getLowerBound(), getUpperBound(), getAmount());
    }
}
