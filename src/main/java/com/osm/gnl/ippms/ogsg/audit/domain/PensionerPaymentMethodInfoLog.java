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
@Table(name = "ippms_pen_payment_method_info_log")
@SequenceGenerator(name = "penPayMethodAuditSeq", sequenceName = "ippms_pen_payment_method_info_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PensionerPaymentMethodInfoLog extends AbstractPaymentMethodAuditEntity{

    @Id
    @GeneratedValue(generator = "penPayMethodAuditSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "audit_inst_id", unique = true, nullable = false)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Pensioner employee;

    @Override
    public int compareTo(Object pO) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public AbstractEmployeeEntity getEmployeeEntity() {
        return this.employee;
    }
}
