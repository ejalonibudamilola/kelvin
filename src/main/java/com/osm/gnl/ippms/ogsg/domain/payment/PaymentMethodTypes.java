/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "ippms_payment_method_types")
@SequenceGenerator(name = "payMethodTypeSeq", sequenceName = "ippms_payment_method_types_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaymentMethodTypes extends AbstractControlEntity implements Serializable {

	private static final long serialVersionUID = 6208005743398348203L;

	@Id
	@GeneratedValue(generator = "payMethodTypeSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "payment_method_types_inst_id")
	private Long id;

	@Column(name = "payment_method_type", length = 25, nullable = false)
	private String name;
	@Column(name = "payment_method_code", length = 3, nullable = false)
	private String paymentMethodCode;

	@Column(name="description", nullable=false)
	private String description;


	public PaymentMethodTypes(Long pId) {
		this.id = pId;
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