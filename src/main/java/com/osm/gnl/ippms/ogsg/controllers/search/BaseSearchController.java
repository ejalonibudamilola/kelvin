/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class BaseSearchController extends BaseController {


    protected  List<AbstractEmployeeEntity> doSearch(HrMiniBean pEHB, BusinessCertificate businessCertificate){

          boolean useFirstName = false;
          boolean useLastName = false;
          boolean useEmployeeId = false;
          boolean useLegacyId = false;


        if (IppmsUtils.isNotNullOrEmpty(pEHB.getEmployeeId())) {
            pEHB.setEmployeeId(PayrollHRUtils.treatSqlStringParam(pEHB.getEmployeeId(), false));

            useEmployeeId = true;
        } else {
            if(businessCertificate.isPensioner()){
                IppmsUtils.isNotNullOrEmpty(pEHB.getLegacyEmployeeId());
                    useLegacyId = true;

            }
            if (IppmsUtils.isNotNullOrEmpty(pEHB.getFirstName())) {
                pEHB.setFirstName(PayrollHRUtils.treatSqlStringParam(pEHB.getFirstName(), false));
                useFirstName = true;
            }
            if (IppmsUtils.isNotNullOrEmpty(pEHB.getLastName())) {
                pEHB.setLastName(PayrollHRUtils.treatSqlStringParam(pEHB.getLastName(), false));
                useLastName = true;
            }

        }
        List<CustomPredicate> wList = new ArrayList<>();
        if(useFirstName)
            wList.add(CustomPredicate.procurePredicate("firstName",pEHB.getFirstName(), Operation.LIKE));
        if(useLastName)
            wList.add(CustomPredicate.procurePredicate("lastName",pEHB.getLastName(), Operation.LIKE));
        if(useEmployeeId) {
            if(pEHB.getEmployeeId().length() <= 5)
               wList.add(CustomPredicate.procurePredicate("employeeId", pEHB.getEmployeeId(), Operation.LIKE));
            else
                wList.add(CustomPredicate.procurePredicate("employeeId", pEHB.getEmployeeId()));
        }
        if(useLegacyId){
            if(pEHB.getLegacyEmployeeId().length() <= 5)
                wList.add(CustomPredicate.procurePredicate("legacyEmployeeId", pEHB.getLegacyEmployeeId().trim().toUpperCase(), Operation.LIKE));
            else
                wList.add(CustomPredicate.procurePredicate("legacyEmployeeId", pEHB.getLegacyEmployeeId().trim().toUpperCase()));
        }
         wList.add(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(wList);

        List<AbstractEmployeeEntity> emp = (List<AbstractEmployeeEntity>)this.genericService.getObjectsFromBuilder(predicateBuilder,IppmsUtils.getEmployeeClass( businessCertificate));

      return emp;
    }

}
