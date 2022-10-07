/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.dao.IPromotionDao;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncreaseBean;
import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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

@Service("promotionService")
@Repository
@Transactional(readOnly = true)
public class PromotionService {

    private final IPromotionDao promotionDao;

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Autowired
    public PromotionService(IPromotionDao promotionDao) {
        this.promotionDao = promotionDao;
    }

    public boolean employeeHasPromotionHistory(BusinessCertificate bc,Long pEmpId) {
        return this.promotionDao.employeeHasPromotionHistory(bc,pEmpId);
    }

    public int getToBePromotedEmpCount(Long pBizId) {
        return this.promotionDao.getToBePromotedEmpCount(pBizId);
    }

    public List<PromotionTracker> getEmpToBePromotedList(BusinessCertificate businessCertificate) {
        return this.promotionDao.getToBePromotedEmployeeList(businessCertificate);
    }

    public HashMap makeSalaryTypeToLevelAndStepMap(Long bizClientId) {
        return promotionDao.makeSalaryTypeToLevelAndStepMap(bizClientId);
    }

    public List getStepIncreasableEmployees(BusinessCertificate businessCertificate, Long MdaId) {
        return this.promotionDao.getStepIncreasableEmployees(businessCertificate, MdaId);
    }

    public HashMap makeSalaryTypeLevelStepToSalaryInfoMap(Long businessClientInstId) {
        return this.promotionDao.makeSalaryTypeLevelStepToSalaryInfoMap(businessClientInstId);
    }

    public List<SalaryInfo> loadSalaryInfoBySalaryScaleAndFilterLGA(Long pSalaryTypeId, int pStep, Rank pRankInfo, Long businessClientInstId) {
        return this.promotionDao.loadSalaryInfoBySalaryTypeAndRank(pSalaryTypeId, pStep, pRankInfo, businessClientInstId);
    }

    public List<SalaryInfo> loadSalaryInfoForPromotions(Long pSalaryTypeId, int pLevel, int pStep, Long businessClientInstId) {
        return this.promotionDao.loadSalaryInfoByForPromotion(pSalaryTypeId, pLevel, pStep, businessClientInstId);
    }

    @Transactional
    public void promoteSingleEmployee(Long pEmpId, Long pSalaryInfoId, BusinessCertificate bc, Rank rank) {
        this.promotionDao.promoteSingleEmployee(pEmpId, pSalaryInfoId, bc, rank);
    }

    @Transactional
    public void updateHiringInfoPromotionDates(Long hiringInfoId, LocalDate wNextPromotionDate, BusinessCertificate bc) {
        this.promotionDao.updateHiringInfoForPromotion(hiringInfoId, wNextPromotionDate, bc);
    }

    @Transactional
    public void updateStaffSalarySteps(List<StepIncreaseBean> wSIBList, BusinessCertificate bc) {
        this.promotionDao.updateEmployeesSalarySteps(wSIBList, bc);
    }



    public List<VariationReportBean> loadFlaggedPromotionsForAllOrganization(int pRunMonth, int pRunYear) throws InstantiationException, IllegalAccessException {
        List wRetList = new ArrayList();

        String wHql = "SELECT e.firstName, e.lastName, e.initials, e.employeeId, bc.name, bc.id, f.promotionDate, u.firstName, u.lastName, m.name, "
                + " fs.id, ts.id "
                + " FROM FlaggedPromotions f, Employee e, SalaryInfo fs, SalaryInfo ts, User u, BusinessClient bc, MdaInfo m WHERE "
                + " f.employee.id = e.id and f.mdaInfo.id = m.id and f.fromSalaryInfo.id = fs.id and f.toSalaryInfo.id = ts.id "
                + " and f.initiator.id = u.id and f.businessClientId = bc.id and f.statusInd = 1 and f.runMonth = :pRunMonth and f.runYear = :pRunYear";


        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeId((String) o[3]);
                String pFirstName = ((String) o[0]);
                String pLastName = ((String) o[1]);
                String pInitials = null;
                if (o[2] != null) {
                    pInitials = ((String) o[2]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setBusinessName((String) o[4]);
                p.setBusinessClientInstId((Long) o[5]);
                p.setPromotionDate((LocalDate) o[6]);
                p.setUserFirstName((String) o[7]);
                p.setUserLastName((String) o[8]);
                p.setMda((String) o[9]);
                p.setOldSalaryInfoInstId((Long) o[10]);
                p.setSalaryInfoInstId((Long) o[11]);

                SalaryInfo salaryInfo = this.genericService.loadObjectWithSingleCondition(SalaryInfo.class,
                        CustomPredicate.procurePredicate("id", p.getSalaryInfoInstId()));

                p.setCurrentAllowance(salaryInfo.getConsolidatedAllowance());
                p.setThisMonthGross(salaryInfo.getMonthlyGrossPay());
                p.setBasicSalary(salaryInfo.getAnnualSalary());

                SalaryInfo OldSalaryInfo = this.genericService.loadObjectWithSingleCondition(SalaryInfo.class,
                        CustomPredicate.procurePredicate("id", p.getOldSalaryInfoInstId()));

                p.setPrevMonthGross(OldSalaryInfo.getMonthlyGrossPay());
                p.setOldAllowance(OldSalaryInfo.getConsolidatedAllowance());
                p.setOldSalary(OldSalaryInfo.getAnnualSalary());
                wRetList.add(p);
            }
        }

        return wRetList;
    }

    public List<VariationReportBean> loadFlaggedPromotionsByOrganization(int pRunMonth, int pRunYear, BusinessCertificate bc, Long bId, Long mId) throws InstantiationException, IllegalAccessException {
        List wRetList = new ArrayList();

        String wHql = "SELECT e.firstName, e.lastName, e.initials, e.employeeId, bc.name, bc.id, f.promotionDate, u.firstName, u.lastName, m.name, m.id, "
                + " fs.id, ts.id "
                + " FROM FlaggedPromotions f, " + IppmsUtils.getEmployeeTableName(bc) + " e, SalaryInfo fs, SalaryInfo ts, User u, BusinessClient bc, MdaInfo m WHERE "
                + " f.employee.id = e.id and f.mdaInfo.id = m.id and f.fromSalaryInfo.id = fs.id and f.toSalaryInfo.id = ts.id "
                + " and f.initiator.id = u.id and f.businessClientId = bc.id and f.statusInd = 1 and f.runMonth = :pRunMonth and f.runYear = :pRunYear ";


        if (IppmsUtils.isNotNullAndGreaterThanZero(bId)) {
            wHql += " and bc.id = :clientId";
        } else if (IppmsUtils.isNotNullAndGreaterThanZero(mId)) {
            wHql += " and m.id = :mdaId";
        }

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);

        if (IppmsUtils.isNotNullAndGreaterThanZero(bId)) {
            query.setParameter("clientId", bId);
        } else if (IppmsUtils.isNotNullAndGreaterThanZero(mId)) {
            query.setParameter("mdaId", mId);
        }


        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeId((String) o[3]);
                String pFirstName = ((String) o[0]);
                String pLastName = ((String) o[1]);
                String pInitials = null;
                if (o[2] != null) {
                    pInitials = ((String) o[2]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setBusinessName((String) o[4]);
                p.setBusinessClientInstId((Long) o[5]);
                p.setPromotionDate((LocalDate) o[6]);
                p.setUserFirstName((String) o[7]);
                p.setUserLastName((String) o[8]);
                p.setPromotedBy(p.getUserFirstName() + " " + p.getUserLastName());
                p.setMda((String) o[9]);
                p.setMdaInstId((Long) o[10]);
                p.setOldSalaryInfoInstId((Long) o[11]);
                p.setSalaryInfoInstId((Long) o[12]);

                SalaryInfo salaryInfo = this.genericService.loadObjectWithSingleCondition(SalaryInfo.class,
                        CustomPredicate.procurePredicate("id", p.getSalaryInfoInstId()));

                p.setCurrentAllowance(salaryInfo.getConsolidatedAllowance());
                p.setThisMonthGross(salaryInfo.getMonthlyGrossPay());
                p.setBasicSalary(salaryInfo.getAnnualSalary());

                SalaryInfo OldSalaryInfo = this.genericService.loadObjectWithSingleCondition(SalaryInfo.class,
                        CustomPredicate.procurePredicate("id", p.getOldSalaryInfoInstId()));

                p.setPrevMonthGross(OldSalaryInfo.getMonthlyGrossPay());
                p.setOldAllowance(OldSalaryInfo.getConsolidatedAllowance());
                p.setOldSalary(salaryInfo.getAnnualSalary());
                wRetList.add(p);
            }
        }

        return wRetList;
    }


    public int getTotalNoOflaggedPromotionsForApprovals(Long businessClientInstId) {
        return this.promotionDao.countNoOfActiveFlaggedPromotions(businessClientInstId);
    }
}
