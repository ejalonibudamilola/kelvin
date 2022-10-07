package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_login_audit")
@SequenceGenerator(name = "loginAuditSeq", sequenceName = "ippms_login_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class LoginAudit implements Comparable<LoginAudit> {


    @Id
    @GeneratedValue(generator = "loginAuditSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_inst_id", nullable = false)
    private User login;
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;
    @Column(name = "user_name", nullable = false, length = 30)
    private String userName;
    @Column(name = "change_descr", nullable = false, length = 200)
    private String description;
    @Column(name = "audit_time", nullable = false, length = 30)
    private String auditTimeStamp;
    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;
    @Column(name = "login_ip_address",length = 20)
    private String remoteIpAddress;
    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs = LocalDate.now();


    @Transient
    private String changedBy;
    @Transient
    private String auditTime;


    public String getChangedBy() {
        if ((this.login != null) && (!this.login.isNewEntity())) {
            this.changedBy = (this.login.getFirstName() + " " + this.login.getLastName());
        }
        return this.changedBy;
    }

    public String getRemoteIpAddress() {
        if(null == remoteIpAddress)
            remoteIpAddress = "Not Available";
        return remoteIpAddress;
    }

    @Override
    public int compareTo(LoginAudit o) {

        return 0;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }


    public String getAuditTime() {
        if (this.lastModTs != null)
            if (this.auditTimeStamp != null)
                this.auditTime = PayrollHRUtils.getDateFormat().format(this.lastModTs) + " " + this.auditTimeStamp;
            else
                this.auditTime = PayrollHRUtils.getDateFormat().format(this.lastModTs);
        return auditTime;
    }
}