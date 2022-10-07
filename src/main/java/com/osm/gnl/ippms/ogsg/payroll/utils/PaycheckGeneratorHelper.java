/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.domain.beans.PayslipHelperBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.engine.PayrollEngineHelper;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import com.osm.gnl.ippms.ogsg.utils.annotation.AnnotationProcessor;
import com.osm.gnl.ippms.ogsg.utils.annotation.PaycheckAllowance;
import com.osm.gnl.ippms.ogsg.utils.annotation.SalaryAllowance;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class PaycheckGeneratorHelper {


    public static PayslipHelperBean setPayGroupAllowances(SalaryInfo pSalaryInfo, EmployeePayMiniBean pEmpPayMiniBean, AbstractPaycheckEntity pEmpPayBean,
                                                          HashMap<String, EmpDeductMiniBean> allowances, boolean pSetCurrent) {

        Map<String,Double> ruleMap = new HashMap<>();
        if(pEmpPayBean.getAllowanceRuleMaster() != null && !pEmpPayBean.getAllowanceRuleMaster().isNewEntity())
            ruleMap = PayrollEngineHelper.getRulesDetails(pEmpPayBean.getAllowanceRuleMaster());

        try {
            Map<Field, Double> allowMap = AnnotationProcessor.getAllowanceFields(pEmpPayBean, Double.class, PaycheckAllowance.class);

            Set<Field> keys = allowMap.keySet();
            String wKey;
            EmpDeductMiniBean egmb;
            boolean mustSet;
            for (Field f : keys) {
                mustSet = false;
                double value = allowMap.get(f);
                wKey = f.getAnnotation(PaycheckAllowance.class).type();
                if(ruleMap.containsKey(wKey))
                    mustSet = true; //This takes care of instances of PayGroup Allowance Rule.

                if (value > 0.0D || mustSet) {


                    if (allowances.containsKey(wKey)) {
                        egmb = allowances.get(wKey);
                    } else {
                        egmb = new EmpDeductMiniBean();
                        egmb.setName(wKey);
                    }
                    if (pSetCurrent) {
                        pEmpPayMiniBean.setCurrentAllowanceTotal(pEmpPayMiniBean.getCurrentAllowanceTotal() + value);
                        egmb.setCurrentDeduction(egmb.getCurrentDeduction() + value);
                    } else {
                        egmb.setYearToDate(egmb.getYearToDate() + value);
                        pEmpPayMiniBean.setAllowanceTotal(pEmpPayMiniBean.getAllowanceTotal() + value);
                    }

                    allowances.put(wKey, egmb);
                }


            }
            //-- Now do the SalaryInfo ones...IIF not pensioner
            if (!pEmpPayBean.getParentObject().isPensioner()) {
                allowMap = AnnotationProcessor.getAllowanceFields(pSalaryInfo, Double.class, SalaryAllowance.class);
                keys = allowMap.keySet();
                String wForPaySlip;
                for (Field f : keys) {
                    wForPaySlip = f.getAnnotation(SalaryAllowance.class).forPaySlip();
                    if(wForPaySlip.equalsIgnoreCase("N")) continue;

                    double value = allowMap.get(f);
                    if (value > 0.0D) {

                        wKey = f.getAnnotation(SalaryAllowance.class).type();

                        if(ruleMap.containsKey(wKey))
                            value = ruleMap.get(wKey);


                        if (allowances.containsKey(wKey)) {
                            egmb = allowances.get(wKey);
                        } else {
                            egmb = new EmpDeductMiniBean();
                            egmb.setName(wKey);
                        }
                        //-- Salary Info Values are Yearly
                        if(pEmpPayBean.getNetPay() == 0)
                            value = 0;
                        else{
                            if(value > 0.0D){
                                value = PayrollPayUtils.convertDoubleToEpmStandard(value / 12.0D);
                            }
                        }


                        if (pEmpPayBean.isPercentagePayment()) {
                            value = EntityUtils.convertDoubleToEpmStandard(value * pEmpPayBean.getPayPercentage());
                        }
                        if (pEmpPayBean.isPayByDays()) {
                            value = PayrollPayUtils.getPartPayment(value, pEmpPayBean.getNoOfDays(), pEmpPayBean.getRunMonth(), pEmpPayBean.getRunYear(), false);
                            //amount = PayrollPayUtils.convertDoubleToEpmStandard(amount / PayrollBeanUtils.getNoOfDays(pEmpPayBean.getRunMonth(), pEmpPayBean.getRunYear()) * pEmpPayBean.getNoOfDays());
                        }
                        if (pSetCurrent) {
                            pEmpPayMiniBean.setCurrentAllowanceTotal(pEmpPayMiniBean.getCurrentAllowanceTotal() + value);
                            egmb.setCurrentDeduction(egmb.getCurrentDeduction() + value);
                        } else {
                            egmb.setYearToDate(egmb.getYearToDate() + value);
                            pEmpPayMiniBean.setAllowanceTotal(pEmpPayMiniBean.getAllowanceTotal() + value);
                        }

                        allowances.put(wKey, egmb);
                    }
                }

            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new PayslipHelperBean(pEmpPayMiniBean, allowances);
    }

    public static PayslipHelperBean setYTDAllowanceValues(SalaryInfo pSalaryInfo, EmployeePayMiniBean pEmpPayMiniBean, AbstractPaycheckEntity pEmpPayBean, HashMap<String, EmpDeductMiniBean> allowances) {
        Map<String,Double> ruleMap = new HashMap<>();
        if(pEmpPayBean.getAllowanceRuleMaster() != null && !pEmpPayBean.getAllowanceRuleMaster().isNewEntity())
            ruleMap = PayrollEngineHelper.getRulesDetails(pEmpPayBean.getAllowanceRuleMaster());
        try {
            Map<Field, Double> AllowMap = AnnotationProcessor.getAllowanceFields(pEmpPayBean, Double.class, PaycheckAllowance.class);
            Set<Field> keys = AllowMap.keySet();
            String wKey;
            EmpDeductMiniBean egmb;
            for (Field f : keys) {

                double value = AllowMap.get(f);
                if (value > 0.0D) {
                    wKey = f.getAnnotation(PaycheckAllowance.class).type();
                    if (allowances.containsKey(wKey)) {
                        egmb = allowances.get(wKey);
                    } else {
                        egmb = new EmpDeductMiniBean();
                        egmb.setName(wKey);
                    }

                    egmb.setYearToDate(egmb.getYearToDate() + value);
                    allowances.put(wKey, egmb);
                }


            }
            if (!pEmpPayBean.getParentObject().isPensioner()) {
                AllowMap = AnnotationProcessor.getAllowanceFields(pSalaryInfo, Double.class, SalaryAllowance.class);
                keys = AllowMap.keySet();
                String wForPaySlip;
                double value;
                for (Field f : keys) {
                    wForPaySlip = f.getAnnotation(SalaryAllowance.class).forPaySlip();

                    if(wForPaySlip.equalsIgnoreCase("N")) continue;
                    wKey = f.getAnnotation(SalaryAllowance.class).type();
                    if(ruleMap.containsKey(wKey))
                        value = ruleMap.get(wKey);
                    else
                        value = AllowMap.get(f);

                    if (value > 0.0D) {

                        if (allowances.containsKey(wKey)) {
                            egmb = allowances.get(wKey);
                        } else {
                            egmb = new EmpDeductMiniBean();
                            egmb.setName(wKey);
                        }
                        //-- Salary Info Values are Yearly
                        value = value / 12.0D;
                        if (pEmpPayBean.isPercentagePayment()) {
                            value = PayrollPayUtils.convertDoubleToEpmStandard(value * pEmpPayBean.getPayPercentage());
                        }
                        if (pEmpPayBean.isPayByDays()) {
                            value = PayrollPayUtils.getPartPayment(value, pEmpPayBean.getNoOfDays(), pEmpPayBean.getRunMonth(), pEmpPayBean.getRunYear(), false);
                        }

                        egmb.setYearToDate(egmb.getYearToDate() + value);
                        allowances.put(wKey, egmb);
                    }
                }

            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new PayslipHelperBean(pEmpPayMiniBean, allowances);

    }
}
