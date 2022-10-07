/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.DeletePayrollBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

@Service("deleteService")
@Repository
@Transactional(readOnly = true)
@Slf4j
public class DeletePaycheckService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public DeletePayrollBean setPaycheckValues(DeletePayrollBean pDPB, BusinessCertificate businessCertificate) {

      String wHql = "select coalesce(sum(emp.taxesPaid),0),coalesce(sum(emp.netPay),0),coalesce(sum(emp.totalPay),0) " +
                "from "+ IppmsUtils.getPaycheckTableName(businessCertificate) +" emp where emp.status = :pStatusVal";

        Query wQuery =sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pStatusVal", "P");


        List<Object[]> list = wQuery.list();

        if (list != null && !list.isEmpty()) {
            for(Object[] d : list){
                pDPB.setMonthlyTaxStr(PayrollHRUtils.getDecimalFormat().format(d[0]));
                pDPB.setNetPayStr(PayrollHRUtils.getDecimalFormat().format(d[1]));
                pDPB.setTotalPayStr(PayrollHRUtils.getDecimalFormat().format(d[2]));
            }
        }
        return pDPB;



    }

    public double getTotalsByTable(BusinessCertificate bc, String dependentTable) {

        String wHql = "select coalesce(sum(p.amount),0)  " +
                "from "+dependentTable+" p,"+IppmsUtils.getPaycheckTableName(bc)+" emp where emp.status = :pStatusVal" +
                " and emp.id = p.employeePayBean.id";


        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pStatusVal", "P");


        List list = wQuery.list();

        if (list != null) {
            return (Double) list.get(0);
        } else {
            return 0.0D;
        }
    }

    public boolean doesPendingPayrollExist(BusinessCertificate businessCertificate) {
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(Arrays.asList(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("status","P")));
        
        return genericService.countObjectsUsingPredicateBuilder(predicateBuilder,IppmsUtils.getPaycheckClass(businessCertificate)) > 0;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deletePendingPaychecks(ArrayList<Long> pListPaychecksToDelete, BusinessCertificate businessCertificate)
            throws Exception
    {

        try{

            if(!pListPaychecksToDelete.isEmpty()){
                this.deleteAllObjectsInCollection(pListPaychecksToDelete,businessCertificate );

            }



        }catch(Exception wEx){
            log.error("Exception thrown from DeletePendingPayrollServiceImpl...");
            System.out.println(wEx.getMessage());
            System.out.println(wEx.getStackTrace());
            throw new Exception(wEx);
        }

    }
    @Transactional(rollbackFor = Exception.class)
    private synchronized void deleteAllObjectsInCollection(
            ArrayList<Long> pActualDeleteList, BusinessCertificate businessCertificate) throws Exception
    {
       // Session sess = this.sessionFactory.getCurrentSession();

      //  Query wQuery = sess.createQuery("delete from "+IppmsUtils.getPaycheckClass(businessCertificate)+" e where e.status = 'P' and e.id in ("+pActualDeleteList.toArray()+")");

        try {
          //  wQuery.setParameterList("pIds", pActualDeleteList.toArray());
           // wQuery.executeUpdate();
            /*for(Long l : pActualDeleteList){
                AbstractPaycheckEntity e = IppmsUtils.makePaycheckObject(businessCertificate);
                e.setId(l);
                this.genericService.deleteObject(e);
            }*/
            executeStoreProcedure(businessCertificate,pActualDeleteList);
        }catch(Exception wEx){

            log.error(wEx.getMessage());

            /*if(sess != null){

                sess.flush();
                sess.clear();

            }*/
            throw wEx;
        }


    }
    @Transactional()
    public void deleteCasacadingObject(PayrollRunMasterBean payrollRunMasterBean, RerunPayrollBean rerunPayrollBean) {
        this.genericService.deleteObject(payrollRunMasterBean);
        if(!rerunPayrollBean.isNewEntity())
            this.genericService.deleteObject(rerunPayrollBean);
    }

    public Vector<AbstractPaycheckEntity> loadPendingPaychecksForDeletion(BusinessCertificate bc) {
        Vector<AbstractPaycheckEntity> wRetList = new Vector<>();

        String wHql = "select epb.id "+
                " from "+IppmsUtils.getPaycheckTableName(bc)+" epb " +
                " where epb.status = 'P' ";

        Query wQuery = sessionFactory.getCurrentSession().createQuery(wHql);


        ArrayList<Long> wRetVal = (ArrayList<Long>)wQuery.list();

        if ((wRetVal != null) && (wRetVal.size() > 0))
        {

            for (Long o : wRetVal) {

                AbstractPaycheckEntity e = IppmsUtils.makePaycheckObject(bc);

                e.setId(o);

                wRetList.add(e);

            }

        }

        return wRetList;
    }

    @Transactional()
    public void executeStoreProcedure(BusinessCertificate bc, ArrayList<Long> pIdList) {
        String  sql  = "delete from  "+IppmsUtils.getPaycheckTableName(bc)+"  where id in (:pIds)";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameterList("pIds", pIdList.toArray());

        wQuery.executeUpdate();
    }
}
