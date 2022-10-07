/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payroll;

import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_payroll_rerun")
@SequenceGenerator(name = "payrollRerunSeq", sequenceName = "ippms_payroll_rerun_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayrollRerun {

    @Id
    @GeneratedValue(generator = "payrollRerunSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "payroll_rerun_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "run_month", columnDefinition = "numeric")
    private int runMonth;

    @Column(name = "run_year", columnDefinition = "numeric")
    private int runYear;

    @ManyToOne
    @JoinColumn(name = "hire_info_inst_id", nullable = false)
    private HiringInfo hiringInfo;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "created_time", nullable = false, length = 20)
    private String auditTime;

    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs;

    @Column(name = "last_mod_by", nullable = false, length = 32)
    private Long lastModBy;

    @Transient
    private boolean deleteWarningIssued;
    @Transient
    private String generatedCaptcha;


    public PayrollRerun(Long pId) {
        this.id = pId;
    }


    public boolean isNewEntity() {
        return this.id == null;
    }


}
