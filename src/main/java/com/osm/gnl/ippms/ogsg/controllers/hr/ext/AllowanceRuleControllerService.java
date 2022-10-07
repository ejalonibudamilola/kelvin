/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;

import java.time.LocalDate;

public abstract class  AllowanceRuleControllerService {

    public static HrMiniBean setRequiredValues(HiringInfo pWhi, HrMiniBean pEmpHrBean, boolean forSuccess) throws IllegalAccessException {


        pEmpHrBean.setName(pWhi.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed());
        pEmpHrBean.setGradeLevelAndStep(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getSalaryScaleLevelAndStepStr());

        if (pEmpHrBean.getAllowanceRuleMaster().getHiringInfo() == null)
            pEmpHrBean.getAllowanceRuleMaster().setHiringInfo(pWhi);

        pEmpHrBean.getAllowanceRuleMaster().build();
        pEmpHrBean.setHireDate(PayrollHRUtils.getDisplayDateFormat().format(pWhi.getHireDate()));

        int noOfYears = LocalDate.now().getYear() - pWhi.getHireDate().getYear();

        pEmpHrBean.setYearsOfService(String.valueOf(noOfYears));

        pEmpHrBean.setAssignedToObject(pWhi.getAbstractEmployeeEntity().getCurrentMdaName());
        pEmpHrBean.setEmployeeId(pWhi.getAbstractEmployeeEntity().getEmployeeId());
        pEmpHrBean.setId(pWhi.getId());
        if(forSuccess){
            pEmpHrBean.setEndDateString(PayrollHRUtils.getDisplayDateFormat().format(pEmpHrBean.getAllowanceRuleMaster().getRuleStartDate())+" To "+
                    PayrollHRUtils.getDisplayDateFormat().format(pEmpHrBean.getAllowanceRuleMaster().getRuleEndDate()));
            pEmpHrBean.setForSuccessDisplay(true);
        }

        return pEmpHrBean;
    }
}
