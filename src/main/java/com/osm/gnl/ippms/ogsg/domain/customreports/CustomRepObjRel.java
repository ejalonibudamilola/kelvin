/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.customreports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_report_obj_relation")
@SequenceGenerator(name = "reportObjRelSeq", sequenceName = "ippms_report_obj_relation_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class CustomRepObjRel {

    @Id
    @GeneratedValue(generator = "reportObjRelSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "rep_obj_rel_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "m_rep_obj_inst_id",nullable = false)
    private CustomReportObject masterCustomRepObj;

    @ManyToOne
    @JoinColumn(name = "c_rep_obj_inst_id",nullable = false)
    private CustomReportObject childCustomRepObj;

    @Column(name = "m_col_id", nullable = false, length = 40)
    private String masterCol;

    @Column(name = "c_col_id", nullable = false, length = 40)
    private String childCol;
}
