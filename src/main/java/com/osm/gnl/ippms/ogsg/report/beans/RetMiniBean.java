package com.osm.gnl.ippms.ogsg.report.beans;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class RetMiniBean extends NamedEntity
{
  private static final long serialVersionUID = 589062367903051486L;
  private String employeeName;
  private double empDed;
  private double compCont;
  private double planTotal;
  private DecimalFormat df;
  private String empDedStr;
  private String compContStr;
  private String planTotalStr;
  private String displayStyle;
  /**
   * For Simulation...
   */
  private double averageTax;
  private double averageSpecAllow;
  private double averageDeductions;
  
  public RetMiniBean()
  {
    this.df = new DecimalFormat("#,##0.00");
  }


  public String getEmpDedStr()
  {
    this.empDedStr = this.df.format(getEmpDed());
    return this.empDedStr;
  }


  public String getCompContStr()
  {
    this.compContStr = this.df.format(getCompCont());
    return this.compContStr;
  }


  public String getPlanTotalStr()
  {
    this.planTotalStr = this.df.format(getPlanTotal());
    return this.planTotalStr;
  }


}