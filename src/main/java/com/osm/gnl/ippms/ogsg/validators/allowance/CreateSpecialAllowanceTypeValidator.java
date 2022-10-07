package com.osm.gnl.ippms.ogsg.validators.allowance;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class CreateSpecialAllowanceTypeValidator extends BaseController
{
  @Autowired
  public CreateSpecialAllowanceTypeValidator(GenericService genericService) {
    this.genericService = genericService;
  }

  public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Special Allowance Type Name is a required field");

    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required.Value", "Description is required");

    SpecialAllowanceType p = (SpecialAllowanceType)pTarget;

    if(IppmsUtils.isNullOrLessThanOne(p.getPayTypeId())){
      pErrors.rejectValue("payTypeId", "Reason.Invalid", "Please select a value for 'Pay As'.");

      return;
    }else{
      if(!p.isNewEntity()){
        //Do for only Old Records....
        if(!p.getPayTypeId().equals(p.getOldPayTypeId())){
           if(!((SpecialAllowanceType)pTarget).isTerminationWarningIssued()){
             ((SpecialAllowanceType)pTarget).setTerminationWarningIssued(true);
             pErrors.rejectValue("payTypeId", "Reason.Invalid", "'Pay As' type change detected. All"+ businessCertificate.getStaffTypeName()+" currently enjoying this Special Allowance");
             pErrors.rejectValue("payTypeId", "Reason.Invalid", "Will automatically inherit this 'Pay As' type. Click cancel if you do not wish to update existing 'Pay As'");

           }
        }
      }
    }

    SpecialAllowanceType _p =  this.genericService.loadObjectUsingRestriction(SpecialAllowanceType.class, Arrays.asList(CustomPredicate.procurePredicate("name",p.getName()), CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())));
    
    SpecialAllowanceType _pat =  this.genericService.loadObjectUsingRestriction(SpecialAllowanceType.class,  Arrays.asList(CustomPredicate.procurePredicate("description",p.getDescription()), CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())));
    if (!_p.isNewEntity()){
      if (!p.isNewEntity()) {
        if (!p.getId().equals(_p.getId()))
        {
          pErrors.rejectValue("", "Reason.Invalid", "A Special Allowance Type with this Code already exists. Please change the 'Special Allowance Type Code'.");

          return;
        }
      }
      else
      {
        pErrors.rejectValue("", "Reason.Invalid", "A Special Allowance Type with this Description already exists. Please change the 'Special Allowance Type Description'.");
        return;
      }
    }
    if (!_pat.isNewEntity()){
        if (!p.isNewEntity()) {
          if (!p.getId().equals(_pat.getId()))
          {
            pErrors.rejectValue("", "Reason.Invalid", "A Special Allowance Type with this description already exists. Please change the 'Special Allowance Type Description'.");

            return;
          }
        }
        else
        {
          pErrors.rejectValue("", "Reason.Invalid", "A Special Allowance Type with this description already exists. Please change the 'Special Allowance Type Description'.");
          return;
        }
      }
    if(!p.isNewEntity()){
    	if(p.getOldName() != null && !p.getOldName().equalsIgnoreCase(p.getName())
    			&& p.getAllowNameEdit() == 1){
    		 pErrors.rejectValue("", "Reason.Invalid", "Special Allowance Type Name is not allowed to be changed.");
    	        return;
    	}
    }
    if (IppmsUtils.isNullOrLessThanOne(p.getPayTypes().getId())) {
      pErrors.rejectValue("", "Reason.Invalid", "Please select a value for 'Deduct As'.");
    }
    if(p.getPayTypes() == null || p.getPayTypes().isNewEntity()){
      pErrors.rejectValue("", "Reason.Invalid", "Please select a value for 'Deduct As'.");
      return;
    }
  }
  
}