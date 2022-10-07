package com.osm.gnl.ippms.ogsg.pagination.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class PensionDeductionReportBean
  implements Serializable, Comparable<PensionDeductionReportBean>
{
  private static final long serialVersionUID = -2993530824995226100L;
  private String employeeId;
  private String firstName;
  private String lastName;
  private String initials;
  private String pensionPinCode;
  private String pfaName;
  private String mda;
  private Long mdaDeptMapId;
  private int objectInd;
  private Long schoolInstId;
  private int schoolInd;
  private double employeeContribution;
  private String employeeContributionStr;
  private double employerContribution;
  private double employerContributionStr;
  private double totalDeductionAmount;
  private String totalDeductionAmountStr;
  private double totalPensionContribution;
  private String totalPensionContributionStr;
  private boolean attachedToSchool;
  private Long pfaId;
  private List<?> innerBeanList;
  private List<?> schoolEmployeeList;
  private String idStr;
  private String schoolName;
private String mdaCodeName;


  public boolean isAttachedToSchool()
  {
    this.attachedToSchool = (getSchoolInd() == 1);
    return this.attachedToSchool;
  }


public List<?> getInnerBeanList()
  {
    if (this.innerBeanList == null)
      this.innerBeanList = new ArrayList();
    return this.innerBeanList;
  }


public List<?> getSchoolEmployeeList()
  {
    if (this.schoolEmployeeList == null)
      this.schoolEmployeeList = new ArrayList();
    return this.schoolEmployeeList;
  }

  public int compareTo(PensionDeductionReportBean pArg0)
  {
    return 0;
  }

}