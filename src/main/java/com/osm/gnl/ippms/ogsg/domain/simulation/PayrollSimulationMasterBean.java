/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;



@Entity
@Table(name = "ippms_pay_sim_master")
@SequenceGenerator(name = "paySimMasterSeq", sequenceName = "ippms_pay_sim_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayrollSimulationMasterBean  
  implements Comparable<PayrollSimulationMasterBean>, Serializable
{
  

private static final long serialVersionUID = -5387409921775836481L;
  
  @Id
  @GeneratedValue(generator = "paySimMasterSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "pay_sim_master_inst_id")
  private Long id;
  
  @Column(name = "pay_sim_master_name", length = 40, nullable = false)
  private String name;
  @Column(name = "simulation_start_month", columnDefinition = "numeric(2,0) default '0'")
  private int simulationStartMonth;
  
  @Column(name = "simulation_start_year", columnDefinition = "numeric(4,0) default '0'")
  private int simulationStartYear;
  
  @Column(name = "simulation_period_ind", columnDefinition = "numeric")
  private int simulationPeriodInd;
  @Column(name = "simulation_crossover_ind", columnDefinition = "numeric")
  private int simulationCrossOverInd;
  @Column(name = "include_promotions", columnDefinition = "numeric")
  private int includePromotions;

  @Column(name = "business_client_inst_id", nullable = false)
  private Long businessClientId;

	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;
	
	@Column(name = "last_mod_by", nullable = false, length = 50)
	private String lastModBy;
  
  @Transient
  private int applyLtgInd;
  @Transient
  private int stepIncrementInd;
  @Transient
  private int deductBaseYearDevLevyInd;
  @Transient
  private int baseYearDevLevyInd;
  @Transient
  private int deductSpillOverYearDevLevy;
  @Transient
  private int spillYearDevLevyInd;
  @Transient
  private boolean canDoStepIncrement;
  @Transient
  private boolean canDeductSpillOverYearDevLevy;
  @Transient
  private boolean takeBaseYearDevLevy;
  @Transient
  private boolean canDeductBasYearDevLevy;
  @Transient
  private boolean canApplyLtg;
  @Transient
  private int applyLtgToAll;
  @Transient
  private boolean ltgAppliedToAll;
  @Transient
  private boolean crossesOver;
  @Transient
  private boolean includePromotionsChecked;
  @Transient
  private String createdDateStr;
  @Transient
  private String createdBy;
  @Transient
  private String simulationMonthStr;
  @Transient
  private String abmpList;
  @Transient
  private List<AbmpBean> abmpBeanList;
  

  public PayrollSimulationMasterBean(Long pId) {
		 this.id = pId;
	 
	}

  public boolean isCrossesOver()
  {

    return this.simulationCrossOverInd == 1;
  }

  public int compareTo(PayrollSimulationMasterBean pO)
  {
    return 0;
  }


  public boolean isIncludePromotionsChecked()
  {

    return this.includePromotions == 1;
  }



  public boolean isLtgAppliedToAll()
  {
    return this.applyLtgToAll == 1;
  }

  public boolean isCanApplyLtg()
  {
    return this.applyLtgInd == 1;
  }


  public boolean isCanDoStepIncrement()
  {
    return this.stepIncrementInd == 1;
  }

  public boolean isCanDeductSpillOverYearDevLevy()
  {

    return this.deductSpillOverYearDevLevy == 1;
  }

  public boolean isCanDeductBasYearDevLevy()
  {

    return this.deductBaseYearDevLevyInd == 1;
  }



  public String getDetails()
  {
    return "Details...";
  }


public boolean isNewEntity() {
	return this.id == null;
}
}