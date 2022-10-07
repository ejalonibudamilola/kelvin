/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.repository;

import com.osm.gnl.ippms.ogsg.base.dao.ISimulationDao;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.report.beans.RetMainBean;
import com.osm.gnl.ippms.ogsg.report.beans.RetMiniBean;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class SimulationDaoImpl implements ISimulationDao {

    @Autowired
    protected SessionFactory sessionFactory;

    @Autowired
    public SimulationDaoImpl(SessionFactory sessionFactory) {

    }

    @Override
    public RetMainBean populateAverages(final BusinessCertificate bcert, int pStartMonth, int pStartYear, int pEndMonth, int pEndYear, int pNoofMonths) {


        RetMainBean wRMB = new RetMainBean();
        String hqlQuery = "select sum(p.monthlyTax),sum(p.totalDeductions), sum(p.specialAllowance),p.employee.id from " + bcert.getPaycheckBeanName()
                + " p where (p.runMonth >= :pStartMonthVar and p.runYear = :pStartYearVar ) "
                + "or (p.runMonth <= :pEndMonthVar and p.runYear = :pEndYearVar ) group by p.employee.id ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        wQuery.setParameter("pStartMonthVar", pStartMonth);
        wQuery.setParameter("pStartYearVar", pStartYear);
        wQuery.setParameter("pEndMonthVar", pEndMonth);
        wQuery.setParameter("pEndYearVar", pEndYear);
        Double wDouble = new Double(String.valueOf(pNoofMonths));
        BigDecimal wNoOfYearsBD = new BigDecimal(wDouble.toString()).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal wTaxesBD = null;
        BigDecimal wDedBD = null;
        BigDecimal wSABD = null;
        List<Object[]> results = wQuery.list();
        HashMap<Long, RetMiniBean> wMap = new HashMap<Long, RetMiniBean>();


        if ((results != null) && (results.size() > 0)) {
            int i = 0;
            for (Object[] o : results) {
                RetMiniBean _wRMB = new RetMiniBean();
                Double _mt = (Double) o[i++];
                Double _td = (Double) o[i++];
                Double _sa = (Double) o[i++];
                Long _id = (Long) o[i++];
                _wRMB.setId(_id);
                wTaxesBD = new BigDecimal(_mt).setScale(2, RoundingMode.HALF_EVEN);
                wDedBD = new BigDecimal(_td).setScale(2, RoundingMode.HALF_EVEN);
                wSABD = new BigDecimal(_sa).setScale(2, RoundingMode.HALF_EVEN);

                if (wTaxesBD.doubleValue() > 0.0D) {
                    wTaxesBD = wTaxesBD.divide(wNoOfYearsBD, 2, RoundingMode.HALF_EVEN);
                    _wRMB.setAverageTax(wTaxesBD.doubleValue());

                }
                if (wDedBD.doubleValue() > 0.0D) {
                    wDedBD = wDedBD.divide(wNoOfYearsBD, 2, RoundingMode.HALF_EVEN);
                    _wRMB.setAverageDeductions(wDedBD.doubleValue());

                }
                if (wSABD.doubleValue() > 0.0D) {
                    wSABD = wSABD.divide(wNoOfYearsBD, 2, RoundingMode.HALF_EVEN);
                    _wRMB.setAverageSpecAllow(wSABD.doubleValue());

                }
                wMap.put(_id, _wRMB);
                i = 0;
            }
            wRMB.setRetMiniBeanMap(wMap);
        }

        return wRMB;

    }

    @Override
    public HashMap<Long, Long> getObjectsToApplyLtg(Long pLtgMasterInstId) {


        HashMap<Long, Long> wRetList = new HashMap<Long, Long>();
        String wSql = "select a.mdaInfo.id,a.mdaInfo.name from AbmpBean a, MdaType m  where a.ltgMasterBean.id = :pPid "
                + "and a.mdaInfo.mdaType.id = m.id and a != null ";

        Query query = sessionFactory.getCurrentSession().createQuery(wSql);

        query.setParameter("pPid", pLtgMasterInstId);

        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();

        wRetVal = (ArrayList) query.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {

            for (Object[] o : wRetVal) {

                if (!wRetList.containsKey(o[0])) {
                    wRetList.put((Long) o[0], (Long) o[0]);
                }


            }

        }


        return wRetList;

    }
}
