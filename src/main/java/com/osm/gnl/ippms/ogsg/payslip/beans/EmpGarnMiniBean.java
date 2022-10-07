package com.osm.gnl.ippms.ogsg.payslip.beans;

import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Data;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;

@Data
public class EmpGarnMiniBean extends NamedEntity
 
{
  private static final long serialVersionUID = -3265589615466363890L;
  private double yearToDate;
  private double currentGarnishment;
  private String yearToDateStr;
  private String currentGarnishmentStr;
  private DecimalFormat df;
  private LocalDate startDate;
  private LocalDate endDate;
  private String startDateStr;
  private String endDateStr;
  private int loanTerm;
  private int noOfMonths;
  private String salaryInfoDesc;
  private String employeeId;
  private MdaDeptMap mdaDeptMap;
  private String mdaName;
  private String garnishmentName;



  public EmpGarnMiniBean()
  {
    this.df = new DecimalFormat("#,##0.00");
  }


  public String getYearToDateStr()
  {
    this.yearToDateStr = this.df.format(this.yearToDate);
    return this.yearToDateStr;
  }


  public String getCurrentGarnishmentStr()
  {
    this.currentGarnishmentStr = this.df.format(this.currentGarnishment);
    return this.currentGarnishmentStr;
  }


}