package com.osm.gnl.ippms.ogsg.exception;

/**
 * For handling invalid business client situations.
 * 
 * @author Mustola
 * @since  January 10th, 2014
 */
public class InvalidBusinessClientException extends Exception {

	private static final long serialVersionUID = 8124658513850093219L;

	public InvalidBusinessClientException(){ super(); }
	
	public InvalidBusinessClientException( String pMessage ){ super( pMessage ); }
	
	public InvalidBusinessClientException( Throwable pThowable ){ super( pThowable ); }
	
	public InvalidBusinessClientException( String pMsg, Throwable pThowable ){ super( pMsg, pThowable ); }
}
