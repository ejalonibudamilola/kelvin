/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionCategory;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("deductionService")
@Repository
@Transactional(readOnly = true)
public class DeductionService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<AbstractDeductionEntity> loadToBePaidEmployeeDeductions(BusinessCertificate businessCertificate, LocalDate pStartDate, LocalDate pEndDate, boolean pRerun)
    {
        List wRetList = new ArrayList();

        ArrayList<Object[]> wRetVal;
        String hqlQuery = "";
        if(businessCertificate.isPensioner()){
            if(pRerun){
                hqlQuery = "select e.id,e.amount,et.accountNumber,p.id,p.name,k.id,b.branchSortCode, ec.statutoryInd,et.taxable,e.startDate,e.endDate,et.id,et.dateDependent,p.percentageInd,et.unionDueInd "
                        + "from "+IppmsUtils.getDeductionInfoTableName(businessCertificate)+" e,HiringInfo h,PayTypes p,Pensioner k,BankBranch b,EmpDeductionType et,EmpDeductionCategory ec, PayrollRerun pr "
                        + "where e.pensioner.id = k.id and k.id = h.pensioner.id and b.id = et.bankBranches.id and e.empDeductionType.id = et.id "
                        + "and ec.id = et.empDeductionCategory.id and e.payTypes.id = p.id and (h.pensionEndFlag = 0 or (h.pensionEndDate >= :pStartDate  and h.pensionEndDate <= :pEndDate)) "
                        + "and e.amount > 0 and h.id = pr.hiringInfo.id and k.businessClientId = :pBizClientIdVar";
            }else{
                hqlQuery = "select e.id,e.amount,et.accountNumber,p.id,p.name,k.id,b.branchSortCode, ec.statutoryInd,et.taxable,e.startDate,e.endDate,et.id,et.dateDependent,p.percentageInd,et.unionDueInd "
                        + "from "+IppmsUtils.getDeductionInfoTableName(businessCertificate)+" e,HiringInfo h,PayTypes p,Pensioner k,BankBranch b,EmpDeductionType et,EmpDeductionCategory ec "
                        + "where e.pensioner.id = k.id and k.id = h.pensioner.id and b.id = et.bankBranches.id and e.empDeductionType.id = et.id "
                        + "and ec.id = et.empDeductionCategory.id and e.payTypes.id = p.id and (h.pensionEndFlag = 0 or (h.pensionEndDate >= :pStartDate  and h.pensionEndDate <= :pEndDate)) "
                        + "and e.amount > 0 and k.businessClientId = :pBizClientIdVar";
            }


        }else{
            if(pRerun){
                hqlQuery = "select e.id,e.amount,et.accountNumber,p.id,p.name,k.id," +
                        "b.branchSortCode, ec.statutoryInd,et.taxable,e.startDate,e.endDate, et.id, et.dateDependent,p.percentageInd ,et.unionDueInd" +
                        "from "+IppmsUtils.getDeductionInfoTableName(businessCertificate)+" e,HiringInfo h,PayTypes p,Employee k,BankBranch b,EmpDeductionType et," +
                        "EmpDeductionCategory ec, PayrollRerun pr where e.employee.id = k.id and k.id = h.employee.id " +
                        "and b.id = et.bankBranches.id and e.empDeductionType.id = et.id and ec.id = et.empDeductionCategory.id " +
                        "and e.payTypes.id = p.id and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate  " +
                        "and h.terminateDate <= :pEndDate)) and e.amount > 0 and h.id = pr.hiringInfo.id and k.businessClientId = :pBizClientIdVar ";
            }else {
                hqlQuery = "select e.id,e.amount,et.accountNumber,p.id,p.name,k.id," +
                        "b.branchSortCode, ec.statutoryInd,et.taxable,e.startDate,e.endDate, et.id, et.dateDependent,p.percentageInd,et.unionDueInd " +
                        "from "+IppmsUtils.getDeductionInfoTableName(businessCertificate)+" e,HiringInfo h,PayTypes p,Employee k,BankBranch b,EmpDeductionType et," +
                        "EmpDeductionCategory ec where e.employee.id = k.id and k.id = h.employee.id " +
                        "and b.id = et.bankBranches.id and e.empDeductionType.id = et.id and ec.id = et.empDeductionCategory.id " +
                        "and e.payTypes.id = p.id and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate  " +
                        "and h.terminateDate <= :pEndDate)) and e.amount > 0 and k.businessClientId = :pBizClientIdVar ";
            }
        }


        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pStartDate", pStartDate);
        query.setParameter("pEndDate", pEndDate);
        query.setParameter("pBizClientIdVar",businessCertificate.getBusinessClientInstId());
        wRetVal = (ArrayList)query.list();
        PayTypes p;
        EmpDeductionType wEDT;
        if (wRetVal.size() > 0)
        {
            AbstractDeductionEntity e;
            for (Object[] o : wRetVal) {
                e = IppmsUtils.makeDeductionInfoObject(businessCertificate);

                e.setId((Long)o[0]);
                e.setAmount(((Double)o[1]));
                e.setAccountNumber((String)o[2]);
                p = new PayTypes();
                p.setId((Long)o[3]);
                p.setName((String)o[4]);
                e.makeParentObject((Long)o[5]);
               // e.setEmployee(new Employee((Long)o[5]));
               // e.setParentId((Long)o[5]);


                e.setSortCode((String)o[6]);

                e.setStatutoryInd(((Integer)o[7]).intValue());
                e.setTaxExemptInd((String)o[8]);
                e.setPayTypes(p);
                if(o[9] != null){
                    e.setStartDate((LocalDate)o[9]);
                }
                if(o[10] != null){
                    e.setEndDate((LocalDate)o[10]);
                }
                wEDT = new EmpDeductionType((Long)o[11], (Integer)o[12]);
                e.getPayTypes().setPercentageInd((Integer)o[13]);
                wEDT.setUnionDueInd((Integer)o[14]);

                e.setEmpDeductionType(wEDT);
                wRetList.add(e);
            }

        }

        return wRetList;
    }

    public List<PaycheckDeduction> loadEmpDeductionsByParentIdAndPayPeriod(Long pDedTypeId, int pRunMonth, int pRunYear,
                                                                               BusinessCertificate bc) {
        List<PaycheckDeduction> l = new ArrayList<>();
        ArrayList<Object[]> wRetVal;

        String hqlQuery = "select emp.id, emp.firstName,emp.lastName,emp.employeeId,p.id," +
                "p.amount,edc.id, edc.name, edi.id, edt.id, edt.name, edt.description, edt.firstAllotAmt, edt.firstAllotment," +
                " edt.secAllotAmt, edt.secAllotment, edt.thirdAllotAmt, edt.thirdAllotment "+
                "from " + IppmsUtils.getPaycheckDeductionTableName(bc) + " p, " +
                "" + IppmsUtils.getEmployeeTableName(bc) + " emp, BankBranch bb, " +
                "EmpDeductionCategory edc, "+IppmsUtils.getDeductionInfoTableName(bc)+" edi, EmpDeductionType edt " +
                "where emp.id = p.employee.id  "+
                "and p.sortCode = bb.branchSortCode " +
                "and p.empDedInfo.id = edi.id "+
                "and edi.empDeductionType.id = edt.id " +
                "and edt.empDeductionCategory.id = edc.id "+
                "and edc.apportionedInd = 1 " +
                "and p.runMonth = :pRunMonth and p.runYear = :pRunYear ";

        if (pDedTypeId != null && pDedTypeId > 0) {
            hqlQuery += "and edt.id = :pGarnTypeIdVal";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);
        if (pDedTypeId != null && pDedTypeId > 0) {
            query.setParameter("pGarnTypeIdVal", pDedTypeId);
        }

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            AbstractDeductionEntity edi;
            Employee e;
            PaycheckDeduction p;
            EmpDeductionCategory edc;
            EmpDeductionType edt;

            for (Object[] o : wRetVal) {
                e = new Employee((Long) o[0], (String) o[1], (String) o[2]);
                e.setEmployeeId((String) o[3]);
                p = new PaycheckDeduction((Long) o[4]);
                p.setAmount(((Double) o[5]));
                edc = new EmpDeductionCategory((Long) o[6]);
                edc.setName((String) o[7]);
                edi = IppmsUtils.makeDeductionInfoObject(bc);
                edi.setId((Long) o[8]);
                edt = new EmpDeductionType((Long) o[9]);
                edt.setName((String) o[10]);
                edt.setDescription((String) o[11]);
                edt.setFirstAllotAmt((Long) o[12]);
                edt.setFirstAllotAmtStr((String) o[13]);
                edt.setSecAllotAmt((Long) o[14]);
                edt.setSecAllotAmtStr((String) o[15]);
                edt.setThirdAllotAmt((Long) o[16]);
                edt.setThirdAllotAmtStr((String) o[17]);

                p.setEmpDeductionCategory(edc);
                p.setEmpDeductionType(edt);
                p.setEmployee(e);
                p.setEmpDedInfo(edi);
                l.add(p);
            }
        }

        return l;
    }

    public int getNoOfEmployeesWithLoanDeductions(Long pGarnTypeId, int pRunMonth, int pRunYear, BusinessCertificate bc) {
        int wRetVal = 0;
        String wHql = "select count(distinct p.employee.id)  " +
                "from " + IppmsUtils.getPaycheckGarnishmentTableName(bc) + " p, " + IppmsUtils.getPaycheckTableName(bc) + " emp,"+IppmsUtils.getGarnishmentInfoTableName(bc)+" edi,EmpGarnishmentType edt" +
                " where emp.runMonth = :pRunMonthVal and emp.runYear = :pRunYearVal" +
                " and emp.id = p.employeePayBean.id and p.empGarnInfo.id = edi.id and edi.empGarnishmentType.id = edt.id";

        if (pGarnTypeId != null && pGarnTypeId > 0) {
            wHql += " and edt.id = :pGarnTypeIdVal";
        }
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonthVal", pRunMonth);
        wQuery.setParameter("pRunYearVal", pRunYear);
        if (pGarnTypeId != null && pGarnTypeId > 0) {
            wQuery.setParameter("pGarnTypeIdVal", pGarnTypeId);
        }

        List list = wQuery.list();

        if (list != null) {
            wRetVal = Integer.parseInt(String.valueOf(list.get(0)));

        }
        return wRetVal;
    }

    public double getTotalDeductions(Long pDedTypeId, int pRunMonth, int pRunYear, BusinessCertificate bc) {
        String wHql = "select coalesce(sum(p.amount),0)  " +
                "from " + IppmsUtils.getPaycheckDeductionTableName(bc) + " p, " + IppmsUtils.getEmployeeTableName(bc) + " emp," +
                " EmpDeductionCategory edc, "+IppmsUtils.getDeductionInfoTableName(bc)+" edi, EmpDeductionType edt " +
                " where p.runMonth = :pRunMonthVal and p.runYear = :pRunYearVal" +
                " and emp.id = p.employee.id and p.empDedInfo.id = edi.id " +
                " and edi.empDeductionType.id = edt.id " +
                " and edt.empDeductionCategory.id = edc.id "+
                " and edc.apportionedInd = 1 ";

        if (pDedTypeId != null && pDedTypeId > 0) {
            wHql += " and edt.id = :pDedTypeIdVal";
        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonthVal", pRunMonth);
        wQuery.setParameter("pRunYearVal", pRunYear);
        if (IppmsUtils.isNotNullAndGreaterThanZero(pDedTypeId)) {
            wQuery.setParameter("pDedTypeIdVal", pDedTypeId);
        }


        List list = wQuery.list();

        if (list != null && !list.isEmpty() && list.get(0) != null) {
            return (Double) list.get(0);
        } else {
            return 0.0D;
        }
    }

    public List<EmpDeductionType> findEmpDeductionsByBusinessClient(Long pBusClientId) {
        List wRetList = new ArrayList();
        String hqlQuery = "select edt.id, edt.name, edt.description from EmpDeductionCategory edc, " +
                " EmpDeductionType edt, BusinessClient b "
                + "where edt.empDeductionCategory.id = edc.id " +
                "  and edc.apportionedInd = 1  and edt.businessClientId = b.id group by edt.id, edt.name, edt.description";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                EmpDeductionType e = new EmpDeductionType();
                e.setId((Long) o[0]);
                e.setName((String) o[1]);
                e.setDescription((String) o[2]);
                wRetList.add(e);
            }
        }

        return wRetList;
    }

    public int getTotalNoOfToBePaidDeduction (BusinessCertificate bc, LocalDate pStartDate, LocalDate pEndDate){
        int wRetVal = 0;

        String hqlQuery;

        if(bc.isPensioner()){
           hqlQuery = "select count(e.id) from "+IppmsUtils.getDeductionInfoTableName(bc)+" e,HiringInfo h,PayTypes p,Pensioner k,BankBranch b,EmpDeductionType et,EmpDeductionCategory ec "
                        + "where e.pensioner.id = k.id and k.id = h.pensioner.id and b.id = et.bankBranches.id and e.empDeductionType.id = et.id "
                        + "and ec.id = et.empDeductionCategory.id and e.payTypes.id = p.id and (h.pensionEndFlag = 0 or (h.pensionEndDate >= :pStartDate  and h.pensionEndDate <= :pEndDate)) "
                        + "and e.amount > 0 and k.businessClientId = :pBizClientIdVar";
        }else{
            hqlQuery = "select count(e.id) from "+IppmsUtils.getDeductionInfoTableName(bc)+" e,HiringInfo h,PayTypes p,Employee k,BankBranch b,EmpDeductionType et," +
                        "EmpDeductionCategory ec where e.employee.id = k.id and k.id = h.employee.id " +
                        "and b.id = et.bankBranches.id and e.empDeductionType.id = et.id and ec.id = et.empDeductionCategory.id " +
                        "and e.payTypes.id = p.id and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate  " +
                        "and h.terminateDate <= :pEndDate)) and e.amount > 0 and k.businessClientId = :pBizClientIdVar ";
        }


        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pStartDate", pStartDate);
        query.setParameter("pEndDate", pEndDate);
        query.setParameter("pBizClientIdVar",bc.getBusinessClientInstId());
        List<Long> results = query.list();
        if ((results != null) && (!results.isEmpty())) {
            wRetVal = results.get(0).intValue();
        }

        return wRetVal;
    }


    public List<AbstractPaycheckDeductionEntity> getDeductionHistory(BusinessCertificate bc, Long pEmpId, Long pDedId) {
        List wRetList = new ArrayList();
        String hqlQuery = "select p.runMonth,p.runYear,p.payDate, p.amount from "+IppmsUtils.getPaycheckDeductionTableName(bc)+
                " p where p.empDedInfo.id = "+pDedId +
                "  and p.employee.id = "+pEmpId;

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();

        if (wRetVal.size() > 0) {
            AbstractPaycheckDeductionEntity abstractPaycheckDeductionEntity;
            for (Object[] o : wRetVal) {
                abstractPaycheckDeductionEntity = IppmsUtils.makePaycheckDeductionObject(bc);
                abstractPaycheckDeductionEntity.setRunMonth((Integer) o[0]);
                abstractPaycheckDeductionEntity.setRunYear((Integer) o[1]);
                abstractPaycheckDeductionEntity.setPayDate((LocalDate) o[2]);
                abstractPaycheckDeductionEntity.setAmount((Double) o[3]);
                wRetList.add(abstractPaycheckDeductionEntity);
            }
        }

        return wRetList;
    }
}
