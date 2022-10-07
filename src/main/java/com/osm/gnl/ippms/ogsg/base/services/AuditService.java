/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.audit.domain.*;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.suspension.ReinstatementLog;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service("auditService")
@Repository
@Transactional(readOnly = true)
public class AuditService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<TransferLog> loadTransferAuditLogsForExport(LocalDate pFromDate, LocalDate pToDate, Long pUserId, Long pEmployeeId, BusinessCertificate bc) {


        List<CustomPredicate> wArrayList = new ArrayList<>();
        if (IppmsUtils.isNotNull(pFromDate) && IppmsUtils.isNotNull(pToDate)) {
            wArrayList.add(CustomPredicate.procurePredicate("transferDate", pFromDate, Operation.GREATER_OR_EQUAL));
            wArrayList.add(CustomPredicate.procurePredicate("transferDate", pToDate, Operation.LESS_OR_EQUAL));

        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
            wArrayList.add(CustomPredicate.procurePredicate("user.id", pUserId));


        if (IppmsUtils.isNotNullAndGreaterThanZero(pEmployeeId))
            wArrayList.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmployeeId));

        return this.genericService.loadAllObjectsUsingRestrictions(TransferLog.class, wArrayList, "id");
    }

    public List<AbstractDeductionAuditEntity> loadDeductionAuditLogsForExport(BusinessCertificate businessCertificate, LocalDate pFromDate, LocalDate pToDate,
                                                                              Long pUserId, Long pTypeId, Long pEmpId) throws IllegalAccessException, InstantiationException {


        boolean wUseDates = (null != pFromDate && null != pToDate);

        String wHql = "select d.oldValue,d.newValue,d.lastModTs,d.auditTime,e.firstName,e.lastName,e.initials" +
                ",e.employeeId,edt.name,l.id,l.firstName,l.lastName, d.columnChanged, d.lastModTs" +
                " from " + IppmsUtils.getDeductionAuditTable(businessCertificate) + " d, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, " +
                "EmpDeductionType edt, User l" +
                " where d.user.id = l.id and d.employee.id = e.id" +
                " and d.deductionType.id = edt.id and d.businessClientId = :pBizId";

        if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId)) {
            wHql += " and l.id = :pLoginId";
        }

        if (IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)) {
            wHql += " and edt.id = :pDedTypeId";

        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)) {
            wHql += " and e.id = :pEmpInstId";
        }
        if (wUseDates)
            wHql += " and d.lastModTs >= :pStartDate and d.lastModTs <= :pEndDate";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        if (wUseDates) {
            wQuery.setParameter("pStartDate", pFromDate);
            wQuery.setParameter("pEndDate", pToDate);
        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
            wQuery.setParameter("pLoginId", pUserId);
        if (IppmsUtils.isNotNullAndGreaterThanZero(pTypeId))
            wQuery.setParameter("pDedTypeId", pTypeId);
        if (IppmsUtils.isNotNullAndGreaterThanZero(pEmpId))
            wQuery.setParameter("pEmpInstId", pEmpId);

        wQuery.setParameter("pBizId", businessCertificate.getBusinessClientInstId());

        List<AbstractDeductionAuditEntity> wRetList = new ArrayList<>();
        Class<?> clazz = IppmsUtils.getDeductionAuditEntityClass(businessCertificate);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            AbstractDeductionAuditEntity p;
            LocalDate wLastModTs;
            String wOldValue;
            String wNewValue;
            String pFirstName;
            String pLastName;
            String pInitials;
            for (Object[] o : wRetVal) {
                p = (AbstractDeductionAuditEntity) clazz.newInstance();
                 wOldValue = (String) o[0];
                 wNewValue = (String) o[1];
                wLastModTs = (LocalDate) o[2];
                p.setAuditTimeStamp((String) o[3]);
                p.setOldValue(wOldValue);
                p.setNewValue(wNewValue);
                p.setLastModTs(wLastModTs);

                pFirstName = (String) o[4];
                pLastName = (String) o[5];
                pInitials = null;
                if (o[6] != null) {
                    pInitials = (String) o[6];
                }

                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setPlaceHolder((String) o[7]);
                p.setDeductType((String) o[8]);
                p.setChangedBy(o[10] + " " + o[11]);
                p.setColumnChanged((String)o[12]);
                p.setAuditTime((String) o[3]);
                p.setTransferDate((LocalDate)o[13]);

                wRetList.add(p);
            }


        }
        return wRetList;

    }

    public List<AbstractSpecAllowAuditEntity> loadSpecAllowAuditLogsForExport(BusinessCertificate bc, LocalDate pStartDate, LocalDate pEndDate, Long pUserId, Long pTypeId, Long pEmpId) {

        boolean wUseDates = (null != pStartDate && null != pEndDate);


        List<AbstractSpecAllowAuditEntity> wRetList = new ArrayList<>();


        String wHql = "select e.employeeId,e.firstName,e.lastName,m.id,m.name," +
                "sat.description,saab.oldValue,saab.newValue,saab.columnChanged,saab.lastModTs,l.id,l.firstName,l.lastName,saab.auditTimeStamp " +
                "from " + IppmsUtils.getEmployeeTableName(bc) + " e, "+IppmsUtils.getSpecAllowAuditTable(bc)+" saab, "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" sai,SpecialAllowanceType sat, User l"
                + ", MdaInfo m " +
                " where e.id = saab."+bc.getEmployeeIdJoinStr()+" and sai.id = saab.specialAllowanceInfo.id and m.id = saab.mdaInfo.id" +
                " and sai.specialAllowanceType.id = sat.id and l.id = saab.user.id and e.businessClientId = :pBizId";


        if (wUseDates)
            wHql += " and saab.lastModTs >= :pStartDateVal and saab.lastModTs <= :pEndDateVal ";

        if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
            wHql += " and saab.user.id  = :pUserIdVal ";

        if (IppmsUtils.isNotNullAndGreaterThanZero(pTypeId))
            wHql += " and sat.id  = :pTypeIdVal ";

        if (IppmsUtils.isNotNullAndGreaterThanZero(pEmpId))
            wHql += " and e.id  = :pUseEmpIdVal ";


        wHql += " order by saab.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        if (wUseDates) {
            wQuery.setParameter("pStartDateVal", pStartDate);
            wQuery.setParameter("pEndDateVal", pEndDate);
        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
            wQuery.setParameter("pUserIdVal", pUserId);
        if (IppmsUtils.isNotNullAndGreaterThanZero(pTypeId))
            wQuery.setParameter("pTypeIdVal", pTypeId);
        if (IppmsUtils.isNotNullAndGreaterThanZero(pEmpId))
            wQuery.setParameter("pUseEmpIdVal", pEmpId);

        wQuery.setParameter("pBizId", bc.getBusinessClientInstId());
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            Employee e;
            AbstractSpecAllowAuditEntity s;
            for (Object[] o : wRetVal) {
                  e = new Employee();


                e.setEmployeeId((String) o[0]);
                e.setFirstName((String) o[1]);
                e.setLastName((String) o[2]);
                e.setMapInstId(((Long) o[3])); //MdaInfo.id
                e.setMdaName((String) o[4]);

                s = IppmsUtils.makeSpecialAllowanceAuditObject(bc);
                s.setAllowanceType(((String) o[5]));
                s.setOldValue(((String) o[6]));
                s.setNewValue(((String) o[7]));
                s.setColumnChanged(((String) o[8]));
                s.setLastModTs(((LocalDate) o[9]));
                User l = new User(((Long) o[10]));
                l.setFirstName(((String) o[11]));
                l.setLastName(((String) o[12]));
                s.setAuditTimeStamp(((String) o[13]));
                s.setEmployee(e);
                s.setUser(l);
                wRetList.add(s);
            }

        }

        return wRetList;

    }

    public List<GarnishmentAudit> loadGarnishmentLogsForExport(BusinessCertificate bc, LocalDate pFromDate, LocalDate pToDate, Long pUserId, Long pTypeId, Long pEmpId) {

        boolean wUseDates = IppmsUtils.isNotNull(pFromDate);

        boolean useType = IppmsUtils.isNotNullAndGreaterThanZero(pUserId);
        boolean useEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEmpId);
        boolean useUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUserId);

        String wHql = "select d.oldValue,d.newValue,d.lastModTs,d.auditTimeStamp,e.firstName,e.lastName,e.initials" +
                ",e.employeeId,egt.name,l.id,l.firstName,l.lastName" +
                " from "+IppmsUtils.getGarnishAuditTableName(bc)+" d, "+IppmsUtils.getEmployeeTableName(bc)+" e, EmpGarnishmentType egt, User l" +
                " where d.user.id = l.id and d.employee.id = e.id" +
                " and d.garnishmentType.id = egt.id  and e.businessClientId = :pBizId";

        if (useUserId) {
            wHql += " and l.id = :pLoginId";
        }

        if (useType) {
            wHql += " and egt.id = :pDedTypeId";

        }
        if (useEmpId) {
            wHql += " and e.id = :pEmpInstId";
        }
        if (wUseDates)
            wHql += " and d.lastModTs > :pStartDate and d.lastModTs < :pEndDate";


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        if (wUseDates) {
            wQuery.setParameter("pStartDate", pFromDate);
            wQuery.setParameter("pEndDate", pToDate);
        }
        if (useUserId)
            wQuery.setParameter("pLoginId", pUserId);
        if (useType)
            wQuery.setParameter("pDedTypeId", pTypeId);
        if (useEmpId)
            wQuery.setParameter("pEmpInstId", pEmpId);

        wQuery.setParameter("pBizId", bc.getBusinessClientInstId());

        List<GarnishmentAudit> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            GarnishmentAudit p;
            for (Object[] o : wRetVal) {
                  p = new GarnishmentAudit();
                p.setOldValue((String) o[0]);
                p.setNewValue((String) o[1]);
                p.setLastModTs((LocalDate) o[2]);
                p.setAuditTimeStamp((String) o[3]);
                String pFirstName = (String) o[4];
                String pLastName = (String) o[5];
                String pInitials = null;
                if (o[6] != null) {
                    pInitials = (String) o[6];
                }

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setPlaceHolder((String) o[7]);
                p.setGarnishType((String) o[8]);
                p.setChangedBy(o[10] + " " + o[11]);

                wRetList.add(p);
            }


        }
        return wRetList;

    }

    public List<AbstractEmployeeAuditEntity> loadEmployeeLogsForExport(BusinessCertificate businessCertificate, LocalDate pFromDate, LocalDate pToDate, Long pUserId, Long pEmpId,
                                                                       boolean pUseUserId, boolean pUseEmpId, Long pMdaId, Long pSchoolId, String pPayPeriod) throws IllegalAccessException, InstantiationException {

        boolean wUseDates = (IppmsUtils.isNotNull(pFromDate) && IppmsUtils.isNotNull(pToDate));

        boolean wUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaId);

        boolean wUseSchool = IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId);

        boolean wUsePayPeriod = pPayPeriod.length() > 1;

        String wHql = "select d.oldValue,d.newValue,d.lastModTs,d.auditTimeStamp,e.firstName,e.lastName,e.initials" +
                ",e.employeeId,l.firstName,l.lastName,d.columnChanged,d.mdaInfo.id,d.mdaInfo.name,d.auditPayPeriod,s.salaryType.name,s.level,s.step" +
                " from " + IppmsUtils.getEmployeeAuditTable(businessCertificate) + " d, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, User l,SalaryInfo s " +
                " where d.user.id = l.id and d.employee.id = e.id and s.id = d.salaryInfo.id and e.businessClientId = :pBizId";

        if (pUseUserId) {
            wHql += " and l.id = :pLoginId";
        }


        if (pUseEmpId) {
            wHql += " and e.id = :pEmpInstId";
        }
        if (wUseDates)
            wHql += " and d.lastModTs > :pStartDate and d.lastModTs < :pEndDate";

        if (wUseMda) {
            wHql += " and d.mdaInfo.id = :pMdaInstIdVar ";
        }
        if (wUseSchool) {
            wHql += " and d.schoolInfo.id = :pSchoolInstIdVar ";
        }
        if (wUsePayPeriod) {
            wHql += " and d.payPeriod = :pPayPeriodVar ";
        }
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        if (wUseDates) {
            wQuery.setParameter("pStartDate", pFromDate);
            wQuery.setParameter("pEndDate", pToDate);
        }
        if (pUseUserId)
            wQuery.setParameter("pLoginId", pUserId);

        if (pUseEmpId)
            wQuery.setParameter("pEmpInstId", pEmpId);
        if (wUseMda) {
            wQuery.setParameter("pMdaInstIdVar", pMdaId);

        }
        if (wUseSchool) {
            wQuery.setParameter("pSchoolInstIdVar", pSchoolId);
        }
        if (wUsePayPeriod) {

            wQuery.setParameter("pPayPeriodVar", pPayPeriod);
        }
        wQuery.setParameter("pBizId", businessCertificate.getBusinessClientInstId());
        List<AbstractEmployeeAuditEntity> wRetList = new ArrayList<AbstractEmployeeAuditEntity>();
        Class<?> clazz = IppmsUtils.getEmployeeAuditEntityClass(businessCertificate);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                AbstractEmployeeAuditEntity p = (AbstractEmployeeAuditEntity) clazz.newInstance();
                p.setOldValue((String) o[0]);
                p.setNewValue((String) o[1]);
                p.setLastModTs((LocalDate) o[2]);
                p.setAuditTime((String) o[3]);
                String pFirstName = (String) o[4];
                String pLastName = (String) o[5];
                String pInitials = null;
                if (o[6] != null) {
                    pInitials = (String) o[6];
                }
                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setEmployeeId((String)o[7]);


                p.setChangedBy(o[8] + " " + o[9]);
                p.setColumnChanged((String) o[10]);
                p.setMdaInfo(new MdaInfo((Long) o[11], (String) o[12]));
                p.setAuditPayPeriod((String) o[13]);
                p.setPlaceHolder(o[14] + " : " + o[15] + "/" + o[16]);

                wRetList.add(p);
            }


        }
        return wRetList;


    }

    public List<EmployeeAudit> loadHiringInfoLogsForExport(BusinessCertificate businessCertificate,
                                                           LocalDate pFromDate, LocalDate pToDate, Long pUserId, Long pEmpId, boolean pUseUserId, boolean pUseEmpId) {

        boolean wUseDates = (null != pFromDate && null != pToDate);

        String wHql = "select d.oldValue,d.newValue,d.lastModTs,d.auditTimeStamp,e.firstName,e.lastName,e.initials" +
                ",e.employeeId,l.firstName,l.lastName,d.columnChanged" +
                " from HiringInfoAudit d, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, User l," +
                 " HiringInfo h" +
                " where d.user.id = l.id and d.hireInfo.id = h.id and h.employee.id = e.id and e.businessClientId = :pBizId";

        if (pUseUserId) {
            wHql += " and l.id = :pLoginId";
        }


        if (pUseEmpId) {
            wHql += " and e.id = :pEmpInstId";
        }
        if (wUseDates)
            wHql += " and d.lastModTs > :pStartDate and d.lastModTs < :pEndDate";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        if (wUseDates) {
            wQuery.setParameter("pStartDate", pFromDate);
            wQuery.setParameter("pEndDate", pToDate);
        }
        if (pUseUserId)
            wQuery.setParameter("pLoginId", pUserId);

        if (pUseEmpId)
            wQuery.setParameter("pEmpInstId", pEmpId);

        wQuery.setParameter("pBizId", businessCertificate.getBusinessClientInstId());

        List<EmployeeAudit> wRetList = new ArrayList<EmployeeAudit>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                EmployeeAudit p = new EmployeeAudit();
                String wOldValue = (String) o[0];
                String wNewValue = (String) o[1];
                LocalDate wLastModTs = (LocalDate) o[2];
                String wAuditTime = (String) o[3];
                p.setAuditTimeStamp(wAuditTime);
                p.setOldValue(wOldValue);
                p.setNewValue(wNewValue);
                p.setLastModTs(wLastModTs);

                String pFirstName = (String) o[4];
                String pLastName = (String) o[5];
                String pInitials = null;
                if (o[6] != null) {
                    pInitials = (String) o[6];
                }

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setPlaceHolder((String) o[7]);

                p.setChangedBy(o[8] + " " + o[9]);
                p.setColumnChanged((String) o[10]);
                wRetList.add(p);
            }


        }
        return wRetList;


    }

    public List<PromotionAudit> loadPromotionLogsForExport(BusinessCertificate businessCertificate,
                                                           LocalDate pFromDate, LocalDate pToDate, Long pUserId, Long pEmpId,
                                                           Long pMdaInstId, boolean pUseUserId, boolean pUseEmpId) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {


        boolean wUseDates = (null != pFromDate && null != pToDate);
        boolean wUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaInstId);
        String wHql = "select d.oldSalaryInfo.id,d.salaryInfo.id,d.promotionDate,d.auditTime,e.firstName,e.lastName,e.initials" +
                ",e.employeeId,l.firstName,l.lastName,d.mdaInfo.name " +
                " from " + IppmsUtils.getPromotionAuditTable(businessCertificate) + " d, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, User l" +
                " where d.user.id = l.id and d.employee.id = e.id and e.businessClientId = :pBizId";

        if (pUseUserId) {
            wHql += " and l.id = :pLoginId";
        }

        if (pUseEmpId) {
            wHql += " and e.id = :pEmpInstId";
        }
        if (wUseDates)
            wHql += " and d.lastModTs > :pStartDate and d.lastModTs < :pEndDate";
        if (wUseMda) {
            wHql += " and d.mdaInfo.id = :pMapId ";
        }
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        if (wUseDates) {
            wQuery.setParameter("pStartDate", pFromDate);
            wQuery.setParameter("pEndDate", pToDate);
        }
        if (pUseUserId)
            wQuery.setParameter("pLoginId", pUserId);

        if (pUseEmpId)
            wQuery.setParameter("pEmpInstId", pEmpId);

        if (wUseMda) {
            wQuery.setParameter("pMapId", pMdaInstId);

        }
        wQuery.setParameter("pBizId", businessCertificate.getBusinessClientInstId());
        List<PromotionAudit> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            HashMap<Long, SalaryInfo> wSalInfoMap = this.genericService.loadObjectAsMapWithConditions(SalaryInfo.class, Arrays.asList(
                    CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())), "id");

            PromotionAudit p;
            Long wOldValue,wNewValue;
            LocalDate wLastModTs;
            String wAuditTime,pFirstName,pLastName,pInitials;
            for (Object[] o : wRetVal) {
                p = new PromotionAudit();
                 wOldValue = (Long) o[0];
                 wNewValue = (Long) o[1];
                 wLastModTs = (LocalDate) o[2];
                  wAuditTime = (String) o[3];
                p.setAuditTime(wAuditTime);
                p.setOldSalaryInfo(wSalInfoMap.get(wOldValue));
                p.setSalaryInfo(wSalInfoMap.get(wNewValue));
                p.setLastModTs(wLastModTs);
                p.setPromotionDate(wLastModTs);

                  pFirstName = (String) o[4];
                  pLastName = (String) o[5];
                  pInitials = null;
                if (o[6] != null) {
                    pInitials = (String) o[6];
                }

                p.setEmployee(new Employee(pFirstName, pLastName, pInitials));
                p.getEmployee().setEmployeeId((String) o[7]);

                p.setPromotedBy(o[8] + " " + o[9]);
                p.setMdaName((String) o[10]);
                wRetList.add(p);
            }


        }
        return wRetList;


    }

    public List<ReinstatementLog> loadReinstatementLogsForExport(BusinessCertificate bc, LocalDate pFromDate, LocalDate pToDate, Long pUserId, Long pEmpId, Long pMdaInstId, boolean pUseUserId, boolean pUseEmpId) {


        boolean wUseDates = (null != pFromDate && null != pToDate);
        boolean wUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaInstId);
        String wHql = "select e.employeeId,e.firstName,e.lastName,e.initials,m.name,r.reinstatementDate,st.name,s.level," +
                "s.step,l.firstName,l.lastName,r.lastModTs " +
                " from ReinstatementLog r, User l, SalaryInfo s, SalaryType st, MdaInfo m, "+IppmsUtils.getEmployeeTableName(bc)+" e " +
                " where r.user.id = l.id and r.salaryInfo.id = s.id and s.salaryType.id = st.id and m.id = r.mdaInfo.id and e.id = r."+bc.getEmployeeIdJoinStr()+"" +
                " and e.businessClientId = :pBizId";

        if (pUseUserId) {
            wHql += " and l.id = :pLoginId";
        }


        if (pUseEmpId) {
            wHql += " and e.id = :pEmpInstId";
        }
        if (wUseDates)
            wHql += " and r.lastModTs > :pStartDate and r.lastModTs < :pEndDate";
        if (wUseMda) {
            wHql += " and m.id = :pMapId";
        }
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        if (wUseDates) {
            wQuery.setParameter("pStartDate", pFromDate);
            wQuery.setParameter("pEndDate", pToDate);
        }
        if (pUseUserId)
            wQuery.setParameter("pLoginId", pUserId);

        if (pUseEmpId)
            wQuery.setParameter("pEmpInstId", pEmpId);

        if (wUseMda) {
            wQuery.setParameter("pMapId", pMdaInstId);

        }
        wQuery.setParameter("pBizId", bc.getBusinessClientInstId());
        List<ReinstatementLog> wRetList = new ArrayList<ReinstatementLog>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {
            int i = 0;
            ReinstatementLog p;
            for (Object[] o : wRetVal) {

                p = new ReinstatementLog();

                p.setMdaCode((String) o[i++]);
                String _fName = (String) o[i++];
                String _lName = (String) o[i++];
                String _init = (String) o[i++];

                p.setEmployeeName(PayrollHRUtils.createDisplayName(_lName, _fName, _init));

                p.setMdaName((String) o[i++]);
                p.setReinstatementDate((LocalDate) o[i++]);

                String wPayGroup = (String) o[i++];
                int wLevel = (Integer) o[i++];
                int wStep = (Integer) o[i++];
                String wFirstName = (String) o[i++];
                String wLastName = (String) o[i++];
                p.setLastModTs((LocalDate) o[i++]);

                p.setPayGroup(wPayGroup);
                String wLevelStepStr;
                if (wStep > 9) {
                    wLevelStepStr = wLevel + "/" + wStep;
                } else {
                    wLevelStepStr = wLevel + "/0" + wStep;
                }
                p.setPayGroupLevelAndStep(wLevelStepStr);
                p.setApprover(wFirstName + " " + wLastName);

                wRetList.add(p);
                i = 0;
            }


        }
        return wRetList;


    }

    public List<PaymentMethodInfoLog> loadEmployeeAccountLogsForExport(BusinessCertificate businessCertificate, LocalDate pFromDate, LocalDate pToDate, Long pUserId, Long pEmpId, boolean pUseUserId, boolean pUseEmpId) {


        boolean wUseDates = (null != pFromDate && null != pToDate);

        String wHql = "select d.oldValue,d.newValue,d.lastModTs,d.auditTimeStamp,e.firstName,e.lastName,e.initials" +
                ",e.employeeId,l.firstName,l.lastName,d.columnChanged " +
                " from " + IppmsUtils.getPaymentMethodAuditTable(businessCertificate) + " d, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, User l" +
                " where d.user.id = l.id and d.employee.id = e.id and e.businessClientId = :pBizId";

        if (pUseUserId) {
            wHql += " and l.id = :pLoginId";
        }


        if (pUseEmpId) {
            wHql += " and e.id = :pEmpInstId";
        }
        if (wUseDates)
            wHql += " and d.lastModTs > :pStartDate and d.lastModTs < :pEndDate";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        if (wUseDates) {
            wQuery.setParameter("pStartDate", pFromDate);
            wQuery.setParameter("pEndDate", pToDate);
        }
        if (pUseUserId)
            wQuery.setParameter("pLoginId", pUserId);

        if (pUseEmpId)
            wQuery.setParameter("pEmpInstId", pEmpId);

        wQuery.setParameter("pBizId", businessCertificate.getBusinessClientInstId());

        List<PaymentMethodInfoLog> wRetList = new ArrayList<PaymentMethodInfoLog>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                PaymentMethodInfoLog p = new PaymentMethodInfoLog();
                String wOldValue = (String) o[0];
                String wNewValue = (String) o[1];
                LocalDate wLastModTs = (LocalDate) o[2];
                String wAuditTime = (String) o[3];
                p.setAuditTimeStamp(wAuditTime);
                p.setOldValue(wOldValue);
                p.setNewValue(wNewValue);
                p.setLastModTs(wLastModTs);

                String pFirstName = (String) o[4];
                String pLastName = (String) o[5];
                String pInitials = null;
                if (o[6] != null) {
                    pInitials = (String) o[6];
                }

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setPlaceHolder((String) o[7]);

                p.setChangedBy(o[8] + " " + o[9]);
                p.setColumnChanged((String) o[10]);
                wRetList.add(p);
            }


        }
        return wRetList;


    }

    public List<PaymentMethodInfoLog> getBankInfoAuditLogByDateAndUserId(BusinessCertificate businessCertificate, LocalDate pStartDate,
                                                                         LocalDate pEndDate, Long pUid, Long pEmpId, boolean pUseUserId, boolean pUseEmpId) {


        List<PaymentMethodInfoLog> wRetList = new ArrayList<PaymentMethodInfoLog>();

        String wHql = "select e.id,e.employeeId,e.firstName,e.lastName,e.initials,t.id,t.name,l.id,l.username,"
                + "l.firstName,l.lastName,p.id,p.oldValue,p.newValue,p.columnChanged,p.auditTimeStamp,m.codeName"
                + " from " + IppmsUtils.getEmployeeTableName(businessCertificate) + " e, " + IppmsUtils.getPaymentMethodAuditTable(businessCertificate) + " p, User l,Title t,MdaInfo m " +
                " where e.id = p.employee.id and p.user.id = l.id and t.id = e.title.id and p.mdaInfo.id = m.id ";

        if (pStartDate != null && pEndDate != null) {
            wHql += "and p.lastModTs > :pStartDateVar and p.lastModTs < :pEndDateVar ";
        }
        if (pUseUserId) {
            wHql += " and l.id = :pUserIdVar";
        }
        if (pUseEmpId) {
            wHql += " and e.id = :pEmpIdVar";
        }
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        if (pStartDate != null && pEndDate != null) {
            wQuery.setParameter("pStartDateVar", pStartDate);
            wQuery.setParameter("pEndDateVar", pEndDate);
        }
        if (pUseUserId)
            wQuery.setParameter("pUserIdVar", pUid);

        if (pUseEmpId)
            wQuery.setParameter("pEmpIdVar", pEmpId);


//        if (pStartRow > 0)
//            wQuery.setFirstResult(pStartRow);
//        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            int i = 0;
            for (Object[] o : wRetVal) {
                Employee e = new Employee();

                e.setId((Long) o[i++]);
                e.setEmployeeId((String) o[i++]);
                e.setFirstName((String) o[i++]);
                e.setLastName((String) o[i++]);

                e.setInitials(StringUtils.trimToEmpty((String) o[i++]));

                e.setTitle(new Title((Long) o[i++], (String) o[i++]));
                User l = new User((Long) o[i++], (String) o[i++], (String) o[i++], (String) o[i++]);
                PaymentMethodInfoLog s = new PaymentMethodInfoLog((Long) o[i++]);
                s.setUser(l);
                s.setEmployee(e);
                s.setOldValue((String) o[i++]);
                s.setNewValue((String) o[i++]);
                s.setColumnChanged((String) o[i++]);
                s.setAuditTimeStamp((String) o[i++]);
                s.setMdaCodeName((String) o[i++]);
                wRetList.add(s);
                i = 0;
            }

        }

        return wRetList;

    }

    public int getTotalNoOfBankInfoAuditLogByDateAndUserId(BusinessCertificate businessCertificate, LocalDate nextORPreviousDay, LocalDate nextORPreviousDay1, Long pUid, Long pEmpId, boolean useUserId, boolean useEmpId) {

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("lastModTs", nextORPreviousDay, Operation.GREATER));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("lastModTs", nextORPreviousDay1, Operation.LESS));
        if (useUserId)
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("user.id", pUid));
        if (useEmpId){
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), pEmpId));

        }


        return this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, PaymentMethodInfoLog.class);
    }


    public List<EmployeeAudit> getEmployeeAuditLogByDateAndUserId(BusinessCertificate bc,  LocalDate pTime,
                                                                  LocalDate pTime2, Long pUid, Long pEmpId, boolean pUseUserId, boolean pUseEmpId, Long pMdaId, Long pSchoolId, String pPayPeriod) {

        boolean wUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaId);
        boolean wUseSchool = IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId);


        boolean wUsePayPeriod = pPayPeriod.length() > 1;

        String wHql = "select e.employeeId,e.lastName, e.firstName, e.initials," +
                "el.oldValue,el.newValue,el.columnChanged,el.lastModTs,el.auditTimeStamp," +
                "m.id,m.name,m.codeName,coalesce(el.schoolInfo.id,0),l.firstName,l.lastName" +
                " from " + IppmsUtils.getEmployeeTableName(bc) + " e," + IppmsUtils.getEmployeeAuditTable(bc) + " el,User l, MdaInfo m " +
                "where el.employee.id = e.id and el.user.id = l.id and m.id = el.mdaInfo.id ";

        if (pTime != null && pTime2 != null) {
            wHql += "and el.lastModTs > :pStartDate and el.lastModTs < :pEndDate ";
        }
        if (wUseMda) {
            wHql += "and m.id = :pMdaInstIdVar ";
        }
        if (wUseSchool) {
            wHql += "and el.schoolInfo.id = :pSchoolInstIdVar ";
        }
        if (wUsePayPeriod) {
            wHql += "and el.payPeriod = :pPayPeriodVar ";
        }
        if (pUseUserId)
            wHql += "and l.id = :pUserIdVar ";
        if (pUseEmpId)
            wHql += "and e.id = :pEmpIdVar ";
        wHql += " order by e.lastName, e.firstName, e.initials";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        if (pTime != null && pTime2 != null) {
            wQuery.setParameter("pStartDate", pTime);
            wQuery.setParameter("pEndDate", pTime2);
        }
        if (wUseMda) {
            wQuery.setParameter("pMdaInstIdVar", pMdaId);

        }
        if (wUseSchool) {
            wQuery.setParameter("pSchoolInstIdVar", pSchoolId);
        }
        if (wUsePayPeriod) {

            wQuery.setParameter("pPayPeriodVar", pPayPeriod);
        }
        if (pUseUserId)
            wQuery.setParameter("pUserIdVar", pUid);

        if (pUseEmpId)
            wQuery.setParameter("pEmpIdVar", pEmpId);

//        if (pStartRow > 0)
//            wQuery.setFirstResult(pStartRow);
//        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();
        List<EmployeeAudit> wRetList = new ArrayList<EmployeeAudit>();
        if (wRetVal.size() > 0) {
            EmployeeAudit s;
            int i = 0;
            for (Object[] o : wRetVal) {
                s = new EmployeeAudit();
                s.setPlaceHolder((String) o[i++]);
                s.setName(PayrollHRUtils.createDisplayName((String) o[i++], (String) o[i++], o[i++]));
                s.setOldValue((String) o[i++]);
                s.setNewValue((String) o[i++]);
                s.setColumnChanged((String) o[i++]);
                s.setLastModTs((LocalDate) o[i++]);
                s.setAuditTimeStamp((String) o[i++]);

                s.setMdaId((Long) o[i++]);
                s.setMdaName((String) o[i++]);
                s.setMdaCodeName((String) o[i++]);
                s.setSchoolId((Long) o[i++]);
                s.setChangedBy(o[i++] + " " + o[i++]);

                wRetList.add(s);
                i = 0;
            }
        }

        return wRetList;


    }

    public int getTotalNoOfEmployeeAuditLogByDateAndUserId(BusinessCertificate businessCertificate, LocalDate pTime, LocalDate pTime2, Long pUid,
                                                           Long pEmpId, boolean pUseUserId, boolean pUseEmpId, Long pMdaId, Long pSchoolId, String pPayPeriod) {

        int wRetVal;

        boolean wUseMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaId);


        boolean wUseSchool = IppmsUtils.isNotNullAndGreaterThanZero(pSchoolId);
        boolean wUsePayPeriod = pPayPeriod.length() > 1;

        List list;
        Criteria wCrit = getSessionFactory().getCurrentSession().createCriteria(IppmsUtils.getEmployeeAuditEntityClass(businessCertificate)).setProjection(Projections.count("id"));
        if (pTime != null && pTime2 != null) {

            Criterion wGt = Restrictions.gt("lastModTs", pTime);
            Criterion wLt = Restrictions.lt("lastModTs", pTime2);
            LogicalExpression wAndExp = Restrictions.and(wGt, wLt);
            wCrit.add(wAndExp);
        }
        if (wUseMda) {
            wCrit.add(Restrictions.eq("mdaInfo.id", pMdaId));

        }
        if (wUseSchool) {
            wCrit.add(Restrictions.eq("schoolInfo.id", pSchoolId));
        }
        if (wUsePayPeriod) {
            wCrit.add(Restrictions.eq("payPeriod", pPayPeriod));
        }
        if (pUseUserId) {
            wCrit.add(Restrictions.eq("user.id", pUid));
        }
        if (pUseEmpId) {
            wCrit.add(Restrictions.eq("employee.id", pEmpId));
        }
        list = wCrit.list();
        if ((list == null) || (list.isEmpty())) {
            return 0;
        }
        wRetVal = ((Long) list.get(0)).intValue();
        return wRetVal;

    }

    public List<HiringInfoAudit> getHireInfoAuditLogByDateAndUserId(BusinessCertificate bc, LocalDate pStartDate,
                                                                                LocalDate pEndDate, Long pUid, Long pEmpId, boolean pUseUserId, boolean pUseEmpId) {

        List<HiringInfoAudit> wRetList = new ArrayList<>();

        String wHql = "select e.id,e.employeeId,e.firstName,e.lastName,m.id,m.name,m.codeName," +
                "hia.oldValue,hia.newValue,hia.columnChanged,hia.lastModTs,l.id,l.firstName,l.lastName,hia.auditTimeStamp " +
                "from " + IppmsUtils.getEmployeeTableName(bc) + " e, HiringInfoAudit hia, HiringInfo h, User l, MdaInfo m" +
                " where h.id = hia.hireInfo.id and e.id = h.employee.id and m.id = e.mdaDeptMap.mdaInfo.id" +
                " and l.id = hia.user.id and hia.businessClientId = :pBizIdVar ";

        boolean wUseDates = (pStartDate != null && pEndDate != null);

        if (wUseDates)
            wHql += " and hia.lastModTs >  :pStartDateVal and hia.lastModTs <  :pEndDateVal ";

        if (pUseUserId)
            wHql += " and hia.user.id  = :pUserIdVal ";


        if (pUseEmpId)
            wHql += " and e.id  = :pUseEmpIdVal ";

        wHql += " order by hia.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        if (wUseDates) {
            wQuery.setParameter("pStartDateVal", pStartDate);
            wQuery.setParameter("pEndDateVal", pEndDate);
        }
        if (pUseUserId)
            wQuery.setParameter("pUserIdVal", pUid);

        if (pUseEmpId)
            wQuery.setParameter("pUseEmpIdVal", pEmpId);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

//        if (pStartRow > 0)
//            wQuery.setFirstResult(pStartRow);
//        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            int i = 0;
            for (Object[] o : wRetVal) {
                Employee e = new Employee();

                e.setId((Long) o[i++]);
                e.setEmployeeId((String) o[i++]);
                e.setFirstName((String) o[i++]);
                e.setLastName((String) o[i++]);
                e.setMdaInstId((Long) o[i++]);
                e.setMdaName((String) o[i++]);
                e.setCurrentMdaName((String) o[i++]); //MDA Code Name

                HiringInfoAudit s = new HiringInfoAudit();

                s.setOldValue((String) o[i++]);
                s.setNewValue((String) o[i++]);
                s.setColumnChanged((String) o[i++]);
                s.setLastModTs(((LocalDate) o[i++]));
                User l = new User((Long) o[i++]);
                l.setFirstName((String) o[i++]);
                l.setLastName((String) o[i++]);
                s.setAuditTimeStamp((String) o[i++]);
                HiringInfo h = new HiringInfo();
                h.setEmployee(e);
                s.setHireInfo(h);
                s.setUser(l);
                wRetList.add(s);
                i = 0;
            }

        }

        return wRetList;

    }

    public int getTotalNoOfHireInfoAuditLogByDateAndUserId(BusinessCertificate bc, LocalDate pStartDate, LocalDate pEndDate, Long pUid,
                                                           Long pEmpId, boolean pUseUserId, boolean pUseEmpId) {

        int wRetVal;

        List list;

        String wHql = "select count(hia.id)" +
                "from " + IppmsUtils.getEmployeeTableName(bc) + " e, HiringInfoAudit hia, HiringInfo h, User l" +
                " where h.id = hia.hireInfo.id and e.id = h.employee.id" +
                " and l.id = hia.user.id and hia.businessClientId = :pBizIdVar";

        boolean wUseDates = (pStartDate != null && pEndDate != null);

        if (wUseDates)
            wHql += " and hia.lastModTs >  :pStartDateVal and hia.lastModTs <  :pEndDateVal ";

        if (pUseUserId)
            wHql += " and hia.user.id  = :pUserIdVal ";

        if (pUseEmpId)
            wHql += " and e.id  = :pUseEmpIdVal ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        if (wUseDates) {
            wQuery.setParameter("pStartDateVal", pStartDate);
            wQuery.setParameter("pEndDateVal", pEndDate);
        }
        if (pUseUserId)
            wQuery.setParameter("pUserIdVal", pUid);

        if (pUseEmpId)
            wQuery.setParameter("pUseEmpIdVal", pEmpId);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        list = wQuery.list();

        if ((list == null) || (list.isEmpty())) {
            return 0;
        }
        wRetVal = ((Long) list.get(0)).intValue();
        return wRetVal;


    }

    public List<?> getSpecAuditLogInfoByClassNameDateAndUserId(BusinessCertificate bc,
                                                               LocalDate pStartDate, LocalDate pEndDate, Long pUid,
                                                               Long pTypeId, Long pEmpId) {
        List<AbstractSpecAllowAuditEntity> wRetList = new ArrayList<>();


        boolean useUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUid);

        boolean useEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEmpId);

        boolean useTypeId = IppmsUtils.isNotNullAndGreaterThanZero(pTypeId);

        boolean useDates = IppmsUtils.isNotNull(pEndDate) && IppmsUtils.isNotNull(pStartDate);



        String wHql = "select e.employeeId,e.firstName,e.lastName, coalesce(e.initials,''),m.id,m.name,m.codeName," +
                "sat.description,saab.oldValue,saab.newValue,saab.columnChanged,saab.lastModTs,l.id,l.firstName,l.lastName,saab.auditTimeStamp " +
                "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getSpecAllowAuditTable(bc)+" saab, "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" sai,SpecialAllowanceType sat, User l, MdaInfo m" +
                " where e.id = saab."+bc.getEmployeeIdJoinStr()+" and sai.id = saab.specialAllowanceInfo.id and saab.mdaInfo.id = m.id" +
                " and sai.specialAllowanceType.id = sat.id and l.id = saab.user.id and saab.businessClientId = :pBizClientVar ";




        if(useDates)
            wHql += " and saab.lastModTs > :pStartDateVal and saab.lastModTs < :pEndDateVal ";

        if(useUserId)
            wHql += " and saab.user.id  = :pUserIdVal ";

        if(useTypeId)
            wHql += " and sat.id  = :pTypeIdVal ";

        if(useEmpId)
            wHql += " and e.id  = :pUseEmpIdVal ";

        wHql += " order by saab.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        if(useDates){
            wQuery.setParameter("pStartDateVal",pStartDate);
            wQuery.setParameter("pEndDateVal",pEndDate);
        }
        if(useUserId)
            wQuery.setParameter("pUserIdVal", pUid);
        if(useTypeId)
            wQuery.setParameter("pTypeIdVal", pTypeId);
        if(useEmpId)
            wQuery.setParameter("pUseEmpIdVal", pEmpId);

        wQuery.setParameter("pBizClientVar", bc.getBusinessClientInstId());
//
//        if (pStartRow > 0)
//            wQuery.setFirstResult(pStartRow);
//        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {

            int i = 0;
            Employee e;
            AbstractSpecAllowAuditEntity s;
            for (Object[] o : wRetVal) {
                  e = new Employee();


                e.setEmployeeId((String)o[i++]);
                e.setFirstName((String)o[i++]);
                e.setLastName((String)o[i++]);
                e.setInitials((String)o[i++]);
                e.setMdaInstId((Long)o[i++]);
                e.setMdaName((String)o[i++]);
                e.setCurrentMdaName((String)o[i++]); //MDA Code Name

                 s = IppmsUtils.makeSpecialAllowanceAuditObject(bc);
                s.setAllowanceType(((String)o[i++]));
                s.setOldValue(((String)o[i++]));
                s.setNewValue(((String)o[i++]));
                s.setColumnChanged(((String)o[i++]));
                s.setLastModTs(((LocalDate)o[i++]));
                User l = new User(((Long)o[i++]));
                l.setFirstName(((String)o[i++]));
                l.setLastName(((String)o[i++]));
                s.setAuditTimeStamp((String)o[i++]);
                s.setEmployee(e);
                s.setUser(l);
                wRetList.add(s);
                i = 0;
            }

        }

        return wRetList;
    }

    public int getSpecTotalNoOfAuditLogInfoByClassNameDateAndUserId(BusinessCertificate bc, LocalDate pStartDate, LocalDate pEndDate, Long pUid, Long pTypeId, Long pEmpId) {



        String wHql = "select count(saab.id) " +
                "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getSpecAllowAuditTable(bc)+" saab, "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" sai,SpecialAllowanceType sat, User l" +
                " where e.id = saab."+bc.getEmployeeIdJoinStr()+" and sai.id = saab.specialAllowanceInfo.id" +
                " and sai.specialAllowanceType.id = sat.id and l.id = saab.user.id and saab.businessClientId = :pBizClientVar";

        boolean wUseDates = (pStartDate != null && pEndDate != null);

        if(wUseDates)
            wHql += " and saab.lastModTs > :pStartDateVal and saab.lastModTs < :pEndDateVal ";

        if(IppmsUtils.isNotNullAndGreaterThanZero(pUid))
            wHql += " and saab.user.id  = :pUserIdVal ";

        if(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId))
            wHql += " and sat.id  = :pTypeIdVal ";

        if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId))
            wHql += " and e.id  = :pUseEmpIdVal ";


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        if(wUseDates){
            wQuery.setParameter("pStartDateVal",pStartDate);
            wQuery.setParameter("pEndDateVal",pEndDate);
        }
        if(IppmsUtils.isNotNullAndGreaterThanZero(pUid))
            wQuery.setParameter("pUserIdVal", pUid);
        if(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId))
            wQuery.setParameter("pTypeIdVal", pTypeId);
        if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpId))
            wQuery.setParameter("pUseEmpIdVal", pEmpId);

        wQuery.setParameter("pBizClientVar", bc.getBusinessClientInstId());



        List list = wQuery.list();

        if (list == null)
            return 0;
        if (list.size() == 0)
            return 0;
        if ((list.size() == 1) && (((Long)list.get(0)).intValue() == 0)) {
            return 0;
        }
        return ((Long)list.get(0)).intValue();
    }


    public List<PaymentMethodInfoLog> loadEmployeeAccountLogsForExport(
            LocalDate pFromDate, LocalDate pToDate,
            Long pUserId, Long pEmpId, boolean pUseUserId,
            boolean pUseEmpId, BusinessCertificate bc)
    {

        boolean wUseDates = (null != pFromDate && null != pToDate);

        String wHql = "select d.oldValue,d.newValue,d.lastModTs,d.auditTimeStamp,e.firstName,e.lastName,e.initials" +
                ",e.employeeId,l.firstName,l.lastName,d.columnChanged " +
                " from "+IppmsUtils.getPaymentMethodAuditTable(bc)+" d, "+IppmsUtils.getEmployeeTableName(bc)+" e, User l" +
                " where d.user.id = l.id and d."+bc.getEmployeeIdJoinStr()+" = e.id";

        if(pUseUserId){
            wHql += " and l.id = :pLoginId";
        }


        if(pUseEmpId){
            wHql += " and e.id = :pEmpInstId";
        }
        if(wUseDates)
            wHql += " and d.lastModTs > :pStartDate and d.lastModTs < :pEndDate";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        if(wUseDates){
            wQuery.setParameter("pStartDate", pFromDate);
            wQuery.setParameter("pEndDate", pToDate);
        }
        if(pUseUserId)
            wQuery.setParameter("pLoginId", pUserId);

        if(pUseEmpId)
            wQuery.setParameter("pEmpInstId", pEmpId);



        List<PaymentMethodInfoLog> wRetList = new ArrayList<PaymentMethodInfoLog>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                PaymentMethodInfoLog p = new PaymentMethodInfoLog();
                String wOldValue = (String)o[0];
                String wNewValue = (String)o[1];
                LocalDate wLastModTs = (LocalDate) o[2];
                String wAuditTime = (String)o[3];
                p.setAuditTimeStamp(wAuditTime);
                p.setOldValue(wOldValue);
                p.setNewValue(wNewValue);
                p.setLastModTs(wLastModTs);

                String pFirstName =  (String)o[4];
                String pLastName =  (String)o[5];
                String pInitials = null;
                if (o[6] != null)
                {
                    pInitials = (String)o[6];
                }

                p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));
                p.setPlaceHolder((String)o[7]);

                p.setChangedBy(o[8] +" "+ o[9]) ;
                p.setColumnChanged((String)o[10]);
                wRetList.add(p);
            }


        }
        return wRetList;
    }


}
