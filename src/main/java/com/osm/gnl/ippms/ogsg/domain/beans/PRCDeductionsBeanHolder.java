package com.osm.gnl.ippms.ogsg.domain.beans;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.BaseEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionMiniBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PRCDeductionsBeanHolder extends BaseEntity
  implements Comparable<PRCDeductionsBeanHolder>{
  private static final long serialVersionUID = 6215002408603339357L;
  private DeductionMiniBean deductionBean;
  private int month;
  private int year;

  public int compareTo(PRCDeductionsBeanHolder pIncoming)
  {
    if ((pIncoming != null) && (!pIncoming.isNew()) && (getDeductionBean() != null) && (!getDeductionBean().isNewEntity())) {
      return getDeductionBean().getName().compareToIgnoreCase(pIncoming.getDeductionBean().getName());
    }
    return 0;
  }

}