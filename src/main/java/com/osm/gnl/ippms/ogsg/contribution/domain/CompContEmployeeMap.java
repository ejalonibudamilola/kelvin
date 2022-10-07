/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.contribution.domain;

import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class CompContEmployeeMap implements Serializable
{
  private static final long serialVersionUID = -1943388631643789847L;
  private PayTypes payTypes;
  private double amount;
  private double rate;
  private double contributionAmount;
  private String category;
  private String provider;
  private int payTypesRef;
  private boolean useAmount;
  private String lastModBy;
  private Date lastModTs;
  private Long employeeInstId;
  private Long id;
  private Long parentInstId;
  private String displayErrors;


  public int getPayTypesRef()
  {
    if ((this.payTypes != null) && (!this.payTypes.isNewEntity())) {
      this.payTypesRef = this.payTypes.getId().intValue();
    }
    return this.payTypesRef;
  }


  public boolean isUseAmount()
  {
    if ((getPayTypes() != null) && (!getPayTypes().isNewEntity())) {
        this.useAmount = getPayTypes().getName().indexOf("%") == -1;
    }

    return this.useAmount;
  }



public boolean isNewEntity() {
	 
	return this.id == null;
}


}