package com.osm.gnl.ippms.ogsg.validators.subvention;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.time.LocalDate;
import java.util.List;


@Component
public class SubventionValidator extends BaseValidator
{

  @Autowired
  public SubventionValidator(GenericService genericService) {
    super(genericService);
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return Subvention.class.isAssignableFrom(aClass);
  }

  @Override
  public void validate(Object pTarget, Errors pErrors) {
    {
      ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Subvention Name is a required field");
      ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "amountStr", "Required Field", "Monthly Amount is a required field");

      Subvention wS = (Subvention)pTarget;

      if (wS.getExpirationDate() != null) {

        LocalDate wToday = LocalDate.now();

        if (wS.getExpirationDate().isBefore(wToday)) {
          pErrors.rejectValue("expirationDate", "Invalid Value", "Expiration Date should be in the future");
          return;
        }if ((wS.getExpirationDate().getYear() == wToday.getYear()) && (wS.getExpirationDate().getMonthValue() == wToday.getMonthValue())) {
          pErrors.rejectValue("expirationDate", "Invalid Value", "Expiration Date can not be in the current month");
          return;
        }
      }
      String amountStr = PayrollHRUtils.removeCommas(wS.getAmountStr());
      try {
        Double.parseDouble(amountStr);
      } catch (Exception ex) {
        pErrors.rejectValue("amountStr", "Invalid Value", "Amount should be numeric!");
        return;
      }
      List<Subvention> wExisting = (this.genericService.loadControlEntity(Subvention.class));

      for (Subvention s : wExisting)
        if (s.getName().equalsIgnoreCase(wS.getName())) {
          if (wS.isEditMode()) {
            if (!s.getId().equals(wS.getId()))
              pErrors.rejectValue("name", "Invalid Value", "This Subvention already exists! It may be expired. Please use a different name");
            return;
          }
          pErrors.rejectValue("name", "Invalid Value", "This Subvention already exists! It may be expired. Please use a different name");
          return;
        }
    }
  }


}