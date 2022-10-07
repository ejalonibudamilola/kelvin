package com.osm.gnl.ippms.ogsg.report.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Getter
@Setter
public class RetMainBean
{
  private static final long serialVersionUID = 4102886427571243323L;
  private double empDedTotal;
  private double compContTotal;
  private double planTotal;
  private boolean hasValues;
  private String empDedTotalStr;
  private String compContTotalStr;
  private String planTotalStr;
  private DecimalFormat df;
  private List<RetMiniBean> retMiniBean;
  private HashMap<Long, RetMiniBean> retMiniBeanMap;
  private String displayStyle;
  /**
   * For Simulation...
   */
  private double averageTax;
  private double averageSpecAllow;
  private double averageDeductions;
  
  
  public RetMainBean()
  {
    this.df = new DecimalFormat("#,##0.00");
  }


  public String getEmpDedTotalStr() {
    this.empDedTotalStr = this.df.format(getEmpDedTotal());
    return this.empDedTotalStr;
  }

  public void setEmpDedTotalStr(String pEmpDedTotalStr) {
    this.empDedTotalStr = pEmpDedTotalStr;
  }

  public String getCompContTotalStr() {
    this.compContTotalStr = this.df.format(getCompContTotal());
    return this.compContTotalStr;
  }

  public void setCompContTotalStr(String pCompContTotalStr) {
    this.compContTotalStr = pCompContTotalStr;
  }

  public String getPlanTotalStr() {
    this.planTotalStr = this.df.format(getPlanTotal());
    return this.planTotalStr;
  }

  public void setPlanTotalStr(String pPlanTotalStr) {
    this.planTotalStr = pPlanTotalStr;
  }

  public List<RetMiniBean> getRetMiniBean() {
    this.retMiniBean = getRetMiniBeanInternal();
    return this.retMiniBean;
  }

  public void setRetMiniBean(List<RetMiniBean> pRetMiniBean) {
    this.retMiniBean = pRetMiniBean;
  }

  public HashMap<Long, RetMiniBean> getRetMiniBeanMap() {
    if (this.retMiniBeanMap == null)
      this.retMiniBeanMap = new HashMap<Long, RetMiniBean>();
    return this.retMiniBeanMap;
  }

  private List<RetMiniBean> getRetMiniBeanInternal() {
		
		List<RetMiniBean> list = new ArrayList<RetMiniBean>();
		
		Set<Entry<Long,RetMiniBean>> set = this.getRetMiniBeanMap().entrySet();
		 Iterator<Entry<Long, RetMiniBean>> i = set.iterator();
		 
		 while(i.hasNext()){
			 Entry<Long,RetMiniBean> me = i.next();
			 list.add(me.getValue());
		 }
		 return list;
	}


}