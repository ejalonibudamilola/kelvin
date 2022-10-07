/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({"/requestNotification.do"})
public class ApprovalNotificationMultiController extends BaseController {

    private final IMenuService menuService;
    @Autowired
    public ApprovalNotificationMultiController(IMenuService menuService) {
        this.menuService = menuService;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"arid","s","oc"})
    public String allowRuleApprovalNotifier(@RequestParam("arid") Long pArid, @RequestParam("s") int pSaved,  @RequestParam("oc") int pObjCode,
                                            HttpServletRequest request, Model model) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);
        addRoleBeanToModel(model, request);
        switch (pObjCode){
            case IConstants.ALLOWANCE_RULE_APPROVAL_URL_IND:
                return ApprovalNotificationHelper.generateAllowanceRuleView(pArid,pSaved,model,genericService);
            case IConstants.ALLOWANCE_RULE_INIT_URL_IND:
                return ApprovalNotificationHelper.generateAllowanceRuleCreateView(pArid,pSaved,model,genericService,bc,request, menuService);
            case IConstants.TRANSFER_APPROVAL_URL_IND:
                return ApprovalNotificationHelper.getTransferApprovalView(pArid, model,pSaved, genericService,bc,menuService,0);
            case IConstants.TRANSFER_REQUEST_URL_IND:
                return ApprovalNotificationHelper.getTransferApprovalView(pArid, model,pSaved, genericService,bc,menuService,pSaved);
            case IConstants.STEP_INC_INIT_URL_IND:
                return ApprovalNotificationHelper.getStepIncrementInitView(pArid,bc,genericService, model,request,0,menuService);
            case IConstants.STEP_INC_APPROVAL_URL_IND:
                return ApprovalNotificationHelper.getStepIncrementInitView(pArid,bc,genericService, model,request,1,menuService);
            case IConstants.PAY_GROUP_INIT_URL_IND:
                return ApprovalNotificationHelper.getSalaryStructureApprovalView(pArid,bc,genericService, model, pSaved,menuService);
            case IConstants.AM_ALIVE_INIT_URL_IND:
                return ApprovalNotificationHelper.getAmAliveApprovalView(pArid,bc,genericService,model,pSaved,menuService);
            case IConstants.STAFF_APPROVAL_INIT_URL_IND:
                return ApprovalNotificationHelper.getStaffCreationInitView(pArid,bc,genericService,model,pSaved,menuService);

        }

        return "redirect:approvalUpdates.do";
    }
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_approve", required = false) String approve,
                                HttpServletRequest request) throws Exception {
        BusinessCertificate bc = getBusinessCertificate(request);
        if(bc.isCanApprove()){
            if(isButtonTypeClick(request,REQUEST_PARAM_APPROVE)){
                if(IppmsUtils.isNotNullOrEmpty(bc.getTargetUrl())){
                    String targetUrl = bc.getTargetUrl();
                    bc.setTargetUrl(null);
                    bc.setCanApprove(false);
                    return "redirect:"+targetUrl;
                }
            }
        }
        return "redirect:approvalUpdates.do";
    }
}
