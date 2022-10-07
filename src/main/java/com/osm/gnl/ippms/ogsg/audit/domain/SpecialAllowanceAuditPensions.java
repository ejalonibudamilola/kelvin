/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceInfoPensions;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_pen_special_allow_audit")
@SequenceGenerator(name = "penSpecAllowAuditSeq", sequenceName = "ippms_pen_special_allow_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SpecialAllowanceAuditPensions extends AbstractSpecAllowAuditEntity{


    @Id
    @GeneratedValue(generator = "penSpecAllowAuditSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "special_allow_audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "special_allow_info_inst_id", nullable = false)
    private SpecialAllowanceInfoPensions specialAllowanceInfo;


    @ManyToOne
    @JoinColumn(name = "employee_inst_id",nullable = false)
    private Pensioner pensioner;


    @Override
    public int compareTo(Object pO) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
