/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class TitleValidator  extends BaseValidator {


    public TitleValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Title.class.isAssignableFrom(clazz);
    }

    @SneakyThrows
    @Override
    public void validate(Object pTarget, Errors pErrors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Cadre Name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", "Cadre Description is required");

        Title wObj = (Title) pTarget;


        Title wDBObj =  this.genericService.loadObjectWithSingleCondition(Title.class,  CustomPredicate.procurePredicate( "name", wObj.getName().trim()));
        if (wObj.isEditMode()) {

            if (!wDBObj.isNewEntity()) {

                if (wDBObj.getName().trim().equalsIgnoreCase(wObj.getName().trim())
                        && !wDBObj.getId().equals(wObj.getId())) {
                    pErrors.rejectValue("name", "name.duplicate", "This Title has already been defined");

                    return;
                }


            }

        } else {

            if (!wDBObj.isNewEntity()) {
                pErrors.rejectValue("name", "name.duplicate", "This Title has already been defined");

            }
        }

    }




}
