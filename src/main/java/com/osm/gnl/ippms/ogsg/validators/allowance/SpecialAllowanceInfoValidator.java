package com.osm.gnl.ippms.ogsg.validators.allowance;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.List;

@Component
public class SpecialAllowanceInfoValidator implements Validator
{ 

	private final GenericService genericService;
  @Autowired
  public SpecialAllowanceInfoValidator(GenericService genericService) {
    this.genericService = genericService;
  }

  @Override
	public boolean supports(Class<?> clazz) {
		return AbstractSpecialAllowanceEntity.class.isAssignableFrom(clazz);
	}

  @Override
  public void validate(Object target, Errors errors) {

  }

  
public void validate(Object target, Errors pErrors, BusinessCertificate businessCertificate)
    
  {
    if (AbstractSpecialAllowanceEntity.class.isAssignableFrom(target.getClass()))
    {
      AbstractSpecialAllowanceEntity eGI = (AbstractSpecialAllowanceEntity)target;

      ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "amountStr", "Required Field", "Value is a required field");

      if (eGI.getStartDate() == null) {
        pErrors.rejectValue("startDate", "Required Field", "Start Date is required for Special Allowance.");
        return;
      }else{
        try{
            LocalDate.parse(String.valueOf(eGI.getStartDate()));
        }catch (Exception wEx){
          pErrors.rejectValue("startDate", "Required Field", "Invalid Date Format for 'Allowance Start Date*'.");
          return;
        }
      }
      if (IppmsUtils.isNullOrLessThanOne(eGI.getTypeInstId())) {
        pErrors.rejectValue("typeInstId", "Required Field", "Please select a Special Allowance Type");
        return;
      }

      if (IppmsUtils.isNullOrLessThanOne(eGI.getPayTypeInstId())) {
        pErrors.rejectValue("payTypeInstId", "Required Field", "Please select a value for 'Apply as'");
        return;
      }
      double wOA;
      try
      {
        String wOwedAmount = eGI.getAmountStr();
        wOA = Double.parseDouble(PayrollHRUtils.removeCommas(wOwedAmount));

        if (wOA < 0.0D) {
          pErrors.rejectValue("amountStr", "Required Field", "Allowance Value can not be a negative number");
          return;
        }
        if (wOA == 0.0D && IppmsUtils.isPendingPaychecksExisting(genericService,businessCertificate)) {
            pErrors.rejectValue("amountStr", "Required Field", "Allowance Value can not be set to 0 (Zero). Pending Paychecks exists.");
            return;
          }
        PayTypes payTypes = this.genericService.loadObjectById(PayTypes.class,eGI.getTypeInstId());
        if(payTypes.isUsingPercentage()){

          ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class, CustomPredicate.procurePredicate("businessClientId",businessCertificate.getBusinessClientInstId()));
          if(wOA > configurationBean.getMaxSpecAllowValue()){
            pErrors.rejectValue("amountStr", "Required Field", "Allowance Value must be "+ PayrollHRUtils.getDecimalFormat().format(configurationBean.getMaxSpecAllowValue()) +" or less.");
            return;
          }
        }

      }
      catch (NumberFormatException wNFE) {
        pErrors.rejectValue("amountStr", "Required Field", "Allowance Value should be 'Numbers' only.");
        return;
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        pErrors.rejectValue("amountStr", "Required Field", "Fatal Error Occurred. Please contact GNL Systems Ltd.");
        e.printStackTrace();
      }


      SpecialAllowanceType wSAT = null;
      //Long pId = eGI.getSpecialAllowanceType().getId();
	try {

		wSAT = genericService.loadObjectById(SpecialAllowanceType.class, eGI.getTypeInstId());
	} catch (Exception e) {
	 
		e.printStackTrace();
	}
	((AbstractSpecialAllowanceEntity)target).setSpecialAllowanceType(wSAT);
      if (wSAT.isEndDateRequired())
      {
       

        if (eGI.getEndDate() == null) {
          pErrors.rejectValue("endDate", "Required Field", "End Date is required for this type of Special Allowance.");
          return;
        }

        if (eGI.getStartDate().isAfter(eGI.getEndDate())) {
          pErrors.rejectValue("startDate", "Required Field", "Start Date must be before End Date.");
          return;
        }
        if ((eGI.getStartDate().getYear() != eGI.getEndDate().getYear()) &&
                (eGI.getStartDate().getMonthValue() != eGI.getEndDate().getMonthValue()))
        {
          pErrors.rejectValue("", "Required Field", "For Salary Arrears, Start Date and End Date must be in the same Month and Year.");
          return;
        }
      }
      if (pErrors.getErrorCount() < 1) {
       
		List<AbstractSpecialAllowanceEntity> eList = (List<AbstractSpecialAllowanceEntity>)genericService.loadAllObjectsWithSingleCondition(IppmsUtils.getSpecialAllowanceInfoClass(businessCertificate), CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(),
                eGI.getParentObject().getId()), null);
        for (AbstractSpecialAllowanceEntity g : eList)
        {
          if ((g.getSpecialAllowanceType().getId().equals(eGI.getTypeInstId())) && (!eGI.isEditMode()) &&
            (eGI.isNewEntity()))
          {
            ((AbstractSpecialAllowanceEntity)target).setId(g.getId());
            ((AbstractSpecialAllowanceEntity)target).setExpire(0);
            break;
          }

        }

      }

      if (eGI.isWarningIssued())
      {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "referenceNumber", "Required Field", "Please enter a value for the Reference Number");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "referenceDate", "Required Field", "Please enter a value for the Reference Date");

        if (pErrors.getErrorCount() < 1)
        {
          if (eGI.getReferenceDate().isAfter(LocalDate.now()))
          {
            pErrors.rejectValue("referenceDate", "Invalid.Value", "Reference Date can not be after Allowance Start Date.");
            return;
          }
        }
      }
    }
  }

  public void validateForMassEntry(Object target, Errors pErrors, GenericService genericService)
    throws Exception
  {
    if (target.getClass().isAssignableFrom(PaginatedPaycheckGarnDedBeanHolder.class))
    {
      PaginatedPaycheckGarnDedBeanHolder eGI = (PaginatedPaycheckGarnDedBeanHolder)target;

      if (IppmsUtils.isNullOrLessThanOne(eGI.getPayTypeInstId())) {
        pErrors.rejectValue("", "Required Field", "Please Select Pay Type.");
      }

      if (eGI.getStartDate() == null) {
        pErrors.rejectValue("startDate", "Required Field", "Start Date is required for Special Allowance.");
        return;
      }
      

      SpecialAllowanceType wSAT = genericService.loadObjectById(SpecialAllowanceType.class, eGI.getSalaryTypeId());

      if (wSAT.isEndDateRequired())
      {
        
        if (eGI.getEndDate() == null) {
          pErrors.rejectValue("endDate", "Required Field", "End Date is required for this type of Special Allowance.");
          return;
        }
        LocalDate wStartDate = LocalDate.from(eGI.getStartDate());
        LocalDate wEndDate = LocalDate.from(eGI.getEndDate());

        if (wStartDate.isAfter(wEndDate)){
          pErrors.rejectValue("startDate", "Required Field", "Start Date must be before End Date.");
          return;
        }
        if ((wStartDate.getDayOfMonth() != wEndDate.getDayOfMonth()) && (wStartDate.getMonthValue() != wEndDate.getMonthValue())
        		&& wSAT.isArrearsType())
        {
          pErrors.rejectValue("", "Required Field", "For Salary Arrears, Start Date and End Date must be in the same Month and Year.");
          return;
        }
      }
    }
  }

  private int getNoOfPendingPaychecks(BusinessCertificate businessCertificate) {
      return genericService.countObjectsUsingPredicateBuilder(
            new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("status", 'P')), IppmsUtils.getPaycheckClass(businessCertificate));
  }
}