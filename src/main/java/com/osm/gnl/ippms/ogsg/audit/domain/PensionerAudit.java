/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_pensioner_audit")
@SequenceGenerator(name = "penTriggAuditSeq", sequenceName = "ippms_pensioner_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PensionerAudit extends AbstractEmployeeAuditEntity {

    @Id
    @GeneratedValue(generator = "penTriggAuditSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner employee;

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public AbstractEmployeeEntity getParentObject() {
        return this.employee;
    }

    @Override
    public boolean isPensionerEntity() {
        return true;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
