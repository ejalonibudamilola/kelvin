package com.osm.gnl.ippms.ogsg.report.beans;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractPaymentReportBean extends AbstractReportBean implements Comparable<Object> {

    private double yearToDate;
    private double currentDeduction;
    private double currentGarnishment;
    private String currentGarnishmentStr;
    private String yearToDateStr;
    private String currentDeductionStr;
    private int level;
    private int step;
    private String gradeLevelAndStep;
    private boolean hasTerminatedEmployee;
    private HashMap<String, HRReportBean> deductionMap;
    private String deductionName;
    private double deductionAmount;
    private LocalDate period;
    private int totalBankBranchCount;
    private int totalDeduction;
    private List<String> listBankName;
    private List<Integer> listBranchCount;
    private List<Integer> listDeduction;
    private String bankTotal;
    private String totalAllBanksAount;
    private transient String yearsMonthsAndIdStr;
    private int reportInd;
    private double amountWitheld;
    private String amountWitheldStr;

    public String getYearToDateStr() {
        this.yearToDateStr = this.df.format(this.yearToDate);
        return this.yearToDateStr;
    }
    public String getCurrentDeductionStr() {
        this.currentDeductionStr = this.df.format(this.currentDeduction);
        return this.currentDeductionStr;
    }
    public int getYearAndId() {
        String yearId = this.getYearInt() + "" + this.getId();
        return Integer.parseInt(yearId);
    }
    public HashMap<String, HRReportBean> getDeductionMap() {
        if (null == this.deductionMap)
            deductionMap = new HashMap<String, HRReportBean>();
        return deductionMap;
    }
    public String getGradeLevelAndStep() {
        gradeLevelAndStep = (this.level + " /" + (this.step));
        return gradeLevelAndStep;
    }
}
