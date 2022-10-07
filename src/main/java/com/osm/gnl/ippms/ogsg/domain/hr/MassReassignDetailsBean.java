/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.hr;

import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_mass_reassign_details")
@SequenceGenerator(name = "massReassignDetailsSeq", sequenceName = "ippms_mass_reassign_details_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MassReassignDetailsBean implements Comparable<MassReassignDetailsBean> {

    @Id
    @GeneratedValue(generator = "massReassignDetailsSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "mass_reassign_det_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mass_reassign_inst_id",nullable = false)
    private MassReassignMasterBean massReassignMasterBean;

    @Column(name = "parent_id", columnDefinition = "numeric")
    private Long parentId;

    @Column(name = "employee_id", columnDefinition = "varchar(20)", nullable = false)
    private String staffId;

    @Column(name = "staff_name", columnDefinition = "varchar(200)", nullable = false)
    private String staffName;

    @Column(name = "mda_name", columnDefinition = "varchar(200)", nullable = false)
    private String mdaName;

    @Column(name = "monthly_implication", columnDefinition = "varchar(20)")
    private String monthlyImplication;

    @ManyToOne
    @JoinColumn(name = "from_salary_info_inst_id",nullable = false)
    private SalaryInfo fromSalaryInfo;

    @ManyToOne
    @JoinColumn(name = "to_salary_info_inst_id",nullable = false)
    private SalaryInfo toSalaryInfo;

    @Column(name = "reassign_status", columnDefinition = "integer default '0'", nullable = false)
    private int status;

    @Transient
    private boolean errorRecord;

    @Transient
    private String levelAndStep;

    public boolean isErrorRecord() {
        errorRecord = this.status == 1;
        return errorRecord;
    }

    @Override
    public int compareTo(MassReassignDetailsBean massReassignDetailsBean) {
        return 0;
    }
}
