package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.EmployeeDesignation;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Ola Mustapha
 */
@Entity
@Table(name = "ippms_reassign_dept_log")
@SequenceGenerator(name = "reassignDeptLogSeq", sequenceName = "ippms_reassign_dept_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ReassignEmployeeLog implements Comparable<ReassignEmployeeLog>, Serializable {

    private static final long serialVersionUID = 5531189779544220124L;

    @Id
    @GeneratedValue(generator = "reassignDeptLogSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "reassign_dept_log_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    //-- For LG
    @ManyToOne
    @JoinColumn(name = "old_rank_inst_id")
    private Rank oldRank;

    @ManyToOne
    @JoinColumn(name = "rank_inst_id")
    private Rank rank;

    //--For SUBEB
    @ManyToOne
    @JoinColumn(name = "designation_inst_id")
    private EmployeeDesignation oldDesignation;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner pensioner;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "ref_number", length = 50, nullable = false)
    private String deptRefNumber;

    @Column(name = "ref_date", nullable = false)
    private LocalDate refDate;

    @ManyToOne
    @JoinColumn(name = "old_salary_info_inst_id", nullable = false)
    private SalaryInfo oldSalaryInfo;

    @Column(name = "demotion_ind", columnDefinition = "integer default '0'")
    private int demotionInd;

    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs;

    @Column(name = "pay_period", nullable = false, length = 6)
    private String auditPayPeriod;

    @Column(name = "audit_time", nullable = false, length = 12)
    private String auditTime;

    @ManyToOne
    @JoinColumn(name = "mda_inst_id", nullable = false)
    private MdaInfo mdaInfo;

    @ManyToOne
    @JoinColumn(name = "school_inst_id")
    private SchoolInfo schoolInfo;

    @ManyToOne
    @JoinColumn(name = "salary_info_inst_id", nullable = false)
    private SalaryInfo salaryInfo;

    @ManyToOne
    @JoinColumn(name = "user_inst_id", nullable = false)
    private User user;


    public ReassignEmployeeLog(Long pId) {
        this.id = pId;

    }
    @Transient private AbstractEmployeeEntity abstractEmployeeEntity;
    @Transient private String refDateStr;
    @Transient private String userName;
    @Transient private boolean demotion;
    @Transient private String employeeId;
    @Transient private String displayName;
    @Transient private String assignedToObject;


    public boolean isDemotion() {
        demotion = this.getDemotionInd() == IConstants.ON;
        return demotion;
    }

    public AbstractEmployeeEntity getParentObject(){
        if(this.employee != null && ! this.employee.isNewEntity())
            abstractEmployeeEntity = employee;
        else  if(this.pensioner != null && ! this.pensioner.isNewEntity())
            abstractEmployeeEntity = pensioner;
        if(abstractEmployeeEntity != null)
           return abstractEmployeeEntity;
        return new Employee<>(); //Hopefully this never happens.
    }

    public String getRefDateStr() {
        if (this.getRefDate() != null)
            refDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.getRefDate());
        return refDateStr;
    }

    public String getAuditTime() {
        if (this.getLastModTs() != null)
            this.auditTime = (PayrollHRUtils.getFullDateFormat().format(getLastModTs()) + " " + this.auditTime);
        return this.auditTime;
    }

    public boolean isNewEntity() {

        return this.id == null;
    }


    public int compareTo(ReassignEmployeeLog o) {

        return 0;
    }


    public String getEmployeeId() {
        if(!this.getParentObject().isNewEntity())
           return  this.getParentObject().getEmployeeId();
        return IppmsUtils.treatNull(employeeId);
    }

    public String getDisplayName() {
        if(!this.getParentObject().isNewEntity())
            return  this.getParentObject().getDisplayName();

        return IppmsUtils.treatNull(displayName);
    }

    public String getAssignedToObject() {
        if(!this.getParentObject().isNewEntity())
            return  this.getParentObject().getAssignedToObject();

        return IppmsUtils.treatNull(assignedToObject);

    }


}