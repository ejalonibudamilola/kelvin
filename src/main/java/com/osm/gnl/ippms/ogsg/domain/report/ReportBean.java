package com.osm.gnl.ippms.ogsg.domain.report;


import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//public class ReportBean extends NamedEntity implements Comparable<ReportBean>{
public class ReportBean extends NamedEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1048708513892172596L;
	
	private String link;
	private String checkBoxStatus;
	private boolean checked;
	private String templateName;
	private boolean newStaffReport;
	private boolean promotionReport;
	private boolean reconciliationReport;
	private boolean reinstatementReport;
	private boolean deductionReport;
	private boolean terminationReport;
	private boolean suspensionReport;
	private boolean reabsorptionReport;
	private boolean specAllowanceReport;
	private boolean incrementReport;
	private boolean demotionReport;
	private boolean paygroupReport;
	private boolean loansReport;
	private boolean employeeChangeReport;
	private boolean transferReport;
	private boolean bankChangeReport;

	private String subReportTemplateName;

	private String subReportDataSource;

	private String mVariationTemplateName;

	private boolean usingMonthlyVariation;
	private boolean usingReconsiliationReport;
	private boolean schoolSelected;
	  
	private boolean specAllowDecreaseReport;
	private boolean prevTerminationReport;


	public ReportBean(Long a, String rName, String pLink)	{
		setId(a);
		setName(rName);
		setLink(pLink);
	}


	  public boolean isChecked() {
	    if (getCheckBoxStatus() == null)
	      this.checked = false;
	    else
	      this.checked = Boolean.valueOf(getCheckBoxStatus()).booleanValue();
	    return this.checked;
	  }


//	@Override
//	public int compareTo(ReportBean arg0) {
//		return this.getId().compareTo(arg0.getId());
//	}



}
