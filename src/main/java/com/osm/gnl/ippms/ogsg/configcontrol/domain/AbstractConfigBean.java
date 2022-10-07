/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.configcontrol.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractConfigBean extends AbstractControlEntity {


     @Column(name = "business_client_inst_id")
    protected Long businessClientId;

    @Column(name = "service_length", columnDefinition = "integer default '35'")
    protected int serviceLength;

    @Column(name = "age_of_retirement", nullable = false,columnDefinition = "integer  default '60'")
    protected int ageAtRetirement;

    @Column(name = "i_am_alive", nullable = false, columnDefinition = "integer  default '70'")
    protected int iamAlive;

    @Column(name = "i_am_alive_ext", nullable = false, columnDefinition = "integer default '3'")
    protected int iamAliveExt;

    @Column(name = "ignore_am_alive", nullable = false, columnDefinition = "integer default '0'")
    protected int ignoreAmAliveInd;

    @Column(name = "req_residence_id", nullable = false, columnDefinition = "integer default '0'")
    protected int requireResidenceId;

    @Column(name = "req_nin", nullable = false, columnDefinition = "integer default '0'")
    protected int requireNin;

    @Column(name = "req_bvn", nullable = false, columnDefinition = "integer default '0'")
    protected int requireBvn;

    @Column(name = "req_staff_email", nullable = false, columnDefinition = "integer default '1'")
    protected int requireStaffEmailInd;

    @Column(name = "req_biometric_info", nullable = false, columnDefinition = "integer default '1'")
    protected int reqBiometricInfoInd;

    @Transient protected boolean useIAmAlive;
    @Transient protected boolean residenceIdRequired;
    @Transient protected boolean ninRequired;
    @Transient protected boolean bioRequired;
    @Transient protected boolean bvnRequired;
    @Transient protected boolean staffEmailRequired;
    @Transient protected boolean iamAliveBind;
    @Transient protected String usingAmAlive;
    @Transient protected String usingResidenceId;
    @Transient protected String usingNin;
    @Transient protected String usingBiometricInfo;
    @Transient protected String usingBvn;

    public String getUsingAmAlive() {
        if(this.isUseIAmAlive())
            usingAmAlive = "Yes";
        else
            usingAmAlive = "No";
        return usingAmAlive;
    }

    public String getUsingResidenceId() {
        if(this.isResidenceIdRequired())
            usingResidenceId = "Yes";
        else
            usingResidenceId = "No";
        return usingResidenceId;
    }

    public String getUsingNin() {
        if(this.isNinRequired())
            usingNin = "Yes";
        else
            usingNin = "No";
        return usingNin;
    }

    public String getUsingBiometricInfo() {
        if(this.isBioRequired())
            usingBiometricInfo = "Yes";
        else
            usingBiometricInfo = "No";
        return usingBiometricInfo;
    }

    public String getUsingBvn() {
        if(this.isBvnRequired())
            usingBvn = "Yes";
        else
            usingBvn = "No";
        return usingBvn;
    }

    public boolean isUseIAmAlive() {
        useIAmAlive = ignoreAmAliveInd == 1;
        return useIAmAlive;
    }

    public boolean isResidenceIdRequired() {
        residenceIdRequired =  requireResidenceId == 1;
        return residenceIdRequired;
    }

    public boolean isStaffEmailRequired() {
        staffEmailRequired = requireStaffEmailInd == 1;
        return staffEmailRequired;
    }

    public boolean isNinRequired() {
        this.ninRequired =  requireNin == 1;
        return ninRequired;
    }
    public boolean isBioRequired() {
        bioRequired = this.reqBiometricInfoInd == 1;
        return bioRequired;
    }

    public boolean isBvnRequired() {
        bvnRequired = this.requireBvn == 1;
        return bvnRequired;
    }
    public abstract int compareTo(Object o) ;

    public abstract boolean isNewEntity() ;
}
