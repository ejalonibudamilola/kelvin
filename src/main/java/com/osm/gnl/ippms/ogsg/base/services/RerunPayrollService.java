/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("rerunPayrollService")
@Repository
@Transactional(readOnly = true)
@Slf4j
public class RerunPayrollService {


    private final SessionFactory sessionFactory;


    @Autowired
    public RerunPayrollService(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private Query makeQuery(String wHql) {
        return sessionFactory.getCurrentSession().createQuery(wHql);
    }

    public HashMap<Long, Long> setPendingPaychecksToEmpMap(BusinessCertificate businessCertificate, Long mdaInfoId, int runMonth, int runYear) {
        String wHql = "select p.employee.id, p.id from "+ IppmsUtils.getPaycheckTableName(businessCertificate)+
        " p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, MdaDeptMap mdm, MdaInfo m "
                + "where p.runMonth = :pRM "
                + "and p.runYear = :pRY and e.id = p.employee.id"
                + " and p.status = :pPendingStatus and m.id = mdm.mdaInfo.id and mdm.id = e.mdaDeptMap.id and m.id = :pMdaInfoIdVar and e.businessClientId = :pBizClientVar";


        HashMap<Long, Long> wRetMap = new HashMap<Long, Long>();
        Query wQuery = makeQuery(wHql);
        wQuery.setParameter("pRM", runMonth);
        wQuery.setParameter("pRY", runYear);
        wQuery.setParameter("pPendingStatus", "P");
        wQuery.setParameter("pMdaInfoIdVar", mdaInfoId);
        wQuery.setParameter("pBizClientVar", businessCertificate.getBusinessClientInstId());
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        for (Object[] o : wRetVal) {
                wRetMap.put((Long)o[0], (Long)o[1]);
        }

        return wRetMap;

    }
    public HashMap<Long, Long> setEmpToPendingGratMap(BusinessCertificate businessCertificate,Long mdaInfoId, int runMonth, int runYear) {
        String wHql = "select p.employee.id, p.id from PaycheckGratuity p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, MdaDeptMap mdm, MdaInfo m "
                + "where p.runMonth = :pRM "
                + "and p.runYear = :pRY and e.id = p.employee.id"
                + " and p.status = :pPendingStatus and m.id = mdm.mdaInfo.id and mdm.id = e.mdaDeptMap.id and m.id = :pMdaInfoIdVar and e.businessClientId = :pBizClientVar";


        HashMap<Long, Long> wRetMap = new HashMap<Long, Long>();
        Query wQuery = makeQuery(wHql);
        wQuery.setParameter("pRM", runMonth);
        wQuery.setParameter("pRY", runYear);
        wQuery.setParameter("pPendingStatus", "P");
        wQuery.setParameter("pMdaInfoIdVar", mdaInfoId);
        wQuery.setParameter("pBizClientVar", businessCertificate.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        for (Object[] o : wRetVal) {
            wRetMap.put((Long)o[0], (Long)o[1]);
        }

        return wRetMap;

    }

    public HashMap<Long, HashMap<Long, Long>> setPendingPaycheckLoanInfo(BusinessCertificate businessCertificate, Long mdaInfoId, int runMonth, int runYear) {

        String wHql = "select p.employee.id, p.empGarnInfo.id,p.id from "+IppmsUtils.getPaycheckGarnishmentTableName(businessCertificate)+" p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+
                " e, "+IppmsUtils.getPaycheckTableName(businessCertificate)+" epb, MdaDeptMap mdm, MdaInfo m  where mdm.mdaInfo.id = m.id and epb.mdaDeptMap.id = mdm.id and p.runMonth = :pRM " +
                "and p.runYear = :pRY and e.id = p.employee.id and p.employeePayBean.id = epb.id and m.id = :pMdaInfoIdVar and e.businessClientId = :pBizClientVar " +
                "and epb.status = :pPendingStatus ";


        HashMap<Long, HashMap<Long,Long>> wRetMap = new HashMap<>();
        Query wQuery = this.makeQuery(wHql);
        wQuery.setParameter("pRM", runMonth);
        wQuery.setParameter("pRY", runYear);
        wQuery.setParameter("pPendingStatus", "P");
        wQuery.setParameter("pMdaInfoIdVar", mdaInfoId);
        wQuery.setParameter("pBizClientVar", businessCertificate.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        for(Object[] o : wRetVal){
            Long wEmpInstId = (Long)o[0];
            Long wGarnInfoInstId = (Long)o[1];
            Long wPayLoanId = (Long)o[2];


            HashMap<Long,Long> wInnerMap;
            if(wRetMap.containsKey(wEmpInstId)){
                wInnerMap = wRetMap.get(wEmpInstId);

            }else{
                wInnerMap = new HashMap<>();

            }

            wInnerMap.put(wGarnInfoInstId, wPayLoanId);
            wRetMap.put(wEmpInstId, wInnerMap);

        }


        return wRetMap;

    }

    public HashMap<Long, HashMap<Long, Long>> setPendingPaycheckDeductionInfo(BusinessCertificate businessCertificate, Long mdaInfoId, int runMonth, int runYear) {

        String wHql = "select p.employee.id, p.empDedInfo.id,p.id from "+IppmsUtils.getPaycheckDeductionTableName(businessCertificate)+" p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, "
                +IppmsUtils.getPaycheckTableName(businessCertificate)+ " epb, MdaDeptMap mdm, MdaInfo m where p.runMonth = :pRM and mdm.mdaInfo.id = m.id and epb.mdaDeptMap.id = mdm.id "
                + "and p.runYear = :pRY and e.id = p.employee.id and p.employeePayBean.id = epb.id and m.id = :pMdaInfoIdVar "
                + "and epb.status = 'P' and e.businessClientId = :pBizClientVar ";


        HashMap<Long, HashMap<Long,Long>> wRetMap = new HashMap<Long, HashMap<Long, Long>>();
        Query wQuery = this.makeQuery(wHql);
        wQuery.setParameter("pRM", runMonth);
        wQuery.setParameter("pRY", runYear);
        wQuery.setParameter("pMdaInfoIdVar", mdaInfoId);
        wQuery.setParameter("pBizClientVar", businessCertificate.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        for (Object[] o : wRetVal) {
            Long wEmpInstId = (Long) o[0];
            Long wGarnInfoInstId = (Long) o[1];
            Long wPayDedId = (Long) o[2];


            HashMap<Long, Long> wInnerMap;
            if (wRetMap.containsKey(wEmpInstId)) {
                wInnerMap = wRetMap.get(wEmpInstId);

            } else {
                wInnerMap = new HashMap<>();

            }
            wInnerMap.put(wGarnInfoInstId, wPayDedId);
            wRetMap.put(wEmpInstId, wInnerMap);

        }

        return wRetMap;

    }

    public HashMap<Long, HashMap<Long, Long>> setPendingPaycheckSpecialAllowance(BusinessCertificate businessCertificate, Long mdaInfoId, int runMonth, int runYear) {

        String wHql = "select p.employee.id, p.specialAllowanceInfo.id,p.id from "+IppmsUtils.getPaycheckSpecAllowTableName(businessCertificate)+" p, "+
                IppmsUtils.getEmployeeTableName(businessCertificate)+" e, "
                + ""+IppmsUtils.getPaycheckTableName(businessCertificate)+" epb, MdaDeptMap mdm, MdaInfo m where p.runMonth = :pRM "
                + "and p.runYear = :pRY and e.id = p.employee.id and p.employeePayBean.id = epb.id and epb.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id and m.id = :pMdaInfoIdVar "
                + "and epb.status = 'P' and e.businessClientId = :pBizClientVar";


        HashMap<Long, HashMap<Long,Long>> wRetMap = new HashMap<Long, HashMap<Long, Long>>();
        Query wQuery = this.makeQuery(wHql);
        wQuery.setParameter("pRM", runMonth);
        wQuery.setParameter("pRY", runYear);
        wQuery.setParameter("pMdaInfoIdVar", mdaInfoId);
        wQuery.setParameter("pBizClientVar", businessCertificate.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        for (Object[] o : wRetVal) {
            Long wEmpInstId = (Long) o[0];
            Long wGarnInfoInstId = (Long) o[1];
            Long wPayDedId = (Long) o[2];


            HashMap<Long, Long> wInnerMap;
            if (wRetMap.containsKey(wEmpInstId)) {
                wInnerMap = wRetMap.get(wEmpInstId);

            } else {
                wInnerMap = new HashMap<>();

            }
            wInnerMap.put(wGarnInfoInstId, wPayDedId);
            wRetMap.put(wEmpInstId, wInnerMap);

        }

        return wRetMap;

    }

    public List<HiringInfo> loadRerunHiringInfo(MdaInfo mdaInfo, BusinessCertificate businessCertificate, LocalDate pPayPeriodStart, LocalDate pPayPeriodEnd) {

        List<HiringInfo> wRetList = new ArrayList<>();

        String hqlQuery= "";
        if(businessCertificate.isPensioner()){
            hqlQuery = "select h.id, h.birthDate,h.hireDate,h.lastPayPeriod,h.currentPayPeriod,h.lastPayDate," +
                    "e.id, e.salaryInfo.id, e.firstName, e.lastName,h.ltgLastPaid,h.lastPromotionDate," +
                    "h.nextPromotionDate,a.id,h.suspended,h.staffInd,h.payRespAllowanceInd," +
                    "h.contractEndDate,h.contractStartDate, h.contractExpiredInd, " +
                    "h.pensionEndDate,et.id,et.politicalInd,p.bankBranches.id,p.accountNumber, " +
                    "h.amAliveDate,h.pfaInfo.id, h.pensionPinCode,e.payApprInstId,p.bvnNo,adm.id,h.monthlyPensionAmount,e.employeeId" +
                    " from HiringInfo h, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, PayrollRerun pr," +
                    "MdaInfo a,MdaDeptMap adm, EmployeeType et,PaymentMethodInfo p " +
                    "where e.id = h." + businessCertificate.getEmployeeIdJoinStr() + " and (h.terminateInactive = 'N' or h.staffInd = 1 or " +
                    "(h.pensionEndDate >= :pBeginDate and h.pensionEndDate <= :pEndDate)) " +
                    "and e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.employeeType.id =  et.id " +
                    "and p." + businessCertificate.getEmployeeIdJoinStr() + " = e.id and h.id = pr.hiringInfo.id and a.id = :pMdaInfoIdVar ";
        }else{
            hqlQuery = "select h.id, h.birthDate,h.hireDate,h.lastPayPeriod,h.currentPayPeriod,h.lastPayDate," +
                    "e.id, e.salaryInfo.id, e.firstName, e.lastName,h.ltgLastPaid,h.lastPromotionDate," +
                    "h.nextPromotionDate,a.id,h.suspended,h.staffInd,h.payRespAllowanceInd," +
                    "h.contractEndDate,h.contractStartDate, h.contractExpiredInd, " +
                    "h.terminateDate,et.id,et.politicalInd,p.bankBranches.id,p.accountNumber, " +
                    "e.schoolInfo.id,h.pfaInfo.id, h.pensionPinCode,e.payApprInstId,p.bvnNo,adm.id,e.employeeId,h.terminateReason.id " +
                    " from HiringInfo h, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, PayrollRerun pr," +
                    "MdaInfo a,MdaDeptMap adm, EmployeeType et,PaymentMethodInfo p " +
                    "where e.id = h." + businessCertificate.getEmployeeIdJoinStr() + " and (h.terminateInactive = 'N' or h.staffInd = 1 or " +
                    "(h.terminateDate >= :pBeginDate and h.terminateDate <= :pEndDate)) " +
                    "and e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.employeeType.id =  et.id " +
                    "and p." + businessCertificate.getEmployeeIdJoinStr() + " = e.id and h.id = pr.hiringInfo.id and a.id = :pMdaInfoIdVar ";
        }


        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);


        query.setParameter("pBeginDate", pPayPeriodStart);
        query.setParameter("pEndDate", pPayPeriodEnd);
        query.setParameter("pMdaInfoIdVar", mdaInfo.getId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();

        if (wRetVal.size() > 0) {
            AbstractEmployeeEntity e;
            HiringInfo h;
            for (Object[] o : wRetVal) {
                h = new HiringInfo();
                h.setId((Long) o[0]);
                h.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                h.setBirthDate((LocalDate) o[1]);
                h.setHireDate((LocalDate) o[2]);
                h.setLastPayPeriod((String) o[3]);
                h.setCurrentPayPeriod((String) o[4]);
                h.setLastPayDate((LocalDate) o[5]);
                if (null != o[10]) {
                    h.setLtgLastPaid((LocalDate) o[10]);
                }
                if (null != o[11]) {
                    h.setLastPromotionDate((LocalDate) o[11]);
                }
                if (null != o[12]) {
                    h.setNextPromotionDate((LocalDate) o[12]);
                }
                //--This employee is of no material importance?
                e = IppmsUtils.makeEmployeeObject(businessCertificate);
                e.setId((Long) o[6]);
                e.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                e.setFirstName((String) o[8]);
                e.setLastName((String) o[9]);
                e.setSalaryInfo(new SalaryInfo((Long) o[7]));
                e.setMdaInstId(((Long) o[13]));
                h.setSuspended(((Integer) o[14]));
                h.setStaffInd(((Integer) o[15]));
                h.setPayRespAllowanceInd(((Integer) o[16]));
                if (o[17] != null) {
                    h.setContractEndDate((LocalDate) o[17]);
                }
                if (o[18] != null) {
                    h.setContractStartDate((LocalDate) o[18]);
                }
                h.setContractExpiredInd(((Integer) o[19]));
                //  h.setPensionableInd(((Integer)o[20]).intValue());
                if (o[20] != null)
                    h.setTerminateDate((LocalDate) o[20]);
                else {
                    h.setTerminateDate(null);
                }
                h.setAbstractEmployeeEntity(e);
                if (o[21] != null) {
                    EmployeeType eType = new EmployeeType((Long) o[21], (Integer) o[22],0,0);
                    h.setPoliticalOfficeHolderType(eType.isPoliticalOfficeHolderType());
                    h.setEmployeeType(eType);

                }

                if (o[23] != null) {
                    h.setBranchInstId((Long) o[23]);
                } else {
                    h.setBranchInstId(4708L);
                }
                if (o[24] != null) {
                    h.setAccountNumber((String) o[24]);
                } else {
                    h.setAccountNumber("N/A");
                }
                if (o[25] != null) {
                    if(businessCertificate.isPensioner())
                        h.setAmAliveDate((LocalDate)o[25]);
                    else
                        h.getAbstractEmployeeEntity().setSchoolInstId((Long) o[25]);
                } else {
                    if(businessCertificate.isPensioner())
                        h.setAmAliveDate(null);
                    else{
                        h.getAbstractEmployeeEntity().setSchoolInstId(null);
                    }

                }

                if (o[26] != null) {
                    h.setPfaInfo(new PfaInfo((Long) o[26]));
                } else {
                    h.setPfaInfo(new PfaInfo());
                }
                if (o[27] != null) {
                    h.setPensionPinCode((String) o[27]);
                } else {
                    h.setPensionPinCode(null);
                }

                if (o[28] != null) {
                    h.getAbstractEmployeeEntity().setPayApprInstId((Long) o[28]);
                } else {
                    h.getAbstractEmployeeEntity().setPayApprInstId(null);
                }
                if (o[29] != null) {
                    //transient value to hold and persist BVN Numbers for report generation.
                    h.setBvnNo((String) o[29]);
                } else {
                    h.setBvnNo(null);
                }
                h.getAbstractEmployeeEntity().setMdaDeptMap(new MdaDeptMap((Long) o[30]));
                if(businessCertificate.isPensioner()) {
                    h.setMonthlyPensionAmount((Double) o[31]);
                    h.getAbstractEmployeeEntity().setEmployeeId((String)o[32]);
                }else{
                    h.getAbstractEmployeeEntity().setEmployeeId((String)o[31]);
                    if(null != o[32])
                        h.setTermId((Long)o[32]);

                }

                h.setRerunInd(1);

                wRetList.add(h);
            }

        }

        return wRetList;

    }
 }
