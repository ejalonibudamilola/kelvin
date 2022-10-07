package com.osm.gnl.ippms.ogsg.validators.approval;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.notifications.NotificationObject;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class CreateApprovalUpdateValidator extends BaseController {

    @Autowired
    public CreateApprovalUpdateValidator(GenericService genericService) {
        this.genericService = genericService;
    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws InstantiationException, IllegalAccessException {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "response", "Required.Value", "Reply can not be empty");

        if(pErrors.getErrorCount() == 0){
            if(((NotificationObject)pTarget).getResponse().length() < 2){
                pErrors.rejectValue("response","","The Length of your reply is too short.");
                return;
            }
        }
    }
}
