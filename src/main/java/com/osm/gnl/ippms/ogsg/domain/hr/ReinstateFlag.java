/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.hr;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_reinstate_flag")
@SequenceGenerator(name = "reinstateFlagSeq", sequenceName = "ippms_reinstate_flag_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ReinstateFlag extends AbstractFlagObject{

    @Id
    @GeneratedValue(generator = "reinstateFlagSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "reinstate_flag_inst_id")
    private Long id;


    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
