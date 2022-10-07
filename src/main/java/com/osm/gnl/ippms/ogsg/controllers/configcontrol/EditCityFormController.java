package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.AddNewCityValidator;
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
import java.util.List;

@Controller
@RequestMapping({"/editCity.do"})
@SessionAttributes(types={City.class})
public class EditCityFormController extends BaseController {

    @Autowired
    AddNewCityValidator addNewCityValidator;

    private final String VIEW_NAME = "configcontrol/editCityForm";

    @ModelAttribute("stateList")
    public List<State> generateStateList(){

        List<State> wStateList = this.genericService.loadAllObjectsWithoutRestrictions(State.class, "name");
        Collections.sort(wStateList);
        return wStateList;
    }


    @RequestMapping(method={RequestMethod.GET},params={"cId"})
    public String setupForm(@RequestParam("cId") Long cId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        City wEDT = this.genericService.loadObjectWithSingleCondition(City.class, CustomPredicate.procurePredicate("id", cId));
        wEDT.setStateId(wEDT.getState().getId());
        model.addAttribute("cityBean", wEDT);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method={RequestMethod.GET},params={"cId","s"})
    public String setupForm(@RequestParam("cId") Long cId, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        City wEDT = this.genericService.loadObjectWithSingleCondition(City.class, CustomPredicate.procurePredicate("id", cId));

        model.addAttribute(IConstants.SAVED_MSG, ""+wEDT.getName()+" edited successfully.");
        wEDT.setStateId(wEDT.getState().getId());
        model.addAttribute("saved", true);
        model.addAttribute("cityBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }


    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @ModelAttribute("cityBean") City pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
        {
            return "redirect:viewAllCities.do";
        }

        addNewCityValidator.validate(pEHB, result);
        if (result.hasErrors())
        {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("cityBean", pEHB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }
        pEHB.setCreatedBy(new User(bc.getLoginId()));
        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        genericService.saveObject(pEHB);

        return "redirect:editCity.do?cId="+pEHB.getId()+"&s=1";
    }
}
