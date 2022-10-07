/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.generic.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.menu.domain.UserMenu;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ippms_client")
@SequenceGenerator(name = "clientSeq", sequenceName = "ippms_client_seq", allocationSize = 1)
@Data
@NoArgsConstructor
public class BusinessClient implements Comparable<BusinessClient>, Serializable {

    @Id
    @Column(name = "business_client_inst_id", nullable = false, unique = true,updatable = false)
    private Long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "city_name")
    private String cityName;
    @Column(name = "state_name")
    private String stateName;
    @Column(name = "civil_service_ind", columnDefinition = "integer default '0'")
    private int civilServiceInd;
    @Column(name = "state_pension_ind", columnDefinition = "integer default  '0'")
    private int statePensionInd;
    @Column(name = "subeb_ind" , columnDefinition = "integer default  '0'")
    private int subebInd;
    @Column(name = "blgp_ind", columnDefinition = "integer default '0'")
    private int blpgInd;
    @Column(name = "lg_ind", columnDefinition = "integer default '0'")
    private int lgInd;
    @Column(name = "exec_ind", columnDefinition = "integer default '0'")
    private int execInd;
    @Column(name = "chart_name")
    private String chartName;


    @Transient private UserMenu userMenu;
    @Transient private Long roleId;
    @Transient private boolean editMode;
    @Transient private String userName;
    @Transient private String firstName;
    @Transient private String lastName;
    @Transient private boolean sysUser;
    @Transient private boolean allowWeekendLogin;
    @Transient private boolean civilService;
    @Transient private boolean statePension;
    @Transient private boolean subeb;
    @Transient private boolean localGovtPension;
    @Transient private boolean localGovernment;
    @Transient private boolean executive;
    @Transient private boolean lengthError;
    @Transient private boolean specCharError;
    @Transient private boolean warningIssued;
    @Transient private String email;
    @Transient private User user;
    @Transient private BusinessCertificate businessCertificate;

    public BusinessClient(final Long id) {
        this.id = id;
    }

    public BusinessClient(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(BusinessClient businessClient) {
        return 0;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }
    public boolean isCivilService() {
        return this.civilServiceInd == 1;
    }

    public boolean isStatePension() {
        return this.statePensionInd == 1;
    }

    public boolean isSubeb() {
        return this.subebInd == 1;
    }

    public boolean isLocalGovtPension() {
        return this.blpgInd == 1;
    }

    public boolean isLocalGovernment() {
        return this.lgInd == 1;
    }

    public boolean isExecutive() {
        return this.execInd == 1;
    }

}
