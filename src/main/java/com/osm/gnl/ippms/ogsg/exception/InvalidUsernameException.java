package com.osm.gnl.ippms.ogsg.exception;

/**
 * 
 * @author Mustola
 * @since  January 10th, 2014.
 */
public class InvalidUsernameException extends Exception {
	
	private static final long serialVersionUID = -8966849666660429751L;

	public InvalidUsernameException(){ super(); }
	
	public InvalidUsernameException( String pMessage ){ super( pMessage ); }
	
	public InvalidUsernameException( Throwable pThowable ){ super( pThowable ); }
	
	public InvalidUsernameException( String pMsg, Throwable pThowable ){ super( pMsg, pThowable ); }
}
