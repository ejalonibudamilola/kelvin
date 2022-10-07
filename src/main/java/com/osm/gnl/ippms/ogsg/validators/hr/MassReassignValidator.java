/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.MassReassignMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class MassReassignValidator extends BaseValidator {

    protected MassReassignValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MassReassignMasterBean.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Required Field", "Mass Reassignment Name is required");
    }


    public void validate(Object target, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Mass Reassignment Name is required");

        MassReassignMasterBean massReassignMasterBean = (MassReassignMasterBean)target;

        if(IppmsUtils.isNullOrLessThanOne(massReassignMasterBean.getFromSalaryType().getId()))
            pErrors.rejectValue("fromSalaryType.id", "Required.Value", "Please select a value for 'From Pay Group' ");

        if(IppmsUtils.isNullOrLessThanOne(massReassignMasterBean.getToSalaryType().getId()))
            pErrors.rejectValue("toSalaryType.id", "Required.Value", "Please select a value for 'To Pay Group' ");

        if (pErrors.getErrorCount() < 1) {
            if (massReassignMasterBean.getFromSalaryType().getId().equals(massReassignMasterBean.getToSalaryType().getId()))
                pErrors.rejectValue("toSalaryType.id", "Required.Value", "'From Pay Group' MUST BE DIFFERENT FROM 'To Pay Group' ");

            //Check if there is an existing non approved MassReassignMasterBean..
            MassReassignMasterBean reassignMasterBean = this.genericService.loadObjectUsingRestriction(MassReassignMasterBean.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("fromSalaryType.id", massReassignMasterBean.getFromSalaryType().getId()),
                            CustomPredicate.procurePredicate("toSalaryType.id", massReassignMasterBean.getToSalaryType().getId())));
            if(!reassignMasterBean.isNewEntity()){
                if(!reassignMasterBean.isRejected()){
                    pErrors.rejectValue("toSalaryType.id", "Required.Value", "Existing Mass Reassignment Found. Please Approve or Reject existing Mass Reassignment to continue. ");
                    return;
                }

            }
            PredicateBuilder predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("salaryInfo.salaryType.id",massReassignMasterBean.getFromSalaryType().getId()));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("statusIndicator",0));
            int noOfEmp = genericService.countObjectsUsingPredicateBuilder(predicateBuilder, Employee.class);
            if(noOfEmp == 0) {
                pErrors.rejectValue("toSalaryType.id", "Required.Value", "" +
                        "No "+bc.getStaffTypeName()+" found to Reassign ");
            }
        }
    }
}
