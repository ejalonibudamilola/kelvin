/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.ON;

@Service("searchService")
@Repository
@Transactional(readOnly = true)
public class SearchService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;



    public List<AbstractEmployeeEntity> searchForEmployeeId(BusinessCertificate bc, String empId, int startRowIndex, int pageLength, String pProperty, int activeInd ) throws Exception {

        List<AbstractEmployeeEntity> wEmpList = new ArrayList<>();

        boolean isLegacy = pProperty.toLowerCase().startsWith("legacy");
        Criteria crit = this.sessionFactory.getCurrentSession().createCriteria( IppmsUtils.getEmployeeClass(bc) )
                .setFirstResult( startRowIndex )
                .setMaxResults( pageLength )
                .createAlias("salaryInfo", "salInfo", CriteriaSpecification.LEFT_JOIN)
                .createAlias("salInfo.salaryType", "salType", CriteriaSpecification.LEFT_JOIN)
                .createAlias("mdaDeptMap", "aDM", CriteriaSpecification.LEFT_JOIN).createAlias("aDM.mdaInfo", "mdaInfo", CriteriaSpecification.LEFT_JOIN)
                .setProjection(
                        Projections.projectionList()
                                .add( Projections.distinct( Projections.property( pProperty ) )  )
                                .add( Projections.id() )
                                .add( Projections.property( "firstName" ) )
                                .add( Projections.property( "initials" ) )
                                .add( Projections.property( "lastName" ) )
                                .add( Projections.property( "statusIndicator" ) )
                                .add( Projections.property( "salInfo.level" ) )
                                .add( Projections.property( "salInfo.step" ) )
                                .add( Projections.property( "salType.name" ) )
                                .add( Projections.property( "mdaInfo.codeName" ) )
                                .add( Projections.property("legacyEmployeeId"))
                );

        //check if the employee id has tokens
        String[] employeeIdTokens = StringUtils.split( empId, ' ' );

        this.addFieldContainsStringToCriteria( crit, employeeIdTokens, new String[] { pProperty } );

        crit.add(Restrictions.eq("businessClientId", bc.getBusinessClientInstId()));
        if(activeInd == ON){
            crit.add(Restrictions.eq("statusIndicator", 0));
        }else if(activeInd == 2){
            crit.add(Restrictions.eq("statusIndicator", 1));
        }

        crit.addOrder( Order.asc( pProperty ) );

        try{
            List<Object[]> list = crit.list();

            if( !list.isEmpty() ) {
                for( Object[] o : list ) {
                    //id is the key and employee id the value
                    AbstractEmployeeEntity employee = IppmsUtils.makeEmployeeObject(bc);
                    employee.setId((Long)o[1]);
                    if(!isLegacy)
                        employee.setEmployeeId( (String) o[ 0 ] );
                    else
                        employee.setLegacyEmployeeId((String) o[ 0 ] );
                    employee.setFirstName( (String) o[ 2 ] );
                    employee.setInitials( (String) o[ 3 ] );
                    employee.setLastName( (String) o[ 4 ] );
                    employee.setStatusIndicator((Integer)o[5]);
                    SalaryInfo s = new SalaryInfo();
                    s.setLevel((Integer)o[6]);
                    s.setStep((Integer)o[7]);
                    String sType = (String)o[8];
                    employee.setLevelStepStr(sType + ":" + s.getLevelAndStepAsStr());
                    employee.setMdaName((String)o[9]);
                    if(o[10] != null && isLegacy) {
                        employee.setLegacyEmployeeId((String) o[10]);
                        employee.setEmployeeId(null);
                    }else
                        employee.setLegacyEmployeeId("");


                    wEmpList.add( employee );
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }




        return wEmpList;
    }


    private Criteria addFieldContainsStringToCriteria( Criteria criteria, String[] tokens, String[] fieldNames  ) {
        if( (criteria != null) && (tokens != null) && (tokens.length > 0)
                && (fieldNames != null) && (fieldNames.length > 0) ) {

            Disjunction disjunction = Restrictions.disjunction();

            for( String token : tokens ) {

                for(String fieldName : fieldNames ) {
                    disjunction.add(Restrictions.ilike(fieldName, token, MatchMode.ANYWHERE) );
                    disjunction.add(Restrictions.ilike(fieldName, token, MatchMode.EXACT) );
                    disjunction.add(Restrictions.ilike(fieldName, token, MatchMode.END) );
                    disjunction.add(Restrictions.ilike(fieldName, token, MatchMode.START) );
                }
            }

            criteria.add(disjunction);
        }

        return criteria;
    }

    public List<String> searchForEmployeeByNames( String wParamValue, String wParam, int startRowIndex, int pageLength, BusinessCertificate bc)  throws Exception {

        List<String> wEmpList = null;

        //check if the employee id has tokens
        char[] employeeIdTokens = wParamValue.toCharArray();
        StringBuilder employeeId = new StringBuilder();
        for(char token : employeeIdTokens){
            if(token == ' ' || String.valueOf(token).equals("'")){
                continue;
            }

            employeeId.append("%").append(token);
        }
        if(!employeeId.toString().isEmpty())
            employeeId.append("%");

        String wHql = "";
        if(!IppmsUtils.isNullOrEmpty(wParam)){
            wHql = "select " + wParam
                    + " from "+IppmsUtils.getEmployeeTableName(bc)+" e where upper(e." + wParam + ") like '"+ employeeId.toString().toUpperCase()+"'"
                    + " and e.businessClientId = :pBizId "
                    + " group by " + wParam
                    + " order by " + wParam;
        }


        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(wHql);

        wQuery.setParameter("pBizId", bc.getBusinessClientInstId());
        if(startRowIndex > 0)
            wQuery.setFirstResult(startRowIndex);
        wQuery.setMaxResults(pageLength);

        wEmpList = wQuery.list();
        if(IppmsUtils.isNullOrEmpty(wEmpList))
            return new ArrayList<>();
        else
            return wEmpList;

    }
}
