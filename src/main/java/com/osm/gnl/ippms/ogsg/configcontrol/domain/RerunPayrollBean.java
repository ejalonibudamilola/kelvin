package com.osm.gnl.ippms.ogsg.configcontrol.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ippms_rerun_payroll_ind")
@SequenceGenerator(name = "rerunPayrollIndSeq", sequenceName = "ippms_rerun_payroll_ind_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class RerunPayrollBean implements Serializable {

    @Id
    @GeneratedValue(generator = "rerunPayrollIndSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "rerun_payroll_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "terminations", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfTerminations;
    @Column(name = "overpayments", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfOverPayments;
    @Column(name = "suspensions", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfSuspensions;
    @Column(name = "reinstatements", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfReinstatements;
    @Column(name = "reabsorptions", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfReabsorptions;
    @Column(name = "promotions", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfPromotions;
    @Column(name = "transfers", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfTransfers;
    @Column(name = "contracts", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfContracts;
    @Column(name = "deductions", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfDeductions;
    @Column(name = "loans", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfLoans;
    @Column(name = "special_allowances", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfSpecialAllowances;
    @Column(name = "rerun_ind", nullable = false, columnDefinition = "integer")
    private int rerunInd;
    @Column(name = "approved_employees", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfApprovedEmployees;
    @Column(name = "rejected_employees", nullable = false, columnDefinition = "numeric(5,0)")
    private int noOfRejectedEmployees;
    @Column(name = "run_month", nullable = false, columnDefinition = "numeric(2,0)")
    private int runMonth;
    @Column(name = "run_year", nullable = false, columnDefinition = "numeric(4,0)")
    private int runYear;

    @Transient
    private boolean terminations;
    @Transient
    private boolean overPayments;
    @Transient
    private boolean suspensions;
    @Transient
    private boolean reinstatements;
    @Transient
    private boolean reabsorptions;
    @Transient
    private boolean promotions;
    @Transient
    private boolean transfers;
    @Transient
    private boolean contracts;
    @Transient
    private String terminationsStr;
    @Transient
    private String overPaymentsStr;
    @Transient
    private String suspensionStr;
    @Transient
    private String reinstateStr;
    @Transient
    private String reabsorbStr;
    @Transient
    private String promotionStr;
    @Transient
    private String transferStr;
    @Transient
    private String contractStr;
    @Transient
    private boolean deductions;
    @Transient
    private boolean loans;
    @Transient
    private boolean specialAllowances;
    @Transient
    private String deductionsStr;
    @Transient
    private String loansStr;
    @Transient
    private String specialAllowancesStr;
    @Transient
    private boolean employeeApprovals;
    @Transient
    private boolean employeeRejections;
    @Transient
    private String employeeApprovalsStr;
    @Transient
    private String employeeRejectionsStr;


    public RerunPayrollBean(Long pId) {
        this.id = pId;
    }


    public boolean isTerminations() {
        this.terminations = this.getNoOfTerminations() > 0;
        return terminations;
    }


    public boolean isOverPayments() {
        this.overPayments = this.noOfOverPayments > 0;
        return overPayments;
    }


    public boolean isSuspensions() {
        this.suspensions = this.noOfSuspensions > 0;
        return suspensions;
    }


    public boolean isReinstatements() {
        this.reinstatements = this.noOfReinstatements > 0;
        return reinstatements;
    }


    public boolean isReabsorptions() {
        this.reabsorptions = this.noOfReabsorptions > 0;
        return reabsorptions;
    }


    public boolean isPromotions() {
        this.promotions = this.noOfPromotions > 0;
        return promotions;
    }


    public boolean isTransfers() {
        this.transfers = this.noOfTransfers > 0;
        return transfers;
    }


    public boolean isContracts() {
        this.contracts = this.noOfContracts > 0;
        return contracts;
    }


    public String getTerminationsStr() {
        if (this.terminations)
            this.terminationsStr = this.noOfTerminations + " Terminations were performed.";
        return terminationsStr;
    }


    public String getOverPaymentsStr() {
        if (this.overPayments)
            this.overPaymentsStr = this.noOfOverPayments + " Overpayments were added.";
        return overPaymentsStr;
    }


    public String getSuspensionStr() {
        if (this.suspensions)
            this.suspensionStr = this.noOfSuspensions + " Employees were Suspended.";
        return suspensionStr;
    }


    public String getReinstateStr() {
        if (this.reinstatements)
            this.reinstateStr = this.noOfReinstatements + " Employees were Reinstated.";

        return reinstateStr;
    }


    public String getReabsorbStr() {
        if (this.reabsorptions)
            this.reabsorbStr = this.noOfReabsorptions + " Employees were Reabsorbed.";

        return reabsorbStr;
    }


    public String getPromotionStr() {
        if (this.promotions)
            this.promotionStr = this.noOfPromotions + " Employees were promoted.";

        return promotionStr;
    }


    public String getTransferStr() {
        if (this.transfers)
            this.transferStr = this.noOfTransfers + " Employees were transfered.";

        return transferStr;
    }


    public String getContractStr() {
        if (this.contracts)
            this.contractStr = this.noOfContracts + " Employees were placed on contract.";

        return contractStr;
    }


    public boolean isDeductions() {
        deductions = this.noOfDeductions != 0;
        return deductions;
    }


    public boolean isLoans() {
        loans = this.noOfLoans != 0;
        return loans;
    }


    public boolean isSpecialAllowances() {
        this.specialAllowances = this.noOfSpecialAllowances != 0;
        return specialAllowances;
    }


    public String getDeductionsStr() {
        if (this.deductions)
            this.deductionsStr = this.noOfDeductions + " Employee Deductions Added.";
        return deductionsStr;
    }


    public String getLoansStr() {
        if (this.loans)
            this.loansStr = this.noOfLoans + " Employee Loans Added.";
        return loansStr;
    }


    public String getSpecialAllowancesStr() {
        if (this.specialAllowances) {
            if (this.noOfSpecialAllowances > 0)
                this.specialAllowancesStr = this.noOfSpecialAllowances + " Employee Special Allowances Added.";
            else if (this.noOfSpecialAllowances < 0)
                this.specialAllowancesStr = this.noOfSpecialAllowances + " Employee Special Allowances Removed.";
        }
        return specialAllowancesStr;
    }


    public boolean isEmployeeApprovals() {
        employeeApprovals = this.noOfApprovedEmployees > 0;
        return employeeApprovals;
    }


    public boolean isEmployeeRejections() {
        employeeRejections = this.noOfRejectedEmployees > 0;
        return employeeRejections;
    }


    public String getEmployeeApprovalsStr() {
        if (this.employeeApprovals)
            this.employeeApprovalsStr = this.noOfApprovedEmployees + " Employees Approved For Payroll.";
        return employeeApprovalsStr;
    }


    public String getEmployeeRejectionsStr() {
        if (this.employeeRejections)
            this.employeeRejectionsStr = this.noOfRejectedEmployees + " Employees Rejected For Payroll.";

        return employeeRejectionsStr;
    }


    public boolean isNewEntity() {
        return this.id == null;
    }


}
