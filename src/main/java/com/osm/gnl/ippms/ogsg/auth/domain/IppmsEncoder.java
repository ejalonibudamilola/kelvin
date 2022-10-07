/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Author Mustola
 */
@Component
@NoArgsConstructor
@Getter
public abstract class IppmsEncoder implements Serializable {


    private static final String firstEncoder = "Ogs_10i20Pp20Ms-EpM_gNL";


    private static final String secondEncoder = "Om_HoM_MoT04_12_DaR16_T_enc_";


    private static final String sessionKey = "_enc_T12_DaR16_HoM_Mo";


    private static final String certificateKey = "oG_Sg82_020da_ag-hCf";

    public static String getFirstEncoder() {
        return firstEncoder;
    }

    public static String getSecondEncoder() {
        return secondEncoder;
    }

    public static String getSessionKey() {
        return sessionKey;
    }

    public static String getCertificateKey() {
        return certificateKey;
    }


    private static final long serialVersionUID = 4246703053248019164L;

}
