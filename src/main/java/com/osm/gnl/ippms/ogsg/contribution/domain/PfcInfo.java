/*
 * Copyright (c) 2020. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.contribution.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ippms_pfc_info")
@SequenceGenerator(name = "pfcSeq", sequenceName = "ippms_pfc_info_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class PfcInfo extends AbstractControlEntity {

    @Id
    @GeneratedValue(generator = "pfcSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "pfc_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "pfc_name", length = 200, nullable = false)
    private String name;

    @Column(name = "address", length = 500, nullable = false)
    private String address;
    @Column(name = "pfc_code", columnDefinition = "numeric(10) default '0'")
    private int pfcCode;

    @Transient
    private String generatedCaptcha;

    @Transient
    private String enteredCaptcha;

    @Transient
    private String oldName;
    @Transient
    private String oldAddress;
    @Transient
    private boolean editMode;


    public PfcInfo(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }


}