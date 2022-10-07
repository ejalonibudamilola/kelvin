/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

/**
 * @Author Mustola.
 * @Since Sept 18, 2020
 */
package com.osm.gnl.ippms.ogsg.controllers.employee.service;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractEmployeeAuditEntity;
import com.osm.gnl.ippms.ogsg.audit.domain.EmployeeAudit;
import com.osm.gnl.ippms.ogsg.audit.domain.PensionerAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Religion;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.approval.EmployeeApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.*;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.FirstLeaveBonus;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class EmployeeControllerService {



    public static Model busEmpOverviewHelper(HttpServletRequest request, Model model, BusinessCertificate businessCertificate,
                                             BaseController.PaginationBean p, CustomPredicate businessClientPredicate, GenericService genericService, int pageLength){

        CustomPredicate predicate = CustomPredicate.procurePredicate("statusIndicator",0);
        List<CustomPredicate> predicateList = new ArrayList<>();
        predicateList.add(predicate);
        predicateList.add(businessClientPredicate);


        List<AbstractEmployeeEntity> empList = (List<AbstractEmployeeEntity>)genericService.loadPaginatedObjects(IppmsUtils.getEmployeeClass(businessCertificate),predicateList,(p.getPageNumber() - 1) *  pageLength, pageLength, p.getSortOrder(), p.getSortCriterion());
        Collections.sort(empList);
        int wNoOfElements = genericService.getTotalPaginatedObjects(Employee.class,predicateList).intValue();

        BusinessEmpOVBean pList = new BusinessEmpOVBean(empList, p.getPageNumber(), pageLength, wNoOfElements, p.getSortCriterion(), p.getSortOrder());

        pList.setShowingInactive(false);
        pList.setId(businessCertificate.getBusinessClientInstId());

        pList.setAdmin(businessCertificate.isSuperAdmin());

        pList.setHidden("hidden");

        model.addAttribute("busEmpOVBean", pList);

        return model;
    }


    public static AbstractEmployeeEntity storeInformation(AbstractEmployeeEntity employee, BusinessCertificate businessCertificate, GenericService genericService, HttpServletRequest request)throws Exception{
        BiometricInfo biometricInfo = new BiometricInfo(), b = null;

        employee.setLgaInfo(new LGAInfo(employee.getLgaId()));
        employee.setCity(new City(employee.getCityId(),employee.getStateInstId()));
        employee.setStateOfOrigin(new State(employee.getStateOfOriginId()));
        employee.setTitle(new Title(employee.getTitleId()));
        employee.setReligion(new Religion(employee.getRelId()));
        employee.setCreatedBy(new User(businessCertificate.getLoginId()));
        employee.setLastModBy(new User(businessCertificate.getLoginId()));
        employee.setLastModTs(new Timestamp(System.currentTimeMillis()));


        employee.setEmployeeId(employee.getEmployeeId().toUpperCase());

        if(!businessCertificate.isPensioner()){
            biometricInfo = (BiometricInfo) request.getSession().getAttribute("biometricData");
            b = genericService.loadObjectUsingRestriction(BiometricInfo.class, Arrays.asList(CustomPredicate.procurePredicate("bioId", biometricInfo.getBioId())));
            employee.setBiometricInfo(b);

        }

        //save employee passport



        if(!businessCertificate.isPensioner()) {
             if ((employee.isSchoolEnabled()) &&
                     (employee.getSchoolInstId() != -1)) {
                 ((Employee) employee).setSchoolInfo(new SchoolInfo(employee.getSchoolInstId()));
             }
         }else{
             //--After Payment, we remove.
             ((Pensioner)employee).setNewEntrantInd(employee.getMapId());
             ((Pensioner)employee).setEntryMonthInd(employee.getObjectInd());
            ((Pensioner)employee).setEntryYearInd(LocalDate.now().getYear());

         }

            //--First get the Payroll Master Bean..
        Long payrollMasterId = genericService.loadMaxValueByClassClientIdAndColumn(PayrollRunMasterBean.class,"id",employee.getBusinessClientId(),"businessClientId");
         int runMonth;
         int runYear;
         if(payrollMasterId == null){
             LocalDate localDate = LocalDate.now();
             runMonth = localDate.getMonthValue();

             runYear = localDate.getYear();
         }else{
             PayrollRunMasterBean payrollRunMasterBean = genericService.loadObjectById(PayrollRunMasterBean.class,payrollMasterId);

             if(payrollRunMasterBean.getRunMonth() == 12) {
                 runMonth = 1;
                 runYear = payrollRunMasterBean.getRunYear() + 1;
             }else{
                 runMonth = payrollRunMasterBean.getRunMonth() + 1;
                 runYear = payrollRunMasterBean.getRunYear();
             }
         }

//            if(businessCertificate.isPensioner() && IppmsUtils.isNotNullAndGreaterThanZero(employee.getParentClientId())) {
//                ((Pensioner) employee).setParentBusinessClientId(employee.getParentClientId());
//            }


            genericService.saveObject(employee);
            createEmployeeApproval(employee,businessCertificate,genericService);

            if(!businessCertificate.isPensioner())
            saveEmployeePhoto(businessCertificate, employee, genericService, biometricInfo);

            createEmployeeAudit(genericService,employee,businessCertificate, runMonth,runYear);
            if(businessCertificate.isSubeb() && !IppmsUtils.isNullOrLessThanOne(employee.getSchoolInstId())){
                //Check if we need to Set Special Allowance For Rural School Type.
                SchoolInfo schoolInfo = genericService.loadObjectById(SchoolInfo.class,employee.getSchoolInstId());
                if(schoolInfo.isRural()){
                    createRuralAllowance(employee,genericService, businessCertificate);
                }
            }



        //Now check if this Employee will spend at least 6 Months in the Civil Service by the end of the year....
        if(businessCertificate.isCivilService()) {

            if (LocalDate.now().getMonthValue() <= 6) {
                //Create FirstLeaveBonus Bean...
                createAndStoreFirstLeaveBonus((Employee)employee,businessCertificate,genericService);
            } else {
                //Now check if this dude is being hired after Approving Payroll For December....
                if (LocalDate.now().getMonthValue() == 12) {
                    //Now check if Payroll Has Been Approved....

                    PayrollFlag wPf = genericService.loadObjectWithSingleCondition(PayrollFlag.class,CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
                    if (wPf.getApprovedMonthInd() != 12) {
                        createAndStoreFirstLeaveBonus((Employee)employee,businessCertificate,genericService);
                    }
                }
            }
        }


      return employee;

    }

    public static Long createEmployeeApproval(AbstractEmployeeEntity abstractEmployeeEntity, BusinessCertificate bc,GenericService genericService) throws InstantiationException, IllegalAccessException {
        EmployeeApproval employeeApproval = new EmployeeApproval();
        employeeApproval.setInitiatedDate(LocalDate.now());
        employeeApproval.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(bc.getLoginId()));
        employeeApproval.setEntityName(abstractEmployeeEntity.getDisplayName());
        employeeApproval.setEntityId(abstractEmployeeEntity.getId());
        employeeApproval.setEmployeeId(abstractEmployeeEntity.getEmployeeId());
        employeeApproval.setBusinessClientId(bc.getBusinessClientInstId());
        employeeApproval.setInitiator(new User(bc.getLoginId()));
        employeeApproval.setLastModTs(LocalDate.now());
        if(bc.isPensioner())
            employeeApproval.setPensioner(new Pensioner(employeeApproval.getEntityId()));
        else
            employeeApproval.setEmployee(new Employee(employeeApproval.getEntityId()));
        genericService.saveObject(employeeApproval);
        NotificationService.storeNotification(bc, genericService, employeeApproval, "requestNotification.do?arid=" + employeeApproval.getId() + "&s=1&oc=" + IConstants.STAFF_APPROVAL_INIT_URL_IND, bc.getStaffTypeName() + " Approval Request",IConstants.STAFF_APPROVAL_CODE );
        return employeeApproval.getId();
    }

    private static void saveEmployeePhoto(BusinessCertificate businessCertificate, AbstractEmployeeEntity employee, GenericService genericService, BiometricInfo biometricInfo) throws InstantiationException, IllegalAccessException {
        HrPassportInfo pHrPassportInfo = genericService.loadObjectWithSingleCondition(HrPassportInfo.class, CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), employee.getId() ));

        if(pHrPassportInfo.isNewEntity()) {
            pHrPassportInfo.setPhoto(biometricInfo.getProfilePicture());
            // pHrmPassport.setPhoto(blob);
            pHrPassportInfo.setPhotoType(biometricInfo.getPhotoType());
            if (businessCertificate.isPensioner())
                pHrPassportInfo.setPensioner(new Pensioner(employee.getId()));
            else
                pHrPassportInfo.setEmployee(new Employee(employee.getId()));

            pHrPassportInfo.setCreatedBy(new User(businessCertificate.getLoginId()));
            pHrPassportInfo.setLastModBy(new User(businessCertificate.getLoginId()));
            pHrPassportInfo.setLastModTs(Timestamp.from(Instant.now()));
            pHrPassportInfo.setBusinessClientId(businessCertificate.getBusinessClientInstId());


        }

        genericService.storeObject(pHrPassportInfo);
    }

    private static void createRuralAllowance(AbstractEmployeeEntity employee, GenericService genericService, BusinessCertificate businessCertificate) throws Exception {
        SpecialAllowanceType specialAllowanceType = genericService.loadObjectUsingRestriction(SpecialAllowanceType.class, Arrays.asList(
                CustomPredicate.procurePredicate("arrearsInd", IConstants.ON), CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())));
        if(specialAllowanceType.isNewEntity())
            throw new Exception("Rural Allowance Type not found!");
        AbstractSpecialAllowanceEntity specialAllowanceInfo = IppmsUtils.makeSpecialAllowanceInfoObject(businessCertificate);
        specialAllowanceInfo.setEmployee(new Employee(employee.getId()));
        specialAllowanceInfo.setName(specialAllowanceType.getName());
        specialAllowanceInfo.setDescription(specialAllowanceType.getDescription());
        specialAllowanceInfo.setAmount(specialAllowanceType.getAmount());
        specialAllowanceInfo.setStartDate(LocalDate.now());
        specialAllowanceInfo.setLastModBy(employee.getLastModBy());
        specialAllowanceInfo.setCreatedBy(employee.getLastModBy());
        specialAllowanceInfo.setExpire(IConstants.OFF);
        specialAllowanceInfo.setPayTypes(specialAllowanceType.getPayTypes());
        specialAllowanceInfo.setReferenceNumber(businessCertificate.getUserName()+"_"+ PayrollBeanUtils.getDateAsString(LocalDate.now()));
        specialAllowanceInfo.setReferenceDate(LocalDate.now());
        genericService.storeObject(specialAllowanceInfo);

    }

    private static synchronized void createEmployeeAudit(GenericService genericService, AbstractEmployeeEntity employee, BusinessCertificate businessCertificate, int runMonth, int runYear) throws IllegalAccessException, InstantiationException {
        AbstractEmployeeAuditEntity employeeAudit = IppmsUtils.makeEmployeeAudit(businessCertificate);

       // employeeAudit.setEmployee(employee);
        employeeAudit.setLastModTs(LocalDate.now());
        employeeAudit.setUser(new User(businessCertificate.getLoginId()));
        employeeAudit.setMdaInfo(employee.getMdaDeptMap().getMdaInfo());
        if(employee.isSchoolEnabled())
            employeeAudit.setSchoolInfo(employee.getSchoolInfo());
        employeeAudit.setSalaryInfo(employee.getSalaryInfo());
        employeeAudit.setBusinessClientId(businessCertificate.getBusinessClientInstId());
        employeeAudit.setAuditPayPeriod(PayrollUtils.makeAuditPayPeriod(runMonth,runYear));
        employeeAudit.setOldValue(IppmsUtilsExt.getGrossSalary(genericService.loadObjectById(SalaryInfo.class,employee.getSalaryInfo().getId())));
        employeeAudit.setNewValue("New "+businessCertificate.getStaffTypeName());
        if(businessCertificate.isPensioner()) {
           ((PensionerAudit)employeeAudit).setEmployee(new Pensioner(employee.getId()));
        }else{
            ((EmployeeAudit)employeeAudit).setEmployee(new Employee(employee.getId()));
        }

        employeeAudit.setColumnChanged("All");
        employeeAudit.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime(false));
        employeeAudit.setAuditActionType("I");
        genericService.storeObject(employeeAudit);
    }

    private static void createAndStoreFirstLeaveBonus(Employee employee, BusinessCertificate businessCertificate, GenericService genericService){
        FirstLeaveBonus wFLB = new FirstLeaveBonus();
        wFLB.setEmployee(new Employee(employee.getId()));
        wFLB.setLogin(new User(businessCertificate.getLoginId()));
        wFLB.setSalaryInfo(employee.getSalaryInfo());
        wFLB.setCreatedMonth(LocalDate.now().getMonthValue());
        wFLB.setCreatedYear(LocalDate.now().getYear());
        wFLB.setLastModTs(LocalDate.now());
        genericService.saveObject(wFLB);
    }
}
