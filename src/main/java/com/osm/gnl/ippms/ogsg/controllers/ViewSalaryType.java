/*
 * Copyright (c) 2022.
 * This code s proprietary to GNL Systems Ltd. All rights reserved
 */

package com.osm.gnl.ippms.ogsg.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
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
@RequestMapping({"/viewSalaryType.do"})
@SessionAttributes(types={SalaryType.class})
public class ViewSalaryType extends BaseController {

    public ViewSalaryType(){}

  private final String VIEW_NAME = "payment/viewSalaryType";

    @RequestMapping(method = {RequestMethod.GET}, params={"stid"})
    public String view(@RequestParam("stid") Long stid, HttpServletRequest request, Model model) throws EpmAuthenticationException, HttpSessionRequiredException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);
         List<SalaryInfo> salaryInfoList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class, Arrays.asList(
                CustomPredicate.procurePredicate("salaryType.id",stid),
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), null);
        Collections.sort(salaryInfoList, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
        model.addAttribute("salary", salaryInfoList);
        if(IppmsUtils.isNotNullOrEmpty(salaryInfoList)){
            model.addAttribute("salStrName", salaryInfoList.get(0).getSalaryType());
        }else{
            model.addAttribute("salStrName", new SalaryType());
        }

        addRoleBeanToModel(model, request);
      return VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,
                               Model model, HttpServletRequest request) throws Exception {

        BusinessCertificate bc = super.getBusinessCertificate(request);

        SessionManagerService.manageSession(request, model);

        isButtonTypeClick(request, REQUEST_PARAM_CANCEL);

        return "redirect:createSalaryType.do";
    }
}
