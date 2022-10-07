/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payroll;


import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeletePayrollBean extends NamedEntity {

	private String runMonthYearStr;
	private int totalEmpProcessed;
	private int totalEmpPaid;
	private int totalEmpRetired;
	private int totalEmpNotPaid;
	private String totalPayStr;
	private String totalDeductionsStr;
	private String specialAllowanceStr;
	private String monthlyTaxStr;
	private String totalGarnishmentsStr;
	private String netPayStr;
	private int totalEmpWithNegPay;
	private boolean deleteWarningIssued;
    private String generatedCaptcha;
    private String enteredCaptcha;
    private boolean captchaError;

}
