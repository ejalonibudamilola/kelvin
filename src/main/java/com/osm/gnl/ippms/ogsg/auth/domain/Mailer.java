/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.domain;

import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mailer {

    private Long id;
    private String copier;
    private String recipient;
    private String name;
    private String subject;
    private String message;
    private List<Mailer> recipientList;
    private List<MdaInfo> mdaList;
    private int runMonth;
    private int runYear;
    private BusinessCertificate businessCertificate;
    private HttpServletRequest httpServletRequest;
    private String autoGenPassword;
    private Timestamp expiration;

}
