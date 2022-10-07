package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.NamedEntityLong;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.report.beans.ReportViewAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;

/**
 * Utility methods for handling entities. Separate from the BaseEntity class
 * mainly because of dependency on the ORM-associated
 * ObjectRetrievalFailureException.
 * 
 * @author Ola Mustapha
 */
public abstract class EntityUtils {

	
	public static double convertDoubleToEpmStandard(double pValue) {
		String value = String.valueOf(pValue);

		if(value.substring(value.indexOf(".") + 1).length() > 2) {

			BigDecimal bd = new BigDecimal(String.valueOf(pValue)).setScale(2, RoundingMode.FLOOR);
			return bd.doubleValue();
		}else {
			return pValue;
		}
	}
	public static HashMap<Long, List<AbstractDeductionEntity>> breakUpDeductionList(List<AbstractDeductionEntity> pEmpListToSet, LocalDate pPayPeriodStart, LocalDate pPayPeriodEnd) {
		HashMap<Long, List<AbstractDeductionEntity>> wRetVal = new HashMap<>();
		if ((pEmpListToSet == null) || (pEmpListToSet.isEmpty()))
			return wRetVal;

		LocalDate wStartDate = pPayPeriodStart;

		LocalDate wEndDate = pPayPeriodEnd;

		LocalDate wDedStartDate;
		LocalDate wDedEndDate;
		for (AbstractDeductionEntity e : pEmpListToSet) {
			if (e.getEmpDeductionType().isMustEnterDate() && e.getStartDate() != null && e.getEndDate() != null) {
				wDedStartDate = e.getStartDate();
				if (wDedStartDate.getMonthValue() > wStartDate.getMonthValue() && (wDedStartDate.getYear() >= wStartDate.getYear())) {
					continue;
				}

				wDedEndDate = e.getEndDate();
				if ((wDedEndDate.getMonthValue() < wEndDate.getMonthValue() && (wDedEndDate.getYear() <= wEndDate.getYear()))) {
					continue;
				}
				if (wDedEndDate.getYear() < wEndDate.getYear())
					continue;

			}
			if (wRetVal.containsKey(e.getParentId())) {
				wRetVal.get(e.getParentId()).add(e);
			} else {
				ArrayList<AbstractDeductionEntity> wList = new ArrayList<>();
				wList.add(e);
				wRetVal.put(e.getParentId(), wList);
			}
		}
		return wRetVal;
	}
	public static HashMap<Long, List<AbstractGarnishmentEntity>> breakUpGarnishmentList(List<AbstractGarnishmentEntity> pGarnListToSplit) {
		HashMap<Long, List<AbstractGarnishmentEntity>> wRetVal = new HashMap<>();
		if ((pGarnListToSplit == null) || (pGarnListToSplit.isEmpty()))
			return wRetVal;
		for (AbstractGarnishmentEntity e : pGarnListToSplit) {
			e.setSortByAmount(true);
			if (wRetVal.containsKey(e.getParentId())) {
				wRetVal.get((e.getParentId())).add(e);
			} else {
				ArrayList<AbstractGarnishmentEntity> wList = new ArrayList<>();
				wList.add(e);
				wRetVal.put(e.getParentId(), wList);
			}
		}
		return wRetVal;
	}

	public static HashMap<Long, List<AbstractSpecialAllowanceEntity>> breakUpAllowanceList(List<AbstractSpecialAllowanceEntity> pAllowListToSplit, LocalDate pStartDate, LocalDate pEndDate)
	  {
		  HashMap<Long, List<AbstractSpecialAllowanceEntity>> wRetVal = new HashMap<>();
	    if ((pAllowListToSplit == null) || (pAllowListToSplit.isEmpty()))
	      return wRetVal;

		  for (AbstractSpecialAllowanceEntity e : pAllowListToSplit)
	    {
			if ((e.getStartDate().getMonthValue() > pStartDate.getMonthValue()) && (e.getStartDate().getYear() >= pStartDate.getYear()))
	      {
	        continue;
	      }

	      if (e.getEndDate() != null) {
			  if ((e.getEndDate().getMonthValue() < pEndDate.getMonthValue()) && (e.getEndDate().getYear() <= pEndDate.getYear()))
	        {
	          continue;
	        }
	        if(e.getEndDate().getYear() < pEndDate.getYear())
	        	continue;

	      }

	      if (e.isExpired()) {
	        continue;
	      }
	      if (wRetVal.containsKey(e.getParentObject().getId())) {
	        wRetVal.get(e.getParentObject().getId()).add(e);
	      } else {
	        ArrayList<AbstractSpecialAllowanceEntity> wList = new ArrayList<AbstractSpecialAllowanceEntity>();
	        wList.add(e);
	        wRetVal.put(e.getParentObject().getId(), wList);
	      }
	    }
	    return wRetVal;
	  }


	public static Map<Long, SalaryInfo> breakSalaryInfo(List<SalaryInfo> list) {
		Map<Long, SalaryInfo> wMap = new HashMap<>();
		for (SalaryInfo s : list) {
			//if (s.getSalaryType().isDeactivated())
			//	continue;
			wMap.put(s.getId(), s);
		}
		return wMap;
	}


	public static int convertDoubleToEpmStandardZeroDecimal(double pValue) {
	    BigDecimal bd = new BigDecimal(String.valueOf(pValue)).setScale(0, RoundingMode.HALF_EVEN);

	    return bd.intValue();
	  }

	 public  static <T> List<T> getDeductionList(HashMap<Long, T> deductionBean)
	  {
	    List <T>list = new ArrayList<T>();

	    Set <Entry<Long,T>>set = deductionBean.entrySet();
	    Iterator <Entry<Long, T>>i = set.iterator();

	    while (i.hasNext()) {
	      Entry <Long,T>me = i.next();
	      list.add(me.getValue());
	    }
	    return list;
	  }

	  public static <T extends ReportViewAttributes> List<T> setFormDisplayStyle(List<T> pEmpList)
	  {
	    int i = 1;
	    for (T e : pEmpList) {
	      if (i % 2 == 1)
	        e.setDisplayStyle("reportEven");
	      else {
	        e.setDisplayStyle("reportOdd");
	      }
	      i++;
	    }
	    return pEmpList;
	  }

	public static <T extends NamedEntityLong> List<T> setFormDisplayStyleOther(List<T> pEmpList)
	{
		int i = 1;
		for (T e : pEmpList) {
			if (i % 2 == 1)
				e.setDisplayStyle("reportEven");
			else {
				e.setDisplayStyle("reportOdd");
			}
			i++;
		}
		return pEmpList;
	}

    public static double divideAsBigDecimal(double yearlyValue, double pDenominator) {
		BigDecimal bd = new BigDecimal(String.valueOf(yearlyValue)).setScale(2, RoundingMode.FLOOR);
		BigDecimal divisor = new BigDecimal(String.valueOf(pDenominator)).setScale(2, RoundingMode.HALF_EVEN);
		bd = bd.divide(divisor, RoundingMode.FLOOR);

		return bd.doubleValue();
    }

	public static double multiplyAsBigDecimal(double yearlyValue, double pMultiplier) {
		BigDecimal bd = new BigDecimal(String.valueOf(yearlyValue)).setScale(2, RoundingMode.FLOOR);
		BigDecimal multiplier = new BigDecimal(String.valueOf(pMultiplier)).setScale(2, RoundingMode.HALF_EVEN);
		bd = bd.multiply(multiplier);

		return bd.doubleValue();
	}

    public static Map<Long, AllowanceRuleMaster> breakUpPayGroupAllowanceList(List<AllowanceRuleMaster> allowanceRuleMasterList) {

			HashMap<Long, AllowanceRuleMaster> wRetVal = new HashMap<>();
			 if(IppmsUtils.isNullOrEmpty(allowanceRuleMasterList))
			 	return wRetVal;
			for (AllowanceRuleMaster e : allowanceRuleMasterList)
			     wRetVal.put(e.getHiringInfo().getEmployee().getId(), e);


			return wRetVal;

    }
}
