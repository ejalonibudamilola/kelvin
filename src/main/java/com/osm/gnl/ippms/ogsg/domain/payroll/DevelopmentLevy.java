/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payroll;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_development_levy")
@SequenceGenerator(name = "devLevyIndSeq", sequenceName = "ippms_development_levy_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class DevelopmentLevy {
    @Id
    @GeneratedValue(generator = "devLevyIndSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "dev_levy_inst_id", unique = true, nullable = false)
    private Long id;
    @Column(name = "year", nullable = false, columnDefinition = "numeric(4,0)")
    private int year;
    @Column(name = "month", nullable = false, columnDefinition = "numeric(2,0)")
    private int month;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs = LocalDate.now();

    @Column(name = "last_mod_by", nullable = false)
    private Long lastModBy;



    public boolean isNewEntity() {
        return this.id == null;
    }
}