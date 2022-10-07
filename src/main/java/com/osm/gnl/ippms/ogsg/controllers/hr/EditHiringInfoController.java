/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.MaritalStatus;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.EmployeeStaffType;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriod;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriodDays;
import com.osm.gnl.ippms.ogsg.domain.payment.PaySchedule;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.HRReportBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.hiringinfo.HiringValidator;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping({"/editHireInfo.do","/editPenHireInfo.do"})
@SessionAttributes(types={HiringInfo.class})
public class EditHiringInfoController extends BaseController {


    private final HiringValidator hiringValidator;
    private final PaycheckService paycheckService;
    
    private final String VIEW_NAME = "hr/hiringInfoEditForm";
    @Autowired
    public EditHiringInfoController(HiringValidator hiringValidator, PaycheckService paycheckService)
    {

        this.hiringValidator = hiringValidator;
        this.paycheckService = paycheckService;
    }
    @ModelAttribute("payPeriod")
    public List<PayPeriod> getPayPeriod() {
        return this.genericService.loadAllObjectsWithoutRestrictions(PayPeriod.class,"name");
    }

    @ModelAttribute("maritalStatusList")
    public List<MaritalStatus> makeMaritalStatusList() {
        return this.genericService.loadAllObjectsWithoutRestrictions(MaritalStatus.class,"name");
    }

    @ModelAttribute("respList")
    public Collection<HRReportBean> populateRespAllowanceList(HttpServletRequest request) {
        List<HRReportBean> wRetList = new ArrayList<>();

        wRetList.add(new HRReportBean(0L, "Pensionable"));
        if(!getBusinessCertificate(request).isPensioner())
            wRetList.add(new HRReportBean(1L, "Non-Pensionable"));
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
        return this.genericService.loadAllObjectsWithoutRestrictions(PfaInfo.class,"name");
    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid"})
    public String setupForm(@RequestParam("eid") Long eid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService,eid,bc);

        if ( wEmp.isNewEntity()) {
            throw new Exception("No "+bc.getStaffTypeName()+" Found to add hiring information");
        }
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
        if (ne == null) {
            ne = new NamedEntity();

            ne.setId(wEmp.getId());
            ne.setName(wEmp.getDisplayNameWivTitlePrefixed());
        }

        HiringInfo hiringInfo =
                this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), eid));

        if (hiringInfo.isNewEntity()) {
            hiringInfo.setDisplayStatus("false");
            if(!bc.isPensioner()) {
                hiringInfo.setEmployee(new Employee(eid));
                AbstractEmployeeEntity employeeEntity = IppmsUtils.loadEmployee(genericService,eid,bc);
                if(employeeEntity.getEmployeeType().isPoliticalOfficeHolderType()){
                    hiringInfo.setPensionableInd(1);
                    hiringInfo.setPfaInfo(genericService.loadObjectWithSingleCondition(PfaInfo.class,CustomPredicate.procurePredicate("defaultInd",ON)));
                    hiringInfo.setPoliticalOfficeHolderType(true);
                }
            } else {
                hiringInfo.setPensioner(new Pensioner(eid));
                hiringInfo.setPensionEndFlag(0);
                hiringInfo.setPensionableInd(0);
            }
            if (wEmp.getTitle().isFeminine()) {
                hiringInfo.setGender("F");
                hiringInfo.setGenderStr("Female");
            }


            hiringInfo.setPoliticalOfficeHolderType( wEmp.getEmployeeType().isPoliticalOfficeHolderType());

            hiringInfo.setStaffInd( wEmp.getEmployeeType().getContractStatusInd());
            hiringInfo.setAbstractEmployeeEntity(wEmp);

            hiringInfo.setHireReportFiled("Y");
            hiringInfo.setTerminateInactive("N");

            hiringInfo.setPfaInfo(genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("defaultInd",1)));

            hiringInfo.setPayPeriod(genericService.loadObjectWithSingleCondition(PayPeriod.class, CustomPredicate.procurePredicate("defaultInd", ON)));

            hiringInfo.setCanEdit(true);
 
            //Now check if we can edit based on Payroll Running....
            PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(
                    getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus",0)));

            if(!wPRB.isNewEntity() && wPRB.isRunning()){
                hiringInfo.setPayrollRunning(true);
                hiringInfo.setPayrollRunningMessage("Payroll is currently being run by "+wPRB.getInitiator().getActualUserName()+ ". Editing of Hiring Information is not allowed at this time.");
                hiringInfo.setDisplayPayrollMsg("block");
            }
            model.addAttribute("namedEntity", ne);
            model.addAttribute("roleBean", bc);
            model.addAttribute(hiringInfo);
            return "hr/hiringForm";
        }
        if(bc.isPensioner()){
            hiringInfo.setOldYearlyPensionAmount(hiringInfo.getYearlyPensionAmount());
            hiringInfo.setYearlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(hiringInfo.getYearlyPensionAmount()));
            hiringInfo.setMonthlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(hiringInfo.getMonthlyPensionAmount()));
            //Determine if dude has Gratuity.
            GratuityInfo wGI = this.genericService.loadObjectUsingRestriction(GratuityInfo.class, Arrays.asList(CustomPredicate.procurePredicate("pensioner.id",eid),
                    getBusinessClientIdPredicate(request)));
            if(!wGI.isNewEntity()){
                hiringInfo.setGratuityAmountStr(wGI.getGratuityAmountStr());
                hiringInfo.setMonthlyGratuityAmountAsStr(PayrollHRUtils.getDecimalFormat().format(wGI.getGratuityAmount() - wGI.getOutstandingAmount()));
                hiringInfo.setHasGratuityInfo(true);
            }
        }
        hiringInfo.setPoliticalOfficeHolderType(wEmp.getEmployeeType().isPoliticalOfficeHolderType());

        hiringInfo.setEditMode(true);
        if ((hiringInfo.getTerminateReason() == null) || (hiringInfo.getTerminateReason().isNewEntity())) {
            // hiringInfo.setTermId(0);
            hiringInfo.setDisplayStatus("false");
            if (!hiringInfo.isContractStaff()) {
                hiringInfo.setShowRespAllowRow(SHOW_ROW);
            }

        }
        else if (hiringInfo.isContractStaff())
        {
            LocalDate contractStartDate = hiringInfo.getContractStartDate();
            LocalDate contractEndDate = hiringInfo.getContractEndDate();
            if ((contractStartDate == null) || (contractEndDate == null)) {
                //hiringInfo.setTermId(0);
                hiringInfo.setDisplayStatus("false");
                hiringInfo.setShowContractDateRows(SHOW_ROW);
            }
            else {
                hiringInfo.setShowContractDateRows(SHOW_ROW);
                LocalDate wToday = LocalDate.now();

                if ((wToday.isBefore(contractEndDate)) || (wToday.isEqual(contractEndDate)))
                {
                    hiringInfo.setContractExpired(true);
                }
            }
        }
        else {
            if(!bc.isPensioner()) {
                hiringInfo.setTerminated(true);
                hiringInfo.setTerminatedStr("Yes");
                hiringInfo.setShowTerminated(SHOW_ROW);
                hiringInfo.setDisplayStatus("true");
                hiringInfo.setTermId(hiringInfo.getTerminateReason().getId());
            }else{
                //We need to use the Pension End Date instead.
                if(hiringInfo.getPensionEndDate() != null){
                    hiringInfo.setTerminated(true);
                    hiringInfo.setTerminatedStr("Yes");
                    hiringInfo.setShowTerminated(SHOW_ROW);
                    hiringInfo.setDisplayStatus("true");
                    hiringInfo.setTermId(hiringInfo.getTerminateReason().getId());
                }

            }


        }

        hiringInfo.setPayPeriodName(hiringInfo.getPayPeriod().getName());

        if (hiringInfo.isTerminatedEmployee()) {
            hiringInfo.setCanEdit(false);
            if (hiringInfo.getGender().equalsIgnoreCase("M"))
                hiringInfo.setGenderStr("Male");
            else {
                hiringInfo.setGenderStr("Female");
            }
            /*
             * if (hiringInfo.getBirthDate() != null)
             * hiringInfo.setBirthDateStr(PayrollHRUtils.getDisplayDateFormat().format(
             * hiringInfo.getBirthDate()));
             */
            if (hiringInfo.getHireDate() != null)
                hiringInfo.setHireDateStr(PayrollHRUtils.getDisplayDateFormat().format(hiringInfo.getHireDate()));
            if (hiringInfo.getExpectedDateOfRetirement() != null)
                hiringInfo.setExpDateOfRetireStr(PayrollHRUtils.getDisplayDateFormat().format(hiringInfo.getExpectedDateOfRetirement()));
        } else {
            hiringInfo.setOldBirthDate(hiringInfo.getBirthDate());
            hiringInfo.setOldHireDate(hiringInfo.getHireDate());
            hiringInfo.setCanEdit(true);
        }
        if (hiringInfo.getLastPromotionDate() != null)
            hiringInfo.setLastPromotionDateStr(PayrollHRUtils.getDisplayDateFormat().format(hiringInfo.getLastPromotionDate()));
        else {
            hiringInfo.setLastPromotionDateStr("");
        }
        if(bc.isSubeb()){
            if(hiringInfo.getEmployeeStaffType() != null && !hiringInfo.getEmployeeStaffType().isNewEntity())
                hiringInfo.setStaffTypeId(hiringInfo.getEmployeeStaffType().getId());
            if(hiringInfo.getAbstractEmployeeEntity() != null && !hiringInfo.getAbstractEmployeeEntity().isNewEntity()) {
                hiringInfo.setStaffDesignationId(hiringInfo.getAbstractEmployeeEntity().getRank().getId());
                hiringInfo.setOldStaffDesignationId(hiringInfo.getStaffDesignationId());
            }
        }

        model.addAttribute("namedEntity", ne);
        model.addAttribute(hiringInfo);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }
    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid", "s"})
    public String setupForm(@RequestParam("eid") Long eid, @RequestParam("s") int pSave, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        //Object userId = this.getSessionId(request);
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, IConstants.NAMED_ENTITY);

        HiringInfo hiringInfo =
                this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), eid));

        if(bc.isCivilService() || bc.isLocalGovt())
            hiringInfo.setPoliticalOfficeHolderType(hiringInfo.getEmployee().getEmployeeType().isPoliticalOfficeHolderType());

        hiringInfo.setEditMode(true);
        if ((hiringInfo.getTerminateReason() == null) || (hiringInfo.getTerminateReason().isNewEntity())) {
            hiringInfo.setTermId(0L);
            hiringInfo.setDisplayStatus("false");
            if (!hiringInfo.isContractStaff()) {
                hiringInfo.setShowRespAllowRow(SHOW_ROW);
            }

        }
        else if (hiringInfo.isContractStaff())
        {

            if (hiringInfo.getContractStartDate() == null || hiringInfo.getContractEndDate() == null) {
                hiringInfo.setTermId(0L);
                hiringInfo.setDisplayStatus("false");
                hiringInfo.setShowContractDateRows(SHOW_ROW);
            }
            else {
                hiringInfo.setShowContractDateRows(SHOW_ROW);
                LocalDate wToday = LocalDate.now();

                if ((wToday.isBefore(hiringInfo.getContractEndDate())) || (wToday.equals(hiringInfo.getContractEndDate())))
                {
                    hiringInfo.setContractExpired(true);
                }

                hiringInfo.setOldContractStartDate(hiringInfo.getContractStartDate());
                hiringInfo.setOldContractEndDate(hiringInfo.getContractEndDate());

            }
        }
        else {
            if(!bc.isPensioner()) {
                hiringInfo.setTerminated(true);
                hiringInfo.setTerminatedStr("Yes");
                hiringInfo.setShowTerminated(SHOW_ROW);
                hiringInfo.setDisplayStatus("true");
                hiringInfo.setTermId(hiringInfo.getTerminateReason().getId());
            }else{
                //We need to use the Pension End Date instead.
                if(hiringInfo.getPensionEndDate() != null){
                    hiringInfo.setTerminated(true);
                    hiringInfo.setTerminatedStr("Yes");
                    hiringInfo.setShowTerminated(SHOW_ROW);
                    hiringInfo.setDisplayStatus("true");
                    hiringInfo.setTermId(hiringInfo.getTerminateReason().getId());
                }

            }
        }
        if(bc.isPensioner()){
            hiringInfo.setOldYearlyPensionAmount(hiringInfo.getYearlyPensionAmount());
            hiringInfo.setYearlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(hiringInfo.getYearlyPensionAmount()));
            hiringInfo.setMonthlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(hiringInfo.getMonthlyPensionAmount()));
            //Determine if dude has Gratuity.
            GratuityInfo wGI = this.genericService.loadObjectUsingRestriction(GratuityInfo.class, Arrays.asList(CustomPredicate.procurePredicate("pensioner.id",eid),
                    getBusinessClientIdPredicate(request)));
            if(!wGI.isNewEntity()){
                hiringInfo.setGratuityAmountStr(wGI.getGratuityAmountStr());
                hiringInfo.setMonthlyGratuityAmountAsStr(PayrollHRUtils.getDecimalFormat().format(wGI.getGratuityAmount() - wGI.getOutstandingAmount()));
                hiringInfo.setHasGratuityInfo(true);
            }
        }
        hiringInfo.setPayPeriodName(hiringInfo.getPayPeriod().getName());

        if ((hiringInfo.isTerminated())) {
            hiringInfo.setCanEdit(false);
            if (hiringInfo.getGender().equalsIgnoreCase("M"))
                hiringInfo.setGenderStr("Male");
            else {
                hiringInfo.setGenderStr("Female");
            }

            if (hiringInfo.getHireDate() != null)
                hiringInfo.setHireDateStr(PayrollHRUtils.getDisplayDateFormat().format(hiringInfo.getHireDate()));
            if (hiringInfo.getExpectedDateOfRetirement() != null)
                hiringInfo.setExpDateOfRetireStr(PayrollHRUtils.getDisplayDateFormat().format(hiringInfo.getExpectedDateOfRetirement()));
        } else {
            hiringInfo.setOldBirthDate(hiringInfo.getBirthDate());
            hiringInfo.setOldHireDate(hiringInfo.getHireDate());
            hiringInfo.setCanEdit(true);
        }
        if (hiringInfo.getLastPromotionDate() != null)
            hiringInfo.setLastPromotionDateStr(PayrollHRUtils.getDisplayDateFormat().format(hiringInfo.getLastPromotionDate()));
        else {
            hiringInfo.setLastPromotionDateStr("");
        }

        if (hiringInfo.getPfaInfo().isNewEntity()) {
            hiringInfo.setPfaInfo(new PfaInfo());
            hiringInfo.getPfaInfo().setName("");
        }
        if(bc.isSubeb()){
            if(hiringInfo.getEmployeeStaffType() != null && !hiringInfo.getEmployeeStaffType().isNewEntity())
                hiringInfo.setStaffTypeId(hiringInfo.getEmployeeStaffType().getId());

                hiringInfo.setStaffDesignationId(hiringInfo.getEmployee().getRank().getId());
        }
        String actionCompleted = "";
        if(bc.isPensioner())
            actionCompleted = "Hiring Information for " + hiringInfo.getPensioner().getDisplayNameWivTitlePrefixed() + " Updated Successfully.";
        else
            actionCompleted = "Hiring Information for " + hiringInfo.getEmployee().getDisplayNameWivTitlePrefixed() + " Updated Successfully.";
        model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
        model.addAttribute("saved", Boolean.valueOf(true));
        model.addAttribute("namedEntity", ne);
        model.addAttribute(hiringInfo);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;

    }

    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("hiringInfo") HiringInfo hireInfo, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        //Object userId = this.getSessionId(request);
        if (!hireInfo.isCanEdit())
        {
            if(bc.isPensioner())
                return "redirect:pensionerOverviewForm.do?eid="+hireInfo.getParentId();
            return "redirect:employeeOverviewForm.do?eid=" + hireInfo.getParentId();
        }



        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
            if(bc.isPensioner())
                return "redirect:pensionerOverviewForm.do?eid="+hireInfo.getParentId();
            return "redirect:employeeOverviewForm.do?eid=" + hireInfo.getEmployee().getId();
        }

        hiringValidator.validate(hireInfo, result, bc);
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("namedEntity", getSessionAttribute(request,IConstants.NAMED_ENTITY));
            model.addAttribute(hireInfo);
           addRoleBeanToModel(model, request);
            return VIEW_NAME;
        }

        hireInfo.setBusinessClientId(bc.getBusinessClientInstId());

        //Add I am Alive Date if empty and Ignore I am alive is not set.
        ConfigurationBean configurationBean = loadConfigurationBean(request);
        if(bc.isPensioner()){

            if(configurationBean.isUseIAmAlive() && hireInfo.getAmAliveDate() == null)
                hireInfo.setAmAliveDate(LocalDate.of(hireInfo.getBirthDate().getYear()+configurationBean.getIamAlive(),hireInfo.getBirthDate().getMonthValue(), hireInfo.getBirthDate().getDayOfMonth()));

        }else{
            hireInfo.setExpectedDateOfRetirement(PayrollBeanUtils.calculateExpDateOfRetirement(hireInfo.getBirthDate(), hireInfo.getHireDate(),configurationBean,bc));
        }


        hireInfo.setLastModBy(new User(bc.getLoginId()));
        hireInfo.setLastModTs(Timestamp.from(Instant.now()));
        if (((HiringInfo)result.getTarget()).isLastPromotionDateChanged()) {
            hireInfo.setLastPromotionDate(((HiringInfo)result.getTarget()).getLastPromotionDate());
            hireInfo.setNextPromotionDate(((HiringInfo)result.getTarget()).getNextPromotionDate());
        }
        //Here check if the hire date or birth date has changed for REINSTATEMENT LOGGING...
        /**
         * Bug found for Monthly Variation Report Generation....
         * OM1795
         * ONLY do this if both the OldBirthDate and Old Hire Dates are not null...
         *
         *
         */

        if(!bc.isPensioner()) {
            if (hireInfo.getOldBirthDate() != null && hireInfo.getOldHireDate() != null) {
                if (!hireInfo.getOldBirthDate().equals(hireInfo.getBirthDate())
                        || !hireInfo.getOldHireDate().equals(hireInfo.getHireDate())) {
                    //See if the Old Birth Date or Old Hire Date will lead to Termination...
                    LocalDate wExpRetireDate = PayrollBeanUtils.calculateExpDateOfRetirement(hireInfo.getOldBirthDate(), hireInfo.getOldHireDate(), loadConfigurationBean(request), bc);

                    //Now get the Last Approved Date
                    PayrollFlag wPF = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));
                    LocalDate wCal = LocalDate.of(wPF.getApprovedYearInd(), wPF.getApprovedMonthInd(), 1);

                    LocalDate wLastApprDate = LocalDate.of(wPF.getApprovedYearInd(), wPF.getApprovedMonthInd(), wCal.lengthOfMonth());

                    LocalDate wNextApprDate = PayrollBeanUtils.makeNextPayPeriod(wLastApprDate, wPF.getApprovedMonthInd(), wPF.getApprovedYearInd());

                    if (wExpRetireDate.isBefore(wLastApprDate) || wExpRetireDate.isBefore(wNextApprDate)) {
                        //This is a kind of reinstatement..so log it...
                        //Now check if there are pending paychecks affected by this Reinstatement...
                        LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
                        if (_wCal != null) {
                            //This means we have a Pending Paycheck. Set for strictly RERUN
                            RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(getBusinessClientIdPredicate(request),
                                    CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", _wCal.getYear())));

                            wRPB.setNoOfReinstatements(wRPB.getNoOfReinstatements() + 1);
                            if (wRPB.isNewEntity()) {
                                wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                                wRPB.setRunMonth(_wCal.getMonthValue());
                                wRPB.setRunYear(_wCal.getYear());
                            }
                            wRPB.setRerunInd(IConstants.ON);
                            this.genericService.storeObject(wRPB);
                        }
                        IppmsUtilsExt.createReinstatementLog(bc,genericService,hireInfo.getParentId(),hireInfo.getAbstractEmployeeEntity().getDisplayName(),hireInfo.getAbstractEmployeeEntity().getMdaDeptMap().getMdaInfo().getId(),
                                hireInfo.getAbstractEmployeeEntity().getSalaryInfo().getId(),hireInfo.getAbstractEmployeeEntity().getSchoolInfo(),wExpRetireDate);

                    }
                }
            }

            //-- Do a little check for Pensionable Indicator for Contract Staffs...
            //  EmployeeType wET = hireInfo.getEmployee().getEmployeeType();

            if (hireInfo.getEmployee().getEmployeeType().isContractStaff() || hireInfo.getEmployee().getEmployeeType().isPoliticalOfficeHolderType()) {
                if (hireInfo.getPfaInfo() == null || !hireInfo.getPfaInfo().isDefaultPfa()) {
                    hireInfo.setPfaInfo(genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("defaultInd", 1)));
                }
                hireInfo.setPensionableInd(IConstants.ON);
                hireInfo.setPensionPinCode(hireInfo.getPfaInfo().getPfaCode());

            }

            if (!hireInfo.isPensionableEmployee()) {

                if (hireInfo.getPfaInfo() == null || !hireInfo.getPfaInfo().isDefaultPfa()) {
                    hireInfo.setPfaInfo(genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("defaultInd", 1)));
                }
                hireInfo.setPensionPinCode(hireInfo.getPfaInfo().getPfaCode());
            }
            if (bc.isSubeb()) {
                hireInfo.setEmployeeStaffType(new EmployeeStaffType(hireInfo.getStaffTypeId()));
                if(!hireInfo.getStaffDesignationId().equals(hireInfo.getOldStaffDesignationId())){
                    //Change Designation.....
                        hireInfo.getEmployee().setRank(new Rank(hireInfo.getStaffDesignationId()));
                        this.genericService.storeObject(hireInfo.getEmployee());

                }
            }
        }
        this.genericService.storeObject(hireInfo);

        boolean mustRerun = false;

        //if contract staff and contract dates have been modified, add contract details to contract history bean
        if(hireInfo.isContractStaff()){


            //there is possibly an active contract history
            if(hireInfo.getOldContractEndDate() != null && hireInfo.getOldContractEndDate() != null){
                //if dates have been changed persist new contract information, else do nothing!!!
                if(!hireInfo.getOldContractStartDate().equals(hireInfo.getContractStartDate())
                        || !hireInfo.getOldContractEndDate().equals(hireInfo.getContractEndDate())){

                    this.persistEmployeeContract(hireInfo, bc);
                    mustRerun = true;

                }
            }
            //else fresh contract
            else {
                //if contract dates not in hiring info but active contract exist in contract history table
                // -- this should technically never happen unless forced through the back end
                //however if both hiring info and contract history do not have contract information -- this should be done
                this.persistEmployeeContract(hireInfo, bc);
                mustRerun = true;
            }

        }

        //Set Pending Payroll Run to be rerun or deleted as new birth date or hire date could affect payment
        if(hireInfo.getOldBirthDate() != null && hireInfo.getOldHireDate() != null && !bc.isPensioner())
        {
            if(!hireInfo.getOldBirthDate().equals(hireInfo.getBirthDate())
                    || !hireInfo.getOldHireDate().equals(hireInfo.getHireDate())){

                mustRerun = true;

            }
        }

        if(mustRerun){
            LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            if(_wCal != null){
                //This means we have a Pending Paycheck. Set for strictly RERUN
                RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class,Arrays.asList(getBusinessClientIdPredicate(request),
                        CustomPredicate.procurePredicate("runMonth",_wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear",_wCal.getYear())));
                wRPB.setNoOfReinstatements(wRPB.getNoOfReinstatements() + 1);
                if(wRPB.isNewEntity()){
                    wRPB.setRunMonth(_wCal.getMonthValue());
                    wRPB.setRunYear(_wCal.getYear());
                    wRPB.setBusinessClientId(bc.getBusinessClientInstId());

                }
                wRPB.setRerunInd(IConstants.ON);
                this.genericService.storeObject(wRPB);
            }
        }

        Long empId;
        if(bc.isPensioner())
            empId = hireInfo.getPensioner().getId();
        else
            empId = hireInfo.getEmployee().getId();

        if (hireInfo.isEditMode()) {

            PaySchedule paySched = this.genericService.loadObjectWithSingleCondition(PaySchedule.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), empId));

                if (paySched.isNewEntity()) {

                         createEmployeePaySchedule(bc,  hireInfo);
                                if(bc.isPensioner())
                                    return "redirect:pensionerOverviewForm.do?eid=" + empId;
                            return "redirect:employeeOverviewForm.do?eid=" + empId;

                }


        }
        if(bc.isPensioner())
            return "redirect:editPenHireInfo.do?eid=" + empId + "&s=1";
        return "redirect:editHireInfo.do?eid=" + empId + "&s=1";
    }

    private void persistEmployeeContract(HiringInfo hireInfo, BusinessCertificate bc) throws Exception{


        //since dates have been changed, expire existing contract if any and create new one
        ContractHistory contractHist = this.genericService.loadObjectUsingRestriction(ContractHistory.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),hireInfo.getParentId()),
                CustomPredicate.procurePredicate("expiredInd",0)));
        if(!contractHist.isNewEntity()){
            //expire existing contract for historical reference and create a new one
            contractHist.setExpiredInd(IConstants.ON);
            contractHist.setExpiredDate(LocalDate.now());
            contractHist.setLastModBy(new User(bc.getLoginId()));
            contractHist.setLastModTs(Timestamp.from(Instant.now()));

            this.genericService.storeObject(contractHist);

            contractHist = new ContractHistory();
            contractHist.setBusinessClientId(bc.getBusinessClientInstId());
        }
        //before continuing, first find out if this is a Reinstatement....
        PayrollFlag wPF = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));
        //--Now Load the Staff Last paycheck.
        Long pid = paycheckService.getMaxPaycheckIdForEmployee(bc, hireInfo.getParentId());
        if (!pid.equals(0L)) {
             //Now Load this Paycheck and check if its net pay = 0 or last Pay Date < wPF.approvedDate....
            AbstractPaycheckEntity employeePayBean = (AbstractPaycheckEntity) this.genericService.loadObjectById(IppmsUtils.getPaycheckClass(bc),pid);
            if(employeePayBean.getNetPay() == 0 || (employeePayBean.getRunMonth() < wPF.getApprovedMonthInd() && employeePayBean.getRunYear() <= wPF.getApprovedYearInd()))
                IppmsUtilsExt.createReinstatementLog(bc,genericService,hireInfo.getParentId(),hireInfo.getAbstractEmployeeEntity().getDisplayName(),hireInfo.getAbstractEmployeeEntity().getMdaDeptMap().getMdaInfo().getId(),hireInfo.getAbstractEmployeeEntity().getSalaryInfo().getId(),
                        hireInfo.getAbstractEmployeeEntity().getSchoolInfo(), LocalDate.now());
        }

        contractHist.setEmployee(hireInfo.getEmployee());
        contractHist.setName("New Contract");
        contractHist.setReferenceNumber("Ref/CONTR/" + bc.getUserName() + PayrollHRUtils.dateWidoutDelimeters());
        contractHist.setReferenceDate(LocalDate.now());
        contractHist.setExpiredInd(IConstants.OFF);
        if(contractHist.getCreatedBy() == null)
            contractHist.setCreatedBy(new User(bc.getLoginId()));
        if(contractHist.getBusinessClientId() == null)
        contractHist.setBusinessClientId(bc.getBusinessClientInstId());
        contractHist.setSalaryInfo(hireInfo.getEmployee().getSalaryInfo());
        contractHist.setContractStartDate(hireInfo.getContractStartDate());
        contractHist.setContractEndDate(hireInfo.getContractEndDate());
        contractHist.setContractEndDay(hireInfo.getContractEndDate().getDayOfMonth());
        contractHist.setContractEndMonth(hireInfo.getContractEndDate().getMonthValue());
        contractHist.setContractEndYear(hireInfo.getContractEndDate().getYear());
        contractHist.setContractStartDay(hireInfo.getContractStartDate().getDayOfMonth());
        contractHist.setContractStartMonth(hireInfo.getContractStartDate().getMonthValue());
        contractHist.setContractStartYear(hireInfo.getContractStartDate().getYear());
        contractHist.setLastModBy(new User(bc.getLoginId()));
        contractHist.setLastModTs(Timestamp.from(Instant.now()));

        this.genericService.storeObject(contractHist);

        hireInfo.setContractExpiredInd(IConstants.OFF);


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
