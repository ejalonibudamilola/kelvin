package com.osm.gnl.ippms.ogsg.validators.contributions;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfcInfo;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class CreatePensionValidator extends BaseValidator
{
  public CreatePensionValidator(GenericService genericService) {
    super(genericService);
  }

  @SneakyThrows
  public void validate(Object pTarget, Errors pErrors){
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "PFC Name is a required field");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "address", "Required Field", "Address is a required field");

    if (pErrors.getErrorCount() < 1)
    {
      PfcInfo wPfcInfo = (PfcInfo)pTarget;

      PfcInfo wRetList = this.genericService.loadObjectWithSingleCondition(PfcInfo.class,
              CustomPredicate.procurePredicate("name",wPfcInfo.getName()));

      if (!wRetList.isNewEntity()) {
        if (wPfcInfo.isNewEntity()) {
          pErrors.rejectValue("name", "Duplicate.Value", "A PFC with the name " + wPfcInfo.getName() + " Exists. Please change name.");
          return;
        }if ((!wPfcInfo.isNewEntity()) && 
          (!wPfcInfo.getId().equals(wRetList.getId()))) {
          pErrors.rejectValue("name", "Duplicate.Value", "A PFC with the name " + wPfcInfo.getName() + " Exists. Please change name.");
          return;
        }
      }
    }
  }

  public void validateForPFA(Object pTarget, Errors pErrors) throws InstantiationException, IllegalAccessException {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "PFC Name is a required field");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "address", "Required Field", "Address is a required field");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "pfaCode", "Required Field", "PFA Code is a required field");

    if (pErrors.getErrorCount() < 1)
    {
      PfaInfo wPfaInfo = (PfaInfo)pTarget;

      if (wPfaInfo.getPfcInfo().getId().intValue() <= 0) {
        pErrors.rejectValue("name", "Invalid.Value", "Please select the PFC for this PFA.");
        return;
      }

      PfaInfo wRetList = this.genericService.loadObjectWithSingleCondition(PfaInfo.class,
              CustomPredicate.procurePredicate("name",wPfaInfo.getName()));
      if (!wRetList.isNewEntity()) {
        if (wPfaInfo.isNewEntity()) {
          pErrors.rejectValue("name", "Duplicate.Value", "A PFA with the name " + wPfaInfo.getName() + " Exists. Please change name.");
          return;
        }if ((!wPfaInfo.isNewEntity()) && 
          (!wPfaInfo.getId().equals(wRetList.getId()))) {
          pErrors.rejectValue("name", "Duplicate.Value", "A PFA with the name " + wPfaInfo.getName() + " Exists. Please change name.");
          return;
        }
      }

      PfaInfo _wRetList = this.genericService.loadObjectWithSingleCondition(PfaInfo.class,
              CustomPredicate.procurePredicate("pfaCode",wPfaInfo.getPfaCode()));
      if (!_wRetList.isNewEntity()) {
        if (wPfaInfo.isNewEntity()) {
          pErrors.rejectValue("name", "Duplicate.Value", "A PFA with the Code " + wPfaInfo.getPfaCode() + " Exists. Please change Pfa Code.");
          return;
        }if ((!wPfaInfo.isNewEntity()) && 
          (!wPfaInfo.getId().equals(_wRetList.getId()))) {
          pErrors.rejectValue("name", "Duplicate.Value", "A PFA with the Code " + wPfaInfo.getPfaCode() + " Exists. Please change Pfa Code.");
          return;
        }
      }
    }
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return false;
  }

}