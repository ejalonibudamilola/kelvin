/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Cadre;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.CadreValidator;
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
@RequestMapping({"/editCadre.do"})
@SessionAttributes({"cadreBean"})
public class CadreFormController extends BaseController {

    @Autowired
    private CadreValidator validator;

    private final String VIEW = "configcontrol/createEditCadreForm";

    @ModelAttribute("salaryTypeList")
    protected List<SalaryType> loadSalaryTypes(HttpServletRequest request) {
        return this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class,
                Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("selectableInd", ON)), "name");
    }

    public CadreFormController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Cadre wEDT = new Cadre();
        wEDT.setSalaryType(new SalaryType());

        model.addAttribute("displayList", this.makeCadreList(request, false));
        model.addAttribute("displayTitle", "Create New Cadre");
        model.addAttribute("cadreBean", wEDT);
        model.addAttribute("roleBean", bc);
        model.addAttribute("ce", IConstants.OFF);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"cid"})
    public String setupForm(@RequestParam("cid") Long pEduId, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Cadre wEDT = this.genericService.loadObjectWithSingleCondition(Cadre.class, CustomPredicate.procurePredicate("id", pEduId));

        if (!wEDT.getBusinessClientId().equals(bc.getBusinessClientInstId()))
            return "redirect:editCadre.do";

        wEDT.setEditMode(true);
        wEDT.setDefaultIndBind(wEDT.isDefaultCadre());
        wEDT.setSelectableBind(wEDT.isSelectable());

        model.addAttribute("displayList", this.makeCadreList(request, false));
        model.addAttribute("displayTitle", "Edit Cadre");
        model.addAttribute("cadreBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"bid", "s"})
    public String setupForm(@RequestParam("bid") Long pObjectId, @RequestParam("s") int pSaved, Model model,
                            HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Cadre wEDT = this.genericService.loadObjectWithSingleCondition(Cadre.class,
                CustomPredicate.procurePredicate("id", pObjectId));

        if (!wEDT.getBusinessClientId().equals(bc.getBusinessClientInstId()))
            return "redirect:editCadre.do";

        String wMessage = "";

        if (pSaved == 1) {
            wMessage = " Cadre " + wEDT.getName() + " edited successfully.";
        } else {
            wMessage = " Cadre " + wEDT.getName() + " created successfully.";
        }

        model.addAttribute(IConstants.SAVED_MSG, wMessage);
        wEDT = new Cadre();

        model.addAttribute("displayList", this.makeCadreList(request, true));
        model.addAttribute("displayTitle", "Create/Edit Cadre");
        model.addAttribute("saved", true);
        model.addAttribute("cadreBean", wEDT);
        model.addAttribute("roleBean", bc);
        model.addAttribute("ce", IConstants.ON);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("cadreBean") Cadre pEHB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }

        validator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("cadreBean", pEHB);
            model.addAttribute("roleBean", bc);
            return VIEW;
        }
        if(IppmsUtils.isNullOrLessThanOne(pEHB.getSalaryType().getId()))
            pEHB.setSalaryType(null);

        if(pEHB.isDefaultIndBind()){
            pEHB.setDefaultInd(ON);
        }else{
            pEHB.setDefaultInd(OFF);
        }

        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        int wEditType = 1;
        if (!pEHB.isEditMode()) {
            wEditType = 2;
            pEHB.setCreatedBy(new User(bc.getLoginId()));
            pEHB.setBusinessClientId(bc.getBusinessClientInstId());
            pEHB.setSelectableInd(ON);
        }else{
            if(pEHB.isSelectableBind()){
                pEHB.setSelectableInd(ON);
            }else{
                pEHB.setSelectableInd(OFF);
            }
        }

        this.genericService.saveObject(pEHB);

        return "redirect:editCadre.do?bid=" + pEHB.getId() + "&s=" + wEditType;
    }

    private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(HttpServletRequest request, boolean pSaved) {

        PaginationBean paginationBean = this.getPaginationInfo(request);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<Cadre> wAllList = null;
        List<Cadre> wRetList = null;

        //Do we do the list upside down now?
        wAllList = this.genericService.loadAllObjectsWithSingleCondition(Cadre.class, getBusinessClientIdPredicate(request), "name");
        if (pSaved) {
            Collections.sort(wAllList, Comparator.comparing(Cadre::getId).reversed());
        }


        if (wAllList.size() > 10) {

            wRetList = (List<Cadre>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 20, wAllList);

        } else {
            wRetList = wAllList;
        }

        PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        return wPBO;
    }
    private List<Cadre> makeCadreList (HttpServletRequest request, boolean pSaved) {


        List<Cadre> wAllList = null;
        List<Cadre> wRetList = null;

        //Do we do the list upside down now?
        wAllList = this.genericService.loadAllObjectsWithSingleCondition(Cadre.class, getBusinessClientIdPredicate(request), "name");
        if (pSaved) {
            Collections.sort(wAllList, Comparator.comparing(Cadre::getId).reversed());
        }

        wRetList = wAllList;


        return wRetList;
    }

}
