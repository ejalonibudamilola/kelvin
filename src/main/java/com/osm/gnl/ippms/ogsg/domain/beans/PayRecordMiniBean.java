package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.text.DecimalFormat;


@Getter
@Setter

public class PayRecordMiniBean extends NamedEntity
{
  private static final long serialVersionUID = 8507506219963185478L;
  private String monthAndYearStr;
  private double basic;
  private String basicStr;
  private double rent;
  private String rentStr;
  private double transport;
  private String transportStr;
  private double meal;
  private String mealStr;
  private double utility;
  private String utilityStr;
  private double furniture;
  private String furnitureStr;
  private double domesticServant;
  private String domesticServantStr;
  private double entertainment;
  private String entertainmentStr;
  private double hazard;
  private String hazardStr;
  private double callDuty;
  private String callDutyStr;
  private double journal;
  private String journalStr;
  private double academicAllowance;
  private String academicAllowanceStr;
  private double ruralPosting;
  private String ruralPostingStr;
  private double unionDues;
  private String unionDuesStr;
  private double nhf;
  private String nhfStr;
  private double paye;
  private String payeStr;
  private double tws;
  private String twsStr;
  private double otherDeductions;
  private String otherDeductionsStr;
  private double otherContributions;
  private String otherContributionsStr;
  private double netPay;
  private String netPayStr;
  private double principalAllowance;
  private String principalAllowanceStr;
  private double tss;
  private String tssStr;
  private boolean gradeLevel;
  private DecimalFormat df;
  public final String naira = "\u20A6";

  public PayRecordMiniBean()
  {
    this.df = new DecimalFormat("#,##0.00");
  }


  public String getBasicStr(){
    if (getBasic() > 0.0D)
      this.basicStr = (naira + this.df.format(getBasic()));
    else {
      this.basicStr = "₦0.00";
    }
    return this.basicStr;
  }


  public String getRentStr(){
    if (getRent() > 0.0D)
      this.rentStr = (naira + this.df.format(getRent()));
    else
      this.rentStr = "₦0.00";
    return this.rentStr;
  }


  public String getTransportStr(){
    if (getTransport() > 0.0D)
      this.transportStr = (naira + this.df.format(getTransport()));
    else {
      this.transportStr = "₦0.00";
    }
    return this.transportStr;
  }

  public String getMealStr(){
    if (getRent() > 0.0D)
      this.mealStr = (naira + this.df.format(getMeal()));
    else {
      this.mealStr = "₦0.00";
    }
    return this.mealStr;
  }

  public String getUtilityStr(){
    if (getUtility() > 0.0D)
      this.utilityStr = (naira + this.df.format(getUtility()));
    else {
      this.utilityStr = "₦0.00";
    }
    return this.utilityStr;
  }

  public String getFurnitureStr(){
    if (getFurniture() > 0.0D)
      this.furnitureStr = (naira + this.df.format(getFurniture()));
    else {
      this.furnitureStr = "₦0.00";
    }
    return this.furnitureStr;
  }

  public String getDomesticServantStr(){
    if (getDomesticServant() > 0.0D)
      this.domesticServantStr = (naira + this.df.format(getDomesticServant()));
    else {
      this.domesticServantStr = "₦0.00";
    }
    return this.domesticServantStr;
  }

  public String getEntertainmentStr(){
    if (getEntertainment() > 0.0D)
      this.entertainmentStr = (naira + this.df.format(getEntertainment()));
    else {
      this.entertainmentStr = "₦0.00";
    }
    return this.entertainmentStr;
  }

  public String getHazardStr(){
    if (getHazard() > 0.0D)
      this.hazardStr = (naira + this.df.format(getHazard()));
    else {
      this.hazardStr = "₦0.00";
    }
    return this.hazardStr;
  }

  public String getCallDutyStr(){
    if (getCallDuty() > 0.0D)
      this.callDutyStr = (naira + this.df.format(getCallDuty()));
    else {
      this.callDutyStr = "₦0.00";
    }
    return this.callDutyStr;
  }

  public String getJournalStr(){
    if (getJournal() > 0.0D)
      this.journalStr = (naira + this.df.format(getJournal()));
    else {
      this.journalStr = "₦0.00";
    }
    return this.journalStr;
  }

  public String getAcademicAllowanceStr(){
    if (getAcademicAllowance() > 0.0D)
      this.academicAllowanceStr = (naira + this.df.format(getAcademicAllowance()));
    else {
      this.academicAllowanceStr = "₦0.00";
    }
    return this.academicAllowanceStr;
  }

  public String getRuralPostingStr(){
    if (getRuralPosting() > 0.0D)
      this.ruralPostingStr = (naira + this.df.format(getRuralPosting()));
    else {
      this.ruralPostingStr = "₦0.00";
    }
    return this.ruralPostingStr;
  }

  public String getUnionDuesStr(){
    if (getUnionDues() > 0.0D)
      this.unionDuesStr = (naira + this.df.format(getUnionDues()));
    else {
      this.unionDuesStr = "₦0.00";
    }
    return this.unionDuesStr;
  }

  public String getNhfStr(){
    if (getNhf() > 0.0D)
      this.nhfStr = (naira + this.df.format(getNhf()));
    else {
      this.nhfStr = "₦0.00";
    }
    return this.nhfStr;
  }

  public String getPayeStr(){
    if (getPaye() > 0.0D)
      this.payeStr = (naira + this.df.format(getPaye()));
    else {
      this.payeStr = "₦0.00";
    }
    return this.payeStr;
  }

  public String getTwsStr(){
    if (getTws() > 0.0D)
      this.twsStr = (naira + this.df.format(getTws()));
    else {
      this.twsStr = "₦0.00";
    }
    return this.twsStr;
  }

  public String getOtherDeductionsStr(){
    if (getOtherDeductions() > 0.0D)
      this.otherDeductionsStr = (naira + this.df.format(getOtherDeductions()));
    else {
      this.otherDeductionsStr = "₦0.00";
    }
    return this.otherDeductionsStr;
  }

  public String getOtherContributionsStr(){
    if (getOtherContributions() > 0.0D)
      this.otherContributionsStr = (naira + this.df.format(getOtherContributions()));
    else {
      this.otherContributionsStr = "₦0.00";
    }
    return this.otherContributionsStr;
  }

  public String getNetPayStr(){
    if (getNetPay() > 0.0D)
      this.netPayStr = (naira + this.df.format(getNetPay()));
    else {
      this.netPayStr = "₦0.00";
    }
    return this.netPayStr;
  }

  public String getPrincipalAllowanceStr(){
    if (getPrincipalAllowance() > 0.0D)
      this.principalAllowanceStr = (naira + this.df.format(getPrincipalAllowance()));
    else {
      this.principalAllowanceStr = "₦0.00";
    }
    return this.principalAllowanceStr;
  }

  public String getTssStr(){
    if (getTss() > 0.0D)
      this.tssStr = (naira + this.df.format(getTss()));
    else
      this.tssStr = "₦0.00";
    return this.tssStr;
  }

}