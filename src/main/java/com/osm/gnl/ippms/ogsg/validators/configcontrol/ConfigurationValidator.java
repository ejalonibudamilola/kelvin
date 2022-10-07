/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.approval.AmAliveApproval;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;

@Component
public class ConfigurationValidator extends BaseValidator {


  public ConfigurationValidator(GenericService genericService) {
        super(genericService);
    }

        @Override
        public boolean supports(Class<?> aClass) {
           return aClass.isAssignableFrom(ConfigurationBean.class);
        }

        @Override
        public void validate(Object pTarget, Errors pErrors)  {

        }
       public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate)  {
           ConfigurationBean p = (ConfigurationBean) pTarget;

            if(!businessCertificate.isPensioner()){
                if(p.getMaxSpecAllowValue() < 1 || p.getMaxSpecAllowValue() > 100){
                    pErrors.rejectValue("maxSpecAllowValue","Invalid.Value","Maximum Percentage for Special Allowance must be between 1 and 100 Percent");
                }
                if(p.getMaxDeductionValue() < 1 || p.getMaxDeductionValue() > 100){
                    pErrors.rejectValue("maxDeductionValue","Invalid.Value","Maximum Percentage for Deductions must be between 1 and 100 Percent");
                }
                if(p.getMaxLoanPercentage() < 1 || p.getMaxLoanPercentage() > 95){

                    pErrors.rejectValue("maxLoanPercentage","Invalid.Value","Maximum Percentage for ALL Loans must be between 1 and 95 Percent");
                }
                if(p.getTaxRate() < 1 || p.getTaxRate() > 30){
                    pErrors.rejectValue("taxRate","Invalid.Value","Estimated Tax Percentage Rate must be between 1 and 30 Percent");
                }
                if(p.getAgeAtRetirement() < 60){
                    pErrors.rejectValue("ageAtRetirement","Invalid.Value","Age at Retirement can not be less than 60 years");
                }
                if(p.getServiceLength() < 35){
                    pErrors.rejectValue("serviceLength","Invalid.Value","Length of Service can not be less than 35 years");
                }
            }
           if(businessCertificate.isPensioner()){
               if(p.getIamAlive() < 70 && p.isUseIAmAlive())
                   pErrors.rejectValue("iamAlive","Invalid.Value","Least value for 'I Am Alive' must be 70 years");
           }
           if(p.isUseIAmAlive()){
               //Make sure there are no pending Am Alive
               PredicateBuilder predicateBuilder = new PredicateBuilder();
               predicateBuilder.addPredicate(CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF));
               predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
               int wNoOfElem = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, AmAliveApproval.class);
               if(wNoOfElem > 0)
                   pErrors.rejectValue("iamAlive","Invalid.Value","There are "+wNoOfElem+" Am Alive Approvals pending. Please treat before turning off configuration");
           }
           /**if(IppmsUtils.isNotNull(p.getCutOffStartDate()) || IppmsUtils.isNotNull(p.getCutOffEndDate())) {
               if (p.getCutOffStartDate().isBefore(LocalDate.now())) {
                   pErrors.rejectValue("cutOffStartDate", "Invalid.Value", "Cut Off Start Date can not be less than Today");
               }
               if(IppmsUtils.isNotNull(p.getCutOffEndDate())){
                   if (p.getCutOffStartDate().isAfter(p.getCutOffEndDate()) || (p.getCutOffStartDate().isEqual(p.getCutOffEndDate()))){
                       pErrors.rejectValue("cutOffEndDate", "Invalid.Value", "Cut Off End Date can not be less than Cut Off Start Date");
                   }
               }

           }
            */
           if(IppmsUtils.isNotNullOrEmpty(p.getCutOffStartTime()) && IppmsUtils.isNullOrEmpty(p.getCutOffEndTime())){
               pErrors.rejectValue("cutOffEndTime","Invalid.Value","Login End Time can not be empty if Login Start Time is not empty");
               return;
           }
           if(IppmsUtils.isNullOrEmpty(p.getCutOffStartTime()) && IppmsUtils.isNotNullOrEmpty(p.getCutOffEndTime())){
               pErrors.rejectValue("cutOffEndTime","Invalid.Value","Login Start Time can not be empty if Login End Time is not empty");
               return;
           }
           if(IppmsUtils.isNotNullOrEmpty(p.getCutOffStartTime()) && IppmsUtils.isNotNullOrEmpty(p.getCutOffEndTime())){
               int startTime = Integer.parseInt(p.getCutOffStartTime().substring(0,p.getCutOffStartTime().indexOf(":"))
                       +p.getCutOffStartTime().substring(p.getCutOffStartTime().indexOf(":") + 1,p.getCutOffStartTime().lastIndexOf(":")));
               int endTime = Integer.parseInt(p.getCutOffStartTime().substring(0,p.getCutOffStartTime().indexOf(":"))
                       +p.getCutOffStartTime().substring(p.getCutOffStartTime().indexOf(":") + 1,p.getCutOffStartTime().lastIndexOf(":")));
               if(startTime >= endTime)
                   pErrors.rejectValue("cutOffEndTime","Invalid.Value","Login Start Time must be 'Before' Login End Time");
           }
       }

}
