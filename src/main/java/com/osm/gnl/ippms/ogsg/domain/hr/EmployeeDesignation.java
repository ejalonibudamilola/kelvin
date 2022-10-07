/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_employee_designation")
@SequenceGenerator(name = "designationSeq", sequenceName = "ippms_employee_designation_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmployeeDesignation extends AbstractDescControlEntity {

    @Id
    @GeneratedValue(generator = "designationSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "emp_designation_inst_id")
    private Long id;

    @Column(name = "semaphore_1")
    private int semaphore1;

    @Column(name = "semaphore_2")
    private int semaphore2;

    public EmployeeDesignation(Long staffDesignationId) {
        this.id = staffDesignationId;
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
