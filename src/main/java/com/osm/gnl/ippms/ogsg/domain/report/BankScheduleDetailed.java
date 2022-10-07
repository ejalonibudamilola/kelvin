package com.osm.gnl.ippms.ogsg.domain.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

/**
 * Kasumu Taiwo
 * 12-2020
 */
@Getter
@Setter
public class BankScheduleDetailed
{
    public String employeeNumber;
    public String employeeName;
    public String bankBranch;
    public String accountNumber;
    public Double payableAmount;
    public LocalDate period;

    public BankScheduleDetailed(String employeeNumber, String employeeName, String bankBranch, String accountNumber, Double payableAmount, LocalDate period) {
        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
        this.bankBranch = bankBranch;
        this.accountNumber = accountNumber;
        this.payableAmount = payableAmount;
        this.period = period;
    }

}