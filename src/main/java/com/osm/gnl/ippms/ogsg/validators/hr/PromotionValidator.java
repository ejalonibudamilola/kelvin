package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.TransferService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.promotion.FlaggedPromotions;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class PromotionValidator extends BaseValidator {
    @Autowired
    protected PromotionValidator(GenericService genericService) {
        super(genericService);
    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate, TransferService transferService) throws InstantiationException, IllegalAccessException {
        HrMiniBean wEHB = (HrMiniBean) pTarget;

        if (wEHB.getTerminateReasonId() == 0) {
            pErrors.rejectValue("terminateReasonId", "Required.Value", "Please select the New Salary Level and Step.");
            return;
        }
        //--New Requirement
        //Mustola 21-02-2022
        //PayGroup Allowance Rules
        if(transferService.getTotalNoOfActiveAllowanceApprovals(businessCertificate,wEHB.getId(), null,null) > 0){
            pErrors.rejectValue("terminateReasonId", "Required.Value", businessCertificate.getStaffTypeName()+" "+wEHB.getName()+" has pending Pay Group Allowance Rule.");
            pErrors.rejectValue("terminateReasonId", "Required.Value", "This PayGroup Allowance Rule must be Rejected or Deleted for Promotion to be allowed");
            return;
        }
        AllowanceRuleMaster allowanceRuleMaster = genericService.loadObjectUsingRestriction(AllowanceRuleMaster.class, Arrays.asList(CustomPredicate.procurePredicate("hiringInfo.id",wEHB.getId()),
                CustomPredicate.procurePredicate("activeInd", IConstants.ON)));
        if(!allowanceRuleMaster.isNewEntity()){
            pErrors.rejectValue("terminateReasonId", "Required.Value", businessCertificate.getStaffTypeName()+" "+wEHB.getName()+" has an Active Pay Group Allowance Rule.");
            pErrors.rejectValue("terminateReasonId", "Required.Value", "This PayGroup Allowance Rule must be Deleted for Promotion to be allowed");
            return;
        }

        //--Check if this is a Step Increment type of Promotion. Strictly for SUBEB & LG...for now....
        //if(!businessCertificate.isCivilService())
        if(IppmsUtilsExt.isStepIncrementTypePromotion(wEHB.getOldSalaryInfoInstId(), wEHB.getTerminateReasonId(), genericService)){
            pErrors.rejectValue("terminateReasonId", "Required.Value", "This is a Step Increment. Step Increments are not allowed in Promotion Module.");
            pErrors.rejectValue("terminateReasonId", "Required.Value", "Please Use the Step Increment Module for Step Increments.");
            return;
        }
        //--New Requirement
        //Mustola 28-Apr-2021
        FlaggedPromotions flaggedPromotions = genericService.loadObjectUsingRestriction(FlaggedPromotions.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("employee.id", wEHB.getEmployeeInstId()), CustomPredicate.procurePredicate("statusInd",0)));

        if(!flaggedPromotions.isNewEntity()){
            pErrors.rejectValue("terminateReasonId", "Required.Value", "There is a pending Flagged Promotion for this "+businessCertificate.getStaffTypeName()+". Please treat Pending Flagged Promotion.");
            return;
        }
        if ((wEHB.getPayArrearsInd() != null) &&
                (Boolean.valueOf(wEHB.getPayArrearsInd()).booleanValue())) {

            Object wRetVal = PayrollHRUtils.getNumberFromString(wEHB.getAmountStr());

            if (wRetVal == null) {
                pErrors.rejectValue("amountStr", "Required.Value", "Arrears amount '" + wEHB.getAmountStr() + "' is not a valid Monetary Value");
                return;
            }



             List<AbstractSpecialAllowanceEntity> wSAI = (List<AbstractSpecialAllowanceEntity>)genericService.loadAllObjectsWithSingleCondition(IppmsUtils.getSpecialAllowanceInfoClass(businessCertificate), CustomPredicate.procurePredicate("employee.id", wEHB.getEmployeeInstId()), null);

            for (AbstractSpecialAllowanceEntity s : wSAI) {

                if (s.getSpecialAllowanceType().isArrearsType()) {

                    if (!s.isExpired() && s.getAmount() > 0) {
                        pErrors.rejectValue("", "Required.Value", businessCertificate.getStaffTypeName()+" currently has an active Salary Arrears. Amount : " + s.getAmount() + " | Dates : " + PayrollHRUtils.getDateFormat().format(s.getStartDate()) + " - " + PayrollHRUtils.getDateFormat().format(s.getEndDate()));
                        pErrors.rejectValue("", "Required.Value", s.getName() + " cannot be added. Please use the Special Allowance Form to edit existing '" + s.getName() + "'");
                        return;
                    }
                }

            }

        }

        if (wEHB.isWarningIssued()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "refNumber", "Required Field", "Reference Number is required");
            if (wEHB.getRefDate() == null) {
                pErrors.rejectValue("refDate", "Required.Value", "Please select the Date this promotion was approved.");
                return;
            }

            if (wEHB.getRefDate().isAfter(LocalDate.now())) {
                pErrors.rejectValue("refDate", "Required.Value", "Promotion Date must be ON or BEFORE today.");
                return;
            }
        }
    }
    public void validateForStepIncrement(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {
        HrMiniBean wEHB = (HrMiniBean) pTarget;
       /* if(transferService.getTotalNoOfActiveAllowanceApprovals(businessCertificate,wEHB.getId(), null,null) > 0){
            pErrors.rejectValue("terminateReasonId", "Required.Value", businessCertificate.getStaffTypeName()+" "+wEHB.getName()+" has pending Pay Group Allowance Rule.");
            pErrors.rejectValue("terminateReasonId", "Required.Value", "This PayGroup Allowance Rule must be Rejected or Deleted for Step Increment to be allowed");
            return;
        }*/
        AllowanceRuleMaster allowanceRuleMaster = genericService.loadObjectUsingRestriction(AllowanceRuleMaster.class, Arrays.asList(CustomPredicate.procurePredicate("hiringInfo.id",wEHB.getId()),
                CustomPredicate.procurePredicate("activeInd", IConstants.ON)));
        if(!allowanceRuleMaster.isNewEntity()){
            pErrors.rejectValue("terminateReasonId", "Required.Value", businessCertificate.getStaffTypeName()+" "+wEHB.getName()+" has an Active Pay Group Allowance Rule.");
            pErrors.rejectValue("terminateReasonId", "Required.Value", "This PayGroup Allowance Rule must be Deleted for Step Increment to be allowed");
            return;
        }
        if (wEHB.isWarningIssued()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "refNumber", "Required Field", "Reference Number is required");
            if (wEHB.getRefDate() == null) {
                pErrors.rejectValue("refDate", "Required.Value", "Please select the Date this Step Increment was approved.");
                return;
            }

            if (wEHB.getRefDate().isAfter(LocalDate.now())) {
                pErrors.rejectValue("refDate", "Required.Value", "Step Increment Date must be ON or BEFORE today.");
                return;
            }
        }
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(HrMiniBean.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}