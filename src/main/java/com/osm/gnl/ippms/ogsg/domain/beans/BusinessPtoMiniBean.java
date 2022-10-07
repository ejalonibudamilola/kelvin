package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;

import java.util.List;

public class BusinessPtoMiniBean extends NamedEntity
{
  private static final long serialVersionUID = -5722417097790064227L;
  private boolean createMode;
  private List<User> loginList;

  public boolean isCreateMode()
  {
    return this.createMode;
  }

  public void setCreateMode(boolean pCreateMode) {
    this.createMode = pCreateMode;
  }

  public List<User> getLoginList() {
    return this.loginList;
  }

  public void setLoginList(List<User> pLoginList) {
    this.loginList = pLoginList;
  }
}