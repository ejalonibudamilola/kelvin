package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckMaster;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class CalculateFuturePayroll
        implements Runnable {

    private final PfaInfo defPfaInfo;
    private final CalculateFuturePayPerEmployee fCalcPayPerEmployee;
    private int fCurrentPercentage;
    private Long parentId;
    int wListSize;
    boolean wBatch;
    boolean fStop;
    int wBatchSize;
    private List<FuturePaycheckBean> wSaveList;
    private final List<MdaInfo> mdaTypeList;
    private final int runMonth;
    private final int runYear;
    private final BusinessCertificate bc;
    private final LocalDate fPayPeriodStart;
    private final LocalDate fPayPeriodEnd;
    private long startTime;
    private long timePerBatch;
    private int fRemainingRecords;
    private final PayrollService payrollService;
    private final int fListSize;
    private String payPeriodStr;

     public CalculateFuturePayroll(PayrollService pPayrollService, BusinessCertificate pBusClientId, int pListToPay, Long pParentId,
                                   CalculateFuturePayPerEmployee pCalcPayPerEmployee, List<MdaInfo> list, int pRunMonth, int pRunYear, PfaInfo pfaInfo) {
        this.bc = pBusClientId;

        this.parentId = pParentId;
        this.fCalcPayPerEmployee = pCalcPayPerEmployee;

        this.wBatchSize = 500;
        this.fStop = false;
        this.wSaveList = new ArrayList();
        this.fListSize = pListToPay;
        this.mdaTypeList = list;
        this.runMonth = pRunMonth;
        this.runYear = pRunYear;
        this.fPayPeriodStart = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear, false) ;
        this.fPayPeriodEnd = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear, true);
        this.payrollService = pPayrollService;
        this.defPfaInfo = pfaInfo;
    }

     public void run() {

        try {
            this.fCalcPayPerEmployee.setNoOfDays(PayrollBeanUtils.getNoOfDays(this.runMonth, this.runYear));
             startTime = System.currentTimeMillis();
            boolean firstBatch = false;
            for (MdaInfo m : this.mdaTypeList) {


                if (getState())
                    break;
                List<HiringInfo> wList = this.payrollService.loadActiveHiringInfoByMDAPInstId(bc,m, fPayPeriodStart, fPayPeriodEnd, defPfaInfo);
                if (wList.isEmpty())
                    continue;
                wListSize = wList.size();
                for (int i = 0; (i < wList.size()) &&
                        (!getState()); i++) {
                    FuturePaycheckBean wEPB = this.fCalcPayPerEmployee
                            .calculatePayroll(wList.get(i));
                    wEPB.setFuturePaycheckMaster(new FuturePaycheckMaster(this.parentId));
                    this.wSaveList.add(wEPB);

                    if ((this.wSaveList.size() >= this.wBatchSize) || (i == this.wListSize - 1)) {
                        saveCalculatedPayroll(this.wSaveList);
                        this.wSaveList = new ArrayList();
                        if (!firstBatch)
                            this.timePerBatch = System.currentTimeMillis() - this.startTime;
                        this.startTime = System.currentTimeMillis();
                        firstBatch = true;

                    }


                    this.fCurrentPercentage += 1;
                }
            }
            this.fCurrentPercentage = this.fListSize;
        } catch (Exception e) {
            this.fCurrentPercentage = -1;
            log.error("Critical Exception thrown from CalculateFuturePayroll " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the remaining time for payroll to run fully.
     *
     * @return Time in years, months, days, hours, minutes, seconds
     */
    public String getTimeToElapse() {
        String wRetVal = "";
        if (fCurrentPercentage == -1)
            return wRetVal;
        if (fCurrentPercentage == 0)
            return wRetVal;

        Date currDate = new Date();
        long timeRemainingMillis = (this.getfRemainingRecords() / this.wBatchSize) * this.getTimePerBatch();
        //System.out.println("Time Per Batch: " + this.getTimePerBatch() + " Remaining Records: " + this.getfRemainingRecords() + " Time Remaining Millis: " + timeRemainingMillis);
        Date endDate = new Date(currDate.getTime() + timeRemainingMillis);

        wRetVal = PayrollBeanUtils.getMeasuredTimeFromDates(currDate, endDate);


        return wRetVal;


    }

    private boolean getState() {
        return this.fStop;
    }

    public int getPercentage() {
        if (fCurrentPercentage == -1)
            return 100;
        if (fCurrentPercentage == 0)
            return fCurrentPercentage;
        double wPercent = (((double) fCurrentPercentage / fListSize)) * 100;
        int wRetVal = EntityUtils.convertDoubleToEpmStandardZeroDecimal(wPercent);
        return wRetVal;
    }

    public int getfRemainingRecords() {
        fRemainingRecords = this.getTotalRecords() - this.getCurrentRecord();
        return fRemainingRecords;
    }

    public void stop(boolean pStop) {
        this.fStop = pStop;
    }

    public int getCurrentRecord() {
        return this.fCurrentPercentage;
    }

    public int getTotalRecords() {
        return this.wListSize;
    }

    public boolean isFinished() {
        return (this.fCurrentPercentage == this.wListSize) || (this.fCurrentPercentage == -1);
    }


    private synchronized void saveCalculatedPayroll(List<FuturePaycheckBean> pEmpPayBeanList) {
        if (!pEmpPayBeanList.isEmpty()) {
           payrollService.saveCollection(pEmpPayBeanList);
        }
    }

    public String getPayPeriodStr() {
        if(fPayPeriodStart != null){
            payPeriodStr = " Period : "+fPayPeriodStart.getMonth().name()+" : "+fPayPeriodStart.getYear();
        }

        return payPeriodStr;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long pParentId) {
        this.parentId = pParentId;
    }

    public long getTimePerBatch() {
        return timePerBatch;
    }

    public void setTimePerBatch(long timePerBatch) {
        this.timePerBatch = timePerBatch;
    }

    public void setfRemainingRecords(int fRemainingRecords) {
        this.fRemainingRecords = fRemainingRecords;
    }


}