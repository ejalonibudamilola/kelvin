package com.osm.gnl.ippms.ogsg.abstractentities;


import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class DependentEntity extends NamedEntity
{
  private static final long serialVersionUID = 6392912206792734248L;
  private boolean approvalNeeded;
  private Long parentInstId;
  private String placeHolder;
  private int lastEdited;
  private String displayTitle;
  private boolean showDetails;
  private boolean stepWarningIssued;

}