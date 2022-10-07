/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.promotion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_mass_reassign_job")
@SequenceGenerator(name = "massReassignJobSeq", sequenceName = "ippms_mass_reassign_job_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MassReassignBean extends AbstractJobBean{


    @Id
    @GeneratedValue(generator = "massReassignJobSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "mass_reassign_job_inst_id", nullable = false, unique = true,updatable = false)
    private Long id;

    @Column(name = "from_salary_type_inst_id", nullable =  false)
    private Long fromSalaryId;

    @Column(name = "to_salary_type_inst_id", nullable =  false)
    private Long toSalaryId;

    public MassReassignBean(Long pId) {
        this.id = pId;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }


}
