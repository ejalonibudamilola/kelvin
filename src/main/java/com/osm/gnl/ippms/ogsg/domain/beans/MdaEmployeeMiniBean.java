package com.osm.gnl.ippms.ogsg.domain.beans;


import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Data;

import java.text.DecimalFormat;
import java.time.LocalDate;

@Data
public class MdaEmployeeMiniBean
  implements Comparable<MdaEmployeeMiniBean>
{
  private static final long serialVersionUID = -5247130579280207710L;
  private String salutation;
  private String employeeId;
  private LocalDate hireDate;
  private LocalDate birthDate;
  private int salaryLevel;
  private int salaryStep;
  private String salaryScale;
  private String salaryLevelAndStep;
  private String hireDateStr;
  private String birthDateStr;
  private String lastPayPeriodStr;
  private LocalDate lastPayPeriod;
  private double lastPayAmount;
  private int noOfYears;
  private String noOfYearsInServStr;
  private LocalDate suspensionDate;
  private String suspensionDateStr;
  private boolean suspended;
  private String suspendedStr;
  private String firstName;
  private String lastName;
  private String initials;
  private LocalDate expDateOfRetire;
  private String expDateOfRetireStr;
  private double lastSalary;
  private String lastSalaryStr;
  private int age;
  private String ageStr;
  private int noOfYearsAtRetirement;
  private String noOfYearsAtRetirementStr;
  private String genderCode;
  private Long mdaInstId;
  private int objectInd;
  private String mda;
  private String lga;
  private String salaryScaleLevelAndStepStr;
  private MdaDeptMap mdaDeptMap;
  private LocalDate pensionStartDate;

  public String getNoOfYearsInServStr()
  {
    this.noOfYearsInServStr = Integer.toString(LocalDate.now().getYear() - getHireDate().getYear());
    return this.noOfYearsInServStr;
  }

  public String getLastSalaryStr()
  {
    this.lastSalaryStr = new DecimalFormat("#,##0.00##").format(getLastSalary());
    return this.lastSalaryStr;
  }

  public String getAgeStr()
  {
    this.ageStr = Integer.toString(LocalDate.now().getYear() - getBirthDate().getYear());
    return this.ageStr;
  }

  public String getExpDateOfRetireStr()
  {
    if (getExpDateOfRetire() != null)
      this.expDateOfRetireStr = PayrollHRUtils.getDisplayDateFormat().format(getExpDateOfRetire());
    return this.expDateOfRetireStr;
  }

  public int compareTo(MdaEmployeeMiniBean pO)
  {
    if ((this != null) && (pO != null) && (getLastName() != null) && (pO.getLastName() != null))
      return getLastName().compareToIgnoreCase(pO.getLastName());
    return 0;
  }


  public String getSalaryScaleLevelAndStepStr()
  {
    if (String.valueOf(getSalaryStep()).length() == 1)
      this.salaryScaleLevelAndStepStr = (getSalaryScale() + " " + getSalaryLevel() + "/0" + getSalaryStep());
    else {
      this.salaryScaleLevelAndStepStr = (getSalaryScale() + " " + getSalaryLevel() + "/" + getSalaryStep());
    }

    return this.salaryScaleLevelAndStepStr;
  }

}