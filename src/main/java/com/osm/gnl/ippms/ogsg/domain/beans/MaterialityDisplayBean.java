/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.abstractentities.DependentEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MaterialityDisplayBean extends DependentEntity {
	private String amountStr;
	private String fileName;
	private String payPeriodStr;
 	private String zeroPercent = "0.00%";
	private int noOfNewEmployees;
	private int noOfEmpReassigned;
	private int noOfEmpPromoted;
	private double newEmpNetEffect;
	private double reassignedNetEffect;
	private double promotedNetEffect;
	private double total;
	private String nuEmpNetEffectStr;
	private String reassignedNetEffectStr;
	private String promotedNetEffectStr;
	private String totalStr;
	private boolean employeesPromoted;
	private boolean employeesReassigned;
	private boolean employeesCreated;
	private int noOfEmpPromotionArrears;
	private int noOfEmpReabsorbArrears;
	private int noOfEmpOvertimePayments;
	private int noOfEmpPayeInstruction;
	private int noOfEmpSalaryInstruction;
	private int noOfEmpThirteenthMonth;
	private int noOfEmpWithSpecialAllowance;
	private int noOfEmpWithUnderpaymentAllowance;
	private double promotionArrearsNetEffect;
	private double reabsorbedNetEffect;
	private double overtimeNetEffect;
	private double payeInstructionNetEffect;
	private double salaryInstructionNetEffect;
	private double thirteenMonthNetEffect;
	private double specialAllowanceNetEffect;
	private double underPaymentAllowanceNetEffect;
	private String promotionArrearsNetEffectStr;
	private String reabsorbedNetEffectStr;
	private String overtimeNetEffectStr;
	private String payeInstructionNetEffectStr;
	private String salaryInstructionNetEffectStr;
	private String thirteenMonthNetEffectStr;
	private String specialAllowanceNetEffectStr;
	private String underPaymentAllowanceNetEffectStr;
	private boolean promotionArrears;
	private boolean reabsorbedArrears;
	private boolean overtimePayments;
	private boolean payeReduction;
	private boolean salaryIncrease;
	private boolean thirteenthMonth;
	private boolean specialAllowances;
	private boolean underPaymentAllowances;
	 

	// --- Payroll Run Statistics Variables
	private double netPaySum;
	private String netPaySumStr;
	private double totalPaySum;
	private String totalPaySumStr;
	private double totalDeductionSum;
	private String totalDeductionSumStr;
	private Integer noOfEmployeesPaid;
	private int noOfPenForRecalc;
	private int noOfEmployeesNotPaid;
	private int noOfEmployeesProcessed;
	private int employeesNotPaidByBirthDate;
	private int employeesNotPaidByHireDate;
	private int employeesNotPaidByContract;
	private int employeesNotPaidBySuspension;
	private int employeesPaidByInterdiction;
	private int pensionersNotPaidByPensionCalc;
	private Integer noOfEmployeesWithNegativePay;
	private Double negativePaySum;
	private String negativePaySumStr;
	private Double netPayByDays;
	private String netPayByDaysStr;
	private Double totalPayByDays;
	private String totalPayByDaysStr;
	private Double specialAllowance;
	private String specialAllowanceStr;
	private Integer noOfEmployeesPaidByDays;

	private Integer noOfEmpPaidSpecAllow;
	private int noOfEmpRetiredByBirthDate;
	private int noOfEmpRetiredByHireDate;
	private int noOfPenNotPaidByPensionCalc;
	private int noOfEmpRetiredByContract;
	private int noOfEmpRetiredBySuspension;
	private int noOfEmpPaidSpecialAllowance;
	
	//Percentage Values...
	private String percentageOfEmployees;
	private String percentageOfEmpForBirthDate;
	private String percentageOfEmpForHireDate;
	private String percentageOfPenAwaitingCalculation;
	private String percentageOfEmpForContract;
	private String percentageOfEmpForSuspension;
	private String percentageOfEmpWithNegPay;
	private String percentageOfEmpPaidByDays;
	private String percentageOfEmpPaidSpecAllow;
	private String percentageOfEmpPaidByContract;
	private String percentageOfEmpPaidByInterdiction;
	private String payByDaysPercentageOfGross;
	private String specAllowPercentageOfGross;
	private String deductionPercentageOfGross;
	private String contractEmpPercentageOfGross;
	private String interdictionEmpPercentageOfGross;
	private String percentageOfEmpNotApproved;
	private String contractEmpNotPaidPercentageOfGross;
	private String percentageOfEmpTotalPay;
	
	
	private int noOfEmpWithNegPay;
	private int noOfEmpPaidByDays;
	private String zeroNaira = IConstants.naira+"0.00" ;
	private String zero = "0.00" ;
	private String monthAndYearStr;
	private String monthAndYearExcelStr;
	private double contractTotalPay;
	private String contractTotalPayStr;
	private double contractNetPay;
	private String contractNetPayStr;
	private double suspendedTotalPay;
	private String suspendedTotalPayStr;
	private double suspendedNetPay;
	private String suspendedNetPayStr;
	private double specAllowNetPay;
	private double specAllowTotalPay;
	private String specAllowNetPayStr;
	private String specAllowTotalPayStr;
	private double netPayByBirthDate;
	private String netPayByBirthDateStr;
	private double netPayByHireDate;
	private String netPayByHireDateStr;
	private double totalPayByHireDate;
	private String totalPayByHireDateStr;
	private double totalPayByBirthDate;
	private String totalPayByBirthDateStr;
	private Integer employeesPaidByContract;
	private int noOfEmpPaidByContract;
	private double totalPayContract;
	private String totalPayContractStr;
	private double netPayContract;
	private String netPayContractStr;
	private double negativePayTotalSum;
	private String negativePayTotalSumStr;
	private String totalPaySpecAllow;
	private int objectInd;
	private int employeesOnInterdiction;
	private int noOfEmpPaidByInterdiction;
	private double netPayInterdiction;
	private String netPayInterdictionStr;
	private double totalPayInterdiction;
	private String totalPayInterdictionStr;
	private int employeesNotPaidByTermination;
	private int noOfEmpRetiredByTermination;
	private String percentageOfEmpRetiredByTermination;
	private String percentageOfEmpSpecAllow;
	private String percentageOfEmpNotPaidByContract;
	private int employeesNotPaidByApproval;
	private int noOfEmpNotPaidByApproval;
	private int noOfEmpNotPaidByContract;
	private double materialityTotal;
	private String materialityTotalStr;

	//--DO NOT CHANGE SIGNATURE of objectList. Used by Materiality Module
	private List<?> objectList;

	public MaterialityDisplayBean(List<?> pObjectList){
		this.objectList = pObjectList;
	}

	public String getMaterialityTotalStr() {
		materialityTotalStr = IConstants.naira + PayrollHRUtils.getDecimalFormat().format(this.materialityTotal);
		return materialityTotalStr;
	}

	public String getNuEmpNetEffectStr()
	{
		this.nuEmpNetEffectStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(getNewEmpNetEffect()));
		return this.nuEmpNetEffectStr;
	}


	public String getReassignedNetEffectStr()
	{
		this.reassignedNetEffectStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat()
				.format(getReassignedNetEffect()));
		return this.reassignedNetEffectStr;
	}

	public String getPromotedNetEffectStr()
	{
		this.promotedNetEffectStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat()
				.format(getPromotedNetEffect()));
		return this.promotedNetEffectStr;
	}

	public boolean isEmployeesPromoted()
	{
		this.employeesPromoted = (getNoOfEmpPromoted() > 0);
		return this.employeesPromoted;
	}

	public boolean isEmployeesReassigned()
	{
		this.employeesReassigned = (getNoOfEmpReassigned() > 0);
		return this.employeesReassigned;
	}
   public boolean isEmployeesCreated()
	{
		this.employeesCreated = (getNoOfNewEmployees() > 0);
		return this.employeesCreated;
	}


	public String getTotalStr()
	{
		this.totalStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(total));
		return this.totalStr;
	}

	public String getPromotionArrearsNetEffectStr()
	{
		if (getPromotionArrearsNetEffect() > 0.0D)
			this.promotionArrearsNetEffectStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat()
					.format(getPromotionArrearsNetEffect()));
		return this.promotionArrearsNetEffectStr;
	}

	public String getReabsorbedNetEffectStr()
	{
		if (getReabsorbedNetEffect() > 0.0D)
			this.reabsorbedNetEffectStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat()
					.format(getReabsorbedNetEffect()));
		return this.reabsorbedNetEffectStr;
	}


	public String getPayeInstructionNetEffectStr()
	{
		this.payeInstructionNetEffectStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat()
				.format(getPayeInstructionNetEffect()));
		return this.payeInstructionNetEffectStr;
	}

	public String getSalaryInstructionNetEffectStr()
	{
		this.salaryInstructionNetEffectStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat()
				.format(getSalaryInstructionNetEffect()));
		return this.salaryInstructionNetEffectStr;
	}

	public String getThirteenMonthNetEffectStr()
	{
		if (getThirteenMonthNetEffect() > 0.0D)
			this.thirteenMonthNetEffectStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat()
					.format(getThirteenMonthNetEffect()));
		return this.thirteenMonthNetEffectStr;
	}


	public boolean isPromotionArrears()
	{
		this.promotionArrears = (getNoOfEmpPromotionArrears() > 0);
		return this.promotionArrears;
	}

	public boolean isReabsorbedArrears()
	{
		this.reabsorbedArrears = (getNoOfEmpReabsorbArrears() > 0);
		return this.reabsorbedArrears;
	}

	public boolean isPayeReduction()
	{
		this.payeReduction = (getNoOfEmpPayeInstruction() > 0);
		return this.payeReduction;
	}


	public boolean isSalaryIncrease()
	{
		this.salaryIncrease = (getNoOfEmpSalaryInstruction() > 0);
		return this.salaryIncrease;
	}

	public boolean isThirteenthMonth()
	{
		this.thirteenthMonth = (getNoOfEmpThirteenthMonth() > 0);
		return this.thirteenthMonth;
	}


	public String getSpecialAllowanceNetEffectStr()
	{
		this.specialAllowanceNetEffectStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat()
				.format(getSpecialAllowanceNetEffect()));
		return this.specialAllowanceNetEffectStr;
	}

	public boolean isSpecialAllowances()
	{
		this.specialAllowances = (getNoOfEmpWithSpecialAllowance() > 0);
		return this.specialAllowances;
	}


	public String getUnderPaymentAllowanceNetEffectStr()
	{
		this.underPaymentAllowanceNetEffectStr = PayrollHRUtils.getDecimalFormat()
				.format(getUnderPaymentAllowanceNetEffect());
		return this.underPaymentAllowanceNetEffectStr;
	}


	public boolean isUnderPaymentAllowances()
	{
		this.underPaymentAllowances = (getNoOfEmpWithUnderpaymentAllowance() > 0);
		return this.underPaymentAllowances;
	}

	public String getPercentageOfEmployees()
	{

		if(this.getNoOfEmployeesProcessed() == 0 || this.getNoOfEmployeesPaid() == 0)
			return zeroPercent;
		percentageOfEmployees = PayrollHRUtils.getDecimalFormat().format(new Double(String.valueOf(this.getNoOfEmployeesPaid()))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed())) * 100 )+"%";
		return percentageOfEmployees;

	}

	public String getTotalPayPercentageOfGross(){
		if(this.getNoOfEmployeesProcessed() == 0 || this.getTotalPaySum() == 0)
			return zeroPercent;
		percentageOfEmpTotalPay = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getTotalPaySum()))/
				new Double(String.valueOf(this.getTotalPaySum()))) * 100.0)+"%";
		return percentageOfEmpTotalPay;
	}

	public String getSpecAllowPercentageOfGross()
	{
		if(this.getTotalPaySum() == 0 || this.getSpecialAllowance() == 0)
			return zeroPercent;
		specAllowPercentageOfGross = PayrollHRUtils.getDecimalFormat().format(new Double(this.getSpecialAllowance())/
				new Double(this.getTotalPaySum())* 100.0)+"%";
		return specAllowPercentageOfGross;
	}

	public String getDeductionPercentageOfGross()
	{
		if(this.getTotalPaySum() == 0 || this.getTotalDeductionSum() == 0)
			return zeroPercent;
		deductionPercentageOfGross = PayrollHRUtils.getDecimalFormat().format(new Double(this.getTotalDeductionSum())/
				new Double(this.getTotalPaySum())* 100.0)+"%";
		
		return deductionPercentageOfGross;
	}


	public String getPercentageOfEmpForBirthDate()
	{
		if(this.getNoOfEmployeesProcessed() == 0 || this.getEmployeesNotPaidByBirthDate() == 0)
			return zeroPercent;
		percentageOfEmpForBirthDate = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getEmployeesNotPaidByBirthDate()))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed()))) * 100.0)+"%";
		return percentageOfEmpForBirthDate;
	}

	public String getPercentageOfEmpForHireDate()
	{
		if(this.getNoOfEmployeesProcessed() == 0 || this.getEmployeesNotPaidByHireDate() == 0)
			return zeroPercent;
		percentageOfEmpForHireDate = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getEmployeesNotPaidByHireDate()))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed())))* 100.0)+"%";
		
		return percentageOfEmpForHireDate;
	}

	public String getPercentageOfEmpForContract()
	{
		if(this.getNoOfEmployeesPaid() == 0 || this.getNoOfEmpRetiredByContract() == 0)
			return zeroPercent;
		percentageOfEmpForContract = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getNoOfEmpRetiredByContract()))/
				new Double(String.valueOf(this.getNoOfEmployeesPaid() )))* 100.0)+"%";
		return percentageOfEmpForContract;
	}

	public String getPercentageOfPenAwaitingCalculation() {
		if(this.getNoOfEmployeesPaid() == 0 || this.getPensionersNotPaidByPensionCalc() == 0)
			return zeroPercent;
		percentageOfPenAwaitingCalculation = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getPensionersNotPaidByPensionCalc()))/
				new Double(String.valueOf(this.getNoOfEmployeesPaid() )))* 100.0)+"%";
		return percentageOfPenAwaitingCalculation;
	}

	public String getPercentageOfEmpForSuspension()
	{
		if(this.getNoOfEmployeesProcessed() == 0 || this.getEmployeesNotPaidBySuspension() == 0)
			return zeroPercent;
		percentageOfEmpForSuspension = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getEmployeesNotPaidBySuspension()))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed() )))* 100.0)+"%";
		return percentageOfEmpForSuspension;
	}

	public String getPercentageOfEmpWithNegPay()
	{
		if(this.getNoOfEmployeesPaid() == 0 || this.getNoOfEmployeesWithNegativePay() == 0)
			return zeroPercent;
		percentageOfEmpWithNegPay = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getNoOfEmployeesWithNegativePay()))/
				new Double(String.valueOf(this.getNoOfEmployeesPaid() )))* 100.0)+"%";
		return percentageOfEmpWithNegPay;
	}

	public String getNegativePaySumStr()
	{
		negativePaySumStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getNegativePaySum());
		return negativePaySumStr;
	}

	public String getNetPayByDaysStr()
	{
		netPayByDaysStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getNetPayByDays());
		return netPayByDaysStr;
	}

	public String getSpecialAllowanceStr()
	{
		this.specialAllowanceStr = PayrollHRUtils.getDecimalFormat().format(this.getSpecialAllowance());
		return specialAllowanceStr;
	}

	public void setNegativePaySumStr(String pNegativePaySumStr)
	{
		negativePaySumStr = PayrollHRUtils.getDecimalFormat().format(this.getNegativePaySum());
		negativePaySumStr = pNegativePaySumStr;
	}


	public String getPercentageOfEmpPaidByDays()
	{
		if(this.getNoOfEmployeesPaid() == 0 || this.getNoOfEmployeesPaidByDays() == 0)
			return zeroPercent;
		percentageOfEmpPaidByDays = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getNoOfEmployeesPaidByDays()))/
				new Double(String.valueOf(this.getNoOfEmployeesPaid() )))* 100.0)+"%";
		return percentageOfEmpPaidByDays;
	}


	public String getPayByDaysPercentageOfGross()
	{
		if(this.getTotalPaySum() == 0 || this.getNetPayByDays() == 0)
			return zeroPercent;
		payByDaysPercentageOfGross = PayrollHRUtils.getDecimalFormat().format((new Double(this.getNetPayByDays())/
				new Double(this.getTotalPaySum()))* 100.0)+"%";
		return payByDaysPercentageOfGross;
	}

	public String getPercentageOfEmpPaidSpecAllow()
	{
		if(this.getNoOfEmployeesProcessed() == 0 || this.getNoOfEmpPaidSpecAllow() == 0)
			return zeroPercent;
		percentageOfEmpPaidSpecAllow = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getNoOfEmpPaidSpecAllow()))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed() )))* 100.0)+"%";
		return percentageOfEmpPaidSpecAllow;
	}

	public String getMonthAndYearStr()
	{ 
		monthAndYearStr = PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(this.getRunMonth(), this.getRunYear());
		return monthAndYearStr;
	}


	public String getContractTotalPayStr()
	{
		if(this.getContractTotalPay() > 0){
			contractTotalPayStr = PayrollHRUtils.getDecimalFormat().format(this.getContractTotalPay());
		}
		return contractTotalPayStr;
	}

	public String getContractEmpPercentageOfGross()
	{
		if(this.getTotalPaySum() == 0 || this.getTotalPayContract() == 0)
			return zeroPercent;
		contractEmpPercentageOfGross = PayrollHRUtils.getDecimalFormat().format((new Double(this.getTotalPayContract())/
				new Double(this.getTotalPaySum()))* 100.0)+"%";
		return contractEmpPercentageOfGross;
	}

	public String getContractNetPayStr()
	{
		if(this.getContractNetPay() > 0){
			this.contractNetPayStr = PayrollHRUtils.getDecimalFormat().format(this.getContractNetPay());
		}
		return contractNetPayStr;
	}

	
	public String getSuspendedTotalPayStr(){
		this.suspendedTotalPayStr = PayrollHRUtils.getDecimalFormat().format(this.getSuspendedTotalPay());
		return suspendedTotalPayStr;
	}


	public String getTotalPayByDaysStr()
	{
		this.totalPayByDaysStr = PayrollHRUtils.getDecimalFormat().format(this.getTotalPayByDays());
		return totalPayByDaysStr;
	}

	public String getSpecAllowNetPayStr()
	{
		specAllowNetPayStr = PayrollHRUtils.getDecimalFormat().format(this.getSpecAllowNetPay());
		return specAllowNetPayStr;
	}

	public String getSpecAllowTotalPayStr()
	{
		specAllowTotalPayStr = PayrollHRUtils.getDecimalFormat().format(this.getSpecAllowTotalPay());
		return specAllowTotalPayStr;
	}
 	public String getNetPaySumStr()
	{
		netPaySumStr = PayrollHRUtils.getDecimalFormat().format(this.getNetPaySum());
		return netPaySumStr;
	}

	public String getTotalPaySumStr()
	{
		totalPaySumStr = PayrollHRUtils.getDecimalFormat().format(this.getTotalPaySum());
		return totalPaySumStr;
	}

	public String getTotalDeductionSumStr()
	{
		totalDeductionSumStr = PayrollHRUtils.getDecimalFormat().format(this.getTotalDeductionSum());
		return totalDeductionSumStr;
	}


	public String getNetPayByBirthDateStr()
	{
		if(this.getNetPayByBirthDate() == 0.0D)
			netPayByBirthDateStr = this.getZeroNaira();
		else{
			netPayByBirthDateStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getNetPayByBirthDate());
		}
		return netPayByBirthDateStr;
	}

	public String getNetPayByHireDateStr()
	{
		if(this.getNetPayByHireDate() == 0.0D)
			netPayByHireDateStr = this.getZeroNaira();
		else{
			netPayByHireDateStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getNetPayByHireDate());
		}
		return netPayByHireDateStr;
	}

	public String getTotalPayByHireDateStr()
	{
		if(this.getTotalPayByHireDate() == 0.0D)
			totalPayByHireDateStr = this.getZeroNaira();
		else{
			totalPayByHireDateStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getTotalPayByHireDate());
		}
		return totalPayByHireDateStr;
	}

	public String getTotalPayByBirthDateStr()
	{
		if(this.getTotalPayByBirthDate() == 0.0D)
			totalPayByBirthDateStr = this.getZeroNaira();
		else{
			totalPayByBirthDateStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getTotalPayByBirthDate());
		}
		return totalPayByBirthDateStr;
	}


	public String getPercentageOfEmpPaidByContract()
	{
		if(this.getNoOfEmployeesProcessed() == 0 || this.getEmployeesPaidByContract() == 0)
			return zeroPercent;
		percentageOfEmpPaidByContract = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getEmployeesPaidByContract()))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed() )))* 100.0)+"%";
		return percentageOfEmpPaidByContract;
	}

	public String getTotalPayContractStr()
	{
		if(this.getTotalPayContract() == 0.0D)
			totalPayContractStr = this.getZeroNaira();
		else{
			totalPayContractStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getTotalPayContract());
		}
		return totalPayContractStr;
	}

	public String getNetPayContractStr()
	{
		if(this.getNetPayContract() == 0.0D)
			netPayContractStr = this.getZeroNaira();
		else{
			netPayContractStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getNetPayContract());
		}
		return netPayContractStr;
	}


	public String getNegativePayTotalSumStr()
	{
		if(this.getNegativePayTotalSum() == 0.0D)
			negativePayTotalSumStr = this.getZeroNaira();
		else{
			negativePayTotalSumStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getNegativePayTotalSum());
		}
		return negativePayTotalSumStr;
	}


	public String getMonthAndYearExcelStr()
	{
		
		monthAndYearExcelStr = PayrollBeanUtils.getMonthNameAndYearForExcelNaming(this.getRunMonth(), this.getRunYear(), true,false);
		return monthAndYearExcelStr;
	}

	public String getInterdictionEmpPercentageOfGross()
	{
		if(this.getTotalPaySum() == 0 || this.getTotalPayInterdiction() == 0)
			return zeroPercent;
		interdictionEmpPercentageOfGross = PayrollHRUtils.getDecimalFormat().format(new Double(this.getTotalPayInterdiction())/
				new Double(this.getTotalPaySum())* 100.0)+"%";
		 
		return interdictionEmpPercentageOfGross;
	}

	public String getNetPayInterdictionStr()
	{
		if(this.getNetPayInterdiction() == 0.0D)
			netPayInterdictionStr = this.getZeroNaira();
		else{
			netPayInterdictionStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getNetPayInterdiction());
		}
		return netPayInterdictionStr;
	}

	public String getTotalPayInterdictionStr()
	{
		if(this.getTotalPayInterdiction() == 0.0D)
			totalPayInterdictionStr = this.getZeroNaira();
		else{
			totalPayInterdictionStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.getTotalPayInterdiction());
		}
		return totalPayInterdictionStr;
	}

	public String getPercentageOfEmpPaidByInterdiction()
	{
		if(this.getNoOfEmployeesProcessed() == 0 || this.getNoOfEmpPaidByInterdiction() == 0)
			return zeroPercent;
		percentageOfEmpPaidByInterdiction = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getNoOfEmpPaidByInterdiction()))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed() )))* 100.0)+"%";
	 
		return percentageOfEmpPaidByInterdiction;
	}

	public String getPercentageOfEmpRetiredByTermination() {
		if(this.getNoOfEmployeesProcessed() == 0 || this.getNoOfEmpRetiredByTermination() == 0)
			return zeroPercent;
		percentageOfEmpRetiredByTermination = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getNoOfEmpRetiredByTermination()))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed() )))* 100.0)+"%";
		return percentageOfEmpRetiredByTermination;
	}

	public int getNoOfEmployeesNotPaid()
	{
		noOfEmployeesNotPaid = this.employeesNotPaidByBirthDate + this.employeesNotPaidByContract
								+ this.employeesNotPaidByHireDate + this.employeesNotPaidBySuspension
								+ this.employeesNotPaidByTermination + this.employeesNotPaidByApproval
								+ this.pensionersNotPaidByPensionCalc;
		return noOfEmployeesNotPaid;
	}



	public String getPercentageOfEmpNotApproved()
	{
		if(this.getNoOfEmployeesProcessed() == 0 || this.getNoOfEmpNotPaidByApproval() == 0)
			return zeroPercent;
		percentageOfEmpNotApproved = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.getNoOfEmpNotPaidByApproval()))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed()))) * 100.0)+"%";
		return percentageOfEmpNotApproved;
	}


	public String getContractEmpNotPaidPercentageOfGross()
	{
		if(this.getTotalPaySum() == 0 || this.getContractTotalPay() == 0)
			return zeroPercent;
		contractEmpNotPaidPercentageOfGross = PayrollHRUtils.getDecimalFormat().format((new Double(this.getNoOfEmpNotPaidByContract())/
				new Double(this.getTotalPaySum()))* 100.0)+"%";
		return contractEmpNotPaidPercentageOfGross;
	
		  
	}

	public String getPercentageOfEmpNotPaidByContract()
	{
		if(this.getNoOfEmployeesProcessed() == 0 || this.getNoOfEmpNotPaidByApproval() == 0)
			return zeroPercent;
		percentageOfEmpNotPaidByContract = PayrollHRUtils.getDecimalFormat().format((new Double(String.valueOf(this.employeesNotPaidByContract))/
				new Double(String.valueOf(this.getNoOfEmployeesProcessed()))) * 100.0)+"%";
		return percentageOfEmpNotPaidByContract;
	}


}