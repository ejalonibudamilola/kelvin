package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Getter
@Setter
public class LgaMiniBean extends NamedEntity
{
  private static final long serialVersionUID = 2400542343370233964L;
  private int totalElements;
  private double percentage;
  private String placeHolder;
  private NumberFormat df;

  public LgaMiniBean()
  {
    this.df = new DecimalFormat("#.#");
  }


  public String getPlaceHolder() {
    String wFormatRes = this.df.format(getTotalElements() / getPercentage() * 100.0D);
    if ((wFormatRes.equals("0")) || (wFormatRes.length() == 1)) {
      DecimalFormat df = new DecimalFormat("#.###");
      wFormatRes = df.format(getTotalElements() / getPercentage() * 100.0D);
    }


    this.placeHolder = (wFormatRes + "%");
    return this.placeHolder;
  }

}