/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("paySlipService")
@Repository
@Transactional(readOnly = true)
public class PaySlipService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    public List<NamedEntity> loadPaySlipDependentObjects(BusinessCertificate bc, Long paycheckId, Integer pObjectCode){
        List<NamedEntity> wRetList = new ArrayList<>();
        String wHqlStr = null;

        switch (pObjectCode){
            case IConstants.DEDUCTION:
                wHqlStr = "select edi.description, pd.amount from "+IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getPaycheckDeductionTableName(bc)+ " pd, "+IppmsUtils.getDeductionInfoTableName(bc)+" edi  "
                        + "where edi.id = pd.empDedInfo.id and pd.employeePayBean.id = p.id and p.id = :pIdVar and p.businessClientId = pd.businessClientId " +
                        "and pd.businessClientId = :pBizIdVar";
                break;
            case IConstants.SPEC_ALLOW_IND:
                 wHqlStr = "select sai.description, pd.amount from "+IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+ " pd, "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" sai  "
                        + "where sai.id = pd.specialAllowanceInfo.id and pd.employeePayBean.id = p.id and p.id = :pIdVar and p.businessClientId = pd.businessClientId " +
                        "and pd.businessClientId = :pBizIdVar";
                 break;
            case IConstants.LOAN:
                wHqlStr = "select egi.description, pd.amount from "+ IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getPaycheckGarnishmentTableName(bc)+ " pd, "+IppmsUtils.getGarnishmentInfoTableName(bc)+" egi  "
                        + "where egi.id = pd.empGarnInfo.id and pd.employeePayBean.id = p.id and p.id = :pIdVar and p.businessClientId = pd.businessClientId " +
                        "and pd.businessClientId = :pBizIdVar";
                break;

        }



        Query query = sessionFactory.getCurrentSession().createQuery(wHqlStr);
        query.setParameter("pIdVar", paycheckId);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            NamedEntity n;
            for (Object[] o : wRetVal) {
                n = new NamedEntity();
                n.setName((String)o[0]);
                n.setDeductionAmount((Double)o[1]);

                wRetList.add(n);
            }
        }



        return wRetList;

    }
    public List<NamedEntity> loadPaySlipDependentObjectsYTD(BusinessCertificate bc, Long pEmpId, LocalDate pYearStart, LocalDate pYearEnd, Integer pObjectType){
        List<NamedEntity> wRetList = new ArrayList<>();
        String wHqlStr = null;
        switch (pObjectType){
            case IConstants.DEDUCTION:
                wHqlStr = "select edi.description, sum(pd.amount)  from "+IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getPaycheckDeductionTableName(bc)+ " pd, "+IppmsUtils.getDeductionInfoTableName(bc)+" edi  "
                        + "where edi.id = pd.empDedInfo.id and pd.employeePayBean.id = p.id and p.employee.id = :pIdVar and p.businessClientId = pd.businessClientId " +
                        "and pd.businessClientId = :pBizIdVar and pd.payPeriodStart >= :pPayPeriodStart and pd.payPeriodEnd < :pPayPeriodEnd " +
                        " group by edi.description";
                break;
            case IConstants.SPEC_ALLOW_IND:
                wHqlStr = "select sai.description, sum(pd.amount)  from "+IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getPaycheckSpecAllowTableName(bc)+ " pd, "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" sai  "
                        + "where sai.id = pd.specialAllowanceInfo.id and pd.employeePayBean.id = p.id and p.employee.id = :pIdVar and p.businessClientId = pd.businessClientId " +
                        "and pd.businessClientId = :pBizIdVar and pd.payPeriodStart >= :pPayPeriodStart and pd.payPeriodEnd < :pPayPeriodEnd " +
                        " group by sai.description";
                break;
            case IConstants.LOAN:
                wHqlStr = "select egi.description, sum(pd.amount)  from "+IppmsUtils.getPaycheckTableName(bc)+" p, "+IppmsUtils.getPaycheckGarnishmentTableName(bc)+ " pd, "+IppmsUtils.getGarnishmentInfoTableName(bc)+" egi  "
                        + "where egi.id = pd.empGarnInfo.id and pd.employeePayBean.id = p.id and p.employee.id = :pIdVar and p.businessClientId = pd.businessClientId " +
                        "and pd.businessClientId = :pBizIdVar and pd.payPeriodStart >= :pPayPeriodStart and pd.payPeriodEnd < :pPayPeriodEnd " +
                        " group by egi.description";
                break;
        }



        Query query = sessionFactory.getCurrentSession().createQuery(wHqlStr);
        query.setParameter("pIdVar", pEmpId);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        query.setParameter("pPayPeriodStart", pYearStart);
        query.setParameter("pPayPeriodEnd", pYearEnd);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {
            NamedEntity n;
            for (Object[] o : wRetVal) {
                n = new NamedEntity();
                n.setName((String)o[0]);
                n.setDeductionAmount((Double)o[1]);

                wRetList.add(n);
            }
        }



        return wRetList;

    }


}
