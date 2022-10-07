/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.HistoryService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.approval.AmAliveApproval;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/viewPendingAmAliveApprovals.do"})
@SessionAttributes(types = {BusinessEmpOVBeanInactive.class})
public class PendingAmAliveApprovalController extends BaseController {

    private final String VIEW_NAME = "employee/treatIamAliveApproval";
    private final PaycheckService paycheckService;
    private final HistoryService historyService;

    @Autowired
    public PendingAmAliveApprovalController(final PaycheckService paycheckService, final HistoryService historyService) {
        this.paycheckService = paycheckService;
        this.historyService = historyService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(@RequestParam(name = "s", required = false) Integer pSaved,
                            @RequestParam(name = "us", required = false) Integer pUnSaved,
                            @RequestParam(name = "r", required = false) Integer pRejected, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        predicates.add(CustomPredicate.procurePredicate("approvalStatusInd", 0));
        List<AmAliveApproval> displayList = new ArrayList<>();
        List<AmAliveApproval> groupLeaderList = new ArrayList<>();
        List<AmAliveApproval> empList = this.genericService.loadAllObjectsUsingRestrictions(AmAliveApproval.class, predicates, null);
        BusinessEmpOVBeanInactive container = new BusinessEmpOVBeanInactive();
        for(AmAliveApproval h : empList){
            if(h.isGroupLeader()){
                groupLeaderList.add(h);
                continue;
            }

            container.setTotalMonthlyBasic(container.getTotalMonthlyBasic() + h.getHiringInfo().getMonthlyPensionAmount());
            container.setClientId(h.getInitiator().getId());
            displayList.add(h);
        }
        container.setShowRow(SHOW_ROW);
        container.setEmployeeList(displayList);
        container.setSomeObjectList(groupLeaderList);

        if (IppmsUtils.isNotNullAndGreaterThanZero(pSaved)) {
            String approvalMessage = "Approvals Done for " + pSaved + " Pending Am Alive Approvals Successfully.";
            if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved))
                approvalMessage += " " + pUnSaved + " Approvals not processed. You extended the " + bc.getStaffTypeName() + " Am Alive Dates";

            addSaveMsgToModel(request, model, approvalMessage);

        } else if (IppmsUtils.isNotNullAndGreaterThanZero(pUnSaved)) {
            addSaveMsgToModel(request, model, " " + pUnSaved + " Approvals not processed. You extended the " + bc.getStaffTypeName() + " Am Alive Dates");
        }
        else if (IppmsUtils.isNotNullAndGreaterThanZero(pRejected)){
            addSaveMsgToModel(request, model, " " + pRejected + " Approvals not processed. You Rejected the " + bc.getStaffTypeName() + " Am Alive Approvals");

        }

        model.addAttribute("saved", true);
        model.addAttribute("miniBean", container);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }



    //approval or reject Am Alive

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @RequestParam(value = "_submit", required = false) String ok, @RequestParam(value = "_confirm", required = false) String confirm,
                                @ModelAttribute("miniBean") BusinessEmpOVBeanInactive pEHB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws InstantiationException, IllegalAccessException, HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        List<HiringInfo> allHiringInfoList = new ArrayList<>();

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        predicates.add(CustomPredicate.procurePredicate("approvalStatusInd", 0));

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
            return REDIRECT_TO_DASHBOARD;




        ConfigurationBean configurationBean = loadConfigurationBean(request);
        if (!configurationBean.isAmAliveCreatorCanApprove()) {
            if (pEHB.getClientId().equals(bc.getLoginId())) {
                result.rejectValue("", "Invalid.True", "You can not approve Am Alive you created.");
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("status", result);
                model.addAttribute("roleBean", bc);
                model.addAttribute("miniBean", pEHB);
                return VIEW_NAME;
            }

        }
        if (isButtonTypeClick(request, REQUEST_PARAM_APPROVE)) {

                if (!pEHB.isAddWarningIssued()) {
                    pEHB.setAddWarningIssued(true);
                    result.rejectValue("approvalMemo", "Warning", "You have chosen to Approve " + bc.getStaffTypeName() + " Am Alive. Please enter a value for 'Approval Memo' and Confirm.");
                    pEHB.setApprovalInd(IConstants.ON);
                    model.addAttribute("miniBean", pEHB);
                    model.addAttribute("roleBean", getBusinessCertificate(request));
                    addDisplayErrorsToModel(model, request);
                    return VIEW_NAME;
                }
            }
        if (isButtonTypeClick(request, REQUEST_PARAM_REJECT)) {

            if (!pEHB.isAddWarningIssued()) {
                pEHB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Reject " + bc.getStaffTypeName() + " Am Alive. Please enter a value for 'Rejection Memo' and Confirm.");
                pEHB.setApprovalInd(IConstants.OFF);
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }

        }


        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {
            if (IppmsUtils.isNullOrEmpty(pEHB.getApprovalMemo()) || StringUtils.trimToEmpty(pEHB.getApprovalMemo()).trim().length() < 8) {
                pEHB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "Invalid Value entered for for 'Approval Memo'");
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            } else {

                Integer processed = 0;
                Integer rejected =0;
                if (pEHB.getApprovalInd() == IConstants.ON){

                    for (AmAliveApproval approveAmAlive1 : (List<AmAliveApproval>) pEHB.getEmployeeList())
                        processed += approveStaff(approveAmAlive1, bc, pEHB.getApprovalMemo(), allHiringInfoList);
                    this.genericService.storeObjectBatch(allHiringInfoList);

                }
                else for (AmAliveApproval approveAmAlive1 : (List<AmAliveApproval>) pEHB.getEmployeeList())
                    rejected += rejectStaff(approveAmAlive1, bc, pEHB.getApprovalMemo());

                if(IppmsUtils.isNotNullOrEmpty(pEHB.getSomeObjectList()))
                    for (AmAliveApproval amAliveApproval : (List<AmAliveApproval>) pEHB.getSomeObjectList())
                        treatStaff(amAliveApproval, bc, pEHB.getApprovalMemo());

                return "redirect:viewPendingAmAliveApprovals.do?s=" + processed + "&us=0"+"&r="+rejected;
            }
            //if we get here...check if the Group Leader is available.

        }


        return REDIRECT_TO_DASHBOARD;
    }

    private Integer rejectStaff(AmAliveApproval approveAmAlive, BusinessCertificate bc, String approvalMemo) {
        approveAmAlive.setApprovalStatusInd(2);
        approveAmAlive.setApprover(new User(bc.getLoginId()));
        approveAmAlive.setApprovedDate(LocalDate.now());
        approveAmAlive.setApprovalMemo(approvalMemo);
        approveAmAlive.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        approveAmAlive.setLastModTs(LocalDate.now());
        this.genericService.saveOrUpdate(approveAmAlive);

        return 1;
    }

    private Integer approveStaff(AmAliveApproval approveAmAlive, BusinessCertificate bc, String typeName,
                                 List<HiringInfo> allHiringInfoList) throws IllegalAccessException, InstantiationException {

        //First we want to update the Am Alive Date On The Hiring Info ....
        HiringInfo hiringInfo = this.genericService.loadObjectById(HiringInfo.class, approveAmAlive.getHiringInfo().getId());
        hiringInfo.setAmAliveDate(approveAmAlive.getAmAliveDate());
        allHiringInfoList.add(hiringInfo);

        approveAmAlive.setApprovalStatusInd(ON);
        approveAmAlive.setApprover(new User(bc.getLoginId()));
        approveAmAlive.setApprovedDate(LocalDate.now());
        approveAmAlive.setApprovalMemo(typeName);
        approveAmAlive.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        approveAmAlive.setLastModTs(LocalDate.now());
        this.genericService.saveOrUpdate(approveAmAlive);

        return 1;
    }
    private void treatStaff(AmAliveApproval approveAmAlive, BusinessCertificate bc, String typeName) throws InstantiationException, IllegalAccessException {

        approveAmAlive.setApprovalStatusInd(TREATED_STATUS);
        approveAmAlive.setApprover(new User(bc.getLoginId()));
        approveAmAlive.setApprovedDate(LocalDate.now());
        approveAmAlive.setApprovalMemo(typeName);
        approveAmAlive.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime(false));
        approveAmAlive.setLastModTs(LocalDate.now());
        this.genericService.saveOrUpdate(approveAmAlive);
        String url = "requestNotification.do?arid="+approveAmAlive.getId()+"&s=4&oc="+IConstants.AM_ALIVE_INIT_URL_IND;
        NotificationService.storeNotification(bc,genericService,approveAmAlive,url,approveAmAlive.getEntityName()+" Am Alive Group Request Treated.",IConstants.AM_ALIVE_APPROVAL_CODE);

    }


}
