package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalarySummaryBean implements Comparable<SalarySummaryBean>{

    private String mdaName;
    private int staffCount;
    private double basicPay;
    private double totalAllowance;
    private double grossPay;
    private double taxPaid;
    private double unionDues;
    private double penContEmp;
    private double rba;
    private double rbaPaid;
    private double totalLoan;
    private double otherDeduction;
    private double totalDeduction;
    private double payableAmount;
    private double monthlyPension;
    private String basicPayStr;
    private String totalAllowanceStr;
    private String grossPayStr;
    private String taxPaidStr;
    private String unionDuesStr;
    private String penContEmpStr;
    private String rbaPaidStr;
    private String totalLoanStr;
    private String otherDeductionStr;
    private String totalDeductionStr;
    private String payableAmountStr;

    public SalarySummaryBean(double rba) {
        this.rba = rba;
    }

    public SalarySummaryBean(String name) {
        this.setMdaName(name);
    }

    public String getBasicPayStr() {
        basicPayStr = PayrollHRUtils.getDecimalFormat().format(this.getBasicPay());
        return basicPayStr;
    }

    public String getTotalAllowanceStr() {
        totalAllowanceStr = PayrollHRUtils.getDecimalFormat().format(this.getTotalAllowance());
        return totalAllowanceStr;
    }

    public String getGrossPayStr() {
        grossPayStr = PayrollHRUtils.getDecimalFormat().format(this.getGrossPay());
        return grossPayStr;
    }

    public String getTaxPaidStr() {
        taxPaidStr = PayrollHRUtils.getDecimalFormat().format(this.getTaxPaid());
        return taxPaidStr;
    }

    public String getUnionDuesStr() {
        unionDuesStr = PayrollHRUtils.getDecimalFormat().format(this.getUnionDues());
        return unionDuesStr;
    }

    public String getPenContEmpStr() {
        penContEmpStr = PayrollHRUtils.getDecimalFormat().format(this.getPenContEmp());
        return penContEmpStr;
    }

    public String getRbaPaidStr() {
        rbaPaidStr =  PayrollHRUtils.getDecimalFormat().format(this.getRbaPaid());
        return rbaPaidStr;
    }

    public String getTotalLoanStr() {
        totalLoanStr =  PayrollHRUtils.getDecimalFormat().format(this.getTotalLoan());
        return totalLoanStr;
    }

    public String getOtherDeductionStr() {
        otherDeductionStr =  PayrollHRUtils.getDecimalFormat().format(this.getOtherDeduction());
        return otherDeductionStr;
    }

    public String getTotalDeductionStr() {
        totalDeductionStr =  PayrollHRUtils.getDecimalFormat().format(this.getTotalDeduction());
        return totalDeductionStr;
    }

    public String getPayableAmountStr() {
        payableAmountStr =  PayrollHRUtils.getDecimalFormat().format(this.getPayableAmount());
        return payableAmountStr;
    }

    @Override
    public int compareTo(SalarySummaryBean salarySummaryBean) {
        return 0;
    }
}
