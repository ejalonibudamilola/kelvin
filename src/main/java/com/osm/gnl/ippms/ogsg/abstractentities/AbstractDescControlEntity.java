package com.osm.gnl.ippms.ogsg.abstractentities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractDescControlEntity extends AbstractNamedEntity{

    @Column(name="description", nullable=false)
    protected String description;



}
