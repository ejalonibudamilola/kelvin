/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.menu.domain;

import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ippms_business_client_menu_link")
@SequenceGenerator(name = "bizClientMenuLinkSeq", sequenceName = "ippms_business_client_menu_link_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class BusinessClientMenuLink implements Serializable {

    @Id
    @GeneratedValue(generator = "bizClientMenuLinkSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "biz_client_menu_link_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_link_inst_id")
    private MenuLink menuLink;

    @ManyToOne
    @JoinColumn(name = "business_client_inst_id")
    private BusinessClient businessClient;


    public BusinessClientMenuLink(BusinessClient businessClient, MenuLink menuLink) {
        this.menuLink = menuLink;
        this.businessClient = businessClient;
    }
}
