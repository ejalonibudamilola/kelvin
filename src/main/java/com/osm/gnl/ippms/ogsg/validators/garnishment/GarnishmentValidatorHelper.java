/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.garnishment;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GarnishmentValidatorHelper {


    public  synchronized List<String> validateGarnishmentFileUpload(GenericService genericService, AbstractGarnishmentEntity eGI, BusinessCertificate bc, ConfigurationBean configurationBean) throws InstantiationException, IllegalAccessException {
        List<String> wErrorMsg = new ArrayList<>();
        double wOA = eGI.getOwedAmount();

        if(wOA == 0){
            //--Coming from Single Loan Source. Convert OwedAmountStr...
            wOA = Double.parseDouble(PayrollHRUtils.removeCommas(eGI.getOwedAmountStr()));
        }
        if(wOA == 0)
            return wErrorMsg; //i.e., do not validate at all.
        List<AbstractGarnishmentEntity> eList = (List<AbstractGarnishmentEntity>)genericService.loadAllObjectsWithSingleCondition(IppmsUtils.getGarnishmentInfoClass(bc), CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), eGI.getParentId()), null);
         double totalDed = 0.0D;
        AbstractEmployeeEntity e = IppmsUtils.loadEmployee(genericService, eGI.getParentId(), bc);

        //
        double tentativeSalary = e.getSalaryInfo().getMonthlySalary();
        double netPay = 0.0;
        double averageTax = new BigDecimal(tentativeSalary * 0.15D).setScale(2,RoundingMode.HALF_EVEN).doubleValue();
        tentativeSalary -= averageTax;

        double monthlyBasicSalary = new BigDecimal(e.getSalaryInfo().getMonthlyBasicSalary() / 12.0D).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        BigDecimal totalDeductionsBD;
        BigDecimal wRateAmt;
        //add all special allowances to the salary
        List<AbstractSpecialAllowanceEntity> wSpecAllow = (List<AbstractSpecialAllowanceEntity>)genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getSpecialAllowanceInfoClass(bc), Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), e.getId()),
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("expire", 0)), null);
        if(IppmsUtils.isNotNullOrEmpty(wSpecAllow)) {

            for (AbstractSpecialAllowanceEntity s : wSpecAllow) {
                 if(s.getAmount() > 0 ) {
                     if (s.getPayTypes().isUsingPercentage()) {
                         wRateAmt = new BigDecimal(Double.toString(s.getAmount()));
                         wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);
                         //rate /= 100.0D;

                         totalDeductionsBD = new BigDecimal(Double.toString((monthlyBasicSalary)));

                         tentativeSalary += totalDeductionsBD.multiply(wRateAmt).doubleValue();
                     } else {
                         tentativeSalary += s.getAmount();
                     }
                 }
            }
        }
        netPay = tentativeSalary;
        //subtract all deductions from the pay of the employee
        List<AbstractDeductionEntity> wDedList = (List<AbstractDeductionEntity>)genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getDeductionInfoClass(bc), Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), e.getId()),
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), null);
        for (AbstractDeductionEntity eDI : wDedList) {
            if (eDI.getAmount() == 0)
                continue;
               if(eDI.getEmpDeductionType().getPayTypes().isUsingPercentage()){
                   wRateAmt = new BigDecimal(Double.toString(eDI.getAmount()));
                   wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);
                   //rate /= 100.0D;

                   totalDeductionsBD = new BigDecimal(Double.toString((monthlyBasicSalary)));

                   totalDed += totalDeductionsBD.multiply(wRateAmt).doubleValue();
               }else{
                   totalDed += eDI.getAmount();
               }


        }

        //if the employee has any garnishments/loans then subtract from employee pay
        double newGarnishAmount;
        if (!eList.isEmpty()) {
            for (AbstractGarnishmentEntity g : eList) {
                double deductAmt = 0.0D;


                if ((!eGI.isNewEntity()) && (eGI.getId().equals(g.getId()))) {
                    if(eGI.getNewLoanTerm() > 0)
                        newGarnishAmount = wOA / eGI.getNewLoanTerm();
                    else
                        newGarnishAmount = wOA / eGI.getLoanTerm();
                    deductAmt = newGarnishAmount;
                } else if (g.getOwedAmount() > 0) {
                    deductAmt = g.getAmount();
                }

                totalDed += deductAmt;

            }
        }
        if (eGI.isNewEntity()) {
            totalDed += wOA / eGI.getLoanTerm();

        }
        tentativeSalary -= totalDed;
        if (tentativeSalary < 0.0D) {
            wErrorMsg.add(bc.getStaffTypeName() + " with '" + bc.getStaffTitle() + "' : " + e.getEmployeeId() +
                            " will have a Net Pay less than 0");
            wErrorMsg.add("Total Emoluments After Tax (@15% estimated)= "+IConstants.naira+PayrollHRUtils.getDecimalFormat().format(netPay));
            wErrorMsg.add("Total Monthly Obligations (Loans and Deductions) = "+ IConstants.naira + "" + PayrollHRUtils.getDecimalFormat().format(totalDed));
            wErrorMsg.add("Loan Application Rejected.");

        }else{
             double loanToWagePercentage = getLoanToWagePercentage(e.getSalaryInfo().getMonthlySalary(),totalDed );
             if(loanToWagePercentage > configurationBean.getMaxLoanPercentage()){
                 wErrorMsg.add(bc.getStaffTypeName() + " with '" + bc.getStaffTitle() + "' : " + e.getEmployeeId() +
                         "  will have a Debt to Income Percentage of "+PayrollHRUtils.getDecimalFormat().format(loanToWagePercentage)+"%");
                 wErrorMsg.add("Configured (Acceptable) Debt to Income percentage is "+PayrollHRUtils.getDecimalFormat().format(configurationBean.getMaxLoanPercentage())+"%");
                 wErrorMsg.add("Total Emoluments After Tax (@15% estimated) = "+ IConstants.naira+PayrollHRUtils.getDecimalFormat().format(netPay));
                 wErrorMsg.add("Total Monthly Obligations (Loans and Deductions) = "+ IConstants.naira + "" + PayrollHRUtils.getDecimalFormat().format(totalDed));
                 wErrorMsg.add("Loan Application Rejected.");

             }
        }

        return wErrorMsg;
    }

    private double getLoanToWagePercentage(double monthlySalary, double totalDeductions) {
        double retVal = 0.0D;
        if(monthlySalary == 0 || totalDeductions <= 0)
            return retVal;
        else{
            BigDecimal monthlySalaryBD =   new BigDecimal(Double.toString(monthlySalary)).setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal totalDeductionsBD =   new BigDecimal(Double.toString(totalDeductions)).setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal _100 = new BigDecimal(Double.toString(100.00));
            totalDeductionsBD = totalDeductionsBD.divide(monthlySalaryBD, 2, RoundingMode.HALF_UP);
            retVal = totalDeductionsBD.multiply(_100).doubleValue();
        }
        return retVal;
    }
}
