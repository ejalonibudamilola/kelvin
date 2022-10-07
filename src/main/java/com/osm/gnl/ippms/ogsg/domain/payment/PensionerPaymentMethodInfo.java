/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_pens_payment_method_info" )
@SequenceGenerator(name = "penPaymentMethodInfoSeq", sequenceName = "ippms_pens_pymt_mthd_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PensionerPaymentMethodInfo extends AbstractPaymentMethodEntity{

    @Id
    @GeneratedValue(generator = "penPaymentMethodInfoSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "payment_method_info_inst_id")
    private Long id;


    @OneToOne
    @JoinColumn(name = "pensioner_inst_id", nullable = false)
    private Pensioner employee;



    public boolean isNewEntity() {
        return this.id == null;
    }
}
