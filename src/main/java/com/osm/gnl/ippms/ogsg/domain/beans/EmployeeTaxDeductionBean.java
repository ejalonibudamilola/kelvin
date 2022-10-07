package com.osm.gnl.ippms.ogsg.domain.beans;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class EmployeeTaxDeductionBean 
{
	private String employeeName;
	private String employeeId;
	private String agency;
	private String totalMoney;
	private LocalDate payPeriod;
	private Double amount;
	
	public EmployeeTaxDeductionBean()
	{
		
	}
	
	public EmployeeTaxDeductionBean(String pEmployeeName, String pEmployeeId,
			String pAgency, String pTotalMoney, LocalDate pPayPeriod, Double pAmount) {
		super();
		employeeName = pEmployeeName;
		employeeId = pEmployeeId;
		agency = pAgency;
		totalMoney = pTotalMoney;
		payPeriod = pPayPeriod;
		amount = pAmount;
	}

	
	
}
