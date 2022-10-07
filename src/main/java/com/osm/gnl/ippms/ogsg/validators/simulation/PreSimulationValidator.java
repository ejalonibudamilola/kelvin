package com.osm.gnl.ippms.ogsg.validators.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationMasterBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class PreSimulationValidator extends BaseValidator
{
  @Autowired
  protected PreSimulationValidator(GenericService genericService) {
    super(genericService);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return false;
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Required Field", "Payroll Simulation Name is required");
  }

  public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws IllegalAccessException, InstantiationException {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Payroll Simulation Name is required");

    SimulationBeanHolder pSBH = (SimulationBeanHolder)pTarget;

    char[] wChar = pSBH.getName().trim().toCharArray();

    for (char c : wChar) {
      if ((Character.isLetterOrDigit(c)) || 
        (Character.isWhitespace(c))) continue;
      pErrors.rejectValue("", "Invalid.Value", "Only Alphabets (A[a]-Z[z]) with/without Numbers (0-9) allowed for in a 'Simulation Name'");
      return;
    }

    if (pSBH.getStartMonthInd() < 0) {
      pErrors.rejectValue("", "Invalid.Value", "'Simulation Start Month' is a required field.");
    }

    if (pSBH.getNoOfMonthsInd() < 0) {
      pErrors.rejectValue("", "Invalid.Value", "Please select a value for 'Simulate for'");
    }

    if (pErrors.getErrorCount() < 1)
    {
      PayrollSimulationMasterBean payrollSimulationMasterBean = genericService.loadObjectUsingRestriction(PayrollSimulationMasterBean.class,
              Arrays.asList(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                      CustomPredicate.procurePredicate("name",pSBH.getName())));
      if (!payrollSimulationMasterBean.isNewEntity() && !pSBH.isEditMode())
        pErrors.rejectValue("", "Invalid.Value", "Another Payroll Simulation with this same name exists. Please change the 'Payroll Simulation Name'");
    }
  }
}