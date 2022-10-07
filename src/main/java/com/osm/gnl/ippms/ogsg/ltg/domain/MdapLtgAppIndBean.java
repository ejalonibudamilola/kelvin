/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.ltg.domain;


import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "ippms_mdap_ltg_app")
@SequenceGenerator(name = "ltgInfoSeq", sequenceName = "ippms_mdap_ltg_app_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class MdapLtgAppIndBean implements Serializable, Comparable<MdapLtgAppIndBean> {
    private static final long serialVersionUID = -8915391045390527128L;

    @Id
    @GeneratedValue(generator = "ltgInfoSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ippms_mdap_ltg_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mda_inst_id")
    private MdaInfo mdaInfo;

    @Column(name = "ltg_app_month")
    private int ltgApplyMonth;

    @Column(name = "ltg_app_year", nullable = false, length = 4)
    private int ltgApplyYear;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs;

    @Column(name = "last_mod_by", nullable = false, length = 50)
    private String lastModBy;



    public MdapLtgAppIndBean(Long pId) {
        this.id = pId;
    }


    @Override
    public int compareTo(MdapLtgAppIndBean o) {

        return 0;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }
}