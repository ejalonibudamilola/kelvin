package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.SalaryDifferenceBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollPayUtils;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

 
public class CalculateFuturePayPerEmployee implements IBigDecimalValues
{
  private Map<Long, SalaryInfo> fSalaryInfoMap;
  
  //private Map<Long,Long> zeroLastPayEmployees;
  
  private Map<Long, SalaryDifferenceBean> salaryDifferenceBean;
  private Map<Long, SuspensionLog> partPaymentMap;
  private Map<Long, List<AbstractDeductionEntity>> fEmployeeDeductions;
  private Map<Long, List<AbstractGarnishmentEntity>> fEmployeeGarnishments;
  private Map<Long, List<AbstractSpecialAllowanceEntity>> fSpecialAllowances;
  private LocalDate sixtyYearsAgo;
  private LocalDate thirtyFiveYearsAgo;
  private int noOfDays;
   private LocalDate payPeriodEnd;
   private ConfigurationBean configurationBean;
   private BusinessCertificate businessCertificate;
  
  public void setSalaryInfoMap(Map<Long, SalaryInfo> pSalaryInfoMap)
  {
    this.fSalaryInfoMap = pSalaryInfoMap;
  }

  public FuturePaycheckBean calculatePayroll(HiringInfo pHiringInfo) 
    throws Exception
  {
	  FuturePaycheckBean pEmpPayBean;
	  pEmpPayBean = determinePayStatus(pHiringInfo, businessCertificate);

	    if (!pEmpPayBean.isDoNotPay())
	    {
	      if (pEmpPayBean.isPayByDays())
	      {
	        SalaryInfo wSS = this.fSalaryInfoMap.get(pEmpPayBean.getHiringInfo().getEmployee().getSalaryInfo().getId());

	        if (wSS == null) {
	          throw new Exception("Employee Salary Structure Undefined : " + pEmpPayBean.getHiringInfo().getEmployee().getEmployeeId());
	        }

	       
	        double payAmt = wSS.getMonthlyBasicSalary();
	        pEmpPayBean.setSalaryInfo(wSS);
	        
	        BigDecimal wNoOfDaysBD = new BigDecimal(  new Double(pEmpPayBean.getNoOfDays()) / new Double(this.getNoOfDays()) ).setScale(2, RoundingMode.HALF_EVEN);
	        
	        BigDecimal wPayAmtBD = new BigDecimal(Double.toString(payAmt/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
	       
	        wPayAmtBD = wPayAmtBD.multiply(wNoOfDaysBD);
	        payAmt = EntityUtils.convertDoubleToEpmStandard(wPayAmtBD.doubleValue());
	        
	       // pEmpPayBean.setMonthlyBasic(payAmt);

	        double nonTaxableSpecialAllowance = getNonTaxableSpecialAllowanceByDays(pEmpPayBean.getHiringInfo().getEmployee().getId(), payAmt, pEmpPayBean);

	        nonTaxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(nonTaxableSpecialAllowance);

	        double taxableSpecialAllowance = getTaxableSpecialAllowanceByDays(pEmpPayBean.getHiringInfo().getEmployee().getId(), payAmt, pEmpPayBean);

	        taxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(taxableSpecialAllowance);
	        
	        BigDecimal wCons = new BigDecimal(Double.toString(PayrollPayUtils.getPartPayment(wSS.getConsolidatedAllowance(), pEmpPayBean.getNoOfDays(), this.getNoOfDays(), true))).setScale(2, RoundingMode.HALF_EVEN);
	        
	   
	        BigDecimal wShiftAndCallDuty = null;
	        if(wSS.getCallDuty() > 0 || wSS.getShiftDuty() > 0){
	        	Double wShiftPlusCall = wSS.getCallDuty() + wSS.getShiftDuty();
	        	wShiftAndCallDuty = new BigDecimal(Double.toString(wShiftPlusCall) ).setScale(2,RoundingMode.HALF_EVEN);
	        	wShiftAndCallDuty.divide(wYearDivisorDB,2,RoundingMode.HALF_EVEN);
	        	wShiftAndCallDuty = wShiftAndCallDuty.multiply(wNoOfDaysBD);
	        }
	        double totalPay = (EntityUtils.convertDoubleToEpmStandard(wCons.doubleValue()) + payAmt + taxableSpecialAllowance + nonTaxableSpecialAllowance);
	        totalPay = EntityUtils.convertDoubleToEpmStandard(totalPay);
	        pEmpPayBean.setTotalPay(totalPay);

	        BigDecimal wGrossBD = new BigDecimal (wCons.doubleValue() + wPayAmtBD.doubleValue() + taxableSpecialAllowance).setScale(2, RoundingMode.HALF_EVEN);
	        
	        if(wShiftAndCallDuty != null){
	        	wGrossBD.subtract(wShiftAndCallDuty);
	        }
	        
	        wGrossBD = wGrossBD.multiply(wYearDivisorDB);
	        double wGrossIncome =  wGrossBD.doubleValue();
	         

	        double wAnnualRelief = EntityUtils.convertDoubleToEpmStandard(PayrollEngineHelper.getRelief(wGrossIncome));

	         
				/*
				 * double totalContAmount = 0.0D; if (pHiringInfo.isPensionableEmployee() &&
				 * !pEmpPayBean.isDoNotDeductContributoryPension() &&
				 * !pEmpPayBean.getHiringInfo().isContractStaff() &&
				 * !pHiringInfo.isPoliticalOfficeHolder()) { if
				 * (wSS.getPayGroupCode().startsWith("SP")) { BigDecimal wTotContAmt = new
				 * BigDecimal(Double.toString(wSS.getMonthlyBasicSalary() + wSS.getRent() +
				 * wSS.getMotorVehicle())); wTotContAmt = wTotContAmt.divide(wYearDivisorDB,2,
				 * RoundingMode.HALF_EVEN); wTotContAmt = wTotContAmt.multiply(w7Point5Percent);
				 * wTotContAmt = wTotContAmt.multiply(wNoOfDaysBD);
				 * 
				 * //First Convert this back to Floor.
				 * 
				 * 
				 * totalContAmount = new BigDecimal(wTotContAmt.doubleValue()).setScale(2,
				 * RoundingMode.FLOOR).doubleValue();//EntityUtils.convertDoubleToEpmStandard(
				 * totalContAmount / getNoOfDays() * pEmpPayBean.getNoOfDays());
				 * pEmpPayBean.setContributoryPension(totalContAmount);
				 * pEmpPayBean.setTotalDeductions(totalContAmount);
				 * 
				 * } else { BigDecimal wTotContAmt = new BigDecimal(wSS.getMonthlyBasicSalary()
				 * + wSS.getRent() + wSS.getTransport()); wTotContAmt =
				 * wTotContAmt.divide(wYearDivisorDB,2, RoundingMode.HALF_EVEN); wTotContAmt =
				 * wTotContAmt.multiply(w7Point5Percent); wTotContAmt =
				 * wTotContAmt.multiply(wNoOfDaysBD); totalContAmount = new
				 * BigDecimal(wTotContAmt.doubleValue()).setScale(2,
				 * RoundingMode.FLOOR).doubleValue();//EntityUtils.convertDoubleToEpmStandard(
				 * totalContAmount / getNoOfDays() * pEmpPayBean.getNoOfDays());
				 * 
				 * //totalContAmount =
				 * wTotContAmt.doubleValue();//EntityUtils.convertDoubleToEpmStandard(
				 * totalContAmount / getNoOfDays() * pEmpPayBean.getNoOfDays());
				 * 
				 * 
				 * pEmpPayBean.setContributoryPension(totalContAmount);
				 * pEmpPayBean.setTotalDeductions(totalContAmount);
				 * 
				 * }
				 * 
				 * }
				 */

	        pEmpPayBean = removeEmployeeNonTaxableDeductionsByDays(pEmpPayBean, wPayAmtBD);

	        double freePay = EntityUtils.convertDoubleToEpmStandard(wAnnualRelief/12.0D) + pEmpPayBean.getTotalDeductions();

 	        double taxesDue = EntityUtils.convertDoubleToEpmStandard(PayrollEngineHelper.calculateTaxes(wGrossIncome, freePay) /*/ getNoOfDays() * pEmpPayBean.getNoOfDays()*/);

	        pEmpPayBean.setTaxesPaid(taxesDue);
	       

	        double grossPay = totalPay - taxesDue;

	        /*if (this.addLifeInsuranceRelief) {
	          grossPay -= 50000.0D;
	        }
	*/
	        pEmpPayBean.setNetPay(grossPay);
	        
	        //BigDecimal wMonthlySalaryBD = new BigDecimal(wSS.getMonthlyBasicSalary() / 12.0D).setScale(2, RoundingMode.HALF_EVEN);

	        pEmpPayBean = removeEmployeeDeductionsByDays(pEmpPayBean, grossPay,wPayAmtBD.doubleValue() );

	        if(pEmpPayBean.getNoOfDays() > 9) //-- Do not remove Garnishments for folks less than 10 days...
	        	pEmpPayBean = removeGarnishments(pEmpPayBean, wPayAmtBD.doubleValue(),false);
	        else{
	        	List<AbstractGarnishmentEntity> pEmpGarnList = this.fEmployeeGarnishments.get(pEmpPayBean.getHiringInfo().getEmployee().getId());
	        	double wTotalLoans = 0.0D;
	        	if(pEmpGarnList != null && !pEmpGarnList.isEmpty()){
		        	for(AbstractGarnishmentEntity e : pEmpGarnList){
		        		if(e.getOwedAmount() > 0.00D)
		        			wTotalLoans += e.getAmount();
		        	}
		        	if(pEmpPayBean.getNetPay() > wTotalLoans)
		        		pEmpPayBean = removeGarnishments(pEmpPayBean, wPayAmtBD.doubleValue(),false);	
		        	else
		        		pEmpPayBean = removeGarnishments(pEmpPayBean, wPayAmtBD.doubleValue(),true);
	        	}
	        }
	        BigDecimal wTA = new BigDecimal(Double.toString(pEmpPayBean.getTotalAllowance())).setScale(2, RoundingMode.FLOOR);
	        pEmpPayBean.setTotalAllowance(wTA.doubleValue());
	        
	        double wTotalSalaryStructureAllowances = PayrollPayUtils.getPartPayment(wSS.getConsolidatedAllowance(), pEmpPayBean.getNoOfDays(), getNoOfDays(), true);
	        pEmpPayBean.setTotalAllowance(pEmpPayBean.getTotalAllowance()+wTotalSalaryStructureAllowances); 
	        pEmpPayBean.setTotalDeductions(pEmpPayBean.getTotalDeductions() + pEmpPayBean.getTotalGarnishments() + pEmpPayBean.getTaxesPaid());
	        pEmpPayBean.setNetPay(pEmpPayBean.getTotalPay() - pEmpPayBean.getTotalDeductions());

	       
	          pEmpPayBean = setMapAndObjectId(pEmpPayBean);
	       }
	      else
	      {
	    	  //Normal Salary Payment
	        SalaryInfo wSS = this.fSalaryInfoMap.get(pEmpPayBean.getHiringInfo().getEmployee().getSalaryInfo().getId());

	        if (wSS == null) {
	          throw new Exception("Employee Salary Structure Undefined : " + pEmpPayBean.getHiringInfo().getEmployee().getEmployeeId());
	        }

	        
	        double actualPay = wSS.getMonthlyBasicSalary();
	        if (pEmpPayBean.isPercentagePayment()) {
	          actualPay *= pEmpPayBean.getPayPercentage();
	        }
	        pEmpPayBean.setSalaryInfo(wSS);
	        double payAmt = EntityUtils.convertDoubleToEpmStandard(actualPay);
	        
	       

	        double nonTaxableSpecialAllowance = getNonTaxableSpecialAllowance(pEmpPayBean.getHiringInfo().getEmployee().getId(), wSS.getMonthlyBasicSalary(), pEmpPayBean);

	        nonTaxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(nonTaxableSpecialAllowance);

	        double taxableSpecialAllowance = getTaxableSpecialAllowance(pEmpPayBean.getHiringInfo().getEmployee().getId(), wSS.getMonthlyBasicSalary(), pEmpPayBean);

	        taxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(taxableSpecialAllowance);
	        double totalPay = 0.0D;
	        if (pEmpPayBean.isPercentagePayment()) {
	        	BigDecimal wCons = new BigDecimal(wSS.getConsolidatedAllowance() * pEmpPayBean.getPayPercentage() + payAmt).setScale(2, RoundingMode.HALF_EVEN);
	            wCons = wCons.divide(wYearDivisorDB,2, RoundingMode.HALF_EVEN);
	            
	          totalPay = EntityUtils.convertDoubleToEpmStandard(wCons.doubleValue() + (taxableSpecialAllowance + nonTaxableSpecialAllowance));
	        }
	        else {
	        	BigDecimal wCons = new BigDecimal(Double.toString((wSS.getConsolidatedMonthlyAllowance()) + EntityUtils.convertDoubleToEpmStandard(payAmt/12.0D))).setScale(2, RoundingMode.HALF_EVEN);
	            totalPay = EntityUtils.convertDoubleToEpmStandard(wCons.doubleValue() + (taxableSpecialAllowance + nonTaxableSpecialAllowance));
	        }
	        pEmpPayBean.setTotalPay(EntityUtils.convertDoubleToEpmStandard(totalPay));
	        BigDecimal wPayPercentBD = new BigDecimal(Double.toString(pEmpPayBean.getPayPercentage())).setScale(2, RoundingMode.HALF_EVEN);
	        double wGrossIncome = 0.0D;
	        BigDecimal wShiftAndCallDuty = null;
	        if(wSS.getCallDuty() > 0 || wSS.getShiftDuty() > 0){
	        	Double wShiftPlusCall = wSS.getCallDuty() + wSS.getShiftDuty();
	        	wShiftAndCallDuty = new BigDecimal(Double.toString(wShiftPlusCall) ).setScale(2,RoundingMode.HALF_EVEN);
	        	
	        }
	        if (pEmpPayBean.isPercentagePayment()) {
	   		   wGrossIncome = EntityUtils.convertDoubleToEpmStandard(wSS.getConsolidatedAllowance() * pEmpPayBean.getPayPercentage() + payAmt + (taxableSpecialAllowance * 12.0D));

	        	if(wShiftAndCallDuty != null){
	        		wShiftAndCallDuty = wShiftAndCallDuty.multiply(wPayPercentBD);
	        		 wGrossIncome -= EntityUtils.convertDoubleToEpmStandard(wShiftAndCallDuty.doubleValue()); 
	        	}  	 
	        }
	        else {
	        	
	          wGrossIncome = EntityUtils.convertDoubleToEpmStandard(wSS.getConsolidatedAllowance() + payAmt + (taxableSpecialAllowance * 12.0D));
	          if(wShiftAndCallDuty != null)
	        	  wGrossIncome -= EntityUtils.convertDoubleToEpmStandard(wShiftAndCallDuty.doubleValue()); 
	        }
	        double wAnnualRelief = PayrollEngineHelper.getRelief(wGrossIncome);

	       
	        double totalContAmount = 0.0D;
				/*
				 * if (pHiringInfo.isPensionableEmployee() &&
				 * !pEmpPayBean.isDoNotDeductContributoryPension() &&
				 * !pHiringInfo.isContractStaff() && !pHiringInfo.isPoliticalOfficeHolder()) {
				 * if (wSS.getPayGroupCode().startsWith("SP")) { BigDecimal wTotContAmt = new
				 * BigDecimal(wSS.getMonthlyBasicSalary() + wSS.getRent() +
				 * wSS.getMotorVehicle()); wTotContAmt = wTotContAmt.divide(wYearDivisorDB,2,
				 * RoundingMode.HALF_EVEN); wTotContAmt = wTotContAmt.multiply(w7Point5Percent);
				 * 
				 * 
				 * if (pEmpPayBean.isPercentagePayment()) { BigDecimal wPercentagePayment = new
				 * BigDecimal(pEmpPayBean.getPayPercentage()).setScale(2,
				 * RoundingMode.HALF_EVEN); wTotContAmt =
				 * wTotContAmt.multiply(wPercentagePayment); } totalContAmount = new
				 * BigDecimal(wTotContAmt.doubleValue()).setScale(2,
				 * RoundingMode.FLOOR).doubleValue(); //totalContAmount =
				 * wTotContAmt.doubleValue();
				 * pEmpPayBean.setContributoryPension(totalContAmount);
				 * pEmpPayBean.setTotalDeductions(totalContAmount);
				 * 
				 * } else { BigDecimal wTotContAmt = new BigDecimal(wSS.getMonthlyBasicSalary()
				 * + wSS.getRent() + wSS.getTransport()); wTotContAmt =
				 * wTotContAmt.divide(wYearDivisorDB,2, RoundingMode.HALF_EVEN); wTotContAmt =
				 * wTotContAmt.multiply(w7Point5Percent);
				 * 
				 * 
				 * if (pEmpPayBean.isPercentagePayment()) { BigDecimal wPercentagePayment = new
				 * BigDecimal(pEmpPayBean.getPayPercentage()).setScale(2,
				 * RoundingMode.HALF_EVEN); wTotContAmt =
				 * wTotContAmt.multiply(wPercentagePayment); } totalContAmount = new
				 * BigDecimal(wTotContAmt.doubleValue()).setScale(2,
				 * RoundingMode.FLOOR).doubleValue(); // totalContAmount =
				 * wTotContAmt.doubleValue();
				 * pEmpPayBean.setContributoryPension(totalContAmount);
				 * pEmpPayBean.setTotalDeductions(totalContAmount);
				 * 
				 * }
				 * 
				 * }
				 */
	        pEmpPayBean = removeEmployeeNonTaxableDeductions(pEmpPayBean, wSS);

	         

 
	        double freePay = EntityUtils.convertDoubleToEpmStandard(wAnnualRelief/12.0D) + pEmpPayBean.getTotalDeductions();

	        double taxesDue = EntityUtils.convertDoubleToEpmStandard(PayrollEngineHelper.calculateTaxes(wGrossIncome, freePay));

	        pEmpPayBean.setTaxesPaid(taxesDue);
	       

	        double grossPay = totalPay - taxesDue;

	      /*  if (this.addLifeInsuranceRelief) {
	          grossPay -= 50000.0D;
	        }*/

	        pEmpPayBean.setNetPay(grossPay);
	        
	        BigDecimal wMonthlyBasic = new BigDecimal(wSS.getMonthlyBasicSalary()/12.0).setScale(2,RoundingMode.HALF_EVEN);
	        
	 
	        pEmpPayBean = removeEmployeeDeductions(pEmpPayBean, grossPay, wMonthlyBasic.doubleValue());

	        pEmpPayBean = removeGarnishments(pEmpPayBean, wMonthlyBasic.doubleValue(),false);

	        pEmpPayBean.setNetPay(pEmpPayBean.getNetPay() - (totalContAmount + pEmpPayBean.getTotalNonTaxableDeductions()));

	        double multiplyFactor = 1.0D;

	        if (pEmpPayBean.isPercentagePayment()) {
	          multiplyFactor = pEmpPayBean.getPayPercentage();
	        }
	        pEmpPayBean.setTotalAllowance(pEmpPayBean.getTotalAllowance() + EntityUtils.convertDoubleToEpmStandard((wSS.getConsolidatedAllowance()/ 12.0D) * multiplyFactor ));
	        
	        
             

	         pEmpPayBean = setMapAndObjectId(pEmpPayBean);
	        pEmpPayBean.setTotalDeductions(pEmpPayBean.getTotalDeductions() + pEmpPayBean.getTotalGarnishments() + pEmpPayBean.getTaxesPaid());

	        pEmpPayBean.setNetPay(pEmpPayBean.getTotalPay() - pEmpPayBean.getTotalDeductions());
	      }
	    }
	    else
	    {
	      SalaryInfo wSS = this.fSalaryInfoMap.get(pEmpPayBean.getHiringInfo().getEmployee().getSalaryInfo().getId());
	      pEmpPayBean.setTotalPay(0.0D);

	       
	      pEmpPayBean.setNhf(0.0D);
	      
	      pEmpPayBean.setUnionDues(0.0D);
	     

	      pEmpPayBean.setTaxesPaid(0.0D);
	       
	      pEmpPayBean.setContributoryPension(0.0D);

	      pEmpPayBean.setNetPay(0.0D);
	      pEmpPayBean.setSalaryInfo(wSS);
	      pEmpPayBean = setMapAndObjectId(pEmpPayBean);
	    }
 
	    if(pEmpPayBean.getNetPay() < 0)
	    	pEmpPayBean.setNegativePayInd(IConstants.ON);
	    return pEmpPayBean;
  }
  private FuturePaycheckBean setMapAndObjectId(FuturePaycheckBean pEmpPayBean) {
	  pEmpPayBean.setMdaInfo(pEmpPayBean.getHiringInfo().getEmployee().getMdaDeptMap().getMdaInfo());
	  
	    return pEmpPayBean;
}

private FuturePaycheckBean removeEmployeeDeductions(FuturePaycheckBean pE, double pCurrentNetPay,
		double pBasicSalary) {
	    BigDecimal wRetVal = new BigDecimal(pCurrentNetPay).setScale(2, RoundingMode.HALF_EVEN);
		  BigDecimal wMonthlyBasicBD = new BigDecimal( Double.toString(pBasicSalary) ).setScale(2, RoundingMode.HALF_EVEN);
		  BigDecimal wPayPercentage = new BigDecimal(pE.getPayPercentage()).setScale(2, RoundingMode.HALF_EVEN);
		  BigDecimal wTotalDeductionsBD = new BigDecimal( Double.toString(pE.getTotalDeductions()) ).setScale(2, RoundingMode.HALF_EVEN);
		  BigDecimal wTotalCurrDedBD = new BigDecimal(pE.getTotCurrDed()).setScale(2, RoundingMode.HALF_EVEN);
		
		 // double retVal = pCurrentNetPay;
	    double rate = 0.0D;

	   /* if (pCurrentNetPay < 1.0D) {
	      pE.setNetPay(pCurrentNetPay);
	      return pE;
	    }*/

	    List<AbstractDeductionEntity> pEmpDedList = this.fEmployeeDeductions.get(pE.getHiringInfo().getEmployee().getId());
	    if ((pEmpDedList == null) || (pEmpDedList.isEmpty())) {
	      pE.setNetPay(pCurrentNetPay);
	      return pE;
	    }
	    
	    for (AbstractDeductionEntity empDed : pEmpDedList) {
	      if (empDed.isTaxExempt())
	        continue;
	      if (empDed.getAmount() > 0.0D) {
	    	  
	        BigDecimal deductionAmount = null;
	        BigDecimal wWorkingDeductionAmount = new BigDecimal("0.00").setScale(4, RoundingMode.HALF_EVEN);
	        if (empDed.getPayTypes().isUsingPercentage())
	        {
	          rate = empDed.getAmount();
	          if ((rate >= 25.0D) || (rate < 0.0D))
	          {
	            continue;
	          }

	          BigDecimal wRateAmt = new BigDecimal( Double.toString(rate) );
	          wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.FLOOR);
	          //rate /= 100.0D;

	          wWorkingDeductionAmount = wMonthlyBasicBD.multiply(wRateAmt);
	          if (pE.isPercentagePayment()) {
	        	  wWorkingDeductionAmount = wWorkingDeductionAmount.multiply(wPayPercentage);
	            }
	         
	          deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);
	         /* if (wRetVal.subtract(deductionAmount).doubleValue() <= 0.0D)
	            continue;*/
	          
	          wRetVal =  wRetVal.subtract(deductionAmount);
	          
	        }
	        else
	        {
	        	wWorkingDeductionAmount = new BigDecimal(empDed.getAmount()).setScale(2, RoundingMode.HALF_EVEN);
	           
	          if (pE.isPercentagePayment()) {
	        	  wWorkingDeductionAmount = wWorkingDeductionAmount.multiply(wPayPercentage);
	            }
	          deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);

	         /* if (wRetVal.subtract(deductionAmount).doubleValue() <= 0.0D)
	            continue;*/
	          wRetVal =  wRetVal.subtract(deductionAmount);
	        }

	       
	        
	        empDed.setAmount(deductionAmount.doubleValue());
	        
	        wTotalCurrDedBD = wTotalCurrDedBD.add(deductionAmount);
	        wTotalDeductionsBD = wTotalDeductionsBD.add(deductionAmount);
	        
	      }
	    }
	    pE.setTotalDeductions(wTotalDeductionsBD.doubleValue());
	    pE.setTotCurrDed(wTotalCurrDedBD.doubleValue());
 	    pE.setNetPay(wRetVal.doubleValue());
	    return pE;
	  }

private FuturePaycheckBean removeGarnishments(FuturePaycheckBean pE, double doubleValue, boolean pAdj4NegPay) {
	    double retVal = pE.getNetPay();
	    double garnishAmt = 0.0D;

	    List<AbstractGarnishmentEntity> pEmpGarnList = this.fEmployeeGarnishments.get(pE.getHiringInfo().getEmployee().getId());
	    if ((pEmpGarnList == null) || (pEmpGarnList.isEmpty())) {
	      pE.setNetPay(retVal);
	      return pE;
	    }
	    Collections.sort(pEmpGarnList);
	    
	    for (AbstractGarnishmentEntity e : pEmpGarnList) {
	      if (e.getOwedAmount() > 0.00D)
	      {
	        garnishAmt = EntityUtils.convertDoubleToEpmStandard(e.getAmount());

	       
	        if (garnishAmt < e.getOwedAmount()) {
	         
	          if(pAdj4NegPay){
		          if(garnishAmt >= retVal){
		        	  continue;
		          }
	          }
	          retVal -= garnishAmt;
	          
	          pE.setTotalGarnishments(pE.getTotalGarnishments() + garnishAmt);
	          e.setOldOwedAmount(e.getOwedAmount());
	          e.setCurrentOwedAmount(e.getOwedAmount() - garnishAmt);
	        } else if ((garnishAmt >= e.getOwedAmount()) && (e.getOwedAmount() > 0.0D)) {
	          garnishAmt = EntityUtils.convertDoubleToEpmStandard(e.getOwedAmount());
	          if(pAdj4NegPay){
		          if(garnishAmt >= retVal){
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
	        
	        
	      }
	    }
	    pE.setNetPay(retVal);
	    return pE;
	  }

private FuturePaycheckBean removeEmployeeDeductionsByDays(FuturePaycheckBean pE, double pCurrentNetPay,
		double pBasicSalary) {
	  BigDecimal wRetVal = new BigDecimal(pCurrentNetPay).setScale(2, RoundingMode.HALF_EVEN);
	  BigDecimal wMonthlyBasicBD = new BigDecimal( Double.toString(pBasicSalary) ).setScale(2, RoundingMode.HALF_EVEN);
	  
	  BigDecimal wTotalDeductionsBD = new BigDecimal( Double.toString(pE.getTotalDeductions()) ).setScale(2, RoundingMode.HALF_EVEN);
	  BigDecimal wTotalCurrDedBD = new BigDecimal(pE.getTotCurrDed()).setScale(2, RoundingMode.HALF_EVEN);
	  BigDecimal wNofDaysBD = new BigDecimal( Double.toString( ( new Double(pE.getNoOfDays()) / new Double(this.getNoOfDays())) ) );

	 // double retVal = pCurrentNetPay;
    double rate = 0.0D;

   /* if (pCurrentNetPay < 1.0D) {
      pE.setNetPay(pCurrentNetPay);
      return pE;
    }*/

    List<AbstractDeductionEntity> pEmpDedList = this.fEmployeeDeductions.get(pE.getHiringInfo().getEmployee().getId());
    if ((pEmpDedList == null) || (pEmpDedList.isEmpty())) {
      pE.setNetPay(pCurrentNetPay);
      return pE;
    }
    for (AbstractDeductionEntity empDed : pEmpDedList) {
      if (empDed.isTaxExempt())
        continue;
      if (empDed.getAmount() > 0.0D) {
    	  BigDecimal deductionAmount = null;
          BigDecimal wWorkingDeductionAmount = new BigDecimal("0.00").setScale(4, RoundingMode.HALF_EVEN);
        if (empDed.getPayTypes().isUsingPercentage())
        {
          rate = empDed.getAmount();
          if ((rate >= 25.0D) || (rate < 0.0D))
          {
            continue;
          }

          BigDecimal wRateAmt = new BigDecimal( Double.toString(rate) );
          wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);

         // deductionAmount = convertDoubleToEpmStandard(pBasicSalary * rate / getNoOfDays() * pE.getNoOfDays());
          wWorkingDeductionAmount = wMonthlyBasicBD.multiply(wRateAmt); 
         
         // wWorkingDeductionAmount = wWorkingDeductionAmount.multiply( wNofDaysBD ).setScale(2, RoundingMode.HALF_EVEN);
          deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);
          /*if (wRetVal.subtract(deductionAmount).doubleValue() <= 0.0D)
            continue;*/
          
         

          wRetVal = wRetVal.subtract(deductionAmount);
          empDed.setAmount(deductionAmount.doubleValue());
        }
        else
        {
        	wWorkingDeductionAmount = new BigDecimal(empDed.getAmount()).setScale(2, RoundingMode.HALF_EVEN);
            
        	wWorkingDeductionAmount = wWorkingDeductionAmount.multiply( wNofDaysBD ).setScale(2, RoundingMode.HALF_EVEN);
            deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);

         /* if (wRetVal.subtract(deductionAmount).doubleValue() <= 0.0D)
            continue;*/

          wRetVal = wRetVal.subtract(deductionAmount);
        }

         
        empDed.setAmount(deductionAmount.doubleValue());
     
        wTotalCurrDedBD = wTotalCurrDedBD.add(deductionAmount);
        wTotalDeductionsBD = wTotalDeductionsBD.add(deductionAmount);
      }
    }
    pE.setTotalDeductions(wTotalDeductionsBD.doubleValue());
    pE.setTotCurrDed(wTotalCurrDedBD.doubleValue());
    
    pE.setNetPay(wRetVal.doubleValue());
    return pE;
  }

private double getTaxableSpecialAllowanceByDays(Long pId, double pMonthlyBasic, FuturePaycheckBean pEmpPayBean)
  {
	//double retVal = 0.0D;
	  BigDecimal retVal = new BigDecimal( "0.00" ).setScale(2, RoundingMode.HALF_EVEN);
	  BigDecimal wMonthlyBasicBD = new BigDecimal( Double.toString(pMonthlyBasic) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wTotalContribBD = new BigDecimal( Double.toString(pEmpPayBean.getTotalAllowance()) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wSpecialAllowBD = new BigDecimal( Double.toString(pEmpPayBean.getSpecialAllowance()) ).setScale(2, RoundingMode.HALF_EVEN);
	  BigDecimal wNofDaysBD = new BigDecimal( Double.toString( ( new Double(pEmpPayBean.getNoOfDays()) / new Double(this.getNoOfDays())) ) ).setScale(2, RoundingMode.HALF_EVEN);

      double rate = 0.0D;
    
    List<AbstractSpecialAllowanceEntity> pEmpAllowList = this.fSpecialAllowances.get(pId);
    if ((pEmpAllowList == null) || (pEmpAllowList.isEmpty()))
      return retVal.doubleValue();
    
    for (AbstractSpecialAllowanceEntity empDed : pEmpAllowList) {
      if (!empDed.getSpecialAllowanceType().isTaxable())
        continue;
      
      BigDecimal wWorkingAllowanceAmt = new BigDecimal( "0.00" ).setScale(4, RoundingMode.HALF_EVEN);
      BigDecimal wAllowanceAmt = null;
      if (empDed.getPayTypes().isUsingPercentage())
      {
        rate = empDed.getAmount();
        if ((rate >= 100.0D) || (rate < 0.0D))
        {
          continue;
        }
        BigDecimal wRateAmt = new BigDecimal( Double.toString(rate) );
        wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);
        //rate /= 100.0D;

        //allowanceAmount = convertDoubleToEpmStandard(pMonthlyBasic * rate / 12.0D / getNoOfDays() * pEmpPayBean.getNoOfDays());

        //retVal += allowanceAmount;
        wWorkingAllowanceAmt = wMonthlyBasicBD.multiply(wRateAmt); 
        wAllowanceAmt = wWorkingAllowanceAmt;
        
        /*if(empDed.getName().equalsIgnoreCase("SALAR")){
        	 wAllowanceAmt = new BigDecimal(String.valueOf(wWorkingAllowanceAmt.doubleValue())).setScale(2,RoundingMode.FLOOR);
        }else{
            wWorkingAllowanceAmt = wWorkingAllowanceAmt.multiply( wNofDaysBD ).setScale(2, RoundingMode.HALF_EVEN);
       	    wAllowanceAmt = new BigDecimal(String.valueOf(wWorkingAllowanceAmt.doubleValue())).setScale(2,RoundingMode.FLOOR);

        }*/
        empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
        

      }
      else
      {
    	if(empDed.getName().equalsIgnoreCase("SALAR")){
    		//Use WHOLE Value...
			wAllowanceAmt = new BigDecimal( Double.toString( empDed.getAmount() ) ).setScale(2, RoundingMode.HALF_EVEN);
			//retVal = retVal.add(wAllowanceAmt);
            empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
    	}else{
    		wAllowanceAmt = new BigDecimal( Double.toString( empDed.getAmount() ) ).setScale(2, RoundingMode.HALF_EVEN);
            wAllowanceAmt = wAllowanceAmt.multiply( wNofDaysBD ).setScale(2, RoundingMode.HALF_EVEN);

            ///retVal = retVal.add(wAllowanceAmt);
            empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
    	}
        
      }

    
      wTotalContribBD = wTotalContribBD.add( wAllowanceAmt );
      wSpecialAllowBD = wSpecialAllowBD.add( wAllowanceAmt );
      retVal = retVal.add(wAllowanceAmt);
    }
   
    pEmpPayBean.setTotalAllowance( wTotalContribBD.doubleValue() );
	pEmpPayBean.setSpecialAllowance( wSpecialAllowBD.doubleValue() );
    return retVal.doubleValue();
  }

  private double getNonTaxableSpecialAllowance(Long pId, double pMonthlyBasic, FuturePaycheckBean pEmpPayBean)
  {
	  BigDecimal retVal = new BigDecimal( "0.00" ).setScale(2, RoundingMode.HALF_EVEN);
	  BigDecimal wMonthlyBasicBD = new BigDecimal( Double.toString(pMonthlyBasic) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wTotalContribBD = new BigDecimal( Double.toString(pEmpPayBean.getTotalAllowance()) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wSpecialAllowBD = new BigDecimal( Double.toString(pEmpPayBean.getSpecialAllowance()) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wYearDivisor = new BigDecimal( Double.toString(12.00) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wPayPercentage = new BigDecimal( Double.toString(pEmpPayBean.getPayPercentage()) ).setScale(2, RoundingMode.HALF_EVEN);
    double rate = 0.0D;

    List<AbstractSpecialAllowanceEntity> pEmpAllowList = this.fSpecialAllowances.get(pId);
    if ((pEmpAllowList == null) || (pEmpAllowList.isEmpty()))
      return retVal.doubleValue();
    
    for (AbstractSpecialAllowanceEntity empDed : pEmpAllowList) {
      if (empDed.getSpecialAllowanceType().isTaxable())
        continue;
      BigDecimal wAllowanceAmt = null;
      BigDecimal wWorkingAllowanceAmt = new BigDecimal( "0.00" ).setScale(4, RoundingMode.HALF_EVEN);
      if (empDed.getPayTypes().isUsingPercentage())
      {
        rate = empDed.getAmount();
        if ((rate >= 100.0D) || (rate < 0.0D))
        {
          continue;
        }
        BigDecimal wRateAmt = new BigDecimal( Double.toString(rate) ).setScale(2, RoundingMode.HALF_EVEN);
          wRateAmt = wRateAmt.divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN);
        //rate /= 100.0D;
        wWorkingAllowanceAmt = wMonthlyBasicBD.multiply(wRateAmt); 
        wWorkingAllowanceAmt = wWorkingAllowanceAmt.divide(wYearDivisor,2, RoundingMode.HALF_EVEN);
        //allowanceAmount = pMonthlyBasic * rate / 12.0D;
        if (pEmpPayBean.isPercentagePayment()) {
        	wWorkingAllowanceAmt = wWorkingAllowanceAmt.multiply(wPayPercentage);
        }
        wAllowanceAmt = new BigDecimal(String.valueOf(wWorkingAllowanceAmt.doubleValue())).setScale(2,RoundingMode.FLOOR);
        //retVal = retVal.add(wAllowanceAmt);
        

        empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
      }
      else
      {
    	  wWorkingAllowanceAmt = new BigDecimal( empDed.getAmount() ).setScale(2, RoundingMode.HALF_EVEN);
         
        if (pEmpPayBean.isPercentagePayment()) {
        	wWorkingAllowanceAmt = wWorkingAllowanceAmt.multiply(wPayPercentage);
        }
        wAllowanceAmt = new BigDecimal(String.valueOf(wWorkingAllowanceAmt.doubleValue())).setScale(2,RoundingMode.FLOOR);
       // retVal = retVal.add(wAllowanceAmt);

        empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
      }

      
      wTotalContribBD = wTotalContribBD.add(wAllowanceAmt);
      wSpecialAllowBD = wSpecialAllowBD.add(wAllowanceAmt);
      retVal = retVal.add(wAllowanceAmt);
    }
      pEmpPayBean.setTotalAllowance(wTotalContribBD.doubleValue());
      pEmpPayBean.setSpecialAllowance(wSpecialAllowBD.doubleValue());
   
      return retVal.doubleValue();
  }
  private double getTaxableSpecialAllowance(Long pId, double pMonthlyBasic, FuturePaycheckBean pEmpPayBean)
  {
	  BigDecimal retVal = new BigDecimal( "0.00" ).setScale(2, RoundingMode.HALF_EVEN);
	  BigDecimal wMonthlyBasicBD = new BigDecimal( Double.toString(pMonthlyBasic) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wTotalContribBD = new BigDecimal( Double.toString(pEmpPayBean.getTotalAllowance()) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wSpecialAllowBD = new BigDecimal( Double.toString(pEmpPayBean.getSpecialAllowance()) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wYearDivisor = new BigDecimal( Double.toString(12.00) ).setScale(2, RoundingMode.HALF_EVEN);
  	  BigDecimal wPayPercentage = new BigDecimal( Double.toString(pEmpPayBean.getPayPercentage()) ).setScale(2, RoundingMode.HALF_EVEN);

    double rate = 0.0D;


    List<AbstractSpecialAllowanceEntity> pEmpAllowList = this.fSpecialAllowances.get(pId);
    if ((pEmpAllowList == null) || (pEmpAllowList.isEmpty()))
      return retVal.doubleValue();
    for (AbstractSpecialAllowanceEntity empDed : pEmpAllowList) {
      if (!empDed.getSpecialAllowanceType().isTaxable())
        continue;
      BigDecimal wAllowanceAmt = null;
      BigDecimal wWorkingAllowanceAmt = new BigDecimal( "0.00" ).setScale(4, RoundingMode.HALF_EVEN);
      if (empDed.getPayTypes().isUsingPercentage())
      {
        rate = empDed.getAmount();
        if ((rate >= 100.0D) || (rate < 0.0D))
        {
          continue;
        }

        BigDecimal wRateAmt = new BigDecimal( Double.toString(rate) );
        wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);
        //rate /= 100.0D;
        wWorkingAllowanceAmt = wMonthlyBasicBD.multiply(wRateAmt); 
        wWorkingAllowanceAmt = wWorkingAllowanceAmt.divide(wYearDivisor,2, RoundingMode.HALF_EVEN);
        //allowanceAmount = pMonthlyBasic * rate / 12.0D;
        if (pEmpPayBean.isPercentagePayment()) {
        	wWorkingAllowanceAmt = wWorkingAllowanceAmt.multiply(wPayPercentage);
        }
        wAllowanceAmt = new BigDecimal(String.valueOf(wWorkingAllowanceAmt.doubleValue())).setScale(2,RoundingMode.FLOOR);

         

        empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
      }
      else
      {
    	  wAllowanceAmt = new BigDecimal( empDed.getAmount() ).setScale(2, RoundingMode.HALF_EVEN);
          
          if (pEmpPayBean.isPercentagePayment()) {
          	wAllowanceAmt = wAllowanceAmt.multiply(wPayPercentage);
          }
         

          empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
        }

     
      wTotalContribBD = wTotalContribBD.add(wAllowanceAmt);
      wSpecialAllowBD = wSpecialAllowBD.add(wAllowanceAmt);
      retVal = retVal.add(wAllowanceAmt);
    }
    pEmpPayBean.setTotalAllowance(wTotalContribBD.doubleValue());
    pEmpPayBean.setSpecialAllowance(wSpecialAllowBD.doubleValue());
    return retVal.doubleValue();
  }

 
  private double getNonTaxableSpecialAllowanceByDays(Long pId, double pMonthlyBasic, FuturePaycheckBean pEmpPayBean) {
	    //double retVal = 0.0D;
		  BigDecimal retVal = new BigDecimal( "0.00" ).setScale(2, RoundingMode.HALF_EVEN);
		  BigDecimal wMonthlyBasicBD = new BigDecimal( Double.toString(pMonthlyBasic) ).setScale(2, RoundingMode.HALF_EVEN);
	  	  BigDecimal wTotalContribBD = new BigDecimal( Double.toString(pEmpPayBean.getTotalAllowance()) ).setScale(2, RoundingMode.HALF_EVEN);
	  	  BigDecimal wSpecialAllowBD = new BigDecimal( Double.toString(pEmpPayBean.getSpecialAllowance()) ).setScale(2, RoundingMode.HALF_EVEN);
	  	  BigDecimal wNofDaysBD = new BigDecimal( Double.toString( ( pEmpPayBean.getNoOfDays() / this.getNoOfDays()) ) );

	    double rate = 0.0D;

	    List<AbstractSpecialAllowanceEntity> pEmpAllowList = this.fSpecialAllowances.get(pId);
	    if ((pEmpAllowList == null) || (pEmpAllowList.isEmpty()))
	      return retVal.doubleValue();
	    
	    
	    for (AbstractSpecialAllowanceEntity empDed : pEmpAllowList) {
	    	
	      if (empDed.getSpecialAllowanceType().isTaxable())
	        continue;
	      
	      //double allowanceAmount = 0.0D;
	      BigDecimal wAllowanceAmt = null;
	      BigDecimal wWorkingAllowanceAmt = new BigDecimal( "0.00" ).setScale(4, RoundingMode.HALF_EVEN);

	      if (empDed.getPayTypes().isUsingPercentage())
	      {
	        rate = empDed.getAmount();
	        if ((rate >= 100.0D) || (rate < 0.0D))
	        {
	          continue;
	        }
	        BigDecimal wRateAmt = new BigDecimal( Double.toString(rate) ).setScale(2, RoundingMode.HALF_EVEN);
              wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);
	        //rate /= 100.0D;

	        //allowanceAmount = convertDoubleToEpmStandard(pMonthlyBasic * rate / 12.0D / getNoOfDays() * pEmpPayBean.getNoOfDays());

	        //retVal += allowanceAmount;
	        wWorkingAllowanceAmt = wMonthlyBasicBD.multiply(wRateAmt); 
	       // wAllowanceAmt = wAllowanceAmt.divide(new BigDecimal(12.00),2,RoundingMode.HALF_EVEN);
	        //wAllowanceAmt = wAllowanceAmt.multiply( wNofDaysBD ).setScale(2, RoundingMode.HALF_EVEN);
	        wAllowanceAmt = new BigDecimal(String.valueOf(wWorkingAllowanceAmt.doubleValue())).setScale(2,RoundingMode.FLOOR);
	        
	      }
	      else
	      {
	    	if(empDed.getName().equalsIgnoreCase("SALAR")){
	    		//Use WHOLE Value...
				wAllowanceAmt = new BigDecimal( Double.toString( empDed.getAmount() ) ).setScale(2, RoundingMode.HALF_EVEN);
				//retVal = retVal.add(wAllowanceAmt);
	            empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
	    	}else{
	    		wAllowanceAmt = new BigDecimal( Double.toString( empDed.getAmount() ) ).setScale(2, RoundingMode.HALF_EVEN);
	            wAllowanceAmt = wAllowanceAmt.multiply( wNofDaysBD ).setScale(2, RoundingMode.HALF_EVEN);

	            //retVal = retVal.add(wAllowanceAmt);
	            empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
	    	}
	        
	      }
	      
	      empDed.setActAllowAmt(wAllowanceAmt.doubleValue());
	     
	      wTotalContribBD = wTotalContribBD.add( wAllowanceAmt );
	      wSpecialAllowBD = wSpecialAllowBD.add( wAllowanceAmt );
	      retVal = retVal.add(wAllowanceAmt);
	     /* pEmpPayBean.setTotalAllowance(pEmpPayBean.getTotalAllowance() + empDed.getActAllowAmt());

	      pEmpPayBean.setSpecialAllowance(pEmpPayBean.getSpecialAllowance() + empDed.getActAllowAmt());*/
	     
	    }
	    pEmpPayBean.setTotalAllowance( wTotalContribBD.doubleValue() );
		pEmpPayBean.setSpecialAllowance( wSpecialAllowBD.doubleValue() );
	    return retVal.doubleValue();
	  }
  
	  private FuturePaycheckBean removeEmployeeNonTaxableDeductions(FuturePaycheckBean pEmpPayBean, SalaryInfo pSS) {
	    
		  List<AbstractDeductionEntity> pEmpDedList = this.fEmployeeDeductions.get(pEmpPayBean.getHiringInfo().getEmployee().getId());
	    if ((pEmpDedList == null) || (pEmpDedList.isEmpty()))
	      return pEmpPayBean;
	    
	    
	    BigDecimal wMonthlyBasicBD = new BigDecimal( Double.toString(pSS.getMonthlyBasicSalary() / 12.0) ).setScale(2, RoundingMode.HALF_EVEN);
	    BigDecimal wPartPayment = new BigDecimal( Double.toString(pEmpPayBean.getPayPercentage()) ).setScale(2, RoundingMode.HALF_EVEN);
	    BigDecimal wTotalDeductionsBD = new BigDecimal( Double.toString(pEmpPayBean.getTotalDeductions()) ).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wTotCurrDedDB = new BigDecimal( Double.toString(pEmpPayBean.getTotCurrDed()) ).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wTotNonTaxDedDB = new BigDecimal( Double.toString(pEmpPayBean.getTotalNonTaxableDeductions()) ).setScale(2, RoundingMode.HALF_EVEN);

		for (AbstractDeductionEntity empDed : pEmpDedList) {
	      if ((empDed.isTaxExempt()) && (empDed.getAmount() > 0.0D))
	      {
	        //double deductionAmount = 0.0D;
	    	  BigDecimal deductionAmount = null;
	    	  BigDecimal wWorkingDeductionAmount = new BigDecimal("0.00").setScale(4, RoundingMode.HALF_EVEN);
	        if (empDed.getPayTypes().isUsingPercentage())
	        {
	          double rate = empDed.getAmount();
	          if ((rate >= 25.0D) || (rate < 0.0D))
	          {
	            continue;
	          }
	          BigDecimal wRateAmt = new BigDecimal( Double.toString(rate) );
	          wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.HALF_EVEN);
	          //rate /= 100.0D;

	          wWorkingDeductionAmount = wMonthlyBasicBD.multiply( wRateAmt ).setScale(2, RoundingMode.HALF_EVEN);
	         
	         
	          if (pEmpPayBean.isPercentagePayment()) {
	        	  
	        	  wWorkingDeductionAmount = wWorkingDeductionAmount.multiply(wPartPayment).setScale(2,RoundingMode.HALF_EVEN);
	            }
	          
	          deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);
	         
	        }
	        else
	        {
	        	
	        	wWorkingDeductionAmount = new BigDecimal(Double.toString(empDed.getAmount()));
	          
	          if (pEmpPayBean.isPercentagePayment()) {
	        	  wWorkingDeductionAmount = wWorkingDeductionAmount.multiply(wPartPayment).setScale(2,RoundingMode.HALF_EVEN);
	        	  
	            }
	          deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);
	        }
	        empDed.setAmount(deductionAmount.doubleValue());
	        //Now Here.. create a 
	        wTotalDeductionsBD = wTotalDeductionsBD.add(deductionAmount);
	        wTotCurrDedDB = wTotCurrDedDB.add(deductionAmount);
	        wTotNonTaxDedDB = wTotNonTaxDedDB.add(deductionAmount);
	        
	        
	        
	       }
	      
	   
	    }
		pEmpPayBean.setTotalDeductions(wTotalDeductionsBD.doubleValue());
	    pEmpPayBean.setTotCurrDed(wTotCurrDedDB.doubleValue());
	    pEmpPayBean.setTotalNonTaxableDeductions(wTotNonTaxDedDB.doubleValue());
	    return pEmpPayBean;
	  }

	  private FuturePaycheckBean removeEmployeeNonTaxableDeductionsByDays(FuturePaycheckBean pEmpPayBean, BigDecimal pPayAmtBD)
	  {
	    List<AbstractDeductionEntity> pEmpDedList = this.fEmployeeDeductions.get(pEmpPayBean.getHiringInfo().getEmployee().getId());
	    
	    if ((pEmpDedList == null) || (pEmpDedList.isEmpty()))
	      return pEmpPayBean;
	    
		BigDecimal wTotalDeductionsBD = new BigDecimal( Double.toString(pEmpPayBean.getTotalDeductions()) ).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wTotCurrDedDB = new BigDecimal( Double.toString(pEmpPayBean.getTotCurrDed()) ).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wTotNonTaxDedDB = new BigDecimal( Double.toString(pEmpPayBean.getTotalNonTaxableDeductions()) ).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal wNofDaysBD = new BigDecimal( Double.toString( ( new Double(pEmpPayBean.getNoOfDays() )/ new Double(this.getNoOfDays())) ) );

	    
	    for (AbstractDeductionEntity empDed : pEmpDedList) {
	      if ((empDed.isTaxExempt()) && (empDed.getAmount() > 0.0D))
	      {
	        //double deductionAmount = 0.0D;
	    	  BigDecimal deductionAmount = null;
	    	  BigDecimal wWorkingDeductionAmount = new BigDecimal("0.00").setScale(4, RoundingMode.HALF_EVEN);
	        if (empDed.getPayTypes().isUsingPercentage())
	        {
	          double rate = empDed.getAmount();
	          if ((rate >= 25.0D) || (rate < 0.0D))
	          {
	            continue;
	          }

	          //rate /= 100.0D;
	          BigDecimal wRateAmt = new BigDecimal( Double.toString(rate) );
	          wRateAmt = wRateAmt.divide(new BigDecimal(100.00), 4, RoundingMode.FLOOR);

	          wWorkingDeductionAmount = pPayAmtBD.multiply( wRateAmt ).setScale(2, RoundingMode.HALF_EVEN);
	          //wWorkingDeductionAmount = wWorkingDeductionAmount.multiply( wNofDaysBD ).setScale(2, RoundingMode.HALF_EVEN);
	         // deductionAmount = deductionAmount.multiply(wNofDaysBD).setScale(2, RoundingMode.HALF_EVEN);
	          deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);

	          empDed.setAmount(deductionAmount.doubleValue());
	        }
	        else
	        {
	        	wWorkingDeductionAmount = 	new BigDecimal(Double.toString(empDed.getAmount())).setScale(2, RoundingMode.HALF_EVEN);
	        	wWorkingDeductionAmount = wWorkingDeductionAmount.multiply( wNofDaysBD ).setScale(2, RoundingMode.HALF_EVEN);
	        	 deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);
	          empDed.setAmount(deductionAmount.doubleValue());
	        }
	        wTotalDeductionsBD = wTotalDeductionsBD.add(deductionAmount);
	        wTotCurrDedDB = wTotCurrDedDB.add(deductionAmount);
	        wTotNonTaxDedDB = wTotNonTaxDedDB.add(deductionAmount);
	        
	       
	       }
	      
	    }
	    pEmpPayBean.setTotalDeductions(wTotalDeductionsBD.doubleValue());
	    pEmpPayBean.setTotCurrDed(wTotCurrDedDB.doubleValue());
	    pEmpPayBean.setTotalNonTaxableDeductions(wTotNonTaxDedDB.doubleValue());
	    return pEmpPayBean;
	  }

	private FuturePaycheckBean determinePayStatus(HiringInfo pHiringInfo, BusinessCertificate businessCertificate) {
		
		FuturePaycheckBean wEmpPayBean = new FuturePaycheckBean();
		wEmpPayBean.setHiringInfo(pHiringInfo);
		if (pHiringInfo.getAccountNumber() == null) {
			wEmpPayBean.setDoNotPay(true);
			wEmpPayBean.setAccountNumber("NS");
		} else {
			pHiringInfo.setAccountNumber(pHiringInfo.getAccountNumber());
		}
		if (pHiringInfo.getBvnNo() == null) {
			wEmpPayBean.setDoNotPay(true);
			wEmpPayBean.setBvnNo("NS");
		} else {
			pHiringInfo.setBvnNo(pHiringInfo.getBvnNo());
		}
		// wEmpPayBean.setBranchInstId(pHiringInfo.getBranchInstId());
		 
		 
		if (!pHiringInfo.getEmployee().isApprovedForPayrolling()) {
			wEmpPayBean.setDoNotPay(true);
			wEmpPayBean.setRejectedForPayrollingInd(IConstants.ON);
		} else if (pHiringInfo.isSuspendedEmployee()) {
			pHiringInfo.setSuspendedInd(IConstants.ON);
			if (partPaymentMap.containsKey(pHiringInfo.getEmployee().getId())) {
				if (pHiringInfo.getTerminateDate() != null) {
					wEmpPayBean.setDoNotPay(true);
				} else {
					SuspensionLog s = partPaymentMap.get(pHiringInfo.getEmployee().getId());
					wEmpPayBean.setPayPercentage(EntityUtils.convertDoubleToEpmStandard(s.getPayPercentage() / 100.0D));
					wEmpPayBean.setPayPercentageInd(1);
					wEmpPayBean.setSalaryInfo(pHiringInfo.getEmployee().getSalaryInfo());
				}

			} else {
				wEmpPayBean.setDoNotPay(true);
				wEmpPayBean.setSalaryInfo(pHiringInfo.getAbstractEmployeeEntity().getSalaryInfo());
				return wEmpPayBean;
			}

		} else if (pHiringInfo.getEmployeeType().isPoliticalOfficeHolder()) {
			if (pHiringInfo.getTerminateDate() == null)
				wEmpPayBean.setDoNotPay(false);
			else { // Test if the Termination was done during the current Pay Period...
			 

				if ( pHiringInfo.getTerminateDate().getMonthValue() == payPeriodEnd.getMonthValue()
						&& pHiringInfo.getTerminateDate().getYear() == payPeriodEnd.getYear()) {
					wEmpPayBean.setDoNotPay(false);
					wEmpPayBean.setPayByDaysInd(1);
					wEmpPayBean.setNoOfDays(pHiringInfo.getTerminateDate().getDayOfMonth() - 1);
				} else {
					wEmpPayBean.setDoNotPay(true);
					wEmpPayBean.setTerminatedInd(IConstants.ON);
				}

			}

		} else if (pHiringInfo.isContractStaff()) {

			boolean doNotPay = true;
			pHiringInfo.setContractStaff(doNotPay);
			wEmpPayBean.getHiringInfo().setPensionableInd(IConstants.ON);
			wEmpPayBean.setContractIndicator(IConstants.ON);

			if ((pHiringInfo.getContractEndDate() == null) || (pHiringInfo.getContractExpiredInd() == 1)) {
				wEmpPayBean.setDoNotPay(doNotPay);
			} else {

				if ((pHiringInfo.getContractEndDate().getMonthValue() == payPeriodEnd.getMonthValue()) && (pHiringInfo.getContractEndDate().getYear() == payPeriodEnd.getYear())) {
					if (pHiringInfo.getContractEndDate().getDayOfMonth() != payPeriodEnd.getDayOfMonth()) {
						wEmpPayBean.setPayByDaysInd(1);
						wEmpPayBean.setNoOfDays(pHiringInfo.getContractEndDate().getDayOfMonth() - 1);
						wEmpPayBean.setDoNotPay(false);
					} else {
						wEmpPayBean.setDoNotPay(false);
					}
				} else if (pHiringInfo.getContractEndDate().compareTo(payPeriodEnd) < 0)
					wEmpPayBean.setDoNotPay(doNotPay);
				else {
					wEmpPayBean.setDoNotPay(!doNotPay);
				}
			}
		} else {
			boolean terminatedByBirthDate = false;

			if (pHiringInfo.getBirthDate().isBefore(sixtyYearsAgo)) {
				boolean doNotPay = true;

				if (pHiringInfo.getBirthDate().getYear() == sixtyYearsAgo.getYear()) {
					if (pHiringInfo.getBirthDate().getMonthValue() == sixtyYearsAgo.getMonthValue()) {
						wEmpPayBean.setPayByDaysInd(1);
						wEmpPayBean.setNoOfDays(pHiringInfo.getBirthDate().getDayOfMonth() - 1);

						doNotPay = !doNotPay;
					}
				}
				wEmpPayBean.setDoNotPay(doNotPay);
				terminatedByBirthDate = doNotPay;
			}
			if (pHiringInfo.getHireDate().isBefore(thirtyFiveYearsAgo)) {
				boolean doNotPay = true;
				if (pHiringInfo.getHireDate().getYear() == thirtyFiveYearsAgo.getYear()) {
					if (pHiringInfo.getHireDate().getMonthValue() == thirtyFiveYearsAgo.getMonthValue()) {
						wEmpPayBean.setPayByDaysInd(1);
						wEmpPayBean.setNoOfDays(pHiringInfo.getHireDate().getDayOfMonth() - 1);

						doNotPay = !doNotPay;
					}
				}
				if (terminatedByBirthDate) {
					wEmpPayBean.setDoNotPay(terminatedByBirthDate);
				} else {
					wEmpPayBean.setDoNotPay(doNotPay);
				}

			}

		/*	if (pHiringInfo.getTerminateDate() != null) {
				if (zeroLastPayEmployees.containsKey(pHiringInfo.getEmployee().getId())) {

					wEmpPayBean.setDoNotPay(true);
					wEmpPayBean.setTerminatedInd(IConstants.ON);
				} else {

					if (pHiringInfo.getTerminateDate().getMonthValue() == 1) {
						wEmpPayBean.setDoNotPay(true);
						wEmpPayBean.setTerminatedInd(IConstants.ON);
					} else {
						wEmpPayBean.setPayByDaysInd(1);
						wEmpPayBean.setNoOfDays(pHiringInfo.getTerminateDate().getDayOfMonth() - 1);
						wEmpPayBean.setDoNotPay(false);
					}
				}
			}*/

		}

		if (!wEmpPayBean.isDoNotPay()) {
			 
			wEmpPayBean.setName(pHiringInfo.getAbstractEmployeeEntity().getFirstName() + " "
					+ pHiringInfo.getAbstractEmployeeEntity().getLastName());
			 

			// -- Now check for TPS and CPS.... 
		    LocalDate wTpsHireDate = PayrollBeanUtils.setDateFromString(IConstants.TPS_HIRE_DATE_STR);
			LocalDate wExpectedRetirementDate = PayrollBeanUtils.setDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR);

			try {
                wEmpPayBean.setDoNotDeductContributoryPension(PayrollBeanUtils.isTPSEmployee(pHiringInfo.getBirthDate(), pHiringInfo.getHireDate(),
                        pHiringInfo.getExpectedDateOfRetirement(), wTpsHireDate, wExpectedRetirementDate, configurationBean,businessCertificate));

			} catch (Exception wEx) { // If an exception is caught...then do not pay...
				wEmpPayBean.setDoNotDeductContributoryPension(true);
			}

		}
        wEmpPayBean.setMdaInfo(pHiringInfo.getEmployee().getMdaDeptMap().getMdaInfo());
		return wEmpPayBean;

	}

 
public Map<Long, SalaryInfo> getfSalaryInfoMap() {
	return fSalaryInfoMap;
}

public void setfSalaryInfoMap(Map<Long, SalaryInfo> fSalaryInfoMap) {
	this.fSalaryInfoMap = fSalaryInfoMap;
}


public Map<Long, SalaryDifferenceBean> getSalaryDifferenceBean() {
	return salaryDifferenceBean;
}

public void setSalaryDifferenceBean(Map<Long, SalaryDifferenceBean> salaryDifferenceBean) {
	this.salaryDifferenceBean = salaryDifferenceBean;
}

public Map<Long, SuspensionLog> getPartPaymentMap() {
	return partPaymentMap;
}

public void setPartPaymentMap(Map<Long, SuspensionLog> partPaymentMap) {
	this.partPaymentMap = partPaymentMap;
}

public Map<Long, List<AbstractDeductionEntity>> getfEmployeeDeductions() {
	return fEmployeeDeductions;
}

public void setfEmployeeDeductions(Map<Long, List<AbstractDeductionEntity>> fEmployeeDeductions) {
	this.fEmployeeDeductions = fEmployeeDeductions;
}

public Map<Long, List<AbstractGarnishmentEntity>> getfEmployeeGarnishments() {
	return fEmployeeGarnishments;
}

public void setfEmployeeGarnishments(Map<Long, List<AbstractGarnishmentEntity>> fEmployeeGarnishments) {
	this.fEmployeeGarnishments = fEmployeeGarnishments;
}

public Map<Long, List<AbstractSpecialAllowanceEntity>> getfSpecialAllowances() {
	return fSpecialAllowances;
}

public void setfSpecialAllowances(Map<Long, List<AbstractSpecialAllowanceEntity>> fSpecialAllowances) {
	this.fSpecialAllowances = fSpecialAllowances;
}

public LocalDate getSixtyYearsAgo() {
	return sixtyYearsAgo;
}

public void setSixtyYearsAgo(LocalDate sixtyYearsAgo) {
	this.sixtyYearsAgo = sixtyYearsAgo;
}

public LocalDate getThirtyFiveYearsAgo() {
	return thirtyFiveYearsAgo;
}

public void setThirtyFiveYearsAgo(LocalDate thirtyFiveYearsAgo) {
	this.thirtyFiveYearsAgo = thirtyFiveYearsAgo;
}

public int getNoOfDays() {
	return noOfDays;
}

public void setNoOfDays(int noOfDays) {
	this.noOfDays = noOfDays;
}

public LocalDate getPayPeriodEnd() {
	return payPeriodEnd;
}

public void setPayPeriodEnd(LocalDate payPeriodEnd) {
	this.payPeriodEnd = payPeriodEnd;
}



   
  
}