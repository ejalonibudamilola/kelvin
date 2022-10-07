/*
 * Copyright (c) 2020. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BusinessCertificateCreator;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;

@Component
public class PaymentMethodInfoValidator extends BaseValidator {

    @Autowired
    public PaymentMethodInfoValidator(GenericService genericService) {
        super(genericService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PaymentMethodInfo.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        errors.rejectValue("bankId", "Invalid Value", "Bank Name is required for Direct Deposit.");
    }

    @SneakyThrows
    public void validate(Object pTarget, Errors pErrors, BusinessCertificate bc) {
        PaymentMethodInfo pInfo = (PaymentMethodInfo) pTarget;


            if (pInfo.getDirectDepositBankId() < 1) {
                pErrors.rejectValue("bankId", "Invalid Value", "Bank Name is required for Direct Deposit.");
            }
            if (IppmsUtils.isNullOrLessThanOne(pInfo.getBankBranches().getId())) {
                pErrors.rejectValue("directDepositBankId", "Invalid Value", "Bank Branch is required for Direct Deposit.");
            }
            if (IppmsUtils.isNullOrEmpty(pInfo.getBvnNo())) {
                pErrors.rejectValue("bvnNo", "Required Value", "BVN (Bank Verification Number) is required");
            } else {
                if (IppmsUtils.treatNull(pInfo.getBvnNo()).trim().length() != IConstants.CURRENT_BVN_LENGTH)
                    pErrors.rejectValue("bvnNo", "Required Value", "BVN (Bank Verification Number) MUST be 11 digits long.");
                else if (IppmsUtils.treatNull(pInfo.getBvnNo()).trim().length() == IConstants.CURRENT_BVN_LENGTH) {
                    if (!allNumeric(IppmsUtils.treatNull(pInfo.getBvnNo()).trim())) {
                        pErrors.rejectValue("bvnNo", "Required Value", "BVN (Bank Verification Number) MUST consist of all Numbers [0-9].");
                    }
                }
            }
            if (IppmsUtils.treatNull(pInfo.getAccountNumber()).trim().equals("")) {
                pErrors.rejectValue("accountNumber", "Required Value", "Account Number is required");
            } else {

                BankInfo bankInfo = genericService.loadObjectById(BankInfo.class,pInfo.getDirectDepositBankId());
                boolean checkLengthAndNumeric = !bankInfo.isMicroFinanceBank();

                if (IppmsUtils.treatNull(pInfo.getAccountNumber2()).trim().equals("")) {
                    pErrors.rejectValue("accountNumber", "Required Value", "Please Confirm Account Number");
                } else if (!allNumeric(pInfo.getAccountNumber()) && checkLengthAndNumeric) {
                   pErrors.rejectValue("accountNumber", "Invalid Value", "Account Number must be Numeric");
                } else if (!pInfo.getAccountNumber().equalsIgnoreCase(pInfo.getAccountNumber2())) {
                    pErrors.rejectValue("accountNumber", "Required Value", "Account Number does not match Confirm Account Number");
                }

                if (pErrors.getErrorCount() < 1) {
                    // PaymentMethodInfo pMI = pPayrollService.findPaymentMethodInfoByAccountAndBranchId(pInfo.getBankId(), pInfo.getAccountNumber());
                    PaymentMethodInfo pMI = genericService.loadObjectWithSingleCondition(PaymentMethodInfo.class, CustomPredicate.procurePredicate("accountNumber", pInfo.getAccountNumber()));

                    if (!pMI.isNewEntity()) {

                        if (!pMI.getParentId().equals(pInfo.getParentId())) {

                            if (pMI.isPensioner() && !pInfo.isPensioner()) {

                                pErrors.rejectValue("accountNumber", "Duplicate", "Pensioner " + pMI.getParentObject().getDisplayNameWivTitlePrefixed() + " has the same Account number '" + pInfo.getAccountNumber() + "'. Account Numbers MUST be unique");
                                return;
                            } else {
                                //-- Find out if Staff is a Political Office Holder.
                                if(pMI.getParentObject().getEmployeeType().isPoliticalOfficeHolderType()) {
                                    Object obj = checkForPoliticalOfficeHolderReinstatement(pMI.getParentObject(), pInfo, bc);
                                    if (obj.getClass().isAssignableFrom(Boolean.class)) {
                                        if (obj.equals(false)) {
                                            pErrors.rejectValue("accountNumber", "Duplicate", bc.getStaffTypeName()+" " + pMI.getParentObject().getDisplayNameWivTitlePrefixed() + " has the same Account number '" + pInfo.getAccountNumber() + "'. Account Numbers are unique per "+bc.getStaffTypeName());
                                            if (pInfo.isNeedsOgNumber())
                                                pInfo.setNeedsOgNumber(false);
                                            return;
                                        }
                                    } else {

                                        if (pInfo.getInheritOgNumber() != null) {
                                            //now check if this is True returned.
                                            if (obj.getClass().isAssignableFrom(Boolean.class))
                                                return; //DO NOTHING.
                                        } else {
                                            if (!((PaymentMethodInfo) pTarget).isConfirmation()) {
                                                ((PaymentMethodInfo) pTarget).setConfirmation(true);
                                                ((PaymentMethodInfo) pTarget).setNeedsOgNumber(true);
                                                pErrors.rejectValue("accountNumber", "Duplicate", obj.toString());
                                                return;
                                            }

                                        }

                                    }
                                }else{
                                    //There might be inter-service transfer....


                                    //Now check if the Parent is in a different Organization and it is Retired in that instance....
                                    AbstractEmployeeEntity a = pMI.getParentObject();
                                    if(a.getBusinessClientId().equals(bc.getBusinessClientInstId())) {

                                        pErrors.rejectValue("accountNumber", "Duplicate", bc.getStaffTypeName() + " " + pMI.getParentObject().getDisplayNameWivTitlePrefixed() + " has the same Account number '" + pInfo.getAccountNumber() + "'. Account Numbers MUST be unique");
                                        return;
                                    }
                                    if(!a.isTerminated()){
                                        BusinessCertificate bizCert = BusinessCertificateCreator.makeBusinessClient(this.genericService.loadObjectById(BusinessClient.class,a.getBusinessClientId()));
                                        pErrors.rejectValue("accountNumber", "Duplicate", bizCert.getStaffTypeName() + " " + a.getDisplayNameWivTitlePrefixed() + " of "+bizCert.getOrgName(bizCert.getBusinessClientInstId())+" has the same Account number '" + pInfo.getAccountNumber() + "'.");
                                        pErrors.rejectValue("accountNumber", "Duplicate", "If this is an Inter-Service Transfer, such "+bizCert.getStaffTypeName()+" Needs to be Terminated. Payment Method Information Setup Denied.");
                                        return;
                                    }else{
                                        if(!(pInfo.isNeedsOgNumber())) {
                                            BusinessClient abc = this.genericService.loadObjectById(BusinessClient.class, a.getBusinessClientId());
                                            BusinessCertificate businessCertificate = BusinessCertificateCreator.makeBusinessClient(abc);
                                            if (!((PaymentMethodInfo) pTarget).isConfirmation()) {
                                                ((PaymentMethodInfo) pTarget).setConfirmation(true);
                                                ((PaymentMethodInfo) pTarget).setNeedsOgNumber(true);
                                                pErrors.rejectValue("accountNumber", "Duplicate", "Another " + businessCertificate.getStaffTypeName() + " " + a.getDisplayName() + "[ " + a.getEmployeeId() + " ] Exists with the Same Account Number & BVN  - Enter a valid " + businessCertificate.getStaffTitle() + " for the terminated " + bc.getStaffTypeName() + " to adopt the Bank Information");

                                                return;
                                            }
                                        }else{
                                            return;
                                        }
                                    }
                                }
                            }
                            if(pInfo.isNeedsOgNumber())
                                pInfo.setNeedsOgNumber(false);

                        }
                    }
                    if(!((PaymentMethodInfo)pTarget).isConfirmation()) {
                        pMI = genericService.loadObjectWithSingleCondition(PaymentMethodInfo.class, CustomPredicate.procurePredicate("bvnNo", pInfo.getBvnNo().trim()));

                        if (!pMI.isNewEntity()) {
                            if(bc.isPensioner()){

                                AbstractEmployeeEntity abstractEmployeeEntity = (AbstractEmployeeEntity) genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), pInfo.getParentId());

                                if (!pMI.getBusinessClientId().equals(abstractEmployeeEntity.getBusinessClientId())){
                                    if(!pMI.getParentObject().isTerminated()){
                                        pErrors.rejectValue("accountNumber", "Duplicate", bc.getStaffTypeName()+" " + pMI.getParentObject().getDisplayNameWivTitlePrefixed() + " has the same BVN '" + pInfo.getBvnNo() + "'. BVN MUST be unique per "+bc.getStaffTypeName());
                                        pErrors.rejectValue("accountNumber", "Duplicate", pMI.getParentObject().getDisplayNameWivTitlePrefixed() + " [" + pMI.getParentObject().getEmployeeId() + "] is not terminated. Termination is required for this BVN to be accepted.");
                                        return;
                                    }
                                }else if(pMI.getBusinessClientId().equals(abstractEmployeeEntity.getBusinessClientId())){
                                    if(!pMI.getParentObject().getId().equals(pInfo.getParentId())){
                                        pErrors.rejectValue("accountNumber", "Duplicate", bc.getStaffTypeName()+" " + pMI.getParentObject().getDisplayNameWivTitlePrefixed() + " has the same BVN '" + pInfo.getBvnNo() + "'. BVN MUST be unique per "+bc.getStaffTypeName()+" and Account Number");
                                        return;
                                    }
                                }
                            }else { //This is for active
                                if (!pMI.getBusinessClientId().equals(bc.getBusinessClientInstId())) {
                                    pErrors.rejectValue("accountNumber", "Duplicate", bc.getStaffTypeName()+" " + pMI.getParentObject().getDisplayNameWivTitlePrefixed() + " has the same BVN '" + pInfo.getBvnNo() + "'. BVN MUST be unique per "+bc.getStaffTypeName()+" and Account Number");

                                    return;

                                }else {
                                    if(!pMI.getParentObject().getId().equals(pInfo.getParentId())){
                                        pErrors.rejectValue("accountNumber", "Duplicate", bc.getStaffTypeName()+" " + pMI.getParentObject().getDisplayNameWivTitlePrefixed() + " has the same BVN '" + pInfo.getBvnNo() + "'. BVN MUST be unique per "+bc.getStaffTypeName()+" and Account Number");

                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

            }


    }

    private Object checkForPoliticalOfficeHolderReinstatement(AbstractEmployeeEntity employee, PaymentMethodInfo pInfo, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {

        if(!employee.isTerminated() &&  !employee.getEmployeeType().isPoliticalOfficeHolderType())
            return false;
        AbstractEmployeeEntity newEmployee = (AbstractEmployeeEntity) genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), pInfo.getParentId());

        if(!newEmployee.getEmployeeType().isPoliticalOfficeHolderType())
            return false;

        //Now Check the BVN values....
        List<PaymentMethodInfo> pMI =genericService.loadAllObjectsWithSingleCondition(PaymentMethodInfo.class, CustomPredicate.procurePredicate("bvnNo", pInfo.getBvnNo().trim()), null);
        if(!pMI.isEmpty()) {
            //This means the BVN is the same as well. Now require Entry of OGNumber.
            //--Check if their is an inherited OG Number and Ignore.
            if(pInfo.getInheritOgNumber() != null) {
                return employee.getEmployeeId().equalsIgnoreCase(pInfo.getInheritOgNumber());
            }
            return "Another "+bc.getStaffTypeName()+" "+employee.getDisplayName()+" Exists with the Same Account Number & BVN  - Enter a valid "+bc.getStaffTitle()+" for the terminated "+bc.getStaffTypeName()+" to adopt the Bank Information";

        }else {
             return true;
        }

    }


}