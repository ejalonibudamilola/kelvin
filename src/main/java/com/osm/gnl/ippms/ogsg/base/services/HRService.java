/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.NamedEntityLong;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.ConjunctionType;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.beans.EmpContMiniBean;
import com.osm.gnl.ippms.ogsg.domain.beans.LgaMiniBean;
import com.osm.gnl.ippms.ogsg.domain.beans.MPBAMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.SetupEmployeeMaster;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusMasterBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaType;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

import static com.osm.gnl.ippms.ogsg.web.ui.WebHelper.getBusinessCertificate;

@Service("hrService")
@Repository
@Transactional(readOnly = true)
public class HRService {
    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public boolean mdaMappingHasActiveEmployees(Long pmid, BusinessCertificate businessCertificate) {
        List<AbstractEmployeeEntity> employees = (List<AbstractEmployeeEntity>) genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getEmployeeClass(businessCertificate),
                Arrays.asList(CustomPredicate.procurePredicate("mdaDeptMap.mdaInfo.id", pmid),
                        CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("statusIndicator", 0)), null);
        return employees.size() > 0;
    }

    public AbmpBean createMBAPMiniBeanForLTGAllowance(Long pPid, Long businessClientId, boolean pSetId)
    {
        String hqlQuery =
                "select sum(s.monthlyBasicSalary/12.0D),sum((s.monthlyBasicSalary/12.0D * 1.2)),count(a.id),e.name from Employee a , " +
                        "SalaryInfo s, MdaDeptMap d, MdaInfo e where a.salaryInfo.id = s.id and a.mdaDeptMap.id = d.id" +
                        " and d.mdaInfo.id = e.id and e.id = :pIdValue and a.statusIndicator = 0 and a.businessClientId = :pBizIdVar group by e.name";

        AbmpBean d = new AbmpBean();

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pIdValue", pPid);
        query.setParameter("pBizIdVar", businessClientId);

        ArrayList <Object[]>wRetVal = new ArrayList<Object[]>();

        wRetVal = (ArrayList<Object[]>)query.list();

        Object[] o = wRetVal.get(0);

        d.setId(pPid);
        d.setMdaInfo(new MdaInfo(pPid));
        d.setBasicSalary(((Double)o[0]).doubleValue());
        d.setLtgCost(((Double)o[1]).doubleValue());
        d.setNoOfEmp(((Long)o[2]).intValue());
        d.setName((String)o[3]);
        d.getMdaInfo().setName(d.getName());

        return d;
    }

    public List<AbmpBean> createMBAPMiniBeanForLTGAllowance(Long pKey, ArrayList<Long> pList, BusinessCertificate bc)
    {
        String hqlQuery = "select sum(s.monthlyBasicSalary),sum((s.monthlyBasicSalary * 1.2)),count(a.id),e.name,e.id, e.mdaType.name from "+IppmsUtils.getEmployeeTableName(bc)+" a , "
                + "SalaryInfo s, MdaDeptMap d, MdaInfo e where a.salaryInfo.id = s.id "
                + "and a.mdaDeptMap.id = d.id and d.mdaInfo.id = e.id and a.businessClientId = :pBizId "
                + "and e.id in (:pIdValue) and a.statusIndicator = 0 group by e.name,e.id,e.mdaType.name";


        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameterList("pIdValue", pList);
        query.setParameter("pBizId", bc.getBusinessClientInstId());

        ArrayList <Object[]>wRetVal = new ArrayList<Object[]>();
        List <AbmpBean>wRetList = new ArrayList<AbmpBean>();

        wRetVal = (ArrayList<Object[]>)query.list();

        for (Object[] o : wRetVal) {
            AbmpBean d = new AbmpBean();

            d.setBasicSalary(((Double)o[0]));
            d.setLtgCost(((Double)o[1]));
            d.setNoOfEmp(((Long)o[2]).intValue());
            d.setName((String)o[3]);
            d.setMdaInfo(new MdaInfo((Long)o[4]));
            d.setObjectCode(o[4] + ":" + pKey);
            wRetList.add(d);
        }

        return wRetList;
    }
    @Transactional
    public void storeLtgDetails(List<AbmpBean> pAssignedList, String pUserName, Long pParentInstId)
    {
        for (AbmpBean a : pAssignedList)
        {
            a.setId(null);
            a.setLastModBy(pUserName);
            a.setLastModTs(LocalDate.now());
            a.setLtgMasterBean(new LtgMasterBean(pParentInstId));

            genericService.saveObject(a);
        }
    }

    public List<AbstractGarnishmentEntity> loadToBePaidEmployeeGarnishments(LocalDate pStartDate, BusinessCertificate bc) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);

        Root<?> root = query.from(IppmsUtils.getGarnishmentInfoClass(bc));
        Predicate where = builder.conjunction();
        where = builder.and(where, builder.greaterThan(PredicateBuilder.getPath("owedAmount", root), 0));
        where = builder.and(where, builder.greaterThan(PredicateBuilder.getPath("amount", root), 0));
        where = builder.and(where, builder.equal(PredicateBuilder.getPath("businessClientId", root), bc.getBusinessClientInstId()));
        query.where(where);

        query.multiselect(PredicateBuilder.getPath("id", root), PredicateBuilder.getPath("amount", root),
                PredicateBuilder.getPath("garnishCap", root), PredicateBuilder.getPath("owedAmount", root),
                PredicateBuilder.getPath("employee.id", root), PredicateBuilder.getPath("empGarnishmentType.id", root),
                PredicateBuilder.getPath("empGarnishmentType.bankBranch.branchSortCode", root), PredicateBuilder.getPath("empGarnishmentType.accountNumber", root),
                PredicateBuilder.getPath("interestAmount", root), PredicateBuilder.getPath("deductInterestSeparatelyInd", root),
                PredicateBuilder.getPath("currentLoanTerm", root), PredicateBuilder.getPath("originalLoanAmount", root),
                PredicateBuilder.getPath("startDate", root));
        TypedQuery<Object[]> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);

        List<Object[]> wRetVal = typedQuery.getResultList();
        List wRetList = new ArrayList();
        if (wRetVal.size() > 0)
        {
            AbstractGarnishmentEntity e = IppmsUtils.makeGarnishmentInfoObject(bc);;
            for (Object[] o : wRetVal) {
                LocalDate wStartDate  = (LocalDate) o[12];
                if(wStartDate == null)continue;
                if (wStartDate.isAfter(pStartDate)) continue;

                e.setId((Long)o[0]);
                e.setAmount(((Double)o[1]));
                e.setGarnishCap(((Double)o[2]));
                e.setOwedAmount(((Double)o[3]));

                e.setEmployee(new Employee((Long)o[4]));

                EmpGarnishmentType et = new EmpGarnishmentType((Long)o[5]);
                e.setSortCode((String)o[6]);
                e.setAccountNumber((String)o[7]);
                e.setInterestAmount(((Double)o[8]));
                e.setDeductInterestSeparatelyInd(((Integer)o[9]));
                e.setCurrentLoanTerm(((Integer)o[10]));
                e.setOriginalLoanAmount(((Double)o[11]));
                e.setEmpGarnishmentType(et);
                wRetList.add(e);
                e = e.getClass().getDeclaredConstructor(null).newInstance();
            }
        }

        return wRetList;
    }

    /*
     Works since Pensioners do not have LTG Simulations....
     */
    public List<AbstractDeductionEntity> loadToBePaidEmployeeDeductions(LocalDate pStartDate, LocalDate pEndDate, BusinessCertificate businessCertificate)
    {
        List wRetList = new ArrayList();

        String hqlQuery = "select e.id,e.amount,et.accountNumber,p.id,p.name,k.id," +
                "b.branchSortCode, ec.statutoryInd,et.taxable,e.startDate,e.endDate, et.id, et.dateDependent " +
                "from "+IppmsUtils.getDeductionInfoTableName(businessCertificate)+" e,HiringInfo h,PayTypes p,Employee k,BankBranch b,EmpDeductionType et," +
                "EmpDeductionCategory ec where e.employee.id = k.id and k.id = h.employee.id " +
                "and b.id = et.bankBranches.id and e.empDeductionType.id = et.id and ec.id = et.empDeductionCategory.id " +
                "and et.payTypes.id = p.id and e.businessClientId = :businessClientId and (h.terminateInactive = 'N' or (h.terminateDate >= :startDate  " +
                "and h.terminateDate <= :endDate)) and e.amount > 0";


        Query<Object[]> query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("businessClientId", businessCertificate);
        query.setParameter("startDate", pStartDate);
        query.setParameter("endDate", pEndDate);

        List<Object[]> wRetVal = query.list();

        if (wRetVal.size() > 0){
            AbstractDeductionEntity e;
            for (Object[] o : wRetVal) {
                 e = IppmsUtils.makeDeductionInfoObject(businessCertificate);

                e.setId((Long)o[0]);
                e.setAmount(((Double)o[1]));
                e.setAccountNumber((String)o[2]);
                PayTypes p = new PayTypes();
                p.setId((Long)o[3]);
                p.setName((String)o[4]);
                e.setEmployee(new Employee((Long)o[5]));
                e.setSortCode((String)o[6]);

                e.setStatutoryInd(((Integer)o[7]).intValue());
                e.setTaxExemptInd((String)o[8]);
                e.setPayTypes(p);
                if(o[9] != null){
                    e.setStartDate((LocalDate) o[9]);
                }
                if(o[10] != null){
                    e.setEndDate((LocalDate) o[10]);
                }
                EmpDeductionType wEDT = new EmpDeductionType((Long)o[11], (Integer)o[12]);
                e.setEmpDeductionType(wEDT);
                wRetList.add(e);
            }

        }

        return wRetList;
    }

    public List<AbstractSpecialAllowanceEntity> loadToBePaidEmployeeSpecialAllowances(LocalDate pStartDate, LocalDate pEndDate, BusinessCertificate bc)
    {

        List wRetList = new ArrayList();

        String hqlQuery = "select e.id,e.amount,k.id,l.id,e.startDate,e.endDate,p.id,p.name," +
                "l.taxExemptInd,l.name from "+IppmsUtils.getSpecialAllowanceInfoTableName(bc)+" e,HiringInfo h,Employee k," +
                "SpecialAllowanceType l,PayTypes p where e.employee.id = k.id and k.id = h.employee.id " +
                "and l.id = e.specialAllowanceType.id and p.id = e.payTypes.id " +
                "and k.businessClientId = :businessClientId and (h.terminateInactive = 'N' or (h.terminateDate >= :pStartDate and h.terminateDate <= :pEndDate)) " +
                "and e.expire = 0";

        Query<Object[]> query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("businessClientId", bc.getBusinessClientInstId());
        query.setParameter("pStartDate", pStartDate);
        query.setParameter("pEndDate", pEndDate);
        List<Object[]> wRetVal = query.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                AbstractSpecialAllowanceEntity e =  IppmsUtils.makeSpecialAllowanceInfoObject(bc);
                e.setId((Long)o[0]);
                e.setAmount(((Double)o[1]));
                e.setEmployee(new Employee((Long)o[2]));

                SpecialAllowanceType et = new SpecialAllowanceType((Long)o[3]);
                e.setStartDate((LocalDate) o[4]);
                e.setEndDate((LocalDate) o[5]);
                et.setTaxExemptInd(((Integer)o[8]).intValue());
                e.setSpecialAllowanceType(et);


                PayTypes p = new PayTypes((Long)o[6], (String)o[7]);

                e.setPayTypes(p);
                e.setName((String)o[9]);
                wRetList.add(e);
            }

        }

        return wRetList;
    }

    public LtgMasterBean loadLtgMasterBeanByMonthAndYear(int pMonthAsHash, int pYearInd, boolean pSimulation) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<LtgMasterBean> query = builder.createQuery(LtgMasterBean.class);
        Root<LtgMasterBean> root = query.from(LtgMasterBean.class);
        Predicate where = builder.conjunction();
        where = builder.and(where, builder.equal(PredicateBuilder.getPath("simulationMonth", root), pMonthAsHash));
        where = builder.and(where, builder.equal(PredicateBuilder.getPath("simulationYear", root), pYearInd));

        if (pSimulation)
            where = builder.and(where, builder.equal(PredicateBuilder.getPath("applicationIndicator", root), 0));
        else {
            where = builder.and(where, builder.equal(PredicateBuilder.getPath("applicationIndicator", root), 1));
        }
        query.where(where);
        List<LtgMasterBean> wLMBList = this.sessionFactory.getCurrentSession().createQuery(query).getResultList();

        if ((wLMBList == null) || (wLMBList.isEmpty()) || (wLMBList.get(0) == null)) {
            return new LtgMasterBean();
        }
        return wLMBList.get(0);
    }

    public HashMap<Long, SuspensionLog> loadToBePaidSuspendedEmployees(BusinessCertificate bc)
    {
        String hqlQuery = "select e.id,s.payPercentage from SuspensionLog s,HiringInfo h , "+IppmsUtils.getEmployeeTableName(bc)+" e " +
                "where s.employee.id = h.employee.id and e.id = h."+bc.getEmployeeTableIdJoinStr()+" and h.suspended = 1 " +
                "and h.suspensionDate = s.suspensionDate and s.payPercentage > 0 and e.businessClientId = "+bc.getBusinessClientInstId();

        Query<Object[]> query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        HashMap wRetList = new HashMap();
        ArrayList<Object[]> wRetVal = (ArrayList)query.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                SuspensionLog e = new SuspensionLog();
                e.setId((Long)o[0]);
                e.setPayPercentage(((Double)o[1]));
                wRetList.put(e.getId(), e);
            }

        }

        return wRetList;
    }
    public GenericService getGenericService() {
        return genericService;
    }

    public int getToBeTerminatedEmpCount(BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {
        ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class, CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        if(businessCertificate.isPensioner()){

            LocalDate wAmAlive = LocalDate.now().minusYears(configurationBean.getIamAlive());
            LocalDate wBirthDate = LocalDate.of(wAmAlive.getYear(), wAmAlive.getMonthValue(), wAmAlive.lengthOfMonth());
            PredicateBuilder predicateBuilder = new PredicateBuilder();

            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("birthDate", wBirthDate, Operation.LESS_OR_EQUAL));
            predicateBuilder.addBuilder(new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())));

            return this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, HiringInfo.class);
        }else {

            LocalDate wSixtyYearsAgo = LocalDate.now().minusYears(configurationBean.getAgeAtRetirement());
            LocalDate wBirthDate = LocalDate.of(wSixtyYearsAgo.getYear(), wSixtyYearsAgo.getMonthValue(), wSixtyYearsAgo.lengthOfMonth());


            LocalDate wThirtyFiveYearsAgo = LocalDate.now().minusYears(configurationBean.getServiceLength());
            LocalDate wHireDate = LocalDate.of(wThirtyFiveYearsAgo.getYear(), wThirtyFiveYearsAgo.getMonthValue(), wThirtyFiveYearsAgo.lengthOfMonth());

            PredicateBuilder predicateBuilder = new PredicateBuilder(ConjunctionType.OR);

            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("birthDate", wBirthDate, Operation.LESS_OR_EQUAL));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("hireDate", wHireDate, Operation.LESS_OR_EQUAL));
            predicateBuilder.addBuilder(new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())));

            return this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, HiringInfo.class);
        }
    }

    public int getNoOfLeaveBonusForApproval(BusinessCertificate businessCertificate){

        return this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(CustomPredicate.procurePredicate("approvedInd",0))
                .addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())), LeaveBonusMasterBean.class);

    }

    public int getNoOfPendingNameConflicts(Long pBizId){
        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("rejectedInd", IConstants.OFF, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvedInd", IConstants.OFF, Operation.EQUALS)).addPredicate(CustomPredicate.procurePredicate("businessClientId", pBizId));
        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder, SetupEmployeeMaster.class);
    }

    public List<MPBAMiniBean> getMDAPDetailsByGenderAndCode(String pGenderCode, MdaType pMdaType, BusinessCertificate businessCertificate, MdaInfo mdaInfo)
    {
        List <MPBAMiniBean>wRetList = new ArrayList<MPBAMiniBean>();
        Long wKey = null;
        String hqlQuery = "";

        if(mdaInfo == null) {
            hqlQuery = "select a.id,a.name,a.codeName,count(c.id), mt.name from MdaInfo a ,"
                    + " MdaDeptMap b, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " c,HiringInfo d, MdaType mt where a.id = b.mdaInfo.id and b.id = c.mdaDeptMap.id and c.id = d." + businessCertificate.getEmployeeIdJoinStr()
                    + " and c.statusIndicator = 0 and d.gender = :pVal and mt.id = :pMdaTypeIdVar "
                    + "and a.mdaType.id = mt.id group by a.id, a.name, a.codeName, mt.name";
            wKey = pMdaType.getId();
        } else{

            hqlQuery = "select a.id,a.name,a.codeName,count(c.id), a.name from MdaInfo a ,"
                    + " MdaDeptMap b, " + IppmsUtils.getEmployeeTableName(businessCertificate) + " c,HiringInfo d, " +
                    "MdaType mt where a.id = b.mdaInfo.id and b.id = c.mdaDeptMap.id and c.id = d." + businessCertificate.getEmployeeIdJoinStr()
                    + " and c.statusIndicator = 0 and d.gender = :pVal and a.id = :pMdaTypeIdVar "
                    + "and a.mdaType.id = mt.id group by a.id, a.name, a.codeName, a.name";
            wKey = mdaInfo.getId();
          }

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pVal", pGenderCode);
        query.setParameter("pMdaTypeIdVar", wKey);

        ArrayList<Object[]> wRetVal;

        wRetVal = (ArrayList<Object[]>)query.list();

        for (Object[] o : wRetVal) {
            MPBAMiniBean m = new MPBAMiniBean();
            if(mdaInfo == null)
               m.setObjectId(pMdaType.getMdaTypeCode());
            m.setId((Long)o[0]);
            m.setName((String)o[1]);
            m.setCodeName((String)o[2]);

            if (pGenderCode.equalsIgnoreCase("F"))
                m.setNoOfFemales(((Long)o[3]).intValue());
            else
                m.setNoOfMales(((Long)o[3]).intValue());

            m.setType((String)o[4]);

            wRetList.add(m);
        }

        return wRetList;
    }

    public List<MPBAMiniBean> getMDAPDetailsByGenderAndCode(String pGenderCode, int pOid, BusinessCertificate bc, MdaInfo pMdaInfo)
    {
        List <MPBAMiniBean>wRetList = new ArrayList<MPBAMiniBean>();

        String hqlQuery = "";
        if(pMdaInfo != null){
            hqlQuery = "select a.id,a.name,a.codeName,count(c.id), mt.name from MdaInfo a ,"
                    + " MdaDeptMap b, "+IppmsUtils.getEmployeeTableName(bc)+" c,HiringInfo d, MdaType mt where a.id = b.mdaInfo.id and b.id = c.mdaDeptMap.id and c.id = d.employee.id "
                    + "and c.statusIndicator = 0 and d.gender = :pVal and a.id = :pTypeCode "
                    + "and a.mdaType.id = mt.id group by a.id, a.name, a.codeName, mt.name";
        }else{
            hqlQuery = "select a.id,a.name,a.codeName,count(c.id), mt.name from MdaInfo a ,"
                    + " MdaDeptMap b, "+IppmsUtils.getEmployeeTableName(bc)+" c,HiringInfo d, MdaType mt where a.id = b.mdaInfo.id and b.id = c.mdaDeptMap.id and c.id = d.employee.id "
                    + "and c.statusIndicator = 0 and d.gender = :pVal and mt.mdaTypeCode = :pTypeCode "
                    + "and a.mdaType.id = mt.id group by a.id, a.name, a.codeName, mt.name";
        }



        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pVal", pGenderCode);
        if(pMdaInfo != null)
           query.setParameter("pTypeCode", pMdaInfo.getId());
        else
            query.setParameter("pTypeCode", pOid);

        ArrayList<Object[]> wRetVal;

        wRetVal = (ArrayList<Object[]>)query.list();

        for (Object[] o : wRetVal) {
            MPBAMiniBean m = new MPBAMiniBean();
            m.setObjectId(pOid);
            m.setId((Long)o[0]);
            m.setName((String)o[1]);
            m.setCodeName((String)o[2]);

            if (pGenderCode.equalsIgnoreCase("F"))
                m.setNoOfFemales(((Long)o[3]).intValue());
            else
                m.setNoOfMales(((Long)o[3]).intValue());

            m.setType((String)o[4]);

            wRetList.add(m);
        }

        return wRetList;
    }


    public List<NamedEntityLong> makeYearListByOrmObject(String pOrmObject){
        String wHqlStr = "select runYear from "+pOrmObject
                + " order by runYear desc ";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);

        ArrayList<Integer> wRetVal = (ArrayList<Integer>) wQuery.list();
        List<NamedEntityLong> wRetMap = new ArrayList<NamedEntityLong>();

        if ((wRetVal != null) && (wRetVal.size() > 0)) {
            for (Integer o : wRetVal) {
                NamedEntityLong n = new NamedEntityLong();
                n.setSalaryInfoInstId(new Long(o));
                n.setName(String.valueOf(o));
                wRetMap.add(n);
            }
        }
        NamedEntityLong n = new NamedEntityLong();
        n.setSalaryInfoInstId(0L);
        n.setName("All");
        wRetMap.add(n);
        Collections.sort(wRetMap);
        return wRetMap;
    }


    public List<LeaveBonusMasterBean> loadLeaveBonusMasterBeansForDisplay(
            int pStartRow, int pEndRow, String pSortOrder,
            String pSortCriterion, int pYear)
    {
        String wHqlStr = "";


        if(pYear > 1){
            wHqlStr = "select l.mdaInfo.id,l.runMonth,l.lastModBy,l.createdDate," +
                    "l.totalAmountPaid,l.totalNoOfEmp,l.approvedInd,n.id, n.userName, n.firstName, n.lastName from LeaveBonusMasterBean l, User n  " +
                    "where l.runYear = :pYearValue and l.approvedBy.id = n.id";
        }else{
            wHqlStr = "select l.runYear,sum(l.totalAmountPaid),sum(l.totalNoOfEmp),l.approvedInd " +
                    "from LeaveBonusMasterBean l" +
                    " group by l.runYear,l.approvedInd order by l.runYear desc" ;

        }

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        if(pYear > 1)
            wQuery.setParameter("pYearValue", pYear);

        if(pStartRow > 0)
            wQuery.setFirstResult(pStartRow);
        wQuery.setMaxResults(pEndRow);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<LeaveBonusMasterBean> wRetMap = new ArrayList<LeaveBonusMasterBean>();
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


    public List<LgaMiniBean> getTotalNoOfEmpPerLGA(BusinessCertificate bc)
    {

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("statusIndicator", IConstants.OFF));

        int wTotalEmp = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getEmployeeClass(bc));

        String sql = "select count(e.id),l.name,l.id from "+IppmsUtils.getEmployeeTableName(bc)+" e, "
                + "LGAInfo l where e.lgaInfo.id = l.id and e.statusIndicator = 0 "
                + "and e.businessClientId = :pBizIdVar group by l.name,l.id order by 1 desc";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);
        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        wQuery.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();
        List<LgaMiniBean> wRetList = new ArrayList<LgaMiniBean>();

        wRetVal = (ArrayList)wQuery.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                LgaMiniBean d = new LgaMiniBean();
                d.setTotalElements(((Long)o[0]).intValue());
                d.setName((String)o[1]);
                d.setId((Long)o[2]);
                d.setPercentage(wTotalEmp);
                wRetList.add(d);
            }

        }

        return wRetList;
    }

    public List<LgaMiniBean> getTotalNoOfEmpByReligion(BusinessCertificate bc)
    {
        String sql = "select count(e.id),r.name,r.id from "+IppmsUtils.getEmployeeTableName(bc)+" e, Religion r where e.religion.id = r.id "
                + "and e.statusIndicator = 0 and e.businessClientId = :pBizIdVar group by r.name,r.id order by 1 desc";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);
        wQuery.setParameter("pBizIdVar",bc.getBusinessClientInstId());

        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();
        List<LgaMiniBean> wRetList = new ArrayList<LgaMiniBean>();

        wRetVal = (ArrayList<Object[]>)wQuery.list();
        int wTotalEmp;
        if (wRetVal.size() > 0) {
            PredicateBuilder predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("statusIndicator", IConstants.OFF));

            wTotalEmp = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getEmployeeClass(bc));
            for (Object[] o : wRetVal) {
                LgaMiniBean d = new LgaMiniBean();
                d.setTotalElements(((Long)o[0]).intValue());
                d.setName((String)o[1]);
                d.setId((Long)o[2]);
                d.setPercentage(wTotalEmp);
                wRetList.add(d);
            }

        }

        return wRetList;
    }

    public List<Employee> loadEmpBVNInformation(int pStartRow, int pEndRow,
                                                String pSortOrder, String pSortCriterion, int pApprovedMonthInd,
                                                int pApprovedYearInd, BusinessEmpOVBean pBEBean, boolean pForExport, BusinessCertificate bc)
    {


        LocalDate wFrom = null;
        LocalDate wTo = null;

        Long wMdaInd = 0L;
        boolean wFilterByBank = pBEBean.getBankTypeInd() > 0;
        boolean wFilterByHireDate = pBEBean.getAllowanceStartDate() != null;
        boolean wFilterByMda = IppmsUtils.isNotNullAndGreaterThanZero(pBEBean.getMdaInstId());
        boolean wFilterByBvnStatus = pBEBean.getBvnStatusInd() > 0;

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth",pApprovedMonthInd));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear",pApprovedYearInd));

        boolean wUsePaycheckInfo = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,
                IppmsUtils.getPaycheckClass(bc)) > 0;
        LocalDate wLastApproved = LocalDate.now();
        boolean wDmb = pBEBean.getBankTypeInd() == 1;
        boolean wMicrofinance = pBEBean.getBankTypeInd() == 2;
        LocalDate.of(pApprovedYearInd, pApprovedMonthInd, 1);
        boolean wUseEmploymentStatus = pBEBean.getStatusInd() != 2 ;

        String hqlQuery ="";
        if(wUsePaycheckInfo){
            hqlQuery = "select e.id,e.employeeId, e.firstName, e.lastName, e.initials,e.gsmNumber,m.id,m.name,m.codeName " +
                    ",bi.name,bb.name, coalesce(p.bvnNo,'No BVN'),p.accountNumber,s.level,s.step,bi.mfbInd "+
                    "from "+IppmsUtils.getEmployeeTableName(bc)+" e, EmployeeType et,HiringInfo h,"+IppmsUtils.getPaycheckTableName(bc)+" p,SalaryInfo s,MdaInfo m," +
                    " BankInfo bi, BankBranch bb" +
                    " where e.id = p.employee.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and p.mdaDeptMap.mdaInfo.id = m.id" +
                    " and p.employeeType.id = et.id" +
                    " and p.bankBranch.id = bb.id and bb.bankInfo.id = bi.id" +
                    " and p.runMonth = :pRunMonthVal and p.runYear = :pRunYearVal and p.netPay > 0 and s.id = p.salaryInfo.id and e.businessClientId = :pBizIdVar ";
        }else{
            hqlQuery = "select e.id,e.employeeId, e.firstName, e.lastName, e.initials,e.gsmNumber, m.id, m.name,m.codeName " +
                    ",bi.name,bb.name, coalesce(p.bvnNo,'No BVN'),p.accountNumber,s.level,s.step,bi.mfbInd "+
                    "from "+IppmsUtils.getEmployeeTableName(bc)+" e, EmployeeType et,HiringInfo h,SalaryInfo s,PaymentMethodInfo p,MdaInfo m," +
                    " BankInfo bi, BankBranch bb" +
                    " where e.id = p.employee.id and e.id = h."+bc.getEmployeeIdJoinStr()+" " +
                    " and e.employeeType.id = et.id and e.mdaDeptMap.mdaInfo.id = m.id " +
                    " and p.bankBranches.id = bb.id and bb.bankInfo.id = bi.id and s.id = e.salaryInfo.id and e.businessClientId = :pBizIdVar " +
                    "  ";

        }

        if(wFilterByBank){
            if(wDmb){
                hqlQuery += "and bi.mfbInd = 0 ";
            }else if(wMicrofinance){
                hqlQuery += "and bi.mfbInd = 1 ";
            }
        }
        if(wFilterByBvnStatus){
            if(pBEBean.getBvnStatusInd() == 2){

                hqlQuery += "and (p.bvnNo is null or trim(p.bvnNo) = '') ";
            }else if(pBEBean.getBvnStatusInd() == 1){
                hqlQuery += "and (p.bvnNo is not null and trim(p.bvnNo) <> '') ";
            }
        }

        if(wFilterByHireDate){
            wFrom = PayrollBeanUtils.getNextORPreviousDay(pBEBean.getAllowanceStartDate(), false);
            wTo = LocalDate.now();
            if(pBEBean.getAllowanceEndDate() != null){
                wTo =pBEBean.getAllowanceEndDate();
            }
            if(bc.isPensioner())
                hqlQuery += "and h.pensionStartDate > :pHireDateVar and h.pensionStartDate < :pHireDateVar2 ";
            else
                hqlQuery += "and h.hireDate > :pHireDateVar and h.hireDate < :pHireDateVar2 ";
        }
        if(wUseEmploymentStatus){

            hqlQuery += "and e.statusIndicator = "+pBEBean.getStatusInd()+" ";

        }
        if(wFilterByMda){

            wMdaInd = pBEBean.getMdaInstId();

            if(wUsePaycheckInfo){
                //Get all Map ID's for this 'MDA'
                //Hopefully they will never have 1000+ departments mapped...

                hqlQuery += " and p.mdaDeptMap.mdaInfo.id = "+wMdaInd;

            }else{

                hqlQuery += " and e.mdaDeptMap.mdaInfo.id = "+wMdaInd;

            }
        }
        hqlQuery += " order by e.lastName,e.firstName,e.initials asc ";


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        if(wUsePaycheckInfo){
            wQuery.setParameter("pRunMonthVal", pApprovedMonthInd);
            wQuery.setParameter("pRunYearVal", pApprovedYearInd);
        }
        if(wFilterByHireDate){
            wQuery.setParameter("pHireDateVar", wFrom);
            wQuery.setParameter("pHireDateVar2", wTo);
        }
        wQuery.setParameter("pBizIdVar",bc.getBusinessClientInstId());
        if(!pForExport){
            if (pStartRow > 0)
                wQuery.setFirstResult(pStartRow);
            wQuery.setMaxResults(pEndRow);
        }


        List<Object[]> results = wQuery.list();
        List<Employee> wRetList = new ArrayList<Employee>();

        if ((results != null) && (results.size() > 0)) {
            int i = 0;
            for (Object[] o : results) {

                Employee e = new Employee();

                e.setId((Long)o[i++]);
                e.setEmployeeId((String)o[i++]);
                e.setFirstName((String)o[i++]);
                e.setLastName((String)o[i++]);
                e.setInitials(StringUtils.trimToNull((String) o[i++]));
                e.setGsmNumber((String)o[i++]);
                e.setMapInstId((Long)o[i++]);
                e.setMdaName((String)o[i++]);
                e.setMdaDeptMap(new MdaDeptMap());
                e.getMdaDeptMap().setMdaInfo(new MdaInfo(e.getMapInstId(), e.getMdaName(), (String)o[i++]));
                String bankName = (String)o[i++];
                String branchName = (String)o[i++];
                e.setBvnNumber((String)o[i++]);
                e.setAccountNumber((String)o[i++]);
                SalaryInfo s = new SalaryInfo();
                s.setLevel((Integer)o[i++]);
                s.setStep((Integer)o[i++]);
                if(wMicrofinance)
                    e.setBusinessName(branchName);
                else if(wDmb)
                    e.setBusinessName(bankName);
                else{
                    if((Integer)o[i++] == 0){
                        e.setBusinessName(bankName);
                    }else{
                        e.setBusinessName(branchName);
                    }
                }


                e.setSalaryInfo(s);

                wRetList.add(e);
                i = 0;
            }

        }
        return wRetList;
    }


    public EmpContMiniBean getTotalNoOfEmpBVNInformation(int pApprovedMonthInd,
                                                         int pApprovedYearInd, BusinessEmpOVBean pBEBean,
                                                         BusinessCertificate bc)
    {


        LocalDate wFrom = null;
        LocalDate wTo = null;


        boolean wFilterByBank = pBEBean.getBankTypeInd() > 0;
        boolean wFilterByHireDate = pBEBean.getAllowanceStartDate() != null;
        boolean wFilterByMda = IppmsUtils.isNotNullAndGreaterThanZero(pBEBean.getMdaInstId());
        boolean wFilterByBvnStatus = pBEBean.getBvnStatusInd() > 0;
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth",pApprovedMonthInd));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear",pApprovedYearInd));

        boolean wUsePaycheckInfo = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,
                IppmsUtils.getPaycheckClass(bc)) > 0;
        GregorianCalendar wLastApproved = new GregorianCalendar();
        boolean wDmb = pBEBean.getBankTypeInd() == 1;
        boolean wMicrofinance = pBEBean.getBankTypeInd() == 2;
        boolean wUseEmploymentStatus = pBEBean.getStatusInd() !=2;
        wLastApproved.set(pApprovedYearInd, pApprovedMonthInd, 1);


        String hqlQuery ="";
        if(wUsePaycheckInfo){
            hqlQuery = "select e.id,e.employeeId, e.firstName, e.lastName, e.initials,e.gsmNumber,m.id,m.name " +
                    ",bi.name,bb.name, coalesce(p.bvnNo,'No BVN'),p.accountNumber,s.level,s.step,bi.mfbInd "+
                    "from "+IppmsUtils.getEmployeeTableName(bc)+" e, EmployeeType et,HiringInfo h,"+IppmsUtils.getPaycheckTableName(bc)+" p,SalaryInfo s,MdaInfo m, MdaDeptMap mdm," +
                    " BankInfo bi, BankBranch bb" +
                    " where e.id = p.employee.id and e.id = h."+bc.getEmployeeIdJoinStr()+"" +
                    " and p.employeeType.id = et.id and mdm.id = p.mdaDeptMap.id and mdm.mdaInfo.id = m.id " +
                    " and p.bankBranch.id = bb.id and bb.bankInfo.id = bi.id" +
                    " and p.runMonth = :pRunMonthVal and p.runYear = :pRunYearVal and p.netPay > 0 and s.id = p.salaryInfo.id and e.businessClientId = :pBizIdVar ";
        }else{
            hqlQuery = "select e.id,e.employeeId, e.firstName, e.lastName, e.initials,e.gsmNumber,m.id,m.name" +
                    ",bi.name,bb.name, coalesce(p.bvnNo,'No BVN'),p.accountNumber,s.level,s.step,bi.mfbInd "+
                    "from "+IppmsUtils.getEmployeeTableName(bc)+" e, EmployeeType et,HiringInfo h,SalaryInfo s,PaymentMethodInfo p,MdaInfo m, MdaDeptMap mdm," +
                    " BankInfo bi, BankBranch bb" +
                    " where e.id = p.employee.id and e.id = h."+bc.getEmployeeIdJoinStr()+" and m.id = mdm.mdaInfo.id and mdm.id = e.mdaDeptMap.id" +
                    " and e.employeeType.id = et.id" +
                    " and p.bankBranches.id = bb.id and bb.bankInfo.id = bi.id and s.id = e.salaryInfo.id and e.businessClientId = :pBizIdVar " +
                    "  ";

        }

        if(wFilterByBank){
            if(wDmb){
                hqlQuery += "and bi.mfbInd = 0 ";
            }else if(wMicrofinance){
                hqlQuery += "and bi.mfbInd = 1 ";
            }
        }
        if(wFilterByBvnStatus){
            if(pBEBean.getBvnStatusInd() == 2){

                hqlQuery += "and (p.bvnNo is null or trim(p.bvnNo) = '') ";
            }else if(pBEBean.getBvnStatusInd() == 1){
                hqlQuery += "and (p.bvnNo is not null and trim(p.bvnNo) <> '') ";
            }
        }
        if(wUseEmploymentStatus){

            hqlQuery += "and e.statusIndicator = "+ pBEBean.getStatusInd()+" ";

        }
        if(wFilterByHireDate){
            wFrom = PayrollBeanUtils.getNextORPreviousDay(pBEBean.getAllowanceStartDate(), false);
            wTo = LocalDate.now();
            if(pBEBean.getAllowanceEndDate() != null){
                wTo = pBEBean.getAllowanceEndDate();
            }
            if(bc.isPensioner())
                hqlQuery += "and h.pensionStartDate > :pHireDateVar and h.pensionStartDate < :pHireDateVar2 ";
            else
                hqlQuery += "and h.hireDate > :pHireDateVar and h.hireDate < :pHireDateVar2 ";
        }

        if(wFilterByMda){


                hqlQuery += " and m.id = "+pBEBean.getMdaInstId();


        }

        hqlQuery += " order by e.lastName,e.firstName,e.initials asc ";


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        if(wUsePaycheckInfo){
            wQuery.setParameter("pRunMonthVal", pApprovedMonthInd);
            wQuery.setParameter("pRunYearVal", pApprovedYearInd);
        }
        if(wFilterByHireDate){
            wQuery.setParameter("pHireDateVar", wFrom);
            wQuery.setParameter("pHireDateVar2", wTo);
        }
        wQuery.setParameter("pBizIdVar",bc.getBusinessClientInstId());
        /*
         * if(wFilterByMda && wUsePaycheckInfo){
         *
         * wQuery.setParameterList("pObjArray",this.getMapIdsForMdaId(wObjectInd,wMdaInd
         * ).toArray()); }
         */

        List<Object[]> results = wQuery.list();
        EmpContMiniBean wRetList = new EmpContMiniBean();

        if ((results != null) && (results.size() > 0)) {
            wRetList.setObjectInd(results.size());
        }else{
            wRetList.setObjectInd(0);
        }
        return wRetList;
    }

     public List<AbmpBean> getMdapListForLtgByCodeAndFilter(BusinessCertificate bc,HashMap<Long, Long> pExemptLtgMap)
    {
        String hqlQuery  = "select distinct a.id,a.name from MdaInfo a, MdaDeptMap d, "+IppmsUtils.getEmployeeTableName(bc)+" e  where a.id = d.mdaInfo.id and d.id = e.mdaDeptMap.id "
                + "and a.businessClientId = "+bc.getBusinessClientInstId();

        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        ArrayList <Object[]>wRetVal = new ArrayList<Object[]>();
        List <AbmpBean>wRetList = new ArrayList<AbmpBean>();

        wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                Long wKey = (Long)o[0];
                if (pExemptLtgMap.containsKey(wKey)) {
                    continue;
                }
                AbmpBean d = new AbmpBean();
                d.setId((Long)o[0]);
                d.setName((String)o[1]);
                d.setMdaInfo(new MdaInfo(d.getId(), d.getName()));
                wRetList.add(d);
            }

        }

        return wRetList;
    }
}
