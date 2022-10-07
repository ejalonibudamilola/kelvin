/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.dao;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.negativepay.domain.NegativePayBean;

import java.util.HashMap;
import java.util.List;

public interface INegPayDao {
    
    List<AbstractPaycheckEntity> loadNegativePaycheckByMonthAndYear(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear, int pStartRow, int pEndRow, Long pEmpInstId, String pLastName, boolean pShowAll) throws IllegalAccessException, InstantiationException;

    int getNoOfNegPaychecksByMonthAndYear(BusinessCertificate businessCertificate, int monthValue, int year, Long pEmpId, String pLastNameStr, boolean pShowAll);

    void resetNegativePayInd(BusinessCertificate bc,Long pEmpInstId, int pRunMonth, int pRunMonth1);
}
