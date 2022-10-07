package com.osm.gnl.ippms.ogsg.domain.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter

public class VariationReportBean extends AbstractControlEntity {
	private static final long serialVersionUID = -1185397300348909114L;
	private DecimalFormat df = PayrollHRUtils.getDecimalFormat();
	private User login;
	private Employee employee;

	private Long salaryInfoInstId;
	private Long mdaInstId;
	private Long id;
	private int objectInd;
	private String oldValue;
	private String newValue;
	private String auditType;
	private String objectType;
	private int runMonth;
	private int runYear;
	private  String mda;
	private LocalDate reviewDate;
	private LocalDate exitDate;
	private String oldGradeStep;
	private String newGradeStep;
	private LocalDate promotionDate;
	private LocalDate dateEmployed;
	private LocalDate period;
	private String payGroup;
	private String gradeStep;
	private Double lastPeriodGross;
	private Double currentPeriodGross;
	private String lastPeriodGrossStr;
	private String currentPeriodGrossStr;
	private String difference;
	private Long employeeInstId;
	private String employeeId;
	private LocalDate reinstatementDate;
	private String approver;
	private int payArrearsInd;
	private double arrearsPercentage;
	private String arrearsPercentageStr;
	private boolean payArrears;
	private LocalDate arrearsStartDate;
	private LocalDate arrearsEndDate;
	private LocalDate terminationDate;
	private String referenceNumber;
	private String employeeName;
	private String userName;
	private String oldBankName;
	private String newBankName;
	private String oldBankNumber;
	private String newBankNumber;
	private String newEmployeeName;
	private String oldEmployeeName;
	private String columnChanged;
	private String auditTime;
	private String reportName;
	private LocalDate changedDate;
	private List<ReportBean> reportsList;
	private String dateName;
	private String oppositeDateName;
	private LocalDate opposingDate;
	private int empCount;
	private Double arrears;
    private Double monthlyPensionAmount;
	private Double thisMonthGross;
	private Double thisMonthNet;
	private Double prevMonthGross;
	private Double prevMonthNet;
	private Double grossDifference;
	private Double netDifference;
	private Double prevTerminated;

	private Double totalGrossDifference;
	private String thisMonthGrossStr;
	private String thisMonthNetStr;
	private String prevMonthGrossStr;
	private String prevMonthNetStr;
	private String grossDifferenceStr;
	private String netDifferenceStr;
	private String currDateStr;
	private String prevDateStr;
	private String payItems;
	private String transferFrom;
	private String transferTo;

	/* NEW */
	private String agency;
	private String oldAgency;
	private String termReason;
	private String promotedDate;
	private String reason;
	private String suspendedBy;
	private String suspendedDate;
	private String absorbedDate;
	private Double totalPay;
	private SalaryInfo salaryInfo;
	private Double basicSalary;
	private Double totalAllowance;
	private int payPercentageInd;
	private int payByDaysInd;
	private LocalDate createdDate;
	private LocalDate terminateDate;
	private LocalDate suspensionDate;
	private Long salaryTypeId;
	private List<NamedEntity> monthList;
	private List<Integer> yearList;
	private String mdaName;
	private String loanCode;
	private int mdaCount;
	private String terminatedBy;
	private boolean schoolSelected;
	private boolean promotion;
	private boolean termination;
	private  boolean newStaff;
	private boolean suspension;
	private boolean reinstatement;
	private boolean reabsorption;
	private boolean specialAllowance;
	private boolean demotion;
	private boolean payGroupChange;
	private boolean loans;
	private boolean employeeChange;
	private boolean bankChange;
	private boolean transfer;
	private boolean deductions;

	private String businessName;
	private Long businessClientInstId;
	private String userFirstName;
	private String userLastName;
	private String agencyName;
	private Double oldAllowance;
	private Double currentAllowance;
	private String oldSalaryStr;
	private String basicSalaryStr;
	private String levelAndStep;
	private String promotedBy;
//	private String createdBy;
	private String createdDateAndAuditTime;
	private String schoolName;
	private List<VariationReportBean> promotionListByMda;
	private List<VariationReportBean> terminationListByMda;
	private List<VariationReportBean> newStaffListByMda;
	private List<VariationReportBean> suspensionListByMda;
	private List<VariationReportBean> reinstatementListByMda;
	private List<VariationReportBean> reabsorptionListByMda;
	private List<VariationReportBean> specialAllowanceListByMda;
	private List<VariationReportBean> demotionListByMda;
	private List<VariationReportBean> payGroupChangeListByMda;
	private List<VariationReportBean> loansListByMda;
	private List<VariationReportBean> employeeChangeListByMda;
	private List<VariationReportBean> bankChangeListByMda;
	private List<VariationReportBean> transferListByMda;
	private List<VariationReportBean> deductionListByMda;



	private int oldStep;
	  private int newStep;
	  private String formatedOldStep;
	  private String formatedNewStep;
	  private int step;
	  private int level;
	  private double specialAllowanceAmount;
	  private Long prevSalInfoInstId;
	  private String auditTimeStamp;


//	public int compareTo(VariationReportBean pArg0)	{
//		return getEmployee().getDisplayName().compareToIgnoreCase(
//				pArg0.getEmployee().getDisplayName());
//	}


	public boolean isPayArrears()	{
		if (getPayArrearsInd() >= 1)
			this.payArrears = true;
		return this.payArrears;
	}

	/*
	 * public int getSalaryInfoInstId() { return this.salaryInfoInstId; }
	 *
	 * public void setSalaryInfoInstId(int pSalaryInfoInstId) {
	 * this.salaryInfoInstId = pSalaryInfoInstId; }
	 */

	public String getAuditTime(){
		if ((getLastModTs() != null) && (getAuditTimeStamp() != null))
			this.auditTime = (PayrollHRUtils.getFullDateFormat().format((TemporalAccessor) getLastModTs())
					+ " " + getAuditTimeStamp());
		return this.auditTime;
	}

	public String getThisMonthGrossStr(){
		if (this.thisMonthGross != null)
			thisMonthGrossStr = df.format(thisMonthGross);
		return thisMonthGrossStr;
	}

	public String getOldSalaryStr(){
		if (super.getOldSalary() != null)
			oldSalaryStr = df.format(super.getOldSalary());
		return oldSalaryStr;
	}

	public String getBasicSalaryStr(){
		if (this.basicSalary != null)
			basicSalaryStr = df.format(basicSalary);
		return basicSalaryStr;
	}



	public String getThisMonthNetStr()
	{
		if (this.thisMonthNet != null)
			thisMonthNetStr = df.format(thisMonthNet);
		return thisMonthNetStr;
	}


	public String getPrevMonthGrossStr()
	{
		if (this.prevMonthGross != null)
			prevMonthGrossStr = df.format(prevMonthGross);
		return prevMonthGrossStr;
	}


	public String getPrevMonthNetStr()
	{
		if (this.prevMonthNet != null)
			prevMonthNetStr = df.format(prevMonthNet);
		return prevMonthNetStr;
	}


	public String getGrossDifferenceStr()	{
		if (this.grossDifference != null)
			grossDifferenceStr = df.format(getGrossDifference());
		return grossDifferenceStr;
	}

	public String getNetDifferenceStr()
	{
		if (this.netDifference != null)
			netDifferenceStr = df.format(netDifference);
		return netDifferenceStr;
	}


	public String getLastPeriodGrossStr()	{
		if (this.lastPeriodGross != null)
			lastPeriodGrossStr = df.format(getLastPeriodGross());
		return lastPeriodGrossStr;
	}

	public String getCurrentPeriodGrossStr()	{
		if (this.currentPeriodGross != null)
			currentPeriodGrossStr = df.format(getCurrentPeriodGross());
		return currentPeriodGrossStr;
	}

	public String getFormatedOldStep() {
		this.formatedOldStep = String.valueOf(this.getOldStep());

		if(this.getOldStep() < 9){
			this.formatedOldStep = "0" + this.getOldStep();
		}
		return formatedOldStep;
	}


	public String getFormatedNewStep() {
		this.formatedNewStep = String.valueOf(this.getNewStep());

		if(this.getOldStep() < 9){
			this.formatedNewStep = "0" + this.getNewStep();
		}
		return formatedNewStep;
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