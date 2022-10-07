/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfig;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfigDetails;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Controller
@RequestMapping({"/deactivateGlobalPercentage.do"})
@SessionAttributes(types={GlobalPercentConfig.class})
public class DeactivateGlobalPercentageController extends BaseController{

    private final String VIEW = "approval/globalPercentApprovalForm";

    public DeactivateGlobalPercentageController(){ }


    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        GlobalPercentConfig wHMB = this.genericService.loadObjectUsingRestriction(GlobalPercentConfig.class,
                Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("payrollStatus",0)));

        if(wHMB.isNewEntity()){
            wHMB.setShowErrorRow(true);
            wHMB.setMode("There are no Configured Percentage payment found for Deactivation.");
        }else{
            if(wHMB.isGlobalApply())
                wHMB.setGlobalPercentStr(PayrollHRUtils.getDecimalFormat().format(wHMB.getGlobalPercentage())+"%");
            else
            {
                wHMB.setConfigDetailsList(this.genericService.loadAllObjectsWithSingleCondition(GlobalPercentConfigDetails.class,CustomPredicate.procurePredicate("parentId", wHMB.getId()),null));
                Collections.sort(wHMB.getConfigDetailsList(), Comparator.comparing(GlobalPercentConfigDetails::getSalaryTypeName).thenComparing(GlobalPercentConfigDetails::getFromLevel).thenComparing(GlobalPercentConfigDetails::getToLevel));
            }
        }
        PaginatedPaycheckGarnDedBeanHolder p =  this.makePaginatedList(wHMB,request,false);
        addRoleBeanToModel(model, request);
        addPageTitle(model,"Global Percentage Deactivation Form");
        addMainHeader(model,"Deactivate Configured Percentage Payment");
        model.addAttribute("miniBean", p.getSomeObject());
        model.addAttribute("displayList", p);
        return VIEW;
    }

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params= {"oid","act"})
    public String setupForm(@RequestParam("oid") Long pId, @RequestParam("act") int pSaved, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        GlobalPercentConfig wHMB = genericService.loadObjectUsingRestriction(GlobalPercentConfig.class, Arrays.asList(CustomPredicate.procurePredicate("id",pId), getBusinessClientIdPredicate(request)));
        if(wHMB.isNewEntity())
            return "redirect:configureGlobalPercent.do";


        if(!wHMB.isGlobalApply()) {

            List<GlobalPercentConfigDetails> wDetails = genericService.loadAllObjectsWithSingleCondition(GlobalPercentConfigDetails.class,CustomPredicate.procurePredicate("parentId", wHMB.getId()),null);
            for(GlobalPercentConfigDetails g : wDetails)
                g.setEntryIndex(wHMB.getSalaryTypeId() + ":" + wHMB.getFromLevel() + ":" + wHMB.getToLevel());

            Collections.sort(wDetails, (globalPercentConfigDetails, t1) -> globalPercentConfigDetails.getEntryIndex().compareToIgnoreCase(t1.getEntryIndex()));


        }else {
            wHMB.setGlobalPercentStr(PayrollHRUtils.getDecimalFormat().format(wHMB.getGlobalPercentage()));

        }
        PaginatedPaycheckGarnDedBeanHolder p =  this.makePaginatedList(wHMB,request,false);

        addRoleBeanToModel(model, request);
        addPageTitle(model,"Global Percentage Deactivation Form");
        addMainHeader(model,"Deactivate Configured Percentage Payment");
        addSaveMsgToModel(request,model,"Percentage Payment Configuration Deactivated Successfully");
        model.addAttribute("miniBean", p.getSomeObject());
        model.addAttribute("displayList", p);
        return VIEW;
    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@ModelAttribute("miniBean") GlobalPercentConfig pHMB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate businessCertificate = getBusinessCertificate(request);


        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }


        if(isButtonTypeClick(request,REQUEST_PARAM_DEACTIVATE)){
            pHMB.setDeactivation(true);
            model = makeModel(pHMB,request,model,false);
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            result.rejectValue("confirmation","Confirm.Value","Please confirm Deactivation of this Percentage Payment Configuration.");
            if(!pHMB.isConfirmation())
                pHMB.setConfirmation(true);
            pHMB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
            return VIEW;
        }
        if(isButtonTypeClick(request,REQUEST_PARAM_CONFIRM)){
            pHMB.setDeactivateInd(ON);
            pHMB.setDeactivatedBy(new User(businessCertificate.getLoginId()));
            pHMB.setDeactivationDate(Timestamp.from(Instant.now()));
            return this.saveRecords(pHMB);

        }

        return "redirect:deactivateGlobalPercentage.do";
    }

    private String saveRecords(GlobalPercentConfig pHMB) {

        Long id = this.genericService.storeObject(pHMB);

        return "redirect:deactivateGlobalPercentage.do?oid="+id+"&act=1";
    }


    private Model makeModel(GlobalPercentConfig pHMB, HttpServletRequest request,Model model, boolean error) {
        PaginatedPaycheckGarnDedBeanHolder p =  this.makePaginatedList(pHMB, request, error);

        model.addAttribute(DISPLAY_ERRORS, BLOCK);
        // model.addAttribute("status", result);
        addPageTitle(model,"Global Percentage Deactivation Form");
        addMainHeader(model,"Deactivate Configured Percentage Payment");
        model.addAttribute("miniBean", p.getSomeObject());
        model.addAttribute("displayList", p);
        addRoleBeanToModel(model, request);
        if(!pHMB.isGlobalApply()) {
            List<SalaryInfo> wSalaryInfoList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, CustomPredicate.procurePredicate("salaryType.id", pHMB.getSalaryTypeId()),null);
            Comparator<SalaryInfo> c = Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep);

            Collections.sort(wSalaryInfoList,c);
            pHMB.setFromLevelList(wSalaryInfoList);
            pHMB.setToLevelList(wSalaryInfoList);
        }
        return model;
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
