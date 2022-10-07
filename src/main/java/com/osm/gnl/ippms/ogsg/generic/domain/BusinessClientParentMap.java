/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.generic.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ippms_client_parent_map")
@SequenceGenerator(name = "clientParentMapSeq", sequenceName = "ippms_client_parent_map_seq", allocationSize = 1)
@Data
@NoArgsConstructor
public class BusinessClientParentMap {

    @Id
    @Column(name = "bc_parent_map_inst_id", nullable = false, unique = true,updatable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "parent_bus_client_inst_id", nullable = false)
    private BusinessClient parentBusinessClient;

    @OneToOne
    @JoinColumn(name = "child_bus_client_inst_id", nullable = false)
    private BusinessClient childBusinessClient;

    public boolean isNewEntity(){
        return this.id == null;
    }

}
