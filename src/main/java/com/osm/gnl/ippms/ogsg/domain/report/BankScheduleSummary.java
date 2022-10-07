package com.osm.gnl.ippms.ogsg.domain.report;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
/**
 * Kasumu Taiwo
 * 12-2020
 */
@Getter
@Setter
public class BankScheduleSummary implements Comparable<BankScheduleSummary>
{


    private String bankName;
    private String bankBranch;
    private double payableAmount;
    private String payableAmountStr;
    private LocalDate period;
    private int totalStaff;
    private Long bankBranchId;
    private double totalBankAmount;

    protected DecimalFormat df = new DecimalFormat("#,##0.00");
    
    public BankScheduleSummary(String bankName, @Nullable String bankBranch, double payableAmount, LocalDate period) {
        this.bankName = bankName;
        this.bankBranch = bankBranch;
        this.payableAmount = payableAmount;
        this.period = period;
    }

    public BankScheduleSummary(String bankName, double payableAmount, int totalStaff) {
        this.bankName = bankName;
        this.payableAmount = payableAmount;
        this.totalStaff = totalStaff;
    }
    public BankScheduleSummary(String branchName, double payableAmount, Long bankBranchId) {
        this.bankBranch = branchName;
        this.payableAmount = payableAmount;
        this.bankBranchId = bankBranchId;
    }

    public BankScheduleSummary() {
    }

    public String getPayableAmountStr() {
        this.payableAmountStr = IConstants.naira + this.df.format(getPayableAmount());
        return this.payableAmountStr;
    }


	@Override
	public int compareTo(BankScheduleSummary pO)
	{
		return this.getBankName().compareToIgnoreCase(pO.getBankName());
	}
    
    
}