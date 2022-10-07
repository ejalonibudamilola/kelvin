/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.dao;

import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.beans.BankPVSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;

import java.time.LocalDate;
import java.util.List;

public interface IPaycheckDao {


    List<?> loadEmployeePayBeanByRunMonthAndRunYear(BusinessCertificate b, int runMonth, int runYear );

    Long getMaxPaycheckIdForEmployee(BusinessCertificate businessCertificate, Long pEmpId);


    LocalDate getPendingPaycheckRunMonthAndYear(BusinessCertificate pBusinessCertificate);

     void updPendPayDedValues(EmpDeductionType pEHB, int pRunMonth, int pRunYear, Object[] pIntValues, BusinessCertificate businessCertificate);

     List<NamedEntity> makePaycheckYearList(BusinessCertificate businessCertificate);


    void updateMdaForPendingPaychecks(AbstractEmployeeEntity wEmp, LocalDate wCal, Long wSchoolInstId, BusinessCertificate bc);

    BankPVSummaryBean loadEmployeePayBeanByFromDateToDateAndBank(LocalDate pSomeDate, BusinessCertificate bc);
}
