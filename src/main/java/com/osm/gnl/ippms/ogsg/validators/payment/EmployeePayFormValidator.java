package com.osm.gnl.ippms.ogsg.validators.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRerun;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Arrays;


@Component
public class EmployeePayFormValidator extends BaseValidator
{
  //private LocalDate payDate;

  @Autowired
  protected EmployeePayFormValidator(GenericService genericService) {
    super(genericService);
  }

  public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {

    PayrollRunMasterBean wPRB = IppmsUtilsExt.loadCurrentlyRunningPayroll(genericService, businessCertificate);
    if (!wPRB.isNewEntity() && wPRB.isRunning()) {
      pErrors.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName() + ".");
      return;
     }
    //Make Sure a Pending Rerun Deleted Paychecks do not exist
    PayPeriodDaysMiniBean bean = (PayPeriodDaysMiniBean)pTarget;
    if(genericService.isObjectExisting(PayrollRerun.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId",businessCertificate.getBusinessClientInstId())))
        && !bean.isReRun()){

      pErrors.rejectValue("", "No.Employees", "Deleted Paychecks Scheduled for Rerun exists. New Paychecks can not be created so long as those Paychecks exists.");
      return;
    }


    if (bean.getPayDate() == null) {
      pErrors.rejectValue("payDate", "Required", "Pay Date is a required field!");
      return;
    }

    if (pErrors.getErrorCount() < 1)
    {
      if (bean.getPayDate().getYear() != bean.getCurrentPayPeriodEnd().getYear())
        pErrors.rejectValue("payDate", "Invalid.Value", "Pay Date must be same year as Pay Period.");
      else if ((bean.getPayDate().isBefore(bean.getCurrentPayPeriodStart())) || (bean.getPayDate().isAfter(bean.getCurrentPayPeriodEnd())))
      {
        pErrors.rejectValue("payDate", "Invalid.Value", "Pay Date must be within the same month as the the Current Pay Period.");
      }
      if(!bean.isReRun() && !businessCertificate.isPensioner()){
      for (Subvention s : bean.getSubventionList())
        if (IppmsUtils.isNullOrEmpty(s.getAmountStr() ) && !s.isEmployeeBound()) {
          pErrors.rejectValue("payDate", "Invalid.Value", "Please enter an amount for Subvention : " + s.getName());
        }
        else
          try
          {
            Double.parseDouble(PayrollHRUtils.removeCommas(s.getAmountStr()));
          } catch (Exception wEx) {
            pErrors.rejectValue("payDate", "Invalid.Value", "Amount " + s.getAmountStr() + " entered for Subvention : " + s.getName() + " is not valid");
          }
      }
    }
  }
  public void validateForRerun(Object pTarget, Errors pErrors, GenericService genericService, BusinessCertificate businessCertificate) throws IllegalAccessException, InstantiationException {
    validate(pTarget,pErrors,businessCertificate);
      //Now check that we have data for which we want to rerun....
      if (!IppmsUtilsExt.pendingPaychecksExists(genericService,businessCertificate)){
          pErrors.rejectValue("payDate", "Invalid.Value", "No Pending Paychecks found for Payroll Rerun!. Contact GNL Systems Ltd. Database corrupt.");
 
      }
    }


  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(PayPeriodDaysMiniBean.class);
  }

  @Override
  public void validate(Object target, Errors errors) {

  }
}