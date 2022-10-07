package com.osm.gnl.ippms.ogsg.validators.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RbaConfigBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class CreateNewBankValidator extends  BaseValidator  {


  public CreateNewBankValidator(GenericService genericService) {
    super(genericService);
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return aClass.isAssignableFrom(BankInfo.class);
  }

  @SneakyThrows
  public void validate(Object pTarget, Errors pErrors)  {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Bank Name is a required field");

    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required.Value", "Description is required");

    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "sortCode", "Required.Value", "Sort Code is required");

    BankInfo p = (BankInfo) pTarget;

    if (p.getMfbInd() == 0) {
      pErrors.rejectValue("", "Reason.Invalid", "Please select the Bank Type.");
      return;
    }
    try {
      Integer.parseInt(p.getSortCode());
    } catch (Exception wEx) {
      if (p.getMfbInd() == 1) {
        pErrors.rejectValue("", "Reason.Invalid", "Sort Code must be made up of only numbers for Deposit Money Banks.");
        return;
      }
    }

    BankInfo _p;
      _p = this.genericService.loadObjectWithSingleCondition(BankInfo.class, CustomPredicate.procurePredicate("name", p.getName()));


    if (!_p.isNewEntity()) {
      if (!p.isNewEntity()) {
        if (p.getId().intValue() != _p.getId().intValue()) {
          pErrors.rejectValue("", "Reason.Invalid", "A Bank with this name already exists. Please change the 'Bank Name'");

          return;
        }
      } else {
        pErrors.rejectValue("", "Reason.Invalid", "A Bank with this name already exists. Please change the 'Bank Name'");
        return;
      }
    }

    _p = this.genericService.loadObjectWithSingleCondition(BankInfo.class, CustomPredicate.procurePredicate("sortCode", p.getSortCode()));
    if (!_p.isNewEntity())
      if (!p.isNewEntity()) {
        if (p.getId().intValue() != _p.getId().intValue()) {
          pErrors.rejectValue("", "Reason.Invalid", "Bank " + _p.getName() + " has this 'Sort Code'. Please change the 'Sort Code'.");

          return;
        }
      } else {
        pErrors.rejectValue("", "Reason.Invalid", "Bank " + _p.getName() + " has this 'Sort Code'. Please change the 'Sort Code'.");
        return;
      }
  }


  public void validateBankBranch(Object pTarget, Errors pErrors) throws InstantiationException, IllegalAccessException {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required.Value", "Bank Branch Name is a required field");


    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "branchSortCode", "Required.Value", "Branch Sort Code is required");

    BankBranch p = (BankBranch) pTarget;

    if (IppmsUtils.isNullOrLessThanOne(p.getBankInfo().getId())) {
      pErrors.rejectValue("", "Reason.Invalid", "Please Select the 'Parent Bank' for this Bank Branch");
      return;
    }
    BankInfo bankInfo = genericService.loadObjectById(BankInfo.class,p.getBankInfo().getId());
    if(!bankInfo.isMicroFinanceBank()) {
      if(!allNumeric(p.getBranchSortCode())) {
        pErrors.rejectValue("", "Reason.Invalid", "Branch Sort Code must be made up of only numbers.");
        return;
      }
    }
    BankBranch _p = this.genericService.loadObjectWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("name", p.getName()));
    if (!_p.isNewEntity()) {
      if (!p.isNewEntity()) {
        if (p.getId().intValue() != _p.getId().intValue()) {
          pErrors.rejectValue("", "Reason.Invalid", "A Bank Branch with this name already exists. Please change the 'Bank Branch Name'");

          return;
        }
      } else {
        pErrors.rejectValue("", "Reason.Invalid", "A Bank Branch with this name already exists. Please change the 'Bank Branch Name'");
        return;
      }
    }

    _p = this.genericService.loadObjectWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("branchSortCode", p.getBranchSortCode()));
    if (!_p.isNewEntity())
      if (!p.isNewEntity()) {
        if (p.getId().intValue() != _p.getId().intValue()) {
          pErrors.rejectValue("", "Reason.Invalid", "Bank Branch " + _p.getName() + " has this 'Branch Sort Code'. Please change the 'Branch Sort Code'.");

          return;
        }
      } else {
        pErrors.rejectValue("", "Reason.Invalid", "Bank Branch " + _p.getName() + " has this 'Branch Sort Code'. Please change the 'Branch Sort Code'.");
        return;
      }
  }

  public void validateRba(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) {

    RbaConfigBean p = (RbaConfigBean) pTarget;
    try {

      double wRetVal = Double.parseDouble(PayrollHRUtils.removeCommas(p.getName()));

      if (wRetVal > 10.D) {
        pErrors.rejectValue("", "Reason.Invalid", "Rba Percentage maximum value is set at 10%");
      }

    } catch (Exception wEx) {
      pErrors.rejectValue("", "Reason.Invalid", "Rba Percentage must be numbers (e.g, 2.5)");
      return;
    }

    PredicateBuilder predicateBuilder = new PredicateBuilder();
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("status", "P"));

    int NoOfPendingPayChecks = genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(businessCertificate));
    if (NoOfPendingPayChecks > 0) {
      pErrors.rejectValue("", "Reason.Invalid", "Rba Percentage cannot be altered. Pending Paychecks exists.");
    }

  }

}