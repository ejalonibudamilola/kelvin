/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.SuspensionTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({"/editEmployeeType.do"})
@SessionAttributes({"miniBean"})
public class AddEmployeeTypeController extends BaseController{


    @Autowired
    private SuspensionTypeValidator validator;

    private final String VIEW = "configcontrol/employeeTypeForm";

    @ModelAttribute("codeList")
    protected List<NamedEntity> makeTypeCodes(HttpServletRequest request) {
        List<NamedEntity> namedEntityList = new ArrayList<>();

        if(!getBusinessCertificate(request).isPensioner()) {
            namedEntityList.add(new NamedEntity(1,"Regular","Use for Regular Staffs"));
            namedEntityList.add(new NamedEntity(2, "Political Appointees", "Use for Political Appointees"));
            namedEntityList.add(new NamedEntity(3, "HOS/PS/GMs/AGs", "Use for HOS/PS/GMs/AGs"));
            namedEntityList.add(new NamedEntity(4, "Contract Staff", "Use for Regular Contract Staffs"));
            namedEntityList.add(new NamedEntity(5, "NYSC Type Contract Staff", "Use for Single Tenured Contract Staffs, i.e., their contracts are not renewable."));
            Collections.sort(namedEntityList, Comparator.comparing(NamedEntity::getId));
        }
        return namedEntityList;
    }

    public AddEmployeeTypeController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        EmployeeType wEDT = new EmployeeType();

        model.addAttribute("displayList", this.makeEmployeeTypeList(request, false));
        model.addAttribute("listSize", (this.makeEmployeeTypeList(request, false)).size());
        model.addAttribute("displayTitle", "Create New "+bc.getStaffTypeName()+" Type");
        model.addAttribute("miniBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"cid"})
    public String setupForm(@RequestParam("cid") Long pEduId, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        EmployeeType wEDT = this.genericService.loadObjectWithSingleCondition(EmployeeType.class, CustomPredicate.procurePredicate("id", pEduId));


        if (!wEDT.getBusinessClientId().equals(bc.getBusinessClientInstId()))
            return "redirect:editEmployeeType.do";

        wEDT.setEditMode(true);

        model.addAttribute("displayList", this.makeEmployeeTypeList(request, false));
        model.addAttribute("listSize", (this.makeEmployeeTypeList(request, false)).size());
        model.addAttribute("displayTitle", "Edit "+bc.getStaffTypeName()+" Type");
        model.addAttribute("miniBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"bid", "s"})
    public String setupForm(@RequestParam("bid") Long pObjectId, @RequestParam("s") int pSaved, Model model,
                            HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        EmployeeType wEDT = this.genericService.loadObjectWithSingleCondition(EmployeeType.class,
                CustomPredicate.procurePredicate("id", pObjectId));

        if (!wEDT.getBusinessClientId().equals(bc.getBusinessClientInstId()))
            return "redirect:editEmployeeType.do";


        String wMessage;

        if (pSaved == 1) {
            wMessage = bc.getStaffTypeName()+" Type '" + wEDT.getName() + "' edited successfully.";
        } else {
            wMessage = bc.getStaffTypeName()+" Type '" + wEDT.getName() + "' created successfully.";
        }

        model.addAttribute(IConstants.SAVED_MSG, wMessage);
        wEDT = new EmployeeType();

        model.addAttribute("displayList", this.makeEmployeeTypeList(request, true));
        model.addAttribute("listSize", (this.makeEmployeeTypeList(request, true)).size());
        model.addAttribute("displayTitle", "Create/Edit "+bc.getStaffTypeName()+" Type");
        model.addAttribute("saved", true);
        model.addAttribute("miniBean", wEDT);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") EmployeeType pEHB, BindingResult result, SessionStatus status,
                                org.springframework.ui.Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }

        validator.validateForEmployeeType(pEHB, result, bc);
        if (result.hasErrors()) {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            model.addAttribute("roleBean", bc);
            return VIEW;
        }

        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        int wEditType = 1;
        if (!pEHB.isEditMode()) {
            wEditType = 2;
            pEHB.setCreatedBy(new User(bc.getLoginId()));
            pEHB.setBusinessClientId(bc.getBusinessClientInstId());
        }
        if(bc.isPensioner()){
            pEHB.setContractStatusInd(0);
            pEHB.setPoliticalInd(0);
            pEHB.setRenewableInd(0);
            pEHB.setEmployeeTypeCode(1);
        }else {
            switch (pEHB.getEmployeeTypeCode()) {
                case 1:
                    pEHB.setContractStatusInd(0);
                    pEHB.setPoliticalInd(0);
                    pEHB.setRenewableInd(0);
                    break;
                case 2:
                    pEHB.setContractStatusInd(0);
                    pEHB.setPoliticalInd(1);
                    pEHB.setRenewableInd(0);
                    break;
                case 3:
                    pEHB.setContractStatusInd(0);
                    pEHB.setPoliticalInd(0);
                    pEHB.setRenewableInd(0);
                    pEHB.setEmployeeTypeCode(3);
                    break;
                case 4:
                    pEHB.setEmployeeTypeCode(1);
                    pEHB.setContractStatusInd(1);
                    pEHB.setPoliticalInd(0);
                    pEHB.setRenewableInd(1);
                    break;
                case 5:
                    pEHB.setEmployeeTypeCode(1);
                    pEHB.setContractStatusInd(1);
                    pEHB.setPoliticalInd(0);
                    pEHB.setRenewableInd(0);
                    break;
            }
        }
        this.genericService.saveObject(pEHB);

        return "redirect:editEmployeeType.do?bid=" + pEHB.getId() + "&s=" + wEditType;
    }

//    private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(HttpServletRequest request, boolean pSaved) {
//
//        BaseController.PaginationBean paginationBean = this.getPaginationInfo(request);
//        BusinessCertificate bc = this.getBusinessCertificate(request);
//
//        List<EmployeeType> wAllList = null;
//        List<EmployeeType> wRetList = null;
//
//        //Do we do the list upside down now?
//        wAllList = this.genericService.loadAllObjectsWithSingleCondition(EmployeeType.class, getBusinessClientPredicate(request), "name");
//        if (pSaved) {
//            Collections.sort(wAllList, Comparator.comparing(EmployeeType::getId).reversed());
//        }
//
//
//        if (wAllList.size() > 5) {
//
//            wRetList = (List<EmployeeType>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 20, wAllList, EmployeeType.class);
//
//        } else {
//            wRetList = wAllList;
//        }
//
//        PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());
//
//        return wPBO;
//    }

    private List<EmployeeType> makeEmployeeTypeList(HttpServletRequest request, boolean pSaved) {
        List<EmployeeType> wAllList;
        List<EmployeeType> wRetList;

        //Do we do the list upside down now?
        wAllList = this.genericService.loadAllObjectsWithSingleCondition(EmployeeType.class, getBusinessClientIdPredicate(request), "name");
        if (pSaved) {
            Collections.sort(wAllList, Comparator.comparing(EmployeeType::getId).reversed());
        }

        wRetList = wAllList;

        return wRetList;
    }


}
