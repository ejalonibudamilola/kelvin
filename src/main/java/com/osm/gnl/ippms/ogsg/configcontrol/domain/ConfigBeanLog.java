/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.configcontrol.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_payroll_config_log")
@SequenceGenerator(name = "payrollConfigLogSeq", sequenceName = "ippms_payroll_config_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ConfigBeanLog  implements Serializable, Comparable<ConfigBeanLog>{

    @Id
    @GeneratedValue(generator = "payrollConfigLogSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "payroll_config_log_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs;

    @Column(name = "pay_period", nullable = false, length = 6)
    private String auditPayPeriod;

    @ManyToOne
    @JoinColumn(name = "user_inst_id", nullable = false)
    private User user;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "audit_time", nullable = false, length = 12)
    protected String auditTime;

    @Column(name = "old_value", nullable = false, length = 300)
    protected String oldValue;

    @Column(name = "new_value", nullable = false, length = 300)
    protected String newValue;

    @Column(name = "column_changed", nullable = false, length = 30)
    protected String columnChanged;

    @Override
    public int compareTo(ConfigBeanLog configBeanLog) {
        return 0;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }
}
