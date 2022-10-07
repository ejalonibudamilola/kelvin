package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;

@Component
public class ReinstatementValidator extends BaseValidator
{
  @Autowired
  protected ReinstatementValidator(GenericService genericService) {
    super(genericService);
  }

  public void validate(Object pTarget, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
    HrMiniBean pEHB = (HrMiniBean)pTarget;

    if (IppmsUtils.isNullOrLessThanOne(pEHB.getSalaryInfoInstId())) {
      pErrors.rejectValue("", "Reason.Invalid", "Please select a value for Reinstatement Level and Step");
    }

    if (StringUtils.trimToEmpty(pEHB.getPayArrearsInd()).equals("0")) {
      ((HrMiniBean)pTarget).setPayArrearsInd("0");
    } else {
      int myInt = Integer.parseInt(pEHB.getPayArrearsInd());
      if (myInt == 2) {
        try {
          double myDouble = Double.parseDouble(parseIt(pEHB.getArrearsPercentageStr()));
          if (myDouble > 100.0D) {
            pErrors.rejectValue("", "Reason.Invalid", "Percentage of Arrears to pay cannot be more than 100.0%");
          }
          else if (myDouble <= 0.0D)
            pErrors.rejectValue("", "Reason.Invalid", "Percentage of Arrears must be greater than 0 (Zero)");
        }
        catch (Exception wEx)
        {
          pErrors.rejectValue("", "Reason.Invalid", "Please enter a valid value for Percentage of Arrears.");
        }
      }

      if (pEHB.getArrearsStartDate() == null) {
        pErrors.rejectValue("", "Reason.Invalid", "'Arrears Start Date' cannot be empty");
        return;
      }
      if (pEHB.getArrearsEndDate() == null) {
        pErrors.rejectValue("", "Reason.Invalid", "'Arrears End Date' cannot be empty");
        return;
      }

      LocalDate wToday = LocalDate.now();

      if ((!pEHB.getArrearsStartDate().isBefore(wToday)) || (!pEHB.getArrearsEndDate().isBefore(wToday))) {
        pErrors.rejectValue("", "Reason.Invalid", "'Arrears dates' must be in the past.");
      }

      if (pEHB.getArrearsStartDate().isAfter(pEHB.getArrearsEndDate())) {
        pErrors.rejectValue("", "Reason.Invalid", "'Arrears Start Date' must be before 'Arrears End Date'.");
      }

      AbstractPaycheckEntity wEmpPayBean = (AbstractPaycheckEntity) genericService.loadObjectById(IppmsUtils.getPaycheckClass(bc),
              IppmsUtilsExt.maxPaycheckIdByEmployee(genericService,bc,pEHB.getEmployeeInstId()));

      if (!wEmpPayBean.isNewEntity()) {

        if ((pEHB.getArrearsStartDate().isBefore(wEmpPayBean.getPayDate())) || (pEHB.getArrearsEndDate().isBefore(wEmpPayBean.getPayDate()))) {
          pErrors.rejectValue("", "Reason.Invalid", "'Arrears dates' must be after " + pEHB.getName() + "'s last pay month.");
        }

      }

    }

    if ((pEHB.getShowForConfirm() != null) && (pEHB.getShowForConfirm().equalsIgnoreCase(IConstants.SHOW_ROW)))
    {
      if (StringUtils.trimToEmpty(pEHB.getRefNumber()).equals("")) {
        pErrors.rejectValue("", "Reason.Invalid", "Please Enter the reference number on the 'Reinstatement' Letter");
      }

      if (pEHB.getRefDate() == null) {
        pErrors.rejectValue("", "Reason.Invalid", "Reference Date cannot be empty");
        return;
      }

      if (pEHB.getRefDate().isAfter(LocalDate.now()))
        pErrors.rejectValue("", "Reason.Invalid", "Reference Date must be on or before today's date.");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(HrMiniBean.class);
  }

  @Override
  public void validate(Object target, Errors errors) {

  }
}