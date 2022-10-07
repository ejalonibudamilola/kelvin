package com.osm.gnl.ippms.ogsg.abstractentities;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionCategory;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.swing.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;


@MappedSuperclass
@Getter
@Setter
public abstract class AbstractPaycheckDeductionEntity implements Serializable, Comparable<AbstractPaycheckDeductionEntity> {

    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;


    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;


    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @Column(name = "amount", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double amount;

    @Column(name = "run_month", columnDefinition = "numeric", nullable = false)
    private int runMonth;

    @Column(name = "run_year", columnDefinition = "numeric", nullable = false)
    private int runYear;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "sort_code", columnDefinition = "varchar(20)", nullable = false)
    private String sortCode;

    @Column(name = "account_number", columnDefinition = "varchar(20)")
    private String accountNumber;

    @Transient private String amountStr;
    @Transient private String payDateStr;
    @Transient private String displayStyle;
    @Transient private Long employeeInstId;
    @Transient private String titleField;
    @Transient private String payPeriodStr;
    @Transient private double deductionAmount;
    @Transient private String deductionAmountStr;
    @Transient private String name;
    @Transient private EmpDeductionCategory empDeductionCategory;
    @Transient private EmpDeductionType empDeductionType;
    @Transient private AbstractEmployeeEntity abstractEmployeeEntity;
    @Transient protected final DecimalFormat df = new DecimalFormat("#,##0.00##");
    @Transient private String employeeName;
    @Transient private String schoolName;


    public String getPayDateStr() {
        if (this.payDate != null)
            this.payDateStr = PayrollHRUtils.getFullDateFormat().format(this.payDate);
        return this.payDateStr;
    }

    public String getEmployeeName() {
        if(this.getAbstractEmployeeEntity() != null & this.getAbstractEmployeeEntity().getDisplayName() != null)
            this.employeeName = this.getAbstractEmployeeEntity().getDisplayName();
        return employeeName;
    }

    public String getPayPeriodStr() {
        return PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(this.runMonth, this.runYear);
    }

    public String getAmountStr() {
        if(this.amount > 0)
            this.amountStr = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(this.amount);
        return amountStr;
    }

    public abstract boolean isNewEntity();
    public abstract void setId(Long pId);

    @Override
    public int compareTo(AbstractPaycheckDeductionEntity abstractPaycheckDeductionEntity) {
        return 0;
    }

    public abstract void setEmpDedInfo(AbstractDeductionEntity abstractDeductionEntity);
    public abstract AbstractDeductionEntity getEmpDedInfo();


}
