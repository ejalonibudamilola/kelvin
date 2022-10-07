/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.hr.ext.AllowanceRuleControllerService;
import com.osm.gnl.ippms.ogsg.domain.approval.AllowanceRuleApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleDetails;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.hr.ReabsorbingValidator;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/createAllowanceRule.do"})
@SessionAttributes(types = {HrMiniBean.class})
public class PayGroupAllowanceRuleController extends BaseController {

    private final ReabsorbingValidator validator;
    private final String VIEW_NAME = "rules/allowanceRuleForm";

    @Autowired
    public PayGroupAllowanceRuleController(ReabsorbingValidator validator) {
        this.validator = validator;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pHid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (IppmsUtils.isNullOrLessThanOne(pHid)) {
            throw new Exception(bc.getStaffTypeName() + " Not Found. ");
        }

        HiringInfo wHI = loadHiringInfoByEmpId(request, bc, pHid);
        if (wHI.isNewEntity()) {
            AbstractEmployeeEntity abstractEmployeeEntity = IppmsUtils.loadEmployee(genericService, pHid, bc);
            if (wHI.isNewEntity())
                throw new Exception("No Hiring Information found for " + abstractEmployeeEntity.getDisplayName() + bc.getStaffTitle() + " : " + abstractEmployeeEntity.getEmployeeId());

        }
        AllowanceRuleMaster allowanceRuleMaster = IppmsUtilsHelper.getActiveAllowanceRule(genericService, bc.getBusinessClientInstId(), wHI.getId());
        if (!allowanceRuleMaster.isNewEntity())
            return "redirect:createAllowanceRule.do?hid=" + wHI.getId() + "&mode=u";
        HrMiniBean empHrBean = new HrMiniBean();
        empHrBean.setAllowanceRuleMaster(allowanceRuleMaster);


        empHrBean = AllowanceRuleControllerService.setRequiredValues(wHI, empHrBean,false);
        empHrBean.setShowDetails(true);

        return this.makeModelAndReturnView(model, request, bc, empHrBean, null);
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"hid", "mode"})
    public String setupForm(@RequestParam("hid") Long pHid, @RequestParam("mode") String pMode, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (IppmsUtils.isNullOrLessThanOne(pHid)) {
            return REDIRECT_TO_DASHBOARD;
        }

        HrMiniBean empHrBean = new HrMiniBean();
        AllowanceRuleMaster allowanceRuleMaster = genericService.loadObjectUsingRestriction(AllowanceRuleMaster.class, Arrays.asList(
                CustomPredicate.procurePredicate("hiringInfo.id", pHid), CustomPredicate.procurePredicate("activeInd", ON)));
        if (allowanceRuleMaster.isNewEntity())
            return "redirect:createAllowanceRule.do?hid=" + pHid;
        if (!this.paycheckWithReductionExists(allowanceRuleMaster.getHiringInfo().getParentId(), request, allowanceRuleMaster.getId())) {
            empHrBean.setDeletable(true);

        }
        empHrBean.setEditMode(true);
        empHrBean.setEndDate(allowanceRuleMaster.getRuleEndDate());
        empHrBean.setAllowanceRuleMaster(allowanceRuleMaster);
        model.addAttribute("saved", false);
        empHrBean = AllowanceRuleControllerService.setRequiredValues(allowanceRuleMaster.getHiringInfo(), empHrBean,false);
        empHrBean.setShowDetails(true);
        return this.makeModelAndReturnView(model, request, bc, empHrBean, null);
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"arid", "s","st"})
    public String setupForm(@RequestParam("arid") Long pArId, @RequestParam("s") int pSaved,@RequestParam("st") int pSemaphore,
                            Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        HrMiniBean empHrBean = new HrMiniBean();
        if (pArId == 0L) {
            //Rare case (hopefully of a delete before application)

            model.addAttribute(IConstants.SAVED_MSG, "Pay Group Allowance Rule Deleted Permanently");
            model.addAttribute("saved", true);
            return this.makeModelAndReturnView(model, request, bc, empHrBean, null);
        }
        AllowanceRuleMaster allowanceRuleMaster = this.genericService.loadObjectById(AllowanceRuleMaster.class, pArId);

        if (allowanceRuleMaster.isNewEntity())
            return REDIRECT_TO_DASHBOARD; //Should technically never happen

        empHrBean.setAllowanceRuleMaster(allowanceRuleMaster);
        empHrBean = AllowanceRuleControllerService.setRequiredValues(allowanceRuleMaster.getHiringInfo(), empHrBean,true);

        String actionCompleted = bc.getStaffTypeName() + " " + allowanceRuleMaster.getHiringInfo().getAbstractEmployeeEntity().getDisplayName();

        if (pSaved == 1) //expired.
            actionCompleted += " Pay Group Allowance Rule expired successfully";
        else if (pSaved == 2)
            actionCompleted += " Pay Group Allowance Rule created successfully";
        else if (pSaved == 3)
            actionCompleted += " Pay Group Allowance Rule edited successfully";
        else {
            actionCompleted += " Pay Group Allowance Rule left unedited (No changes or invalid changes made) ";
        }
        empHrBean.setShowDetails(true);
        if(Navigator.getInstance(getSessionId(request)).getFromClass() != null &&
                Navigator.getInstance(getSessionId(request)).getFromClass().isAssignableFrom(this.getClass())) {
            model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
            Navigator.getInstance(getSessionId(request)).setFromClass(null); //Remove the key for show success message.
        }
        model.addAttribute("saved", true);
        return this.makeModelAndReturnView(model, request, bc, empHrBean, null);

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_delete", required = false) String delete,
                                @RequestParam(value = "_updateReport", required = false) String updateReport,
                                @ModelAttribute("miniBean") HrMiniBean pEHB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return  Navigator.getInstance(getSessionId(request)).getFromForm();
        }
        PayrollRunMasterBean wPRB = IppmsUtilsExt.loadCurrentlyRunningPayroll(genericService, bc);
        if (!wPRB.isNewEntity() && wPRB.isRunning()) {
            result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ". Pay Group Allowance Rule can not be effected during a Payroll Run");
            return this.makeModelAndReturnView(model, request, bc, pEHB, result);
        }

        validator.validateForAllowanceRule(pEHB, result, bc);
        if (result.hasErrors()) {
            return this.makeModelAndReturnView(model, request, bc, pEHB, result);
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_DELETE)) {
            pEHB.setDeleteOperation(true);
            pEHB.setConfirmation(true);
            result.rejectValue("", "No.Employees", "Please Enter a value for 'Entered Captcha' to confirm.");
            pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
            return this.makeModelAndReturnView(model, request, bc, pEHB, result);
        } else if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
            pEHB.setUpdateOperation(true);
            pEHB.setConfirmation(true);
            pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
            result.rejectValue("", "No.Employees", "Please Enter a value for 'Entered Captcha' to confirm.");
            return this.makeModelAndReturnView(model, request, bc, pEHB, result);
        }

        if (!pEHB.getGeneratedCaptcha().equalsIgnoreCase(pEHB.getEnteredCaptcha())) {
            result.rejectValue("", "No.Employees", "Entered Captcha does not match Generated Captcha!");
            pEHB.setCaptchaError(true);
            return this.makeModelAndReturnView(model, request, bc, pEHB, result);
        }
        //If we get here...set redirect info
        Navigator.getInstance(getSessionId(request)).setFromClass(this.getClass());
        if (pEHB.isDeleteOperation())
            return doDelete(request, pEHB, bc);
        else
            return doUpdate(pEHB, bc);

    }

    private String doUpdate(HrMiniBean pEHB, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {

        int pSaved;
        Long id = 0L;
        if (pEHB.getAllowanceRuleMaster().isNewEntity()) {
            pSaved = 2;
        } else {
            pSaved = 3;
        }
        pEHB.getAllowanceRuleMaster().setLastModBy(new User(bc.getLoginId()));
        List<AllowanceRuleDetails> chosenOnes = new ArrayList<>();
        for (AllowanceRuleDetails r : pEHB.getAllowanceRuleMaster().getAllowanceRuleDetailsList()) {
            if (add(r)) {
                r.setAllowanceRuleMaster(pEHB.getAllowanceRuleMaster());
                chosenOnes.add(r);
            }
        }
        if (!chosenOnes.isEmpty()) {
            pEHB.getAllowanceRuleMaster().setBusinessClientId(bc.getBusinessClientInstId());
            pEHB.getAllowanceRuleMaster().setCreatedBy(new User(bc.getLoginId()));
            pEHB.getAllowanceRuleMaster().setLastModTs(Timestamp.from(Instant.now()));
            pEHB.getAllowanceRuleMaster().setLastModBy(new User(bc.getLoginId()));
            pEHB.getAllowanceRuleMaster().setAllowanceRuleDetailsList(chosenOnes);
            this.genericService.saveObject(pEHB.getAllowanceRuleMaster());
            //We need to create approval now....

            createApprovalAndNotify(pEHB.getAllowanceRuleMaster(), bc, "Pending PayGroup Allowance Rule",pSaved);
            return "redirect:createAllowanceRule.do?arid=" + pEHB.getAllowanceRuleMaster().getId() + "&s=" + pSaved+"&st=1";
        } else {

            return "redirect:createAllowanceRule.do?arid=" + id + "&s=4&st=1";
        }
    }

    private void createApprovalAndNotify(AllowanceRuleMaster allowanceRuleMaster, BusinessCertificate bc, String pMsg, int pSaved) throws InstantiationException, IllegalAccessException {
        AllowanceRuleApproval allowanceRuleApproval = genericService.loadObjectUsingRestriction(AllowanceRuleApproval.class, Arrays.asList(CustomPredicate.procurePredicate("allowanceRuleMaster.id", allowanceRuleMaster.getId()),
                CustomPredicate.procurePredicate("approvalStatusInd", 0)));
        if(allowanceRuleApproval.isNewEntity()){
            allowanceRuleApproval.setAllowanceRuleMaster(allowanceRuleMaster);
            allowanceRuleApproval.setEntityId(allowanceRuleMaster.getHiringInfo().getParentId());
            allowanceRuleApproval.setEntityName(allowanceRuleMaster.getHiringInfo().getAbstractEmployeeEntity().getDisplayName());
            allowanceRuleApproval.setEmployeeId(allowanceRuleMaster.getHiringInfo().getAbstractEmployeeEntity().getEmployeeId());
            allowanceRuleApproval.setMdaDeptMap(allowanceRuleMaster.getHiringInfo().getAbstractEmployeeEntity().getMdaDeptMap());
            allowanceRuleApproval.setSchoolInfo(allowanceRuleMaster.getHiringInfo().getAbstractEmployeeEntity().getSchoolInfo());
            allowanceRuleApproval.setBusinessClientId(bc.getBusinessClientInstId());
            allowanceRuleApproval.setInitiator(new User(bc.getLoginId()));
            allowanceRuleApproval.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
            allowanceRuleApproval.setLastModTs(LocalDate.now());
            allowanceRuleApproval.setInitiatedDate(LocalDate.now());

            allowanceRuleApproval.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(bc.getLoginId()));
        }
        genericService.saveObject(allowanceRuleApproval);

        String url = "requestNotification.do?arid="+allowanceRuleApproval.getId()+"&s="+pSaved+"&oc="+IConstants.ALLOWANCE_RULE_INIT_URL_IND;
        NotificationService.storeNotification(bc, genericService, allowanceRuleApproval, url, pMsg, IConstants.ALLOWANCE_RULE_CODE);


    }


    private String doDelete(HttpServletRequest request, HrMiniBean pEHB, BusinessCertificate bc) {

        pEHB.getAllowanceRuleMaster().setRuleEndDate(LocalDate.now());
        pEHB.getAllowanceRuleMaster().setLastModBy(new User(bc.getLoginId()));
        pEHB.getAllowanceRuleMaster().setActiveInd(OFF);

        return "redirect:createAllowanceRule.do?arid=" + this.genericService.storeObject(pEHB.getAllowanceRuleMaster()) + "&s=1&st=1";


    }


    private boolean add(AllowanceRuleDetails r) {
        Object amountStr = PayrollHRUtils.getNumberFromString(r.getAmountStr());
        if (amountStr != null) {
            if (amountStr.equals(0)) {
                r.setApplyMonthlyValue(0.0D);
                r.setApplyYearlyValue(0.0D);
            } else {
                //This might be an edit done after creation.
                if (((Double) amountStr) < r.getMonthlyValue()) {
                    r.setApplyMonthlyValue((Double) amountStr);
                    r.setApplyYearlyValue(EntityUtils.multiplyAsBigDecimal(r.getApplyMonthlyValue(), 12.0D));
                }

            }
            return true;
        }
        return false;
    }

    private boolean paycheckWithReductionExists(Long pEmpId, HttpServletRequest request, Long pReductionId) {
        if (pReductionId == null)
            return false;
        PredicateBuilder predicateBuilder = new PredicateBuilder().
                addPredicate(CustomPredicate.procurePredicate("employee.id", pEmpId))
                .addPredicate(CustomPredicate.procurePredicate("allowanceRuleId", pReductionId))
                .addPredicate(getBusinessClientIdPredicate(request));
        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(getBusinessCertificate(request))) > 0;
    }



    private String makeModelAndReturnView(Model model, HttpServletRequest request, BusinessCertificate bc, HrMiniBean pEHB, BindingResult result) {

        if (result != null) {
            addDisplayErrorsToModel(model, request);
            model.addAttribute("saved", false);
            model.addAttribute("status", result);
        }
        model.addAttribute("miniBean", pEHB);
        model.addAttribute("roleBean", bc);
        if(pEHB.isForSuccessDisplay()){
            addPageTitle(model,"Pay Group Allowance Rule Details");
        }else {
            if (pEHB.isEditMode())
                addPageTitle(model, "Edit/Expire Pay Group Allowance Rule");
            else
                addPageTitle(model, "Create Pay Group Allowance Rule");
        }

        return VIEW_NAME;
    }
}
