/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;


@Entity
@Table(name = "ippms_paychecks_lg_info")
@SequenceGenerator(name = "paycheckLGSeq", sequenceName = "ippms_paychecks_lg_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmployeePayBeanLG extends AbstractPaycheckEntity {


    @Id
    @GeneratedValue(generator = "paycheckLGSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paychecks_inst_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Employee employee;

    public EmployeePayBeanLG(Long pId) {
       this.id = pId;
    }

    @Override
    public AbstractEmployeeEntity getParentObject() {
        return this.employee;
    }
    @Override
    public void setParentObject(AbstractEmployeeEntity abstractEmployeeEntity) {
        this.employee = (Employee) abstractEmployeeEntity;
    }
    @Override
    public String getName() {
        if (employee != null && !employee.isNewEntity())
            return employee.getDisplayName();
        return "";
    }
    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public void setName(String pName) {

    }


    public void setDisplayNameWivAsterix(String s) {
        this.employee.setDisplayNameWivAsterix(s);
    }


    public void setEmployee(AbstractEmployeeEntity e) {
            this.employee = (Employee) e;
    }
}
