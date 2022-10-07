package com.osm.gnl.ippms.ogsg.controllers.paygroup;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.MassReassignmentService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignMasterBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/approveMassReassign.do"})
@SessionAttributes({"miniBean"})
public class ApproveMassReassignment extends BaseController {

    @Autowired
    MassReassignmentService massReassignmentService;


    private final String VIEW_NAME = "configcontrol/approveMassReassign";


    public ApproveMassReassignment() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public  String viewAll( Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {

        SessionManagerService.manageSession(request, model);


        List<MassReassignMasterBean> pMR  = this.genericService.loadAllObjectsUsingRestrictions(MassReassignMasterBean.class,Arrays.asList(getBusinessClientIdPredicate(request),
                    CustomPredicate.procurePredicate("approvalStatus", IConstants.OFF)),"name");

        List<MassReassignMasterBean> aMR = this.genericService.loadAllObjectsUsingRestrictions(MassReassignMasterBean.class,Arrays.asList(getBusinessClientIdPredicate(request),
                    CustomPredicate.procurePredicate("approvalStatus", IConstants.ON)),"name");

        List<MassReassignMasterBean> rMR  = this.genericService.loadAllObjectsUsingRestrictions(MassReassignMasterBean.class,Arrays.asList(getBusinessClientIdPredicate(request),
                    CustomPredicate.procurePredicate("approvalStatus", 2)),"name");

        List<MassReassignMasterBean> allMR = this.genericService.loadAllObjectsWithSingleCondition(MassReassignMasterBean.class,getBusinessClientIdPredicate(request),"name");

        model.addAttribute("pList", pMR);
        model.addAttribute("aList", aMR);
        model.addAttribute("rList", rMR);
        model.addAttribute("allList", allMR);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"aid"})
    public String viewSingle(@RequestParam("aid") Long aid, Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);
        MassReassignMasterBean massReassignMasterBeans = this.genericService.loadObjectUsingRestriction(MassReassignMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("approvalStatus",OFF), CustomPredicate.procurePredicate("id", aid)));

        List<MassReassignDetailsBean> mRd = massReassignMasterBeans.getMassReassignDetailsBeans();

        double sumFrom = 0.00D;
        double sumTo = 0.00D;
        for(MassReassignDetailsBean mr: mRd){
            sumFrom+=mr.getFromSalaryInfo().getMonthlySalary();
            sumTo+=mr.getToSalaryInfo().getMonthlySalary();
        }

        double netDiff = sumTo-sumFrom;

        massReassignMasterBeans.setSumFrom(PayrollHRUtils.getDecimalFormat().format(sumFrom));
        massReassignMasterBeans.setSumTo(PayrollHRUtils.getDecimalFormat().format(sumTo));
        massReassignMasterBeans.setNetDiff(PayrollHRUtils.getDecimalFormat().format(netDiff));

        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", massReassignMasterBeans);
        model.addAttribute("approve",IConstants.ON);

        return "configcontrol/massReassignResultForm";
    }

    @RequestMapping(method = {RequestMethod.POST })
    public String processSubmit(@RequestParam(value="_close", required=false) String close,
                                @RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_approve", required=false) String approve,
                                @RequestParam(value="_reject", required=false) String reject,
                                @ModelAttribute("miniBean") MassReassignMasterBean pEHB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:approveMassReassign.do";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {
            if (!bc.isSuperAdmin()) {
                result.rejectValue("", "approvalMemo", "Your profile is not enabled to Approve a Mass Reassignment.");
                addDisplayErrorsToModel(model, request);
                model.addAttribute("pageErrors", result);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("approve",IConstants.OFF);
                addRoleBeanToModel(model, request);
                addDisplayErrorsToModel(model, request);
                return "configcontrol/massReassignResultForm";
            }
            if (bc.getLoginId().equals(pEHB.getCreatedBy().getId())) {
                result.rejectValue("", "approvalMemo", "Your cannot approve a Mass Reassignment created by you");
                pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
                pEHB.setEnteredCaptcha(null);
                model.addAttribute("pageErrors", result);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("approve",IConstants.OFF);
                addRoleBeanToModel(model, request);
                addDisplayErrorsToModel(model, request);
                return "configcontrol/massReassignResultForm";
            }
            if (!pEHB.isConfirmation()) {
                pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
                pEHB.setEnteredCaptcha(null);
                pEHB.setConfirmation(true);
                System.out.println("captcha is " + pEHB.getGeneratedCaptcha());
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("approve",IConstants.ON);
                addRoleBeanToModel(model, request);
                return "configcontrol/massReassignResultForm";
            }
            if (!pEHB.getGeneratedCaptcha().equalsIgnoreCase(pEHB.getEnteredCaptcha())) {
                result.rejectValue("", "approvalMemo", "Entered Captcha " + pEHB.getEnteredCaptcha() + " did not match Generated Captcha " + pEHB.getGeneratedCaptcha() + " ");
                result.rejectValue("", "approvalMemo", "Please make sure they match.");
                pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
                pEHB.setEnteredCaptcha(null);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("pageErrors", result);
                model.addAttribute("approve",IConstants.ON);
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                return "configcontrol/massReassignResultForm";
            }
            //run the code

            List<MassReassignDetailsBean> mRdb = pEHB.getMassReassignDetailsBeans();

            for(MassReassignDetailsBean mr: mRdb){
                this.massReassignmentService.updateSalaryInfo(mr.getParentId(), mr.getToSalaryInfo().getId(),
                        bc.getBusinessClientInstId());
            }
            pEHB.setApprovalStatus(IConstants.ON);
            pEHB.setConfirmation(false);
            this.genericService.saveOrUpdate(pEHB);
            model.addAttribute(SAVED_MSG, pEHB.getName()+" Reassignment approved successfully");
            model.addAttribute("saved", Boolean.TRUE);
            model.addAttribute("approve",IConstants.OFF);
            addRoleBeanToModel(model, request);
            return "configcontrol/massReassignResultForm";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_REJECT)) {
            pEHB.setApprovalStatus(2);
            pEHB.setConfirmation(false);
            this.genericService.saveOrUpdate(pEHB);
            model.addAttribute(SAVED_MSG, pEHB.getName()+" Reassignment has been Rejected");
            model.addAttribute("saved", Boolean.TRUE);
            model.addAttribute("approve",IConstants.OFF);
            addRoleBeanToModel(model, request);
            return "configcontrol/massReassignResultForm";
        }

        isButtonTypeClick(request, REQUEST_PARAM_CLOSE);
            return CONFIG_HOME_URL;

    }
}

