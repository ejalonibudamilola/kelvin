package com.osm.gnl.ippms.ogsg.base.services;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("massReassignmentService")
@Repository
@Transactional
public class MassReassignmentService {

    @Autowired
    private GenericService genericService;
    @Autowired
    private SessionFactory sessionFactory;

    public MassReassignmentService(){}

    public void updateSalaryInfo(Long pEmpId,  Long tSId, Long pBid){

        String wHql = "update Employee e set e.salaryInfo.id = :toSalaryInfoId where " +
                "e.id = :pEmpId and e.businessClientId = :pBidVar";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("toSalaryInfoId", tSId);
        query.setParameter("pEmpId", pEmpId);
        query.setParameter("pBidVar",pBid);
        query.executeUpdate();

    }
}
