package com.osm.gnl.ippms.ogsg.validators.employee;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeHrBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class CreateNewEmployeeValidator extends BaseValidator {
    @Autowired
    public CreateNewEmployeeValidator(GenericService genericService) {
        super(genericService);
    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {


            ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "employeeId", "Required Field", "" + businessCertificate.getStaffTitle() + " is required");

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EmployeeHrBean.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fileNo", "Required Field", "File No. is Required.");
    }
}
