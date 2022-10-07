/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.repository;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IApprovePayrollDao;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.payroll.DevelopmentLevy;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.MdapLtgAppIndBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

@Service("approvePayrollService")
@Repository
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
@Slf4j
public class ApprovePayrollDaoImpl implements IApprovePayrollDao {



    /**
     *
     */
    private static final long serialVersionUID = -1357025468478202404L;

    private final SessionFactory sessionFactory;

    private final GenericService genericService;

    @Autowired
    public ApprovePayrollDaoImpl(SessionFactory sessionFactory, GenericService genericService) {
        this.sessionFactory = sessionFactory;
        this.genericService = genericService;
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void savePendingPayroll(List<AbstractGarnishmentEntity> pWListToSave)
            throws Exception
    {
        try{
            Vector<AbstractGarnishmentEntity> wActualSaveList = new Vector<>();

            for(AbstractGarnishmentEntity g : pWListToSave){
                g.setOwedAmount(g.getOwedAmount() - g.getActGarnAmt());
                g.setCurrentLoanTerm(g.getCurrentLoanTerm() - 1);
                if(g.getOwedAmount() <= 0.0D){
                    g.setEndDate(LocalDate.now());
                    g.setAmount(0.00D);
                    g.setOwedAmount(0.00D);
                }
                wActualSaveList.add(g);
                if(wActualSaveList.size() == 50){
                    genericService.storeVectorObjectBatch(wActualSaveList);
                    wActualSaveList = new Vector<>();
                }
            }
            if(!wActualSaveList.isEmpty()){
                saveObjects(wActualSaveList);
            }



        }catch(Exception wEx){
            log.error("Exception thrown from PayrollDaoImpl...");
            System.out.println(wEx.getMessage());
            System.out.println(wEx.getStackTrace());
            throw new Exception(wEx);
        }

    }

    @Override
    public void saveObjects(List<?> wListToSave) throws Exception {
        this.genericService.storeObjectBatch(wListToSave);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void savePayrollInformation(int pRunMonth,int pRunYear, LocalDate pPayDate,LocalDate pEndDate,LocalDate pStartDate,
                                       String pCurrPayPeriod, int pDevLevy,BusinessCertificate pBc) throws Exception
    {
        try{
             setPaychecksToApproved(pRunMonth, pRunYear, IppmsUtils.getPaycheckTableName(pBc));
             updateHiringInfoPayDates(pPayDate, pCurrPayPeriod, pBc);

            PayrollRunMasterBean wPRMB =  this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                    Arrays.asList(CustomPredicate.procurePredicate("runMonth", pRunMonth) ,
                            CustomPredicate.procurePredicate("runYear", pRunYear),
                            CustomPredicate.procurePredicate("businessClientId", pBc.getBusinessClientInstId())));

            PayrollFlag pf = IppmsUtilsExt.getPayrollFlagForClient(genericService,pBc);

            if (pf.isNewEntity()) {
                pf.setBusinessClientId(pBc.getBusinessClientInstId());
            }

            pf.setPayPeriodEnd(pEndDate);
            pf.setPayPeriodStart(pStartDate);

            pf.setApprovedMonthInd(pRunMonth);
            pf.setApprovedYearInd(pRunYear);
            pf.setPayrollRunMasterBean(wPRMB);
            this.storeObject(pf);


            List<Subvention> wListCurrent = this.genericService.loadAllObjectsUsingRestrictions(Subvention.class, Arrays.asList(
                    CustomPredicate.procurePredicate("businessClientId", pBc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("expire", 0)), null);
            List<Subvention> wListToInactivate = null;
            LocalDate wToday = LocalDate.now();
            for (Subvention s : wListCurrent) {
                if (s.getExpirationDate() != null) {

                    if ((s.getExpirationDate().isBefore(wToday)) || (s.getExpirationDate().compareTo(wToday) == 0)) {
                        if (wListToInactivate == null)
                            wListToInactivate = new ArrayList<>();
                        wListToInactivate.add(s);
                    }
                }
            }
            if (wListToInactivate != null) {
                 inactivateSubventions(wListToInactivate,pBc);
            }

            if (pDevLevy > 0) {
                DevelopmentLevy d = new DevelopmentLevy();

                d.setYear(pRunYear);
                d.setMonth(pRunMonth);
                d.setLastModBy(pBc.getLoginId());
                d.setBusinessClientId(pBc.getBusinessClientInstId());
                this.storeObject(d);
            }



            LtgMasterBean wLMB = this.genericService.loadObjectUsingRestriction(LtgMasterBean.class, Arrays.asList(
                    CustomPredicate.procurePredicate("simulationMonth",pEndDate.getMonthValue()),
                    CustomPredicate.procurePredicate("simulationYear", pEndDate.getYear()),
                    CustomPredicate.procurePredicate("businessClientId",pBc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("applicationIndicator",1)));

            if (!wLMB.isNewEntity())
            {
                
                List<AbmpBean> wDetails = this.genericService.loadAllObjectsWithSingleCondition(AbmpBean.class, CustomPredicate.procurePredicate("ltgMasterBean.id",wLMB.getId()), null);

                for (AbmpBean a : wDetails)
                {
                    MdapLtgAppIndBean m = new MdapLtgAppIndBean();

                    m.setLastModBy(pBc.getUserName());
                    m.setMdaInfo(a.getMdaInfo());
                    m.setLtgApplyMonth(pRunMonth);
                    m.setLtgApplyYear(pEndDate.getYear());

                    this.storeObject(m);
                }

            }

            PayrollRun pr = new PayrollRun();

            pr.setBusinessClientId(pBc.getBusinessClientInstId());
            pr.setPayPeriodStart( pStartDate);
            pr.setPayPeriodEnd( pEndDate);
            pr.setLastPayDate( pPayDate);
            pr.setPayrollApprovedBy(pBc.getLoggedOnUserNames());
            pr.setPayrollApprovalDate(LocalDate.now());
            pr.setPayrollApproved("Y");

            this.genericService.storeObject(pr);


        }catch(Exception wEx){
            log.error("Exception thrown from ApprovePayrollServiceImpl [ savePayrollInformation() ]");
            System.out.println(wEx.getMessage());
            System.out.println(wEx.getStackTrace());
            throw new Exception(wEx);
        }

    }

    @Override
    public void storeObject(Object pObject) {
        this.genericService.storeObject(pObject);
    }


    @Transactional
        public void setPaychecksToApproved(int pRunMonth, int pRunYear, String paycheckBeanName) {


            String hqlQuery = "update "+paycheckBeanName+" set runMonth = :pRunMonth, runYear = :pRunYear, status = :pNewStatus where status = :pOldStatus";
            Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
            query.setParameter("pRunMonth", pRunMonth);
            query.setParameter("pRunYear", pRunYear);
            query.setParameter("pNewStatus", "A");
            query.setParameter("pOldStatus", "P");

            query.executeUpdate();
        }

    @Transactional
    public void updateHiringInfoPayDates(LocalDate pPayDate, String pCurrentPayPeriod, BusinessCertificate bc) {
        String hqlQuery = "update HiringInfo set lastPayDate = :pDate where currentPayPeriod = :pCPP and businessClientId = :pBizIdVar";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pDate", pPayDate);
        query.setParameter("pCPP", pCurrentPayPeriod);
        query.setParameter("pBizIdVar", bc.getBusinessClientInstId());
        query.executeUpdate();
    }
    @Transactional()
    public void inactivateSubventions(List<Subvention> listToInactivate, BusinessCertificate businessCertificate) {
        for(Subvention l : listToInactivate){
            l.setExpire(1);
            l.setLastModBy(new User(businessCertificate.getLoginId()));
            l.setLastModTs(Timestamp.from(Instant.now()));
            storeObject(l);
        }

    }

}
