package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class MDAValidator extends BaseValidator {

    public MDAValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MdaInfo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "name", "Required Field", businessCertificate.getMdaTitle() + " Name is required");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "description", "Required Field", "Description is required");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "codeName", "Required Field", businessCertificate.getMdaTitle() + " Code is required");

        MdaInfo wObj = (MdaInfo) pTarget;
        if (pErrors.getErrorCount() < 1) {

            if (!IppmsUtils.isNotNullAndGreaterThanZero(wObj.getMdaType().getId())) {
                pErrors.rejectValue("mdaType.id", "Required.Value", "Please select a value for " + businessCertificate.getMdaTitle() + " Type for this " + businessCertificate.getMdaTitle());
                return;
            }

            List<MdaInfo> wMdaList = this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()), null);
            if (!wObj.isEditMode()) {

                for (MdaInfo p : wMdaList) {
                    if (p.getName().trim().equalsIgnoreCase(wObj.getName().trim())) {
                        pErrors.rejectValue("name", "name.duplicate", "This " + businessCertificate.getMdaTitle() + " has already been defined");

                        return;
                    }
                    if (p.getCodeName().trim().equalsIgnoreCase(wObj.getCodeName().trim())) {
                        pErrors.rejectValue("codeName", "name.duplicate",
                                "The Code Name value exists for a different " + businessCertificate.getMdaTitle());

                        return;
                    }

                }

            } else {

                for (MdaInfo p : wMdaList) {
                    if ((p.getName().trim().equalsIgnoreCase(wObj.getName().trim()))
                            && (!p.getId().equals(wObj.getId()))) {
                        pErrors.rejectValue("name", "name.duplicate", "This " + businessCertificate.getMdaTitle() + " has already been defined");

                        return;
                    }
                    if ((p.getCodeName().trim().equalsIgnoreCase(wObj.getCodeName().trim()))
                            && (!p.getId().equals(wObj.getId()))) {
                        pErrors.rejectValue("codeName", "name.duplicate",
                                "The Code Name '"+wObj.getCodeName().trim()+"' exists for a different " + businessCertificate.getMdaTitle());

                        return;
                    }

                }
            }
            if(IppmsUtils.isNotNullOrEmpty(wObj.getEmailAddress())){
                  MdaInfo mdaInfo = this.genericService.loadObjectWithSingleCondition(MdaInfo.class, CustomPredicate.procurePredicate("emailAddress", wObj.getEmailAddress(), Operation.STRING_EQUALS));
                  if(!mdaInfo.isNewEntity()){
                      if(!mdaInfo.getId().equals(wObj.getId())){
                          pErrors.rejectValue("codeName", "name.duplicate",
                                  "The Email Address '"+wObj.getEmailAddress().trim()+"' exists for a different " + businessCertificate.getMdaTitle()+ " [ "+mdaInfo.getName() +" ] ");
                      return;
                      }
                  }
                AbstractEmployeeEntity abstractEmployeeEntity = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(businessCertificate),
                        Arrays.asList(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()), CustomPredicate.procurePredicate("email",wObj.getEmailAddress(),Operation.STRING_EQUALS)));
                  if(!abstractEmployeeEntity.isNewEntity())
                      pErrors.rejectValue("codeName", "name.duplicate",
                              "The Email Address '"+wObj.getEmailAddress().trim()+"' exists for  " + businessCertificate.getStaffTypeName()+ " [ "+abstractEmployeeEntity.getDisplayName() +" ] ");
            }
        }
    }

}