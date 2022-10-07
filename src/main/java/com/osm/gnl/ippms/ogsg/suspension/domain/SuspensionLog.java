package com.osm.gnl.ippms.ogsg.suspension.domain;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractAuditEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_suspension_log")
@SequenceGenerator(name = "suspendLogSeq", sequenceName = "ippms_suspension_log_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class SuspensionLog extends AbstractAuditEntity {
    private static final long serialVersionUID = 2623660985306984710L;

    @Id
    @GeneratedValue(generator = "suspendLogSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "suspension_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "suspension_type_inst_id", nullable = false)
    private SuspensionType suspensionType;

    @Column(name = "employee_name", nullable = false, length = 80)
    private String name;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner pensioner;

    @Column(name = "suspension_date", nullable = false)
    private LocalDate suspensionDate;

    @Column(name = "reference_number", nullable = false, length = 80)
    private String referenceNumber;

    @Column(name = "pay_percentage", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double payPercentage;


    @Transient
    private String suspensionDateStr;
    @Transient
    private int salaryLevel;
    @Transient
    private int salaryStep;
    @Transient
    private String salaryScale;
    @Transient
    private String levelAndStep;
    @Transient
    private String payPercentageStr;


    public SuspensionLog(Long pId) {
        this.id = pId;
    }


    public String getSuspensionDateStr() {
        if (this.suspensionDate != null)
            this.suspensionDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.suspensionDate);
        return this.suspensionDateStr;
    }

    public String getLevelAndStep() {
        this.levelAndStep = getLevelAndStepAsStr();
        return this.levelAndStep;
    }

    private String getLevelStr() {
        return String.valueOf(this.salaryLevel);
    }

    private String getStepStr() {
        String stepStr = String.valueOf(this.salaryStep);
        if (stepStr.length() == 1)
            stepStr = "0" + stepStr;
        return stepStr;
    }

    private String getLevelAndStepAsStr() {
        return getLevelStr() + "." + getStepStr();
    }


    public String getPayPercentageStr() {
        if (this.payPercentage > 0.0D)
            this.payPercentageStr = (this.payPercentage + "%");
        else {
            this.payPercentageStr = "";
        }
        return this.payPercentageStr;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

    protected boolean isPensioner() {
          return this.pensioner != null && !this.pensioner.isNewEntity();
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}