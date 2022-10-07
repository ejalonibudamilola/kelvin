package com.osm.gnl.ippms.ogsg.validators.payment;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.payment.PaySchedule;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component
public class MonthlyFormValidator extends BaseValidator
{
  @Autowired
  public MonthlyFormValidator(GenericService genericService) {
    super(genericService);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return PaySchedule.class.isAssignableFrom(clazz);
  }

  public void validate(Object pTarget, Errors pErrors)
  {
    /*PaySchedule p = (PaySchedule)pTarget;
    String refType2 = p.getRefType2();
    String refType1 = p.getRefType1();
    if ((refType1 == null) && (refType2 == null))
      pErrors.rejectValue("workPeriodDays", "InvalidValue", "Please select either a Period or No. of working days. ");
    if (pErrors.getErrorCount() < 1) {
      if (refType2 != null) {
        try {
          int _refType2 = Integer.parseInt(refType2);
          if ((_refType2 == 1) && 
            (p.getWorkPeriodDays() < 1)) {
            pErrors.rejectValue("workPeriodDays", "Invalid Value", "Enter a number for 'days before the payday'");
          }
        }
        catch (Exception ex)
        {
        }
      }
      if (refType1 != null)
        try {
          int _refType2 = Integer.parseInt(refType1);
          if (_refType2 == 0) {
            if (p.getWorkPeriodInstId() <= 0) {
              pErrors.rejectValue("workPeriodInstId", "Invalid Value", "'Pay period ending on' is invalid");
            }
            if (p.getWorkPeriodEffective() <= 0)
              pErrors.rejectValue("workPeriodEffective", "Invalid Value", "Please select a value for 'of the....month'");
          }
        }
        catch (Exception ex)
        {
        }
    }*/
  }
}