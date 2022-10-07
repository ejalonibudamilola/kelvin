/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;

@Service("storedProcService")
@Repository
@Transactional()
public class StoredProcedureService {


    @Autowired
    private SessionFactory sessionFactory;


    public void callStoredProcedure(String procName, Long pBizClientId){
        Session session = sessionFactory.getCurrentSession();
        if(session.isDirty())
            session.flush();

        session.doWork(connection -> {
            CallableStatement callableStatement = connection.prepareCall("{call "+procName+"(?)}");
            callableStatement.setLong(1,pBizClientId);
            callableStatement.execute();
        });
    }

    public void callStoredProcedure(String procName, Long pBizClientId, Long pUserId,Long pSalaryTypeId, String pPayGroupCode){
        Session session = sessionFactory.getCurrentSession();
        if(session.isDirty())
            session.flush();

        session.doWork(connection -> {
            CallableStatement callableStatement = connection.prepareCall("{call "+procName+"(?,?,?,?)}");
            callableStatement.setLong(1,pBizClientId);
            callableStatement.setLong(2,pUserId);
            callableStatement.setLong(3,pSalaryTypeId);
            callableStatement.setString(4,pPayGroupCode);
            callableStatement.execute();
        });
    }
    public void callStoredProcedure(String procName, Long pBizClientId, Long pSpecAllowTypeId,Long pPayTypeId, Long pUserId){
        Session session = sessionFactory.getCurrentSession();
        if(session.isDirty())
            session.flush();

        session.doWork(connection -> {
            CallableStatement callableStatement = connection.prepareCall("{call "+procName+"(?,?,?,?)}");
            callableStatement.setLong(1,pBizClientId);
            callableStatement.setLong(2,pSpecAllowTypeId);
            callableStatement.setLong(3,pPayTypeId);
            callableStatement.setLong(4,pUserId);
            callableStatement.execute();
        });
    }
}
