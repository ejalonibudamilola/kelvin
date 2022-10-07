package com.osm.gnl.ippms.ogsg.domain.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.BaseEntity;
import com.osm.gnl.ippms.ogsg.domain.beans.MPBAMiniBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter

public class MDAPMiniBeanHolder extends BaseEntity{
  private static final long serialVersionUID = -4403475503497839769L;
  private List<MPBAMiniBean> mpbaMiniBeanList;
  private int totalStaffStrength;
  private int totalNoOfMales;
  private int totalNoOfFemales;
  private double malePercentage;
  private double femalePercentage;
  private String malePercentageStr;
  private String femalePercentageStr;
  private double totalMalePercentage;
  private double totalFemalePercentage;
  private String totalFemalePercentageStr;
  private String totalMalePercentageStr;
  private NumberFormat df;

  public String getMalePercentageStr()  {
    if ((getTotalStaffStrength() > 0) && (getTotalNoOfMales() > 0)) {
      String wFormatRes = this.df.format(new Double(getTotalNoOfMales()).doubleValue() / new Double(getTotalStaffStrength()).doubleValue() * 100.0D);
      if ((wFormatRes.equals("0")) || (wFormatRes.length() == 1)) {
        DecimalFormat df = new DecimalFormat("#.##");
        wFormatRes = df.format(new Double(getTotalNoOfMales()).doubleValue() / new Double(getTotalStaffStrength()).doubleValue() * 100.0D);
      }
      this.malePercentageStr = (wFormatRes + "%");
    } else {
      this.malePercentageStr = "0.0%";
    }

    return this.malePercentageStr;
  }


  public String getFemalePercentageStr()  {
    if ((getTotalStaffStrength() > 0) && (getTotalNoOfFemales() > 0)) {
      String wFormatRes = this.df.format(new Double(getTotalNoOfFemales()).doubleValue() / new Double(getTotalStaffStrength()).doubleValue() * 100.0D);
      if ((wFormatRes.equals("0")) || (wFormatRes.length() == 1)) {
        DecimalFormat df = new DecimalFormat("#.##");
        wFormatRes = df.format(new Double(getTotalNoOfFemales()).doubleValue() / new Double(getTotalStaffStrength()).doubleValue() * 100.0D);
      }
      this.femalePercentageStr = (wFormatRes + "%");
    } else {
      this.femalePercentageStr = "0.0%";
    }

    return this.femalePercentageStr;
  }
}