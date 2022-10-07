package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class CreateNewLGAValidator extends BaseValidator {
    protected CreateNewLGAValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(LGAInfo.class);
    }

    @SneakyThrows
    @Override
    public void validate(Object pTarget, Errors pErrors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Name is a required field");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "recordCode", "Required.Value", "Record Code is required");

        LGAInfo p = (LGAInfo) pTarget;

        if (IppmsUtils.isNullOrLessThanOne(p.getStateId())) {
            pErrors.rejectValue("", "Reason.Invalid", "Please Select the 'Parent State' for this LGA");
            return;
        }


        LGAInfo _p;
        _p = this.genericService.loadObjectWithSingleCondition(LGAInfo.class, CustomPredicate.procurePredicate("name", p.getName()));


        if (!_p.isNewEntity()) {
            if (!p.isNewEntity()) {
                if (p.getId().intValue() != _p.getId().intValue()) {
                    pErrors.rejectValue("", "Reason.Invalid", "An LGA with this name already exists. Please change the 'LGA Name'");

                    return;
                }
            } else {
                pErrors.rejectValue("", "Reason.Invalid", "An LGA with this name already exists. Please change the 'LGA Name'");
                return;
            }
        }

        _p = this.genericService.loadObjectWithSingleCondition(LGAInfo.class, CustomPredicate.procurePredicate("recordCode", p.getRecordCode()));
        if (!_p.isNewEntity())
            if (!p.isNewEntity()) {
                if (p.getId().intValue() != _p.getId().intValue()) {
                    pErrors.rejectValue("", "Reason.Invalid", "LGA " + _p.getName() + " has this 'Record Code'. Please change the 'Record Code'.");

                    return;
                }
            } else {
                pErrors.rejectValue("", "Reason.Invalid", "LGA " + _p.getName() + " has this 'Record Code'. Please change the 'Record Code'.");
                return;
            }
    }

    public void validateUpdate(Object pTarget, Errors pErrors) {


        LGAInfo p = (LGAInfo) pTarget;

        if (IppmsUtils.isNullOrLessThanOne(p.getStateId())) {
            pErrors.rejectValue("", "Reason.Invalid", "Please Select the 'Parent State' for this LGA");
            return;
        }
    }
}
