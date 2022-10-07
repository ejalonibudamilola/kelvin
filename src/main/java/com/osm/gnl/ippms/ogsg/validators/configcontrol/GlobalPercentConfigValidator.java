/*
 * Copyright (c) 2021. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfig;
import com.osm.gnl.ippms.ogsg.domain.payroll.GlobalPercentConfigDetails;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
@Component
public class GlobalPercentConfigValidator extends BaseValidator {

    @Autowired
    protected GlobalPercentConfigValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return GlobalPercentConfig.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        errors.rejectValue("name", "Required.Value", "Configuration Name is Required.");
    }

    public void validateForApproval(Object arg0, Errors pErrors, BusinessCertificate businessCertificate) {
        GlobalPercentConfig wHMB = (GlobalPercentConfig)arg0;

        if(wHMB.getApplicationMode() == 0) {
            pErrors.rejectValue("applicationMode", "Required.Value", "Percentage Application Mode is required.");
            return;
        }

        if(wHMB.getStartDate() == null) {
            pErrors.rejectValue("startDate", "Required.Value", "Start Date is required.");
            return;

        }
        if(wHMB.getEndDate() == null) {
            pErrors.rejectValue("endDate", "Required.Value", "End Date is required.");
            return;
        }
        if(wHMB.getStartDate().isAfter(wHMB.getEndDate())){
            pErrors.rejectValue("startDate", "Required.Value", "Start Date must be before End Date.");
            return;
        }
        if(wHMB.getStartDate().getDayOfMonth() != 1){
            pErrors.rejectValue("startDate", "Required.Value", "Start Date must be from the first day of the Month.");
            return;
        }
        if(wHMB.getEndDate().getDayOfMonth() < wHMB.getEndDate().lengthOfMonth()){
            pErrors.rejectValue("endDate", "Required.Value", "End Date must be an 'Actual last day of the month.");
            pErrors.rejectValue("endDate", "Required.Value", "For "+wHMB.getEndDate().getMonth().getDisplayName(TextStyle.FULL, Locale.UK)+", it should be "+wHMB.getEndDate().lengthOfMonth());
            return;
        }
        if(wHMB.isConfirmation()){
            if(String.CASE_INSENSITIVE_ORDER.compare(wHMB.getEnteredCaptcha(),wHMB.getGeneratedCaptcha()) != 0){
                pErrors.rejectValue("startDate", "Required.Value", "Entered Captcha does not match Generated Captcha.");
                wHMB.setCaptchaError(true);

            }else{
                wHMB.setCaptchaError(false);
            }
            return;
        }
    }

    public void validate(Object arg0, Errors pErrors, BusinessCertificate businessCertificate) throws IllegalAccessException, InstantiationException {

        //check if there is an unapproved global percent config

        List<GlobalPercentConfig> myList = this.genericService.loadAllObjectsWithSingleCondition(GlobalPercentConfig.class, CustomPredicate.procurePredicate("payrollStatus", IConstants.OFF), null);
        if(myList.size()>0){
            pErrors.rejectValue("", "Required.Value", "There is a Global Percent pending approval");
            return;
        }


        GlobalPercentConfig wHMB = (GlobalPercentConfig)arg0;

        if(wHMB.getApplicationMode() == 0) {
            pErrors.rejectValue("applicationMode", "Required.Value", "Percentage Application Mode is required.");
            return;
        }

        if(wHMB.getStartDate() == null) {
            pErrors.rejectValue("startDate", "Required.Value", "Start Date is required.");
            return;

        }
        if(wHMB.getEndDate() == null) {
            pErrors.rejectValue("endDate", "Required.Value", "End Date is required.");
            return;
        }
        if(wHMB.getStartDate().isAfter(wHMB.getEndDate())){
            pErrors.rejectValue("startDate", "Required.Value", "Start Date must be before End Date.");
            return;
        }
        if(wHMB.getStartDate().getDayOfMonth() != 1){
            pErrors.rejectValue("startDate", "Required.Value", "Start Date must be from the first day of the Month.");
            return;
        }
        if(wHMB.getEndDate().getDayOfMonth() < wHMB.getEndDate().lengthOfMonth()){
            pErrors.rejectValue("endDate", "Required.Value", "End Date must be an 'Actual last day of the month.");
            pErrors.rejectValue("endDate", "Required.Value", "For "+wHMB.getEndDate().getMonth().getDisplayName(TextStyle.FULL, Locale.UK)+", it should be "+wHMB.getEndDate().lengthOfMonth());
            return;
        }

        //--If we get here, check if there is another Percent Configuration that is Approved and With the Same or Overlapping dates.
        GlobalPercentConfig globalPercentConfig = IppmsUtilsExt.loadActiveGlobalPercentConfigByClient(genericService,businessCertificate);
        
        if(!globalPercentConfig.isNewEntity()){
            pErrors.rejectValue("applicationMode", "Required.Value", "There is an active 'Percentage Payment Configuration'");
            pErrors.rejectValue("applicationMode", "Required.Value", "Deactivate the existing one ('"+globalPercentConfig.getName()+"') before creating another");
            return;
        }
        globalPercentConfig = genericService.loadObjectUsingRestriction(GlobalPercentConfig.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("name",wHMB.getName(), Operation.STRING_EQUALS)));
        if(!globalPercentConfig.isNewEntity()){
            pErrors.rejectValue("applicationMode", "Required.Value", "The chosen name for this Configuration already exist for a different Configuration");
            return;
        }

        if(wHMB.getApplicationMode() == 1){
            //Pay Group Application.
            if((wHMB.getMode().equalsIgnoreCase("_add"))&&(IppmsUtils.isNullOrLessThanOne(wHMB.getSalaryTypeId()))){
                pErrors.rejectValue("applicationMode", "Required.Value", "Please select a 'Pay Group'.");
                return;
            }else{
                if((wHMB.getMode().equalsIgnoreCase("_add"))&&(wHMB.getFromLevel() == 0)){
                    pErrors.rejectValue("fromLevel", "Required.Value", "Please select a 'From Level'.");
                    return;
                }
                if((wHMB.getMode().equalsIgnoreCase("_add"))&&(wHMB.getToLevel() == 0)){
                    pErrors.rejectValue("toLevel", "Required.Value", "Please select a 'To Level'.");
                    return;
                }
                if((wHMB.getMode().equalsIgnoreCase("_add"))&&(wHMB.getFromLevel() > wHMB.getToLevel())){
                    pErrors.rejectValue("toLevel", "Required.Value", "'From Level' must be less than or equal to 'To Level'.");
                    return;
                }
                if(IppmsUtils.isNullOrEmpty(wHMB.getPercentageStr())){
                    pErrors.rejectValue("toLevel", "Required.Value", "Please enter a value for percentage to pay for this Pay Group, Level and Step.");
                    return;
                }else{
                    try{
                        double value = Double.parseDouble(PayrollHRUtils.removeCommas(wHMB.getPercentageStr()));
                        if(value > 100.00){
                            pErrors.rejectValue("toLevel", "Required.Value", "Percentage must be less than or equal to 100%.");
                            return;
                        }else if(value < 1){
                            pErrors.rejectValue("toLevel", "Required.Value", "Percentage must be defined "+(100.0D * value) +"%.");
                            return;
                        }else if(value <= 0){
                            pErrors.rejectValue("toLevel", "Required.Value", "Percentage must be greater 0.");
                            return;
                        }
                    }catch (Exception wEx){
                        pErrors.rejectValue("toLevel", "Required.Value", "Please enter a value for percentage to pay for this Pay Group, Level and Step.");
                        return;
                    }
                }

                    //Now see if this has been added before.
                    if(IppmsUtils.isNotNullOrEmpty(wHMB.getConfigDetailsList())){
                        for(GlobalPercentConfigDetails g : wHMB.getConfigDetailsList()){
                            if(g.getSalaryTypeId().equals(wHMB.getSalaryTypeId())){
                                //Now check for Same Level and step or overlapping level and step.
                                if(wHMB.getFromLevel() <= g.getFromLevel() || wHMB.getFromLevel() <= g.getToLevel() ){
                                    pErrors.rejectValue("toLevel", "Required.Value", "Overlapping 'From Level' Found. Please select different values.");
                                    return;
                                }else if(wHMB.getToLevel() <= g.getFromLevel() || wHMB.getToLevel() <= g.getToLevel()){
                                    pErrors.rejectValue("toLevel", "Required.Value", "Overlapping 'To Level' Found. Please select different values.");
                                    return;
                                }else if(wHMB.getFromLevel() == g.getFromLevel() && wHMB.getToLevel() == g.getToLevel()){
                                    pErrors.rejectValue("toLevel", "Required.Value", "Payment Configuration for this Pay Group Level & Step exists.");
                                    return;
                                }
                            }
                        }
                    }

            }


        }else{
            //--Global Value.
            if(IppmsUtils.isNullOrEmpty(wHMB.getGlobalPercentStr())){
                pErrors.rejectValue("applicationMode", "Required.Value", "Please enter a value for 'Global Value' ");
                return;
            }else{
                try{
                    Double.parseDouble(PayrollHRUtils.removeCommas(wHMB.getGlobalPercentStr()));
                }catch (Exception wEx){
                    //Eat the Exception
                    pErrors.rejectValue("applicationMode", "Required.Value", "Invalid value entered for 'Global Value'");
                    return;
                }
            }
        }

        if(wHMB.getLeastNoOfDays() == 0 && wHMB.isConfirmation()) {

                pErrors.rejectValue("applicationMode", "Required.Value", "Please select a value for 'Least Number of Days'");
                return;


        }

     }


}
