/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_deduct_cat_info")
@SequenceGenerator(name = "empDedCatSeq", sequenceName = "ippms_deduct_cat_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmpDeductionCategory extends AbstractDescControlEntity {
    private static final long serialVersionUID = -2163131285100191538L;


    @Id
    @GeneratedValue(generator = "empDedCatSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "deduction_cat_inst_id")
    private Long id;

    @Column(name = "statutory_ind", columnDefinition = "integer default '0'", nullable = false)
    private int statutoryInd;

    @Column(name = "ranged_ind", columnDefinition = "integer default '0'", nullable = false)
    private int rangedInd;

    @Column(name = "apportioned_ind", columnDefinition = "integer default '0'", nullable = false)
    private int apportionedInd;

    @Transient
    private boolean statutoryDeduction;

    @Transient
    private boolean apportionedDeduction;

    @Transient
    private boolean rangedDeduction;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;


    public EmpDeductionCategory(Long pId) {
        this.id = pId;
    }


    public boolean isStatutoryDeduction() {
        return this.statutoryInd == 1;
    }


    public boolean isRangedDeduction(){
        return this.rangedInd == 1;
    }

    public boolean isApportionedDeduction(){return this.apportionedInd == 1;}

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }
}