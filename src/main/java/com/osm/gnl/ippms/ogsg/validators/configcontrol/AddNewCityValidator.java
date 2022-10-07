package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class AddNewCityValidator extends BaseValidator {

    protected AddNewCityValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(City.class);
    }

    @SneakyThrows
    @Override
    public void validate(Object pTarget, Errors pErrors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Name is a required field");

        City p = (City) pTarget;

        if (IppmsUtils.isNullOrLessThanOne(p.getStateId())) {
            pErrors.rejectValue("", "Reason.Invalid", "Please Select the 'Parent State' for this City");
            return;
        }


        City _p;
        _p = this.genericService.loadObjectWithSingleCondition(City.class, CustomPredicate.procurePredicate("name", p.getName()));


        if (!_p.isNewEntity()) {
            if (!p.isNewEntity()) {
                if (p.getId().intValue() != _p.getId().intValue()) {
                    pErrors.rejectValue("", "Reason.Invalid", "A City with this name already exists. Please change the 'City Name'");

                    return;
                }
            } else {
                pErrors.rejectValue("", "Reason.Invalid", "A City with this name already exists. Please change the 'City Name'");
                return;
            }
        }


    }

    public void validateUpdate(Object pTarget, Errors pErrors) throws InstantiationException, IllegalAccessException {

//        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "recordCode", "Required.Value", "Record Code is required");

        City p = (City) pTarget;

        if (IppmsUtils.isNullOrLessThanOne(p.getStateId())) {
            pErrors.rejectValue("", "Reason.Invalid", "Please Select the 'Parent State' for this City");
            return;
        }
    }
}
