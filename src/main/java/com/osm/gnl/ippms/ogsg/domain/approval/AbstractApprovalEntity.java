/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.approval;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.*;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractApprovalEntity  implements Serializable {


    @Column(name = "entity_name", length = 50, nullable = false)
    private String entityName;

    @Column(name = "employee_id", length = 20 , nullable = false)
    private String employeeId;

    @Column(name = "entity_inst_id", nullable = false)
    private Long entityId;

    @Column(name = "business_client_inst_id", nullable =  false)
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "user_inst_id", nullable = false)
    protected User initiator;

    @Column(name = "initiated_date" , nullable = false)
    protected LocalDate initiatedDate;

    @ManyToOne
    @JoinColumn(name = "approver_inst_id")
    protected User approver;

    @Column(name = "approval_date")
    protected LocalDate approvedDate;

    @Column(name = "approval_status", columnDefinition = "integer default '0'")
    protected int approvalStatusInd;

    @Column(name = "appr_memo", length = 250)
    protected String approvalMemo;

    @Column(name = "approval_audit_time", length = 20)
    private String approvalAuditTime;

    @Column(name = "last_mod_ts", nullable = false)
    protected LocalDate lastModTs;

    @Column(name = "ticket_id", nullable = false)
    protected Long ticketId;

    @Column(name = "group_ticket_id")
    protected Long groupTicketId;

    @Column(name = "group_leader_ind", columnDefinition = "integer default '0'")
    protected int groupLeaderInd;

    @Transient protected String approvalStatusStr;
    @Transient protected String initiatedDateStr;
    @Transient protected String approvalDateStr;
    @Transient protected boolean approved;
    @Transient protected boolean rejected;
    @Transient protected String rowSelected;
    @Transient protected boolean confirmed;
    @Transient protected String displayErrors;
    @Transient protected String generatedCaptcha;
    @Transient protected String enteredCaptcha;
    @Transient private String bankName;
    @Transient private String firstName;
    @Transient private String annualSalaryStrWivNaira;
    @Transient private String lastName;
    @Transient private String staffType;
    @Transient private String initials;
    @Transient private String accountNo;
    @Transient private String displayName;
    @Transient private String bvnNo;
    @Transient private String mdaName;
    @Transient private String birthDateStr;
    @Transient private String payGroup;
    @Transient private String createdBy;
    @Transient private boolean deletable;
    @Transient private String approvedBy;
    @Transient private boolean newRequest;
    @Transient private boolean groupTicket;
    @Transient private Long initiatorId;
    /**
     * For Pensioners...during approval only.
     */
    @Transient private String monthlySalary;

    @Transient private String yearlySalary;

    public boolean isDeletable() {
        deletable =  approvalStatusInd == 2;
        return deletable;
    }

    public boolean isNewRequest() {
        newRequest = approvalStatusInd == 0;
        return newRequest;
    }

    public boolean isGroupTicket() {
        groupTicket = this.groupTicketId != null;
        return groupTicket;
    }

    public String getApprovalStatusStr()
    {
        switch(this.approvalStatusInd){
            case PENDING_STATUS:
                approvalStatusStr = "Awaiting Approval";
                break;
            case APPROVED_STATUS:
                approvalStatusStr = "Approved";
                break;
            case REJECTED_STATUS:
                approvalStatusStr = "Rejected";
                break;
            case DELETED_STATUS:
                approvalStatusStr = "Deleted";
                break;
            case TREATED_STATUS:
                approvalStatusStr = "Treated"; //For Group Leaders.
                break;
        }
        return approvalStatusStr;
    }


    public String getInitiatedDateStr()
    {
        if(this.lastModTs != null)
            this.initiatedDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.lastModTs);
        return initiatedDateStr;
    }


    public String getApprovalDateStr()
    {
        if(this.approvedDate != null)
            this.approvalDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.approvedDate);

        return approvalDateStr;
    }


    public boolean isApproved()
    {
        return this.approvalStatusInd == 1;
    }


    public boolean isRejected()
    {

        return this.approvalStatusInd == 2;
    }

    public abstract Long getId();
    public abstract boolean isNewEntity();

}
