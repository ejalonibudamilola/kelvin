/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PaySlipService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import org.springframework.ui.Model;

//Does nothing
public interface IPaycheckGenerator {

     Object generatePaySlipModel(EmployeePayMiniBean empPayMiniBean, AbstractPaycheckEntity empPayBean, GenericService genericService, BusinessCertificate businessCertificate, Model pModel, ConfigurationBean configurationBean, PaySlipService pPaySlipService) throws Exception;

}
