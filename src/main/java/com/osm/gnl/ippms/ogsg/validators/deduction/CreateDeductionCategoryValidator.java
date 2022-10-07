package com.osm.gnl.ippms.ogsg.validators.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionCategory;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class CreateDeductionCategoryValidator extends BaseValidator {

    @Autowired
    public CreateDeductionCategoryValidator(GenericService genericService) {
        super(genericService);
    }


    public void validate(Object pTarget, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Deduction Category Name is a required field");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required.Value", "Description is required");

        EmpDeductionCategory p = (EmpDeductionCategory) pTarget;

        EmpDeductionCategory _p = this.genericService.loadObjectUsingRestriction(EmpDeductionCategory.class, Arrays.asList(CustomPredicate.procurePredicate("name", p.getName()),
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
        if (!_p.isNewEntity()) {
            if (!p.isNewEntity()) {
                if (!p.getId().equals(p.getId())) {
                    pErrors.rejectValue("name", "Reason.Invalid", "A Deduction Category with this name already exists. Please change the 'Deduction Category Name'.");

                    return;
                }
            } else {
                pErrors.rejectValue("name", "Reason.Invalid", "A Deduction Category with this name already exists. Please change the 'Deduction Category Name'.");
                return;
            }

        }
        if (p.isRangedDeduction()) {
            _p = this.genericService.loadObjectUsingRestriction(EmpDeductionCategory.class, Arrays.asList(CustomPredicate.procurePredicate("rangedInd", IConstants.ON),
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
            if (!_p.isNewEntity()) {
                pErrors.rejectValue("rangedInd", "Reason.Invalid", "Deduction Category '" + _p.getName() + "' is already designated as a 'Ranged' Deduction Category'");
                return;
            }
        }
        if (p.isApportionedDeduction()) {
            _p = this.genericService.loadObjectUsingRestriction(EmpDeductionCategory.class, Arrays.asList(CustomPredicate.procurePredicate("apportionedInd", IConstants.ON),
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
            if (!_p.isNewEntity()) {
                pErrors.rejectValue("apportionedInd", "Reason.Invalid", "Deduction Category '" + _p.getName() + "' is already designated as an 'Apportioned' Deduction Category'");
                return;
            }
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return EmpDeductionCategory.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Required.Value", "Deduction Category Name is a required field");
    }
}
