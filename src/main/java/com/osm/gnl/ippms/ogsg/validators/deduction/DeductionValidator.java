/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.List;

@Component
public class DeductionValidator extends BaseValidator
{

    @Autowired
    public DeductionValidator(GenericService genericService) {
        super(genericService);
    }

    
public void validate(Object target, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException
  {

      AbstractDeductionEntity eGI = (AbstractDeductionEntity)target;
      EmpDeductionType wEDT;

      if (eGI.isDeleteWarningIssued()) {
        return;
      }
      if (eGI.getEmpDeductCatRef() < 1) {
        pErrors.rejectValue("empDeductCatRef", "Required Field", "Please select a Deduction Category");
      }
      if (eGI.getEmpDeductTypeRef() < 1) {
        pErrors.rejectValue("empDeductTypeRef", "Required Field", "Please select a Deduction Type");
      }
      if (eGI.getEmpDeductPayTypeRef() < 1) {
        pErrors.rejectValue("empDeductPayTypeRef", "Required Field", "Please select a 'Deduct as' Type");
      }
      ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field");

      if (pErrors.getErrorCount() < 1) {
        List<AbstractDeductionEntity> eList = (List<AbstractDeductionEntity>)this.genericService.loadAllObjectsWithSingleCondition(IppmsUtils.getDeductionInfoClass(businessCertificate), CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), eGI.getParentId()),null);
        for (AbstractDeductionEntity e : eList) {
          if ((e.getEmpDeductionType().getId().equals(eGI.getEmpDeductionType().getId())) && 
            (!e.getId().equals(eGI.getId())))
          {
            pErrors.rejectValue("description", "DuplicateEntry", businessCertificate.getStaffTypeName()+" already has this deduction defined");
            break;
          }

        }

        //Now check if the Deduction Type Needs Dates...
          wEDT = genericService.loadObjectById(EmpDeductionType.class, eGI.getEmpDeductTypeRef() );
        if(wEDT.isMustEnterDate()){
        	//Now Make sure the Dates are there...
        	if(eGI.getStartDate() == null){
        		 pErrors.rejectValue("startDate", "DuplicateEntry", "Deduction Start Date is required");
                 return;
        	}else if(eGI.getEndDate() == null){
        		 pErrors.rejectValue("endDate", "DuplicateEntry", "Deduction End Date is required");
                 return;
        	}
        	//Now see if this cat has good startDate and End Date...
        	PayrollFlag wPF =  genericService.loadObjectWithSingleCondition(PayrollFlag.class, CustomPredicate.procurePredicate("businessClientId", wEDT.getBusinessClientId()));
        	LocalDate wLastApproveDate = LocalDate.of(wPF.getApprovedYearInd(), wPF.getApprovedMonthInd(),1);

        	
        	if(eGI.getStartDate().compareTo(eGI.getEndDate()) >= 0){
        		 pErrors.rejectValue("startDate", "DuplicateEntry", "Deduction Start Date must be before Deduction End Date");
                 return;
        	}
        	//Now check if the Start Date is after the last Payroll Run
        	if(eGI.getStartDate().getMonthValue() < wLastApproveDate.getMonthValue()
        			&& eGI.getStartDate().getYear() == wLastApproveDate.getYear() ){
        		pErrors.rejectValue("startDate", "DuplicateEntry", "Deduction Start Date must be after last payroll approval period");
                return;
        	}
        	//Now check if there is a pending paycheck for the start date chosen...
            PredicateBuilder predicateBuilder = new PredicateBuilder();
        	predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", eGI.getStartDate().getMonthValue())).addPredicate(
        	        CustomPredicate.procurePredicate("runYear", eGI.getStartDate().getYear())).addPredicate(
        	                CustomPredicate.procurePredicate("businessClientId", wEDT.getBusinessClientId())).addPredicate(
        	                        CustomPredicate.procurePredicate("status","P"));

        	if(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(businessCertificate)) > 0){
        		pErrors.rejectValue("startDate", "DuplicateEntry", "Unapproved Paychecks exist for this deduction start date. Please change dates.");
                return;
        	}
        		
        }
        //Now check if the Deduction Type is of Percentage....

        if(wEDT.getPayTypes().isUsingPercentage()){
        	if(eGI.getAmount() != 0.0D &&
        			eGI.getAmount() != wEDT.getAmount()){
        		pErrors.rejectValue("startDate", "DuplicateEntry", "Deduction Amount must either be 0 or match defined value in "+wEDT.getName()+" Deduction Type ");
        		pErrors.rejectValue("startDate", "DuplicateEntry", "For Deduction Types that are percentage (%) deductions. ");

                return;
        	}
        }
      }

      if (eGI.getAmount() == 0.0D && eGI.getNegativePayId() != null && eGI.getNegativePayId() > 0){
          PredicateBuilder predicateBuilder = new PredicateBuilder();
          predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", eGI.getStartDate().getMonthValue())).addPredicate(
                  CustomPredicate.procurePredicate("runYear", eGI.getStartDate().getYear())).addPredicate(
                  CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())).addPredicate(
                  CustomPredicate.procurePredicate("status","P"));
    	  if(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,IppmsUtils.getPaycheckClass(businessCertificate)) > 0){
    		  ((AbstractDeductionEntity)target).setMustDeletePayroll(true);
    	  }
    	  
      }
       
      if ((pErrors.getErrorCount() < 1) && 
        (eGI.getMode() != null) && (eGI.getMode().equalsIgnoreCase("delete"))) {
        EmpDeductionType eType =this.genericService.loadObjectById(EmpDeductionType.class, eGI.getEmpDeductTypeRef());
        if (eType.isInherited())
          pErrors.rejectValue("empDeductionTypeRef", "Required Field", "Can not delete an Inherited Deduction");
      }

  }

    /**
     * Can this {@link Validator} {@link #validate(Object, Errors) validate}
     * instances of the supplied {@code clazz}?
     * <p>This method is <i>typically</i> implemented like so:
     * <pre class="code">return Foo.class.isAssignableFrom(clazz);</pre>
     * (Where {@code Foo} is the class (or superclass) of the actual
     * object instance that is to be {@link #validate(Object, Errors) validated}.)
     *
     * @param clazz the {@link Class} that this {@link Validator} is
     *              being asked if it can {@link #validate(Object, Errors) validate}
     * @return {@code true} if this {@link Validator} can indeed
     * {@link #validate(Object, Errors) validate} instances of the
     * supplied {@code clazz}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return AbstractDeductionEntity.class.isAssignableFrom(clazz);
    }

    /**
     * Validate the supplied {@code target} object, which must be
     * of a {@link Class} for which the {@link #supports(Class)} method
     * typically has (or would) return {@code true}.
     * <p>The supplied {@link Errors errors} instance can be used to report
     * any resulting validation errors.
     *
     * @param target the object that is to be validated
     * @param errors contextual state about the validation process
     * @see ValidationUtils
     */
    @Override
    public void validate(Object target, Errors errors) {

    }


}