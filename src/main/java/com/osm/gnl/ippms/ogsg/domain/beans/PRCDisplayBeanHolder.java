package com.osm.gnl.ippms.ogsg.domain.beans;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.BaseEntity;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PRCDisplayBeanHolder extends BaseEntity
  implements Comparable<PRCDisplayBeanHolder>{
  private static final long serialVersionUID = 8565014067576478650L;
  private EmployeePayBean employeePayBean;
  private int month;
  private int year;

  public int compareTo(PRCDisplayBeanHolder pIncoming)
  {
    if ((pIncoming != null) && (!pIncoming.isNew()) && (getEmployeePayBean() != null) && (!getEmployeePayBean().isNewEntity())) {
      if (getEmployeePayBean().getRunYear() == pIncoming.getEmployeePayBean().getRunYear())
      {
        return getEmployeePayBean().getRunMonth() - pIncoming.getEmployeePayBean().getRunMonth();
      }
      return getEmployeePayBean().getRunYear() - pIncoming.getEmployeePayBean().getRunYear();
    }

    return 0;
  }

}