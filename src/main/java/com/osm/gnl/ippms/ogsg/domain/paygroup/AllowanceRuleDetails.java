/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.paygroup;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ippms_allow_rule_details" )
@SequenceGenerator(name = "allowRuleDetSeq", sequenceName = "ippms_allow_rule_details_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class AllowanceRuleDetails {

    @Id
    @GeneratedValue(generator = "allowRuleDetSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "allow_rule_det_inst_id", nullable = false,unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rule_master_inst_id", nullable = false)
    private AllowanceRuleMaster allowanceRuleMaster;

    @Column(name = "bean_field_name", nullable = false)
    private String beanFieldName;

    @Column(name = "monthly_value", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double monthlyValue;

    @Column(name = "yearly_value", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double yearlyValue;

    @Column(name = "apply_monthly_value", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double applyMonthlyValue;

    @Column(name = "apply_yearly_value", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double applyYearlyValue;

    @Transient private String monthlyValueAsStr;
    @Transient private String yearlyValueAsStr;
    @Transient private String displayStyle;
    @Transient private String monthlyValueStr;
    @Transient private String yearlyValueStr;
    @Transient private String applyYearlyValueStr;
    @Transient private String applyMonthlyValueStr;
    @Transient private String entryIndex;
    @Transient private String amountStr;
    @Transient private String remove = "Remove...";

    public String getApplyYearlyValueStr() {
        applyYearlyValueStr = IConstants.naira+ PayrollHRUtils.getDecimalFormat().format(this.applyYearlyValue);
        return applyYearlyValueStr;
    }

    public String getApplyMonthlyValueStr() {
        applyMonthlyValueStr  = IConstants.naira+ PayrollHRUtils.getDecimalFormat().format(this.applyMonthlyValue);
        return applyMonthlyValueStr;
    }

    public String getMonthlyValueAsStr() {
        monthlyValueAsStr = PayrollHRUtils.getDecimalFormat().format(this.monthlyValue);
        return monthlyValueAsStr;
    }

    public String getYearlyValueAsStr() {
        yearlyValueAsStr = PayrollHRUtils.getDecimalFormat().format(this.yearlyValue);
        return yearlyValueAsStr;
    }
    public String getMonthlyValueStr() {
        monthlyValueStr = IConstants.naira+ PayrollHRUtils.getDecimalFormat().format(this.monthlyValue);
        return monthlyValueStr;
    }

    public String getYearlyValueStr() {
        yearlyValueStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.yearlyValue);
        return yearlyValueStr;
    }
    public String getEntryIndex() {
        entryIndex = this.beanFieldName+":"+this.monthlyValue;
        return entryIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AllowanceRuleDetails)) return false;
        AllowanceRuleDetails that = (AllowanceRuleDetails) o;
        return Double.compare(that.getMonthlyValue(), getMonthlyValue()) == 0 &&
                Double.compare(that.getYearlyValue(), getYearlyValue()) == 0 &&
                getId().equals(that.getId()) &&
                getAllowanceRuleMaster().equals(that.getAllowanceRuleMaster()) &&
                getBeanFieldName().equals(that.getBeanFieldName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAllowanceRuleMaster(), getBeanFieldName(), getMonthlyValue(), getYearlyValue());
    }
}
