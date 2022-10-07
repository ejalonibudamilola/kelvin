/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.EmployeeApproval;
import com.osm.gnl.ippms.ogsg.domain.beans.PaginatedEpmBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.promotion.FlaggedPromotions;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.engine.PromotionHelperService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping({"/viewPendingFlaggedPromo.do"})
@SessionAttributes(types={PaginatedBean.class})
public class PendingFlaggedPromoController extends BaseController {


    private final PromotionService promotionService;
    private final PaycheckService paycheckService;

    private int pageLength = 20;
    private final String VIEW = "promotion/viewPendingFlaggedPromoForm";

    @Autowired
    public PendingFlaggedPromoController(PromotionService promotionService, PaycheckService paycheckService) {
        this.promotionService = promotionService;
        this.paycheckService = paycheckService;
    }

    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        BaseController.PaginationBean paginationBean = this.getPaginationInfo(request);


        List<FlaggedPromotions> empList = this.genericService.loadPaginatedObjects(FlaggedPromotions.class,
                Arrays.asList(CustomPredicate.procurePredicate("treatedDate", null, Operation.IS_NULL)),
                (paginationBean.getPageNumber() - 1) * this.pageLength,
                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());


        int wNoOfElements = this.promotionService.getTotalNoOflaggedPromotionsForApprovals(bc.getBusinessClientInstId());

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength,
                wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());


        if (bc.isSuperAdmin())
            wPHDB.setEditMode(true);

        Navigator.getInstance(super.getSessionId(request)).setFromForm("redirect:viewPendingFlaggedPromo.do");
        model.addAttribute("miniBean", wPHDB);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET} ,params={"s","us","r"})
    public String setupForm(@RequestParam(name = "s") Integer pSaved,
                            @RequestParam(name = "us") Integer pUnSaved,
                            @RequestParam(name = "r") Integer pRejected,
                             Model model, HttpServletRequest pRequest) throws Exception {
        SessionManagerService.manageSession(pRequest, model);

        BusinessCertificate bc = this.getBusinessCertificate(pRequest);
        PaginationBean paginationBean = this.getPaginationInfo(pRequest);


        List<FlaggedPromotions> empList = this.genericService.loadPaginatedObjects(FlaggedPromotions.class,
                Arrays.asList(CustomPredicate.procurePredicate("treatedDate", null, Operation.IS_NULL)),
                (paginationBean.getPageNumber() - 1) * this.pageLength,
                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());


        int wNoOfElements = this.promotionService.getTotalNoOflaggedPromotionsForApprovals(bc.getBusinessClientInstId());

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength,
                wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());


        if (bc.isSuperAdmin())
            wPHDB.setEditMode(true);


        if (IppmsUtils.isNotNullAndGreaterThanZero(pRejected)){
            addSaveMsgToModel(pRequest, model, " " + pUnSaved + " Flagged promotions Rejected Successfully");
        }
        else{
            String approvalMessage = "";
            if (IppmsUtils.isNotNullAndGreaterThanZero(pSaved))
                 approvalMessage = "Promotions effected for " + pSaved + " Flagged Promotions Successfully.";

            if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved))
                approvalMessage += " " + pUnSaved + " Flagged Promotions not processed.";

            addSaveMsgToModel(pRequest, model, approvalMessage.trim());
        }

        if (requestsAreSelected(pRequest)) {
            setSelectedRequests(getSelectedRequests(pRequest), empList);
        }

        model.addAttribute("miniBean", wPHDB);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }
    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @ModelAttribute("miniBean") PaginatedBean wPHDB,
                                 BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }

        int found = 0;
        for (FlaggedPromotions eA : (List<FlaggedPromotions>) wPHDB.getList()) {

            if (Boolean.valueOf(eA.getRowSelected())) {

                found++;
                break;
            }
        }
        if(found == 0){
             if(isButtonTypeClick(request,REQUEST_PARAM_APPROVE))
                   result.rejectValue("approvalMemo", "Warning", " Please pick at least 1 Flagged Promotion to Approve .");
             else if(isButtonTypeClick(request,REQUEST_PARAM_REJECT))
                   result.rejectValue("approvalMemo", "Warning", " Please pick at least 1 Flagged Promotion to Reject .");
             else
                 result.rejectValue("approvalMemo", "Warning", " Please pick at least 1 Flagged Promotion to Confirm .");
            model.addAttribute("status",result);
            model.addAttribute("miniBean", wPHDB);
            addRoleBeanToModel(model, request);
            addDisplayErrorsToModel(model, request);
            return VIEW;
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {
            if (!wPHDB.isAddWarningIssued()) {
                wPHDB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Approve Selected " + bc.getStaffTypeName() + " Promotions. Please enter a value for 'Approval Memo' and Confirm.");
                wPHDB.setApprovalMemo("REF/"+bc.getUserName()+"/FP_APRV/"+PayrollBeanUtils.getDateAsStringWidoutSeparators(LocalDate.now()));
                model.addAttribute("status", result);
                model.addAttribute("miniBean", wPHDB);
                addRoleBeanToModel(model, request);
                addDisplayErrorsToModel(model, request);
                return VIEW;
            }

        }

        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {
            if (IppmsUtils.isNullOrEmpty(wPHDB.getApprovalMemo()) || StringUtils.trimToEmpty(wPHDB.getApprovalMemo()).trim().length() < 8) {
                wPHDB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Approval Memo'");
                model.addAttribute("status", result);
                model.addAttribute("miniBean", wPHDB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW;
            } else {
                Integer unprocessed = 0;
                Integer processed = 0;
                for (FlaggedPromotions flaggedPromotions : (List<FlaggedPromotions>) wPHDB.getObjectList()) {

                    if (!Boolean.valueOf(flaggedPromotions.getRowSelected())) {
                        ++unprocessed;
                        continue;
                    }
                    processed += promoteSingleEmployee(flaggedPromotions, bc, wPHDB.getApprovalMemo(), request);


                }
                return "redirect:viewPendingFlaggedPromo.do?s=" + processed + "&us=" + unprocessed+"&r=0";
            }
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_REJECT)) {

            //if we get here...check if Rejection Reason is set..
            if (!wPHDB.isAddWarningIssued()) {
                wPHDB.setAddWarningIssued(true);
                wPHDB.setMemoType("Rejection");
                wPHDB.setRejection(true);

                result.rejectValue("", "warning", "Please enter a Rejection Reason.");
                wPHDB.setApprovalMemo("REF/"+bc.getUserName()+"/FP_REJ/"+PayrollBeanUtils.getDateAsStringWidoutSeparators(LocalDate.now()));
                model.addAttribute("status", result);
                model.addAttribute("miniBean", wPHDB);
                model.addAttribute("roleBean", bc);
                return VIEW;
            }
            if (IppmsUtils.isNullOrEmpty(wPHDB.getApprovalMemo()) || StringUtils.trimToEmpty(wPHDB.getApprovalMemo()).trim().length() < 8) {
                wPHDB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Rejection Memo'");
                model.addAttribute("miniBean", wPHDB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW;
            } else {
                Integer unprocessed = 0;
                Integer processed = 0;
                for (FlaggedPromotions flaggedPromotions : (List<FlaggedPromotions>) wPHDB.getObjectList()) {

                    if (!Boolean.valueOf(flaggedPromotions.getRowSelected())) {
                        ++unprocessed;
                        continue;
                    }
                    processed += rejectPromotion(flaggedPromotions, bc, wPHDB.getApprovalMemo());
                }

                return "redirect:viewPendingFlaggedPromo.do?s=" + unprocessed + "&us=" + processed+"&r=1";

            }

        }
        return REDIRECT_TO_DASHBOARD;
    }

    // The approval method goes here

    private Integer promoteSingleEmployee(FlaggedPromotions flaggedPromotions, BusinessCertificate bc, String approvalMemo, HttpServletRequest request) throws Exception {

        Long schoolId = flaggedPromotions.getEmployee().isSchoolStaff() ? flaggedPromotions.getEmployee().getSchoolInfo().getId() : null;
        PromotionTracker wPT = this.genericService.loadObjectUsingRestriction(PromotionTracker.class,Arrays.asList(CustomPredicate.procurePredicate("employee.id", flaggedPromotions.getEmployee().getId()),
                getBusinessClientIdPredicate(request)));
        PromotionHelperService.applyPromotion(bc,flaggedPromotions.getEmployee().getId(),flaggedPromotions.getHireInfoId(),flaggedPromotions.getToSalaryInfo().getLevel(),promotionService,paycheckService,flaggedPromotions.getNewRank().getId(),genericService,flaggedPromotions.getFromSalaryInfo().getId()
                ,flaggedPromotions.getToSalaryInfo().getId(),flaggedPromotions.getMdaInfo().getId(),schoolId,wPT,flaggedPromotions.getArrears(),flaggedPromotions.getRefNumber(),flaggedPromotions.getRefDate());

        flaggedPromotions.setApprover(new User(bc.getLoginId()));
        flaggedPromotions.setTreatedDate(Timestamp.from(Instant.now()));
        flaggedPromotions.setStatusInd(ON);
        flaggedPromotions.setLastModTs(Timestamp.from(Instant.now()));
        this.genericService.storeObject(flaggedPromotions);

        return 1;

    }

    // The rejection method goes here

    private Integer rejectPromotion(FlaggedPromotions pEHB, BusinessCertificate bc, String approvalMemo) {

        pEHB.setStatusInd(2);
        pEHB.setTreatedDate(Timestamp.from(Instant.now()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        pEHB.setApprover(new User(bc.getLoginId()));
        pEHB.setRejectionReason(approvalMemo);
        this.genericService.storeObject(pEHB);
        return 1;

    }
    private boolean requestsAreSelected(HttpServletRequest request) {
        return !getSelectedRequests(request).isEmpty();
    }

    @RequestMapping(method = RequestMethod.POST, params = {"checkboxState", "reqList"})
    public void handleCheckboxSelectionState(
            @RequestParam("checkboxState") boolean isChecked,
            @RequestParam("reqList") List<Long> reqIdList,
            HttpServletRequest request) {
        if (reqIdList != null && reqIdList.size() > 0) {
            Set<Long> selectedIds = getSelectedRequests(request);

            if (isChecked) {
                selectedIds.addAll(reqIdList);
            } else {
                selectedIds.removeAll(reqIdList);
            }
        }
    }

    private Set<Long> getSelectedRequests(HttpServletRequest request) {
        //Check for the collection of selected ID's
        Set<Long> selectedIds = (Set<Long>) request.getSession().getAttribute(SELECTED_IDS_KEY);
        if (selectedIds == null) {

            selectedIds = new HashSet<>();

            //store it in the session
            request.getSession().setAttribute(SELECTED_IDS_KEY, selectedIds);

        }
        return selectedIds;
    }

    private void setSelectedRequests(Set<Long> selectedReqIds, List<FlaggedPromotions> currReqApprs) {
        if (selectedReqIds != null && selectedReqIds.size() > 0
                && currReqApprs != null && currReqApprs.size() > 0) {
            for (FlaggedPromotions reqAppr : currReqApprs) {
                if (selectedReqIds.contains(reqAppr.getId())) {
                    reqAppr.setRowSelected(String.valueOf(true));
                }
            }
        }
    }
}
