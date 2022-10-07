package com.osm.gnl.ippms.ogsg.report.beans;

import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationInfoSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationMiniBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Getter
@Setter
public class WageBeanContainer extends WageSummaryBean
{
  private static final long serialVersionUID = -1679552515118665347L;
  private List<WageSummaryBean> wageSummaryBeanList;
  private List<WageSummaryBean> subventionList;
  private List<WageSummaryBean> deductionList;
  private List<WageSummaryBean> contributionList;
  private List<WageSummaryBean> headerList;
  private List<WageSummaryBean> summaryBean;
  private List<NamedEntityBean> namedEntityBeanList;
  private List<WageBeanContainer> beanList;
  private List<List<VariationReportBean>> variationList;
  private HashMap<String,List<EmployeePayBean>> paySummaryDetailsMap;
  private int totalNoOfEmp;
  private int totalNoOfPrevMonthEmp;
  private int totalEmpDiff;
  private double totalSubBal;
  private double totalPrevSubBal;
  private double totalSubBalDiff;
  private String totalSubBalDiffStr;
  private String totalPrevSubBalStr;
  private String totalSubBalStr;
  private double totalPrevBal;
  private double totalCurrBal;
  private double totalCurrBalDiff;
  private String totalCurrBalDiffStr;
  private double totalDedBal;
  private double totalPrevDedBal;
  private double totalDedDiff;
  private double grandTotal;
  private double grandPrevTotal;
  private double totalCurrCont;
  private double totalPrevCont;
  private double totalCurrDiff;
  private double totalCurrOutGoing;
  private double totalPrevOutGoing;
  private double totalOutGoing;
  private String totalDedDiffStr;
  private String totalOutGoingStr;
  private String totalCurrDiffStr;
  private String totalCurrOutGoingStr;
  private String totalPrevOutGoingStr;
  private String totalCurrContStr;
  private String totalPrevContStr;
  private String grandPrevTotalStr;
  private String totalPrevDedBalStr;
  private String grandTotalStr;
  private String grandTotalDiffStr;
  private String totalDedBalStr;
  private String totalPrevBalStr;
  private String totalCurrBalStr;
  private String monthAndYearStr;
  private String prevMonthAndYearStr;
  private LocalDate fromDate;
  private LocalDate toDate;
  private String fromDateStr;
  private String toDateStr;
  private int totalNoOfItems;
  private int totalNoOfStaffs;
  private double totalBasicSalary;
  private String totalBasicSalaryStr;
  private String totalBasicSalaryAsStr;
  private double totalAllowances;
  private String totalAllowancesStr;
  private double totalGrossAmount;
  private String totalGrossAmountStr;
  private double totalPaye;
  private String totalPayeStr;
  private double totalOtherDeductions;
  private String totalOtherDeductionsStr;
  private double totalDeductions;
  private String totalDeductionsStr;
  private double totalNetPay;
  private String totalNetPayStr;
  private String totalNetPayAsStr;
  private boolean showUnionDues;
  private String politicoSalary;
  private String politicoTax;
  private int month;
  private int year;
  private double totalRent;
  private String totalRentStr;
  private double totalTransport;
  private String totalTransportStr;
  private double totalMeal;
  private String totalMealStr;
  private double totalUtility;
  private String totalUtilityStr;
  private double totalInducement;
  private String totalInducementStr;
  private double totalRuralPosting;
  private String totalRuralPostingStr;
  private double totalMedicals;
  private String totalMedicalsStr;
  private double totalHazard;
  private String totalHazardStr;
  private double totalHardship;
  private String totalHardshipStr;
  private double totalCallDuty;
  private String totalCallDutyStr;
  private double totalJournal;
  private String totalJournalStr;
  private double totalDomServ;
  private String totalDomServStr;
  private double totalEntAllow;
  private String totalEntAllowStr;
  private double totalAccAllow;
  private String totalAccAllowStr;
  private double totalOutfit;
  private String totalOutfitStr;
  private double totalTorchLight;
  private String totalTorchLightStr;
  private double totalTss;
  private String totalTssStr;
  private double totalGrossSalary;
  private String totalGrossSalaryStr;
  private double totalTws;
  private String totalTwsStr;
  private double totalUnionDues;
  private String totalUnionDuesStr;
  private double totalNhf;
  private String totalNhfStr;
  private double totalPAYE;
  private String totalPAYEStr;
  private double totalAdminAllowance;
  private String totalAdminAllowanceStr;
  private double totalDriversAllowance;
  private String totalDriversAllowanceStr;
  private double totalFurniture;
  private String totalFurnitureStr;
  private double totalConsolidated;
  private String totalConsolidatedStr;
  private double totalSecurity;
  private String totalSecurityStr;
  private double totalOtherAllowances;
  private String totalOtherAllowancesStr;
  private String totalDeductionsAsStr;
  private String excelUrl;
  private String payInd;
  private List<EmployeePayBean> employeePayBeanList;
  private boolean showMda;
  private boolean usingSalaryType;
  private int showDiffInd;
  private boolean showDiff;
  private Long businessClientInstId;


  public String getTotalPrevBalStr() {
    this.totalPrevBalStr = (naira + this.df.format(this.totalPrevBal));
    return this.totalPrevBalStr;
  }

  public String getTotalCurrBalStr() {
    this.totalCurrBalStr = (naira + this.df.format(this.totalCurrBal));
    return this.totalCurrBalStr;
  }

  public String getTotalSubBalStr() {
    this.totalSubBalStr = (naira + this.df.format(this.totalSubBal));
    return this.totalSubBalStr;
  }
  public String getGrandTotalStr() {
    this.grandTotalStr = (naira + this.df.format(this.grandTotal));
    return this.grandTotalStr;
  }
 public String getTotalDedBalStr() {
    this.totalDedBalStr = (naira + this.df.format(this.totalDedBal));
    return this.totalDedBalStr;
  }
public String getTotalPrevDedBalStr() {
    this.totalPrevDedBalStr = (naira + this.df.format(this.totalPrevDedBal));
    return this.totalPrevDedBalStr;
  }
  public String getTotalPrevSubBalStr() {
    this.totalPrevSubBalStr = (naira + this.df.format(this.totalPrevSubBal));
    return this.totalPrevSubBalStr;
  }
  public String getGrandPrevTotalStr() {
    this.grandPrevTotalStr = (naira + this.df.format(this.grandPrevTotal));
    return this.grandPrevTotalStr;
  }

  public int getTotalEmpDiff()
  {
    this.totalEmpDiff = (this.totalNoOfEmp - this.totalNoOfPrevMonthEmp);

    return this.totalEmpDiff;
  }

  public String getTotalBasicSalaryStr()
  {
    this.totalBasicSalaryStr = (naira + this.df.format(this.totalBasicSalary));
    return this.totalBasicSalaryStr;
  }

  public String getTotalAllowancesStr()
  {
    this.totalAllowancesStr = (naira + this.df.format(this.totalAllowances));
    return this.totalAllowancesStr;
  }
  public String getTotalGrossAmountStr()
  {
    this.totalGrossAmountStr = (naira + this.df.format(this.totalGrossAmount));
    return this.totalGrossAmountStr;
  }

  public String getTotalPayeStr()
  {
    this.totalPayeStr = (naira + this.df.format(this.totalPaye));
    return this.totalPayeStr;
  }
  public String getTotalOtherDeductionsStr()
  {
    this.totalOtherDeductionsStr = (naira + this.df.format(this.totalOtherDeductions));
    return this.totalOtherDeductionsStr;
  }

  public String getTotalDeductionsStr()
  {
    this.totalDeductionsStr = (naira + this.df.format(this.totalDeductions));
    return this.totalDeductionsStr;
  }

  public String getTotalNetPayStr()
  {
    this.totalNetPayStr = (naira + this.df.format(this.totalNetPay));
    return this.totalNetPayStr;
  }

  public String getTotalRentStr()
  {
    this.totalRentStr = this.df.format(this.totalRent);
    return this.totalRentStr;
  }

  public double getTotalTransport()
  {
    return this.totalTransport;
  }

  public String getTotalTransportStr()
  {
    this.totalTransportStr = this.df.format(this.totalTransport);
    return this.totalTransportStr;
  }

  public double getTotalMeal()
  {
    return this.totalMeal;
  }

  public String getTotalMealStr()
  {
    this.totalMealStr = this.df.format(this.totalMeal);
    return this.totalMealStr;
  }

  public String getTotalUtilityStr()
  {
    this.totalUtilityStr = this.df.format(this.totalUtility);
    return this.totalUtilityStr;
  }

  public String getTotalInducementStr()
  {
    this.totalInducementStr = this.df.format(this.totalInducement);
    return this.totalInducementStr;
  }

  public String getTotalRuralPostingStr()
  {
    this.totalRuralPostingStr = this.df.format(this.ruralPosting);
    return this.totalRuralPostingStr;
  }

  public String getTotalMedicalsStr()
  {
    this.totalMedicalsStr = this.df.format(this.totalMedicals);
    return this.totalMedicalsStr;
  }

  public String getTotalHazardStr()
  {
    this.totalHazardStr = this.df.format(this.totalNetPay);
    return this.totalHazardStr;
  }

  public String getTotalHardshipStr()
  {
    this.totalHardshipStr = this.df.format(this.totalHardship);
    return this.totalHardshipStr;
  }

  public String getTotalCallDutyStr()
  {
    this.totalCallDutyStr = this.df.format(this.totalCallDuty);
    return this.totalCallDutyStr;
  }

  public String getTotalJournalStr()
  {
    this.totalJournalStr = this.df.format(this.totalJournal);
    return this.totalJournalStr;
  }
  public String getTotalDomServStr()
  {
    this.totalDomServStr = this.df.format(this.totalDomServ);
    return this.totalDomServStr;
  }

  public String getTotalEntAllowStr()
  {
    this.totalEntAllowStr = this.df.format(this.totalEntAllow);
    return this.totalEntAllowStr;
  }

  public String getTotalAccAllowStr()
  {
    this.totalAccAllowStr = this.df.format(this.totalAccAllow);
    return this.totalAccAllowStr;
  }

  public String getTotalOutfitStr()
  {
    this.totalOutfitStr = this.df.format(this.totalOutfit);
    return this.totalOutfitStr;
  }

  public String getTotalTorchLightStr()
  {
    this.totalTorchLightStr = this.df.format(this.totalTorchLight);
    return this.totalTorchLightStr;
  }

  public String getTotalTssStr()
  {
    this.totalTssStr = this.df.format(this.totalTss);
    return this.totalTssStr;
  }

  public String getTotalGrossSalaryStr()
  {
    this.totalGrossSalaryStr = this.df.format(this.totalGrossSalary);
    return this.totalGrossSalaryStr;
  }

  public String getTotalTwsStr()
  {
    this.totalTwsStr = this.df.format(this.totalTws);
    return this.totalTwsStr;
  }

  public String getTotalUnionDuesStr()
  {
    this.totalUnionDuesStr = this.df.format(this.totalUnionDues);
    return this.totalUnionDuesStr;
  }

  public String getTotalNhfStr()
  {
    this.totalNhfStr = this.df.format(this.totalNhf);
    return this.totalNhfStr;
  }

  public String getTotalPAYEStr()
  {
    this.totalPAYEStr = this.df.format(this.totalPAYE);
    return this.totalPAYEStr;
  }


  public String getTotalAdminAllowanceStr()
  {
    this.totalAdminAllowanceStr = this.df.format(this.totalAdminAllowance);
    return this.totalAdminAllowanceStr;
  }

  public String getTotalDriversAllowanceStr()
  {
    this.totalDriversAllowanceStr = this.df.format(this.totalDriversAllowance);
    return this.totalDriversAllowanceStr;
  }

  public String getTotalFurnitureStr()
  {
    this.totalFurnitureStr = this.df.format( this.totalFurniture);
    return this.totalFurnitureStr;
  }

  public String getTotalConsolidatedStr()
  {
    this.totalConsolidatedStr = this.df.format(this.totalConsolidated);
    return this.totalConsolidatedStr;
  }

  public String getTotalSecurityStr()
  {
    this.totalSecurityStr = this.df.format(this.totalSecurity);
    return this.totalSecurityStr;
  }
   public String getTotalOtherAllowancesStr()
  {
    this.totalOtherAllowancesStr = this.df.format(this.totalOtherAllowances);
    return this.totalOtherAllowancesStr;
  }

  public String getTotalBasicSalaryAsStr()
  {
    this.totalBasicSalaryAsStr = this.df.format(this.totalBasicSalary);
    return this.totalBasicSalaryAsStr;
  }

  public String getTotalDeductionsAsStr()
  {
    this.totalDeductionsAsStr = this.df.format(this.totalDeductions);
    return this.totalDeductionsAsStr;
  }

  public String getTotalNetPayAsStr()
  {
    this.totalNetPayAsStr = this.df.format(this.totalNetPay);
    return this.totalNetPayAsStr;
  }


  public String getTotalCurrContStr()
  {
    this.totalCurrContStr = (naira + this.df.format(this.totalCurrCont));
    return this.totalCurrContStr;
  }

  public String getTotalPrevContStr()
  {
    this.totalPrevContStr = (naira + this.df.format(this.totalPrevCont));

    return this.totalPrevContStr;
  }

  public String getTotalCurrOutGoingStr()
  {
    this.totalCurrOutGoingStr = (naira + this.df.format(this.totalCurrOutGoing));
    return this.totalCurrOutGoingStr;
  }

  public String getTotalPrevOutGoingStr()
  {
    this.totalPrevOutGoingStr = (naira + this.df.format(totalPrevOutGoing));
    return this.totalPrevOutGoingStr;
  }


public double getTotalCurrDiff() {
	this.totalCurrDiff = this.totalCurrCont - this.totalPrevCont;
	return totalCurrDiff;
}


public String getTotalCurrDiffStr() {
	this.totalCurrDiffStr = naira + df.format(this.getTotalCurrDiff());
	return totalCurrDiffStr;
}

public double getTotalDedDiff() {
	this.totalDedDiff =  this.getTotalDedBal() - this.getTotalPrevDedBal();
	return totalDedDiff;
}

public String getTotalDedDiffStr() {
	this.totalDedDiffStr = naira + df.format(this.getTotalDedDiff());
	return totalDedDiffStr;
}

public String getGrandTotalDiffStr() {
	this.grandTotalDiffStr = naira + df.format(this.getGrandTotal() - this.getGrandPrevTotal());
	return grandTotalDiffStr;
}

public boolean isShowDiff() {
	this.showDiff = this.showDiffInd == 1;
	return showDiff;
}

public double getTotalCurrBalDiff() {
	this.totalCurrBalDiff = this.getTotalCurrBal() - this.getTotalPrevBal();
	return totalCurrBalDiff;
}

public String getTotalCurrBalDiffStr() {
	this.totalCurrBalDiffStr = this.naira + df.format(this.getTotalCurrBalDiff());
	return totalCurrBalDiffStr;
}

public double getTotalSubBalDiff() {
	this.totalSubBalDiff = this.getTotalSubBal() - this.getTotalPrevSubBal();
	return totalSubBalDiff;
}

public String getTotalSubBalDiffStr() {
	this.totalSubBalDiffStr = naira + df.format(this.getTotalSubBalDiff());
	return totalSubBalDiffStr;
}

public String getFromDateStr() {
    if (this.fromDate != null)
      fromDateStr = PayrollHRUtils.getFullDateFormat().format(this.fromDate);
    return fromDateStr;
  }

  public String getToDateStr() {
    if (this.toDate != null)
      toDateStr = PayrollHRUtils.getFullDateFormat().format(this.toDate);
    return toDateStr;
  }

}