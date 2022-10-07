package com.osm.gnl.ippms.ogsg.abstractentities;


import com.osm.gnl.ippms.ogsg.auth.domain.Role;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class BaseEntityLong
  implements Serializable, IConstants
{
  private static final long serialVersionUID = 243798317455895993L;
  private Long id;
  private int pageSize;
  private boolean canEdit;
  private boolean user;
  private LocalDate createdDate;
  private Role role;
  private boolean admin;
  private MdaDeptMap mdaDeptMap;
  private MdaInfo mdaInfo;
  //-- Ola Test
  private BusinessCertificate roleBean;

  public final boolean isAdmin()
  {
    return this.admin;
  }

  public final void setAdmin(boolean pAdmin) {
    this.admin = pAdmin;
  }

  public final void setUser(boolean pUser) {
    this.user = pUser;
  }
  public final String treatNull(String str) {
    if (str == null) {
      return "";
    }
    return str.trim();
  }

    public boolean isNewEntity() {
      return this.id == null;
    }
}