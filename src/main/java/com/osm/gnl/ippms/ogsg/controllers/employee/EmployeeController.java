/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

/**
 * @Author - Mustola
 */
package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.control.entities.Religion;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.employee.service.EmployeeControllerService;
import com.osm.gnl.ippms.ogsg.domain.approval.EmployeeApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.HRReportBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.employee.EmployeeValidator;
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
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/employeeForm.do","/pensionerForm.do"})
@SessionAttributes({"employee"})
public class EmployeeController extends BaseController {

    private final byte[] imgContent = null;

    private final String header = null;



    private final EmployeeValidator validator;

    private final String VIEW_NAME = "employee/casp/employeeForm";
    @Autowired
    public EmployeeController(EmployeeValidator validator) {

        this.validator = validator;
    }

    @ModelAttribute(value = "cities")
    public List<City> populateCities() {
        return this.genericService.loadAllObjectsWithoutRestrictions(City.class, "name");
    }

    @ModelAttribute(value = "empTypes")
    public List<EmployeeType> populateEmployeeType(HttpServletRequest httpServletRequest) {

        return this.genericService.loadAllObjectsWithSingleCondition(EmployeeType.class,
                super.getBusinessClientIdPredicate(httpServletRequest), "name");
    }

    @ModelAttribute("religionList")
    public List<Religion> populateReligionList() {
        return this.genericService.loadAllObjectsWithoutRestrictions(Religion.class, "name");
    }


    @ModelAttribute("titleList")
    public List<Title> populateTitleList() {
        return this.genericService.loadAllObjectsWithoutRestrictions(Title.class, "name");
    }
    @ModelAttribute("statesOfOriginList")
    public List<State> populateStateOfOriginList() {
        return this.genericService.loadAllObjectsWithoutRestrictions(State.class, "name");
    }

    @ModelAttribute("schoolList")
    public List<SchoolInfo> populateSchoolList(HttpServletRequest request) {

        return this.genericService.loadAllObjectsWithSingleCondition(SchoolInfo.class, super.getBusinessClientIdPredicate(request), "name");
    }
    @ModelAttribute("entrantList")
    public List<HRReportBean> populateEntrantList(HttpServletRequest request) {
        List<HRReportBean> wRetList = new ArrayList<>();
        wRetList.add(new HRReportBean(1, "New Entrant"));
        wRetList.add(new HRReportBean(2, "Existing Pensioner"));
        return wRetList;
    }
    @ModelAttribute("entryMonthList")
    public List<HRReportBean> populateEntryMonth(HttpServletRequest request) throws InstantiationException, IllegalAccessException {
        PayrollFlag payrollFlag = genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));
        int startMonth = 0;
        if(!payrollFlag.isNewEntity()){
            startMonth = payrollFlag.getApprovedMonthInd();
        }else{
            startMonth = LocalDate.now().getMonthValue();
        }
        if(startMonth == 12)
            startMonth = 1;
        List<HRReportBean> wRetList = new ArrayList<>();
        wRetList.add(new HRReportBean(startMonth, PayrollBeanUtils.getMonthNameFromInteger(startMonth)));
        wRetList.add(new HRReportBean(startMonth + 1, PayrollBeanUtils.getMonthNameFromInteger(startMonth + 1)));
        return wRetList;
    }
    @RequestMapping(method = {RequestMethod.GET})
    public String setupEmployeeForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        AbstractEmployeeEntity employee = (AbstractEmployeeEntity) getSessionAttribute(request, EMP_SKEL);

        if (employee == null) {
            if(bc.isPensioner())
                return "redirect:createNewPensioner.do";
            return "redirect:setUpNewEmployee.do";
        }

        employee.setBusinessClientId(bc.getBusinessClientInstId());
        addModelValues(model, request, bc, employee);
        model.addAttribute("employee", employee);

        return VIEW_NAME;

    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("employee") AbstractEmployeeEntity employee, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        Object userId = getSessionId(request);
        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            removeSessionAttribute(request, IConstants.EMP_SKEL);

            if (Navigator.getInstance(userId).getFromForm() != null && Navigator.getInstance(userId).getFromForm().equalsIgnoreCase("redirect:busEmpOverviewForm.do")) {
                return Navigator.getInstance(userId).getFromForm();
            }
            return REDIRECT_TO_DASHBOARD;
        }

        validator.validate(employee, result, bc, loadConfigurationBean(request));

        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            addModelValues(model, request, bc, employee);
            model.addAttribute("status", result);
            return VIEW_NAME;
        }


        employee = EmployeeControllerService.storeInformation(employee, bc, genericService, request);

        //at this point. Create Employee Approval Notification.
       /* EmployeeApproval employeeApproval = this.genericService.loadObjectUsingRestriction(EmployeeApproval.class, Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),employee.getId())));
        if(!employeeApproval.isNewEntity()) {
            if(IppmsUtils.isNotNullOrEmpty(employeeApproval.getEmployeeId())) {
                employeeApproval.setEmployeeId(employeeApproval.getAbstractEmployeeEntity().getEmployeeId());
                employeeApproval.setEntityId(employeeApproval.getAbstractEmployeeEntity().getId());
                employeeApproval.setEntityName(employeeApproval.getAbstractEmployeeEntity().getDisplayName());
                employeeApproval.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(bc.getLoginId()));
                employeeApproval.setInitiatedDate(LocalDate.now());
                genericService.saveObject(employeeApproval);
            }
            NotificationService.storeNotification(bc, genericService, employeeApproval, "requestNotification.do?arid=" + employeeApproval.getId() + "&s=1&oc=" + IConstants.STAFF_APPROVAL_CODE, bc.getStaffTypeName() + " Approval Request", IConstants.STAFF_APPROVAL_INIT_URL_IND);
        }
*/
        NamedEntity nE = new NamedEntity();

        nE.setId(employee.getId());
        nE.setName(employee.getDisplayNameWivTitlePrefixed());
        nE.setMode("create");

        if(employee.isPensioner()){
            bc.setParentClientId(((Pensioner)employee).getParentBusinessClientId());
            addSessionAttribute(request, IppmsEncoder.getCertificateKey(),bc);
        }

        addSessionAttribute(request, IConstants.NAMED_ENTITY, nE);

        removeSessionAttribute(request, IConstants.EMP_SKEL);

        addSessionAttribute(request, "ne", nE);
        Navigator.getInstance(userId).setFromForm(null);
        if(bc.isPensioner())
            return "redirect:penHireInfo.do?oid=" + employee.getId();
        return "redirect:hiringForm.do?oid=" + employee.getId();
    }





    private void addModelValues(Model model, HttpServletRequest request, BusinessCertificate bc, AbstractEmployeeEntity employee) throws IllegalAccessException, InstantiationException {
        addPageTitle(model, bc.getStaffTypeName() + " Form");
        addMainHeader(model, "Add New " + bc.getStaffTypeName());
        addRoleBeanToModel(model, request);
        if(bc.isPensioner()){
            model.addAttribute("rankTypes", this.genericService.loadAllObjectsWithSingleCondition(Rank.class, CustomPredicate.procurePredicate("businessClientId", ((Pensioner)employee).getParentBusinessClientId()), "name"));
        }else{
            model.addAttribute("rankTypes", this.genericService.loadAllObjectsWithSingleCondition(Rank.class, getBusinessClientIdPredicate(request), "name"));

        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(employee.getCityId())) {
            City city = genericService.loadObjectById(City.class, employee.getCityId());
            employee.setStateInstId(city.getState().getId());
            List<State> stateList = new ArrayList<>();
            stateList.add(city.getState());

            model.addAttribute("statesList", stateList);
        }
        if(IppmsUtils.isNotNullAndGreaterThanZero(employee.getStateOfOriginId())){
            List<LGAInfo> lgaInfoList = genericService.loadAllObjectsWithSingleCondition(LGAInfo.class, CustomPredicate.procurePredicate("state.id",employee.getStateOfOriginId()), "name");
            model.addAttribute("LGAList", lgaInfoList);
            model.addAttribute("statesOfOriginList",genericService.loadAllObjectsWithoutRestrictions(State.class,"name"));
        }

    }

}
