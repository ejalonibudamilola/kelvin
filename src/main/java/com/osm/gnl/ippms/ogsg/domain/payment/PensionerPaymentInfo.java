/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;


import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_pen_payment_info")
@SequenceGenerator(name = "penPymtInfoSeq", sequenceName = "ippms_pen_payment_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PensionerPaymentInfo extends AbstractPaymentInfoEntity{

    @Id
    @GeneratedValue(generator = "penPymtInfoSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "pymt_info_inst_id", nullable = false, unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pensioner_inst_id", nullable = false)
    private Pensioner pensioner;


    @Override
    public Long getParentInstId() {
        if(this.id != null)
            return this.id;
        return null;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public AbstractEmployeeEntity getAbstractEmployee() {
        return this.pensioner;
    }
}
