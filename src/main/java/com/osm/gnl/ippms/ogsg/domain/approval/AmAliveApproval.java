/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.approval;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_approve_am_alive")
@SequenceGenerator(name = "appAmAliveSeq", sequenceName = "ippms_approve_am_alive_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class AmAliveApproval extends AbstractApprovalEntity {
    @Id
    @GeneratedValue(generator = "appAmAliveSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "approve_am_alive_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hire_info_inst_id", nullable = false)
    private HiringInfo hiringInfo;

    @Column(name = "i_am_alive_date", nullable = false)
    private LocalDate amAliveDate;

    @Column(name = "reset_pension_ind", columnDefinition = "integer not null default '0'")
    private int suspendInd;

    @Column(name = "run_month", columnDefinition = "integer not null")
    private int runMonth;

    @Column(name = "run_year", columnDefinition = "integer not null")
    private int runYear;


    @Transient private boolean suspendStaff;
    @Transient private String suspensionStatus;
    @Transient private AbstractEmployeeEntity abstractEmployeeEntity;
    @Transient private boolean groupLeader;


    public boolean isGroupLeader() {
        groupLeader = this.groupLeaderInd == 1;
        return groupLeader;
    }

    public boolean isSuspendStaff() {
        this.suspendStaff = this.suspendInd == 1;
        return this.suspendStaff;
    }

    public String getSuspensionStatus() {
        if(this.isSuspendStaff())
            suspensionStatus = "Awaiting Suspension";
        else
            suspensionStatus = "-";
        return suspensionStatus;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
