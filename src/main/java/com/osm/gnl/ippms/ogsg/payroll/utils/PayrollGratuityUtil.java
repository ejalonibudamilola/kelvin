/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.NewPensionerBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class PayrollGratuityUtil {

	public static NewPensionerBean generateGatuityAndPension(
			NewPensionerBean pNewPensionerBean, PensionService pPensionService, BusinessCertificate businessCertificate)
	{

	
		BigDecimal wGratuity = new BigDecimal(Double.valueOf("0.00")).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wPension = wGratuity;
		BigDecimal wMonthlyPension = wGratuity;

		BigDecimal w10 = new BigDecimal(10.0D).setScale(2, RoundingMode.HALF_EVEN);
		boolean consolidated = false;
		double totalEmoluments = 0.0D;
		if(!IppmsUtils.isNullOrEmpty(pNewPensionerBean.getTotalPayStr()) && Double.parseDouble(PayrollHRUtils.removeCommas(pNewPensionerBean.getTotalPayStr())) > 0.0D){
			totalEmoluments = Double.parseDouble(PayrollHRUtils.removeCommas(pNewPensionerBean.getTotalPayStr()));
			consolidated = Boolean.valueOf(pNewPensionerBean.getConsolidatedIndStr());
		}else{
			totalEmoluments = pNewPensionerBean.getSalaryInfo().getTotalGrossPay();
			consolidated = pNewPensionerBean.getSalaryInfo().isPermanentSecretary();
		}
		//BigDecimal w8 = new BigDecimal(8.0D);
		BigDecimal w2 = new BigDecimal(2.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w20 = new BigDecimal(20.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w6 = new BigDecimal(6.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w3 = new BigDecimal(3.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w100 = new BigDecimal(100.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w60 = new BigDecimal(60.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wBeforeDiv = new BigDecimal(0.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wTotLen = new BigDecimal(pNewPensionerBean.getTotalLengthOfService());
		BigDecimal wTotEmol = new BigDecimal(totalEmoluments).setScale(2, RoundingMode.HALF_EVEN);
		
		if(pNewPensionerBean.getTotalLengthOfService() < 5){
			
		}else{
			
			if(pNewPensionerBean.isCalculateGratuity()
					&& pNewPensionerBean.getTotalLengthOfService() > 9){
				
			    wGratuity = wTotLen.multiply(w2); 
			    wBeforeDiv = wGratuity.divide(w3, 2, RoundingMode.HALF_EVEN);
			    wBeforeDiv = wBeforeDiv.add(w20).multiply(wTotEmol);
			    wGratuity = wBeforeDiv.divide(w100,2,RoundingMode.HALF_EVEN);
			    	
			}else if(pNewPensionerBean.isCalculateGratuity()
					&& pNewPensionerBean.getTotalLengthOfService() > 5 && pNewPensionerBean.getTotalLengthOfService() < 10){
				//wGratuity = wTotLen.multiply(w2).divide(w3).add(w60).multiply(wTotEmol);
				
				wGratuity = wTotLen.multiply(w2); 
			    wBeforeDiv = wGratuity.divide(w3, 2, RoundingMode.HALF_EVEN);
			    wBeforeDiv = wBeforeDiv.add(w60).multiply(wTotEmol);
			    wGratuity = wBeforeDiv.divide(w100,2,RoundingMode.HALF_EVEN);
			}
			if(pNewPensionerBean.getTotalLengthOfService() > 9 && pNewPensionerBean.isCalculatePensions()){
				if(consolidated){
					wPension = wTotEmol;
				}else{
					wPension = wTotLen.divide(w6,2,RoundingMode.HALF_EVEN).add(w10).multiply(wTotEmol).divide(w100, 2, RoundingMode.HALF_EVEN);
				}
				wMonthlyPension = new BigDecimal(Double.valueOf(wPension.doubleValue()/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
				
			}
		}
		if(pNewPensionerBean.isRecalculation()){
			if(pNewPensionerBean.isCalculateGratuity()){
				
				//We need to check if this guy has ever been paid gratuity...
				if(!pPensionService.isPensionerBeenPaidGratuity(pNewPensionerBean.getHiringInfo().getPensioner().getId(), businessCertificate )){
					    String wGratPaid = PayrollHRUtils.getDecimalFormat().format(wGratuity.doubleValue());
					  	pNewPensionerBean.getHiringInfo().setGratuityAmount(Double.parseDouble(PayrollHRUtils.removeCommas(wGratPaid)));
						pNewPensionerBean.getHiringInfo().setGratuityAmountStr(wGratPaid);
						 	
					 
				}else{
					double wOldGratuityAmount = pNewPensionerBean.getHiringInfo().getGratuityAmount();
					
					double wDifference = wGratuity.doubleValue() - wOldGratuityAmount;
					double wNewGratuityAmount = wDifference + wOldGratuityAmount;
					if(wNewGratuityAmount > 0){
						pNewPensionerBean.getHiringInfo().setGratuityAmount(wNewGratuityAmount + wOldGratuityAmount);
						pNewPensionerBean.getHiringInfo().setGratuityAmountStr(PayrollHRUtils.getDecimalFormat().format(wNewGratuityAmount + wOldGratuityAmount));
						 	
					} 
				}
				
			}
			if(pNewPensionerBean.isCalculatePensions()){
				if(!pPensionService.hasPensionerEverBeenPaid(pNewPensionerBean.getHiringInfo().getPensioner().getId(),businessCertificate)){
						pNewPensionerBean.getHiringInfo().setYearlyPensionAmount(wPension.doubleValue() );
						pNewPensionerBean.getHiringInfo().setYearlyPensionAmountStr(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(wPension.doubleValue()));
						
						wMonthlyPension = new BigDecimal(Double.valueOf(wPension.doubleValue()/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
						pNewPensionerBean.getHiringInfo().setMonthlyPensionAmount(wMonthlyPension.doubleValue());
						pNewPensionerBean.getHiringInfo().setMonthlyPensionAmountStr(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(wMonthlyPension.doubleValue()));
						
					
				}else{
					double wOldYearlyPensionAmount = pNewPensionerBean.getHiringInfo().getYearlyPensionAmount();
					double wDifference = wPension.doubleValue() - wOldYearlyPensionAmount;
					double wNewYearlyPensionAmount = wDifference + wOldYearlyPensionAmount;
					if(wNewYearlyPensionAmount > 0){
						pNewPensionerBean.getHiringInfo().setYearlyPensionAmount( pNewPensionerBean.getHiringInfo().getYearlyPensionAmount() + wNewYearlyPensionAmount);
						pNewPensionerBean.getHiringInfo().setYearlyPensionAmountStr(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(pNewPensionerBean.getHiringInfo().getYearlyPensionAmount()));
						
						wMonthlyPension = new BigDecimal(Double.valueOf(wNewYearlyPensionAmount/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
						pNewPensionerBean.getHiringInfo().setMonthlyPensionAmount(wMonthlyPension.doubleValue());
						pNewPensionerBean.getHiringInfo().setMonthlyPensionAmountStr(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(wMonthlyPension.doubleValue()));
						
					}
				}
				
			}
			
		}else{
			pNewPensionerBean.getHiringInfo().setYearlyPensionAmount(wPension.doubleValue());
			pNewPensionerBean.getHiringInfo().setYearlyPensionAmountStr(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(wPension.doubleValue()));
			pNewPensionerBean.getHiringInfo().setMonthlyPensionAmount(wMonthlyPension.doubleValue());
			pNewPensionerBean.getHiringInfo().setMonthlyPensionAmountStr(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(wMonthlyPension.doubleValue()));
			
			pNewPensionerBean.getHiringInfo().setGratuityAmount(wGratuity.doubleValue());
			pNewPensionerBean.getHiringInfo().setGratuityAmountStr(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(wGratuity.doubleValue()));
			pNewPensionerBean.setTotalEmolumentsStr(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(totalEmoluments));
			 
		}
		
		return pNewPensionerBean;
	}

	/**
	 * 
	 * @return instance of <code>HiringInfo</code>
	 */
	public static HiringInfo generateGatuityAndPension(
			HiringInfo pHiringInfo)
	{

	
		BigDecimal wGratuity = new BigDecimal(Double.valueOf("0.00")).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wPension = new BigDecimal(Double.valueOf("0.00")).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wMonthlyPension = new BigDecimal(Double.valueOf("0.00")).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w10 = new BigDecimal(10.0D).setScale(2, RoundingMode.HALF_EVEN);
		boolean consolidated = false;
		double totalEmoluments = pHiringInfo.getSalaryInfo().getTotalGrossPay();
		consolidated = pHiringInfo.getSalaryInfo().isPermanentSecretary();
	
		BigDecimal w3 = new BigDecimal(3.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w2 = new BigDecimal(2.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w20 = new BigDecimal(20.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w60 = new BigDecimal(60.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w6 = new BigDecimal(6.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w12 = new BigDecimal(12.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w100 = new BigDecimal(100.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wTotLen = new BigDecimal(pHiringInfo.getTotalLenOfServInMonths());
		BigDecimal wTotEmol = new BigDecimal(totalEmoluments).setScale(2, RoundingMode.HALF_EVEN);
		
		if(pHiringInfo.getLengthOfService() < 5){
			
		}else{


			BigDecimal divide = wTotLen.multiply(w2).divide(w3, 2, RoundingMode.HALF_EVEN);
			if(pHiringInfo.getLengthOfService() > 9){
				wGratuity = divide.add(w20).multiply(wTotEmol).divide(w100, 2, RoundingMode.HALF_EVEN);
				if(consolidated){
					wPension = wTotEmol;
					pHiringInfo.setConsolidatedStr(pHiringInfo.getSalaryInfo().getSalaryType().getName());
				}else{
					//wPension = wTotLen.multiply(w2).add(w10).divide(w100).multiply(wTotEmol);
					wPension = wTotLen.divide(w6, 2, RoundingMode.HALF_EVEN).add(w10).multiply(wTotEmol).divide(w100, 2, RoundingMode.HALF_EVEN);
				}
				
				wMonthlyPension = wPension.divide(w12, 2, RoundingMode.HALF_EVEN);
			}else{
				wGratuity = divide.add(w60).multiply(wTotEmol).divide(w100, 2,RoundingMode.HALF_EVEN);
			}
		}
		
		pHiringInfo.setYearlyPensionAmount(wPension.doubleValue());
		pHiringInfo.setYearlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(wPension.doubleValue()));
		pHiringInfo.setMonthlyPensionAmount(wMonthlyPension.doubleValue());
		pHiringInfo.setMonthlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(wMonthlyPension.doubleValue()));
		
		pHiringInfo.setGratuityAmount(wGratuity.doubleValue());
		pHiringInfo.setGratuityAmountStr(PayrollHRUtils.getDecimalFormat().format(wGratuity.doubleValue()));
		pHiringInfo.setTotalEmolumentsStr(PayrollHRUtils.getDecimalFormat().format(wTotEmol.doubleValue()));
	    pHiringInfo.setTotalEmoluments(wTotEmol.doubleValue());
		
		return pHiringInfo;
	}

	public static NewPensionerBean generateGatuityAndPensionByApportionment(
			NewPensionerBean pNewPensionerBean, PensionService pPensionService, BusinessCertificate businessCertificate)
	{

	
		BigDecimal wGratuity = new BigDecimal(Double.valueOf("0.00")).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wPension = new BigDecimal(Double.valueOf("0.00")).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wMonthlyPension = new BigDecimal(Double.valueOf("0.00")).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w10 = new BigDecimal(10.0D).setScale(2, RoundingMode.HALF_EVEN);
		double totalEmoluments = 0.0D;
		BigDecimal w3 = new BigDecimal(3.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w20 = new BigDecimal(20.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w60 = new BigDecimal(60.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w6 = new BigDecimal(6.0D).setScale(2, RoundingMode.HALF_EVEN);
		totalEmoluments = Double.parseDouble(PayrollHRUtils.removeCommas(pNewPensionerBean.getTotalPayStr()));
		 
		//BigDecimal w8 = new BigDecimal(8.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w2 = new BigDecimal(2.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal w100 = new BigDecimal(100.0D).setScale(2, RoundingMode.HALF_EVEN);
		
		//BigDecimal wGrat = new BigDecimal(0.0D).setScale(2, RoundingMode.HALF_EVEN);
		//BigDecimal wPen = new BigDecimal(0.0D).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wTotEmol = new BigDecimal(totalEmoluments).setScale(2, RoundingMode.HALF_EVEN);
		int wActualNoOfMonths =  getNumberOfMonths(pNewPensionerBean.getHiringInfo());
	    //BigDecimal wLOS = new BigDecimal(new Double(pNewPensionerBean.getTotalLengthOfService())/ new Double(wActualNoOfMonths)).setScale(3, RoundingMode.HALF_EVEN);
	    BigDecimal wTotLen = new BigDecimal(wActualNoOfMonths);
		if(pNewPensionerBean.getFinalLengthOfService() < 5){
			
		}else{
			 
			if(pNewPensionerBean.isCalculateGratuity() && pNewPensionerBean.getFinalLengthOfService() >= 10){
			    wGratuity = wTotLen.multiply(w2).divide(w3, 2, RoundingMode.HALF_EVEN).add(w20).multiply(wTotEmol).divide(w100, 2,RoundingMode.HALF_EVEN);
			    //wGrat = wGratuity.multiply(wLOS);
			}else if(pNewPensionerBean.isCalculateGratuity() && pNewPensionerBean.getFinalLengthOfService() < 10){
				wGratuity = wTotLen.multiply(w2).divide(w3, 2, RoundingMode.HALF_EVEN).add(w60).multiply(wTotEmol).divide(w100, 2,RoundingMode.HALF_EVEN);
			}
			if(pNewPensionerBean.getFinalLengthOfService() > 9 && pNewPensionerBean.isCalculatePensions()){
				
				wPension = wTotLen.divide(w6, 2, RoundingMode.HALF_EVEN).add(w10).multiply(wTotEmol).divide(w100, 2,RoundingMode.HALF_EVEN);
					//wPension = wTotLen.multiply(w2).add(w10).divide(w100).multiply(wTotEmol);
					//wPen = wPension.multiply(wLOS);
				
				
			}
		}
		if(pNewPensionerBean.isRecalculation()){
			if(pNewPensionerBean.isCalculateGratuity()){
				
				//We need to check if this guy has ever been paid gratuity...
				if(!pPensionService.isPensionerBeenPaidGratuity(pNewPensionerBean.getHiringInfo().getId(), businessCertificate)){
					 
					 
					 
						pNewPensionerBean.getHiringInfo().setGratuityAmount(wGratuity.doubleValue());
						pNewPensionerBean.getHiringInfo().setGratuityAmountStr(PayrollHRUtils.getDecimalFormat().format(wGratuity.doubleValue()));
						 	
					 
				}else{
					double wOldGratuityAmount = pNewPensionerBean.getHiringInfo().getGratuityAmount();
					double wDifference = wGratuity.doubleValue() - wOldGratuityAmount;
					double wNewGratuityAmount = wDifference + wOldGratuityAmount;
					if(wNewGratuityAmount > 0){
						pNewPensionerBean.getHiringInfo().setGratuityAmount(wNewGratuityAmount + wOldGratuityAmount);
						pNewPensionerBean.getHiringInfo().setGratuityAmountStr(PayrollHRUtils.getDecimalFormat().format(wNewGratuityAmount + wOldGratuityAmount));
						 	
					} 
				}
				
			}
			if(pNewPensionerBean.isCalculatePensions()){
				if(!pPensionService.hasPensionerEverBeenPaid(pNewPensionerBean.getHiringInfo().getId(), businessCertificate)){
						pNewPensionerBean.getHiringInfo().setYearlyPensionAmount(wPension.doubleValue() );
						pNewPensionerBean.getHiringInfo().setYearlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(wPension.doubleValue()));
						
						wMonthlyPension = new BigDecimal(Double.valueOf(wPension.doubleValue()/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
						pNewPensionerBean.getHiringInfo().setMonthlyPensionAmount(wMonthlyPension.doubleValue());
						pNewPensionerBean.getHiringInfo().setMonthlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(wMonthlyPension.doubleValue()));
						
					
				}else{
					double wOldYearlyPensionAmount = pNewPensionerBean.getHiringInfo().getYearlyPensionAmount();
					double wDifference = wPension.doubleValue() - wOldYearlyPensionAmount;
					double wNewYearlyPensionAmount = wOldYearlyPensionAmount + wDifference;
					if(wNewYearlyPensionAmount > 0){
						pNewPensionerBean.getHiringInfo().setYearlyPensionAmount( pNewPensionerBean.getHiringInfo().getYearlyPensionAmount() + wNewYearlyPensionAmount);
						pNewPensionerBean.getHiringInfo().setYearlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(pNewPensionerBean.getHiringInfo().getYearlyPensionAmount()));
						
						wMonthlyPension = new BigDecimal(Double.valueOf(wNewYearlyPensionAmount/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
						pNewPensionerBean.getHiringInfo().setMonthlyPensionAmount(wMonthlyPension.doubleValue());
						pNewPensionerBean.getHiringInfo().setMonthlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(pNewPensionerBean.getHiringInfo().getMonthlyPensionAmount()));
						
					}
				}
				
			}
			
		}else{
			pNewPensionerBean.getHiringInfo().setYearlyPensionAmount(wPension.doubleValue());
			pNewPensionerBean.getHiringInfo().setYearlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(wPension.doubleValue()));
			wMonthlyPension = new BigDecimal(Double.valueOf(wPension.doubleValue()/12.0D)).setScale(2, RoundingMode.HALF_EVEN);

			pNewPensionerBean.getHiringInfo().setMonthlyPensionAmount(wMonthlyPension.doubleValue());
			pNewPensionerBean.getHiringInfo().setMonthlyPensionAmountStr(PayrollHRUtils.getDecimalFormat().format(wMonthlyPension.doubleValue()));
			
			pNewPensionerBean.getHiringInfo().setGratuityAmount(wGratuity.doubleValue());
			pNewPensionerBean.getHiringInfo().setGratuityAmountStr(PayrollHRUtils.getDecimalFormat().format(wGratuity.doubleValue()));
			 
		}
		
		return pNewPensionerBean;
	}

	private static int getNumberOfMonths(HiringInfo pHiringInfo)
	{

		 int wNoOfMonths = (pHiringInfo.getTerminateDate().getYear() - pHiringInfo.getHireDate().getYear()) * 12;
		 if(pHiringInfo.getTerminateDate().getMonthValue() > pHiringInfo.getHireDate().getMonthValue()){
		   int wMonthDiff = (pHiringInfo.getTerminateDate().getMonthValue() - pHiringInfo.getHireDate().getMonthValue()) + 1;
		   wNoOfMonths += wMonthDiff;
		 }
		 
		return wNoOfMonths;
	}

}
