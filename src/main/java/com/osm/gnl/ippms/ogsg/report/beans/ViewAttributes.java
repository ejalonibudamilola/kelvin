package com.osm.gnl.ippms.ogsg.report.beans;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;

@Getter
@Setter
public abstract class ViewAttributes {

	
	@Transient 
	private boolean approved;
	
	@Transient
	private String generatedCaptcha;
	
	@Transient 
	private boolean approving;
	
	@Transient 
	private String enteredCaptcha;
	

}
