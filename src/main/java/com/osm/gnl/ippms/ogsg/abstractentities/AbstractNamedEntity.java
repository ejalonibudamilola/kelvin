package com.osm.gnl.ippms.ogsg.abstractentities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractNamedEntity extends AbstractControlEntity {

	@Column(name="name", nullable=false)
	protected String name;
}
