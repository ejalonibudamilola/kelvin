/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.domain.suspension.AbsorptionReason;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionType;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class SuspensionTypeValidator extends BaseValidator {

    protected SuspensionTypeValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SuspensionType.class) || clazz.isAssignableFrom(EmployeeType.class)
                || clazz.isAssignableFrom(AbsorptionReason.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
    public void validateForAbsorption(Object pTarget, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Absorption Reason is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", "Description is required");

        AbsorptionReason wObj = (AbsorptionReason) pTarget;

        AbsorptionReason wDBObj =  this.genericService.loadObjectUsingRestriction(AbsorptionReason.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("name", wObj.getName().trim())));
        if (wObj.isEditMode()) {

            if (!wDBObj.isNewEntity()) {

                if (wDBObj.getName().trim().equalsIgnoreCase(wObj.getName().trim())
                        && !wDBObj.getId().equals(wObj.getId())) {
                    pErrors.rejectValue("name", "name.duplicate", "This Absorption Reason has already been defined");

                    return;
                }


            }

        } else {

            if (!wDBObj.isNewEntity()) {
                pErrors.rejectValue("name", "name.duplicate", "This Absorption Reason has already been defined");
                return;

            }
        }
    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Suspension Name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", "Suspension Description is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Suspension Type is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", "Suspension Description is required");

        SuspensionType wObj = (SuspensionType) pTarget;

        SuspensionType wDBObj =  this.genericService.loadObjectUsingRestriction(SuspensionType.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("name", wObj.getName().trim())));
        if (wObj.isEditMode()) {

            if (!wDBObj.isNewEntity()) {

                if (wDBObj.getName().trim().equalsIgnoreCase(wObj.getName().trim())
                        && !wDBObj.getId().equals(wObj.getId())) {
                    pErrors.rejectValue("name", "name.duplicate", "This Suspension Type has already been defined");

                    return;
                }


            }

        } else {

            if (!wDBObj.isNewEntity()) {
                pErrors.rejectValue("name", "name.duplicate", "This Suspension Type has already been defined");
                return;

            }
        }
    }
    public void validateForEmployeeType(Object pTarget, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", bc.getStaffTypeName()+" Type is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", bc.getStaffTypeName()+" Type Description is required");

        EmployeeType wObj = (EmployeeType) pTarget;

        EmployeeType wDBObj =  this.genericService.loadObjectUsingRestriction(EmployeeType.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("name", wObj.getName().trim())));
        if (wObj.isEditMode()) {

            if (!wDBObj.isNewEntity()) {

                if (wDBObj.getName().trim().equalsIgnoreCase(wObj.getName().trim())
                        && !wDBObj.getId().equals(wObj.getId())) {
                    pErrors.rejectValue("name", "name.duplicate", "This "+bc.getStaffTypeName()+" Type has already been defined");

                    return;
                }


            }

        } else {

            if (!wDBObj.isNewEntity()) {
                pErrors.rejectValue("name", "name.duplicate", "This "+bc.getStaffTypeName()+" Type has already been defined");
                return;

            }
        }
        if(wObj.getEmployeeTypeCode() == 0 && !bc.isPensioner()){
            pErrors.rejectValue("name", "name.duplicate", " "+bc.getStaffTypeName()+" Type Code is Required.");
            return;
        }
    }

}
