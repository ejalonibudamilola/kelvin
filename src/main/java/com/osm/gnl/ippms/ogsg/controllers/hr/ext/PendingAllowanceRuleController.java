/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.TransferService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.approval.AllowanceRuleApproval;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/viewPendingAllowanceRule.do"})
@SessionAttributes(types={PaginatedBean.class})
public class PendingAllowanceRuleController extends BaseController {


    private final TransferService transferService;
    private final IMenuService menuService;
    private final PaycheckService paycheckService;

    private final int pageLength = 20;
    private final String VIEW = "rules/viewPendingAllowanceApprovalsForm";

    @Autowired
    public PendingAllowanceRuleController(TransferService transferService, IMenuService menuService, PaycheckService paycheckService) {
        this.transferService = transferService;
        this.menuService = menuService;
        this.paycheckService = paycheckService;
    }


    @ModelAttribute("userList")
    public List<User> populateUsersList(HttpServletRequest request) {
        return this.transferService.loadCurrentViewActiveLogin(super.getBusinessCertificate(request));
    }

    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);


        List<AllowanceRuleApproval> empList = this.genericService.loadPaginatedObjects(AllowanceRuleApproval.class,
                Arrays.asList(CustomPredicate.procurePredicate("approvedDate", null, Operation.IS_NULL),
                        CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())),
                (paginationBean.getPageNumber() - 1) * this.pageLength,
                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());


        int wNoOfElements = this.transferService.getTotalNoOfActiveAllowanceApprovals(bc.getBusinessClientInstId());

        PaginatedBean paginatedBean = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength,
                wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        if( bc.isSuperAdmin())
            paginatedBean.setEditMode(true);

        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewPendingAllowanceRule.do");
        return makeModelAndReturnView(model,request,bc,paginatedBean,null);

    }


    @RequestMapping(method={RequestMethod.GET} ,params={"s","us" })
    public String setupForm(@RequestParam("s") Integer pSaved,
                            @RequestParam("us") Integer pUnSaved,Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);


        List<AllowanceRuleApproval> empList = this.genericService.loadPaginatedObjects(AllowanceRuleApproval.class,
                Arrays.asList(CustomPredicate.procurePredicate("approvedDate", null,Operation.IS_NULL)),
                (paginationBean.getPageNumber() - 1) * this.pageLength,
                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

         int wNoOfElements = this.transferService.getTotalNoOfActiveAllowanceApprovals(bc.getBusinessClientInstId());

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength,
                wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        String action = "Approved";
        if(model.getAttribute("rejection") != null) {
            action = "Rejected";
        }

        if (IppmsUtils.isNotNullAndGreaterThanZero(pSaved)) {
            String approvalMessage = "" + pSaved + " Pending PayGroup Allowance Rule "+action+" Successfully.";
            if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved))
                approvalMessage += " " + pUnSaved + " PayGroup Allowance Rule Approvals not processed.";

            addSaveMsgToModel(request, model, approvalMessage);

        } else if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved)) {
            addSaveMsgToModel(request, model, " " + pUnSaved + " PayGroup Allowance not processed.");
        }
        wPHDB.setShowLink(true);
        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewPendingAllowanceRule.do");
        return makeModelAndReturnView(model,request,bc,wPHDB,null);

    }
    @RequestMapping(method={RequestMethod.GET},params={"eid","ln","uid"})
    public String setupForm(@RequestParam("eid") Long pEmpId,
                            @RequestParam("ln") String pLastName,
                            @RequestParam("uid") Long pUid,Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);


        if(pEmpId.equals(0L))
            pEmpId = null;

        if(pUid.equals(0L))
            pUid = null;

        //Now Get the list of Id's with the Last Name...
        List<Long> wEmpIds =  null;
        if(!StringUtils.trimToEmpty(pLastName).equals(EMPTY_STR)){
            wEmpIds = this.transferService.loadHiringInfoIdsByStringValue(bc,pLastName);
            if(wEmpIds.isEmpty() )
                wEmpIds =  null;
        }

        List<CustomPredicate> customPredicates = new ArrayList<>();
        if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId))
            customPredicates.add(CustomPredicate.procurePredicate("hiringInfo.id", pEmpId));
        if(IppmsUtils.isNotNullAndGreaterThanZero(pUid))
            customPredicates.add(CustomPredicate.procurePredicate("initiator.id", pUid));
        if(IppmsUtils.isNotNullOrEmpty(wEmpIds))
            customPredicates.add(CustomPredicate.procurePredicate("hiringInfo.id", (Comparable)wEmpIds));
        if(IppmsUtils.isNotNullOrEmpty(pLastName))
            customPredicates.add(CustomPredicate.procurePredicate("lastName", pLastName, Operation.LIKE));

        List<AllowanceRuleApproval> empList = this.genericService.loadPaginatedObjects(AllowanceRuleApproval.class,  customPredicates,
                (paginationBean.getPageNumber() - 1) * this.pageLength,
                this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

         int wNoOfElements = this.transferService.getTotalNoOfActiveAllowanceApprovals(bc,pEmpId,wEmpIds,pUid);

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength,
                wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        if(IppmsUtils.isNotNullAndGreaterThanZero(pUid))
            wPHDB.setId(pUid);
        else
            wPHDB.setId(0L);
        if(!StringUtils.trimToEmpty(pLastName).equals(EMPTY_STR)){
            wPHDB.setLastName(pLastName);
        }
        if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)){
            wPHDB.setOgNumber(loadHiringInfoById(request,bc,pEmpId).getAbstractEmployeeEntity().getEmployeeId());
        }
        if( bc.isSuperAdmin())
            wPHDB.setEditMode(true);

        return makeModelAndReturnView(model,request,bc,wPHDB,null);
    }

    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_update", required=false) String pUpd,
                                @ModelAttribute("miniBean") PaginatedBean pLPB, BindingResult result, SessionStatus
                                        status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        AllowanceRuleApproval singleEntry = null;

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE)) {
            Long empId = 0L;
            String wLastName = "";
            Long pUid = 0L;
            if (!StringUtils.trimToEmpty(pLPB.getOgNumber()).equals(EMPTY_STR)) {

                AbstractEmployeeEntity abstractEmployeeEntity = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(
                        CustomPredicate.procurePredicate("employeeId", pLPB.getOgNumber()), getBusinessClientIdPredicate(request)));

                if (abstractEmployeeEntity.isNewEntity()) {
                    result.rejectValue("", "InvalidValue", "No " + bc.getStaffTypeName() + " found with " + bc.getStaffTitle() + " " + pLPB.getOgNumber());
                    return makeModelAndReturnView(model,request,bc,pLPB,result);
                }
                HiringInfo hiringInfo = loadHiringInfoByEmpId(request,bc,abstractEmployeeEntity.getId());
                empId = hiringInfo.getId();
            }
            if (!StringUtils.trimToEmpty(pLPB.getLastName()).equals(EMPTY_STR)) {
                wLastName = pLPB.getLastName();
            }
            if (pLPB.getId() > 0)
                pUid = pLPB.getId();

            Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewPendingAllowanceRule.do?eid=" + empId + "&ln=" + wLastName + "&uid=" + pUid);
            return "redirect:viewPendingAllowanceRule.do?eid=" + empId + "&ln=" + wLastName + "&uid=" + pUid;

        }
        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {

            pLPB.setMemoType("Approval");
            if (!pLPB.isAddWarningIssued()) {
                pLPB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Approve Selected " + bc.getStaffTypeName() + " PayGroup Allowance Rules. Please enter a value for 'Approval Memo' and Confirm.");
                return makeModelAndReturnView(model,request,bc,pLPB,result);
            }

        }
        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {
            if (IppmsUtils.isNullOrEmpty(pLPB.getApprovalMemo()) || StringUtils.trimToEmpty(pLPB.getApprovalMemo()).trim().length() < 8) {
                pLPB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Approval Memo'");
               return makeModelAndReturnView(model,request,bc,pLPB,result);
            } else {
                Integer unprocessed = 0;
                Integer processed = 0;
                for (AllowanceRuleApproval allowanceRuleApproval : (List<AllowanceRuleApproval>) pLPB.getObjectList()) {

                    if (!Boolean.valueOf(allowanceRuleApproval.getRowSelected())) {
                        ++unprocessed;
                        continue;
                    }
                    processed += doWork(allowanceRuleApproval, bc, pLPB.getApprovalMemo(),pLPB.isRejection());
                    if(singleEntry == null)
                       singleEntry = allowanceRuleApproval;
                }

                return "redirect:viewPendingAllowanceRule.do?s=" + processed + "&us=" + unprocessed;

            }
        }

        if (isButtonTypeClick(request,REQUEST_PARAM_REJECT)) {

            //if we get here...check if Rejection Reason is set..
            if (!pLPB.isAddWarningIssued()) {
                pLPB.setAddWarningIssued(true);
                pLPB.setMemoType("Rejection");
                pLPB.setRejection(true);

                result.rejectValue("", "warning", "Please enter a Rejection Reason.");
                return makeModelAndReturnView(model,request,bc,pLPB,result);

            }
            if (IppmsUtils.isNullOrEmpty(pLPB.getApprovalMemo()) || StringUtils.trimToEmpty(pLPB.getApprovalMemo()).trim().length() < 8) {
                pLPB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Rejection Memo'");

                return makeModelAndReturnView(model,request,bc,pLPB,result);
            } else {
                Integer unprocessed = 0;
                Integer processed = 0;
                for (AllowanceRuleApproval allowanceRuleApproval : (List<AllowanceRuleApproval>) pLPB.getObjectList()) {

                    if (!Boolean.valueOf(allowanceRuleApproval.getRowSelected())) {
                        ++unprocessed;
                        continue;
                    }
                    processed += doWork(allowanceRuleApproval, bc, pLPB.getApprovalMemo(), pLPB.isRejection());
                    if(singleEntry == null)
                       singleEntry = allowanceRuleApproval;
                }

                model.addAttribute("rejection",true);
                return "redirect:viewPendingAllowanceRule.do?s=" + unprocessed + "&us=" + processed;

            }
        }




        return REDIRECT_TO_DASHBOARD;
    }
     private Integer doWork(AllowanceRuleApproval allowanceApproval, BusinessCertificate bc, String approvalMemo, boolean rejection) throws Exception {
         AllowanceRuleMaster allowanceRuleMaster = allowanceApproval.getAllowanceRuleMaster();
        if(rejection) {
            allowanceApproval.setApprovalStatusInd(2);

        } else {
            allowanceApproval.setApprovalStatusInd(1);
        }
         allowanceRuleMaster.setActiveInd(allowanceApproval.getApprovalStatusInd());
         allowanceApproval.setApprovedDate(LocalDate.now());
        allowanceApproval.setApprover(new User(bc.getLoginId()));
        allowanceApproval.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        allowanceApproval.setApprovalMemo(approvalMemo);

        this.genericService.saveObject(allowanceApproval);
        this.genericService.saveObject(allowanceRuleMaster);
         String url = "approveAllowanceRule.do?arid=" + allowanceApproval.getId()+ "&s="+allowanceApproval.getApprovalStatusInd();
         NotificationService.storeNotification(bc, genericService, allowanceApproval, url, bc.getStaffTypeName()+" Allowance Rule Approval", ALLOWANCE_RULE_CODE);
        return 1;
    }
    private String makeModelAndReturnView(Model model, HttpServletRequest request, BusinessCertificate bc, PaginatedBean pEHB, BindingResult result) {

        if (result != null) {
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
        }
        model.addAttribute("miniBean", pEHB);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

}
