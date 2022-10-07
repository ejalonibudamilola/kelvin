/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeHrBean;
import com.osm.gnl.ippms.ogsg.domain.employee.BiometricInfo;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.utils.RestCall;
import com.osm.gnl.ippms.ogsg.validators.employee.CreateNewEmployeeValidator;
import com.osm.gnl.ippms.ogsg.validators.employee.NewEmployeeApiValidator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({"/createNewEmployee.do"})
@SessionAttributes(types = {EmployeeHrBean.class})
public class BiometricStaffController extends BaseController {

    @Autowired
    private CreateNewEmployeeValidator validator;

    @Autowired
    private NewEmployeeApiValidator newEmployeeApiValidator;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private RestCall restCall;

    @Value("${API_IP}")
    private String API_IP;

    @Value("${API_USERNAME}")
    private String API_USERNAME;

   // @Value("${API_EMPLOYEE_ID}")
   // private String API_EMPLOYEE_ID;

    @Value("${API_LAST_NAME}")
    private String API_LAST_NAME;

    @Value("${API_PASSWORD}")
    private String API_PASSWORD;

    private final String VIEW_NAME = "employee/verifyEmployeeForm";




    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if(bc.isPensioner()){
            return "redirect:createNewPensioner.do";
        }

        EmployeeHrBean wEHB = new EmployeeHrBean();

        wEHB.setRoleBean(bc);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wEHB);
        return VIEW_NAME;
    }



    @RequestMapping(method = {RequestMethod.GET}, params={"eId"})
    public String setupForm(@RequestParam("eId") String eId, Model model, HttpServletRequest request, @ModelAttribute("miniBean") EmployeeHrBean wEHB, BindingResult result) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        BiometricInfo biometricInfo = new BiometricInfo();

        wEHB.setRoleBean(bc);

        String endpoint = "api/Employee/GetAll?username="+API_USERNAME+"" +
                "&password="+API_PASSWORD+"&staffId="+eId;
        JSONObject resp;

        try{
            resp = new JSONObject(restCall.executeGet(endpoint));
        }catch (Exception wEx){
           // wEHB.setFail(true);
            wEHB.setResponse(false);
            result.rejectValue("employeeId", "Biometric ID Exist", "Biometric Database returns no value.");
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", wEHB);
            return VIEW_NAME;
        }
        if(resp.isEmpty()){
            wEHB.setFail(true);
            wEHB.setResponse(false);
            result.rejectValue("employeeId", "Biometric ID Exist", "Biometric Database returns no value.");
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", wEHB);
            return VIEW_NAME;
        }
        else {
            try {
                biometricInfo.setEmployeeId(resp.getString("employeeId").toUpperCase());


                biometricInfo.setBioId(resp.getLong("id"));
               // biometricInfo.setBvnNumber(resp.getString("bvn"));
                if (!resp.isNull("phoneNumber"))
                   biometricInfo.setPhoneNumber(resp.getString("phoneNumber"));
                else
                    biometricInfo.setPhoneNumber("0");

                //--Create a validator...
                //1. It checks for the existence
                newEmployeeApiValidator.validate(biometricInfo, result, getBusinessCertificate(request));
                if (result.hasErrors()) {
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("miniBean", wEHB);
                    return VIEW_NAME;
                }

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
                biometricInfo.setProfilePicture(base64ToMultipart(biometricInfo.getPhotoString()).getBytes());
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
            } catch(Exception ex) {
                wEHB.setFail(true);
                wEHB.setResponse(false);
                result.rejectValue("employeeId", "Biometric ID Exist", bc.getStaffTitle()+" Does not exist on the Biometric Database.");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", wEHB);
                return VIEW_NAME;
            }

        }


        //Data Integrity Issues
        biometricInfo.setBusinessClientId(bc.getBusinessClientInstId());
        wEHB.setPhoto(biometricInfo.getPhotoString());
        wEHB.setSignature(biometricInfo.getSignatureString());
        wEHB.setBiometricInfoBean(biometricInfo);




        wEHB.setResponse(true);
        request.getSession().setAttribute("biometricData", biometricInfo);
        model.addAttribute("miniBean", wEHB);

        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }





    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_go", required = false) String go,
                                @ModelAttribute("miniBean") EmployeeHrBean pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (super.isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }

        else if(super.isButtonTypeClick(request, REQUEST_PARAM_GO)){
            return "redirect:setUpNewEmployee.do";
        }

        validator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            return VIEW_NAME;
        }

            return "redirect:createNewEmployee.do?eId="+pEHB.getEmployeeId();
    }

}
