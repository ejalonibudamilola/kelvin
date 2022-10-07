
/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleDetails;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollPayUtils;
import com.osm.gnl.ippms.ogsg.utils.annotation.AnnotationProcessor;
import com.osm.gnl.ippms.ogsg.utils.annotation.PaycheckAllowance;
import com.osm.gnl.ippms.ogsg.utils.annotation.SalaryAllowance;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@Slf4j
public abstract class PayrollEngineHelper implements IBigDecimalValues{
	
	 protected synchronized static double calculateTaxes(double pGrossIncome, double pFreePay) {
		    double taxesDue;

		    BigDecimal reminant = new BigDecimal(pGrossIncome - pFreePay * 12.0).setScale(2,RoundingMode.HALF_EVEN);
		    BigDecimal divisorDB = new BigDecimal(12.0).setScale(2, RoundingMode.HALF_EVEN);
		    BigDecimal workingTaxAmount = new BigDecimal("0.00").setScale(2,RoundingMode.HALF_EVEN);
		    
		    
		    if (reminant.doubleValue() <= 0.0D) {
		      workingTaxAmount = new BigDecimal(pGrossIncome * 0.01).setScale(2,RoundingMode.HALF_EVEN);
		      taxesDue = workingTaxAmount.divide(divisorDB,2, RoundingMode.HALF_EVEN).doubleValue();
		      return taxesDue;
		    }

		    if (reminant.doubleValue() <= 300000.0D)
		    {
		    	
		      workingTaxAmount = reminant.multiply(w7Percent);
		      taxesDue = workingTaxAmount.divide(divisorDB,2, RoundingMode.HALF_EVEN).doubleValue();
		      return taxesDue;
		    }

		    if (reminant.doubleValue() >= 300000.0D) {
		      workingTaxAmount = w21k;
		      reminant = reminant.subtract(w300k);
		    }

		    if (reminant.doubleValue() >= 300000.0D) {
		      workingTaxAmount = workingTaxAmount.add(w33k);
		      reminant = reminant.subtract(w300k);
		    }
		    else {
		      workingTaxAmount = workingTaxAmount.add(reminant.multiply(w11Percent));
		      //reminant = new BigDecimal("0.00");
		      taxesDue = workingTaxAmount.divide(divisorDB,2, RoundingMode.HALF_EVEN).doubleValue();
		      return taxesDue;
		    }

		    if (reminant.doubleValue() >= 500000.0D) {
		      workingTaxAmount = workingTaxAmount.add(w75k);
		      reminant = reminant.subtract(w500k);
		    } else {
		      workingTaxAmount = workingTaxAmount.add(reminant.multiply(w15Percent));
		      taxesDue = workingTaxAmount.divide(divisorDB,2, RoundingMode.HALF_EVEN).doubleValue();
		      return taxesDue;
		    }
		    if (reminant.doubleValue() >= 500000.0D) {
		    	 workingTaxAmount = workingTaxAmount.add(w95k);
		    	 reminant = reminant.subtract(w500k);
		    } else {
		    	workingTaxAmount = workingTaxAmount.add(reminant.multiply(w19Percent));
		        taxesDue = workingTaxAmount.divide(divisorDB,2, RoundingMode.HALF_EVEN).doubleValue();
		        return taxesDue;
		    }
		    if (reminant.doubleValue() >= 1600000.0D) {
		    	 workingTaxAmount = workingTaxAmount.add(w336k);
		    	 reminant = reminant.subtract(w1Point6M);
		    } else {
		    	workingTaxAmount = workingTaxAmount.add(reminant.multiply(w21Percent));
		        taxesDue = workingTaxAmount.divide(divisorDB,2, RoundingMode.HALF_EVEN).doubleValue();
		      return taxesDue;
		    }
		    if (reminant.doubleValue() > 0.0D) {
		    	workingTaxAmount = workingTaxAmount.add(reminant.multiply(w24Percent));
		      
		    }

		    taxesDue = workingTaxAmount.divide(divisorDB,2, RoundingMode.HALF_EVEN).doubleValue();
		    return EntityUtils.convertDoubleToEpmStandard(taxesDue);
		  }
	 protected synchronized static double getRelief(double pGrossIncome)
	  {
		
		BigDecimal wReliefAmount = new BigDecimal( 0.2D * pGrossIncome).setScale(2, RoundingMode.HALF_EVEN);
	    BigDecimal wConsolidatedReliefBD = new BigDecimal( "200000.00").setScale(2, RoundingMode.HALF_EVEN);
	    BigDecimal wOnePercentBD = new BigDecimal(0.01 * pGrossIncome).setScale(2, RoundingMode.HALF_EVEN);
	    double reliefAmount = 0.0D;
	    //double consolidatedRelief = 200000.0D;
	    //double onePercent = 0.01D * pGrossIncome;

	    if (wConsolidatedReliefBD.doubleValue() > wOnePercentBD.doubleValue())
	    	reliefAmount = wConsolidatedReliefBD.doubleValue();
	    else {
	      reliefAmount = wOnePercentBD.doubleValue();
	    }
	    
	    reliefAmount += wReliefAmount.doubleValue();

	   /* if (this.addLifeInsuranceRelief) {
	      reliefAmount += 600000.0D;
	    }*/

	    return reliefAmount;
	  }

	public static synchronized AbstractPaycheckEntity setSalaryInfoValues(SalaryInfo pSalaryInfo, AbstractPaycheckEntity pEmpPayBean, int pNoOfDays,
						 double multiplyFactor, boolean effectOnTerm, double globalPercentFactor, int leastNoOfDays, AllowanceRuleMaster allowanceRuleMaster) {

		try {
            Map<String,Field> paycheckMap = AnnotationProcessor.getAllowanceFields(pEmpPayBean, PaycheckAllowance.class);
			Map<Field, Double>	AllowMap = AnnotationProcessor.getAllowanceFields(pSalaryInfo, Double.class, SalaryAllowance.class);
			Map<String,Double> ruleMap = getRulesDetails(allowanceRuleMaster);
			Set<Field> keys = AllowMap.keySet();
			//Class<?>[] parameterTypes = null;
			boolean setFieldValue;
			String wTaxable;
			Field newField;
				for (Field f : keys) {
					setFieldValue = true;
					wTaxable = f.getAnnotation(SalaryAllowance.class).key();
					if(String.CASE_INSENSITIVE_ORDER.compare(wTaxable,"Y") != 0){
						setFieldValue = false;
					}
					double value = AllowMap.get(f);
					if(ruleMap.containsKey(f.getAnnotation(SalaryAllowance.class).type()))
						value = ruleMap.get(f.getAnnotation(SalaryAllowance.class).type());

					double valueToSet;
					if (value > 0.0D) {
						//--First get the actual value to Set
						if(pEmpPayBean.isPayByDays()){
							valueToSet = PayrollPayUtils.getPartPayment(value,pEmpPayBean.getNoOfDays(), pNoOfDays, true);
							if(effectOnTerm && leastNoOfDays <= pEmpPayBean.getNoOfDays())
								valueToSet = PayrollPayUtils.convertDoubleToEpmStandard(valueToSet * globalPercentFactor);
						}else{
							   valueToSet = PayrollPayUtils.convertDoubleToEpmStandard((value / 12.0D) * multiplyFactor * globalPercentFactor);
						}

						if(setFieldValue){
							try{
								if(paycheckMap.containsKey(f.getName())){
									newField = paycheckMap.get(f.getName());
									newField.setAccessible(true);
									newField.set(pEmpPayBean,valueToSet);
								}
							}catch(Exception wEx){
								log.error(wEx.getMessage());
							}
						}

						pEmpPayBean.setTotalAllowance(pEmpPayBean.getTotalAllowance() + valueToSet);
					}

				}



		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return pEmpPayBean;

	}

	public static Map<String, Double> getRulesDetails(AllowanceRuleMaster allowanceRuleMaster) {
	 	Map<String,Double> wRetMap = new HashMap<>();
	 	if(allowanceRuleMaster != null && !allowanceRuleMaster.isNewEntity()){
	 		for(AllowanceRuleDetails details : allowanceRuleMaster.getAllowanceRuleDetailsList())
	 			wRetMap.put(details.getBeanFieldName(),details.getApplyYearlyValue());
		}
	 	return wRetMap;
	}

//	private static synchronized boolean objectContainsField(Object object, String fieldName) {
//		return Arrays.stream(object.getClass().getFields()).allMatch(f -> f.getName().equals(fieldName));
//	}

}
