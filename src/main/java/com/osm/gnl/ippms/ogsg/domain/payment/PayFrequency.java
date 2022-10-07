/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "ippms_pay_frequency")
@SequenceGenerator(name = "payFeqSeq", sequenceName = "pay_frequency_seq", allocationSize = 1)
public class PayFrequency extends AbstractControlEntity {
    private static final long serialVersionUID = 7901601584410914688L;


    @Id
    @GeneratedValue(generator = "", strategy = GenerationType.SEQUENCE)
    @Column(name = "pay_frequency_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "pay_frequency_name", nullable = false, length = 30)
    private String name;

    @Column(name = "pay_frequency_value", nullable = false)
    private Long payFrequencyValue;

    @Column(name="description", nullable=false)
    private String description;

    public PayFrequency(Long pId) {
        this.id = pId;
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {

        return false;
    }
}