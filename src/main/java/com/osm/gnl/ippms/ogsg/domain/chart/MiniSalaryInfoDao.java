/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.chart;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ippms_mini_salary_info")
@SequenceGenerator(name = "miniSalaryInfoSeq", sequenceName = "ippms_mini_salary_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MiniSalaryInfoDao {

    @Id
    @GeneratedValue(generator = "miniSalaryInfoSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "mini_salary_info_inst_id",nullable = false,updatable = false,unique = true)
    private Long id;

    @Column(name = "salary_info_inst_id", nullable = false,updatable = false,unique = true)
    private Long salaryInfoId;

    @Column(name = "annual_gross", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double annualGross;

    @Column(name = "monthly_gross", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double monthlyGross;

    @Column(name = "annual_basic", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double annualBasic;

    @Column(name = "monthly_basic", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double monthlyBasic;

    @Column(name = "annual_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double annualAllowance;

    @Column(name = "monthly_allowance", columnDefinition = "numeric(15,2) default '0.00'", nullable = false)
    private double monthlyAllowance;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "salary_level", nullable = false)
    private int level;
    @Column(name = "salary_step", nullable = false)
    private int step;

    @Column(name = "salary_type_name", columnDefinition = "varchar(60)", nullable = false)
    private String salaryTypeName;

    @Column(name = "user_inst_id", nullable = false)
    protected Long lastModBy;

    @Column(name = "creation_date", nullable = false)
    protected Timestamp creationDate = Timestamp.from(Instant.now());

    @Column(name = "last_mod_ts", nullable = false)
    protected Timestamp lastModTs;

    @Transient private String levelStepStr;
    @Transient private String levelStr;
    @Transient private String stepStr;
    @Transient private List<MiniSalaryInfoDao> miniSalaryInfoDaoList;
    @Transient private List<NamedEntity> salaryTypeNameList;

    public String getLevelAndStepAsStr() {
        return getLevelStr() + "/" + getStepStr();
    }

    public String getLevelStepStr() {
        this.levelStepStr = getLevelAndStepAsStr();
        return this.levelStepStr;
    }

    public String getLevelStr() {
        this.levelStr = String.valueOf(this.level);
        return this.levelStr;
    }
    public String getStepStr() {
        this.stepStr = String.valueOf(this.step);
        if (this.stepStr.length() == 1)
            this.stepStr = ("0" + this.stepStr);
        return this.stepStr;
    }

}
