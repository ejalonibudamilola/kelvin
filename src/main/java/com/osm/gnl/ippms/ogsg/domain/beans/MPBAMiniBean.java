package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MPBAMiniBean extends NamedEntity

{
  private static final long serialVersionUID = -3387146306950665113L;
  private String codeName;
  private String head;
  private String type;
  private String parentObjectName;
  private int objectId;
  private int staffStrength;
  private int noOfFemales;
  private int noOfMales;
  private double femalePercentage;
  private double malePercentage;
  private String femalePercentageStr;
  private String malePercentageStr;
  private List<MdaEmployeeMiniBean> employeeList;
  private List<MdaEmployeeMiniBean> femaleList;
  private List<MdaEmployeeMiniBean> maleList;
  private double totalMalePercentage;
  private double totalFemalePercentage;
  private HashMap<Integer, List<MdaEmployeeMiniBean>> ministryList;
  private HashMap<Integer, List<MdaEmployeeMiniBean>> parastatalList;
  private HashMap<Integer, List<MdaEmployeeMiniBean>> agencyList;
  private HashMap<Integer, List<MdaEmployeeMiniBean>> boardList;
  
  private LocalDate fromDate;
  private LocalDate toDate;
  private String displayStyle;
  private DecimalFormat df =  this.df = new DecimalFormat("#.##");
    private String mapObj;


  public int compareTo(MPBAMiniBean pO) {
    if ((getName() != null) && (pO.getName() != null))
      return getName().compareToIgnoreCase(pO.getName());
    return 0;
  }


  public String getFemalePercentageStr()
  {
    if ((getNoOfFemales() > 0) && (getStaffStrength() > 0))
    {
      String wFormatRes = this.df.format(new Double(getNoOfFemales()).doubleValue() / new Double(getStaffStrength()).doubleValue() * 100.0D);
      if ((wFormatRes.equals("0")) || (wFormatRes.length() == 1)) {
        DecimalFormat df = new DecimalFormat("#.##");
        wFormatRes = df.format(new Double(getNoOfFemales()).doubleValue() / new Double(getStaffStrength()).doubleValue() * 100.0D);
      }
      this.femalePercentageStr = (wFormatRes + "%");
    } else {
      this.femalePercentageStr = "0.0%";
    }

    return this.femalePercentageStr;
  }

  public void setFemalePercentageStr(String pFemalePercentageStr)
  {
    this.femalePercentageStr = pFemalePercentageStr;
  }

  public String getMalePercentageStr()
  {
    if ((getNoOfMales() > 0) && (getStaffStrength() > 0)) {
      String wFormatRes = this.df.format(new Double(getNoOfMales()).doubleValue() / new Double(getStaffStrength()).doubleValue() * 100.0D);
      if ((wFormatRes.equals("0")) || (wFormatRes.length() == 1)) {
        DecimalFormat df = new DecimalFormat("#.##");
        wFormatRes = df.format(new Double(getNoOfMales()).doubleValue() / new Double(getStaffStrength()).doubleValue() * 100.0D);
      }
      this.malePercentageStr = (wFormatRes + "%");
    } else {
      this.malePercentageStr = "0.0%";
    }

    return this.malePercentageStr;
  }


public String getMapObj()
{
	 mapObj = this.getId() +":"+ this.getObjectId();
	return mapObj;
}



}