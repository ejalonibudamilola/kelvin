/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusMasterBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGratuity;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service("payrollService")
@Repository
@Transactional(readOnly = true)
@Slf4j
public class PayrollService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    public List<NamedEntityBean> loadPayrollMonthsByYear(BusinessCertificate businessCertificate, int pRunYear) {


        String wHqlStr = "select  distinct(p.runMonth)  " +
                "from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p  " +
                "where p.runYear = :pRunYear ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);

        wQuery.setParameter("pRunYear", pRunYear);

        List<NamedEntityBean> wRetList = new ArrayList<>();
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {


            for (Object[] o : wRetVal) {

                NamedEntityBean wBean = new NamedEntityBean();
                Integer wMonthInd = (Integer) o[0];
                wBean.setId(new Long(wMonthInd + 1));
                wBean.setName(PayrollBeanUtils.getMonthNameFromInteger(wMonthInd));
                wRetList.add(wBean);

            }
        }

        Collections.sort(wRetList, Comparator.comparing(NamedEntityBean::getName));
        return wRetList;


    }

    @Transactional()
    public void updatePaymentMethodInfoUsingHql(Long pFromBranchInstId,
                                                Long pToBranchInstId, Long pLastModBy) {
        String hqlQuery = "update PaymentMethodInfo set bankBranches.id = :pToValue, lastModBy.id = :pModifiedBy, lastModTs = :pLastModTs where bankBranches.id = :pFromValue";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pToValue", pToBranchInstId);
        query.setParameter("pFromValue", pFromBranchInstId);
        query.setParameter("pModifiedBy", pLastModBy);
        query.setParameter("pLastModTs", Timestamp.from(Instant.now()));

        query.executeUpdate();

    }

    @Transactional()
    public void saveCollection(List<?> pCollections){
        this.genericService.storeObjectBatch(pCollections);
    }


    public List<NamedEntity> makeLevelOrStepList(Long pSalaryTypeId, boolean pLevel){
        String wHql = "";
        if(pLevel){
            wHql = "select distinct si.level from SalaryInfo si where si.salaryType.id = :pSalaryTypeIdVar ";
        }else{
            wHql = "select distinct si.step from SalaryInfo si where si.salaryType.id = :pSalaryTypeIdVar ";
        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pSalaryTypeIdVar",pSalaryTypeId);

        ArrayList<Integer> wRetList = (ArrayList<Integer>) wQuery.list();
        List<NamedEntity> wRetVal = new ArrayList<>();
        // int count;
        if (wRetList.size() > 0) {
            //count = 0;
            for (Integer o : wRetList) {
                NamedEntity e = new NamedEntity();
                e.setObjectInd(o);
                e.setName(String.valueOf(o));
                wRetVal.add(e);
            }
        }
        Collections.sort(wRetVal,Comparator.comparing(NamedEntity::getObjectInd));
        return wRetVal;
    }
    public List<NamedEntityBean> makeFutureYearsList(Long pBizId) {


        String wHql = "select distinct(p.contractEndYear) from ContractHistory p where p.businessClientId = :pBizClientId order by p.contractEndYear desc";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pBizClientId", pBizId);
        ArrayList<Integer> wRetList = (ArrayList<Integer>) wQuery.list();
        List<NamedEntityBean> wRetVal = new ArrayList<>();
        // int count;
        if (wRetList.size() > 0) {
            //count = 0;
            for (Integer o : wRetList) {
                NamedEntityBean e = new NamedEntityBean();
                e.setId(new Long(o));
                e.setName(String.valueOf(o));
                wRetVal.add(e);
            }
        }
        Collections.sort(wRetVal);
        return wRetVal;
    }

    public List<ContractHistory> getActiveContracts(int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion, int pEndMonth, int pEndYear
            , Long pEmpInstId, String pLastName, int pTypeOfContract, BusinessCertificate bc) {

        ArrayList<Object[]> wRetVal;
        List<ContractHistory> wRetList = new ArrayList<>();
        String wSql = "select ch.id,e.employeeId, e.lastName, e.firstName, coalesce(e.initials,''), ch.name,st.name, si.level, si.step,"
                + "ch.contractStartDate, ch.contractEndDate,ch.referenceNumber,ch.referenceDate,user.firstName, user.lastName,m.name,ch.lastModTs,ch.expiredDate,h.terminateDate,ch.expiredInd"
                + " from ContractHistory ch, "+IppmsUtils.getEmployeeTableName(bc)+" e, SalaryType st, SalaryInfo si, HiringInfo h, MdaDeptMap mdm, MdaInfo m,User user "
                + " where ch.employee.id = e.id and ch.salaryInfo.id = si.id and si.salaryType.id = st.id and h.employee.id = e.id "
                + "and mdm.id = e.mdaDeptMap.id and mdm.mdaInfo.id = m.id and ch.lastModBy.id = user.id and ch.businessClientId = "+bc.getBusinessClientInstId();


        if (pTypeOfContract < 2) {
            wSql += " and ch.expiredInd = :pTypeOfContract ";
        }

        if ((pEndMonth > -1) && (pEndYear > 0)) {
            wSql += "and ch.contractEndMonth >= :pEndMonth and ch.contractEndYear >= :pEndYear ";
        }

        if (pEmpInstId != null && pEmpInstId > 0) {
            wSql += "and e.id = :pEmpInstId ";
        }

        if (StringUtils.isNotBlank(pLastName)) {
            wSql += "and e.lastName like :pLastNameVal";
        }

        wSql += " order by e.lastName,e.firstName, coalesce(e.initials,'')";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wSql);

        if (StringUtils.isNotBlank(pLastName)) {
            wQuery.setParameter("pLastNameVal", "%" + pLastName + "%");
        }

        if (pTypeOfContract < 2) {
            wQuery.setParameter("pTypeOfContract", pTypeOfContract);
        }

        if ((pEndMonth > -1) && (pEndYear > 0)) {
            wQuery.setParameter("pEndMonth", pEndMonth);
            wQuery.setParameter("pEndYear", pEndYear);
        }

        if (pEmpInstId != null && pEmpInstId > 0) {
            wQuery.setParameter("pEmpInstId", pEmpInstId);
        }

        wRetVal = (ArrayList<Object[]>) wQuery.list();
        int i = 0;
        if (wRetVal.size() > 0) {
            ContractHistory s = new ContractHistory();
            String lastName;
            String firstName;
            String initials;
            Object mda ;
            Object lastMod;
            Object expiredDate;
            Object terminDate;
            for (Object[] o : wRetVal) {

                s.setId((Long) o[i++]);
                s.setOgNumber((String) o[i++]);
                 lastName = ((String) o[i++]);
                 firstName = ((String) o[i++]);
                 initials = ((String) o[i++]);
                s.setEmployeeName(PayrollHRUtils.createDisplayName(lastName, firstName, initials));
                s.setName(((String) o[i++]));
                s.setContractType((String) o[i++]); //Salary Type Name.
                String level = Integer.toString((Integer) o[i++]);
                String step = Integer.toString((Integer) o[i++]);
                if (step.length() < 2) {
                    step = "0" + step;
                }
                s.setContractLength(level + "." + step);//Level & Step
                s.setContractStartDate(((LocalDate) o[i++]));
                s.setContractEndDate((LocalDate) o[i++]);
                s.setReferenceNumber((String) o[i++]);
                s.setReferenceDate((LocalDate) o[i++]);
                s.setChangedBy(o[i++] + " " + o[i++]);
                 mda = o[i++];
                 lastMod = o[i++];
                 expiredDate = o[i++];
                 terminDate = o[i++];
                    s.setMdaName((String) mda);

                    s.setLastModTs((Timestamp) lastMod);

                    if (expiredDate != null)
                        s.setExpiredDate((LocalDate) expiredDate);

                    if (terminDate != null) {
                        s.setTerminationDate((LocalDate) terminDate);
                    }
                s.setExpiredInd((int) o[i++]);
                wRetList.add(s);
                s = new ContractHistory();
                i = 0;
            }

        }

        return wRetList;
    }

    public int getTotalNumberOfActiveContracts(int pEndMonth, int pEndYear, Long pEmpInstId, String pLastName, int pTypeOfContract, BusinessCertificate bc) {


        String wSql = "select count(ch.id)"
                + " from ContractHistory ch, "+IppmsUtils.getEmployeeTableName(bc)+" e, SalaryType st, SalaryInfo si, HiringInfo h, MdaDeptMap mdm, MdaInfo m,User user "
                + " where ch.employee.id = e.id and ch.salaryInfo.id = si.id and si.salaryType.id = st.id and h.employee.id = e.id "
                + "and mdm.id = e.mdaDeptMap.id and mdm.mdaInfo.id = m.id and ch.lastModBy.id = user.id and ch.businessClientId = "+bc.getBusinessClientInstId();
        if (pTypeOfContract < 2)
            wSql += " and ch.expiredInd = " + pTypeOfContract + " ";

        if (pEndMonth > -1 && pEndYear > 0)
            wSql += "and ch.contractEndMonth >= " + pEndMonth + " and ch.contractEndYear >= " + pEndYear + " ";

        if (pEmpInstId != null && pEmpInstId > 0)
            wSql += "and e.id = " + pEmpInstId + " ";

        if (StringUtils.isNotBlank(pLastName))
            wSql += "and upper(e.lastName) like :pLastNameVal";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wSql);

        if (StringUtils.isNotBlank(pLastName)) {
            wQuery.setParameter("pLastNameVal", "%" + pLastName + "%");
        }


        List<Long> wRetList = wQuery.list();


        if ((wRetList != null) && (!wRetList.isEmpty())) {
            return wRetList.get(0).intValue();
        } else {
            return 0;
        }


    }

    public List<SalaryInfo> loadSalaryInfoByRankInfo(Rank pR, Long businessClientId, boolean pUseLevelAndStep) throws IllegalAccessException, InstantiationException {
        List<SalaryInfo> wRetList = new ArrayList<>();
        pUseLevelAndStep = false; //MUST BE FALSE INITIALLY
        boolean defaultUse = false;
        Rank rank = genericService.loadObjectById(Rank.class,pR.getId());
        String hqlQuery = "";
        if(rank.getCadre().getSalaryType() == null || rank.getCadre().getSalaryType().isNewEntity()){
            hqlQuery = "select s.id,s.level,s.step,st.name from SalaryInfo s,SalaryType st where s.salaryType.id = st.id "
                    + "and st.selectableInd = 1 and s.businessClientId = :pBizIdVar ";
            pUseLevelAndStep = true; //RESET.
        }else {

            hqlQuery = "select s.id,s.level,s.step,st.name from SalaryInfo s, Rank r, Cadre c, SalaryType st where s.salaryType.id = "
                    + "c.salaryType.id and r.cadre.id = c.id and c.salaryType.id = st.id and st.selectableInd = 1 "
                    + "and s.level >= r.fromLevel and s.level <= r.toLevel and s.step >= r.fromStep and s.step <= r.toStep "
                    + "and r.id = :pRankIdVar and r.businessClientId = :pBizIdVar ";
            defaultUse = true;
        }
        if (pUseLevelAndStep)
            hqlQuery = hqlQuery + " and r.fromLevel = :pFromLevel and r.toLevel = :pToLevel and r.fromStep = :pFromStep and r.toStep = :pToStep ";
        else
            hqlQuery += " order by s.step asc";

        Query query = getSessionFactory().getCurrentSession().createQuery(hqlQuery);
        ArrayList<Object[]> wRetVal = new ArrayList();

        query.setParameter("pBizIdVar", businessClientId);
        if(defaultUse)
            query.setParameter("pRankIdVar", pR.getId());
        if (pUseLevelAndStep  ) {
            query.setParameter("pFromLevel", pR.getFromLevel());
            query.setParameter("pToLevel", pR.getToLevel());
            query.setParameter("pFromStep", pR.getFromStep());
            query.setParameter("pToStep", pR.getToStep());
        }
        wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0)
            for (Object[] o : wRetVal) {
                SalaryInfo s = new SalaryInfo();
                s.setId((Long) o[0]);
                s.setLevel((Integer) o[1]);
                s.setStep((Integer) o[2]);
                s.setDescription((String) o[3]);
                s.setName(s.getDescription());
                wRetList.add(s);
            }
        return wRetList;
    }

    public List<SalaryInfo> loadLevelAndStepBySalaryTypeId(Long pSalTypeId) {

        String wHqlStr = "select distinct s.id,s.level,s.step from SalaryInfo s " +
                " where s.salaryType.id = :pSalaryTypeId order by s.level,s.step";

        Query query = this.getSessionFactory().getCurrentSession().createQuery(wHqlStr);
        query.setParameter("pSalaryTypeId", pSalTypeId);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        List<SalaryInfo> wRetList = new ArrayList<>();
        if (wRetVal.size() > 0) {
            int i = 0;
            for (Object[] o : wRetVal) {

                SalaryInfo s = new SalaryInfo((Long) o[i++]);
                s.setLevel((Integer) o[i++]);
                s.setStep((Integer) o[i++]);
                wRetList.add(s);
                i = 0;
            }
        }
        return wRetList;

    }

    public List<HiringInfo> loadPayableActiveHiringInfoByBusinessId(BusinessCertificate businessCertificate, int pStartRow, int pEndRow) {
        List<HiringInfo> hiringInfoList;
        if (businessCertificate.isPensioner()) {
            hiringInfoList = genericService.loadPaginatedObjects(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("pensionEndFlag",  0), CustomPredicate.procurePredicate("monthlyPensionAmount",  0, Operation.GREATER)), pStartRow, pEndRow, null, null);
        } else {
            hiringInfoList = genericService.loadPaginatedObjects(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("terminateInactive", "N")), pStartRow, pEndRow, null, null);
        }
        return hiringInfoList;
    }

    public int getTotalNoOfPayableEmployees(BusinessCertificate bc, LocalDate pStartDate, LocalDate pEndDate) {
        String hqlQuery = "";
        if (bc.isPensioner()) {
            hqlQuery = "select count(h.id) from HiringInfo h where h.businessClientId = :pBizIdVar and pensionEndFlag = 0";
        } else {
            hqlQuery = "select count(h.id) from HiringInfo h, Employee e " +
                    " where e.id = h.employee.id and h.businessClientId = e.businessClientId and" +
                    " e.businessClientId = :pBizIdVar and (h.terminateInactive = 'N' or h.staffInd = 1 or " +
                    "(h.terminateDate >= :pBeginDate and h.terminateDate <= :pEndDate)) and " +
                    " h is not null";
        }
        int wRetVal = 0;

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        if (!bc.isPensioner()) {
            query.setParameter("pBeginDate", pStartDate);
            query.setParameter("pEndDate", pEndDate);
        }
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        List<Long> results = query.list();
        if ((results != null) && (!results.isEmpty())) {
            wRetVal = results.get(0).intValue();
        }

        return wRetVal;
    }

    public int getTotalNoOfMdaProcessed(BusinessCertificate bc){
        String hqlQuery="SELECT count(distinct mi.id) FROM MdaInfo mi, MdaDeptMap mdm, "+IppmsUtils.getEmployeeTableName(bc)+" p " +
                "where mdm.mdaInfo.id = mi.id and p.mdaDeptMap.id = mdm.id";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        Long results = (Long)query.uniqueResult();

        return results.intValue();
    }


    public HashMap<Long, SuspensionLog> loadToBePaidSuspendedEmployees(BusinessCertificate bc) {

        HashMap wRetList = new HashMap();

        ArrayList<Object[]> wRetVal;
        String hqlQuery = "";
        if (bc.isPensioner()) {
            hqlQuery = "select e.id,s.payPercentage from SuspensionLog s,HiringInfo h , Pensioner e " +
                    "where s.pensioner.id = h.pensioner.id and e.id = h.pensioner.id and h.suspended = 1 " +
                    "and h.suspensionDate = s.suspensionDate and s.payPercentage > 0";
        } else {
            hqlQuery = "select e.id,s.payPercentage from SuspensionLog s,HiringInfo h , Employee e " +
                    "where s.employee.id = h.employee.id and e.id = h.employee.id and h.suspended = 1 " +
                    "and h.suspensionDate = s.suspensionDate and s.payPercentage > 0";
        }


        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                SuspensionLog e = new SuspensionLog();
                e.setId((Long) o[0]);
                e.setPayPercentage(((Double) o[1]));
                wRetList.put(e.getId(), e);
            }

        }

        return wRetList;
    }

   /* public Map<Long, Long> loadEmployeesWithLastPayZero(BusinessCertificate businessCertificate, LocalDate pLastPay) {

        String wHql = "select p.employee.id from " + IppmsUtils.getPaycheckTableName(businessCertificate) + " p " +
                "where (p.netPay = 0 or p.payByDaysInd = 1) and p.runMonth = :pRunMonth " +
                "and p.runYear = :pRunYear";


        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pLastPay.getMonthValue());
        wQuery.setParameter("pRunYear", pLastPay.getYear());

        HashMap<Long, Long> wRetMap = new HashMap<Long, Long>();

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Object[] o : wRetVal) {

                wRetMap.put((Long) o[0], (Long) o[1]);
            }

        }

        return wRetMap;
    }
*/


    @Transactional()
    public synchronized void updateHiringInfoUsingHql(String hqlQuery, LocalDate payPeriodStart) {

        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pPPS", payPeriodStart);
        query.executeUpdate();
    }

    public List<HiringInfo> loadActiveHiringInfoByMDAPInstId(BusinessCertificate businessCertificate, MdaInfo mdaInfo, LocalDate pPayPeriodStart, LocalDate pPayPeriodEnd, PfaInfo defPfaInfo) {

        ArrayList<Object[]> wRetVal;
        List<HiringInfo> wRetList = new ArrayList<>();
        if(businessCertificate.isPensioner())
            return this.loadActiveHiringInfoForPensions(businessCertificate, mdaInfo,  pPayPeriodStart,  pPayPeriodEnd,  defPfaInfo);
        String hqlQuery = "select h.id, h.birthDate,h.hireDate,h.lastPayPeriod,h.currentPayPeriod,h.lastPayDate," +
                    "e.id, e.salaryInfo.id, e.firstName, e.lastName,h.ltgLastPaid,h.lastPromotionDate," +
                    "h.nextPromotionDate,adm.id,h.suspended,h.staffInd,h.payRespAllowanceInd," +
                    "h.contractEndDate,h.contractStartDate, h.contractExpiredInd, " +
                    "h.terminateDate,et.id,et.politicalInd,p.bankBranches.id,p.accountNumber," +
                    " e.schoolInfo.id,e.payApprInstId,p.bvnNo,h.pensionPinCode,h.pfaInfo.id,h.monthlyPensionAmount,e.initials,a.id,e.biometricInfo.id,h.terminateReason.id,e.employeeId  from HiringInfo h, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, " +
                    "MdaInfo a,MdaDeptMap adm, EmployeeType et,PaymentMethodInfo p " +
                    "where e.id = h."+businessCertificate.getEmployeeIdJoinStr()+" and (h.terminateInactive = 'N' or h.staffInd = 1 or " +
                    "(h.terminateDate >= :pBeginDate and h.terminateDate <= :pEndDate)) " +
                    "and e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.employeeType.id =  et.id " +
                    "and p."+businessCertificate.getEmployeeIdJoinStr()+" = e.id and a.id = :pMdaTypeId and e.businessClientId = :pBusId and h is not null";




        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);


        query.setParameter("pBeginDate", pPayPeriodStart);
        query.setParameter("pEndDate", pPayPeriodEnd);
        query.setParameter("pMdaTypeId", mdaInfo.getId());
        query.setParameter("pBusId", businessCertificate.getBusinessClientInstId());

        wRetVal = (ArrayList<Object[]>) query.list();

         if (wRetVal.size() > 0) {
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

                Employee e = new Employee((Long) o[6]);
                e.setSalaryInfo(new SalaryInfo((Long) o[7]));
                e.setFirstName((String) o[8]);
                e.setLastName((String) o[9]);
                e.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                if (null != o[10]) {
                    h.setLtgLastPaid((LocalDate) o[10]);
                }
                if (null != o[11]) {
                    h.setLastPromotionDate((LocalDate) o[11]);
                }
                if (null != o[12]) {
                    h.setNextPromotionDate((LocalDate) o[12]);
                }

                e.setMdaDeptMap(new MdaDeptMap((Long) o[13]));

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

                if (o[20] != null)
                    h.setTerminateDate((LocalDate) o[20]);
                else {
                    h.setTerminateDate(null);
                }
                h.setEmployee(e);
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
                if (o[25] != null)
                     h.getEmployee().setSchoolInstId((Long)o[25]);

                if (o[26] != null)
                    h.getEmployee().setPayApprInstId((Long) o[26]);

                if (o[27] != null) {
                    h.setBvnNo((String) o[27]);
                } else {
                    h.setBvnNo(null);
                }

                if (o[28] != null) {
                    h.setPensionPinCode((String) o[28]);
                } else {
                    h.setPensionPinCode(null);
                }
                if (o[29] != null) {
                    h.setPfaInfo(new PfaInfo((Long) o[29]));
                } else {
                    h.setPfaInfo(defPfaInfo);
                }
                if(businessCertificate.isPensioner())
                    h.setMonthlyPensionAmount((Double)o[30]);
                if(o[31] != null)
                    h.getEmployee().setInitials((String)o[31]);
                else
                    h.getEmployee().setInitials("");
                h.getEmployee().getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[32]));
                if(o[33] != null)
                    h.getEmployee().setBiometricId((Long)o[33]);
                if(null != o[34])
                    h.setTermId((Long)o[34]);

                h.getEmployee().setEmployeeId((String)o[35]);
                if(h.getContractEndDate() != null && h.getTerminateDate() != null){
                    if(!addRecord(h,pPayPeriodStart))
                        continue;;
                }

                wRetList.add(h);
            }

        }

        return wRetList;

    }

    private boolean addRecord(HiringInfo h, LocalDate pPayPeriodStart) {
        if(h.getTerminateDate().isBefore(pPayPeriodStart) && h.getContractEndDate().isBefore(pPayPeriodStart))
            return false;
        else
            if(h.getTerminateDate().isBefore(pPayPeriodStart) && h.getContractEndDate().isAfter(pPayPeriodStart))
                return  true;
            else if (h.getTerminateDate().isAfter(pPayPeriodStart) && h.getContractEndDate().isAfter(pPayPeriodStart))
                return true;

            return false;
    }

    private List<HiringInfo> loadActiveHiringInfoForPensions(BusinessCertificate businessCertificate, MdaInfo mdaInfo, LocalDate pPayPeriodStart, LocalDate pPayPeriodEnd, PfaInfo defPfaInfo) {
        ArrayList<Object[]> wRetVal;
        List<HiringInfo> wRetList = new ArrayList<>();

        String hqlQuery = "select h.id, h.birthDate,h.hireDate,h.lastPayPeriod,h.currentPayPeriod,h.lastPayDate," +
                    "e.id, e.salaryInfo.id, e.firstName, e.lastName,h.ltgLastPaid,h.lastPromotionDate," +
                    "h.nextPromotionDate,adm.id,h.suspended,h.staffInd,h.payRespAllowanceInd," +
                    "h.contractEndDate,h.contractStartDate, h.contractExpiredInd, " +
                    "h.pensionEndDate,et.id,et.politicalInd,p.bankBranches.id,p.accountNumber," +
                    " h.amAliveDate,e.payApprInstId,p.bvnNo,h.pensionPinCode,h.pfaInfo.id,h.monthlyPensionAmount,e.initials,e.biometricInfo.id from HiringInfo h, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e, " +
                    "MdaInfo a,MdaDeptMap adm, EmployeeType et,PaymentMethodInfo p " +
                    "where e.id = h."+businessCertificate.getEmployeeIdJoinStr()+" and h.pensionEndDate is null " +
                    "and e.mdaDeptMap.id = adm.id and adm.mdaInfo.id = a.id and e.employeeType.id =  et.id " +
                    "and p."+businessCertificate.getEmployeeIdJoinStr()+" = e.id and a.id = :pMdaTypeId and e.businessClientId = :pBusId and h is not null";



        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pMdaTypeId", mdaInfo.getId());
        query.setParameter("pBusId", businessCertificate.getBusinessClientInstId());

        wRetVal = (ArrayList<Object[]>) query.list();
         if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                HiringInfo h = new HiringInfo();
                h.setId((Long) o[0]);
                h.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                h.setBirthDate((LocalDate) o[1]);
                h.setHireDate((LocalDate) o[2]);
                h.setLastPayPeriod((String) o[3]);
                h.setCurrentPayPeriod((String) o[4]);
                h.setLastPayDate((LocalDate) o[5]);

                Pensioner e = new Pensioner((Long) o[6]);
                e.setSalaryInfo(new SalaryInfo((Long) o[7]));
                e.setFirstName((String) o[8]);
                e.setLastName((String) o[9]);
                e.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                if (null != o[10]) {
                    h.setLtgLastPaid((LocalDate) o[10]);
                }
                if (null != o[11]) {
                    h.setLastPromotionDate((LocalDate) o[11]);
                }
                if (null != o[12]) {
                    h.setNextPromotionDate((LocalDate) o[12]);
                }

                e.setMdaDeptMap(new MdaDeptMap((Long) o[13]));

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

                        h.setAmAliveDate((LocalDate)o[25]);

                } else {

                        h.setAmAliveDate(null);


                }


                if (o[26] != null) {
                    h.getAbstractEmployeeEntity().setPayApprInstId((Long) o[26]);
                } else {
                    h.getAbstractEmployeeEntity().setPayApprInstId(null);
                }
                if (o[27] != null) {
                    h.setBvnNo((String) o[27]);
                } else {
                    h.setBvnNo(null);
                }

                if (o[28] != null) {
                    h.setPensionPinCode((String) o[28]);
                } else {
                    h.setPensionPinCode(null);
                }
                if (o[29] != null) {
                    h.setPfaInfo(new PfaInfo((Long) o[29]));
                } else {
                    h.setPfaInfo(defPfaInfo);
                }
                if(businessCertificate.isPensioner())
                    h.setMonthlyPensionAmount((Double)o[30]);
                if(o[31] != null)
                    h.getAbstractEmployeeEntity().setInitials((String)o[31]);
                else
                    h.getAbstractEmployeeEntity().setInitials("");
                if(o[32] != null)
                    h.getAbstractEmployeeEntity().setBiometricId((Long)o[32]);
                wRetList.add(h);
            }

        }

        return wRetList;

    }

    @Transactional()
    public synchronized void updateHiringInfoUsingHqlWidoutParameter(String hqlQuery) {
        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.executeUpdate();
    }
    @Transactional()
    public void storeEmployeePayBeanList(BusinessCertificate businessCertificate,List<Object> pEmpPayBeanList, LocalDate pPayPeriodStart,  LocalDate pPayPeriodEnd, LocalDate pPayDate,
           HashMap<Long,HashMap<Long, Long>> wEmpSpecAllowMap,HashMap<Long,  HashMap<Long, Long>> wEmpDeductionMap  ,HashMap<Long, HashMap<Long, Long>> wEmpLoanMap,HashMap<Long, Long> wEmpPayCheckGratMap) throws Exception{
        if (!pEmpPayBeanList.isEmpty())
        {
            org.hibernate.Session session =sessionFactory.getCurrentSession();

            int i = 0;
            int wListSize = pEmpPayBeanList.size();
           // BusinessClient businessClient = new BusinessClient(businessCertificate.getBusinessClientInstId());
            try
            {
                for (Object e : pEmpPayBeanList) {
                    i++;

                    ((AbstractPaycheckEntity)e).setPayPeriodStart(pPayPeriodStart);
                    ((AbstractPaycheckEntity)e).setPayPeriodEnd(pPayPeriodEnd);
                    ((AbstractPaycheckEntity)e).setPayDate(pPayDate);
                    ((AbstractPaycheckEntity)e).setStatus("P");
                    ((AbstractPaycheckEntity)e).setPayStatus("N");
                    ((AbstractPaycheckEntity)e).setRunMonth(pPayPeriodEnd.getMonthValue());
                    ((AbstractPaycheckEntity)e).setRunYear(pPayPeriodEnd.getYear());


                    session.saveOrUpdate(e);

                    List<AbstractDeductionEntity> empDedInfo = ((AbstractPaycheckEntity)e).getEmployeeDeductions();

                    if (empDedInfo != null) {

                        for (AbstractDeductionEntity emp : empDedInfo) {

                            Object pDed =   IppmsUtils.getPaycheckDeductionClass(businessCertificate).newInstance();

                            pDed = IppmsUtilsExt.setDeductionRequiredValues(businessCertificate, pDed, ((AbstractPaycheckEntity) e).getId(),e);

                            ((AbstractPaycheckDeductionEntity)pDed).setBusinessClientId(businessCertificate.getBusinessClientInstId());

                            ((AbstractPaycheckDeductionEntity)pDed).setEmpDedInfo(emp);
                            ((AbstractPaycheckDeductionEntity)pDed).setAmount(emp.getAmount());
                            ((AbstractPaycheckDeductionEntity)pDed).setPayDate(pPayDate);
                            ((AbstractPaycheckDeductionEntity)pDed).setPayPeriodStart(pPayPeriodStart);
                            ((AbstractPaycheckDeductionEntity)pDed).setPayPeriodEnd(pPayPeriodEnd);
                            ((AbstractPaycheckDeductionEntity)pDed).setRunMonth(pPayPeriodEnd.getMonthValue());
                            ((AbstractPaycheckDeductionEntity)pDed).setRunYear(pPayPeriodEnd.getYear());
                            ((AbstractPaycheckDeductionEntity)pDed).setSortCode(emp.getSortCode());
                            ((AbstractPaycheckDeductionEntity)pDed).setAccountNumber(emp.getAccountNumber());

                            if(wEmpDeductionMap != null && !wEmpDeductionMap.isEmpty()){
                                HashMap<Long,Long> map =  (wEmpDeductionMap.get(((AbstractPaycheckEntity)e).getParentObject().getId()));
                                if(map != null){
                                    Long id = map.get(emp.getId());
                                    if(id != null){
                                        ((AbstractPaycheckDeductionEntity)pDed).setId(id);
                                    }
                                }

                            }
                            session.saveOrUpdate(pDed);
                        }
                    }
                    List<AbstractGarnishmentEntity> empGarnInfo =  ((AbstractPaycheckEntity)e).getEmployeeGarnishments();

                    if (empGarnInfo != null) {
                        for (AbstractGarnishmentEntity empGarn : empGarnInfo) {
                            Object pGarn = IppmsUtils.getPaycheckGarnishmentClass(businessCertificate).newInstance();

                            pGarn = IppmsUtilsExt.setLoanRequiredValues(businessCertificate, pGarn,((AbstractPaycheckEntity) e).getId(),e,empGarn);
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setBusinessClientId(businessCertificate.getBusinessClientInstId());

                            ((AbstractPaycheckGarnishmentEntity)pGarn).setAmount(empGarn.getActGarnAmt());
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setPayDate(pPayDate);
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setPayPeriodStart(pPayPeriodStart);
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setPayPeriodEnd(pPayPeriodEnd);
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setRunMonth(pPayPeriodEnd.getMonthValue());
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setRunYear(pPayPeriodEnd.getYear());
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setSortCode(empGarn.getSortCode());
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setAccountNumber(empGarn.getAccountNumber());
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setStartingLoanBalance(empGarn.getOldOwedAmount());
                            ((AbstractPaycheckGarnishmentEntity)pGarn).setAftGarnBal(empGarn.getCurrentOwedAmount());


                            if(wEmpLoanMap != null && !wEmpLoanMap.isEmpty()){
                                HashMap<Long,Long> map =  (wEmpLoanMap.get(((AbstractPaycheckEntity)e).getParentObject().getId()));
                                if(map != null){
                                    Long id = map.get(empGarn.getId());
                                    if(id != null){
                                        ((AbstractPaycheckGarnishmentEntity)pGarn).setId(id);
                                    }
                                }

                            }
                            session.saveOrUpdate(pGarn);

                        }
                    }
                    List<AbstractSpecialAllowanceEntity> empAllowInfo = ((AbstractPaycheckEntity)e).getSpecialAllowanceList();
                    if (empAllowInfo != null) {
                        for (AbstractSpecialAllowanceEntity empSpecAllow : empAllowInfo) {
                            Object pGarn = IppmsUtils.makePaycheckSpecAllowClass(businessCertificate).newInstance();

                            pGarn = IppmsUtilsExt.setSpecAllowReqValues(businessCertificate, pGarn,((AbstractPaycheckEntity) e).getId(),e,empSpecAllow);
                            ((AbstractPaycheckSpecAllowEntity)pGarn).setBusinessClientId(businessCertificate.getBusinessClientInstId());

                            ((AbstractPaycheckSpecAllowEntity)pGarn).setAmount(empSpecAllow.getActAllowAmt());
                            ((AbstractPaycheckSpecAllowEntity)pGarn).setPayDate(pPayDate);
                            ((AbstractPaycheckSpecAllowEntity)pGarn).setPayPeriodStart(pPayPeriodStart);
                            ((AbstractPaycheckSpecAllowEntity)pGarn).setPayPeriodEnd(pPayPeriodEnd);
                            ((AbstractPaycheckSpecAllowEntity)pGarn).setRunMonth(pPayPeriodEnd.getMonthValue());
                            ((AbstractPaycheckSpecAllowEntity)pGarn).setRunYear(pPayPeriodEnd.getYear());

                            if(wEmpSpecAllowMap != null && !wEmpSpecAllowMap.isEmpty()){
                                HashMap<Long,Long> map =  (wEmpLoanMap.get(((AbstractPaycheckEntity)e).getParentObject().getId()));
                                if(map != null){
                                    Long id = map.get(empSpecAllow.getId());
                                    if(id != null){
                                        ((AbstractPaycheckSpecAllowEntity)pGarn).setId(id);
                                    }
                                }

                            }
                            session.saveOrUpdate(pGarn);
                        }

                    }
                    if(businessCertificate.isPensioner()) {
                        PaycheckGratuity wPG = ((AbstractPaycheckEntity) e).getPaycheckGratuity();
                        if (wPG != null) {
                            wPG.setParentInstId(((AbstractPaycheckEntity) e).getId());
                            wPG.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                            wPG.setPayDate(pPayDate);
                            wPG.setPayPeriodStart(pPayPeriodStart);
                            wPG.setPayPeriodEnd(pPayPeriodEnd);
                            wPG.setPensioner(new Pensioner(((AbstractPaycheckEntity) e).getParentObject().getId()));

                            if(wEmpPayCheckGratMap != null && !wEmpPayCheckGratMap.isEmpty()){
                                Long id = wEmpPayCheckGratMap.get(wPG.getPensioner().getId());
                                if(id != null){
                                    wPG.setId(id);

                                }
                            }
                            session.saveOrUpdate(wPG);
                            //TODO Move to Approval...
                           // updateEmployeeGratuityUsingHql(wPG.getGratuityInfo().getId(), wPG.getAmount());
                        }
                    }
                        if ((i % 50 == 0) || (i == wListSize)) {
                        session.flush();
                        session.clear();
                    }
                }

            }
            catch (Exception wEx)
            {
                log.error(wEx.getMessage());
                wEx.printStackTrace();
                if (session != null)
                {
                    session.flush();
                    session.clear();
                }
            }
        }
    }

    @Transactional()
    private void updateEmployeeGratuityUsingHql(Long pGratuityId,  double pAmount) {

        String sql = "update GratuityInfo g set g.outstandingAmount = (g.outstandingAmount - :pAmountPaid) where g.id = :pGratId";

        Query query = sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameter("pAmountPaid", pAmount);
        query.setParameter("pGratId", pGratuityId);

        query.executeUpdate();

    }

    public List<NamedEntityBean> loadPayrollSummaryByRunMonthAndYear(BusinessCertificate pBizCert, int pRunMonth, int pRunYear) {
        ArrayList<Object[]> wRetVal = new ArrayList<>();
        List wRetList = new ArrayList();

        String hqlQuery = "select sum(p.totalPay), sum(p.netPay), sum(p.totalDeductions),  " +
                "s.level, et.id, et.employeeTypeCode, count(p.employee.id),et.name " +
                "from "+IppmsUtils.getPaycheckTableName(pBizCert)+" p, EmployeeType et, SalaryInfo s " +
                "where p.runMonth = :pRunMonth and p.runYear = :pRunYear " +
                "and p.salaryInfo.id = s.id and p.employeeType.id = et.id and p.netPay > 0 " +
                " group by s.level,et.id, et.employeeTypeCode,et.name ";

        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);

        wRetVal = (ArrayList)query.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                NamedEntityBean p = new NamedEntityBean();
                p.setTotalPay((Double)o[0]);
                p.setNetPay((Double)o[1]);
                p.setTotalDeductions((Double)o[2]);
                p.setPageSize((Integer)o[3]);
                p.setId((Long)o[4]);
                p.setObjectInd((Integer)o[5]);
                p.setNoOfActiveEmployees(((Long)o[6]).intValue());
                p.setTypeOfEmpType(p.getObjectInd() +":"+ p.getPageSize());
                p.setName((String)o[7]);
                wRetList.add(p);
            }

        }

        return wRetList;
    }

    public List<EmpDeductMiniBean> loadTPSEmployeeByRunMonthAndYear(boolean forExcel, int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion,
                                                                    int pRunMonth, int pRunYear, Long pPfaInstId, Long pMdaInstId, boolean pUseRule, boolean pIncludeTerminated, boolean pTps){

        String wHql = "";

        String wTpsPartForRule = " and h.expectedDateOfRetirement is not null "
                + "and (h.expectedDateOfRetirement <= :pTPSEndDate and h.hireDate <= :pTPSStartDate) ";

        String wCpsPartForRule =  " and h.expectedDateOfRetirement is not null "
                + "and ( not (h.expectedDateOfRetirement <= :pTPSEndDate and h.hireDate <= :pTPSStartDate)) " ;

        String wTpsPartNoRule =  " and emp.contributoryPension = 0 ";
        String wCpsPartNoRule = " and emp.contributoryPension > 0 " ;

        if(pMdaInstId > 0){


            if(pUseRule){
                wHql = "select emp.netPay,emp.suspendedInd,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, " +
                        "a.id,a.name,a.codeName, emp.contributoryPension , h.expectedDateOfRetirement, h.hireDate, h.birthDate,s.level,s.step,h.terminateDate,st.name " +
                        "from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s, SalaryType st,MdaDeptMap adm, MdaInfo a"
                        + " where emp.employee.id = e.id and s.salaryType.id = st.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";
                if(pTps){
                    wHql += wTpsPartForRule;

                }else{
                    wHql += wCpsPartForRule;
                }
                wHql += "and adm.id = emp.mdaDeptMap.id and adm.mdaInfo.id = a.id and a.id = "+pMdaInstId+" ";

            }else{
                wHql = "select emp.netPay,emp.suspendedInd,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, " +
                        ".id,a.name,a.codeName, emp.contributoryPension , h.expectedDateOfRetirement, h.hireDate, h.birthDate,s.level,s.step,h.terminateDate,st.name " +
                        "from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s,SalaryType st, MdaDeptMap adm, MdaInfo a "
                        + " where emp.employee.id = e.id and s.salaryType.id = st.id and  " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";

                if(pTps){
                    wHql += wTpsPartNoRule;

                }else{
                    wHql += wCpsPartNoRule;
                }

                wHql += "and adm.id = emp.mdaDeptMap.id and adm.mdaInfo.id = a.id and a.id = "+pMdaInstId+" ";
            }



        }else{
            if(pUseRule){
                wHql = "select emp.netPay,emp.suspendedInd,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, " +
                        "m.id,m.name,m.codeName, emp.contributoryPension , h.expectedDateOfRetirement, h.hireDate, h.birthDate,s.level,s.step,h.terminateDate,st.name " +
                        "from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s,SalaryType st, MdaInfo m, MdaDeptMap mdm "
                        + " where emp.employee.id = e.id and s.salaryType.id = st.id and  " +
                        " h.employee.id = e.id and m.id = mdm.mdaInfo.id and emp.mdaDeptMap.id = mdm.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";
                if(pTps){
                    wHql += wTpsPartForRule;

                }else{
                    wHql += wCpsPartForRule;
                }



            }else{
                wHql = "select emp.netPay,emp.suspendedInd,e.firstName,e.lastName, e.initials,e.employeeId, emp.mdaDeptMap.id, " +
                        "m.id,m.name,m.codeName, emp.contributoryPension , h.expectedDateOfRetirement, h.hireDate, h.birthDate,s.level,s.step,h.terminateDate,st.name " +
                        "from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s,SalaryType st,MdaInfo m, MdaDeptMap mdm "
                        + " where emp.employee.id = e.id and s.salaryType.id = st.id and  " +
                        " h.employee.id = e.id and m.id = mdm.mdaInfo.id and emp.mdaDeptMap.id = mdm.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id ";
                if(pTps){
                    wHql += wTpsPartNoRule;

                }else{
                    wHql += wCpsPartNoRule;
                }
            }


        }
        if(!pIncludeTerminated)
            wHql += " and h.terminateDate is null and emp.netPay > 0 ";
        if(pPfaInstId != null && pPfaInstId > 0)
            wHql += " and h.pfaInfo.id = "+pPfaInstId;

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);

        if(pUseRule){
            wQuery.setParameter("pTPSEndDate", PayrollBeanUtils.setDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR));
            wQuery.setParameter("pTPSStartDate", PayrollBeanUtils.setDateFromString(IConstants.TPS_HIRE_DATE_STR));
        }

        if(!forExcel)
        {	if (pStartRow > 0)
            wQuery.setFirstResult(pStartRow);
            wQuery.setMaxResults(pEndRow);
        }

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        if (wRetVal.size() > 0) {
            List wRetList = new ArrayList();
            for (Object[] o : wRetVal) {
                EmpDeductMiniBean p = new EmpDeductMiniBean();
                int i = 0;
                double wNetPay = (Double)o[i++];
                int wSuspInd = (Integer)o[i++];
                String pFirstName = (String)o[i++];
                String pLastName = (String)o[i++];
                String pInitials = StringUtils.trimToEmpty((String)o[i++]);

                if(wNetPay == 0){
                    if(wSuspInd == 1){
                        p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials)+"*");
                        p.setErrorRecord(true);
                    }else{
                        p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials)+"**");
                        p.setErrorRecord(true);
                    }

                }else
                    p.setName(PayrollHRUtils.createDisplayName(pLastName, pFirstName, pInitials));

                p.setEmployeeId((String)o[i++]);
                p.setMdaDeptMap(new MdaDeptMap((Long)o[i++]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++],(String)o[i++], (String)o[i++]));

                p.setCurrentDeduction(((Double)o[i++]));
                p.setExpDateOfRetirement((LocalDate)o[i++]);
                p.setHireDate((LocalDate)o[i++]);
                p.setBirthDate((LocalDate)o[i++]);
                p.setLevel((Integer)o[i++]);
                p.setStep((Integer)o[i++]);
                p.setExpDateOfRetirement((LocalDate)o[i++]);
                p.setSalaryInfoDesc((String)o[i++]);
                wRetList.add(p);
            }

            return wRetList;
        }
        return new ArrayList();

    }


    public int getTotalNoOfTPSEmployeesByRunMonthAndRunYear(int pRunMonth,
                                                            int pRunYear,Long pPfaInstId,Long pMdaInstId,  boolean pUseRule,
                                                            boolean pIncludeTerminated,boolean pTps){

        int wRetVal = 0;
        boolean useMda = IppmsUtils.isNotNullAndGreaterThanZero(pMdaInstId);

        List list = null;
        String wHql = "";
        String wTpsPartForRule = " and h.expectedDateOfRetirement is not null "
                + "and (h.expectedDateOfRetirement <= :pTPSEndDate and h.hireDate <= :pTPSStartDate) ";

        String wCpsPartForRule =  " and h.expectedDateOfRetirement is not null "
                + "and ( not (h.expectedDateOfRetirement <= :pTPSEndDate and h.hireDate <= :pTPSStartDate)) " ;

        String wTpsPartNoRule =  " and emp.contributoryPension = 0 ";
        String wCpsPartNoRule = " and emp.contributoryPension > 0 " ;
        if(useMda){


            if(pUseRule){
                wHql  = "select count(e.id) from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s, MdaDeptMap adm, MdaInfo a"
                        + " where emp.employee.id = e.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id"
                        + " and h.expectedDateOfRetirement is not null ";
                if(pTps){
                    wHql += wTpsPartForRule;

                }else{
                    wHql += wCpsPartForRule;
                }

                wHql +="and adm.id = emp.mdaDeptMap.id and adm.mdaInfo.id = a.id and a.id = "+pMdaInstId+" ";

            }else{
                wHql = "select count(e.id) from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s, MdaDeptMap adm, MdaInfo a "
                        + " where emp.employee.id = e.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";
                if(pTps){
                    wHql += wTpsPartNoRule;

                }else{
                    wHql += wCpsPartNoRule;
                }
                wHql +="and adm.id = emp.mdaDeptMap.id  and adm.mdaInfo.id = a.id and a.id = "+pMdaInstId+" ";
            }




        }else{
            if(pUseRule){
                wHql = "select count(e.id) from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s "
                        + " where emp.employee.id = e.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id"
                        + " and h.expectedDateOfRetirement is not null ";
                if(pTps){
                    wHql += wTpsPartForRule;

                }else{
                    wHql += wCpsPartForRule;
                }



            }else{
                wHql = "select count(e.id) from Employee e, EmployeePayBean emp,HiringInfo h, SalaryInfo s "
                        + " where emp.employee.id = e.id and " +
                        " h.employee.id = e.id " +
                        "and emp.runMonth = :pRunMonth and emp.runYear = :pRunYear and emp.salaryInfo.id = s.id";
                if(pTps){
                    wHql += wTpsPartNoRule;

                }else{
                    wHql += wCpsPartNoRule;
                }

            }


        }
        if(!pIncludeTerminated){
            wHql += " and h.terminateDate is null and emp.netPay > 0";
        }
        if(pPfaInstId > 0)
            wHql += " and h.pfaInfo.id = "+pPfaInstId;

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);
        if(pUseRule){
            wQuery.setParameter("pTPSEndDate", PayrollBeanUtils.setDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR));
            wQuery.setParameter("pTPSStartDate", PayrollBeanUtils.setDateFromString(IConstants.TPS_HIRE_DATE_STR));
        }

        list = wQuery.list();

        if ((list == null) || (list.isEmpty())) {
            return 0;
        }
        String wStr = String.valueOf(list.get(0));
        wRetVal =Integer.parseInt(wStr);
        return wRetVal;

    }


    public List<LeaveBonusMasterBean> loadLeaveBonusMasterBeansForExcelDisplay(BusinessCertificate bc,
            int pYear)
    {
        String wHqlStr = "";
        List<LeaveBonusMasterBean> wRetList = new ArrayList<>();

        if(pYear > 1){
            wHqlStr = "select l.id,l.mdaInfo.id,l.mdaInfo.name, l.mdaInfo.codeName,l.runMonth,l.lastModBy,l.createdDate," +
                    "l.totalAmountPaid,l.totalNoOfEmp,l.approvedInd,l.approvedBy.id, l.approvedBy.userName,l.approvedBy.firstName"
                    + ",l.approvedBy.lastName from LeaveBonusMasterBean l " +
                    "where l.runYear = :pYearValue and l.businessClientId = :pBizIdVar";
        }else{
            wHqlStr = "select runYear,sum(totalAmountPaid),sum(totalNoOfEmp),approvedInd from LeaveBonusMasterBean where businessClientId = :pBizIdVar" +
                    " group by run_year,approvedInd " ;

        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        if(pYear > 1)
            wQuery.setParameter("pYearValue", pYear);

        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<LeaveBonusMasterBean> wRetMap = new ArrayList<>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            int i = 0;
            for (Object[] o : wRetVal) {
                LeaveBonusMasterBean n = new LeaveBonusMasterBean();

                if(pYear > 1){

                    n.setId((Long)o[i++]);
                    n.setMdaInfo(new MdaInfo((Long)o[i++], (String)o[i++] ,(String)o[i++]));
                    n.setRunMonth((Integer)o[i++]);
                    n.setLastModBy((String)o[i++]);
                    n.setCreatedDate((LocalDate)o[i++]);
                    n.setTotalAmountPaid((Double)o[i++]);
                    n.setTotalNoOfEmp((Integer)o[i++]);
                    n.setApprovedInd((Integer)o[i++]);
                    n.setApprovedBy(new User((Long)o[i++],(String)o[i++],(String)o[i++],(String)o[i++]));

                    n.setMode(n.getMdaInfo().getId() +":"+ pYear);

                }else{
                    n.setRunYear((Integer)o[i++]);
                    n.setTotalAmountPaid((Double)o[i++]);
                    n.setTotalNoOfEmp(((Long)o[2]).intValue());
                    n.setApprovedInd(((Long)o[i++]).intValue());
                    n.setId(new Long(String.valueOf(n.getRunYear())));
                }
                i = 0;
                wRetMap.add(n);
            }
        }
        return wRetList;
    }

    @Transactional
    public List<LeaveBonusMasterBean> loadLeaveBonusMasterBeansForDisplay(int pYear)
    {
        String wHqlStr = "";


        if(pYear > 1){
            wHqlStr = "select l.mdaInfo.id,l.runMonth,l.lastModBy,l.createdDate," +
                    "l.totalAmountPaid,l.totalNoOfEmp,l.approvedInd,n.id,n.userName,n.firstName,n.lastName from LeaveBonusMasterBean l, User n  " +
                    "where l.runYear = :pYearValue and l.approvedBy.id = n.id";
        }else{
            wHqlStr = "select l.runYear,sum(l.totalAmountPaid),sum(l.totalNoOfEmp),l.approvedInd " +
                    "from LeaveBonusMasterBean l" +
                    " group by l.runYear,l.approvedInd order by l.runYear desc" ;

        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        if(pYear > 1)
            wQuery.setParameter("pYearValue", pYear);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<LeaveBonusMasterBean> wRetMap = new ArrayList<>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                LeaveBonusMasterBean n = new LeaveBonusMasterBean();
                if(pYear > 1){
                    n.setMdaInfo(new MdaInfo((Long)o[0]));
                    n.setRunMonth((Integer)o[1]);
                    n.setLastModBy((String)o[2]);
                    n.setCreatedDate((LocalDate)o[3]);
                    n.setTotalAmountPaid((Double)o[4]);
                    n.setTotalNoOfEmp((Integer)o[5]);
                    n.setApprovedInd((Integer)o[6]);
                    n.setApprovedBy(new User((Long)o[7], (String)o[8], (String)o[9], (String)o[10]));
                    n.setId(n.getMdaInfo().getId());
                    n.setMode(n.getMdaInfo().getId() + ":" + pYear);
                }else{
                    n.setRunYear((Integer)o[0]);
                    n.setTotalAmountPaid((Double)o[1]);
                    n.setTotalNoOfEmp(((Long)o[2]).intValue());
                    n.setApprovedInd((Integer)o[3]);
                    n.setId(new Long(String.valueOf(n.getRunYear())));
                }

                wRetMap.add(n);
            }
        }
        return wRetMap;
    }

    public List<NamedEntityBean> loadPayrollSummaryByRunMonthAndYearForTsc(
            int pRunMonth, int pRunYear, BusinessCertificate bc)
    {
        ArrayList<Object[]> wRetVal = new ArrayList<>();
        List wRetList = new ArrayList();

        String hqlQuery = "select sum(p.totalPay), sum(p.netPay), sum(p.totalDeductions),  " +
                "s.level, et.id, et.employeeTypeCode, count(p.employee.id),et.name " +
                "from "+IppmsUtils.getPaycheckTableName(bc)+" p, EmployeeType et, SalaryInfo s " +
                "where p.runMonth = :pRunMonth and p.runYear = :pRunYear and (p.schoolInstId is not null and p.schoolInstId > 0 )" +
                "and p.salaryInfo.id = s.id and p.employeeType.id = et.id and p.netPay > 0 " +
                " group by s.level,et.id, et.employeeTypeCode,et.name ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        wQuery.setParameter("pRunMonth", pRunMonth);
        wQuery.setParameter("pRunYear", pRunYear);

        wRetVal = (ArrayList)wQuery.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                NamedEntityBean p = new NamedEntityBean();
                p.setTotalPay((Double)o[0]);
                p.setNetPay((Double)o[1]);
                p.setTotalDeductions((Double)o[2]);
                p.setParentInstId((Long)o[3]);
                p.setId((Long)o[4]);
                p.setObjectInd((Integer)o[5]);
                p.setNoOfActiveEmployees(((Long)o[6]).intValue());
                p.setTypeOfEmpType(p.getObjectInd() +":"+ p.getParentInstId());
                p.setName((String)o[7]);
                wRetList.add(p);
            }

        }

        return wRetList;
    }

    public List<EmployeePayBean> loadEmployeePayBeanByLastPayPeriodForTsc(
            int pRunMonth, int pRunYear, int pTypeCode,List<Integer> pMapIdList, int pObjectInd, Integer pSalaryTypeId, int pLevel, BusinessCertificate bc)
    {



        List<EmployeePayBean> wRetList = new ArrayList<>();

        String  wHqlQuery = "select p.id,e.id,e.employeeId, e.firstName, e.lastName,e.initials,s.level,s.step,s.salaryType.name" +
                ",p.totalPay, p.totalDeductions,p.freePay, p.taxableIncome, p.monthlyReliefAmount,p.monthlyTax,p.netPay, p.mdaDeptMap.id, m.id, m.name,m.codeName," +
                "et.id, et.employeeTypeCode,b.name,p.accountNumber,p.totalAllowance,p.contributoryPension,p.totalGarnishments, p.schoolInfo.id " +
                "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" p, SalaryInfo s, EmployeeType et, BankBranch b, MdaInfo m, MdaDeptMap mdm " +
                "where e.id = p.employee.id and p.salaryInfo.id = s.id and p.bankBranch.id = b.id and m.id = mdm.mdaInfo.id and mdm.id = p.mdaDeptMap.id " +
                "and et.id = p.employeeType.id and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar and p.netPay > 0" +
                " and p.schoolInfo.id is not null and p.schoolInfo.id > 0 ";

        if(pTypeCode > 0){
            wHqlQuery += "and et.employeeTypeCode = :pEmpTypeCodeVar ";

        }
        if(pLevel > 0){
            wHqlQuery += "and s.level = :pLevelVar ";
        }
        if(pSalaryTypeId > 0){
            wHqlQuery += "and s.salaryType.id = :pSalaryTypeVar ";
        }


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlQuery);


        wQuery.setParameter("pRunMonthVar", pRunMonth);
        wQuery.setParameter("pRunYearVar", pRunYear);

        if(pTypeCode > 0)
            wQuery.setParameter("pEmpTypeCodeVar", pTypeCode);
        if(pLevel > 0)
            wQuery.setParameter("pLevelVar", pLevel);
        if(pSalaryTypeId > 0)
            wQuery.setParameter("pSalaryTypeVar", pSalaryTypeId);

        ArrayList<Object[]> wRetVal = new ArrayList<>();

        wRetVal = (ArrayList<Object[]>)wQuery.list();


        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                EmployeePayBean p = new EmployeePayBean();
                int i = 0;
                p.setId((Long)o[i++]);
                Employee e = new Employee((Long)o[i++]);
                e.setEmployeeId((String)o[i++]);

                e.setFirstName((String)o[i++]);
                e.setLastName((String)o[i++]);
                Object wInit = o[i++];
                if(wInit == null)
                    e.setInitials(IConstants.EMPTY_STR);
                else
                    e.setInitials((String)wInit);
                p.setEmployee(e);
                SalaryInfo s = new SalaryInfo();
                s.setLevel((Integer)o[i++]);
                s.setStep((Integer)o[i++]);
                //Salary Type Name
                s.setName((String)o[i++]);
                p.setTotalPay((Double)o[i++]);
                p.setTotalDeductions((Double)o[i++]);
                p.setFreePay((Double)o[i++]);
                p.setTaxableIncome((Double)o[i++]);
                p.setMonthlyReliefAmount((Double)o[i++]);
                p.setTaxesPaid((Double)o[i++]);
                p.setNetPay((Double)o[i++]);

                p.setMdaDeptMap(new MdaDeptMap((Long)o[i++]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++], (String)o[i++], (String)o[i++]));
                p.setEmployeeName(o[i++] +":"+ s.getLevel());
                p.setObjectInd((Integer)o[i++]);
                p.setBranchName((String)o[i++]);
                p.setAccountNumber((String)o[i++]);
                p.setTotalAllowance((Double)o[i++]);
                p.setContributoryPension((Double)o[i++]);
                p.setTotalGarnishments((Double)o[i++]);

                p.setSchoolInstId((Long)o[i++]);

                p.setSalaryInfo(s);

                wRetList.add(p);
            }

        }

        return wRetList;
    }

    public List<EmployeePayBean> loadEmployeePayBeanByParentIdAndLastPayPeriod(
            int pRunMonth, int pRunYear, int pTypeCode,List<Long> pMapIdList, int pObjectInd, Long pSalaryTypeId, int pLevel, BusinessCertificate bc)
    {


        boolean wUseMapIds = (pMapIdList  != null && !pMapIdList.isEmpty()) && pObjectInd > 0;


        List<EmployeePayBean> wRetList = new ArrayList<>();

        String  wHqlQuery = "select p.id,e.id,e.employeeId, e.firstName, e.lastName,e.initials,s.level,s.step,s.salaryType.name" +
                ",p.totalPay, p.totalDeductions,p.freePay, p.taxableIncome, p.monthlyReliefAmount,p.monthlyTax,p.netPay, p.mdaDeptMap.id, mda.id, mda.name, mda.codeName," +
                "et.id, et.employeeTypeCode,b.name,p.accountNumber,p.totalAllowance,p.contributoryPension,p.totalGarnishments, p.schoolInfo.id, et.name " +
                "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" p, SalaryInfo s, EmployeeType et, BankBranch b, MdaInfo mda, MdaDeptMap m " +
                "where e.id = p.employee.id and p.salaryInfo.id = s.id and p.bankBranch.id = b.id and mda.id = m.mdaInfo.id and m.id = e.mdaDeptMap.id " +
                "and et.id = p.employeeType.id and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar and p.netPay > 0 ";

        if(pTypeCode > 0){
            wHqlQuery += "and et.employeeTypeCode = :pEmpTypeCodeVar ";

        }
        if(pLevel > 0){
            wHqlQuery += "and s.level = :pLevelVar ";
        }
        if(pSalaryTypeId > 0){
            wHqlQuery += "and s.salaryType.id = :pSalaryTypeVar ";
        }
        if(wUseMapIds)
            wHqlQuery += "and p.mdaDeptMap.id in (:pMapIdsListVar ) ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlQuery);


        wQuery.setParameter("pRunMonthVar", pRunMonth);
        wQuery.setParameter("pRunYearVar", pRunYear);

        if(pTypeCode > 0)
            wQuery.setParameter("pEmpTypeCodeVar", pTypeCode);
        if(pLevel > 0)
            wQuery.setParameter("pLevelVar", pLevel);
        if(pSalaryTypeId > 0)
            wQuery.setParameter("pSalaryTypeVar", pSalaryTypeId);
        if(wUseMapIds){
            wQuery.setParameterList("pMapIdsListVar", pMapIdList.toArray());
            // wQuery.setInteger("pObjectIndVar", pObjectInd);
        }
        ArrayList<Object[]> wRetVal;

        wRetVal = (ArrayList<Object[]>)wQuery.list();


        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                EmployeePayBean p = new EmployeePayBean();
                int i = 0;
                p.setId((Long)o[i++]);
                Employee e = new Employee((Long)o[i++]);
                e.setEmployeeId((String)o[i++]);

                e.setFirstName((String)o[i++]);
                e.setLastName((String)o[i++]);
                Object wInit = o[i++];
                if(wInit == null)
                    e.setInitials(IConstants.EMPTY_STR);
                else
                    e.setInitials((String)wInit);
                p.setEmployee(e);
                SalaryInfo s = new SalaryInfo();
                s.setLevel((Integer)o[i++]);
                s.setStep((Integer)o[i++]);
                //Salary Type Name
                s.setName((String)o[i++]);
                p.setTotalPay((Double)o[i++]);
                p.setTotalDeductions((Double)o[i++]);
                p.setFreePay((Double)o[i++]);
                p.setTaxableIncome((Double)o[i++]);
                p.setMonthlyReliefAmount((Double)o[i++]);
                p.setTaxesPaid((Double)o[i++]);
                p.setNetPay((Double)o[i++]);

                p.setMdaDeptMap(new MdaDeptMap((Long)o[i++]));
                p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++],(String)o[i++],(String)o[i++]));
                p.setEmployeeName(o[i++] +":"+ s.getLevel());
                p.setObjectInd((Integer)o[i++]);
                p.setBranchName((String)o[i++]);
                p.setAccountNumber((String)o[i++]);
                p.setTotalAllowance((Double)o[i++]);
                p.setContributoryPension((Double)o[i++]);
                p.setTotalGarnishments((Double)o[i++]);
                Object wObj = o[i++];
                if(wObj != null ){
                    p.setSchoolInfo(new SchoolInfo((Long)wObj));
                }else{
                    p.setSchoolInfo(new SchoolInfo());
                }
                p.setSalaryInfo(s);
                if(!wUseMapIds){
                    p.setMda(p.getMdaDeptMap().getMdaInfo().getCodeName());
                }
                p.setEmployeeTypeName((String)o[i++]);
                wRetList.add(p);
            }
        }
        return wRetList;
    }



    public List<EmployeePayBean> loadEmployeePayBeanByParentIdGlAndBank(
            int pRunMonth, int pRunYear, int fromLevel, int toLevel, String bank,  BusinessCertificate bc, boolean pForSchool)
    {

        List<EmployeePayBean> wRetList = new ArrayList<>();

        String  wHqlQuery = "select p.id,e.id,e.employeeId,p.firstName, p.lastName,p.initials,s.level,s.step, p.monthlyBasic, s.salaryType.name" +
                ",p.totalPay, p.totalDeductions,p.freePay, p.taxableIncome, p.monthlyReliefAmount,p.monthlyTax,p.netPay, mda.name," +
                "et.id, et.employeeTypeCode,b.name,p.accountNumber,p.totalAllowance,p.contributoryPension,p.totalGarnishments, et.name, bI.name,p.specialAllowance,p.monthlyPension " +
                "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" p, SalaryInfo s, EmployeeType et, BankBranch b, BankInfo bI, MdaInfo mda, MdaDeptMap m " +
                "where e.id = p.employee.id and p.salaryInfo.id = s.id and p.bankBranch.id = b.id and b.bankInfo.id = bI.id and mda.id = m.mdaInfo.id and m.id = e.mdaDeptMap.id " +
                "and et.id = p.employeeType.id and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar and p.netPay > 0 ";

        if(pForSchool){
            wHqlQuery = "select p.id,e.id,e.employeeId,p.firstName, p.lastName,p.initials,s.level,s.step, p.monthlyBasic, s.salaryType.name" +
                    ",p.totalPay, p.totalDeductions,p.freePay, p.taxableIncome, p.monthlyReliefAmount,p.monthlyTax,p.netPay,mda.name," +
                    "et.id, et.employeeTypeCode,b.name,p.accountNumber,p.totalAllowance,p.contributoryPension,p.totalGarnishments, et.name, bI.name, sch.name,p.specialAllowance,p.monthlyPension " +
                    "from "+IppmsUtils.getEmployeeTableName(bc)+" e, "+IppmsUtils.getPaycheckTableName(bc)+" p, SalaryInfo s, EmployeeType et, BankBranch b, BankInfo bI, MdaInfo mda, MdaDeptMap m, SchoolInfo sch " +
                    "where e.id = p.employee.id and p.salaryInfo.id = s.id and p.bankBranch.id = b.id and b.bankInfo.id = bI.id and mda.id = m.mdaInfo.id and m.id = e.mdaDeptMap.id " +
                    "and et.id = p.employeeType.id and p.runMonth = :pRunMonthVar and p.runYear = :pRunYearVar and p.netPay > 0 and p.schoolInfo.id = sch.id ";
        }


        if((fromLevel > 0) && (toLevel > 0)){
            wHqlQuery += "and s.level >= :fromLevelVar and s.level <= :toLevelVar ";
        }
        if(IppmsUtils.isNotNullOrEmpty(bank)){
            wHqlQuery += "and bI.name = :pBankVar ";
        }
        wHqlQuery += " order by p.lastName,p.firstName,p.initials";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlQuery);


        wQuery.setParameter("pRunMonthVar", pRunMonth);
        wQuery.setParameter("pRunYearVar", pRunYear);

        if(IppmsUtils.isNotNullOrEmpty(bank))
            wQuery.setParameter("pBankVar", bank);
        if((fromLevel > 0) && (toLevel > 0)) {
            wQuery.setParameter("fromLevelVar", fromLevel);
            wQuery.setParameter("toLevelVar", toLevel);
        }

        ArrayList<Object[]> wRetVal  = (ArrayList<Object[]>)wQuery.list();


        if (wRetVal.size() > 0)
        {
            EmployeePayBean p;
            int i = 0;
            Object wInit;
            Employee e;
            SalaryInfo s;
            for (Object[] o : wRetVal) {
                p = new EmployeePayBean();

                p.setId((Long)o[i++]);
                e = new Employee((Long)o[i++]);
                e.setEmployeeId((String)o[i++]);

                e.setFirstName((String)o[i++]);
                e.setLastName((String)o[i++]);
                 wInit = o[i++];
                if(wInit == null)
                    e.setInitials(IConstants.EMPTY_STR);
                else
                    e.setInitials((String)wInit);
                p.setEmployee(e);
                s = new SalaryInfo();
                s.setLevel((Integer)o[i++]);
                s.setStep((Integer)o[i++]);
                p.setMonthlyBasic((Double) o[i++]);
                //Salary Type Name
                s.setName(o[i++] + ":"+ s.getLevelAndStepAsStr());
                p.setTotalPay((Double)o[i++]);
                p.setTotalDeductions((Double)o[i++]);
                p.setFreePay((Double)o[i++]);
                p.setTaxableIncome((Double)o[i++]);
                p.setMonthlyReliefAmount((Double)o[i++]);
                p.setTaxesPaid((Double)o[i++]);
                p.setNetPay((Double)o[i++]);
                p.setMda((String)o[i++]);
                //p.setMdaDeptMap(new MdaDeptMap((Long)o[i++]));
                //p.getMdaDeptMap().setMdaInfo(new MdaInfo((Long)o[i++],(String)o[i++],(String)o[i++]));
                p.setEmployeeName(o[i++] +":"+ s.getLevel());
                p.setObjectInd((Integer)o[i++]);
                p.setBranchName((String)o[i++]);
                p.setAccountNumber((String)o[i++]);
                p.setTotalAllowance((Double)o[i++]);
                p.setContributoryPension((Double)o[i++]);
                p.setTotalGarnishments((Double)o[i++]);
                p.setSalaryInfo(s);
                p.setEmployeeTypeName((String)o[i++]);
                p.setBankName((String)o[i++]);
                if(pForSchool)
                    p.setSchoolName((String) o[i++]);


                p.setSpecialAllowance((Double)o[i++]);
                p.setMonthlyPension((Double)o[i++]);
                p.setFirstName(e.getFirstName());
                p.setLastName(e.getLastName());
                p.setInitials(e.getInitials());
                wRetList.add(p);
                i = 0;
            }
        }
        return wRetList;
    }

    public List<PayrollSimulationMasterBean> loadPayrollSimulationsMasterBean(int pStartRow, int pEndRow, String pSortOrder, String pSortCriterion)
    {
        String sql = "select l.id,l.name,l.simulationStartMonth,l.simulationStartYear,l.lastModTs,g.firstName,g.lastName "
                + "from PayrollSimulationMasterBean l, User g where l.lastModBy = g.username  order by l.lastModTs desc";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        if (pStartRow > 0)
            wQuery.setFirstResult(pStartRow);
        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal;
        ArrayList<PayrollSimulationMasterBean> wRetList = new ArrayList<>();

        wRetVal = (ArrayList<Object[]>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                PayrollSimulationMasterBean l = new PayrollSimulationMasterBean();
                l.setId((Long)o[0]);

                l.setName((String)o[1]);
                l.setSimulationStartMonth(((Integer)o[2]).intValue());
                l.setSimulationStartYear((Integer)o[3]);
                l.setCreatedDateStr(PayrollHRUtils.getFullDateFormat().format((LocalDate)o[4]));
                l.setCreatedBy(o[5] + " " + o[6]);
                l.setSimulationMonthStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(l.getSimulationStartMonth(),  l.getSimulationStartYear()));
                wRetList.add(l);
            }

        }

        return wRetList;
    }
}


