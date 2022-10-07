package com.osm.gnl.ippms.ogsg.payroll.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class PayrollPayUtils
{
  private Calendar today;
  private Calendar hireDate;

  public double calcPolicyCurrentBalance(double pEmpEarn, double pMaxHrs, Date pHireDate, Long pPayPolicyId)
  {
    double currentBalance = 0.0D;

    if (pEmpEarn == 0.0D)
    {
      return 0.0D;
    }

    if (pHireDate == null)
    {
      return 0.0D;
    }

    if (hasAccrualHours(pHireDate))
    {
      long totalWorkingDays = 0L;
      if (this.hireDate.get(1) == this.today.get(1))
      {
        totalWorkingDays = getWorkingDays(this.today, this.hireDate);
      }
      else
      {
        totalWorkingDays = getWorkingDays(this.today, null);
      }

      if (totalWorkingDays < 1L)
      {
        return currentBalance;
      }

      long workDaysInCurrentYear = getWorkingDaysInCurrentYear(this.today);
      currentBalance = getCurrentBalance(totalWorkingDays, workDaysInCurrentYear, pEmpEarn, pMaxHrs, pPayPolicyId);
    }

    return currentBalance;
  }

  private double getCurrentBalance(long noOfDaysWorked, long pWorkDaysInCurrentYear, double pEmpEarn, double pMaxHrs, Long pPayPeriodInstId)
  {
    double currentBalance = 0.0D;

    if (noOfDaysWorked == 0L) {
      return 0.1D;
    }
    currentBalance = pEmpEarn * noOfDaysWorked / pWorkDaysInCurrentYear;
    DecimalFormat df = new DecimalFormat("#0.0#");
    currentBalance = Double.parseDouble(df.format(currentBalance));
    if (currentBalance > pMaxHrs)
      currentBalance = pMaxHrs;
    return currentBalance;
  }

  private long getWorkingDaysInCurrentYear(Calendar pToday)
  {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    cal.set(pToday.get(1), 11, 31);
    Date currentDate = cal.getTime();

    cal.set(pToday.get(1), 0, 1);

    Date workStartDate = cal.getTime();

    return noOfWorkingDays(currentDate, workStartDate);
  }

  public long getWorkingDays(Calendar pToday, Calendar pHireDate)
  {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    cal.set(pToday.get(1), pToday.get(2), pToday.get(5));

    Date currentDate = cal.getTime();
    if (pHireDate != null)
    {
      cal.set(pHireDate.get(1), pHireDate.get(2), pHireDate.get(5));
    }
    else
    {
      cal.set(pToday.get(1), 0, 1);
    }

    Date workStartDate = cal.getTime();

    return noOfWorkingDays(currentDate, workStartDate);
  }

  public long getWorkingDaysForSalary(Calendar pPayPeriodEndDate, Date pHireDate)
  {
    return noOfWorkingDays(pPayPeriodEndDate.getTime(), pHireDate);
  }

  private long noOfWorkingDays(Date firstDate, Date secondDate)
  {
    return getDays(firstDate) - getDays(secondDate);
  }

  private long getDays(Date date)
  {
    long l = div(date.getTime(), 86400000L)[0] + 3L;
    long[] d = div(l, 7L);
    return 5L * d[0] + Math.min(d[1], 5L);
  }

  private long[] div(long n, long d)
  {
    long q = n / d;

    long r = n % d;

    if (r < 0L)
    {
      q -= 1L;
      r += d;
    }
    return new long[] { q, r };
  }

  private boolean hasAccrualHours(Date pHireDate)
  {
    boolean retVal = true;

    this.today = new GregorianCalendar();

    this.hireDate = new GregorianCalendar();
    this.hireDate.setTime(pHireDate);

    if (this.hireDate.after(this.today))
    {
      return false;
    }

    return retVal;
  }

  public static String formatDates(LocalDate dateToFormat)
  {
    return DateTimeFormatter.ofPattern("MMM dd, yyyy").format(dateToFormat);
   /* SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
    return sdf.format(dateToFormat);*/
  }

  public static Date getDateFromPayPeriod(String pPayPeriod, boolean pFirst)
  {
    StringTokenizer str = new StringTokenizer(pPayPeriod, "-", false);
    String strToConvert = "";
    int count = 0;
    while (str.hasMoreElements()) {
      String nextToken = str.nextToken();
      if ((pFirst) && (count == 0)) {
        strToConvert = nextToken;
        break;
      }
      if ((count == 1) && (!pFirst)) {
        strToConvert = nextToken;
        break;
      }

      count++;
    }
    if ((strToConvert != null) && (!strToConvert.equals(""))) {
      Date aDate = getCorrectDate(strToConvert);
      return aDate;
    }
    return null;
  }

  private static Date getCorrectDate(String pStrToConvert)
  {
    String day = pStrToConvert.substring(0, pStrToConvert.indexOf("/")).trim();
    String month = pStrToConvert.substring(pStrToConvert.indexOf("/") + 1, pStrToConvert.lastIndexOf("/")).trim();
    String year = pStrToConvert.substring(pStrToConvert.lastIndexOf("/") + 1).trim();

    int _day = Integer.parseInt(day);
    int _month = Integer.parseInt(month) - 1;
    int _year = Integer.parseInt(year);

    Calendar cal = new GregorianCalendar();
    cal.set(_year, _month, _day);
    return cal.getTime();
  }

  /*public static BusinessPayPolicyInfo findMatchingPolicy(List<BusinessPayPolicyInfo> list, double pCurrentBalance) {
    BusinessPayPolicyInfo bp = null;

    if ((list != null) && (list.size() > 0)) {
      for (BusinessPayPolicyInfo bPPI : list) {
        if (bPPI.getCurrentBalance() == pCurrentBalance) {
          bp = bPPI;
          break;
        }
      }
    }

    return bp;
  }*/

  
  public static double getPartPayment(double pAmount, int pNoOfDaysPaid, int pRunMonth, int pRunYear,boolean pYearlyValue ){
       
	  BigDecimal wNoOfDaysBD = new BigDecimal(  new Double(pNoOfDaysPaid) / new Double(
    		  PayrollBeanUtils.getNoOfDays(pRunMonth, pRunYear) )).setScale(2, RoundingMode.HALF_EVEN);
      BigDecimal wPayAmtBD;
      if(pYearlyValue){
    	  wPayAmtBD = new BigDecimal(Double.toString(pAmount/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
      }else{
    	  wPayAmtBD = new BigDecimal(Double.toString(pAmount)).setScale(2, RoundingMode.HALF_EVEN); 
      }
      wPayAmtBD = wPayAmtBD.multiply(wNoOfDaysBD);
      return EntityUtils.convertDoubleToEpmStandard(wPayAmtBD.doubleValue());
      
  }

public static double getPartPayment(double pAmount, int pNoOfDays,
		int pNoOfDaysInMonth, boolean pYearlyValue)
{
	 BigDecimal wNoOfDaysBD = new BigDecimal(  new Double(pNoOfDays) / new Double(pNoOfDaysInMonth )).setScale(2, RoundingMode.HALF_EVEN);
     // BigDecimal wNoOfDaysBD = new BigDecimal(  String.valueOf(new Double(pNoOfDaysInMonth )));

     BigDecimal wPayAmtBD;
     if(pYearlyValue){
   	  wPayAmtBD = new BigDecimal(Double.toString(pAmount/12.0D)).setScale(2, RoundingMode.HALF_EVEN);
     }else{
   	  wPayAmtBD = new BigDecimal(Double.toString(pAmount)).setScale(2, RoundingMode.HALF_EVEN); 
     }
     BigDecimal wRetVal = wPayAmtBD.multiply(wNoOfDaysBD).setScale(2, RoundingMode.HALF_EVEN);
     return wRetVal.doubleValue();
}
  public static double convertDoubleToEpmStandard(double pValue) {
    BigDecimal bd = new BigDecimal(String.valueOf(pValue)).setScale(2, RoundingMode.HALF_EVEN);

    return bd.doubleValue();
  }


}