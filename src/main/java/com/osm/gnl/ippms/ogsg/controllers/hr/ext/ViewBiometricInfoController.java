/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.*;
import com.osm.gnl.ippms.ogsg.domain.util.Base64Util;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.utils.RestCall;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;

@Controller
@RequestMapping({"/viewBiometricInfo.do"})
@SessionAttributes(types = {BiometricInfo.class})
public class ViewBiometricInfoController extends BaseController {


    @Autowired
    private RestCall restCall;

    @Value("${API_IP}")
    private String API_IP;

    @Value("${API_USERNAME}")
    private String API_USERNAME;

    @Value("${API_PASSWORD}")
    private String API_PASSWORD;

    private final String VIEW_NAME = "employee/verifyBiometricForm";



    @RequestMapping(method = {RequestMethod.GET}, params={"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);
        byte[] encoded;
        AbstractEmployeeEntity abstractEmployeeEntity = IppmsUtils.loadEmployee(genericService,pEmpId,bc);

        BiometricInfo biometricInfo = this.genericService.loadObjectWithSingleCondition(BiometricInfo.class,CustomPredicate.procurePredicate("employeeId",abstractEmployeeEntity.getEmployeeId()));

        if(biometricInfo.isNewEntity()){
            biometricInfo.setParentId(abstractEmployeeEntity.getId());
            biometricInfo.setLastName(abstractEmployeeEntity.getLastName());
            biometricInfo.setEmployeeId(abstractEmployeeEntity.getEmployeeId());
            biometricInfo.setFirstName(abstractEmployeeEntity.getFirstName());
            biometricInfo.setMiddleName(abstractEmployeeEntity.getInitials());
            if(IppmsUtils.isNotNullOrEmpty(abstractEmployeeEntity.getLegacyEmployeeId() ))
                biometricInfo.setLegacyId(abstractEmployeeEntity.getLegacyEmployeeId());
            biometricInfo.setBiometricDataExists(false);
        }else{
            biometricInfo.setParentId(abstractEmployeeEntity.getId());
            if(IppmsUtils.isNotNullOrEmpty(abstractEmployeeEntity.getLegacyEmployeeId() ))
                biometricInfo.setLegacyId(abstractEmployeeEntity.getLegacyEmployeeId());
            biometricInfo.setBiometricDataExists(true);
            if(biometricInfo.getHasPassport() == IConstants.ON){
                encoded = Base64.getEncoder().encode(biometricInfo.getProfilePicture());
                biometricInfo.setPhotoString(new String(encoded));
            }
            if(biometricInfo.getHasSignature() == IConstants.ON){
                encoded = Base64.getEncoder().encode(biometricInfo.getSignature());
                biometricInfo.setSignatureString(new String(encoded));
            }
            if(biometricInfo.getVerifiedBy() != null) {
                biometricInfo.setVerifiedByMsg("Last Verified by : " + biometricInfo.getVerifiedBy().getActualUserName());
                biometricInfo.setVerifiedDateMsg("Last Verified Date : " + PayrollUtils.formatTimeStamp(biometricInfo.getLastModTs()));
            }
        }

        model.addAttribute("miniBean", biometricInfo);

        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }


    public static MultipartFile base64ToMultipart(String base64) {
        try {


            byte[] b = Base64.getDecoder().decode(base64);

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }

            return new Base64Util(b, base64);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_verify", required = false) String verify,
                                @RequestParam(value = "_update", required = false) String update,
                                @ModelAttribute("miniBean") BiometricInfo pEHB, BindingResult result,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);
        Long parentId = pEHB.getParentId();

        if (super.isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
           // if(!bc.isPensioner())
                return "redirect:employeeOverviewForm.do?eid="+parentId;

            //return "redirect:pensionerOverviewForm?eid="+parentId;
        }
        if(isButtonTypeClick(request,REQUEST_PARAM_VERIFY)){


            pEHB.setParentId(parentId);
            String endpoint = "api/Employee/GetAll?username="+API_USERNAME+"" +
                    "&password="+API_PASSWORD+"&staffId="+pEHB.getEmployeeId();
            if(pEHB.isHasLegacyId())
                endpoint = "api/Employee/GetAll?username="+API_USERNAME+"" +
                        "&password="+API_PASSWORD+"&staffId="+pEHB.getLegacyId();

            JSONObject resp;

            try{
                resp = new JSONObject(restCall.executeGet(endpoint));
            }catch (Exception wEx){
                // wEHB.setFail(true);
                pEHB.setResponse(false);
                pEHB.setBiometricDataExists(false);
                result.rejectValue("employeeId", "Biometric ID Exist", "Biometric Database returns no value.");
                System.out.println(wEx.getMessage());
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pEHB);
                return VIEW_NAME;
            }
            if(resp.isEmpty() ||  resp.get("employeeId") == null){
                pEHB.setResponse(false);
                pEHB.setBiometricDataExists(false);
                result.rejectValue("employeeId", "Biometric ID Exist", "Biometric Database returns no value.");
                System.out.println("Biometric Server Returned Empty Result.");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pEHB);
                return VIEW_NAME;
            }
            else {
                try {
                    if(!resp.getBoolean("hasBiometric"))
                        throw new Exception("ID not found");
                    pEHB.setEmployeeId(resp.getString("employeeId").toUpperCase());
                     if(pEHB.isHasLegacyId())
                         if(pEHB.getEmployeeId().equalsIgnoreCase(pEHB.getLegacyId()));
                            pEHB.setUsedLegacyId(true);


                    pEHB.setBioId(resp.getLong("id"));
                    // pEHB.setBvnNumber(resp.getString("bvn"));
                    if (!resp.isNull("phoneNumber"))
                        pEHB.setPhoneNumber(resp.getString("phoneNumber"));
                    else
                        pEHB.setPhoneNumber("0");


                    if (!resp.isNull("agencyName")) {
                        pEHB.setAgencyName(resp.getString("agencyName"));
                    }

                    pEHB.setCounter(resp.getLong("counter"));
                    pEHB.setFirstName(resp.getString("firstName"));
                    pEHB.setLastName(resp.getString("lastName"));
                    if (!resp.isNull("username")) {
                        pEHB.setUsername(resp.getString("username"));
                    }
                    if (!resp.isNull("middleName")) {
                        pEHB.setMiddleName(resp.getString("middleName"));
                    }
                    if (!resp.isNull("email"))
                        pEHB.setEmail(resp.getString("email"));
                    if (!resp.isNull("employeeAgency"))
                        pEHB.setEmployeeAgency("employeeAgency");
                    if (!resp.isNull("gender"))
                        pEHB.setGender(resp.getString("gender"));
                    if (!resp.isNull("registryId"))
                        pEHB.setRegistryId(resp.getLong("registryId"));

                    if (resp.getBoolean("hasBiometric") == true) {
                        pEHB.setHasBiometric(1);
                    } else {
                        pEHB.setHasBiometric(0);
                    }
                    if (resp.getBoolean("hasPassport") == true) {
                        pEHB.setHasPassport(1);
                    } else {
                        pEHB.setHasPassport(0);
                    }
                    if (resp.getBoolean("hasSignature") == true) {
                        pEHB.setHasSignature(1);
                    } else {
                        pEHB.setHasSignature(0);
                    }
                    if (!resp.isNull("password")) {
                        pEHB.setPassword(resp.getString("password"));
                    }
                    if (!resp.isNull("reasonForDelete")) {
                        pEHB.setReasonForDelete(resp.getString("reasonForDelete"));
                    }
                    pEHB.setUserId(resp.getString("userId"));

                    pEHB.setPhotoString(resp.getString("profilePicture"));

                    //Convert base64 Image to Multipart
                    pEHB.setProfilePicture(base64ToMultipart(pEHB.getPhotoString()).getBytes());
                    pEHB.setPhotoType(IConstants.BIOMETRIC_PHOTO_TYPE);

                    if (!resp.isNull("registrars")) {
                        pEHB.setRegistrars(resp.getString("registrars"));
                    }
                    pEHB.setSignature(resp.getString("signature").getBytes());
                    pEHB.setSignatureString(resp.getString("signature"));
                    if (!resp.isNull("role")) {
                        pEHB.setRole(resp.getString("role"));
                    }
                    if (resp.getBoolean("isDeleted") == false) {
                        pEHB.setDeleted(0);
                    } else {
                        pEHB.setDeleted(1);
                    }
                    if (!resp.isNull("deletedBy")) {
                        pEHB.setDeletedBy(resp.getString("deletedBy"));
                    }
                    if (!resp.isNull("dateDeleted"))
                        pEHB.setDateDeleted(resp.getString("dateDeleted"));
                    if (!resp.isNull("dateCreated"))
                        pEHB.setDateCreated(resp.getString("dateCreated"));
                    if (!resp.isNull("staffRoles")) {
                        pEHB.setUserRoles(resp.getJSONArray("staffRoles").toString());
                    }
                    pEHB.setCounter(resp.getLong("counter"));
                    pEHB.setEmpty("false");
                } catch(Exception ex) {
                    pEHB.setResponse(false);
                    pEHB.setBiometricDataExists(false);
                    result.rejectValue("employeeId", "Biometric ID Exist", bc.getStaffTitle()+" Does not exist on the Biometric Database.");
                    System.out.println(ex.getMessage());
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("miniBean", pEHB);
                    return VIEW_NAME;
                }

            }
            pEHB.setBiometricDataExists(true);
            pEHB.setResponse(true);
            addRoleBeanToModel(model, request);
            model.addAttribute("miniBean", pEHB);
            return VIEW_NAME;
       }
         if(pEHB.isResponse() || isButtonTypeClick(request,REQUEST_PARAM_UPDATE)) {
             pEHB.setVerifiedBy(new User(bc.getLoginId()));
             pEHB.setLastModTs(Timestamp.from(Instant.now()));
             this.genericService.saveObject(pEHB);
             AbstractEmployeeEntity employeeEntity = IppmsUtils.loadEmployee(genericService,parentId,bc,bc.getBusinessClientInstId());
             employeeEntity.setBiometricInfo(pEHB);
             this.genericService.saveObject(employeeEntity);
             if(pEHB.isUpdPixBind()){
                 HrPassportInfo hrPassportInfo = this.genericService.loadObjectWithSingleCondition(HrPassportInfo.class,CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), parentId ));
                 if(hrPassportInfo.isNewEntity()){
                     //Blob blob = Hibernate.createBlob(file.getInputStream());
                     hrPassportInfo.setPhoto(pEHB.getProfilePicture());
                     // pHrmPassport.setPhoto(blob);
                     hrPassportInfo.setPhotoType(pEHB.getPhotoType());
                     if(bc.isPensioner())
                         hrPassportInfo.setPensioner(new Pensioner(parentId));
                     else
                         hrPassportInfo.setEmployee(new Employee(parentId));

                     hrPassportInfo.setCreatedBy(new User(bc.getLoginId()));

                 }
                 else if(  !hrPassportInfo.isNewEntity() ) {

                     hrPassportInfo.setPhoto(pEHB.getProfilePicture());
                     if(bc.isPensioner())
                         hrPassportInfo.setPensioner(new Pensioner(parentId));
                     else
                         hrPassportInfo.setEmployee(new Employee(parentId));

                 }
                 hrPassportInfo.setPhotoType(IConstants.BIOMETRIC_PHOTO_TYPE);
                 hrPassportInfo.setLastModBy(new User(bc.getLoginId()));
                 hrPassportInfo.setLastModTs(Timestamp.from(Instant.now()));
                 hrPassportInfo.setBusinessClientId(bc.getBusinessClientInstId());
                 this.genericService.saveObject(hrPassportInfo);
             }

         }else{
             if(pEHB.getVerifiedBy() == null){
                 pEHB.setVerifiedBy(new User(bc.getLoginId()));
                 pEHB.setLastModTs(Timestamp.from(Instant.now()));
                 this.genericService.saveObject(pEHB);
             }
         }

        //if(!bc.isPensioner())
            return "redirect:employeeOverviewForm.do?eid="+parentId;

       // return "redirect:employeeEnquiryViewForm?eid="+parentId;
    }



}

