/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.approval;

import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_pensioner_approval")
@SequenceGenerator(name = "penPayApprSeq", sequenceName = "ippms_pensioner_approval_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PensionerApproval extends AbstractApprovalEntity {

    @Id
    @GeneratedValue(generator = "penPayApprSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "pay_approval_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id", nullable = false)
    private Pensioner employee;

    public PensionerApproval(Long id){this.id = id;}

    public boolean isNewEntity() {
        return this.id == null;
    }
}
