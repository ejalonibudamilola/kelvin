/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfig;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({"/viewGlobalPercentages.do"})
public class ViewConfiguredPayPercentController extends BaseController {

    private final String VIEW_NAME = "configcontrol/allGlobalPercentForm";

    public ViewConfiguredPayPercentController(){ }


    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<GlobalPercentConfig> empList =  this.genericService.loadAllObjectsWithSingleCondition(GlobalPercentConfig.class,getBusinessClientIdPredicate(request),null);

        Collections.sort(empList, Comparator.comparing(GlobalPercentConfig::getEndDate).reversed());

        GlobalPercentConfig globalPercentConfig = IppmsUtilsExt.loadActiveGlobalPercentConfigByClient(genericService,bc);


        addPageTitle(model, "Configured Payment Percentages");
        addMainHeader(model, "Configured Payment Percentages");
        model.addAttribute("roleBean", bc);
        model.addAttribute("canCreate",globalPercentConfig.isNewEntity());
        model.addAttribute("miniBean", empList);

        return VIEW_NAME;
    }


    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(  Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);


            return CONFIG_HOME_URL;

    }
}
