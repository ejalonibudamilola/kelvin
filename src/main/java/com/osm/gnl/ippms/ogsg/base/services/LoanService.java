/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("loanService")
@Repository
@Transactional(readOnly = true)
public class LoanService {
    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<AbstractGarnishmentEntity> loadToBePaidEmployeeGarnishments(BusinessCertificate businessCertificate, LocalDate pStartDate, boolean pRerun)
    {
        List wRetList = new ArrayList();

        ArrayList<Object[]> wRetVal;
        String hqlQuery = "";
        if(businessCertificate.isPensioner()){
            if(pRerun){
                hqlQuery = "select e.id,e.amount,e.garnishCap,e.owedAmount,k.id,l.id,b.branchSortCode," +
                        "coalesce(l.accountNumber,''),e.interestAmount,e.deductInterestSeparatelyInd,e.currentLoanTerm,e.originalLoanAmount,e.startDate " +
                        " from "+IppmsUtils.getGarnishmentInfoTableName(businessCertificate)+" e,HiringInfo h,Pensioner k,EmpGarnishmentType l,BankBranch b, BusinessClient bc, PayrollRerun pr " +
                        "where e.pensioner.id = k.id and k.id = h.pensioner.id and l.id = e.empGarnishmentType.id and k.businessClientId = bc.id " +
                        "and b.id = l.bankBranch.id and e.owedAmount > 0 and e.amount > 0 and bc.id = :pBizIdVar and h.id = pr.hiringInfo.id ";
            }else{
                hqlQuery = "select e.id,e.amount,e.garnishCap,e.owedAmount,k.id,l.id,b.branchSortCode," +
                        "coalesce(l.accountNumber,''),e.interestAmount,e.deductInterestSeparatelyInd,e.currentLoanTerm,e.originalLoanAmount,e.startDate " +
                        " from "+IppmsUtils.getGarnishmentInfoTableName(businessCertificate)+" e,HiringInfo h,Pensioner k,EmpGarnishmentType l,BankBranch b, BusinessClient bc " +
                        "where e.pensioner.id = k.id and k.id = h.pensioner.id and l.id = e.empGarnishmentType.id and k.businessClientId = bc.id " +
                        "and b.id = l.bankBranch.id and e.owedAmount > 0 and e.amount > 0 and bc.id = :pBizIdVar";
            }

        }else{
            if(pRerun){
                hqlQuery = "select e.id,e.amount,e.garnishCap,e.owedAmount,k.id,l.id,b.branchSortCode," +
                        "coalesce(l.accountNumber,''),e.interestAmount,e.deductInterestSeparatelyInd,e.currentLoanTerm,e.originalLoanAmount,e.startDate " +
                        " from "+IppmsUtils.getGarnishmentInfoTableName(businessCertificate)+" e,HiringInfo h,Employee k,EmpGarnishmentType l,BankBranch b, BusinessClient bc, PayrollRerun pr " +
                        "where e.employee.id = k.id and k.id = h.employee.id and l.id = e.empGarnishmentType.id and k.businessClientId = bc.id " +
                        "and b.id = l.bankBranch.id and e.owedAmount > 0 and e.amount > 0 and bc.id = :pBizIdVar and h.id = pr.hiringInfo.id";
            }else{
                hqlQuery = "select e.id,e.amount,e.garnishCap,e.owedAmount,k.id,l.id,b.branchSortCode," +
                        "coalesce(l.accountNumber,''),e.interestAmount,e.deductInterestSeparatelyInd,e.currentLoanTerm,e.originalLoanAmount,e.startDate " +
                        " from "+IppmsUtils.getGarnishmentInfoTableName(businessCertificate)+" e,HiringInfo h,Employee k,EmpGarnishmentType l,BankBranch b, BusinessClient bc " +
                        "where e.employee.id = k.id and k.id = h.employee.id and l.id = e.empGarnishmentType.id and k.businessClientId = bc.id " +
                        "and b.id = l.bankBranch.id and e.owedAmount > 0 and e.amount > 0 and bc.id = :pBizIdVar";
            }

        }



        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId());

        wRetVal = (ArrayList)query.list();

        if (wRetVal.size() > 0)
        {
            LocalDate wStartDate;
            AbstractGarnishmentEntity e;

            for (Object[] o : wRetVal) {
                  wStartDate  = (LocalDate)o[12];
                if(wStartDate == null)continue;

                if(wStartDate.compareTo(pStartDate) > 0) {
                    //Check if they are in the same month and Year..
                    if(!(wStartDate.getYear() == pStartDate.getYear() && wStartDate.getMonthValue() <= pStartDate.getMonthValue()))
                        continue;
                }

                 e = IppmsUtils.makeGarnishmentInfoObject(businessCertificate);
                e.setId((Long)o[0]);
                e.setAmount(((Double)o[1]));
                e.setGarnishCap(((Double)o[2]));
                e.setOwedAmount(((Double)o[3]));
                if(businessCertificate.isPensioner())
                    e.setPensioner(new Pensioner((Long)o[4]));
                else
                    e.setEmployee(new Employee((Long)o[4]));

                EmpGarnishmentType et = new EmpGarnishmentType((Long)o[5]);
                e.setSortCode((String)o[6]);
                e.setAccountNumber((String)o[7]);
                e.setInterestAmount(((Double)o[8]));
                e.setDeductInterestSeparatelyInd((Integer)o[9]);
                e.setCurrentLoanTerm(((Integer)o[10]));
                e.setOriginalLoanAmount(((Double)o[11]));

                e.setEmpGarnishmentType(et);
                wRetList.add(e);
            }

        }

        return wRetList;
    }


    public List<AbstractGarnishmentEntity> loadAllPendingPaycheckLoans(BusinessCertificate bc, int pRunMonth, int pRunYear) {

        List<AbstractGarnishmentEntity> wRetList = new ArrayList<>();

        String wHql = "select e.id,e.garnishCap,e.amount,e.owedAmount,e.description" +
                ",e.originalLoanAmount,e.employee.id,e.interestAmount,e.loanTerm" +
                ",e.currentLoanTerm,e.deductInterestSeparatelyInd,e.govtLoan,e.lastModBy.id," +
                "e.lastModTs,e.startDate,e.endDate,egt.id,pg.amount,epb.businessClientId,e.createdBy.id" +
                " from "+IppmsUtils.getGarnishmentInfoTableName(bc)+" e,EmpGarnishmentType egt, "+ IppmsUtils.getPaycheckGarnishmentTableName(bc) +" pg,"+IppmsUtils.getPaycheckTableName(bc)+" epb " +
                " where e.id = pg.empGarnInfo.id and pg.employeePayBean.id = epb.id and epb.status = 'P' and" +
                " pg.employee.id = e.employee.id and pg.employee.id = epb.employee.id " +
                " and e.empGarnishmentType.id = egt.id and epb.runMonth = :pRunMonth and epb.runYear = :pRunYear ";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            int i = 0;
            AbstractGarnishmentEntity e;
            for (Object[] o : wRetVal) {

               e = IppmsUtils.makeGarnishmentInfoObject(bc);

                e.setId((Long)o[i++]);
                e.setGarnishCap((Double)o[i++]);
                e.setAmount((Double)o[i++]);
                e.setOwedAmount((Double)o[i++]);
                e.setDescription((String)o[i++]);
                e.setOriginalLoanAmount((Double)o[i++]);
                e.setEmployee(new Employee((Long)o[i++]));
                e.setInterestAmount((Double)o[i++]);
                e.setLoanTerm((Integer)o[i++]);
                e.setCurrentLoanTerm((Integer)o[i++]);
                e.setDeductInterestSeparatelyInd((Integer)o[i++]);
                e.setGovtLoan((Integer)o[i++]);
                e.setLastModBy(new User((Long)o[i++]));
                e.setLastModTs((Timestamp) o[i++]);
                e.setStartDate((LocalDate) o[i++]);
                Object wEndDate = o[i++];
                if(wEndDate != null){
                    e.setEndDate((LocalDate)wEndDate);
                }else{
                    e.setEndDate(PayrollUtils.makeEndDate(e.getStartDate(), e.getLoanTerm()));
                }
                e.setEmpGarnishmentType(new EmpGarnishmentType((Long)o[i++]));
                e.setActGarnAmt((Double)o[i++]);
                e.setBusinessClientId((Long)o[i++]);
                e.setRunTrigInd(1);
                e.setCreatedBy(new User((Long)o[i++]));

                wRetList.add(e);
                i = 0;
            }

        }

        return wRetList;

    }


    public double getTotalGarnishments(BusinessCertificate bc,Long pGarnId, Long pEmpId)
    {
        String wHql = "select coalesce(sum(p.amount),0)  " +
                "from "+IppmsUtils.getPaycheckGarnishmentTableName(bc)+" p where p.empGarnInfo.id = :pGarnInfoId  " +
                " and p.employee.id = :pEmpIdValue";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pGarnInfoId", pGarnId);
        wQuery.setParameter("pEmpIdValue", pEmpId);


        List  list = wQuery.list();

        if (list != null  ) {
            return (Double) list.get(0);
        }else{
            return 0.0D;
        }


    }

    public int getTotalNoOfToBePaidLoan(BusinessCertificate bc){
        int wRetVal = 0;
        String hqlQuery;
        if(bc.isPensioner()){
            hqlQuery = "select count(e.id) from "+IppmsUtils.getGarnishmentInfoTableName(bc)+" e,HiringInfo h,Pensioner k,EmpGarnishmentType l,BankBranch b, BusinessClient bc " +
                        "where e.pensioner.id = k.id and k.id = h.pensioner.id and l.id = e.empGarnishmentType.id and k.businessClientId = bc.id " +
                        "and b.id = l.bankBranch.id and e.owedAmount > 0 and e.amount > 0 and bc.id = :pBizIdVar";
        }else{
            hqlQuery = "select count(e.id) from "+IppmsUtils.getGarnishmentInfoTableName(bc)+" e,HiringInfo h,Employee k,EmpGarnishmentType l,BankBranch b, BusinessClient bc " +
                        "where e.employee.id = k.id and k.id = h.employee.id and l.id = e.empGarnishmentType.id and k.businessClientId = bc.id " +
                        "and b.id = l.bankBranch.id and e.owedAmount > 0 and e.amount > 0 and bc.id = :pBizIdVar";
        }



        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());


        List<Long> results = query.list();
        if ((results != null) && (!results.isEmpty())) {
            wRetVal = results.get(0).intValue();
        }

        return wRetVal;

    }

    public List<AbstractPaycheckGarnishmentEntity> loadAllPaychecksGarnishments(BusinessCertificate bc, Long pEmpId, Long pGarnId) {
        List<AbstractPaycheckGarnishmentEntity> paycheckGarnishmentEntityList = new ArrayList<>();
        String hqlQuery;
        if(bc.isPensioner()){
            hqlQuery = "select e.payDate,e.payPeriodStart,e.payPeriodEnd,e.amount,e.startingLoanBalance from "+IppmsUtils.getPaycheckGarnishmentTableName(bc)+" e " +
                    "where  e.empGarnInfo.id = :pGarnInfoIdVar and e.pensioner.id = :pEmpIdVar";
        }else{
            hqlQuery = "select e.payDate,e.payPeriodStart,e.payPeriodEnd,e.amount,e.startingLoanBalance from "+IppmsUtils.getPaycheckGarnishmentTableName(bc)+" e " +
                    "where  e.empGarnInfo.id = :pGarnInfoIdVar and e.employee.id = :pEmpIdVar";
        }



        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pGarnInfoIdVar",pGarnId);
        query.setParameter("pEmpIdVar",pEmpId);


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
              AbstractPaycheckGarnishmentEntity garnishmentEntity;
              int i = 0;
             for(Object[] o : wRetVal){
                 garnishmentEntity = IppmsUtils.makePaycheckGarnishmentObject(bc);
                 garnishmentEntity.setPayDate((LocalDate)o[i++]);
                 garnishmentEntity.setPayPeriodStart((LocalDate)o[i++]);
                 garnishmentEntity.setPayPeriodEnd((LocalDate)o[i++]);
                 garnishmentEntity.setAmount((Double)o[i++]);
                 garnishmentEntity.setStartingLoanBalance((Double)o[i++]);
                 paycheckGarnishmentEntityList.add(garnishmentEntity);
                 i = 0;
             }
        }

        return paycheckGarnishmentEntityList;
    }
}
