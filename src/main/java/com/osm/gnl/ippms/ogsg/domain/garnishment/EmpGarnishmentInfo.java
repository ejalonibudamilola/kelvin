/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.garnishment;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_garnishment_info")
@SequenceGenerator(name = "garnishSeq", sequenceName = "ippms_garnishment_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmpGarnishmentInfo  extends  AbstractGarnishmentEntity{
	private static final long serialVersionUID = -2753980106876979692L;

	@Id
	@GeneratedValue(generator = "garnishSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "garn_info_inst_id")
	private Long id;

	public EmpGarnishmentInfo(Long pId, String pDesc) {
		this.id = pId;
		super.setDescription(pDesc);
	}

	public EmpGarnishmentInfo(Long pId) {
		this.id = pId;
	}


	@Override
	public boolean isNewEntity() {

		return this.id == null;
	}


}