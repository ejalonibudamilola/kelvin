package com.osm.gnl.ippms.ogsg.abstractentities;


import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class NamedEntityLong extends BaseEntityLong
  implements Comparable<NamedEntityLong>
{
  private static final long serialVersionUID = -5093278499522592093L;
	private String name;
	private String mode;
	private String displayErrors;
	private String displayTitle;
	private String displayName;
	private int objectInd;
	private Long mdaInstId;
	private int suspendedInd;
	private LocalDate lastLtgPaid;
	private Long salaryInfoInstId;
	private LocalDate dateOfBirth;
	private LocalDate dateOfHire;
	private LocalDate dateTerminated;
	private boolean approvedForPayroll;
	private String titleField;
	private LocalDate expRetireDate;
	private String generatedCaptcha;
	private String enteredCaptcha;
	private boolean approving;
	private boolean approved;
	private LocalDate approvedDate;
	private String approvedDateStr;
	private String approvedTime;
	private String lastModBy;
	private Timestamp lastModTs;
	private Long parentInstId;
	private int approvedInd;
	private String description;
	private String details = "Details...";
	private String delete = "Delete File";
	private String mdaName;
	private String employeeId;
	private int reportType;
	private String displayStyle;
	private String payrollRunningMessage;
	private String displayPayrollMsg;
	private boolean payrollRunning;
	private int runMonth;
	private int runYear;
	private boolean singleEmployee;
	private boolean filteredByUserId;
	private String userName;
	private boolean confirmation;
	private boolean editMode;
	private boolean deleteWarningIssued;
	private int noOfYearsAsPensioner;
	private String pageUrl;
   
   public String getDisplayErrors() {
    if (this.displayErrors == null)
      this.displayErrors = "none";
    return this.displayErrors;
  }

  public int compareTo(NamedEntityLong pIncoming)
  {
	if(this.getId() != null && pIncoming.getId() != null)
       return getId().compareTo(pIncoming.getId());
	else if(this.getName() != null && pIncoming.getName() != null)
		return this.getName().compareToIgnoreCase(pIncoming.getName());
	else if(this.getDisplayName() != null && pIncoming.getDisplayName() != null)
		return this.getDisplayName().compareToIgnoreCase(pIncoming.getDisplayName());
	return 0;
  }


public String getApprovedDateStr()
{
	if(this.getApprovedDate() != null)
	  this.approvedDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.getApprovedDate());
	return approvedDateStr;
}

}