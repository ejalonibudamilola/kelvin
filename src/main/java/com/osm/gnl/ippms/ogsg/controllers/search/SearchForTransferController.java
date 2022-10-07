package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/searchEmpForTransfer.do"})
@SessionAttributes(types = {HrMiniBean.class})
public class SearchForTransferController extends BaseSearchController {

    @Autowired
    private SearchValidator searchValidator;

    private final String VIEW_NAME = "search/searchEmpForPromotionForm";

    public SearchForTransferController() {
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        HrMiniBean empHrBean = new HrMiniBean();
        empHrBean.setDisplayTitle("Search for "+bc.getStaffTypeName()+" to Transfer");
        empHrBean.setActiveInd(ON);
        return this.makeAndReturnView(null, empHrBean, model, request);

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }

        searchValidator.validate(pEHB, result);
        if (result.hasErrors()) {
            return this.makeAndReturnView(result, pEHB, model, request);
        }

        List<AbstractEmployeeEntity> emp = doSearch(pEHB, bc);

        if (emp.size() < 1) {
            result.rejectValue("", "search.no_values", "No " + bc.getStaffTypeName() + "(s) found! Please retry.");
            return this.makeAndReturnView(result, pEHB, model, request);
        }

        if (emp.size() == 1) {
            AbstractEmployeeEntity e = emp.get(0);
            if (e.isTerminated()) {
                result.rejectValue("", "search.no_values", e.getDisplayName() + " is currently Terminated. Transfer Denied.");
                return this.makeAndReturnView(result, pEHB, model, request);
            }
            HiringInfo wHI = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), e.getId()), getBusinessClientIdPredicate(request)));

            if (wHI == null || wHI.isNewEntity()) {
                result.rejectValue("", "search.no_values", e.getDisplayName() + " has no Hiring Information. Transfer Denied.");
                return this.makeAndReturnView(result, pEHB, model, request);

            } else if (wHI.isSuspendedEmployee()) {
                result.rejectValue("", "search.no_values", e.getDisplayName() + " is currently Suspended. Transfer Denied.");
                return this.makeAndReturnView(result, pEHB, model, request);
            }
            return "redirect:transferEmployee.do?eid=" + e.getId();
        }

        return "redirect:viewMultiEmployeeResults.do?eid="+ StringUtils.trimToEmpty(pEHB.getEmployeeId())+"&fn=" + pEHB.getFirstName() + "&ln=" + pEHB.getLastName() + "&noe=" + emp.size() + "&cn=6";
    }

    private String makeAndReturnView(BindingResult result, HrMiniBean pEHB, Model model, HttpServletRequest request) {

        if (result != null) {
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
        }
        addRoleBeanToModel(model, request);
        model.addAttribute("empMiniBean", pEHB);
        return VIEW_NAME;
    }
}