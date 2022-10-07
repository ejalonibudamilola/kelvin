/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionType;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.SuspensionTypeValidator;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({"/editSuspensionType.do"})
@SessionAttributes({"miniBean"})
public class AddSuspensionType extends BaseController{


        @Autowired
        private SuspensionTypeValidator validator;

        private final String VIEW = "configcontrol/suspensionTypeForm";

       
    public AddSuspensionType() {
    }

        @RequestMapping(method = {RequestMethod.GET})
        public String setupForm(Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        SuspensionType wEDT = new SuspensionType();
        
//        model.addAttribute("displayList", this.makePaginatedList(request, false));
        model.addAttribute("displayList", this.makeSuspensionList(request, false));
        model.addAttribute("listSize", (this.makeSuspensionList(request, false)).size());
        model.addAttribute("displayTitle", "Create New Suspension Type");
        model.addAttribute("miniBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

        @RequestMapping(method = {RequestMethod.GET}, params = {"cid"})
        public String setupForm(@RequestParam("cid") Long pEduId, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

            SuspensionType wEDT = this.genericService.loadObjectWithSingleCondition(SuspensionType.class, CustomPredicate.procurePredicate("id", pEduId));


        if (!wEDT.getBusinessClientId().equals(bc.getBusinessClientInstId()))
            return "redirect:editSuspensionType.do";

        wEDT.setEditMode(true);

//        model.addAttribute("displayList", this.makePaginatedList(request, false));
        model.addAttribute("displayList", this.makeSuspensionList(request, false));
        model.addAttribute("listSize", (this.makeSuspensionList(request, false)).size());
        model.addAttribute("displayTitle", "Edit Suspension Type");
        model.addAttribute("miniBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }


        @RequestMapping(method = {RequestMethod.GET}, params = {"bid", "s"})
        public String setupForm(@RequestParam("bid") Long pObjectId, @RequestParam("s") int pSaved, Model model,
            HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        SuspensionType wEDT = this.genericService.loadObjectWithSingleCondition(SuspensionType.class,
                CustomPredicate.procurePredicate("id", pObjectId));

            if (!wEDT.getBusinessClientId().equals(bc.getBusinessClientInstId()))
                return "redirect:editSuspensionType.do";


            String wMessage;

        if (pSaved == 1) {
            wMessage = " Suspension Type '" + wEDT.getName() + "' edited successfully.";
        } else {
            wMessage = " Suspension Type '" + wEDT.getName() + "' created successfully.";
        }

        model.addAttribute(IConstants.SAVED_MSG, wMessage);
        wEDT = new SuspensionType();

//        model.addAttribute("displayList", this.makePaginatedList(request, true));
        model.addAttribute("displayList", this.makeSuspensionList(request, true));
        model.addAttribute("listSize", (this.makeSuspensionList(request, true)).size());
        model.addAttribute("displayTitle", "Create/Edit Suspension Type");
        model.addAttribute("saved", true);
        model.addAttribute("miniBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

        @RequestMapping(method = {RequestMethod.POST})
        public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
            @ModelAttribute("miniBean") SuspensionType pEHB, BindingResult result, SessionStatus status,
            org.springframework.ui.Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }

        validator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            model.addAttribute("roleBean", bc);
            return VIEW;
        }

        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        int wEditType = 1;
        if (!pEHB.isEditMode()) {
            wEditType = 2;
            pEHB.setCreatedBy(new User(bc.getLoginId()));
            pEHB.setBusinessClientId(bc.getBusinessClientInstId());
        }

        this.genericService.saveObject(pEHB);

        return "redirect:editSuspensionType.do?bid=" + pEHB.getId() + "&s=" + wEditType;
    }

//        private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(HttpServletRequest request, boolean pSaved) {
//
//        BaseController.PaginationBean paginationBean = this.getPaginationInfo(request);
//        BusinessCertificate bc = this.getBusinessCertificate(request);
//
//        List<SuspensionType> wAllList = null;
//        List<SuspensionType> wRetList = null;
//
//        //Do we do the list upside down now?
//        wAllList = this.genericService.loadAllObjectsWithSingleCondition(SuspensionType.class, getBusinessClientPredicate(request), "name");
//        if (pSaved) {
//            Collections.sort(wAllList, Comparator.comparing(SuspensionType::getId).reversed());
//        }
//
//
//        if (wAllList.size() > 5) {
//
//            wRetList = (List<SuspensionType>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 20, wAllList, SuspensionType.class);
//
//        } else {
//            wRetList = wAllList;
//        }
//
//        PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());
//
//        return wPBO;
//    }

    private List<SuspensionType> makeSuspensionList(HttpServletRequest request, boolean pSaved){
        List<SuspensionType> wAllList;
        List<SuspensionType> wRetList;

        //Do we do the list upside down now?
        wAllList = this.genericService.loadAllObjectsWithSingleCondition(SuspensionType.class, getBusinessClientIdPredicate(request), "name");
        if (pSaved) {
            Collections.sort(wAllList, Comparator.comparing(SuspensionType::getId).reversed());
        }
        wRetList = wAllList;
        return wRetList;
    }


}
