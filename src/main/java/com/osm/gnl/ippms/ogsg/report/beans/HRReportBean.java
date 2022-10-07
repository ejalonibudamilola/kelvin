package com.osm.gnl.ippms.ogsg.report.beans;

import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class HRReportBean extends AbstractReportBean
{
  private static final long serialVersionUID = 1081490990184796061L;
  protected int serialNum;
  protected String assignedToObject;
  protected int noOfEmp;
  protected Long mdaDeptMapId;
  protected Long mdaInfoId;
  private Long clientId;
  protected int previousNoOfEmp;
  protected int empDiff;
  protected int noOfItems;
  private Long parentInstId;
  private String placeHolder;
  private int lastEdited;

  public HRReportBean(String pName)
  {
    this.name = pName;
  }

  public HRReportBean(Long pId, String pName) {
     this.id = pId;
     this.name = pName;
  }

  public HRReportBean(String pName, String pUrl) {
      this.name = pName;
      this.urlName = pUrl;
	}

  public HRReportBean(int pNoOfEmp, String pName) {
    this.noOfEmp = pNoOfEmp;
    this.name = pName;

  }
  public String getEndDateStr()
  {
    if (getAllowanceEndDate() != null)
      this.endDateStr = PayrollHRUtils.getDisplayDateFormat().format(getAllowanceEndDate());
    else
      this.endDateStr = "";
    return this.endDateStr;
  }


  public String getStartDateStr()
  {
    if (getAllowanceStartDate() != null)
      this.startDateStr = PayrollHRUtils.getDisplayDateFormat().format(getAllowanceStartDate());
    else
      this.startDateStr = "";
    return this.startDateStr;
  }


  @Override
  public int compareTo(Object o) {
    return 0;
  }
}