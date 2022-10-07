/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_relevant_period")
@SequenceGenerator(name = "relPerSeq", sequenceName = "ippms_relevant_period_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class RelevantPeriod  extends AbstractDescControlEntity
{
 
	
  /**
	 * 
	 */
	private static final long serialVersionUID = 38685290153576196L;

@Id
  @GeneratedValue(generator = "relPerSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "relevant_pay_period_inst_id", unique = true, nullable = false)
  private Long id;
  

  @Column(name = "relevant_period_code", length = 1, nullable = false)
  private String relevantPeriodCode;

  public RelevantPeriod(Long pId) {
	  this.id = pId;
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