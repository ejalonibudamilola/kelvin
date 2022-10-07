package com.osm.gnl.ippms.ogsg.menu.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 
 * @author Femi Adegebsan
 * @Author Mustola
 * Adapted for new IPPMS Spring Boot
 */
@SuppressWarnings("serial")
@MappedSuperclass
@NoArgsConstructor
@Getter@Setter
public abstract class AbstractEntity implements Serializable {

	


	@Column(name = "time_created", updatable = false, nullable = false)
	private LocalDate timeCreated = LocalDate.now();

	@Column(name = "created_by", updatable = false, length = 50, nullable = false)
	private String createdBy;


	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;
	
	@Column(name = "last_mod_by", nullable = false, length = 50)
	private String lastModBy;
	
	
	//Non-persistent fields
	@Transient private boolean selected;
	@Transient private int serialNo;
	@Transient private boolean editMode;
	@Transient private boolean hasRecords;
	@Transient private boolean errorRecord;


	public abstract boolean isNewEntity();
	
	public boolean isNotNewEntity() {
		return !this.isNewEntity();
	}



}
