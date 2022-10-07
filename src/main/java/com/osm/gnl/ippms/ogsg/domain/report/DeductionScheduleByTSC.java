package com.osm.gnl.ippms.ogsg.domain.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class DeductionScheduleByTSC 
{
	public String agencyName;
    public String schoolName;
    public String deductionName;
    public String employeeName;
    public String employeeNumber;
    public Double deductionAmount;
    public LocalDate period;
    
    public DeductionScheduleByTSC()
    {    }
    
	public DeductionScheduleByTSC(String pAgencyName, String pSchoolName,
			String pDeductionName, String pEmployeeName, String pEmployeeNumber, Double pDeductionAmount, LocalDate pPeriod) {
		agencyName = pAgencyName;
		schoolName = pSchoolName;
		deductionName = pDeductionName;
		employeeName = pEmployeeName;
		employeeNumber = pEmployeeNumber;
		deductionAmount = pDeductionAmount;
		period = pPeriod;
	}
    
    
}
