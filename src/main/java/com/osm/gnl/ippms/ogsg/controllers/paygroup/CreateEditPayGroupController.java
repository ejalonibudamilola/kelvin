/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.paygroup;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.StoredProcedureService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.SalaryService;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.deduction.CreateDeductionTypeValidator;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({"/createSalaryType.do"})
@SessionAttributes(types = {SalaryType.class})
public class CreateEditPayGroupController extends BaseController {

    private final SalaryService salaryService;
    private final StoredProcedureService storedProcedureService;
    private final CreateDeductionTypeValidator createDeductionTypeValidator;

    private final String VIEW_NAME = "configcontrol/payGroupTypeForm";

    @Autowired
    public CreateEditPayGroupController(SalaryService salaryService, StoredProcedureService storedProcedureService, CreateDeductionTypeValidator createDeductionTypeValidator) {
        this.salaryService = salaryService;
        this.storedProcedureService = storedProcedureService;
        this.createDeductionTypeValidator = createDeductionTypeValidator;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        SalaryType salaryType = new SalaryType();
        salaryType.setPenContTypeInd(-1);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", salaryType);
        model.addAttribute("displayList", this.makeSalaryTypeList(request, false));
        model = this.addViewHeaders(model, "Create");
        model.addAttribute("ce", IConstants.OFF);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"bid"})
    public String setupForm(@RequestParam("bid") Long pSalId, Model model, HttpServletRequest request)
            throws Exception {

        SessionManagerService.manageSession(request, model);

        SalaryType wEDT = this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(CustomPredicate.procurePredicate("id", pSalId),
                getBusinessClientIdPredicate(request)));
        wEDT.setEditMode(true);
//        PredicateBuilder predicateBuilder = new PredicateBuilder();
 //       predicateBuilder.addPredicate(getBusinessClientIdPredicate(request))
//                .addPredicate(CustomPredicate.procurePredicate("salaryType.id", pSalId));
//        if(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,SalaryInfo.class) > 0)
//            wEDT.setShowEditDetailsRow(true);

        wEDT.setSelectableBind(wEDT.isSelectable());
        wEDT.setConsolidatedBind(wEDT.isConsolidated());
        wEDT.setOldPayGroupCode(wEDT.getPayGroupCode());
        wEDT.setPensionExemptIndBind(wEDT.isExemptFromPension());
        model.addAttribute("displayList", this.makeSalaryTypeList(request, false));
        model = this.addViewHeaders(model, "Edit");
        model.addAttribute("miniBean", wEDT);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"bid", "s"})
    public String setupForm(@RequestParam("bid") Long pSalId, @RequestParam("s") int pSaved, Model model,
                            HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        SalaryType wEDT = this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(CustomPredicate.procurePredicate("id", pSalId),
                getBusinessClientIdPredicate(request)));

        String wMessage;

        if (pSaved == 1) {
            wMessage = "Pay Group '" + wEDT.getName() + "' edited successfully.";
            model = this.addViewHeaders(model, "Edit");
        } else {
            wMessage = "Pay Group '" + wEDT.getName() + "' created successfully.";
            model = this.addViewHeaders(model, "Create");
        }

        model.addAttribute(IConstants.SAVED_MSG, wMessage);
        wEDT = new SalaryType();
        wEDT.setPenContTypeInd(-1);
        model.addAttribute("displayList", this.makeSalaryTypeList(request, true));
        model.addAttribute("saved", true);
        model.addAttribute("miniBean", wEDT);
        addRoleBeanToModel(model, request);
        model.addAttribute("ce", IConstants.ON);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") SalaryType pEHB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }

        createDeductionTypeValidator.validateForPayGroup(pEHB, result, bc);
        if (result.hasErrors()) {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            model.addAttribute("displayList", this.makeSalaryTypeList(request, false));
            model = this.addViewHeaders(model, "Create");
            model.addAttribute("ce", IConstants.OFF);
            addRoleBeanToModel(model, request);
            return VIEW_NAME;
        }

        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        int wEditType = 1;
        boolean updatePayGroupCode = false;
        if (!pEHB.isEditMode()) {
            wEditType = 2;

            pEHB.setBusinessClientId(bc.getBusinessClientInstId());
            pEHB.setSelectableInd(ON);
            pEHB.setSalaryTypeCode(genericService.loadMaxValueByClassAndIntColName(SalaryType.class, "salaryTypeCode") + 1);
            pEHB.setCreatedBy(new User(bc.getLoginId()));
        } else {
            if (IppmsUtils.isNotNullOrEmpty(pEHB.getPayGroupCode())) {
                if (!pEHB.getPayGroupCode().equalsIgnoreCase(pEHB.getOldPayGroupCode()))
                    updatePayGroupCode = true;
            }
        }
        pEHB.setConsolidatedInd(pEHB.isConsolidatedBind() ? ON : OFF);
        pEHB.setSelectableInd(pEHB.isSelectableBind() ? ON : OFF);
        pEHB.setPensionExemptInd(pEHB.isPensionExemptIndBind() ? ON : OFF);


        this.genericService.saveObject(pEHB);
        if (updatePayGroupCode)
            storedProcedureService.callStoredProcedure(IConstants.UPD_PAY_GROUP_CODE, bc.getBusinessClientInstId(), bc.getLoginId(), pEHB.getId(), pEHB.getPayGroupCode());


        return "redirect:createSalaryType.do?bid=" + pEHB.getId() + "&s=" + wEditType;
    }


    private List<SalaryType> makeSalaryTypeList(HttpServletRequest request, boolean pSaved) {


        List<SalaryType> wAllList;
        List<SalaryType> wRetList;

        //Do we do the list upside down now?
        wAllList = this.genericService.loadAllObjectsWithSingleCondition(SalaryType.class, getBusinessClientIdPredicate(request), "name");
        if (pSaved) {
            Collections.sort(wAllList, Comparator.comparing(SalaryType::getId).reversed());
        }

        wRetList = wAllList;


        return wRetList;
    }

    private Model addViewHeaders(Model model, String action) {
        addPageTitle(model, action + " Pay Group");
        addMainHeader(model, action + " Pay Group");
        addTableHeader(model, "Pay Group Details");
        return model;
    }
}
