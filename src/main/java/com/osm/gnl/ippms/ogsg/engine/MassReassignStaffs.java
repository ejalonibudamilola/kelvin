/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.controllers.BaseStatusClass;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignMasterBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.MassReassignBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MassReassignStaffs extends BaseStatusClass implements Runnable{


    private final GenericService genericService;
    private final HashMap<String, Long> salaryInfoList;
    private final HashMap<String, Long> toSalaryInfoList;
    private  List<MassReassignDetailsBean> employeeList;
    private final BusinessCertificate businessCertificate;
    private  MassReassignBean massReassignBean;

    //private Vector<MassReassignDetailsBean> wActualSaveList;
    private  MassReassignMasterBean massReassignMasterBean;

    public MassReassignStaffs(GenericService genericService, HashMap<String, Long> pSalaryInfoList, HashMap<String, Long> toSalaryInfoList, List<MassReassignDetailsBean> employeeList,
                              BusinessCertificate businessCertificate,
                              MassReassignMasterBean massReassignMasterBean, MassReassignBean wSJB) {
        this.genericService = genericService;
        this.salaryInfoList = pSalaryInfoList;
        this.employeeList = employeeList;
        this.toSalaryInfoList = toSalaryInfoList;
        this.businessCertificate = businessCertificate;
        this.massReassignMasterBean = massReassignMasterBean;
        this.massReassignBean = wSJB;

    }

    @SneakyThrows
    @Override
    public void run() {

        try {
            doTask();
        } catch (Exception wEx) {
            throw wEx;
        }
    }

    private synchronized void doTask() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        startTime = System.currentTimeMillis();
        this.listSize = employeeList.size();
        Map<Long,SalaryInfo> wFromMap = this.genericService.loadObjectAsMapWithConditions(SalaryInfo.class, Arrays.asList(CustomPredicate.procurePredicate("salaryType.id",massReassignMasterBean.getFromSalaryType().getId())),"id");
        Map<Long,SalaryInfo> wToMap =this.genericService.loadObjectAsMapWithConditions(SalaryInfo.class, Arrays.asList(CustomPredicate.procurePredicate("salaryType.id",massReassignMasterBean.getToSalaryType().getId())),"id");

       // wActualSaveList = new Vector<>();
        if(this.listSize > 50)
            this.batchSize = 20;
        else
            this.batchSize = 5;
        int noOfObject = 0;
        Long salaryInfoId;
        Double monthlyImpl = 0.00D;
        Double transMonthlyImpl = 0.00D;

        for (MassReassignDetailsBean p : employeeList) {

            salaryInfoId = this.toSalaryInfoList.get(p.getLevelAndStep());
            if(salaryInfoId == null){
                p.setStatus(1);
                p.setToSalaryInfo(p.getFromSalaryInfo());
            }else{
                p.setToSalaryInfo(new SalaryInfo(salaryInfoId));
            }
            transMonthlyImpl = EntityUtils.convertDoubleToEpmStandard(wToMap.get(salaryInfoId).getMonthlySalary() - wFromMap.get(p.getFromSalaryInfo().getId()).getMonthlySalary());
            p.setMonthlyImplication(PayrollHRUtils.getDecimalFormat().format(transMonthlyImpl));
            monthlyImpl += transMonthlyImpl;
            p.setMassReassignMasterBean(massReassignMasterBean);

            noOfObject++;

            this.currentPercentage += 1;
        }
        massReassignMasterBean.setMonthlyImplication(PayrollHRUtils.getDecimalFormat().format(monthlyImpl));
        massReassignMasterBean.setMassReassignDetailsBeans(employeeList);

       this.genericService.saveObject(massReassignMasterBean);

       this.massReassignBean.setRunning(0);
       this.massReassignBean.setEndDate(LocalDate.now());
       this.massReassignBean.setEndTime(PayrollBeanUtils.getCurrentTime(false));
       this.genericService.storeObject(massReassignBean);

        this.currentPercentage = this.listSize;
    }


    public MassReassignMasterBean getMassReassignMasterBean() {
        return massReassignMasterBean;
    }

}
