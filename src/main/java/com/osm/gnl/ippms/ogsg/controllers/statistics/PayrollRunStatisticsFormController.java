package com.osm.gnl.ippms.ogsg.controllers.statistics;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.base.services.StatisticsService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.PayrollConfigImpact;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.MaterialityDisplayBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
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
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collection;


@Controller
@RequestMapping({"/payrollRunStatistics.do"})
@SessionAttributes(types = {MaterialityDisplayBean.class})
public class PayrollRunStatisticsFormController extends BaseController {

    private final String VIEW = "statistics/payrollRunStatisticsForm";


    private final PaycheckService paycheckService;
    private final PayrollService payrollService;
    private final StatisticsService statisticsService;
    @Autowired
    public PayrollRunStatisticsFormController(PaycheckService paycheckService, PayrollService payrollService, StatisticsService statisticsService) {
        this.paycheckService = paycheckService;
        this.payrollService = payrollService;
        this.statisticsService = statisticsService;
    }

    @ModelAttribute("monthList")
    public Collection<NamedEntity> getMonthList() {
        return PayrollBeanUtils.makeAllMonthList();
    }

    @ModelAttribute("yearList")
    public Collection<NamedEntity> makeYearList(HttpServletRequest request) {

        return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        //--First check for Pending Payroll Run...
        int pRunMonth;
        int pRunYear;
        LocalDate localDate = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
        if (localDate != null) {
            pRunMonth = localDate.getMonthValue();
            pRunYear = localDate.getYear();
        } else {
            PayrollFlag wPR = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);
            if (wPR.isNewEntity()) {
                pRunMonth = LocalDate.now().getMonthValue();
                pRunYear = LocalDate.now().getYear();
            } else {
                pRunMonth = wPR.getApprovedMonthInd();
                pRunYear = wPR.getApprovedYearInd();
            }
        }

        return this.createModelForView(pRunMonth, pRunYear, model, bc);
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"rm", "ry"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                            Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, ParseException, InstantiationException, IllegalAccessException {

        SessionManagerService.manageSession(request, model);

        return this.createModelForView(pRunMonth, pRunYear, model, this.getBusinessCertificate(request));


    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_close", required = false) String pClose,
                                @RequestParam(value = "_update", required = false) String pUpdateReport,
                                @ModelAttribute("statBean") MaterialityDisplayBean pMDB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CLOSE)) {
            return "redirect:paystubsReportForm.do?rm=" + pMDB.getRunMonth() + "&ry=" + pMDB.getRunYear();
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_UPD)) {
            if (pMDB.getRunMonth() == -1) {
                result.rejectValue("", "Invalid.Value", "Please select a value for 'Payroll Run Month'.");
                addDisplayErrorsToModel(model, request);
                model.addAttribute("pageErrors", result);
                model.addAttribute("statBean", pMDB);
                model.addAttribute("roleBean", bc);

                return VIEW;
            }
            if (pMDB.getRunYear() == -1) {
                result.rejectValue("", "Invalid.Value", "Please select a value for 'Payroll Run Year'.");
                addDisplayErrorsToModel(model, request);
                model.addAttribute("pageErrors", result);
                model.addAttribute("statBean", pMDB);
                model.addAttribute("roleBean", bc);

                return VIEW;
            }
            return "redirect:payrollRunStatistics.do?rm=" + pMDB.getRunMonth() + "&ry=" + pMDB.getRunYear();
        }

        return "redirect:payrollRunStatistics.do";

    }

    private String createModelForView(int pRunMonth, int pRunYear, Model pModel, BusinessCertificate pBusinessCertificate) throws InstantiationException, IllegalAccessException {

        MaterialityDisplayBean wMDB = new MaterialityDisplayBean();


        int wTotalNoProcessed = IppmsUtilsExt.countNoOfPayChecks(genericService, pBusinessCertificate, pRunMonth, pRunYear, false);
        wMDB.setNoOfEmployeesProcessed(wTotalNoProcessed);
        //Net Pay for Active Employees...
        NamedEntityBean wNamedEntity = this.statisticsService.loadPaycheckSummaryInfoByMonthAndYear(pBusinessCertificate, pRunMonth, pRunYear);


        wMDB.setNetPaySum(wNamedEntity.getNetPay());
        wMDB.setTotalPaySum(wNamedEntity.getTotalPay());
        wMDB.setTotalDeductionSum(wNamedEntity.getTotalDeductions());
        wMDB.setNoOfEmployeesPaid(wNamedEntity.getNoOfActiveEmployees());

        //--- Employees NOT Paid.
        int wSerialNum = 1;


        // wMDB.setEmployeesNotPaidByBirthDate(wNamedEntity.getRetiredByBirthDate());


        wMDB = this.statisticsService.loadEmployeesNotPaidByMonthAndYear(pBusinessCertificate, pRunMonth, pRunYear, wMDB);
        if (pBusinessCertificate.isPensioner()) {
            if (wMDB.getPensionersNotPaidByPensionCalc() > 0)
                wMDB.setNoOfPenForRecalc(++wSerialNum);


        } else {
            if (wMDB.getEmployeesNotPaidByBirthDate() > 0)
                wMDB.setNoOfEmpRetiredByBirthDate(++wSerialNum);


            //wMDB.setEmployeesNotPaidByHireDate(wNamedEntity.getRetiredByHireDate());

            if (wMDB.getEmployeesNotPaidByHireDate() > 0)
                wMDB.setNoOfEmpRetiredByHireDate(++wSerialNum);

            wNamedEntity = this.statisticsService.loadEmployeesPaidByContract(pBusinessCertificate, pRunMonth, pRunYear, false);

            wMDB.setEmployeesPaidByContract(wNamedEntity.getNoOfActiveEmployees());
            wMDB.setNetPayContract(wNamedEntity.getNetPay());
            wMDB.setTotalPayContract(wNamedEntity.getTotalPay());

            if (wMDB.getEmployeesPaidByContract() > 0)
                wMDB.setNoOfEmpPaidByContract(++wSerialNum);

            // System.out.println("wSerialNum in paid contract in view is " +wSerialNum);

            wNamedEntity = this.statisticsService.loadEmployeesPaidByContract(pBusinessCertificate, pRunMonth, pRunYear, true);

            wMDB.setEmployeesNotPaidByContract(wNamedEntity.getNoOfActiveEmployees());

            //    System.out.println("Employee not paid on contract in view is "+wMDB.getEmployeesNotPaidByContract());

            if (wMDB.getEmployeesNotPaidByContract() > 0)
                wMDB.setNoOfEmpNotPaidByContract(++wSerialNum);

            wNamedEntity = this.statisticsService.loadNoOfEmployeesPaidByInterdiction(pBusinessCertificate, pRunMonth, pRunYear);
            wMDB.setEmployeesOnInterdiction(wNamedEntity.getNoOfActiveEmployees());
            if (wMDB.getEmployeesOnInterdiction() > 0) {
                wMDB.setNoOfEmpPaidByInterdiction(++wSerialNum);
                wMDB.setNetPayInterdiction(wNamedEntity.getNetPay());
                wMDB.setTotalPayInterdiction(wNamedEntity.getTotalPay());

            }

            wNamedEntity = this.statisticsService.loadEmployeesPaidByDays(pBusinessCertificate, pRunMonth, pRunYear);

            wMDB.setNetPayByDays(wNamedEntity.getNetPayByDays());
            wMDB.setTotalPayByDays(wNamedEntity.getTotalPay());
            wMDB.setNoOfEmployeesPaidByDays(wNamedEntity.getNoOfEmployeesPaidByDays());
            if (wMDB.getNoOfEmployeesPaidByDays() > 0)
                wMDB.setNoOfEmpPaidByDays(++wSerialNum);

            wNamedEntity = this.statisticsService.loadEmployeesWithNegativeNetPay(pBusinessCertificate, pRunMonth, pRunYear);

            wMDB.setNoOfEmployeesWithNegativePay(wNamedEntity.getNoOfEmployeesWithNegPay());

            if (wNamedEntity.getNoOfEmployeesWithNegPay() > 0) {
                wMDB.setNegativePaySum(wNamedEntity.getNegativePay());
                wMDB.setNegativePayTotalSum(wNamedEntity.getTotalPay());
                wMDB.setNoOfEmpWithNegPay(++wSerialNum);
            }

        }


        wMDB.setEmployeesNotPaidByTermination(this.statisticsService.loadEmpNotPaidDueToReason(pBusinessCertificate, pRunMonth, pRunYear, false, true, false));
        if (wMDB.getEmployeesNotPaidByTermination() > 0)
            wMDB.setNoOfEmpRetiredByTermination(++wSerialNum);


        wMDB.setEmployeesNotPaidBySuspension(this.statisticsService.loadEmpNotPaidDueToReason(pBusinessCertificate, pRunMonth, pRunYear, true, false, false));
        if (wMDB.getEmployeesNotPaidBySuspension() > 0)
            wMDB.setNoOfEmpRetiredBySuspension(++wSerialNum);

        wMDB.setEmployeesNotPaidByApproval(this.statisticsService.loadEmpNotPaidDueToReason(pBusinessCertificate, pRunMonth, pRunYear, false, false, true));
        if (wMDB.getEmployeesNotPaidByApproval() > 0)
            wMDB.setNoOfEmpNotPaidByApproval(++wSerialNum);


        //Now Get the Number of Employees Paid Special Allowances...
        wNamedEntity = this.statisticsService.loadEmployeesPaidSpecialAllowancesByMonthAndYear(pBusinessCertificate, pRunMonth, pRunYear);

        wMDB.setSpecialAllowance(wNamedEntity.getNetPayByDays());
        wMDB.setNoOfEmpPaidSpecAllow(wNamedEntity.getNoOfEmployeesPaidByDays());
        wMDB.setTotalPaySpecAllow(wNamedEntity.getTotalPayStr());
        if (wMDB.getNoOfEmpPaidSpecAllow() > 0)
            wMDB.setNoOfEmpPaidSpecialAllowance(++wSerialNum);


        wMDB.setRunMonth(pRunMonth);
        wMDB.setRunYear(pRunYear);

        PayrollRunMasterBean wPRMB = IppmsUtilsExt.getPayrollRunMasterBean(genericService, pBusinessCertificate.getBusinessClientInstId(), pRunMonth, pRunYear);
        if (wTotalNoProcessed == 0)
            pModel.addAttribute("no_recs", true);
        else {
            if (!wPRMB.isNewEntity()) {
                //There is a bug that happens maybe cos of Multithreading...we need to investigate more.....
                if (wPRMB.getEndTime() == null) {
                    wPRMB.setEndTime(PayrollBeanUtils.getCurrentTime(false));
                }
                wPRMB.setPayrollRunTime(PayrollUtils.getElapseTime(wPRMB.getStartDate(), wPRMB.getStartTime(), wPRMB.getEndDate(), wPRMB.getEndTime()));


            }

            pModel.addAttribute("no_recs", false);
        }
        pModel.addAttribute("configImpact", genericService.loadObjectWithSingleCondition(PayrollConfigImpact.class, CustomPredicate.procurePredicate("masterBean.id", wPRMB.getId())));
        pModel.addAttribute("statMasterBean", wPRMB);

        pModel.addAttribute("statBean", wMDB);
        pModel.addAttribute("roleBean", pBusinessCertificate);

        return VIEW;

    }

}
