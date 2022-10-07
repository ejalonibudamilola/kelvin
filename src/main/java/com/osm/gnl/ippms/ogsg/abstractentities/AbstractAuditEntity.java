package com.osm.gnl.ippms.ogsg.abstractentities;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author ola Mustapha All Audit Entities should extend this class.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractAuditEntity<T> implements Serializable, Comparable<T> {


    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs;

    @Column(name = "pay_period", nullable = false, length = 6)
    private String auditPayPeriod;

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

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;


    @Column(name = "audit_time", nullable = false, length = 12)
    protected String auditTime;

    @Transient
    private String employeeName;
    @Transient
    private String userName;
    @Transient
    private String mdaName;
    @Transient
    private String mdaCode;
    @Transient
    protected String reinstatedDateStr;
    @Transient
    private String payGroup;
    @Transient
    private String payGroupLevelAndStep;
    @Transient
    protected int payArrearsInd;
    @Transient
    private double arrearsPercentage;
    @Transient
    private String arrearsPercentageStr;
    @Transient
    private String auditTimeStamp;
    @Transient
    private String placeHolder;
    @Transient
    private String changedBy;

    @Transient
    protected final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public String getAuditTimeStamp() {
        if (this.lastModTs != null)
            if (this.auditTime != null)
                this.auditTimeStamp = (dtf.format(this.lastModTs) + " " + this.auditTime);
            else
                this.auditTimeStamp = dtf.format(this.lastModTs);

        return this.auditTimeStamp;
    }


    public abstract boolean isNewEntity();


}
