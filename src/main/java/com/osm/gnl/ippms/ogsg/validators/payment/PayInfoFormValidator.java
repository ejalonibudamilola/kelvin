package com.osm.gnl.ippms.ogsg.validators.payment;

import com.osm.gnl.ippms.ogsg.domain.payment.PaymentInfo;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class PayInfoFormValidator
{
  public void validate(Object pTarget, Errors pErrors)
  {
    if (pTarget.getClass().isAssignableFrom(PaymentInfo.class)) {
      PaymentInfo p = (PaymentInfo)pTarget;

      if (p.getSalaryRef().equalsIgnoreCase("1"))
      {
        if (p.getHoursWorkedPerDay() >= 24.0D) {
          pErrors.rejectValue("", "InvalidValue", "Employee Can not work 24 or more hours a day.");
        }
        if (p.getDaysWorkedPerWeek() > 7.0D)
          pErrors.rejectValue("", "InvalidValue", "Employee Can not work more than 7 days a week.");
      }
    }
  }
}