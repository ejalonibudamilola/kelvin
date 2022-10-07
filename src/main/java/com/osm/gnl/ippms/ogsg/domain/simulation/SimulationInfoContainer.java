/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SimulationInfoContainer extends NamedEntity
{
  private List<SimulationMiniBean> mdapList;
  private List<SimulationMiniBean> headerList;
  private List<SimulationMiniBean> mdapFooterList;
  private List<SimulationInfoSummaryBean> deductionsList;
  private List<SimulationInfoSummaryBean> contributionsList;
  private List<SimulationMiniBean> footerList;
  private List<SimulationMiniBean> deductionsTotals;
  private List<SimulationMiniBean> contributionsTotals;
  private List<SimulationInfoSummaryBean> summaryBean;
  private LocalDate fromDate;
  private LocalDate toDate;
  private String fromDateStr;
  private String toDateStr;


}