package com.osm.gnl.ippms.ogsg.statistics.domain;

import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.DecimalFormat;

 
@Entity
@Table(name = "ippms_payroll_percentile")
@SequenceGenerator(name = "payrollPercentileSeq", sequenceName = "ippms_payroll_percentile_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MdaPayrollStatistics {

	@Id
	@GeneratedValue(generator = "payrollPercentileSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "payroll_master_inst_id",nullable = false)
	private PayrollRunMasterBean payrollRunMasterBean;

	@ManyToOne
	@JoinColumn(name = "mda_inst_id", nullable = false)
	private MdaInfo mdaInfo;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Column(name = "total_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalPay;
	@Column(name = "net_pay", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double netPay;
	@Column(name = "total_taxes", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalTaxes;
	@Column(name = "total_loans", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalGarnishments;
	@Column(name = "total_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalAllowance;
	@Column(name = "total_spec_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalSpecialAllowance;
	@Column(name = "total_ded", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalDeductions;
	@Column(name = "total_pay_percentile", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalPayPercentile;
	@Column(name = "net_pay_percentile", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double netPayPercentile;
	@Column(name = "total_tax_percentile", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalTaxesPercentile;
	@Column(name = "total_loan_percentile", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalGarnishmentsPercentile;
	@Column(name = "total_allow_percentile", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalAllowancePercentile;
	@Column(name = "total_spec_allow_per", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalSpecialAllowancePercentile;
	@Column(name = "total_ded_percentile", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalDeductionsPercentile;
	@Column(name = "total_staff_percentile", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
	private double totalStaffPercentile;
	@Column(name = "total_staff", columnDefinition = "numeric(9) default '0'", nullable = false)
	private int totalStaff;
	
	@Transient
	private String totalStaffPercentileStr;
	@Transient
	private String totalPayStr;
	@Transient
	private String netPayStr;
	@Transient
	private String totalTaxesStr;
	@Transient
	private String totalGarnishmentsStr;
	@Transient
	private String totalAllowanceStr;
	@Transient
	private String totalSpecialAllowanceStr;
	@Transient
	private String totalDeductionsStr;
	@Transient
	private String totalTaxesPercentileStr;
	@Transient
	private String totalPayPercentileStr;
	@Transient
	private String netPayPercentileStr;
	@Transient
	private String totalGarnPercentileStr;
	@Transient
	private String totalAllowPercentileStr;
	@Transient
	private String totalSpecAllowPercentileStr;
	@Transient
	private String totalDedPercentileStr;
	@Transient
	private DecimalFormat df = new DecimalFormat("#,##0.00");
	@Transient
	private final String ZERO = "0.00";
	@Transient
	private final String ZERO_PERCENT = "0.00%";


	public String getNetPayStr() {
		if(this.getNetPay() > 0.0D) {
			this.netPayStr = this.df.format(this.getNetPay());
		}else {
			this.netPayStr = ZERO;
		}
		return netPayStr;
	}
	
	public String getTotalPayStr() {
		if(this.getTotalPay() > 0.0D) {
			this.totalPayStr = this.df.format(this.getTotalPay());
		}else {
			this.totalPayStr = ZERO;
		}
		return totalPayStr;
	}

	public String getTotalTaxesStr() {
		if(this.getTotalTaxes() > 0.0D) {
			this.totalTaxesStr = this.df.format(this.getTotalTaxes());
		}else {
			this.totalTaxesStr = ZERO;
		}
		return totalTaxesStr;
	}

	public String getTotalGarnishmentsStr() {
		if(this.getTotalGarnishments() > 0.0D) {
			this.totalGarnishmentsStr = this.df.format(this.getTotalGarnishments());
		}else {
			this.totalGarnishmentsStr = ZERO;
		}
		return totalGarnishmentsStr;
	}

	public String getTotalAllowanceStr() {
		if(this.getTotalAllowance() > 0.0D) {
			this.totalAllowanceStr = this.df.format(this.getTotalAllowance());
		}else {
			this.totalAllowanceStr = ZERO;
		}
		return totalAllowanceStr;
	}

	public String getTotalSpecialAllowanceStr() {
		if(this.getTotalSpecialAllowance() > 0.0D) {
			this.totalSpecialAllowanceStr = this.df.format(this.getTotalSpecialAllowance());
		}else {
			this.totalSpecialAllowanceStr = ZERO;
		}
		return totalSpecialAllowanceStr;
	}

	public String getTotalDeductionsStr() {
		if(this.getTotalDeductions() > 0.0D) {
			this.totalDeductionsStr = this.df.format(this.getTotalDeductions());
		}else {
			this.totalDeductionsStr = ZERO;
		}
		return totalDeductionsStr;
	}
	public String getTotalPayPercentileStr() {
		if(this.getTotalPayPercentile() > 0.00D) {
			this.totalPayPercentileStr = this.df.format(this.getTotalPayPercentile());
		}else {
			this.totalPayPercentileStr = ZERO_PERCENT;
		}
		return totalPayPercentileStr;
	}


	public String getNetPayPercentileStr() {
		if(this.getNetPayPercentile() > 0.00D) {
			this.netPayPercentileStr = this.df.format(this.getNetPayPercentile());
		}else {
			this.netPayPercentileStr = ZERO_PERCENT;
		}
		return netPayPercentileStr;
	}


	public void setNetPayPercentileStr(String netPayPercentileStr) {
		this.netPayPercentileStr = netPayPercentileStr;
	}
	public String getTotalTaxesPercentileStr() {
		if(this.getTotalTaxesPercentile() > 0.00D) {
			this.totalDeductionsStr = this.df.format(this.getTotalTaxesPercentile());
		}else {
			this.totalTaxesPercentileStr = ZERO_PERCENT;
		}
		return totalTaxesPercentileStr;
	}

	public String getTotalGarnPercentileStr() {
		if(this.getTotalGarnishmentsPercentile() > 0.00D) {
			this.totalGarnPercentileStr = this.df.format(this.getTotalGarnishmentsPercentile());
		}else {
			this.totalGarnPercentileStr = ZERO_PERCENT;
		}
		return totalGarnPercentileStr;
	}

	public String getTotalAllowPercentileStr() {
		if(this.totalAllowancePercentile > 0.00D) {
			this.totalAllowPercentileStr = this.df.format(this.totalAllowancePercentile);
		}else {
			this.totalAllowPercentileStr = ZERO_PERCENT;
		}
		return totalAllowPercentileStr;
	}

	public String getTotalSpecAllowPercentileStr() {
		if(this.totalSpecialAllowancePercentile > 0.00D) {
			this.totalSpecAllowPercentileStr = this.df.format(this.totalSpecialAllowancePercentile);
		}else {
			this.totalSpecAllowPercentileStr = ZERO_PERCENT;
		}
		return totalSpecAllowPercentileStr;
	}

	public String getTotalDedPercentileStr() {
		if(this.totalDeductionsPercentile > 0.00D) {
			this.totalDedPercentileStr = this.df.format(this.totalDeductionsPercentile);
		}else {
			this.totalDedPercentileStr = ZERO_PERCENT;
		}
		return totalDedPercentileStr;
	}


	public boolean isNewEntity() {
		return this.id == null;
	}


	public String getTotalStaffPercentileStr() {
		if(this.totalStaffPercentile > 0.0D)
			this.totalStaffPercentileStr = df.format(this.totalStaffPercentile);
		else
			this.totalStaffPercentileStr = ZERO_PERCENT;
		return totalStaffPercentileStr;
	}

}
