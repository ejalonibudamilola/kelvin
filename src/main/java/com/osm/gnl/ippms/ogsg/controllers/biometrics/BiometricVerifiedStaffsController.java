/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.biometrics;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HrServiceHelper;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.EmployeeReportService;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping({"/biometricVerified.do"})
@SessionAttributes(types={BusinessEmpOVBean.class})
public class BiometricVerifiedStaffsController extends BaseController{


    private final EmployeeReportService employeeReportService;
    private final HrServiceHelper hrServiceHelper;
    private final PayrollService payrollService;
    private final PaycheckService paycheckService;
    private final String VIEW_NAME = "verification/verifiedBiometricsStaffsForm";

    @Autowired
    public BiometricVerifiedStaffsController(EmployeeReportService employeeReportService, HrServiceHelper hrServiceHelper, PayrollService payrollService, PaycheckService paycheckService) {
        this.employeeReportService = employeeReportService;
        this.hrServiceHelper = hrServiceHelper;
        this.payrollService = payrollService;
        this.paycheckService = paycheckService;
    }

    @ModelAttribute("mdaList")
    protected List<MdaInfo> loadAllMdas(HttpServletRequest request) {
        return this.genericService.loadAllObjectsUsingRestrictions(MdaInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("mappableInd", 0)), "name");
    }

//    @RequestMapping(method={RequestMethod.GET},params = {"pid"})
//    public String setupForm(@RequestParam(value = "pid") Long pBizId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);



        List<Employee> empList = hrServiceHelper.loadBiometricVerifiedStaffs(bc,0L,true);

        BusinessEmpOVBean pList = new BusinessEmpOVBean(empList);

        pList.setId(bc.getBusinessClientInstId());
        pList.setMdaInstId(0L);
        pList.setShowingInactive(false);
        pList.setDisplayTitle(bc.getStaffTypeName()+" with Verified Biometrics Data");
        Object userId = getSessionId(request);
        Navigator.getInstance(userId).setFromClass(getClass());

        model.addAttribute("busEmpOVBean", pList);
        Navigator.getInstance(userId).setFromClass(getClass());
        model.addAttribute("all","none");
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method={RequestMethod.GET},params = {"pid", "mid"})
    public String setupForm(@RequestParam(value = "pid") Long pBizId,
                            @RequestParam(value = "mid") Long pMdaId,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);



        List<Employee> empList = hrServiceHelper.loadBiometricVerifiedStaffs(bc,pMdaId,true);

        BusinessEmpOVBean pList = new BusinessEmpOVBean(empList);

        pList.setId(bc.getBusinessClientInstId());
        pList.setMdaInstId(pMdaId);
        pList.setShowingInactive(false);
        pList.setDisplayTitle(bc.getStaffTypeName()+" with Verified Biometrics Data");
        Object userId = getSessionId(request);
        Navigator.getInstance(userId).setFromClass(getClass());

        model.addAttribute("busEmpOVBean", pList);
        Navigator.getInstance(userId).setFromClass(getClass());
        model.addAttribute("all","none");
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }
    @RequestMapping(method={RequestMethod.GET},params = {"mid","sind"})
    public String setupForm(@RequestParam(value = "mid") Long pMdaId,
                            @RequestParam(value = "sind") int statusInd,
                            Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        List<Employee> empList = hrServiceHelper.loadBiometricVerifiedStaffs(bc,pMdaId,statusInd == 0);

        BusinessEmpOVBean pList = new BusinessEmpOVBean(empList);

        pList.setId(bc.getBusinessClientInstId());
        pList.setMdaInstId(pMdaId);
        pList.setShowingInactive(statusInd == 1);
        if(statusInd == 1)
            pList.setDisplayTitle(bc.getStaffTypeName()+" with Unverified Biometrics Data");
        else
            pList.setDisplayTitle(bc.getStaffTypeName()+" with Verified Biometrics Data");
        Object userId = getSessionId(request);
        Navigator.getInstance(userId).setFromClass(getClass());

        model.addAttribute("busEmpOVBean", pList);
        Navigator.getInstance(userId).setFromClass(getClass());
        model.addAttribute("all","none");
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
                                @RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("busEmpOVBean")
                                        BusinessEmpOVBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {

            return "redirect:biometricVerified.do?mid="+pLPB.getMdaInstId()+"&sind="+pLPB.getStatusInd();

        }
        return "redirect:biometricVerified.do?pid="+bc.getBusinessClientInstId();
    }

}
