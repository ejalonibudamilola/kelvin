/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class RankValidator extends BaseValidator {


    public RankValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Rank.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Rank Name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", "Rank Description is required");

        Rank wObj = (Rank) pTarget;

        if (IppmsUtils.isNullOrLessThanOne(wObj.getCadreInstId())) {
             pErrors.rejectValue("cadreInstId", "Invalid.Value", "Please Select the Cadre this Rank belongs to.");
             return;

        }else{
            if(wObj.getFromLevel() >= 0 && wObj.getToLevel() >= 0){
                if(wObj.getFromLevel() > wObj.getToLevel()){
                    pErrors.rejectValue("fromLevel", "Invalid.Value", "'From Level' MUST be less than or equal to 'To Level'");
                }
                if(wObj.getFromStep() > wObj.getToStep()){
                    pErrors.rejectValue("fromStep", "Invalid.Value", "'From Step' MUST be less than or equal to 'To Step'");
                }
                if(wObj.getFromStep() == 0 && wObj.getFromLevel() > 0)
                    pErrors.rejectValue("fromStep", "Invalid.Value", "'From Step' MUST be greater than 0 if 'From Level' is greater than 0");

                if(wObj.getToLevel() > 0 && wObj.getToStep() == 0)
                    pErrors.rejectValue("toStep", "Invalid.Value", "'To Step' MUST be greater than 0 if 'To Level' is greater than 0");

            }

        }
        Rank wDBObj =  this.genericService.loadObjectUsingRestriction(Rank.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("name", wObj.getName().trim())));
        if (wObj.isEditMode()) {

            if (!wDBObj.isNewEntity()) {

                if (wDBObj.getName().trim().equalsIgnoreCase(wObj.getName().trim())
                        && !wDBObj.getId().equals(wObj.getId())) {
                    pErrors.rejectValue("name", "name.duplicate", "This Rank has already been defined");

                    return;
                }


            }

        } else {

            if (!wDBObj.isNewEntity()) {
                pErrors.rejectValue("name", "name.duplicate", "This Rank has already been defined");
                return;

            }
        }
        //--Now check if a Rank with this same cadre and Level/Step + SalaryType exists.
        if(wObj.getFromLevel() > 0 && wObj.getToLevel() > 0 && wObj.getFromStep() > 0 && wObj.getToStep() > 0) {
            wDBObj = this.genericService.loadObjectUsingRestriction(Rank.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("cadre.id", wObj.getCadreInstId()), CustomPredicate.procurePredicate("fromLevel", wObj.getFromLevel()),
                    CustomPredicate.procurePredicate("toLevel", wObj.getToLevel()), CustomPredicate.procurePredicate("fromStep", wObj.getFromStep()), CustomPredicate.procurePredicate("toStep", wObj.getToStep())));
            if (wObj.isEditMode()) {

                if (!wDBObj.isNewEntity()) {

                    if (wDBObj.getName().trim().equalsIgnoreCase(wObj.getName().trim())
                            && !wDBObj.getId().equals(wObj.getId())) {
                        pErrors.rejectValue("name", "name.duplicate", "This Rank has already been defined with a different Name - " + wDBObj.getName());

                        return;
                    }


                }

            } else {

                if (!wDBObj.isNewEntity()) {
                    pErrors.rejectValue("name", "name.duplicate", "This Rank has already been defined with a different Name - " + wDBObj.getName());
                    return;

                }
            }
        }

    }


}
