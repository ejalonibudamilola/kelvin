package com.osm.gnl.ippms.ogsg.report.beans;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WageSummaryBean extends HRReportBean {
    private static final long serialVersionUID = 281489643217026576L;

    protected double previousBalance;
    protected double currentBalance;
    protected double netDifference;

    protected String previousBalanceStr;
    protected String currentBalanceStr;
    protected String currentBalanceStrSansNaira;
    protected String previousBalanceStrSansNaira;
    protected String netDifferenceStr;
    protected String netDifferenceStrSansNaira;

    protected boolean materialityThreshold;

    protected boolean subvention;
    private boolean loans;
    private boolean taxes;
    private boolean pensionContribution;


    protected double basicSalary;
    protected double totalAllowance;
    protected double grossAmount;
    protected double paye;
    protected double otherDeductions;
    protected double totalDeductions;
    protected double netPay;
    protected double totalAmount;
    protected String basicSalaryStr;
    protected String totalAllowanceStr;
    protected String grossAmountStr;
    protected String payeStr;
    protected String otherDeductionsStr;
    protected String totalDeductionsStr;
    protected String netPayStr;
    protected String totalAmountStr;
    protected double rent;
    protected double transport;
    protected double meal;
    protected double utility;
    protected double inducement;
    protected double ruralPosting;
    protected double medicals;
    protected double hazard;
    protected double hardship;
    protected double callDuty;
    protected double journal;
    protected double domesticServant;
    protected double entertainmentAllowance;
    protected double academicAllowance;
    protected double outfit;
    protected double torchLight;
    protected double tss;
    protected double tws;
    protected double unionDues;
    protected double nhf;
    protected double furniture;
    protected double consolidated;
    protected double securityAllowance;
    protected double driversAllowance;
    protected double adminAllowance;
    protected double otherAllowances;
    private String otherAllowancesStr;
    private String furnitureStr;
    private String consolidatedStr;
    private String securityAllowanceStr;
    private String driversAllowanceStr;
    private String adminAllowanceStr;
    private String rentStr;
    private String transportStr;
    private String mealStr;
    private String utilityStr;
    private String inducementStr;
    private String ruralPostingStr;
    private String medicalsStr;
    private String hazardStr;
    private String hardshipStr;
    private String callDutyStr;
    private String journalStr;
    private String domesticServantStr;
    private String entertainmentAllowanceStr;
    private String academicAllowanceStr;
    private String outfitStr;
    private String torchLightStr;
    private String tssStr;
    private String twsStr;
    private String unionDuesStr;
    private String nhfStr;
    private String basicSalaryAsStr;
    private String totalDeductionsAsStr;
    private String netPayAsStr;
    private String payeAsStr;
    private String grossAmountAsStr;
    private String thisMonth;
    private String previousMonth;
    private String payInd;
    private String monthAndYearStr;
    private String prevMonthAndYearStr;
    private Integer integerId;
    private Long parentKey;
    private Long currentValue;
    private Long countValue;
    private String currentValueStr;
    private List<WageSummaryBean> miniBeanList;
    private List<WageSummaryBean> mdapList;
    private List<WageSummaryBean> mdapFooterList;
    private String emailUrl;

    public WageSummaryBean(String pName) {
        super(pName);
    }

    public String getPreviousBalanceStr() {
        this.previousBalanceStr = (naira + this.df.format(this.previousBalance));
        return this.previousBalanceStr;
    }


    public String getCurrentBalanceStr() {
        this.currentBalanceStr = (naira + this.df.format(this.currentBalance));
        return this.currentBalanceStr;
    }

    public int compareTo(WageSummaryBean pO) {
        if ((this.assignedToObject != null) && (pO.assignedToObject != null)) {
            return String.CASE_INSENSITIVE_ORDER.compare(this.assignedToObject, pO.assignedToObject);
        }
        return this.serialNum - pO.serialNum;
    }

    public int getEmpDiff() {
        return this.noOfEmp - this.previousNoOfEmp;
    }


    public String getBasicSalaryStr() {
        this.basicSalaryStr = (naira + this.df.format(this.basicSalary));
        return this.basicSalaryStr;
    }


    public String getTotalAllowanceStr() {
        this.totalAllowanceStr = (naira + this.df.format(this.totalAllowance));
        return this.totalAllowanceStr;
    }

    public String getGrossAmountStr() {
        this.grossAmountStr = (naira + this.df.format(this.grossAmount));
        return this.grossAmountStr;
    }

    public String getPayeStr() {
        this.payeStr = (naira + this.df.format(this.paye));
        return this.payeStr;
    }

    public String getOtherDeductionsStr() {
        this.otherDeductionsStr = (naira + this.df.format(this.otherDeductions));
        return this.otherDeductionsStr;
    }

    public String getTotalDeductionsStr() {
        this.totalDeductionsStr = (naira + this.df.format(this.totalDeductions));
        return this.totalDeductionsStr;
    }

    public String getNetPayStr() {
        this.netPayStr = (naira + this.df.format(this.netPay));
        return this.netPayStr;
    }

    public String getTotalAmountStr() {
        this.totalAmountStr = (naira + this.df.format(this.totalAmount));
        return this.totalAmountStr;
    }

    public String getRentStr() {
        this.rentStr = this.df.format(this.rent);
        return this.rentStr;
    }

    public String getTransportStr() {
        this.transportStr = this.df.format(this.transport);
        return this.transportStr;
    }

    public String getMealStr() {
        this.mealStr = this.df.format(this.meal);
        return this.mealStr;
    }

    public String getUtilityStr() {
        this.utilityStr = this.df.format(this.utility);
        return this.utilityStr;
    }

    public String getInducementStr() {
        this.inducementStr = this.df.format(this.inducement);
        return this.inducementStr;
    }

    public String getRuralPostingStr() {
        this.ruralPostingStr = this.df.format(this.ruralPosting);
        return this.ruralPostingStr;
    }

    public String getMedicalsStr() {
        this.medicalsStr = this.df.format(this.medicals);
        return this.medicalsStr;
    }

    public String getHazardStr() {
        this.hazardStr = this.df.format(this.hazard);
        return this.hazardStr;
    }

    public String getHardshipStr() {
        this.hardshipStr = this.df.format(this.hardship);
        return this.hardshipStr;
    }

    public String getCallDutyStr() {
        this.callDutyStr = this.df.format(this.callDuty);
        return this.callDutyStr;
    }

    public String getJournalStr() {
        this.journalStr = this.df.format(this.journal);
        return this.journalStr;
    }

    public String getDomesticServantStr() {
        this.domesticServantStr = this.df.format(this.domesticServant);
        return this.domesticServantStr;
    }

    public String getEntertainmentAllowanceStr() {
        this.entertainmentAllowanceStr = this.df.format(this.entertainmentAllowance);
        return this.entertainmentAllowanceStr;
    }

    public String getAcademicAllowanceStr() {
        this.academicAllowanceStr = this.df.format(this.academicAllowance);
        return this.academicAllowanceStr;
    }

    public String getOutfitStr() {
        this.outfitStr = this.df.format(this.outfit);
        return this.outfitStr;
    }

    public String getTorchLightStr() {
        this.torchLightStr = this.df.format(this.torchLight);
        return this.torchLightStr;
    }

    public String getTssStr() {
        this.tssStr = this.df.format(this.tss);
        return this.tssStr;
    }

    public String getTwsStr() {
        this.twsStr = this.df.format(this.tws);
        return this.twsStr;
    }

    public String getUnionDuesStr() {
        this.unionDuesStr = this.df.format(this.unionDues);
        return this.unionDuesStr;
    }

    public String getNhfStr() {
        this.nhfStr = this.df.format(this.nhf);
        return this.nhfStr;
    }

    public String getBasicSalaryAsStr() {
        this.basicSalaryAsStr = this.df.format(this.basicSalary);
        return this.basicSalaryAsStr;
    }

    public String getTotalDeductionsAsStr() {
        this.totalDeductionsAsStr = this.df.format(this.totalDeductions);
        return this.totalDeductionsAsStr;
    }

    public String getNetPayAsStr() {
        this.netPayAsStr = this.df.format(this.netPay);
        return this.netPayAsStr;
    }

    public String getPayeAsStr() {
        this.payeAsStr = this.df.format(this.paye);
        return this.payeAsStr;
    }

    public String getOtherAllowancesStr() {
        this.otherAllowancesStr = this.df.format(this.otherAllowances);
        return this.otherAllowancesStr;
    }

    public String getFurnitureStr() {
        this.furnitureStr = this.df.format(this.furniture);
        return this.furnitureStr;
    }

    public String getConsolidatedStr() {
        this.consolidatedStr = this.df.format(this.consolidated);
        return this.consolidatedStr;
    }

    public String getSecurityAllowanceStr() {
        this.securityAllowanceStr = this.df.format(this.securityAllowance);
        return this.securityAllowanceStr;
    }

    public String getDriversAllowanceStr() {
        this.driversAllowanceStr = this.df.format(this.driversAllowance);
        return this.driversAllowanceStr;
    }

    public String getAdminAllowanceStr() {
        this.adminAllowanceStr = this.df.format(this.adminAllowance);
        return this.adminAllowanceStr;
    }

    public String getGrossAmountAsStr() {
        this.grossAmountAsStr = this.df.format(this.grossAmount);
        return this.grossAmountAsStr;
    }

    public String getCurrentBalanceStrSansNaira() {
        currentBalanceStrSansNaira = this.df.format(currentBalance);
        return currentBalanceStrSansNaira;
    }

    public String getPreviousBalanceStrSansNaira() {
        previousBalanceStrSansNaira = this.df.format(previousBalance);
        return previousBalanceStrSansNaira;
    }

    public double getNetDifference() {
        this.netDifference = this.getCurrentBalance() - this.getPreviousBalance();
        return netDifference;
    }

    public String getNetDifferenceStr() {
        this.netDifferenceStr = naira + df.format(this.getNetDifference());
        return netDifferenceStr;
    }

    public String getNetDifferenceStrSansNaira() {
        this.netDifferenceStrSansNaira = naira + df.format(this.getNetDifference());
        return netDifferenceStrSansNaira;
    }

    public String getCurrentValueStr()
    {
        if (this.currentValue < 1.0D)
            this.currentValueStr = "₦0.00";
        else
            this.currentValueStr = (IConstants.naira + this.df.format(this.currentValue));
        return this.currentValueStr;
    }

}