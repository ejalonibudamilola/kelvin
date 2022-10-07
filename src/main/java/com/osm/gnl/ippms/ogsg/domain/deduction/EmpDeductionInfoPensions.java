/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.deduction;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_deduction_info_pen")
@SequenceGenerator(name = "empDedInfoSeqPen", sequenceName = "ippms_deduction_info_pen_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmpDeductionInfoPensions extends AbstractDeductionEntity {

    private static final long serialVersionUID = 7271242807748610678L;

    @Id
    @GeneratedValue(generator = "empDedInfoSeqPen", strategy = GenerationType.SEQUENCE)
    @Column(name = "deduct_info_inst_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner pensioner;

    public EmpDeductionInfoPensions(Long pId, String pDesc) {
        this.id = pId;
        this.description = pDesc;
    }

    public EmpDeductionInfoPensions(Long pId) {
        this.id = pId;
    }

    @Override
    public Long getParentId() {
        return this.pensioner.getId();
    }

    @Override
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
    public boolean isNewEntity() {

        return this.id == null;
    }

}