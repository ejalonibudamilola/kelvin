package com.osm.gnl.ippms.ogsg.controllers.payroll;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.DeletePaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.StoredProcedureService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.engine.DeletePendingPayroll;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.DeletePayrollBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRerun;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/deletePendingPayroll.do"})
@SessionAttributes("payBean")
public class DeletePayrollFormController extends BaseController {



    private final PaycheckService paycheckService;
    private final StoredProcedureService storedProcedureService;
    private final DeletePaycheckService deletePaycheckService;
    private final TransactionTemplate transactionTemplate;

    private final String VIEW = "payroll/deletePayrollRunForm";

    @Autowired
    public DeletePayrollFormController(PaycheckService paycheckService, StoredProcedureService storedProcedureService, DeletePaycheckService deletePaycheckService, TransactionTemplate transactionTemplate) {
        this.paycheckService = paycheckService;
        this.storedProcedureService = storedProcedureService;
        this.deletePaycheckService = deletePaycheckService;
        this.transactionTemplate = transactionTemplate;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);
        DeletePayrollBean wDPB = new DeletePayrollBean();

        //First Load the Month And Year for this Payroll Run...

        wDPB.setRunMonthYearStr(PayrollHRUtils.getMonthAndYearFromDate(paycheckService.getPendingPaycheckRunMonthAndYear(bc)));

        wDPB = this.deletePaycheckService.setPaycheckValues(wDPB, bc);
        double wTotalGarn = this.deletePaycheckService.getTotalsByTable(bc, IppmsUtils.getPaycheckGarnishmentTableName(bc));
        double wTotalSpecAllow = this.deletePaycheckService.getTotalsByTable(bc, IppmsUtils.getPaycheckSpecAllowTableName(bc));
        double wTotalDeductions = this.deletePaycheckService.getTotalsByTable(bc, IppmsUtils.getPaycheckDeductionTableName(bc));
        int wTotalEmpProcessed = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(Arrays.asList(CustomPredicate.procurePredicate("status", "P"), getBusinessClientIdPredicate(request))), IppmsUtils.getPaycheckClass(bc));
         int wTotalEmpPaid = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(Arrays.asList(CustomPredicate.procurePredicate("status", "P"), getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("netPay", 0, Operation.GREATER))), IppmsUtils.getPaycheckClass(bc));
          int wTotalEmpRetired = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(Arrays.asList(CustomPredicate.procurePredicate("status", "P"), getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("terminatedInd", ON))), IppmsUtils.getPaycheckClass(bc));

        int wTotalEmpNotPaid = wTotalEmpProcessed - wTotalEmpPaid;

        int wTotalEmpWithNegPay = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(Arrays.asList(CustomPredicate.procurePredicate("status", "P"), getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("negativePayInd", 1))), IppmsUtils.getPaycheckClass(bc));

        wDPB.setTotalGarnishmentsStr(PayrollHRUtils.getDecimalFormat().format(wTotalGarn));
        wDPB.setSpecialAllowanceStr(PayrollHRUtils.getDecimalFormat().format(wTotalSpecAllow));
        wDPB.setTotalDeductionsStr(PayrollHRUtils.getDecimalFormat().format(wTotalDeductions));
        wDPB.setTotalEmpProcessed(wTotalEmpProcessed);
        wDPB.setTotalEmpPaid(wTotalEmpPaid);
        wDPB.setTotalEmpRetired(wTotalEmpRetired);
        wDPB.setTotalEmpNotPaid(wTotalEmpNotPaid);
        wDPB.setTotalEmpWithNegPay(wTotalEmpWithNegPay);

        model.addAttribute("payBean", wDPB);
        addRoleBeanToModel(model, request);

        return VIEW;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"pp", "s"})
    public String setupForm(@RequestParam("pp") String pPayPeriod, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        //Here...find out if we have any Pending Deleted single Paycheck..
        List<PayrollRerun> wPR = this.genericService.loadAllObjectsWithSingleCondition(PayrollRerun.class, getBusinessClientIdPredicate(request), null);

        if (wPR != null && !wPR.isEmpty()) {
            this.storedProcedureService.callStoredProcedure(DEL_PAYROLL_RERUN_PROC,bc.getBusinessClientInstId());

        }
        DeletePayrollBean wDPB = new DeletePayrollBean();
        String actionCompleted = "";
        if (pPayPeriod == null || pPayPeriod.length() == 0 || pPayPeriod.equalsIgnoreCase("null")) {
            actionCompleted = "Pending Payroll Deleted Successfully.";
        } else {
            actionCompleted = "Pending Payroll for " + pPayPeriod + " Deleted Successfully.";
        }


        model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
        model.addAttribute("saved", pSaved == 1);
        model.addAttribute("payBean", wDPB);
        addRoleBeanToModel(model, request);
        return VIEW;

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_delete", required = false) String pDelete,
                                @RequestParam(value = "_confirm", required = false) String pConfirm,
                                @RequestParam(value = "_cancel", required = false) String pCancel,
                                @ModelAttribute("payBean") DeletePayrollBean ppDMB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        // Object userId = this.getSessionId(request);
        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_DELETE)) {

            ppDMB.setDeleteWarningIssued(true);
            ppDMB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
            result.rejectValue("", "No.Employees", "Please confirm deletion of payroll for " + ppDMB.getRunMonthYearStr());

            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("payBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW;


        }

        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)) {
            try {

                LocalDate wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
                PayrollRunMasterBean wPRMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                        Arrays.asList(super.getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("runMonth", wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear",wCal.getYear())));
                //-- Check if there are records in the PayrollRerun Table...
                RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(
                        CustomPredicate.procurePredicate("runMonth", wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", wCal.getYear()),
                        getBusinessClientIdPredicate(request)));

                DeletePendingPayroll wDPP = new DeletePendingPayroll(
                        this.deletePaycheckService,
                        this.deletePaycheckService.loadPendingPaychecksForDeletion(bc),
                        this.transactionTemplate, ppDMB, wRPB, wPRMB, bc);
                addSessionAttribute(request, "delPay", wDPP);

                Thread t = new Thread(wDPP);
                t.start();

                return "redirect:displayDeletePayroll.do";
            } catch (Exception wEx) {
                result.rejectValue("", "No.Employees", "Exception occurred while deleting payroll for " + ppDMB.getRunMonthYearStr());

                addDisplayErrorsToModel(model, request);
                model.addAttribute("pageErrors", result);
                model.addAttribute("payBean", ppDMB);
                model.addAttribute("roleBean", bc);
                return VIEW;
            }

        }

        return "redirect:deletePendingPayroll.do?pp=" + ppDMB.getRunMonthYearStr() + "&s=1";
    }


}
