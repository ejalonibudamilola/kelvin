package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class HomePageBean extends NamedEntity
{
  private static final long serialVersionUID = 8419062614620362756L;
  private int totalEmployee;
  private int totalMinistries;
  private int totalDepartments;
  private int totalInactiveEmployee;


}