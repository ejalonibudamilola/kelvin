package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleDetails;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;


@Component
public class ReabsorbingValidator extends BaseValidator
{
  @Autowired
  protected ReabsorbingValidator(GenericService genericService) {
    super(genericService);
  }

  public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate, PaycheckService paycheckService)
  {
    HrMiniBean pEHB = (HrMiniBean)pTarget;

    if (pEHB.getTerminateReasonId() == 0) {
      pErrors.rejectValue("", "Reason.Invalid", "Please select a reason for Reabsorbing into the "+businessCertificate.getBusinessName()+" IPPMS");
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

      AbstractPaycheckEntity wEmpPayBean = paycheckService.loadLastNoneZeroPaycheckForEmployee(businessCertificate,pEHB.getEmployeeInstId());

      if (!wEmpPayBean.isNewEntity()) {

        if ((!pEHB.getArrearsStartDate().isBefore(wEmpPayBean.getPayDate())) || (!pEHB.getArrearsEndDate().isBefore(wEmpPayBean.getPayDate()))) {
          pErrors.rejectValue("", "Reason.Invalid", "'Arrears dates' must be after " + pEHB.getName() + "'s last pay date.");
        }

      }

    }

    if ((pEHB.getShowForConfirm() != null) && (pEHB.getShowForConfirm().equalsIgnoreCase(IConstants.SHOW_ROW)))
    {
      if (StringUtils.trimToEmpty(pEHB.getRefNumber()).equals("")) {
        pErrors.rejectValue("", "Reason.Invalid", "Please Enter the reference number on the 'Re-absorption Letter");
        return;
      }

      if (pEHB.getRefDate() == null) {
        pErrors.rejectValue("", "Reason.Invalid", "Reference Date cannot be empty");
        return;
      }

      if (pEHB.getRefDate().isAfter(LocalDate.now())) {
        pErrors.rejectValue("", "Reason.Invalid", "Reference Date must be on or before today's date.");
        return;
      }
    }
  }

  public void validateForAllowanceRule(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws IllegalAccessException, InstantiationException {
        HrMiniBean pEHB = (HrMiniBean)pTarget;
        if(pEHB.getAllowanceRuleMaster().getHiringInfo() != null){
          if(pEHB.getAllowanceRuleMaster().getHiringInfo().isTerminatedEmployee()){
            pErrors.rejectValue("", "Reason.Invalid", businessCertificate.getStaffTypeName()+" "+
                    pEHB.getAllowanceRuleMaster().getHiringInfo().getAbstractEmployeeEntity().getDisplayName()+" is Terminated. Pay Group Allowance Rule Denied.");
            return;
          }
        }
        if(pEHB.getArrearsStartDate()== null){
          pErrors.rejectValue("", "Reason.Invalid", "Please select a value for 'Start Date'");
          return;

        }else if(pEHB.getArrearsEndDate() == null){
          pErrors.rejectValue("", "Reason.Invalid", "Please select a value for 'End Date'");
          return;
        }
        if(pEHB.getArrearsEndDate().isBefore(pEHB.getArrearsStartDate())){
          pErrors.rejectValue("", "Reason.Invalid", "'Start Date' must be before 'End Date'");
          return;
        }
         if(pEHB.getArrearsStartDate().getDayOfMonth() != 1){
           pErrors.rejectValue("", "Reason.Invalid", "Start Date must start from the first day of "
                   + PayrollBeanUtils.getMonthNameFromInteger(pEHB.getArrearsStartDate().getMonthValue()));
           return;
         }
         if(pEHB.getArrearsEndDate().getDayOfMonth() != PayrollBeanUtils.getEndOfMonth(pEHB.getArrearsEndDate()).getDayOfMonth()){
           pErrors.rejectValue("", "Reason.Invalid", "End Date must be the last day of "
                   + PayrollBeanUtils.getMonthNameFromInteger(pEHB.getArrearsStartDate().getMonthValue()));
           return;
         }
        //Check if start date is after last Payroll Run....
        //Check if we have a pending payroll run...
        if(IppmsUtilsExt.pendingPaychecksExists(genericService,businessCertificate)){
          pErrors.rejectValue("", "Reason.Invalid", "Pending Payroll Exists. Pay Group Allowance Rule creation denied.");
          return;
        }
        PayrollFlag payrollFlag = IppmsUtilsExt.getPayrollFlagForClient(genericService,businessCertificate);

        if(pEHB.getArrearsStartDate().getYear() < payrollFlag.getApprovedYearInd() ){
          pErrors.rejectValue("", "Reason.Invalid", "Start Date Year must be "+payrollFlag.getApprovedYearInd()+" or later.");
          return;
        }
        if(pEHB.getArrearsStartDate().getYear() == payrollFlag.getApprovedYearInd() && pEHB.getArrearsStartDate().getMonthValue() <= payrollFlag.getApprovedMonthInd()){
          pErrors.rejectValue("", "Reason.Invalid", "Start Date must be after "+ PayrollBeanUtils.getMonthNameFromInteger(payrollFlag.getApprovedMonthInd()));
          return;
        }
        //if we get here, all good. Set the Date values...
        ((HrMiniBean)pTarget).getAllowanceRuleMaster().setRuleStartDate(pEHB.getArrearsStartDate());
        ((HrMiniBean)pTarget).getAllowanceRuleMaster().setRuleEndDate(pEHB.getArrearsEndDate());
        //--Check the Entered Values.
        int count = 0;
        double value;
        for(AllowanceRuleDetails details : pEHB.getAllowanceRuleMaster().getAllowanceRuleDetailsList()) {
            if(IppmsUtils.isNullOrEmpty(details.getAmountStr()))
              continue;
          try {
            value = Double.parseDouble(PayrollHRUtils.removeCommas(details.getAmountStr()));
            if(value >= details.getMonthlyValue()){
              pErrors.rejectValue("", "Reason.Invalid", "Value for 'Rule Monthly Amount' of  "+details.getBeanFieldName()+" MUST be less than actual Pay Group Structure Value of "+PayrollHRUtils.getDecimalFormat().format(details.getMonthlyValue()));
              continue;
            }
            count++;
          } catch (Exception wEx) {
            pErrors.rejectValue("", "Reason.Invalid", "Please enter a valid numeric value for 'Rule Monthly Amount' of  "+details.getBeanFieldName());

          }
        }
        if(pErrors.getErrorCount() == 0 && count == 0 && !pEHB.isEditMode()){
             if(!pEHB.isEditMode())
                pErrors.rejectValue("", "Reason.Invalid", "To create a Pay Group Allowance Rule, at least 1 (One) Allowance Name must have a value for 'Rule Monthly Amount'");
             else {
               pErrors.rejectValue("", "Reason.Invalid", "Update not allowed as not value exists for 'Rule Monthly Amount' of ANY Allowance Name");
               pErrors.rejectValue("", "Reason.Invalid", "You may choose to 'Delete' this Pay Group Allowance Rule instead.");

             }

        }


  }



  @Override
  public boolean supports(Class<?> aClass) {
    return HrMiniBean.class.isAssignableFrom(aClass);
  }

  @Override
  public void validate(Object o, Errors errors) {

  }
}