package com.osm.gnl.ippms.ogsg.employee.beans;

import com.osm.gnl.ippms.ogsg.abstractentities.DependentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class RuleMaster extends DependentEntity
{
  private static final long serialVersionUID = -3158639101395853715L;
  private int baseRule;
  private LocalDate startDate;
  private LocalDate endDate;
  private boolean userCreated;

  public RuleMaster(Long pId)
  {
    setId(pId);
  }


  public boolean isUserCreated()
  {
    if (getBaseRule() == 1)
      this.userCreated = true;
    return this.userCreated;
  }
}