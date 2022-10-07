package com.osm.gnl.ippms.ogsg.validators.pension;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;


@Component
public class GratuityPaymentValidator extends BaseValidator {

	@Autowired
	protected GratuityPaymentValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return GratuityMasterBean.class.isAssignableFrom(clazz);
	}

	public void validate(Object pObject, Errors pErrors)
	{
		
		GratuityMasterBean wGMB = (GratuityMasterBean)pObject;
		List<NamedEntity> wList = wGMB.getGratuityList();
		for(NamedEntity n : wList){
			if(Boolean.valueOf(n.getPayGratuity())){
				double wPayPercentage = 0.0D;
				try{
					wPayPercentage = Double.parseDouble(PayrollHRUtils.removeCommas(n.getPayPercentageStr()));
				}catch(Exception wEx){
					pErrors.rejectValue("", "Invalid.Value", "Pay Percentage Value for '"+n.getName()+"' is not a valid value. Enter value between 1-100");
					continue;
				}
				//Now check if this value is 0.00...
				if(wPayPercentage <= 0.0D){
					pErrors.rejectValue("", "Invalid.Value", "Pay Percentage Value for '"+n.getName()+"' is not a valid value. Enter value between 1-100");
					continue;
				}else if(wPayPercentage > 100){
					pErrors.rejectValue("", "Invalid.Value", "Pay Percentage Value for '"+n.getName()+"' can not be greater than 100%");
					continue;
				}
				
			}
		}
	}

}
