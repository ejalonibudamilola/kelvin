/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.dao.INegPayDao;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("negPayService")
public class NegPayService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Autowired
    private final INegPayDao negPayDao;


    public NegPayService(INegPayDao negPayDao) {
        this.negPayDao = negPayDao;
    }

    public List<AbstractPaycheckEntity> loadNegativeEmployeePayBeanByRunMonthAndYear(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear, int pStartRow,
                                                                                     int pEndRow, Long pEmpInstId, String pLastName, boolean pShowAll) throws InstantiationException, IllegalAccessException {
        return this.negPayDao.loadNegativePaycheckByMonthAndYear( businessCertificate, pRunMonth,  pRunYear,  pStartRow, pEndRow,  pEmpInstId, pLastName, pShowAll);
    }


    public int getNoOfNegativePaychecksByRunMonthAndYear(BusinessCertificate businessCertificate,int monthValue, int year, Long pEmpId, String pLastNameStr, boolean pShowAll) {

        return this.negPayDao.getNoOfNegPaychecksByMonthAndYear(businessCertificate, monthValue, year, pEmpId, pLastNameStr, pShowAll);

    }

    public void resetNegativePayInd(BusinessCertificate businessCertificate,Long pEmpInstId, int pRunMonth, int pRunYear) {
        this.negPayDao.resetNegativePayInd(businessCertificate,pEmpInstId,pRunMonth,pRunMonth);
    }

}
