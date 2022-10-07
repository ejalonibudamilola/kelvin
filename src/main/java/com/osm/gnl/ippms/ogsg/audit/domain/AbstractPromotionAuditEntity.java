package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractAuditEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractPromotionAuditEntity extends AbstractAuditEntity {

    @Column(name="promotion_date", nullable = false)
    private LocalDate promotionDate;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Employee employee;


    @ManyToOne
    @JoinColumn(name = "old_salary_info_inst_id")
    private SalaryInfo oldSalaryInfo;

    @Column(name = "step_increment_ind" , columnDefinition = "integer default '0'")
    private int stepIncrementInd;

    @Transient private String netIncreaseStr;
    @Transient private double netIncrease;
    @Transient private String promotedBy;
    @Transient private String promotionDateStr;
    @Transient private boolean stepIncrement;
    @Transient  private String oldLevelAndStep;
    @Transient private String newLevelAndStep;
    @Transient private String salaryScale;
    @Transient private String firstName;
    @Transient private String middleName;
    @Transient private String lastName;
    @Transient  private double oldSalary;
    @Transient private double newSalary;
    @Transient private String mdaName;


    public boolean isStepIncrement() {
        stepIncrement = this.stepIncrementInd == 1;
        return stepIncrement;
    }

    public String getPromotionDateStr() {
        if(this.promotionDate != null)
            promotionDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.promotionDate);
        return promotionDateStr;
    }

    public double getNetIncrease() {
        if(this.getSalaryInfo() != null && this.getOldSalaryInfo() != null)
            netIncrease = this.getSalaryInfo().getAnnualSalary() - this.getOldSalaryInfo().getAnnualSalary();
        return netIncrease;
    }

    public String getNetIncreaseStr() {
        if(getSalaryInfo() != null && getOldSalaryInfo() != null)
            netIncreaseStr = IConstants.naira + PayrollHRUtils.getDecimalFormat().format(this.getNetIncrease());
        return netIncreaseStr;
    }
}
