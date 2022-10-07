/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.promotion;


import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_increment_tracker")
@SequenceGenerator(name = "stepIncSeq", sequenceName = "ippms_increment_tracker_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class StepIncrementTracker{


    @Id
    @GeneratedValue(generator = "stepIncSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "step_increment_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "yearly_increment_ind" , columnDefinition = "integer default '0'")
    private int noOfTimes;

    @Column(name = "year" , columnDefinition = "numeric(4,0) ", nullable = false)
    private int year;

    public StepIncrementTracker(Long pId){
        this.id = pId;
    }
    public boolean isNewEntity() {
        return this.id == null;
    }
}
