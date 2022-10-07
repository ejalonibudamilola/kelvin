/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.exception.InvalidBusinessClientException;
import com.osm.gnl.ippms.ogsg.exception.NoBusinessCertificationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClientMap;

import javax.servlet.http.HttpServletRequest;


public abstract class BusinessCertificateCreator {



    public static BusinessCertificate setValuesFromBusinessClient(BusinessCertificate pBusinessCertificate, BusinessClient pBCM ) throws NoBusinessCertificationException {


        pBusinessCertificate.setBusinessName( pBCM.getName());
        pBusinessCertificate.setBusinessClientInstId( pBCM.getId());
        pBusinessCertificate.setBusinessClientUID(pBusinessCertificate.getBusinessClientUID());
        pBusinessCertificate.setBusinessState(pBusinessCertificate.getBusinessState());
        pBusinessCertificate.setCityName( pBCM.getCityName());
        pBusinessCertificate.setBizAddr( pBCM.getAddress());
        pBusinessCertificate.setStateName( pBCM.getStateName());
        pBusinessCertificate.setGovernment(true);
        if( pBCM.isCivilService()){
            pBusinessCertificate.setCivilService( pBCM.isCivilService());
            pBusinessCertificate.setClientLogo("clientLogo.png");
            pBusinessCertificate.setClientReportLogo("clientLogo.png");
            pBusinessCertificate.setClientDesc("Ogun State Government IPPMS - Integrated Payroll & Personnel Management System ");
        }else if( pBCM.isLocalGovtPension()){
            pBusinessCertificate.setLocalGovtPension( pBCM.isLocalGovtPension());
            pBusinessCertificate.setClientDesc("Bureau Of Local Government Pension IPPMS - Integrated Payroll & Personnel Management System ");
            pBusinessCertificate.setClientLogo("clientLogoPen.png");
        }else if( pBCM.isStatePension()){
            pBusinessCertificate.setStatePension( pBCM.isStatePension());
            pBusinessCertificate.setClientLogo("clientLogoPen.png");
            pBusinessCertificate.setClientReportLogo("clientLogoPen.png");
            pBusinessCertificate.setClientDesc("Bureau Of State Pension IPPMS - Integrated Payroll & Personnel Management System ");
        }else if( pBCM.isSubeb()){
            pBusinessCertificate.setSubeb( pBCM.isSubeb());
            pBusinessCertificate.setClientLogo("clientLogoSubeb.png");
            pBusinessCertificate.setClientReportLogo("clientLogoSubeb.png");
            pBusinessCertificate.setClientDesc("Ogun State Universal Basic Education Board IPPMS - Integrated Payroll & Personnel Management System ");
        }else if( pBCM.isLocalGovernment()){
            pBusinessCertificate.setLocalGovt( pBCM.isLocalGovernment());
            pBusinessCertificate.setClientLogo("clientLogoLG.png");
            pBusinessCertificate.setClientReportLogo("clientLogoLG.png");
            pBusinessCertificate.setClientDesc("Ogun Local Government Service Commission IPPMS - Integrated Payroll & Personnel Management System ");
        }else if (pBCM.isExecutive()){
            pBusinessCertificate.setExecutive(pBCM.isExecutive());
            pBusinessCertificate.setClientLogo("clientLogoExec.png");
            pBusinessCertificate.setClientReportLogo("clientLogoExec.png");
            pBusinessCertificate.setClientDesc("Ogun State Executive IPPMS Dashboard- Integrated Payroll & Personnel Management System ");
        }else{
            throw new NoBusinessCertificationException("Business Certificate is Invalid. Not a known Business Client.");
        }
        //Set the Paycheck Bean Names.
        pBusinessCertificate.setBeanNames();

        return pBusinessCertificate;
    }
    public static BusinessCertificate makeBusinessClient(BusinessClient pBCM ) throws NoBusinessCertificationException {

        BusinessCertificate pBusinessCertificate = new BusinessCertificate();
        return setValuesFromBusinessClient(pBusinessCertificate,pBCM);
    }
    public static BusinessCertificate createBusinessCertificate(HttpServletRequest pRequest, User pLogin,GenericService genericService) throws Exception {


        BusinessCertificate bCert = new BusinessCertificate();


        if( pLogin.isFirstTimeLogin() ) {
            bCert.setFirstTimeLogin(true);
        }
        bCert.setRoleDisplayName(pLogin.getRole().getDisplayName());
        bCert.setUserName( pLogin.getUserName() );
        bCert.setLoggedOnUserNames( pLogin.getFirstName() + " " + pLogin.getLastName() );
        bCert.setLoginId( pLogin.getId() );
        bCert.setLogonUserRole( pLogin.getRole().getName() );
        bCert.setSysUser(pLogin.isSysUser());
        bCert.setSuperAdmin(pLogin.getRole().isSuperAdmin());
        //TODO - Set Admin Role too.

        BusinessClientMap wBCM = genericService.loadObjectWithSingleCondition(BusinessClientMap.class, CustomPredicate.procurePredicate("user.id", pLogin.getId()));

        if( wBCM.isNewEntity() ) {

            throw new InvalidBusinessClientException("Access Denied. Network Intrusion Suspected!!!");
        }

        if( (wBCM != null) && !(wBCM.isNewEntity()) ) {
            bCert = BusinessCertificateCreator.setValuesFromBusinessClient( bCert, wBCM.getBusinessClient() );
        }

        Object wSessionId = BaseController.getSessionId( pRequest );
        //set the session id
        bCert.setSessionId( wSessionId );
        bCert.setPrivilegedUser(pLogin.isPrivilegedUser());


        //put the certificate in the session
        BaseController.addSessionAttribute( pRequest, IppmsEncoder.getCertificateKey(), bCert );

        //put userName in the session
        BaseController.addSessionAttribute(pRequest, "userName", bCert.getLoggedOnUserNames());

        return bCert;
    }


}
