package com.osm.gnl.ippms.ogsg.domain.massentry;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncrementTracker;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.temporal.TemporalAccessor;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class PromotionHistory extends AbstractControlEntity {
  private static final long serialVersionUID = 6147342281351765803L;
  private Long id;
  private AbstractEmployeeEntity employee;
  private String fromSalaryInfo;
  private String toSalaryInfo;
  private Date effectiveDate;
  private String effectiveDateStr;
  private String promotionDateStr;
  private Integer entryIndex;
  private String remove = "Remove Entry...";
  private String oldSalaryLevelAndStep;
  private String newSalaryLevelAndStep;
  private String displayStyle;
  private AbstractGarnishmentEntity empGarnishInfo;
  private Integer salaryArrearsInstId;
  private SalaryInfo salaryInfo;
  private String transferFrom;
  private String transferTo;
  private AbstractDeductionEntity empDeductionInfo;
  private boolean stepIncrementType;
  private StepIncrementTracker stepIncrementTracker;
  


  public int compareTo(PromotionHistory pIncoming)
  {
    if (getEffectiveDate() == null) {
      return pIncoming.getEntryIndex().compareTo(getEntryIndex());
    }
    if (getEffectiveDate().equals(pIncoming.getEffectiveDate())) {
      return getId().intValue() - pIncoming.getId().intValue();
    }
    return getEffectiveDate().compareTo(pIncoming.getEffectiveDate());
  }

  public String getEffectiveDateStr()
  {
    if (getEffectiveDate() != null)
      this.effectiveDateStr = PayrollHRUtils.getDisplayDateFormat().format((TemporalAccessor) getEffectiveDate());
    return this.effectiveDateStr;
  }

  public String getPromotionDateStr()
  {
    if (getLastModTs() != null)
      this.promotionDateStr = PayrollHRUtils.getDisplayDateFormat().format((TemporalAccessor) getLastModTs());
    return this.promotionDateStr;
  }


  @Override
  public int compareTo(Object o) {
    return 0;
  }

  @Override
  public boolean isNewEntity() {
    return false;
  }
}