package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleDetails;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfig;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfigDetails;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.*;


@Getter
@Setter
public class CalculatePayPerEmployee implements IBigDecimalValues {
    private Map<Long, SalaryInfo> salaryInfoMap;

    // private Map<Long, Long> zeroLastPayEmployees;
    private GlobalPercentConfig globalPercentConfig;
    private ConfigurationBean configurationBean;
    private Map<Long, SuspensionLog> partPaymentMap;
    private boolean deductDevelopmentLevy;
    private List<Long> absorptions;
    private List<Long> reinstatements;
    private boolean hasSalaryDifference;
    private BusinessCertificate businessCertificate;
    private int runMonth;
    private int runYear;

    private Map<Long, List<AbstractDeductionEntity>> employeeDeductions;
    private Map<Long, List<AbstractGarnishmentEntity>> employeeGarnishments;
    private Map<Long, List<AbstractSpecialAllowanceEntity>> specialAllowances;

    private LocalDate sixtyYearsAgo;
    private LocalDate thirtyFiveYearsAgo;
    private LocalDate payPeriodEnd;
    private int noOfDays;
    private GenericService genericService;
    private BigDecimal globalPercent, wPercentagePayment, wMonthlyBasic, retVal, wRateAmt, wNoOfDaysBD, wTotNonTaxDedDB;
    private BigDecimal wMonthlyBasicBD, staffTotalAmountBD, wCurrentAmountBD, amountBD, workingAmountBD,unionDuesBD;
    private List<AbstractSpecialAllowanceEntity> allowanceList;
    private int leastNoOfDays;
    private boolean useGlobalPercent;
    private boolean effectOnTerm;
    private boolean effectOnSuspension;
    private boolean loanEffect;
    private boolean deductionEffect;
    private boolean rerun;
    private boolean effectOnDeductions;
    private boolean effectOnSpecAllow;
    private Map<Long, TerminateReason> terminateReasonMap;
    private Map<Long, AllowanceRuleMaster> payGroupAllowanceRule;
    private double shiftDuty, callDuty, rent, transport, motorVehicle, totalPay, payAmt, nonTaxableSpecialAllowance;
    private double taxableSpecialAllowance, wGrossIncome, wAnnualRelief, totalContAmount, taxableIncome, taxesDue;
    private double grossPay, wTotalLoans, actualPay, rate;
    private List<AbstractGarnishmentEntity> loanList;
    private List<AbstractDeductionEntity> deductionList;
    private AllowanceRuleMaster allowanceRuleMaster;
    private AbstractPaycheckEntity pEmpPayBean;
    private double globalPercentFactor = 1.0D;
    private double multiplyFactor = 1.0D;
    private BigDecimal wCons, wConsMonthly, wShiftAndCallDuty, wGrossBD, wTotContAmt, wPayAmtBD, wTA, wPayPercentBD, monthlyPay,oneDayPayBD;
    private ArrayList<String> debugEmps;
    private Double wShiftPlusCall;
    private LocalDate wTpsHireDate;
    private LocalDate wExpectedRetirementDate;

    public CalculatePayPerEmployee() {
        init();
    }

    private void init() {
        wTpsHireDate = PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_HIRE_DATE_STR);
        wExpectedRetirementDate = PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR);


    }

    public AbstractPaycheckEntity calculatePayroll(HiringInfo pHiringInfo)
            throws Exception {

        pEmpPayBean = PayrollUtils.determinePayStatus(pHiringInfo, rerun, partPaymentMap, payPeriodEnd, businessCertificate, configurationBean, terminateReasonMap);

        wCons = wConsMonthly = wShiftAndCallDuty = unionDuesBD = wGrossBD = wTotContAmt = wPayAmtBD = wTA = wPayPercentBD  = oneDayPayBD = monthlyPay = null;
        wShiftPlusCall = shiftDuty = callDuty = rent = transport = motorVehicle = totalPay = payAmt = nonTaxableSpecialAllowance = 0.0D;
        taxableSpecialAllowance = wGrossIncome = wAnnualRelief = totalContAmount = taxableIncome = taxesDue = 0.0D;
        grossPay = wTotalLoans = actualPay = rate = 0.0D;

        if (this.absorptions.contains(pHiringInfo.getParentId()))
            pEmpPayBean.setReabsorptionInd(1);
        if (this.reinstatements.contains(pHiringInfo.getParentId()))
            pEmpPayBean.setReinstatedInd(1);

     /*   if(pEmpPayBean.getParentObject().getId().equals(10815L)){
            System.out.println("Debug Guy");
        }*/
        SalaryInfo wSS = this.salaryInfoMap.get(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getSalaryInfo().getId());

        if (globalPercentConfig != null && !globalPercentConfig.isNewEntity()) {
            useGlobalPercent = true;
            if (globalPercentConfig.isEffectOnTerm()) {
                leastNoOfDays = globalPercentConfig.getLeastNoOfDays();
                effectOnTerm = true;
            }

            effectOnSuspension = globalPercentConfig.isEffectOnSuspension();
            effectOnDeductions = globalPercentConfig.isEffectOnDeduction();
            loanEffect = globalPercentConfig.isEffectOnLoan();
            effectOnSpecAllow = globalPercentConfig.isEffectOnSpecAllow();

            if (globalPercentConfig.isGlobalApply()) {
                globalPercent = new BigDecimal(Double.toString(globalPercentConfig.getGlobalPercentage() / 100.0D)).setScale(2, RoundingMode.HALF_EVEN);
            } else {
                //This means we have to set using details....
                for (GlobalPercentConfigDetails g : globalPercentConfig.getConfigDetailsList()) {
                    if (g.getSalaryTypeId().equals(wSS.getSalaryType().getId())) {
                        if (g.getFromLevel() >= wSS.getLevel() && g.getToLevel() <= wSS.getLevel()) {
                            globalPercent = new BigDecimal(Double.toString(g.getPayPercentage() / 100.0D)).setScale(2, RoundingMode.HALF_EVEN);
                            break;
                        }
                    }
                }
            }
        }
        if (globalPercent != null) {
            pEmpPayBean.setGlobalPerInd(ON);
            pEmpPayBean.setGlobalPercentage(globalPercent.doubleValue() * 100.0D);
            globalPercentFactor = globalPercent.doubleValue();
        }

        callDuty = wSS.getCallDuty();
        shiftDuty = wSS.getShiftDuty();
        rent = wSS.getRent();
        transport = wSS.getTransport();
        motorVehicle = wSS.getMotorVehicle();
        allowanceRuleMaster = this.payGroupAllowanceRule.get(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId());
        if (allowanceRuleMaster != null) {
            pEmpPayBean.setAllowanceRuleMaster(allowanceRuleMaster);
            for (AllowanceRuleDetails d : allowanceRuleMaster.getAllowanceRuleDetailsList()) {
                if (d.getBeanFieldName().equalsIgnoreCase(CALL_DUTY))
                    callDuty = d.getApplyYearlyValue();
                if (d.getBeanFieldName().equalsIgnoreCase(SHIFT_DUTY))
                    shiftDuty = d.getApplyYearlyValue();
                if (d.getBeanFieldName().equalsIgnoreCase(RENT))
                    rent = d.getApplyYearlyValue();
                if (d.getBeanFieldName().equalsIgnoreCase(TRANSPORT))
                    transport = d.getApplyYearlyValue();
                if (d.getBeanFieldName().equalsIgnoreCase(MOTOR_VEHICLE))
                    motorVehicle = d.getApplyYearlyValue();
            }
        }

        if (!pEmpPayBean.isDoNotPay()) {

            staffTotalAmountBD = new BigDecimal("0.00");
            wCurrentAmountBD = new BigDecimal("0.00");
            multiplyFactor = 1.0D;

            if (pEmpPayBean.isPayByDays()) {


                if (wSS == null) {
                    throw new Exception("Employee Salary Structure Undefined : " + pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getEmployeeId());
                }


                payAmt = wSS.getMonthlyBasicSalary();
                pEmpPayBean.setSalaryInfo(wSS);

                wNoOfDaysBD = new BigDecimal(new Double(pEmpPayBean.getNoOfDays()) / new Double(this.getNoOfDays())).setScale(2, RoundingMode.FLOOR);

                wPayAmtBD = new BigDecimal(Double.toString(((payAmt / 12.0D) /new Double(this.getNoOfDays()) * pEmpPayBean.getNoOfDays()))).setScale(2, RoundingMode.HALF_EVEN);

               // oneDayPayBD = wPayAmtBD.divide(BigDecimal.valueOf(Double.valueOf(String.valueOf(this.getNoOfDays()))).setScale(2,RoundingMode.FLOOR)).setScale(2,RoundingMode.HALF_EVEN);

              //  wPayAmtBD = oneDayPayBD.multiply(BigDecimal.valueOf(new Double(pEmpPayBean.getNoOfDays())).setScale(2,RoundingMode.FLOOR)).setScale(2,RoundingMode.HALF_EVEN);

                // wPayAmtBD = wPayAmtBD.multiply(wNoOfDaysBD);
                if (useGlobalPercent)
                    if (globalPercentConfig.isEffectOnTerm()) {
                        if (globalPercentConfig.getLeastNoOfDays() >= this.getNoOfDays()) {
                            wPayAmtBD = wPayAmtBD.multiply(globalPercent);
                        }

                    }
                payAmt = EntityUtils.convertDoubleToEpmStandard(wPayAmtBD.doubleValue());

                wMonthlyBasicBD = new BigDecimal(Double.toString(payAmt)).setScale(2, RoundingMode.HALF_EVEN);
                pEmpPayBean.setMonthlyBasic(payAmt);

                nonTaxableSpecialAllowance = getNonTaxableSpecialAllowanceByDays(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId(), payAmt, pEmpPayBean);

                nonTaxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(nonTaxableSpecialAllowance);

                taxableSpecialAllowance = getTaxableSpecialAllowanceByDays(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId(), payAmt, pEmpPayBean);

                taxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(taxableSpecialAllowance);

                wCons = new BigDecimal(Double.toString(PayrollPayUtils.getPartPayment(IppmsUtilsExt.getConsolidatedAllowance(wSS, false, makeAllowanceRuleMap(allowanceRuleMaster)), pEmpPayBean.getNoOfDays(), this.getNoOfDays(), true))).setScale(2, RoundingMode.HALF_EVEN);
                // wConsMonthly = wCons.divide(wYearDivisorDB);
                if (useGlobalPercent)
                    if (effectOnTerm && leastNoOfDays <= pEmpPayBean.getNoOfDays())
                        wCons = wCons.multiply(globalPercent);

                wShiftAndCallDuty = null;
                /**
                 * This logic is so as NO EMPLOYEE can have both. They are not taxable.
                 */

                if (callDuty > 0 || shiftDuty > 0) {
                    //--We need to check if there is a reduction or removal of Call Duty.

                    wShiftAndCallDuty = new BigDecimal(Double.toString(shiftDuty + callDuty)).setScale(2, RoundingMode.HALF_EVEN);
                    wShiftAndCallDuty.divide(wYearDivisorDB, 2, RoundingMode.HALF_EVEN);
                    wShiftAndCallDuty = wShiftAndCallDuty.multiply(wNoOfDaysBD);

                    if (useGlobalPercent)
                        if (effectOnTerm && leastNoOfDays <= pEmpPayBean.getNoOfDays())
                            wShiftAndCallDuty = wShiftAndCallDuty.multiply(globalPercent);
                }
                totalPay = EntityUtils.convertDoubleToEpmStandard(wCons.doubleValue() + payAmt + taxableSpecialAllowance + nonTaxableSpecialAllowance);
                //totalPay = EntityUtils.convertDoubleToEpmStandard(totalPay);
                pEmpPayBean.setTotalPay(totalPay);

                wGrossBD = new BigDecimal(wCons.doubleValue() + wPayAmtBD.doubleValue() + taxableSpecialAllowance).setScale(2, RoundingMode.HALF_EVEN);

                if (wShiftAndCallDuty != null) {
                    wGrossBD.subtract(wShiftAndCallDuty);
                }

                wGrossBD = wGrossBD.multiply(wYearDivisorDB);
                wGrossIncome = EntityUtils.convertDoubleToEpmStandard(wGrossBD.doubleValue());


                wAnnualRelief = EntityUtils.convertDoubleToEpmStandard(PayrollEngineHelper.getRelief(wGrossIncome));

                pEmpPayBean.setYearlyReliefAmount(wAnnualRelief);
                pEmpPayBean.setMonthlyReliefAmount(EntityUtils.convertDoubleToEpmStandard(wAnnualRelief / 12.0D));
                if (!wSS.getSalaryType().isExemptFromPension()) {
                    if (pHiringInfo.isPensionableEmployee() && (!pEmpPayBean.isContractStaff() && !pHiringInfo.isPoliticalOfficeHolderType())) {
                        if (!PayrollBeanUtils.isTPSEmployee(pHiringInfo.getBirthDate(), pHiringInfo.getHireDate(), pHiringInfo.getExpectedDateOfRetirement(), wTpsHireDate, wExpectedRetirementDate, configurationBean, businessCertificate)) {

                            if (wSS.getSalaryType().isBasicRentTransportType()) {

                                wTotContAmt = new BigDecimal(wSS.getMonthlyBasicSalary() + rent +
                                        transport);

                            } else {
                                wTotContAmt = new
                                        BigDecimal(Double.toString(wSS.getMonthlyBasicSalary() + rent +
                                        motorVehicle));


                            }
                            wTotContAmt = wTotContAmt.divide(wYearDivisorDB, 2,
                                    RoundingMode.HALF_EVEN);
                            wTotContAmt = wTotContAmt.multiply(w7Point5Percent);
                            wTotContAmt = wTotContAmt.multiply(wNoOfDaysBD);
                            if (useGlobalPercent)
                                if (effectOnTerm && leastNoOfDays <= pEmpPayBean.getNoOfDays())
                                    wTotContAmt = wTotContAmt.multiply(globalPercent);

                            totalContAmount = new BigDecimal(wTotContAmt.doubleValue()).setScale(2, RoundingMode.FLOOR).doubleValue();
                            pEmpPayBean.setContributoryPension(totalContAmount);
                            pEmpPayBean.setTotalDeductions(totalContAmount);
                            pEmpPayBean.setTotalContributions(totalContAmount);

                        }
                    }
                }


                pEmpPayBean = removeEmployeeNonTaxableDeductionsByDays(pEmpPayBean, wPayAmtBD);

                pEmpPayBean.setFreePay(pEmpPayBean.getMonthlyReliefAmount() + pEmpPayBean.getTotalDeductions());

                taxableIncome = EntityUtils.convertDoubleToEpmStandard(wGrossIncome / 12.0D - pEmpPayBean.getFreePay());

                pEmpPayBean.setTaxableIncome(taxableIncome);

                taxesDue = EntityUtils.convertDoubleToEpmStandard(PayrollEngineHelper.calculateTaxes(wGrossIncome, pEmpPayBean.getFreePay()) /*/ getNoOfDays() * pEmpPayBean.getNoOfDays()*/);

                pEmpPayBean.setTaxesPaid(taxesDue);
                pEmpPayBean.setMonthlyTax(taxesDue);

                grossPay = totalPay - taxesDue;
                pEmpPayBean.setNetPay(grossPay);


                pEmpPayBean = removeEmployeeDeductionsByDays(pEmpPayBean, grossPay, wPayAmtBD.doubleValue());

                if (pEmpPayBean.getNoOfDays() > 9) //-- Do not remove Garnishments for folks less than 10 days...unless it will not result in Negative Pay
                    pEmpPayBean = removeGarnishments(pEmpPayBean, false);
                else {
                    loanList = this.employeeGarnishments.get(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId());
                    wTotalLoans = 0.0D;
                    if (loanList != null && !loanList.isEmpty()) {
                        for (AbstractGarnishmentEntity e : loanList) {
                            if (e.getOwedAmount() > 0.00D)
                                wTotalLoans += e.getAmount();
                        }
                        if (pEmpPayBean.getNetPay() > wTotalLoans)
                            pEmpPayBean = removeGarnishments(pEmpPayBean, false);
                        else
                            pEmpPayBean = removeGarnishments(pEmpPayBean, true);
                    }
                }
                wTA = new BigDecimal(Double.toString(pEmpPayBean.getTotalAllowance())).setScale(2, RoundingMode.FLOOR);
                pEmpPayBean.setTotalAllowance(wTA.doubleValue());

            } else {

                actualPay = wSS.getMonthlyBasicSalary();
                if (pEmpPayBean.isPercentagePayment()) {
                    actualPay *= pEmpPayBean.getPayPercentage();
                    wPayPercentBD = new BigDecimal(Double.toString(pEmpPayBean.getPayPercentage())).setScale(2, RoundingMode.HALF_EVEN);
                    if (useGlobalPercent && effectOnSuspension)
                        actualPay *= globalPercentFactor;
                }
                pEmpPayBean.setSalaryInfo(wSS);
                payAmt = EntityUtils.convertDoubleToEpmStandard(actualPay);


                nonTaxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(getNonTaxableSpecialAllowance(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId(), wSS.getMonthlyBasicSalary(), pEmpPayBean));

                taxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(getTaxableSpecialAllowance(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId(), pEmpPayBean, wSS.getMonthlyBasicSalary()));

                if (pEmpPayBean.isPercentagePayment()) {
                    wCons = new BigDecimal(IppmsUtilsExt.getConsolidatedAllowance(wSS, false, makeAllowanceRuleMap(allowanceRuleMaster)) * pEmpPayBean.getPayPercentage() + payAmt).setScale(2, RoundingMode.HALF_EVEN);
                    if (useGlobalPercent && effectOnSuspension)
                        wCons = wCons.multiply(globalPercent);
                    wCons = wCons.divide(wYearDivisorDB, 2, RoundingMode.HALF_EVEN);
                    totalPay = EntityUtils.convertDoubleToEpmStandard((wCons.doubleValue()  + (taxableSpecialAllowance + nonTaxableSpecialAllowance)) *pEmpPayBean.getPayPercentage());
                } else {
                    wCons = new BigDecimal(Double.toString(IppmsUtilsExt.getConsolidatedAllowance(wSS, true, makeAllowanceRuleMap(allowanceRuleMaster)) + EntityUtils.convertDoubleToEpmStandard(payAmt / 12.0D))).setScale(2, RoundingMode.HALF_EVEN);
                    if (useGlobalPercent)
                        wCons = wCons.multiply(globalPercent);
                    totalPay = EntityUtils.convertDoubleToEpmStandard(wCons.doubleValue() + (taxableSpecialAllowance + nonTaxableSpecialAllowance));
                }


                //monthlyPay = new BigDecimal(EntityUtils.convertDoubleToEpmStandard(payAmt/12.0D));

                //  pEmpPayBean.setMonthlyBasic(monthlyPay.doubleValue());
                //  wConsMonthly = wCons;
                //   wCons = wCons.add(monthlyPay);

                // totalPay = EntityUtils.convertDoubleToEpmStandard(totalPay);
                //
                //  pEmpPayBean.setTotalPay(totalPay);
                wPayPercentBD = new BigDecimal(Double.toString(pEmpPayBean.getPayPercentage())).setScale(2, RoundingMode.HALF_EVEN);

                if (callDuty > 0 || shiftDuty > 0)
                    wShiftAndCallDuty = new BigDecimal(Double.toString(callDuty + shiftDuty)).setScale(2, RoundingMode.HALF_EVEN);
                else
                    wShiftAndCallDuty = null;

                if (pEmpPayBean.isPercentagePayment()) {
                    wGrossIncome = EntityUtils.convertDoubleToEpmStandard(IppmsUtilsExt.getConsolidatedAllowance(wSS, false, makeAllowanceRuleMap(allowanceRuleMaster)) * pEmpPayBean.getPayPercentage() + payAmt);

                    if (wShiftAndCallDuty != null) {
                        wShiftAndCallDuty = wShiftAndCallDuty.multiply(wPayPercentBD);
                        if (useGlobalPercent && effectOnSuspension)
                            wShiftAndCallDuty = wShiftAndCallDuty.multiply(globalPercent);
                        wGrossIncome -= EntityUtils.convertDoubleToEpmStandard(wShiftAndCallDuty.doubleValue());
                    }

                } else {

                    wGrossIncome = EntityUtils.convertDoubleToEpmStandard(IppmsUtilsExt.getConsolidatedAllowance(wSS, false, makeAllowanceRuleMap(allowanceRuleMaster)) + payAmt + (taxableSpecialAllowance * 12.0D));
                    if (wShiftAndCallDuty != null) {
                        if (useGlobalPercent)
                            wShiftAndCallDuty = wShiftAndCallDuty.multiply(globalPercent);
                        wGrossIncome -= EntityUtils.convertDoubleToEpmStandard(wShiftAndCallDuty.doubleValue());
                    }
                }
                wAnnualRelief = PayrollEngineHelper.getRelief(wGrossIncome);

                pEmpPayBean.setYearlyReliefAmount(wAnnualRelief);
                pEmpPayBean.setMonthlyReliefAmount(EntityUtils.convertDoubleToEpmStandard(wAnnualRelief / 12.0D));
                if (!wSS.getSalaryType().isExemptFromPension()) {
                    if (pHiringInfo.isPensionableEmployee() &&
                            !pEmpPayBean.isContractStaff() && !pHiringInfo.isPoliticalOfficeHolderType()
                            && !PayrollBeanUtils.isTPSEmployee(pHiringInfo.getBirthDate(), pHiringInfo.getHireDate(), pHiringInfo.getExpectedDateOfRetirement(), wTpsHireDate, wExpectedRetirementDate, configurationBean, businessCertificate)) {

                        if (wSS.getSalaryType().isBasicRentTransportType()) {
                            wTotContAmt = new
                                    BigDecimal(wSS.getMonthlyBasicSalary() + rent +
                                    transport);


                        } else {
                            //because of SUBEB and Others, the SalaryType values might not be set.
                            if (businessCertificate.isCivilService())
                                wTotContAmt = new BigDecimal(wSS.getMonthlyBasicSalary() + rent + motorVehicle);
                            else
                                wTotContAmt = new BigDecimal(wSS.getMonthlyBasicSalary() + rent + transport);


                        }
                        wTotContAmt = wTotContAmt.divide(wYearDivisorDB, 2,
                                RoundingMode.HALF_EVEN);
                        wTotContAmt = wTotContAmt.multiply(w7Point5Percent);
                        if (pEmpPayBean.isPercentagePayment()) {
                            wPercentagePayment = new
                                    BigDecimal(pEmpPayBean.getPayPercentage()).setScale(2,
                                    RoundingMode.HALF_EVEN);
                            wTotContAmt =
                                    wTotContAmt.multiply(wPercentagePayment);
                            if (useGlobalPercent && effectOnSuspension)
                                wTotContAmt = wTotContAmt.multiply(globalPercent);

                        } else {
                            if (useGlobalPercent)
                                wTotContAmt = wTotContAmt.multiply(globalPercent);

                        }
                        totalContAmount = new
                                BigDecimal(wTotContAmt.doubleValue()).setScale(2,
                                RoundingMode.FLOOR).doubleValue();
                        pEmpPayBean.setContributoryPension(totalContAmount);
                        pEmpPayBean.setTotalDeductions(totalContAmount);
                        pEmpPayBean.setTotalContributions(totalContAmount);

                    }
                }

                pEmpPayBean = removeEmployeeNonTaxableDeductions(pEmpPayBean, wSS);

                pEmpPayBean.setFreePay(pEmpPayBean.getMonthlyReliefAmount() + pEmpPayBean.getTotalDeductions());

                taxableIncome = EntityUtils.convertDoubleToEpmStandard(wGrossIncome / 12.0D - pEmpPayBean.getFreePay());

                pEmpPayBean.setTaxableIncome(taxableIncome);

                taxesDue = EntityUtils.convertDoubleToEpmStandard(PayrollEngineHelper.calculateTaxes(wGrossIncome, pEmpPayBean.getFreePay()));

                pEmpPayBean.setTaxesPaid(taxesDue);
                pEmpPayBean.setMonthlyTax(taxesDue);

                grossPay = EntityUtils.convertDoubleToEpmStandard(totalPay - taxesDue);

                pEmpPayBean.setNetPay(grossPay);
                if(pEmpPayBean.isPercentagePayment()){
                    wMonthlyBasic = new BigDecimal((wSS.getMonthlyBasicSalary() / 12.0) * pEmpPayBean.getPayPercentage()).setScale(2, RoundingMode.HALF_EVEN);
                }else{
                    wMonthlyBasic = new BigDecimal(wSS.getMonthlyBasicSalary() / 12.0).setScale(2, RoundingMode.HALF_EVEN);
                }

                pEmpPayBean.setMonthlyBasic(wMonthlyBasic.doubleValue());

                pEmpPayBean = removeEmployeeDeductions(pEmpPayBean, grossPay, wMonthlyBasic.doubleValue());

                pEmpPayBean = removeGarnishments(pEmpPayBean, false);

                pEmpPayBean.setNetPay(pEmpPayBean.getNetPay() - (totalContAmount + pEmpPayBean.getTotalNonTaxableDeductions()));


                if (pEmpPayBean.isPercentagePayment()) {
                    multiplyFactor = pEmpPayBean.getPayPercentage();
                }

                //  pEmpPayBean.setMonthlyBasic(pEmpPayBean.getTotalPay() - pEmpPayBean.getTotalAllowance());


            }
            pEmpPayBean = PayrollEngineHelper.setSalaryInfoValues(wSS, pEmpPayBean, getNoOfDays(), multiplyFactor, effectOnTerm, globalPercentFactor, leastNoOfDays, allowanceRuleMaster);
            pEmpPayBean.setTotalDeductions(pEmpPayBean.getTotalDeductions() + pEmpPayBean.getTotalGarnishments() + pEmpPayBean.getTaxesPaid());
            pEmpPayBean.setTotalPay(pEmpPayBean.getMonthlyBasic() + pEmpPayBean.getTotalAllowance());
            pEmpPayBean.setNetPay(pEmpPayBean.getTotalPay() - pEmpPayBean.getTotalDeductions());
            if (this.deductDevelopmentLevy) {
                pEmpPayBean.setDevelopmentLevy(100.0D);
                pEmpPayBean.setNetPay(pEmpPayBean.getNetPay() - 100.0D);
            }
        } else {
            pEmpPayBean.setSalaryInfo(wSS);
        }

        pEmpPayBean.setRunMonth(this.runMonth);
        pEmpPayBean.setRunYear(this.runYear);
        if (pHiringInfo.getAbstractEmployeeEntity().getSchoolInstId() != null) {
            pEmpPayBean.setSchoolInfo(new SchoolInfo(pHiringInfo.getAbstractEmployeeEntity().getSchoolInstId()));
        } else {
            pEmpPayBean.setSchoolInstId(null);
        }
        if (pEmpPayBean.getNetPay() < 0)
            pEmpPayBean.setNegativePayInd(1);
        return pEmpPayBean;
    }

    private double getTaxableSpecialAllowance(Long pId, AbstractPaycheckEntity pEmpPayBean, double pMonthlyBasic) {
        retVal = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_EVEN);
        wMonthlyBasicBD = new BigDecimal(Double.toString(pMonthlyBasic)).setScale(2, RoundingMode.HALF_EVEN);
        staffTotalAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getTotalAllowance())).setScale(2, RoundingMode.HALF_EVEN);
        wCurrentAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getSpecialAllowance())).setScale(2, RoundingMode.HALF_EVEN);
        wPercentagePayment = new BigDecimal(Double.toString(pEmpPayBean.getPayPercentage())).setScale(2, RoundingMode.HALF_EVEN);

        workingAmountBD = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_EVEN);
        allowanceList = this.specialAllowances.get(pId);
        if ((allowanceList == null) || (allowanceList.isEmpty()))
            return retVal.doubleValue();
        for (AbstractSpecialAllowanceEntity empDed : allowanceList) {
            if (!empDed.getSpecialAllowanceType().isTaxable())
                continue;
            if (empDed.getPayTypes().isUsingPercentage()) {
                rate = empDed.getAmount();
                if ((rate >= 100.0D) || (rate <= 0.0D)) {
                    continue;
                }

                wRateAmt = new BigDecimal(Double.toString(rate));
                wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);
                //rate /= 100.0D;
                workingAmountBD = wMonthlyBasicBD.multiply(wRateAmt).setScale(2, RoundingMode.HALF_EVEN);
                workingAmountBD = workingAmountBD.divide(wYearDivisorDB, 2, RoundingMode.HALF_EVEN);
                if (pEmpPayBean.isPercentagePayment()) {
                    workingAmountBD = workingAmountBD.multiply(wPercentagePayment);
                    if (useGlobalPercent) {
                        if (effectOnSpecAllow && effectOnSuspension)
                            workingAmountBD = workingAmountBD.multiply(globalPercent);
                    }
                } else {
                    if (useGlobalPercent) {
                        if (effectOnSpecAllow)
                            workingAmountBD = workingAmountBD.multiply(globalPercent);
                    }
                }
                amountBD = new BigDecimal(String.valueOf(workingAmountBD.doubleValue())).setScale(2, RoundingMode.FLOOR);


            } else {
                amountBD = new BigDecimal(empDed.getAmount()).setScale(2, RoundingMode.HALF_EVEN);

                if (pEmpPayBean.isPercentagePayment()) {
                    amountBD = amountBD.multiply(wPercentagePayment);
                    if (useGlobalPercent) {
                        if (effectOnSpecAllow && effectOnSuspension)
                            amountBD = amountBD.multiply(globalPercent);
                    }
                } else {
                    if (useGlobalPercent) {
                        if (effectOnSpecAllow)
                            amountBD = amountBD.multiply(globalPercent);
                    }
                }


            }
            empDed.setActAllowAmt(amountBD.doubleValue());

            if (pEmpPayBean.getSpecialAllowanceList() == null) {
                pEmpPayBean.setSpecialAllowanceList(new ArrayList<>());
            }
            pEmpPayBean.getSpecialAllowanceList().add(empDed);

            staffTotalAmountBD = staffTotalAmountBD.add(amountBD);
            wCurrentAmountBD = wCurrentAmountBD.add(amountBD);
            retVal = retVal.add(amountBD);
        }
        pEmpPayBean.setTotalAllowance(staffTotalAmountBD.doubleValue());
        pEmpPayBean.setSpecialAllowance(wCurrentAmountBD.doubleValue());
        return retVal.doubleValue();
    }

    private double getTaxableSpecialAllowanceByDays(Long pId, double pMonthlyBasic, AbstractPaycheckEntity pEmpPayBean) {
        //double retVal = 0.0D;
        retVal = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_EVEN);
        wMonthlyBasicBD = new BigDecimal(Double.toString(pMonthlyBasic)).setScale(2, RoundingMode.HALF_EVEN);
        staffTotalAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getTotalAllowance())).setScale(2, RoundingMode.HALF_EVEN);
        wCurrentAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getSpecialAllowance())).setScale(2, RoundingMode.HALF_EVEN);
        //wNoOfDaysBD = new BigDecimal(Double.toString((new Double(pEmpPayBean.getNoOfDays()) / new Double(this.getNoOfDays())))).setScale(2, RoundingMode.HALF_EVEN);

        rate = 0.0D;

        allowanceList = this.specialAllowances.get(pId);
        if ((allowanceList == null) || (allowanceList.isEmpty()))
            return retVal.doubleValue();

        for (AbstractSpecialAllowanceEntity empDed : allowanceList) {
            if (!empDed.getSpecialAllowanceType().isTaxable())
                continue;


            if (empDed.getPayTypes().isUsingPercentage()) {
                rate = empDed.getAmount();
                if (rate >= 100.0D || rate <= 0.0D) {
                    continue;
                }
                wRateAmt = new BigDecimal(Double.toString(rate));
                wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);

                amountBD = wMonthlyBasicBD.multiply(wRateAmt).setScale(2, RoundingMode.HALF_EVEN);


            } else {
                if (empDed.getSpecialAllowanceType().isArrearsType()) {
                    //Use WHOLE Value...
                    amountBD = new BigDecimal(Double.toString(empDed.getAmount())).setScale(2, RoundingMode.HALF_EVEN);
                } else {
                    amountBD = new BigDecimal(Double.toString(empDed.getAmount())).setScale(2, RoundingMode.HALF_EVEN);
                    amountBD = amountBD.multiply(wNoOfDaysBD).setScale(2, RoundingMode.HALF_EVEN);


                }
            }
            if (useGlobalPercent) {
                if (effectOnSpecAllow && effectOnTerm && this.leastNoOfDays <= pEmpPayBean.getNoOfDays())
                    amountBD = amountBD.multiply(globalPercent).setScale(2, RoundingMode.HALF_EVEN);
            }
            empDed.setActAllowAmt(amountBD.doubleValue());

            if (pEmpPayBean.getSpecialAllowanceList() == null) {
                pEmpPayBean.setSpecialAllowanceList(new ArrayList<>());
            }
            pEmpPayBean.getSpecialAllowanceList().add(empDed);

            staffTotalAmountBD = staffTotalAmountBD.add(amountBD);
            wCurrentAmountBD = wCurrentAmountBD.add(amountBD);
            retVal = retVal.add(amountBD);
        }

        pEmpPayBean.setTotalAllowance(staffTotalAmountBD.doubleValue());
        pEmpPayBean.setSpecialAllowance(wCurrentAmountBD.doubleValue());
        return retVal.doubleValue();
    }

    private double getNonTaxableSpecialAllowance(Long pId, double pMonthlyBasic, AbstractPaycheckEntity pEmpPayBean) {
        retVal = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_EVEN);
        wMonthlyBasicBD = new BigDecimal(Double.toString(pMonthlyBasic)).setScale(2, RoundingMode.HALF_EVEN);
        staffTotalAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getTotalAllowance())).setScale(2, RoundingMode.HALF_EVEN);
        wCurrentAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getSpecialAllowance())).setScale(2, RoundingMode.HALF_EVEN);
        wPayPercentBD = new BigDecimal(Double.toString(pEmpPayBean.getPayPercentage())).setScale(2, RoundingMode.HALF_EVEN);
        rate = 0.0D;

        allowanceList = this.specialAllowances.get(pId);
        if ((allowanceList == null) || (allowanceList.isEmpty()))
            return retVal.doubleValue();

        for (AbstractSpecialAllowanceEntity empDed : allowanceList) {
            if (empDed.getSpecialAllowanceType().isTaxable())
                continue;
            if (empDed.getPayTypes().isUsingPercentage()) {
                rate = empDed.getAmount();
                if ((rate >= 100.0D) || (rate < 0.0D)) {
                    continue;
                }
                wRateAmt = new BigDecimal(Double.toString(rate)).setScale(2, RoundingMode.HALF_EVEN);

                wRateAmt = wRateAmt.divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN);
                workingAmountBD = wMonthlyBasicBD.multiply(wRateAmt);
                workingAmountBD = workingAmountBD.divide(wYearDivisorDB, 2, RoundingMode.HALF_EVEN);

            } else {
                workingAmountBD = new BigDecimal(empDed.getAmount()).setScale(2, RoundingMode.HALF_EVEN);

                // retVal = retVal.add(wAllowanceAmt);

            }
            if (pEmpPayBean.isPercentagePayment()) {
                workingAmountBD = workingAmountBD.multiply(wPayPercentBD);
                if (useGlobalPercent) {
                    if (effectOnSuspension && effectOnSpecAllow)
                        workingAmountBD = workingAmountBD.multiply(globalPercent);
                }
            } else {
                if (useGlobalPercent) {
                    if (effectOnSpecAllow)
                        workingAmountBD = workingAmountBD.multiply(globalPercent);
                }
            }
            amountBD = new BigDecimal(String.valueOf(workingAmountBD.doubleValue())).setScale(2, RoundingMode.FLOOR);
            empDed.setActAllowAmt(amountBD.doubleValue());

            if (pEmpPayBean.getSpecialAllowanceList() == null) {
                pEmpPayBean.setSpecialAllowanceList(new ArrayList<>());
            }
            pEmpPayBean.getSpecialAllowanceList().add(empDed);

            staffTotalAmountBD = staffTotalAmountBD.add(amountBD);
            wCurrentAmountBD = wCurrentAmountBD.add(amountBD);
            retVal = retVal.add(amountBD);
        }
        pEmpPayBean.setTotalAllowance(staffTotalAmountBD.doubleValue());
        pEmpPayBean.setSpecialAllowance(wCurrentAmountBD.doubleValue());

        return retVal.doubleValue();
    }

    private double getNonTaxableSpecialAllowanceByDays(Long pId, double pMonthlyBasic, AbstractPaycheckEntity pEmpPayBean) {
        //double retVal = 0.0D;
        retVal = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_EVEN);
        wMonthlyBasicBD = new BigDecimal(Double.toString(pMonthlyBasic)).setScale(2, RoundingMode.HALF_EVEN);
        staffTotalAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getTotalAllowance())).setScale(2, RoundingMode.HALF_EVEN);
        wCurrentAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getSpecialAllowance())).setScale(2, RoundingMode.HALF_EVEN);
        // wNoOfDaysBD = new BigDecimal(Double.toString((pEmpPayBean.getNoOfDays() / this.getNoOfDays())));

        rate = 0.0D;

        allowanceList = this.specialAllowances.get(pId);
        if ((allowanceList == null) || (allowanceList.isEmpty()))
            return retVal.doubleValue();


        for (AbstractSpecialAllowanceEntity empDed : allowanceList) {

            if (empDed.getSpecialAllowanceType().isTaxable())
                continue;


            if (empDed.getPayTypes().isUsingPercentage()) {
                rate = empDed.getAmount();
                if ((rate >= 100.0D) || (rate <= 0.0D)) {
                    continue;
                }
                wRateAmt = new BigDecimal(Double.toString(rate)).setScale(2, RoundingMode.HALF_EVEN);

                wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);

                workingAmountBD = wMonthlyBasicBD.multiply(wRateAmt);

                amountBD = new BigDecimal(String.valueOf(workingAmountBD.doubleValue())).setScale(2, RoundingMode.FLOOR);

            } else {
                if (empDed.getSpecialAllowanceType().isArrearsType()) {
                    //Use WHOLE Value...
                    amountBD = new BigDecimal(Double.toString(empDed.getAmount())).setScale(2, RoundingMode.HALF_EVEN);

                    empDed.setActAllowAmt(amountBD.doubleValue());
                } else {
                    amountBD = new BigDecimal(Double.toString(empDed.getAmount())).setScale(2, RoundingMode.HALF_EVEN);
                    amountBD = amountBD.multiply(this.wNoOfDaysBD).setScale(2, RoundingMode.HALF_EVEN);

                }

            }
            if (useGlobalPercent)
                if (effectOnSpecAllow && effectOnTerm && this.leastNoOfDays <= pEmpPayBean.getNoOfDays())
                    amountBD = amountBD.multiply(globalPercent);

            empDed.setActAllowAmt(amountBD.doubleValue());
            if (pEmpPayBean.getSpecialAllowanceList() == null) {
                pEmpPayBean.setSpecialAllowanceList(new ArrayList<>());
            }
            pEmpPayBean.getSpecialAllowanceList().add(empDed);
            staffTotalAmountBD = staffTotalAmountBD.add(amountBD);
            wCurrentAmountBD = wCurrentAmountBD.add(amountBD);
            retVal = retVal.add(amountBD);

        }
        pEmpPayBean.setTotalAllowance(staffTotalAmountBD.doubleValue());
        pEmpPayBean.setSpecialAllowance(wCurrentAmountBD.doubleValue());
        return retVal.doubleValue();
    }

    private AbstractPaycheckEntity removeEmployeeNonTaxableDeductions(AbstractPaycheckEntity pEmpPayBean, SalaryInfo pSS) {

        deductionList = this.employeeDeductions.get(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId());
        if ((deductionList == null) || (deductionList.isEmpty()))
            return pEmpPayBean;


        wMonthlyBasicBD = new BigDecimal(Double.toString(pSS.getMonthlyBasicSalary() / 12.0)).setScale(2, RoundingMode.HALF_EVEN);
        wPayPercentBD = new BigDecimal(Double.toString(pEmpPayBean.getPayPercentage())).setScale(2, RoundingMode.HALF_EVEN);
        staffTotalAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getTotalDeductions())).setScale(2, RoundingMode.HALF_EVEN);
        wTotContAmt = new BigDecimal(Double.toString(pEmpPayBean.getTotCurrDed())).setScale(2, RoundingMode.HALF_EVEN);
        wTotNonTaxDedDB = new BigDecimal(Double.toString(pEmpPayBean.getTotalNonTaxableDeductions())).setScale(2, RoundingMode.HALF_EVEN);
        unionDuesBD = new BigDecimal("0.00").setScale(2,RoundingMode.HALF_EVEN);
        rate = 0.0D;
        boolean setAsideUnionDues = this.businessCertificate.isSubeb();
        for (AbstractDeductionEntity empDed : deductionList) {
            if ((empDed.isTaxExempt()) && (empDed.getAmount() > 0.0D)) {
                //double deductionAmount = 0.0D;
                if (empDed.getPayTypes().isUsingPercentage()) {
                    rate = empDed.getAmount();
                    if ((rate >= 95.0D) || (rate <= 0.0D)) {
                        continue;
                    }
                    wRateAmt = new BigDecimal(Double.toString(rate));
                    wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);
                    //rate /= 100.0D;

                    workingAmountBD = wMonthlyBasicBD.multiply(wRateAmt).setScale(2, RoundingMode.HALF_EVEN);


                } else {

                    workingAmountBD = new BigDecimal(Double.toString(empDed.getAmount()));

                }


                if (pEmpPayBean.isPercentagePayment()) {

                    workingAmountBD = workingAmountBD.multiply(wPayPercentBD).setScale(2, RoundingMode.HALF_EVEN);
                    if (useGlobalPercent) {
                        if (effectOnDeductions && effectOnSuspension)
                            workingAmountBD = workingAmountBD.multiply(globalPercent);
                    }
                } else {
                    if (useGlobalPercent) {
                        if (effectOnDeductions)
                            workingAmountBD = workingAmountBD.multiply(globalPercent);
                    }
                }
                amountBD = new BigDecimal(String.valueOf(workingAmountBD.doubleValue())).setScale(2, RoundingMode.FLOOR);
                empDed.setAmount(amountBD.doubleValue());
                //Now Here.. create a
                staffTotalAmountBD = staffTotalAmountBD.add(amountBD);
                wTotContAmt = wTotContAmt.add(amountBD);
                wTotNonTaxDedDB = wTotNonTaxDedDB.add(amountBD);
                if(setAsideUnionDues && empDed.getEmpDeductionType().isUnionDueType())
                    unionDuesBD = unionDuesBD.add(amountBD);

                if (pEmpPayBean.getEmployeeDeductions() == null) {
                    pEmpPayBean.setEmployeeDeductions(new ArrayList<>());
                }

                pEmpPayBean.getEmployeeDeductions().add(empDed);
            }


        }
        pEmpPayBean.setTotalDeductions(staffTotalAmountBD.doubleValue());
        pEmpPayBean.setTotCurrDed(wTotContAmt.doubleValue());
        pEmpPayBean.setTotalNonTaxableDeductions(wTotNonTaxDedDB.doubleValue());
        if(setAsideUnionDues)
            pEmpPayBean.setUnionDues(pEmpPayBean.getUnionDues() + unionDuesBD.doubleValue());
        return pEmpPayBean;
    }

    private AbstractPaycheckEntity removeEmployeeNonTaxableDeductionsByDays(AbstractPaycheckEntity pEmpPayBean, BigDecimal pPayAmtBD) {
        deductionList = this.employeeDeductions.get(pEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getId());

        if ((deductionList == null) || (deductionList.isEmpty()))
            return pEmpPayBean;

        staffTotalAmountBD = new BigDecimal(Double.toString(pEmpPayBean.getTotalDeductions())).setScale(2, RoundingMode.HALF_EVEN);
        wTotContAmt = new BigDecimal(Double.toString(pEmpPayBean.getTotCurrDed())).setScale(2, RoundingMode.HALF_EVEN);
        wTotNonTaxDedDB = new BigDecimal(Double.toString(pEmpPayBean.getTotalNonTaxableDeductions())).setScale(2, RoundingMode.HALF_EVEN);
        // wNoOfDaysBD = new BigDecimal(Double.toString((new Double(pEmpPayBean.getNoOfDays()) / new Double(this.getNoOfDays()))));
        unionDuesBD = new BigDecimal("0.00").setScale(2,RoundingMode.HALF_EVEN);

        boolean setAsideUnionDues = this.businessCertificate.isSubeb();

        for (AbstractDeductionEntity empDed : deductionList) {
            if ((empDed.isTaxExempt()) && (empDed.getAmount() > 0.0D)) {
                //double deductionAmount = 0.0D;
                if (empDed.getPayTypes().isUsingPercentage()) {
                    double rate = empDed.getAmount();
                    if ((rate >= 100.0D) || (rate <= 0.0D)) {
                        continue;
                    }

                    //rate /= 100.0D;
                    wRateAmt = new BigDecimal(Double.toString(rate));
                    wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.FLOOR);

                    workingAmountBD = pPayAmtBD.multiply(wRateAmt).setScale(2, RoundingMode.HALF_EVEN);

                } else {
                    workingAmountBD = new BigDecimal(Double.toString(empDed.getAmount())).setScale(2, RoundingMode.HALF_EVEN);
                    workingAmountBD = workingAmountBD.multiply(wNoOfDaysBD).setScale(2, RoundingMode.HALF_EVEN);
                }

                if (useGlobalPercent) {
                    if (effectOnDeductions && effectOnTerm && this.leastNoOfDays <= pEmpPayBean.getNoOfDays())
                        workingAmountBD = workingAmountBD.multiply(globalPercent);
                }
                amountBD = new BigDecimal(String.valueOf(workingAmountBD.doubleValue())).setScale(2, RoundingMode.FLOOR);
                empDed.setAmount(amountBD.doubleValue());
                staffTotalAmountBD = staffTotalAmountBD.add(amountBD);
                wTotContAmt = wTotContAmt.add(amountBD);
                wTotNonTaxDedDB = wTotNonTaxDedDB.add(amountBD);
                if(setAsideUnionDues && empDed.getEmpDeductionType().isUnionDueType())
                    unionDuesBD = unionDuesBD.add(amountBD);

                if (pEmpPayBean.getEmployeeDeductions() == null) {
                    pEmpPayBean.setEmployeeDeductions(new ArrayList<>());
                }
                pEmpPayBean.getEmployeeDeductions().add(empDed);
            }

        }
        pEmpPayBean.setTotalDeductions(staffTotalAmountBD.doubleValue());
        pEmpPayBean.setTotCurrDed(wTotContAmt.doubleValue());
        pEmpPayBean.setTotalNonTaxableDeductions(wTotNonTaxDedDB.doubleValue());
        if(setAsideUnionDues)
            pEmpPayBean.setUnionDues(pEmpPayBean.getUnionDues() + unionDuesBD.doubleValue());
        return pEmpPayBean;
    }

    private AbstractPaycheckEntity removeGarnishments(AbstractPaycheckEntity pE, boolean pAdj4NegPay) {
        double retVal = pE.getNetPay();
        double garnishAmt;
        BigDecimal _amount;
        List<AbstractGarnishmentEntity> pEmpGarnList = this.employeeGarnishments.get(pE.getHiringInfo().getAbstractEmployeeEntity().getId());
        if ((pEmpGarnList == null) || (pEmpGarnList.isEmpty())) {
            pE.setNetPay(retVal);
            return pE;
        }
        Collections.sort(pEmpGarnList);

        for (AbstractGarnishmentEntity e : pEmpGarnList) {
            if (e.getOwedAmount() > 0.00D) {
                garnishAmt = EntityUtils.convertDoubleToEpmStandard(e.getAmount());

                if (useGlobalPercent)
                    if (loanEffect) {
                        _amount = new BigDecimal(Double.toString(garnishAmt));
                        _amount.multiply(globalPercent);
                        garnishAmt = _amount.doubleValue();
                    }

                if (garnishAmt < e.getOwedAmount()) {

                    if (pAdj4NegPay) {
                        if (garnishAmt >= retVal) {
                            continue;
                        }
                    }
                    retVal -= garnishAmt;

                    pE.setTotalGarnishments(pE.getTotalGarnishments() + garnishAmt);
                    e.setOldOwedAmount(e.getOwedAmount());
                    e.setCurrentOwedAmount(e.getOwedAmount() - garnishAmt);
                } else if ((garnishAmt >= e.getOwedAmount()) && (e.getOwedAmount() > 0.0D)) {
                    garnishAmt = EntityUtils.convertDoubleToEpmStandard(e.getOwedAmount());
                    if (useGlobalPercent)
                        if (loanEffect) {
                            _amount = new BigDecimal(Double.toString(garnishAmt));
                            _amount.multiply(globalPercent);
                            garnishAmt = _amount.doubleValue();
                        }

                    if (pAdj4NegPay) {
                        if (garnishAmt >= retVal) {
                            continue;
                        }
                    }
                    retVal -= garnishAmt;
                    pE.setTotalGarnishments(pE.getTotalGarnishments() + garnishAmt);

                    e.setOldOwedAmount(e.getOwedAmount());
                    e.setCurrentOwedAmount(0.0D);
                    e.setOwedAmount(0.0D);
                }
                e.setActGarnAmt(garnishAmt);

                if (pE.getEmployeeGarnishments() == null)
                    pE.setEmployeeGarnishments(new ArrayList<>());
                pE.getEmployeeGarnishments().add(e);
            }
        }
        pE.setNetPay(retVal);
        return pE;
    }

    private AbstractPaycheckEntity removeEmployeeDeductions(AbstractPaycheckEntity pE, double pCurrentNetPay, double pBasicSalary) {
        retVal = new BigDecimal(pCurrentNetPay).setScale(2, RoundingMode.HALF_EVEN);
        wMonthlyBasicBD = new BigDecimal(Double.toString(pBasicSalary)).setScale(2, RoundingMode.HALF_EVEN);
        wPayPercentBD = new BigDecimal(pE.getPayPercentage()).setScale(2, RoundingMode.HALF_EVEN);
        staffTotalAmountBD = new BigDecimal(Double.toString(pE.getTotalDeductions())).setScale(2, RoundingMode.HALF_EVEN);
        wTotContAmt = new BigDecimal(pE.getTotCurrDed()).setScale(2, RoundingMode.HALF_EVEN);
        workingAmountBD = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_EVEN);
        unionDuesBD = new BigDecimal("0.00").setScale(2,RoundingMode.HALF_EVEN);
        rate = 0.0D;
        boolean setAsideUnionDues = this.businessCertificate.isSubeb();


        deductionList = this.employeeDeductions.get(pE.getHiringInfo().getAbstractEmployeeEntity().getId());
        if ((deductionList == null) || (deductionList.isEmpty())) {
            pE.setNetPay(pCurrentNetPay);
            return pE;
        }

        for (AbstractDeductionEntity empDed : deductionList) {
            if (empDed.isTaxExempt())
                continue;

            if (empDed.getAmount() > 0.0D) {

                if (empDed.getPayTypes().isUsingPercentage()) {
                    rate = empDed.getAmount();
                    if ((rate >= 25.0D) || (rate < 0.0D)) {
                        continue;
                    }

                    wRateAmt = new BigDecimal(Double.toString(rate));
                    wRateAmt = wRateAmt.divide(w100BD, 4, RoundingMode.FLOOR);


                    workingAmountBD = wMonthlyBasicBD.multiply(wRateAmt);

                } else {
                    workingAmountBD = new BigDecimal(empDed.getAmount()).setScale(2, RoundingMode.HALF_EVEN);

                }
                if (pE.isPercentagePayment()) {
                    workingAmountBD = workingAmountBD.multiply(wPayPercentBD);
                    if (effectOnDeductions && effectOnSuspension)
                        workingAmountBD = workingAmountBD.multiply(globalPercent);
                }
                amountBD = new BigDecimal(String.valueOf(workingAmountBD.doubleValue())).setScale(2, RoundingMode.FLOOR);
                retVal = retVal.subtract(amountBD);

                if (pE.getEmployeeDeductions() == null) {
                    pE.setEmployeeDeductions(new ArrayList<>());
                }

                empDed.setAmount(amountBD.doubleValue());
                pE.getEmployeeDeductions().add(empDed);
                staffTotalAmountBD = staffTotalAmountBD.add(amountBD);
                wTotContAmt = wTotContAmt.add(amountBD);
                if(setAsideUnionDues && empDed.getEmpDeductionType().isUnionDueType())
                    unionDuesBD = unionDuesBD.add(amountBD);

            }
        }

        pE.setTotalDeductions(staffTotalAmountBD.doubleValue());
        pE.setTotCurrDed(wTotContAmt.doubleValue());
        pE.setNetPayAfterTaxableDeductions(retVal.doubleValue());
        pE.setNetPay(retVal.doubleValue());
        if(setAsideUnionDues)
            pE.setUnionDues(pE.getUnionDues() + unionDuesBD.doubleValue());
        return pE;
    }

    private AbstractPaycheckEntity removeEmployeeDeductionsByDays(AbstractPaycheckEntity pE, double pCurrentNetPay, double pBasicSalary) {
        retVal = new BigDecimal(pCurrentNetPay).setScale(2, RoundingMode.HALF_EVEN);
        wMonthlyBasicBD = new BigDecimal(Double.toString(pBasicSalary)).setScale(2, RoundingMode.HALF_EVEN);

        staffTotalAmountBD = new BigDecimal(Double.toString(pE.getTotalDeductions())).setScale(2, RoundingMode.HALF_EVEN);
        wTotContAmt = new BigDecimal(pE.getTotCurrDed()).setScale(2, RoundingMode.HALF_EVEN);
        // wNoOfDaysBD = new BigDecimal(Double.toString((new Double(pE.getNoOfDays()) / new Double(this.getNoOfDays()))));
        unionDuesBD = new BigDecimal("0.00").setScale(2,RoundingMode.HALF_EVEN);
        rate = 0.0D;
        boolean setAsideUnionDues = this.businessCertificate.isSubeb();

        workingAmountBD = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_EVEN);
        deductionList = this.employeeDeductions.get(pE.getHiringInfo().getAbstractEmployeeEntity().getId());
        if ((deductionList == null) || (deductionList.isEmpty())) {
            pE.setNetPay(pCurrentNetPay);
            return pE;
        }
        for (AbstractDeductionEntity empDed : deductionList) {
            if (empDed.isTaxExempt())
                continue;
            if (empDed.getAmount() > 0.0D) {
                if (empDed.getPayTypes().isUsingPercentage()) {
                    rate = empDed.getAmount();
                    if ((rate >= 95.0D) || (rate <= 0.0D)) {
                        continue;
                    }

                    wRateAmt = new BigDecimal(Double.toString(rate));
                    wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);

                    workingAmountBD = wMonthlyBasicBD.multiply(wRateAmt);

                } else {
                    workingAmountBD = new BigDecimal(empDed.getAmount()).setScale(2, RoundingMode.HALF_EVEN);
                    workingAmountBD = workingAmountBD.multiply(wNoOfDaysBD).setScale(2, RoundingMode.HALF_EVEN);
                }

                if (effectOnDeductions && effectOnTerm && this.leastNoOfDays <= pE.getNoOfDays())
                    workingAmountBD = workingAmountBD.multiply(globalPercent);

                amountBD = new BigDecimal(String.valueOf(workingAmountBD.doubleValue())).setScale(2, RoundingMode.FLOOR);
                retVal = retVal.subtract(amountBD);
                if (pE.getEmployeeDeductions() == null)
                    pE.setEmployeeDeductions(new ArrayList<>());
                empDed.setAmount(amountBD.doubleValue());
                pE.getEmployeeDeductions().add(empDed);
                wTotContAmt = wTotContAmt.add(amountBD);
                staffTotalAmountBD = staffTotalAmountBD.add(amountBD);
                if(setAsideUnionDues && empDed.getEmpDeductionType().isUnionDueType())
                    unionDuesBD = unionDuesBD.add(amountBD);
            }
        }
        pE.setTotalDeductions(staffTotalAmountBD.doubleValue());
        pE.setTotCurrDed(wTotContAmt.doubleValue());
        pE.setNetPayAfterTaxableDeductions(retVal.doubleValue());
        pE.setNetPay(retVal.doubleValue());
        if(setAsideUnionDues)
            pE.setUnionDues(pE.getUnionDues()+unionDuesBD.doubleValue());
        return pE;
    }

   /* public double convertDoubleToEpmStandard(double pValue) {
       // BigDecimal bd = new BigDecimal(String.valueOf(pValue)).setScale(2, RoundingMode.HALF_EVEN);
      //  return bd.doubleValue();
        String value = String.valueOf(pValue);

        if(value.substring(value.indexOf(".") + 1).length() > 2) {

            BigDecimal bd = new BigDecimal(String.valueOf(pValue)).setScale(2, RoundingMode.FLOOR);
            return bd.doubleValue();
        }else {
            return pValue;
        }

    }*/

    private Map<String, Double> makeAllowanceRuleMap(AllowanceRuleMaster allowanceRuleMaster) {
        Map<String, Double> allowMap = new HashMap<>();
        if (allowanceRuleMaster == null || allowanceRuleMaster.isNewEntity())
            return allowMap;
        for (AllowanceRuleDetails details : allowanceRuleMaster.getAllowanceRuleDetailsList())
            allowMap.put(details.getBeanFieldName(), details.getApplyYearlyValue());

        return allowMap;

    }

    protected boolean isRerun() {
        return rerun;
    }

}