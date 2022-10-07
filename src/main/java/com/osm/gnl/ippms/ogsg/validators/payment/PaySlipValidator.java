package com.osm.gnl.ippms.ogsg.validators.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;


@Component
public class PaySlipValidator extends BaseValidator {
	@Autowired
	private PaycheckService paycheckService;

	protected PaySlipValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> arg0) {
		 
		return HrMiniBean.class.isInstance(arg0);
	}

	@Override
	public void validate(Object o, Errors errors) {
		errors.rejectValue("runMonth", "Required.Value", "Payroll Run Month is required.");
	}


	public void validate(Object arg0, Errors pErrors, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
		 
		 HrMiniBean wHMB = (HrMiniBean)arg0;
		 
		 if(wHMB.getMapId() == 0) {
			 pErrors.rejectValue("mapId", "Required.Value", "Pay Slip Generation Mode is required."); 
			 return;		 
		 }
		 
		 if(wHMB.getRunMonth() == -1) {
			 pErrors.rejectValue("runMonth", "Required.Value", "Payroll Run Month is required."); 
			 return;
			 
		 }
		 if(wHMB.getRunYear() == 0) {
			 pErrors.rejectValue("runYear", "Required.Value", "Payroll Run Year is required."); 
			 return;
		 }
		 
		 if(wHMB.isMdaType()) {
			  
			 if(IppmsUtils.isNullOrLessThanOne(wHMB.getMdaId())) {
				 pErrors.rejectValue("mapId", "Required.Value", "Please select "+bc.getMdaTitle());
				 return;
			 }
			 
		 }else if(wHMB.isSalaryType()) {
			 if(IppmsUtils.isNullOrLessThanOne(wHMB.getSalaryTypeId())) {
				 pErrors.rejectValue("mapId", "Required.Value", "Please select Pay Group"); 
				 return;
				 
			 }else {
				 if(wHMB.getFromLevel() == 0)
					 pErrors.rejectValue("mapId", "Required.Value", "Please select a value for 'From Level'"); 
				 if(wHMB.getToLevel() > 0 && (wHMB.getToLevel() < wHMB.getFromLevel()))
					 pErrors.rejectValue("mapId", "Required.Value", "'To Level' must be greater than 'From Level'"); 
			 }
			
		 }else {
			 
			 ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "staffId", "Required Field", bc.getStaffTitle()+" is required") ;
			 
			 if(pErrors.hasErrors()) {
				 return;
				 
		       }else {
		    	   //--Find the Employee...
//		    	   Employee e = (Employee) this.payrollService.loadObectByClassAndName(Employee.class, "employeeId",wHMB.getStaffId());

				 AbstractEmployeeEntity e = (AbstractEmployeeEntity) this.genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(bc),
						   CustomPredicate.procurePredicate("employeeId", wHMB.getStaffId()));


				 if(e.isNewEntity()) {
		    		   //--Employee not found.
		    		   pErrors.rejectValue("staffId", "Value.NotFound", "No "+bc.getStaffTypeName()+" Found using ID "+wHMB.getStaffId());
		    		   return;
		    	   }else {
		    	 
		    		   if(wHMB.getIdMap() != null) 
		    			    if(wHMB.getIdMap().get(e.getId()) != null) {
		    			    	 pErrors.rejectValue("staffId", "Value.NotFound", bc.getStaffTypeName()+" - "+e.getDisplayNameWivTitlePrefixed()+" has already been added.");
		  		    		   return;	
		    			    }   
 		    		   //Pass By Reference...
		    		  ( (HrMiniBean)arg0).setAbstractEmployeeEntity(e);
		    	   }
		       }
			
		 }
		 //--If we get here...lets see if there is even a value for the Selected Type for PaySlip Generation...
		 if(!pErrors.hasErrors()) {
		   NamedEntityBean n =  this.paycheckService.createPaySlipDisplayBean(wHMB,bc);
		   String wName = "";
		   if(n.getNoOfActiveEmployees() == 0) {
			   if(wHMB.isMdaType()) {

				   MdaInfo m = this.genericService.loadObjectUsingRestriction(MdaInfo.class, Arrays.asList(
				   		CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
						   CustomPredicate.procurePredicate("id",wHMB.getMdaId())));

				   wName = m.getName();

				   pErrors.rejectValue("staffId", "Value.NotFound", "No Paychecks to print for  "+bc.getMdaTitle()+" - "+wName+" for "+PayrollBeanUtils.getMonthNameFromInteger(wHMB.getRunMonth())+", "+ wHMB.getRunYear());
			   }else if(wHMB.isSalaryType()) {

				   SalaryType s = this.genericService.loadObjectUsingRestriction(SalaryType.class, Arrays.asList(
				   		CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
						   CustomPredicate.procurePredicate("id",wHMB.getSalaryTypeId())));

				   wName = s.getName();

				   pErrors.rejectValue("staffId", "Value.NotFound", "No Paychecks to print for  Pay Group - "+wName+", Level "+wHMB.getFromLevel()+" to "+wHMB.getToLevel()+" for "+PayrollBeanUtils.getMonthNameFromInteger(wHMB.getRunMonth())+", "+ wHMB.getRunYear());

			   }else {

				   AbstractEmployeeEntity e = (AbstractEmployeeEntity) this.genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(bc),
				   		CustomPredicate.procurePredicate("employeeId",wHMB.getStaffId()));

				   wName = e.getDisplayNameWivTitlePrefixed();
				   pErrors.rejectValue("staffId", "Value.NotFound", "No Paychecks to print for  "+bc.getStaffTypeName()+" - "+wName+" for "+ PayrollBeanUtils.getMonthNameFromInteger(wHMB.getRunMonth())+", "+ wHMB.getRunYear());

			   }
			  
		   }
			  
  		   return;	
		 }
	}

	public void validateForPrint(Object arg0, Errors pErrors) {
		 HrMiniBean wHMB = (HrMiniBean)arg0;
		 
		 if(wHMB.getMappedParentDeptList() == null || wHMB.getMappedParentDeptList().isEmpty())
			   pErrors.rejectValue("staffId", "Value.NotFound", "There are no selections added. Please Add a selection ");

			return; 
	}
}
