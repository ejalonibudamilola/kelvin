/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.controllers.BaseStatusClass;
import com.osm.gnl.ippms.ogsg.domain.chart.MiniSalaryInfoDao;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Vector;

@Slf4j
public class PersistMiniSalaryInfo extends BaseStatusClass implements Runnable {

    private final GenericService genericService;
    private final MiniSalaryInfoDao miniSalaryInfoDao;
    private final BusinessCertificate businessCertificate;


    public PersistMiniSalaryInfo(GenericService genericService, MiniSalaryInfoDao miniSalaryInfoDao, BusinessCertificate businessCertificate) {
        this.genericService = genericService;
        this.miniSalaryInfoDao = miniSalaryInfoDao;
        this.businessCertificate = businessCertificate;

    }

    @Override
    public void run() {
        try {
            persistMiniSalaryInformation();
        } catch (Exception wEx) {
            log.error(wEx.getMessage());
            wEx.printStackTrace();
        }
    }

    private void persistMiniSalaryInformation()  {
        startTime = System.currentTimeMillis();
        this.listSize = miniSalaryInfoDao.getMiniSalaryInfoDaoList().size();

        if (this.listSize > 50)
            this.batchSize = 20;
        else
            this.batchSize = 5;
        Vector<Object> wActualSaveList = new Vector<>();

        for (MiniSalaryInfoDao p : miniSalaryInfoDao.getMiniSalaryInfoDaoList()) {

            p.setLastModBy(businessCertificate.getLoginId());
            p.setLastModTs(Timestamp.from(Instant.now()));

            wActualSaveList.add(p);
            if (wActualSaveList.size() == 50) {
                this.genericService.storeObjectBatch(wActualSaveList);
                wActualSaveList = new Vector<>();
                if (!firstBatch)
                    this.timePerBatch = System.currentTimeMillis() - this.startTime;
                this.startTime = System.currentTimeMillis();
                firstBatch = true;
            }
            this.currentPercentage += 1;
        }
        if (!wActualSaveList.isEmpty())
            this.genericService.storeObjectBatch(wActualSaveList);

        this.currentPercentage = this.listSize;
    }

    public MiniSalaryInfoDao getMiniSalaryInfoDao() {
        return miniSalaryInfoDao;
    }
}
