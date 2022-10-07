/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.employee;

import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "ippms_employee")
@SequenceGenerator(name = "empSeq", sequenceName = "ippms_employee_seq", allocationSize = 1)
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Employee<T> extends AbstractEmployeeEntity {
    private static final long serialVersionUID = -8152207096932364931L;

    @Id
    @GeneratedValue(generator = "empSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "employee_inst_id")
    private Long id;

    //SUBEB Specific
    @Column(name = "period_month", columnDefinition = "integer default '0'")
    protected int periodMonth;
    //SUBEB Specific
    @Column(name = "period_year", columnDefinition = "integer default '0'")
    protected int periodYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_inst_id")
    protected SchoolInfo schoolInfo;


    public Employee(Long pId, String pFirstName, String pLastName) {
        setId(pId);
        setFirstName(pFirstName);
        setLastName(pLastName);
    }

    public Employee(Long pId, String pFirstName, String pLastName, Object pInitials) {
        setId(pId);
        setFirstName(pFirstName);
        setLastName(pLastName);
        if (pInitials != null) {
            this.setInitials((String) pInitials);
        }
    }

    public Employee(Long pId) {
        setId(pId);
    }


    public Employee(String pLastName, String pFirstName, String pInitials) {
        setInitials(pInitials);
        setFirstName(pFirstName);
        setLastName(pLastName);
    }

    public Employee(Long pId, String pEmpId, String pLastName, String pFirstName, String pInitials) {
        setId(pId);
        this.setEmployeeId(pEmpId);
        setInitials(pInitials);
        setFirstName(pFirstName);
        setLastName(pLastName);
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String getParentObjectName() {
        if (this.getSchoolInfo() != null && !this.getSchoolInfo().isNewEntity()) {
            this.parentObjectName = this.getSchoolInfo().getName();
        } else {
            this.parentObjectName = this.mdaDeptMap.getMdaInfo().getName();
        }
        return this.parentObjectName;
    }

    @Override
    public boolean isSchoolStaff() {
        if (isSchoolEnabled() && this.getSchoolInfo() != null && !this.getSchoolInfo().isNewEntity())
             schoolStaff = true;
        return schoolStaff;
    }

    public String getSchoolName() {
        if (this.getSchoolInfo() != null && !this.getSchoolInfo().isNewEntity()) {
            this.schoolName = this.getSchoolInfo().getName();
        }
        return schoolName;
    }

    @Override
    public boolean isPensioner() {
        return false;
    }

    @Override
    protected Object getChildEntity() {
        return this;
    }
}


