package com.osm.gnl.ippms.ogsg.validators.simulation;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class SimulationStep3Validator extends BaseValidator
{

    private final HRService hrService;
    @Autowired
    protected SimulationStep3Validator(GenericService genericService, HRService hrService) {
        super(genericService);
        this.hrService = hrService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate bc) throws Exception
  {
    SimulationBeanHolder wSBH = (SimulationBeanHolder)pTarget;

    if (IppmsUtils.isNullOrLessThanOne(wSBH.getMdaInstId()) && (StringUtils.trimToEmpty(wSBH.getApplyToAllInd()).equals(""))) {
      pErrors.rejectValue("objectCode", "Invalid.Selection", "Please select a "+bc.getStaffTypeName()+" to apply Leave Transport Grant");
    }else {
    	if(!this.hrService.mdaMappingHasActiveEmployees(wSBH.getMdaInstId(),bc)) {
  		  MdaInfo wMdaInfo = genericService.loadObjectById(MdaInfo.class, wSBH.getMdaInstId());
  		   
  		  pErrors.rejectValue("", "Invalid.Value", "No Active "+bc.getStaffTypeName()+" currently in "+wMdaInfo.getName());
  		  
  	  }
    }
    if (wSBH.getAssignToMonthInd() == -1)
      pErrors.rejectValue("assignToMonthInd", "Invalid.Selection", "Please select a Month to apply Leave Transport Grant");
  }
}