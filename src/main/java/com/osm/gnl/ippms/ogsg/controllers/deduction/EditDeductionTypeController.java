package com.osm.gnl.ippms.ogsg.controllers.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.UtilityBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.validators.deduction.CreateDeductionTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping({"/editDeductionType.do"})
@SessionAttributes(types = {EmpDeductionType.class})
public class EditDeductionTypeController extends BaseController {

    private final String VIEW_NAME = "deduction/editEmpDeductionTypeForm";

    private final CreateDeductionTypeValidator createDeductionTypeValidator;
    private final PaycheckService paycheckService;


    @ModelAttribute("bankList")
    public Collection<BankInfo> getBankInfo() {
        List<BankInfo> wBankList = this.genericService.loadControlEntity(BankInfo.class);
        Collections.sort(wBankList);
        return wBankList;
    }

    @ModelAttribute("payTypeList")
    public Collection<PayTypes> getPayTypesList() {
        return this.genericService.loadControlEntity(PayTypes.class);
    }
    @Autowired
    public EditDeductionTypeController(CreateDeductionTypeValidator createDeductionTypeValidator, PaycheckService paycheckService) {
        this.createDeductionTypeValidator = createDeductionTypeValidator;
        this.paycheckService = paycheckService;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"dtid"})
    public String setupForm(@RequestParam("dtid") Long pDtid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = super.getBusinessCertificate(request);

        EmpDeductionType wP = this.genericService.loadObjectUsingRestriction(EmpDeductionType.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId",
                bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id", pDtid)));

        if (wP.isNewEntity()) {
            return "redirect:createDeductionType.do";
        }
        if (wP.getEmpDeductionCategory().isStatutoryDeduction() || wP.getEmpDeductionCategory().isApportionedDeduction()) {
            wP.setBranchInstId(wP.getBankBranches().getId());
            wP.setBankInstId(wP.getBankBranches().getBankInfo().getId());

            wP.setConfirmAccountNumber(wP.getAccountNumber());
            wP.setOldAcctNo(wP.getAccountNumber());
            wP.setOldBranchInstId(wP.getBranchInstId());
            wP.setShowForConfirm(SHOW_ROW);
            if (wP.getEmpDeductionCategory().isApportionedDeduction()) {
                wP.setShowApportionsRows(true);
                if (wP.getFirstAllotAmt() > 0)
                    wP.setFirstAllotAmtStr(PayrollHRUtils.getDecimalFormat().format(wP.getFirstAllotAmt()));
                if (wP.getSecAllotAmt() > 0)
                    wP.setSecAllotAmtStr(PayrollHRUtils.getDecimalFormat().format(wP.getSecAllotAmt()));
                if (wP.getThirdAllotAmt() > 0)
                    wP.setThirdAllotAmtStr(PayrollHRUtils.getDecimalFormat().format(wP.getThirdAllotAmt()));
            }
            if (this.paycheckService.getPendingPaycheckRunMonthAndYear(bc) != null) {
                wP.setShowUpdPaychecksLink(true);
            }
        } else {

            wP.setShowForConfirm(HIDE_ROW);
        }
        wP.setEmpDeductPayTypeRef(wP.getPayTypes().getId());
        addRoleBeanToModel(model, request);
        model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", wP.getBankInstId()), "name"));
        model.addAttribute("deductionTypeBean", wP);
        return VIEW_NAME;

    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"dtid", "s"})
    public String setupForm(@RequestParam("dtid") Long pDtid, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = super.getBusinessCertificate(request);

        EmpDeductionType wP = this.genericService.loadObjectUsingRestriction(EmpDeductionType.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId",
                bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id", pDtid)));

        if (wP.isNewEntity()) {
            return "redirect:createDeductionType.do";
        }
        if (wP.getEmpDeductionCategory().isStatutoryDeduction()
                || wP.getEmpDeductionCategory().isApportionedDeduction()) {
            wP.setBranchInstId(wP.getBankBranches().getId());
            wP.setBankInstId(wP.getBankBranches().getBankInfo().getId());
            wP.setConfirmAccountNumber(wP.getAccountNumber());
            wP.setShowForConfirm(SHOW_ROW);
            if (wP.getEmpDeductionCategory().isApportionedDeduction()) {
                wP.setShowApportionsRows(true);
                if (wP.getFirstAllotAmt() > 0)
                    wP.setFirstAllotAmtStr(PayrollHRUtils.getDecimalFormat().format(wP.getFirstAllotAmt()));
                if (wP.getSecAllotAmt() > 0)
                    wP.setSecAllotAmtStr(PayrollHRUtils.getDecimalFormat().format(wP.getSecAllotAmt()));
                if (wP.getThirdAllotAmt() > 0)
                    wP.setThirdAllotAmtStr(PayrollHRUtils.getDecimalFormat().format(wP.getThirdAllotAmt()));
            }
        } else {

            wP.setShowForConfirm(HIDE_ROW);
        }
        wP.setEmpDeductPayTypeRef(wP.getPayTypes().getId());
        String actionCompleted = "Deduction Type " + wP.getDescription() + " updated Successfully!";
    /*if(pSaved == 2){
    	actionCompleted += " Pending Paychecks Updated Successfully.";
    }*/
        model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class,
                CustomPredicate.procurePredicate("bankInfo.id", wP.getBankInstId()), "name"));
        model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
        addRoleBeanToModel(model, request);
        model.addAttribute("saved", Boolean.valueOf(pSaved > 0));
        model.addAttribute("deductionTypeBean", wP);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("deductionTypeBean") EmpDeductionType pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return CONFIG_HOME_URL;
        }

        createDeductionTypeValidator.validate(pEHB, result, bc);
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class,
                    CustomPredicate.procurePredicate("bankInfo.id", pEHB.getBankInstId()), "name"));
            model.addAttribute("deductionTypeBean", pEHB);
            return VIEW_NAME;
        }
        if ((pEHB.getEmpDeductionCategory().isStatutoryDeduction()) && (!pEHB.isWarningIssued())) {
            pEHB.setWarningIssued(true);
            result.rejectValue("", "Global.Change", "NOTE: Changing the values for this deduction type will propagate to all " + bc.getStaffTypeName() + "'s currently paying this deduction!");
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class,
                    CustomPredicate.procurePredicate("bankInfo.id", pEHB.getBankInstId()), "name"));
            model.addAttribute("deductionTypeBean", pEHB);
            return VIEW_NAME;
        }
        pEHB.setBankBranches(this.genericService.loadObjectWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("id", pEHB.getBranchInstId())));
        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        this.genericService.saveObject(pEHB);

        //-- Now Update All Deduction Information for Employees on this Deduction Type...
        //First load the PayTypes
        PayTypes wPT = this.genericService.loadObjectById(PayTypes.class, pEHB.getEmpDeductPayTypeRef());
        if (wPT.isUsingPercentage()) {
            pEHB.setErrorRecord(true); //This makes sure we do not update the Amount if it is using Amount.
        }
        this.genericService.saveObject(pEHB);
        if (pEHB.isShowUpdPaychecksLink() && Boolean.valueOf(pEHB.getUpdPendingPaychecks())
                && pEHB.isUpdAcctDetails()) {
            LocalDate wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            HashMap<Long, AbstractDeductionEntity> wEDIIList = (HashMap<Long, AbstractDeductionEntity>)this.genericService.loadObjectAsMapWithConditions(IppmsUtils.getDeductionInfoClass(bc), Arrays.asList(CustomPredicate.procurePredicate("empDeductionType.id", pEHB.getId()), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "id");
            UtilityBean wUB = new UtilityBean();
            wUB.makeEmpDeductionIdMap(wEDIIList);
            for (int i = 1; i <= wUB.getKeySet().size(); i++) {
                this.paycheckService.updPendPayDedValues(pEHB, wCal.getMonthValue(), wCal.getYear(), wUB.getValueAsObjectArray(i), bc);
            }

        }

        return "redirect:editDeductionType.do?dtid=" + pEHB.getId() + "&s=1";
    }
}