/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.SimulationService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.report.beans.RetMainBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class PayrollUtils {


    public static int expireContract(HiringInfo hiringInfo, GenericService genericService, Long pLastModBy) throws InstantiationException, IllegalAccessException {

        int wRetVal = 0;
        ContractHistory wCH = genericService.loadObjectUsingRestriction(ContractHistory.class, Arrays.asList(CustomPredicate.procurePredicate("employee.id", hiringInfo.getEmployee().getId()),
                CustomPredicate.procurePredicate("contractStartYear", hiringInfo.getContractStartDate().getYear()), CustomPredicate.procurePredicate("contractStartMonth", hiringInfo.getContractStartDate().getMonthValue()),
                CustomPredicate.procurePredicate("contractStartDay", hiringInfo.getContractStartDate().getDayOfMonth()), CustomPredicate.procurePredicate("contractEndYear", hiringInfo.getContractEndDate().getYear()),
                CustomPredicate.procurePredicate("contractEndMonth", hiringInfo.getContractEndDate().getMonthValue()), CustomPredicate.procurePredicate("contractEndDay", hiringInfo.getContractEndDate().getDayOfMonth())));

        if(wCH == null || wCH.isNewEntity()){
            //Mustola - This is to help with Data Migration issues with Contract Staffs not having Contracts or Regular Staffs being designated as Contract Staffs during Data Migration.
            hiringInfo.setContractExpiredInd(0);
            hiringInfo.setContractEndDate(null);
            hiringInfo.setContractStartDate(null);

        }else {
            hiringInfo.setContractExpiredInd(1);
            hiringInfo.setContractEndDate(LocalDate.now());
            wCH.setExpiredInd(1);
            wCH.setExpiredDate(LocalDate.now());
            wCH.setLastModBy(new User(pLastModBy));
            wCH.setLastModTs(Timestamp.from(Instant.now()));
            try{
                genericService.storeObject(wCH);

            }catch (Exception wEx){
                return 1;
            }
        }

        hiringInfo.setStaffInd(0);
        hiringInfo.setLastModBy(new User(pLastModBy));
        hiringInfo.setLastModTs(Timestamp.from(Instant.now()));
        genericService.saveObject(hiringInfo);
        return wRetVal;
    }

    public static String makePayPeriodForAuditLogs(GenericService genericService, BusinessCertificate businessCertificate) throws Exception {
        String wRetVal;


        int wRunMonth;
        int wRunYear;

        Long wInt = genericService.loadMaxValueByClassClientIdAndColumn(PayrollRunMasterBean.class, "id", businessCertificate.getBusinessClientInstId(), "businessClientId");

        PayrollRunMasterBean wPMB = genericService.loadObjectById(PayrollRunMasterBean.class, wInt);
        wRunMonth = wPMB.getRunMonth();
        wRunYear = wPMB.getRunYear();
        if (wPMB.isApproved()) {
            if (wRunMonth == 12) {
                wRunMonth = 1;
                wRunYear += 1;
            } else {
                wRunMonth += 1;
            }
        }
        if (wRunMonth < 10) {
            wRetVal = "0" + wRunMonth + wRunYear;
        } else {
            wRetVal = String.valueOf(wRunMonth) + wRunYear;
        }
        return wRetVal;
    }

    public static String makeAuditPayPeriod(int pRunMonth, int pRunYear) {
        if (pRunMonth < 10)
            return "0" + pRunMonth + pRunYear;
        else
            return String.valueOf(pRunMonth) + pRunYear;

    }

    /**
     * Use to manually paginate a list without using Database...
     */
    public static List<?> paginateList(int pPageNumber,
                                       int pRecordsPerPage, Collection<?> pAllList) {
        List<Object> wRetList = new ArrayList<Object>();
        int totalNoOfRecords = pAllList.size();

        int wEndRow = (pPageNumber * pRecordsPerPage) > totalNoOfRecords ? totalNoOfRecords : (pPageNumber * pRecordsPerPage);
        int wStartRow = ((pPageNumber - 1) * pRecordsPerPage) + 1;
        int wCount = 0;
        for (Object entity : pAllList) {
            ++wCount;
            if (wCount >= wStartRow
                    && wCount <= wEndRow && entity != null) {
                wRetList.add(entity);
            }
            if (wCount == wEndRow)
                break;
        }

        return wRetList;
    }

    public static NamedEntityBean makeCode(NamedEntityBean pNamedEntityBean, Integer pOtherId) {

        if (pNamedEntityBean.getObjectInd() == 1) {
            pNamedEntityBean.setName("GL" + pNamedEntityBean.getPageSize());
            pNamedEntityBean.setId(Long.valueOf(pNamedEntityBean.getPageSize()));
        } else if (pNamedEntityBean.getObjectInd() == 2) {
            pNamedEntityBean.setName("Political Appointees");
            pNamedEntityBean.setId(Long.valueOf(IConstants.POL_APP_ID));
        } else if (pNamedEntityBean.getObjectInd() == 3) {
            pNamedEntityBean.setName("HOS/PS/GMs/AGs");
            pNamedEntityBean.setId(Long.valueOf(IConstants.HOPS_ID));

        } else {
            pOtherId++;
            pNamedEntityBean.setId(Long.valueOf(pOtherId));
            pNamedEntityBean.setCurrentOtherId(pOtherId);
        }


        return pNamedEntityBean;
    }

    public static AbstractPaycheckEntity makeCode(AbstractPaycheckEntity pEmployeePayBean, Integer pOtherId) {

        if (pEmployeePayBean.getObjectInd() == 1) {
            pEmployeePayBean.setEmployeeName("GL" + pEmployeePayBean.getSalaryInfo().getLevel());
            pEmployeePayBean.setId(Long.valueOf(pEmployeePayBean.getSalaryInfo().getLevel()));
        } else if (pEmployeePayBean.getObjectInd() == 2) {
            pEmployeePayBean.setEmployeeName("Political Appointees");
            pEmployeePayBean.setId(Long.valueOf(IConstants.POL_APP_ID));
        } else if (pEmployeePayBean.getObjectInd() == 3) {
            pEmployeePayBean.setEmployeeName("HOS/PS/GMs/AGs");
            pEmployeePayBean.setId(Long.valueOf(IConstants.HOPS_ID));

        } else {
            pOtherId++;
            pEmployeePayBean.setId(Long.valueOf(pOtherId));
            pEmployeePayBean.setCurrentObjectId(pOtherId);
        }


        return pEmployeePayBean;
    }

    public static String makeCode(int pObjectInd, int pLevel, String pName) {
        String wRetVal = "";
        if (pObjectInd == 1) {
            wRetVal = "GL" + pLevel;

        } else if (pObjectInd == 2) {
            wRetVal = "Political Appointees";

        } else if (pObjectInd == 3) {
            wRetVal = "HOS/PS/GMs/AGs";


        } else {
            wRetVal = pName;
        }


        return wRetVal;
    }

    public static String getElapseTime(LocalDate pStartDate, String pStartTime,
                                       LocalDate pEndDate, String pEndTime) {
        String wStartTime = treatTimeStamp(pStartTime);
        String wEndTime = treatTimeStamp(pEndTime);


        LocalDateTime oldDate = LocalDateTime.of(pStartDate.getYear(), pStartDate.getMonthValue(), pStartDate.getDayOfMonth(),
                getTimeValueByCode(wStartTime,1), getTimeValueByCode(wStartTime,2), getTimeValueByCode(wStartTime,3));

        LocalDateTime newDate = LocalDateTime.of(pEndDate.getYear(), pEndDate.getMonthValue(), pEndDate.getDayOfMonth(),
                getTimeValueByCode(wEndTime,1), getTimeValueByCode(wEndTime,2), getTimeValueByCode(wEndTime,3));

        long hours = ChronoUnit.HOURS.between(oldDate, newDate);
        long minutes = ChronoUnit.MINUTES.between(oldDate, newDate);
        long seconds = ChronoUnit.SECONDS.between(oldDate, newDate);

        //Now Convert the seconds to minutes....
        int actualSeconds = 0;
        if(seconds > 0)
            actualSeconds = (int) (seconds % 60);
        String wRetVal = "";
        boolean useAnd = false;
        if (hours > 0) {
            wRetVal += hours + " Hours ";
        }
        if (minutes > 0) {
            wRetVal += minutes + " Minutes ";
            useAnd = true;
        }
        if (useAnd)
            wRetVal += " and ";
        wRetVal += actualSeconds + " Seconds ";


        return wRetVal;

    }
    private final static int getTimeValueByCode(String pTimeString, int pCode){
        String[] split = pTimeString.split(":");
        int wRetVal = 0;
        switch (pCode){
            case 1:
                wRetVal =  Integer.parseInt(split[0]);
                break;
            case 2:
                wRetVal =  Integer.parseInt(split[1]);
                break;
            case 3:
                if(split[2] != null){
                    wRetVal =  Integer.parseInt(split[2]);
                }else{
                    wRetVal = 0;
                }
                break;
        }
        return wRetVal;
    }
    private static String treatTimeStamp(String pString ) {

        StringTokenizer wStr = new StringTokenizer(pString);
        String strToConvert = "";
        String AM_PM = "";
        int count = 0;
        while (wStr.hasMoreElements()) {
            String nextToken = wStr.nextToken();
            if (count == 0) {
                strToConvert = nextToken;

            }
            if ((count == 1)) {
                AM_PM = nextToken;

            }

            count++;
        }
        String wHr = "";
        String wMin = "";
        String wSec = "";

        count = 0;
        int wHrInt = 0;
        wStr = new StringTokenizer(strToConvert, ":");
        while (wStr.hasMoreElements()) {
            String nextToken = wStr.nextToken();
            if (count == 0)
                wHr = nextToken;


            if (count == 1)
                wMin = nextToken;

            if (count == 2)
                wSec = nextToken;
            count++;
        }
        if(wSec.equals(IConstants.EMPTY_STR))
            wSec = "00";

        if (AM_PM.trim().equalsIgnoreCase("PM")) {
             wHrInt = Integer.parseInt(wHr) + 12;
            strToConvert = wHrInt + ":" + wMin + ":" + wSec;
        }else{
            if(wHr.length() < 2){
                wHr = "0"+wHr;
            }
            strToConvert = wHr + ":" + wMin + ":" + wSec;
        }


        return strToConvert;
    }

    public static String formatStep(Integer pInteger) {
        if (pInteger < 10)
            return "0" + pInteger;
        return String.valueOf(pInteger);

    }

    public static synchronized RetMainBean makeAverageValues(SimulationService simulationService, BusinessCertificate businessCertificate,
                                                             PayrollSimulationMasterBean pPayrollSimulationMasterBean, PayrollFlag pPayrollFlag) {

       //RetMainBean wRetBean = new RetMainBean();

        int wRunMonth = pPayrollFlag.getApprovedMonthInd();
        int wRunYear = pPayrollFlag.getApprovedYearInd();

        int noOfMonths = pPayrollSimulationMasterBean.getSimulationPeriodInd();

        int wStartMonth = 0;
        int wStartYear = wRunYear;

        int diff = wRunMonth - noOfMonths;

        if (diff < 1) {
            diff += 1;
            wStartMonth = 12 - diff;
            wStartYear -= 1;
        } else {
            wStartMonth = diff;
        }

        return simulationService.populateAverages(businessCertificate, wStartMonth, wStartYear, wRunMonth, wRunYear, noOfMonths);


    }

    /**
     * This method is only applicable for Employees.
     *
     * @param pHiringInfo
     * @param pRerun
     * @param pPartPaymentMap
     * @param pPayPeriodEnd
     * @param businessCertificate
     * @param configurationBean
     * @return
     */
    public static synchronized AbstractPaycheckEntity determinePayStatus(HiringInfo pHiringInfo, boolean pRerun, Map<Long, SuspensionLog> pPartPaymentMap, LocalDate pPayPeriodEnd,
                                                                         BusinessCertificate businessCertificate, ConfigurationBean configurationBean,Map<Long, TerminateReason> terminateReasonMap) {


        AbstractPaycheckEntity wEmpPayBean = IppmsUtils.makePaycheckObject(businessCertificate);
        wEmpPayBean.setBusinessClientId(businessCertificate.getBusinessClientInstId());
        wEmpPayBean.setEmployeeType(pHiringInfo.getEmployeeType());
        wEmpPayBean.setHiringInfo(pHiringInfo);

        wEmpPayBean.setParentObject(pHiringInfo.getAbstractEmployeeEntity());
        wEmpPayBean.setMdaDeptMap(pHiringInfo.getAbstractEmployeeEntity().getMdaDeptMap());
        wEmpPayBean.setFirstName(pHiringInfo.getAbstractEmployeeEntity().getFirstName());
        wEmpPayBean.setLastName(pHiringInfo.getAbstractEmployeeEntity().getLastName());
        wEmpPayBean.setInitials(pHiringInfo.getAbstractEmployeeEntity().getInitials());
        wEmpPayBean.setOgNumber(pHiringInfo.getAbstractEmployeeEntity().getEmployeeId());
        wEmpPayBean.setObjectInd(pHiringInfo.getAbstractEmployeeEntity().getObjectInd());
        wEmpPayBean.setPensionPinCode(pHiringInfo.getPensionPinCode());
        wEmpPayBean.setPfaInfo(pHiringInfo.getPfaInfo());

        if (pHiringInfo.getAccountNumber() == null) {
            wEmpPayBean.setDoNotPay(true);
            wEmpPayBean.setAccountNumber("NS");
        } else {
            wEmpPayBean.setAccountNumber(pHiringInfo.getAccountNumber());
        }
        if (pHiringInfo.getBvnNo() == null) {
            if(configurationBean.isBvnRequired())
            wEmpPayBean.setDoNotPay(true);
            wEmpPayBean.setBvnNo("NS");
        } else {
            wEmpPayBean.setBvnNo(pHiringInfo.getBvnNo());
        }

        wEmpPayBean.setBankBranch(new BankBranch(pHiringInfo.getBranchInstId()));

        if (pRerun) {
            wEmpPayBean.setReRunInd(IConstants.ON);
        }
        if (!pHiringInfo.getAbstractEmployeeEntity().isApprovedForPayrolling()) {
            wEmpPayBean.setDoNotPay(true);
            wEmpPayBean.setRejectedForPayrollingInd(IConstants.ON);
        } else if (wEmpPayBean.getHiringInfo().isSuspendedEmployee()) {
            wEmpPayBean.setSuspendedInd(IConstants.ON);
            if (pPartPaymentMap.containsKey(pHiringInfo.getAbstractEmployeeEntity().getId())) {
                if (pHiringInfo.getTerminateDate() != null) {
                    wEmpPayBean.setDoNotPay(true);
                } else {
                    SuspensionLog s = pPartPaymentMap.get(pHiringInfo.getAbstractEmployeeEntity().getId());
                    wEmpPayBean.setPayPercentage(EntityUtils.convertDoubleToEpmStandard(s.getPayPercentage() / 100.0D));
                    wEmpPayBean.setPayPercentageInd(1);
                    wEmpPayBean.setSalaryInfo(pHiringInfo.getAbstractEmployeeEntity().getSalaryInfo());
                }

            } else {
                wEmpPayBean.setDoNotPay(true);
                wEmpPayBean.setSalaryInfo(pHiringInfo.getAbstractEmployeeEntity().getSalaryInfo());
                return wEmpPayBean;
            }

        } else if (pHiringInfo.isPoliticalOfficeHolderType() && !pHiringInfo.isContractStaff()) {
            if (pHiringInfo.getTerminateDate() == null)
                wEmpPayBean.setDoNotPay(false);
            else {
                //Test if the Termination was done during the current Pay Period...
                LocalDate wCal = pHiringInfo.getTerminateDate();

                if (wCal.getMonthValue() == pPayPeriodEnd.getMonthValue()
                        && wCal.getYear() == pPayPeriodEnd.getYear()) {
                    wEmpPayBean.setDoNotPay(false);
                    wEmpPayBean.setPayByDaysInd(1);
                    wEmpPayBean.setNoOfDays(wCal.getDayOfMonth() - 1);
                 } else {
                    wEmpPayBean.setDoNotPay(true);
                    wEmpPayBean.setTerminatedInd(IConstants.ON);
                }

            }

        } else if (pHiringInfo.isContractStaff()) {

            wEmpPayBean.setContractStaff(true);
            wEmpPayBean.getHiringInfo().setPensionableInd(IConstants.ON);
            wEmpPayBean.setContractIndicator(IConstants.ON);
            LocalDate contractEndDate = pHiringInfo.getContractEndDate();
            if ((contractEndDate == null) || (pHiringInfo.getContractExpiredInd() == 1)) {
                wEmpPayBean.setDoNotPay(true);
            } else {

                if ((contractEndDate.getMonthValue() == pPayPeriodEnd.getMonthValue()) && (contractEndDate.getYear() == pPayPeriodEnd.getYear())) {
                    if (contractEndDate.getDayOfMonth() != pPayPeriodEnd.getDayOfMonth()) {
                        wEmpPayBean.setPayByDaysInd(1);
                        wEmpPayBean.setNoOfDays(contractEndDate.getDayOfMonth() - 1);
                    }
                    wEmpPayBean.setDoNotPay(false);
                } else if (contractEndDate.compareTo(pPayPeriodEnd) < 0)
                    wEmpPayBean.setDoNotPay(true);
                else {
                    wEmpPayBean.setDoNotPay(false);
                }
            }
            if(!wEmpPayBean.isDoNotPay() && null != pHiringInfo.getTermId()){
                //Now check the new requirement that if the termination is by death or dismissed....DO NOT PAY.
                /**
                 * This only applies to Staffs that are terminated by
                 * 1. Death
                 * 2. Dismissal
                 */
                if(terminateReasonMap.containsKey(pHiringInfo.getTermId())) {
                    wEmpPayBean.setDoNotPay(true);
                    wEmpPayBean.setTerminatedInd(IConstants.ON);
                }

            }
        } else {
            boolean terminatedByBirthDate = false;
            LocalDate pSixtyYearsAgo = pPayPeriodEnd.minusYears(configurationBean.getAgeAtRetirement());
            LocalDate pThirtyFiveYearsAgo = pPayPeriodEnd.minusYears(configurationBean.getServiceLength());
            if (pHiringInfo.getBirthDate().isBefore(pSixtyYearsAgo)) {
                boolean doNotPay = true;

                if (pHiringInfo.getBirthDate().getYear() == pSixtyYearsAgo.getYear()) {
                    if (pHiringInfo.getBirthDate().getMonthValue() == pSixtyYearsAgo.getMonthValue()) {
                        wEmpPayBean.setPayByDaysInd(1);
                        wEmpPayBean.setNoOfDays(pHiringInfo.getBirthDate().getDayOfMonth() - 1);
                        doNotPay = false;
                    }
                }
                wEmpPayBean.setDoNotPay(doNotPay);
                terminatedByBirthDate = doNotPay;
                if (doNotPay) {
                    wEmpPayBean.setBirthDateTerminatedInd(IConstants.ON);
                }
            }
            if (pHiringInfo.getHireDate().isBefore(pThirtyFiveYearsAgo)) {
                boolean doNotPay = true;
                if (pHiringInfo.getHireDate().getYear() == pThirtyFiveYearsAgo.getYear()) {
                    if (pHiringInfo.getHireDate().getMonthValue() == pThirtyFiveYearsAgo.getMonthValue()) {
                        wEmpPayBean.setPayByDaysInd(1);
                        wEmpPayBean.setNoOfDays(pHiringInfo.getHireDate().getDayOfMonth() - 1);
                         doNotPay = false;
                    }
                }
                if (terminatedByBirthDate) {
                    wEmpPayBean.setDoNotPay(true);
                } else {
                    wEmpPayBean.setDoNotPay(doNotPay);
                }
                if (doNotPay && !terminatedByBirthDate) {
                    wEmpPayBean.setHireDateTerminatedInd(IConstants.ON);
                }
            }
            if (pHiringInfo.getTerminateDate() != null)
            {

                if(null != pHiringInfo.getTermId()){
                    //Now check the new requirement that if the termination is by death or dismissed....DO NOT PAY.
                    /**
                     * This only applies to Staffs that are terminated by
                     * 1. Death
                     * 2. Dismissal
                     */
                    if(terminateReasonMap.containsKey(pHiringInfo.getTermId())) {
                        wEmpPayBean.setDoNotPay(true);
                        wEmpPayBean.setTerminatedInd(IConstants.ON);
                    }else{
                        if (pHiringInfo.getTerminateDate().getMonthValue() == pPayPeriodEnd.getMonthValue()
                                && pHiringInfo.getTerminateDate().getYear() == pPayPeriodEnd.getYear()) {
                            wEmpPayBean.setDoNotPay(false);
                            wEmpPayBean.setPayByDaysInd(1);
                            wEmpPayBean.setNoOfDays(pHiringInfo.getTerminateDate().getDayOfMonth() - 1);
                        } else {
                            wEmpPayBean.setDoNotPay(true);
                            wEmpPayBean.setTerminatedInd(IConstants.ON);
                        }
                    }

                }else {
                    if (pHiringInfo.getTerminateDate().getMonthValue() == pPayPeriodEnd.getMonthValue()
                            && pHiringInfo.getTerminateDate().getYear() == pPayPeriodEnd.getYear()) {
                        wEmpPayBean.setDoNotPay(false);
                        wEmpPayBean.setPayByDaysInd(1);
                        wEmpPayBean.setNoOfDays(pHiringInfo.getTerminateDate().getDayOfMonth() - 1);
                    } else {
                        wEmpPayBean.setDoNotPay(true);
                        wEmpPayBean.setTerminatedInd(IConstants.ON);
                    }
                }
            }

        }

            if(configurationBean.isBioRequired()){
                if(pHiringInfo.getEmployee().getBiometricId() == null){
                    wEmpPayBean.setDoNotPay(true);
                    wEmpPayBean.setBiometricInd(1);
                }
            }


        if (!wEmpPayBean.isDoNotPay()) {
            wEmpPayBean.setPayEmployeeRef(true);
            wEmpPayBean.setPaymentType("Salaried");
            wEmpPayBean.setName(wEmpPayBean.getHiringInfo().getAbstractEmployeeEntity().getDisplayName());
            if (pHiringInfo.getLastPayDate() != null)
                wEmpPayBean.setLastPayDate(DateTimeFormatter.ofPattern("MMM dd, yyyy").format(pHiringInfo.getLastPayDate()));
            else {
                wEmpPayBean.setLastPayDate("");
            }


            if ((pHiringInfo.getCurrentPayPeriod() == null) || (pHiringInfo.getCurrentPayPeriod().equalsIgnoreCase(""))) {
                pHiringInfo.setFirstTimePay(true);
            } else {
                pHiringInfo.setNormalPay(true);
            }

        }

        if (wEmpPayBean.getPayByDaysInd() == 1 && wEmpPayBean.getNoOfDays() == 0) {
            wEmpPayBean.setTerminatedDate(pHiringInfo.getTerminatedDateStr());
            wEmpPayBean.setDoNotPay(true);
            wEmpPayBean.setTerminatedInd(IConstants.ON);
        }
        if (wEmpPayBean.isDoNotPay()) {
            wEmpPayBean.setYtdIgnoreInd(IConstants.ON);
            if(wEmpPayBean.getPayByDaysInd() == 1){
                wEmpPayBean.setPayByDaysInd(0);
                wEmpPayBean.setNoOfDays(0);
            }


        }
        return wEmpPayBean;
    }

    public static synchronized double calculateTaxes(double pGrossIncome, double pFreePay) {
        double taxesDue = 0.0D;
        BigDecimal w300k = new BigDecimal("300000.00").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w21k = new BigDecimal("21000.00").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w33k = new BigDecimal("33000.00").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w11Percent = new BigDecimal("0.11").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w15Percent = new BigDecimal("0.15").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w19Percent = new BigDecimal("0.19").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w21Percent = new BigDecimal("0.21").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w24Percent = new BigDecimal("0.24").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w75k = new BigDecimal("75000.00").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w500k = new BigDecimal("500000.00").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w95k = new BigDecimal("95000.00").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w336k = new BigDecimal("336000.00").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w1Point6M = new BigDecimal("1600000.00").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal w7Percent = new BigDecimal("0.07").setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal reminant = new BigDecimal(pGrossIncome - pFreePay * 12.0).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal divisorDB = new BigDecimal(12.0).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal workingTaxAmount = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_EVEN);


        if (reminant.doubleValue() <= 0.0D) {
            workingTaxAmount = new BigDecimal(pGrossIncome * 0.01).setScale(2, RoundingMode.HALF_EVEN);
            taxesDue = workingTaxAmount.divide(divisorDB, 2, RoundingMode.HALF_EVEN).doubleValue();
            return taxesDue;
        }

        if (reminant.doubleValue() <= 300000.0D) {

            workingTaxAmount = reminant.multiply(w7Percent);
            taxesDue = workingTaxAmount.divide(divisorDB, 2, RoundingMode.HALF_EVEN).doubleValue();
            return taxesDue;
        }

        if (reminant.doubleValue() >= 300000.0D) {
            workingTaxAmount = w21k;
            reminant = reminant.subtract(w300k);
        }

        if (reminant.doubleValue() >= 300000.0D) {
            workingTaxAmount = workingTaxAmount.add(w33k);
            reminant = reminant.subtract(w300k);
        } else {
            workingTaxAmount = workingTaxAmount.add(reminant.multiply(w11Percent));
            //reminant = new BigDecimal("0.00");
            taxesDue = workingTaxAmount.divide(divisorDB, 2, RoundingMode.HALF_EVEN).doubleValue();
            return taxesDue;
        }

        if (reminant.doubleValue() >= 500000.0D) {
            workingTaxAmount = workingTaxAmount.add(w75k);
            reminant = reminant.subtract(w500k);
        } else {
            workingTaxAmount = workingTaxAmount.add(reminant.multiply(w15Percent));
            //reminant = new BigDecimal("0.00");
            taxesDue = workingTaxAmount.divide(divisorDB, 2, RoundingMode.HALF_EVEN).doubleValue();
            return taxesDue;
        }
        if (reminant.doubleValue() >= 500000.0D) {
            workingTaxAmount = workingTaxAmount.add(w95k);
            reminant = reminant.subtract(w500k);
        } else {
            workingTaxAmount = workingTaxAmount.add(reminant.multiply(w19Percent));
            taxesDue = workingTaxAmount.divide(divisorDB, 2, RoundingMode.HALF_EVEN).doubleValue();
            return taxesDue;
        }
        if (reminant.doubleValue() >= 1600000.0D) {
            workingTaxAmount = workingTaxAmount.add(w336k);
            reminant = reminant.subtract(w1Point6M);
        } else {
            workingTaxAmount = workingTaxAmount.add(reminant.multiply(w21Percent));
            taxesDue = workingTaxAmount.divide(divisorDB, 2, RoundingMode.HALF_EVEN).doubleValue();
            return taxesDue;
        }
        if (reminant.doubleValue() > 0.0D) {
            workingTaxAmount = workingTaxAmount.add(reminant.multiply(w24Percent));

        }

        taxesDue = workingTaxAmount.divide(divisorDB, 2, RoundingMode.HALF_EVEN).doubleValue();
        return taxesDue;
    }

    public static BusinessClient checkStringValidity(BusinessClient pBc, int minLength, boolean checkForSpecChar, int id) {
        //--Remove Code Later.
        if (pBc.getUser() != null && pBc.getUser().isEditMode())
            return pBc;
        switch (id) {
            case 1:
                //TODO User Name. Will be auto-configurable Later
                pBc.setLengthError(pBc.getUserName().trim().length() < minLength);
                if (checkForSpecChar) {
                    pBc.setSpecCharError(pBc.getUserName().indexOf("_") == -1 && pBc.getUserName().indexOf("@") == -1 && pBc.getUserName().indexOf("-") == -1);
                    pBc.setSpecCharError(!containsOneNumeric(pBc.getUserName()));
                }
                break;
            case 2:
                //Last Name
                pBc.setLengthError(pBc.getLastName().trim().length() < minLength);
                break;
            case 3:
                //First Name
                pBc.setLengthError(pBc.getFirstName().trim().length() < minLength);
                break;


        }

        return pBc;
    }

    public static boolean containsOneNumeric(String userName) {
        char[] _charr = userName.toCharArray();
        boolean found = false;
        for (Character c : _charr) {
            try {
                int i = Integer.parseInt(String.valueOf(c));
                found = true;
                break;
            } catch (Exception wEx) {
                //eat it.
            }
        }
        return found;
    }

    public static String formatTimeStamp(Timestamp lastModTs) {
        if(lastModTs == null)
            return "";
        String originalTimeStamp = String.valueOf(lastModTs);
        StringTokenizer tokenizer = new StringTokenizer(originalTimeStamp," ");
        String datePart = tokenizer.nextToken();
        String timePart = tokenizer.nextToken();
        tokenizer = new StringTokenizer(timePart,":");
        String hour = tokenizer.nextToken();
        String min = tokenizer.nextToken();
        String sec = tokenizer.nextToken();
        sec = sec.substring(0,sec.indexOf("."));
        if(Integer.parseInt(hour) >= 12)
           return datePart+" "+hour+":"+min+":"+sec+" PM";
        else
            return datePart+" "+hour+":"+min+":"+sec+" AM";
    }

    public static LocalDate makeEndDate(LocalDate startDate, int loanTerm) {
        int noOfYears = loanTerm/12;
        int noOfMonths = loanTerm%12;
        LocalDate retVal = LocalDate.of(startDate.getYear() + noOfYears, startDate.getMonthValue(),startDate.getDayOfMonth());
        retVal = retVal.plusMonths(new Long(noOfMonths).longValue());
        return retVal;
    }

    public static String makeLevelAndStep(Integer level, Integer step) {

        if(step < 10)
            return level+"/0"+step;
        else
            return  level+"/"+step;
    }

    public static boolean isWeekend(@Nullable LocalDate now) {
        if(now == null)
            now = LocalDate.now();
        return (now.getDayOfWeek().equals(DayOfWeek.SATURDAY) || now.getDayOfWeek().equals(DayOfWeek.SUNDAY));
    }
}
