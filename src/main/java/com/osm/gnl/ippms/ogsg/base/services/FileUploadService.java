/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncrementTracker;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusBean;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusMasterBean;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service("fileUploadService")
@Repository
@Transactional(readOnly = true)
public class FileUploadService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;


    public <T> HashMap<String, T> makeDedLoanSpecHashMap(Class<T> pClass, BusinessCertificate businessCertificate)
    {

        List<T> wRetVal = this.genericService.loadAllObjectsWithSingleCondition(pClass,CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()), null);

        HashMap<String, T> wRetMap = new HashMap<>();


            if(wRetVal != null && !wRetVal.isEmpty()){
                for(T t : wRetVal){
                    if(pClass.isAssignableFrom(SpecialAllowanceType.class)){
                        wRetMap.put(((SpecialAllowanceType)t).getName().toUpperCase(),t);
                    }else if(pClass.isAssignableFrom(EmpDeductionType.class)){
                        wRetMap.put(((EmpDeductionType)t).getName().toUpperCase(),t);
                    }else if(pClass.isAssignableFrom(EmpGarnishmentType.class)){
                        wRetMap.put(((EmpGarnishmentType)t).getName().toUpperCase(),t);
                    }

                }
            }
     return wRetMap;
    }

    public  HashMap<String, NamedEntity> makeEmployeeMap(BusinessCertificate bc, boolean pActive){
        HashMap<String,NamedEntity> wRetMap = new HashMap<>();

        String wHqlStr = "select  e.id,e.employeeId, e.firstName,e.lastName,coalesce(e.initials,''),m.name, st.name, s.level,s.step,st.id,h.id,s.id" +
                "  from "+ IppmsUtils.getEmployeeTableName(bc)+" e, MdaDeptMap mdm, MdaInfo m, SalaryInfo s, SalaryType st, HiringInfo h" +
                " where e.businessClientId = :pBizClientId and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id and e.id = h."+bc.getEmployeeIdJoinStr() +
                "  and e.statusIndicator = :pStatusIndVar and s.id = e.salaryInfo.id and s.salaryType.id = st.id";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);

        query.setParameter("pBizClientId", bc.getBusinessClientInstId());
        if(pActive)
            query.setParameter("pStatusIndVar", 0);
        else
            query.setParameter("pStatusIndVar", 1);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();
        if (wRetVal.size() > 0) {
            int i = 0;
            NamedEntity n;
            int level,step;
            for (Object[] o : wRetVal) {
                n = new NamedEntity();
                n.setId((Long) o[i++]);
                n.setStaffId((String)o[i++]);
                n.setName( o[i++]+" "+o[i++]+" "+o[i++]);
                n.setOrganization((String)o[i++]);
                n.setPayTypeName((String)o[i++]);
                level = (Integer)o[i++];
                step = (Integer) o[i++];
                n.setLevel(level);
                n.setStep(step);
                n.setLevelAndStep(PayrollUtils.makeLevelAndStep(level,step));
                n.setSalaryTypeId((Long)o[i++]);
                n.setHiringInfoId((Long)o[i++]);
                n.setOldSalaryId((Long)o[i++]);
                wRetMap.put(n.getStaffId(),n);
                i = 0;

            }
        }
        return wRetMap;
    }

    public HashMap<String, NamedEntity> makeSuspendedMap(BusinessCertificate businessCertificate) {
        HashMap<String,NamedEntity> wRetMap = new HashMap<>();

        String wHqlStr = "select  e.id,e.employeeId, e.firstName,e.lastName,coalesce(e.initials,''),m.name, st.name, s.level,s.step" +
                "  from "+ IppmsUtils.getEmployeeTableName(businessCertificate)+" e, MdaDeptMap mdm, MdaInfo m, SalaryInfo s, SalaryType st, HiringInfo h" +
                " where e.businessClientId = :pBizClientId and h."+businessCertificate.getEmployeeIdJoinStr()+" = e.id and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id " +
                " and s.id = e.salaryInfo.id and s.salaryType.id = st.id and h.suspended = 1 ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);

        query.setParameter("pBizClientId", businessCertificate.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)query.list();
        if (wRetVal.size() > 0) {
            int i = 0;
            NamedEntity n;
            for (Object[] o : wRetVal) {
                n = new NamedEntity();
                n.setId((Long) o[i++]);
                n.setStaffId((String)o[i++]);
                n.setName( o[i++]+" "+o[i++]+" "+o[i++]);
                n.setOrganization((String)o[i++]);
                n.setPayTypeName((String)o[i++]);
                n.setLevelAndStep(PayrollUtils.makeLevelAndStep((Integer) o[i++],(Integer) o[i++]));
                wRetMap.put(n.getStaffId(),n);
                i = 0;

            }
        }
        return wRetMap;

    }
    public HashMap<String, PayTypes> makePayTypesAsMap() {
        List<PayTypes> payTypesList = genericService.loadAllObjectsWithSingleCondition(PayTypes.class,CustomPredicate.procurePredicate("selectableInd",0), null);
        HashMap<String,PayTypes> wRetMap = new HashMap<>();
        for(PayTypes payTypes : payTypesList)
            wRetMap.put(payTypes.getName(),payTypes);

        return wRetMap;
    }
    public HashMap<String, PayTypes> makePayTypeHashMapByClassName(BusinessCertificate businessCertificate,Class<?> pObjectTypeClass)
    {
        String wStr;

        if (pObjectTypeClass.isAssignableFrom(EmpDeductionType.class)) {
            wStr = "select distinct t.name,p.id,p.percentageInd from EmpDeductionType t, EmpDeductionInfo ti,PayTypes p where t.id = ti.empDeductionType.id  and p.id = ti.payTypes.id ";
        }
        else {
            wStr = "select distinct t.name,p.id,p.percentageInd from SpecialAllowanceType t, "+IppmsUtils.getSpecialAllowanceInfoTableName(businessCertificate)+" ti,PayTypes p where t.id = ti.specialAllowanceType.id  and p.id = ti.payTypes.id ";
        }
         wStr += "and t.businessClientId = :pBizClientId";



        Query wQuery = sessionFactory.getCurrentSession().createQuery(wStr);

        wQuery.setParameter("pBizClientId",businessCertificate.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        HashMap<String, PayTypes> wRetMap = new HashMap<>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                PayTypes n = new PayTypes();
                n.setId((Long)o[1]);
                n.setName((String)o[0]);
                n.setPercentageInd(((Integer)o[2]).intValue());

                wRetMap.put(n.getName().trim().toUpperCase(), n);
            }
        }

        return wRetMap;
    }


    public HashMap<String, NamedEntity> loadActiveEmployeesInMDAAsMap(BusinessCertificate businessCertificate,Long mdaInstId) {
        
        String wStr =  "select m.id,m.employeeId,m.firstName,m.lastName,m.initials from Employee m,MdaDeptMap adm, MdaInfo a" +
                        " where m.statusIndicator = 0 and m.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and a.id = :pMdaVal" +
                " and m.businessClientId = :pBizIdVar";
            


        Query wQuery = sessionFactory.getCurrentSession().createQuery(wStr);
        wQuery.setParameter("pMdaVal", mdaInstId);
        wQuery.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId());
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        HashMap<String, NamedEntity> wRetMap = new HashMap<String, NamedEntity>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                NamedEntity n = new NamedEntity();
                n.setId((Long) o[0]);
                n.setStaffId((String)o[1]);
                String pFirstName = (String)o[2];
                String pLastName = (String)o[3];
                String pInitials = null;
                if (null != o[4])
                    pInitials = (String)o[4];
                n.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));

                wRetMap.put(n.getStaffId().toUpperCase(), n);
            }
        }

        return wRetMap;


    }

    public List<Long> loadMappedIdsByInnerClassId(Long wMdaInstId) {

        String wHql =   "select adm.id from MdaDeptMap adm where adm.mdaInfo.id = :pMdaId";

        List<Long> wRetMap = new ArrayList<Long>();
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pMdaId", wMdaInstId);

        ArrayList<Long> wRetVal = (ArrayList<Long>) wQuery.list();
        for (Long o : wRetVal) {
            wRetMap.add(o);
        }
        return wRetMap;

    }

    public HashMap<Long, LeaveBonusBean> loadPromotedEmployeeByDates(BusinessCertificate bc,LocalDate wStartDate, LocalDate wEndDate) {

        String wHql = "select p.employee.id,p.newSalaryInfo.id,s.monthlyBasicSalary * .1,e.employeeId,e.lastName,e.firstName,e.initials"
                + ",h.birthDate,h.hireDate,h.terminateDate,h.ltgLastPaid,h.suspended,e.payApprInstId,h.expectedDateOfRetirement"
                + " from "+IppmsUtils.getPromotionAuditTable(bc)+" p,Employee e, HiringInfo h,SalaryInfo s "
                + " where p.employee.id = e.id and e.id = h.employee.id and p.salaryInfo.id = s.id and "
                + "p.promotionDate > :pStartDateVal and p.promotionDate < :pEndDateVal order by p.id desc";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pStartDateVal", wStartDate);

        wQuery.setParameter("pEndDateVal", wEndDate);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        HashMap<Long, LeaveBonusBean> wRetMap = new HashMap<Long, LeaveBonusBean>();
        for (Object[] o : wRetVal) {

            Long wEmpId = (Long) o[0];
            Long wSalId = (Long) o[1];
            Double wLeaveBonus = (Double) o[2];

            String wOgNum = (String) o[3];
            String wLastName = (String) o[4];
            String wFirstName = (String) o[5];
            String wInitials = null;
            if (o[6] != null) {
                wInitials = (String) o[6];
            }

            LocalDate wBirthDate = (LocalDate) o[7];
            LocalDate wHireDate = (LocalDate) o[8];
            LocalDate wTermDate = null;
            if (o[9] != null)
                wTermDate = (LocalDate) o[9];
            LocalDate wLtgLastPaid = null;
            if (o[10] != null)
                wLtgLastPaid = (LocalDate) o[10];
            int wSuspInd = (Integer) o[11];
            Long wPayAppr = null;
            if (o[12] != null)
                wPayAppr = (Long) o[12];
            LocalDate wExpTermDate = null;
            if (o[13] != null)
                wExpTermDate = (LocalDate) o[13];
            String wEmployeeName = PayrollHRUtils.createDisplayName(wLastName, wFirstName, wInitials);

            LeaveBonusBean n = new LeaveBonusBean();
            n.setEmployeeInstId(wEmpId);
            n.setMode(wOgNum);
            n.setName(wEmployeeName);
            n.setLeaveBonusAmount(wLeaveBonus);
            n.setSalaryInfoInstId(wSalId);
            n.setDateOfBirth(wBirthDate);
            n.setDateOfHire(wHireDate);
            n.setDateTerminated(wTermDate);
            n.setLastLtgPaid(wLtgLastPaid);
            n.setSuspendedInd(wSuspInd);
            n.setApprovedForPayroll(wPayAppr != null);
            n.setExpRetireDate(wExpTermDate);

            if (!wRetMap.containsKey(wEmpId))
            /**
             * This is so that if the Employee was promoted more than once in January ONLY
             * the most recent one will be placed in the Map to use...hopefully, it is not a
             * demotion....XX <-- Fingers crossed.
             */
                wRetMap.put(wEmpId, n);

        }

        return wRetMap;

    }

    public HashMap<Long, LeaveBonusBean> getCreatedEmployeeByMdaAndYear(List<Long> wMapIds, int wYear) {


        String wHql = "select e.id,e.employeeId,e.lastName,e.firstName,e.initials"
                + ",f.salaryInfo.id,f.createdMonth,f.createdYear,h.birthDate,h.hireDate,h.terminateDate,"
                + "h.ltgLastPaid,h.suspended,e.payApprInstId,h.expectedDateOfRetirement "
                + " from Employee e, FirstLeaveBonus f,SalaryInfo s,HiringInfo h"
                + " where e.id = f.employee.id and f.salaryInfo.id = s.id and h.employee.id = e.id "
                + " and f.createdYear = :pCreatedYear and e.mdaDeptMap.io in (:pMapIds)";


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pCreatedYear", wYear);
        wQuery.setParameterList("pMapIds", wMapIds.toArray());
      
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        HashMap<Long, LeaveBonusBean> wRetMap = new HashMap<Long, LeaveBonusBean>();
        for (Object[] o : wRetVal) {
            Long wEmpId = (Long) o[0];
            String wOgNum = (String) o[1];
            String wLastName = (String) o[2];
            String wFirstName = (String) o[3];
            String wInitials = null;
            if (o[4] != null) {
                wInitials = (String) o[4];
            }
            Long wSalaryInfo = (Long) o[5];
            LocalDate wBirthDate = (LocalDate) o[8];
            LocalDate wHireDate = (LocalDate) o[9];
            LocalDate wTermDate = null;
            if (o[10] != null)
                wTermDate = (LocalDate) o[10];
            LocalDate wLtgLastPaid = null;
            if (o[11] != null)
                wLtgLastPaid = (LocalDate) o[11];
            int wSuspInd = (Integer) o[12];
            Long wPayAppr = null;
            if (o[13] != null)
                wPayAppr = (Long) o[13];
            LocalDate wExpTermDate = null;
            if (o[14] != null)
                wExpTermDate = (LocalDate) o[14];
            String wEmployeeName = PayrollHRUtils.createDisplayName(wLastName, wFirstName, wInitials);

            LeaveBonusBean n = new LeaveBonusBean();
            n.setEmployeeInstId(wEmpId);
            n.setMode(wOgNum);
            n.setName(wEmployeeName);
            n.setSalaryInfoInstId(wSalaryInfo);
            
            n.setDateOfBirth(wBirthDate);
            n.setDateOfHire(wHireDate);
            n.setDateTerminated(wTermDate);
            n.setLastLtgPaid(wLtgLastPaid);
            n.setSuspendedInd(wSuspInd);
            n.setApprovedForPayroll(wPayAppr != null);
            n.setExpRetireDate(wExpTermDate);

            wRetMap.put(wEmpId, n);

        }

        return wRetMap;

    }

    public HashMap<Long, Long> loadLeaveBonusByMdaAndYear(Long wMdaInstId, int wYear) {
        String wHqlStr = "select l.employee.id from LeaveBonusBean l, Employee e, LeaveBonusMasterBean lmb "
                + "where l.employee.id = e.id and l.leaveBonusMasterBean.id = lmb.id "
                + "and lmb.mdaInfo.id = :pMdaId and lmb.runYear = :pRunYear";

        Query wQuery = sessionFactory.getCurrentSession().createQuery (wHqlStr);
        wQuery.setParameter("pMdaId", wMdaInstId);

        wQuery.setParameter("pRunYear", wYear);
        ArrayList<Long> wRetVal = (ArrayList<Long>) wQuery.list();
        HashMap<Long, Long> wRetMap = new HashMap<Long, Long>();
        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Long o : wRetVal) {

                wRetMap.put(o, o);
            }
        }

        return wRetMap;
    }
    @Transactional()
    public void storeLeaveBonus(Vector<LeaveBonusBean> wSaveList, LeaveBonusMasterBean wLBMB) {
        for (LeaveBonusBean l : wSaveList) {
            l.setLeaveBonusMasterBean(wLBMB);
            l.setCreatedTime(wLBMB.getCreatedTime());
            genericService.storeObject(l);
        }
    }
    public HashMap<Long, LeaveBonusBean> loadEmpFromLastDecPayroll(int pYearInQuestion, Collection<Long> pMapIds,
                                                                       List<Long> pList, BusinessCertificate bc) {

        String wHql = "select e.id,e.employeeId,e.lastName,e.firstName,e.initials"
                + ",p.salaryInfo.id,h.birthDate,h.hireDate,h.terminateDate,"
                + "h.ltgLastPaid,h.suspended,e.payApprInstId,h.expectedDateOfRetirement,s.monthlyBasicSalary * .1 "
                + " from Employee e, "+IppmsUtils.getPaycheckTableName(bc)+" p,SalaryInfo s,HiringInfo h"
                + " where e.id = p.employee.id and p.salaryInfo.id = s.id" + " and h.employee.id = e.id" +

                " and p.netPay > 0" + " and p.runMonth = 11"
                + " and p.runYear = :pRunYear and p.employee.id in (:pEmpIds)";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunYear", pYearInQuestion);

        wQuery.setParameterList("pEmpIds", pList.toArray());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        HashMap<Long, LeaveBonusBean> wRetMap = new HashMap<Long, LeaveBonusBean>();
        for (Object[] o : wRetVal) {
            Long wEmpId = (Long) o[0];
            String wOgNum = (String) o[1];
            String wLastName = (String) o[2];
            String wFirstName = (String) o[3];
            String wInitials = null;
            if (o[4] != null) {
                wInitials = (String) o[4];
            }
            Long wSalaryInfo = (Long) o[5];

            LocalDate wBirthDate = (LocalDate) o[6];
            LocalDate wHireDate = (LocalDate) o[7];
            LocalDate wTermDate = null;
            if (o[8] != null)
                wTermDate = (LocalDate) o[8];
            LocalDate wLtgLastPaid = null;
            if (o[9] != null)
                wLtgLastPaid = (LocalDate) o[9];
            int wSuspInd = (Integer) o[10];
            Long wPayAppr = null;
            if (o[11] != null)
                wPayAppr = (Long) o[11];
            LocalDate wExpTermDate = null;
            if (o[12] != null)
                wExpTermDate = (LocalDate) o[12];
            String wEmployeeName = PayrollHRUtils.createDisplayName(wLastName, wFirstName, wInitials);

            LeaveBonusBean n = new LeaveBonusBean();
            n.setEmployeeInstId(wEmpId);
            n.setMode(wOgNum);
            n.setName(wEmployeeName);
            n.setSalaryInfoInstId(wSalaryInfo);
            n.setDateOfBirth(wBirthDate);
            n.setDateOfHire(wHireDate);
            n.setDateTerminated(wTermDate);
            n.setLastLtgPaid(wLtgLastPaid);
            n.setSuspendedInd(wSuspInd);
            n.setApprovedForPayroll(wPayAppr != null);
            n.setExpRetireDate(wExpTermDate);
            n.setLeaveBonusAmount((Double) o[13]);
            wRetMap.put(wEmpId, n);

        }

        return wRetMap;
    }

    public HashMap<Long, StepIncrementTracker> makeStepIncrementTrackerMap(BusinessCertificate bc, int pYear) {

        String wHql = "select e.id,sit.id,sit.noOfTimes from" +
                " Employee e, StepIncrementTracker sit where sit.employee.id = e.id and e.businessClientId = :pBizIdVar and sit.year = :pYearVar";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pYearVar", pYear);

        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        HashMap<Long, StepIncrementTracker> wRetMap = new HashMap<>();
        StepIncrementTracker sit;
        Long wEmpId;

        for (Object[] o : wRetVal) {
            wEmpId = (Long) o[0];
             sit = new StepIncrementTracker((Long)o[1]);
             sit.setNoOfTimes((Integer)o[2]);
             wRetMap.put(wEmpId, sit);

        }

        return wRetMap;

    }

    public HashMap<Long,HashMap<String,SalaryInfo>> makeLevelStepSalaryMapMap(BusinessCertificate bc) {
        String wHql = "select st.id,s.id,s.level,s.step from" +
                " SalaryInfo s, SalaryType st where s.salaryType.id = st.id and st.businessClientId = :pBizIdVar " +
                " and st.selectableInd = :pVar";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pVar", IConstants.ON);

        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        HashMap<String, SalaryInfo> innerMap;
        SalaryInfo salaryInfo;
        Long wEmpId;
        HashMap<Long,HashMap<String,SalaryInfo>> wRetMap = new HashMap<>();
        for (Object[] o : wRetVal) {
            wEmpId = (Long) o[0];
            salaryInfo = new SalaryInfo((Long)o[1], (Integer)o[2], (Integer)o[3]);

            if(wRetMap.containsKey(wEmpId)){
                wRetMap.get(wEmpId).put(salaryInfo.getLevelAndStepAsStr(),salaryInfo);
            }else{
                innerMap = new HashMap<>();
                innerMap.put(salaryInfo.getLevelAndStepAsStr(),salaryInfo);
                wRetMap.put(wEmpId,innerMap);
            }

        }

        return wRetMap;

    }

    public List<Long> makeAllowanceRuleMap(BusinessCertificate bc) {

        String wHql = "select h.id from" +
                " AllowanceRuleMaster s,HiringInfo h where h.businessClientId = :pBizIdVar " +
                " and s.activeInd = :pVar and s != null and s.hiringInfo.id = h.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pVar", IConstants.ON);

        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        List<Long> innerMap = new ArrayList<>();

        for (Object[] o : wRetVal) {

            innerMap.add((Long)o[0]);
        }

        return innerMap;
    }
    public List<Long> makeIncrementApprovalList(BusinessCertificate bc, int pYear) {

        String wHql = "select s.parentId from" +
                " StepIncrementApproval s , StepIncrementTracker sit where sit.businessClientId = :pBizIdVar " +
                " and s.approvalStatusInd = :pVar and s.stepIncrementTracker.id = sit.id and sit.year = :pYearVar";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pVar", IConstants.OFF);
        wQuery.setParameter("pYearVar", pYear);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Long> wRetVal = (ArrayList<Long>) wQuery.list();
        List<Long> innerMap = new ArrayList<>();

        for (Long o : wRetVal) {

            innerMap.add(o);
        }

        return innerMap;
    }
}
