/*
 * Copyright (c) 2020. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.dao;

import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.report.beans.RetMainBean;

import java.util.HashMap;

public interface ISimulationDao {

    RetMainBean  populateAverages(final BusinessCertificate businessCertificate,int wStartMonth, int wStartYear, int wRunMonth, int wRunYear, int noOfMonths);

    HashMap<Long, Long> getObjectsToApplyLtg(Long pLtgMasterInstId);
}
