/*
 * Copyright (c) 2021. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payroll;

import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_global_percent_details")
@SequenceGenerator(name = "globalMasterDetSeq", sequenceName = "ippms_global_percent_details_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class GlobalPercentConfigDetails {

    @Id
    @GeneratedValue(generator = "globalMasterDetSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "global_per_details_inst_id")
    private Long id;


    @Column(name = "global_per_master_inst_id", nullable = false)
    private Long parentId;


    @Column(name = "salary_type_inst_id", nullable = false)
    private Long salaryTypeId;

    @Column(name = "salaryTypeName", columnDefinition = "varchar(40)")
    private String salaryTypeName;

    @Column(name = "from_level", columnDefinition = "numeric(2,0) default 0")
    private int fromLevel;

    @Column(name = "to_level", columnDefinition = "numeric(2,0) default 0")
    private int toLevel;

    @Column(name = "pay_percentage", columnDefinition = "numeric(5,2) default '0.00'")
    private double payPercentage;

    @Column(name = "no_of_staffs", columnDefinition = "numeric(5,0) default 0")
    private int noOfStaffs;

    @Transient
    private String payPercentageStr;

    @Transient
    private String remove = "Remove...";

    @Transient
    private String entryIndex;

    public String getPayPercentageStr() {
        payPercentageStr = PayrollHRUtils.getDecimalFormat().format(this.payPercentage);
        return payPercentageStr;
    }
}
