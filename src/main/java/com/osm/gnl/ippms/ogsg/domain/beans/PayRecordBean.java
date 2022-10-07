package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter

public class PayRecordBean extends NamedEntity{
  private static final long serialVersionUID = 970588254968547374L;
  private String employeeName;
  private String employeeId;
  private String organization;
  private String birthDateStr;
  private String hireDateStr;
  private String salaryScaleLevelAndStep;
  private boolean gradeLevel;
  private boolean hattis;
  private int noOfYearsInService;
  private int ultimateYear;
  private int penultimateYear;
  private int baseYear;
  private List<PRCDisplayBeanHolder> baseYearBean;
  private List<PRCDisplayBeanHolder> penultimateBean;
  private List<PRCDisplayBeanHolder> ultimateBean;
  private List<PRCDeductionsBeanHolder> ultimateDeductions;
  private List<PRCDeductionsBeanHolder> penultimateDeductions;
  private List<PRCDeductionsBeanHolder> baseYearDeductions;
  private List<String> ultimateDeductionsNames;
  private List<String> penultimateDeductionsNames;
  private List<String> baseYearDeductionsNames;

  public List<String> getUltimateDeductionsNames(){
    if ((getUltimateDeductions() != null) && (!getUltimateDeductions().isEmpty())) {
      this.ultimateDeductionsNames = new ArrayList<String>();
      for (PRCDeductionsBeanHolder p : getUltimateDeductions())
        this.ultimateDeductionsNames.add(p.getDeductionBean().getName());
    }
    else
    {
      this.ultimateDeductionsNames = new ArrayList<String>();
    }
    return this.ultimateDeductionsNames;
  }

  public List<String> getPenultimateDeductionsNames(){
    if ((getPenultimateDeductions() != null) && (!getPenultimateDeductions().isEmpty())) {
      this.penultimateDeductionsNames = new ArrayList<String>();
      for (PRCDeductionsBeanHolder p : getPenultimateDeductions())
        this.penultimateDeductionsNames.add(p.getDeductionBean().getName());
    }
    else
    {
      this.penultimateDeductionsNames = new ArrayList<String>();
    }

    return this.penultimateDeductionsNames;
  }

  public List<String> getBaseYearDeductionsNames(){
    if ((getBaseYearDeductions() != null) && (!getBaseYearDeductions().isEmpty())) {
      this.baseYearDeductionsNames = new ArrayList<String>();
      for (PRCDeductionsBeanHolder p : getBaseYearDeductions())
        this.baseYearDeductionsNames.add(p.getDeductionBean().getName());
    }
    else
    {
      this.baseYearDeductionsNames = new ArrayList<String>();
    }

    return this.baseYearDeductionsNames;
  }
}