package com.osm.gnl.ippms.ogsg.controllers.nextofkin;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.NextOfKin;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.RelationshipType;
import com.osm.gnl.ippms.ogsg.validators.hr.NextOfKinValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping({"/editNextOfKin.do"})
@SessionAttributes({"nokBean"})
public class EditNextOfKinFormController extends BaseController {


    @Autowired
    private NextOfKinValidator validator;

    private final String VIEW = "employee/editNextOfKinForm";

    public EditNextOfKinFormController() {
    }

    
    @ModelAttribute("cities")
    public List<City> populateStates() {
        return this.genericService.loadAllObjectsWithoutRestrictions(City.class, "name");
    }


    @ModelAttribute("relationshipTypes")
    public List<RelationshipType> populateRelationshipType() {
        return this.genericService.loadAllObjectsWithoutRestrictions(RelationshipType.class, "name");
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"nokId"})
    public String setupForm(@RequestParam("nokId") Long pNextOfKinId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        NextOfKin wNextOfKin = genericService.loadObjectById(NextOfKin.class, pNextOfKinId);

        if (wNextOfKin.isNewEntity())
            return REDIRECT_TO_DASHBOARD;
        model.addAttribute(DISPLAY_ERRORS, NONE);
        model.addAttribute("roleBean", bc);
        model.addAttribute("nokBean", wNextOfKin);
        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"nokId", "dest"})
    public String setupForm(@RequestParam("nokId") Long pNextOfKinId, @RequestParam("dest") String pDest, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        NextOfKin wNextOfKin = genericService.loadObjectById(NextOfKin.class, pNextOfKinId);

        if (wNextOfKin.isNewEntity())
            return REDIRECT_TO_DASHBOARD;

        wNextOfKin.setDisplayName(pDest);
        model.addAttribute(DISPLAY_ERRORS, NONE);
        model.addAttribute("roleBean", bc);
        model.addAttribute("nokBean", wNextOfKin);
        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"nokId", "s", "dest"})
    public String setupForm(@RequestParam("nokId") Long pNextOfKinId, @RequestParam("s") int pSaved,
                            @RequestParam("dest") String pDest, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        NextOfKin wNextOfKin = genericService.loadObjectById(NextOfKin.class, pNextOfKinId);

        wNextOfKin.setDisplayName(pDest);

        model.addAttribute(IConstants.SAVED_MSG, "Next Of Kin edited successfully.");
        model.addAttribute(DISPLAY_ERRORS, NONE);
        model.addAttribute("saved", true);
        model.addAttribute("roleBean", bc);
        model.addAttribute("nokBean", wNextOfKin);
        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("nokBean") NextOfKin pNextOfKin, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            if (StringUtils.isBlank(pNextOfKin.getDisplayName()) || !pNextOfKin.getDisplayName().equals("enq")) {
                  if(bc.isPensioner())
                      return "redirect:pensionerOverviewForm.do?eid=" + pNextOfKin.getParentId();
                return "redirect:employeeOverviewForm.do?eid=" + pNextOfKin.getParentId();
            } else {
                return "redirect:employeeEnquiryForm.do?eid=" + pNextOfKin.getParentId();

            }


        }
        String pDest = pNextOfKin.getDisplayName();
        validator.validate(pNextOfKin, result);

        if (result.hasErrors()) {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("roleBean", bc);
            model.addAttribute("nokBean", pNextOfKin);
            return VIEW;
        }


        //pNextOfKin.setCreatedBy(bc.getUserName());
        pNextOfKin.setLastModBy(new User(bc.getLoginId()));
        pNextOfKin.setLastModTs(Timestamp.from(Instant.now()));

        Long pId = this.genericService.storeObject(pNextOfKin);

        if (pDest == null) {
            pDest = "";
        }


        return "redirect:editNextOfKin.do?nokId=" + pId + "&s=1&dest=" + pDest;
    }


}
