/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.utils;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.BiometricInfo;
import org.json.JSONObject;

import java.io.Serializable;

public class BiometricsUtil implements Serializable {
    /**
     *
     */

    private static BiometricsUtil instance;


    private BiometricsUtil() {
    }

    public static BiometricsUtil getInstance() {


        if (instance == null) {
            instance = new BiometricsUtil();
        }
        return instance;
    }

    public static BiometricInfo validateEmployee(BiometricInfo biometricInfo, String apiUserName, String apiPassword, RestCall restCall) {
        String endpoint = "api/Employee/GetAll?username=" + apiUserName + "" +
                "&password=" + apiPassword + "&staffId=" + biometricInfo.getEmployeeId();
        if(biometricInfo.isHasLegacyId())
            endpoint = "api/Employee/GetAll?username=" + apiUserName + "" +
                    "&password=" + apiPassword + "&staffId=" + biometricInfo.getLegacyId();
        JSONObject resp;

        try {
            resp = new JSONObject(restCall.executeGet(endpoint));
        } catch (Exception wEx) {
            // wEHB.setFail(true);
            biometricInfo.setResponse(false);

            return biometricInfo;
        }
        if (resp.isEmpty()) {
            biometricInfo.setResponse(false);

        } else {
            try {
                biometricInfo.setResponse(true);
                biometricInfo.setEmployeeId(resp.getString("employeeId").toUpperCase());


                biometricInfo.setBioId(resp.getLong("id"));
                // biometricInfo.setBvnNumber(resp.getString("bvn"));
                if (!resp.isNull("phoneNumber"))
                    biometricInfo.setPhoneNumber(resp.getString("phoneNumber"));
                else
                    biometricInfo.setPhoneNumber("0");

                //--Create a validator...
                //1. It checks for the exosten

                if (!resp.isNull("agencyName")) {
                    biometricInfo.setAgencyName(resp.getString("agencyName"));
                }

                biometricInfo.setCounter(resp.getLong("counter"));
                biometricInfo.setFirstName(resp.getString("firstName"));
                biometricInfo.setLastName(resp.getString("lastName"));
                if (!resp.isNull("username")) {
                    biometricInfo.setUsername(resp.getString("username"));
                }
                if (!resp.isNull("middleName")) {
                    biometricInfo.setMiddleName(resp.getString("middleName"));
                }
                if (!resp.isNull("email"))
                    biometricInfo.setEmail(resp.getString("email"));
                if (!resp.isNull("employeeAgency"))
                    biometricInfo.setEmployeeAgency("employeeAgency");
                if (!resp.isNull("gender"))
                    biometricInfo.setGender(resp.getString("gender"));
                if (!resp.isNull("registryId"))
                    biometricInfo.setRegistryId(resp.getLong("registryId"));

                if (resp.getBoolean("hasBiometric") == true) {
                    biometricInfo.setHasBiometric(1);
                } else {
                    biometricInfo.setHasBiometric(0);
                }
                if (resp.getBoolean("hasPassport") == true) {
                    biometricInfo.setHasPassport(1);
                } else {
                    biometricInfo.setHasPassport(0);
                }
                if (resp.getBoolean("hasSignature") == true) {
                    biometricInfo.setHasSignature(1);
                } else {
                    biometricInfo.setHasSignature(0);
                }
                if (!resp.isNull("password")) {
                    biometricInfo.setPassword(resp.getString("password"));
                }
                if (!resp.isNull("reasonForDelete")) {
                    biometricInfo.setReasonForDelete(resp.getString("reasonForDelete"));
                }
                biometricInfo.setUserId(resp.getString("userId"));

                biometricInfo.setPhotoString(resp.getString("profilePicture"));

                //Convert base64 Image to Multipart
                biometricInfo.setProfilePicture(BaseController.base64ToMultipart(biometricInfo.getPhotoString()).getBytes());
                biometricInfo.setPhotoType(IConstants.BIOMETRIC_PHOTO_TYPE);

                if (!resp.isNull("registrars")) {
                    biometricInfo.setRegistrars(resp.getString("registrars"));
                }
                biometricInfo.setSignature(resp.getString("signature").getBytes());
                biometricInfo.setSignatureString(resp.getString("signature"));
                if (!resp.isNull("role")) {
                    biometricInfo.setRole(resp.getString("role"));
                }
                if (resp.getBoolean("isDeleted") == false) {
                    biometricInfo.setDeleted(0);
                } else {
                    biometricInfo.setDeleted(1);
                }
                if (!resp.isNull("deletedBy")) {
                    biometricInfo.setDeletedBy(resp.getString("deletedBy"));
                }
                if (!resp.isNull("dateDeleted"))
                    biometricInfo.setDateDeleted(resp.getString("dateDeleted"));
                if (!resp.isNull("dateCreated"))
                    biometricInfo.setDateCreated(resp.getString("dateCreated"));
                if (!resp.isNull("staffRoles")) {
                    biometricInfo.setUserRoles(resp.getJSONArray("staffRoles").toString());
                }
                biometricInfo.setCounter(resp.getLong("counter"));
                biometricInfo.setEmpty("false");
            } catch (Exception ex) {

                biometricInfo.setResponse(false);

                return biometricInfo;
            }
        }
        return biometricInfo;
    }
}


