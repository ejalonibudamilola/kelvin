/*
 * Copyright (c) 2020. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.nextofkin.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NextOfKinDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5286032936983217030L;
	
	private NextOfKin primaryNextOfKin;
	
	private NextOfKin secondaryNextOfKin;

	private String name;

	private Long parentInstId;
	
	private boolean hasSecondaryNextOfKin;

	private boolean canNotEdit;
	 
	


}
