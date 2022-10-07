/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/viewMdaPayDetails.do"})
@SessionAttributes(types={PaginatedBean.class})
public class MonthlyWageReportByOrgController extends BaseController {

    private static final String VIEW_NAME = "report/singleOrgWageSummary";

    public MonthlyWageReportByOrgController() {
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(@RequestParam(value = "mid", required = false) Long pMid,
                            @RequestParam(value = "rm", required = false) int pRunMonth,
                            @RequestParam(value = "ry", required = false) int pRunYear,
                            Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),CustomPredicate.procurePredicate("runMonth",pRunMonth),
                CustomPredicate.procurePredicate("runYear",pRunYear),
                CustomPredicate.procurePredicate("mdaDeptMap.mdaInfo.id", pMid)));

        List<AbstractPaycheckEntity>  wList  = (List<AbstractPaycheckEntity>) this.genericService.loadPaginatedObjects(IppmsUtils.getPaycheckClass(bc),predicateBuilder.getPredicates()
                    , (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
        int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,IppmsUtils.getPaycheckClass(bc));

        PaginatedBean paginatedBean = new PaginatedBean(wList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        paginatedBean.setMdaInstId(pMid);
        paginatedBean.setRunMonth(pRunMonth);
        paginatedBean.setRunYear(pRunYear);
        model.addAttribute("roleBean", bc);
        model.addAttribute("miniBean", paginatedBean);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") PaginatedBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
            return "redirect:viewWageSummary.do?rm="+pEHB.getRunMonth()+"&ry="+pEHB.getRunYear();


            int found = 0;
            for (AbstractPaycheckEntity eA : (List<AbstractPaycheckEntity>) pEHB.getList()) {

                if (IppmsUtils.isNullOrEmpty(eA.getParentObject().getEmail()) && Boolean.valueOf(eA.getRowSelected())) {
                    eA.setRowSelected("false");
                    found++;
                }
            }
            if(found > 0){
                result.rejectValue("approvalMemo", "Warning", found+" "+bc.getStaffTypeName()+" Were Found not to have Email Addresses.");
                result.rejectValue("approvalMemo", "Warning", found+" "+bc.getStaffTypeName()+" Have been deselected automatically for you.");

                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }


        if (isButtonTypeClick(request, REQUEST_PARAM_SEND_EMAIL)) {

            if (!pEHB.isAddWarningIssued()) {
                pEHB.setAddWarningIssued(true);
                result.rejectValue("approvalMemo", "Warning", "You have chosen to Send Payslips by Email to Selected " + bc.getStaffTypeName() + ". Please the 'Confirm' button to proceed..");
                result.rejectValue("approvalMemo", "Warning", "If the "+bc.getMdaTitle()+" has an Email Address, the Paylips will be zipped and sent to the Email Address.");
                result.rejectValue("approvalMemo", "Warning", "Otherwise, they will be sent separately to each "+bc.getStaffTypeName()+" with an Email Address.");
                model.addAttribute("miniBean", pEHB);
                model.addAttribute("roleBean", getBusinessCertificate(request));
                addDisplayErrorsToModel(model, request);
                return VIEW_NAME;
            }

        }
        List<Long> paycheckIdList = new ArrayList<>();
        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {

            MdaInfo mdaInfo = this.genericService.loadObjectById(MdaInfo.class,pEHB.getMdaInstId());



                Integer unprocessed = 0;
                Integer processed = 0;
                for (AbstractPaycheckEntity eA : (List<AbstractPaycheckEntity>) pEHB.getList()) {

                    if (!Boolean.valueOf(eA.getRowSelected())) {
                        ++unprocessed;
                        continue;
                    }
                    paycheckIdList.add(eA.getId());
                }
                if (paycheckIdList.size() > 0) {
                    //---First Check if the Organization has an Email Address....
                    // This is where we call Taiwo's Method....




                }
                return "redirect:viewEmpForPayApproval.do?s=" + processed + "&us=" + unprocessed;


        }


        return REDIRECT_TO_DASHBOARD;
    }
}
