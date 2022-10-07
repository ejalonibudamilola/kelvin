/*
 * Copyright (c) 2022. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.approval;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_allow_rule_approval")
@SequenceGenerator(name = "allowRuleApprovalSeq", sequenceName = "ippms_allow_rule_approval_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class AllowanceRuleApproval extends AbstractApprovalEntity implements Comparable<AllowanceRuleApproval>{

    @Id
    @GeneratedValue(generator = "allowRuleApprovalSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "allow_rule_approval_inst_id", nullable =  false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rule_master_inst_id", nullable = false)
    private AllowanceRuleMaster allowanceRuleMaster;

    @ManyToOne
    @JoinColumn(name = "mda_dept_map_inst_id", nullable = false)
    private MdaDeptMap mdaDeptMap;

    @ManyToOne
    @JoinColumn(name = "school_inst_id")
    private SchoolInfo schoolInfo;

    @Column(name = "audit_time", length = 20, nullable = false)
    private String auditTime;

    @Column(name = "deletion_date")
    private LocalDate deleteDate;

    @Column(name = "rejection_reason", length = 100)
    private String rejectionReason;


    @Transient protected String rowSelected;
    @Transient private String initiatedDateStr;
    @Transient private String deletionDateStr;
    @Transient private String rejectionDateStr;
    @Transient private String oldMda;
    @Transient private String newMda;
    @Transient private boolean rejection;
    @Transient private boolean approval;
    @Transient private int overrideInd;
    @Transient private boolean showOverride;
    @Transient private Long parentId;
    @Transient private AbstractEmployeeEntity parentObject;



    public void setInitiatedDateStr(String initiatedDateStr) {
        this.initiatedDateStr = initiatedDateStr;
    }

    public String getInitiatedDateStr() {
        if (this.initiatedDate != null)
            initiatedDateStr = PayrollBeanUtils.getJavaDateAsString(this.initiatedDate);
        return initiatedDateStr;
    }


    @Override
    public int compareTo(AllowanceRuleApproval pIncoming) {
        return 0;
    }


    public String getRejectionDateStr() {
        if (this.getApprovedDate() != null)
            rejectionDateStr = PayrollBeanUtils.getJavaDateAsString(this.getApprovedDate());
        return rejectionDateStr;

    }
    public String getDeletionDateStr() {
        if (this.deleteDate != null)
            deletionDateStr = PayrollBeanUtils.getJavaDateAsString(this.deleteDate);
        return deletionDateStr;
    }

    public boolean isNewEntity() {

        return this.id == null;
    }

    public Long getParentId() {
        if(this.allowanceRuleMaster != null && this.allowanceRuleMaster.getHiringInfo() != null &&
           this.allowanceRuleMaster.getHiringInfo().getAbstractEmployeeEntity() != null)
           parentId = this.allowanceRuleMaster.getHiringInfo().getAbstractEmployeeEntity().getId();

        return parentId;
    }

}
