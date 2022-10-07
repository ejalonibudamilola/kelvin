/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.biometrics;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HrServiceHelper;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeHrBean;
import com.osm.gnl.ippms.ogsg.engine.VerifyAllBiometrics;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.utils.RestCall;
import com.osm.gnl.ippms.ogsg.validators.employee.CreateNewEmployeeValidator;
import com.osm.gnl.ippms.ogsg.validators.employee.NewEmployeeApiValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/autoVerifyBiometrics.do"})
@SessionAttributes(types = {EmployeeHrBean.class})
public class AutoVerifyBiometricController extends BaseController {

    private final CreateNewEmployeeValidator validator;

    private final NewEmployeeApiValidator newEmployeeApiValidator;

    private final HrServiceHelper hrServiceHelper;

    private final PayrollService payrollService;

    private final RestCall restCall;

    @Value("${API_IP}")
    private String API_IP;

    @Value("${API_USERNAME}")
    private String API_USERNAME;

    @Value("${API_PASSWORD}")
    private String API_PASSWORD;

    private final String VIEW_NAME = "verification/verifyBiometricsForm";

    @ModelAttribute("mdaList")
    protected List<MdaInfo> loadAllMdas(HttpServletRequest request) {
        return this.genericService.loadAllObjectsUsingRestrictions(MdaInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("mappableInd", 0)), "name");
    }

    public AutoVerifyBiometricController(CreateNewEmployeeValidator validator, NewEmployeeApiValidator newEmployeeApiValidator,
                                         HrServiceHelper employeeService, PayrollService payrollService, RestCall restCall) {
        this.validator = validator;
        this.newEmployeeApiValidator = newEmployeeApiValidator;
        this.hrServiceHelper = employeeService;
        this.payrollService = payrollService;
        this.restCall = restCall;
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        EmployeeHrBean wEHB = new EmployeeHrBean();

        wEHB.setRoleBean(bc);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wEHB);
        return VIEW_NAME;
    }




    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_go", required = false) String go,
                                @ModelAttribute("miniBean") EmployeeHrBean pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        if (super.isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }


        if(IppmsUtils.isNullOrLessThanOne(pEHB.getMdaId())){
            result.rejectValue("mdaId","REQUIRED.VALUE", "Please select a value for "+getBusinessCertificate(request).getMdaTitle());
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            return VIEW_NAME;
        }


        int size = this.hrServiceHelper.getNoOfRecords(getBusinessCertificate(request),pEHB.getMdaId());

        VerifyAllBiometrics verifier = new VerifyAllBiometrics(genericService,hrServiceHelper,API_USERNAME,API_PASSWORD,restCall,getBusinessCertificate(request),size,this.genericService.loadObjectById(MdaInfo.class, pEHB.getMdaId()));

             addSessionAttribute(request,"bv_mda",pEHB.getMdaId());

             addSessionAttribute(request, "myVerifier", verifier);

            Thread t = new Thread(verifier);
            t.start();
        return "redirect:displayBioVerificationStatus.do";
    }

}
