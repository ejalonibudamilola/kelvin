/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.base.services.RerunPayrollService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.PayrollConfigImpact;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
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
public class CalculatePayrollPensioner implements Runnable{


    private final GenericService genericService;
    private final PensionService pensionService;
    private final PayrollService payrollService;
    private final CalculatePayPerPensioner fCalcPayPerEmployee;
    private final PayrollRunMasterBean payrollRunMasterBean;
    private  RerunPayrollService rerunPayrollService;
    private LocalDate fPayDate;
    private LocalDate fPayPeriodStart;
    private LocalDate fPayPeriodEnd;
    private String fPayPeriod;
    private String fCurrentUser;
    private boolean fObjectSaved;
    private int fListSize;
    private int fCurrentPercentage;
    private PfaInfo defPfaInfo;
    private final BusinessCertificate businessCertificate;
    private final ConfigurationBean configurationBean;
    private final PayrollConfigImpact payrollConfigImpact;
    private int runMonth;
    private int runYear;
    private long startTime;
    //private long endTime;
    private long timePerBatch;
    private int fRemainingRecords;
    private int currentPercentage;
    private boolean wBatch;
    private boolean fStop;
    private boolean deletedPaychecks;
    private int batchSize;
    private int firstTime = -1;
    private List<Object> wSaveList;
    private List<MdaInfo> mdaInfoList;
    private boolean rerun;
    private RerunPayrollBean rerunPayrollBean;



    public CalculatePayrollPensioner(GenericService genericService, PayrollService payrollService, PensionService pensionService, BusinessCertificate businessCertificate, LocalDate pPayDate,
                                     LocalDate pPayPeriodStart, LocalDate pPayPeriodEnd, String pPayPeriod, String pCurrentUser, CalculatePayPerPensioner pCalcPayPerEmployee,
                                     int pFullListSize, PayrollRunMasterBean pPayrollRunMasterBean, List<MdaInfo> pMdaInfoList, PfaInfo defPfaInfo, ConfigurationBean configurationBean,PayrollConfigImpact payrollConfigImpact) {
        this.genericService = genericService;
        this.payrollService = payrollService;
        this.pensionService = pensionService;
        this.fCurrentUser = pCurrentUser;
        this.fPayDate = pPayDate;
        this.fPayPeriod = pPayPeriod;
        this.fPayPeriodEnd = pPayPeriodEnd;
        this.fPayPeriodStart = pPayPeriodStart;
        fCalcPayPerEmployee = pCalcPayPerEmployee;
        this.businessCertificate= businessCertificate;
        this.fListSize = pFullListSize;
        this.batchSize = 500;
        fStop = false;
        this.wSaveList = new ArrayList<>();

        this.payrollRunMasterBean = pPayrollRunMasterBean;
        this.runMonth = pPayPeriodEnd.getMonthValue();
        this.runYear = pPayPeriodEnd.getYear();
        this.mdaInfoList = pMdaInfoList;
        this.defPfaInfo = defPfaInfo;
        this.configurationBean = configurationBean;
        this.payrollConfigImpact = payrollConfigImpact;

    }
    public CalculatePayrollPensioner(GenericService genericService, PayrollService payrollService, PensionService pPensionService, BusinessCertificate businessCertificate, LocalDate pPayDate,
                                     LocalDate pPayPeriodStart, LocalDate pPayPeriodEnd, String pPayPeriod, String pCurrentUser, CalculatePayPerPensioner pCalcPayPerEmployee,
                                     int pFullListSize, boolean pRerun, PayrollRunMasterBean pPayrollRunMasterBean, RerunPayrollBean pRerunPayrollBean, List<MdaInfo> pMdaInfoList, RerunPayrollService rerunPayrollService1,
                                     boolean pDeleted, PfaInfo defPfaInfo, ConfigurationBean configurationBean,PayrollConfigImpact payrollConfigImpact) throws Exception{
        this.genericService = genericService;
        this.payrollService = payrollService;
        this.pensionService = pPensionService;
        this.fCurrentUser = pCurrentUser;
        this.fPayDate = pPayDate;
        this.fPayPeriod = pPayPeriod;
        this.fPayPeriodEnd = pPayPeriodEnd;
        this.fPayPeriodStart = pPayPeriodStart;
        fCalcPayPerEmployee = pCalcPayPerEmployee;
        this.businessCertificate = businessCertificate;
        this.fListSize = pFullListSize ;
        this.batchSize = 500;
        fStop = false;
        this.wSaveList = new ArrayList<>();
        this.rerun = pRerun;
        this.runMonth = pPayPeriodEnd.getMonthValue();
        this.runYear = pPayPeriodEnd.getYear();
        this.payrollRunMasterBean = pPayrollRunMasterBean;
        this.rerunPayrollBean = pRerunPayrollBean;
        this.mdaInfoList = pMdaInfoList;
        this.rerunPayrollService = rerunPayrollService1;
        this.deletedPaychecks = pDeleted;
        this.defPfaInfo = defPfaInfo;
        this.configurationBean = configurationBean;
        this.payrollConfigImpact = payrollConfigImpact;
    }


    /**
     * @see java.lang.Runnable#run()

    public void run() {

    if(wListSize > batchSize)
    wBatch = true;
    try {

    for(int i = 0; i < wListSize; i++){
    if(getState())
    break;
    EmployeePayBean wEPB = fCalcPayPerEmployee.calculatePayroll(listToPay.get(i));
    if(firstTime == -1){
    if(wEPB.getHiringInfo().isFirstTimePay()){
    firstTime = 0;
    }else{
    firstTime = 1;
    }
    }

    if(wBatch){
    wSaveList.add(wEPB);
    if(wSaveList.size() >= batchSize || i == wListSize -1){
    saveCalculatedPayroll(wSaveList);
    wSaveList = new ArrayList<EmployeePayBean>();
    }
    }else{
    saveCalculatedPayroll(wEPB);
    }
    fCurrentPercentage++;
    // Thread.sleep(2000);
    }
    //Now Update HiringInfo.
    if(!getState())
    updateHiringInfo(firstTime);
    } catch (Exception e) {
    fCurrentPercentage = -1;
    Logger.getLogger(this.getClass()).error("Critical Exception thrown from CalculatePayroll"+e.getMessage());
    }

    }
     */
    @SneakyThrows
    public void run() {


        try {
            fCalcPayPerEmployee.setConfigurationBean(this.configurationBean);
            fCalcPayPerEmployee.setNoOfDays(PayrollBeanUtils.getNoOfDays(this.runMonth,this.runYear));
            fCalcPayPerEmployee.setRerun(this.rerun);
            if(this.rerun){
                startTime = System.currentTimeMillis();
                boolean firstBatch = false;
                for (MdaInfo m : mdaInfoList) {

                    if (getState())
                        break;

                    //-- Let it find all RERUN
                    List<HiringInfo> wList = this.findAllRerunEmployees(  businessCertificate, m, this.fPayPeriodStart,this.fPayPeriodEnd);
                    if(wList.isEmpty())
                        continue;
                    HashMap<Long,Long> wEmpPayCheckMap = this.setEmployeeToPaycheckMap(m);
                    HashMap<Long,HashMap<Long, Long>> wEmpSpecAllowMap = this.setEmpToSpecAllowMap(m);
                    HashMap<Long, HashMap<Long, Long>> wEmpDeductionMap = this.setEmpToDeductionMap(m);
                    HashMap<Long, HashMap<Long, Long>> wEmpLoanMap = this.setEmpToLoanMap(m);
                    HashMap<Long,Long> wEmpPayCheckGratMap = this.setEmployeeToPaycheckMap(m);

                    int wListSize = wList.size();
                    for (int i = 0; i < wList.size() && (!getState()); i++) {

                        AbstractPaycheckEntity wEPB = fCalcPayPerEmployee.calculatePayroll(wList.get(i));
                        wEPB.setMasterBean(payrollRunMasterBean);
                        if(wEmpPayCheckMap.containsKey(wEPB.getHiringInfo().getAbstractEmployeeEntity().getId())){
                            wEPB.setId(wEmpPayCheckMap.get(wEPB.getHiringInfo().getAbstractEmployeeEntity().getId()));
                        }
                        if (firstTime == -1) {
                            if (wEPB.getHiringInfo().isFirstTimePay()) {
                                firstTime = 0;
                            } else {
                                firstTime = 1;
                            }
                        }

                        //if (wBatch) {
                        wSaveList.add(wEPB);
                        if (wSaveList.size() >= batchSize || (i == wListSize - 1)) {
                            saveCalculatedPayroll(wSaveList,wEmpSpecAllowMap,wEmpDeductionMap,wEmpLoanMap,wEmpPayCheckGratMap);
                            wSaveList = new ArrayList<>();
                            if (!firstBatch)
                                this.timePerBatch = System.currentTimeMillis() - this.startTime;
                            firstBatch = true;
                        }

                        this.currentPercentage += 1;
                        // Thread.sleep(2000);
                    }



                }
            }else{
                startTime = System.currentTimeMillis();
                boolean firstBatch = false;
                for (MdaInfo m : mdaInfoList) {

                    if (getState())
                        break;
                    List<HiringInfo> wList = this.findAllEmployees(  businessCertificate, m, this.fPayPeriodStart,this.fPayPeriodEnd);

                    if(wList.isEmpty())
                        continue;
                    int wListSize = wList.size();

                    wBatch = true;

                    for (int i = 0; i < wList.size(); i++) {
                        if (getState())
                            break;
                        AbstractPaycheckEntity wEPB = fCalcPayPerEmployee.calculatePayroll(wList.get(i));
                        if(this.deletedPaychecks)
                            wEPB.setReRunInd(1);
                        wEPB.setMasterBean(payrollRunMasterBean);
                        if (firstTime == -1) {
                            if (wEPB.getHiringInfo().isFirstTimePay()) {
                                firstTime = 0;
                            } else {
                                firstTime = 1;
                            }
                        }

                        //if (wBatch) {
                        wSaveList.add(wEPB);
                        if (wSaveList.size() >= batchSize
                                || i == wListSize - 1) {
                            saveCalculatedPayroll(wSaveList,null,null,null, null);
                            wSaveList = new ArrayList<>();
                            if (!firstBatch)
                                this.timePerBatch = System.currentTimeMillis() - this.startTime;
                            this.startTime = System.currentTimeMillis();
                            firstBatch = true;
                        }

                        this.currentPercentage += 1;
                        // Thread.sleep(2000);
                    }



                }
            }

            // Now Update HiringInfo.
            if (!getState())
                updateHiringInfo(firstTime);
            this.currentPercentage = this.fListSize;
           // fCurrentPercentage = fListSize;

        } catch (Exception e) {
            this.currentPercentage = -1;
            log.error("Critical Exception thrown from CalculatePayroll "+e.getMessage());
            e.printStackTrace();
            this.payrollRunMasterBean.setPayrollStatus(IConstants.ERROR_PAYROLL_IND);
        }
        finally{
           /* if(this.rerun || this.deletedPaychecks)
            {
                PayrollBeanUtils.endPayrollRun(this.payrollRunMasterBean, this.rerunPayrollBean,genericService,businessCertificate.getBusinessClientInstId());
            }
            else
            {
                PayrollBeanUtils.endPayrollRun(this.payrollRunMasterBean, null, genericService,businessCertificate.getBusinessClientInstId());
            }*/
        }

    }

    private HashMap<Long, HashMap<Long, Long>> setEmpToLoanMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setPendingPaycheckLoanInfo(businessCertificate,mdaInfo.getId(),runMonth,runYear);
    }

    private HashMap<Long, HashMap<Long, Long>> setEmpToDeductionMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setPendingPaycheckDeductionInfo(businessCertificate,mdaInfo.getId(),runMonth,runYear);
    }

    private HashMap<Long, HashMap<Long, Long>> setEmpToSpecAllowMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setPendingPaycheckSpecialAllowance(businessCertificate,mdaInfo.getId(),runMonth,runYear);
    }

    private HashMap<Long, Long> setEmployeeToPaycheckMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setPendingPaychecksToEmpMap(businessCertificate,mdaInfo.getId(),runMonth,runYear);
    }
    private HashMap<Long, Long> setEmployeeToPaycheckGratMap(MdaInfo mdaInfo) {
        return rerunPayrollService.setEmpToPendingGratMap(businessCertificate,mdaInfo.getId(),runMonth,runYear);
    }
    private List<HiringInfo> findAllRerunEmployees(BusinessCertificate businessCertificate, MdaInfo mdaInfo, LocalDate fPayPeriodStart, LocalDate fPayPeriodEnd) {
        return this.payrollService.loadActiveHiringInfoByMDAPInstId(businessCertificate,mdaInfo,fPayPeriodStart,fPayPeriodEnd,defPfaInfo);

    }

    private boolean getState(){
        return this.fStop;
    }


    /**
     * Gets the current percentage that is done.
     * @return a percentage or 1111 if something went wrong.
     */
    public int getPercentage() {
        if (currentPercentage == -1)
            return 100;
        if (currentPercentage == 0)
            return currentPercentage;
        double wPercent = (((double) currentPercentage / fListSize)) * 100;
        int wRetVal = EntityUtils.convertDoubleToEpmStandardZeroDecimal(wPercent);
        return wRetVal;
    }
    public void stop(boolean pStop){
        fStop = pStop;
    }
    public int getCurrentRecord(){
        return this.currentPercentage;
    }

    public int getTotalRecords(){
        return this.fListSize;
    }
    public boolean isFinished(){
        return currentPercentage == fListSize || currentPercentage == -1;
    }
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
    public void setTimePerBatch(long timePerBatch) {
        this.timePerBatch = timePerBatch;
    }
    public int getfRemainingRecords() {
        fRemainingRecords = this.getTotalRecords() - this.getCurrentRecord();
        return fRemainingRecords;
    }

    private synchronized void updateHiringInfo(int pFirstTime){
        String hql = "";
        if(pFirstTime == 0){
            hql = "update HiringInfo h set currentPayPeriod = '"+fPayPeriod+"' where h.pensioner.id not in (select p.employee.id from "+ IppmsUtils.getPaycheckTableName(businessCertificate)+
            " p where p.payPeriodStart = :pPPS and p.netPay = 0) and h.terminateInactive = 'N' and h.businessClientId = "+businessCertificate.getBusinessClientInstId();

        }else if(pFirstTime == 1){
            hql = "update HiringInfo h set lastPayPeriod = currentPayPeriod, currentPayPeriod = '"+fPayPeriod+"' where h.pensioner.id not in (select p.employee.id from "+IppmsUtils.getPaycheckTableName(businessCertificate)+" p " +
                    "where p.payPeriodStart = :pPPS and p.netPay = 0) and h.terminateInactive = 'N' and h.businessClientId = "+businessCertificate.getBusinessClientInstId();

        }

        this.payrollService.updateHiringInfoUsingHql(hql,fPayPeriodStart);


    }

    private synchronized void saveCalculatedPayroll(List<Object> pEmpPayBeanList,HashMap<Long,HashMap<Long, Long>> wEmpSpecAllowMap,HashMap<Long,
            HashMap<Long, Long>> wEmpDeductionMap  ,HashMap<Long, HashMap<Long, Long>> wEmpLoanMap ,HashMap<Long,  Long>wEmpPayCheckGratMap    ) throws Exception{

        this.payrollService.storeEmployeePayBeanList(businessCertificate,pEmpPayBeanList, fPayPeriodStart, fPayPeriodEnd, fPayDate, wEmpSpecAllowMap ,wEmpDeductionMap,wEmpLoanMap,wEmpPayCheckGratMap);


    }
    private List<HiringInfo> findAllEmployees(BusinessCertificate businessCertificate, MdaInfo pMdaInfo, LocalDate pPayPeriodStart, LocalDate pPayPeriodEnd) {

        if(this.deletedPaychecks)
            return rerunPayrollService.loadRerunHiringInfo(pMdaInfo,businessCertificate,pPayPeriodStart,pPayPeriodEnd);
        return payrollService.loadActiveHiringInfoByMDAPInstId(businessCertificate, pMdaInfo,pPayPeriodStart,pPayPeriodEnd,defPfaInfo);

    }




}
