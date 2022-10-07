/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.NamedEntityLong;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public class DeductGarnMiniBean extends NamedEntityLong  {

  /**
   *
   */
  private static final long serialVersionUID = 4651703305997478385L;
  private String description;
  private double amount;
  private double ownerOccAmount;
  private double carLoanAmount;
  private double otherGarnAmount;
  private double ownerOccAmountBalance;
  private double carLoanAmountBalance;
  private double otherGarnAmountBalance;
  private String ownerOccAmountBalanceStr;
  private String carLoanAmountBalanceStr;
  private String otherGarnAmountBalanceStr;
  private String ownerOccAmountStr;
  private String carLoanAmountStr;
  private String otherGarnAmountStr;
  private String amountStr;
  private double balanceAmount;
  private String balanceAmountStr;
  private double monthlyInterestTotal;
  private String monthlyInterestTotalStr;
  private DecimalFormat df;
  private String displayStyle;
  private String employeeId;
  private String type;
  private Long deductionId;
  private boolean usingPercentage;
  private boolean usingCurrency;
  private List<DeductionMiniBean> employees;
  private String accountNumber;
  private Double monthlyBasic = 0.0D;
  private String monthlyBasicStr;
  private Double arrears = 0.0D;
  private String arrearsStr;
  private Double otherArrears = 0.0D;
  private String OtherArrearsStr;

  //for apportioned deductions
  private Double firstAllotPercent;
  private Double secondAllotPercent;
  private Double thirdAllotPercent;
  private String firstAllotment;
  private Double firstAllotAmt;
  private String secondAllotment;
  private Double secondAllotAmt;
  private String thirdAllotment;
  private Double thirdAllotmentAmt;
  private Double firstAllotAmtTotal = 0.0D;
  private Double secondAllotAmtTotal = 0.0D;
  private Double thirdAllotAmtTotal = 0.0D;
  private boolean apportionedType;

  public DeductGarnMiniBean() {
    this.df = new DecimalFormat("#,##0.00");
  }


  public String getBalanceAmountStr() {
    this.balanceAmountStr = this.df.format(getBalanceAmount());
    return this.balanceAmountStr;
  }


  public String getOwnerOccAmountStr() {
    this.ownerOccAmountStr = this.df.format(getOwnerOccAmount());
    return this.ownerOccAmountStr;
  }

  public String getArrearsStr() {
    this.arrearsStr = this.df.format(getArrears());
    return this.arrearsStr;
  }

  public String getMonthlyBasicStr() {
    this.monthlyBasicStr = this.df.format(getMonthlyBasic());
    return this.monthlyBasicStr;
  }

  public String getOtherArrearsStr() {
    this.OtherArrearsStr = this.df.format(getOtherArrears());
    return this.OtherArrearsStr;
  }


  public String getCarLoanAmountStr() {
    this.carLoanAmountStr = this.df.format(getCarLoanAmount());
    return this.carLoanAmountStr;
  }

  public String getOtherGarnAmountStr() {
    this.otherGarnAmountStr = this.df.format(getOtherGarnAmount());
    return this.otherGarnAmountStr;
  }


  public String getOwnerOccAmountBalanceStr() {
    this.ownerOccAmountBalanceStr = this.df.format(getOwnerOccAmountBalance());
    return this.ownerOccAmountBalanceStr;
  }


  public String getCarLoanAmountBalanceStr() {
    this.carLoanAmountBalanceStr = this.df.format(getCarLoanAmountBalance());
    return this.carLoanAmountBalanceStr;
  }

  public String getAmountStr() {
    this.amountStr = this.df.format(getAmount());
    return this.amountStr;
  }


  public String getOtherGarnAmountBalanceStr() {
    this.otherGarnAmountBalanceStr = this.df.format(getOtherGarnAmountBalance());
    return this.otherGarnAmountBalanceStr;
  }


  public List<DeductionMiniBean> getEmployees() {
    if (this.employees == null)
      this.employees = new ArrayList<DeductionMiniBean>();
    return this.employees;
  }

  public String getMonthlyInterestTotalStr() {
    this.monthlyInterestTotalStr = this.df.format(getMonthlyInterestTotal());
    return this.monthlyInterestTotalStr;
  }

  public int compareTo(DeductGarnMiniBean pIncoming) {
    if ((getName() != null) && (pIncoming.getName() != null))
      return getName().compareToIgnoreCase(pIncoming.getName());
    return 0;
  }

}