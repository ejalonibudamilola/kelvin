package com.osm.gnl.ippms.ogsg.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAuthenticationException extends AuthenticationException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4780018743680659129L;

	private boolean passwordException;

	public UserAuthenticationException(String msg) {
		super(msg);
	}

	public UserAuthenticationException(String msg, Throwable t) {
		super(msg, t);
	}
}
