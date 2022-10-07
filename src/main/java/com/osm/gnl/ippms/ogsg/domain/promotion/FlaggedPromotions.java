/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.promotion;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ippms_flagged_promotions")
@SequenceGenerator(name = "flagPromoSeq", sequenceName = "ippms_flagged_promotions_seq", allocationSize = 1)
public class FlaggedPromotions {

    @Id
    @GeneratedValue(generator = "flagPromoSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "flagged_promo_inst_id")
    private Long id;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;


    @Column(name = "hire_info_inst_id")
    private Long hireInfoId;


    @ManyToOne
    @JoinColumn(name = "mda_inst_id" ,nullable = false)
    private MdaInfo mdaInfo;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id",nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "from_salary_info_inst_id",nullable = false)
    private SalaryInfo fromSalaryInfo;

    @ManyToOne
    @JoinColumn(name = "to_salary_info_inst_id",nullable = false)
    private SalaryInfo toSalaryInfo;

    @Column(name = "pay_period", nullable = false, length = 6)
    private String auditPayPeriod;

    @Column(name = "status_ind",columnDefinition = "integer default '0'")
    private int statusInd;

    @Column(name = "promotion_date", nullable = false)
    private LocalDate promotionDate;

    @Column(name = "run_month", columnDefinition = "numeric", nullable = false)
    private int runMonth;

    @Column(name = "run_year", columnDefinition = "numeric", nullable = false)
    private int runYear;

    @Column(name = "treated_date")
    private Timestamp treatedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approver_inst_id")
    private User approver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "initiator_inst_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_inst_id")
    private Rank newRank;

    @Column(name = "last_mod_ts")
    protected Timestamp lastModTs;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "ref_number")
    private String refNumber;

    @Column(name = "ref_date")
    private LocalDate refDate;

    @Column(name = "arrears")
    private double arrears;



    @Transient private String initiatedDateStr;
    @Transient private String approvalStatusStr;
    @Transient private boolean rejection;
    @Transient private boolean showRankRow;
    @Transient protected String rowSelected;


    public String getInitiatedDateStr() {
        if (this.promotionDate != null)
            initiatedDateStr = PayrollBeanUtils.getJavaDateAsString(this.promotionDate);
        return initiatedDateStr;
    }
    public String getApprovalStatusStr() {
        // 1 is approved
        if (this.statusInd == 1)
            this.approvalStatusStr = "Approved";
            // 2 is rejected
        else if (statusInd == 2)
            this.approvalStatusStr = "Rejected";
            // 0 is pending
        else
            this.approvalStatusStr = "Pending";
        return approvalStatusStr;
    }



    public int compareTo(Object o) {
        return 0;
    }


    public boolean isNewEntity() {
        return this.id == null;
    }
}
