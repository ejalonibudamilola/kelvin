package com.osm.gnl.ippms.ogsg.domain.report;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class GlobalDeduction implements Comparable<GlobalDeduction>
{
	public String employeeName;
        public String employeeNumber;
        public Double contributoryAmount;
        public String deductionName;
        public String agencyName;
        public LocalDate period;
        public String schoolName;

    public GlobalDeduction(String employeeName, String employeeNumber, Double contributoryAmount, String deductionName, String agencyName, LocalDate period, String schoolName) {
        this.employeeName = employeeName;
        this.employeeNumber = employeeNumber;
        this.contributoryAmount = contributoryAmount;
        this.deductionName = deductionName;
        this.agencyName = agencyName;
        this.period = period;
        this.schoolName = schoolName;
    }


    @Override
    public int compareTo(GlobalDeduction o) {
        return 0;
    }
}