package com.osm.gnl.ippms.ogsg.engine;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationPaycheckBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import lombok.Data;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Data
public class CalculatePayPerABMPSimulator implements IBigDecimalValues
{
  private GenericService genericService ;
  private BusinessCertificate businessCertificate;
  private Map<Long, SalaryInfo> fSalaryInfoMap;
  private Map<Long, Long> fAgencyMap;
  private Map<Long, List<AbstractDeductionEntity>> fEmployeeDeductions;
  private Map<Long, List<AbstractGarnishmentEntity>> fEmployeeGarnishments;
  private Map<Long, List<AbstractSpecialAllowanceEntity>> fSpecialAllowances;
  private Map<Long, SuspensionLog> partPaymentMap;
  private boolean fDeductDevelopmentLevy;
  private LocalDate sixtyYearsAgo;
  private LocalDate thirtyFiveYearsAgo;
  private LocalDate payPeriodEnd;
   private int noOfDays;
   private BigDecimal wNoOfDaysBD;

  public void setGenericService(GenericService genericService)
  {
    this.genericService = genericService;
  }
  public void setSalaryInfoMap(Map<Long, SalaryInfo> pSalaryInfoMap) {
    this.fSalaryInfoMap = pSalaryInfoMap;
  }

  public SimulationPaycheckBean calculatePayroll(SimulationPaycheckBean pEmpPayBean)
    throws Exception
  {
    pEmpPayBean = setPaycheckEligibility(pEmpPayBean,payPeriodEnd);

    if (!pEmpPayBean.isDoNotPay())
    {
      SalaryInfo wSS = this.fSalaryInfoMap.get(pEmpPayBean.getSalaryInfoId());

      if (wSS == null) {
        return pEmpPayBean;
      }

      
      
      if(pEmpPayBean.isPayPerDays()) {
    	  wNoOfDaysBD = new BigDecimal(  new Double(pEmpPayBean.getNoOfDays()) / new Double(this.getNoOfDays()) ).setScale(2, RoundingMode.HALF_EVEN);
      }
      
      if(pEmpPayBean.getPayPercentage() < 1 && pEmpPayBean.getPayPercentage() != 0) {
    	   //Leave as is...
      }else {
    	  pEmpPayBean.setPayPercentage(1.0D);
      }
       pEmpPayBean = this.determineLtgStatus(pEmpPayBean);
       
      //TODO ADD Pensioner Simulation.
      double yearlyBasic = wSS.getMonthlyBasicSalary();
      
      if(pEmpPayBean.isLtgEnabled()) {
    	  pEmpPayBean.setLeaveTransportGrant(EntityUtils.convertDoubleToEpmStandard(wSS.getMonthlyBasicSalary() * 0.1D));
      }
      
      double payAmt = EntityUtils.convertDoubleToEpmStandard(wSS.getMonthlyBasicSalary()/12.0D * pEmpPayBean.getPayPercentage());
      
      if(pEmpPayBean.isPayPerDays()) {
    	  BigDecimal wPayAmtBD = new BigDecimal(Double.toString(payAmt/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
          
          wPayAmtBD = wPayAmtBD.multiply(wNoOfDaysBD);
          payAmt = wPayAmtBD.doubleValue();
      }
      
      double totalAllowances = this.addAllowances(wSS, yearlyBasic, pEmpPayBean);
      
      
     
      pEmpPayBean.setTotalPay(payAmt + totalAllowances);
      pEmpPayBean.setTotalAllowance(totalAllowances);
      
      double nonTaxableSpecialAllowance = getSpecialAllowance( payAmt, pEmpPayBean, false, true);

      nonTaxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(nonTaxableSpecialAllowance);

      double taxableSpecialAllowance = getSpecialAllowance(payAmt, pEmpPayBean,true, false);

      taxableSpecialAllowance = EntityUtils.convertDoubleToEpmStandard(taxableSpecialAllowance);
       
      BigDecimal wPayPercentBD = new BigDecimal(Double.toString(pEmpPayBean.getPayPercentage())).setScale(2, RoundingMode.HALF_EVEN);
      double wGrossIncome = 0.0D;
      BigDecimal wShiftAndCallDuty = null;
      if(wSS.getCallDuty() > 0 || wSS.getShiftDuty() > 0){
      	Double wShiftPlusCall = wSS.getCallDuty() + wSS.getShiftDuty();
      	wShiftAndCallDuty = new BigDecimal(Double.toString(wShiftPlusCall) ).setScale(2,RoundingMode.HALF_EVEN);
      	
      }
      if (!pEmpPayBean.isPayPerDays()) {
 		   wGrossIncome = EntityUtils.convertDoubleToEpmStandard(wSS.getConsolidatedAllowance() * pEmpPayBean.getPayPercentage() + payAmt + (taxableSpecialAllowance * 12.0D));

      	if(wShiftAndCallDuty != null){
      		wShiftAndCallDuty = wShiftAndCallDuty.multiply(wPayPercentBD);
      		 wGrossIncome -= EntityUtils.convertDoubleToEpmStandard(wShiftAndCallDuty.doubleValue()); 
      	}  	 
      }
      else {
      	
        wGrossIncome = EntityUtils.convertDoubleToEpmStandard(wSS.getConsolidatedAllowance() + payAmt + (taxableSpecialAllowance * 12.0D));
        BigDecimal wPayAmtBD = new BigDecimal(Double.toString(wGrossIncome)).setScale(2, RoundingMode.HALF_EVEN);
        
        wPayAmtBD = wPayAmtBD.multiply(wNoOfDaysBD);
        wGrossIncome = wPayAmtBD.doubleValue();
        
        if(wShiftAndCallDuty != null) {
        	wGrossIncome -= EntityUtils.convertDoubleToEpmStandard(wShiftAndCallDuty.multiply(wNoOfDaysBD).doubleValue()); 
        }
      	  
      }
      double wAnnualRelief = PayrollEngineHelper.getRelief(wGrossIncome);

      double monthlyRelief =  EntityUtils.convertDoubleToEpmStandard(wAnnualRelief/12.0D);
      double totalContAmount = 0.0D;
      if (pEmpPayBean.isPensionableEmployee() && !pEmpPayBean.isDoNotDeductContributoryPension()
      		&& !pEmpPayBean.isContractStaff()
      		&& !pEmpPayBean.isPoliticalOfficeHolder()) {
		  BigDecimal wTotContAmt;
		  if (wSS.getSalaryType().isBasicRentTransportType()) {
			  wTotContAmt = new BigDecimal(wSS.getMonthlyBasicSalary() + wSS.getRent() + wSS.getTransport());
        	wTotContAmt = wTotContAmt.divide(wYearDivisorDB,2, RoundingMode.HALF_EVEN);
        	wTotContAmt = wTotContAmt.multiply(w7Point5Percent);
        	
          
          if (!pEmpPayBean.isPayPerDays()) {
          	BigDecimal wPercentagePayment = new BigDecimal(pEmpPayBean.getPayPercentage()).setScale(2, RoundingMode.HALF_EVEN);
          	
          	  wTotContAmt = wTotContAmt.multiply(wPercentagePayment);
           }else {
        	   wTotContAmt = wTotContAmt.multiply(wNoOfDaysBD);
           }
			  //totalContAmount = wTotContAmt.doubleValue();

		  }
        else {
			  wTotContAmt = new BigDecimal(wSS.getMonthlyBasicSalary() + wSS.getRent() + wSS.getMotorVehicle());

			  wTotContAmt = wTotContAmt.divide(wYearDivisorDB,2, RoundingMode.HALF_EVEN);
          	wTotContAmt = wTotContAmt.multiply(w7Point5Percent);
          	
            
            if (!pEmpPayBean.isPayPerDays()) {
            	BigDecimal wPercentagePayment = new BigDecimal(pEmpPayBean.getPayPercentage()).setScale(2, RoundingMode.HALF_EVEN);
            	wTotContAmt = wTotContAmt.multiply(wPercentagePayment);
              }else {
            	  wTotContAmt = wTotContAmt.multiply(wNoOfDaysBD);
              }
			  // totalContAmount = wTotContAmt.doubleValue();

		  }
		  totalContAmount = new BigDecimal(wTotContAmt.doubleValue()).setScale(2, RoundingMode.FLOOR).doubleValue();
		  pEmpPayBean.setTotalDeductions(totalContAmount);
		  pEmpPayBean.setTotalContributions(totalContAmount);

	  }

      pEmpPayBean = removeEmployeeNonTaxableDeductions(pEmpPayBean, wSS);

      double freePay = monthlyRelief + pEmpPayBean.getTotalDeductions();

      double taxesDue = EntityUtils.convertDoubleToEpmStandard(PayrollEngineHelper.calculateTaxes(wGrossIncome, freePay));

     // pEmpPayBean.setTws(wSS.getTws());

    

      pEmpPayBean.setTaxesPaid(taxesDue);
      pEmpPayBean.setMonthlyTax(taxesDue);

      payAmt = removeGarnishments(pEmpPayBean);

      payAmt = removeNHFandUnionDues(wSS, payAmt);

     // payAmt = addAllowances(wSS, payAmt, pEmpPayBean);

    
        pEmpPayBean.setNetPay(payAmt);
     
      pEmpPayBean.setNhf(wSS.getNhf());
      pEmpPayBean.setUnionDues(0.0D);

 
      pEmpPayBean.setGrossPay(pEmpPayBean.getTotalPay());
    }
    else {
      pEmpPayBean.setTotalPay(0.0D);

      pEmpPayBean.setNhf(0.0D);

      pEmpPayBean.setGrossPay(0.0D);

      pEmpPayBean.setTaxesPaid(0.0D);

      pEmpPayBean.setTotalAllowance(0.0D);

      pEmpPayBean.setMonthlyTax(0.0D);

      pEmpPayBean.setNetPay(0.0D);
     }

    return pEmpPayBean;
  }
  private SimulationPaycheckBean removeEmployeeNonTaxableDeductions(SimulationPaycheckBean pEmpPayBean, SalaryInfo pSS) {
	    
	  List<AbstractDeductionEntity> pEmpDedList = this.fEmployeeDeductions.get(pEmpPayBean.getEmployee().getId());
    if ((pEmpDedList == null) || (pEmpDedList.isEmpty()))
      return pEmpPayBean;
    
    
    BigDecimal wMonthlyBasicBD = new BigDecimal( Double.toString(pSS.getMonthlyBasicSalary() / 12.0) ).setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal wPartPayment = new BigDecimal( Double.toString(pEmpPayBean.getPayPercentage()) ).setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal wTotalDeductionsBD = new BigDecimal( Double.toString(pEmpPayBean.getTotalDeductions()) ).setScale(2, RoundingMode.HALF_EVEN);
	 
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
            
          wWorkingDeductionAmount = wWorkingDeductionAmount.multiply(wPartPayment).setScale(2,RoundingMode.HALF_EVEN);
         if(pEmpPayBean.isPayPerDays()) {
        	 wWorkingDeductionAmount = wWorkingDeductionAmount.multiply( wNoOfDaysBD ).setScale(2, RoundingMode.HALF_EVEN);
          }
          
          
          deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);
         
         
        }
        else
        {
        	
        	wWorkingDeductionAmount = new BigDecimal(Double.toString(empDed.getAmount()));
          
            wWorkingDeductionAmount = wWorkingDeductionAmount.multiply(wPartPayment).setScale(2,RoundingMode.HALF_EVEN);
            if(pEmpPayBean.isPayPerDays()) {
           	    wWorkingDeductionAmount = wWorkingDeductionAmount.multiply( wNoOfDaysBD ).setScale(2, RoundingMode.HALF_EVEN);
             }
             
          deductionAmount = new BigDecimal(String.valueOf(wWorkingDeductionAmount.doubleValue())).setScale(2,RoundingMode.FLOOR);
        }
        empDed.setAmount(deductionAmount.doubleValue());
        //Now Here.. create a 
        wTotalDeductionsBD = wTotalDeductionsBD.add(deductionAmount);
       
        
        if (pEmpPayBean.getEmployeeDeductions() == null) {
          pEmpPayBean.setEmployeeDeductions(new ArrayList<>());
        }
        
        pEmpPayBean.getEmployeeDeductions().add(empDed);
       }
      
   
    }
	pEmpPayBean.setTotalDeductions(wTotalDeductionsBD.doubleValue());
    
     
    return pEmpPayBean;
  }

	private SimulationPaycheckBean determineLtgStatus(SimulationPaycheckBean pEmpPayBean) {
		 if (this.fAgencyMap.containsKey(pEmpPayBean.getMdaInfo().getId()))  
       	  pEmpPayBean.setLtgEnabled(true);  
		 if(pEmpPayBean.getLtgLastPaid() != null) {
			 if(pEmpPayBean.getLtgLastPaid().getYear() ==  this.getPayPeriodEnd().getYear()) {
				 pEmpPayBean.setLtgEnabled(false);
			 }
		 }
		 if(pEmpPayBean.isPoliticalOfficeHolder() ||pEmpPayBean.isContractStaff() )
			 pEmpPayBean.setLtgEnabled(false);
		 
	 
	return pEmpPayBean;
}
	 

  private SimulationPaycheckBean setPaycheckEligibility(SimulationPaycheckBean pEmpPayBean, LocalDate payPeriodEnd) throws InstantiationException, IllegalAccessException {


  	    if(pEmpPayBean.isPensionerType()){
  	    	if(pEmpPayBean.getTerminateDate() != null){

					if(pEmpPayBean.getTerminateDate().getMonthValue() == this.getPayPeriodEnd().getMonthValue()
							&& pEmpPayBean.getTerminateDate().getYear() ==  this.getPayPeriodEnd().getYear()){
						pEmpPayBean.setDoNotPay(false);
						pEmpPayBean.setNoOfDays(pEmpPayBean.getTerminateDate().getDayOfMonth() - 1);
						pEmpPayBean.setPayPerDays(true);
					}else{
						pEmpPayBean.setDoNotPay(true);

					}

				if(!pEmpPayBean.isApprovedForPayroll()){
					pEmpPayBean.setDoNotPay(true);

				}else if (pEmpPayBean.isSuspended()){
					pEmpPayBean.setDoNotPay(true);
				}
			}
  	    	return pEmpPayBean;
		}
	    if(!pEmpPayBean.isApprovedForPayroll()){
	    	 pEmpPayBean.setDoNotPay(true);
	    	
	    }else if (pEmpPayBean.isSuspended())
	    {
	    	
	      if (this.partPaymentMap.containsKey(pEmpPayBean.getEmployee().getId())) {
	    	if(pEmpPayBean.getTerminateDate() != null){
	    		 pEmpPayBean.setDoNotPay(true);
	    	}else{
	    		SuspensionLog s = this.partPaymentMap.get(pEmpPayBean.getEmployee().getId());
	    		pEmpPayBean.setPayPercentage(EntityUtils.convertDoubleToEpmStandard(s.getPayPercentage() / 100.0D));
	    		
	    		
	    	}
	        
	      } else {
	        pEmpPayBean.setDoNotPay(true);
	       
	        return pEmpPayBean;
	      }
	      
	    }
	    else if (pEmpPayBean.isPoliticalOfficeHolder())
	    {
	      if (pEmpPayBean.getTerminateDate() == null)
	        pEmpPayBean.setDoNotPay(false);
	      else {
	    	  //Test if the Termination was done during the current Pay Period...
	    	  if(pEmpPayBean.getTerminateDate().getMonthValue() == this.getPayPeriodEnd().getMonthValue()
	    			  && pEmpPayBean.getTerminateDate().getYear() ==  this.getPayPeriodEnd().getYear()){
	    		  pEmpPayBean.setDoNotPay(false);
	    		  pEmpPayBean.setNoOfDays(pEmpPayBean.getTerminateDate().getDayOfMonth() - 1);
	    		  pEmpPayBean.setPayPerDays(true);
	    	  }else{
	    		  pEmpPayBean.setDoNotPay(true);
	    		 
	    	  }
	       
	      }
	      
	    }
	    else if (pEmpPayBean.isContractStaff())
	    {
	      
	      boolean doNotPay = true;
	      LocalDate contractEndDate = pEmpPayBean.getContractEndDate();

	        if ((contractEndDate.getMonthValue() ==  this.getPayPeriodEnd().getMonthValue()) && (contractEndDate.getYear() ==  this.getPayPeriodEnd().getYear()))
	        {
	          if (contractEndDate.getDayOfMonth() !=  this.getPayPeriodEnd().getDayOfMonth()) {
	          
	            pEmpPayBean.setNoOfDays(contractEndDate.getDayOfMonth() - 1);
	            pEmpPayBean.setDoNotPay(false);
	            pEmpPayBean.setPayPerDays(true);
	          } else {
	            pEmpPayBean.setDoNotPay(false);
	          }
	        }
	        else if (contractEndDate.isBefore( this.getPayPeriodEnd()))
	          pEmpPayBean.setDoNotPay(doNotPay);
	        else {
	          pEmpPayBean.setDoNotPay(!doNotPay);
	        }
	      }
	    
	    else
	    {
	    	boolean terminatedByBirthDate = false;
	      if (pEmpPayBean.getBirthDate().isBefore(getSixtyYearsAgo())) {
	        boolean doNotPay = true;

	        if (pEmpPayBean.getBirthDate().getYear() == getSixtyYearsAgo().getYear())
	        {
	          if (pEmpPayBean.getBirthDate().getMonthValue() == getSixtyYearsAgo().getMonthValue())
	          {
	           
	            pEmpPayBean.setNoOfDays(pEmpPayBean.getBirthDate().getDayOfMonth() - 1);

	            doNotPay = !doNotPay;
	          }
	        }
	        pEmpPayBean.setDoNotPay(doNotPay);
	        terminatedByBirthDate = doNotPay;
	      }
	      if (pEmpPayBean.getHireDate().isBefore(getThirtyFiveYearsAgo())) {
	        boolean doNotPay = true;
	        if (pEmpPayBean.getHireDate().getYear() == getThirtyFiveYearsAgo().getYear())
	        {
	          if (pEmpPayBean.getHireDate().getMonthValue() == getThirtyFiveYearsAgo().getMonthValue())
	          {
	           
	            pEmpPayBean.setNoOfDays(pEmpPayBean.getHireDate().getDayOfMonth() - 1);

	            doNotPay = !doNotPay;
	          }
	        }
	        if(terminatedByBirthDate){
	        	 pEmpPayBean.setDoNotPay(terminatedByBirthDate);
	        }else{
	        	pEmpPayBean.setDoNotPay(doNotPay);
	        }
	        
	      }
	    }
	      
	    LocalDate wTpsHireDate = PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_HIRE_DATE_STR);
	  	LocalDate wExpectedRetirementDate = PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR);
	  ConfigurationBean configurationBean = this.genericService.loadObjectWithSingleCondition(ConfigurationBean.class,CustomPredicate.procurePredicate("businessClientId",businessCertificate.getBusinessClientInstId()));
		  
		  try{
			  pEmpPayBean.setDoNotDeductContributoryPension(PayrollBeanUtils.isTPSEmployee(pEmpPayBean.getBirthDate(), pEmpPayBean.getHireDate(), pEmpPayBean.getExpectedDateOfRetirement(), wTpsHireDate, wExpectedRetirementDate,configurationBean,businessCertificate));
		    
		     
		  }catch(Exception wEx){
			  //If an exception is caught...then do not pay...
			  pEmpPayBean.setDoNotDeductContributoryPension(true);
		  }
	    return pEmpPayBean;
	  
  }

  private double getSpecialAllowance(double pMonthlyBasic, SimulationPaycheckBean pEmpPayBean, boolean pTaxableOnly, boolean pNonTaxableOnly  ) {
	    //double retVal = 0.0D;
		  BigDecimal retVal = new BigDecimal( "0.00" ).setScale(2, RoundingMode.HALF_EVEN);
		  BigDecimal wMonthlyBasicBD = new BigDecimal( Double.toString(pMonthlyBasic) ).setScale(2, RoundingMode.HALF_EVEN);
	  	  BigDecimal wTotalContribBD = new BigDecimal( Double.toString(pEmpPayBean.getTotalAllowance()) ).setScale(2, RoundingMode.HALF_EVEN);
	  	  BigDecimal wNofDaysBD = null;
	  	   

	    double rate = 0.0D;

	    List<AbstractSpecialAllowanceEntity> pEmpAllowList = this.fSpecialAllowances.get(pEmpPayBean.getEmployee().getId());
	    if ((pEmpAllowList == null) || (pEmpAllowList.isEmpty()))
	      return retVal.doubleValue();
	    
	    
	    for (AbstractSpecialAllowanceEntity empDed : pEmpAllowList) {
	    	
	      if (empDed.getSpecialAllowanceType().isTaxable()) {
	    	  if(pNonTaxableOnly)
	    		  continue;
	    	  
	      }else {
	    	  if(pTaxableOnly)
	    		  continue;
	      }
	        
	      
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
	        
	        wWorkingAllowanceAmt = wMonthlyBasicBD.multiply(wRateAmt); 
	       
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
	    		if(pEmpPayBean.isPayPerDays())
	               wAllowanceAmt = wAllowanceAmt.multiply( wNofDaysBD ).setScale(2, RoundingMode.HALF_EVEN);
 
	    	}
	        
	      }
	    
	      retVal = retVal.add(wAllowanceAmt);
	     
	     
	    }
	    pEmpPayBean.setTotalAllowance( wTotalContribBD.doubleValue() );
	 
	    return retVal.doubleValue();
	  }
  private double addAllowances(SalaryInfo pSS, double pPayAmt, SimulationPaycheckBean pSPB)
  {
    double wRetVal = 0.0D;
    
     wRetVal = EntityUtils.convertDoubleToEpmStandard(pSS.getConsAllowance()/12.0D * pSPB.getPayPercentage());
     
     if(pSPB.isPayPerDays()) {
    	 BigDecimal wPayAmtBD = new BigDecimal(Double.toString(pSS.getConsAllowance()/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
         
         wPayAmtBD = wPayAmtBD.multiply(wNoOfDaysBD);
    	 wRetVal = EntityUtils.convertDoubleToEpmStandard(wPayAmtBD.doubleValue());
     }
    

    if (pSPB.isLtgEnabled()) {
      wRetVal += pSPB.getLeaveTransportGrant();
    }
    return wRetVal;
  }
  private double removeNHFandUnionDues(SalaryInfo pSalaryStructure, double pPayAmt) {
    double wRetVal = 0.0D;
    wRetVal = pPayAmt - (pSalaryStructure.getNhf() );
    return wRetVal;
  }
  private double removeGarnishments(SimulationPaycheckBean pE) {
    double retVal = pE.getTotalPay();
    double garnishAmt;

	List<AbstractGarnishmentEntity> pEmpGarnList =
			(List<AbstractGarnishmentEntity>)this.genericService.loadAllObjectsWithSingleCondition(IppmsUtils.getGarnishmentInfoClass(businessCertificate), CustomPredicate.procurePredicate("employee.id",pE.getEmployee().getId()), null);

    if ((pEmpGarnList == null) || (pEmpGarnList.isEmpty())) {
      return retVal;
    }
    for (AbstractGarnishmentEntity e : pEmpGarnList) {
      if (e.getOwedAmount() > 0.0D)
      {
        garnishAmt = e.getAmount();

        if ((garnishAmt < e.getOwedAmount()) && ((e.getGarnishCap() <= garnishAmt) || (e.getGarnishCap() == 0.0D))) {
          retVal -= garnishAmt;
          pE.setTotalGarnishments(pE.getTotalGarnishments() + garnishAmt);
        } else if ((garnishAmt >= e.getOwedAmount()) && (e.getOwedAmount() > 0.0D)) {
          retVal -= e.getOwedAmount();
          pE.setTotalGarnishments(pE.getTotalGarnishments() + e.getOwedAmount());
        }
      }

    }

    return retVal;
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
	public void setfAgencyMap(HashMap<Long, Long> hashMap)
	{
		this.fAgencyMap = hashMap;
	}
	public Map<Long, SalaryInfo> getfSalaryInfoMap() {
		return fSalaryInfoMap;
	}
	public void setfSalaryInfoMap(Map<Long, SalaryInfo> fSalaryInfoMap) {
		this.fSalaryInfoMap = fSalaryInfoMap;
	}
	public Map<Long, Long> getfAgencyMap() {
		return fAgencyMap;
	}

}