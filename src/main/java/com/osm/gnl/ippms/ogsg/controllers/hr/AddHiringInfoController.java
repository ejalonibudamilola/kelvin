/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.Cadre;
import com.osm.gnl.ippms.ogsg.control.entities.MaritalStatus;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.employee.EmployeeGeneralOverviewForm;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.EmployeeDesignation;
import com.osm.gnl.ippms.ogsg.domain.hr.EmployeeStaffType;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriod;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriodDays;
import com.osm.gnl.ippms.ogsg.domain.payment.PaySchedule;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.HRReportBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.hiringinfo.HiringValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping({"/hiringForm.do","/penHireInfo.do"})
@SessionAttributes(types = {HiringInfo.class})
public class AddHiringInfoController extends BaseController {

    private final HiringValidator hiringValidator;

    private final String VIEW = "hr/hiringForm";
    @Autowired
    public AddHiringInfoController(HiringValidator hiringValidator) {
        this.hiringValidator = hiringValidator;
    }


    @ModelAttribute("payPeriod")
    public List<PayPeriod> getPayPeriod() {
        return this.genericService.loadAllObjectsWithoutRestrictions(PayPeriod.class, "name");
    }

    @ModelAttribute("maritalStatusList")
    public List<MaritalStatus> makeMaritalStatusList() {
        return this.genericService.loadAllObjectsWithoutRestrictions(MaritalStatus.class, "name");
    }

    @ModelAttribute("respList")
    public List<HRReportBean> populateRespAllowanceList(HttpServletRequest request) {
        List<HRReportBean> wRetList = new ArrayList<>();

        wRetList.add(new HRReportBean(1L, "Pensionable"));
        if (!getBusinessCertificate(request).isPensioner())
            wRetList.add(new HRReportBean(2L, "Non-Pensionable"));
        return wRetList;
    }

    @ModelAttribute("designationList")
    public List<Rank> populateDesignationList(HttpServletRequest request) {
        return this.genericService.loadAllObjectsWithSingleCondition(Rank.class, getBusinessClientIdPredicate(request),"name");
    }


    @ModelAttribute("subTypeList")
    public List<EmployeeStaffType> populateSubtypeList() {
        return this.genericService.loadAllObjectsWithoutRestrictions(EmployeeStaffType.class, "name");
    }

    @ModelAttribute("pfaList")
    public List<PfaInfo> populatePfaInfoList() {
        return this.genericService.loadAllObjectsWithoutRestrictions(PfaInfo.class, "name");
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"oid"})
    public String setupForm(@RequestParam("oid") Long eid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        //Object userId = super.getSessionId(request);
        BusinessCertificate bc = this.getBusinessCertificate(request);


        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService, eid, bc);

        if (emp.isNewEntity()) {
            throw new Exception("No " + bc.getStaffTypeName() + " Found to add hiring information");
        }

        NamedEntity namedEntity = (NamedEntity) getSessionAttribute(request, IConstants.NAMED_ENTITY);
        if (namedEntity == null) {
            namedEntity = new NamedEntity();

        }
        namedEntity.setId(eid);
        namedEntity.setName(emp.getDisplayNameWivTitlePrefixed());
        addSessionAttribute(request, IConstants.NAMED_ENTITY, namedEntity);


        HiringInfo hiringInfo =
                this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), eid));

        if (hiringInfo.isNewEntity()) {
            hiringInfo.setAbstractEmployeeEntity(emp);
            hiringInfo.setParentObjectId(eid);
            hiringInfo.setGender("M");
            if (!bc.isPensioner()) {


                if(emp.getEmployeeType().isPoliticalOfficeHolderType()){
                    hiringInfo.setPensionableInd(2);
                    hiringInfo.setPfaInfo(genericService.loadObjectWithSingleCondition(PfaInfo.class,CustomPredicate.procurePredicate("defaultInd",ON)));
                    hiringInfo.setPoliticalOfficeHolderType(true);
                }
            } else {

                hiringInfo.setPensionEndFlag(0);
                hiringInfo.setPensionableInd(0);
            }
            if (emp.getTitle().isFeminine()) {
                hiringInfo.setGender("F");
            }
            hiringInfo.setStaffInd(emp.getEmployeeType().getContractStatusInd());
            //hiringInfo.setAbstractEmployeeEntity(emp);
            hiringInfo.setHireReportFiled("Y");
            hiringInfo.setTerminateInactive("N");

            hiringInfo.setPfaInfo(new PfaInfo());

            hiringInfo.setPayPeriod(genericService.loadObjectWithSingleCondition(PayPeriod.class, CustomPredicate.procurePredicate("defaultInd", ON)));
            if(bc.isSubeb())
                hiringInfo.setStaffDesignationId(emp.getRank().getId());

            model.addAttribute("namedEntity", namedEntity);

            model.addAttribute(hiringInfo);
             model.addAttribute("roleBean", bc);
            model.addAttribute("hiringInfo", hiringInfo);

            return VIEW;
        }
          if(bc.isPensioner())
              return "redirect:editPenHireInfo.do?eid=" + eid;
        return "redirect:editHireInfo.do?eid=" + eid;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("hiringInfo") HiringInfo hireInfo, BindingResult result
            , SessionStatus status, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        Object userId = getSessionId(request);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        Long empId = hireInfo.getParentId();


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            //PayPeriod pp = this.genericService.loadObjectById(PayPeriod.class, hireInfo.getPayPeriod().getId());

            PaySchedule paySched = this.genericService.loadObjectWithSingleCondition(PaySchedule.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), empId));

            if (paySched.isNewEntity()) {
                createEmployeePaySchedule(bc, hireInfo);
                if(bc.isPensioner())
                    return "redirect:pensionerOverviewForm.do?eid=" + empId;
                return "redirect:employeeOverviewForm.do?eid=" + empId;

            }


            try {
                if (Navigator.getInstance(userId).getFromClass().isAssignableFrom(EmployeeGeneralOverviewForm.class)) {
                    if (Navigator.getInstance(userId).getFromForm() != null) {
                        Navigator.getInstance(userId).setFromClass(getClass());
                        return Navigator.getInstance(userId).getFromForm();
                    }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
            if(bc.isPensioner())
                return "redirect:pensionerOverviewForm.do?eid=" + empId;
            return "redirect:employeeOverviewForm.do?eid=" + empId;
        }
        hiringValidator.validate(hireInfo, result, bc);
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("namedEntity", getSessionAttribute(request, NAMED_ENTITY));
            model.addAttribute(hireInfo);
            addRoleBeanToModel(model, request);
            return VIEW;
        }
        if (hireInfo.getConfirmDate() == null/* && (!hireInfo.isConfirmEmployeeWarning())*/) {
            LocalDate today = LocalDate.now();

            LocalDate hireDate = hireInfo.getHireDate();

            LocalDate twoYearsBeforeToday =  hireInfo.getHireDate().plusYears(2L);
            hireInfo.setConfirmDate(twoYearsBeforeToday);

           /* if ((hireDate.isBefore(twoYearsBeforeToday)) && (!hireInfo.isConfirmEmployeeWarning())) {

                hireInfo.setConfirmEmployeeWarning(true);

               result.rejectValue("confirmDate", "Warning.Confirm",  bc.getStaffTypeName()+" " + hireInfo.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed() + " Should have been confirmed");

                model.addAttribute("status", result);
                model.addAttribute("namedEntity", getSessionAttribute(request, NAMED_ENTITY));
                model.addAttribute(hireInfo);
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                return VIEW;
            }*/
        }


        hireInfo.setBusinessClientId(hireInfo.getAbstractEmployeeEntity().getBusinessClientId());
        ConfigurationBean configurationBean = loadConfigurationBean(request);
        if(!bc.isPensioner()) {
            hireInfo.setExpectedDateOfRetirement(PayrollBeanUtils.calculateExpDateOfRetirement(hireInfo.getBirthDate(), hireInfo.getHireDate(), configurationBean,bc));
            hireInfo.setEmployee(new Employee(hireInfo.getParentId()));
        }else{
            hireInfo.setPensioner(new Pensioner(hireInfo.getParentId()));
             if(configurationBean.isUseIAmAlive())
               hireInfo.setAmAliveDate(LocalDate.of(hireInfo.getBirthDate().getYear()+configurationBean.getIamAlive(),hireInfo.getBirthDate().getMonthValue(), hireInfo.getBirthDate().getDayOfMonth()));
        }
        hireInfo.setLastModBy(new User(bc.getLoginId()));
        hireInfo.setLastModTs(Timestamp.from(Instant.now()));
        hireInfo.setCreatedBy(new User(bc.getLoginId()));
        //-Check Pensionable Status...
        if(hireInfo.getAbstractEmployeeEntity().getEmployeeType().isContractStaff() || hireInfo.getAbstractEmployeeEntity().getEmployeeType().isPoliticalOfficeHolder()){
            hireInfo.setPensionableInd(1);
        }else{
            hireInfo.setPensionableInd(hireInfo.getPensionableInd() - 1);
        }
        if(!bc.isSubeb()){
            hireInfo.setEmployeeType(null);
            hireInfo.setEmployeeDesignation(null);
        }else{
            hireInfo.setEmployeeStaffType(new EmployeeStaffType(hireInfo.getStaffTypeId()));

        }
        this.genericService.saveObject(hireInfo);

        createEmployeePaySchedule(bc, hireInfo);

        if (bc.isPensioner())
            return "redirect:calcPensionAndGratuity.do?eid="+ empId;
        return "redirect:paymentInfoForm.do?oid=" + empId;


    }


    private void createEmployeePaySchedule(BusinessCertificate pBc, HiringInfo hiringInfo) throws InstantiationException, IllegalAccessException {

        PaySchedule p = new PaySchedule();
        if (pBc.isPensioner())
            p.setPensioner(hiringInfo.getPensioner());
        else
            p.setEmployee(hiringInfo.getEmployee());

        p.setPayPeriod(hiringInfo.getPayPeriod());

        p.setPayPeriodDays(genericService.loadObjectWithSingleCondition(PayPeriodDays.class, CustomPredicate.procurePredicate("defaultInd", ON)));
       /* p.setPeriodDayInstId2(b.getPeriodDayInstId2());
        p.setWorkPeriodDays(b.getWorkPeriodDays());
        p.setWorkPeriodDays2(b.getWorkPeriodDays2());
        p.setWorkPeriodEffective(b.getWorkPeriodEffective());
        p.setWorkPeriodEffective2(b.getWorkPeriodEffective2());
        p.setWorkPeriodInstId(b.getWorkPeriodInstId());
        p.setWorkPeriodInstId2(b.getWorkPeriodInstId2());*/
        this.genericService.saveObject(p);


    }

}
