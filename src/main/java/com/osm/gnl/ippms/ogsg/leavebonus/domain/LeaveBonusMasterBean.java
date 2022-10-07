package com.osm.gnl.ippms.ogsg.leavebonus.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.ViewAttributes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ippms_leave_bonus_master")
@SequenceGenerator(name = "leaveBonusMasterSeq", sequenceName = "ippms_leave_bonus_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class LeaveBonusMasterBean extends ViewAttributes implements Serializable, Comparable<LeaveBonusMasterBean> {

    /**
     *
     */
    private static final long serialVersionUID = 8485639677255201524L;

    @Id
    @GeneratedValue(generator = "leaveBonusMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "leave_bonus_master_inst_id")
    private Long id;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "mda_inst_id")
    private MdaInfo mdaInfo;

    @Column(name = "total_leave_bonus", columnDefinition = "numeric(15,2) default '0.00'")
    private double totalAmountPaid;

    @Column(name = "total_no_of_emp")
    private int totalNoOfEmp;
    @Column(name = "last_mod_by", nullable = false, length = 32)
    private String lastModBy;

    @Column(name = "run_month")
    private int runMonth;
    @Column(name = "run_year")
    private int runYear;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate;
    @Column(name = "created_time", nullable = false, length = 20)
    private String createdTime;
    @Column(name = "approved_ind")
    private int approvedInd;
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "add_to_payroll")
    private int addToPayroll;

    @Column(name = "approved_date")
    private LocalDate approvedDate;

    @Transient
    private String runMonthYearStr;
    @Transient
    private List<?> leaveBonusList;
    @Transient
    private boolean leaveBonusApproved;
    @Transient
    private String approvedByStr;
    @Transient
    private String totalLeaveBonusStr;
    @Transient
    private String name;
    @Transient
    private String mode;
    @Transient
    private User user;


    public LeaveBonusMasterBean(Long pId) {
        this.id = pId;
    }


    public String getTotalLeaveBonusStr() {
        this.totalLeaveBonusStr = PayrollHRUtils.getDecimalFormat().format(this.totalAmountPaid);
        return totalLeaveBonusStr;
    }

    public String getRunMonthYearStr() {
        this.runMonthYearStr = PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(runMonth, runYear);
        return runMonthYearStr;
    }


    public boolean isLeaveBonusApproved() {

        return this.approvedInd == 1;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }


    public String getMode() {
        if (!this.isNewEntity() && !this.getMdaInfo().isNewEntity())
            this.mode = this.getMdaInfo().getId() + ":" + this.getRunYear();
        return mode;
    }


    @Override
    public int compareTo(LeaveBonusMasterBean o) {

        return 0;
    }
}
