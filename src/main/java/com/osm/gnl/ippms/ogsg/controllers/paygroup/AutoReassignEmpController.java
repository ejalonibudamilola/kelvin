/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.paygroup;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.SalaryService;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignMasterBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.promotion.MassReassignBean;
import com.osm.gnl.ippms.ogsg.engine.MassReassignStaffs;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.validators.hr.MassReassignValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping({"/massReassign.do"})
@SessionAttributes(types={MassReassignMasterBean.class})
public class AutoReassignEmpController extends BaseController {


    private final SalaryService salaryService;


    private final MassReassignValidator massReassignValidator;

    private final String VIEW_NAME = "configcontrol/massReassignForm";

    @Autowired
    public AutoReassignEmpController(SalaryService salaryService, MassReassignValidator massReassignValidator) {
        this.salaryService = salaryService;
        this.massReassignValidator = massReassignValidator;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        List<SalaryType> salaryTypeList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class,Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("selectableInd",ON), CustomPredicate.procurePredicate("deactivatedInd", OFF)),"name");
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", new MassReassignMasterBean());
        model.addAttribute("salaryTypeList", salaryTypeList);

        return VIEW_NAME;
    }


    @RequestMapping(method = {RequestMethod.POST })
    public String processSubmit(@RequestParam(value="_go", required=false) String go,
                                @RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") MassReassignMasterBean pEHB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }

        massReassignValidator.validate(pEHB, result,bc);
        if (result.hasErrors()) {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            List<SalaryType> salaryTypeList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class,Arrays.asList(getBusinessClientIdPredicate(request),
                    CustomPredicate.procurePredicate("selectableInd",ON), CustomPredicate.procurePredicate("deactivatedInd", OFF)),"name");

            model.addAttribute("salaryTypeList", salaryTypeList);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            addRoleBeanToModel(model, request);
            return VIEW_NAME;
        }

        pEHB.setCreatedBy(new User(bc.getLoginId()));
        pEHB.setLastModBy(pEHB.getCreatedBy());
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        pEHB.setBusinessClientId(bc.getBusinessClientInstId());
        //-- Get all employees on this pay group.
        HashMap<String, Long> toSalaryInfoList = salaryService.makeSalaryLevelStepAndIdMap(bc, pEHB.getToSalaryType().getId());
        HashMap<String, Long> fromSalaryInfoList = salaryService.makeSalaryLevelStepAndIdMap(bc, pEHB.getFromSalaryType().getId());
        List<MassReassignDetailsBean> affectedEmps = salaryService.getEmpForPayGroupMove(bc,pEHB.getFromSalaryType().getId());

        MassReassignBean wSJB = this.genericService.loadObjectUsingRestriction(MassReassignBean.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("fromSalaryId", pEHB.getFromSalaryType().getId()),
                        CustomPredicate.procurePredicate("toSalaryId", pEHB.getToSalaryType().getId())));

        if (!wSJB.isNewEntity() && wSJB.isBeingRun() ) {
            SalaryType st = genericService.loadObjectById(SalaryType.class,pEHB.getFromSalaryType().getId());
            SalaryType _st = genericService.loadObjectById(SalaryType.class,pEHB.getToSalaryType().getId());
            result.rejectValue("", "InvalidValue", "Mass Reassignment from '"+st.getName()+"' to '"+_st.getName()+"' is being run currently by "+wSJB.getLastRunBy());
            List<SalaryType> salaryTypeList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class,Arrays.asList(getBusinessClientIdPredicate(request),
                    CustomPredicate.procurePredicate("selectableInd",ON), CustomPredicate.procurePredicate("deactivatedInd", OFF)),"name");

            model.addAttribute("salaryTypeList", salaryTypeList);
            addDisplayErrorsToModel(model, request);

            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            addRoleBeanToModel(model, request);
            return VIEW_NAME;
        }
        wSJB.setRunning(1);
        wSJB.setBusinessClientId(bc.getBusinessClientInstId());
        wSJB.setLastRunBy(bc.getUserName());
        wSJB.setStartDate(LocalDate.now());
        wSJB.setStartTime(PayrollBeanUtils.getCurrentTime(false));
        wSJB.setFromSalaryId(pEHB.getFromSalaryType().getId());
        wSJB.setToSalaryId(pEHB.getToSalaryType().getId());
        this.genericService.saveObject(wSJB);
        MassReassignStaffs peas = new MassReassignStaffs(genericService,fromSalaryInfoList,toSalaryInfoList,affectedEmps,bc,pEHB,wSJB);

        addSessionAttribute(request, "massReassign", peas);

        Thread t = new Thread(peas);
        t.start();


        return "redirect:statusForMassReassign.do" ;
    }


}
