/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.paygroup;

import com.osm.gnl.ippms.ogsg.domain.approval.AbstractApprovalEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractMasterSalaryTemp extends AbstractApprovalEntity {
    @ManyToOne
    @JoinColumn(name = "salary_type_inst_id", nullable = false, referencedColumnName = "salary_type_inst_id")
    private SalaryType salaryType;

}
