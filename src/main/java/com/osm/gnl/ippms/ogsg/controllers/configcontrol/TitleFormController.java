/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.TitleValidator;
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
@RequestMapping({"/editTitle.do"})
@SessionAttributes({"titleBean"})
public class TitleFormController extends BaseController {

    @Autowired
    private TitleValidator validator;

    private final String VIEW = "configcontrol/createEditTitleForm";

  

    public TitleFormController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Title wEDT = new Title();
      

        model.addAttribute("displayList", this.makeTitleList(request, false,bc));
        model.addAttribute("displayTitle", "Create New Title");
        model.addAttribute("titleBean", wEDT);
        model.addAttribute("roleBean", bc);
        model.addAttribute("ce", IConstants.OFF);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"cid"})
    public String setupForm(@RequestParam("cid") Long pEduId, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Title wEDT = this.genericService.loadObjectById(Title.class,pEduId);


        wEDT.setEditMode(true);


        model.addAttribute("displayList", this.makeTitleList(request, false,bc));
        model.addAttribute("displayTitle", "Edit Title");
        model.addAttribute("titleBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"bid", "s"})
    public String setupForm(@RequestParam("bid") Long pObjectId, @RequestParam("s") int pSaved, Model model,
                            HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Title wEDT = this.genericService.loadObjectById(Title.class,pObjectId);


        String wMessage = "";

        if (pSaved == 1) {
            wMessage = " Title " + wEDT.getName() + " edited successfully.";
        } else {
            wMessage = " Title " + wEDT.getName() + " created successfully.";
        }

        model.addAttribute(IConstants.SAVED_MSG, wMessage);
        wEDT = new Title();

        model.addAttribute("displayList", this.makeTitleList(request, true,bc));
        model.addAttribute("displayTitle", "Create/Edit Title");
        model.addAttribute("saved", true);
        model.addAttribute("titleBean", wEDT);
        model.addAttribute("roleBean", bc);
        model.addAttribute("ce", IConstants.ON);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("titleBean") Title pEHB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }

        validator.validate(pEHB, result);
        if (result.hasErrors()) {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("titleBean", pEHB);
            model.addAttribute("roleBean", bc);
            return VIEW;
        }


        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        int wEditType = 1;
        if (!pEHB.isEditMode()) {
            wEditType = 2;
            pEHB.setCreatedBy(new User(bc.getLoginId()));
        }

        this.genericService.saveObject(pEHB);

        return "redirect:editTitle.do?bid=" + pEHB.getId() + "&s=" + wEditType;
    }


    private List<Title> makeTitleList (HttpServletRequest request, boolean pSaved, BusinessCertificate bc) {


        List<Title> wAllList = null;


        //Do we do the list upside down now?
        wAllList = this.genericService.loadAllObjectsWithoutRestrictions(Title.class,  "name");
        for(Title t : wAllList)
            t.setModifierOrg(bc.getOrgName(t.getLastModBy().getRole().getBusinessClient().getId()));

        if (pSaved) {
            Collections.sort(wAllList, Comparator.comparing(Title::getId).reversed());
        }

        return wAllList;
    }

}
