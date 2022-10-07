/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.organization.model.Department;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("mdaService")
@Repository
@Transactional(readOnly = true)
public  class MdaService {

    @Autowired
    private GenericService genericService;
    @Autowired
    private SessionFactory sessionFactory;


    public List<Department> getDepartmentsByMdaId(Long pMdaId, boolean pExclude){

        String wHqlStr = "";

        if(pExclude) {
            wHqlStr = "select distinct d.id, d.name from Department d, MdaDeptMap m  " +
                    "where d.id = m.department.id and m.mdaInfo.id <> :pMdaInfoIdVar " +
                    "order by d.name";
        }else {
            wHqlStr = "select distinct d.id, d.name from Department d, MdaDeptMap m  " +
                    "where d.id = m.department.id and m.mdaInfo.id = :pMdaInfoIdVar " +
                    "order by d.name";
        }



        Query wQuery = this.genericService.getCurrentSession().createQuery(wHqlStr);
        wQuery.setParameter("pMdaInfoIdVar", pMdaId);
        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>)wQuery.list();
        List<Department> wRetMap = new ArrayList<Department>();
        if ((wRetVal != null) && (wRetVal.size() > 0))
        {
            for (Object[] o : wRetVal) {
                Department n = new Department();
                n.setId((Long)o[0]);
                n.setName((String)o[1]);

                wRetMap.add(n);
            }
        }

        return wRetMap;
    }
    public List<Long> loadMappedIdsByInnerClassId(
            Long pMdaInstId)
    {
        String wHql =  "select adm.id from MdaDeptMap adm where adm.mdaInfo.id = :pMdaId";

        List<Long> wRetMap = new ArrayList<Long>();
        Query wQuery = this.genericService.getCurrentSession().createQuery(wHql);
        wQuery.setParameter("pMdaId", pMdaInstId);

        ArrayList<Long> wRetVal = (ArrayList<Long>)wQuery.list();
        for(Long o : wRetVal){
            wRetMap.add(o);
        }
        return wRetMap;
    }
}
