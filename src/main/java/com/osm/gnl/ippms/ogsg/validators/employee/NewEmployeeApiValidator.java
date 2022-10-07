package com.osm.gnl.ippms.ogsg.validators.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeHrBean;
import com.osm.gnl.ippms.ogsg.domain.employee.BiometricInfo;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Arrays;

@Component
public class NewEmployeeApiValidator extends BaseValidator {

    @Autowired
    public NewEmployeeApiValidator(GenericService genericService) {
        super(genericService);
    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {
        BiometricInfo wEHB = (BiometricInfo) pTarget;

        BiometricInfo _b =  this.genericService.loadObjectUsingRestriction(BiometricInfo.class, Arrays.asList(CustomPredicate.procurePredicate("bioId",wEHB.getBioId())));
//
//        BiometricInfo _c =  this.genericService.loadObjectUsingRestriction(BiometricInfo.class, Arrays.asList(CustomPredicate.procurePredicate("bvnNumber",wEHB.getBvnNumber())));
//
//        BiometricInfo _d =  this.genericService.loadObjectUsingRestriction(BiometricInfo.class, Arrays.asList(CustomPredicate.procurePredicate("phoneNumber",wEHB.getPhoneNumber())));
//

        if(!_b.isNewEntity()){
            //Now check if the OGNumbers Match....
            if(!_b.getEmployeeId().equalsIgnoreCase(wEHB.getEmployeeId())) {
                pErrors.rejectValue("employeeId", "Biometric ID Exist", "Biometric ID Already Exist For " + businessCertificate.getStaffTitle());
                return;
            }else{
                ( (BiometricInfo)pTarget).setId(_b.getId());
            }
        }
        //if we get here...do another test...
        if(wEHB.getEmployeeId() != null) {
            Employee employee = this.genericService.loadObjectWithSingleCondition(Employee.class, CustomPredicate.procurePredicate("employeeId", wEHB.getEmployeeId().toUpperCase()));
            if (!employee.isNewEntity()) {
                pErrors.rejectValue("employeeId", "Biometric ID Exist", "The "+businessCertificate.getStaffTitle()+" " + wEHB.getEmployeeId() + " already exists for " + employee.getDisplayNameWivTitlePrefixed() + " of " + employee.getMdaDeptMap().getMdaInfo().getName());
                return;
            }
        }
//        else if(IppmsUtils.isNotNull(_c)){
//            pErrors.rejectValue("employeeId", "BVN Exist", "Bvn Already Exist For " + businessCertificate.getStaffTitle());
//            return;
//        }
//        else if(IppmsUtils.isNotNull(_d)){
//            pErrors.rejectValue("employeeId", "Phone Number Exist", "Phone Number Already Exist For " + businessCertificate.getStaffTitle());
//            return;
//        }


    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EmployeeHrBean.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
