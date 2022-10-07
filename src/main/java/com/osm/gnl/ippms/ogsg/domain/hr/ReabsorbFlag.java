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
@Table(name = "ippms_reabsorb_flag")
@SequenceGenerator(name = "reabsorbFlagSeq", sequenceName = "ippms_reabsorb_flag_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ReabsorbFlag extends AbstractFlagObject{

    @Id
    @GeneratedValue(generator = "reabsorbFlagSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "reabsorb_flag_inst_id")
    private Long id;


    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
