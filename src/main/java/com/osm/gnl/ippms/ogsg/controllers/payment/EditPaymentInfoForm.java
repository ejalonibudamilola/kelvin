/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.approvals.ViewEmployeeForApprovalController;
import com.osm.gnl.ippms.ogsg.controllers.employee.EmployeeGeneralOverviewForm;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodTypes;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.payment.PaymentMethodInfoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@Controller
@RequestMapping({"/paymentInfoForm.do","/penPaymentInfoForm.do"})
@SessionAttributes(types = {PaymentMethodInfo.class})
public class EditPaymentInfoForm extends BaseController {


    private final PaymentMethodInfoValidator validator;
    private final IMenuService menuService;

    private Collection<PaymentMethodTypes> paymentMethodTypes;
    private List<BankInfo> bankInfo;
    private List<BankBranch> bankBranchesInfo;
    private final String VIEW = "payment/paymentInfoForm";


    @ModelAttribute("bankInfo")
    private List<BankInfo> makeBankList() {
        return this.genericService.loadAllObjectsWithSingleCondition(BankInfo.class, CustomPredicate.procurePredicate("selectableInd", OFF), "name");
    }
    @Autowired
    public EditPaymentInfoForm(PaymentMethodInfoValidator validator, IMenuService menuService) {
        this.validator = validator;
        this.menuService = menuService;
    }

    
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        this.paymentMethodTypes = genericService.loadAllObjectsWithoutRestrictions(PaymentMethodTypes.class, "name");

        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
        PaymentMethodInfo pMI = null;

        Long empId = ne.getId();
        if (empId > 0) {
            pMI = this.genericService.loadObjectUsingRestriction(PaymentMethodInfo.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), empId), getBusinessClientIdPredicate(request)));
            if (!pMI.isNewEntity()) {
                ne.setEditMode(true);
                if (pMI.getPaymentMethodTypes() != null & !pMI.getPaymentMethodTypes().isNewEntity()) {
                    if (pMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("DD")) {
                        pMI.setPaymentTypeRef("1");
                        pMI.setShowRow("table-row");
                        pMI.setCashCardShowRow("none");
                        pMI.setDirectDepositBankId(pMI.getBankBranches().getBankInfo().getId());
                        pMI.setBranchId(pMI.getBankBranches().getId());
                        pMI.setAccountNumber2(pMI.getAccountNumber());
                    } else if (pMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("CHQ")) {
                        pMI.setPaymentTypeRef("0");
                        pMI.setShowRow("none");
                        pMI.setCashCardShowRow("none");
                    } else if (pMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("CC")) {
                        pMI.setPaymentTypeRef("2");
                        pMI.setCashCardShowRow("table-row");
                        pMI.setShowRow("none");
                        pMI.setCashCardBankId(pMI.getBankId());
                    }
                }
            } else {
                pMI = new PaymentMethodInfo();
                pMI.setParentId(empId);
                pMI.setPaymentTypeRef("1");
                pMI.setAccountType("C");
                pMI.setShowRow("table-row");
                pMI.setCashCardShowRow("none");
                //pMI.setDirectDepositBankId(pMI.getBankId());
            }
        }


        model.addAttribute("namedEntity", ne);
        model.addAttribute("bankBranches", new ArrayList<BankBranch>());
        model.addAttribute("paymentMethodInfo", pMI);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"oid"})
    public String setupForm(@RequestParam("oid") Long parentId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        //Object userId = this.getSessionId(request);

        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService, parentId, bc);

        NamedEntity nE = new NamedEntity();
        nE.setId(emp.getId());
        nE.setName(emp.getDisplayNameWivTitlePrefixed());

        /**
         * Sometimes the Employee Creation Process is never completed.
         */
        HiringInfo hiringInfo = this.loadHiringInfoByEmpId(request,bc,emp.getId());
        if(hiringInfo.isNewEntity()){
            if(bc.isPensioner() && menuService.canUserAccessURL(bc,"penHireInfo.do","penHireInfo.do"))
                return "redirect:penHireInfo.do?oid="+nE.getId();
            else if(!bc.isPensioner() && menuService.canUserAccessURL(bc,"hiringForm.do","hiringForm.do"))
                return "redirect:hiringForm.do?oid="+nE.getId();
            else
                return "accessDeniedForm";
        }
        this.paymentMethodTypes = genericService.loadAllObjectsWithoutRestrictions(PaymentMethodTypes.class, "name");
        List<BankBranch> wBankBranchesList = new ArrayList<>();
        PaymentMethodInfo pMI = this.genericService.loadObjectUsingRestriction(PaymentMethodInfo.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), emp.getId()), getBusinessClientIdPredicate(request)));
        if (!pMI.isNewEntity()) {
            nE.setEditMode(true);
            if (pMI.getPaymentMethodTypes() != null) {
                if (pMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("DD")) {
                    pMI.setPaymentTypeRef("1");
                    pMI.setShowRow("table-row");
                    pMI.setCashCardShowRow("none");
                    if ((pMI.getBankBranches() != null) && (!pMI.getBankBranches().isNewEntity())) {
                        pMI.setBranchId(pMI.getBankBranches().getId());
                        pMI.setDirectDepositBankId(pMI.getBankBranches().getBankInfo().getId());
                        wBankBranchesList = getBankBranches(pMI.getBankBranches().getBankInfo().getId());
                    }
                    pMI.setAccountNumber2(pMI.getAccountNumber());
                } else if (pMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("CHQ")) {
                    pMI.setPaymentTypeRef("0");
                    pMI.setShowRow("none");
                    pMI.setCashCardShowRow("none");
                } else if (pMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("CC")) {
                    pMI.setPaymentTypeRef("2");
                    pMI.setShowRow("none");
                    pMI.setCashCardShowRow("table-row");
                    if ((pMI.getBankBranches() != null) && (!pMI.getBankBranches().isNewEntity()))
                        pMI.setCashCardBankId(pMI.getBankBranches().getBankInfo().getId());
                }
            }
        } else {
            pMI = new PaymentMethodInfo();
            pMI.setParentId(emp.getId());
            pMI.setPaymentTypeRef("1");
            pMI.setAccountType("C");
            pMI.setShowRow("table-row");
            pMI.setCashCardShowRow("none");

        }

        pMI.setTerminated(emp.isTerminated());
        addSessionAttribute(request, NAMED_ENTITY, nE);
        model.addAttribute(nE);
        model.addAttribute("bankBranches", wBankBranchesList);
        model.addAttribute("paymentMethodInfo", pMI);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }

    
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"oid", "bid"})
    public String setupForm(@RequestParam("oid") Long parentId, @RequestParam("bid") Long pBankId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        //Object userId = this.getSessionId(request);
        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService, parentId, bc);
        NamedEntity nE = new NamedEntity();
        nE.setId(emp.getId());
        nE.setName(emp.getDisplayNameWivTitlePrefixed());
        nE.setEditMode(true);
        addSessionAttribute(request, NAMED_ENTITY, nE);
        model.addAttribute(nE);
        this.paymentMethodTypes = genericService.loadAllObjectsWithoutRestrictions(PaymentMethodTypes.class, "name");

        List<BankBranch> wBankBranchesList = getBankBranches(pBankId);

        PaymentMethodInfo pMI = this.genericService.loadObjectUsingRestriction(PaymentMethodInfo.class, Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), emp.getId()), getBusinessClientIdPredicate(request)));

        if (!pMI.isNewEntity()) {
            if (pMI.getPaymentMethodTypes() != null) {
                if (pMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("DD")) {
                    pMI.setPaymentTypeRef("1");
                    pMI.setShowRow("table-row");
                    pMI.setCashCardShowRow("none");

                    pMI.setBankId(pBankId);
                    pMI.setDirectDepositBankId(pBankId);

                    pMI.setAccountNumber2(pMI.getAccountNumber());
                } else if (pMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("CHQ")) {
                    pMI.setPaymentTypeRef("0");
                    pMI.setShowRow("none");
                    pMI.setCashCardShowRow("none");
                } else if (pMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("CC")) {
                    pMI.setPaymentTypeRef("2");
                    pMI.setShowRow("none");
                    pMI.setCashCardShowRow("table-row");
                    if ((pMI.getBankBranches() != null) && (!pMI.getBankBranches().isNewEntity()))
                        pMI.setCashCardBankId(pMI.getBankBranches().getBankInfo().getId());
                }
            }
        } else {
            pMI = new PaymentMethodInfo();
            pMI.setParentId(parentId);
            pMI.setPaymentTypeRef("1");
            pMI.setAccountType("C");
            pMI.setShowRow("table-row");
            pMI.setCashCardShowRow("none");
            pMI.setDirectDepositBankId(pBankId);
            pMI.setBankId(pBankId);
        }
        pMI.setTerminated(emp.isTerminated());

        pMI.setBusinessClientId(bc.getBusinessClientInstId());
        model.addAttribute("namedEntity", nE);
        model.addAttribute("bankBranches", wBankBranchesList);
        model.addAttribute("paymentMethodInfo", pMI);
        model.addAttribute("roleBean", bc);
        return VIEW;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("paymentMethodInfo") PaymentMethodInfo pInfo,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        Object userId = getSessionId(request);
        NamedEntity wNE = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {

            if (wNE != null && !wNE.isEditMode()) {
                return "redirect:dedGarnForm.do";
            }

            if ((Navigator.getInstance(userId).getFromClass().isAssignableFrom(EmployeeGeneralOverviewForm.class)) &&
                    (Navigator.getInstance(userId).getFromForm() != null)) {
                Navigator.getInstance(userId).setFromClass(getClass());
                return Navigator.getInstance(userId).getFromForm();
            }else if((Navigator.getInstance(userId).getFromClass().isAssignableFrom(ViewEmployeeForApprovalController.class)) &&
                    (Navigator.getInstance(userId).getFromForm() != null)) {
                return Navigator.getInstance(userId).getFromForm();
            }

            return "redirect:dedGarnForm.do";
        }

        if (pInfo != null) {
            if (pInfo.getPaymentTypeRef().equals("0")) {
                pInfo.setPaymentMethodTypes(getPaymentMethodTypeByCode("CHQ"));
                pInfo.setAccountNumber(null);
                pInfo.setAccountNumber2(null);
                pInfo.setRoutingNumber(null);
                pInfo.setRoutingNumber2(null);
                pInfo.setAccountType(null);
                pInfo.setBankId(0L);
            } else if (pInfo.getPaymentTypeRef().equals("1")) {
                pInfo.setPaymentMethodTypes(getPaymentMethodTypeByCode("DD"));
                pInfo.setBankId(pInfo.getDirectDepositBankId());

                validator.validate(pInfo, result, bc);
                if (result.hasErrors()) {
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("namedEntity",wNE);
                    if (pInfo.getDirectDepositBankId() == 0L) {
                        model.addAttribute("bankBranches", new ArrayList<BankBranch>());
                    } else {
                        model.addAttribute("bankBranches", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", pInfo.getDirectDepositBankId()), "name"));
                    }

                    model.addAttribute("paymentMethodInfo", pInfo);
                    return VIEW;
                }
                //pInfo.setBankBranches(this.payrollService.loadObjectByClassAndId(BankBranch.class, pInfo.getBankBranches().getId()));
                pInfo.setAccountNumber2(null);
                if(pInfo.isNeedsOgNumber()) {
                    //--Check if the OG Number is Empty.
                    if(pInfo.getInheritOgNumber() == null) {
                        result.rejectValue("", "Required.Value", "A value for 'Inheriting From'");
                        addDisplayErrorsToModel(model, request);
                        model.addAttribute("status", result);
                        model.addAttribute("namedEntity",wNE);
                        model.addAttribute("paymentMethodInfo", pInfo);
                        model.addAttribute("roleBean", bc);
                        return VIEW;
                    }else {
                        //--No check if this OG Number meets the Criteria.
                        Employee emp = genericService.loadObjectWithSingleCondition(Employee.class, CustomPredicate.procurePredicate("employeeId",pInfo.getInheritOgNumber()));
                        if(emp.isNewEntity()) {
                            result.rejectValue("", "Required.Value", "Inherit "+bc.getStaffTitle()+" does not belong to ANY "+bc.getStaffTypeName()+".");
                            addDisplayErrorsToModel(model, request);
                            model.addAttribute("status", result);
                            model.addAttribute("namedEntity",wNE);
                            model.addAttribute("paymentMethodInfo", pInfo);
                            model.addAttribute("roleBean", bc);
                            return VIEW;
                        }else {
                            AbstractEmployeeEntity newEmp = IppmsUtils.loadEmployee(genericService,pInfo.getParentId(),bc);
                            if(emp.getEmployeeId().equalsIgnoreCase(newEmp.getEmployeeId())) {
                                result.rejectValue("", "Required.Value", "Inherit "+bc.getStaffTitle()+" Must belong to ANOTHER "+bc.getStaffTypeName()+".");
                                addDisplayErrorsToModel(model, request);
                                model.addAttribute("status", result);
                                model.addAttribute("namedEntity",wNE);
                                model.addAttribute("paymentMethodInfo", pInfo);
                                model.addAttribute("roleBean", bc);
                                return VIEW;
                            }else {
                                pInfo.setShareAccountCreator(new User(bc.getLoginId()));
                            }
                        }
                    }
                }
            } else if (pInfo.getPaymentTypeRef().equals("2")) {
                pInfo.setPaymentMethodTypes(getPaymentMethodTypeByCode("CC"));
                pInfo.setBankId(pInfo.getCashCardBankId());
                validator.validate(pInfo, result);
                if (result.hasErrors()) {
                    addDisplayErrorsToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("namedEntity", getSessionAttribute(request, NAMED_ENTITY));
                    model.addAttribute("paymentMethodInfo", pInfo);
                    model.addAttribute("roleBean", bc);
                    return VIEW;
                }
                pInfo.setAccountNumber(null);
                pInfo.setAccountNumber2(null);
                pInfo.setRoutingNumber(null);
                pInfo.setRoutingNumber2(null);
                pInfo.setBvnNo(null);
                pInfo.setAccountType(null);
            }
            pInfo.setBusinessClientId(bc.getBusinessClientInstId());
            pInfo.setLastModBy(new User(bc.getLoginId()));
            pInfo.setLastModTs(Timestamp.from(Instant.now()));
            if (bc.isPensioner())
                pInfo.setPensioner(new Pensioner(pInfo.getParentId()));
            else
                pInfo.setEmployee(new Employee(pInfo.getParentId()));

            if (!wNE.isEditMode()) {
                pInfo.setCreatedBy(new User(bc.getLoginId()));
                pInfo.setCreationDate(Timestamp.from(Instant.now()));
            }
            this.genericService.storeObject(pInfo);
        }
        if (wNE != null && wNE.isEditMode()) {
            if ((Navigator.getInstance(userId).getFromClass().isAssignableFrom(EmployeeGeneralOverviewForm.class)) &&
                    (Navigator.getInstance(userId).getFromForm() != null)) {
                Navigator.getInstance(userId).setFromClass(getClass());
                return Navigator.getInstance(userId).getFromForm();
            } else if ((Navigator.getInstance(userId).getFromClass().isAssignableFrom(ViewEmployeeForApprovalController.class)) &&
                    (Navigator.getInstance(userId).getFromForm() != null)) {
                return Navigator.getInstance(userId).getFromForm();
            }
        }

        return "redirect:dedGarnForm.do";
    }


    private PaymentMethodTypes getPaymentMethodTypeByCode(String pString) {
        PaymentMethodTypes pMT = null;
        if ((this.paymentMethodTypes == null) || (this.paymentMethodTypes.isEmpty())) {

        }
        for (PaymentMethodTypes p : this.paymentMethodTypes) {
            if (p.getPaymentMethodCode().equalsIgnoreCase(pString)) {
                pMT = p;
                break;
            }
        }
        return pMT;
    }

    private List<BankBranch> getBankBranches(Long pBankId) throws Exception {
        return this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", pBankId), "name");

    }
}