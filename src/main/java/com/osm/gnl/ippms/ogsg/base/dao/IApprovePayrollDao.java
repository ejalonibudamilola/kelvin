/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.dao;


import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public interface IApprovePayrollDao extends Serializable {

    void savePendingPayroll(List<AbstractGarnishmentEntity> wListToSave) throws Exception;

    void saveObjects(List<?> wListToSave) throws Exception;

    /**
     * @param pRunMonth
     * @param pRunYear
     * @param pPayDate
     * @param pEndDate
     * @param pStartDate
     * @param pCurrPayPeriod
     * @param pDevLevy
     * @param businessCertificate
     * @throws Exception
     */
    void savePayrollInformation(int pRunMonth, int pRunYear, LocalDate pPayDate, LocalDate pEndDate, LocalDate pStartDate,
                                String pCurrPayPeriod, int pDevLevy, BusinessCertificate businessCertificate) throws Exception;

    void storeObject(Object pObject);
}
