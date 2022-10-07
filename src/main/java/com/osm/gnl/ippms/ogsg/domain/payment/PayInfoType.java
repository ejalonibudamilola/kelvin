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


@Entity
@Table(name = "ippms_pay_info_type")
@SequenceGenerator(name = "payInfoTypeSeq", sequenceName = "ippms_pay_info_type_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayInfoType extends AbstractControlEntity {

	private static final long serialVersionUID = 2242873260163227778L;

	@Id
	@GeneratedValue(generator = "payInfoTypeSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "pay_info_type_inst_id", nullable = false, unique = true)
	private Long id;

	@Column(name = "pay_info_type", nullable = false, length = 15)
	private String name;

	@Column(name = "description", nullable = false, length = 255)
	private String description;

	public PayInfoType(Long long1) {
		this.id = long1;
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