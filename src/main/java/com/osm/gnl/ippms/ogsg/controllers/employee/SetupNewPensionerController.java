/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Cadre;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeHrBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.employee.SetupEmployeeMaster;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClientParentMap;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.EmployeeMiniBean;
import com.osm.gnl.ippms.ogsg.validators.employee.SetupNewEmployeeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping({ "/createNewPensioner.do" })
@SessionAttributes(types = { Pensioner.class })
public class SetupNewPensionerController extends BaseController {

    private final SetupNewEmployeeValidator validator;

    private final EmployeeService employeeService;

    private PayrollService payrollService;

    private final String VIEW_NAME = "employee/setupNewPensionerForm";

    @ModelAttribute("mdaList")
    protected List<MdaInfo> loadAllMdas(HttpServletRequest request) {
        return this.genericService.loadAllObjectsUsingRestrictions(MdaInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("mappableInd", 0)), "name");
    }

    //--For LGA We need Cadres
    @ModelAttribute("cadreList")
    protected List<Cadre> loadCadreList(HttpServletRequest request) {
        return this.genericService.loadAllObjectsWithSingleCondition(Cadre.class, getBusinessClientIdPredicate(request), "name");
    }

    @Autowired
    public SetupNewPensionerController(SetupNewEmployeeValidator validator, EmployeeService employeeService, PayrollService payrollService) {
        this.validator = validator;
        this.employeeService = employeeService;
        this.payrollService = payrollService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        EmployeeHrBean wEHB = new EmployeeHrBean();

        wEHB.setRoleBean(bc);
        ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class,getBusinessClientIdPredicate(request));
        if(configurationBean.isResidenceIdRequired())
            wEHB.setResidenceIdStr("Residence ID*");
        else
            wEHB.setResidenceIdStr("Residence ID");

        wEHB.setNinStr("NIN");
        if(configurationBean.isNinRequired())
            wEHB.setNinStr(wEHB.getNinStr()+"*");

        addRoleBeanToModel(model, request);

        model = treatPensioners(model, bc, request);
        model.addAttribute("salaryStructureList", new ArrayList<SalaryInfo>());
        model.addAttribute("departmentList", new ArrayList<Department>());
        model.addAttribute("miniBean", wEHB);
        return VIEW_NAME;
    }


    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") EmployeeHrBean pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }

        validator.validate(pEHB, result, getBusinessCertificate(request));
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            //Now we need to set all settable...
            model = this.setSettables(pEHB, model, request, bc);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            return VIEW_NAME;
        }

        AbstractEmployeeEntity wEmp =  IppmsUtils.makeEmployeeObject(bc);

        if(bc.isPensioner()){
            //Add its parent client.
            ( (Pensioner)wEmp).setParentBusinessClientId(pEHB.getParentClientId());
        }
        if(bc.isLocalGovt()){
            wEmp.setFileNo(pEHB.getFileNo());
        }
        wEmp.setSalaryInfo(this.genericService.loadObjectById(SalaryInfo.class, pEHB.getSalaryStructureId()));
        wEmp.setResidenceId(pEHB.getResidenceId());
        wEmp.setNin(pEHB.getNin());
        wEmp.setFirstName(pEHB.getFirstName());
        wEmp.setLastName(pEHB.getLastName());
        wEmp.setInitials(pEHB.getMiddleName());
        wEmp.setEmployeeId(pEHB.getEmployeeId());
        wEmp.setRank(this.genericService.loadObjectById(Rank.class, pEHB.getRankInstId()));

        MdaDeptMap mdaDeptMap = this.genericService.loadObjectUsingRestriction(MdaDeptMap.class, Arrays.asList(CustomPredicate.procurePredicate("mdaInfo.id", pEHB.getMdaId()),
                CustomPredicate.procurePredicate("department.id", pEHB.getDeptId())));
        if(mdaDeptMap.isNewEntity()){
            result.rejectValue("mdaId","Invalid.value",bc.getMdaTitle()+ " Is not Mapped to a Default Department.");
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            //Now we need to set all settable...
            model = this.setSettables(pEHB, model, request, bc);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            return VIEW_NAME;
        }

        wEmp.setMdaDeptMap(mdaDeptMap);


        //Before we do all this...find out if this Employee has been tried before
        SetupEmployeeMaster wSEM = this.genericService.loadObjectUsingRestriction(SetupEmployeeMaster.class, Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("employeeId", wEmp.getEmployeeId())));
        if (!wSEM.isNewEntity()) {
            if (wSEM.isRejected()) {
                result.rejectValue("", "warning", "This " + bc.getStaffTypeName() + " creation has been rejected because of 'Name Conflict'");
                result.rejectValue("", "warning", "Please contact user '" + wSEM.getApprovedBy() + "' for more information and or resolution.");
                addRoleBeanToModel(model, request);
                addDisplayErrorsToModel(model, request);
                //Now we need to set all settables...
                model = this.setSettables(pEHB, model, request, bc);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pEHB);
                return VIEW_NAME;
            }
            if (!wSEM.isRejectedOrApproved()) {
                if(bc.isPensioner())
                    return "redirect:setUpPenErrorForm.do?mid=" + wSEM.getId();
                return "redirect:setUpEmpErrorForm.do?mid=" + wSEM.getId();
            }
        } else {
            //before we save this dude....check if this exists by name.
            /*List<EmployeeMiniBean> wErrorList = employeeService.findSimilarlyNamedEmps(wEmp, bc);

            if (wErrorList != null && !wErrorList.isEmpty()) {
                addSessionAttribute(request, IConstants.EMP_SKEL, wEmp);
                addSessionAttribute(request, IConstants.EMP_SKEL_ERR, wErrorList);
                if(bc.isPensioner())
                    return "redirect:setUpPenErrorForm.do";
                return "redirect:setUpEmpErrorForm.do";
            }*/
        }


        addSessionAttribute(request, IConstants.EMP_SKEL, wEmp);
        if(bc.isPensioner())
            return "redirect:pensionerForm.do";
        return "redirect:employeeForm.do";
    }


    private Model setSettables(EmployeeHrBean pEHB, Model pModel, HttpServletRequest request, BusinessCertificate businessCertificate)
            throws IllegalAccessException, InstantiationException {

        if (businessCertificate.isPensioner()) {

            List<BusinessClientParentMap> wList = this.genericService.loadAllObjectsWithSingleCondition(BusinessClientParentMap.class,CustomPredicate.procurePredicate("childBusinessClient.id", businessCertificate.getBusinessClientInstId()), null);
            pModel.addAttribute("parentClientList", wList);
            if(IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getMdaId()))
                pModel.addAttribute("departmentList", genericService.loadAllObjectsWithSingleCondition(Department.class,CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),null));

            if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getParentClientId())) {
                List<Rank> rankList = this.genericService.loadAllObjectsWithSingleCondition(Rank.class, CustomPredicate.procurePredicate("businessClientId", pEHB.getParentClientId()), "name");
                pModel.addAttribute("rankList", rankList);
                if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getRankInstId())) {
                    Rank rank = this.genericService.loadObjectById(Rank.class, pEHB.getRankInstId());
                    if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getSalaryTypeId())) {
                        pModel.addAttribute("salaryTypeList", this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId",rank.getBusinessClientId()),
                                CustomPredicate.procurePredicate("selectableInd", IConstants.ON)), "name"));

                        List<SalaryInfo> salaryInfoList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class, Arrays.asList(
                                CustomPredicate.procurePredicate("salaryType.id", pEHB.getSalaryTypeId())), null);
                        Collections.sort(salaryInfoList, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
                        pModel.addAttribute("salaryStructureList", salaryInfoList);

                    } else {

                        List<SalaryType> wSalaryTypeList;
                        if (rank.getCadre() == null || rank.getCadre().getSalaryType() == null) {
                            wSalaryTypeList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(
                                    CustomPredicate.procurePredicate("selectableInd", IConstants.ON), CustomPredicate.procurePredicate("businessClientId",rank.getBusinessClientId())), "name");
                        } else {
                            wSalaryTypeList = new ArrayList<>();
                            wSalaryTypeList.add(rank.getCadre().getSalaryType());
                        }
                        pModel.addAttribute("salaryTypeList", wSalaryTypeList);
                    }
                }

            }
        } else {
            if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getMdaId())) {

                List<MdaDeptMap> wList = this.genericService.loadAllObjectsWithSingleCondition(MdaDeptMap.class, CustomPredicate.procurePredicate("mdaInfo.id", pEHB.getMdaId()), null);
                List<Department> wDeptList = new ArrayList<Department>();
                for (MdaDeptMap m : wList)
                    wDeptList.add(m.getDepartment());

                pModel.addAttribute("departmentList", wDeptList);
                //Now check if a Salary Type has been chosen...
            }
            if (businessCertificate.isLocalGovt()) {
                if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getCadreInstId())) {
                    List<Rank> rankList = this.genericService.loadAllObjectsWithSingleCondition(Rank.class, CustomPredicate.procurePredicate("cadre.id", pEHB.getCadreInstId()), "name");
                    pModel.addAttribute("rankList", rankList);
                    if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getRankInstId())) {
                        Rank wR = this.genericService.loadObjectById(Rank.class, pEHB.getRankInstId());
                        pModel.addAttribute("salaryStructureList", this.payrollService.loadSalaryInfoByRankInfo(wR, businessCertificate.getBusinessClientInstId(), true));
                    } else {
                        pModel.addAttribute("salaryStructureList", new ArrayList());
                    }
                } else {
                    pModel.addAttribute("rankList", new ArrayList());
                    pModel.addAttribute("salaryStructureList", new ArrayList());
                }
            } else {
                pModel.addAttribute("rankList", this.genericService.loadAllObjectsWithSingleCondition(Rank.class, getBusinessClientIdPredicate(request), "name"));
                if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getRankInstId())) {
                    Rank rank = this.genericService.loadObjectById(Rank.class, pEHB.getRankInstId());
                    if (pEHB.getSalaryTypeId() != -1) {
                        pModel.addAttribute("salaryTypeList", this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(getBusinessClientIdPredicate(request),
                                CustomPredicate.procurePredicate("selectableInd", IConstants.ON)), "name"));

                        List<SalaryInfo> salaryInfoList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class, Arrays.asList(
                                CustomPredicate.procurePredicate("salaryType.id", pEHB.getSalaryTypeId())), null);
                        Collections.sort(salaryInfoList, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
                        pModel.addAttribute("salaryStructureList", salaryInfoList);

                    } else {

                        List<SalaryType> wSalaryTypeList;
                        if (rank.getCadre() == null || rank.getCadre().getSalaryType() == null) {
                            wSalaryTypeList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(
                                    CustomPredicate.procurePredicate("selectableInd", IConstants.ON),
                                    this.getBusinessClientIdPredicate(request)), "name");
                        } else {
                            wSalaryTypeList = new ArrayList<>();
                            wSalaryTypeList.add(rank.getCadre().getSalaryType());
                        }
                        pModel.addAttribute("salaryTypeList", wSalaryTypeList);
                    }

                }

            }
        }

        ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class,getBusinessClientIdPredicate(request));
        if(configurationBean.isResidenceIdRequired())
            pEHB.setResidenceIdStr("Residence ID*");
        else
            pEHB.setResidenceIdStr("Residence ID");

        pEHB.setNinStr("NIN");
        if(configurationBean.isNinRequired())
            pEHB.setNinStr(pEHB.getNinStr()+"*");

        return pModel;

    }

    private Model treatPensioners(Model model, BusinessCertificate bc, HttpServletRequest request) {
        if (!bc.isLocalGovt()) {
            if (bc.isPensioner()) {
                model.addAttribute("parentClientList", this.genericService.loadAllObjectsWithSingleCondition(BusinessClientParentMap.class, CustomPredicate.procurePredicate("childBusinessClient.id", bc.getBusinessClientInstId()), null));

            } else {
                model.addAttribute("rankList", this.genericService.loadAllObjectsWithSingleCondition(Rank.class, getBusinessClientIdPredicate(request), "name"));
            }

        } else {
            model.addAttribute("salaryTypeList", new ArrayList<SalaryType>());
        }


        return model;
    }
}
