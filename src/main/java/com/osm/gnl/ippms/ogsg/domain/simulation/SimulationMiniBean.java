/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.Data;

import java.text.DecimalFormat;

@Data
public class SimulationMiniBean 
  implements Comparable<SimulationMiniBean>
{
  private Long id;
  private Integer integerId;
  private double currentValue;
  private String currentValueStr;
  private int comparator;
  private int yearInd;
  private DecimalFormat df;
  private boolean hasName;
  private String displayStyle;
  private Long parentKey;
  private String name;

  public SimulationMiniBean()
  {
    this.df = new DecimalFormat("#,##0.00");
  }


  public String getCurrentValueStr()
  {
    if (this.currentValue < 1.0D)
      this.currentValueStr = "₦0.00";
    else
      this.currentValueStr = (IConstants.naira + this.df.format(this.currentValue));
    return this.currentValueStr;
  }


  public boolean isHasName()
  {
    return IppmsUtils.isNotNullOrEmpty(this.name);
  }


  public int compareTo(SimulationMiniBean pO)
  {
    return getComparator() - pO.getComparator();
  }


}