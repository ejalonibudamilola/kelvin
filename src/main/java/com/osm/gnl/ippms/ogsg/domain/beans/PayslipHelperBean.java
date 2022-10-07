/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class PayslipHelperBean {

    private HashMap<String, EmpDeductMiniBean> allowances;
    private EmployeePayMiniBean employeePayMiniBean;

    public PayslipHelperBean(EmployeePayMiniBean pEmpPayMiniBean, HashMap<String, EmpDeductMiniBean> allowances) {
        this.allowances = allowances;
        this.employeePayMiniBean = pEmpPayMiniBean;
    }

}
