package com.osm.gnl.ippms.ogsg.domain.suspension;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractAuditEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_reinstatement_log")
@SequenceGenerator(name = "reinstateSeq", sequenceName = "ippms_reinstatement_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ReinstatementLog extends AbstractAuditEntity implements Serializable {
    private static final long serialVersionUID = 8227864169152165193L;

    @Id
    @GeneratedValue(generator = "reinstateSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "reinstatement_inst_id", nullable = false)
    private Long id;

    @Column(name = "approver", nullable = false, length = 50)
    private String approver;
    @Column(name = "employee_name", nullable = false, length = 200)
    private String employeeName;
    @Column(name = "reinstated_date", nullable = false)
    private LocalDate reinstatementDate;
    @Column(name = "arrears_start_date")
    private LocalDate arrearsStartDate;
    @Column(name = "arrears_end_date")
    private LocalDate arrearsEndDate;
    @Column(name = "termination_date", nullable = false)
    private LocalDate terminationDate;
    @Column(name = "reference_number", nullable = false, length = 32)
    private String referenceNumber;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner pensioner;

    @Transient
    private boolean payArrears;
    @Transient
    private String auditTime;

    @Transient
    private String employeeId;
    @Transient
    private String displayName;
    @Transient
    private String assignedToObject;

    public boolean isPayArrears() {
        if (getPayArrearsInd() >= 1)
            this.payArrears = true;
        return this.payArrears;
    }


    public String getReinstatedDateStr() {
        this.reinstatedDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.getReinstatementDate());
        return reinstatedDateStr;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }


    @Override
    public String getAuditTimeStamp() {

        if (this.getLastModTs() != null)
            this.auditTime = (PayrollHRUtils.getFullDateFormat().format(this.getLastModTs()));
        return this.auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    @Override
    public int compareTo(Object that) {

        return String.CASE_INSENSITIVE_ORDER.compare(this.getEmployeeName(), ((ReinstatementLog) that).getEmployeeName());
    }
    public String getEmployeeId() {
        if (this.employee != null && !this.employee.isNewEntity())
            return this.employee.getEmployeeId();
        else if (this.pensioner != null && !this.pensioner.isNewEntity())
            return this.pensioner.getEmployeeId();

        return IppmsUtils.treatNull(employeeId);
    }

    public String getDisplayName() {
        if (this.employee != null && !this.employee.isNewEntity())
            return this.employee.getDisplayName();
        else if (this.pensioner != null && !this.pensioner.isNewEntity())
            return this.pensioner.getDisplayName();

        return IppmsUtils.treatNull(displayName);
    }

    //TODO - Remove when all dependents are identified...

    public String getAssignedToObject() {
        if (this.employee != null && !this.employee.isNewEntity())
            return this.employee.getAssignedToObject();
        else if (this.pensioner != null && !this.pensioner.isNewEntity())
            return this.pensioner.getAssignedToObject();

        return IppmsUtils.treatNull(assignedToObject);

    }
}