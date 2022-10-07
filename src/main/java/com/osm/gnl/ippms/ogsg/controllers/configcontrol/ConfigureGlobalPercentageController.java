/*
 * Copyright (c) 2021. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfig;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfigDetails;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.GlobalPercentConfigValidator;
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
import java.util.*;

@Controller
@RequestMapping({"/configureGlobalPercent.do"})
@SessionAttributes(types={GlobalPercentConfig.class})
public class ConfigureGlobalPercentageController extends BaseController {


    private final GlobalPercentConfigValidator validator;
    private final PaycheckService paycheckService;
    private final EmployeeService employeeService;

    private final String VIEW = "configcontrol/globalPercentConfigForm";

    @Autowired
    public ConfigureGlobalPercentageController(GlobalPercentConfigValidator validator, PaycheckService paycheckService, EmployeeService employeeService){
        this.validator = validator;
        this.paycheckService = paycheckService;
        this.employeeService = employeeService;
    }


    @ModelAttribute("payGroupList")
    public List<NamedEntityBean> makeYearList(HttpServletRequest request) {

        return this.paycheckService.loadObjectIdAndNameByClassAndConditions("SalaryType",1,"where selectableInd = 1 and deactivatedInd = 0 and businessClientId = :pBizIdVar", getBusinessCertificate(request).getBusinessClientInstId());
    }

    @ModelAttribute("noOfDaysList")
    public Collection<NamedEntity> getMonthList() {
        List<NamedEntity> retList = new ArrayList<>();
        for(int i = 1; i < 32; i++){
            retList.add(new NamedEntity(i));
        }
          return retList;
    }


    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        GlobalPercentConfig wHMB = new GlobalPercentConfig();
        wHMB.setHideRow(HIDE_ROW);
        wHMB.setHideRow1(HIDE_ROW);
        wHMB.setHideRow2(HIDE_ROW);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wHMB);
        model.addAttribute("action", "");
        return VIEW;
    }



    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params= {"oid","act"})
    public String setupForm(@RequestParam("oid") Long pId,@RequestParam("act") int pSaved, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        GlobalPercentConfig wHMB = genericService.loadObjectUsingRestriction(GlobalPercentConfig.class,Arrays.asList(CustomPredicate.procurePredicate("id",pId), getBusinessClientIdPredicate(request)));
        if(wHMB.isNewEntity())
            return "redirect:configureGlobalPercent.do";


        if(!wHMB.isGlobalApply()) {
                //--Get No Of Active Staffs within this range.

               List<GlobalPercentConfigDetails> wDetails = genericService.loadAllObjectsWithSingleCondition(GlobalPercentConfigDetails.class,CustomPredicate.procurePredicate("parentId", wHMB.getId()),null);
               for(GlobalPercentConfigDetails g : wDetails)
                   g.setEntryIndex(wHMB.getSalaryTypeId() + ":" + wHMB.getFromLevel() + ":" + wHMB.getToLevel());

               Collections.sort(wDetails, (globalPercentConfigDetails, t1) -> globalPercentConfigDetails.getEntryIndex().compareToIgnoreCase(t1.getEntryIndex()));


        }else {
            wHMB.setGlobalPercentStr(PayrollHRUtils.getDecimalFormat().format(wHMB.getGlobalPercentage()));

        }
        wHMB.setMode("_close");
        PaginatedPaycheckGarnDedBeanHolder p =  this.makePaginatedList(wHMB,request,false);

        addRoleBeanToModel(model, request);
        addSaveMsgToModel(request,model,"Percentage Payment Configuration Saved Successfully");
        model.addAttribute("miniBean", p.getSomeObject());
        model.addAttribute("displayList", p);
        model.addAttribute("action", wHMB.getMode());
        return VIEW;
    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                                @ModelAttribute("miniBean") GlobalPercentConfig pHMB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate businessCertificate = getBusinessCertificate(request);


        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }
        if(isButtonTypeClick(request, REQUEST_PARAM_ADD)) {
            pHMB.setMode(REQUEST_PARAM_ADD);
            validator.validate(pHMB, result,businessCertificate);
            if(result.hasErrors()) {
                model = makeModel(pHMB,request,model,true);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("status", result);
                model.addAttribute("action", "");
                return VIEW;
            }

            if(!pHMB.isGlobalApply()) {
                GlobalPercentConfigDetails globalPercentConfigDetails = new GlobalPercentConfigDetails();
                globalPercentConfigDetails.setNoOfStaffs(this.employeeService.getTotalNoOfEmployeesOnPayGroup(getBusinessCertificate(request),pHMB.getSalaryTypeId(),pHMB.getFromLevel(),pHMB.getToLevel()));
                globalPercentConfigDetails.setSalaryTypeId(pHMB.getSalaryTypeId());
                globalPercentConfigDetails.setFromLevel(pHMB.getFromLevel());
                globalPercentConfigDetails.setToLevel(pHMB.getToLevel());
                globalPercentConfigDetails.setSalaryTypeName(genericService.loadObjectById(SalaryType.class, pHMB.getSalaryTypeId()).getName());
                globalPercentConfigDetails.setPayPercentage(Double.parseDouble(PayrollHRUtils.removeCommas(pHMB.getPercentageStr())));
                globalPercentConfigDetails.setEntryIndex(pHMB.getSalaryTypeId() + ":" + pHMB.getFromLevel() + ":" + pHMB.getToLevel());
                pHMB.getConfigDetailsList().add(globalPercentConfigDetails);
                pHMB.setHideRow(SHOW_ROW);
                pHMB.setHideRow2(SHOW_ROW);

            }else{
                pHMB.setConfigDetailsList(new ArrayList<>());
                pHMB.setHideRow(HIDE_ROW);
                pHMB.setHideRow2(HIDE_ROW);
                pHMB.setHideRow1(SHOW_ROW);
            }

            PaginatedPaycheckGarnDedBeanHolder p =  this.makePaginatedList(pHMB,request,false);
            p.setSalaryTypeId(0L);
            addRoleBeanToModel(model, request);
             model.addAttribute("miniBean", p.getSomeObject());
            model.addAttribute("displayList", p);
            if(pHMB.getGlobalPercentStr().isEmpty()){
                model.addAttribute("action", REQUEST_PARAM_ADD);
            }else{
                model.addAttribute("action", REQUEST_PARAM_DONE);
            }
            return VIEW;

        }

        if (isButtonTypeClick(request,REQUEST_PARAM_DONE)){
            pHMB.setMode(REQUEST_PARAM_DONE);

            validator.validate(pHMB, result,businessCertificate);
            if(result.hasErrors()) {
                model = makeModel(pHMB,request,model,true);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("status", result);

            }else{
                if(!pHMB.isConfirmation())
                    pHMB.setConfirmation(true);
                model = makeModel(pHMB,request,model,false);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                result.rejectValue("confirmation","Confirm.Value","Please confirm this Percentage Payment Configuration.");
            }
            model.addAttribute("action", REQUEST_PARAM_DONE);
            return VIEW;

        }
        if(isButtonTypeClick(request,REQUEST_PARAM_CONFIRM)){
            validator.validate(pHMB, result,businessCertificate);
            if(result.hasErrors()) {
                model = makeModel(pHMB,request,model,true);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("status", result);
                model.addAttribute("action", REQUEST_PARAM_CONFIRM);
                return VIEW;
            }else{
                 return this.saveRecords(pHMB, businessCertificate);
            }
        }

        return "redirect:configureGlobalPercent.do";
    }

    private String saveRecords(GlobalPercentConfig pHMB, BusinessCertificate businessCertificate) {
        List<GlobalPercentConfigDetails> detailsList = pHMB.getConfigDetailsList();
        pHMB.setBusinessClientId(businessCertificate.getBusinessClientInstId());
        pHMB.setCreator(new User(businessCertificate.getLoginId()));

        if(IppmsUtils.isNotNullOrEmpty(pHMB.getGlobalPercentStr()))
        pHMB.setGlobalPercentage(Double.parseDouble(pHMB.getGlobalPercentStr()));

        if(pHMB.isEffectOnSuspensionBind())
            pHMB.setEffectOnSuspensionInd(ON);
        else
            pHMB.setEffectOnSuspensionInd(OFF);

        if(pHMB.isEffectOnTermBind())
            pHMB.setPayPerDaysEffectInd(ON);
        else
            pHMB.setPayPerDaysEffectInd(OFF);

        if(pHMB.isLoanEffectBind())
            pHMB.setLoanEffectInd(ON);
        else
            pHMB.setLoanEffectInd(OFF);

        if(pHMB.isDeductionEffectBind())
            pHMB.setDeductionEffectInd(ON);
        else
            pHMB.setDeductionEffectInd(OFF);

        Long id = this.genericService.storeObject(pHMB);
        for(GlobalPercentConfigDetails g : detailsList){
            g.setParentId(id);
            genericService.storeObject(g);
        }
        return "redirect:configureGlobalPercent.do?oid="+id+"&act=1";
    }


    private Model makeModel(GlobalPercentConfig pHMB, HttpServletRequest request,Model model, boolean error) {
        PaginatedPaycheckGarnDedBeanHolder p =  this.makePaginatedList(pHMB, request, error);

        if(!pHMB.isGlobalApply()) {
            List<SalaryInfo> wSalaryInfoList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, CustomPredicate.procurePredicate("salaryType.id", pHMB.getSalaryTypeId()),null);
            Comparator<SalaryInfo> c = Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep);

            Collections.sort(wSalaryInfoList,c);
            pHMB.setFromLevelList(wSalaryInfoList);
            pHMB.setToLevelList(wSalaryInfoList);
        }
        model.addAttribute(DISPLAY_ERRORS, BLOCK);
        // model.addAttribute("status", result);
        model.addAttribute("miniBean", p.getSomeObject());
        model.addAttribute("displayList", p);
        addRoleBeanToModel(model, request);
        return model;
    }



    private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(GlobalPercentConfig pHMB ,HttpServletRequest request, boolean errorRec ) {

        PaginationBean paginationBean = getPaginationInfo(request);


        List<GlobalPercentConfigDetails> wAllList = pHMB.getConfigDetailsList();
        if(wAllList == null){
            wAllList = new ArrayList<>();
        }
        //Make a new NamedEntity....

        List<GlobalPercentConfigDetails> wRetList;

        //Do we do the list upside down now?

        if(pHMB.getMode().equalsIgnoreCase(REQUEST_PARAM_ADD)) {
            Comparator<GlobalPercentConfigDetails> c = Comparator.comparing(GlobalPercentConfigDetails::getEntryIndex);
            Collections.sort(wAllList,c.reversed());
        }else {
            Comparator<GlobalPercentConfigDetails> c = Comparator.comparing(GlobalPercentConfigDetails::getSalaryTypeName).thenComparing(GlobalPercentConfigDetails::getFromLevel).thenComparing(GlobalPercentConfigDetails::getToLevel);
            Collections.sort(wAllList,c);
        }


        if(wAllList.size() > 10) {

            wRetList = (List<GlobalPercentConfigDetails>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 10, wAllList);

        }else {
            wRetList = wAllList;
        }

        PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        if(wAllList == null || wAllList.isEmpty()) {
            pHMB.setMapId(0);
            pHMB.setHideRow(HIDE_ROW);
            pHMB.setHideRow1(HIDE_ROW);
            pHMB.setHideRow2(HIDE_ROW);
        }
        if(!pHMB.isGlobalApply())
            makeLevelStepList(pHMB);
        else{
            pHMB.setSalaryTypeId(0L);
        }
        pHMB.setSalaryTypeId(0L);

        wPBO.setSomeObject(pHMB);


        return wPBO;
    }

    private void makeLevelStepList(GlobalPercentConfig pHMB) {

        List<SalaryInfo> wSalaryInfoList = this.paycheckService.loadSalaryInfoBySalaryTypeId(pHMB.getSalaryTypeId());
        Comparator<SalaryInfo> c = Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep);

        Collections.sort(wSalaryInfoList,c);
        pHMB.setFromLevelList(wSalaryInfoList);
        pHMB.setToLevelList(wSalaryInfoList);
    }

}
