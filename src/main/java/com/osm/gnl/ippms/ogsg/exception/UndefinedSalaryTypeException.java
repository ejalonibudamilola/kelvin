package com.osm.gnl.ippms.ogsg.exception;

public class UndefinedSalaryTypeException extends Exception
{
  private static final long serialVersionUID = -3534402649135546996L;

  public UndefinedSalaryTypeException(String pDetailMessage)
  {
    super(pDetailMessage);
  }
}