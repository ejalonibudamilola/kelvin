/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.allowance;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "ippms_spec_allow_info_pen")
@SequenceGenerator(name = "specAllowSeqPen", sequenceName = "ippms_spec_allow_info_pen_seq" , allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SpecialAllowanceInfoPensions extends AbstractSpecialAllowanceEntity {
    private static final long serialVersionUID = 1840603817779318751L;

    @Id
    @GeneratedValue(generator = "specAllowSeqPen", strategy = GenerationType.SEQUENCE)
    @Column(name = "spec_allow_info_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner pensioner;

    public SpecialAllowanceInfoPensions(Long pSpecAllowId, String pDesc)
    {
        this.id = pSpecAllowId;
        this.description = pDesc;

    }

    public SpecialAllowanceInfoPensions(Long pId) {
        this.id = pId;
    }
    public Long getParentId() {
        if(this.pensioner != null && !this.pensioner.isNewEntity())
            return this.pensioner.getId();
        else
            return null;
    }

    public AbstractEmployeeEntity getParentObject() {
        return this.pensioner;
    }

    @Override
    public void setParentObject(AbstractEmployeeEntity abstractEmployeeEntity) {
        this.pensioner = (Pensioner) abstractEmployeeEntity;

    }

    @Override
    public void makeParentObject(Long pId) {
        this.pensioner = new Pensioner(pId);
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
        SpecialAllowanceInfoPensions other = (SpecialAllowanceInfoPensions) obj;
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