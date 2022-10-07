/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.contribution.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_pfa_info")
@SequenceGenerator(name = "pfaSeq", sequenceName = "ippms_pfa_info_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class PfaInfo extends AbstractControlEntity {
    private static final long serialVersionUID = -494813441738808813L;

    @Id
    @GeneratedValue(generator = "pfaSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "pfa_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pfc_info_inst_id")
    private PfcInfo pfcInfo;

    @Column(name = "pfa_name", length = 200, nullable = false)
    private String name;
    @Column(name = "pfa_code", length = 40, nullable = false)
    private String pfaCode;

    @Column(name = "address", length = 500, nullable = false)
    private String address;

    @Column(name = "email_addr", length = 80, nullable = true)
    private String emailAddr;
    @Column(name = "pfa_id", length = 4, nullable = false)
    private String pfaId;
    @Column(name = "website", length = 50, nullable = true)
    private String webSite;
    @Column(name = "phone_no", length = 20, nullable = true)
    private String phoneNumber;

    @Column(name = "default_ind", columnDefinition = "integer default '0'")
    private int defaultInd;

    @Transient
    private Long newPfcInfoInstId;
    @Transient
    private String oldAddress;
    @Transient
    private String oldName;

    @Transient
    private int noOfEmployees;
    @Transient
    private double totalContribution;

    @Transient
    private boolean defaultPfa;
    @Transient
    private boolean editMode;
    @Transient
    private String generatedCaptcha;
    @Transient
    private String enteredCaptcha;

    public PfaInfo(Long pId) {
        this.id = pId;
    }

    public boolean isDefaultPfa() {
        return this.defaultInd == 1;
    }

    @Override
    public int compareTo(Object o) { return 0; }

    @Override
    public boolean isNewEntity() { return this.id == null; }
}