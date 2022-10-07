/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.approval.EmployeeApproval;
import com.osm.gnl.ippms.ogsg.domain.approval.AllowanceRuleApproval;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.domain.approval.TransferApproval;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("transferService")
@Repository
@Transactional(readOnly = true)
public class TransferService {
    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public int getTotalNoOfActiveTransferApprovals(Long pBizId) {
        return this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF))
                .addPredicate(CustomPredicate.procurePredicate("businessClientId", pBizId)), TransferApproval.class);

    }

    public int getTotalNoOfActiveAllowanceApprovals(Long pBizId) {
        return this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF))
                .addPredicate(CustomPredicate.procurePredicate("businessClientId", pBizId)), AllowanceRuleApproval.class);

    }
    public int getTotalNoOfActiveTransferApprovals(BusinessCertificate bc,Long pEmpId,List<Long> pEmpIds,Long pUid)
    {
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
        if(IppmsUtils.isNotNullAndGreaterThanOrEqualToZero(pEmpId)){
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmpId));
        }
        if(IppmsUtils.isNotNullOrEmpty(pEmpIds)){
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), (Comparable) pEmpIds,Operation.IN));
        }
        if(IppmsUtils.isNotNullAndGreaterThanOrEqualToZero(pUid)){
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("initiator.id", pUid));
        }
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        return this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, TransferApproval.class);
    }

    public int getTotalNoOfActiveAllowanceApprovals(BusinessCertificate bc,Long pEmpId,List<Long> pEmpIds,Long pUid)
    {
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
        if(IppmsUtils.isNotNullAndGreaterThanOrEqualToZero(pEmpId)){
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("allowanceRuleMaster.hiringInfo.id", pEmpId));
        }
        if(IppmsUtils.isNotNullOrEmpty(pEmpIds)){
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("allowanceRuleMaster.hiringInfo.id", (Comparable) pEmpIds, Operation.IN));
        }
        if(IppmsUtils.isNotNullAndGreaterThanOrEqualToZero(pUid)){
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("initiator.id", pUid));
        }
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        return this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, AllowanceRuleApproval.class);
    }

    public List<User> loadCurrentViewActiveLogin(BusinessCertificate businessCertificate)
    {
        String wStr = "select distinct(l.id),l.firstName,l.lastName from User l, TransferApproval t" +
                " where t.initiator.id = l.id and t.approvalStatusInd = 0 and t.businessClientId = :pBizIdVar order by l.firstName,l.lastName";
        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wStr);
        wQuery.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId());
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<User> wRetMap = new ArrayList<>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                User n = new User();
                n.setId((Long)o[0]);
                n.setFirstName((String)o[1]);
                n.setLastName((String)o[2]);
                wRetMap.add(n);
            }
        }
        return wRetMap;
    }

    public List<Long> loadEmployeeInstIdsByStringValue(BusinessCertificate businessCertificate,String pCompareValue)
    {
        String wHqlStr = "select e.id from "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e,TransferApproval t where e.lastName like :pLastNameVar " +
                " and t."+businessCertificate.getEmployeeIdJoinStr()+" = e.id and t.approvalStatusInd = 0 and t.businessClientId = :pBizIdVar";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        wQuery.setParameter("pLastNameVar", "%"+pCompareValue+"%");
        wQuery.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId());
        List<Long> wRetList = wQuery.list();

        if(wRetList == null)
            wRetList = new ArrayList<>();
        return wRetList;
    }
    public List<Long> loadHiringInfoIdsByStringValue(BusinessCertificate businessCertificate,String pCompareValue)
    {
        String wHqlStr = "select h.id from Employee e,AllowanceRuleApproval t, HiringInfo h where e.lastName like :pLastNameVar " +
                " and t.hiringInfo.id = h.id and h.employee.id = e.id and t.approvalStatusInd = 0 and h.businessClientId = e.businessClientId " +
                " and t.businessClientId = e.businessClientId and e.businessClientId = :pBizIdVar";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        wQuery.setParameter("pLastNameVar", "%"+pCompareValue+"%");
        wQuery.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId());
        List<Long> wRetList = wQuery.list();

        if(wRetList == null)
            wRetList = new ArrayList<>();
        return wRetList;
    }

    public int getNoOfEmpForPayrollApproval(BusinessCertificate businessCertificate,boolean includeRejections) {
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        if(includeRejections){
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", 1, Operation.NOT_EQUAL));
        }else{
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", 0, Operation.EQUALS));
        }
         return genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmployeeApproval.class);
    }


}
