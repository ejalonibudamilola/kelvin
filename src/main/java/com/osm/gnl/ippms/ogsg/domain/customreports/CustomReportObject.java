/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.customreports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_report_obj")
@SequenceGenerator(name = "reportObjSeq", sequenceName = "ippms_report_obj_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class CustomReportObject {

    @Id
    @GeneratedValue(generator = "reportObjSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "rep_obj_inst_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "tbl_name", nullable = false, length = 40)
    private String tableName;

    @Column(name = "tbl_id", nullable = false, length = 30)
    private String tableId;

    @Column(name = "aka", nullable = false, length = 5)
    private String aka;

    @Column(name = "ctrl_entity", columnDefinition="numeric default '0'")
    private int controlEntityInd;

    @Column(name = "ogrp_by_entity", columnDefinition="numeric default '0'")
    private int outerGroupByInd;

    @Column(name = "igrp_by_entity", columnDefinition="numeric default '0'")
    private int innerGroupByInd;

    @Column(name = "in_use", columnDefinition="numeric default '0'")
    private int activeInd;

    @Column(name = "pension_restricted", columnDefinition="numeric default '0'")
    private int pensionRestrictedInd;

    @Column(name = "abstract_ind", columnDefinition="numeric default '0'")
    private int abstractTableInd;

    @Column(name = "use_client_id", columnDefinition="numeric default '0'")
    private int useClientIdInd;

    @Column(name = "dependent_abstract_entity_ind", columnDefinition="numeric default '0'")
    private int depAbsEntInd;

    @Column(name = "hierarchy_ind", columnDefinition="numeric default '0'")
    private int hierarchyInd;

    @Transient private boolean active;
    @Transient private boolean controlEntity;
    @Transient private boolean outerGroupBy;
    @Transient private boolean innerGroupBy;
    @Transient private boolean pensionRestricted;
    @Transient private boolean abstractTable;
    @Transient private boolean useClientId;
    @Transient private boolean dependentEntity;

    public boolean isAbstractTable() {
        abstractTable = this.abstractTableInd == 1;
        return abstractTable;
    }

    public boolean isDependentEntity() {
        this.dependentEntity = this.depAbsEntInd == 1;
        return dependentEntity;
    }

    public boolean isUseClientId() {
        useClientId = this.useClientIdInd == 1;
        return useClientId;
    }

    public boolean isActive() {
        active = this.activeInd == 1;
        return active;
    }

    public boolean isControlEntity() {
        controlEntity = this.controlEntityInd == 1;
        return controlEntity;
    }

    public boolean isOuterGroupBy() {
        outerGroupBy = this.outerGroupByInd == 1;
        return outerGroupBy;
    }

    public boolean isInnerGroupBy() {
        innerGroupBy = this.innerGroupByInd == 1;
        return innerGroupBy;
    }

    public boolean isPensionRestricted() {
        pensionRestricted = this.pensionRestrictedInd == 1;
        return pensionRestricted;
    }
}
