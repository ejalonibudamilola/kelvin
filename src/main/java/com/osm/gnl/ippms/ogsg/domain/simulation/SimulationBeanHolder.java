package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Getter
@Setter
public class SimulationBeanHolder extends NamedEntity
{
  private static final long serialVersionUID = 3927576399229783908L;
 
  private List<AbmpBean> agencyList;
  private boolean applyLtg;
  private String applyLtgInd;
  private List<AbmpBean> assignedMBAPList = LazyList.decorate(new ArrayList<AbmpBean>(), FactoryUtils.instantiateFactory(AbmpBean.class));
  private List<NamedEntity> baseMonthList;
  private String baseYear;
  private int baseYearDevLevyInd;
  private List<AbmpBean> boardList;
  private boolean canDeductBaseYearDevLevy;
  private boolean canDeductSpillOverYearDevLevy;
  private boolean canDoStepIncrement;
  private String currentParentType;
  private String deductBaseYearDevLevy;
  private String deductSpillOverYearDevLevy;
  private DecimalFormat df;
  private int effectiveYear;
  private boolean forPayrollRun;
  private boolean forSimulation;
  private boolean hasDataForDisplay;
  private boolean includePromotion;
  private String includePromotionInd;
  private String ltgInstructionName;
  private String messageString;
  private List<AbmpBean> ministryList;
  private int monthId;
  private String monthToApply;
  private int noOfMonthsInd;
  private int objectId;
  private int objectTypeInd;
  private List<AbmpBean> parastatalList;
  private boolean payrollRunWarning;
  private PayrollSimulationMasterBean payrollSimulationMasterBean;
  private boolean showCloseButton;
  private boolean showConfirmation;
  private String showMonthList;
  private String showNameRow;
  private String showSelectionList;
  private String showSelectTypeList;
  private String simulationLength;
  private List<NamedEntity> spillOverMonthList;
  private String spillYear;
  private int spillYearDevLevyInd;
  private int startMonthInd;
  private String startMonthName;
  private boolean stepIncreaseInd;
  private String stepIncrementInd;
  private int totalAssigned;
  private double totalBasicSalary;
  private String totalBasicSalaryStr;
  private double totalLtgCost;
  private String totalLtgCostStr;
  private double totalNetIncrease;
  private String totalNetIncreaseStr;
  private int totalNoOfEmp;
  private int totalUnassigned;
  private HashMap<Long, Long> exemptLtgMap;
  private boolean canApplyLtg;
  private String includePromotionStr;
  private boolean deductDevLevyForBaseYear;
  private boolean deductDevLevyForSpillOverYear;
  private String deductBaseYearDevLevyStr;
  private String deductDevLevyForSpillOverYearStr;
  private int assignToMonthInd;
  private String objectCode;
  private int deductSpillYearDevLevy;
  private boolean editMode;
  private String applyToAllInd;
  private boolean cancelWarningIssued;
  private List<AbmpBean> unAssignedObjects = LazyList.decorate(new ArrayList<AbmpBean>(), FactoryUtils.instantiateFactory(AbmpBean.class));
  private boolean warningIssued;
  private String yearToApply;

  public SimulationBeanHolder()
  {
    this.df = new DecimalFormat("#,##0.00");
  }


  public String getBaseYear()
  {
    this.baseYear = PayrollBeanUtils.getYearAsString(LocalDate.now());
    return this.baseYear;
  }



  public String getShowSelectionList()
  {
    if ((isForPayrollRun()) || (isForSimulation()))
      this.showSelectionList = IConstants.HIDE_ROW;
    return this.showSelectionList;
  }

  public String getShowSelectTypeList()
  {
    if ((this.forPayrollRun) || (this.forSimulation))
      this.showSelectTypeList = IConstants.HIDE_ROW;
    else
      this.showSelectTypeList = IConstants.SHOW_ROW;
    return this.showSelectTypeList;
  }


  public String getSpillYear()
  {
    LocalDate wCal = LocalDate.now().plusYears(1L);

    this.spillYear = PayrollBeanUtils.getYearAsString(wCal);
    return this.spillYear;
  }



  public String getTotalBasicSalaryStr()
  {
    this.totalBasicSalaryStr = (IConstants.naira + this.df.format(getTotalBasicSalary()));
    return this.totalBasicSalaryStr;
  }


  public String getTotalLtgCostStr()
  {
    this.totalLtgCostStr = (IConstants.naira + this.df.format(getTotalLtgCost()));
    return this.totalLtgCostStr;
  }


  public String getTotalNetIncreaseStr()
  {
    this.totalNetIncreaseStr = (IConstants.naira + this.df.format(getTotalNetIncrease()));
    return this.totalNetIncreaseStr;
  }

  public boolean isApplyLtg()
  {
    if (Boolean.parseBoolean(this.applyLtgInd))
      this.applyLtg = true;
    return this.applyLtg;
  }



  public boolean isHasDataForDisplay()
  {
    if (!IppmsUtils.isNullOrEmpty(getAssignedMBAPList()))
      this.hasDataForDisplay = true;
    return this.hasDataForDisplay;
  }


  public boolean isCanApplyLtg()
  {
    if (!IppmsUtils.isNullOrEmpty(getUnAssignedObjects()) )
      this.canApplyLtg = true;

    return this.canApplyLtg;
  }

  public boolean isDeductDevLevyForBaseYear()
  {
    if (Boolean.parseBoolean(deductBaseYearDevLevy))
      this.deductDevLevyForBaseYear = true;
    return this.deductDevLevyForBaseYear;
  }


  public boolean isDeductDevLevyForSpillOverYear()
  {
    if (Boolean.parseBoolean(deductSpillOverYearDevLevy))
      this.deductDevLevyForSpillOverYear = true;
    return this.deductDevLevyForSpillOverYear;
  }


 
}