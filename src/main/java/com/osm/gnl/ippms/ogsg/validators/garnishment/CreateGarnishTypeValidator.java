package com.osm.gnl.ippms.ogsg.validators.garnishment;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class CreateGarnishTypeValidator extends BaseValidator
{
  @Autowired
  public CreateGarnishTypeValidator(GenericService genericService) {
    super(genericService);
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return false;
  }

  @SneakyThrows
  public void validate(Object pTarget, Errors pErrors)
  {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Garnishment Type Name is a required field");

    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required.Value", "Description is required");

    EmpGarnishmentType p = (EmpGarnishmentType)pTarget;

    EmpGarnishmentType _p =  this.genericService.loadObjectWithSingleCondition(EmpGarnishmentType.class, CustomPredicate.procurePredicate("name",p.getName()));
    if (!_p.isNewEntity()) {
      if (!p.isNewEntity()) {
        if (!p.getId().equals(p.getId()))
        {
          pErrors.rejectValue("", "Reason.Invalid", "A Garnishment Type with this name already exists. Please change the 'Garnishment Type Name'.");

          return;
        }
      }
      else
      {
        pErrors.rejectValue("", "Reason.Invalid", "A Garnishment Type with this name already exists. Please change the 'Garnishment Type Name'.");
        return;
      }

    }

    if (IppmsUtils.isNullOrLessThanOne(p.getBankInstId())) {
      pErrors.rejectValue("bankInstId", "Required Field", "Please select a 'Bank'");
    }
    else {
      BankInfo b = this.genericService.loadObjectById(BankInfo.class,  p.getBankInstId() );

      if (!b.isDefaultBank())
      {
        if (IppmsUtils.isNullOrLessThanOne(p.getBranchInstId())) {
          pErrors.rejectValue("accountNumber", "Required Field", "Please select the Bank Branch");
          return;
        }

        if (IppmsUtils.isNullOrEmpty(p.getAccountNumber())) {
          pErrors.rejectValue("accountNumber", "Required Field", "Please enter a value for Account Number");
        }
        else {
          /*char[] wChar = p.getAccountNumber().toCharArray();
          for (char c : wChar) {
            if (!Character.isDigit(c)) {
              pErrors.rejectValue("accountNumber", "Required Field", "Account Number should be all numeric");
              break;
            }

          }

          if (p.getAccountNumber().length() != 10) {
            pErrors.rejectValue("accountNumber", "Required Field", "Account Number should be 10 digits long");
          }*/

          if ((p.getConfirmAccountNumber() == null) || (p.getConfirmAccountNumber().trim() == "")) {
            pErrors.rejectValue("accountNumber", "Required Field", "Please enter a number for 'Confirm Account Number'");
          }
          else if (!p.getConfirmAccountNumber().equalsIgnoreCase(p.getAccountNumber())) {
            pErrors.rejectValue("accountNumber", "Required Field", "Account Number does not match Confirm Account Number");
          }

        }

        PaymentMethodInfo pInfo = this.genericService.loadObjectUsingRestriction(PaymentMethodInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("bankBranches.id", p.getBranchInstId()),
                CustomPredicate.procurePredicate( "accountNumber", p.getAccountNumber())));
        if (!pInfo.isNewEntity()) {
           
          pErrors.rejectValue("accountNumber", "Duplicate", "Employee " + pInfo.getEmployee().getDisplayNameWivTitlePrefixed() + " has the same Account number in the same Bank!");
          pErrors.rejectValue("", "Duplicate", "Deduction Accounts can not belong to a Civil Servant.");
        }
      }
    }
  }
}