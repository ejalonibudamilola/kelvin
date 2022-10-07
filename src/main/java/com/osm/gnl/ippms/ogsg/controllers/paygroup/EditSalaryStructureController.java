package com.osm.gnl.ippms.ogsg.controllers.paygroup;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({"/editSalaryStructure.do"})
@SessionAttributes({"miniBean"})
public class EditSalaryStructureController extends BaseController {

    private final String VIEW_NAME = "paygroup/salaryStructureDetailsForm";


    public EditSalaryStructureController() {}

    @RequestMapping(method = {RequestMethod.GET}, params = {"sid"})
    public String setupForm(@RequestParam("sid") Long pSalId, Model model, HttpServletRequest request)
            throws Exception {

        SessionManagerService.manageSession(request, model);

        SalaryType wEDT = this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(CustomPredicate.procurePredicate("id", pSalId),
                getBusinessClientIdPredicate(request)));

        List<SalaryInfo> salaryInfoList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, CustomPredicate.procurePredicate("salaryType.id", wEDT.getId()),null);
        Collections.sort(salaryInfoList, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
        model.addAttribute("displayList", salaryInfoList);
        model = this.addViewHeaders(model, "View", wEDT);
        model.addAttribute("miniBean", wEDT);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }


    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") SalaryType pEHB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        return "redirect:createSalaryType.do?bid=" + pEHB.getId();
    }


    private Model addViewHeaders(Model model, String action, SalaryType salaryType) {
        addPageTitle(model, action + " Edit Salary Structure");
        addMainHeader(model, action + " Salary Structure Details");
        addTableHeader(model, salaryType.getName()+ " Details");
        return model;
    }





}
