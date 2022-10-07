/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.customreports;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.CustomReportService;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.control.entities.MaritalStatus;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.customreports.utils.CustomReportGenHelper;
import com.osm.gnl.ippms.ogsg.customreports.utils.CustomReportGenUtil;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepGenBean;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomReportObjectAttr;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/customReportGenerator.do")
public class CustomReportGeneratorController extends BaseController {


    private final CustomReportService customReportService;
    @Autowired
    public CustomReportGeneratorController(CustomReportService customReportService) {

        this.customReportService = customReportService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView customReportGenerator(HttpServletRequest request, Model pModel) throws EpmAuthenticationException, HttpSessionRequiredException {
        ModelAndView model = new ModelAndView();

        SessionManagerService.manageSession(request, pModel);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(CustomPredicate.procurePredicate("inActiveInd",OFF));
        if (bc.isPensioner()) {
            predicates.add(CustomPredicate.procurePredicate("pensionerRestricted", OFF));
        } else {
            predicates.add(CustomPredicate.procurePredicate("nonPensionerRestricted", OFF));

        }

        List<CustomReportObjectAttr> rList = this.genericService.loadAllObjectsUsingRestrictions(CustomReportObjectAttr.class, predicates, "prefDisplayName");
        rList = CustomReportGenHelper.treatList(rList, bc, true,false);

        List<BankInfo> banks = this.genericService.loadControlEntity(BankInfo.class);
        List<MdaInfo> mdaInfo = this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), "name");
        List<State> state = this.genericService.loadControlEntity(State.class);
        List<MaritalStatus> marital = this.genericService.loadControlEntity(MaritalStatus.class);
        List<EmployeeType> empList = this.genericService.loadAllObjectsWithSingleCondition(EmployeeType.class, getBusinessClientIdPredicate(request), "name");
        List<EmpGarnishmentType> garnTypeList = this.genericService.loadControlEntity(EmpGarnishmentType.class);
        List<SalaryType> salaryTypeList = this.genericService.loadControlEntity(SalaryType.class);
        List<PfaInfo> pfaList = this.genericService.loadControlEntity(PfaInfo.class);
        model.addObject("bList", banks);
        model.addObject("mList", mdaInfo);
        model.addObject("stList", state);
        model.addObject("eList", empList);
        model.addObject("rList", rList);
        model.addObject("msList", marital);
        model.addObject("gList", garnTypeList);
        model.addObject("sList", salaryTypeList);
        model.addObject("pList", pfaList);
        model.setViewName("custom_report/customReportGenerator");
        addRoleBeanToModel(pModel, request);
        return model;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"headers", "filters"})
    public String customReport(@RequestParam(value = "headers") String headersList, @RequestParam(value = "filters") String filtersList,
                                     HttpServletRequest request, HttpSession session, Model pModel) throws Exception {

        SessionManagerService.manageSession(request, pModel);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        CustomRepGenBean customRepGenBean = new CustomReportGenUtil().createCustomReportBean(genericService,bc,headersList,filtersList);

        addSessionAttribute(request,"_custRepGenBean", customRepGenBean);

        return "redirect:finalizeCustomReport.do";
    }



}

