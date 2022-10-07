/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.repository;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.dao.IPaycheckDao;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.beans.BankPVSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class PaycheckDaoImpl implements IPaycheckDao {



    private final SessionFactory sessionFactory;

    @Autowired
    public PaycheckDaoImpl(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<?> loadEmployeePayBeanByRunMonthAndRunYear(BusinessCertificate b, int runMonth, int runYear) {

        ArrayList<Object[]> wRetVal = new ArrayList<>();
        List wRetList = new ArrayList();


        String hqlQuery = "select p.id, p.otherAllowance,p.taxesPaid,p.unionDues,p.netPay,p.salaryInfo.id, "
                + "mdm.id,m.id,m.name,p.specialAllowance,p.leaveTransportGrant,p.contractAllowance  "
                + "from "+b.getPaycheckBeanName()+" p, MdaInfo m, MdaDeptMap mdm where p.runMonth = :pRunMonth and p.runYear = :pRunYear "
                + "and p.status = 'A' and mdm.id = p.mdaDeptMap.id and mdm.mdaInfo.id = m.id and p is not null";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pRunMonth", runMonth);
        query.setParameter("pRunYear", runYear);

        wRetVal = (ArrayList)query.list();

        if (wRetVal.size() > 0)
        {

            int i = 0;
            for (Object[] o : wRetVal) {
                AbstractPaycheckEntity p = IppmsUtils.makePaycheckObject(b);
                p.setId((Long)o[i++]);
                p.setOtherAllowance(((Double)o[i++]));
                p.setTaxesPaid(((Double)o[i++]));
                p.setMonthlyTax(p.getTaxesPaid());
                p.setUnionDues(((Double)o[i++]));
                p.setNetPay(((Double)o[i++]));
                SalaryInfo s = new SalaryInfo((Long)o[i++]);
                p.setSalaryInfo(s);
                p.setMdaDeptMap(new MdaDeptMap((Long)o[i++]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++],(String)o[i++]));

                p.setSpecialAllowance(((Double)o[i++]));
                p.setLeaveTransportGrant(((Double)o[i++]));
                p.setContractAllowance(((Double)o[i++]));

                wRetList.add(p);
                i = 0;
            }

        }

        return wRetList;

    }

    @Override
    public Long getMaxPaycheckIdForEmployee(BusinessCertificate businessCertificate, Long pEmpId) {

        Long wRetVal = 0L;
        String tableName = IppmsUtils.getPaycheckTableName(businessCertificate);
        try
        {

            String _wSql = "select max(ev.runYear) from "+tableName+"  ev " +
                    "where ev.employee.id = :pEmpIdVar  " ;
            Query<Integer> wQuery = this.sessionFactory.getCurrentSession().createQuery(_wSql);
            wQuery.setParameter("pEmpIdVar", pEmpId);


            Integer _wRetVal = wQuery.uniqueResult();

            if(_wRetVal == null || _wRetVal == 0)
                return wRetVal;

            Integer wRunYear = _wRetVal;

            String wSql = "select e.id  from "+tableName+" e " +
                    "where e.runYear = :pYearVar " +
                    "and e.employee.id = :pEmpIdVar order by e.runMonth desc ";

            Query<Long> _wQuery =  this.sessionFactory.getCurrentSession().createQuery(wSql);
            _wQuery.setParameter("pYearVar", wRunYear);
            _wQuery.setParameter("pEmpIdVar", pEmpId);

            List<Long> wList = _wQuery.getResultList();
            if(wList != null && !wList.isEmpty())
                  wRetVal = wList.get(0);

            return wRetVal;

        }
        catch (Exception nPEx) {
            return wRetVal;
        }

    }

    @Override
    public LocalDate getPendingPaycheckRunMonthAndYear(BusinessCertificate businessCertificate) {
        LocalDate localDate = null;
        String wHql = "select distinct(e.runMonth),e.runYear from "+IppmsUtils.getPaycheckTableName(businessCertificate)+" e where e.status = :pPendingStatus and e.businessClientId = :pBizId";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pPendingStatus", "P");
        wQuery.setParameter("pBizId", businessCertificate.getBusinessClientInstId());
        List<Object[]> wRetList = wQuery.list();
        if(wRetList == null || wRetList.isEmpty())
            return null;
        Integer wRunMonth = 0;
        Integer wRunYear = 0;
        for(Object[] o : wRetList){
            wRunMonth = (Integer)o[0];
            wRunYear = (Integer)o[1];
        }
        try{
            localDate = LocalDate.of(wRunYear, wRunMonth,1);
        }catch(Exception wEx){
            localDate = null;
            //eat it.
        }
        return localDate;
    }

    @Override
    @Transactional()
    public void updPendPayDedValues(EmpDeductionType pEHB, int pRunMonth, int pRunYear, Object[] pIntValues, BusinessCertificate businessCertificate)
    {
        String hqlQuery = "update "+IppmsUtils.getPaycheckDeductionTableName(businessCertificate)+" s SET s.sortCode = :pSortCodeVar , s.accountNumber = :pAcctNoVar " +
                "where s.empDedInfo.id in (:pDedInfoId) and s.runMonth = :pRunMonthVar and s.runYear = :pRunYearVar ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pSortCodeVar", pEHB.getBankBranches().getBranchSortCode());
        query.setParameter("pAcctNoVar", pEHB.getAccountNumber());
        query.setParameterList("pDedInfoId", pIntValues);
        query.setParameter("pRunMonthVar", pRunMonth);
        query.setParameter("pRunYearVar", pRunYear);
        query.executeUpdate();

    }

    @Override
    public List<NamedEntity> makePaycheckYearList(BusinessCertificate businessCertificate)
    {
        String wHql = "select distinct(p.runYear) from "+IppmsUtils.getPaycheckTableName(businessCertificate)+" p order by p.runYear desc";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        ArrayList<Integer> wRetList = (ArrayList<Integer>)wQuery.list();
        List<NamedEntity> wRetVal = new ArrayList<>();
        // int count;
        if (wRetList.size() > 0) {
            //count = 0;
            for (Integer o : wRetList) {
                NamedEntity e = new NamedEntity();
                e.setId(new Long(o));
                e.setName(String.valueOf(o));
                /*count++; e.setId(Integer.valueOf(count));*/
                wRetVal.add(e);
            }
        }
        Collections.sort(wRetVal);
        return wRetVal;
    }

    @Override
    @Transactional()
    public synchronized void updateMdaForPendingPaychecks(AbstractEmployeeEntity wEmp, LocalDate wCal, Long wSchoolInstId, BusinessCertificate bc) {
//        Transaction tx;
//        tx = this.sessionFactory.getCurrentSession().beginTransaction();
        String wHql = "update "+IppmsUtils.getPaycheckTableName(bc)+" p set p.mdaDeptMap.id = :pMdaMapIdVar ";
        if(IppmsUtils.isNotNullAndGreaterThanZero(wSchoolInstId))
            wHql += ", p.schoolInfo.id = :pSchoolInfoIdVar ";
        wHql += " where p.employee.id = :pEmpIdVar and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar and p.status = 'P' and p.businessClientId = :pBizIdVar ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pMdaMapIdVar", wEmp.getMdaDeptMap().getId());
        if(IppmsUtils.isNotNullAndGreaterThanZero(wSchoolInstId))
            wQuery.setParameter("pSchoolInfoIdVar", wSchoolInstId);
        wQuery.setParameter("pEmpIdVar", wEmp.getId());
        wQuery.setParameter("pRunMonthVar", wCal.getMonthValue());
        wQuery.setParameter("pRunYearVar", wCal.getYear());
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        wQuery.executeUpdate();
//        tx.commit();
    }

    public BankPVSummaryBean loadEmployeePayBeanByFromDateToDateAndBank(LocalDate pSomeDate, BusinessCertificate bc)
    {
        BankPVSummaryBean wRetMap = new BankPVSummaryBean();
        String wSql = "select e.employee.id,e.totalPay,e.totalDeductions,bb.id,bb.name," +
                "b.id, b.name from "+IppmsUtils.getPaycheckTableName(bc)+" e, BankBranch bb, BankInfo b where " +
                "e.runMonth = :pRunMonth and e.runYear = :pRunYear and " +
                "bb.id = e.bankBranch.id and bb.bankInfo.id = b.id  and e.netPay > 0 order by e.id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(wSql);
        query.setParameter("pRunMonth", pSomeDate.getMonthValue());
        query.setParameter("pRunYear", pSomeDate.getYear());
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();
        if (wRetVal.size() > 0)
        {
            HashMap wBranchMap = new HashMap();
            HashMap wBankInfoMap = new HashMap();
            for (Object[] o : wRetVal)
            {
                double wTotalPay = ((Double)o[1]);
                double wTotalDeduction = ((Double)o[2]);
                double wNetPay = wTotalPay - wTotalDeduction;
                Long wBranchId = (Long)o[3];
                Long wBankId = (Long)o[5];
                String wBranchName = (String)o[4];
                String wBankName = (String)o[6];
                wRetMap.setNetPay(wRetMap.getNetPay() + wNetPay);
                if (wBranchMap.containsKey(wBranchId)) {
                    BankBranch wBB = (BankBranch)wBranchMap.get(wBranchId);
                    wBB.setBankId(wBankId);
                    wBB.setTotalNetPay(wBB.getTotalNetPay() + wNetPay);
                    wBranchMap.put(wBranchId, wBB);
                }
                else {
                    BankBranch wBB = new BankBranch(wBranchId, wBranchName);
                    wBB.setBankId(wBankId);
                    wBB.setTotalNetPay(wBB.getTotalNetPay() + wNetPay);
                    wBranchMap.put(wBranchId, wBB);
                }

                if (wBankInfoMap.containsKey(wBankId)) {
                    BankInfo wBB = (BankInfo)wBankInfoMap.get(wBankId);
                    wBB.setTotalNetPay(wBB.getTotalNetPay() + wNetPay);
                    wBankInfoMap.put(wBankId, wBB);
                }
                else {
                    BankInfo wBB = new BankInfo(wBankId, wBankName);
                    wBB.setTotalNetPay(wBB.getTotalNetPay() + wNetPay);
                    wBankInfoMap.put(wBankId, wBB);
                }
            }
            wRetMap.setBankBranchMap(wBranchMap);
            wRetMap.setBankInfoMap(wBankInfoMap);
        }
        return wRetMap;
    }


}
