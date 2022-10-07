/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.allowance;

import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import lombok.Data;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.*;

@Data
public class LeaveTransportGrantHolder extends NamedEntity
{
  private static final long serialVersionUID = 3927576399229783908L;
  private int totalAssigned;
  private int totalUnassigned;
  private int totalNoOfEmp;
  private String showSelectionList;
  private String showMonthList;
  private String showNameRow;
  private String currentParentType;
  private String showSelectTypeList;
  
  private List<MdaInfo> unAssignedObjects = LazyList.decorate(new ArrayList<MdaInfo>(), FactoryUtils.instantiateFactory(MdaInfo.class));

  private List<AbmpBean> assignedMBAPList = LazyList.decorate(new ArrayList<AbmpBean>(), FactoryUtils.instantiateFactory(AbmpBean.class));
 
  private List<MdaInfo> mdaList;
  
  private MdaInfo mdaInfo;
  
  private List<?> fromPayGroupList;
  private List<?> toPayGroupList;
  private List<NamedEntityBean> monthList;
  private int runYear;
  private int monthId;
  private double totalBasicSalary;
  private double totalLtgCost;
  private double totalNetIncrease;
  private String totalBasicSalaryStr;
  private String totalLtgCostStr;
  private String totalNetIncreaseStr;
  private int objectTypeInd;
  private int objectId;
  private String messageString;
  private String monthToApply;
  private String yearToApply;
  private boolean hasDataForDisplay;
  private boolean forSimulation;
  private boolean warningIssued;
  private boolean forPayrollRun;
  private boolean payrollRunWarning;
  private boolean showConfirmation;
  private String ltgInstructionName;
  private DecimalFormat df;
  private boolean showCloseButton;

  public LeaveTransportGrantHolder()
  {
    this.df = new DecimalFormat("#,##0.00");
  }

  public String getShowSelectionList()
  {
    if ((isForPayrollRun()) || (isForSimulation()))
      this.showSelectionList = HIDE_ROW;
    return this.showSelectionList;
  }

  public String getTotalBasicSalaryStr()
  {
    if (getTotalBasicSalary() > 0.0D)
      this.totalBasicSalaryStr = (naira + this.df.format(getTotalBasicSalary()));
    else
      totalBasicSalaryStr = null;
    return this.totalBasicSalaryStr;
  }

  public String getTotalLtgCostStr()
  {
    if (getTotalLtgCost() > 0.0D)
      this.totalLtgCostStr = (naira + this.df.format(getTotalLtgCost()));
    else
      totalLtgCostStr= null;
    return this.totalLtgCostStr;
  }

  public String getTotalNetIncreaseStr()
  {
    if (getTotalNetIncrease() > 0.0D)
      this.totalNetIncreaseStr = (naira + this.df.format(getTotalNetIncrease()));
    else
      totalNetIncreaseStr = null;
    return this.totalNetIncreaseStr;
  }

  public boolean isHasDataForDisplay()
  {
    if ((getAssignedMBAPList() != null) && (!getAssignedMBAPList().isEmpty()))
      this.hasDataForDisplay = true;
    return this.hasDataForDisplay;
  }

  public String getShowSelectTypeList()
  {
    if ((this.forPayrollRun) || (this.forSimulation))
      this.showSelectTypeList = HIDE_ROW;
    else
      this.showSelectTypeList = SHOW_ROW;
    return this.showSelectTypeList;
  }

}