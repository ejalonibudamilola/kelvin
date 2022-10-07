/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.employee;

import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ippms_pensioner")
@SequenceGenerator(name = "penSeq", sequenceName = "ippms_pensioner_seq", allocationSize = 1)
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Pensioner<T> extends AbstractEmployeeEntity {

    @Id
    @GeneratedValue(generator = "penSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "pensioner_inst_id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "pension_end_ind", columnDefinition = "integer default 0", nullable = false)
    private int pensionEndInd;

    @Column(name = "new_entrant_ind", columnDefinition = "integer default 0", nullable = false)
    private int newEntrantInd;
    @Column(name = "entry_month_ind", columnDefinition = "integer default 0", nullable = false)
    private int entryMonthInd;

    @Column(name = "entry_year_ind", columnDefinition = "integer default 0", nullable = false)
    private int entryYearInd;

    @ManyToOne()
    @JoinColumn(name = "retire_mda_inst_id")
    private MdaInfo mdaAtRetirement;


    //--Ties this Pensioner to its parent. Also parent will be updated with this too.
    //In Ogun State, Pensioners might exist without corresponding Employee Information.
    //If they do, we use the parentBusinessClientId to represent the Service Organization.
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_inst_id", unique = true, updatable = false)
    private Employee employee;

    @Column(name = "parent_business_client_inst_id",nullable = false)
    private Long parentBusinessClientId;

    @Column(name = "og_number", columnDefinition = "varchar(30)")
    private String ogNumber;

    @Transient
    private boolean hasEmployee;


    public Pensioner(Long pPid) {
        this.id = pPid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pensioner)) return false;
        Pensioner pensioner = (Pensioner) o;
        return Objects.equals(id, ((Pensioner) o).getId()) &&
                Objects.equals(employeeId, pensioner.getEmployeeId());
    }

    public boolean isHasEmployee() {
        if (this.employee != null && !this.employee.isNewEntity())
            hasEmployee = true;
        return hasEmployee;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeeId);
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
    public SchoolInfo getSchoolInfo() {
        return new SchoolInfo();
    }

    @Override
    public String getParentObjectName() {
          this.parentObjectName = this.getMdaDeptMap().getMdaInfo().getName();
         return this.parentObjectName;
  }

    @Override
    public boolean isSchoolStaff() {
        return false;
    }

    @Override
    public boolean isPensioner() {
        return true;
    }

    @Override
    public Object getChildEntity() {
        return this;
    }


}
