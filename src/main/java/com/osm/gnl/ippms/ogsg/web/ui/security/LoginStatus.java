package com.osm.gnl.ippms.ogsg.web.ui.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Login Status of a user.
 */
public final class LoginStatus implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty(value="username")
	public final String username;
	
	@JsonProperty(value="loggedIn")
	public final boolean loggedIn;
	
	public LoginStatus(String username, boolean loggedIn) {
		this.username = username;
		this.loggedIn = loggedIn;
	}

}
