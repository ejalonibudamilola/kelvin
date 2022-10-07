package com.osm.gnl.ippms.ogsg.validators.search;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class SearchValidator extends BaseValidator
{

  @Autowired
  protected SearchValidator(GenericService genericService) {
    super(genericService);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return HrMiniBean.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {

  }

  public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate)
  {
    HrMiniBean wHEMB = (HrMiniBean)pTarget;

    if (!PayrollHRUtils.treatNull(wHEMB.getEmployeeId()).equals("")) {
      return;
    }
    if ((PayrollHRUtils.treatNull(wHEMB.getFirstName()).equals("")) && (PayrollHRUtils.treatNull(wHEMB.getLastName()).equals("")))
    {
      if(businessCertificate.isPensioner()){
        if(PayrollHRUtils.treatNull(wHEMB.getLegacyEmployeeId()).equals("")){
          pErrors.rejectValue("", "search.criteria", "Please enter a value for First Name or Last Name or just "+businessCertificate.getStaffTitle());
          return;
        }
      }else {
        pErrors.rejectValue("", "search.criteria", "Please enter a value for First Name or Last Name or just " + businessCertificate.getStaffTitle());
      }
    }
  }

  public void validate(Object pTarget, Errors pErrors, boolean pSubvention)
  {
    Subvention wHEMB = (Subvention)pTarget;

    if (PayrollHRUtils.treatNull(wHEMB.getName()).equals(""))
    {
      pErrors.rejectValue("", "search.criteria", "Please enter a value to search with");
    }
  }
  public void validateForActiveSearch(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) {
    HrMiniBean wHMB = (HrMiniBean)pTarget;

    if(!IppmsUtils.isNotNullOrEmpty(wHMB.getEmployeeId()))
       pErrors.rejectValue("", "search.criteria", "Please enter a value for "+businessCertificate.getStaffTitle()+".");

    if(IppmsUtils.isNullOrLessThanOne(wHMB.getParentInstId()))
      pErrors.rejectValue("parentInstId", "search.criteria", "Please select Active Service Organization.");

    return;
  }
  public void validateForCashbook(HrMiniBean pEHB, Errors pErrors , BusinessCertificate businessCertificate)
  {
    if (pEHB.getMonthInd() == -1) {
      pErrors.rejectValue("monthId", "search.criteria", "Please enter a value for the Cashbook Month");
      return;
    }
    if (pEHB.getYearInd() == -1) {
      pErrors.rejectValue("yearInd", "search.criteria", "Please enter a value for the Cashbook Year");
      return;
    }
    double paye = 0.0D; double salary = 0.0D;

    String amountStr = PayrollHRUtils.removeCommas(pEHB.getAmountStr());
    try {
      salary = Double.parseDouble(amountStr);
    } catch (Exception ex) {
      pErrors.rejectValue("amountStr", "Invalid Value", "Public Office Holders Emoluments should be numberic!");
    }

    String payeStr = PayrollHRUtils.removeCommas(pEHB.getPayeStr());
    try {
      paye = Double.parseDouble(payeStr);
    } catch (Exception ex) {
      pErrors.rejectValue("amountStr", "Invalid Value", "Public Office Holders PAYE should be numeric!");
    }

    if (((paye > 0.0D) && (salary == 0.0D)) || ((paye > 0.0D) && (salary > 0.0D) && (paye >= salary))) {
      pErrors.rejectValue("amountStr", "Invalid Value", "Public Office Holders PAYE can not be greater than their Emoluments!");
    }


    int noOfPaychecks = IppmsUtilsExt.countNoOfPayChecks(genericService,businessCertificate, pEHB.getMonthInd(), pEHB.getYearInd(), false);

    if (noOfPaychecks == 0) {
      pErrors.rejectValue("yearInd", "Invalid.Value", "No 'APPROVED' Paycheck information found for " + PayrollBeanUtils.getMonthNameFromInteger(pEHB.getMonthInd()) + " " + pEHB.getYearInd() + ". Please select new values. ");

      return;
    }
  }

  public void validateForPensionSearch(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) {
      HrMiniBean hrMiniBean = (HrMiniBean)pTarget;
     if(IppmsUtils.isNullOrLessThanOne(hrMiniBean.getParentId()) && businessCertificate.isLocalGovtPension()) {
          pErrors.rejectValue("parentId", "Invalid.Value", "Please pick a value for 'Service Organization");
          return;
        }


  }

  public void validateRetiringEmployee(Errors pErrors, Employee retiringEmployee, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {
    if(retiringEmployee.isNewEntity()){
        pErrors.rejectValue("", "search.no_values", "No Retired Employee found! Please retry.");
        return;
     }else{
      //check if he is created before...
      Pensioner pensioner = this.genericService.loadObjectWithSingleCondition(Pensioner.class,CustomPredicate.procurePredicate("employee.id", retiringEmployee.getId()));
      if(!pensioner.isNewEntity()){
        pErrors.rejectValue("", "search.no_values", "Employee already created on the "+businessCertificate.getBusinessName()+" Database.");
        return;
      }
      if(!retiringEmployee.isApprovedForPayrolling()){
        pErrors.rejectValue("", "search.no_values", "Employee was never approved for Payroll. Have Employee Approved before moving to Pensions.");
        return;
      }
      HiringInfo hiringInfo = this.genericService.loadObjectWithSingleCondition(HiringInfo.class,CustomPredicate.procurePredicate("employee.id", retiringEmployee.getId()));
      if(hiringInfo.isNewEntity()){
          pErrors.rejectValue("", "search.no_values", "No Hiring Information found for "+retiringEmployee.getDisplayNameWivTitlePrefixed());
          return;
      }else{
        if(hiringInfo.getTerminateReason().isNotReinstateable()){
          pErrors.rejectValue("", "search.no_values", retiringEmployee.getDisplayNameWivTitlePrefixed()+" was terminated due to "+hiringInfo.getTerminateReason().getName()
                              +". Such "+businessCertificate.getStaffTypeName()+" are not Pensionable");
        }else if(!hiringInfo.isPensionableEmployee()){
          pErrors.rejectValue("", "search.no_values", retiringEmployee.getDisplayNameWivTitlePrefixed()+" is NOT Pensionable.");
        }else if(!hiringInfo.isTerminatedEmployee()){
          pErrors.rejectValue("", "search.no_values", retiringEmployee.getDisplayNameWivTitlePrefixed()+" is currently Active.");

        }
      }
    }
  }
}