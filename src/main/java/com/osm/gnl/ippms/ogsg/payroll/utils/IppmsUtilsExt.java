/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.PayrollConfigImpact;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.domain.allowance.*;
import com.osm.gnl.ippms.ogsg.domain.approval.AmAliveApproval;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionCategory;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.garnishment.*;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfig;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.domain.suspension.ReinstatementLog;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.*;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionType;
import com.osm.gnl.ippms.ogsg.utils.annotation.AnnotationProcessor;
import com.osm.gnl.ippms.ogsg.utils.annotation.SalaryAllowance;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public abstract class IppmsUtilsExt {


    public static String getStaffIdLabel(BusinessCertificate bc){
        if(bc.isCivilService()){
            return "OG Number";
        }else if(bc.isSubeb()){
            return "OGSB Number";
        }else if(bc.isLocalGovt()){
            return "OG Number";
        }
        return "Pensioner ID";
    }

    /**
     *
     * @param genericService
     * @param businessCertificate
     * @param pRunMonth
     * @param pRunYear
     * @param pendingPaychecksOnly - Use to
     * @return
     */
    public static int countNoOfPayChecks(GenericService genericService, BusinessCertificate businessCertificate, int pRunMonth, int pRunYear , boolean pendingPaychecksOnly) {
        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", pRunMonth));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear", pRunYear));
        if(pendingPaychecksOnly)
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("status", "P"));

        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder,IppmsUtils.getPaycheckClass(businessCertificate));
    }
    public static boolean paycheckExistsForEmployee(GenericService genericService, BusinessCertificate businessCertificate, int pRunMonth, int pRunYear , Long pEmpId) {
        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", pRunMonth));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear", pRunYear));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("employee.id", pEmpId));

        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder,IppmsUtils.getPaycheckClass(businessCertificate)) > 0;
    }
    public static boolean pendingPaychecksExists(GenericService genericService, BusinessCertificate businessCertificate) {
        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("status", "P"));

        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder,IppmsUtils.getPaycheckClass(businessCertificate)) > 0;
    }
    public static boolean pendingPaychecksExistsForEntity(GenericService genericService, BusinessCertificate businessCertificate, Long pEntityId) {
        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("status", "P"));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("employee.id", pEntityId));

        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder,IppmsUtils.getPaycheckClass(businessCertificate)) > 0;
    }
    public static Long maxPaycheckIdByEmployee(GenericService genericService, BusinessCertificate businessCertificate, Long pEmpId){
        List results = genericService
                .getCurrentSession()
                .createCriteria(IppmsUtils.getPaycheckClass(businessCertificate))
                .add(Restrictions.eq("employee.id",pEmpId ))
                .add(Restrictions.eq("businessClientId", businessCertificate.getBusinessClientInstId()))
                .add(Restrictions.gt("netPay", 0))
                .setProjection(Projections.max("id")).list();

        if ((results == null) || (results.size() < 1) || (results.isEmpty()) || (results.get(0) == null)) {
            return 0L;
        }
        return ((Long)results.get(0));
    }
    public static PayrollFlag getPayrollFlagForClient(GenericService genericService, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {
        return genericService.loadObjectWithSingleCondition(PayrollFlag.class, CustomPredicate.procurePredicate("businessClientId",businessCertificate.getBusinessClientInstId()));

    }

    public static synchronized Object setDeductionRequiredValues(BusinessCertificate businessCertificate, Object pDed, Long pId, Object pWepb) {

            if (businessCertificate.isLocalGovtPension()) {
                ((PaycheckDeductionBLGP) pDed).setEmployee(new Pensioner(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
                ((PaycheckDeductionBLGP) pDed).setEmployeePayBean(new EmployeePayBeanBLGP(pId));
            } else if (businessCertificate.isLocalGovt()) {
                ((PaycheckDeductionLG) pDed).setEmployee(new Employee(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
                ((PaycheckDeductionLG) pDed).setEmployeePayBean(new EmployeePayBeanLG(pId));
            } else if (businessCertificate.isStatePension()) {
                ((PaycheckDeductionPension) pDed).setEmployee(new Pensioner(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
                ((PaycheckDeductionPension) pDed).setEmployeePayBean(new EmployeePayBeanPension(pId));
            } else if (businessCertificate.isCivilService()) {
                ((PaycheckDeduction) pDed).setEmployee(new Employee(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
                ((PaycheckDeduction) pDed).setEmployeePayBean(new EmployeePayBean(pId));
            } else if (businessCertificate.isSubeb()) {
                ((PaycheckDeductionSubeb) pDed).setEmployee(new Employee(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
                ((PaycheckDeductionSubeb) pDed).setEmployeePayBean(new EmployeePayBeanSubeb(pId));
            }

        return pDed;
    }
    public static synchronized Object setLoanRequiredValues(BusinessCertificate businessCertificate, Object pDed, Long pId, Object pWepb, AbstractGarnishmentEntity pGarnInfo) {
        if (businessCertificate.isLocalGovtPension()) {
            ((PaycheckGarnishmentBLGP) pDed).setEmployee(new Pensioner(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckGarnishmentBLGP) pDed).setEmployeePayBean(new EmployeePayBeanBLGP(pId));
            ((PaycheckGarnishmentBLGP)pDed).setEmpGarnInfo(new EmpGarnishmentInfoPensions(pGarnInfo.getId()));
        } else if (businessCertificate.isLocalGovt()) {
            ((PaycheckGarnishmentLG) pDed).setEmployee(new Employee(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckGarnishmentLG) pDed).setEmployeePayBean(new EmployeePayBeanLG(pId));
            ((PaycheckGarnishmentLG)pDed).setEmpGarnInfo(new EmpGarnishmentInfoLG(pGarnInfo.getId()));
        } else if (businessCertificate.isStatePension()) {
            ((PaycheckGarnishmentPension) pDed).setEmployee(new Pensioner(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckGarnishmentPension) pDed).setEmployeePayBean(new EmployeePayBeanPension(pId));
            ((PaycheckGarnishmentPension)pDed).setEmpGarnInfo(new EmpGarnishmentInfoPensions(pGarnInfo.getId()));
        } else if (businessCertificate.isCivilService()) {
            ((PaycheckGarnishment) pDed).setEmployee(new Employee(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckGarnishment) pDed).setEmployeePayBean(new EmployeePayBean(pId));
            ((PaycheckGarnishment)pDed).setEmpGarnInfo(new EmpGarnishmentInfo(pGarnInfo.getId()));
        } else if (businessCertificate.isSubeb()) {
            ((PaycheckGarnishmentSubeb) pDed).setEmployee(new Employee(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckGarnishmentSubeb) pDed).setEmployeePayBean(new EmployeePayBeanSubeb(pId));
            ((PaycheckGarnishmentSubeb)pDed).setEmpGarnInfo(new EmpGarnishmentInfoSubeb(pGarnInfo.getId()));
        }

        return pDed;
    }

    public static synchronized Object setSpecAllowReqValues(BusinessCertificate businessCertificate, Object pDed, Long pId, Object pWepb, AbstractSpecialAllowanceEntity pSpecInfo) {
        if (businessCertificate.isLocalGovtPension()) {
            ((PaycheckSpecialAllowanceBLGP) pDed).setEmployee(new Pensioner(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckSpecialAllowanceBLGP) pDed).setEmployeePayBean(new EmployeePayBeanBLGP(pId));
            ((PaycheckSpecialAllowanceBLGP) pDed).setSpecialAllowanceInfo(new SpecialAllowanceInfoPensions(pSpecInfo.getId()));
        } else if (businessCertificate.isLocalGovt()) {
            ((PaycheckSpecialAllowanceLG) pDed).setEmployee(new Employee(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckSpecialAllowanceLG) pDed).setEmployeePayBean(new EmployeePayBeanLG(pId));
            ((PaycheckSpecialAllowanceLG) pDed).setSpecialAllowanceInfo(new SpecialAllowanceInfoLG(pSpecInfo.getId()));
        } else if (businessCertificate.isStatePension()) {
            ((PaycheckSpecialAllowancePension) pDed).setEmployee(new Pensioner(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckSpecialAllowancePension) pDed).setEmployeePayBean(new EmployeePayBeanPension(pId));
            ((PaycheckSpecialAllowancePension) pDed).setSpecialAllowanceInfo(new SpecialAllowanceInfoPensions(pSpecInfo.getId()));
        } else if (businessCertificate.isCivilService()) {
            ((PaycheckSpecialAllowance) pDed).setEmployee(new Employee(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckSpecialAllowance) pDed).setEmployeePayBean(new EmployeePayBean(pId));
            ((PaycheckSpecialAllowance) pDed).setSpecialAllowanceInfo(new SpecialAllowanceInfo(pSpecInfo.getId()));
        } else if (businessCertificate.isSubeb()) {
            ((PaycheckSpecialAllowanceSubeb) pDed).setEmployee(new Employee(((AbstractPaycheckEntity) pWepb).getParentObject().getId()));
            ((PaycheckSpecialAllowanceSubeb) pDed).setEmployeePayBean(new EmployeePayBeanSubeb(pId));
            ((PaycheckSpecialAllowanceSubeb) pDed).setSpecialAllowanceInfo(new SpecialAllowanceInfoSubeb(pSpecInfo.getId()));
        }
        return pDed;
    }

    public static PayrollRunMasterBean getPayrollRunMasterBean(GenericService genericService,Long pBusClientId, int pRunMonth, int pRunYear) throws IllegalAccessException, InstantiationException {
       return  genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(CustomPredicate.procurePredicate("runMonth", pRunMonth) ,
                        CustomPredicate.procurePredicate("runYear", pRunYear),
                        CustomPredicate.procurePredicate("businessClientId", pBusClientId)));
    }

    public static String getGrossSalary(SalaryInfo loadObjectById) throws IllegalAccessException {
        double grossDouble = 0.0D;
        Map<Field, Double> map = AnnotationProcessor.getAllowanceFields(loadObjectById,Double.class, SalaryAllowance.class) ;
        Set<Field> keySet = map.keySet();
        for(Field field : keySet)
            grossDouble += EntityUtils.convertDoubleToEpmStandard(map.get(field));

        return PayrollHRUtils.getDecimalFormat(false).format(grossDouble);
    }

    public static boolean isStepIncrementTypePromotion(Long oldSalaryInfoId, Long newSalaryInfoId, GenericService genericService) throws IllegalAccessException, InstantiationException {
        boolean wRetVal = false;

        SalaryInfo oldSalaryInfo = genericService.loadObjectById(SalaryInfo.class,oldSalaryInfoId);
        SalaryInfo newSalaryInfo = genericService.loadObjectById(SalaryInfo.class,newSalaryInfoId);
        if(oldSalaryInfo.getSalaryType().getId().equals(newSalaryInfo.getSalaryType().getId())){
            if(oldSalaryInfo.getLevel() == newSalaryInfo.getLevel() && ((oldSalaryInfo.getStep() + 1) == newSalaryInfo.getStep()))
                wRetVal = true;
        }


        return wRetVal;
    }
    public static double getAnnualSalary(SalaryInfo pSalaryInfo) {
            double retVal = 0.0D;
        try {
            Map<Field, Double>	AllowMap = AnnotationProcessor.getAllowanceFields(pSalaryInfo, Double.class, SalaryAllowance.class);
            Set<Field> keys = AllowMap.keySet();
            Class<?>[] parameterTypes = null;

            for (Field f : keys) {
                retVal += AllowMap.get(f);

            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return retVal + pSalaryInfo.getMonthlyBasicSalary();

    }
    public static double getMonthlySalary(SalaryInfo pSalaryInfo) {
        double retVal = 0.0D;
        try {
            Map<Field, Double>	AllowMap = AnnotationProcessor.getAllowanceFields(pSalaryInfo, Double.class, SalaryAllowance.class);
            Set<Field> keys = AllowMap.keySet();

            for (Field f : keys) {
                retVal += AllowMap.get(f);

            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        retVal += pSalaryInfo.getMonthlyBasicSalary();

        return EntityUtils.convertDoubleToEpmStandard(retVal/12.0D);

    }
    public static double getConsolidatedAllowance(SalaryInfo pSalaryInfo, boolean pMonthly, Map<String, Double> allowanceRuleDetailsMap) {
        double retVal = 0.0D;
        double valueToPrintOut;
        double valueBe4Div;
        try {
            Map<Field, Double>	allowMap = AnnotationProcessor.getAllowanceFields(pSalaryInfo, Double.class, SalaryAllowance.class);
            Set<Field> keys = allowMap.keySet();

            for (Field f : keys) {
                if(allowanceRuleDetailsMap != null && allowanceRuleDetailsMap.containsKey(f.getAnnotation(SalaryAllowance.class).type())){
                    if(pMonthly) {
                        retVal += PayrollPayUtils.convertDoubleToEpmStandard(allowanceRuleDetailsMap.get(f.getAnnotation(SalaryAllowance.class).type()) / 12.0D);
                    } else {
                        retVal += allowanceRuleDetailsMap.get(f.getAnnotation(SalaryAllowance.class).type());
                    }
                }else{
                    if(pMonthly) {
                        retVal += PayrollPayUtils.convertDoubleToEpmStandard(allowMap.get(f) / 12.0D);
                    }else
                        retVal += allowMap.get(f);
                }


            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return retVal ;

    }
    public static double getConsolidatedAllowance(SalaryInfo pSalaryInfo, boolean pMonthly) {
        double retVal = 0.0D;
        try {
            Map<Field, Double>	AllowMap = AnnotationProcessor.getAllowanceFields(pSalaryInfo, Double.class, SalaryAllowance.class);
            Set<Field> keys = AllowMap.keySet();
            //Class<?>[] parameterTypes = null;

            for (Field f : keys) {
                  retVal += AllowMap.get(f);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(pMonthly)
            return  EntityUtils.convertDoubleToEpmStandard(retVal/12.0D);
        return retVal ;

    }

    public static PayrollRunMasterBean loadCurrentlyRunningPayroll(GenericService genericService, BusinessCertificate businessCertificate) throws IllegalAccessException, InstantiationException {
        return genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,Arrays.asList(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId() ),
                CustomPredicate.procurePredicate("payrollStatus",1)));
    }

    public static RerunPayrollBean loadRerunPayrollBean(GenericService genericService, Long businessClientInstId, LocalDate wCal) throws IllegalAccessException, InstantiationException {
       return genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(
                CustomPredicate.procurePredicate("runMonth", wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", wCal.getYear()),
                CustomPredicate.procurePredicate("businessClientId", businessClientInstId)));
    }
     public static <T> int countNoOfNegativePay(Class<T> abstractPaycheck,GenericService genericService, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
         PredicateBuilder predicateBuilder = new PredicateBuilder();
         predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));
         predicateBuilder.addPredicate(CustomPredicate.procurePredicate("status", "P", Operation.EQUALS));
         predicateBuilder.addPredicate(CustomPredicate.procurePredicate("negativePayInd",1));
        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder,abstractPaycheck );
    }

    public static boolean employeeHasExistingContract(GenericService genericService,BusinessCertificate businessCertificate, Long pEmpId){
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",businessCertificate.getBusinessClientInstId()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("employee.id", pEmpId));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("expiredInd",0));
        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder, ContractHistory.class) > 0;
    }

    public static ConfigurationBean loadConfigurationBean(GenericService genericService, BusinessCertificate bc) throws InstantiationException, IllegalAccessException {
      return genericService.loadObjectWithSingleCondition(ConfigurationBean.class, CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId() ));
    }
    public static GlobalPercentConfig loadActiveGlobalPercentConfigByClient(GenericService genericService, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        return genericService.loadObjectUsingRestriction(GlobalPercentConfig.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("deactivateInd",0),CustomPredicate.procurePredicate("payrollStatus",1)));
    }

    public static EmpDeductionCategory createRangedDeductionCategory(GenericService genericService,BusinessCertificate bc) {
        EmpDeductionCategory pEHB = new EmpDeductionCategory();
        pEHB.setName("Ranged");
        pEHB.setDescription("Ranged Deduction Category");
        pEHB.setRangedInd(1);
        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setCreatedBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        pEHB.setBusinessClientId(bc.getBusinessClientInstId());
        genericService.saveObject(pEHB);
        return pEHB;
    }

    public static int countNoOfIamAliveApprovals(GenericService genericService,BusinessCertificate bc, int pRunMonth, int pRunYear) {
        PredicateBuilder predicates = new PredicateBuilder();
        predicates.addPredicate( CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        predicates.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", 0));
        predicates.addPredicate(CustomPredicate.procurePredicate("runMonth", pRunMonth));
        predicates.addPredicate(CustomPredicate.procurePredicate("runYear", pRunYear));
        return genericService.countObjectsUsingPredicateBuilder(predicates, AmAliveApproval.class);
    }

    public static PayrollConfigImpact configurePayrollConfigImpact(GenericService genericService, ConfigurationBean configurationBean, PayrollRunMasterBean wPMB, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        PayrollConfigImpact payrollConfigImpact  = genericService.loadObjectUsingRestriction(PayrollConfigImpact.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("masterBean.id", wPMB.getId())));
        if(payrollConfigImpact.isNewEntity()){
            payrollConfigImpact = new PayrollConfigImpact();
            payrollConfigImpact.setBusinessClientId(bc.getBusinessClientInstId());
         }
        payrollConfigImpact.setMasterBean(wPMB);
        payrollConfigImpact.setCreatedBy(new User(bc.getLoginId()));
        payrollConfigImpact.setRequireBvn(configurationBean.getRequireBvn());
        payrollConfigImpact.setReqBiometricInfoInd(configurationBean.getReqBiometricInfoInd());
        payrollConfigImpact.setIamAlive(configurationBean.getIamAlive());
        payrollConfigImpact.setIamAliveExt(configurationBean.getIamAliveExt());
        payrollConfigImpact.setLastModBy(new User(bc.getLoginId()));
        payrollConfigImpact.setLastModTs(Timestamp.from(Instant.now()));
        payrollConfigImpact.setServiceLength(configurationBean.getServiceLength());
        payrollConfigImpact.setAgeAtRetirement(configurationBean.getAgeAtRetirement());


        return payrollConfigImpact;
    }

    public static SuspensionType loadAmAliveSuspensionType(BusinessCertificate bc, GenericService genericService) throws IllegalAccessException, InstantiationException {
        SuspensionType suspensionType = genericService.loadObjectUsingRestriction(SuspensionType.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("iAmAliveSusTypeInd",1)
        ));
        return  suspensionType;
    }
    public static void createReinstatementLog(BusinessCertificate bc, GenericService genericService, Long pStaffId,String EmployeeName, Long pMdaInfo,Long pSalaryId, @Nullable SchoolInfo schoolInfo,
                                              LocalDate pTermDate) throws Exception {
        //Check if there is already a reinstatement for this Pay Period.
        String payPeriod = PayrollUtils.makePayPeriodForAuditLogs(genericService, bc);
        ReinstatementLog s =genericService.loadObjectUsingRestriction(ReinstatementLog.class,
                Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),pStaffId),CustomPredicate.procurePredicate("auditPayPeriod",payPeriod)));
        if(s.isNewEntity()){
            if(bc.isPensioner())
                s.setPensioner(new Pensioner(pStaffId));
            else
                s.setEmployee(new Employee(pStaffId));
            s.setBusinessClientId(bc.getBusinessClientInstId());
            s.setPayArrearsInd(0);
            s.setArrearsStartDate(null);
            s.setArrearsEndDate(null);

        }
            s.setUser(new User(bc.getLoginId()));
            s.setReinstatementDate(LocalDate.now());
            s.setMdaInfo(new MdaInfo(pMdaInfo));
            s.setApprover(bc.getUserName());
            s.setLastModTs(LocalDate.now());
            s.setAuditTime(PayrollBeanUtils.getCurrentTime());
            s.setReferenceNumber(bc.getUserName());
            s.setEmployeeName(EmployeeName);
            s.setTerminationDate(pTermDate);
            s.setSalaryInfo(new SalaryInfo(pSalaryId));
            if (null != schoolInfo) {
                s.setSchoolInfo(schoolInfo);
            } else {
                s.setSchoolInfo(null);
            }
            s.setAuditPayPeriod(payPeriod);
         genericService.storeObject(s);
    }
}
