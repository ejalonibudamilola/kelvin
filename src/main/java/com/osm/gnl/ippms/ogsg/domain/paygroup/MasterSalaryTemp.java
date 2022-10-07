/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.paygroup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ippms_master_salary_info_temp")
@SequenceGenerator(name = "masterSalaryInfoTempSeq", sequenceName = "ippms_master_salary_info_temp_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MasterSalaryTemp extends AbstractMasterSalaryTemp {


    @Id
    @GeneratedValue(generator = "masterSalaryInfoTempSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "master_salary_temp_inst_id", nullable = false, unique = true)
    private Long id;

    @Transient private List<SalaryTemp>  salaryTempList;

    @Override
    public Long getId() {
        return this.id;
    }



    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
