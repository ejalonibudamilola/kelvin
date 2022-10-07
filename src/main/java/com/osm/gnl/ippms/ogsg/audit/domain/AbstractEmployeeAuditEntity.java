/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractTriggerAuditEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEmployeeAuditEntity extends AbstractTriggerAuditEntity {

    @Override
    public int compareTo(Object pO) {
        return 0;
    }

    @Transient
    private AbstractEmployeeEntity parentObject;

    public abstract AbstractEmployeeEntity getParentObject();
    public abstract boolean isPensionerEntity();


}
