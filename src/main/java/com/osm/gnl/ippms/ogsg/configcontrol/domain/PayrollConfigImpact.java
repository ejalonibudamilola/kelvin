/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.configcontrol.domain;

import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_payroll_config_info")
@SequenceGenerator(name = "payrollConfigSeq", sequenceName = "ippms_payroll_config_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayrollConfigImpact  extends  AbstractConfigBean{

    @Id
    @GeneratedValue(generator = "payrollConfigSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "payroll_config_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payroll_master_inst_id", nullable = false)
    private PayrollRunMasterBean masterBean;

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
