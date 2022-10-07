package com.osm.gnl.ippms.ogsg.validators.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class CreateDeductionTypeValidator extends BaseValidator {
    @Autowired
    public CreateDeductionTypeValidator(GenericService genericService) {
        super(genericService);
    }

    @SneakyThrows
    public void validate(Object pTarget, Errors pErrors, BusinessCertificate bc) {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Deduction Type Name is a required field");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required.Value", "Description is required");
        if(!pErrors.hasErrors()) {
            EmpDeductionType p = (EmpDeductionType) pTarget;

            EmpDeductionType _p = this.genericService.loadObjectUsingRestriction(EmpDeductionType.class, Arrays.asList(CustomPredicate.procurePredicate("name", p.getName()),
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
            if (!_p.isNewEntity()) {
                if (!p.isNewEntity()) {
                    if (!p.getId().equals(p.getId())) {
                        pErrors.rejectValue("", "Reason.Invalid", "A Deduction Type with this name already exists. Please change the 'Deduction Type Name'.");

                        return;
                    }
                } else {
                    pErrors.rejectValue("", "Reason.Invalid", "A Deduction Type with this name already exists. Please change the 'Deduction Type Name'.");
                    return;
                }
            }
            if(p.isNewEntity()) {//Only Validate for Creation
                if (IppmsUtils.isNullOrLessThanOne(p.getEmpDeductCatRef())) {
                    pErrors.rejectValue("", "Reason.Invalid", "Please select a value for 'Deduction Category*'.");
                    return;
                }
            }else{
                //Check if this is Restricted Edit...
                if(p.isEditRestricted() && !bc.isSuperAdmin()){
                    pErrors.rejectValue("", "Reason.Invalid", "This Deduction Type is 'Edit Restricted' only Super Administrators can edit it.");
                    return;
                }

            }
            if (p.getEmpDeductionCategory().isApportionedDeduction()) {
                boolean firstSet;
                boolean secondSet;
                boolean thirdSet = false;
                double firstAllotAmt = 0;
                double secAllotAmt = 0;
                double thirdAllotAmt = 0;
                if (IppmsUtils.isNullOrEmpty(p.getFirstAllotment())) {
                    pErrors.rejectValue("", "Reason.Invalid", "'Allotment 1' is a required field for Apportioned Deductions.");
                    return;
                } else if (IppmsUtils.isNullOrEmpty(p.getSecAllotment())) {
                    pErrors.rejectValue("", "Reason.Invalid", "'Allotment 2' is a required field for Apportioned Deductions.");
                    return;
                } else {

                    try {
                        firstAllotAmt = Double.valueOf(p.getFirstAllotAmt());
                        firstSet = true;
                    } catch (Exception wEx) {
                        pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Invalid amount set for 'Allotment 1'.");
                        return;
                    }
                    if (firstAllotAmt >= 100) {
                        pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Invalid amount set for 'Allotment 1'. Apportionment are in percentages.");
                        return;
                    }
                    try {
                        secAllotAmt = Double.valueOf(p.getSecAllotAmt());
                        secondSet = true;
                    } catch (Exception wEx) {
                        pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Invalid amount set for 'Allotment 2'.");
                        return;
                    }
                    if (secAllotAmt >= 100) {
                        pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Invalid amount set for 'Allotment 2'. Apportionment are in percentages.");
                        return;
                    }
                    if (IppmsUtils.isNotNullOrEmpty(p.getThirdAllotment())) {

                        try {
                            thirdAllotAmt = Double.valueOf(p.getThirdAllotAmt());

                        } catch (Exception wEx) {
                            pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Invalid amount set for 'Allotment 3'.");
                            return;
                        }
                        if (thirdAllotAmt >= 100) {
                            pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Invalid amount set for 'Allotment 3'. Apportionment are in percentages.");
                            return;
                        }
                        thirdSet = true;
                    }
                }

                if (!secondSet) {
                    pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Allotments must be contiguous. Allotment 2 can not be skipped.");
                    return;
                } else if (!firstSet) {
                    pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Allotments must be contiguous. Allotment 1 can not be skipped.");
                    return;
                } else if (!firstSet && !secondSet && thirdSet) {
                    pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Allotments must be contiguous. Allotment 1 and 2 can not be skipped.");
                    return;
                } else if (!firstSet && secondSet && (thirdSet || !thirdSet)) {
                    pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Allotments must be contiguous. Allotment 1 can not be skipped.");
                    return;
                }
                //Now if we get here, make sure the values are not more than
                if (firstAllotAmt + secAllotAmt + thirdAllotAmt != 100) {
                    pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Allotments must total 100%");
                    return;
                }
                if (firstSet) {
                    if (secondSet)
                        if (p.getFirstAllotment().equalsIgnoreCase(p.getSecAllotment())) {
                            pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Allotment 1 is the same as Allotment 2.");
                            return;
                        }
                    if (thirdSet) {
                        if (p.getFirstAllotment().equalsIgnoreCase(p.getThirdAllotment())) {
                            pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Allotment 1 is the same as Allotment 2.");
                            return;
                        }
                    }
                }
                if (secondSet) {
                    if (thirdSet) {
                        if (p.getSecAllotment().equalsIgnoreCase(p.getThirdAllotment())) {
                            pErrors.rejectValue("firstAllotAmt", "Reason.Invalid", "Allotment 2 is the same as Allotment 3.");
                            return;
                        }
                    }
                }
                //if we get here...set the Values...
                if (firstSet)
                    ((EmpDeductionType) pTarget).setFirstAllotAmt(firstAllotAmt);
                if (secondSet)
                    ((EmpDeductionType) pTarget).setSecAllotAmt(secAllotAmt);
                if (thirdSet)
                    ((EmpDeductionType) pTarget).setThirdAllotAmt(thirdAllotAmt);

            } else {
                if (p.getEmpDeductionCategory().isStatutoryDeduction() || p.getEmpDeductionCategory().isApportionedDeduction()) {
                    if (IppmsUtils.isNullOrLessThanOne(p.getEmpDeductPayTypeRef())) {
                        pErrors.rejectValue("empDeductPayTypeRef", "Required Field", "Please select a 'Deduct as' Type");
                        return;
                    } else {


                        PayTypes payTypes = this.genericService.loadObjectById(PayTypes.class, p.getEmpDeductPayTypeRef());
                        if (payTypes.isUsingPercentage()) {

                            ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class, CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
                            if (p.getAmount() > configurationBean.getMaxDeductionValue()) {
                                pErrors.rejectValue("amountStr", "Required Field", "Deduction Value must be " + PayrollHRUtils.getDecimalFormat().format(configurationBean.getMaxDeductionValue()) + " or less.");
                                return;
                            }
                        }
                    }
                    if (IppmsUtils.isNullOrLessThanOne(p.getBankInstId())) {
                        pErrors.rejectValue("bankInstId", "Required Field", "Please select a 'Bank'");
                    } else {


                        BankInfo b = this.genericService.loadObjectById(BankInfo.class, p.getBankInstId());

                        if (!b.isDefaultBank()) {
                            if (IppmsUtils.isNullOrLessThanOne(p.getBranchInstId())) {
                                pErrors.rejectValue("accountNumber", "Required Field", "Please select the Bank Branch");
                                return;
                            }

                            if ((p.getAccountNumber() == null) || (p.getAccountNumber().trim() == "")) {
                                pErrors.rejectValue("accountNumber", "Required Field", "Please enter a value for Account Number");
                            } else {/*
            char[] wChar = p.getAccountNumber().toCharArray();
            for (char c : wChar) {
              if (!Character.isDigit(c)) {
                pErrors.rejectValue("accountNumber", "Required Field", "Account Number should be all numeric");
                break;
              }

            }

            if (p.getAccountNumber().length() != 10) {
              pErrors.rejectValue("accountNumber", "Required Field", "Account Number should be 10 digits long");
            }
           */
                                if ((p.getConfirmAccountNumber() == null) || (p.getConfirmAccountNumber().trim() == "")) {
                                    pErrors.rejectValue("accountNumber", "Required Field", "Please enter a number for 'Confirm Account Number'");
                                } else if (!p.getConfirmAccountNumber().equalsIgnoreCase(p.getAccountNumber())) {
                                    pErrors.rejectValue("accountNumber", "Required Field", "Account Number does not match Confirm Account Number");
                                }

                            }

                            PaymentMethodInfo pInfo = this.genericService.loadObjectUsingRestriction(PaymentMethodInfo.class, Arrays.asList(CustomPredicate.procurePredicate(
                                    "bankBranches.id", p.getBranchInstId()), CustomPredicate.procurePredicate("accountNumber", p.getAccountNumber())));
                            if (!pInfo.isNewEntity()) {

                                pErrors.rejectValue("accountNumber", "Duplicate", bc.getStaffTypeName() + " " + pInfo.getParentObject().getDisplayNameWivTitlePrefixed() + " has the same Account number.");
                                pErrors.rejectValue("", "Duplicate", "Deduction Accounts can not belong to " + bc.getStaffTypeName() + "s");


                            }

                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(EmpDeductionType.class) || aClass.isAssignableFrom(SalaryType.class);
    }

    public void validateForPayGroup(Object pTarget, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Pay Group Type Name is a required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required.Value", "Pay Group Type Description is a required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "payGroupCode", "Required.Value", "Pay Group Code  is a required field");
        SalaryType p = (SalaryType) pTarget;

        if(StringUtils.contains(p.getPayGroupCode()," ")){
            pErrors.rejectValue("", "Reason.Invalid",
                    "Pay Group Code can not contain empty spaces");
            return;
        }


        SalaryType _p = this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(CustomPredicate.procurePredicate("name", p.getName()),
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));

        if (!_p.isNewEntity()) {
            if (!p.isNewEntity()) {
                if (!p.getId().equals(p.getId())) {
                    pErrors.rejectValue("", "Reason.Invalid", "A Pay Group Type with this name already exists. Please change the 'Pay Group Type Name'.");

                    return;
                }
            } else {
                pErrors.rejectValue("", "Reason.Invalid", "A Pay Group Type with this name already exists. Please change the 'Pay Group Type Name'.");
                return;
            }
        }

        //Validate for Pay Group Code with Business Client.....
        _p = this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(CustomPredicate.procurePredicate("payGroupCode", p.getPayGroupCode()),
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
        if (!_p.isNewEntity()) {
            if (!p.isNewEntity()) {
                if (!p.getId().equals(p.getId())) {
                    pErrors.rejectValue("", "Reason.Invalid", "A Pay Group Type with this 'Pay Group Code' already exists. Please change the 'Pay Group Code'.");

                    return;
                }
            } else {
                pErrors.rejectValue("", "Reason.Invalid", "A Pay Group Type with this 'Pay Group Code' already exists. Please change the 'Pay Group Code'.");
                return;
            }
        }
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Required.Value", "Deduction Type Name is a required field");

    }


}