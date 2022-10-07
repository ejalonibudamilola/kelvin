/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.paygroup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_del_master_salary_info_temp")
@NoArgsConstructor
@Getter
@Setter
public class DelMasterSalaryTemp extends AbstractMasterSalaryTemp{

    @Id
    @Column(name = "del_master_salary_temp_inst_id", nullable = false, unique = true)
    private Long id;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
