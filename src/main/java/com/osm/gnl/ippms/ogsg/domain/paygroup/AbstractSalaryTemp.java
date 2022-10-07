/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.paygroup;

import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollPayUtils;
import com.osm.gnl.ippms.ogsg.utils.annotation.SalaryAllowance;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;

@MappedSuperclass
@Getter
@Setter
public abstract class  AbstractSalaryTemp implements Serializable,Comparable<Object> {


    @Column(name = "master_salary_temp_inst_id", nullable = false)
    private Long parentId;

    @Column(name = "salary_type_inst_id", nullable = false)
    private Long salaryTypeId;

    @Column(name = "salary_level", nullable = false)
    private int level;
    @Column(name = "salary_step", nullable = false)
    private int step;
    @Column(name = "paye", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double paye;

    @Column(name = "pay_group_code", length = 10, nullable = false)
    private String payGroupCode;

    @Column(name = "basic_salary", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double monthlyBasicSalary;

    @SalaryAllowance(type = "Personal Assistant", key = "N")
    @Column(name = "personal_assistant", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double personalAssistant;

    @SalaryAllowance(type = "Motor Vehicle", key = "N")
    @Column(name = "motor_vehicle", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double motorVehicle;

    @SalaryAllowance(type = "Exam Allowance", key = "N")
    @Column(name = "exam_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double examAllowance;

    @SalaryAllowance(type = "LCOS Allowance", key = "N")
    @Column(name = "lcos_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double lcosAllowance;

    @SalaryAllowance(type = "CONS Allowance", key = "N")
    @Column(name = "cons_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double consAllowance;

    @SalaryAllowance(type = "Outfit Allowance", key = "N", forPaySlip = "N")
    @Column(name = "outfit_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double outfitAllowance;

    @SalaryAllowance(type = "Qtrs Allowance", key = "N")
    @Column(name = "quaters_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double quatersAllowance;

    @SalaryAllowance(type = "SPA Allowance", key = "N")
    @Column(name = "spa_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double spaAllowance;

    @SalaryAllowance(type = "Responsibility Allow.", key = "N")
    @Column(name = "responsibility_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double responsibilityAllowance;

    @SalaryAllowance(type = "Security Allowance", key = "Y")
    @Column(name = "security_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double securityAllowance;

    @SalaryAllowance(type = "SWES Allowance", key = "N")
    @Column(name = "swes_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double swesAllowance;

    @SalaryAllowance(type = "Research Allowance", key = "N")
    @Column(name = "research_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double researchAllowance;

    @SalaryAllowance(type = "Totor Allowance", key = "N")
    @Column(name = "totor_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double totorAllowance;

    @SalaryAllowance(type = "Medical Allowance", key = "N")
    @Column(name = "medical_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double medicalAllowance;

    @SalaryAllowance(type = "Sitting Allowance", key = "N")
    @Column(name = "sitting_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double sittingAllowance;

    @SalaryAllowance(type = "Overtime Allowance", key = "N")
    @Column(name = "overtime_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double overtimeAllowance;

    @SalaryAllowance(type = "Tools/Torchlight Allowance", key = "N")
    @Column(name = "tools_torchlight_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double toolsTorchLightAllowance;

    @SalaryAllowance(type = "Uniform Allowance", key = "N")
    @Column(name = "uniform_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double uniformAllowance;

    @SalaryAllowance(type = "Teaching Allowance", key = "N")
    @Column(name = "teaching_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double teachingAllowance;

    @SalaryAllowance(type = "Enhanced Allowance", key = "N")
    @Column(name = "enhanced_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double enhancedHealthAllowance;

    @SalaryAllowance(type = "Spec. Health Allowance", key = "N")
    @Column(name = "special_health_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double specialHealthAllowance;

    @SalaryAllowance(type = "Shift Duty", key = "N")
    @Column(name = "shift_duty", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double shiftDuty;

    @SalaryAllowance(type = "Specialist Allowance", key = "N")
    @Column(name = "specialist_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double specialistAllowance;

    @SalaryAllowance(type = "Admin Allowance", key = "Y",forPaySlip = "N")
    @Column(name = "admin_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double adminAllowance;

    @SalaryAllowance(type = "Call Duty", key = "Y", forPaySlip = "N")
    @Column(name = "call_duty", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double callDuty;

    @SalaryAllowance(type = "Domestic Servant", key = "Y",forPaySlip = "N")
    @Column(name = "domestic_servant", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double domesticServant;

    @SalaryAllowance(type = "Drivers Allowance", key = "Y",forPaySlip = "N")
    @Column(name = "drivers_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double driversAllowance;

    @SalaryAllowance(type = "Entertainment", key = "Y",forPaySlip = "N")
    @Column(name = "entertainment", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double entertainment;

    @SalaryAllowance(type = "Furniture", key = "Y",forPaySlip = "N")
    @Column(name = "furniture", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double furniture;

    @SalaryAllowance(type = "Hazard", key = "Y",forPaySlip = "N")
    @Column(name = "hazard", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double hazard;

    @SalaryAllowance(type = "Inducement", key = "Y",forPaySlip = "N")
    @Column(name = "inducement", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double inducement;

    @SalaryAllowance(type = "Journal", key = "Y",forPaySlip = "N")
    @Column(name = "journal", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double journal;

    @SalaryAllowance(type = "Meal", key = "Y",forPaySlip = "N")
    @Column(name = "meal", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double meal;

    @Column(name = "nhf", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double nhf;

    @SalaryAllowance(type = "Nurse Other Allow.", key = "Y",forPaySlip = "N")
    @Column(name = "nurse_other_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double nurseOtherAllowance;

    @SalaryAllowance(type = "Rent", key = "Y",forPaySlip = "N")
    @Column(name = "rent", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double rent;

    @SalaryAllowance(type = "Rural Posting", key = "Y",forPaySlip = "N")
    @Column(name = "rural_posting", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double ruralPosting;

    @SalaryAllowance(type = "Transport", key = "Y",forPaySlip = "N")
    @Column(name = "transport", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double transport;

    @Column(name = "tss", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double tss;

    @Column(name = "tws", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double tws;

    @Column(name = "union_dues", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double unionDues;

    @SalaryAllowance(type = "Utility", key = "Y",forPaySlip = "N")
    @Column(name = "utility", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double utility;
    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @Column(name = "last_mod_ts", nullable = false)
    private Timestamp lastModTs;

    @Column(name = "last_mod_by", nullable = false, length = 50)
    private Long lastModBy;

    @Column(name = "approved_ind", columnDefinition = "numeric(1) default '0'")
    private int approvedInd;

    @Transient private String monthlyGrossPayWivNairaStr;
    @Transient protected double basicMonthlySalary;
    @Transient private String annualSalaryStr;
    @Transient private String annualSalaryStrWivNaira;
    @Transient private String annualTaxAmountStr;
    @Transient private String consAllowanceStr;
    @Transient private String consAllowanceStrSansNaira;
    @Transient private double consolidateAllowanceSansCallDuty;
    @Transient private double consolidated;
    @Transient private double consolidatedAllowance;
    @Transient private String consolidatedAllowanceStr;
    @Transient private double consolidatedMonthlyAllowance;
    @Transient private String consolidatedStr;
    @Transient private String consolidatedStrSansNaira;
    @Transient private String examAllowanceStr;
    @Transient private String examAllowanceStrSansNaira;
    @Transient private double hardship;
    @Transient private String hardshipStr;
    @Transient private String hardshipStrSansNaira;
    @Transient private String lcosAllowanceStr;
    @Transient private String lcosAllowanceStrSansNaira;
    @Transient private String levelStepStr;
    @Transient private String levelStr;
    @Transient private String medicalAllowanceStr;
    @Transient private String medicalAllowanceStrSansNaira;
    @Transient private double medicals;
    @Transient private String medicalsStr;
    @Transient private String medicalsStrSansNaira;
    @Transient private String monthlyBasicSalaryStr;
    @Transient private double monthlyBasicSalaryDouble;
    @Transient private String monthlyBasicSalaryStrSansNaira;
    @Transient private String monthlyConsolidatedAllowanceStr;
    @Transient private double monthlyGrossPay;
    @Transient private String monthlyGrossPayStr;
    @Transient private double monthlySalary;
    @Transient private String monthlySalaryStr;
    @Transient private String monthlyTaxAmountStr;
    @Transient private String motorVehicleStr;
    @Transient private String motorVehicleStrSansNaira;
    @Transient private double netPaySansDeductions;
    @Transient private double outfit;
    @Transient private String outfitAllowanceStr;
    @Transient private String outfitAllowanceStrSansNaira;
    @Transient private String outfitStr;
    @Transient private String outfitStrSansNaira;
    @Transient private String overtimeAllowanceStr;
    @Transient private String overtimeAllowanceStrSansNaira;
    @Transient private String partBasicSalaryStr;
    @Transient private double partBasicSalary;
    @Transient private String partConsolidatedAllowanceStr;
    @Transient private String personalAssistantStr;
    @Transient private String personalAssistantStrSansNaira;
    @Transient private String quatersAllowanceStr;
    @Transient private String quatersAllowanceStrSansNaira;
    @Transient private String researchAllowanceStr;
    @Transient private String researchAllowanceStrSansNaira;
    @Transient private String responsibilityAllowanceStr;
    @Transient private String responsibilityAllowanceStrSansNaira;
    @Transient private String salaryScaleLevelAndStepStr;
    @Transient private String salaryTypeLevelAndStep;
    @Transient private String sittingAllowanceStr;
    @Transient private String sittingAllowanceStrSansNaira;
    @Transient private String spaAllowanceStr;
    @Transient private String spaAllowanceStrSansNaira;
    @Transient private double standardDeductions;
    @Transient private String stepStr;
    @Transient protected String swesAllowanceStr;
    @Transient private String swesAllowanceStrSansNaira;
    @Transient private String taxPercentageStr;
    @Transient private double torchLight;
    @Transient private String torchLightStr;
    @Transient private String torchLightStrSansNaira;
    @Transient private double totalDedAmount;
    @Transient private double totalGrossPay;
    @Transient private String totalGrossPayStr;
    @Transient private String totalMonthlySalaryStr;
    @Transient private String totorAllowanceStr;
    @Transient private String totorAllowanceStrSansNaira;
    @Transient private String uniformAllowanceStr;
    @Transient private String uniformAllowanceStrSansNaira;
    @Transient private double xmagr;
    @Transient private String xmagrStr;
    @Transient private String xmagrStrSansNaira;
    @Transient private double xmas;
    @Transient private String xmasStr;
    @Transient private String xmasStrSansNaira;
    @Transient private String teachingAllowanceStr;
    @Transient private String teachingAllowanceStrSansNaira;
    @Transient private String enhancedHealthAllowanceStr;
    @Transient private String enhancedHealthAllowanceStrSansNaira;
    @Transient private String specialHealthAllowanceStr;
    @Transient private String specialHealthAllowanceStrSansNaira;
    @Transient private String shiftDutyStr;
    @Transient private String shiftDutyStrSansNaira;
    @Transient private String specialistAllowanceStr;
    @Transient private String specialistAllowanceStrSansNaira;
    @Transient private String totalMonthlySalaryStrSansNaira;
    @Transient private boolean permanentSecretary;
    @Transient private double annualSalary;
    @Transient private String monthlyGrossStr;
    @Transient private String monthlyGrossSalaryStr;
    @Transient private String monthlyGrossSalaryWivNairaStr;
    @Transient private String netPayStr;

    @Transient protected final String naira = "\u20A6";
    @Transient private boolean hasErrors;


    /**
     * Use to represent the Concrete Classes ID value for HQL Queries
     */
    @Transient
    protected Long objectInstId;
    @Transient
    private String totalAllowanceStr;
    @Transient
    protected DecimalFormat df = new DecimalFormat("#,##0.00");
    @Transient
    private String academicAllowanceStr;
    @Transient
    private String toolsTorchLightAllowanceStr;
    @Transient
    private String academicAllowanceStrSansNaira;
    @Transient
    private String entertainmentStr;
    @Transient
    private String adminAllowanceStr;
    @Transient
    private String entertainmentStrSansNaira;
    @Transient
    private String furnitureStr;
    @Transient
    private String furnitureStrSansNaira;
    @Transient
    private String hazardStr;
    @Transient
    private String hazardStrSansNaira;
    @Transient
    private String callDutyStr;
    @Transient
    private String domesticServantStr;
    @Transient
    private String callDutyStrSansNaira;
    @Transient
    private String inducementStr;
    @Transient
    private String inducementStrSansNaira;
    @Transient
    private String journalStr;
    @Transient
    private String journalStrSansNaira;
    @Transient
    private String mealStr;
    @Transient
    private String mealStrSansNaira;
    @Transient
    private String nhfStr;
    @Transient
    private String nhfStrSansNaira;
    @Transient
    private String nurseOtherAllowanceStr;
    @Transient
    private String nurseOtherAllowanceStrSansNaira;
    @Transient
    private String rentStr;
    @Transient
    private String rentStrSansNaira;
    @Transient
    private String ruralPostingStr;
    @Transient
    private String ruralPostingStrSansNaira;
    @Transient
    private String transportStr;
    @Transient
    private String transportStrSansNaira;
    @Transient
    private String tssStr;
    @Transient
    private String tssStrSansNaira;
    @Transient
    private String unionDuesStr;
    @Transient
    private String unionDuesStrSansNaira;
    @Transient
    private String utilityStr;
    @Transient
    private String utilityStrSansNaira;
    @Transient
    private String twsStr;
    @Transient
    private String twsStrSansNaira;
    @Transient
    private String securityAllowanceStr;
    @Transient
    private String securityAllowanceStrSansNaira;
    @Transient
    private String name;
    @Transient
    private String description;
    @Transient
    private boolean warningIssued;
    @Transient
    private String displayStyle;
    @Transient
    private String generatedCaptcha;
    @Transient
    private String displayErrors;
    @Transient
    private String enteredCaptcha;
    @Transient
    private boolean captchaError;
    @Transient
    private String annualGrossAmount;
    @Transient
    private String driversAllowanceStr;

    public abstract boolean isNewEntity();

    public String getConsolidatedAllowanceStr() {
        this.consolidatedAllowanceStr = df.format(this.consAllowance);
        return this.consolidatedAllowanceStr;
    }

    public String getConsolidatedStr() {
        this.consolidatedStr = df.format(this.consolidated);
        return this.consolidatedStr;
    }

    public String getConsolidatedStrSansNaira() {
        this.consolidatedStrSansNaira = df.format(this.consolidated);
        return this.consolidatedStrSansNaira;
    }


    public String getHardshipStrSansNaira() {
        this.hardshipStrSansNaira = df.format(getHardship());
        return this.hardshipStrSansNaira;
    }


    public String getLevelAndStepAsStr() {
        return getLevelStr() + "/" + getStepStr();
    }

    public String getLevelStepStr() {
        this.levelStepStr = getLevelAndStepAsStr();
        return this.levelStepStr;
    }

    public String getLevelStr() {
        this.levelStr = String.valueOf(this.level);
        return this.levelStr;
    }

    public String getMedicalsStrSansNaira() {
        this.medicalsStrSansNaira = df.format(getMedicals());
        return this.medicalsStrSansNaira;
    }


    public String getMonthlyBasicSalaryStr() {
        this.monthlyBasicSalaryStr = (naira + df
                .format(getMonthlyBasicSalary()));
        return this.monthlyBasicSalaryStr;
    }

    public Double getMonthlyBasicSalaryDouble() {
        this.monthlyBasicSalaryDouble = EntityUtils.convertDoubleToEpmStandard(getMonthlyBasicSalary());
        return this.monthlyBasicSalaryDouble;
    }

    public String getMonthlyBasicSalaryStrSansNaira() {
        this.monthlyBasicSalaryStrSansNaira = df
                .format(EntityUtils.convertDoubleToEpmStandard(getMonthlyBasicSalary()));
        return this.monthlyBasicSalaryStrSansNaira;
    }

    public String getMonthlyConsolidatedAllowanceStr() {
        this.monthlyConsolidatedAllowanceStr = df
                .format(getConsolidatedAllowance() / 12.0D);
        return this.monthlyConsolidatedAllowanceStr;
    }


    public String getMonthlyGrossPayStr() {
        this.monthlyGrossPayStr = df.format(getMonthlyGrossPay());
        return this.monthlyGrossPayStr;
    }
    public String getMonthlyGrossPayWivNairaStr() {
        this.monthlyGrossPayWivNairaStr = naira+df.format(getMonthlyGrossPay());
        return this.monthlyGrossPayWivNairaStr;
    }
    public double getMonthlySalary() {
        monthlySalary = new java.math.BigDecimal(this.getTotalGrossPay() / 12.0D).setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();

        return monthlySalary;
    }

    public String getMonthlySalaryStr() {
        if (this.monthlySalaryStr == null) {
            this.monthlySalaryStr = df.format(getMonthlyBasicSalary());
        }
        return this.monthlySalaryStr;
    }

    public double getNetPaySansDeductions() {
        this.netPaySansDeductions = (getConsolidatedAllowance() - (getStandardDeductions() + this.getPaye()));
        return this.netPaySansDeductions;
    }

    public String getOutfitStrSansNaira() {
        this.outfitStrSansNaira = df.format(getOutfit());
        return this.outfitStrSansNaira;
    }

    public String getPartBasicSalaryStr(int pNoOfDays, int pRunMonth, int pRunYear) {
        this.partBasicSalaryStr = df
                .format(PayrollPayUtils.getPartPayment(getMonthlyBasicSalary(), pNoOfDays, pRunMonth, pRunYear, true));
        return this.partBasicSalaryStr;
    }

    public Double getPartBasicSalary(int pNoOfDays, int pRunMonth, int pRunYear) {
        this.partBasicSalary = PayrollPayUtils.getPartPayment(getMonthlyBasicSalary(), pNoOfDays, pRunMonth, pRunYear, true);
        return this.partBasicSalary;
    }

    public String getNetPayStr() {
        this.netPayStr = this.df.format(getNetPaySansDeductions());
        return this.netPayStr;
    }



    public String getPartConsolidatedAllowanceStr(int pNoOfDays, int pRunMonth, int pRunYear) {
        this.partConsolidatedAllowanceStr = df.format(
                PayrollPayUtils.getPartPayment(getConsolidatedAllowance(), pNoOfDays, pRunMonth, pRunYear, true));

        return this.partConsolidatedAllowanceStr;
    }

    /*
     * returns PayGroup : Level.Step
     *

    public String getSalaryScaleLevelAndStepStr() {
        this.salaryScaleLevelAndStepStr = (this.getMasterSalaryTemp().getSalaryType().getName() + " : " + getLevelStepStr());
        return this.salaryScaleLevelAndStepStr;
    }
   */
    public double getStandardDeductions() {
        this.standardDeductions = (getNhf() + getUnionDues());
        return this.standardDeductions;
    }

    public String getStepStr() {
        this.stepStr = String.valueOf(this.step);
        if (this.stepStr.length() == 1)
            this.stepStr = ("0" + this.stepStr);
        return this.stepStr;
    }

    public String getTorchLightStrSansNaira() {
        this.torchLightStrSansNaira = df.format(getTorchLight());
        return this.torchLightStrSansNaira;
    }

    public double getTotalGrossPay() {
        this.totalGrossPay = (getConsolidatedAllowance() + getMonthlyBasicSalary());
        return this.totalGrossPay;
    }

    public String getTotalMonthlySalaryStr() {
        totalMonthlySalaryStr = naira + df.format(this.getMonthlySalary());
        return totalMonthlySalaryStr;
    }

    public String getUniformAllowanceStr() {
        this.uniformAllowanceStr = naira
                + df.format(this.getUniformAllowance());

        return uniformAllowanceStr;
    }

    public String getUniformAllowanceStrSansNaira() {
        this.uniformAllowanceStrSansNaira = df.format(this.getUniformAllowance());

        return uniformAllowanceStrSansNaira;
    }

    public String getXmagrStr() {
        this.xmagrStr = (naira + df.format(getXmagr()));
        return this.xmagrStr;
    }

    public String getXmagrStrSansNaira() {
        this.xmagrStrSansNaira = df.format(getXmagr());
        return this.xmagrStrSansNaira;
    }

    public String getXmasStr() {
        this.xmasStr = (naira + df.format(getXmas()));
        return this.xmasStr;
    }

    public String getXmasStrSansNaira() {
        this.xmasStrSansNaira = df.format(getXmas());
        return this.xmasStrSansNaira;
    }

    public void setMonthlyGrossPay(double pMonthlyGrossPay) {
        this.monthlyGrossPay = (getMonthlyBasicSalary() + getConsolidatedAllowance());
        this.monthlyGrossPay = pMonthlyGrossPay;
    }

    public String getTotalMonthlySalaryStrSansNaira() {
        totalMonthlySalaryStrSansNaira = df.format(this.getMonthlySalary());
        return totalMonthlySalaryStrSansNaira;
    }

    public String getTeachingAllowanceStr() {
        teachingAllowanceStr = (naira
                + df.format(this.getTeachingAllowance()));

        return teachingAllowanceStr;
    }

    public String getTeachingAllowanceStrSansNaira() {
        teachingAllowanceStrSansNaira = (df.format(this.getTeachingAllowance()));

        return teachingAllowanceStrSansNaira;
    }

    public String getEnhancedHealthAllowanceStr() {
        enhancedHealthAllowanceStr = (naira
                + df.format(this.getEnhancedHealthAllowance()));

        return enhancedHealthAllowanceStr;
    }

    public String getEnhancedHealthAllowanceStrSansNaira() {
        enhancedHealthAllowanceStrSansNaira = (df
                .format(this.getEnhancedHealthAllowance()));

        return enhancedHealthAllowanceStrSansNaira;
    }

    public String getSpecialHealthAllowanceStr() {
        specialHealthAllowanceStr = (naira
                + df.format(this.getSpecialHealthAllowance()));

        return specialHealthAllowanceStr;
    }

    public String getSpecialHealthAllowanceStrSansNaira() {
        specialHealthAllowanceStrSansNaira = (df
                .format(this.getSpecialHealthAllowance()));

        return specialHealthAllowanceStrSansNaira;
    }

    public String getShiftDutyStr() {
        shiftDutyStr = (naira + df.format(this.getShiftDuty()));

        return shiftDutyStr;
    }

    public String getShiftDutyStrSansNaira() {
        shiftDutyStrSansNaira = (df.format(this.getShiftDuty()));
        return shiftDutyStrSansNaira;
    }

    public String getSpecialistAllowanceStr() {
        specialistAllowanceStr = (naira
                + df.format(this.getSpecialistAllowance()));
        return specialistAllowanceStr;
    }

    public String getSpecialistAllowanceStrSansNaira() {
        specialistAllowanceStrSansNaira = (df.format(this.getSpecialistAllowance()));

        return specialistAllowanceStrSansNaira;
    }


    public String getTotalGrossPayStr() {
        this.totalGrossPayStr = naira + df.format(this.getTotalGrossPay());
        return totalGrossPayStr;
    }


    public String getCallDutyStr() {
        this.callDutyStr = (naira + this.df.format(this.callDuty));
        return this.callDutyStr;
    }

    public String getDomesticServantStr() {
        this.domesticServantStr = (naira + this.df.format(this.domesticServant));
        return this.domesticServantStr;
    }
    public String getDriversAllowanceStr() {
        this.driversAllowanceStr = (naira + this.df.format(this.driversAllowance));
        return this.driversAllowanceStr;
    }


    public String getCallDutyStrSansNaira() {
        this.callDutyStrSansNaira = this.df.format(this.callDuty);
        return this.callDutyStrSansNaira;
    }


    public String getEntertainmentStrSansNaira() {
        this.entertainmentStrSansNaira = this.df.format(this.entertainment);
        return this.entertainmentStrSansNaira;
    }

    public String getFurnitureStr() {
        this.furnitureStr = (naira + this.df.format(this.furniture));
        return this.furnitureStr;
    }

    public String getFurnitureStrSansNaira() {
        this.furnitureStrSansNaira = this.df.format(this.furniture);
        return this.furnitureStrSansNaira;
    }

    public String getHazardStr() {
        this.hazardStr = (naira + this.df.format(this.hazard));
        return this.hazardStr;
    }

    public String getHazardStrSansNaira() {
        this.hazardStrSansNaira = this.df.format(this.hazard);
        return this.hazardStrSansNaira;
    }

    public String getInducementStr() {
        this.inducementStr = (naira + this.df.format(this.inducement));
        return this.inducementStr;
    }

    public String getInducementStrSansNaira() {
        this.inducementStrSansNaira = this.df.format(this.inducement);
        return this.inducementStrSansNaira;
    }

    public String getJournalStr() {
        this.journalStr = (naira + this.df.format(this.journal));
        return this.journalStr;
    }

    public String getJournalStrSansNaira() {
        this.journalStrSansNaira = this.df.format(this.journal);
        return this.journalStrSansNaira;
    }

    public String getMealStr() {
        this.mealStr = (naira + this.df.format(this.meal));
        return this.mealStr;
    }

    public String getMotorVehicleStr() {
        this.motorVehicleStr = (naira + this.df.format(this.motorVehicle));
        return this.motorVehicleStr;
    }
    public String getExamAllowanceStr() {
        this.examAllowanceStr = (naira + this.df.format(this.examAllowance));
        return this.examAllowanceStr;
    }

    public String getlcosAllowanceStr() {
        this.lcosAllowanceStr = (naira + this.df.format(this.lcosAllowance));
        return this.lcosAllowanceStr;
    }

    public String getConsAllowanceStr() {
        this.consAllowanceStr = (naira + this.df.format(this.consAllowance));
        return this.consAllowanceStr;
    }
    public String getOutfitAllowanceStr() {
        this.outfitAllowanceStr = (naira + this.df.format(this.outfitAllowance));
        return this.outfitAllowanceStr;
    }

    public String getQuartersAllowanceStr() {
        this.quatersAllowanceStr = (naira + this.df.format(this.quatersAllowance));
        return this.quatersAllowanceStr;
    }
    public String getSpaAllowanceStr() {
        this.spaAllowanceStr = (naira + this.df.format(this.spaAllowance));
        return this.spaAllowanceStr;
    }

    public String getResponsibilityAllowanceStr() {
        this.responsibilityAllowanceStr = (naira + this.df.format(this.responsibilityAllowance));
        return this.responsibilityAllowanceStr;
    }

    public String getResearchAllowanceStr() {
        this.researchAllowanceStr = (naira + this.df.format(this.researchAllowance));
        return this.researchAllowanceStr;
    }
    public String getTotorAllowanceStr() {
        this.totorAllowanceStr = (naira + this.df.format(this.totorAllowance));
        return this.totorAllowanceStr;
    }

    public String getMedicalAllowanceStr() {
        this.medicalAllowanceStr = (naira + this.df.format(this.medicalAllowance));
        return this.medicalAllowanceStr;
    }

    public String getSittingAllowanceStr() {
        this.sittingAllowanceStr = (naira + this.df.format(this.sittingAllowance));
        return this.sittingAllowanceStr;
    }

    public String getOvertimeAllowanceStr() {
        this.overtimeAllowanceStr = (naira + this.df.format(this.overtimeAllowance));
        return this.overtimeAllowanceStr;
    }
    public String getToolsTorchLightAllowanceStr() {
        this.toolsTorchLightAllowanceStr = (naira + this.df.format(this.toolsTorchLightAllowance));
        return this.toolsTorchLightAllowanceStr;
    }

    public String getMealStrSansNaira() {
        this.mealStrSansNaira = this.df.format(this.meal);
        return this.mealStrSansNaira;
    }

    public String getNhfStr() {
        this.nhfStr = (naira + this.df.format(this.nhf));
        return this.nhfStr;
    }

    public String getNhfStrSansNaira() {
        this.nhfStrSansNaira = this.df.format(this.nhf);
        return this.nhfStrSansNaira;
    }

    public String getEntertainmentStr() {
        this.entertainmentStr = (naira + this.df.format(this.entertainment));
        return this.entertainmentStr;
    }

    public String getRentStr() {
        this.rentStr = (naira + this.df.format(this.rent));
        return this.rentStr;
    }

    public String getRentStrSansNaira() {
        this.rentStrSansNaira = this.df.format(this.rent);
        return this.rentStrSansNaira;
    }

    public String getRuralPostingStr() {
        this.ruralPostingStr = (naira + this.df.format(this.ruralPosting));
        return this.ruralPostingStr;
    }

    public String getRuralPostingStrSansNaira() {
        this.ruralPostingStrSansNaira = this.df.format(this.ruralPosting);
        return this.ruralPostingStrSansNaira;
    }

    public String getUnionDuesStr() {
        this.unionDuesStr = (naira + this.df.format(this.unionDues));
        return this.unionDuesStr;
    }

    public String getUnionDuesStrSansNaira() {
        this.unionDuesStrSansNaira = this.df.format(this.unionDues);
        return this.unionDuesStrSansNaira;
    }

    public String getTransportStrSansNaira() {
        this.transportStrSansNaira = this.df.format(this.transport);
        return this.transportStrSansNaira;
    }

    public String getTransportStr() {
        this.transportStr = (naira + this.df.format(this.transport));
        return this.transportStr;
    }


    public String getTwsStrSansNaira() {
        this.twsStrSansNaira = this.df.format(this.tws);
        return this.twsStrSansNaira;
    }

    public String getTssStrSansNaira() {
        this.tssStrSansNaira = this.df.format(this.tss);
        return this.tssStrSansNaira;
    }


    public String getSecurityAllowanceStr() {
        this.securityAllowanceStr = (naira + this.df.format(this.securityAllowance));
        return this.securityAllowanceStr;
    }

    public String getSwesAllowanceStr() {
        this.swesAllowanceStr = (naira + this.df.format(this.swesAllowance));
        return this.swesAllowanceStr;
    }

    public String getSecurityAllowanceStrSansNaira() {
        this.securityAllowanceStrSansNaira = this.df.format(this.securityAllowance);
        return this.securityAllowanceStrSansNaira;
    }


    public String getNurseOtherAllowanceStrSansNaira() {
        this.nurseOtherAllowanceStrSansNaira = this.df.format(this.nurseOtherAllowance);
        return this.nurseOtherAllowanceStrSansNaira;
    }

    public String getNurseOtherAllowanceStr() {
        this.nurseOtherAllowanceStr = (naira + this.df.format(this.nurseOtherAllowance));
        return this.nurseOtherAllowanceStr;
    }

    public String getUtilityStr() {
        this.utilityStr = (naira + this.df.format(this.utility));
        return this.utilityStr;
    }

    public String getUtilityStrSansNaira() {
        this.utilityStrSansNaira = this.df.format(this.utility);
        return this.utilityStrSansNaira;
    }

    public double getBasicMonthlySalary() {
        if (this.getMonthlyBasicSalary() > 0) {
            this.basicMonthlySalary = EntityUtils.convertDoubleToEpmStandard(this.basicMonthlySalary / 12.0D);
        }
        return basicMonthlySalary;
    }

//    public boolean isPermanentSecretary() {
//        permanentSecretary = this.getMasterSalaryTemp().getSalaryType().getConsolidatedInd() == IConstants.ON;
//        return permanentSecretary;
//    }

    public String getAnnualSalaryStr() {
        annualSalaryStr = this.df.format(this.getAnnualSalary());
        return annualSalaryStr;
    }

    public String getAdminAllowanceStr() {
        this.adminAllowanceStr = this.df.format(this.adminAllowance);
        return adminAllowanceStr;
    }


//    public double getAnnualSalary() {
//        annualSalary = IppmsUtilsExt.getAnnualSalary(this);
//        return annualSalary;
//    }

    public String getMonthlyGrossSalaryStr() {
        monthlyGrossSalaryStr = this.df.format(this.getAnnualSalary()/12.0D);
        return monthlyGrossSalaryStr;
    }

    public String getAnnualSalaryStrWivNaira() {
        annualSalaryStrWivNaira = naira + this.getAnnualSalaryStr();
        return annualSalaryStrWivNaira;
    }

    public String getMonthlyGrossSalaryWivNairaStr() {
        monthlyGrossSalaryWivNairaStr = naira + this.df.format(this.getAnnualSalary()/12.0D);
        return monthlyGrossSalaryWivNairaStr;
    }

}
