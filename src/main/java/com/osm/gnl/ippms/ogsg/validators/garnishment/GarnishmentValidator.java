/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.garnishment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GarnishmentValidator extends BaseValidator
{
    private final PaycheckService paycheckService;
    @Autowired
    public GarnishmentValidator(GenericService genericService, PaycheckService paycheckService) {
        super(genericService);
        this.paycheckService = paycheckService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return AbstractGarnishmentEntity.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
  public void validate(Object target, Errors pErrors, BusinessCertificate pBc)
  {
    if (AbstractGarnishmentEntity.class.isAssignableFrom(target.getClass()))
    {
        AbstractGarnishmentEntity eGI = (AbstractGarnishmentEntity)target;

     boolean zerorizing = false;

      double wOA;
     // double wGA = 0.0D;
      try {
        String wOwedAmount = eGI.getOwedAmountStr();
        wOA = Double.parseDouble(PayrollHRUtils.removeCommas(wOwedAmount));

        if (wOA < 0.0D) {
          pErrors.rejectValue("owedAmountStr", "Required Field", "Loan Amount can not be a negative number");
          return;
        }else if(wOA == 0.0D){
        	//Now check if there are pending paychecks...
        	if( this.paycheckService.getPendingPaycheckRunMonthAndYear(pBc) != null ) {
        		pErrors.rejectValue("owedAmountStr", "Required Field", "Loan Amount can not be set to 0 (zero) if Pending paychecks exist");
                return;
        	}else{
        	    //This is zerorizing...set flag and exit.
                zerorizing = true;
            }
        }

      }
      catch (NumberFormatException wNFE) {
        pErrors.rejectValue("owedAmountStr", "Required Field", "Loan Amount should be 'Numbers' only.");
        return;
      }
       if(zerorizing)
           return;
        if (eGI.getLoanTerm() <= 0 && eGI.isNewEntity()) {
            pErrors.rejectValue("loanTerm", "Required Field", "Please select a value for Loan Term");
            return;
        }
        if (eGI.getNewLoanTerm() <= 0 && !eGI.isNewEntity() && eGI.getOwedAmount() > 0) {
            pErrors.rejectValue("loanTerm", "Required Field", "Please select a value for New Loan Term");
            return;
        }
        if (IppmsUtils.isNullOrLessThanOne(eGI.getEmpGarnishmentType().getId())) {
            pErrors.rejectValue("empGarnishTypeInstId", "Required Field", "Please select a Loan Type");
            return;
        }
     /* if (wGA > wOA) {
        pErrors.rejectValue("interestAmountStr", "Required Field", "Monthly Interest Amount can not be greater than Amount Owed.");
        return;
      }

      if (wGA * eGI.getLoanTerm() > wOA * 1.3D)
      {
        pErrors.rejectValue("interestAmountStr", "Required Field", "Interest Rate is greater than 30%.");
        return;
      }*/
      
      //If we get here..make sure this employee CANNOT take a loan that will not end with his service....
      try {
          ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class,CustomPredicate.procurePredicate("businessClientId", pBc.getBusinessClientInstId()));
    	  HiringInfo wHI = this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(pBc.getEmployeeIdJoinStr(), eGI.getParentId()));
          if(!wHI.isNewEntity()){
	    	  int wLoanTerm;
	    	  if(eGI.isNewEntity()){
	    		  wLoanTerm = eGI.getLoanTerm();
	    	  }else{
	    		  wLoanTerm = eGI.getNewLoanTerm(); 
	    	  }
	    	  LocalDate wToday = LocalDate.now();
	    	  LocalDate serviceEndDate = null;
	    	  boolean contractStaff = false;
              if(wHI.getExpectedDateOfRetirement() == null){
                  wHI.setExpectedDateOfRetirement(PayrollBeanUtils.calculateExpDateOfRetirement(wHI.getBirthDate(), wHI.getHireDate(),configurationBean,pBc));
              }else{
                  serviceEndDate = wHI.getExpectedDateOfRetirement();
              }

              //First check if staff is a contract staff...
              if(wHI.isContractStaff()){
                  contractStaff = true;
                  serviceEndDate = wHI.getContractEndDate();

              }



	    	  if(serviceEndDate.getYear() < wToday.getYear()){


                  if(contractStaff){
                      pErrors.rejectValue("loanTerm", "Required Field", pBc.getStaffTypeName()+"'s Contract should have been terminated by "+PayrollBeanUtils.getDateAsString(serviceEndDate)+" ");
                      pErrors.rejectValue("loanTerm", "Required Field", "Loans can not be given to an "+ pBc.getStaffTypeName()+" with Contracts that should have been terminated");
                  }else{
                      pErrors.rejectValue("loanTerm", "Required Field", pBc.getStaffTypeName()+" should have been retired by "+PayrollBeanUtils.getDateAsString(serviceEndDate)+" ");
                      pErrors.rejectValue("loanTerm", "Required Field", "Loans can not be given to an "+ pBc.getStaffTypeName()+" that should have been terminated");
                  }

                  return;
              }
	    	   int noOfMonthsToRetire = (wHI.getExpectedDateOfRetirement().getYear() - wToday.getYear())* 12;
	    	  
	    	  //Now check if noOfMonthsToRetire == 0, meaning same year...use months...

	    	  if(noOfMonthsToRetire == 0){
	    		  //Same Year...
	    		  noOfMonthsToRetire = wHI.getExpectedDateOfRetirement().getMonthValue() - wToday.getMonthValue();
	    		  
	    		  if(noOfMonthsToRetire < 0)
	    			  noOfMonthsToRetire = 0;
	    	  }
	    	  //Good Candidate for Customizing....
	    	  if(wLoanTerm > noOfMonthsToRetire){
	    		  //Now we need to check if the owed amount == 0...
	    		  //In this instance is loan is being stopped. 
	    		  //It makes no sense to check for loan term vs retirement date...
	    		  if(wOA != 0){

                      if(contractStaff){
                          pErrors.rejectValue("loanTerm", "Required Field", pBc.getStaffTypeName()+"'s Contract ends in  "+noOfMonthsToRetire+" Months from now.");
                      }else{
                          pErrors.rejectValue("loanTerm", "Required Field", pBc.getStaffTypeName()+" retires in "+noOfMonthsToRetire+" Months from now.");
                      }
	    			  pErrors.rejectValue("loanTerm", "Required Field", "Selected Loan Term of "+wLoanTerm+" will not enable "+ pBc.getStaffTypeName()+" pay up the loan.");
	    			  return;
	    		  }
	    	  }
	    	  
          }
      } catch (Exception wEx) {
		
		wEx.printStackTrace();
      }
      //--Now Check if this Loan will take him to negative Pay...
    }
  }

  public void validateForDelete(Object target, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException
  {
    if (AbstractGarnishmentEntity.class.isAssignableFrom(target.getClass()))
    {
        AbstractGarnishmentEntity eGI = (AbstractGarnishmentEntity)target;

             PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("empGarnInfo.id", eGI.getId()));
              if(genericService.countObjectsUsingPredicateBuilder(predicateBuilder,IppmsUtils.getPaycheckGarnishmentClass(businessCertificate)) > 0)
             {
                 pErrors.rejectValue("", "DependentEntities", "This Loan/Garnishment has payroll data attached. Can not be deleted.");
             }

    }
  }

}