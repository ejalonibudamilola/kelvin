package com.osm.gnl.ippms.ogsg.control.entities;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_title")
@SequenceGenerator(name = "titleSeq", sequenceName = "ippms_title_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class Title extends AbstractDescControlEntity
{
  private static final long serialVersionUID = 6711831357210514989L;

  
  @Id
  @GeneratedValue(generator = "titleSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "title_inst_id")
  private Long id;

  @Transient
  private boolean feminine;

  @Transient
  private String modifierOrg;


  public Title(Long pId, String pName)
  {
    this.id = pId;
     this.name = pName;
  }

public Title(Long pTitleId) {
    this.id = pTitleId;
}


public boolean isFeminine()
{
    this.feminine = (this.getName().equalsIgnoreCase(IConstants.ALHAJA)
    || this.getName().equalsIgnoreCase(IConstants.MISS) || this.getName().equalsIgnoreCase(IConstants.MRS) 
    || this.getName().equalsIgnoreCase(IConstants.DEACONESS) 
    || this.getName().equalsIgnoreCase(IConstants.MS) 
    || this.getName().equalsIgnoreCase(IConstants.DR_MRS));
	return feminine;
}




    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}