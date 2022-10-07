/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "ippms_pay_types" )
@SequenceGenerator(name = "payTypeSeq", sequenceName = "ippms_pay_types_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayTypes extends AbstractNamedEntity
{
  private static final long serialVersionUID = -7294283144091322553L;
  
  @Id
  @GeneratedValue(generator = "payTypeSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "pay_types_inst_id")
  private Long id;

  @Column(name="code_name")
  private String codeName;

  @Column(name = "percent_ind", columnDefinition = "integer default '0'" ,nullable = false)
  private int percentageInd;

  @Column(name = "selectable_ind", columnDefinition = "integer default '0'" ,nullable = false)
  private int selectableInd;

  @Column(name = "default_ind", columnDefinition = "integer default '0'" ,nullable = false)
  private int defaultInd;

  @Transient private boolean selectable;
  @Transient private boolean usingPercentage;
  @Transient private boolean defaultObject;


  public PayTypes(Long pId, String pName)
  {
    this.id = pId;
    this.name = pName;
  }
  public PayTypes(Long pId, String pName, int pPercentInd)
  {
    this.id = pId;
    this.name = pName;
    this.percentageInd = pPercentInd;
  }

  public PayTypes(Long pId) {
    this.id = pId;
  }



  public boolean isSelectable()
  {
    return this.selectableInd == 0;
  }

  public boolean isUsingPercentage()
  {
    return this.percentageInd == 1;
  }

  public boolean isDefaultObject() {
    return this.defaultInd == 1;
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