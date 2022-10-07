/*
 * Copyright (c) 2020. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.nextofkin.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
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
import javax.persistence.Transient;

@Entity
@Table(name = "ippms_relationship_type")
@SequenceGenerator(name = "relationTypeSeq", sequenceName = "ippms_relationship_type_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class RelationshipType extends AbstractNamedEntity   {

	/**
	 * 
	 */
	private static final long serialVersionUID = 511343822640017431L;
	
	@Id
	@GeneratedValue(generator = "relationTypeSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "relationship_type_inst_id", nullable = false, unique = true)
	private Long id;

	public RelationshipType(Long id) {
		this.id = id;
	}

	@Transient
	private boolean editMode;
	

	@Override
	public int compareTo(Object o) {
		if(o == null || !(o instanceof RelationshipType))
		return 0;
		return String.CASE_INSENSITIVE_ORDER.compare(this.name,((RelationshipType) o).name);
	}

	public boolean isNewEntity() {
		return this.id == null;
	}



}
