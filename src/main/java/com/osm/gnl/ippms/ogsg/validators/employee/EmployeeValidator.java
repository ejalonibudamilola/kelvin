package com.osm.gnl.ippms.ogsg.validators.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;


@Component
public class EmployeeValidator extends BaseValidator {

    @Autowired
    public EmployeeValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Employee.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

    }

    /**
     * @param pTarget
     * @param pErrors
     * @param businessCertificate - Will Help when we integrate the other 4 solutions
     */
    public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate, ConfigurationBean configurationBean) throws IllegalAccessException, InstantiationException {

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "firstName", "Required Field", "First Name is a required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "lastName", "Required Field", "Last Name is a required field");
//        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "gsmNumber", "Required Field", "GSM Number is a required field");
        if(!businessCertificate.isPensioner()) {
            if (configurationBean.isStaffEmailRequired())
                ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "email", "Required Field", "E-Mail is a required field");
            if (configurationBean.isResidenceIdRequired())
                ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "residenceId", "Required Field", "Residence ID is required");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "address1", "Required Field", "Address is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "cityId", "Required Field", "Please select a valid Residence City");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "stateOfOriginId", "Required Field", "Please select a valid State Of Origin");
        if (businessCertificate.isLocalGovt())
            ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "fileNo", "Required Field", "File No is a required field");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "employeeId", "Required Field", businessCertificate.getStaffTitle() + " is required");
        boolean residenceIdSet = false;
        if (pErrors.getErrorCount() < 1) {
            AbstractEmployeeEntity emp = (AbstractEmployeeEntity) pTarget;
            if(IppmsUtils.isNotNullOrEmpty(emp.getResidenceId())){
                if(emp.getResidenceId().length() > 9){
                    pErrors.rejectValue("residenceId", "Required Field", "Residence ID should be less than 10 Characters.");
                }else{
                    residenceIdSet = true;
                }
            }
            if (emp.getTitleId() < 1) {
                pErrors.rejectValue("titleId", "Required Field", "Please select a value 'Title'");

            }
            if (emp.getRelId() < 1) {
                pErrors.rejectValue("relId", "Required Field", "Please select a value for 'Religion'");

            }
            if (emp.getStateOfOriginId() <= 0)
                pErrors.rejectValue("stateInstId", "Required Field", "Please select " + businessCertificate.getStaffTypeName() + " State Of Origin");

            if (emp.getLgaId() < 1)
                pErrors.rejectValue("lgaId", "Required Field", "Local Government Area is a required field");


            if (emp.getEmployeeType().getId() < 1)
                pErrors.rejectValue("", "Required Field", "Please select " + businessCertificate.getStaffTypeName() + " Type");


            if (emp.getCityId() <= 0)
                pErrors.rejectValue("city", "Required Field", "Please select " + businessCertificate.getStaffTypeName() + " Residence City");


            if (emp.getStateInstId() <= 0)
                pErrors.rejectValue("stateInstId", "Required Field", "Please select " + businessCertificate.getStaffTypeName() + " Residence State");

            if(!businessCertificate.isPensioner()) {
                if (configurationBean.isStaffEmailRequired())
                    if (emp.getEmail().indexOf("@") == -1)
                        pErrors.rejectValue("email", "Required Field", "Email value entered is invalid.");
            }
            if(IppmsUtils.isNotNullOrEmpty(emp.getGsmNumber())) {
                if (!allNumeric(emp.getGsmNumber())) {
                    pErrors.rejectValue("gsmNumber", "Required Field", "GSM Number should be all numbers only.");

                } else {
                    if (emp.getGsmNumber().length() != 11) {
                        pErrors.rejectValue("gsmNumber", "Required Field", "GSM Number Must be of the format (0###########).0 + 10 numbers.");

                    } else {
                        //Make sure no other employee has the same GSM Number.
                        AbstractEmployeeEntity entity = (AbstractEmployeeEntity) genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(businessCertificate), CustomPredicate.procurePredicate("gsmNumber", emp.getGsmNumber()));
                        if (!entity.isNewEntity() && emp.isNewEntity()) {
                            pErrors.rejectValue("gsmNumber", "Required Field", businessCertificate.getStaffTypeName() + " " + entity.getDisplayNameWivTitlePrefixed() + " Has the same GSM Number. GSM Number must be unique per " + businessCertificate.getStaffTypeName());

                        } else if (!entity.isNewEntity() && !emp.isNewEntity() && !entity.getId().equals(emp.getId())) {
                            pErrors.rejectValue("gsmNumber", "Required Field", businessCertificate.getStaffTypeName() + " " + entity.getDisplayNameWivTitlePrefixed() + " Has the same GSM Number. GSM Number must be unique per " + businessCertificate.getStaffTypeName());

                        }
                    }
                }

            }
            if (businessCertificate.isLocalGovt()) {
                AbstractEmployeeEntity e = (AbstractEmployeeEntity) genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(businessCertificate), CustomPredicate.procurePredicate("fileNo", emp.getFileNo()));
                if (!emp.isNewEntity() && !e.isNewEntity() && !e.getId().equals(emp.getId())) {
                    pErrors.rejectValue("fileNo", "File No non-unique", businessCertificate.getStaffTypeName()+" " + e.getDisplayNameWivTitlePrefixed() + " has the same LGC File Number");

                } else {
                    //Make sure the file No is not more than 20 Characters in length.
                    if (emp.getFileNo().length() > 20)
                        pErrors.rejectValue("fileNo", "File No non-unique", " LGC File Number should not be greater than 20 Characters.");

                }

            }
            if(businessCertificate.isPensioner() && emp.isNewEntity()){
                if(emp.getMapId() == 0){
                    pErrors.rejectValue("mapId", "Required Field", " Please Select a value for 'Pensioner Entrant Status'");
                }
                if(emp.getObjectInd() == 0){
                    pErrors.rejectValue("objectInd", "Required Field", " Please Select a value for 'Entry Month'");
                }
            }


            AbstractEmployeeEntity e = (AbstractEmployeeEntity) genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(businessCertificate), CustomPredicate.procurePredicate("employeeId", emp.getEmployeeId()));

            if (!emp.isNewEntity()) {
                if ((!e.isNewEntity()) && (!e.getId().equals(emp.getId()))) {
                    pErrors.rejectValue("employeeId", businessCertificate.getStaffTypeName() + " non unique", businessCertificate.getStaffTypeName() + " " + e.getDisplayNameWivTitlePrefixed() + " has the same " + businessCertificate.getStaffTitle());
                }
            } else {
                if (!e.isNewEntity()) {
                    pErrors.rejectValue("employeeId", businessCertificate.getStaffTypeName() + " non unique", businessCertificate.getStaffTypeName() + " " + e.getDisplayNameWivTitlePrefixed() + " has the same " + businessCertificate.getStaffTitle());

                }

            }
            if(configurationBean.isResidenceIdRequired() && residenceIdSet){

                  e = (AbstractEmployeeEntity) genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(businessCertificate),CustomPredicate.procurePredicate("residenceId",emp.getResidenceId()));
                if(!e.isNewEntity()){
                    if(emp.isNewEntity()){
                        pErrors.rejectValue("residenceId", businessCertificate.getStaffTypeName() + " non unique", businessCertificate.getStaffTypeName() + " " + e.getDisplayNameWivTitlePrefixed() + " has the same Residence ID" );

                    }else if(!emp.isNewEntity() && !emp.getId().equals(e.getId())){
                        pErrors.rejectValue("residenceId", businessCertificate.getStaffTypeName() + " non unique", businessCertificate.getStaffTypeName() + " " + e.getDisplayNameWivTitlePrefixed() + " has the same Residence ID" );

                    }
                }
            }

        }
    }


}