/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractTriggerAuditEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractSpecAllowAuditEntity extends AbstractTriggerAuditEntity {


    @Transient
    private String allowanceType;

    @Transient
    private Employee employee;



}
