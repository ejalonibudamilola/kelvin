/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PaySlipService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.employee.HrPassportInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGratuity;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpGarnMiniBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpPayBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.*;


public class PensionPaycheckGenerator implements  IPaycheckGenerator{


    private HashMap<String, EmpDeductMiniBean> deductionMap;
    private HashMap<String, EmpGarnMiniBean> garnMap;
    private HashMap<String, EmpDeductMiniBean> allowances;
    private HashMap<String, EmpDeductMiniBean> specAllowMap;
    private HashMap<String, EmpDeductMiniBean> pensionsMap;
    private HashMap<String, EmpDeductMiniBean> gratuityMap;
    private PaySlipService paySlipService;

    private final String naira = "\u20A6";
    private boolean setYTDValues;

    /**
     * This method is for ONLY Pensioners.
     * @param empPayMiniBean
     * @param empPayBean
     * @param genericService
     * @param businessCertificate
     * @param pModel
     * @return
     * @throws Exception
     */
    public Object generatePaySlipModel(EmployeePayMiniBean empPayMiniBean, AbstractPaycheckEntity empPayBean, GenericService genericService, BusinessCertificate businessCertificate, Model pModel, ConfigurationBean configurationBean, PaySlipService paySlipService ) throws Exception
    {
        this.deductionMap = new HashMap<>();
        this.garnMap = new HashMap<>();
        this.allowances = new HashMap<>();
        this.specAllowMap = new HashMap<>();
        this.pensionsMap = new HashMap<>();
        this.gratuityMap = new HashMap<>();
        this.paySlipService = paySlipService;
        empPayMiniBean.setEmployeeName(empPayBean.getParentObject().getDisplayNameWivTitlePrefixed());
        empPayMiniBean.setId(empPayBean.getId());
        empPayMiniBean.setParentInstId(empPayBean.getParentObject().getId());
        empPayMiniBean.setEmployeeId(empPayBean.getParentObject().getEmployeeId());
        empPayMiniBean.setMda(empPayBean.getMdaDeptMap().getMdaInfo().getName());
        empPayMiniBean.setCurrentNetPay(empPayBean.getNetPay());
        empPayMiniBean.setCurrentTotalPay(empPayBean.getTotalPay());
        empPayMiniBean.setCurrentTaxesPaid(empPayBean.getTaxesPaid());
        empPayMiniBean.setPayType(empPayBean.getSalaryInfo().getSalaryType().getName());
        empPayMiniBean.setPayTypeDescr(empPayBean.getSalaryInfo().getSalaryType().getDescription());
        empPayMiniBean.setLevelAndStep(empPayBean.getSalaryInfo().getLevelAndStepAsStr());
        empPayMiniBean.setAccountNumber(empPayBean.getAccountNumber());
        empPayMiniBean.setBankBranchName(empPayBean.getBankBranch().getName());

        double monthlyBasic = empPayBean.getHiringInfo().getMonthlyPensionAmount();
        if (empPayBean.isPayByDays()) {
              monthlyBasic = EntityUtils.convertDoubleToEpmStandard(
                        (monthlyBasic / PayrollBeanUtils.getNoOfDays(empPayBean.getRunMonth(), empPayBean.getRunYear()))
                                * empPayBean.getNoOfDays());
        }
        empPayMiniBean.setBasicSalaryStr(this.naira + PayrollHRUtils.getDecimalFormat().format(monthlyBasic));
        if(empPayBean.getNetPay() == 0){
            empPayMiniBean.setBasicSalaryStr("0.00");
        }
        if (empPayBean.getStatus().equalsIgnoreCase("P")) {
            empPayMiniBean.setPaycheckStatus("UNAPPROVED PAYCHECK");
            empPayMiniBean.setDeletable(true);
            if(empPayBean.getReRunInd() == IConstants.ON){
                empPayMiniBean.setRerunPaycheck(true);
            }
        } else {
            empPayMiniBean.setPaycheckStatus("ADVICE OF DEPOSIT");
            empPayMiniBean.setApprovedPaycheck(true);
            empPayMiniBean.setDeletable(false);
        }

        empPayMiniBean.setPayPeriod(PayrollBeanUtils.getJavaDateAsString(empPayBean.getPayPeriodStart()) + " - " + PayrollBeanUtils.getJavaDateAsString(empPayBean.getPayPeriodEnd()));
        empPayMiniBean.setPayDate(PayrollBeanUtils.getJavaDateAsString(empPayBean.getPayDate()));
        empPayMiniBean.setRunMonth(empPayBean.getRunMonth());
        empPayMiniBean.setRunYear(empPayBean.getRunYear());
        if(configurationBean.isShowPikso()) {
            HrPassportInfo hrPassportInfo = genericService.loadObjectUsingRestriction(HrPassportInfo.class,Arrays.asList(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(),empPayBean.getParentObject().getId())));
            if (!hrPassportInfo.isNewEntity()) {
                empPayMiniBean.setEmployeePikso(hrPassportInfo.getPhoto());
                empPayMiniBean.setPhotoType(hrPassportInfo.getPhotoType());
                empPayMiniBean.setPiksoPresent(true);
            }
        }
        empPayMiniBean.setShowBank(configurationBean.isShowBankInfo());
       /* if(configurationBean.isShowRetirementDate()){
            empPayMiniBean.setShowRetireDate(configurationBean.isShowRetirementDate());
            empPayMiniBean.setExpectedRetirementDate(empPayBean.getHiringInfo().getExpDateOfRetireStr());
        }*/



        List<PaycheckGratuity> currGratuity = genericService.loadAllObjectsWithSingleCondition(PaycheckGratuity.class,CustomPredicate.procurePredicate("parentInstId",empPayBean.getId()),null);
      // List<NamedEntity> currGratuity = this.pensionService.loadPensionerGratuityPaymentByParentId(businessCertificate,empPayBean.getId());
        List<NamedEntity> currDed = paySlipService.loadPaySlipDependentObjects(businessCertificate,empPayBean.getId(),IConstants.DEDUCTION);
        if ((currDed != null) && (currDed.size() > 0)) {
            for (NamedEntity p : currDed) {
                EmpDeductMiniBean e = new EmpDeductMiniBean();
                e.setName(p.getName());
                 e.setCurrentDeduction(p.getDeductionAmount());
                 empPayMiniBean.setCurrentGarnTotal(empPayMiniBean.getCurrentGarnTotal() + p.getDeductionAmount());

                this.deductionMap.put(e.getName(), e);
            }
        }

        if (currGratuity != null && currGratuity.size() > 0) {
            EmpDeductMiniBean e;
            for (PaycheckGratuity p : currGratuity) {
                  e = new EmpDeductMiniBean();

                e.setName("Gratuity Payment (" + PayrollHRUtils.getDecimalFormat().format(p.getGratuityPercentage()) + "%)");
                e.setYearToDate(p.getAmount());
                e.setCurrentDeduction(p.getAmount());
                empPayMiniBean.setAllowanceTotal(empPayMiniBean
                        .getAllowanceTotal() + p.getAmount());
                empPayMiniBean.setCurrentAllowanceTotal(empPayMiniBean
                        .getCurrentAllowanceTotal() + p.getAmount());
                gratuityMap.put(e.getName(), e);

            }
        }
        List<NamedEntity> currSpecAllow = this.paySlipService.loadPaySlipDependentObjects(businessCertificate,empPayBean.getId(),IConstants.SPEC_ALLOW_IND);
        if ((currSpecAllow != null) && (currSpecAllow.size() > 0)) {
            for (NamedEntity p : currSpecAllow) {
                EmpDeductMiniBean e = new EmpDeductMiniBean();

                e.setName(p.getName());

                e.setCurrentDeduction(p.getDeductionAmount());

                empPayMiniBean.setCurrentAllowanceTotal(empPayMiniBean.getCurrentAllowanceTotal() + p.getDeductionAmount());
                this.specAllowMap.put(e.getName(), e);
            }
        }

        if (empPayBean.getUnionDues() > 0.0D) {
            EmpDeductMiniBean egmb = null;
            if (this.pensionsMap.containsKey("Union Dues")) {
                egmb = this.pensionsMap.get("Union Dues");
            }
            else {
                egmb = new EmpDeductMiniBean();
                egmb.setName("Union Dues");
            }
            egmb.setCurrentDeduction(empPayBean.getUnionDues());
            empPayMiniBean.setCurrentGarnTotal(empPayMiniBean.getCurrentGarnTotal() + empPayBean.getUnionDues());
            this.pensionsMap.put( egmb.getName(), egmb);
        }


        if (empPayBean.getArrears() > 0.0D) {
            EmpDeductMiniBean egmb = null;
            if (this.allowances.containsKey("Promotion Arrears")) {
                egmb = this.allowances.get("Promotion Arrears");
            }
            else {
                egmb = new EmpDeductMiniBean();
                egmb.setName("Promotion Arrears");
            }
            egmb.setCurrentDeduction(egmb.getCurrentDeduction() + empPayBean.getArrears());
            empPayMiniBean.setCurrentAllowanceTotal(empPayMiniBean.getCurrentAllowanceTotal() + empPayBean.getArrears());
            this.allowances.put(egmb.getName(), egmb);
        }
        if (empPayBean.getOtherArrears() > 0.0D) {
            EmpDeductMiniBean egmb = null;
            if (this.allowances.containsKey("Re-Absorption Arrears")) {
                egmb = this.allowances.get("Re-Absorption Arrears");
            }
            else {
                egmb = new EmpDeductMiniBean();
                egmb.setName("Re-Absorption Arrears");
            }
            egmb.setCurrentDeduction(egmb.getCurrentDeduction() + empPayBean.getOtherArrears());
            //egmb.setYearToDate(egmb.getYearToDate());
            empPayMiniBean.setCurrentAllowanceTotal(empPayMiniBean.getCurrentAllowanceTotal() + empPayBean.getOtherArrears());
            //empPayMiniBean.setAllowanceTotal(empPayMiniBean.getAllowanceTotal() + empPayBean.getOtherArrears());
            this.allowances.put( egmb.getName(), egmb);
        }

        Long empId = empPayBean.getParentObject().getId();

        List<NamedEntity> wInstList = new ArrayList<NamedEntity>();

        if (empPayBean.isPayByDays()) {
            if (empPayBean.getNoOfDays() == 1)
                wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" was paid for " + empPayBean.getNoOfDays() + " day"));
            else {
                wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" was paid for " + empPayBean.getNoOfDays() + " days"));
            }
        }

        if (empPayBean.getNetPay() == 0.0D) {
            if (empPayBean.getRejectedForPayrollingInd() == IConstants.ON) {
                wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" NOT currently approved for Payment on the IPPMS."));
            } else {
                if (empPayBean.getSuspendedInd() == IConstants.ON)
                    wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" was on Suspension"));
                else {
                     if (empPayBean.isTerminationCandidate()) {
                        wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" was not paid (Termination Candidate)"));
                        wInstList.add(new NamedEntity(
                                "Expected Retirement Date - " + empPayBean.getHiringInfo().getExpDateOfRetireStr()));
                    }else if(empPayBean.getBvnNo() != null && empPayBean.getBvnNo().equals("NS")) {
                        wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" was not paid (No BVN Supplied.)"));
                    }else if(empPayBean.getAccountNumber() != null && empPayBean.getAccountNumber().equals("NS")) {
                        wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" was not paid (Account Number Not Supplied.)"));
                    }else if(empPayBean.isIamAliveTermination()) {
                        wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" was not paid (I Am Alive)"));
                        wInstList.add(new NamedEntity(
                                businessCertificate.getStaffTypeName()+" Birth Date - " + empPayBean.getHiringInfo().getBirthDateStr()));
                        wInstList.add(new NamedEntity(
                                "Current Age Using I Am Alive - " + empPayBean.getHiringInfo().getExpDateOfRetireStr()));
                    }else if(empPayBean.getBiometricInd() == 1){
                         wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" Biometric Information not verified with the Biometric Database"));
                     }
                }
            }
        }
        if (empPayBean.isPercentagePayment()) {
            wInstList.add(new NamedEntity(businessCertificate.getStaffTypeName()+" was paid a percentage of their salary  (" + empPayBean.getPayPercentageStr() + ")"));
        }

        LocalDate today = LocalDate.of(empPayBean.getPayPeriodEnd().getYear(), empPayBean.getPayPeriodEnd().getMonthValue(), empPayBean.getPayPeriodEnd().getDayOfMonth());

        LocalDate yearStart = LocalDate.of(today.getYear(),1,1);



        List<AbstractPaycheckEntity> empYTDList;

        if(today.getYear() < empPayBean.getPayPeriodEnd().getYear()){
            empYTDList = new ArrayList<>();
        }else{
           empYTDList = (List<AbstractPaycheckEntity>) genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(businessCertificate),
                    Arrays.asList(CustomPredicate.procurePredicate("employee.id", empId),
                            CustomPredicate.procurePredicate("runMonth", today.getMonthValue(), Operation.LESS_OR_EQUAL),
                            CustomPredicate.procurePredicate("runYear", empPayBean.getPayPeriodEnd().getYear())),null);
        }


        if ((empYTDList != null) && (empYTDList.size() > 0)) {
            for (AbstractPaycheckEntity e : empYTDList) {
                if (e.isIgnoreYtdValues() || (e.getRunMonth() == empPayBean.getRunMonth() && e.isUnApprovedPaycheck()))
                    continue;


                empPayMiniBean.setTaxesPaidYTD(empPayMiniBean.getTaxesPaidYTD() + e.getTaxesPaid());
                empPayMiniBean.setSalaryYTD(empPayMiniBean.getSalaryYTD() + e.getTotalPay());

                if (e.getTotalDeductions() > 0.0D) {
                   /* List<AbstractPaycheckDeductionEntity> pDed = (List<AbstractPaycheckDeductionEntity>)genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckDeductionClass(businessCertificate),Arrays.asList(CustomPredicate.procurePredicate("employee.id", empId),
                            CustomPredicate.procurePredicate("payPeriodStart", yearStart, Operation.GREATER_OR_EQUAL),
                            CustomPredicate.procurePredicate("payPeriodEnd", empPayBean.getPayPeriodEnd(),Operation.LESS)),null);*/
                    List<NamedEntity> pDed = this.paySlipService.loadPaySlipDependentObjectsYTD(businessCertificate,empId,yearStart,today,IConstants.DEDUCTION);

                    for (NamedEntity p : pDed) {
                        EmpDeductMiniBean eDMB;
                        if (!this.deductionMap.isEmpty())
                        {
                            eDMB = this.deductionMap.get(p.getName());
                            if (eDMB == null)
                            {
                                eDMB = new EmpDeductMiniBean();

                                eDMB.setName(p.getName());

                            }
                        }
                        else {
                            eDMB = new EmpDeductMiniBean();

                            eDMB.setName(p.getName());

                        }
                        eDMB.setYearToDate(eDMB.getYearToDate() + p.getDeductionAmount());
                        empPayMiniBean.setGarnishmentTotal(empPayMiniBean.getGarnishmentTotal() + p.getDeductionAmount());
                        this.deductionMap.put(eDMB.getName(), eDMB);
                    }
                }

                if (e.getTotalAllowance() > 0.0D) {
                    /*List<AbstractPaycheckSpecAllowEntity> pCont = (List<AbstractPaycheckSpecAllowEntity>)genericService.loadAllObjectsUsingRestrictions(IppmsUtils.makePaycheckSpecAllowClass(businessCertificate),Arrays.asList(CustomPredicate.procurePredicate("employee.id", empId),
                            CustomPredicate.procurePredicate("payPeriodStart", yearStart, Operation.GREATER_OR_EQUAL),
                            CustomPredicate.procurePredicate("payPeriodEnd", empPayBean.getPayPeriodEnd(),Operation.LESS)),null);*/
                    List<NamedEntity> pCont = this.paySlipService.loadPaySlipDependentObjectsYTD(businessCertificate,empId,yearStart,today,IConstants.SPEC_ALLOW_IND);

                    for (NamedEntity p : pCont) {
                        EmpDeductMiniBean eCMB;
                        if (!this.specAllowMap.isEmpty()) {
                            eCMB = this.specAllowMap.get(p.getName());
                            if (eCMB == null)
                            {
                                eCMB = new EmpDeductMiniBean();

                                eCMB.setName(p.getName());
                            }

                        } else {
                            eCMB = new EmpDeductMiniBean();

                            eCMB.setName(p.getName());
                        }
                        eCMB.setYearToDate(eCMB.getYearToDate() + p.getDeductionAmount());
                        empPayMiniBean.setAllowanceTotal(empPayMiniBean.getAllowanceTotal() + p.getDeductionAmount());
                        this.specAllowMap.put(eCMB.getName(), eCMB);
                    }
                }
               /* if (e.getTotalGarnishments() > 0.0D) {
                    List<AbstractPaycheckGarnishmentEntity> pGarn = (List<AbstractPaycheckGarnishmentEntity>)genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckGarnishmentClass(businessCertificate),Arrays.asList(CustomPredicate.procurePredicate("employee.id", empId),
                            CustomPredicate.procurePredicate("payPeriodStart", yearStart, Operation.GREATER_OR_EQUAL),
                            CustomPredicate.procurePredicate("payPeriodEnd", empPayBean.getPayPeriodEnd(),Operation.LESS)),null);

                    for (AbstractPaycheckGarnishmentEntity p : pGarn) {
                        EmpGarnMiniBean eGMB;
                        if (!this.garnMap.isEmpty()) {
                            eGMB = this.garnMap.get(p.getEmpGarnInfo().getDescription());
                            if (eGMB == null)
                            {
                                eGMB = new EmpGarnMiniBean();
                                eGMB.setId(p.getEmpGarnInfo().getId());
                                eGMB.setName(p.getEmpGarnInfo().getDescription());

                            }
                        } else {
                            eGMB = new EmpGarnMiniBean();
                            eGMB.setId(p.getEmpGarnInfo().getId());
                            eGMB.setName(p.getEmpGarnInfo().getDescription());

                        }
                        eGMB.setYearToDate(eGMB.getYearToDate() + p.getAmount());
                        empPayMiniBean.setGarnishmentTotal(empPayMiniBean.getGarnishmentTotal() + p.getAmount());
                        this.garnMap.put(eGMB.getName(), eGMB);

                    }

                }
*/
                if (e.getUnionDues() > 0.0D) {
                    EmpDeductMiniBean egmb = null;
                    if (this.pensionsMap.containsKey("Union Dues")) {
                        egmb = this.pensionsMap.get("Union Dues");
                    }
                    else {
                        egmb = new EmpDeductMiniBean();
                        egmb.setName("Union Dues");
                    }

                    egmb.setYearToDate(egmb.getYearToDate() + e.getUnionDues());
                    empPayMiniBean.setGarnishmentTotal(empPayMiniBean.getGarnishmentTotal() + e.getUnionDues());

                    this.pensionsMap.put(egmb.getName(), egmb);
                }
                if (e.getDevelopmentLevy() > 0.0D) {
                    EmpGarnMiniBean egmb = null;
                    if (this.garnMap.containsKey("Dev. Levy")) {
                        egmb = this.garnMap.get("Dev. Levy");
                    }
                    else {
                        egmb = new EmpGarnMiniBean();
                        egmb.setName("Dev. Levy");
                    }
                    egmb.setYearToDate(egmb.getCurrentGarnishment() + e.getDevelopmentLevy());
                    empPayMiniBean.setGarnishmentTotal(empPayMiniBean.getGarnishmentTotal() + e.getDevelopmentLevy());

                    this.garnMap.put(egmb.getName(), egmb);
                }


                if (e.getArrears() > 0.0D) {
                    EmpDeductMiniBean egmb = null;
                    if (this.allowances.containsKey("Promotion Arrears")) {
                        egmb = this.allowances.get("Promotion Arrears");
                    }
                    else {
                        egmb = new EmpDeductMiniBean();
                        egmb.setName("Promotion Arrears");
                    }

                    egmb.setYearToDate(egmb.getYearToDate() + e.getArrears());

                    empPayMiniBean.setAllowanceTotal(empPayMiniBean.getAllowanceTotal() + e.getArrears());
                    this.allowances.put(egmb.getName(), egmb);
                }
                if (e.getOtherArrears() > 0.0D) {
                    EmpDeductMiniBean egmb = null;
                    if (this.allowances.containsKey("Re-Absorbtion Arrears")) {
                        egmb = this.allowances.get("Re-Absorbtion Arrears");
                    }
                    else {
                        egmb = new EmpDeductMiniBean();
                        egmb.setName("Re-Absorbtion Arrears");
                    }

                    egmb.setYearToDate(egmb.getYearToDate() + e.getOtherArrears());

                    empPayMiniBean.setAllowanceTotal(empPayMiniBean.getAllowanceTotal() + e.getOtherArrears());
                    this.allowances.put(egmb.getName(), egmb);
                }
                if (e.getSalaryDifference() != 0.0D)
                {
                    if (e.getSalaryDifference() > 0.0D) {
                        EmpDeductMiniBean egmb = null;
                        if (this.allowances.containsKey("Salary Diff(UP)")) {
                            egmb = this.allowances.get("Salary Diff(UP)");
                        }
                        else {
                            egmb = new EmpDeductMiniBean();
                            egmb.setName("Salary Diff(UP)");
                        }

                        egmb.setYearToDate(egmb.getYearToDate() + e.getSalaryDifference());
                        empPayMiniBean.setAllowanceTotal(empPayMiniBean.getAllowanceTotal() + e.getSalaryDifference());
                        this.allowances.put(egmb.getName(), egmb);
                    } else {
                        EmpGarnMiniBean egmb = null;
                        if (this.garnMap.containsKey("Salary Diff(OP)")) {
                            egmb = this.garnMap.get("Salary Diff(OP)");
                        }
                        else {
                            egmb = new EmpGarnMiniBean();
                            egmb.setName("Salary Diff(OP)");
                        }
                        egmb.setYearToDate(egmb.getYearToDate() + e.getSalaryDifference() * -1.0D);
                        empPayMiniBean.setGarnishmentTotal(empPayMiniBean.getGarnishmentTotal() + e.getSalaryDifference() * -1.0D);

                        this.garnMap.put(egmb.getName(), egmb);
                    }

                }

              // PayslipHelperBean payslipHelperBean = PaycheckGeneratorHelper.setPayGroupAllowances(e.getSalaryInfo(), empPayMiniBean, e,this.allowances, false);
            //    this.allowances = payslipHelperBean.getAllowances();
               // empPayMiniBean = payslipHelperBean.getEmployeePayMiniBean();
            }
        } else {
//            if(empPayBean.getNetPay() != 0) {
//                PayslipHelperBean payslipHelperBean = PaycheckGeneratorHelper.setYTDAllowanceValues(empPayBean.getSalaryInfo(), empPayMiniBean, empPayBean, this.allowances);
//                this.allowances = payslipHelperBean.getAllowances();
//                empPayMiniBean = payslipHelperBean.getEmployeePayMiniBean();
//            }

            setYTDValues = !setYTDValues;
            empPayMiniBean.setGarnishmentTotal(empPayMiniBean.getCurrentGarnTotal());
            empPayMiniBean.setAllowanceTotal(empPayMiniBean.getCurrentAllowanceTotal());
        }

        empPayMiniBean.setEmployerName(businessCertificate.getBusinessName());
        empPayMiniBean.setEmployerState(businessCertificate.getStateName());

        empPayMiniBean.setEmployerAddress(businessCertificate.getBizAddr());
        empPayMiniBean.setEmployerCityStateZip(businessCertificate.getCityName());


        empPayMiniBean.setEmployeeState(empPayBean.getParentObject().getCity().getState().getFullName());
        empPayMiniBean.setEmployeeAddress(empPayBean.getParentObject().getAddress1() );
        empPayMiniBean.setEmployeeCityStateZip(empPayBean.getParentObject().getCity().getName() + ", " + empPayMiniBean.getEmployerState() + " " + StringUtils.trimToEmpty(empPayBean.getParentObject().getZipCode()));
        if (empPayMiniBean.getHoursWorked() > 0.0D)
            empPayMiniBean.setTotalHours(Double.toString(empPayMiniBean.getHoursWorked()));
        else {
            empPayMiniBean.setTotalHours("-");
        }

        List<EmpDeductMiniBean>  deductList = getDeductionList(this.deductionMap);
        if(setYTDValues) {
            deductList = this.setYTDValues(deductList);
            empPayMiniBean.setTaxesPaidYTD(empPayMiniBean.getCurrentTaxesPaid());
            empPayMiniBean.setSalaryYTD(empPayMiniBean.getCurrentTotalPay());
        }

        this.deductionMap = null;
        List<EmpGarnMiniBean>  garnList = getGarnishmentList(this.garnMap);
        if(setYTDValues) {
            garnList = this.setGarnYTDValues(garnList);
        }
        this.garnMap = null;
        List<EmpDeductMiniBean> mandatoryList = getDeductionList(this.allowances);
        this.allowances = null;
        List<EmpDeductMiniBean> allowList = getDeductionList(this.specAllowMap);
        this.specAllowMap = null;
        if(setYTDValues) {
            allowList = this.setYTDValues(allowList);
        }
        Collections.sort(allowList);

        List<EmpDeductMiniBean> pensionList = getDeductionList(this.pensionsMap);
        if(setYTDValues) {
            pensionList = this.setYTDValues(pensionList);
        }
        this.pensionsMap = null;
        Collections.sort(pensionList);

        empPayMiniBean.setDateToUse(empPayBean.getPayPeriodStart());
        empPayMiniBean.setMandList(mandatoryList);
        empPayMiniBean.setDeductList(deductList);
        empPayMiniBean.setGarnList(garnList);
        empPayMiniBean.setAllowanceList(allowList);
        empPayMiniBean.setPensionsContList(pensionList);
        empPayMiniBean.setInstructionList(wInstList);

        if(pModel == null) {
            EmpDeductMiniBean totalPay = new EmpDeductMiniBean();
            totalPay.setName("Gross Pay");
            totalPay.setCurrentDeduction(empPayMiniBean.getCurrentTotalPay());
            totalPay.setYearToDate(empPayMiniBean.getSalaryYTD());


            EmpDeductMiniBean totalDeductions = new EmpDeductMiniBean();
            totalDeductions.setName("Deductions");
            totalDeductions.setCurrentDeduction(empPayMiniBean.getCurrentGarnTotal());
            totalDeductions.setYearToDate(empPayMiniBean.getGarnishmentTotal());


            EmpDeductMiniBean taxesPayable = new EmpDeductMiniBean();
            taxesPayable.setName("Taxes Paid");
            taxesPayable.setCurrentDeduction(empPayMiniBean.getCurrentTaxesPaid());
            taxesPayable.setYearToDate(empPayMiniBean.getTaxesPaidYTD());



            List<EmpDeductMiniBean> summaryList = new ArrayList<EmpDeductMiniBean>();
            summaryList.add(totalPay);
            summaryList.add(totalDeductions);

            summaryList.add(taxesPayable);
            empPayMiniBean.setSummaryList(summaryList);
            List<EmpDeductMiniBean> statList = new ArrayList<EmpDeductMiniBean>();
            taxesPayable.setName(empPayMiniBean.getEmployeeState()+" Income Tax");
            statList.add(taxesPayable);
            empPayMiniBean.setStatutoryDedList(statList);
            List<EmpPayBean> payList = new ArrayList<EmpPayBean>();
            EmpPayBean payBean = new EmpPayBean();
            payBean.setPayType(empPayMiniBean.getPayType());
            payBean.setLevelStepStr(empPayMiniBean.getLevelAndStep());
            payBean.setBasicSalaryStr(empPayMiniBean.getBasicSalaryStr());
            payBean.setCurrentTotalPay(empPayMiniBean.getCurrentTotalPay());
            payBean.setSalaryYTD(empPayMiniBean.getSalaryYTD());

            payList.add(payBean);
            empPayMiniBean.setPayList(payList);

            return empPayMiniBean;
        }


        pModel.addAttribute("allowanceList", allowList);
        pModel.addAttribute("mandatoryList", mandatoryList);
        pModel.addAttribute("deductList", deductList);
        pModel.addAttribute("garnList", garnList);
        pModel.addAttribute("pensionList", pensionList);
        pModel.addAttribute("empPayMiniBean", empPayMiniBean);

        return pModel;
    }



    private List<EmpGarnMiniBean> setGarnYTDValues(List<EmpGarnMiniBean> garnList) {
        for(EmpGarnMiniBean m : garnList) {
            m.setYearToDate(m.getCurrentGarnishment());
        }
        return garnList;
    }



    private List<EmpDeductMiniBean> setYTDValues(List<EmpDeductMiniBean> deductList) {
        for(EmpDeductMiniBean m : deductList) {
            m.setYearToDate(m.getCurrentDeduction());
        }

        return deductList;
    }



    private List<EmpGarnMiniBean> getGarnishmentList(HashMap<String, EmpGarnMiniBean> garnMap2)
    {
        List<EmpGarnMiniBean> list = new ArrayList<>();

        Set<Map.Entry<String, EmpGarnMiniBean>> set = garnMap2.entrySet();
        Iterator<Map.Entry<String, EmpGarnMiniBean>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, EmpGarnMiniBean> me = i.next();
            list.add(me.getValue());
        }
        Collections.sort(list);
        return list;
    }

    private List<EmpDeductMiniBean> getDeductionList(HashMap<String, EmpDeductMiniBean> pDeductionMap)
    {
        List<EmpDeductMiniBean> list = new ArrayList<>();

        Set<Map.Entry<String, EmpDeductMiniBean>> set = pDeductionMap.entrySet();
        Iterator<Map.Entry<String, EmpDeductMiniBean>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, EmpDeductMiniBean> me = i.next();
            list.add(me.getValue());
        }
        Collections.sort(list);
        return list;
    }


}
