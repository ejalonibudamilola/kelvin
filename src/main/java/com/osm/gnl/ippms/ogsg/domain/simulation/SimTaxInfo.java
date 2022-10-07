/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.menu.domain.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "ippms_sim_tax_info")
@SequenceGenerator(name = "simTaxInfoSeq", sequenceName = "ippms_sim_tax_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SimTaxInfo extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1493488912798339993L;

	@Id
	@GeneratedValue(generator = "simTaxInfoSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "sim_tax_inst_id")
	private Long id;

	@Column(name = "lower_bound", columnDefinition = "numeric(15,2) default '0.00'")
	private double lowerBound;
	@Column(name = "upper_bound", columnDefinition = "numeric(15,2) default '0.00'")
	private double upperBound;
	@Column(name = "tax_rate", columnDefinition = "numeric(6,4) default '0.00'")
	private double taxRate;



	@Override
	public boolean isNewEntity() {
		 
		return this.id == null;
	}



}
