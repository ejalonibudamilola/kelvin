package com.osm.gnl.ippms.ogsg.domain.beans;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProgressBean
  implements Serializable
{
  private static final long serialVersionUID = -4012343374373276862L;
  private int percentage;
  private int currentCount;
  private int totalElements;
  private String displayMessage;
  private boolean finished;
  private String currentReport;
  private String payPeriod;
  private String timeRemaining;
  private String pageTitle;
  private boolean download;

}