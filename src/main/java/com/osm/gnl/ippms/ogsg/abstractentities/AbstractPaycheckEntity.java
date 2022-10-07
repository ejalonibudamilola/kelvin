/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.abstractentities;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGarnishment;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGratuity;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import com.osm.gnl.ippms.ogsg.utils.annotation.PaycheckAllowance;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractPaycheckEntity implements Comparable<Object>, Serializable {



    /**
     * Necessary for Archiving. This object exists for the Employee as well.
     */
    @ManyToOne
    @JoinColumn(name = "mda_dept_map_inst_id", nullable = false)
    private MdaDeptMap mdaDeptMap;

    /**
     * Necessary for Archiving. This object exists for the Employee as well.
     */
    @ManyToOne
    @JoinColumn(name = "employee_type_inst_id", nullable = false)
    private EmployeeType employeeType;

    @ManyToOne
    @JoinColumn(name = "salary_info_inst_id", nullable = false)
    private SalaryInfo salaryInfo;

    @ManyToOne
    @JoinColumn(name = "payroll_master_inst_id", nullable = false)
    private PayrollRunMasterBean masterBean;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "branch_inst_id", nullable = false)
    private BankBranch bankBranch;

    @ManyToOne
    @JoinColumn(name = "pfa_inst_id")
    private PfaInfo pfaInfo;

    @ManyToOne
    @JoinColumn(name = "rule_master_inst_id")
    private AllowanceRuleMaster allowanceRuleMaster;

    @ManyToOne
    @JoinColumn(name = "school_inst_id")
    private SchoolInfo schoolInfo;

    @Column(name = "run_month", nullable = false)
    private int runMonth;

    @Column(name = "run_year", nullable = false)
    private int runYear;

    @Column(name = "pay_by_days_ind", nullable = false)
    private int payByDaysInd;

    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;

    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @Column(name = "terminated_date")
    private String terminatedDate;

    @Column(name = "total_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double totalPay;

    @Column(name = "net_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double netPay;
    @Column(name = "net_pay_after_taxes", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double netPayAfterTaxes;

    @Column(name = "taxes_paid", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double taxesPaid;

    @Column(name = "total_contributions", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double totalContributions;

    @Column(name = "total_deductions", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double totalDeductions;

    @Column(name = "total_garnishments", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double totalGarnishments;

    @Column(name = "net_pay_after_tax_ded", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double netPayAfterTaxableDeductions;

    @Column(name = "overtime_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double overtimePay;

    @Column(name = "bonus_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double bonusPay;

    @Column(name = "commission", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double commissionPay;

    @Column(name = "total_hours", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double hoursWorked;

    @Column(name = "status", columnDefinition = "VARCHAR(1) default 'P'", nullable = false)
    private String status;

    @Column(name = "pay_status", columnDefinition = "VARCHAR(1) default 'N'", nullable = false)
    private String payStatus;

    @Column(name = "total_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double totalAllowance;

    @Column(name = "other_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double otherAllowance;

    @Column(name = "development_levy", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double developmentLevy;

    @PaycheckAllowance(type = "L.T.G")
    @Column(name = "leave_transport_grant", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double leaveTransportGrant;

    @PaycheckAllowance(type = "Contract Allowance")
    @Column(name = "contract_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double contractAllowance;

    @PaycheckAllowance(type = "Promotion Arrears")
    @Column(name = "arrears", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double arrears;

    @Column(name = "basic_net", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double basicNet;

    @Column(name = "taxes_net", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double taxesPaidNet;

    @PaycheckAllowance(type = "Re-Absorbtion Arrears")
    @Column(name = "other_arrears", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double otherArrears;

    @Column(name = "salary_difference", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double salaryDifference;

    @Column(name = "special_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double specialAllowance;

    @Column(name = "contributory_pension", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double contributoryPension;

    @Column(name = "relief_amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double monthlyReliefAmount;

    @Column(name = "taxable_income", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double taxableIncome;

    @Column(name = "free_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double freePay;

    @Column(name = "no_of_days", columnDefinition = "integer default  '0'",nullable = false)
    private int noOfDays;

    @Column(name = "percentage_ind",columnDefinition = "integer default  '0'", nullable = false)
    private int payPercentageInd;

    @Column(name = "percentage_payment", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double payPercentage;

    @Column(name = "tot_curr_ded", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double totCurrDed;

    @Column(name = "monthly_basic", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double monthlyBasic;

    @Column(name = "account_number", length = 20, nullable = false)
    private String accountNumber;

    @Column(name = "pension_pin_code", length = 40)
    private String pensionPinCode;

    @Column(name = "bvn", length = 11, nullable = false)
    private String bvnNo;

    @Column(name = "rejected_for_pay_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int rejectedForPayrollingInd;

    @Column(name = "rerun_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int reRunInd;

    @Column(name = "neg_pay_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int negativePayInd;

    @Column(name = "suspended_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int suspendedInd;

    @Column(name = "contract_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int contractIndicator;

    @Column(name = "terminated_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int terminatedInd;

    @Column(name = "i_am_alive_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int iAmAliveInd;

    @Column(name = "birth_date_term_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int birthDateTerminatedInd;

    @Column(name = "hire_date_term_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int hireDateTerminatedInd;

    @Column(name = "ytd_ignore_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int ytdIgnoreInd;

    @Column(name = "global_per_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int globalPerInd;

    @Column(name = "awaiting_calc_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int awaitingPenCalcInd;

    @Column(name = "reinstated_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int reinstatedInd;

    @Column(name = "reabsorption_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int reabsorptionInd;

    @Column(name = "biometric_ind", columnDefinition = "integer default  '0'", nullable = false)
    private int biometricInd;

    @Column(name = "payroll_run_date", insertable = false)
    private LocalDate payrollRunDate = LocalDate.now();


    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;

    @Column(name = "paye", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double monthlyTax;

    @PaycheckAllowance(type = "Principal Allowance")
    @Column(name = "principal_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double principalAllowance;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "initials", length = 50)
    private String initials;

    @Column(name = "og_number", length = 25)
    private String ogNumber;

    @PaycheckAllowance(type = "Academic Allowance")
    @Column(name = "academic_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double academicAllowance;

    @PaycheckAllowance(type = "Gratuity")
    @Column(name = "gratuity_amount_paid", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double gratuityAmountPaid;

    @PaycheckAllowance(type = "Admin Allowance")
    @Column(name = "admin_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double adminAllowance;

    @PaycheckAllowance(type = "Call Duty")
    @Column(name = "call_duty", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double callDuty;

    @PaycheckAllowance(type = "Domestic Servant")
    @Column(name = "domestic_servant", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double domesticServant;

    @PaycheckAllowance(type = "Drivers Allowance")
    @Column(name = "drivers_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double driversAllowance;

    @PaycheckAllowance(type = "Entertainment")
    @Column(name = "entertainment", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double entertainment;

    @PaycheckAllowance(type = "Furniture")
    @Column(name = "furniture", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double furniture;

    @PaycheckAllowance(type = "Hazard")
    @Column(name = "hazard", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double hazard;

    @PaycheckAllowance(type = "Inducement")
    @Column(name = "inducement", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double inducement;

    @PaycheckAllowance(type = "Journal")
    @Column(name = "journal", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double journal;

    @PaycheckAllowance(type = "Meal")
    @Column(name = "meal", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double meal;

    @Column(name = "nhf", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double nhf;

    @PaycheckAllowance(type = "Nurser Other Allowance")
    @Column(name = "nurse_other_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double nurseOtherAllowance;

    @PaycheckAllowance(type = "Rent")
    @Column(name = "rent", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double rent;

    @PaycheckAllowance(type = "Rural Posting")
    @Column(name = "rural_posting", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double ruralPosting;

    @PaycheckAllowance(type = "Security Allowance")
    @Column(name = "security_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double securityAllowance;

    @PaycheckAllowance(type = "Transport")
    @Column(name = "transport", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double transport;

    @PaycheckAllowance(type = "Peculiar Allowance")
    @Column(name = "perculiar_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double peculiarAllowance;

    @Column(name = "tss", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double tss;

    @Column(name = "tws", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double tws;

    @Column(name = "union_dues", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double unionDues;

    @Column(name = "global_percentage", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double globalPercentage;

    @PaycheckAllowance(type = "Utility")
    @Column(name = "utility", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double utility;

    @PaycheckAllowance(type = "Monthly Pension")
    @Column(name = "monthly_pension", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    protected double monthlyPension;

    @Transient private boolean unApprovedPaycheck;
    @Transient private String monthlyPensionStr;
    @Transient private String monthlyPensionStrSansNaira;
    @Transient private String levelAndStepStr;
    @Transient private final String naira = "\u20A6";
    @Transient private boolean gratuityPaymentMade;
    @Transient private String bankName;
    @Transient private double allDedTotal;
    @Transient private String allDedTotalStr;
    @Transient private String allDedTotalStrSansNaira;
    @Transient private String bonusPayStr;
    @Transient private String bonusPayStrSansNaira;
    @Transient private String checkStatus;
    @Transient private String commissionPayStr;
    @Transient private String deductionAmountStr;
    @Transient private LocalDate currentPayDate;
    @Transient private final String delete = "Delete";
    @Transient private String departmentName;
    @Transient private final String details = "Details...";
    @Transient private String displayStyle;
    @Transient private String developmentLevyStr;
    @Transient private String developmentLevyStrSansNaira;
    @Transient private String domesticServantStr;
    @Transient private String domesticServantStrSansNaira;
    @Transient private boolean doNotPay;
    @Transient private String driversAllowanceStr;
    @Transient private String driversAllowanceStrSansNaira;
    @Transient private List<AbstractSpecialAllowanceEntity> specialAllowanceList;
    @Transient private List<AbstractDeductionEntity> employeeDeductions;
    @Transient private List<AbstractGarnishmentEntity> employeeGarnishments;
    @Transient private double grossPay;
    @Transient private String grossPayStr;
    @Transient private String grossPayStrSansNaira;
    @Transient private boolean hasBonusPay;
    @Transient private boolean hasCommissionPay;
    @Transient private boolean hasOvertimePay;
    @Transient private boolean hasPaymentMethod;
    @Transient private HiringInfo hiringInfo;
    @Transient private boolean hourlyPayInd;
    @Transient private String lastPayDate;
    @Transient private String ministryName;
    @Transient private double netPayAfterAllowances;
    @Transient private String netPayAfterAllowancesStr;
    @Transient private String netPayAfterAllowancesStrSansNaira;
    @Transient private String netPayAfterTaxableDeductionsStr;
    @Transient private String netPayAfterTaxableDeductionsStrSansNaira;
    @Transient private String netPayAfterTaxesStr;
    @Transient private String netPayAfterTaxesStrSansNaira;
    @Transient private String netPayStr;
    @Transient private String netPayStrSansNaira;
    @Transient private String absoluteNetPayStr;
    @Transient private String academicAllowanceStr;
    @Transient private String academicAllowanceStrSansNaira;
    @Transient private String otherAllowanceStr;
    @Transient private String otherAllowanceStrSansNaira;
    @Transient private String overtimePayStr;
    @Transient private boolean paid;
    @Transient private String payEmployee;
    @Transient private boolean payEmployeeRef;
    @Transient private PaymentMethodInfo paymentMethodInfo;
    @Transient private String paymentType;
    @Transient private String payPeriodAsString;
    @Transient private String paySched;
    @Transient private String payStatusStr;
    @Transient private boolean PFADefined;
    @Transient private String salaryScale;
    @Transient private String taxesPaidStr;
    @Transient private String taxesPaidStrSansNaira;
    @Transient private String totalContributionsStr;
    @Transient private String totalContributionsStrSansNaira;
    @Transient private double totalCost;
    @Transient private String totalCostStr;
    @Transient private String totalDeductionsStr;
    @Transient private String totalDeductionsStrSansNaira;
    @Transient private String totalGarnishmentsStr;
    @Transient private String totalGarnishmentsStrSansNaira;
    @Transient private double totalOther;
    @Transient private String totalOtherStr;
    @Transient private String totalOtherStrSansNaira;
    @Transient private String totalPayStr;
    @Transient private String totalPayStrSansNaira;
    @Transient private String leaveTransportGrantStr;
    @Transient private String leaveTransportGrantStrSansNaira;
    @Transient private String principalAllowanceStr;
    @Transient private String principalAllowanceStrSansNaira;
    @Transient private SalaryType salaryType;
    @Transient private String arrearsStr;
    @Transient private String arrearsStrSansNaira;
    @Transient private Long currentMapId;
    @Transient private int currentObjectId;
    @Transient private boolean ltgEnabled;
    @Transient private int salaryTypeCode;
    @Transient private boolean contractStaff;
    @Transient private String contractAllowanceStr;
    @Transient private String contractAllowanceStrSansNaira;
    @Transient private String basicNetStr;
    @Transient private String taxesPaidNetStr;
    @Transient private String taxesPaidNetStrSansNaira;
    @Transient private String basicNetStrSansNaira;
    @Transient private String otherArrearsStr;
    @Transient private String otherArrearsStrSansNaira;
    @Transient private String salaryDifferenceStr;
    @Transient private String salaryDifferenceStrSansNaira;
    @Transient private String specialAllowanceStr;
    @Transient private String specialAllowanceStrSansNaira;
    @Transient private String contributoryPensionStr;
    @Transient private String contributoryPensionStrSansNaira;
    @Transient private double reliefAmount;
    @Transient private String reliefAmountStr;
    @Transient private String reliefAmountStrSansNaira;
    @Transient private String taxableIncomeStr;
    @Transient private String taxableIncomeStrSansNaira;
    @Transient private String mda;
    @Transient private double consolidatedAllowance;
    @Transient private String totalDedStr;
    @Transient private String totalOtherAllowStr;
    @Transient private String standardAllowanceStr;
    @Transient private String nonStandardAllowanceStr;
    @Transient private String totalAllowPlusBasic;
    @Transient private List<PaycheckGarnishment> garnishList;
    @Transient private List<PaycheckDeduction> deductionList;
    @Transient private double yearlyReliefAmount;
    @Transient private String monthlyReliefAmountStr;
    @Transient private String monthlyReliefAmountStrSansNaira;
    @Transient private String freePayStr;
    @Transient private String freePayStrSansNaira;
    @Transient private double basicSalary;
    @Transient private String basicSalaryStr;
    @Transient private String basicSalaryStrSansNaira;
    @Transient private double totalNonTaxableDeductions;
    @Transient private double totalTaxableDeductions;
    @Transient private String schoolName;
    @Transient private boolean memberOfSchool;
    @Transient private boolean payByDays;
    @Transient private String branchName;
    @Transient private String branchSortCode;
    @Transient private String employeeTypeName;
    @Transient private String payPercentageStr;
    @Transient private boolean percentagePayment;
    @Transient private String totCurrDedStr;
    @Transient private String totCurrDedStrSansNaira;
    @Transient private double grossPayYTD;
    @Transient private double taxableIncomeYTD;
    @Transient private double taxPaidYTD;
    @Transient private String grossPayYTDStr;
    @Transient private String taxableIncomeYTDStr;
    @Transient private String taxPaidYTDStr;
    @Transient private Long empInstId;
    @Transient private String employeeId;
    @Transient private String employeeName;
    @Transient private boolean doNotDeductContributoryPension;
    @Transient private String adminAllowanceStr;
    @Transient private String adminAllowanceStrSansNaira;
    @Transient private Long schoolInstId;
    @Transient private Long branchInstId;
    @Transient private Long pfaInstId;
    @Transient private Long payrollRerunInstId;
    @Transient private boolean noPaymentWarningIssued;
    @Transient private String treatedStatus;
    @Transient private String payPeriodStr;
    @Transient private double deductionAmount;
    @Transient private String monthlyBasicStr;
    @Transient private String monthlyBasicStrSansNaira;
    @Transient private String monthlyTaxStr;
    @Transient private SuspensionLog suspensionLog;
    @Transient private String fullName;
    @Transient private int objectInd;
    @Transient private boolean terminationCandidate;
    @Transient private boolean birthDateTermination;
    @Transient private boolean hireDateTermination;
    @Transient private AbstractEmployeeEntity abstractEmployeeEntity;
    @Transient private boolean ignoreYtdValues;
    @Transient private boolean usedGlobalPercentage;
    @Transient protected String rowSelected;
    @Transient protected Long objectInstId;
    @Transient private String totalAllowanceStr;
    @Transient private String totalAllowancesStr;
    @Transient protected DecimalFormat df = new DecimalFormat("#,##0.00");
    @Transient private String entertainmentStr;
    @Transient private String entertainmentStrSansNaira;
    @Transient private String furnitureStr;
    @Transient private String furnitureStrSansNaira;
    @Transient private String hazardStr;
    @Transient private String hazardStrSansNaira;
    @Transient private String callDutyStr;
    @Transient private String callDutyStrSansNaira;
    @Transient private String inducementStr;
    @Transient private String inducementStrSansNaira;
    @Transient private String journalStr;
    @Transient private String journalStrSansNaira;
    @Transient private String mealStr;
    @Transient private String mealStrSansNaira;
    @Transient private String nhfStr;
    @Transient private String nhfStrSansNaira;
    @Transient private String nurseOtherAllowanceStr;
    @Transient private String nurseOtherAllowanceStrSansNaira;
    @Transient private String rentStr;
    @Transient private String rentStrSansNaira;
    @Transient private String ruralPostingStr;
    @Transient private String ruralPostingStrSansNaira;
    @Transient private String transportStr;
    @Transient private String transportStrSansNaira;
    @Transient private String tssStr;
    @Transient private String tssStrSansNaira;
    @Transient private String unionDuesStr;
    @Transient private String unionDuesStrSansNaira;
    @Transient private String utilityStr;
    @Transient private String utilityStrSansNaira;
    @Transient private String twsStr;
    @Transient private String twsStrSansNaira;
    @Transient private String securityAllowanceStr;
    @Transient private String securityAllowanceStrSansNaira;
    @Transient private String name;
    @Transient private String description;
    @Transient private boolean warningIssued;
    @Transient private String generatedCaptcha;
    @Transient private String displayErrors;
    @Transient private String enteredCaptcha;
    @Transient private boolean captchaError;
    @Transient private PaycheckGratuity paycheckGratuity;
    @Transient private String BusinessClientName;
    @Transient private String rankName;
    @Transient private String salaryTypeName;
    @Transient private String lgaName;
    @Transient private LocalDate IncrementalDate;
    @Transient private int entryIndex;
    @Transient private int level;
    @Transient private int step;
    //paycheck deductions for lga
    @Transient private Double mahwun = 0.0D;
    @Transient private Double nannm = 0.0D;
    @Transient private Double nulge = 0.0D;
    @Transient private Double nachp = 0.0D;
    @Transient private Double adv = 0.0D;
    @Transient private String advName = IConstants.EMPTY_STR;
    @Transient private Double coop= 0.0D;
    @Transient private String coopName = IConstants.EMPTY_STR;
    @Transient private Double dep= 0.0D;
    @Transient private String depName = IConstants.EMPTY_STR;
    @Transient private Double totalDeduction1;

    public abstract AbstractEmployeeEntity getParentObject();
    public abstract void setParentObject(AbstractEmployeeEntity abstractEmployeeEntity);

    public boolean isUsedGlobalPercentage() {
        usedGlobalPercentage = this.globalPerInd == 1;
        return usedGlobalPercentage;
    }

    public boolean isIgnoreYtdValues() {
        return this.ytdIgnoreInd == 1;
    }

    public boolean isTerminationCandidate() {
        return this.terminatedInd == 1;
    }

    public boolean isBirthDateTermination() {
        return this.birthDateTerminatedInd == 1;
    }

    public boolean isIamAliveTermination(){
        return this.iAmAliveInd == 1;
    }

    public boolean isGratuityPaymentMade() {
        gratuityPaymentMade = this.gratuityAmountPaid > 0.0D;
        return gratuityPaymentMade;
    }

    public boolean isHireDateTermination() {
        return this.hireDateTerminatedInd == 1;
    }

    public String getFullName(){
        return PayrollHRUtils.createDisplayName(this.lastName, this.firstName, this.initials);
    }

    @Override
    public int compareTo(Object o) { return 0; }

    public String getAbsoluteNetPayStr() {
        absoluteNetPayStr = naira+ this.df.format(Math.abs(this.netPay));
        return absoluteNetPayStr;
    }

    public String getAcademicAllowanceStr() {
        this.academicAllowanceStr = (naira+ this.df.format(this.academicAllowance));
        return this.academicAllowanceStr;
    }

    public String getAcademicAllowanceStrSansNaira() {
        this.academicAllowanceStrSansNaira = this.df.format(this.academicAllowance);

        return this.academicAllowanceStrSansNaira;
    }

    public String getAdminAllowanceStr() {
        if (this.adminAllowance > 0.0D)
            this.adminAllowanceStr = (naira+ this.df.format(this.adminAllowance));
        return this.adminAllowanceStr;
    }

    public String getAdminAllowanceStrSansNaira() {
        this.adminAllowanceStrSansNaira = this.df.format(this.adminAllowance);
        return this.adminAllowanceStrSansNaira;
    }

    public double getAllDedTotal() {
        this.allDedTotal = (this.getNhf() + this.totalDeductions );
        return this.allDedTotal;
    }

    public String getAllDedTotalStr() {
        this.allDedTotalStr = (naira+ this.df.format(getAllDedTotal()));
        return this.allDedTotalStr;
    }

    public String getAllDedTotalStrSansNaira() {
        this.allDedTotalStrSansNaira = this.df.format(getAllDedTotal());
        return this.allDedTotalStrSansNaira;
    }


    public String getArrearsStrSansNaira() {
        this.arrearsStrSansNaira = this.df.format(this.arrears);
        return this.arrearsStrSansNaira;
    }

    public String getBasicSalaryStr() {
        this.basicSalaryStr = (naira+ this.df.format(this.basicSalary));
        return this.basicSalaryStr;
    }

    public String getBasicSalaryStrSansNaira() {
        this.basicSalaryStrSansNaira = this.df.format(this.basicSalary);
        return this.basicSalaryStrSansNaira;
    }

    public String getBonusPayStr() {
        this.bonusPayStr = (naira+ this.df.format(this.bonusPay));
        return this.bonusPayStr;
    }

    public String getCheckStatus() {
        if (this.status != null) {
            if (this.status.equalsIgnoreCase("p"))
                this.checkStatus = "Pending";
            else {
                this.checkStatus = "Approved";
            }
        }
        return this.checkStatus;
    }

    public String getCommissionPayStr() {
        this.commissionPayStr = (naira+ this.df.format(this.commissionPay));
        return this.commissionPayStr;
    }

    public String getMonthlyPensionStr() {
        this.monthlyPensionStr = (naira+ this.df.format(this.monthlyPension));
        return monthlyPensionStr;
    }

    public String getMonthlyPensionStrSansNaira() {
        this.monthlyPensionStrSansNaira = this.df.format(this.monthlyPension);
        return monthlyPensionStrSansNaira;
    }

    public AbstractEmployeeEntity getAbstractEmployeeEntity() {
        return abstractEmployeeEntity;
    }

    public String getContractAllowanceStr() {
        this.contractAllowanceStr = (naira+ this.df.format(this.contractAllowance));
        return this.contractAllowanceStr;
    }

    public String getContractAllowanceStrSansNaira() {
        this.contractAllowanceStrSansNaira = this.df.format(this.contractAllowance);
        return this.contractAllowanceStrSansNaira;
    }

    public String getContributoryPensionStr() {
        this.contributoryPensionStr = (naira+ this.df.format(this.contributoryPension));
        return this.contributoryPensionStr;
    }

    public String getContributoryPensionStrSansNaira() {
        this.contributoryPensionStrSansNaira = this.df.format(this.contributoryPension);
        return this.contributoryPensionStrSansNaira;
    }

    public String getDevelopmentLevyStrSansNaira() {
        this.developmentLevyStrSansNaira = this.df.format(this.developmentLevy);
        return this.developmentLevyStrSansNaira;
    }


    public String getDomesticServantStr() {
        this.domesticServantStr = (naira+ this.df.format(getDomesticServant()));
        return this.domesticServantStr;
    }

    public String getDomesticServantStrSansNaira() {
        this.domesticServantStrSansNaira = this.df.format(getDomesticServant());
        return this.domesticServantStrSansNaira;
    }

    public String getDriversAllowanceStr() {
        this.driversAllowanceStr = (naira+ this.df.format(getDriversAllowance()));
        return this.driversAllowanceStr;
    }

    public String getDriversAllowanceStrSansNaira() {
        this.driversAllowanceStrSansNaira = this.df.format(this.getDriversAllowance());
        return this.driversAllowanceStrSansNaira;
    }

    public String getFreePayStr() {
        this.freePayStr = (naira+ this.df.format(this.freePay));
        return this.freePayStr;
    }

    public String getFreePayStrSansNaira() {
        this.freePayStrSansNaira = this.df.format(this.freePay);
        return this.freePayStrSansNaira;
    }

    public String getGrossPayStr() {
        this.grossPayStr = (naira+ this.df.format(this.totalPay));
        return this.grossPayStr;
    }

    public String getGrossPayStrSansNaira() {
        this.grossPayStrSansNaira = this.df.format(this.totalPay);
        return this.grossPayStrSansNaira;
    }

    public String getGrossPayYTDStr() {
        grossPayYTDStr = naira+ this.df.format(grossPayYTD);
        return grossPayYTDStr;
    }

    public String getLeaveTransportGrantStr() {
        this.leaveTransportGrantStr = (naira+ this.df.format(this.leaveTransportGrant));
        return this.leaveTransportGrantStr;
    }

    public String getLeaveTransportGrantStrSansNaira() {
        this.leaveTransportGrantStrSansNaira = this.df.format(this.leaveTransportGrant);
        return this.leaveTransportGrantStrSansNaira;
    }


    public String getMonthlyBasicStr() {
        monthlyBasicStr = naira+ this.df.format(this.monthlyBasic);
        return monthlyBasicStr;
    }

    public String getMonthlyBasicStrSansNaira() {
        monthlyBasicStrSansNaira = this.df.format(this.monthlyBasic);
        return monthlyBasicStrSansNaira;
    }

    public String getMonthlyReliefAmountStr() {
        this.monthlyReliefAmountStr = (naira+ this.df.format(this.monthlyReliefAmount));
        return this.monthlyReliefAmountStr;
    }

    public String getMonthlyReliefAmountStrSansNaira() {
        this.monthlyReliefAmountStrSansNaira = this.df.format(this.monthlyReliefAmount);
        return this.monthlyReliefAmountStrSansNaira;
    }

    public abstract String getName() ;

    public String getNetPayAfterAllowancesStr() {
        this.netPayAfterAllowancesStr = (naira+ this.df.format(this.netPayAfterAllowances));
        return this.netPayAfterAllowancesStr;
    }

    public String getNetPayAfterAllowancesStrSansNaira() {
        this.netPayAfterAllowancesStrSansNaira = this.df.format(this.netPayAfterAllowances);
        return this.netPayAfterAllowancesStrSansNaira;
    }
    public String getNetPayAfterTaxableDeductionsStr() {
        this.netPayAfterTaxableDeductionsStr = (naira+ this.df.format(this.netPayAfterTaxableDeductions));
        return this.netPayAfterTaxableDeductionsStr;
    }

    public String getNetPayAfterTaxableDeductionsStrSansNaira() {
        this.netPayAfterTaxableDeductionsStrSansNaira = this.df.format(this.netPayAfterTaxableDeductions);
        return this.netPayAfterTaxableDeductionsStrSansNaira;
    }

    public String getNetPayAfterTaxesStr() {
        this.netPayAfterTaxesStr = (naira+ this.df.format(this.netPayAfterTaxes));
        return this.netPayAfterTaxesStr;
    }

    public String getNetPayAfterTaxesStrSansNaira() {
        this.netPayAfterTaxesStrSansNaira = this.df.format(this.netPayAfterTaxes);
        return this.netPayAfterTaxesStrSansNaira;
    }

    public String getNetPayStr() {
        this.netPayStr = (naira+ this.df.format(this.netPay));
        return this.netPayStr;
    }

    public String getNetPayStrSansNaira() {
        this.netPayStrSansNaira = this.df.format(this.netPay);
        return this.netPayStrSansNaira;
    }

    public String getNonStandardAllowanceStr() {
        this.nonStandardAllowanceStr = this.df.format(getAcademicAllowance() + getAdminAllowance() + getCallDuty()
                + getCallDuty() + getContractAllowance() + getDomesticServant() + getDriversAllowance()
                + getEntertainment() + getHazard() + getInducement() + getJournal() + getNurseOtherAllowance()
                + getPrincipalAllowance() + getRuralPosting() + getTss());

        return this.nonStandardAllowanceStr;
    }


    public String getOtherAllowanceStr() {
        this.otherAllowanceStr = (naira+ this.df.format(this.otherAllowance));
        return this.otherAllowanceStr;
    }

    public String getOtherAllowanceStrSansNaira() {
        this.otherAllowanceStrSansNaira = this.df.format(this.otherAllowance);
        return this.otherAllowanceStrSansNaira;
    }


    public String getOtherArrearsStr() {
        this.otherArrearsStr = (naira+ this.df.format(this.otherArrears));
        return this.otherArrearsStr;
    }

    public String getOtherArrearsStrSansNaira() {
        this.otherArrearsStrSansNaira = this.df.format(this.otherArrears);
        return this.otherArrearsStrSansNaira;
    }

    public String getOvertimePayStr() {
        this.overtimePayStr = (naira+ this.df.format(this.overtimePay));
        return this.overtimePayStr;
    }

    public String getPayPercentageStr() {
        this.payPercentageStr = (this.payPercentage * 100.0D + "%");
        return this.payPercentageStr;
    }

    public String getPayPeriodStr() {
        this.payPeriodStr = PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(this.runMonth, this.runYear);
        return this.payPeriodStr;
    }
    public String getPayStatusStr() {
        if (this.payStatus != null) {
            if (this.payStatus.equalsIgnoreCase("p")) {
                return "Paid";
            }
            this.payStatusStr = "Pending";
        }

        return this.payStatusStr;
    }

    public String getPrincipalAllowanceStr() {
        if (this.principalAllowance > 0.0D)
            this.principalAllowanceStr = (naira+ this.df.format(this.principalAllowance));
        return this.principalAllowanceStr;
    }

    public String getPrincipalAllowanceStrSansNaira() {
        this.principalAllowanceStrSansNaira = this.df.format(this.principalAllowance);
        return this.principalAllowanceStrSansNaira;
    }

    public String getReliefAmountStrSansNaira() {
        this.reliefAmountStrSansNaira = this.df.format(this.reliefAmount);
        return this.reliefAmountStrSansNaira;
    }

    public String getSalaryDifferenceStr() {
        this.salaryDifferenceStr = (naira+ this.df.format(this.salaryDifference));
        return this.salaryDifferenceStr;
    }

    public String getSalaryDifferenceStrSansNaira() {
        this.salaryDifferenceStrSansNaira = this.df.format(this.salaryDifference);
        return this.salaryDifferenceStrSansNaira;
    }

    public String getSalaryScale() {
        this.salaryScale = getSalaryInfo().getSalaryType().getName();
        return this.salaryScale;
    }

    public String getSpecialAllowanceStr() {
        this.specialAllowanceStr = (naira+ this.df.format(this.specialAllowance));
        return this.specialAllowanceStr;
    }

    public String getSpecialAllowanceStrSansNaira() {
        this.specialAllowanceStrSansNaira = this.df.format(this.specialAllowance);
        return this.specialAllowanceStrSansNaira;
    }

    public String getStandardAllowanceStr() {
        this.standardAllowanceStr = this.df.format(this.getMeal() + getTransport() + getRent() + getUtility());
        return this.standardAllowanceStr;
    }

    public String getTaxableIncomeStr() {
        this.taxableIncomeStr = (naira+ this.df.format(this.taxableIncome));
        return this.taxableIncomeStr;
    }

    public String getTaxableIncomeStrSansNaira() {
        this.taxableIncomeStrSansNaira = this.df.format(this.taxableIncome);
        return this.taxableIncomeStrSansNaira;
    }

    public String getTaxableIncomeYTDStr() {
        taxableIncomeYTDStr = naira+ this.df.format(taxableIncomeYTD);
        return taxableIncomeYTDStr;
    }

    public String getTaxesPaidStr() {
        this.taxesPaidStr = (naira+ this.df.format(this.taxesPaid));
        return this.taxesPaidStr;
    }

    public String getTaxesPaidStrSansNaira() {
        this.taxesPaidStrSansNaira = this.df.format(this.taxesPaid);
        return this.taxesPaidStrSansNaira;
    }


    public String getTaxPaidYTDStr() {
        taxPaidYTDStr = naira+ this.df.format(this.taxPaidYTD);
        return taxPaidYTDStr;
    }

    public String getTotalAllowanceStr() {

        return this.df.format(this.totalAllowance);
    }
    public String getTotalAllowancesStr() {

        return naira+this.df.format(this.totalAllowance);
    }
    public String getTotalAllowPlusBasic() {
        if ((getSalaryInfo() != null) && (!getSalaryInfo().isNewEntity()))
            this.totalAllowPlusBasic = this.df.format(this.totalAllowance + this.monthlyBasic);
        else
            return getTotalAllowanceStr();
        return this.totalAllowPlusBasic;
    }

    public String getTotalContributionsStr() {
        this.totalContributionsStr = (naira+ this.df.format(this.totalContributions));
        return this.totalContributionsStr;
    }

    public String getTotalContributionsStrSansNaira() {
        this.totalContributionsStrSansNaira = this.df.format(this.totalContributions);
        return this.totalContributionsStrSansNaira;
    }

    public String getTotalCostStr() {
        this.totalCostStr = (naira+ this.df.format(this.totalCost));
        return this.totalCostStr;
    }

    public String getTotalDedStr() {
        this.totalDedStr = this.df.format(this.totalDeductions + this.totalGarnishments);
        return this.totalDedStr;
    }

    public String getTotalDeductionsStr() {
        this.totalDeductionsStr = (naira+ this.df.format(this.totalDeductions));
        return this.totalDeductionsStr;
    }

    public Double getTotalDeduction1(){
        this.totalDeduction1 = (getTaxesPaid() + getTotalContributions() + getNulge() + getMahwun() + getNannm() + getNachp());
        return this.totalDeduction1;
    }

    public String getTotalDeductionsStrSansNaira() {
        this.totalDeductionsStrSansNaira = this.df.format(this.totalDeductions);
        return this.totalDeductionsStrSansNaira;
    }

    public String getTotalGarnishmentsStr() {
        this.totalGarnishmentsStr = (naira+ this.df.format(this.totalGarnishments));
        return this.totalGarnishmentsStr;
    }

    public String getTotalGarnishmentsStrSansNaira() {
        this.totalGarnishmentsStrSansNaira = this.df.format(this.totalGarnishments);
        return this.totalGarnishmentsStrSansNaira;
    }

    public String getTotalOtherStrSansNaira() {
        this.totalOtherStrSansNaira = this.df.format(this.totalOther);
        return this.totalOtherStrSansNaira;
    }

    public String getTotalPayStr() {
        this.totalPayStr = (naira+ this.df.format(this.totalPay));
        return this.totalPayStr;
    }

    public String getTotalPayStrSansNaira() {
        this.totalPayStrSansNaira = this.df.format(this.totalPay);
        return this.totalPayStrSansNaira;
    }

    public boolean isHasBonusPay() {
        if (this.bonusPay > 0.0D)
            this.hasBonusPay = true;
        return this.hasBonusPay;
    }

    public boolean isHasCommissionPay() {
        if (this.commissionPay > 0.0D)
            this.hasCommissionPay = true;
        return this.hasCommissionPay;
    }

    public boolean isHasOvertimePay() {
        if (getOvertimePay() > 0.0D)
            this.hasOvertimePay = true;
        return this.hasOvertimePay;
    }

    public abstract boolean isNewEntity();

    public boolean isPaid() {
        if (getPayStatus().equalsIgnoreCase("paid"))
            this.paid = true;
        return this.paid;
    }

    public boolean isPayByDays() {
        if (getPayByDaysInd() == 1)
            this.payByDays = true;
        return this.payByDays;
    }


    public boolean isPercentagePayment() {
        this.percentagePayment = (getPayPercentageInd() == 1);
        return this.percentagePayment;
    }

    public String getMonthlyTaxStr() {
        this.monthlyTaxStr = naira+ this.df.format(this.getMonthlyTax());
        return this.monthlyTaxStr;
    }
    public String getDeductionAmountStr() {
        this.deductionAmountStr = naira+ this.df.format(this.getDeductionAmount());
        return deductionAmountStr;
    }
    public abstract void setId(Long pId);
    public abstract Long getId();
    public abstract void setName(String pName);

    public boolean isUnApprovedPaycheck(){
        this.unApprovedPaycheck =  this.getStatus() != null && this.getStatus().equalsIgnoreCase("p");
        return unApprovedPaycheck;
    }
}
