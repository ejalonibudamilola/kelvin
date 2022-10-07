package com.osm.gnl.ippms.ogsg.payment.beans;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayPeriodDaysBean
{
  private static final long serialVersionUID = 3971637048556002989L;
  private String payPeriod;
  private String payPeriodCode;
  private LocalDate currentPayPeriodStart;
  private LocalDate currentPayPeriodEnd;
  private String currentPayPeriod;
  private List<String> payPeriodList;


}