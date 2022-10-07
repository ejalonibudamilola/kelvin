package com.osm.gnl.ippms.ogsg.validators.contract;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;


@Component
public class CreateEmployeeContractValidator extends BaseValidator
{
  @Autowired
  protected CreateEmployeeContractValidator(GenericService genericService) {
    super(genericService);
  }

  public void validate(Object pTarget, Errors pErrors, BusinessCertificate bc)
    throws Exception
  {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", "Contract Name is a required field");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "contractStartDate", "Required Field", "Contract Start Date is a required field");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "contractEndDate", "Required Field", "Contract End Date is a required field");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "referenceNumber", "Required Field", "Reference Number is a required field");
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "referenceDate", "Required Field", "Reference Date is a required field");

    if (pErrors.getErrorCount() < 1)
    {
      ContractHistory h = (ContractHistory)pTarget;

      if (h.getReferenceDate().isAfter(LocalDate.now())) {
        pErrors.rejectValue("referenceDate", "Invalid.Value", "Reference Date must be either today or in the past.");
      }
      long monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(h.getContractStartDate()), YearMonth.from(LocalDate.now()));
      if (monthsBetween > 6) {
        pErrors.rejectValue("contractStartDate", "Invalid.Value", "Contract Start Date must be a maximum of Six (6) Months ago.");
      }
      if(h.getContractStartDate().isAfter(LocalDate.now())){
        pErrors.rejectValue("contractStartDate", "Invalid.Value", "Contract Start Date must be before today");

      }
      if ((h.getContractStartDate().isAfter(h.getContractEndDate())) || (h.getContractStartDate().equals(h.getContractEndDate()))) {
        pErrors.rejectValue("contractEndDate", "Invalid.Value", "Contract Start Date must be before Contract End Date.");
      }


      if (h.getHiringInfo().getEmployee().getEmployeeType().isRenewable())
      {
        if (h.getContractEndDate().getYear() - h.getContractStartDate().getYear() > 2) {
          pErrors.rejectValue("contractEndDate", "Invalid.Value", "Contract length must not be greater than 2 years.");
        }
        if (h.getContractEndDate().getMonthValue() - h.getContractStartDate().getMonthValue() == 2)
        {
          int noOfDays = Period.between(h.getContractStartDate(),h.getContractEndDate()).getDays();
          if (noOfDays > 742) {
            pErrors.rejectValue("contractEndDate", "Invalid.Value", "Contract length must not be greater than 2 years.");
          }
        }
      }
      else
      {
        if (h.getContractEndDate().getYear() - h.getContractStartDate().getYear() > 1) {
          pErrors.rejectValue("contractEndDate", "Invalid.Value", "Internships are for 1 year.");
        }
        if (h.getContractEndDate().getYear() - h.getContractStartDate().getYear() == 1)
        {
          int noOfDays = Period.between(h.getContractStartDate(),h.getContractEndDate()).getDays();
          if (noOfDays > 375) {
            pErrors.rejectValue("contractEndDate", "Invalid.Value", "Internship period is more than 365 days and 10 days of grace.");
          }
        }
      }
      if (pErrors.getErrorCount() < 1)
      {
        if (IppmsUtilsExt.employeeHasExistingContract(genericService,bc,h.getHiringInfo().getParentId()))
          pErrors.rejectValue("", "Invalid.Value", h.getHiringInfo().getEmployee().getDisplayNameWivTitlePrefixed()+ " has an existing Contract. Multiple Contracts Not Allowed.");
      }
    }
  }


  @Override
  public boolean supports(Class<?> clazz) {
    return false;
  }

  @Override
  public void validate(Object target, Errors errors) {

  }
}