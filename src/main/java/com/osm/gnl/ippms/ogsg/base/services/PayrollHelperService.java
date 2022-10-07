/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("payrollHelperService")
@Repository
@Transactional(readOnly = true)
public class PayrollHelperService {



    private final SessionFactory sessionFactory;
    @Autowired
    public PayrollHelperService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public List<Long> makeAbsorbReinstateList(BusinessCertificate bc, boolean absorbtion, int pRunMonth, int pRunYear) {

        String sql;
        String pTableName;
        if(absorbtion)
            pTableName = "ReabsorbFlag";
        else
            pTableName = "ReinstateFlag";
        if(bc.isPensioner()){
            sql = "select p.pensioner.id from "+pTableName+" p where runMonth = "+pRunMonth+" and runYear = "+pRunYear+" "+
                    "and p.businessClientId = "+bc.getBusinessClientInstId();
        }else{
            sql = "select p.employee.id from "+pTableName+" p where runMonth = "+pRunMonth+" and runYear = "+pRunYear+" "+
                    "and p.businessClientId = "+bc.getBusinessClientInstId();
        }

        Query query = sessionFactory.getCurrentSession().createQuery(sql);

       return (ArrayList<Long>)query.list();


    }


}
