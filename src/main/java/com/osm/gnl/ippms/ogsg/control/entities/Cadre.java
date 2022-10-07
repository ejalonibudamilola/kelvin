/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.control.entities;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_cadre")
@SequenceGenerator(name = "cadreSeq", sequenceName = "ippms_cadre_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class Cadre extends AbstractDescControlEntity {

    @Id
    @GeneratedValue(generator = "cadreSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "cadre_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="salary_type_inst_id")
    private SalaryType salaryType;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "selectable_ind")
    private int selectableInd;

    @Column(name = "default_ind", columnDefinition = "integer default '0'")
    private int defaultInd;


    @Transient
    private boolean selectableBind;

    @Transient
    private boolean selectable;

    @Transient
    private boolean defaultIndBind;


    @Transient
    private boolean defaultCadre;

    @Transient
    private String payGroupName;

    public Cadre(Long id) {
        this.id = id;
    }

    public String getPayGroupName(){
        if(this.salaryType != null && !this.salaryType.isNewEntity())
            payGroupName = salaryType.getName();
        else
            payGroupName = "N/A";
        return payGroupName;
    }

    public boolean isSelectable(){
        this.selectable = this.selectableInd == 1;
        return selectable;
    }


    public boolean isDefaultCadre() {
        this.defaultCadre = this.defaultInd == 1;
        return defaultCadre;
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
