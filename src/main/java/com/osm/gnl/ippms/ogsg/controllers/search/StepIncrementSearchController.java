/*
 * Copyright (c) 2021. 
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
import java.util.List;

@Controller
@RequestMapping({"/searchEmpStepIncrement.do"})
@SessionAttributes(types={HrMiniBean.class})
public class StepIncrementSearchController extends BaseSearchController{
    @Autowired
    private SearchValidator searchValidator;

    private final String VIEW = "search/searchEmpForPromotionForm";

    public StepIncrementSearchController()
    {
    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        HrMiniBean empHrBean = new HrMiniBean();

        empHrBean.setDisplayTitle("Search for "+bc.getStaffTypeName()+" for Step Increment");
        return makeModelAndReturnView(model,null,empHrBean,bc,request,false);

    }

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("empMiniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }

        searchValidator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            return makeModelAndReturnView(model,result,pEHB,bc,request,false);
        }

        List<AbstractEmployeeEntity> emp = doSearch(pEHB,bc);

        if (emp.size() < 1) {
            result.rejectValue("", "search.no_values", "No "+bc.getStaffTypeName()+"(s) found! Please retry.");

            return makeModelAndReturnView(model,result,pEHB,bc,request,false);
        }

        if (emp.size() == 1)
        {

            HiringInfo wHireInfo = genericService.loadObjectWithSingleCondition(HiringInfo.class,CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), emp.get(0).getId()));

            if(wHireInfo.isNewEntity()) {
                result.rejectValue("", "search.no_values", bc.getStaffTypeName()+" does not have Hiring Information. Please create hiring information.");
                if(bc.isPensioner())
                    result.rejectValue("", "search.no_values", "<a href=/ogsg_ippms/penHireInfo.do?oid="+emp.get(0).getId()+" <i>Create Hiring Information</i></a> ");
                else
                    result.rejectValue("", "search.no_values", "<a href=/ogsg_ippms/hiringForm.do?oid="+emp.get(0).getId()+" <i>Create Hiring Information</i></a> ");
                pEHB.setEmployeeInstId(emp.get(0).getId());
                return makeModelAndReturnView(model,result,pEHB,bc,request,true);
            }

            return "redirect:stepIncrement.do?eid=" + emp.get(0).getId();
        }
        return "redirect:viewMultiEmployeeResults.do?eid="+ StringUtils.trimToEmpty(pEHB.getEmployeeId())+"&fn=" + StringUtils.trimToEmpty(pEHB.getFirstName()) + "&ln=" + StringUtils.trimToEmpty(pEHB.getLastName()) + "&noe=" + emp.size()+"&cn=18";
    }

    private String makeModelAndReturnView(Model model, BindingResult result, HrMiniBean pEHB,BusinessCertificate bc, HttpServletRequest request, boolean addNoHireInfo){
        if(null != result){
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
            if(addNoHireInfo)
                model.addAttribute("no_hire_info",true);
        }
        addRoleBeanToModel(model,request);
        pEHB.setActiveInd(ON);
        model.addAttribute("empMiniBean", pEHB);

        return VIEW;
    }
}
