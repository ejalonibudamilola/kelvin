/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.hibernate.query.NativeQuery;

import java.util.Arrays;

public abstract class IppmsUtilsHelper {



    public static AllowanceRuleMaster getActiveAllowanceRule(GenericService genericService, Long pBusClientId, Long pHireId) throws IllegalAccessException, InstantiationException {
        return  genericService.loadObjectUsingRestriction(AllowanceRuleMaster.class,
                Arrays.asList(CustomPredicate.procurePredicate("hiringInfo.id", pHireId) ,
                        CustomPredicate.procurePredicate("activeInd", 1, Operation.GREATER_OR_EQUAL),
                        CustomPredicate.procurePredicate("hiringInfo.businessClientId", pBusClientId)));
    }


}
