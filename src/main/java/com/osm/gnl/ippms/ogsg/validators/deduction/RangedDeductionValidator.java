/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.RangedDeduction;
import com.osm.gnl.ippms.ogsg.domain.payment.RangedDeductionDetails;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class RangedDeductionValidator extends BaseValidator {
    @Autowired
    protected RangedDeductionValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RangedDeduction.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

    public void validateForEdit(Object target, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {


        RangedDeduction rangedDeduction = (RangedDeduction) target;
        List<RangedDeductionDetails> checkList = rangedDeduction.getRangedDeductionDetailsList();

        Object checkLowerBound;
        Object checkUpperBound;
        Object checkAmount;
        Object compareLowerBound;
        Object compareUpperBound;
        Object compareAmount;

        for (RangedDeductionDetails r1 : checkList) {
            checkLowerBound = checkIfValidDouble(r1.getLowerBoundAsStr());
            boolean errorRecord = false;
            if (checkLowerBound.getClass().isAssignableFrom(String.class)) {
                pErrors.rejectValue("lowerBoundAsStr", "Required.Value", "Lower Bound must be all Numeric.");
                errorRecord = true;
            }
            checkUpperBound = checkIfValidDouble(r1.getUpperBoundAsStr());
            if (checkUpperBound.getClass().isAssignableFrom(String.class)) {
                pErrors.rejectValue("upperBoundAsStr", "Required.Value", "Upper Bound must be all Numeric.");
                errorRecord = true;
            }
            checkAmount = checkIfValidDouble(r1.getAmountAsStr());
            if (checkAmount.getClass().isAssignableFrom(String.class)) {
                pErrors.rejectValue("upperBoundAsStr", "Required.Value", "Amount must be all Numeric.");
                errorRecord = true;
            }

            if (errorRecord) continue;

            for (RangedDeductionDetails r : checkList) {
                if (r1.getId().equals(r.getId()))
                    continue;
                errorRecord = false;
                compareLowerBound = checkIfValidDouble(r.getLowerBoundAsStr());
                if (compareLowerBound.getClass().isAssignableFrom(String.class)) {
                    errorRecord = true;
                }
                compareUpperBound = checkIfValidDouble(r.getUpperBoundAsStr());
                if (compareUpperBound.getClass().isAssignableFrom(String.class)) {
                    errorRecord = true;
                }
                compareAmount = checkIfValidDouble(r.getAmountAsStr());
                if (compareAmount.getClass().isAssignableFrom(String.class)) {

                    errorRecord = true;
                }

                if (errorRecord) continue;
                //@NOTE: Do Not remove the CASTS....
                if ((Double) checkLowerBound < (Double) compareLowerBound) {
                    if ((Double) checkUpperBound >= (Double) compareUpperBound)
                        pErrors.rejectValue("upperBoundAsStr", "Required.Value", "Invalid value for " + r.getUpperBoundAsStr());
                    if ((Double) checkAmount > (Double) compareAmount)
                        pErrors.rejectValue("upperBoundAsStr", "Required.Value", "Invalid value for " + r.getAmountAsStr());
                } else if ((Double) checkLowerBound > (Double) compareLowerBound) {
                    if ((Double) checkUpperBound <= (Double) compareUpperBound)
                        pErrors.rejectValue("upperBoundAsStr", "Required.Value", "Invalid value for " + r.getUpperBoundAsStr());
                    if ((Double) checkAmount <= (Double) compareAmount)
                        pErrors.rejectValue("upperBoundAsStr", "Required.Value", "Invalid value for " + r.getAmountAsStr());
                }
            }
        }


    }

    private Object checkIfValidDouble(String str) {
        Object retVal;
        try {
            retVal = Double.parseDouble(PayrollHRUtils.removeCommas(str));
        } catch (Exception wEx) {
            retVal = "Error";
        }
        return retVal;
    }

    public void validate(Object target, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "lowerBoundValue", "Lower Bound is a Required Field");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "upperBoundValue", "Upper Bound is a Required Field");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "amountStr", "Deduction Amount is a Required Field");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Deduction Name is a Required Field");

        if (pErrors.getErrorCount() < 1) {
            RangedDeduction rangedDeduction = (RangedDeduction) target;
            Double lowerBound = 0.0D;
            Double upperBound = 0.0D;
            Double amount = 0.0D;
            try {
                lowerBound = Double.parseDouble(PayrollHRUtils.removeCommas(rangedDeduction.getLowerBoundValue()));
            } catch (Exception wEx) {
                pErrors.rejectValue("lowerBoundValue", "DuplicateEntry", " Lower Bound Value must be Numeric.");
                return;
            }
            try {
                upperBound = Double.parseDouble(PayrollHRUtils.removeCommas(rangedDeduction.getUpperBoundValue()));
            } catch (Exception wEx) {
                pErrors.rejectValue("upperBoundValue", "DuplicateEntry", " Upper Bound Value must be Numeric.");
                return;
            }

            //check if the Upper and Lower bound values is already defined.
            if (!IppmsUtils.isNullOrEmpty(rangedDeduction.getRangedDeductionDetailsList())) {
                for (RangedDeductionDetails r : rangedDeduction.getRangedDeductionDetailsList()) {
                    if (lowerBound < r.getLowerBound()) {
                        if (upperBound >= r.getUpperBound()) {
                            pErrors.rejectValue("lowerBoundValue", "Required.Value", "Overlapping 'Lower Bound / Upper Bound' Found. Please enter different values.");
                            return;
                        }
                    } else if (lowerBound > r.getLowerBound()) {
                        if (upperBound <= r.getUpperBound()) {
                            pErrors.rejectValue("upperBoundValue", "Required.Value", "Overlapping 'Upper Bound' Found. Please enter different values.");
                            return;
                        }
                        if (lowerBound < r.getUpperBound()) {
                            pErrors.rejectValue("upperBoundValue", "Required.Value", "Overlapping 'Lower Bound' Found. Please enter different values.");
                            return;
                        }
                    }
                    if (amount == r.getAmount()) {
                        pErrors.rejectValue("amountStr", "Required.Value", "Duplicated Deduction Amount for different Upper and Lower Bounds found.");
                        return;
                    }
                }
            }
            if (rangedDeduction.isFirstTimePay()) {
                RangedDeduction rangedDeduction1 = genericService.loadObjectUsingRestriction(RangedDeduction.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("name", rangedDeduction.getName(), Operation.STRING_EQUALS)));
                if (rangedDeduction.isNewEntity() && !rangedDeduction1.isNewEntity()) {
                    pErrors.rejectValue("amountStr", "Required.Value", "A Ranged Deduction with this name already exists.");
                    return;
                }

                if (!rangedDeduction.isNewEntity() && !rangedDeduction1.isNewEntity() && !rangedDeduction.getId().equals(rangedDeduction1.getId())) {
                    pErrors.rejectValue("amountStr", "Required.Value", "A Ranged Deduction with this name already exists.");
                    return;
                }
                //--Now check if this Name exists for a Deduction Type.
                EmpDeductionType empDeductionType = genericService.loadObjectUsingRestriction(EmpDeductionType.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("name", rangedDeduction.getName(), Operation.STRING_EQUALS)));
                if (!empDeductionType.isNewEntity()) {
                    pErrors.rejectValue("amountStr", "Required.Value", "There is an Existing Deduction Type with this name.");
                    pErrors.rejectValue("amountStr", "Required.Value", "A Deduction Type is auto-generated for a Ranged Deduction Type");
                    return;
                }

            }
        }

    }
}
