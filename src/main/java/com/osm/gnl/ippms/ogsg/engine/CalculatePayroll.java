package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.base.services.RerunPayrollService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.PayrollConfigImpact;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsHelper;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Slf4j
public class CalculatePayroll
        implements Runnable {
    private final PayrollService payrollService;
    private final GenericService genericService;
    private final RerunPayrollService rerunPayrollService;
    private final CalculatePayPerEmployee fCalcPayPerEmployee;
    private final PayrollRunMasterBean payrollRunMasterBean;
    private LocalDate payDate;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private String payPeriod;
    private String currentUser;
    private PfaInfo defaultPfaInfo;
    private boolean objectSaved;
    private int listSize;
    private int currentPercentage;
    private final BusinessCertificate businessCertificate;
    private int runMonth;
    private int runYear;
    private boolean stop;
    private int batchSize;
    private int firstTime = -1;
    private List<Object> wSaveList;
    private boolean rerun;
    private boolean deleted;
    private final ConfigurationBean configurationBean;
    private final PayrollConfigImpact payrollConfigImpact;


    private RerunPayrollBean rerunPayrollBean;

    private long startTime;
    //private long endTime;
    private long timePerBatch;
    private int fRemainingRecords;
    private List<MdaInfo> mdaInfoList;
    private AbstractPaycheckEntity saveList;
    private List<TerminateReason> terminateReasonList;



    public CalculatePayroll(GenericService genericService, PayrollService payrollService, BusinessCertificate businessCertificate, LocalDate pPayDate, LocalDate pPayPeriodStart, LocalDate pPayPeriodEnd, String pPayPeriod,
                            String pCurrentUser, CalculatePayPerEmployee pCalcPayPerEmployee, int pFullListSize, PayrollRunMasterBean pPayrollRunMasterBean,
                            boolean pRerun, RerunPayrollBean pRerunPayrollBean, PfaInfo pfaInfo, List<MdaInfo> mdaList, RerunPayrollService rerunPayrollService1, boolean pDeleted,
                            ConfigurationBean pConfigurationBean, PayrollConfigImpact payrollConfigImpact) {
        this.genericService = genericService;

        this.currentUser = pCurrentUser;
        this.payDate = pPayDate;
        this.payPeriod = pPayPeriod;
        this.payPeriodEnd = pPayPeriodEnd;
        this.payPeriodStart = pPayPeriodStart;
        this.fCalcPayPerEmployee = pCalcPayPerEmployee;
        this.businessCertificate = businessCertificate;
        this.listSize = pFullListSize;
        this.batchSize = 500;
        this.stop = false;
        this.wSaveList = new ArrayList<>();
        this.defaultPfaInfo = pfaInfo;

        this.runMonth = pPayPeriodEnd.getMonthValue();
        this.runYear = pPayPeriodEnd.getYear();
        this.payrollRunMasterBean = pPayrollRunMasterBean;
        this.mdaInfoList = mdaList;
        this.rerunPayrollBean = pRerunPayrollBean;
        this.rerun = pRerun;
        this.payrollService = payrollService;
        this.rerunPayrollService = rerunPayrollService1;
        this.deleted = pDeleted;
        this.configurationBean = pConfigurationBean;
        this.payrollConfigImpact = payrollConfigImpact;


    }

    @SneakyThrows
    public void run() {
        try {
            this.fCalcPayPerEmployee.setNoOfDays(PayrollBeanUtils.getNoOfDays(this.runMonth, this.runYear));
            this.fCalcPayPerEmployee.setConfigurationBean(this.configurationBean);
             fCalcPayPerEmployee.setRerun(this.rerun);

             //Before we do anything. Reset the Sequences.

            if (this.rerun) {

                startTime = System.currentTimeMillis();
                boolean firstBatch = false;

                for (MdaInfo m : this.mdaInfoList) {
                    if (getState())
                        break;
                    List<HiringInfo> wList = findAllRerunEmployees(this.businessCertificate, m, this.payPeriodStart, this.payPeriodEnd);
                    HashMap<Long, Long> wEmpPayCheckMap = this.setEmployeeToPaycheckMap(m);
                    HashMap<Long, HashMap<Long, Long>> wEmpSpecAllowMap = this.setEmpToSpecAllowMap(m);
                    HashMap<Long, HashMap<Long, Long>> wEmpDeductionMap = this.setEmpToDeductionMap(m);
                    HashMap<Long, HashMap<Long, Long>> wEmpLoanMap = this.setEmpToLoanMap(m);
                    HashMap<Long, Long> wEmpPayCheckGratMap = this.setEmployeeToPaycheckMap(m);
                    int wListSize = wList.size();

                    for (int i = 0; (i < wList.size()) &&
                            (!getState()); i++) {
                        AbstractPaycheckEntity wEPB = this.fCalcPayPerEmployee.calculatePayroll(wList.get(i));

                        wEPB.setMasterBean(payrollRunMasterBean);
                        if (wEmpPayCheckMap.containsKey(wEPB.getHiringInfo().getAbstractEmployeeEntity().getId())) {
                            wEPB.setId(wEmpPayCheckMap.get(wEPB.getHiringInfo().getAbstractEmployeeEntity().getId()));
                        }
                        if (this.firstTime == -1) {
                            if (wEPB.getHiringInfo().isFirstTimePay())
                                this.firstTime = 0;
                            else {
                                this.firstTime = 1;
                            }

                        }

                        this.wSaveList.add(wEPB);
                        if ((this.wSaveList.size() >= this.batchSize) || (i == wListSize - 1)) {
                            saveCalculatedPayroll(wSaveList, wEmpSpecAllowMap, wEmpDeductionMap, wEmpLoanMap, wEmpPayCheckGratMap);
                            this.wSaveList = new ArrayList<>();

                            if (!firstBatch)
                                this.timePerBatch = System.currentTimeMillis() - this.startTime;
                            firstBatch = true;

                        }

                        this.currentPercentage += 1;
                    }
                }


            } else {

                startTime = System.currentTimeMillis();
                boolean firstBatch = false;

                for (MdaInfo m : this.mdaInfoList) {
                    if (getState())
                        break;
                    List<HiringInfo> wList = findAllEmployees(m, this.payPeriodStart, this.payPeriodEnd, defaultPfaInfo);

                    if (wList.isEmpty())
                        continue;
                    int wListSize = wList.size();

                    for (int i = 0; (i < wList.size()) &&
                            (!getState()); i++) {
                        AbstractPaycheckEntity wEPB = this.fCalcPayPerEmployee.calculatePayroll(wList.get(i));
                        if(this.deleted)
                            wEPB.setReRunInd(1);
                        wEPB.setMasterBean(payrollRunMasterBean);
                        if (this.firstTime == -1) {
                            if (wEPB.getHiringInfo().isFirstTimePay())
                                this.firstTime = 0;
                            else {
                                this.firstTime = 1;
                            }

                        }

                        this.wSaveList.add(wEPB);
                        if ((this.wSaveList.size() >= this.batchSize) || (i == wListSize - 1)) {
                            saveCalculatedPayroll(this.wSaveList, null,null,null,null);
                            this.wSaveList = new ArrayList<>();

                            if (!firstBatch)
                                this.timePerBatch = System.currentTimeMillis() - this.startTime;
                            this.startTime = System.currentTimeMillis();
                            firstBatch = true;

                        }

                        this.currentPercentage += 1;
                    }
                }
            }
            if (!getState()) {
                updateHiringInfo(this.firstTime);
            }
            this.currentPercentage = this.listSize;
        } catch (Exception e) {
            this.currentPercentage = -1;
            log.error("Critical Exception thrown from CalculatePayroll " + e.getMessage());
            e.printStackTrace();

            this.payrollRunMasterBean.setPayrollStatus(IConstants.ERROR_PAYROLL_IND);
        }
    }

    private HashMap<Long, HashMap<Long, Long>> setEmpToLoanMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setPendingPaycheckLoanInfo(businessCertificate, mdaInfo.getId(), runMonth, runYear);
    }

    private HashMap<Long, HashMap<Long, Long>> setEmpToDeductionMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setPendingPaycheckDeductionInfo(businessCertificate, mdaInfo.getId(), runMonth, runYear);
    }

    private HashMap<Long, HashMap<Long, Long>> setEmpToSpecAllowMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setPendingPaycheckSpecialAllowance(businessCertificate, mdaInfo.getId(), runMonth, runYear);
    }

    private HashMap<Long, Long> setEmployeeToPaycheckMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setPendingPaychecksToEmpMap(businessCertificate, mdaInfo.getId(), runMonth, runYear );
    }

    private HashMap<Long, Long> setEmployeeToPaycheckGratMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setEmpToPendingGratMap(businessCertificate, mdaInfo.getId(), runMonth, runYear);
    }

    private List<HiringInfo> findAllRerunEmployees(BusinessCertificate businessCertificate, MdaInfo mdaInfo,
                                                   LocalDate payPeriodStart, LocalDate payPeriodEnd) {

        return this.payrollService.loadActiveHiringInfoByMDAPInstId(businessCertificate,  mdaInfo, payPeriodStart, payPeriodEnd, defaultPfaInfo);

    }

    private boolean getState() {
        return this.stop;
    }


    /**
     * Gets the current percentage that is done.
     *
     * @return a percentage or 1111 if something went wrong.
     */
    public int getPercentage() {
        if (currentPercentage == -1)
            return 100;
        if (currentPercentage == 0)
            return currentPercentage;
        double wPercent = (((double) currentPercentage / listSize)) * 100;
        int wRetVal = EntityUtils.convertDoubleToEpmStandardZeroDecimal(wPercent);
        return wRetVal;
    }

    /**
     * Gets the remaining time for payroll to run fully.
     *
     * @return Time in years, months, days, hours, minutes, seconds
     */
    public String getTimeToElapse() {
        String wRetVal = "";
        if (currentPercentage == -1)
            return wRetVal;
        if (currentPercentage == 0)
            return wRetVal;

        Date currDate = new Date();
        long timeRemainingMillis = (this.getfRemainingRecords() / this.batchSize) * this.getTimePerBatch();
         Date endDate = new Date(currDate.getTime() + timeRemainingMillis);

        wRetVal = PayrollBeanUtils.getMeasuredTimeFromDates(currDate, endDate);


        return wRetVal;


    }

    public void stop(boolean pStop) {
        this.stop = pStop;
    }

    public int getCurrentRecord() {
        return this.currentPercentage;
    }

    public int getTotalRecords() {
        return this.listSize;
    }

    public boolean isFinished() {
        return (this.currentPercentage == this.listSize) || (this.currentPercentage == -1);
    }

    private synchronized void updateHiringInfo(int pFirstTime) {
        String hql = "";
        if (pFirstTime == 0) {
            hql = "update HiringInfo h set currentPayPeriod = '" + this.payPeriod + "' where h." + businessCertificate.getEmployeeIdJoinStr() + " not in (select p.employee.id from " + IppmsUtils.getPaycheckTableName(businessCertificate) +
                    " p where p.payPeriodStart = :pPPS and p.netPay = 0) and h.terminateInactive = 'N' and h.businessClientId = " + businessCertificate.getBusinessClientInstId();
        } else if (pFirstTime == 1) {
            hql = "update HiringInfo h set lastPayPeriod = currentPayPeriod, currentPayPeriod = '" + this.payPeriod + "' where h." + businessCertificate.getEmployeeIdJoinStr() + " not in (select p.employee.id from " + IppmsUtils.getPaycheckTableName(businessCertificate) +
                    " p where p.payPeriodStart = :pPPS and p.netPay = 0) and h.terminateInactive = 'N' and h.businessClientId = " + businessCertificate.getBusinessClientInstId();
        }

        this.payrollService.updateHiringInfoUsingHql(hql, this.payPeriodStart);
    }



    private synchronized void saveCalculatedPayroll(List<Object> pEmpPayBeanList, HashMap<Long, HashMap<Long, Long>> wEmpSpecAllowMap, HashMap<Long,
            HashMap<Long, Long>> wEmpDeductionMap, HashMap<Long, HashMap<Long, Long>> wEmpLoanMap, HashMap<Long, Long> wEmpPayCheckGratMap) throws Exception {

        this.payrollService.storeEmployeePayBeanList(businessCertificate, pEmpPayBeanList, this.payPeriodStart, this.payPeriodEnd, this.payDate, wEmpSpecAllowMap, wEmpDeductionMap, wEmpLoanMap, wEmpPayCheckGratMap);
    }

    private synchronized List<HiringInfo> findAllEmployees(MdaInfo pMdaType, LocalDate pPayPeriodStart, LocalDate pPayPeriodEnd, PfaInfo p) {

        if(this.deleted)
            return rerunPayrollService.loadRerunHiringInfo(pMdaType,businessCertificate,pPayPeriodStart,pPayPeriodEnd);
        return this.payrollService.loadActiveHiringInfoByMDAPInstId(businessCertificate, pMdaType, pPayPeriodStart, pPayPeriodEnd, p);
    }


    public void setTimePerBatch(long timePerBatch) {
        this.timePerBatch = timePerBatch;
    }


    public void setfRemainingRecords(int fRemainingRecords) {
        this.fRemainingRecords = fRemainingRecords;
    }


    public int getfRemainingRecords() {
        fRemainingRecords = this.getTotalRecords() - this.getCurrentRecord();
        return fRemainingRecords;
    }

}