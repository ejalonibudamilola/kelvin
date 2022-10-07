/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


@Entity
@Table(name = "ippms_banks")
@SequenceGenerator(name = "bankSeq", sequenceName = "ippms_banks_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class BankInfo extends AbstractControlEntity implements Serializable


{
  private static final long serialVersionUID = -5470886581184862391L;
  @Id
	@GeneratedValue(generator = "bankSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "bank_inst_id", nullable = false, unique = true)
	private Long id;
  
  @Column(name = "bank_name", nullable = false, length = 20, unique = true)
  private String name;
  @Column(name = "sort_code", nullable = false, length = 5, unique = true)
  private String sortCode;

  @Column(name = "default_ind", columnDefinition = "integer default '0'")
  private int defaultInd;
  @Column(name = "mfb_ind", columnDefinition = "integer default '0'")
  private int mfbInd;
  @Column(name = "selectable_ind", columnDefinition = "integer default '0'")
  private int selectableInd;
  @Column(name="description", nullable=false)
  private String description;

  @Transient private boolean microFinanceBank;
  @Transient private double totalNetPay;
  @Transient private List<BankBranch> branches;
  @Transient private boolean editMode;
  @Transient private boolean defaultBank;
  @Transient private HashMap<Long, BankBranch> bankBranchMap;
  @Transient private boolean selectable;

  public BankInfo(Long pBankInstId)
  {
    setId( pBankInstId);
  }

  public BankInfo(Long pBankId, String pBankName) {
    setId(pBankId);
    setName(pBankName);
  }



  public int compareTo(BankInfo pIncoming)
  {
    if (pIncoming != null)
      return getName().compareToIgnoreCase(pIncoming.getName());
    return 0;
  }


  public boolean isDefaultBank()
  {
    this.defaultBank = (getDefaultInd() == 1);
    return this.defaultBank;
  }


public HashMap<Long, BankBranch> getBankBranchMap()
{
	if(bankBranchMap == null)
		bankBranchMap = new HashMap<>();
	return bankBranchMap;
}


public boolean isMicroFinanceBank()
{
	this.microFinanceBank = this.mfbInd == 1;
	return microFinanceBank;
}


  public boolean isSelectable() {
    return this.selectableInd == 0;
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