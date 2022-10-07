/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.audit.domain.HiringInfoAudit;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityMasterBean;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfig;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGratuity;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.ON;

@Getter
@Setter
@Slf4j
public class CalculatePayPerPensioner {


    private Map<Long, SalaryInfo> salaryInfoMap;
    private List<Long> absorptions;
    private List<Long> reinstatements;
    private Map<Long, SuspensionLog> partPaymentMap;
    private boolean deductDevelopmentLevy;

    private boolean hasSalaryDifference;
    private BusinessCertificate businessCertificate;
    private int runMonth;
    private int runYear;
    private boolean useIamAlive;
    private Map<Long, List<AbstractDeductionEntity>> employeeDeductions;
    // private Map<Long, List<EmpGarnishmentInfo>> employeeGarnishments;
    private Map<Long, List<AbstractSpecialAllowanceEntity>> specialAllowances;
    private GlobalPercentConfig globalPercentConfig;
    private ConfigurationBean configurationBean;

    private LocalDate sixtyYearsAgo;
    private LocalDate thirtyFiveYearsAgo;
    private LocalDate payPeriodEnd;
    private int noOfDays;
    private GenericService genericService;

    private boolean rerun;
    // private SimpleDateFormat sdf;

    private boolean payGratuity;

    private HashMap<String, Double> gratuityPaymentMethodMap;

    private HashMap<Long, NamedEntity> employeeGratuityPaymentMap;

    private GratuityMasterBean gratuityMasterBean;

    private double maxNoOfDays;


    private Map<Long, TerminateReason> terminateReasonMap;

    private LocalDate payPeriodStart;

    private HashMap<Long, List<HiringInfoAudit>> hireInfoAudit;


    public AbstractPaycheckEntity calculatePayroll(HiringInfo pHiringInfo) {



        AbstractPaycheckEntity pEmpPayBean = this.determinePayStatus(pHiringInfo, configurationBean);

        BigDecimal wUnionDueValueBD = new BigDecimal("0.01").setScale(2, RoundingMode.HALF_EVEN);

        if (!pEmpPayBean.isDoNotPay()) {

            if (pEmpPayBean.isPayByDays()) {


                // This should work a little different

                double actualPay = pEmpPayBean.getHiringInfo().getMonthlyPensionAmount();
                pEmpPayBean.setSalaryInfo(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getSalaryInfo());
                double payAmt = actualPay;

                // First Get the Special Allowances if this dude has that are
                // NOT TAXABLE...
                double specialAllowance = this.getSpecialAllowanceByDays(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId(), payAmt, pEmpPayBean);

                // Gross Pay
                // this.maxNoOfDays) * pNoOfDays)));
                BigDecimal wMonthlyPensionAmtBD = new BigDecimal(
                        Double.toString(EntityUtils
                                .convertDoubleToEpmStandard((pEmpPayBean
                                        .getHiringInfo()
                                        .getMonthlyPensionAmount()
                                        / getNoOfDays() * pEmpPayBean
                                        .getNoOfDays()))));

                BigDecimal wSpecialAllowanceBD = new BigDecimal(
                        Double.toString(EntityUtils
                                .convertDoubleToEpmStandard(specialAllowance
                                        / getNoOfDays()
                                        * pEmpPayBean.getNoOfDays())));


                BigDecimal wTotalPayBD = new BigDecimal(Double.toString(0.00));
                wTotalPayBD = wTotalPayBD.add(wSpecialAllowanceBD)
                        .add(wMonthlyPensionAmtBD);

                pEmpPayBean.setTotalPay(wTotalPayBD.doubleValue());

                double totalDeductions = new BigDecimal(
                        Double.toString(wTotalPayBD.doubleValue() * .01))
                        .setScale(2, RoundingMode.HALF_EVEN).doubleValue();

                pEmpPayBean.setTotalDeductions(totalDeductions);

                pEmpPayBean.setUnionDues(totalDeductions);

                pEmpPayBean.setNetPay(pEmpPayBean.getTotalPay()
                        - totalDeductions);

                if (this.deductDevelopmentLevy) {
                    pEmpPayBean.setDevelopmentLevy(IConstants.DEVELOPMENT_LEVY);
                    pEmpPayBean.setNetPay(pEmpPayBean.getNetPay()
                            - IConstants.DEVELOPMENT_LEVY);
                    totalDeductions += IConstants.DEVELOPMENT_LEVY;
                }


                pEmpPayBean.setMonthlyBasic(wMonthlyPensionAmtBD.doubleValue());


            } else {
                // Normal Salary Payment

                double actualPay = pEmpPayBean.getHiringInfo().getMonthlyPensionAmount();
                pEmpPayBean.setSalaryInfo(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getSalaryInfo());

                double payAmt = actualPay;

                double specialAllowance = this
                        .getSpecialAllowance(pEmpPayBean
                                        .getHiringInfo().getAbstractEmployeeEntity().getId(), payAmt,
                                pEmpPayBean);


                BigDecimal wMonthlyPensionAmtBD = new BigDecimal(
                        Double.toString(EntityUtils
                                .convertDoubleToEpmStandard(payAmt)));

                BigDecimal wSpecialAllowanceBD = new BigDecimal(
                        Double.toString(EntityUtils
                                .convertDoubleToEpmStandard(specialAllowance)));


                BigDecimal wTotalPayBD = new BigDecimal(Double.toString(0.00));
                wTotalPayBD = wTotalPayBD.add(wSpecialAllowanceBD)
                        .add(wMonthlyPensionAmtBD);

                // Now find out if we have Gratuity Payments....
                if (this.payGratuity) {
                    if (this.getEmployeeGratuityPaymentMap().containsKey(
                            pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId())) {

                        pEmpPayBean = this.getGratuityPayment(pEmpPayBean);
                        BigDecimal wGratuityPayment = new BigDecimal(
                                Double.toString(pEmpPayBean.getGratuityAmountPaid()));

                        wTotalPayBD = wTotalPayBD.add(wGratuityPayment);


                    }

                }

                pEmpPayBean.setTotalPay(wTotalPayBD.doubleValue());
                BigDecimal wUnionDue = wTotalPayBD.multiply(wUnionDueValueBD).setScale(2,
                        RoundingMode.HALF_EVEN);

                double totalDeductions = wUnionDue.doubleValue();

                pEmpPayBean.setTotalDeductions(totalDeductions);

                pEmpPayBean.setUnionDues(totalDeductions);

                // Remove Employee Deductions and Garnishments...

                pEmpPayBean = this
                        .removeEmployeeSpecificDeductions(pEmpPayBean);

                // pEmpPayBean = this.removeEmployeeLoans(pEmpPayBean);

                totalDeductions = pEmpPayBean.getTotalDeductions()
                        + pEmpPayBean.getTotalGarnishments();

                pEmpPayBean.setNetPay(pEmpPayBean.getTotalPay()
                        - totalDeductions);

                if (this.deductDevelopmentLevy) {
                    pEmpPayBean.setDevelopmentLevy(IConstants.DEVELOPMENT_LEVY);
                    pEmpPayBean.setNetPay(pEmpPayBean.getNetPay()
                            - IConstants.DEVELOPMENT_LEVY);
                    pEmpPayBean
                            .setTotalDeductions(pEmpPayBean
                                    .getTotalDeductions()
                                    + IConstants.DEVELOPMENT_LEVY);
                }

                pEmpPayBean.setMonthlyPension(wMonthlyPensionAmtBD
                        .doubleValue());


            }

        } else {
            pEmpPayBean.setSalaryInfo(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getSalaryInfo());
            pEmpPayBean.setTotalPay(0);

            pEmpPayBean.setMeal(0);
            pEmpPayBean.setCallDuty(0);
            pEmpPayBean.setDomesticServant(0);
            pEmpPayBean.setDriversAllowance(0);
            pEmpPayBean.setEntertainment(0);
            pEmpPayBean.setFurniture(0);
            pEmpPayBean.setHazard(0);
            pEmpPayBean.setInducement(0);
            pEmpPayBean.setJournal(0);
            pEmpPayBean.setNhf(0);
            pEmpPayBean.setRent(0);
            pEmpPayBean.setRuralPosting(0);
            pEmpPayBean.setUtility(0);
            pEmpPayBean.setUnionDues(0);
            pEmpPayBean.setTransport(0);
            pEmpPayBean.setAcademicAllowance(0);
            pEmpPayBean.setTss(0);
            pEmpPayBean.setGrossPay(0);

            pEmpPayBean.setTaxesPaid(0);
            pEmpPayBean.setNetPayAfterTaxes(0);
            pEmpPayBean.setOtherAllowance(0);

            pEmpPayBean.setNetPayAfterTaxableDeductions(0);

            pEmpPayBean.setContributoryPension(0);

            // Now add all allowances
            pEmpPayBean.setNetPay(0);


        }
        pEmpPayBean.setParentObject(new Pensioner(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId()));
        pEmpPayBean.setRunMonth(runMonth);
        pEmpPayBean.setRunYear(runYear);
        return pEmpPayBean;
    }

    private AbstractPaycheckEntity getGratuityPayment(AbstractPaycheckEntity pEmpPayBean) {
        BigDecimal wRetVal;
        NamedEntity wNamedEntity = this.getEmployeeGratuityPaymentMap().get(
                pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId());
        if (wNamedEntity == null)
            return pEmpPayBean;

        // Now make the Month and Year into String value (month:year) and use it
        // to get the percentage value...
        String wKey = wNamedEntity.getNoOfEmployees() + ":"
                + wNamedEntity.getPageSize();
        Double wDouble = this.getGratuityPaymentMethodMap().get(wKey);
        if (wDouble == null)
            return pEmpPayBean;
        // Else
        BigDecimal wRateAmt = new BigDecimal(Double.toString(wDouble.doubleValue()));
        wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4,
                RoundingMode.FLOOR);
        BigDecimal wOutStandingBD = new BigDecimal(Double.toString(wNamedEntity
                .getAllowanceAmount()));
        wRetVal = wOutStandingBD.multiply(wRateAmt);

        pEmpPayBean.setGratuityAmountPaid(wRetVal.doubleValue());
        // Now Add the Paycheck Gratuity....
        PaycheckGratuity wPG = new PaycheckGratuity();
        wPG.setGratuityInfo(new GratuityInfo(wNamedEntity.getId()));
        BigDecimal wRemnant = new BigDecimal(Double.toString(wOutStandingBD
                .doubleValue() - wRateAmt.doubleValue())).setScale(2,
                RoundingMode.HALF_EVEN);
        wPG.setCurrBalAmt(wRemnant.doubleValue());
        wPG.setAmount(wRetVal.doubleValue());
        wPG.setRunMonth(this.runMonth);
        wPG.setRunYear(this.runYear);
        wPG.setGratuityPercentage(wDouble);
        pEmpPayBean.setPaycheckGratuity(wPG);
        pEmpPayBean.setGratuityAmountPaid(wRetVal.doubleValue());
        return pEmpPayBean;
    }


    private AbstractPaycheckEntity removeEmployeeSpecificDeductions(AbstractPaycheckEntity pE) {
        BigDecimal wRetVal = new BigDecimal(pE.getTotalPay()).setScale(2,
                RoundingMode.HALF_EVEN);
        BigDecimal wMonthlyBasicBD = new BigDecimal(pE.getTotalPay()).setScale(
                2, RoundingMode.HALF_EVEN);

        BigDecimal wTotalDeductionsBD = new BigDecimal(pE.getTotalDeductions())
                .setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal wTotalCurrDedBD = new BigDecimal("0.00").setScale(2,
                RoundingMode.HALF_EVEN);

        // double retVal = pCurrentNetPay;
        double rate = 0.0D;

        if (pE.getTotalPay() < 1.0D) {

            return pE;
        }

        List<AbstractDeductionEntity> pEmpDedList = this.employeeDeductions
                .get(pE.getHiringInfo().getAbstractEmployeeEntity().getId());
        if ((pEmpDedList == null) || (pEmpDedList.isEmpty())) {

            return pE;
        }

        for (AbstractDeductionEntity empDed : pEmpDedList) {

            if (empDed.getAmount() > 0.0D) {

                BigDecimal deductionAmount;
                BigDecimal wWorkingDeductionAmount;
                if (empDed.getPayTypes().isUsingPercentage()) {
                    rate = empDed.getAmount();
                    if ((rate >= 100.0D) || (rate < 0.0D)) {
                        continue;
                    }

                    BigDecimal wRateAmt = new BigDecimal(Double.toString(rate));
                    wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4,
                            RoundingMode.FLOOR);


                    wWorkingDeductionAmount = wMonthlyBasicBD
                            .multiply(wRateAmt);

                } else {
                    wWorkingDeductionAmount = new BigDecimal(
                            empDed.getAmount()).setScale(2,
                            RoundingMode.HALF_EVEN);


                }
                deductionAmount = new BigDecimal(
                        String.valueOf(wWorkingDeductionAmount
                                .doubleValue())).setScale(2,
                        RoundingMode.FLOOR);
                if (wRetVal.subtract(deductionAmount).doubleValue() <= 0.0D)
                    continue;
                wRetVal = wRetVal.subtract(deductionAmount);

                if (pE.getEmployeeDeductions() == null) {
                    pE.setEmployeeDeductions(new ArrayList<>());
                }

                empDed.setAmount(deductionAmount.doubleValue());
                pE.getEmployeeDeductions().add(empDed);
                wTotalCurrDedBD = wTotalCurrDedBD.add(deductionAmount);
                wTotalDeductionsBD = wTotalDeductionsBD.add(deductionAmount);

            }
        }
        pE.setTotalDeductions(wTotalDeductionsBD.doubleValue());
        pE.setTotCurrDed(wTotalCurrDedBD.doubleValue());
        pE.setNetPayAfterTaxableDeductions(wRetVal.doubleValue());
        pE.setNetPay(wRetVal.doubleValue());
        return pE;
    }

    private double getSpecialAllowance(Long pId,
                                       double pMonthlyBasic, AbstractPaycheckEntity pEmpPayBean) {
        // double retVal = 0.0;
        BigDecimal wRetValBD = new BigDecimal(Double.toString(0.00)).setScale(
                2, RoundingMode.HALF_EVEN);

        double rate = 0.0;

        List<AbstractSpecialAllowanceEntity> pEmpAllowList = this.specialAllowances
                .get(pId);
        if (pEmpAllowList == null || pEmpAllowList.isEmpty())
            return wRetValBD.doubleValue();

        BigDecimal wMonthlyBasicBD = new BigDecimal(
                Double.toString(pMonthlyBasic)).setScale(2,
                RoundingMode.HALF_EVEN);

        BigDecimal wTotalContribBD = new BigDecimal(Double.toString(pEmpPayBean
                .getTotalContributions())).setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal wSpecialAllowBD = new BigDecimal(Double.toString(pEmpPayBean
                .getSpecialAllowance())).setScale(2, RoundingMode.HALF_EVEN);

        for (AbstractSpecialAllowanceEntity empDed : pEmpAllowList) {

            BigDecimal wAllowanceAmt = new BigDecimal("0.00").setScale(2,
                    RoundingMode.HALF_EVEN);
            if (empDed.getPayTypes().isUsingPercentage()) {
                // This is a percentage.
                rate = empDed.getAmount();
                if (rate >= 100 || rate < 0) {
                    continue;
                } else {

                    BigDecimal wRateAmt = new BigDecimal(Double.toString(rate));
                    wRateAmt = wRateAmt.divide(new BigDecimal(100), 2,
                            RoundingMode.HALF_EVEN);

                    wAllowanceAmt = wMonthlyBasicBD.multiply(wRateAmt)
                            .setScale(2, RoundingMode.HALF_EVEN);

                    wRetValBD = wRetValBD.add(wAllowanceAmt);
                    empDed.setActAllowAmt(wAllowanceAmt.doubleValue());

                }
            } else {

                wAllowanceAmt = new BigDecimal(Double.toString(empDed
                        .getAmount())).setScale(2, RoundingMode.HALF_EVEN);

                wRetValBD = wRetValBD.add(wAllowanceAmt);
                empDed.setActAllowAmt(wAllowanceAmt.doubleValue());

            }
            if (pEmpPayBean.getSpecialAllowanceList() == null)
                pEmpPayBean
                        .setSpecialAllowanceList(new ArrayList<>());

            pEmpPayBean.getSpecialAllowanceList().add(empDed);

            wTotalContribBD = wTotalContribBD.add(wAllowanceAmt);
            wSpecialAllowBD = wSpecialAllowBD.add(wAllowanceAmt);
        }
        pEmpPayBean.setTotalContributions(wTotalContribBD.doubleValue());
        pEmpPayBean.setSpecialAllowance(wSpecialAllowBD.doubleValue());


        return wRetValBD.doubleValue();
    }

    private double getSpecialAllowanceByDays(Long pId,
                                             double pMonthlyBasic, AbstractPaycheckEntity pEmpPayBean) {
        // double retVal = 0.0;
        BigDecimal retVal = new BigDecimal("0.00").setScale(2,
                RoundingMode.HALF_EVEN);
        double rate = 0.0;

        List<AbstractSpecialAllowanceEntity> pEmpAllowList = this.specialAllowances
                .get(pId);
        if (pEmpAllowList == null || pEmpAllowList.isEmpty())
            return retVal.doubleValue();

        BigDecimal wMonthlyBasicBD = new BigDecimal(
                Double.toString(pMonthlyBasic)).setScale(2,
                RoundingMode.HALF_EVEN);
        BigDecimal wTotalContribBD = new BigDecimal(Double.toString(pEmpPayBean
                .getTotalContributions())).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal wSpecialAllowBD = new BigDecimal(Double.toString(pEmpPayBean
                .getSpecialAllowance())).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal wNofDaysBD = new BigDecimal(Double.toString((pEmpPayBean
                .getNoOfDays() / this.getNoOfDays())));

        for (AbstractSpecialAllowanceEntity empDed : pEmpAllowList) {

            BigDecimal wAllowanceAmt = new BigDecimal("0.00").setScale(2,
                    RoundingMode.HALF_EVEN);
            if (empDed.getPayTypes().isUsingPercentage()) {
                // This is a percentage.
                rate = empDed.getAmount();
                if (rate >= 100 || rate < 0) {
                    continue;
                } else {
                    // if(rate > 0 &&){
                    // rate = rate/100; //Get the real percent.
                    BigDecimal wRateAmt = new BigDecimal(Double.toString(rate));
                    wRateAmt = wRateAmt.divide(new BigDecimal(100), 2,
                            RoundingMode.HALF_EVEN);
                    // }
                    // allowanceAmount = (((pMonthlyBasic * rate) /
                    // 12)/this.getNoOfDays()) * pEmpPayBean.getNoOfDays();
                    wAllowanceAmt = wMonthlyBasicBD.multiply(wRateAmt);
                    wAllowanceAmt = wAllowanceAmt.divide(new BigDecimal(12), 2,
                            RoundingMode.HALF_EVEN);
                    wAllowanceAmt = wAllowanceAmt.multiply(wNofDaysBD)
                            .setScale(2, RoundingMode.HALF_EVEN);

                }
            } else {
                // allowanceAmount = (empDed.getAmount()/this.getNoOfDays()) *
                // pEmpPayBean.getNoOfDays();
                wAllowanceAmt = new BigDecimal(Double.toString(empDed
                        .getAmount())).setScale(2, RoundingMode.HALF_EVEN);
                wAllowanceAmt = wAllowanceAmt.multiply(wNofDaysBD).setScale(2,
                        RoundingMode.HALF_EVEN);

            }
            retVal = retVal.add(wAllowanceAmt);
            empDed.setActAllowAmt(wAllowanceAmt.doubleValue());

            if (pEmpPayBean.getSpecialAllowanceList() == null)
                pEmpPayBean
                        .setSpecialAllowanceList(new ArrayList<>());

            pEmpPayBean.getSpecialAllowanceList().add(empDed);
            wTotalContribBD = wTotalContribBD.add(wAllowanceAmt);
            wSpecialAllowBD = wSpecialAllowBD.add(wAllowanceAmt);

        }
        // set the total contribution and special allowance here
        pEmpPayBean.setTotalContributions(wTotalContribBD.doubleValue());
        pEmpPayBean.setSpecialAllowance(wSpecialAllowBD.doubleValue());

        return retVal.doubleValue();
    }

    private AbstractPaycheckEntity determinePayStatus(HiringInfo pHiringInfo, ConfigurationBean configurationBean) {

        AbstractPaycheckEntity wEmpPayBean = IppmsUtils.makePaycheckObject(businessCertificate);
        if (this.absorptions.contains(pHiringInfo.getParentId()))
            wEmpPayBean.setReabsorptionInd(1);
        if (this.reinstatements.contains(pHiringInfo.getParentId()))
            wEmpPayBean.setReinstatedInd(1);

        wEmpPayBean.setHiringInfo(pHiringInfo);
        wEmpPayBean.setBusinessClientId(businessCertificate.getBusinessClientInstId());
        wEmpPayBean.setEmployeeType(pHiringInfo.getEmployeeType());
        wEmpPayBean.setParentObject(pHiringInfo.getAbstractEmployeeEntity());
        wEmpPayBean.setMdaDeptMap(pHiringInfo.getAbstractEmployeeEntity().getMdaDeptMap());
        wEmpPayBean.setFirstName(pHiringInfo.getAbstractEmployeeEntity().getFirstName());
        wEmpPayBean.setLastName(pHiringInfo.getAbstractEmployeeEntity().getLastName());
        wEmpPayBean.setInitials(pHiringInfo.getAbstractEmployeeEntity().getInitials());
        wEmpPayBean.setObjectInd(pHiringInfo.getAbstractEmployeeEntity().getObjectInd());
        wEmpPayBean.setPfaInfo(pHiringInfo.getPfaInfo());
        wEmpPayBean.setBankBranch(new BankBranch(pHiringInfo.getBranchInstId()));
        wEmpPayBean.setMdaDeptMap(pHiringInfo.getAbstractEmployeeEntity().getMdaDeptMap());
        wEmpPayBean.setSalaryInfo(pHiringInfo.getAbstractEmployeeEntity().getSalaryInfo());
        wEmpPayBean.setPensionPinCode(pHiringInfo.getPensionPinCode());

        if (this.isRerun()) {
            wEmpPayBean.setReRunInd(ON);
        }
        if (pHiringInfo.getAccountNumber() == null) {
            wEmpPayBean.setDoNotPay(true);
            wEmpPayBean.setAccountNumber("NS");
        } else {
            wEmpPayBean.setAccountNumber(pHiringInfo.getAccountNumber());
        }

        if (pHiringInfo.getBvnNo() == null) {
            wEmpPayBean.setBvnNo("NS");
            if (configurationBean.isBvnRequired()) {
                wEmpPayBean.setDoNotPay(true);
                return wEmpPayBean;
            }


        } else {
            wEmpPayBean.setBvnNo(pHiringInfo.getBvnNo());
        }


        if (wEmpPayBean.getHiringInfo().getMonthlyPensionAmount() == 0) {
            wEmpPayBean.setAwaitingPenCalcInd(ON);
            wEmpPayBean.setDoNotPay(true);

            return wEmpPayBean;
        } else if (wEmpPayBean.getHiringInfo().isSuspendedEmployee()) {
            wEmpPayBean.setDoNotPay(true);
            return wEmpPayBean;

        }
        if (!pHiringInfo.getAbstractEmployeeEntity().isApprovedForPayrolling()) {
            wEmpPayBean.setDoNotPay(true);
            wEmpPayBean.setRejectedForPayrollingInd(ON);
        }
        LocalDate wToday = LocalDate.now();
        // Now check if this dude is pensionable this month.
        if (pHiringInfo.getTerminateDate() != null) {
            // This means we need to pay this guy partly.
            if (pHiringInfo.getTerminateDate().getYear() == wToday.getYear()
                    && pHiringInfo.getTerminateDate().getMonthValue() == wToday.getMonthValue()) {

                wEmpPayBean.setPayByDaysInd(ON);
                wEmpPayBean.setNoOfDays(pHiringInfo.getTerminateDate().getDayOfMonth() - 1);
                wEmpPayBean.setDoNotPay(false);

            }

        }
        if (useIamAlive) {
            if (pHiringInfo.getAmAliveDate() != null) {
                if (pHiringInfo.getAmAliveDate().getYear() == wToday.getYear() && pHiringInfo.getAmAliveDate().getMonthValue() <= wToday.getMonthValue()
                        || pHiringInfo.getAmAliveDate().getYear() > wToday.getYear()) {
                    wEmpPayBean.setDoNotPay(false);
                } else {
                    wEmpPayBean.setIAmAliveInd(ON);
                }
            }
        }


        if (!pHiringInfo.getAbstractEmployeeEntity().isApprovedForPayrolling()) {
            wEmpPayBean.setDoNotPay(true);
            wEmpPayBean.setRejectedForPayrollingInd(ON);
        }
        if (configurationBean.isBioRequired()) {
            if (pHiringInfo.getEmployee().getBiometricId() == null) {
                wEmpPayBean.setDoNotPay(true);
                wEmpPayBean.setBiometricInd(ON);
            }

        }
        if (!wEmpPayBean.isDoNotPay()) {

            wEmpPayBean.setPayEmployeeRef(true);
            wEmpPayBean.setPaymentType("Pensioned");
            wEmpPayBean.setName(wEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed());
            if (pHiringInfo.getLastPayDate() != null) {
                wEmpPayBean.setLastPayDate(DateTimeFormatter.ofPattern("MMM dd, yyyy")
                        .format((pHiringInfo.getLastPayDate())));
            } else {
                wEmpPayBean.setLastPayDate("");
            }

            if (pHiringInfo.getCurrentPayPeriod() == null
                    || pHiringInfo.getCurrentPayPeriod().equalsIgnoreCase("")) {
                // This is the first time. Set a flag.
                pHiringInfo.setFirstTimePay(true);

            } else {
                pHiringInfo.setNormalPay(true);
            }
        }


        if (wEmpPayBean.isDoNotPay()) {
            wEmpPayBean.setYtdIgnoreInd(ON);
        }
        return wEmpPayBean;
    }


}
