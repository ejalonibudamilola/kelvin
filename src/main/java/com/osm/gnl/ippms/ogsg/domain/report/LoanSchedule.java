package com.osm.gnl.ippms.ogsg.domain.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class LoanSchedule
{
    public String agencyName;
    public String schoolName;
    public String loanName;
    public String employeeName;
    public String employeeNumber;
    public String loanDeduction;
    public String loanBalance;
    public LocalDate period;

    public LoanSchedule() {
    }

    public LoanSchedule(String agencyName, String schoolName, String loanName, String employeeName, String employeeNumber, String loanDeduction, String loanBalance, LocalDate period) {
        this.agencyName = agencyName;
        this.schoolName = schoolName;
        this.loanName = loanName;
        this.employeeName = employeeName;
        this.employeeNumber = employeeNumber;
        this.loanDeduction = loanDeduction;
        this.loanBalance = loanBalance;
        this.period = period;
    }
}