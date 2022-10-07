/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "ippms_payment_method_info" )
@SequenceGenerator(name = "paymentMethodInfoSeq", sequenceName = "ippms_payment_method_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaymentMethodInfo extends AbstractPaymentMethodEntity implements Serializable {

  private static final long serialVersionUID = -4874759194408978596L;

  @Id
  @GeneratedValue(generator = "paymentMethodInfoSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "payment_method_info_inst_id")
  private Long id;


  @OneToOne
  @JoinColumn(name = "employee_inst_id")
  private Employee employee;

  @OneToOne
  @JoinColumn(name = "pensioner_inst_id")
  private Pensioner pensioner;

  /**
   * This field is used to allow 'duplicated' account details
   * i.e., when an Employee retires, they can carry his/her
   * bank details along with them without flagging errors.
   * This is imperative so we can separate concerns regarding
   * historical data.
   */
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "service_pymt_method_info_inst_id")
  private PaymentMethodInfo servicePaymentMethodInfo;

  @Transient
  private Long parentId;

  @Transient
  private AbstractEmployeeEntity parentObject;



  public PaymentMethodInfo(Long pId) {
    this.id = pId;
  }


  public boolean isNewEntity(){
    return this.id == null;
  }

  public boolean isPensioner(){
      return this.pensioner != null && !this.pensioner.isNewEntity();
  }


  public Long getParentId() {
    if(parentId == null){
      if(this.isPensioner())
        parentId = this.getPensioner().getId();
      else
        parentId = this.getEmployee().getId();
    }
    return parentId;
  }

  public AbstractEmployeeEntity getParentObject() {
    if(parentObject == null){
      if(this.isPensioner())
        parentObject= this.getPensioner();
      else
        parentObject = this.getEmployee();
    }
    return parentObject;
  }

  public boolean isOgNumberNotEmpty() {
    this.ogNumberNotEmpty = StringUtils.trimToEmpty(this.getInheritOgNumber()).length() > 0;
    return this.ogNumberNotEmpty;
  }
}