/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.List;

@Data
public class SimulationInfoSummaryBean  
  implements Comparable<SimulationInfoSummaryBean>
{
  private Long id;
  private String name;
  private Integer parentKey;
  private int serialNum;
  private String assignedToObject;
  private double currentBalance;
  private int noOfEmp;
  private String currentBalanceStr;
  private boolean materialityThreshold;
  private int objectInd;
  private int mapId;
  private int monthInd;
  private int yearInd;
  private String monthAndYearStr;
  private List<SimulationMiniBean> miniBeanList;
  private String displayStyle;
  private DecimalFormat df;

  public SimulationInfoSummaryBean()
  {
    this.df = new DecimalFormat("#,##0.00");
  }



  public String getCurrentBalanceStr()
  {
    this.currentBalanceStr = (IConstants.naira + this.df.format(getCurrentBalance()));
    return this.currentBalanceStr;
  }

  public int compareTo(SimulationInfoSummaryBean pIncoming)
  {
    if ((getAssignedToObject() != null) && (pIncoming.getAssignedToObject() != null))
      return getAssignedToObject().compareToIgnoreCase(pIncoming.getAssignedToObject());
    return getId().intValue() - pIncoming.getId().intValue();
  }


}