package com.osm.gnl.ippms.ogsg.validators.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeHrBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class SetupNewEmployeeValidator extends BaseValidator
{
  @Autowired
  public SetupNewEmployeeValidator(GenericService genericService) {
    super(genericService);
  }

  public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {
    EmployeeHrBean wEHB = (EmployeeHrBean) pTarget;

    if(businessCertificate.isPensioner()){
      if(IppmsUtils.isNullOrLessThanOne(wEHB.getParentClientId())){
        pErrors.rejectValue("parentClientId", "Required.Value", "Service Organization" + " is required.");
        return;
      }
    }
    if (IppmsUtils.isNullOrLessThanOne(wEHB.getMdaId())) {
      pErrors.rejectValue("mdaId", "Required.Value", businessCertificate.getStaffTypeName() + " " + businessCertificate.getMdaTitle() + " is required.");
      return;
    }
    if(!businessCertificate.isPensioner())
    if (IppmsUtils.isNullOrLessThanOne(wEHB.getDeptId())) {
      pErrors.rejectValue("deptId", "Required.Value", "Department is required.");
      return;
    }
    if (businessCertificate.isCivilService() || businessCertificate.isSubeb()){
      if (IppmsUtils.isNullOrLessThanOne(wEHB.getSalaryTypeId())) {
        pErrors.rejectValue("salaryTypeId", "Required.Value", "Please select " + businessCertificate.getStaffTypeName() + "Designation.");
      }
  }
    if(businessCertificate.isLocalGovt()){
      if (IppmsUtils.isNullOrLessThanOne(wEHB.getCadreInstId()))
            pErrors.rejectValue("cadreInstId", "Required.Value", "Please select"+ businessCertificate.getStaffTypeName() +"  Cadre.");


    }
    if (IppmsUtils.isNullOrLessThanOne(wEHB.getRankInstId()))
      pErrors.rejectValue("rankInstId", "Required.Value", "Please select "+ businessCertificate.getStaffTypeName() +" Rank.");
      if (IppmsUtils.isNullOrLessThanOne(wEHB.getSalaryStructureId())) {
        pErrors.rejectValue("salaryStructureId", "Required.Value", "Please select the Pay Group Level And Step");
        return;
      }
      ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "firstName", "Required Field", "First Name is a required field");
      ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "lastName", "Required Field", "Last Name is a required field");
    ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class,CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
    if(configurationBean.isResidenceIdRequired())
       ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "residenceId", "Required Field", "Residence ID is a required field");
    if(configurationBean.isNinRequired()) {
      ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "nin", "Required Field", "NIN (National Identification Number) is a required field");
    }

      ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "employeeId", "Required Field", "" + businessCertificate.getStaffTitle() + " is required");
      if (businessCertificate.isLocalGovt())
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "fileNo", "Required Field", "File No. is Required.");

      if (pErrors.getErrorCount() == 0) {

        switch (IppmsUtils.validateOgNumber(businessCertificate,wEHB.getEmployeeId())){
          case 1:
            pErrors.rejectValue("employeeId", "Required Field", businessCertificate.getStaffTypeName()+" Must have "+businessCertificate.getStaffTitle()+" starting with "+businessCertificate.getEmpIdStartVal());
            return;
          case 2:
            pErrors.rejectValue("employeeId", "Required Field", businessCertificate.getStaffTypeName()+" Must have ONLY NUMERIC Values after "+businessCertificate.getEmpIdStartVal());
            return;
        }
        if(configurationBean.isNinRequired()){
           if(!allNumeric(wEHB.getNin().trim())) {
             pErrors.rejectValue("nin", " ID Not Valid", "NIN must be All Numeric i.e., [0-9] ");
             return;
           }
           if(wEHB.getNin().trim().length() != 11) {
             pErrors.rejectValue("nin", " ID Not Valid", "NIN length must be 11 ");
             return;
           }
         if(pErrors.getErrorCount() == 0) {
           AbstractEmployeeEntity e = (AbstractEmployeeEntity) genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(businessCertificate), CustomPredicate.procurePredicate("nin", wEHB.getNin().trim()));
           if (!e.isNewEntity()) {
             pErrors.rejectValue("nin", "nin non unique", businessCertificate.getStaffTypeName() + " " + e.getDisplayName() + " has the same 'NIN', NIN must be unique by "+businessCertificate.getStaffTypeName());
           }

         }
        }
//        if (!IppmsUtils.validateEmpId(businessCertificate, wEHB.getEmployeeId())) {
//
//          pErrors.rejectValue("employeeId", " ID Not Valid", "Invalid Value for " + businessCertificate.getStaffTitle());
//          pErrors.rejectValue("employeeId","Invalid.Value", businessCertificate.getStaffTitle()+" Must start with "+businessCertificate.getEmpIdStartVal());
//          return;
//        }
        if(businessCertificate.isLocalGovt()){
          AbstractEmployeeEntity e = (AbstractEmployeeEntity) genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(businessCertificate), CustomPredicate.procurePredicate("fileNo", wEHB.getFileNo()));
          if (!e.isNewEntity()) {
            pErrors.rejectValue("fileNo", "File No. non unique", businessCertificate.getStaffTypeName() + " " + e.getFirstName() + " " + e.getLastName() + " has the same 'File No.'");
          }
        }


         AbstractEmployeeEntity e = (AbstractEmployeeEntity) genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(businessCertificate), CustomPredicate.procurePredicate("employeeId", wEHB.getEmployeeId()));

        if (!e.isNewEntity()) {
          pErrors.rejectValue("employeeId", "Employee ID non unique", businessCertificate.getStaffTypeName() + " " + e.getFirstName() + " " + e.getLastName() + " has the same " + businessCertificate.getStaffTitle());
        }
      }


     
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(EmployeeHrBean.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fileNo", "Required Field", "File No. is Required.");
  }
}