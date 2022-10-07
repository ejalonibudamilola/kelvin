package com.osm.gnl.ippms.ogsg.validators.hiringinfo;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.time.LocalDate;


@Component
public class HiringValidator extends BaseValidator {


    private LocalDate today;

    private LocalDate confirmDate;
    private LocalDate lastPromoDate;

    @Autowired
    public HiringValidator(GenericService genericService ) {
        super(genericService);
     }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate pBc) throws InstantiationException, IllegalAccessException {
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "birthDate", "Required Field", "Birth Date is a required field");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "hireDate", "Required Field", "Hire Date is a required field");
        if (pBc.isPensioner()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "terminateDate", "Required Field", "Retirement Date is a required field");
            ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "pensionStartDate", "Required Field", "Pension Start Date is a required field");
        }

        if (pErrors.getErrorCount() < 1) {


            HiringInfo h = (HiringInfo) pTarget;

            if(IppmsUtils.isNotNullOrEmpty(h.getTin())){
                if(!allNumeric(h.getTin().trim())){
                    pErrors.rejectValue("tin", "Required.Value", "TIN MUST be all Numeric");
                }else{
                    if(h.getTin().trim().length() != IConstants.TIN_LENGTH){
                        pErrors.rejectValue("employeeStaffType.id", "Required.Value", "TIN MUST be "+IConstants.TIN_LENGTH +" in Length");
                    }else{
                        HiringInfo _h = this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate("tin",h.getTin().trim()));
                        if(h.isNewEntity() && !_h.isNewEntity()){
                            pErrors.rejectValue("employeeStaffType.id", "Required.Value", pBc.getStaffTypeName()+_h.getEmployee().getDisplayName()+" Already has this TIN. TIN is unique per "+ pBc.getStaffTypeName());

                        }else if(!h.isNewEntity() && !_h.isNewEntity())
                        {
                            if(!h.getId().equals(_h.getId()))
                                pErrors.rejectValue("employeeStaffType.id", "Required.Value", pBc.getStaffTypeName()+_h.getEmployee().getDisplayName()+" Already has this TIN. TIN is unique per "+ pBc.getStaffTypeName());

                        }
                    }

                }
            }
            if (pBc.isSubeb()) {
                if (IppmsUtils.isNullOrLessThanOne(h.getStaffTypeId())) {
                    pErrors.rejectValue("staffTypeId", "Required.Value", "Please select a value for 'SUBEB Staff Type'");
                }

            }
            if (h.getMaritalStatus().getId() == 0L) {
                pErrors.rejectValue("maritalStatus.id", "Required.Value", "Please select a value for 'Marital Status'");
            }

            if (h.getPensionableInd() == -1 && !pBc.isPensioner()) {
                if (!h.isContractStaff()) {
                    pErrors.rejectValue("pensionableInd", "Required.Value",
                            "Please select a value for 'Pensionable " + pBc.getStaffTypeName() + "'");
                    return;
                }
                ((HiringInfo) pTarget).setPensionableInd(1);
            } else if (h.getPensionableInd() == -1 && pBc.isPensioner()) {
                ((HiringInfo) pTarget).setPensionableInd(0);
            }
            if(!pBc.isPensioner()) {
                if (h.getPfaInfo() == null || h.getPfaInfo().isNewEntity()) {
                    if (h.isContractStaff() || h.isPoliticalOfficeHolderType() && !pBc.isPensioner()) {
                        ((HiringInfo) pTarget).setPfaInfo(genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("defaultInd", 1)));
                    } else {
                        pErrors.rejectValue("pfaInfo.id", "Required.Value",
                                "Please select a value for 'Pension Fund Administrator (PFA)'");
                    }

                } else {
                    PfaInfo pfaInfo = genericService.loadObjectById(PfaInfo.class, h.getPfaInfo().getId());
                    if (!pfaInfo.isDefaultPfa()) {
                        if
                        (!h.isContractStaff() && !h.isPoliticalOfficeHolderType() && !pBc.isPensioner()) {
                            ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "pensionPinCode",
                                    "Required Field",
                                    "Pension PIN Code is a required if you select an existing PFA.");
                        } else if (pBc.isPensioner()) {
                            ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "pensionPinCode",
                                    "Required Field",
                                    "Pension PIN Code is a required if you select an existing PFA.");
                        }
                    }
                }
            }else{
                ((HiringInfo) pTarget).setPfaInfo(genericService.loadObjectWithSingleCondition(PfaInfo.class,CustomPredicate.procurePredicate("defaultInd", IConstants.ON)));
            }

        }

        if (pErrors.getErrorCount() < 1) {
            HiringInfo h = (HiringInfo) pTarget;
            if (pBc.isPensioner()) {

                ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class, CustomPredicate.procurePredicate("businessClientId", pBc.getBusinessClientInstId()));
                if (h.getTerminateDate().isAfter(h.getPensionStartDate())) {
                    ((HiringInfo) pTarget).setTerminateDate(h.getPensionStartDate());
                }
                if (h.getTerminateDate().isBefore(h.getHireDate())) {
                    pErrors.rejectValue("terminateDate", "Invalid Value", "Pensioner could not have been terminated before being hired.");
                }
                if (h.getTerminateDate().getYear() - h.getHireDate().getYear() > configurationBean.getServiceLength()) {
                    pErrors.rejectValue("terminateDate", "Invalid Value", "Service Length can not be more than " + configurationBean.getServiceLength() + " years.");
                }/* else {
                    if (h.getTerminateDate().getYear() - h.getBirthDate().getYear() > configurationBean.getAgeAtRetirement()) {
                       if( h.getTerminateDate().getYear() - h.getHireDate().getYear() > configurationBean.getServiceLength())
                          pErrors.rejectValue("terminateDate", "Invalid Value", "Pensioner would have been more than " + configurationBean.getAgeAtRetirement() + " years Old.");
                    }
                }*/
            }

            if (h.isContractStaff()) {
                ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "contractStartDate", "Required Field", "Contract Start Date is a required field");
                ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "contractEndDate", "Required Field", "Contract End Date is a required field");


                if ((h.getContractStartDate() == null) || (h.getContractEndDate() == null)) {
                    pErrors.rejectValue("contractStartDate", "Action.Denied", "Contract Dates can not be empty");
                    return;
                }

                if (h.getContractStartDate().compareTo(h.getContractEndDate()) >= 0) {
                    pErrors.rejectValue("contractStartDate", "Action.Denied", "Contract Start Date must be before Contract End Date");

                    return;
                }

            }/* else if (h.getPensionPinCode() != null && !pBc.isPensioner()) {
                HiringInfo _wH = this.pensionService.loadHiringInfoByPensionPinCode(h.getPensionPinCode());
                if ((!_wH.isNewEntity()) && (!h.isNewEntity()) && !(h.getId().equals(_wH.getId()))) {

                    pErrors.rejectValue("pensionPinCode", "Action.Denied",
                            "This Pension Pin Code is already assigned to  " + pBc.getStaffTypeName() + " " +
                                    _wH.getAbstractEmployeeEntity().getDisplayName() + " [" + _wH.getEmployeeId() + " ]");

                    return;
                }


            }
*/
            if (h.isEditMode() && !h.isNewEntity() && !pBc.isPensioner()) {
                if (h.getOldBirthDate() != null) {
                    if (!pBc.isSuperAdmin()) {
                        if (!h.getOldBirthDate().equals(h.getBirthDate())) {
                            pErrors.rejectValue("birthDate", "Action.Denied", "Only a Super Admin can change " + pBc.getStaffTypeName() + "'s Birth Date");
                            return;
                        }

                    } else {

                        if (IppmsUtilsExt.pendingPaychecksExistsForEntity(genericService, pBc, h.getParentId()) && (!h.getBirthDate().equals(h.getOldBirthDate())) && !pBc.isSuperAdmin()) {

                            pErrors.rejectValue("birthDate", "Action.Denied", "Birth Date can not be changed. " + pBc.getStaffTypeName() + " has payroll information based on this value");

                            return;
                        }

                    }

                } else if ((!pBc.isSuperAdmin()) &&
                        (h.getBirthDate() != null)) {
                    pErrors.rejectValue("birthDate", "Action.Denied", "Your profile does not allow you to change Birth Date");

                    return;
                }

                if (h.getOldHireDate() != null) {
                    if (!pBc.isSuperAdmin()) {
                        if (!h.getHireDate().equals(h.getOldHireDate())) {
                            pErrors.rejectValue("hireDate", "Action.Denied", "Your profile does not allow you to change Hiring Date");

                            return;
                        }

                    } else {

                        if (IppmsUtilsExt.pendingPaychecksExistsForEntity(genericService, pBc, h.getParentId()) && (!h.getHireDate().equals(h.getOldHireDate())) && (!pBc.isSuperAdmin())) {
                            pErrors.rejectValue("hireDate", "Action.Denied", "Hire Date can not be changed. " + pBc.getStaffTypeName() + " has payroll information based to this value");

                            return;
                        }
                    }
                } else if ((!pBc.isSuperAdmin()) &&
                        (h.getHireDate() != null)) {
                    pErrors.rejectValue("hireDate", "Action.Denied", "Your profile does not allow you to alter Hiring Date");

                    return;
                }

            }

            this.today = LocalDate.now();
            LocalDate AgeAt15ForHire;
            AgeAt15ForHire = h.getBirthDate().plusYears(15L);
            LocalDate ageAt15ForBirth = today.minusYears(15L);

            if (h.getBirthDate().isAfter(this.today)) {
                pErrors.rejectValue("birthDate", "Invalid Value", pBc.getStaffTypeName() + " date of birth cannot be in the future");
            }
            if (h.getBirthDate().isAfter(ageAt15ForBirth)) {
                pErrors.rejectValue("birthDate", "Invalid Value", pBc.getStaffTypeName() + " cannot be less than 16 years of age");
            }

            if ((h.getHireDate().isAfter(this.today)) || (h.getHireDate().equals(this.today))) {
                pErrors.rejectValue("hireDate", "Invalid Value", "Hire date must be in the past.");
            }
            if (h.getHireDate().isBefore(h.getBirthDate())) {
                pErrors.rejectValue("hireDate", "Invalid Value", "Hire date must be after Birth Date");
            } else if (h.getHireDate().equals(h.getBirthDate()))
                pErrors.rejectValue("hireDate", "Invalid Value", pBc.getStaffTypeName() + " can not be hired on the day he was born");
            else if (h.getHireDate().isBefore(AgeAt15ForHire))
                pErrors.rejectValue("hireDate", "Invalid Value", pBc.getStaffTypeName() + " can not be hired before they are 16 years old");
            else if ((this.today.getYear() - h.getHireDate().getYear() >= 35) && (!h.isContractStaff() && !pBc.isPensioner())) {

                if (this.today.getMonthValue() > h.getHireDate().getMonthValue()) {
                    pErrors.rejectValue("hireDate", "Invalid Value", "Hire Date must be less than 35 years ago.");
                    return;
                } else if (this.today.getMonthValue() == h.getHireDate().getMonthValue()) {

                    if (this.today.getDayOfMonth() > h.getHireDate().getDayOfMonth()) {
                        pErrors.rejectValue("hireDate", "Invalid Value", "Hire Date must be less than 35 years ago.");
                        return;
                    }
                }

            }
            if (pErrors.getErrorCount() >= 1) return;
            if (h.getConfirmDate() != null) {

                this.confirmDate = h.getConfirmDate();
            }

            if (h.getLastPromotionDate() != null && !pBc.isPensioner()) {
                this.lastPromoDate = h.getLastPromotionDate();
                if ((h.getConfirmDate() != null) &&
                        (this.lastPromoDate.isBefore(this.confirmDate))) {
                    pErrors.rejectValue("lastPromotionDate", "Invalid Value", pBc.getStaffTypeName() + " can not be promoted before being confirmed!");
                    return;
                }

                PromotionTracker wPT = this.genericService.loadObjectWithSingleCondition(PromotionTracker.class, CustomPredicate.procurePredicate("employee.id", h.getAbstractEmployeeEntity().getId()));
                if ((wPT.isNewEntity()) && (!h.isContractStaff())) {
                    if (this.lastPromoDate.isBefore(h.getHireDate())) {
                        pErrors.rejectValue("lastPromotionDate", "Invalid Value", "Last Promotion Date should greater than Date of Hire");
                        return;
                    }
                    if (this.lastPromoDate.isBefore(h.getBirthDate())) {
                        pErrors.rejectValue("lastPromotionDate", "Invalid Value", "Last Promotion Date should after Date of Birth");
                        return;
                    }

                    wPT.setEmployee(new Employee(h.getAbstractEmployeeEntity().getId()));
                    wPT.setLastPromotionDate(h.getLastPromotionDate());
                    LocalDate wNPD = PayrollHRUtils.determineNextPromotionDate(h.getLastPromotionDate(), h.getAbstractEmployeeEntity().getSalaryInfo().getLevel());
                    wPT.setNextPromotionDate(wNPD);
                    wPT.setBusinessClientId(pBc.getBusinessClientInstId());
                    wPT.setUser(new User(pBc.getLoginId()));

                    ((HiringInfo) pTarget).setNextPromotionDate(wNPD);
                    ((HiringInfo) pTarget).setLastPromotionDateChanged(true);
                    this.genericService.saveObject(wPT);
                } else if (!h.isContractStaff()) {
                    if (this.lastPromoDate.isBefore(h.getHireDate())) {
                        pErrors.rejectValue("lastPromotionDate", "Invalid Value", "Last Promotion Date should be after Date of Hire");
                        return;
                    }
                    if (this.lastPromoDate.isBefore(h.getBirthDate())) {
                        pErrors.rejectValue("lastPromotionDate", "Invalid Value", "Last Promotion Date should be after Date of Birth");
                        return;
                    }

                    if (pBc.isSuperAdmin()) {

                        if (wPT.getLastPromotionDate().compareTo(this.lastPromoDate) != 0) {
                            wPT.setLastPromotionDate(h.getLastPromotionDate());
                            wPT.setNextPromotionDate(PayrollHRUtils.determineNextPromotionDate(h.getLastPromotionDate(), h.getAbstractEmployeeEntity().getSalaryInfo().getLevel()));
                            ((HiringInfo) pTarget).setNextPromotionDate(wPT.getNextPromotionDate());
                            ((HiringInfo) pTarget).setLastPromotionDateChanged(true);
                            this.genericService.saveObject(wPT);
                        }
                    } else {
                        h.setLastPromotionDate(wPT.getLastPromotionDate());
                    }
                }
            }
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return HiringInfo.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

    }


}