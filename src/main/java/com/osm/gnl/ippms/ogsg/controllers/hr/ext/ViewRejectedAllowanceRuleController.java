/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.AllowanceRuleApproval;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/viewRejectedAllowanceRules.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewRejectedAllowanceRuleController extends BaseController {


    private final int pageLength = 20;
    private final String VIEW_NAME = "rules/viewRejectedAllowanceApprovalsForm";

    public ViewRejectedAllowanceRuleController() {}

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);
        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.addAll(Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("approvalStatusInd",2),
                CustomPredicate.procurePredicate("initiator.id",bc.getLoginId())));
        List<AllowanceRuleApproval> empList = this.genericService.loadPaginatedObjects(AllowanceRuleApproval.class,predicates,(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder()
                , paginationBean.getSortCriterion());

         int wNoOfElements = this.genericService.getTotalPaginatedObjects(AllowanceRuleApproval.class,predicates).intValue();

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wPHDB);

        return VIEW_NAME;
    }

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                 @ModelAttribute("miniBean") PaginatedBean pHADB, BindingResult result, SessionStatus
                                        status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        return Navigator.getInstance(getSessionId(request)).getFromForm();
    }


}
