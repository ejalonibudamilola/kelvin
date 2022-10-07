/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.deduction;

import java.text.DecimalFormat;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class DeductionMiniBean extends NamedEntity
{
  private static final long serialVersionUID = -5801936514411008560L;
  private int serialNum;
  private double amount;
  private double owedAmount;
  private double monthlyInterest;
  private String monthlyInsterestStr;
  private String owedAmountStr;
  private String amountStr;
  private DecimalFormat df;
  private String garnishmentType;



  public DeductionMiniBean()
  {
    this.df = new DecimalFormat("#,##0.00");
  }

  public String getAmountStr() {
    this.amountStr = (IConstants.naira + this.df.format(getAmount()));

    return this.amountStr;
  }
  public void setAmountStr(String pAmountStr) {
    this.amountStr = pAmountStr;
  }

  public int compareTo(DeductionMiniBean pIncoming) {
    if ((getName() != null) && (pIncoming.getName() != null))
      return getName().compareToIgnoreCase(pIncoming.getName());
    return 0;
  }

  public String getOwedAmountStr()
  {
    this.owedAmountStr = (IConstants.naira + this.df.format(getOwedAmount()));

    return this.owedAmountStr;
  }


  public String getMonthlyInsterestStr() {
    this.monthlyInsterestStr = (IConstants.naira + this.df.format(getMonthlyInterest()));
    return this.monthlyInsterestStr;
  }
}