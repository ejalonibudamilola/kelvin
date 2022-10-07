package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.AddNewCityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({"/viewAllCities.do"})
@SessionAttributes(types={City.class})
public class ViewCityFormController extends  BaseController {

    @Autowired
    AddNewCityValidator addNewCityValidator;

    private final int pageLength = 20;

    private final String VIEW_NAME = "configcontrol/viewAllCityForm";

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

        City wEDT = new City();
        model.addAttribute("cityBean", wEDT);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method={RequestMethod.GET}, params={"cid"})
    public String editForm(@RequestParam("cid") Long cId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        return "redirect:editCity.do?cId="+cId;
    }

    @RequestMapping(method={RequestMethod.GET},params={"stId"})
    public String setupForm(@RequestParam("stId") Long stId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        BaseController.PaginationBean paginationBean = getPaginationInfo(request);

        PredicateBuilder predicateBuilder = new PredicateBuilder();

        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("state.id", stId));

        City wEDT = new City();
        wEDT.setShowOverride(true);
        wEDT.setStateId(stId);


//        List<City> wCity = this.genericService.loadPaginatedObjects(City.class, Arrays.asList(
//                CustomPredicate.procurePredicate("state.id", stId)),(paginationBean.getPageNumber() - 1) * this.pageLength,
//                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        List<City> wCity = this.genericService.loadAllObjectsWithSingleCondition(City.class, CustomPredicate.procurePredicate("state.id", stId), "name");

        Collections.sort(wCity, Comparator.comparing(City::getName).thenComparing(City::getName));


        int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, City.class);

        PaginatedBean wPELB = new PaginatedBean(wCity, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements,
                paginationBean.getSortCriterion(), paginationBean.getSortOrder());


        model.addAttribute("cityBean", wEDT);
        model.addAttribute("cityList", wPELB);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }


    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_update", required=false) String update,
                                @RequestParam(value="_add", required=false) String add,
                                @ModelAttribute("cityBean") City pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
        {
            return CONFIG_HOME_URL;
        }
        else if(isButtonTypeClick(request, REQUEST_PARAM_ADD)){
            return "redirect:addNewCity.do";
        }

        addNewCityValidator.validateUpdate(pEHB, result);
        if (result.hasErrors())
        {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("cityBean", pEHB);
            addRoleBeanToModel(model, request);
            return VIEW_NAME;
        }

        if(isButtonTypeClick(request, REQUEST_PARAM_UPDATE))
        {
            return "redirect:viewAllCities.do?stId="+pEHB.getStateId();
        }

        return "redirect:viewAllCities.do";
    }
}
