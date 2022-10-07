package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.CreateNewLGAValidator;
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
@RequestMapping({"/createLga.do"})
@SessionAttributes(types={LGAInfo.class})
public class CreateLgaFormController extends BaseController {

    @Autowired
    CreateNewLGAValidator createNewLGAValidator;

    private final String VIEW_NAME = "configcontrol/createLGAForm";

    @ModelAttribute("stateList")
    public List<State> generateStateList(){

        List<State> wStateList = this.genericService.loadAllObjectsWithoutRestrictions(State.class, "name");
        Collections.sort(wStateList);
        return wStateList;
    }


    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);



        LGAInfo wEDT = new LGAInfo();
        model.addAttribute("lgaBean", wEDT);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method={RequestMethod.GET},params={"lgId","s"})
    public String setupForm(@RequestParam("lgId") Long lgaId,@RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        LGAInfo wEDT = this.genericService.loadObjectWithSingleCondition(LGAInfo.class, CustomPredicate.procurePredicate("id", lgaId));

        model.addAttribute(IConstants.SAVED_MSG, ""+wEDT.getName()+" created successfully.");
//        wEDT.setName(IConstants.EMPTY_STR);
//        wEDT.setName(wEDT.getName());
//        wEDT.setRecordCode(IConstants.EMPTY_STR);
//        wEDT.setRecordCode(wEDT.getRecordCode());
        wEDT.setStateId(wEDT.getState().getId());
        model.addAttribute("saved", true);
        model.addAttribute("lgaBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }


    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @ModelAttribute("lgaBean") LGAInfo pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
        {
            return CONFIG_HOME_URL;
        }

        createNewLGAValidator.validate(pEHB, result);
        if (result.hasErrors())
        {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("lgaBean", pEHB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }
        pEHB.setState(new State(pEHB.getStateId()));
        pEHB.setCreatedBy(new User(bc.getLoginId()));
        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        genericService.saveObject(pEHB);

        return "redirect:createLga.do?lgId="+pEHB.getId()+"&s=1";
    }
}
