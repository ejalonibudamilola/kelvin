/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.promotion;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractJobBean {

    @Column(name = "running", columnDefinition = "integer default '0'")
    private int running;


    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "start_time", length = 20)
    private String startTime;

    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "end_time", length = 20)
    private String endTime;
    @Column(name = "last_mod_by", length = 20, nullable = false)
    private String lastRunBy;

    @Column(name = "business_client_inst_id", nullable =  false)
    private Long businessClientId;

    @Transient
    private boolean beingRun;

    public boolean isBeingRun()
    {
        return this.running == 1;
    }
    public abstract boolean isNewEntity();
}
