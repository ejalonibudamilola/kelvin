package com.osm.gnl.ippms.ogsg.domain.promotion;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;



@Entity
@Table(name = "ippms_manual_promo_ind")
@SequenceGenerator(name = "manPromoSeq", sequenceName = "ippms_manual_promo_ind_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ManualPromotionBean extends AbstractNamedEntity
{
  private static final long serialVersionUID = -5918315664971568411L;
  
  @Id
  @GeneratedValue(generator = "manPromoSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "manual_promo_inst_id")
  private Long id;
  
  @Column(name = "last_man_promo_year", nullable = false)
  private String lastPromoYear;

  @Column(name = "run_month", nullable = false)
  private int runMonth;
  @Column(name = "run_year", nullable = false)
  private int runYear;

  @Column(name = "business_client_inst_id", nullable = false)
  private Long businessClientId;
  
  @Transient
  private int totalEmpNo;
  @Transient
  private int filteredNo;
  @Transient
  private int employeesAtBar;
  @Transient
  private List<StepIncreaseBean> stepBeanList;
  @Transient
  private HashMap<Long, HashMap<Integer, Integer>> SalaryTypeLevelStepMap;
  @Transient
  private boolean deleteWarningIssued;

  public ManualPromotionBean(Long pId) {
	  this.id = pId;
  }


  @Override
  public int compareTo(Object o) {
    return 0;
  }

  @Override
public boolean isNewEntity() {
	 
	return this.id == null;
}

}