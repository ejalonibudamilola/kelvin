package com.osm.gnl.ippms.ogsg.exception;

import org.springframework.dao.DataAccessException;

public class ApplicationException extends DataAccessException
{
  
/**
	 * 
	 */
	private static final long serialVersionUID = 8569488760912568180L;

public ApplicationException(String detailMessage)
  {
    super(detailMessage);
  }
}