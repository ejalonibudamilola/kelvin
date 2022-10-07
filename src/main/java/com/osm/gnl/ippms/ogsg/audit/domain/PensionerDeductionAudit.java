/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_pen_deduction_audit")
@SequenceGenerator(name = "penDeductTriggAuditSeq", sequenceName = "ippms_pen_deduction_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PensionerDeductionAudit extends AbstractDeductionAuditEntity{
    @Id
    @GeneratedValue(generator = "penDeductTriggAuditSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id", nullable = false)
    private Pensioner employee;


    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
