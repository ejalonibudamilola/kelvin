package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranchAsBank;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.configcontrol.CreateNewBankValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/editBankBranch.do"})
@SessionAttributes(types = {BankBranch.class})
public class EditBankBranchFormController extends BaseController {

    @Autowired
    private CreateNewBankValidator createNewBankValidator;

    private static final String VIEW_NAME = "bank/editBankBranchForm";

    @ModelAttribute("banksList")
    public List<BankInfo> generateBanksList() {
        List<BankInfo> wBankInfoList = this.genericService.loadControlEntity(BankInfo.class);
        return wBankInfoList;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"bbid"})
    public String setupForm(@RequestParam("bbid") Long pBankBranchId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BankBranch bankBranch = this.genericService.loadObjectWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("id", pBankBranchId));

        if (bankBranch.isNewEntity()) {
            return "redirect:addNewBankBranch.do";
        }
        if (bankBranch.getBankInfo().isMicroFinanceBank()) {
            BankBranchAsBank bankBranchAsBank = this.genericService.loadObjectUsingRestriction(BankBranchAsBank.class,
                    Arrays.asList(CustomPredicate.procurePredicate("branchInstId", bankBranch.getId()), getBusinessClientIdPredicate(request)));
            if (!bankBranchAsBank.isNewEntity()) {
                bankBranch.setBankStatusDesignate(true);
                bankBranch.setDesignationId(bankBranchAsBank.getId());
            }
            bankBranch.setShowOverride(true);
        }

        bankBranch.setEditMode(true);
        //wP.setBankId(wP.getBankInfo().getId());
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("bankBranches.id", bankBranch.getId()));

        PredicateBuilder predicateBuilder2 = new PredicateBuilder();
        predicateBuilder2.addPredicate(CustomPredicate.procurePredicate("bankBranch.id", bankBranch.getId()));

        int cCount1 = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, PaymentMethodInfo.class);

        int cCount2 = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder2, EmployeePayBean.class);

        bankBranch.setCanDelete((IppmsUtils.isNullOrLessThanOne(cCount1)) && (IppmsUtils.isNullOrLessThanOne(cCount2)));

        model.addAttribute("bankBranchBean", bankBranch);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"bbid", "s"})
    public String setupForm(@RequestParam("bbid") Long pBankBranchId,
                            @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        BankBranch bankBranch = this.genericService.loadObjectWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate(
                "id", pBankBranchId));

        if (bankBranch.getBankInfo().isMicroFinanceBank()) {
            BankBranchAsBank bankBranchAsBank = this.genericService.loadObjectUsingRestriction(BankBranchAsBank.class,
                    Arrays.asList(CustomPredicate.procurePredicate("branchInstId", bankBranch.getId()), getBusinessClientIdPredicate(request)));
            if (!bankBranchAsBank.isNewEntity())
                bankBranch.setBankStatusDesignate(true);
            bankBranch.setShowOverride(true);
        }

        if (bankBranch.isNewEntity()) {
            return "redirect:addNewBankBranch.do";
        }
        bankBranch.setEditMode(true);
        //wP.setBankId(wP.getBankInfo().getId());
        bankBranch.setCanDelete(false);
        if(pSaved == 1)
           model.addAttribute(IConstants.SAVED_MSG, "Bank Branch " + bankBranch.getName() + " updated successfully.");
        else if(pSaved == 2)
            model.addAttribute(IConstants.SAVED_MSG, "Bank Branch " + bankBranch.getName() + " Designated As 'Bank' for Reports successfully.");
        else if(pSaved == 3)
            model.addAttribute(IConstants.SAVED_MSG, "Bank Branch " + bankBranch.getName() + " Reverted back to a Bank Branch for Reports successfully.");

        model.addAttribute("saved", true);
        model.addAttribute("roleBean", bc)
        ;
        model.addAttribute("bankBranchBean", bankBranch);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"bbn", "s", "bn"})
    public String setupForm(@RequestParam("bbn") String pBranchName,
                            @RequestParam("s") int pSaved, @RequestParam("bn") String pBankName, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        BankBranch bankBranch = new BankBranch();


        bankBranch.setEditMode(true);
        bankBranch.setBankInfo(new BankInfo());
        bankBranch.setCanDelete(false);
        model.addAttribute(IConstants.SAVED_MSG, "Bank Branch '" + pBranchName + "' deleted successfully.");
        model.addAttribute("saved", true);
        model.addAttribute("roleBean", bc);
        model.addAttribute("bankBranchBean", bankBranch);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_delete", required = false) String delete,
                                @ModelAttribute("bankBranchBean") BankBranch pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL))
            return "redirect:viewAllBankBranches.do";

        if (!pEHB.isNewEntity()) {
            if (pEHB.isDefaultObject()) {
                result.rejectValue("", "DEF.EDIT.VOID", "Default Bank Branch can not be edited.");
                model.addAttribute("status", result);
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("roleBean", bc);
                model.addAttribute("bankBranchBean", pEHB);
                return VIEW_NAME;
            }
        }
        if (isButtonTypeClick(request, REQUEST_PARAM_DELETE)) {

            String branchName = pEHB.getName();
            String bankName = pEHB.getBankInfo().getName();
            if (pEHB.getBankInfo().isMicroFinanceBank())
                if (pEHB.isBankStatusDesignate())
                    this.genericService.deleteObject(new BankBranchAsBank(pEHB.getDesignationId(), bc.getBusinessClientInstId(), pEHB.getId()));

            this.genericService.deleteObject(pEHB);
            return "redirect:editBankBranch.do?bbn=" + branchName + "&s=2&bn=" + bankName;
        }

        createNewBankValidator.validateBankBranch(pEHB, result);
        if (result.hasErrors()) {
            //((BankBranch)result.getTarget()).setDisplayErrors("block");
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("roleBean", bc);
            model.addAttribute("bankBranchBean", pEHB);
            return VIEW_NAME;
        }
        //pEHB.setBankInfo(new BankInfo(pEHB.getBankId()));
        pEHB.setLastModBy(new User(bc.getLoginId()));
        pEHB.setLastModTs(Timestamp.from(Instant.now()));
        int saveStatus = 1;
        if(pEHB.isBankStatusDesignate() && IppmsUtils.isNullOrLessThanOne(pEHB.getDesignationId())) {
            this.genericService.saveObject(new BankBranchAsBank(null, bc.getBusinessClientInstId(), pEHB.getId()));
            saveStatus = 2;
        }else if(!pEHB.isBankStatusDesignate() && IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getDesignationId())){
            this.genericService.deleteObject(new BankBranchAsBank(pEHB.getDesignationId()));
            saveStatus = 3;
        }
        this.genericService.saveObject(pEHB);

        return "redirect:editBankBranch.do?bbid=" + pEHB.getId() + "&s="+saveStatus;
    }

}
