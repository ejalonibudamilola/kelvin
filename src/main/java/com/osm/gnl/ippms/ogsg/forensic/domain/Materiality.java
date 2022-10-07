/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.forensic.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_materiality")
@SequenceGenerator(name = "materialitySeq", sequenceName = "ippms_materiality_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class Materiality extends AbstractControlEntity {
	private static final long serialVersionUID = 2494265208549078433L;

	@Id
	@GeneratedValue(generator = "materialitySeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "materiality_inst_id", nullable = false, unique = true)
	private Long id;
	@Column(name = "value", columnDefinition = "numeric(15,2) default 0.00", nullable = false)
	private double value;
	@ManyToOne
	@JoinColumn(name = "user_inst_id", nullable = false)
	private User user;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Transient private String valueAsStr;
	@Transient private double oldValue;


	public int compareTo(Materiality pMateriality) {
		return String.CASE_INSENSITIVE_ORDER.compare(this.getUser().getActualUserName(),
				pMateriality.getUser().getActualUserName());
	}


	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public boolean isNewEntity() {
		return this.id == null;
	}
}