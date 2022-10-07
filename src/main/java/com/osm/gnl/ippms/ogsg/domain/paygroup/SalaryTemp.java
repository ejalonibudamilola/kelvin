/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.paygroup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_salary_info_temp")
@SequenceGenerator(name = "salaryInfoTempSeq", sequenceName = "ippms_salary_info_temp_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SalaryTemp extends AbstractSalaryTemp {
    private static final long serialVersionUID = 1278681082461908986L;

    @Id
    @GeneratedValue(generator = "salaryInfoTempSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "salary_temp_inst_id", nullable = false, unique = true)
    private Long id;


    public SalaryTemp(Long pId) {
        this.id = pId;
    }

    public SalaryTemp(Long pId, int pLevel, int pStep) {
        this.id = pId;
        this.setLevel(pLevel);
        this.setStep(pStep);
    }

    public String getSwesAllowanceStr() {
        this.swesAllowanceStr = (naira + this.df.format(this.getSwesAllowance()));
        return this.swesAllowanceStr;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

  }
