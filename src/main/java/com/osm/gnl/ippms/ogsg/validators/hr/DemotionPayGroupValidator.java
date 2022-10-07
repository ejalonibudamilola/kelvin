package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeHrBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.time.LocalDate;


@Component
public class DemotionPayGroupValidator extends BaseValidator
{
    @Autowired
    protected DemotionPayGroupValidator(GenericService genericService) {
        super(genericService);
    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws Exception
  {
    EmployeeHrBean wEHB = (EmployeeHrBean)pTarget;

      if (IppmsUtils.isNullOrLessThanOne(wEHB.getSalaryTypeId()))
    {
        pErrors.rejectValue("salaryTypeId", "Required.Value", "Please select the 'New Pay Group' for " + wEHB.getEmployee().getDisplayNameWivTitlePrefixed() + ".");
        return;
     
    }else{
    	 if(IppmsUtils.isNullOrLessThanOne(wEHB.getSalaryStructureId())){
    		 pErrors.rejectValue("salaryStructureId", "Required.Value", "Please select the 'New Level/Step' for " + wEHB.getEmployee().getDisplayNameWivTitlePrefixed() + ".");
    	        return;
    	      
    	 }
    	//Now check if this Salary Type is same as the Old one.
    	SalaryType wOST = genericService.loadObjectById(SalaryType.class, wEHB.getSalaryTypeId());
        if(wOST.getId().equals(wEHB.getEmployee().getSalaryInfo().getSalaryType().getId())){
        	//Must be demoted.
        	SalaryInfo wNSI =  genericService.loadObjectById(SalaryInfo.class, wEHB.getSalaryStructureId());
        	if(wNSI.getId().equals(wEHB.getEmployee().getSalaryInfo().getId())){
        		 pErrors.rejectValue("salaryStructureId", "Required.Value", "This module is only for 'Demotions' or 'Moving "+businessCertificate.getStaffTypeName()+" across Pay Groups'");
     	         return;
        	}else{
        		if(wNSI.getLevel() > wEHB.getEmployee().getSalaryInfo().getLevel()){
        			pErrors.rejectValue("salaryStructureId", "Required.Value", "This module is only for 'Demotions' or 'Moving "+businessCertificate.getStaffTypeName()+" across Pay Groups'");
        			pErrors.rejectValue("salaryStructureId", "Required.Value", "New Level "+wNSI.getLevel()+" must be equal or less than Current Level. " +wEHB.getEmployee().getSalaryInfo().getLevel());
        	         return;
        		}else if(wNSI.getLevel() == wEHB.getEmployee().getSalaryInfo().getLevel()
        				&& (wNSI.getStep() >= wEHB.getEmployee().getSalaryInfo().getStep())){
        			pErrors.rejectValue("salaryStructureId", "Required.Value", "This module is only for 'Demotions' or 'Moving "+businessCertificate.getStaffTypeName()+" across Pay Groups'");
        			pErrors.rejectValue("salaryStructureId", "Required.Value", "New Step "+wNSI.getStepStr()+" must be less than Current Step " +wEHB.getEmployee().getSalaryInfo().getStepStr());
        	         return;
        		}
        	}
        } 
    }

    
    if (wEHB.isConfirm()) {
      ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "refNumber", "Required Field", "Reference Number is required");
      if (wEHB.getRefDate() == null) {
        pErrors.rejectValue("refDate", "Required.Value", "Please select the Date this reassignment was approved.");
        return;
      }

      if (wEHB.getRefDate().isAfter(LocalDate.now())) {
        pErrors.rejectValue("refDate", "Required.Value", "Reference Date must be before today.");
        return;
      }
    }
  }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EmployeeHrBean.class);
    }

    @SneakyThrows
    @Override
    public void validate(Object target, Errors pErrors){
            EmployeeHrBean wEHB = (EmployeeHrBean)target;
             if (IppmsUtils.isNullOrLessThanOne(wEHB.getCadreInstId()))
              pErrors.rejectValue("cadreInstId", "Required.Value", "Please select  Cadre.");
           if (IppmsUtils.isNullOrLessThanOne(wEHB.getRankInstId())) {
               pErrors.rejectValue("rankInstId", "Required.Value", "Please select  Rank.");
                return;
            }
           if (IppmsUtils.isNullOrLessThanOne(wEHB.getSalaryStructureId())) {
              pErrors.rejectValue("salaryStructureId", "Required.Value", "Please select the 'New Level/Step' for " + wEHB.getEmployee().getDisplayNameWivTitlePrefixed() + ".");
                return;
            }
           if(IppmsUtils.isNotNullAndGreaterThanZero(wEHB.getSalaryTypeId())){
               //This is a Pay Group Change.
               //New Pay Group Must not be equal to old pay Group
               if(wEHB.getEmployee().getSalaryInfo().getSalaryType().getId().equals(wEHB.getSalaryTypeId())){
                   Rank wOR = wEHB.getEmployee().getRank();
                   Rank wNR = genericService.loadObjectById(Rank.class, wEHB.getRankInstId());
                   SalaryInfo wNSI = genericService.loadObjectById(SalaryInfo.class, wEHB.getSalaryStructureId());
                   if (wOR.getId().equals(wNR.getId())) {

                       if (wNSI.getId().equals(wEHB.getEmployee().getSalaryInfo().getId())) {
                           pErrors.rejectValue("salaryStructureId", "Required.Value", "This module is only for 'Demotions' or 'Moving Staffs across Pay Groups/Ranks and or Cadres'");
                           return;
                       }

                   }
                   if (wNSI.getLevel() > wEHB.getEmployee().getSalaryInfo().getLevel()) {
                       pErrors.rejectValue("salaryStructureId", "Required.Value", "This module is only for 'Demotions' or 'Moving Staffs across Pay Groups/Ranks and or Cadres'");
                       pErrors.rejectValue("salaryStructureId", "Required.Value", "New Level " + wNSI.getLevel() + " must be equal or less than Current Level. " + wEHB.getEmployee().getSalaryInfo().getLevel());
                       return;
                   }
                   if (wNSI.getLevel() == wEHB.getEmployee().getSalaryInfo().getLevel() && wNSI.getStep() >= wEHB.getEmployee().getSalaryInfo().getStep()) {
                       pErrors.rejectValue("salaryStructureId", "Required.Value", "This module is only for 'Demotions' or 'Moving Staffs across Pay Groups/Ranks and or Cadres'");
                       pErrors.rejectValue("salaryStructureId", "Required.Value", "New Step " + wNSI.getStepStr() + " must be less than Current Step " + wEHB.getEmployee().getSalaryInfo().getStepStr());
                       return;
                   }
               }
           }else {
               Rank wOR = wEHB.getEmployee().getRank();
               Rank wNR = genericService.loadObjectById(Rank.class, wEHB.getRankInstId());
               SalaryInfo wNSI = genericService.loadObjectById(SalaryInfo.class, wEHB.getSalaryStructureId());
               if (wOR.getId().equals(wNR.getId())) {

                   if (wNSI.getId().equals(wEHB.getEmployee().getSalaryInfo().getId())) {
                       pErrors.rejectValue("salaryStructureId", "Required.Value", "This module is only for 'Demotions' or 'Moving Staffs across Pay Groups/Ranks and or Cadres'");
                       return;
                   }

               }
               if (wNSI.getLevel() > wEHB.getEmployee().getSalaryInfo().getLevel()) {
                   pErrors.rejectValue("salaryStructureId", "Required.Value", "This module is only for 'Demotions' or 'Moving Staffs across Pay Groups/Ranks and or Cadres'");
                   pErrors.rejectValue("salaryStructureId", "Required.Value", "New Level " + wNSI.getLevel() + " must be equal or less than Current Level. " + wEHB.getEmployee().getSalaryInfo().getLevel());
                   return;
               }
               if (wNSI.getLevel() == wEHB.getEmployee().getSalaryInfo().getLevel() && wNSI.getStep() >= wEHB.getEmployee().getSalaryInfo().getStep()) {
                   pErrors.rejectValue("salaryStructureId", "Required.Value", "This module is only for 'Demotions' or 'Moving Staffs across Pay Groups/Ranks and or Cadres'");
                   pErrors.rejectValue("salaryStructureId", "Required.Value", "New Step " + wNSI.getStepStr() + " must be less than Current Step " + wEHB.getEmployee().getSalaryInfo().getStepStr());
                   return;
               }
           }
             if (wEHB.isConfirm()) {
                 ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "refNumber", "Required Field", "Reference Number is required");
                 if (wEHB.getRefDate() == null) {
                  pErrors.rejectValue("refDate", "Required.Value", "Please select the Date this reassignment was approved.");
                    return;
                }
                 if (wEHB.getRefDate().isAfter(LocalDate.now())) {
                     pErrors.rejectValue("refDate", "Required.Value", "Reference Date must be before today.");
                     return;
                 }
            }
        }
    }
