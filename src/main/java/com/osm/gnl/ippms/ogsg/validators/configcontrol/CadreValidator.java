/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.control.entities.Cadre;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class CadreValidator extends BaseValidator {


    public CadreValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Cadre.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Cadre Name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", "Cadre Description is required");

        Cadre wObj = (Cadre) pTarget;

        if (IppmsUtils.isNullOrLessThanOne(wObj.getSalaryType().getId())) {
            if (!wObj.isTerminationWarningIssued()) {
                wObj.setTerminationWarningIssued(true);

                pErrors.rejectValue("salaryType.id", "Invalid.Value", "Warning! This Cadre will be created without a Pay Group attached.");
            }
        }
        Cadre wDBObj =  this.genericService.loadObjectUsingRestriction(Cadre.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("name", wObj.getName().trim())));
        if (wObj.isEditMode()) {

            if (!wDBObj.isNewEntity()) {

                if (wDBObj.getName().trim().equalsIgnoreCase(wObj.getName().trim())
                        && !wDBObj.getId().equals(wObj.getId())) {
                    pErrors.rejectValue("name", "name.duplicate", "This Cadre has already been defined");

                    return;
                }


            }

        } else {

            if (!wDBObj.isNewEntity()) {
                pErrors.rejectValue("name", "name.duplicate", "This Cadre has already been defined");

            }
        }
        if(wObj.isDefaultIndBind()){
            wDBObj = this.genericService.loadObjectUsingRestriction(Cadre.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("defaultInd",1)));
            if(!wDBObj.isNewEntity()){
                if(!wDBObj.getId().equals(wObj.getId())){
                    pErrors.rejectValue("name", "name.duplicate", "Cadre '"+wDBObj.getName()+"' is already designated as the 'Default Cadre'");
                }
            }
        }


    }


}
