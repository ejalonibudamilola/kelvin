package com.osm.gnl.ippms.ogsg.report.beans;

import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EmployeeMiniBean implements Comparable<EmployeeMiniBean>
{
  private Long id;
  private String name;
  private Long paycheckId;
  private String payDateStr;
  private double totalWithheld;
  private String totalWithheldStr;
  private String firstName;
  private String lastName;
  private String initials;
  private String displayStyle;
  private String employeeName;
  private String accountNumber;
  private String employeeId;



  public String getTotalWithheldStr() {
    this.totalWithheldStr = PayrollHRUtils.getDecimalFormat().format(getTotalWithheld());
    return this.totalWithheldStr;
  }


  @Override
  public int compareTo(EmployeeMiniBean employeeMiniBean) {
    return 0;
  }
}