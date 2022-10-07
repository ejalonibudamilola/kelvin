/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.customreport;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepGenBean;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomReportObjectAttr;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollExcelUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class CustomReportValidator extends BaseValidator {

    protected CustomReportValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(CustomRepGenBean.class);
    }

    @Override
    public void validate(Object target, Errors pErrors) {
        CustomRepGenBean customRepGenBean = (CustomRepGenBean)target;

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "fileName", "Required.Value", "Report File Name is a required field");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "mainHeader", "Required.Value", "Main Report Header is a required field");


        if(customRepGenBean.isUseDefInd()){
            for(CustomReportObjectAttr c : customRepGenBean.getHeaderObjects()){
                if(IppmsUtils.isNullOrEmpty(c.getDefDisplayName())){
                    pErrors.rejectValue("excelInd", "Required.Value", "Please enter the Current Display Name for Default Display Name '"+c.getDefDisplayName()+"'");
                    return;
                }else if(c.getDefDisplayName().trim().length() < 3){
                    pErrors.rejectValue("excelInd", "Required.Value", "Current Display Name for Default Display Name '"+c.getDefDisplayName()+"' must be more than 2 characters");
                    return;
                }else{
                    if(!PayrollExcelUtils.isXlsNameCompatible(c.getDefDisplayName())){
                        pErrors.rejectValue("excelInd", "Required.Value", "Current Display Name for Default Display Name '"+c.getDefDisplayName()+"' contains special characters.");
                        pErrors.rejectValue("excelInd", "Required.Value", "Special Characters are not allowed in File Naming");
                        return;
                    }
                }
            }
        }

    }


}
