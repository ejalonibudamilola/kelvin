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
import com.osm.gnl.ippms.ogsg.domain.payment.RangedDeduction;
import com.osm.gnl.ippms.ogsg.domain.payment.RangedDeductionDetails;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/editRangedDeduction.do"})
@SessionAttributes(types = {RangedDeduction.class})
public class EditRangeDeductionController extends BaseController {


    @Autowired
    private RangedDeductionValidator validator;


    private final String VIEW = "configcontrol/editDeductionRangeForm";

    public EditRangeDeductionController() {
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"oid"})
    public String setupForm(@RequestParam("oid") Long pOid,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        RangedDeduction wHMB  = genericService.loadObjectUsingRestriction(RangedDeduction.class,Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pOid)));
        if(wHMB.isNewEntity())
            return "redirect:addRangedDeduction.do";

        wHMB = setDisplayStyle(wHMB);


        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wHMB);
        return VIEW;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"oid", "act"})
    public String setupForm(@RequestParam("oid") Long pId, @RequestParam("act") int pSaved, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        RangedDeduction wHMB = genericService.loadObjectUsingRestriction(RangedDeduction.class, Arrays.asList(CustomPredicate.procurePredicate("id", pId), getBusinessClientIdPredicate(request)));
        if (wHMB.isNewEntity())
            return "redirect:addRangedDeduction.do";

        wHMB = setDisplayStyle(wHMB);
        addRoleBeanToModel(model, request);
        addSaveMsgToModel(request, model, "Ranged Deduction Configuration Edited Successfully");
        model.addAttribute("miniBean", wHMB);

        return VIEW;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") RangedDeduction pHMB, BindingResult
                                        result, SessionStatus status, org.springframework.ui.Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate businessCertificate = getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            //This should redirect to the VIEW ALL....
            return "redirect:viewRangedDeductions.do";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_DONE)) {

            validator.validateForEdit(pHMB, result, businessCertificate);
            if (result.hasErrors()) {
                model = makeModel(pHMB, request, model);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("status", result);

            } else {
                model = makeModel(pHMB, request, model);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                result.rejectValue("confirmation", "Confirm.Value", "Please confirm this Edit of Ranged Deduction.");
                if (!pHMB.isConfirmation())
                    pHMB.setConfirmation(true);
            }
            return VIEW;

        }
        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {
            validator.validate(pHMB, result, businessCertificate);
            if (result.hasErrors()) {
                model = makeModel(pHMB, request, model);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("status", result);
                return VIEW;
            } else {
                return this.saveRecords(pHMB);
            }
        }

        return "redirect:viewRangedDeductions.do";
    }
    private RangedDeduction setDisplayStyle(RangedDeduction wHMB) {
        int i = 1;
        for(RangedDeductionDetails r : wHMB.getRangedDeductionDetailsList()){

            if (i % 2 == 1) {
                r.setDisplayStyle("reportEven");
            }else {
                r.setDisplayStyle("reportOdd");
            }
            r.setAmountAsStr(PayrollHRUtils.getDecimalFormat().format(r.getAmount()));
            r.setLowerBoundAsStr(PayrollHRUtils.getDecimalFormat().format(r.getLowerBound()));
            r.setUpperBoundAsStr(PayrollHRUtils.getDecimalFormat().format(r.getUpperBound()));
            i++;
        }

        return wHMB;
    }
    private Model makeModel(RangedDeduction pHMB, HttpServletRequest request, org.springframework.ui.Model model) {

        model.addAttribute(DISPLAY_ERRORS, BLOCK);
        model.addAttribute("miniBean", pHMB);
        addRoleBeanToModel(model, request);

        return model;
    }
    private String saveRecords(RangedDeduction pHMB) {
        List<RangedDeductionDetails> detailsList = pHMB.getRangedDeductionDetailsList();
        pHMB.setLastModBy(pHMB.getCreatedBy());
        pHMB.setLastModTs(Timestamp.from(Instant.now()));

        for (RangedDeductionDetails g : detailsList) {
             genericService.storeObject(g);
        }
        return "redirect:editRangedDeduction.do?oid=" + pHMB.getId() + "&act=1";
    }

}
