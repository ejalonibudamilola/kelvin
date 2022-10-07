/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
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
import java.util.List;


@Controller
@RequestMapping("/searchForApportionment.do")
@SessionAttributes(types = HrMiniBean.class)
public class SearchPensionerForApportionment extends BaseSearchController {

    private final String VIEW = "search/searchEmployeeForEditForm";

    @Autowired
    private SearchValidator searchValidator;

    public SearchPensionerForApportionment() {
    }

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request);
        HrMiniBean empHrBean = new HrMiniBean();

        empHrBean.setDisplayTitle("Search for Pensioner(s) to Put on Apportionment");
        addRoleBeanToModel(model, request);
        model.addAttribute("empMiniBean", empHrBean);
        return VIEW;

    }


    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel, @ModelAttribute("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            return "redirect:determineDashBoard.do";
        }

        searchValidator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("empMiniBean", pEHB);
            return VIEW;
        }

        List<AbstractEmployeeEntity> emp = this.doSearch(pEHB,bc);
        //Now do a search with the supplied information.

        if (emp.size() < 1) {

            result.rejectValue("", "search.no_values", "No Pensioner(s) found! Please retry.");

            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("empMiniBean", pEHB);
            return VIEW;

        }

        if (emp.size() == 1) {
            //first get the employee.

            //status.setComplete();
            HiringInfo wHI = this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate("pensioner.id", emp.get(0).getId()));
            if (null == wHI || wHI.isNewEntity()) {
                result.rejectValue("", "search.no_values", "Pensioner " + emp.get(0).getDisplayName() + " has no hiring information data. Apportionment Denied.");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("empMiniBean", pEHB);
                return VIEW;
            }
            return "redirect:apportionPensioner.do?hid=" + wHI.getId();
        } else {
            return "redirect:viewMultiEmployeeResults.do?eid="+ StringUtils.trimToEmpty(pEHB.getEmployeeId())+"&fn=" + IppmsUtils.treatNull(pEHB.getFirstName()) + "&ln=" + IppmsUtils.treatNull(pEHB.getLastName()) + "&noe=" + emp.size() + "&cn=16";
        }


    }

}
