/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_gratuity_info")
@SequenceGenerator(name = "gratuitySeq", sequenceName = "ippms_gratuity_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class GratuityInfo extends AbstractControlEntity {

    @Id
    @GeneratedValue(generator = "gratuitySeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "gratuity_inst_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "pensioner_inst_id", nullable = false)
    private Pensioner pensioner;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "gratuity", columnDefinition = "numeric(15,2) default '0.00'")
    private double gratuityAmount;

    @Column(name = "outstanding_payment", columnDefinition = "numeric(15,2) default '0.00'")
    private double outstandingAmount;

    @Column(name = "gratuity_month", nullable = false)
    private int month;
    @Column(name = "gratuity_year", nullable = false)
    private int year;

    public GratuityInfo(Long id) {
        this.id = id;
    }

    @Transient
    private String gratuityAmountStr;
    @Transient
    private String outstandingAmountStr;
    @Transient
    private String difference;


    public String getGratuityAmountStr() {
        gratuityAmountStr = PayrollHRUtils.getDecimalFormat().format(this.getGratuityAmount());
        return gratuityAmountStr;
    }
    public String getDifference()
    {
        this.difference = PayrollHRUtils.getDecimalFormat().format(this.gratuityAmount - this.outstandingAmount);
        return difference;
    }
    public String getOutstandingAmountStr()
    {
        this.outstandingAmountStr = PayrollHRUtils.getDecimalFormat().format(this.outstandingAmount);
        return outstandingAmountStr;
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
