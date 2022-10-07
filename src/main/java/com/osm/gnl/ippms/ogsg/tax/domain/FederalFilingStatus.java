package com.osm.gnl.ippms.ogsg.tax.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;

public class FederalFilingStatus
{
  private static final long serialVersionUID = 1L;
  private int filingStatusValue;

  public int getFilingStatusValue()
  {
    return this.filingStatusValue;
  }

  public void setFilingStatusValue(int pFilingStatusValue)
  {
    this.filingStatusValue = pFilingStatusValue;
  }
}