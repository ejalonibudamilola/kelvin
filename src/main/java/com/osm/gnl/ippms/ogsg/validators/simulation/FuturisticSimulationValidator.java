package com.osm.gnl.ippms.ogsg.validators.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckMaster;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Component
public class FuturisticSimulationValidator extends BaseValidator
{

  @Autowired
  protected FuturisticSimulationValidator(GenericService genericService) {
    super(genericService);
  }

  public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException
  {
    ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Invalid.Name", "Futuristic Payroll Simulation Name is required.");

    FuturePaycheckMaster f = (FuturePaycheckMaster)pTarget;

    if (f.getCreatedDate() == null) {
      pErrors.rejectValue("", "Invalid.Value", "'Payroll Period' is a required field.");
      return;
    }
    PayrollFlag payrollFlag = IppmsUtilsExt.getPayrollFlagForClient(genericService,businessCertificate);
    LocalDate localDate = LocalDate.of(payrollFlag.getApprovedYearInd(),payrollFlag.getApprovedMonthInd(),1);
    if (localDate.getYear() == f.getCreatedDate().getYear() && localDate.getMonthValue() == f.getCreatedDate().getMonthValue()) {
      pErrors.rejectValue("", "Invalid.Value", "Futuristic Simulation can not be done for an Approved Payroll Run Period.");
      return;
    }

    if (pErrors.getErrorCount() < 1)
    {
      List<CustomPredicate> predicateList = new ArrayList<>();
      predicateList.add(CustomPredicate.procurePredicate("name", f.getName()));
      predicateList.add(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
      FuturePaycheckMaster _f = genericService.loadObjectUsingRestriction(FuturePaycheckMaster.class,predicateList);
      if (!_f.isNewEntity()) {
        pErrors.rejectValue("", "Invalid.Value", "A Futuristic Simulation with this name exists please change.");
        return;
      }

      predicateList.add(CustomPredicate.procurePredicate("simulationMonth", f.getCreatedDate().getMonthValue()));
      predicateList.add(CustomPredicate.procurePredicate("simulationYear", f.getCreatedDate().getYear()));
      _f = genericService.loadObjectUsingRestriction(FuturePaycheckMaster.class,predicateList);
      if (!_f.isNewEntity())
        pErrors.rejectValue("", "Invalid.Value", "A Futuristic Simulation for " + PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(f.getCreatedDate().getMonthValue(), f.getCreatedDate().getYear()) + " exists please change.");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(FuturePaycheckMaster.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    errors.rejectValue("", "Invalid.Value", "'Payroll Period' is a required field.");
  }
}