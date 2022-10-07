/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionCategory;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.domain.payment.RangedDeduction;
import com.osm.gnl.ippms.ogsg.domain.payment.RangedDeductionDetails;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.validators.deduction.RangedDeductionValidator;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Controller
@RequestMapping({"/addRangedDeduction.do"})
@SessionAttributes(types = {RangedDeduction.class})
public class AddRangedDeductionController extends BaseController {


    @Autowired
    private RangedDeductionValidator validator;

    private final String VIEW = "configcontrol/deductionRangeForm";

    public AddRangedDeductionController() {
    }

 

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        RangedDeduction wHMB = new RangedDeduction();
        wHMB.setFirstTimePay(true);
       
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wHMB);
        model.addAttribute("action", "");
        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"iid"})
    public String setupForm(@RequestParam("iid") String pObjId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);


        // HrMiniBean wHMB = (HrMiniBean) super.getSessionAttribute(request, PS_BEAN);
        RangedDeduction wHMB = (RangedDeduction) model.getAttribute(PS_BEAN);
        if (wHMB == null)
            return "redirect:addRangedDeduction.do";

        if (pObjId.equalsIgnoreCase(REQUEST_PARAM_ADD)) {
             
                //--Get No Of Active Staffs within this range.

            RangedDeductionDetails rangedDeductionDetails = new RangedDeductionDetails();
            rangedDeductionDetails.setAmount(Double.parseDouble(PayrollHRUtils.removeCommas(wHMB.getAmountStr())));
            rangedDeductionDetails.setLowerBound(Double.parseDouble(PayrollHRUtils.removeCommas(wHMB.getLowerBoundValue())));
            rangedDeductionDetails.setUpperBound(Double.parseDouble(PayrollHRUtils.removeCommas(wHMB.getUpperBoundValue())));
             if(wHMB.getRangedDeductionDetailsList() == null)
                 wHMB.setRangedDeductionDetailsList(new ArrayList<>());
             wHMB.getRangedDeductionDetailsList().add(rangedDeductionDetails);
        } else {

            List<RangedDeductionDetails> nuList = new ArrayList<>();
            for (RangedDeductionDetails n : wHMB.getRangedDeductionDetailsList()) {
                if (n.getEntryIndex().equalsIgnoreCase(pObjId))
                    continue;
                nuList.add(n);

            }
            wHMB.setRangedDeductionDetailsList(nuList);
        }
        wHMB.setAmountStr(null);
        wHMB.setLowerBoundValue(null);
        wHMB.setUpperBoundValue(null);
        model.addAttribute(PS_BEAN,null);
        PaginatedPaycheckGarnDedBeanHolder p = this.makePaginatedList(wHMB, request, false);

        addRoleBeanToModel(model, request);
        model.addAttribute("action", pObjId);
        model.addAttribute("miniBean", p.getSomeObject());
        model.addAttribute("displayList", p);
        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"oid", "act"})
    public String setupForm(@RequestParam("oid") Long pId, @RequestParam("act") int pSaved, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        RangedDeduction wHMB = genericService.loadObjectUsingRestriction(RangedDeduction.class, Arrays.asList(CustomPredicate.procurePredicate("id", pId), getBusinessClientIdPredicate(request)));
        if (wHMB.isNewEntity())
            return "redirect:addRangedDeduction.do";

 
        PaginatedPaycheckGarnDedBeanHolder p = this.makePaginatedList(wHMB, request, false);

        addRoleBeanToModel(model, request);
        addSaveMsgToModel(request, model, "Ranged Deduction Configuration Saved Successfully");
        model.addAttribute("miniBean", p.getSomeObject());
        model.addAttribute("displayList", p);
        model.addAttribute("action", "");
        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") RangedDeduction pHMB, BindingResult
                                        result, SessionStatus status, org.springframework.ui.Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate businessCertificate = getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_ADD)) {

            validator.validate(pHMB, result, businessCertificate);
            if (result.hasErrors()) {
                model = makeModel(pHMB, request, model, true);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("status", result);
                model.addAttribute("action", "");
                return VIEW;
            }
            if(pHMB.isFirstTimePay()) {
                pHMB.setFirstTimePay(false);
                pHMB.setExpired(true);
            }
            //super.addSessionAttribute(request, PS_BEAN, pHMB);
            model.addAttribute(PS_BEAN, pHMB);
            return "redirect:addRangedDeduction.do?iid=" + REQUEST_PARAM_ADD;

        }

        if (isButtonTypeClick(request, REQUEST_PARAM_DONE)) {

            //validator.validate(pHMB, result, businessCertificate);
            if (!pHMB.isConfirmation())
                pHMB.setConfirmation(true);
                model = makeModel(pHMB, request, model, false);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                result.rejectValue("confirmation", "Confirm.Value", "Please confirm this Ranged Deduction.");
                result.rejectValue("confirmation", "Confirm.Value", "Note** - A Deduction Type of the same name will be auto-created for you");
                result.rejectValue("confirmation", "Confirm.Value", "The Deduction Type Name will not be editable.");
                model.addAttribute("action", "");
            return VIEW;

        }
        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {

                return this.saveRecords(pHMB, businessCertificate,request);

        }

        return "redirect:addRangedDeduction.do";
    }

    private String saveRecords(RangedDeduction pHMB, BusinessCertificate bc,HttpServletRequest request) throws IllegalAccessException, InstantiationException {

        for(RangedDeductionDetails r : pHMB.getRangedDeductionDetailsList())
            r.setRangedDeduction(pHMB);
        pHMB.setBusinessClientId(bc.getBusinessClientInstId());
        pHMB.setCreatedBy(new User(bc.getLoginId()));
        pHMB.setLastModBy(pHMB.getCreatedBy());
        pHMB.setLastModTs(Timestamp.from(Instant.now()));
        Long id = this.genericService.storeObject(pHMB);
        //Auto Create a Deduction Type if it does not exists...

        EmpDeductionType wEDT = new EmpDeductionType();
        EmpDeductionCategory empDeductionCategory = this.genericService.loadObjectUsingRestriction(EmpDeductionCategory.class,Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate( "rangedInd",ON)));
        if(empDeductionCategory.isNewEntity())
            empDeductionCategory = IppmsUtilsExt.createRangedDeductionCategory(genericService,bc);
        wEDT.setEmpDeductionCategory(empDeductionCategory);
        wEDT.setName(pHMB.getName());
        wEDT.setDescription(pHMB.getName());
        wEDT.setAmount(0.0D);
        wEDT.setTaxable("Y");
        wEDT.setSubTypeEnable("N");
        wEDT.setInherited(false);
        wEDT.setCompanyTypeEnable("N");
        wEDT.setLastModBy(new User(bc.getLoginId()));
        wEDT.setCreatedBy(new User(bc.getLoginId()));
        wEDT.setLastModTs(Timestamp.from(Instant.now()));
        wEDT.setBusinessClientId(bc.getBusinessClientInstId());
        // Now Add Default PayTypes and Default Bank Branch..
        wEDT.setBankBranches(this.genericService.loadDefaultObject(BankBranch.class, Arrays.asList(CustomPredicate.procurePredicate("defaultInd", IConstants.ON))));
        wEDT.setPayTypes(this.genericService.loadDefaultObject(PayTypes.class, Arrays.asList(CustomPredicate.procurePredicate("defaultInd", IConstants.ON))));
        wEDT.setAutoGenInd(ON);
        this.genericService.storeObject(wEDT);


        return "redirect:addRangedDeduction.do?oid=" + id + "&act=1";
    }


    private Model makeModel(RangedDeduction pHMB, HttpServletRequest request, org.springframework.ui.Model model, boolean error) {
        PaginatedPaycheckGarnDedBeanHolder p = this.makePaginatedList(pHMB, request, error);

        model.addAttribute(DISPLAY_ERRORS, BLOCK);
        model.addAttribute("miniBean", p.getSomeObject());
        model.addAttribute("displayList", p);
        addRoleBeanToModel(model, request);
       
        return model;
    }


    private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(RangedDeduction pHMB, HttpServletRequest request, boolean errorRec) {

        PaginationBean paginationBean = getPaginationInfo(request);


        List<RangedDeductionDetails> wAllList = pHMB.getRangedDeductionDetailsList();
        if (wAllList == null) {
            wAllList = new ArrayList<>();
        }
        //Make a new NamedEntity....

        List<RangedDeductionDetails> wRetList;

        //Do we do the list upside down now?

        
            Comparator<RangedDeductionDetails> c = Comparator.comparing(RangedDeductionDetails::getEntryIndex);
            Collections.sort(wAllList, c.reversed());
         

        if (wAllList.size() > 10) {

            wRetList = (List<RangedDeductionDetails>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 10, wAllList);

        } else {
            wRetList = wAllList;
        }

        PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());
 
        wPBO.setSomeObject(pHMB);


        return wPBO;
    }
 

}
