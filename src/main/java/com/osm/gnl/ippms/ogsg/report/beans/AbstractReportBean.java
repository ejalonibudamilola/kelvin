package com.osm.gnl.ippms.ogsg.report.beans;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDate;

@Getter
@Setter
public abstract class AbstractReportBean implements Comparable<Object> {


    protected Long id;
    protected String name;
    protected String description;
    protected String codeName;
    protected int pageSize;
    protected boolean canEdit;
    protected boolean user;
    protected LocalDate createdDate;
    protected boolean admin;
    public String naira = "\u20A6";
    protected boolean selected;
    protected boolean editMode;
    protected BusinessCertificate businessCertificate;
    protected String mode;
    protected String displayErrors;
    protected String displayTitle;
    protected String displayStyle;
    protected String generatedCaptcha;
    protected String enteredCaptcha;
    protected boolean captchaError;
    protected String actionCompleted;
    protected boolean confirmation;
    protected String deductionCode;
    protected double deductionAmount;
    protected String deductionAmountStr;
    protected String staffId;
    protected String displayName;
    protected boolean fixableError;
    protected Object errorField;
    protected double loanBalance;
    protected double tenor;
    protected String loanCode;
    protected double allowanceAmount;
    protected LocalDate allowanceStartDate;
    protected LocalDate allowanceEndDate;
    protected String startDateStr;
    protected String endDateStr;
    protected String allowanceStartDateStr;
    protected String allowanceEndDateStr;
    protected int runMonth;
    protected int runYear;
    protected int reportType;
    protected boolean showLink;
    protected String startAndEndPeriod;
    protected int objectInd;
    protected Long mdaInstId;
    protected int suspendedInd;
    protected LocalDate lastLtgPaid;
    protected Long salaryInfoInstId;
    protected LocalDate dateOfBirth;
    protected LocalDate dateOfHire;
    protected LocalDate dateTerminated;
    protected boolean approvedForPayroll;
    protected String titleField;
    protected boolean deleteWarningIssued;
    protected boolean saveMode;
    protected String ltgYear;
    protected String errorMsg;
    protected String bankBranchSortCode;
    protected String bankSortCode;
    protected Long bankInstIdFileUpload;
    protected Long branchInstIdFileUpload;
    protected Long paycheckTypeInstId;
    protected String payrollRunningMessage;
    protected String displayPayrollMsg;
    protected boolean payrollRunning;
    protected String amountStr;
    protected String startMonthStr;
    protected String startYearStr;
    protected String endMonthStr;
    protected String endYearStr;
    protected boolean errorRecord;
    protected String amountInWords;
    protected String excelFileName;
    protected String urlName;
    protected String searchAgainUrl;
    protected String payPeriodStr;
    protected boolean warningIssued;
    protected String goToLink;
    protected DecimalFormat df = new DecimalFormat("#,##0.00");
    protected Employee employee;
    protected String salaryInfoDesc;
    protected MdaDeptMap mdaDeptMap;
    protected String mdaName;
    protected Long pfaId;
    protected String details;
    protected String fromDateStr;
    protected int yearInt;
    protected String accountNumber;
    protected String branchName;
    protected LocalDate expDateOfRetirement;
    protected String expDateOfRetirementStr;
    protected LocalDate hireDate;
    protected String hireDateStr;
    protected LocalDate birthDate;
    protected String birthDateStr;
    protected String bankName;
    protected String bankBranchName;

    public String getDeductionAmountStr() {
        deductionAmountStr = naira + df.format(this.deductionAmount);
        return deductionAmountStr;
    }

    public String getDetails() {
        if (this.getFromDateStr() != null && !IppmsUtils.isNotNullOrEmpty(getFromDateStr())) {
            details = "" + this.getId() + "&sDate=" + this.getFromDateStr();
        }
        return details;
    }

    public String getExpDateOfRetirementStr() {
        if (this.expDateOfRetirement != null)
            expDateOfRetirementStr = PayrollHRUtils.getDisplayDateFormat().format(this.expDateOfRetirement);
        return expDateOfRetirementStr;
    }

    public String getBirthDateStr() {
        if (this.birthDate != null)
            birthDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.birthDate);
        return birthDateStr;
    }

    public String getHireDateStr() {
        if (this.hireDate != null)
            hireDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.hireDate);
        return hireDateStr;
    }

 }
