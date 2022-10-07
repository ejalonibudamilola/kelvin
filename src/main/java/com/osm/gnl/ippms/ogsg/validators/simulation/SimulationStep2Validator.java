package com.osm.gnl.ippms.ogsg.validators.simulation;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class SimulationStep2Validator extends BaseValidator
{
  @Autowired
  protected SimulationStep2Validator(GenericService genericService) {
    super(genericService);
  }

  public void validate(Object pTarget, Errors pErrors )
  {
    SimulationBeanHolder wSBH = (SimulationBeanHolder)pTarget;

    if (Boolean.valueOf(wSBH.getDeductBaseYearDevLevy()).booleanValue())
    {
      if (wSBH.getBaseYearDevLevyInd() == -1) {
        pErrors.rejectValue("", "Invalid.Value", "Please 'Select Month to deduct Dev. Levy' for the Year " + wSBH.getBaseYear());
      }
    }

    if ((Boolean.valueOf(wSBH.getDeductSpillOverYearDevLevy()).booleanValue()) && 
      (wSBH.getSpillYearDevLevyInd() == -1))
      pErrors.rejectValue("", "Invalid.Value", "Please 'Select Month to deduct Dev. Levy' for the Year " + wSBH.getSpillYear());
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(SimulationBeanHolder.class);
  }

}