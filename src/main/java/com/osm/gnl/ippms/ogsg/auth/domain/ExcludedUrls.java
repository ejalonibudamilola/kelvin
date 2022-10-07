/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_excluded_urls")
@SequenceGenerator(name = "excludedUrlSeq", sequenceName = "ippms_excluded_urls_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ExcludedUrls{

    @Id
    @GeneratedValue(generator = "excludedUrlSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "excluded_url_inst_id")
    private Long id;

    @Column(name="url_name", nullable = false, unique = true)
    private String urlName;

    @Column(name="privileged_ind", columnDefinition = "integer default '0'")
    private int privilegedInd;

}
