package com.osm.gnl.ippms.ogsg.exception;

import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;

/**
 * This represents a case where a client doesn't have a 
 * {@link BusinessCertificate}.
 * 
 * @author Mustola
 *
 */
public class NoBusinessCertificationException extends Exception {

	private static final long serialVersionUID = 2297802506730551103L;

	public NoBusinessCertificationException(){ super(); }
	
	public NoBusinessCertificationException( String pMessage ){ super( pMessage ); }
	
	public NoBusinessCertificationException( Throwable pThowable ){ super( pThowable ); }
	
	public NoBusinessCertificationException( String pMsg, Throwable pThowable ){ super( pMsg, pThowable ); }
}
