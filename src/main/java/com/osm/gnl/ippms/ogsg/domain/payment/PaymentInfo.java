/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Entity
@Table(name = "ippms_payment_info")
@SequenceGenerator(name = "pymtInfoSeq", sequenceName = "ippms_payment_info_seq", allocationSize = 1)
@Getter
@Setter
public class PaymentInfo extends AbstractPaymentInfoEntity implements Serializable
{

  @Id
  @GeneratedValue(generator = "pymtInfoSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "pymt_info_inst_id", nullable = false, unique = true)
   private Long id;
  
  @ManyToOne
  @JoinColumn(name = "employee_inst_id")
  private Employee employee;

  @ManyToOne
  @JoinColumn(name = "pensioner_inst_id")
  private Pensioner pensioner;

  public PaymentInfo(Long pId) {
	  this.id = pId;
  }

  private final boolean fieldHasValue(double fieldValue)
  {
    return fieldValue > 0.0D;
  }


  @Override
  public Long getParentInstId() {
    if(IppmsUtils.isNotNull(this.id))
      return this.id;
    return null;
  }

  @Override
  public boolean isNewEntity() {
    return this.id == null;
  }
  public boolean isPensioner(){
    return this.pensioner != null && !this.pensioner.isNewEntity();
  }
  @Override
  public AbstractEmployeeEntity getAbstractEmployee() {
    if(this.isPensioner())
      return this.pensioner;
    return this.employee;
  }
}