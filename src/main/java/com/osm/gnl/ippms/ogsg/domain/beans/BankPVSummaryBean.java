package com.osm.gnl.ippms.ogsg.domain.beans;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankPVSummaryBean extends AbstractNamedEntity
{
  private static final long serialVersionUID = -2810354917989739969L;
  private double netPay;
  private HashMap<Long, BankBranch> bankBranchMap;
  private HashMap<Long, BankInfo> bankInfoMap;
  private HashMap<Long, List<BankBranch>> bankInfoBranchMap;
  private LocalDate payDate;
  private String bankSortCode;
  private String bankBranchSortCode;
  private String bankName;
  private String bankBranchName;
  


  @Override
  public int compareTo(Object o) {
    return 0;
  }

  @Override
  public boolean isNewEntity() {
    return false;
  }
}