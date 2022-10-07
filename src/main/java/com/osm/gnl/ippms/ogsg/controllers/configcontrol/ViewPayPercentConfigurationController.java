/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfig;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfigDetails;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.GlobalPercentConfigValidator;
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
import java.util.*;
@Controller
@RequestMapping({"/viewPayPercentConfig.do"})
@SessionAttributes(types={GlobalPercentConfig.class})
public class ViewPayPercentConfigurationController extends BaseController {
    @Autowired
    private GlobalPercentConfigValidator validator;

    private final String VIEW = "configcontrol/singleGlobalConfigForm";


    public ViewPayPercentConfigurationController(){ }



    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params= {"oid"})
    public String setupForm(@RequestParam("oid") Long pId,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        GlobalPercentConfig wHMB = genericService.loadObjectUsingRestriction(GlobalPercentConfig.class, Arrays.asList(CustomPredicate.procurePredicate("id",pId), getBusinessClientIdPredicate(request)));
        if(wHMB.isNewEntity())
            return "redirect:configureGlobalPercent.do";


        if(!wHMB.isGlobalApply()) {
            //--Get No Of Active Staffs within this range.

            List<GlobalPercentConfigDetails> wDetails = genericService.loadAllObjectsWithSingleCondition(GlobalPercentConfigDetails.class,CustomPredicate.procurePredicate("parentId", wHMB.getId()),null);
            for(GlobalPercentConfigDetails g : wDetails)
                g.setEntryIndex(wHMB.getSalaryTypeId() + ":" + wHMB.getFromLevel() + ":" + wHMB.getToLevel());

            Collections.sort(wDetails, (globalPercentConfigDetails, t1) -> globalPercentConfigDetails.getEntryIndex().compareToIgnoreCase(t1.getEntryIndex()));


        }else {
            wHMB.setGlobalPercentStr(PayrollHRUtils.getDecimalFormat().format(wHMB.getGlobalPercentage()));

        }
        PaginatedPaycheckGarnDedBeanHolder p =  this.makePaginatedList(wHMB,request,false);

        addRoleBeanToModel(model, request);

         model.addAttribute("miniBean", p.getSomeObject());
        model.addAttribute("displayList", p);
        return VIEW;
    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@ModelAttribute("miniBean") GlobalPercentConfig pHMB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate businessCertificate = getBusinessCertificate(request);

        return "redirect:viewGlobalPercentages.do";
    }



    private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(GlobalPercentConfig pHMB ,HttpServletRequest request, boolean errorRec ) {

        BaseController.PaginationBean paginationBean = getPaginationInfo(request);


        List<GlobalPercentConfigDetails> wAllList = pHMB.getConfigDetailsList();
        if(wAllList == null){
            wAllList = new ArrayList<>();
        }else{
            Comparator<GlobalPercentConfigDetails> c = Comparator.comparing(GlobalPercentConfigDetails::getSalaryTypeName).thenComparing(GlobalPercentConfigDetails::getFromLevel).thenComparing(GlobalPercentConfigDetails::getToLevel);
            Collections.sort(wAllList,c);


        }
        //Make a new NamedEntity....

        List<GlobalPercentConfigDetails> wRetList;

        if(wAllList.size() > 10) {

            wRetList = (List<GlobalPercentConfigDetails>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 10, wAllList);

        }else {
            wRetList = wAllList;
        }

        PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());


        wPBO.setSomeObject(pHMB);


        return wPBO;
    }


}
