package com.osm.gnl.ippms.ogsg.payslip.beans;


import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpPayBean extends NamedEntity {

	private static final long serialVersionUID = -3044618250889563638L;
	private String employeeType;
	private String payEmployee;
	private String lastPayDate;
	private String employeeId;
	private double hoursWorked;
	private String basicSalaryStr;
	private double salaryYTD;
	private double currentTotalPay;
	private String levelStepStr;
	private String payType;
	private int reportInd;


}