/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.allowance;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "ippms_spec_allow_info_subeb")
@SequenceGenerator(name = "specAllowSeqSubeb", sequenceName = "ippms_spec_allow_info_subeb_seq" , allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SpecialAllowanceInfoSubeb extends AbstractSpecialAllowanceEntity {
    private static final long serialVersionUID = 1840603817779318751L;

    @Id
    @GeneratedValue(generator = "specAllowSeqSubeb", strategy = GenerationType.SEQUENCE)
    @Column(name = "spec_allow_info_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    public SpecialAllowanceInfoSubeb(Long pSpecAllowId, String pDesc)
    {
        this.id = pSpecAllowId;
        this.description = pDesc;

    }

    public SpecialAllowanceInfoSubeb(Long pId) {
        this.id = pId;
    }

    public Long getParentId() {
        if(this.employee != null && !this.employee.isNewEntity())
            return this.employee.getId();
        else
            return null;
    }
    @Override
    public AbstractEmployeeEntity getParentObject() {
        return this.employee;
    }

    @Override
    public void setParentObject(AbstractEmployeeEntity abstractEmployeeEntity) {
        this.employee = (Employee)abstractEmployeeEntity;

    }

    @Override
    public void makeParentObject(Long pId) {
      this.employee = new Employee(pId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SpecialAllowanceInfoSubeb other = (SpecialAllowanceInfoSubeb) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return this.getName() == other.getName();
    }
    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

}