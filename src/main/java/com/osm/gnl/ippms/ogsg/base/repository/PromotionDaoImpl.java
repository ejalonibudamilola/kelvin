/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.repository;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.dao.IPromotionDao;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.domain.promotion.FlaggedPromotions;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncreaseBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class PromotionDaoImpl implements IPromotionDao {

    @Autowired
    private GenericService genericService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private IppmsUtils ippmsUtils;


    public PromotionDaoImpl() {
    }

    @Override
    public boolean employeeHasPromotionHistory(BusinessCertificate bc,Long pEmpId) {
        int wRetVal = 0;
        String wSql = "select count(distinct p.employee.id)" +
                " from "+IppmsUtils.getPromotionAuditTable(bc)+" p " +
                " where p.employee.id = :pEmpIdVar";

        Query<Long> query = this.sessionFactory.getCurrentSession().createQuery(wSql);
        query.setParameter("pEmpIdVar", pEmpId);

        List results = query.list();
        wRetVal = ((Long) results.get(0)).intValue();

        return wRetVal > 0;
    }

    @Override
    public int getToBePromotedEmpCount(Long pBizId) {
        int retVal = 0;

        LocalDate wToday = LocalDate.now();
        LocalDate wMonthStart = LocalDate.of(wToday.getYear(), wToday.getMonthValue(), 1);
        LocalDate wMonthEnd = LocalDate.of(wToday.getYear(), wToday.getMonthValue(), wMonthStart.lengthOfMonth());

        Query query = this.sessionFactory.getCurrentSession().createQuery("select count(p.id) from PromotionTracker p, Employee e, BusinessClient b " +
                " where p.nextPromotionDate >= :pStartDate and p.nextPromotionDate <= :pEndDate" +
                " and p.employee.id = e.id and e.businessClientId = b.id and b.id = :pBizIdVar");

        query.setParameter("pStartDate", wMonthStart);
        query.setParameter("pEndDate", wMonthEnd);
        query.setParameter("pBizIdVar", pBizId);

        List results = query.list();

        retVal = ((Long) results.get(0)).intValue();
        return retVal;
    }

    @Override
    public List<PromotionTracker> getToBePromotedEmployeeList(BusinessCertificate businessCertificate) {
        LocalDate wToday = LocalDate.now();
        LocalDate wMonthStart = LocalDate.of(wToday.getYear(), wToday.getMonthValue(), 1);
        LocalDate wMonthEnd = LocalDate.of(wToday.getYear(), wToday.getMonthValue(), wMonthStart.lengthOfMonth());

        return this.genericService.loadAllObjectsUsingRestrictions(PromotionTracker.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("nextPromotionDate", wMonthStart, Operation.GREATER_OR_EQUAL),
                CustomPredicate.procurePredicate("nextPromotionDate", wMonthEnd, Operation.LESS_OR_EQUAL)), null);


    }

    @Override
    public HashMap<Long, HashMap<Integer, Integer>> makeSalaryTypeToLevelAndStepMap(Long pBizId) {

        String sql = "select st.id, s.level, count(s.step) from SalaryInfo s, SalaryType st "
                + "where st.id = s.salaryType.id and st.businessClientId = s.businessClientId " +
                "and st.businessClientId = :pBizIdVar and st.deactivatedInd = 0 group by st.id,s.level order by st.id, s.level";

        Query query = sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pBizIdVar", pBizId);

        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();
        HashMap<Long, HashMap<Integer, Integer>> wRetMap = new HashMap<Long, HashMap<Integer, Integer>>();

        wRetVal = (ArrayList<Object[]>) query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                Long key = (Long) o[0];
                Integer level = (Integer) o[1];
                Integer step = new Integer(((Long) o[2]).intValue());

                if (wRetMap.containsKey(key)) {
                    if (!wRetMap.get(key).containsKey(level)) {
                        wRetMap.get(key).put(level, step);
                    }
                } else {
                    HashMap<Integer, Integer> wInnerMap = new HashMap<Integer, Integer>();

                    wInnerMap.put(level, step);
                    wRetMap.put(key, wInnerMap);
                }

            }

        }

        return wRetMap;

    }

    @Override
    public List<StepIncreaseBean> getStepIncreasableEmployees(BusinessCertificate bc, Long pMdaId) {

        String hql = "select e.id,e.firstName,e.lastName,coalesce(e.initials,''),m.id, m.name,e.salaryInfo.id, s.salaryType.id, s.level,s.step,e.employeeId,sc.name from "
                + IppmsUtils.getEmployeeTableName(bc)+" e ,SalaryInfo s, SalaryType sc, HiringInfo h, MdaInfo m, MdaDeptMap mdm " +
                "where e.salaryInfo.id = s.id and s.salaryType.id = sc.id and e.statusIndicator = 0 and e.mdaDeptMap.id = mdm.id and mdm.mdaInfo.id = m.id " +
                "and e.businessClientId = :pBizIdVar and e.id = h."+bc.getEmployeeIdJoinStr()+" and h.suspended = 0 and h.suspensionDate is null";

        if(IppmsUtils.isNotNullAndGreaterThanZero(pMdaId))
            hql += " and m.id = "+pMdaId;

       // if(!bc.isCivilService())
            hql += " and e.id not in (select a.employee.id from StepIncrementTracker a where a.year = "+LocalDate.now().getYear()+" and a.businessClientId = "+bc.getBusinessClientInstId()+" ) ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        ArrayList<Object[]> wRetVal;
        ArrayList<StepIncreaseBean> wRetList = new ArrayList<>();

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            int i = 0;
            for (Object[] o : wRetVal) {

                StepIncreaseBean s = new StepIncreaseBean();
                s.setId((Long) o[i++]);
                s.setName(o[i++] + " " + o[i++] + " "+o[i++]);
                s.setMdaInfo(new MdaInfo((Long)o[i++], (String)o[i++]));
                s.setSalaryInfoInstId((Long) o[i++]);
                s.setSalaryTypeInstId((Long) o[i++]);
                s.setLevel((Integer) o[i++]);
                s.setStep((Integer) o[i++]);
                s.setEmployeeId((String)o[i++]);
                s.setSalaryScaleName((String)o[i++]);
                s.setBusinessClientId(bc.getBusinessClientInstId());

                wRetList.add(s);
                i = 0;
            }

        }

        return wRetList;

    }


    @Override
    public HashMap<String, Long> makeSalaryTypeLevelStepToSalaryInfoMap(Long pBizId) {
        String sql = "select s.id, st.id, s.level, s.step from SalaryInfo s, SalaryType st where s.salaryType.id = st.id" +
                " and st.businessClientId = :pBizIdVar";

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);

        query.setParameter("pBizIdVar", pBizId);

        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();
        HashMap<String, Long> wRetList = new HashMap<String, Long>();

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                Long value = (Long) o[0];
                Long part1 = (Long) o[1];
                Integer part2 = (Integer) o[2];
                Integer part3 = (Integer) o[3];
                String key = part1 + ":" + part2 + ":" + part3;
                wRetList.put(key, value);
            }
        }

        return wRetList;
    }

    @Override
    public List<SalaryInfo> loadSalaryInfoBySalaryTypeAndRank(Long pSalaryTypeId, int pStep, Rank pRankInfo, Long businessClientInstId) {
        List<SalaryInfo> wRetList = new ArrayList<SalaryInfo>();
        String hqlQuery = "";
        hqlQuery = "select s.id,s.level,s.step  from SalaryInfo s, Rank r, Cadre c, SalaryType st where s.salaryType.id = c.salaryType.id "
                + "and r.cadre.id = c.id and c.salaryType.id = st.id and r.businessClientId = :pBizClientIdVar "
                + "and s.level >= r.fromLevel and s.level <= r.toLevel and s.step >= r.fromStep and s.step <= r.toStep "
                + "and s.salaryType.id = :pSalTypeVar and r.id = " + pRankInfo.getId() + " and s.step > " + pStep;
        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        ArrayList<Object[]> wRetVal = new ArrayList();
        query.setParameter("pSalTypeVar", pSalaryTypeId);
        query.setParameter("pBizClientIdVar", businessClientInstId);
        wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0)
            for (Object[] o : wRetVal) {
                SalaryInfo s = new SalaryInfo();
                s.setId((Long) o[0]);
                s.setLevel(((Integer) o[1]));
                s.setStep(((Integer) o[2]));
                wRetList.add(s);
            }
        return wRetList;
    }
    @Override
    public List<SalaryInfo> loadSalaryInfoByForPromotion(Long pSalaryTypeId, int pLevel, int pStep, Long businessClientInstId) {
        List<SalaryInfo> wRetList = new ArrayList<SalaryInfo>();
        String hqlQuery = "";
        hqlQuery = "select s.id,s.level,s.step  from SalaryInfo s, SalaryType st where s.salaryType.id = st.id "
                + "and s.businessClientId = :pBizClientIdVar and st.businessClientId = :pBizClientIdVar "
                + "and (s.level > :pLevelVar or (s.level = :pLevelVar and s.step > :pStepVar)) and s.salaryType.id = :pSalTypeVar " +
                " order by s.id asc";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        ArrayList<Object[]> wRetVal;
        query.setParameter("pSalTypeVar", pSalaryTypeId);
        query.setParameter("pBizClientIdVar", businessClientInstId);
        query.setParameter("pLevelVar",pLevel);
        query.setParameter("pStepVar",pStep);

        wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0)
            for (Object[] o : wRetVal) {
                SalaryInfo s = new SalaryInfo();
                s.setId((Long) o[0]);
                s.setLevel(((Integer) o[1]));
                s.setStep(((Integer) o[2]));
                wRetList.add(s);
            }
        return wRetList;
    }

    @Override
    @Transactional
    public void promoteSingleEmployee(Long pEmpId, Long pSalaryInfoId, BusinessCertificate bc, Rank rank) {
        String hqlQuery;
        if(rank.isNewEntity())
            hqlQuery = "update "+IppmsUtils.getEmployeeTableName(bc)+" set salaryInfo.id = :pNewSalary, lastModBy.id = :pLMB, lastModTs = :pLMTS where id = :pId";
        else
            hqlQuery = "update "+IppmsUtils.getEmployeeTableName(bc)+" set salaryInfo.id = :pNewSalary, lastModBy.id = :pLMB, rank.id = :pRankIdVar, lastModTs = :pLMTS where id = :pId";
        Query query =sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pNewSalary", pSalaryInfoId);
        query.setParameter("pId", pEmpId);
        query.setParameter("pLMB", bc.getLoginId());
        if(!rank.isNewEntity())
            query.setParameter("pRankIdVar", rank.getId());
        query.setParameter("pLMTS", Timestamp.from(Instant.now()));

        query.executeUpdate();
    }

    @Override
    @Transactional()
    public void updateHiringInfoForPromotion(Long hiringInfoId, LocalDate wNextPromotionDate, BusinessCertificate bc) {
        String hqlQuery = "update HiringInfo set lastModBy.id = :pLMB, lastModTs = :pLMTS, lastPromotionDate = :pLPD, nextPromotionDate = :pNPD where id = :pId";
        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pId", hiringInfoId);
        query.setParameter("pLPD", LocalDate.now());
        query.setParameter("pNPD", wNextPromotionDate);
        query.setParameter("pLMB", bc.getLoginId());
        query.setParameter("pLMTS", Timestamp.from(Instant.now()));

        query.executeUpdate();
    }
    @Override
    @Transactional()
    public void updateEmployeesSalarySteps(List<StepIncreaseBean> pSIBList, BusinessCertificate bc) {
        try {


            for (int i = 0; i < pSIBList.size(); i++)
            {
                StepIncreaseBean s = pSIBList.get(i);

                String sql = "update Employee e set e.salaryInfo.id = :pSalId, e.lastModBy.id = :pLastModBy, e.lastModTs = :pLastModTs where id = :pEmpId";

                Query query = sessionFactory.getCurrentSession().createQuery(sql);
                query.setParameter("pSalId", s.getNewSalaryInfoInstId());
                query.setParameter("pLastModBy", bc.getLoginId());
                query.setParameter("pEmpId", s.getId());
                query.setParameter("pLastModTs", Timestamp.from(Instant.now()));

                query.executeUpdate();
                if ((i % 50 != 0) && (i != pSIBList.size() - 1)) {
                    continue;
                }
                sessionFactory.getCurrentSession().flush();
                sessionFactory.getCurrentSession().clear();
            }

        } catch (Exception wEx) {
            wEx.printStackTrace();
        }
    }

    @Override
    public int countNoOfActiveFlaggedPromotions(Long businessClientInstId) {
        return this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(CustomPredicate.procurePredicate("statusInd", IConstants.OFF))
                .addPredicate(CustomPredicate.procurePredicate("businessClientId", businessClientInstId)), FlaggedPromotions.class);
    }
}
