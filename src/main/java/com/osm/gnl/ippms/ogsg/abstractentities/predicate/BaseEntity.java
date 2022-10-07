package com.osm.gnl.ippms.ogsg.abstractentities.predicate;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class BaseEntity
  implements Serializable, IConstants
{
  private Long id;
  private int pageSize;
  private boolean canEdit;
  private boolean user;
  private Date createdDate;
  private boolean admin;
  public final String naira = "\u20A6";
  
  private boolean selected;
  private boolean editMode;
  private BusinessCertificate businessCertificate;


  public final String treatNull(String str) {
    if (str == null) {
      return "";
    }
    return str.trim();
  }
  public final String treatNull(Object pObject){
	  if(pObject == null)
		  return "";
	  return String.valueOf(pObject);
  }

  public final boolean isNew() {
    return this.id == null;
  }

}