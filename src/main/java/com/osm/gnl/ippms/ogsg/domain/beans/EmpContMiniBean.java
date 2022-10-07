package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Setter
@Getter
public class EmpContMiniBean extends NamedEntity
{
  private double yearToDate;
  private double currentContribution;
  private String yearToDateStr;
  private String currentContributionStr;
  private DecimalFormat df;
  private double netPay;
  private double monthlyBasic;
  private String netPayStr;
  private String monthlyBasicStr;
  private double monthlyPension;
  private String monthlyPensionStr;

//  public EmpContMiniBean()
//  {
//    this.df = new DecimalFormat("#,##0.00");
//  }


  public String getYearToDateStr() {
    this.yearToDateStr = PayrollHRUtils.getDecimalFormat().format(this.yearToDate);
    return this.yearToDateStr;
  }

  public String getCurrentContributionStr() {
    this.currentContributionStr = PayrollHRUtils.getDecimalFormat().format(this.currentContribution);
    return this.currentContributionStr;
  }


  public String getMonthlyBasicStr() {
    this.monthlyBasicStr = PayrollHRUtils.getDecimalFormat().format(this.monthlyBasic);
    return monthlyBasicStr;
  }
  public String getMonthlyPensionStr() {
    this.monthlyPensionStr = PayrollHRUtils.getDecimalFormat().format(this.monthlyPension);
    return monthlyPensionStr;
  }

public String getNetPayStr()
{
	this.netPayStr = PayrollHRUtils.getDecimalFormat().format(this.netPay);
	return netPayStr;
}
}