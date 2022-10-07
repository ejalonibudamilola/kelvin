package com.osm.gnl.ippms.ogsg.exception;

/**
 * 
 * @author Mustola
 * @since  January 10th, 2014
 */
public class EpmAuthenticationException extends Exception {
	
	private static final long serialVersionUID = 8583539157246383960L;
	
	public EpmAuthenticationException(){ super(); }
	public EpmAuthenticationException(String pMsg){ super(pMsg); }
	public EpmAuthenticationException(Throwable pThrowable){ super(pThrowable); }
	public EpmAuthenticationException(String pMsg, Throwable pThrowable ){ super(pMsg, pThrowable); }
}
