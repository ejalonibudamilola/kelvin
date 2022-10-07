/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.approval;

import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncrementTracker;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_step_increment_approval")
@SequenceGenerator(name = "stepIncrementApprovalSeq", sequenceName = "ippms_step_increment_approval_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class StepIncrementApproval extends AbstractApprovalEntity implements Comparable<StepIncrementApproval>{
    @Id
    @GeneratedValue(generator = "transferApprovalSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "increment_approval_inst_id")
    private Long id;

    @Column(name = "employee_inst_id", nullable = false)
    private Long parentId;

    @OneToOne
    @JoinColumn(name = "tracker_inst_id", nullable =  false)
    private StepIncrementTracker stepIncrementTracker;

    @ManyToOne
    @JoinColumn(name = "salary_info_inst_id", nullable = false)
    private SalaryInfo salaryInfo;

    @ManyToOne
    @JoinColumn(name = "old_salary_info_inst_id", nullable = false)
    private SalaryInfo oldSalaryInfo;

    @Transient private int overrideInd;
    @Transient private boolean showOverride;

    public StepIncrementApproval(Long pId) {
        this.id = pId;
    }


    public boolean isNewEntity() {

        return this.id == null;
    }

    @Override
    public int compareTo(StepIncrementApproval stepIncrementApproval) {
        return 0;
    }
}
