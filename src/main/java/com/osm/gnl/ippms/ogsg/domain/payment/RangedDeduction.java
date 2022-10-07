/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ippms_deduction_range" )
@SequenceGenerator(name = "rangedDedSeq", sequenceName = "ippms_deduction_range_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class RangedDeduction extends AbstractNamedEntity {

    @Id
    @GeneratedValue(generator = "rangedDedSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ranged_ded_inst_id")
    private Long id;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @OneToMany(mappedBy = "rangedDeduction", fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
    private List<RangedDeductionDetails> rangedDeductionDetailsList;


    @Transient
    private String lowerBoundValue;

    @Transient
    private String upperBoundValue;

    @Transient
    private String amountStr;

    public RangedDeduction(Long id) {
        this.id = id;
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
