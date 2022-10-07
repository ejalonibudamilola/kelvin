package com.osm.gnl.ippms.ogsg.payslip.beans;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.Date;


@Getter
@Setter
public class MDAPPaySlipSummaryBean extends NamedEntity
{
  private static final long serialVersionUID = -3755040559083205576L;
  private int noOfEmployees;
  private double totalPay;
  private String totalPayStr;
  private String printChecks;
  private String payPeriod;
  private String payDateStr;
  private Date payDate;
  private Date payPeriodStart;
  private Date payPeriodEnd;
  private String checkStatus;
  private String displayStyle;
  private int objectInd;
  private DecimalFormat df;

  public MDAPPaySlipSummaryBean()
  {
    this.df = new DecimalFormat("#,##0.00");
  }


  public String getTotalPayStr()
  {
    this.totalPayStr = (IConstants.naira + this.df.format(getTotalPay()));
    return this.totalPayStr;
  }


}