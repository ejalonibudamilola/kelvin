package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.CreateNewLGAValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({"/viewAllLga.do"})
@SessionAttributes(types={LGAInfo.class})
public class ViewLgaFormController extends BaseController {

    @Autowired
    CreateNewLGAValidator createNewLGAValidator;

    private final int pageLength = 20;

    private final String VIEW_NAME = "configcontrol/viewAllLgaForm";

    @ModelAttribute("stateList")
    public List<State> generateStateList(){

        List<State> wStateList = this.genericService.loadAllObjectsWithoutRestrictions(State.class, "name");
        Collections.sort(wStateList);
        return wStateList;
    }


    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        LGAInfo wEDT = new LGAInfo();
        model.addAttribute("lgaBean", wEDT);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method={RequestMethod.GET}, params={"lid"})
    public String editForm(@RequestParam("lid") Long lId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        return "redirect:editLga.do?lId="+lId;
    }

    @RequestMapping(method={RequestMethod.GET},params={"stId"})
    public String setupForm(@RequestParam("stId") Long stId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        PaginationBean paginationBean = getPaginationInfo(request);

        PredicateBuilder predicateBuilder = new PredicateBuilder();

        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("state.id", stId));

        LGAInfo wEDT = new LGAInfo();
        wEDT.setShowOverride(true);
        wEDT.setStateId(stId);

//        List<LGAInfo> wLga = this.genericService.loadPaginatedObjects(LGAInfo.class,
//                predicateBuilder.getPredicates(),(paginationBean.getPageNumber() - 1) * this.pageLength,
//                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        List<LGAInfo> wLga = this.genericService.loadAllObjectsUsingRestrictions(LGAInfo.class, predicateBuilder.getPredicates(),"name");

        Collections.sort(wLga, Comparator.comparing(LGAInfo::getName).thenComparing(LGAInfo::getName));

        int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, LGAInfo.class);

        PaginatedBean wPELB = new PaginatedBean(wLga, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements,
                paginationBean.getSortCriterion(), paginationBean.getSortOrder());


        model.addAttribute("lgaBean", wEDT);
        model.addAttribute("lgaList", wPELB);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }


    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_update", required=false) String update,
                                @RequestParam(value="_add", required=false) String add,
                                @ModelAttribute("lgaBean") LGAInfo pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
        {
            return CONFIG_HOME_URL;
        }
        else if(isButtonTypeClick(request, REQUEST_PARAM_ADD)){
            return "redirect:createLga.do";
        }

        createNewLGAValidator.validateUpdate(pEHB, result);
        if (result.hasErrors())
        {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("lgaBean", pEHB);
            addRoleBeanToModel(model, request);
            return VIEW_NAME;
        }

        if(isButtonTypeClick(request, REQUEST_PARAM_UPDATE))
        {
            return "redirect:viewAllLga.do?stId="+pEHB.getStateId();
        }

        return "redirect:viewAllLga.do";
    }
}
