package com.osm.gnl.ippms.ogsg.domain.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class SummaryPage 
{
	private Integer currStaffCount;
	private Integer prevStaffCount;
	private Integer staffCountDifference;
	
	private Double currBasicSalary;
	private Double prevBasicSalary;
	private Double basicSalaryDifference;
	
	private Double currTotalAllowance;
	private Double prevTotalAllowance;
	private Double totalAllowanceDifference;
	
	private Double currGrossSum;
	private Double prevGrossSum;
	private Double grossSumDifference;
		
	private Double currTaxSum;
	private Double prevTaxSum;
	private Double taxSumDifference;

	private Double currOtherDeductions;
	private Double prevOtherDeductions;
	private Double otherDeductionsDifference;
	
	private Double currTotalDeductions;
	private Double prevTotalDeductions;
	private Double totalDeductionsDifference;
	
	private Double currNetPay;
	private Double prevNetPay;
	private Double netPayDifference;
	
	private Double currOvertime;
	private Double prevOvertime;
	private Double overtimeDifference;
	
	private Double currSalaryIncrease;
	private Double prevSalaryIncrease;
	private Double salaryIncreaseDifference;
	
	private Double currLeaveAllowance;
	private Double prevLeaveAllowance;
	private Double leaveAllowanceDifference;
	
	private Double newStaffGross;
	private Double promotedStaffGrossDifference;
	private Double reassignedStaffGrossDifference;
	private Double reinstatedStaffGrossDifference;
	private Double terminatedStaffGrossDifference;
	private Double prevTerminatedStaffGrossDifference;
	private Double reabsorbedStaffGrossDifference;
	private Double suspendedStaffGrossDifference;
	
	private Double specAllowChange;
	private Double specAllowDecrease;
	private Double stepIncrementGrossDifference;
	
	private Double salaryIncrease;
	private Double salaryDecrease;
	
	private Double incrementSubTotal;
	private Double decrementSubTotal;
	
	int newStaffCount;
    int promotedEmpCount;
    int reassignedEmpCount;
    int reinstatedEmpCount;
    int reabsorbedEmpCount;
    int terminatedEmpCount;
    int prevTerminatedEmpCount;
    int suspendedEmpCount;
    int specAllowDiffCount;		
    int stepIncrementCount;	
    int specAllowDecreaseCount;	
	
	private LocalDate period;
	
	

	
	
	
}
