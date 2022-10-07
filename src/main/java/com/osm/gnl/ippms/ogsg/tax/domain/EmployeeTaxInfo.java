package com.osm.gnl.ippms.ogsg.tax.domain;

public class EmployeeTaxInfo
{
  private static final long serialVersionUID = 6543288331146345918L;
  private FederalFilingStatus federalFilingStatus;
  private StateFilingStatus stateFilingStatus;
  private int fedFilingInd;
  private int stateFilingInd;
  private int fedAllowances;
  private double fedAddAmount;
  private int stateAllowances;
  private double stateAddAmount;
  private boolean editMode;
  private String defaultFedFilingStatus;

  public FederalFilingStatus getFederalFilingStatus()
  {
    return this.federalFilingStatus;
  }

  public void setFederalFilingStatus(FederalFilingStatus pFederalFilingStatus)
  {
    this.federalFilingStatus = pFederalFilingStatus;
  }

  public StateFilingStatus getStateFilingStatus()
  {
    return this.stateFilingStatus;
  }

  public void setStateFilingStatus(StateFilingStatus pStateFilingStatus)
  {
    this.stateFilingStatus = pStateFilingStatus;
  }

  public int getFedAllowances()
  {
    return this.fedAllowances;
  }

  public void setFedAllowances(int pFedAllowances)
  {
    this.fedAllowances = pFedAllowances;
  }

  public double getFedAddAmount()
  {
    return this.fedAddAmount;
  }

  public void setFedAddAmount(double pFedAddAmount)
  {
    this.fedAddAmount = pFedAddAmount;
  }

  public int getStateAllowances()
  {
    return this.stateAllowances;
  }

  public void setStateAllowances(int pStateAllowances)
  {
    this.stateAllowances = pStateAllowances;
  }

  public double getStateAddAmount()
  {
    return this.stateAddAmount;
  }

  public void setStateAddAmount(double pStateAddAmount)
  {
    this.stateAddAmount = pStateAddAmount;
  }

  public int getfedFilingInd()
  {
    return this.fedFilingInd;
  }

  public void setfedFilingInd(int pfedFilingInd)
  {
    this.fedFilingInd = pfedFilingInd;
  }

  public int getstateFilingInd()
  {
    return this.stateFilingInd;
  }

  public void setstateFilingInd(int pstateFilingInd)
  {
    this.stateFilingInd = pstateFilingInd;
  }

  public boolean isEditMode()
  {
    return this.editMode;
  }

  public void setEditMode(boolean pEditMode)
  {
    this.editMode = pEditMode;
  }

  public String getDefaultFedFilingStatus()
  {
    return this.defaultFedFilingStatus;
  }

  public void setDefaultFedFilingStatus(String pDefaultFedFilingStatus)
  {
    this.defaultFedFilingStatus = pDefaultFedFilingStatus;
  }
}