/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.hr;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractFlagObject {
    @OneToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner pensioner;

    @Column(name = "run_month", nullable = false)
    private int runMonth;

    @Column(name = "run_year", nullable = false)
    private int runYear;


    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    public abstract boolean isNewEntity();


}
