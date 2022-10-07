/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.garnishment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_garnishment_type")
@SequenceGenerator(name = "garnTypeSeq", sequenceName = "ippms_garnishment_type_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmpGarnishmentType extends AbstractDescControlEntity {
    private static final long serialVersionUID = -976234952819063589L;

    @Id
    @GeneratedValue(generator = "garnTypeSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "garn_type_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_inst_id", nullable = false)
    private BankBranch bankBranch;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    @Column(name = "account_number", nullable = false, length = 32)
    private String accountNumber;

    @Column(name = "restrict_edit_ind", columnDefinition = "integer default '0'")
    private int editRestriction;

    @Transient
    private String confirmAccountNumber;
    @Transient
    private Long branchInstId;
    @Transient
    private Long bankInstId;
    @Transient boolean editRestricted;



    public EmpGarnishmentType(Long pId) {
        this.id = pId;
    }

    public boolean isEditRestricted() {
        return this.editRestriction == 1;
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