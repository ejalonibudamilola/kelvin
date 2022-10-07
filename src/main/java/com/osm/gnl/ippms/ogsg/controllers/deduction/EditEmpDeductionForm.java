package com.osm.gnl.ippms.ogsg.controllers.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionCategory;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.domain.payment.RangedDeduction;
import com.osm.gnl.ippms.ogsg.domain.payment.RangedDeductionDetails;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.negativepay.domain.NegativePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.deduction.DeductionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequestMapping({"/empEditDeductionForm.do"})
@SessionAttributes("empDeductionInfo")
public class EditEmpDeductionForm extends BaseController {


    @Autowired
    private DeductionValidator deductionValidator;
    @Autowired
    private PaycheckService paycheckService;

    private List<EmpDeductionType> empDedTypeList;
    private List<PayTypes> payTypesList;
    private final String VIEW_NAME = "deduction/empEditDeductionForm";


    public EditEmpDeductionForm() { }

    @ModelAttribute("deductionCategory")
    public Collection<EmpDeductionCategory> getDeductionCategories() {
        return this.genericService.loadAllObjectsWithoutRestrictions(EmpDeductionCategory.class, "name");
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid", "cid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("cid") Long pId, Model model,
                            HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);

        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService, pEmpId, bc);
        ne.setName(emp.getDisplayNameWivTitlePrefixed());
        ne.setId(emp.getId());


        AbstractDeductionEntity empDeductInfo = IppmsUtils.makeDeductionInfoObject(bc);
        empDeductInfo.setLocked(false);
        empDeductInfo.setEmpDeductCatRef(pId);

            empDeductInfo.setParentObject( emp);

        empDeductInfo.setEditDenied(false);
        List<EmpDeductionType> empDedType = this.getEmpDedTypeList(request, pId);
        empDeductInfo.setShowDateRows(false);
        empDeductInfo.setDisplayPayrollMsg("Create Deduction for : " + ne.getName());
        empDeductInfo.setDisplayTitle("Enter Deduction Details");
        empDeductInfo.setErrorMsg(bc.getStaffTitle() + " [ " + emp.getEmployeeId() + " ]");
        model.addAttribute("namedEntity", ne);
        model.addAttribute("empTypeList", empDedType);
        model.addAttribute("payType", getPayTypesList());
        model.addAttribute("roleBean", bc);
        model.addAttribute("empDeductionInfo", empDeductInfo);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid", "cid",
            "ind"})
    public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("cid") Long pId,
                            @RequestParam("ind") int pInd, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
        // if ((ne.isNew()) || (ne.getName() == null)) {
        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService, pEmpId, bc);

        ne.setName(emp.getDisplayNameWivTitlePrefixed());
        ne.setId(emp.getId());
        // }

        AbstractDeductionEntity empDeductInfo = IppmsUtils.makeDeductionInfoObject(bc);
        EmpDeductionType wEDT = this.genericService.loadObjectUsingRestriction(EmpDeductionType.class, Arrays.asList(
                CustomPredicate.procurePredicate("id", pId), this.getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("displayableInd", OFF)));

        empDeductInfo.setEmpDeductTypeRef(pId);
        empDeductInfo.setEmpDeductionType(wEDT);
        if (wEDT.getEmpDeductionCategory().isStatutoryDeduction() || wEDT.getEmpDeductionCategory().isApportionedDeduction()) {
            empDeductInfo.setDescription(wEDT.getDescription());
            empDeductInfo.setEmpDeductPayTypeRef(wEDT.getPayTypes().getId());
            empDeductInfo.setAmount(wEDT.getAmount());

            empDeductInfo.setAccountNumber(wEDT.getAccountNumber());
            empDeductInfo.setConfirmAccountNumber(wEDT.getAccountNumber());
            empDeductInfo.setLocked(true);
            empDeductInfo.setLockAmount(false);
            empDeductInfo.setLockDescription(true);
            empDeductInfo.setLockPayType(true);
        }else if(wEDT.getEmpDeductionCategory().isRangedDeduction()){
            //If it is a Ranged Deduction...then set the Required Values based on the Ranged deduction Type.
            RangedDeduction rangedDeduction = this.genericService.loadObjectUsingRestriction(RangedDeduction.class,Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("name",wEDT.getName(),Operation.STRING_EQUALS)));
            if(!rangedDeduction.isNewEntity()) {
                empDeductInfo = setRangedValues(rangedDeduction, bc, empDeductInfo, emp);
                empDeductInfo.setDescription(wEDT.getDescription());
                empDeductInfo.setEmpDeductPayTypeRef(wEDT.getPayTypes().getId());
                empDeductInfo.setLocked(true);
                empDeductInfo.setLockAmount(true);
                empDeductInfo.setLockDescription(true);
                empDeductInfo.setLockPayType(true);
            } else{
                //Do nothing. This Deduction should not be creatable.
            }

        } else{
            empDeductInfo.setLocked(false);
            empDeductInfo.setLockAmount(false);
            empDeductInfo.setLockDescription(false);
            empDeductInfo.setLockPayType(false);
        }

        empDeductInfo.setShowDateRows(wEDT.isMustEnterDate());
        empDeductInfo.setEmpDeductCatRef(wEDT.getEmpDeductionCategory().getId());
        empDeductInfo.setParentObject(emp);

        empDeductInfo.setEditDenied(false);
        List<EmpDeductionType> empDedType = getEmpDedTypeList(request, wEDT.getEmpDeductionCategory().getId());

        empDeductInfo.setDisplayPayrollMsg("Create Deduction for : " + ne.getName());
        empDeductInfo.setDisplayTitle("Enter Deduction Details");
        empDeductInfo.setErrorMsg(bc.getStaffTitle() + " [ " + emp.getEmployeeId() + " ]");
        model.addAttribute("namedEntity", ne);
        model.addAttribute("empTypeList", empDedType);
        model.addAttribute("roleBean", bc);
        model.addAttribute("payType", getPayTypesList());
        model.addAttribute("empDeductionInfo", empDeductInfo);
        return VIEW_NAME;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid", "cid", "atn"})
    public String setupForm(@RequestParam("eid") Long empId, @RequestParam("cid") Long pId,
                            @RequestParam("atn") String pAction, Model model, HttpServletRequest request) throws Exception {

        AbstractDeductionEntity empDeductInfo;
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);

        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService, empId, bc);
        ne.setName(emp.getDisplayNameWivTitlePrefixed());
        ne.setId(emp.getId());


        empDeductInfo = (AbstractDeductionEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getDeductionInfoClass(bc), Arrays.asList(CustomPredicate.procurePredicate("id", pId), this.getBusinessClientIdPredicate(request)));
        if (IppmsUtils.treatNull(pAction).equalsIgnoreCase("d")) {

            empDeductInfo.setEmpDeductCatRef(empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().getId());
            empDeductInfo.setEmpDeductTypeRef(empDeductInfo.getEmpDeductionType().getId());
            empDeductInfo.setEmpDeductPayTypeRef(empDeductInfo.getPayTypes().getId());

            if (empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().isStatutoryDeduction() ||
                    empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().isApportionedDeduction()) {
                empDeductInfo.setDescription(empDeductInfo.getEmpDeductionType().getDescription());
                empDeductInfo.setEmpDeductPayTypeRef(empDeductInfo.getEmpDeductionType().getPayTypes().getId());

                empDeductInfo.setAccountNumber(empDeductInfo.getEmpDeductionType().getAccountNumber());
                empDeductInfo.setConfirmAccountNumber(empDeductInfo.getEmpDeductionType().getAccountNumber());
                empDeductInfo.setLocked(true);
                empDeductInfo.setLockAmount(false);
                empDeductInfo.setLockDescription(true);
                empDeductInfo.setLockDeductType(true);
                empDeductInfo.setLockCategory(true);
                empDeductInfo.setLockPayType(empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().isRangedDeduction());
            }else if(empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().isRangedDeduction()){
                    //If it is a Ranged Deduction...then set the Required Values based on the Ranged deduction Type.
                    RangedDeduction rangedDeduction = this.genericService.loadObjectUsingRestriction(RangedDeduction.class,Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("name",empDeductInfo.getEmpDeductionType().getName(),Operation.STRING_EQUALS)));
                    if(!rangedDeduction.isNewEntity()) {
                        empDeductInfo = setRangedValues(rangedDeduction, bc, empDeductInfo, emp);
                        empDeductInfo.setDescription(empDeductInfo.getEmpDeductionType().getDescription());
                        empDeductInfo.setEmpDeductPayTypeRef(empDeductInfo.getEmpDeductionType().getPayTypes().getId());
                        empDeductInfo.setLocked(true);
                        empDeductInfo.setLockAmount(true);
                        empDeductInfo.setLockDescription(true);
                        empDeductInfo.setLockPayType(true);
                    } else{
                        //Do nothing. This Deduction should not be creatable.
                    }
            }
            else {
                empDeductInfo.setLocked(false);
                empDeductInfo.setLockAmount(false);
                empDeductInfo.setLockDescription(false);
                empDeductInfo.setLockPayType(false);
            }


            empDeductInfo.setDelete(true);
            empDeductInfo.setDisplayPayrollMsg("Delete Deduction for : " + ne.getName());
            empDeductInfo.setDisplayTitle("Deduction Details");
            if (empDeductInfo.getEmpDeductionType().isEditRestricted()) {
                empDeductInfo.setEditDenied(!bc.isSuperAdmin());
            }
        } else {
            empDeductInfo.setBankInstId(empDeductInfo.getEmpDeductionType().getBankBranches().getBankInfo().getId());
            empDeductInfo.setAction(pAction.toLowerCase());
            empDeductInfo.setEmpDeductCatRef(empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().getId());
            empDeductInfo.setEmpDeductTypeRef(empDeductInfo.getEmpDeductionType().getId());
            empDeductInfo.setEmpDeductPayTypeRef(empDeductInfo.getPayTypes().getId());
            empDeductInfo.setAction("e");
            if (empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().isStatutoryDeduction()||
                    empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().isApportionedDeduction()) {
                empDeductInfo.setDescription(empDeductInfo.getEmpDeductionType().getDescription());
                empDeductInfo.setAccountNumber(empDeductInfo.getEmpDeductionType().getAccountNumber());
                empDeductInfo.setConfirmAccountNumber(empDeductInfo.getEmpDeductionType().getAccountNumber());
                empDeductInfo.setLocked(true);
                empDeductInfo.setLockAmount(false);
                empDeductInfo.setLockDescription(true);
            } else {
                empDeductInfo.setLocked(false);
                empDeductInfo.setLockAmount(false);
                empDeductInfo.setLockDescription(false);
            }
            empDeductInfo.setLockPayType(false);


            empDeductInfo.setShowDateRows(empDeductInfo.getEmpDeductionType().isMustEnterDate());
            if (empDeductInfo.getEmpDeductionType().isEditRestricted()) {
                empDeductInfo.setEditDenied(!bc.isSuperAdmin());
            }
            empDeductInfo.setDisplayPayrollMsg("Edit Deduction for : " + ne.getName());
            empDeductInfo.setDisplayTitle("Edit Deduction Details");
        }

        empDeductInfo.setErrorMsg(employeeText(request) + " [ " + emp.getEmployeeId() + " ]");
        model.addAttribute("empTypeList", getEmpDedTypeList(request, empDeductInfo.getEmpDeductCatRef()));
        model.addAttribute("payType", getPayTypesList());
        model.addAttribute("roleBean", bc);
        model.addAttribute("empDeductionInfo", empDeductInfo);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid", "cid", "npv"})
    public String setupForm(@RequestParam("eid") Long empId, @RequestParam("cid") Long pDeductionId,
                            @RequestParam("npv") Long pNegPayId, Model model, HttpServletRequest request) throws Exception {
        BusinessCertificate bc = this.getBusinessCertificate(request);
        SessionManagerService.manageSession(request, model);
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
        // if ((ne.isNew()) || (ne.getName() == null)) {
        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService, empId, this.getBusinessCertificate(request));
        ne.setName(emp.getFirstName() + " " + emp.getLastName());
        ne.setId(emp.getId());
        // }

        AbstractDeductionEntity empDeductInfo = (AbstractDeductionEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getDeductionInfoClass(bc), Arrays.asList(CustomPredicate.procurePredicate("id", pDeductionId), this.getBusinessClientIdPredicate(request)));

        empDeductInfo.setBankInstId(empDeductInfo.getEmpDeductionType().getBankBranches().getBankInfo().getId());
        empDeductInfo.setEmpDeductCatRef(empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().getId());
        empDeductInfo.setEmpDeductTypeRef(empDeductInfo.getEmpDeductionType().getId());
        empDeductInfo.setEmpDeductPayTypeRef(empDeductInfo.getPayTypes().getId());
        empDeductInfo.setAction("e");
        if (empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().isStatutoryDeduction()||
                empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().isRangedDeduction()) {
            empDeductInfo.setDescription(empDeductInfo.getEmpDeductionType().getDescription());
            empDeductInfo.setEmpDeductPayTypeRef(empDeductInfo.getEmpDeductionType().getPayTypes().getId());

            empDeductInfo.setAccountNumber(empDeductInfo.getEmpDeductionType().getAccountNumber());
            empDeductInfo.setConfirmAccountNumber(empDeductInfo.getEmpDeductionType().getAccountNumber());

            empDeductInfo.setLockPayType(empDeductInfo.getEmpDeductionType().getEmpDeductionCategory().isRangedDeduction());

        }

        empDeductInfo.setLocked(true);
        empDeductInfo.setLockPayType(true);
        empDeductInfo.setLockDescription(true);
        empDeductInfo.setLockCategory(true);
        empDeductInfo.setLockDeductType(true);

        empDeductInfo.setShowDateRows(empDeductInfo.getEmpDeductionType().isMustEnterDate());

        // -- Now set the Old Value. We need it later..
        empDeductInfo.setOldAmount(empDeductInfo.getAmount());
        empDeductInfo.setNegativePayId(pNegPayId);
        NegativePayBean wNPB = this.genericService.loadObjectUsingRestriction(NegativePayBean.class, Arrays.asList(
                CustomPredicate.procurePredicate("id", pNegPayId), CustomPredicate.procurePredicate("businessClientId", this.getBusinessCertificate(request).getBusinessClientInstId())));

        empDeductInfo.setRunMonth(wNPB.getRunMonth());
        empDeductInfo.setRunYear(wNPB.getRunYear());
        empDeductInfo.setDisplayPayrollMsg("Edit Deduction for : " + ne.getName());
        if (!wNPB.isNewEntity()) {
            empDeductInfo.setDisplayTitle("Reduction Target : " + wNPB.getCurrDiff());
        } else {
            empDeductInfo.setDisplayTitle("Reduce Deduction ");
        }
        // empDeductInfo
        empDeductInfo.setErrorMsg(employeeText(request) + "  [ " + emp.getEmployeeId() + " ]");
        model.addAttribute("empTypeList", getEmpDedTypeList(request, empDeductInfo.getEmpDeductCatRef()));
        model.addAttribute("payType", getPayTypesList());
        model.addAttribute("empDeductionInfo", empDeductInfo);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long empId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
        AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService, empId, bc);
        ne.setName(emp.getDisplayNameWivTitlePrefixed());
        ne.setId(emp.getId());

        AbstractDeductionEntity empDeductInfo = IppmsUtils.makeDeductionInfoObject(bc);
        empDeductInfo.setLocked(false);
        empDeductInfo.setAction("n");
        empDeductInfo.setParentObject(emp);
        empDeductInfo.setShowDateRows(false);

        empDeductInfo.setEditDenied(false);
        empDeductInfo.setDisplayPayrollMsg("Create Deduction for : " + ne.getName());
        empDeductInfo.setDisplayTitle("Enter Deduction Details");
        empDeductInfo.setErrorMsg(bc.getStaffTitle() + " [ " + emp.getEmployeeId() + " ]");
        model.addAttribute("namedEntity", ne);
        model.addAttribute("roleBean", bc);
        model.addAttribute("payType", getPayTypesList());
        model.addAttribute("empDeductionInfo", empDeductInfo);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_delete", required = false) String delete,
                                @ModelAttribute("empDeductionInfo") AbstractDeductionEntity empDeductInfo, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        // Object userId = super.getSessionId(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:dedGarnForm.do?eid=" + empDeductInfo.getParentId() + "&pid="
                    + bc.getBusinessClientInstId() + "&oid=0&tid=" + DEDUCTION;
        }
        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(super.getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", 1)));
        if (!wPRB.isNewEntity() && wPRB.isRunning()) {
            result.rejectValue("", "No.Employees",
                    "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName()
                            + ". Deductions can not be edited,deleted or added during a Payroll Run");
            addDisplayErrorsToModel(model, request);
            NamedEntity ne = new NamedEntity();

            ne.setName(empDeductInfo.getParentObject().getDisplayNameWivTitlePrefixed());
            ne.setId(empDeductInfo.getParentId());

            model.addAttribute("roleBean", bc);
            model.addAttribute("empDeductionInfo", empDeductInfo);
            model.addAttribute("namedEntity", ne);
            model.addAttribute("pageErrors", result);
            model.addAttribute("empDeductionInfo", empDeductInfo);
            return VIEW_NAME;
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_DELETE)) {
            empDeductInfo.setMode("delete");

            PredicateBuilder predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("empDedInfo.id", empDeductInfo.getId(), Operation.EQUALS));

            Long empId = empDeductInfo.getParentId();
            if (this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckDeductionClass(bc)) > 0) {

                if (empDeductInfo.getPayTypes() == null) {
                    empDeductInfo.setPayTypes(new PayTypes(empDeductInfo.getEmpDeductPayTypeRef()));
                }

                if (empDeductInfo.getEmpDeductionType() == null) {
                    empDeductInfo
                            .setEmpDeductionType(this.genericService.loadObjectById(EmpDeductionType.class, empDeductInfo.getEmpDeductTypeRef()));
                }

                empDeductInfo.setBusinessClientId(bc.getBusinessClientInstId());
                empDeductInfo.setAmount(0.0D);
                if (empDeductInfo.getEmpDeductionType().isMustEnterDate()) {
                    empDeductInfo.setEndDate(LocalDate.now());
                }

                this.genericService.saveObject(empDeductInfo);

                return "redirect:dedGarnForm.do?eid=" + empDeductInfo.getParentObject().getId()
                        + bc.getBusinessClientInstId() + "&oid=" + empDeductInfo.getId() + "&tid=" + DEDUCTION;
            }


            if (!empDeductInfo.isNewEntity()) {
                this.genericService.deleteObject(empDeductInfo);
            }
            return "redirect:dedGarnForm.do?eid=" + empId + "&pid=" + bc.getBusinessClientInstId() + "&oid=0&tid="
                    + DEDUCTION;
        }

        if (empDeductInfo != null) {
            // Always Set The Pay Type should in case the DEDUCTION_TYPE PayType has been
            // changed.
            empDeductInfo.setPayTypes(new PayTypes(empDeductInfo.getEmpDeductPayTypeRef()));

            deductionValidator.validate(empDeductInfo, result, bc);
            if (result.hasErrors()) {
                NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
                if ((ne.isNewEntity()) || (ne.getName() == null)) {
                    AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService,
                            empDeductInfo.getParentId(), bc);

                        ((AbstractDeductionEntity) result.getTarget()).setParentObject(emp);
                    ne.setName(emp.getFirstName() + " " + emp.getLastName());
                    ne.setId(emp.getId());
                }
                addDisplayErrorsToModel(model, request);
                model.addAttribute("namedEntity", ne);
                model.addAttribute("pageErrors", result);
                model.addAttribute("roleBean", bc);
                model.addAttribute("empDeductionInfo", empDeductInfo);
                return VIEW_NAME;
            }

            if (empDeductInfo.getEmpDeductionType() == null) {
                empDeductInfo.setEmpDeductionType(this.genericService.loadObjectById(EmpDeductionType.class, empDeductInfo.getEmpDeductTypeRef()));
            }

            empDeductInfo.setBusinessClientId(bc.getBusinessClientInstId());
            empDeductInfo.setCreatedBy(new User(bc.getLoginId()));
            empDeductInfo.setName(empDeductInfo.getEmpDeductionType().getName());
            empDeductInfo.setDescription(empDeductInfo.getDescription());
            empDeductInfo.setLastModBy(new User(bc.getLoginId()));
            empDeductInfo.setLastModTs(Timestamp.from(Instant.now()));

            LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);


            PredicateBuilder predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(this.getBusinessClientIdPredicate(request));

            if (IppmsUtils.isNotNull(_wCal)) {
                // This means we have a Pending Paycheck. Set for strictly RERUN
                RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(
                        CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()),
                        CustomPredicate.procurePredicate("runYear", _wCal.getYear()),
                        this.getBusinessClientIdPredicate(request)));

                wRPB.setNoOfDeductions(wRPB.getNoOfDeductions() + 1);
                if (wRPB.isNewEntity()) {
                    wRPB.setRunMonth(_wCal.getMonthValue());
                    wRPB.setRunYear(_wCal.getYear());
                    wRPB.setRerunInd(IConstants.ON);
                    wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                }
                this.genericService.storeObject(wRPB);
            }
            // before storing... check for the new value...
            if (empDeductInfo.getNegativePayId() != null) {
                saveNegativePayInformation(empDeductInfo,bc);
            }
            empDeductInfo.makeParentObject(empDeductInfo.getParentObject().getId());
            empDeductInfo.setBusinessClientId(bc.getBusinessClientInstId());
            this.genericService.saveObject(empDeductInfo);
        }

        return "redirect:dedGarnForm.do?eid=" + empDeductInfo.getParentObject().getId() + "&pid="
                + bc.getBusinessClientInstId() + "&oid=" + empDeductInfo.getId() + "&tid=" + DEDUCTION;
    }

    /**
     * For now, Pensioners use their monthly pension values whilst others use their Monthly Basic...
     * @param rangedDeduction
     * @param bc
     * @param empDeductInfo
     * @return
     */
    private AbstractDeductionEntity setRangedValues(RangedDeduction rangedDeduction, BusinessCertificate bc, AbstractDeductionEntity empDeductInfo, AbstractEmployeeEntity abstractEmployeeEntity) throws InstantiationException, IllegalAccessException {
        double value;
        if(bc.isPensioner()){
            HiringInfo hiringInfo = genericService.loadObjectWithSingleCondition(HiringInfo.class,CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),abstractEmployeeEntity.getId()));
                value = hiringInfo.getMonthlyPensionAmount();
        }else{
            value = abstractEmployeeEntity.getSalaryInfo().getBasicMonthlySalary();
        }

        for(RangedDeductionDetails r : rangedDeduction.getRangedDeductionDetailsList())
            if(r.getLowerBound() >= value && r.getUpperBound() <= value)
                empDeductInfo.setAmount(r.getAmount());
        empDeductInfo.setAmount(value);
        return empDeductInfo;
    }

    private void saveNegativePayInformation(AbstractDeductionEntity pEmpDeductInfo, BusinessCertificate bc) throws Exception {
        NegativePayBean wNPB = this.genericService.loadObjectById(NegativePayBean.class, pEmpDeductInfo.getNegativePayId());
        AbstractEmployeeEntity wEmp = (AbstractEmployeeEntity) this.genericService.loadObjectById(IppmsUtils.getEmployeeClass(bc), pEmpDeductInfo.getParentObject().getId());

        if (pEmpDeductInfo.getPayTypes().isUsingPercentage()) {

            BigDecimal wOldValueBd = new BigDecimal(
                    pEmpDeductInfo.getOldAmount() * (wEmp.getSalaryInfo().getMonthlyBasicSalary() / 12.0D)).setScale(2,
                    RoundingMode.HALF_EVEN);
            BigDecimal wNewValueBd = new BigDecimal(
                    pEmpDeductInfo.getAmount() * (wEmp.getSalaryInfo().getMonthlyBasicSalary() / 12.0D))
                    .setScale(2, RoundingMode.HALF_EVEN);
            wNPB.setReductionAmount(
                    wNPB.getReductionAmount() + (wOldValueBd.doubleValue() - wNewValueBd.doubleValue()));
        } else {
            wNPB.setReductionAmount(
                    wNPB.getReductionAmount() + (pEmpDeductInfo.getOldAmount() - pEmpDeductInfo.getAmount()));
        }
        this.genericService.storeObject(wNPB);
    }


    private List<EmpDeductionType> getEmpDedTypeList(HttpServletRequest request, Long pId) {
        empDedTypeList = this.genericService.loadAllObjectsUsingRestrictions(EmpDeductionType.class, Arrays.asList(
                CustomPredicate.procurePredicate("empDeductionCategory.id", pId), CustomPredicate.procurePredicate("displayableInd", 0),
                this.getBusinessClientIdPredicate(request)), null);
        Collections.sort(empDedTypeList, Comparator.comparing(EmpDeductionType::getDescription));

        return this.empDedTypeList;
    }

    private List<PayTypes> getPayTypesList() {

        return this.genericService.loadAllObjectsWithSingleCondition(PayTypes.class, CustomPredicate.procurePredicate("selectableInd", OFF), "name");
    }
}