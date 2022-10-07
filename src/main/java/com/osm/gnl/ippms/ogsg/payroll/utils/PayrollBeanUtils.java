package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.ConjunctionType;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RbaConfigBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.approval.EmployeeApproval;
import com.osm.gnl.ippms.ogsg.domain.approval.StepIncrementApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.SetupEmployeeMaster;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignMasterBean;
import com.osm.gnl.ippms.ogsg.domain.notifications.NotificationObject;
import com.osm.gnl.ippms.ogsg.domain.paygroup.MasterSalaryTemp;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.approval.AmAliveApproval;
import com.osm.gnl.ippms.ogsg.domain.payment.BusinessPaySchedule;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriod;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.FlaggedPromotions;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusMasterBean;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.domain.approval.TransferApproval;
import lombok.NonNull;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Map.Entry;


public class PayrollBeanUtils {


    public static PayPeriodDaysBean getMonthlyPayPeriod(PayrollFlag pPf) {

        PayPeriodDaysBean ppdb = new PayPeriodDaysBean();
        List<String> payPeriods = new ArrayList<String>();
        LocalDate today = LocalDate.now();
        LocalDate lastDayofCurrentMonth = null;
        if (pPf.isNewEntity()) {
            //First time -- use BusinessPaySchedule Start Date as the Month and roll back 1 Month.
            //PayPeriodDays ppd = EntityUtils.getById(pPPD, PayPeriodDays.class, pBps.getPeriodDayInstId());

            StringBuffer day = new StringBuffer();


            lastDayofCurrentMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
            int _day = lastDayofCurrentMonth.getMonthValue();


			/*if(today.get(Calendar.MONTH) == 1 && _day >= 28){
				if(isLeapYear(today.get(Calendar.YEAR))){
					_day = 29;
				}else{
					_day = 28;
				}

			}*/

            LocalDate endDate = LocalDate.of(lastDayofCurrentMonth.getYear(), lastDayofCurrentMonth.getMonthValue(), _day);
            LocalDate startDate = LocalDate.of(lastDayofCurrentMonth.getYear(), lastDayofCurrentMonth.getMonthValue(), 1);
            //int addendum = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);

            //startDate.add(Calendar.DATE, - (addendum - 1));
            ppdb.setCurrentPayPeriodStart(startDate);
            ppdb.setCurrentPayPeriodEnd(endDate);
            ppdb.setPayPeriod(getDateAsString(startDate) + " - " + getDateAsString(endDate));
            payPeriods.add(ppdb.getPayPeriod());

            ppdb.setPayPeriodList(payPeriods);
        } else {
            //This means we have a payroll run. Use PayrollFlag endDate to get the next start Pay Period.
            LocalDate startDate = pPf.getPayPeriodEnd();
            LocalDate newStartDate = startDate.plusDays(1L);
            lastDayofCurrentMonth = newStartDate.with(TemporalAdjusters.lastDayOfMonth());
            LocalDate endDate = lastDayofCurrentMonth;
			/*	int addendum = 0;
		if(startDate.get(Calendar.MONTH) > today.get(Calendar.MONTH)){
				//This means we want to run for a future month...
				//Leave startDate as is...
				//startDate.setTime(pPf.getPayPeriodStart());
				endDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
				ppdb.setCurrentPayPeriodStart(startDate);
				ppdb.setCurrentPayPeriodEnd(endDate);
				ppdb.setPayPeriod(getDateAsString(startDate)+" - "+getDateAsString(endDate));
				payPeriods.add(getDateAsString(startDate)+" - "+getDateAsString(endDate));

			}else{*/

            ppdb.setCurrentPayPeriodStart(newStartDate);
            ppdb.setCurrentPayPeriodEnd(endDate);
            ppdb.setPayPeriod(getDateAsString(newStartDate) + " - " + getDateAsString(endDate));
            payPeriods.add(getDateAsString(newStartDate) + " - " + getDateAsString(endDate));
            //Now get the previous month.

            payPeriods.add(getDateAsString(pPf.getPayPeriodStart()) + " - " + getDateAsString(pPf.getPayPeriodEnd()));
            //}

        }
        ppdb.setPayPeriodList(payPeriods);

        return ppdb;
    }

    public static BusinessPaySchedule getDefaultBusinessPaySchedule(List<BusinessPaySchedule> pBusPaySched) throws Exception {
        BusinessPaySchedule bps = new BusinessPaySchedule();

        if ((pBusPaySched == null) || (pBusPaySched.isEmpty()))
            return bps;
        if (pBusPaySched.size() == 1) {
            return pBusPaySched.get(0);
        }


        for (BusinessPaySchedule b : pBusPaySched) {
            if (b.getUseAsDefault().equalsIgnoreCase("y")) {
                return b;
            }

        }

        switch (pBusPaySched.size()) {
            case 2:
                try {
                    PayPeriod pp1 = pBusPaySched.get(0).getPayPeriod();
                    PayPeriod pp2 = pBusPaySched.get(1).getPayPeriod();
                    if (pp1.getPayPeriodValue() > pp2.getPayPeriodValue())
                        bps = pBusPaySched.get(0);
                    else {
                        bps = pBusPaySched.get(1);
                    }
                } catch (Exception ex) {
                    throw new Exception("Exception Thrown " + ex.getMessage());
                }
            case 3:
                try {
                    PayPeriod pp1 = pBusPaySched.get(0).getPayPeriod();
                    PayPeriod pp2 = pBusPaySched.get(1).getPayPeriod();
                    PayPeriod pp3 = pBusPaySched.get(2).getPayPeriod();
                    if ((pp1.getPayPeriodValue() > pp2.getPayPeriodValue()) && (pp1.getPayPeriodValue() > pp3.getPayPeriodValue()))
                        bps = pBusPaySched.get(0);
                    else if ((pp2.getPayPeriodValue() > pp1.getPayPeriodValue()) && (pp2.getPayPeriodValue() > pp3.getPayPeriodValue()))
                        bps = pBusPaySched.get(1);
                    else
                        bps = pBusPaySched.get(2);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

        }

        return bps;
    }

    public static String getDateAsString(LocalDate date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return dateFormat.format(date);
    }

    public static String getJavaDateAsString(LocalDate date) {
        if (date == null)
            return "";
        return getDateAsString(date);
    }

  /*  public static String customDateFormat(LocalDate sDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMMM-yyyy");
        return dateFormat.format(sDate);
    }

    public static String customDateTimeStampFormat(LocalDate sDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
        return dateFormat.format(sDate);
    }
*/
    public static String getCurrentYearAsString() {

        LocalDate localDate = LocalDate.now();
        return String.valueOf(localDate.getYear());

    }

    public static String getYearAsString(LocalDate pCal) {
        return String.valueOf(pCal.getYear());
    }

    public static boolean isLeapYear(int year) {

        return LocalDate.of(year, 1, 1).isLeapYear();


    }

    public static ArrayList<LocalDate> getDefaultCheckRegisterDates() {
        ArrayList<LocalDate> retVal = new ArrayList<LocalDate>();

        LocalDate today = LocalDate.now();
        int _day = 0;
        if (today.getMonthValue() == 1) {
            if (isLeapYear(today.getYear())) {
                _day = 29;
            } else {
                _day = 28;
            }

        } else {
//				_day = getMaxDayOfMonth(today);
            _day = today.lengthOfMonth();
        }

        LocalDate endDate = LocalDate.of(today.getYear(), today.getMonthValue(), _day);

        LocalDate startDate = LocalDate.of(today.getYear(), today.getMonthValue(), 1);


        retVal.add(0, startDate);
        retVal.add(1, endDate);


        return retVal;

    }

    private static int getMaxDayOfMonth(LocalDate pDate) {

        if(pDate.isLeapYear())
            return pDate.getMonth().maxLength();
        return pDate.getMonth().minLength();

    }

    public static LocalDate getPreviousMonthDate(LocalDate pDate, boolean pFindEnd) {
//      Date wRetVal = Calendar.getInstance().getTime();
        LocalDate wRetVal = LocalDate.now();
        int _day = 0;

        int wCurrMonth = pDate.getMonthValue();
        int wPrevMonth = 0;
//    GregorianCalendar wPrevDate = new GregorianCalendar();
        LocalDate wPrevDate;
        if (wCurrMonth == 1) {
            wPrevMonth = 12;
//      wPrevDate.set(pDate.getYear() - 1, wPrevMonth, 1);
            wPrevDate = LocalDate.of(pDate.getYear() - 1, wPrevMonth, 1);
        } else {
            wPrevMonth = wCurrMonth - 1;
//      wPrevDate.set(pDate.getYear(), wPrevMonth, 1);
            wPrevDate = LocalDate.of(pDate.getYear(), wPrevMonth, 1);
        }
        if (pFindEnd) {


            if (wPrevMonth == 1) {
                if (isLeapYear(pDate.getYear()))
                    _day = 29;
                else {
                    _day = 28;
                }

            } else {

                _day = wPrevDate.lengthOfMonth();
            }
            LocalDate wCal = LocalDate.now();

            wRetVal = wCal.of(wPrevDate.getYear(), wPrevMonth, _day);
        } else {

            LocalDate wCal = LocalDate.now();

            wRetVal = wCal.of(wPrevDate.getYear(), wPrevMonth, 1);
            ;
        }
        return wRetVal;
    }

    public static LocalDate setDateFromString(String pDateAsString) {
        LocalDate cal = null;
        String[] retVal = new String[3];
        if (pDateAsString != null) {
            retVal = StringUtils.tokenizeToStringArray(pDateAsString, "/", true, true);
        }
        if ((retVal != null) && (retVal.length == 3)) {
            try {
                cal = LocalDate.of(Integer.parseInt(retVal[2]), Integer.parseInt(retVal[1]), Integer.parseInt(retVal[0]));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return cal;
    }

    public static LocalDate setDateFromStringCustomReporter(String pDateAsString) {
        LocalDate cal = null;
        String[] retVal = new String[3];
        if (pDateAsString != null) {
            retVal = StringUtils.tokenizeToStringArray(pDateAsString, "-", true, true);
        }
        if ((retVal != null) && (retVal.length == 3)) {
            try {
                cal = LocalDate.of(Integer.parseInt(retVal[0]), Integer.parseInt(retVal[1]), Integer.parseInt(retVal[2]));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return cal;
    }

 /*   public static Date setDateFromLocalDate(LocalDate pLocalDate) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(pLocalDate.getYear(), pLocalDate.getMonthValue() - 1, pLocalDate.getDayOfMonth());
        return calendar.getTime();

    }
*/
    public static Calendar setDateFromString(String pDateAsString, boolean pStrict) throws Exception {
        Calendar cal = new GregorianCalendar();
        String[] retVal = new String[3];
        if (pDateAsString != null) {
            retVal = StringUtils.tokenizeToStringArray(pDateAsString, "/", true, true);
        }

        try {
            if ((Integer.parseInt(retVal[1]) > 12) || (Integer.parseInt(retVal[1]) < 1))
                throw new Exception("Bad Date Format");
            if ((Integer.parseInt(retVal[0]) < 1) || (Integer.parseInt(retVal[0]) > 31))
                throw new Exception("Bad Date Format");
            cal.set(Integer.parseInt(retVal[2]), Integer.parseInt(retVal[1]) - 1, Integer.parseInt(retVal[0]));
        } catch (Exception ex) {
            throw ex;
        }

        return cal;
    }

    /**
     * @param pForFileNaming
     * @return
     * @Note - if true returns time as 121432PM else 12:14:32 PM
     */
    public static String getCurrentTime(boolean pForFileNaming) {
        String wCurrentTime = "";
        Calendar calendar = new GregorianCalendar();

        int hour = calendar.get(10);
        int minute = calendar.get(12);
        int second = calendar.get(13);
        String am_pm;

        if (calendar.get(9) == 0)
            am_pm = "AM";
        else {
            am_pm = "PM";
        }
        String _minute = String.valueOf(minute);

        if (_minute.length() == 1) {
            _minute = "0" + _minute;
        }
        String _second = String.valueOf(second);
        if (_second.length() == 1) {
            _second = "0" + _second;
        }
        if (pForFileNaming)
            wCurrentTime = hour + "" + _minute + "" + _second + "" + am_pm;
        else
            wCurrentTime = hour + ":" + _minute + ":" + _second + " " + am_pm;
        return wCurrentTime;
    }

    /**
     * Get current Time For File Naming..
     * 123517
     *
     * @return
     */
    public static String getCurrentTimeWidoutAMPM() {
        String wCurrentTime = "";
        LocalTime localTime = LocalTime.now();

        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        int second = localTime.getSecond();


        String _minute = String.valueOf(minute);

        if (_minute.length() == 1) {
            _minute = "0" + _minute;
        }
        String _second = String.valueOf(second);
        if (_second.length() == 1) {
            _second = "0" + _second;
        }
        wCurrentTime = hour + "" + _minute + "" + _second;

        return wCurrentTime;
    }


    /**
     * @param @link java.lang.int  pMonth
     * @param @link java.lang.int  pYear
     * @return Month, Year e.g., January, 2013.
     */
    public static String getMonthNameAndYearFromCalendarMonth(int pMonth, int pYear) {
        return LocalDate.of(pYear, pMonth, 1).getMonth().name() + ", " + pYear;
    }

    public static String getSimulationLengthInMonths(int pNoOfMonthsInd) {
        pNoOfMonthsInd++;
        return pNoOfMonthsInd + " Months.";
    }

    public static String getMonthNameFromInteger(int pMonthId) {
        return LocalDate.of(2020, pMonthId, 1).getMonth().name();
    }

    public static String getShortMonthNameFromInteger(int pMonthId) {
        Calendar wCal = Calendar.getInstance();

        wCal.set(2, pMonthId);

        return wCal.getDisplayName(2, 1, Locale.UK);
    }

    public static Map<Long, SalaryInfo> breakSalaryInfo(List<SalaryInfo> list) {
        HashMap<Long, SalaryInfo> wMap = new HashMap<Long, SalaryInfo>();
        for (SalaryInfo s : list) {
            wMap.put(s.getId(), s);
        }
        return wMap;
    }

    public static String makePayPeriod(LocalDate pCal, LocalDate pCal2) {

        return getDateAsString(pCal) + " - " + getDateAsString(pCal2);
    }

    public static String makePayPeriod(LocalDate pEndPayPeriod) {

        return createPayPeriodFromInt(pEndPayPeriod.getMonthValue(), pEndPayPeriod.getYear());
    }

    public static LocalDate makeNextPayPeriod(LocalDate pCal, int pMonth, int pYear) {
        LocalDate wRetVal;
        LocalDate wFirstTry;
        if (pMonth == 12) {
            pYear++;
            pMonth = 1;
        } else {
            pMonth++;
        }
        wFirstTry = LocalDate.of(pYear, pMonth, 1);
        wRetVal = LocalDate.of(pYear, pMonth, wFirstTry.lengthOfMonth());

        return wRetVal;
    }

    public static LocalDate makeNextPayPeriodStart(int pMonth, int pYear) {


        if (pMonth == 12) {
            pYear++;
            pMonth = 1;
        } else {
            pMonth++;
        }

        return LocalDate.of(pYear, pMonth, 1);
    }

    public static LocalDate getDateFromMonthAndYear(int pMonth, int pYear) {

        return LocalDate.of(pYear, pMonth, 1);
    }

    public static LocalDate getDateFromMonthAndYear(int pMonth, int pYear, boolean pLastDay) {

        LocalDate start = LocalDate.of(pYear, pMonth, 1);


        if (pLastDay) {
            LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
            return end;
        }
        return start;
    }

    public static String createPayPeriodFromInt(int pSimulationMonth, int pSimulationYear) {
        LocalDate start = LocalDate.of(pSimulationYear, pSimulationMonth, 1);

        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return getDateAsString(start) + " - " + getDateAsString(end);
    }

    public static String createPayPeriodFromInt(int pSimulationMonth, int pSimulationYear, boolean pFirstDay) {
        LocalDate wGc = LocalDate.of(pSimulationYear, pSimulationMonth, 1);

        LocalDate _wGc = LocalDate.of(pSimulationYear, pSimulationMonth, getMaxDayOfMonth(wGc));

        if (pFirstDay)
            return getDateAsString(wGc);

        return getDateAsString(_wGc);

    }

    public static String getDateAsStringWidoutSeparators(LocalDate pTime) {
        String wRetVal = getDateAsString(pTime);
        char[] wArrayChar = wRetVal.toCharArray();
        StringBuffer wRetValStr = new StringBuffer();
        for (char c : wArrayChar) {
            if (Character.isDigit(c)) {
                wRetValStr.append(c);
            }
        }
        return wRetValStr.toString();
    }

    public static LocalDate getDateFromPayPeriod(String pPayPeriod, boolean pFirst) {
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
            LocalDate aDate = getCorrectDate(strToConvert);
            return aDate;
        }

        return null;
    }

    private static LocalDate getCorrectDate(String pStrToConvert) {
        String day = pStrToConvert.substring(0, pStrToConvert.indexOf("/")).trim();
        String month = pStrToConvert.substring(pStrToConvert.indexOf("/") + 1, pStrToConvert.lastIndexOf("/")).trim();
        String year = pStrToConvert.substring(pStrToConvert.lastIndexOf("/") + 1).trim();

        int _day = Integer.parseInt(day);
        int _month = Integer.parseInt(month);
        int _year = Integer.parseInt(year);


        return LocalDate.of(_year, _month, _day);
    }

    public static boolean unionDuesDeducted(LocalDate pToDate) {
        return (pToDate.getMonthValue() < 10) && (pToDate.getYear() <= 2011);
    }

    public static boolean isUnionDuesDeducted(LocalDate pCalendarObject) {
        return false;
        //return ((pCalendarObject.getMonthValue() < 10) || (pCalendarObject.getYear() != 2011)) && ((pCalendarObject.getMonthValue() != 1) || (pCalendarObject.getYear() != 2012));
    }

    public static int getNoOfDays(int pRunMonth, int pRunYear) {
        LocalDate wCal = LocalDate.of(pRunYear, pRunMonth, 1);

         return getMaxDayOfMonth(wCal);
    }

    public static ArrayList<NamedEntity> makeMonthList() {
        LocalDate wCal = LocalDate.now();
        ArrayList<NamedEntity> wMonthList = new ArrayList<>();

        int wYear = wCal.getYear();

        for (int wStart = wCal.getMonthValue(); (wStart <= 12) && (wCal.getYear() == wYear); wStart++) {
            NamedEntity n = new NamedEntity();
            n.setId((long) wStart);
            n.setName(LocalDate.of(wYear, wStart, 1).getMonth().getDisplayName(TextStyle.FULL, Locale.UK));
            wMonthList.add(n);
        }

        Collections.sort(wMonthList, Comparator.comparing(NamedEntity::getId));
        return wMonthList;
    }

    public static ArrayList<NamedEntity> makeAllMonthList() {
        //Calendar wCal = Calendar.getInstance();
        ArrayList<NamedEntity> wMonthList = new ArrayList<>();

        //int wYear = wGC.get(1);

        for (int wStart = 1; wStart <= 12; wStart++) {
            NamedEntity n = new NamedEntity();
            n.setId((long) wStart);
            n.setName(LocalDate.of(2020, wStart, 1).getMonth().getDisplayName(TextStyle.FULL, Locale.UK));
            //wGC.roll(2, 1);
            wMonthList.add(n);
        }
        Comparator<NamedEntity> comp = Comparator.comparing(NamedEntity::getId);
        Collections.sort(wMonthList, comp);
        return wMonthList;
    }

    public static LocalDate getEndOfMonth(LocalDate wCal) {
        return LocalDate.of(wCal.getYear(), wCal.getMonthValue(), getMaxDayOfMonth(wCal));

    }


    public static boolean isTPSEmployee(LocalDate pBirthDate, LocalDate pHireDate, LocalDate pExpDateOfRetire
            , LocalDate pTPSBaseHireDate, LocalDate pTPSBaseRetireDate, ConfigurationBean configurationBean, BusinessCertificate businessCertificate) throws Exception {
        boolean wRetVal = false;

        boolean wHireDateOnOrBef31Dec2007;
        boolean wExpRetDateOnOrBef1Jul2025;

        LocalDate wHireDate = pHireDate;

        wHireDateOnOrBef31Dec2007 = wHireDate.compareTo(pTPSBaseHireDate) <= 0;

        if (pExpDateOfRetire == null) {
            pExpDateOfRetire = calculateExpDateOfRetirement(pBirthDate, pHireDate, configurationBean, businessCertificate);
        }

        wExpRetDateOnOrBef1Jul2025 = pExpDateOfRetire.compareTo(pTPSBaseRetireDate) <= 0;

        if (wHireDateOnOrBef31Dec2007 && wExpRetDateOnOrBef1Jul2025) {
            wRetVal = true;
        }


        return wRetVal;
    }

    public static boolean isTPSEmployeeOld(Date pBirthDate, Date pHireDate, Date pExpDateOfRetire
            , GregorianCalendar pTPSStartDate, GregorianCalendar pTPSEndDate) throws Exception {


        if (pHireDate != null) {
            GregorianCalendar wGc = new GregorianCalendar();
            wGc.setTime(pHireDate);
            return wGc.compareTo(pTPSStartDate) <= 0;
        }
        //Now lets look for the ones that are date dependent....
	  /*if(pExpDateOfRetire == null){
		  pExpDateOfRetire = calculateExpDateOfRetirement(pBirthDate,pHireDate);
	  }
	  Calendar wHireDate = new GregorianCalendar();
	  wHireDate.setTime(pHireDate);
	  GregorianCalendar wCompareHireDate = getCalendarDateFromString(IConstants.TPS_AGE_START);

	  if(wHireDate.compareTo(wCompareHireDate) <= 0){
		  GregorianCalendar wCal = getCalendarDateFromString(IConstants.TPS_AGE_END);
		  Calendar wRetirementDate = new GregorianCalendar();
		  wRetirementDate.setTime(pExpDateOfRetire);
		  if(wRetirementDate.compareTo(wCal) <= 0){
			  wRetVal = true;
		  }
	  }*/

        return false;
    }

    public static LocalDate calculateExpDateOfRetirement(LocalDate pBirthDate, LocalDate pHireDate, ConfigurationBean configurationBean,BusinessCertificate businessCertificate) {

        if (businessCertificate.isPensioner())
            return LocalDate.of(pBirthDate.getYear() + configurationBean.getIamAlive(), pBirthDate.getMonthValue(), pBirthDate.getDayOfMonth());

        LocalDate thirtyFiveYearsOfService = pHireDate.plusYears(configurationBean.getServiceLength());
        LocalDate sixtyYearsFromBirthDate = pBirthDate.plusYears(configurationBean.getAgeAtRetirement());

        if (sixtyYearsFromBirthDate.compareTo(thirtyFiveYearsOfService) <= 0) {
            return sixtyYearsFromBirthDate;
        } else {
            return thirtyFiveYearsOfService;
        }

    }

    public static LocalDate getLocalDateFromString(String pSDate) {
        LocalDate wRetVal = LocalDate.now();

        try {
            wRetVal = getCorrectDate(pSDate);

        } catch (Exception wEx) {
            return wRetVal;
        }
        return wRetVal;
    }

    /**
     * @param pCal
     * @param pNext - If true, it calculates the next day if false, it returns the previous day.
     * @return
     */
    public static LocalDate getNextORPreviousDay(LocalDate pCal, boolean pNext) {
        LocalDate localDate;
        if (pNext)
            localDate = pCal.plusDays(1L);
        else
            localDate = pCal.minusDays(1L);


        return localDate;

    }


    public static void endPayrollRun(PayrollRunMasterBean pPayrollRunMasterBean,
                                     RerunPayrollBean pRerunPayrollBean, GenericService genericService, Long pBid) throws InstantiationException, IllegalAccessException {
        RbaConfigBean wRCB = genericService.loadObjectWithSingleCondition(RbaConfigBean.class, CustomPredicate.procurePredicate("businessClientId", pBid));
        if (!wRCB.isNewEntity())
            pPayrollRunMasterBean.setRbaPercentage(wRCB.getRbaPercentage());
        pPayrollRunMasterBean.setEndDate(LocalDate.now());
        pPayrollRunMasterBean.setEndTime(getCurrentTime(false));
        if (!pPayrollRunMasterBean.isPayrollError() && !pPayrollRunMasterBean.isPayrollCancelled())
            pPayrollRunMasterBean.setPayrollStatus(IConstants.AWAITING_APPROVAL_IND);
        genericService.saveObject(pPayrollRunMasterBean);

        if (pRerunPayrollBean != null) {
            pRerunPayrollBean.setRerunInd(IConstants.OFF);
            genericService.saveOrUpdate(pRerunPayrollBean);
        }


    }

    public static List<NamedEntityBean> getListFromMap(
            HashMap<String, NamedEntityBean> pModelBean, boolean pSetDisplay) {
        List<NamedEntityBean> wRetList = new ArrayList<NamedEntityBean>();
        Set<Entry<String, NamedEntityBean>> wHashMapKeys = pModelBean.entrySet();
        Iterator<Entry<String, NamedEntityBean>> i = wHashMapKeys.iterator();

        while (i.hasNext()) {
            Entry<String, NamedEntityBean> me = i.next();
            if (pSetDisplay) {

                me.getValue().setDisplayStyle("reportOdd");
            }

            wRetList.add(me.getValue());
        }
        Collections.sort(wRetList);
        return wRetList;
    }

    public static String getMonthNameAndYearForExcelNaming(
            int pMonth, int pYear, boolean pUseShortMonthName, boolean pNoUnderScore) {
        String wRetVal = "";
        LocalDate localDate = LocalDate.of(pYear,pMonth,1);

        if (pUseShortMonthName) {
            if(pNoUnderScore)
               wRetVal = localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK) + " " + pYear;
            else
                wRetVal = localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK) + "_" + pYear;
        } else {
            if(pNoUnderScore)
                wRetVal = localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + " " + pYear;
            else
                wRetVal = localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + "_" + pYear;
        }
        return wRetVal;
    }

    public static LocalDate calculate35YearsAgo(LocalDate pPayPeriodDate, ConfigurationBean configurationBean) {
        LocalDate retVal = pPayPeriodDate.minusYears(new Long(configurationBean.getServiceLength()));
        return retVal;
    }

    public static LocalDate calculate60yrsAgo(LocalDate pPayPeriodDate, boolean pensioner, ConfigurationBean configurationBean) {

        if (pensioner) {
            return pPayPeriodDate.minusYears(new Long(configurationBean.getIamAlive()));
        } else {
            return pPayPeriodDate.minusYears(new Long(configurationBean.getAgeAtRetirement()));
        }


    }
//      public static int hiredDateVersusBirthDate(LocalDate pSixtyYearsAgo, LocalDate p35YearsAgo,
//                                                 LocalDate pHireDate, LocalDate pBirthDate ){
//      int wRetVal = 0;
//      LocalDate wBirthDate = pBirthDate;
//      LocalDate wHireDate = pHireDate;
//
//
//      //-- Determine if this dude was retired because of hired date.
//      if(wHireDate.isBefore(p35YearsAgo)){
//          wRetVal = 1;
//          return wRetVal;
//      }else
//      if(wBirthDate.isBefore(pSixtyYearsAgo)){
//          wRetVal = 2;
//          return wRetVal;
//      }
//      return  wRetVal;
//  }


    public static String createPayPeriod(int pRunMonth, int pRunYear,
                                         int pToMonth, int pToYear, boolean pFileName, boolean pFullMonthName) {
        String wRetVal = null;
        LocalDate pFromDate = getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate wToDate = getDateFromMonthAndYear(pToMonth, pToYear);
        if (pToYear - pRunYear > 0) {
            //-- This will be in Jan/January 9999 - Feb/February 2009
            if (pFileName) {
                wRetVal = pFromDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                        + "_" + pRunYear + "_" + wToDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                        + "_" + pToYear;
            } else {
                if (pFullMonthName) {
                    wRetVal = pFromDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK)
                            + " " + pRunYear + " - " + wToDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK)
                            + " " + pToYear;
                } else {
                    wRetVal = pFromDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                            + " " + pRunYear + " - " + wToDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                            + " " + pToYear;
                }
            }

        } else if (pToMonth > pRunMonth && pRunYear == pToYear) {
            //-- This will be in Jan-Mar 2001
            if (pFileName) {
                wRetVal = pFromDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                        + "_" + wToDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                        + "_" + pToYear;
            } else {
                if (pFullMonthName) {
                    wRetVal = pFromDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK)
                            + "-" + wToDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK)
                            + " " + pToYear;
                } else {
                    wRetVal = pFromDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                            + "-" + wToDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                            + " " + pToYear;
                }
            }
        } else {
            //-- This will be in Jan 2010
            if (pFileName) {
                wRetVal = pFromDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                        + "_" + pToYear;
            } else {
                if (pFullMonthName) {
                    wRetVal = pFromDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK)
                            + " " + pToYear;
                } else {
                    wRetVal = pFromDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK)
                            + " " + pToYear;
                }
            }
        }
        return wRetVal;
    }

    public static Collection<NamedEntity> getNotificationBeans(GenericService genericService, IMenuService pMenuService, BusinessCertificate bc) {
        List<NamedEntity> wRetList = new ArrayList<>();
        PredicateBuilder approvalPredicate = new PredicateBuilder()
                .addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()))
                .addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
        int wNoOfEmpApprovals = genericService.countObjectsUsingPredicateBuilder(approvalPredicate, EmployeeApproval.class);
        int wTotalNoOfTransfers = genericService.countObjectsUsingPredicateBuilder(approvalPredicate, TransferApproval.class);
        int wNoOfIamAlive = genericService.countObjectsUsingPredicateBuilder(approvalPredicate, AmAliveApproval.class);
        int wNoOfPayGroupApprovals = genericService.countObjectsUsingPredicateBuilder(approvalPredicate, MasterSalaryTemp.class);
        int wNoOfStepIncApprovals = genericService.countObjectsUsingPredicateBuilder(approvalPredicate, StepIncrementApproval.class);

        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("rejectedInd", IConstants.OFF));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvedInd", IConstants.OFF));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        int wNoOfEmployeeNameConflicts = genericService.countObjectsUsingPredicateBuilder(predicateBuilder, SetupEmployeeMaster.class);
        int wNoOfLeaveBonus = genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("mdaInfo.businessClientId", bc.getBusinessClientInstId())).addPredicate(CustomPredicate.procurePredicate("approvedInd", IConstants.OFF)), LeaveBonusMasterBean.class);

        PredicateBuilder orPredicate = new PredicateBuilder(ConjunctionType.OR);
        orPredicate.addPredicate(CustomPredicate.procurePredicate("login.id",
                new User(bc.getLoginId()))).addPredicate(CustomPredicate.procurePredicate("approver.id",
                new User(bc.getLoginId())));

        int wNoOfApprovalUpdate = genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId",
                bc.getBusinessClientInstId())).addBuilder(orPredicate).addPredicate(CustomPredicate.procurePredicate("ticketOpen", IConstants.OFF))
                .addPredicate(CustomPredicate.procurePredicate("responseInd",IConstants.OFF)), NotificationObject.class);

        //Add Flagged Promotions for Notification
        int wNoOfFlagPromo = genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())).addPredicate(CustomPredicate.procurePredicate("statusInd", IConstants.OFF)), FlaggedPromotions.class);

        //Add Mass Reassignment Approval Notification
        int wNoOfMassApprovals = genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())).addPredicate(CustomPredicate.procurePredicate("approvalStatus", IConstants.OFF)), MassReassignMasterBean.class);

        if (wNoOfFlagPromo > 0 && pMenuService.canUserAccessURL(bc, "/viewPendingFlaggedPromo.do", "/viewPendingFlaggedPromo.do") && bc.isSuperAdmin()) {
            wRetList.add(new NamedEntity(wNoOfFlagPromo, "/viewPendingFlaggedPromo.do", "View Pending Flagged Promotions"));
        }
        if (wTotalNoOfTransfers > 0 && pMenuService.canUserAccessURL(bc, "/viewPendingTransfers.do", "/viewPendingTransfers.do")) {
            wRetList.add(new NamedEntity(wTotalNoOfTransfers, "/viewPendingTransfers.do", "View Pending Transfers"));
        }

        if (wNoOfEmpApprovals > 0 && pMenuService.canUserAccessURL(bc, "/viewEmpForPayApproval.do", "/viewEmpForPayApproval.do")) {
            wRetList.add(new NamedEntity(wNoOfEmpApprovals, "/viewEmpForPayApproval.do", "View Pending " + bc.getStaffTypeName()));
        }

        if (wNoOfEmployeeNameConflicts > 0 && pMenuService.canUserAccessURL(bc, "/viewPendingConflicts.do", "/viewPendingConflicts.do")) {
            wRetList.add(new NamedEntity(wNoOfEmployeeNameConflicts, "/viewPendingConflicts.do", "View " + bc.getStaffTypeName() + " Name Conflicts"));
        }

        if (wNoOfLeaveBonus > 0 && pMenuService.canUserAccessURL(bc, "/viewPendingLeaveBonus.do", "/viewPendingLeaveBonus.do")) {
            wRetList.add(new NamedEntity(wNoOfLeaveBonus, "/viewPendingLeaveBonus.do", "View Pending Leave Bonus"));
        }

        if (wNoOfMassApprovals > 0 && pMenuService.canUserAccessURL(bc, "/approveMassReassign.do", "/approveMassReassign.do")) {
            wRetList.add(new NamedEntity(wNoOfMassApprovals, "/approveMassReassign.do", "View Pending Mass Reassignment"));
        }
        if (wNoOfIamAlive > 0 && pMenuService.canUserAccessURL(bc, "/viewApprovalRejectIamAlive.do", "/viewApprovalRejectIamAlive.do")) {
            wRetList.add(new NamedEntity(wNoOfIamAlive, "/viewApprovalRejectIamAlive.do", "View Pending 'Am Alive' Requests"));
        }

        if (wNoOfPayGroupApprovals > 0 && pMenuService.canUserAccessURL(bc, "/approveSalaryStructure.do", "/approveSalaryStructure.do")) {
            wRetList.add(new NamedEntity(wNoOfPayGroupApprovals, "/approveSalaryStructure.do", "View Pending 'Approve Pay Group' Requests"));
        }

        if(wNoOfApprovalUpdate > 0 && pMenuService.canUserAccessURL(bc, "/approvalUpdates.do", "/approvalUpdates.do" )){
            wRetList.add(new NamedEntity(wNoOfApprovalUpdate, "/approvalUpdates.do", "View Approval Update"));
        }

        if(wNoOfStepIncApprovals > 0 && pMenuService.canUserAccessURL(bc, "/viewStepIncrementApproval.do", "/viewStepIncrementApproval.do" )){
            wRetList.add(new NamedEntity(wNoOfStepIncApprovals, "/viewStepIncrementApproval.do", "View Pending Step Increment Approvals"));
        }

        return wRetList;

    }

    public static String getCurrentTime() {
        String wCurrentTime = "";
        Calendar calendar = new GregorianCalendar();
        String am_pm;
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (calendar.get(Calendar.AM_PM) == 0)
            am_pm = "AM";
        else
            am_pm = "PM";
        //Pad Minute if length is less than 2.
        String _minute = String.valueOf(minute);

        if (_minute.length() == 1)
            _minute = "0" + _minute;
        //Pad Second if length is less than 2.
        String _second = String.valueOf(second);
        if (_second.length() == 1)
            _second = "0" + _second;

        wCurrentTime = hour + ":" + _minute + ":" + _second + " " + am_pm;

        return wCurrentTime;
    }

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getMeasuredTimeFromDates(Date startDate, Date endDate) {
        String wRetVal = "";
        Interval interval = new Interval(startDate.getTime(), endDate.getTime());
        Period period = interval.toPeriod();

        if (period.getYears() > 0) {
            wRetVal += String.format("%d Years, ", period.getYears());
        }
        if (period.getMonths() > 0) {
            wRetVal += String.format("%d Months, ", period.getMonths());
        }
        if (period.getDays() > 0) {
            wRetVal += String.format("%d Days, ", period.getDays());
        }
        if (period.getHours() > 0) {
            wRetVal += String.format("%d Hours, ", period.getHours());
        }
        if (period.getMinutes() > 0) {
            wRetVal += String.format("%d Minutes, ", period.getMinutes());
        }

        wRetVal += String.format("%d Seconds ", period.getSeconds());


        return wRetVal;
    }


    public static long getNoOfMonths(@NonNull LocalDate fromDate, @NonNull LocalDate toDate) {
        return ChronoUnit.MONTHS.between(YearMonth.from(fromDate), YearMonth.from(toDate));
    }

    public static LocalDate setDateFromStringExcel(String pDateAsString) throws Exception {
        LocalDate cal = null;
        String[] retVal = new String[3];
        if (pDateAsString != null) {
            retVal = StringUtils.tokenizeToStringArray(pDateAsString, "-", true, true);
        }
        if (retVal != null && retVal.length == 3) {
            //We are good so it should be in the format 'dd' = 0; Month = 1; YYYY = 2
            if(String.valueOf(retVal[2]).length() < 4)
                retVal[2] = "20"+retVal[2];
            try {
                cal = LocalDate.of(Integer.parseInt(retVal[2]), getMonthIntFromString(retVal[1]), Integer.parseInt(retVal[0]));

            } catch (Exception ex) {
                //log this later -- always return todays date.
                ex.printStackTrace();
                throw new Exception();
            }
        }
        return cal;
    }

    public static Integer getMonthIntFromString(String wMonth) {
        Integer wRetVal = null;
        if (wMonth.equalsIgnoreCase("Jan") || wMonth.equalsIgnoreCase("January"))
            wRetVal = Month.JANUARY.getValue();
        else if (wMonth.equalsIgnoreCase("Feb") || wMonth.equalsIgnoreCase("February"))
            wRetVal = Month.FEBRUARY.getValue();
        else if (wMonth.equalsIgnoreCase("Mar") || wMonth.equalsIgnoreCase("March"))
            wRetVal = Month.MARCH.getValue();
        else if (wMonth.equalsIgnoreCase("Apr") || wMonth.equalsIgnoreCase("April"))
            wRetVal = Month.APRIL.getValue();
        else if (wMonth.equalsIgnoreCase("May") || wMonth.equalsIgnoreCase("May"))
            wRetVal = Month.MAY.getValue();
        else if (wMonth.equalsIgnoreCase("Jun") || wMonth.equalsIgnoreCase("June"))
            wRetVal = Month.JUNE.getValue();
        else if (wMonth.equalsIgnoreCase("Jul") || wMonth.equalsIgnoreCase("July"))
            wRetVal = Month.JULY.getValue();
        else if (wMonth.equalsIgnoreCase("Aug") || wMonth.equalsIgnoreCase("August"))
            wRetVal = Month.AUGUST.getValue();
        else if (wMonth.equalsIgnoreCase("Sep") || wMonth.equalsIgnoreCase("September"))
            wRetVal = Month.SEPTEMBER.getValue();
        else if (wMonth.equalsIgnoreCase("Oct") || wMonth.equalsIgnoreCase("October"))
            wRetVal = Month.OCTOBER.getValue();
        else if (wMonth.equalsIgnoreCase("Nov") || wMonth.equalsIgnoreCase("November"))
            wRetVal = Month.NOVEMBER.getValue();
        else if (wMonth.equalsIgnoreCase("Dec") || wMonth.equalsIgnoreCase("December"))
            wRetVal = Month.DECEMBER.getValue();
        return wRetVal;
    }

    public static Long getCurrentDateTimeAsLong() {
        LocalTime localTime = LocalTime.now();

        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        int second = localTime.getSecond();

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        int day = LocalDate.now().getDayOfMonth();

        return Long.valueOf(day +"" + month +"" + year +"" + hour +"" + minute +"" + second);
    }
    public static Long getCurrentDateTimeAsLong(Long pUserId) {
        LocalTime localTime = LocalTime.now();

        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        int second = localTime.getSecond();

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        int day = LocalDate.now().getDayOfMonth();

        return Long.valueOf(day +"" + month +"" + year +"" + hour +"" + minute +"" + second+""+getRandomNumberInRange()) + pUserId ;
    }

    private static Long getRandomNumberInRange() {

        Random r = new Random();
        return r.longs(1, (999 + 1)).findFirst().getAsLong();

    }
}