package com.osm.gnl.ippms.ogsg.payslip.beans;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
public class EmployeePayMiniBean extends NamedEntity
        implements Comparable<Object> {
    private static final long serialVersionUID = -8398928812933843651L;
    private double basicNet;

    private double taxesPaidNet;
    private double taxesPaidYTD;
    private double salaryYTD;
    private double currentNetPay;
    private double garnishmentTotal;
    private double contributionTotal;
    private double deductionTotal;
    private double currentTotalPay;
    private double taxesPaidTotal;
    private double deductionTotalYTD;
    private String deductionTotalYTDStr;
    private String taxesPaidYTDStr;
    private String salaryYTDStr;
    private String currentNetPayStr;
    private String garnishmentTotalStr;
    private String contributionTotalStr;
    private String deductionTotalStr;
    private String currentTotalPayStr;
    private String taxesPaidTotalStr;
    private String employeeName;
    private String employeeAddress;
    private String employeeState;
    private String employerName;
    private String employerAddress;
    private String employerState;
    private String payPeriod;
    private String payPeriodStart;
    private String payPeriodEnd;
    private String payDate;
    private String paycheckStatus;
    private String employerCityStateZip;
    private String employeeCityStateZip;
    private String payType;
    private double hoursWorked;
    private String totalHours;
    private String payRate;
    private String monthYearAsStr;
    private boolean negative;
    private boolean showAccNo;
    private boolean showBank;
    private double currentContTotal;
    private double currentDedTotal;
    private double currentGarnTotal;
    private double currentTaxesPaid;
    private boolean deletable;
    private String currentContTotalStr;
    private String currentDedTotalStr;
    private String currentGarnTotalStr;
    private String currentTaxesPaidStr;
    private List<EmpDeductMiniBean> deductList;
    private List<EmpGarnMiniBean> garnList;
    private List<EmpDeductMiniBean> mandList;
    private List<EmpDeductMiniBean> allowanceList;
    private List<EmpDeductMiniBean> pensionsContList;
    private List<EmpDeductMiniBean> statutoryDedList;
    private List<NamedEntity> instructionList;
    private LocalDate dateToUse;
    private boolean hasDeduction;
    private String levelAndStep;
    private String basicSalaryStr;
    private String employeeId;
    private String mda;
    private double currentAllowanceTotal;
    private String currentAllowanceTotalStr;
    private double allowanceTotal;
    private String allowanceTotalStr;
    private double currentTotalOtherAllowance;
    private String currentTotalOtherAllowanceStr;
    private String totalOtherAllowanceStr;
    private String salaryInstructionStr;
    private String payeInstructionStr;
    private double totalOtherAllowance;
    private DecimalFormat df;
    private boolean approvedPaycheck;
    private boolean hasDevelopmentLevy;
    private boolean hasTws;
    private String schoolName;
    private boolean memberOfSchool;
    private String accountNumber;
    private String bankBranchName;
    private boolean rerunPaycheck;
    private String payTypeDescr;
    private boolean showRetireDate;
    private String bankName;
    private String expectedRetirementDate;
    private List<EmpDeductMiniBean> summaryList;
    private List<EmpPayBean> payList;
    private byte[] employeePikso;
    private String photoType;
    private boolean piksoPresent;
    private String netPayColor;



    public EmployeePayMiniBean() {
        this.df = new DecimalFormat("#,##0.00");
    }


    public String getTaxesPaidYTDStr() {
        this.taxesPaidYTDStr = this.df.format(this.taxesPaidYTD);
        return this.taxesPaidYTDStr;
    }

    public String getSalaryYTDStr() {
        return this.df.format(this.salaryYTD);
    }

    public String getCurrentNetPayStr() {
        return this.df.format(this.currentNetPay);
    }

    public String getGarnishmentTotalStr() {
        return this.df.format(this.garnishmentTotal);
    }


    public String getContributionTotalStr() {
        return this.df.format(contributionTotal);
    }


    public String getDeductionTotalStr() {
        this.deductionTotalStr = this.df.format(getDeductionTotal());
        return this.deductionTotalStr;
    }


    public String getCurrentTotalPayStr() {
        this.currentTotalPayStr = this.df.format(getCurrentTotalPay());
        return this.currentTotalPayStr;
    }


    public String getTaxesPaidTotalStr() {
        this.taxesPaidTotalStr = this.df.format(getTaxesPaidTotal());
        return this.taxesPaidTotalStr;
    }

    public String getCurrentContTotalStr() {
        this.currentContTotalStr = this.df.format(getCurrentContTotal());
        return this.currentContTotalStr;
    }

    public String getCurrentDedTotalStr() {
        this.currentDedTotalStr = this.df.format(getCurrentDedTotal());
        return this.currentDedTotalStr;
    }

    public String getCurrentGarnTotalStr() {
        this.currentGarnTotalStr = this.df.format(getCurrentGarnTotal());
        return this.currentGarnTotalStr;
    }

    public String getCurrentTaxesPaidStr() {
        this.currentTaxesPaidStr = this.df.format(getCurrentTaxesPaid());
        return this.currentTaxesPaidStr;
    }


    public String getCurrentAllowanceTotalStr() {
        this.currentAllowanceTotalStr = this.df.format(getCurrentAllowanceTotal());
        return this.currentAllowanceTotalStr;
    }

    public String getAllowanceTotalStr() {
        this.allowanceTotalStr = this.df.format(getAllowanceTotal());
        return this.allowanceTotalStr;
    }


    public String getCurrentTotalOtherAllowanceStr() {
        this.currentTotalOtherAllowanceStr = this.df.format(getCurrentTotalOtherAllowance());
        return this.currentTotalOtherAllowanceStr;
    }


    public String getTotalOtherAllowanceStr() {
        this.totalOtherAllowanceStr = this.df.format(getTotalOtherAllowance());
        return this.totalOtherAllowanceStr;
    }


    @Override
    public int compareTo(Object employeePayMiniBean) {
        return 0;
    }
}