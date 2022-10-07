package com.osm.gnl.ippms.ogsg.validators.user;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;



@Component
public class CreateNewUserValidator implements Validator
{

	private final GenericService genericService;
    @Autowired
	public CreateNewUserValidator(GenericService genericService) {
        this.genericService = genericService;
    }

	@Override
	public boolean supports(Class<?> arg0) {
		return BusinessClient.class.isAssignableFrom(arg0);
	}

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "Required Field", "User Name is required");
    }

    @SneakyThrows
  public void validate(Object target, Errors pErrors, BusinessCertificate businessCertificate)
  {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "userName", "Required Field", "User Name is required");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "firstName", "Required Field", "First Name is a required field");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "lastName", "Required Field", "Last Name is a required field");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "email", "Required Field", "Email is a required field");


	BusinessClient pBc = (BusinessClient) target;
	pBc = PayrollUtils.checkStringValidity(pBc,8,true,1);
      if(pBc.isLengthError() || pBc.isSpecCharError()){
          if(pBc.isLengthError())
          pErrors.rejectValue("userName", "Invalid.Value", "User Name must be at least 8 Characters in Length");
          if(pBc.isSpecCharError())
          pErrors.rejectValue("userName", "Invalid.Value", "User Name must contain at least 1 Number with any of ['_','@', '-']");
      }
	pBc = PayrollUtils.checkStringValidity(pBc,3,false,2);
      if(pBc.isLengthError()){
          pErrors.rejectValue("lastName", "Invalid.Value", "Last Name must be at least 3 Characters in Length");
      }
      pBc = PayrollUtils.checkStringValidity(pBc,3,false,3);
      if(pBc.isLengthError()){
          pErrors.rejectValue("firstName", "Invalid.Value", "First Name must be at least 3 Characters in Length");
      }
    if (pBc.getRoleId() <= 0)
      pErrors.rejectValue("roleId", "Invalid.Value", "'New Role' is a required field.");
    if (!(pBc.isEditMode()) && !(pErrors.hasErrors()))
    {
      User wLogin = this.genericService.loadObjectWithSingleCondition(User.class, CustomPredicate.procurePredicate("username", pBc.getUserName()));

      if (!wLogin.isNewEntity())
        pErrors.rejectValue("userName", "Existing.User", "User Name already exists. Please enter a new one.");
    }
    //-Check if Email is good.
      ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class,CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
    if(configurationBean.isUseOgsEmail()) {
        if (!pBc.getEmail().endsWith("@ogunstate.gov.ng")) {
            User loggedOnUser = this.genericService.loadObjectById(User.class, businessCertificate.getLoginId());
            if (!loggedOnUser.isPrivilegedUser()) {
                pErrors.rejectValue("userName", "Existing.User", "IPPMS Users in Ogun State must use an Ogun State email address.");
                return;
            }

        }
    }
      User user = this.genericService.loadObjectWithSingleCondition(User.class, CustomPredicate.procurePredicate("email", pBc.getEmail()));
      if(!user.isNewEntity()){
          if(pBc.isNewEntity())
          pErrors.rejectValue("email", "Existing.User", "Email already exists for "+user.getActualUserName()+". Please enter a different one.");
          else if(!pBc.isNewEntity()){
              //--First check for Edit Mode.
              if(pBc.getUser() != null){
                  //Edit Mode.
                  if(!user.getId().equals(pBc.getUser().getId())){
                      pErrors.rejectValue("email", "Existing.User", "Email already exists for "+user.getActualUserName()+". Please enter a different one.");
                  }
              }

          }
      }
  }


}