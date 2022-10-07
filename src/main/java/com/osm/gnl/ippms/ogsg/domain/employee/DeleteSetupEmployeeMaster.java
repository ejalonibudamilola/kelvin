package com.osm.gnl.ippms.ogsg.domain.employee;

import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_del_setup_emp_master")
@SequenceGenerator(name = "DelSetupEmpMasterSeq", sequenceName = "ippms_del_setup_emp_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class DeleteSetupEmployeeMaster implements Serializable,Comparable<DeleteSetupEmployeeMaster> {

    /**
     *
     */
    private static final long serialVersionUID = 3944774180625378055L;

    @Id
    @GeneratedValue(generator = "DelSetupEmpMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "del_setup_emp_inst_id")
    private Long id;

    @Column(name = "setup_emp_inst_id")
    private Long setupEmpInstId;

    @Column(name = "first_name", length = 25, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 25, nullable = false)
    private String lastName;

    @Column(name = "initials", length = 25)
    private String initials;
    @Column(name = "employee_id", length = 20, nullable = false)
    private String employeeId;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "approved_date", nullable = true)
    private LocalDate approvedDate;
    @Column(name = "approved_by", length = 32, nullable = true)
    private String approvedBy;
    @Column(name = "rejected_status", columnDefinition = "integer default '0'")
    private int rejectedStatus;
    /**
     * We might need to remove this column. Not sure if it is not duplicated by
     * function
     */
    @Column(name = "rejected_ind", columnDefinition = "integer default '0'")
    private int rejectedInd;
    @Column(name = "approved_ind", columnDefinition = "integer default '0'")
    private int approvedInd;
    @Column(name = "deleted_by", length = 32, nullable = true)
    private String deletedBy;


    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs;

    @Column(name = "last_mod_by", nullable = false, length = 32)
    private String lastModBy;
    @Column(name = "pay_group", nullable = false, length = 120)
    private String payGroup;
    @Column(name = "pay_period", nullable = false, length = 6)
    private String payPeriod;

    @Column(name = "mda_name", nullable = false, length = 120)
    private String mdaName;
    @Column(name = "school_name", nullable = true, length = 120)
    private String schoolName;

    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public int compareTo(DeleteSetupEmployeeMaster o) {
        return 0;
    }
}
