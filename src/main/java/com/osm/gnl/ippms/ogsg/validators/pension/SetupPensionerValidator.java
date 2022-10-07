package com.osm.gnl.ippms.ogsg.validators.pension;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.NewPensionerBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.List;


@Component
public class SetupPensionerValidator extends BaseValidator {

    @Autowired
    protected SetupPensionerValidator(GenericService genericService) {
        super(genericService);
    }

    public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) throws Exception {

        NewPensionerBean wEHB = (NewPensionerBean) pTarget;

        if (IppmsUtils.isNullOrEmpty(wEHB.getPensioner().getEmployeeId())) {
            pErrors.rejectValue("", "Required.Value", "Pensioner ID is required.");

        } else {
            switch (IppmsUtils.validateOgNumber(businessCertificate,wEHB.getPensioner().getEmployeeId())){
                case 1:
                    pErrors.rejectValue("employeeId", "Required Field", businessCertificate.getStaffTypeName()+" Must have "+businessCertificate.getStaffTitle()+" starting with "+businessCertificate.getEmpIdStartVal());
                    return;
                case 2:
                    pErrors.rejectValue("employeeId", "Required Field", businessCertificate.getStaffTypeName()+" Must have ONLY NUMERIC Values after "+businessCertificate.getEmpIdStartVal());
                    return;
            }
            //Check if we have the Pensioner ID set...
            Pensioner wEmp = this.genericService.loadObjectUsingRestriction(Pensioner.class, Arrays.asList(CustomPredicate.procurePredicate("employeeId", wEHB.getPensioner().getEmployeeId()),
                    CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())));
            if (wEmp != null && !wEmp.isNewEntity()) {
                pErrors.rejectValue("", "Required.Value", "Pensioner " + wEmp.getDisplayName() + " Has the same Pensioner ID. Please change value. ");
            }
        }
        ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class, CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
        if (configurationBean.isResidenceIdRequired()) {
            if (IppmsUtils.isNullOrEmpty(wEHB.getPensioner().getResidenceId())) {
                pErrors.rejectValue("", "Required.Value", "Residence ID is required.");
            } else {
                if (wEHB.getPensioner().getResidenceId().length() > 9) {
                    pErrors.rejectValue("", "Required.Value", "Residence ID must be 9 characters in length.");
                }

            }
        }else{
            //--Check if there is a value..
            if(IppmsUtils.isNotNullOrEmpty(wEHB.getPensioner().getResidenceId())){
                Pensioner pensioner = genericService.loadObjectWithSingleCondition(Pensioner.class,CustomPredicate.procurePredicate("residenceId", wEHB.getPensioner().getResidenceId().trim()));
                if(!pensioner.isNewEntity()){
                    pErrors.rejectValue("", "Required.Value", "Pensioner "+pensioner.getDisplayNameWivTitlePrefixed()+" has the same Residence ID.");
                    pErrors.rejectValue("", "Required.Value", "Residence ID MUST be unique per Pensioner.");
                }
            }
        }
        if (configurationBean.isNinRequired()) {
            if (IppmsUtils.isNullOrEmpty(wEHB.getPensioner().getNin())) {
                pErrors.rejectValue("", "Required.Value", "NIN is required.");
            } else {
                if (wEHB.getPensioner().getNin().length() < 11) {
                    pErrors.rejectValue("", "Required.Value", "NIN must be 11 characters in length.");
                }
            }

        }else{

        }

        if (IppmsUtils.isNullOrLessThanOne(wEHB.getPensionerTypeId())) {
            pErrors.rejectValue("", "Required.Value", "Pensioner Type is required.");
        }
        if (IppmsUtils.isNullOrEmpty(wEHB.getPensioner().getFirstName())) {
            pErrors.rejectValue("", "Required.Value", "Pensioner First Name is required.");

        } else {
            if (wEHB.getPensioner().getFirstName().length() < 3) {
                pErrors.rejectValue("", "Required.Value", "Pensioner First Name must be more that 2 Characters.");
            }
        }
        if (IppmsUtils.isNullOrEmpty(wEHB.getPensioner().getLastName())) {
            pErrors.rejectValue("", "Required.Value", "Pensioner Last Name is required.");

        } else {
            if (wEHB.getPensioner().getLastName().length() < 3) {
                pErrors.rejectValue("", "Required.Value", "Pensioner First Last must be more that 2 Characters.");
            }
        }
        if (IppmsUtils.isNullOrEmpty(wEHB.getPensioner().getAddress1())) {
            pErrors.rejectValue("", "Required.Value", "Pensioner Address is required.");

        }
        if (IppmsUtils.isNullOrLessThanOne(wEHB.getCityId())) {
            pErrors.rejectValue("", "Required.Value", "Please select City.");

        }
        if (IppmsUtils.isNullOrLessThanOne(wEHB.getStateId())) {
            pErrors.rejectValue("", "Required.Value", "Please select State Of Origin.");

        }
        if (IppmsUtils.isNullOrLessThanOne(wEHB.getLgaId())) {
            pErrors.rejectValue("", "Required.Value", "Please select Local Govt. Area");

        }


        if (IppmsUtils.isNullOrLessThanOne(wEHB.getPensioner().getReligion().getId())) {
            pErrors.rejectValue("", "Required.Value", "Please select Pensioner Religion");

        }
		/*if(wEHB.getSalaryTypeId() == 0){
			pErrors.rejectValue("", "Required.Value", "Please select Pensioner Pay Group");

		}
		if(wEHB.getLevelAndStepInd() == 0){
			pErrors.rejectValue("", "Required.Value", "Please select Pensioner Level & Step");

		}*/
        if (wEHB.getBankId().intValue() == 0) {
            pErrors.rejectValue("", "Required.Value", "Please select Pensioner Bank");
        }
        if (wEHB.getBankBranchId().intValue() == 0) {
            pErrors.rejectValue("", "Required.Value", "Please select Pensioner Bank Branch");
        }
        if (IppmsUtils.isNullOrEmpty(wEHB.getPaymentMethodInfo().getAccountNumber())) {
            pErrors.rejectValue("", "Required.Value", "Please enter Pensioner Account Number");
        } else {
            BankInfo bankInfo = genericService.loadObjectById(BankInfo.class, wEHB.getBankId());
            if (!bankInfo.isMicroFinanceBank()) {
                if (wEHB.getPaymentMethodInfo().getAccountNumber().length() != 10)
                    pErrors.rejectValue("", "Required.Value", "For Money Deposit Banks, Account Numbers must be 10 numbers");
                else if (!allNumeric(wEHB.getPaymentMethodInfo().getAccountNumber()))
                    pErrors.rejectValue("", "Required.Value", "For Money Deposit Banks, Account Numbers must be made up of Numbers [0-9] only.");
            }
            //Check if another Pensioner Has this Bank Account Number...

            List<PaymentMethodInfo> wPMIList = genericService.loadAllObjectsWithSingleCondition(PaymentMethodInfo.class, CustomPredicate.procurePredicate("accountNumber", wEHB.getPaymentMethodInfo().getAccountNumber()), null);
            if (IppmsUtils.isNotNullOrEmpty(wPMIList)) {
                for (PaymentMethodInfo p : wPMIList) {
                    if (p.isPensioner()) {
                        pErrors.rejectValue("", "Required.Value", "Pensioner " + p.getPensioner().getDisplayName() + " has this same Account Number " + (wEHB.getPaymentMethodInfo().getAccountNumber()));
                    } else {
                        if (!p.getId().equals(wEHB.getPaymentMethodInfo().getServicePaymentMethodInfo().getId()))
                            pErrors.rejectValue("", "Required.Value", "Employee " + p.getEmployee().getDisplayName() + " has this same Account Number " + (wEHB.getPaymentMethodInfo().getAccountNumber()));
                    }
                }

            }
        }
        if (IppmsUtils.isNullOrEmpty(wEHB.getPaymentMethodInfo().getBvnNo())) {
            pErrors.rejectValue("", "Required.Value", "Please enter Pensioner BVN");
        } else {

            List<PaymentMethodInfo> wPMIList = genericService.loadAllObjectsWithSingleCondition(PaymentMethodInfo.class, CustomPredicate.procurePredicate("bvnNo", wEHB.getPaymentMethodInfo().getBvnNo()), null);
            if (IppmsUtils.isNotNullOrEmpty(wPMIList)) {
                for (PaymentMethodInfo p : wPMIList) {
                    if (p.isPensioner()) {
                        pErrors.rejectValue("", "Required.Value", "Pensioner " + p.getPensioner().getDisplayName() + " has this same BVN " + (wEHB.getPaymentMethodInfo().getBvnNo()));
                    } else {
                        if (!p.getId().equals(wEHB.getPaymentMethodInfo().getServicePaymentMethodInfo().getId()))
                            pErrors.rejectValue("", "Required.Value", "Employee " + p.getEmployee().getDisplayName() + " has this same BVN " + (wEHB.getPaymentMethodInfo().getBvnNo()));
                    }
                }

            }
        }

        if (wEHB.getMdaId() == 0) {
            pErrors.rejectValue("", "Required.Value", "Please select " + businessCertificate.getMdaTitle() + " to assign Pensioner");
        }
        if (wEHB.getMaritalStatusId() == 0) {
            pErrors.rejectValue("", "Required.Value", "Please Pensioner's Marital Status");
        }
        if (wEHB.getHiringInfo().getPensionStartDate() == null) {
            pErrors.rejectValue("", "Required.Value", "Please Pensioner's Pension Start Date");
        } else {
            try {

                if (wEHB.getHiringInfo().getPensionStartDate().isBefore(wEHB.getHiringInfo().getTerminateDate())) {
                    pErrors.rejectValue("", "Required.Value", "Pensioner's Pension Start Date CANNOT be before Pensioner's Service Termination Date.");
                }

            } catch (Exception wEx) {
                pErrors.rejectValue("", "Required.Value", "Pensioner's Pension Start Date is not a valid date.");

            }

        }
        /*if (IppmsUtils.isNullOrLessThanOne(wEHB.getPfaId())) {
            pErrors.rejectValue("", "Required.Value", "Please select Pensioner's Pension Fund Administrator(PFA)");

        }
        if (IppmsUtils.isNullOrEmpty(wEHB.getHiringInfo().getPensionPinCode())) {
            pErrors.rejectValue("", "Required.Value", "Pension PIN Code is required.");
        } else {
            List<HiringInfo> wHireInfoList = genericService.loadAllObjectsWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate("pensionPinCode", wEHB.getHiringInfo().getPensionPinCode()), null);
            if (IppmsUtils.isNotNullOrEmpty(wHireInfoList)) {
                for (HiringInfo p : wHireInfoList) {
                    if (p.isPensionerType()) {
                        pErrors.rejectValue("", "Required.Value", "Pensioner " + p.getPensioner().getDisplayName() + " has this same Pension PIN Code " + (p.getPensionPinCode()));
                    } else {
                        if (!p.getEmployee().getId().equals(wEHB.getPensioner().getEmployee().getId()))
                            pErrors.rejectValue("", "Required.Value", "Employee " + p.getEmployee().getDisplayName() + " has this same Pension PIN Code  " + (p.getPensionPinCode()));
                    }
                }

            }
        }*/
        if (wEHB.getMapId() == 0) {
            pErrors.rejectValue("", "Required.Value", "Please select Pensioner Entrant Status");
        }
        if (wEHB.getObjectInd() == 0) {
            pErrors.rejectValue("", "Required.Value", "Please select Entry Month");
        }
        if (wEHB.getUseDefPension() == 0) {
            pErrors.rejectValue("", "Required.Value", "Please indicate whether to Apply Calculated Pension Value or not.");
        }
        if (wEHB.getUseDefGratuity() == 0) {
            pErrors.rejectValue("", "Required.Value", "Please indicate whether to Apply Calculated Gratuity Value or not.");
        }
        if (wEHB.isHasNextOfKin()) {
            if (IppmsUtils.isNullOrLessThanOne(wEHB.getRelationTypeId())) {
                pErrors.rejectValue("", "Required.Value", "Please select Pensioner's Next of Kin's Relationship");
            }
            if (IppmsUtils.isNullOrLessThanOne(wEHB.getNokStateId())) {
                pErrors.rejectValue("", "Required.Value", "Please select Pensioner's Next of Kin's State");
            }
            if (IppmsUtils.isNullOrLessThanOne(wEHB.getNokCityId())) {
                pErrors.rejectValue("", "Required.Value", "Please select Pensioner's Next of Kin's City");
            }
            if (IppmsUtils.isNotNullOrEmpty(wEHB.getNextOfKin().getGsmNumber())) {
                pErrors.rejectValue("", "Required.Value", "Please set Pensioner's Next Of Kin Primary Phone Number.");
            }
        }
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(NewPensionerBean.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
