/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGratuity;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service("pensionService")
@Repository
@Transactional(readOnly = true)
public class PensionService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public boolean isPensionerBeenPaidGratuity(Long pensionerId, BusinessCertificate businessCertificate) {
        PredicateBuilder predicateBuilder  = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("pensioner.id", pensionerId)).addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        return this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, PaycheckGratuity.class) > 0;
    }

    public boolean hasPensionerEverBeenPaid(Long pensionerId, BusinessCertificate businessCertificate) {
        PredicateBuilder predicateBuilder  = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("employee.id", pensionerId)).addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        return this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(businessCertificate)) > 0;
    }

    public List<NamedEntity> loadUniqueOutstandingGratuities() {
        String wHqlStr = "select g.month,g.year from GratuityInfo g where g.outstandingAmount > 0.00"
                + " group by g.month,g.year order by g.month,g.year ";

        Query query = sessionFactory.getCurrentSession().createQuery(wHqlStr);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        List<NamedEntity> wRetList = new ArrayList<NamedEntity>();

        if (wRetVal.size() > 0) {
            int i = 0;
            for (Object[] o : wRetVal) {
                int month = (Integer) o[0];
                int year = (Integer) o[1];
                String name = PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(month, year);
                NamedEntity n = new NamedEntity(new Integer(++i), name);
                n.setPayPercentageStr("0.00");
                n.setNoOfEmployees(month);
                n.setPageSize(year);
                n.setPayGratuity(IConstants.HIDE_ROW);
                wRetList.add(n);
            }
        }
        return wRetList;

    }


     public HashMap<Long, NamedEntity> loadEmployeeGratuityByMonthAndYear(HashMap<Long, NamedEntity> pEmpGratuityMap, int applyMonth, int applyYear) {
        String wHqlStr = "select g.pensioner.id,g.outstandingAmount,g.id from GratuityInfo g"
                + " where g.outstandingAmount > 0 and g.month = :pMonth and g.year = :pYear ";

        Query query = sessionFactory.getCurrentSession().createQuery(wHqlStr);
        query.setParameter("pMonth", applyMonth);
        query.setParameter("pYear", applyYear);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();

        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                Long pEmpId = (Long) o[0];
                Double wOutstanding = (Double) o[1];
                Long pGid = (Long) o[2];
                NamedEntity wNamedEntity = new NamedEntity();
                wNamedEntity.setAllowanceAmount(wOutstanding);
                wNamedEntity.setNoOfEmployees(applyMonth);
                wNamedEntity.setPageSize(applyYear);
                wNamedEntity.setId(pGid);
                if (!pEmpGratuityMap.containsKey(pEmpId)) {
                    pEmpGratuityMap.put(pEmpId, wNamedEntity);
                }
            }
        }
        return pEmpGratuityMap;
    }
    public List<MdaInfo> loadPaidGratuityByYear(BusinessCertificate bc, int applyYear,boolean forExcel, int pStartRow, int pEndRow) {
        List<MdaInfo> wRetList = new ArrayList<>();

        String wHqlStr = "select m.id, m.name,sum(g.amount),count(g.pensioner.id) from PaycheckGratuity g, Pensioner p,MdaInfo m, MdaDeptMap mdm " +
                " where p.id = g.pensioner.id and p.businessClientId = :pBizIdVar and " +
                "g.amount > 0 and g.runYear = :pYear and p.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id" +
                " group by m.id,m.name";

        Query query = sessionFactory.getCurrentSession().createQuery(wHqlStr);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        query.setParameter("pYear", applyYear);

        if(!forExcel)
        {	if (pStartRow > 0)
               query.setFirstResult(pStartRow);
            query.setMaxResults(pEndRow);
        }
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        int i = 0;
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                Long mdaId = (Long) o[i++];
                String mdaName = (String)o[i++];
                Double wOutstanding = (Double) o[1];
                int noOfStaff = ((Long)o[i++]).intValue();
                MdaInfo mdaInfo = new MdaInfo(mdaId,mdaName);
                mdaInfo.setTotalNoOfEmployees(noOfStaff);
                mdaInfo.setTotalGrossPay(wOutstanding);
                wRetList.add(mdaInfo);

            }
        }
        return wRetList;
    }
    public int getTotalNoOfGratuityPaidByYear(Long pBizId ) {
         String wHqlStr = "select count(m.id) from Pensioner p,MdaInfo m, MdaDeptMap mdm " +
                " where p.businessClientId = :pBizIdVar and p.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id" +
                " group by m.id,m.name";

        Query query = sessionFactory.getCurrentSession().createQuery(wHqlStr);
        query.setParameter("pBizIdVar",pBizId);
        int count = 0;
        List results = query.list();

        count =  results.size();
        return count;

    }

   /* public HiringInfo loadHiringInfoByPensionPinCode(@NotNull String pPensionPinCode){
        String wHqlStr = "select h.id,e.id, e.firstName,e.lastName, coalesce(e.initials,' '), e.employeeId from HiringInfo h, Employee e"+
                        " where e.id = h.employee.id and h.pensionPinCode is not null and upper(trim(h.pensionPinCode)) = :pPinCodeVar" ;

        Query query = sessionFactory.getCurrentSession().createQuery(wHqlStr);
         query.setParameter("pPinCodeVar", pPensionPinCode.trim().toUpperCase());

        HiringInfo hiringInfo = new HiringInfo();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                 hiringInfo.setId((Long)o[0]);
                 hiringInfo.setEmployee(new Employee((Long)o[1], (String)o[2], (String)o[3], o[4]));
                 hiringInfo.setEmployeeId((String)o[5]);
            }
        }
        return hiringInfo;
    }*/

    public List<NamedEntity> makeYearList(BusinessCertificate businessCertificate) {

        String wHql = "SELECT distinct(p.entryYearInd) FROM Pensioner p where p.entryYearInd > 1900 order by p.entryYearInd desc ";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        ArrayList<Integer> wRetList = (ArrayList<Integer>) wQuery.list();
        List<NamedEntity> wRetVal = new ArrayList<>();
        if (wRetList.size() > 0) {
            for (Integer o : wRetList) {
                NamedEntity e = new NamedEntity();
                e.setId(new Long(o));
                e.setName(String.valueOf(o));
                wRetVal.add(e);
            }
        }
        Collections.sort(wRetVal);
        return wRetVal;
    }
   public List<AbstractPaycheckEntity> loadPensionersByEntrantMonthAndYear(BusinessCertificate bc, int rm, int ry) {

       String wSql = "select p.id, p.lastName, p.firstName, p.initials, emp.accountNumber," +
               " emp.monthlyPension, emp.specialAllowance, emp.totalDeductions, emp.netPay, p.employeeId, m.name from" +
               " "+IppmsUtils.getEmployeeTableName(bc)+" p, "+IppmsUtils.getPaycheckTableName(bc)+" emp, MdaInfo m " +
               " where p.mdaDeptMap.mdaInfo.id = m.id and p.id = emp.employee.id and" +
               " p.entryMonthInd = :pRunMonthVar and p.entryYearInd = :pRunYearVar";

       Query query = sessionFactory.getCurrentSession().createQuery(wSql);
       query.setParameter("pRunMonthVar", rm);
       query.setParameter("pRunYearVar", ry);

       ArrayList<Object[]> wRetVal = (ArrayList)query.list();

       List<AbstractPaycheckEntity> wRetList = new ArrayList<>();

       EmployeePayBean p;
       for (Object[] o : wRetVal) {
           p = new EmployeePayBean();
           p.setId((Long) o[0]);
           p.setEmployeeName(PayrollHRUtils.createDisplayName((String) o[1], (String) o[2], (String) o[3]));
           p.setAccountNumber((String) o[4]);
           p.setMonthlyPension((Double) o[5]);
           p.setArrears((Double) o[6]);
           p.setTotalDeductions((Double) o[7]);
           p.setNetPay((Double) o[8]);
           p.setEmployeeId((String) o[9]);
           p.setMda((String) o[10]);
           wRetList.add(p);
       }
        return wRetList;

    }
}
