package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.search.SearchValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/searchEmpForPromotion.do"})
@SessionAttributes(types = {HrMiniBean.class})
public class SearchForPromotionController extends BaseSearchController {


    private final SearchValidator validator;
    private final String VIEW_NAME = "search/searchEmpForPromotionForm";

    @Autowired
    public SearchForPromotionController(SearchValidator validator) {
        this.validator = validator;
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        HrMiniBean empHrBean = new HrMiniBean();

        empHrBean.setDisplayTitle("Search for employee to Promote");
        empHrBean.setActiveInd(ON);
        model.addAttribute("roleBean", bc);
        model.addAttribute("empMiniBean", empHrBean);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("empMiniBean") HrMiniBean pEHB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }

        validator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);

            model.addAttribute("status", result);
            model.addAttribute("empMiniBean", pEHB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }


        List<AbstractEmployeeEntity> emp = doSearch(pEHB, bc);

        if (emp.size() < 1) {
            result.rejectValue("", "search.no_values", "No Employee(s) found! Please retry.");

            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("empMiniBean", pEHB);
            return VIEW_NAME;
        }

        if (emp.size() == 1) {
            Employee e = (Employee) emp.get(0);
            //Now Test whether Employee is Suspended or Terminated....
            if (e.isTerminated()) {
                result.rejectValue("", "search.no_values", e.getDisplayName() + " is currently Terminated. Promotion Denied.");

                addDisplayErrorsToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("empMiniBean", pEHB);

                model.addAttribute("roleBean", bc);
                return VIEW_NAME;
            }
            HiringInfo wHI = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate("employee.id", e.getId()), getBusinessClientIdPredicate(request)));

            if (wHI == null || wHI.isNewEntity()) {
                result.rejectValue("", "search.no_values", e.getDisplayName() + " has no Hiring Information. Promotion Denied.");

                addDisplayErrorsToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("empMiniBean", pEHB);

                model.addAttribute("roleBean", bc);
                return VIEW_NAME;
            } else if (wHI.isSuspendedEmployee()) {
                result.rejectValue("", "search.no_values", e.getDisplayName() + " is currently Suspended. Promotion Denied.");

                addDisplayErrorsToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("empMiniBean", pEHB);

                model.addAttribute("roleBean", bc);
                return VIEW_NAME;
            }
            return "redirect:promoteEmployee.do?eid=" + e.getId();
        } else {
            return "redirect:viewMultiEmployeeResults.do?eid=" + StringUtils.trimToEmpty(pEHB.getEmployeeId()) + "&fn=" + pEHB.getFirstName() + "&ln=" + pEHB.getLastName() + "&noe=" + emp.size() + "&cn=5";

        }

    }
}