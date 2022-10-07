/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.deduction;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

import com.osm.gnl.ippms.ogsg.abstractentities.DependentEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class DeductionDetailsBean extends DependentEntity
{
  private static final long serialVersionUID = 4612829785439081036L;
  private LocalDate fromDate;
  private LocalDate toDate;
  private String fromDateStr;
  private String toDateStr;
  private Long deductionId;
  private double total;
  private String totalStr;
  private double totalCarLoan;
  private double totalOwnerOccupier;
  private double totalOtherGarn;
  private String totalCarLoanStr;
  private String totalOwnerOccupierStr;
  private String totalOtherGarnStr;
  private double totalBalance;
  private String totalBalanceStr;
  private List<DeductGarnMiniBean> deductionMiniBean;
  private List<EmpDeductMiniBean> pensionContributionBean;
  private String currentDeduction;
  private String companyName;
  private String amountInWords;
  private String subLinkSelect;
  private DecimalFormat df;
  private String payPeriod;
  private String empId;
  private int noOfEmployees;
  private String totalCurrentDeductionStr;
  private double totalCurrentDeduction;
  private String monthAndYearStr;
  private Double totalGross;
  private String totalGrossStr;
  private Double netPay;
  private String netPayStr;
  private Double firstAllotAmtTotal = 0.0D;
  private Double secondAllotAmtTotal = 0.0D;
  private Double thirdAllotAmtTotal = 0.0D;

  public DeductionDetailsBean()
  {
    this.df = new DecimalFormat("#,##0.00");
  }


  public String getTotalStr() {
    this.totalStr = this.df.format(getTotal());
    return this.totalStr;
  }

  public void setTotalStr(String pTotalStr) {
    this.totalStr = pTotalStr;
  }


  public String getTotalCarLoanStr() {
    this.totalCarLoanStr = this.df.format(getTotalCarLoan());
    return this.totalCarLoanStr;
  }

  public String getTotalOwnerOccupierStr() {
    this.totalOwnerOccupierStr = this.df.format(getTotalOwnerOccupier());
    return this.totalOwnerOccupierStr;
  }


  public String getTotalOtherGarnStr() {
    this.totalOtherGarnStr = this.df.format(getTotalOtherGarn());
    return this.totalOtherGarnStr;
  }

  public String getNetPayStr() {
    this.netPayStr = this.df.format(getNetPay());
    return this.netPayStr;
  }

  public String getTotalGrossStr() {
    this.totalGrossStr = this.df.format(getTotalGross());
    return this.totalGrossStr;
  }

  public String getTotalBalanceStr() {
    this.totalBalanceStr = this.df.format(getTotalBalance());
    return this.totalBalanceStr;
  }


public int getNoOfEmployees()
{
	return noOfEmployees;
}

public String getTotalCurrentDeductionStr()
{
 totalCurrentDeductionStr = IConstants.naira +this.df.format(this.getTotalCurrentDeduction());
	return totalCurrentDeductionStr;
}

public int compareTo(DeductionDetailsBean pArg0)
{
	if(this.getName() != null && pArg0.getName() != null)
		return this.getName().compareToIgnoreCase(pArg0.getName());
	return 0;
}
}