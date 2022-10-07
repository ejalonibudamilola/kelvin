/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Cadre;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.RankValidator;
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
import java.util.*;

@Controller
@RequestMapping({"/editRank.do"})
@SessionAttributes({"rankBean"})
public class CreateEditRankController extends BaseController {

    @Autowired
    private PayrollService payrollService;
    @Autowired
    private RankValidator validator;

    private final String VIEW = "configcontrol/createEditRankForm";

    @ModelAttribute("cadreList")
    protected List<Cadre> loadSalaryTypes(HttpServletRequest request) {
        return this.genericService.loadAllObjectsUsingRestrictions(Cadre.class,
                Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("selectableInd", ON)), "name");
    }

    public CreateEditRankController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Rank wEDT = new Rank();
        wEDT.setCadreInstId(0L);
//        model.addAttribute("displayList", this.makePaginatedList(request, false));
        model.addAttribute("displayList", this.makeRankList(request, false));
        model.addAttribute("displayTitle", "Create New Rank");
        model.addAttribute("rankBean", wEDT);
        model.addAttribute("roleBean", bc);
        model.addAttribute("ce", IConstants.OFF);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"cid"})
    public String setupForm(@RequestParam("cid") Long pEduId, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Rank wEDT = this.genericService.loadObjectWithSingleCondition(Rank.class, CustomPredicate.procurePredicate("id", pEduId));
        wEDT.setCadreInstId(wEDT.getCadre().getId());

        if (!wEDT.getBusinessClientId().equals(bc.getBusinessClientInstId()))
            return "redirect:editRank.do";

        wEDT.setEditMode(true);
        if(wEDT.getCadre().getSalaryType() != null && !wEDT.getCadre().getSalaryType().isNewEntity() ){
            model.addAttribute("levelList",payrollService.makeLevelOrStepList(wEDT.getCadre().getSalaryType().getId(),true));
            model.addAttribute("stepList",payrollService.makeLevelOrStepList(wEDT.getCadre().getSalaryType().getId(),false));
        }else{
            model.addAttribute("levelList",new ArrayList<NamedEntity>());
            model.addAttribute("stepList",new ArrayList<NamedEntity>());
        }

        model.addAttribute("displayList", this.makeRankList(request, false));
        model.addAttribute("displayTitle", "Edit Rank");
        model.addAttribute("rankBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"bid", "s"})
    public String setupForm(@RequestParam("bid") Long pObjectId, @RequestParam("s") int pSaved, Model model,
                            HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Rank wEDT = this.genericService.loadObjectWithSingleCondition(Rank.class,
                CustomPredicate.procurePredicate("id", pObjectId));

        if (!wEDT.getBusinessClientId().equals(bc.getBusinessClientInstId()))
            return "redirect:editRank.do";

        String wMessage = "";

        if (pSaved == 1) {
            wMessage = " Rank '" + wEDT.getName() + "' edited successfully.";
        } else {
            wMessage = " Rank '" + wEDT.getName() + "' created successfully.";
        }

        model.addAttribute(IConstants.SAVED_MSG, wMessage);
        wEDT = new Rank();

//        model.addAttribute("displayList", this.makePaginatedList(request, true));
        model.addAttribute("displayList", this.makeRankList(request, true));
        model.addAttribute("displayTitle", "Create/Edit Rank");

        model.addAttribute("saved", true);
        model.addAttribute("rankBean", wEDT);
        model.addAttribute("roleBean", bc);
        model.addAttribute("ce", IConstants.ON);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("rankBean") Rank pEHB, BindingResult result, SessionStatus status,
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
            model.addAttribute("rankBean", pEHB);
            model.addAttribute("roleBean", bc);
            return VIEW;
        }
        pEHB.setCadre(new Cadre(pEHB.getCadreInstId()));
        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        int wEditType = 1;
        if (!pEHB.isEditMode()) {
            wEditType = 2;
            pEHB.setCreatedBy(new User(bc.getLoginId()));
            pEHB.setBusinessClientId(bc.getBusinessClientInstId());
        }

        this.genericService.saveObject(pEHB);

        return "redirect:editRank.do?bid=" + pEHB.getId() + "&s=" + wEditType;
    }

    private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(HttpServletRequest request, boolean pSaved) {

        PaginationBean paginationBean = this.getPaginationInfo(request);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<Rank> wAllList = null;
        List<Rank> wRetList = null;

        //Do we do the list upside down now?
        wAllList = this.genericService.loadAllObjectsWithSingleCondition(Rank.class, getBusinessClientIdPredicate(request), "name");
        if (pSaved) {
            Collections.sort(wAllList, Comparator.comparing(Rank::getId).reversed());
        }


        if (wAllList.size() > 10) {

            wRetList = (List<Rank>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 20, wAllList);

        } else {
            wRetList = wAllList;
        }

        PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        return wPBO;
    }

    private List<Rank> makeRankList (HttpServletRequest request, boolean pSaved) {


        List<Rank> wAllList = null;
        List<Rank> wRetList = null;

        //Do we do the list upside down now?
        wAllList = this.genericService.loadAllObjectsWithSingleCondition(Rank.class, getBusinessClientIdPredicate(request), "name");
        if (pSaved) {
            Collections.sort(wAllList, Comparator.comparing(Rank::getId).reversed());
        }

        wRetList = wAllList;


        return wRetList;
    }

}
