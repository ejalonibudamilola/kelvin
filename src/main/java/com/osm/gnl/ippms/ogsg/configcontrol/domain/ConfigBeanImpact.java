/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.configcontrol.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_config_impact")
@SequenceGenerator(name = "configImpactSeq", sequenceName = "ippms_config_impact_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ConfigBeanImpact extends AbstractNamedEntity {

    @Id
    @GeneratedValue(generator = "configImpactSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "config_impact_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "config_inst_id", nullable = false)
    private ConfigurationBean configurationBean;

    @ManyToOne
    @JoinColumn(name = "mda_dept_map_inst_id", nullable = false)
    private MdaDeptMap mdaDeptMap;

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
