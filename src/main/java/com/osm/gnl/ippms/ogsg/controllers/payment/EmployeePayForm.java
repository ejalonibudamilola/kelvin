package com.osm.gnl.ippms.ogsg.controllers.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.*;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.PayrollConfigImpact;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RbaConfigBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.approvals.AmAliveHelperService;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriod;
import com.osm.gnl.ippms.ogsg.domain.payroll.*;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.domain.subvention.SubventionHistory;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayPerEmployee;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayPerPensioner;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayroll;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayrollPensioner;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBeanPension;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysBean;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import com.osm.gnl.ippms.ogsg.validators.payment.EmployeePayFormValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequestMapping({"/paydayForm.do"})
@SessionAttributes(types = {PayPeriodDaysMiniBean.class, PayPeriodDaysBean.class})
@Slf4j
public class EmployeePayForm extends BaseController {


    private final EmployeePayFormValidator validator;
    private final HrServiceHelper hrServiceHelper;
    private final PensionService pensionService;
    private final GenericService genericService;
    private final PaycheckService paycheckService;
    private final TransferService transferService;
    private final PayrollService payrollService;
    private final DeductionService deductionService;
    private final LoanService loanService;
    private final SpecAllowService specAllowService;
    private final PayrollHelperService payrollHelperService;
    private final StatisticsService statisticsService;
    private final StoredProcedureService storedProcedureService;

    private HashMap<Long, List<AbstractDeductionEntity>> fEmpDedMap;
    private HashMap<Long, List<AbstractGarnishmentEntity>> fGarnMap;
    private HashMap<Long, List<AbstractSpecialAllowanceEntity>> fAllowanceMap;
    private CalculatePayPerEmployee fCalcPayPerEmployee;
    private CalculatePayPerPensioner fCalcPayPerPensioner;
    private String fPayPeriod;
    private LocalDate fPayDate;
    private LocalDate fPayPeriodStart;
    private LocalDate fPayPeriodEnd;
    private String fCurrentUser;
    private int fNoOfElements;
    private int mNoOfMda;
    private int noOfDeduction;
    private int noOfSpecAllow;
    private int noOfLoan;

    private final String VIEW_NAME = "payment/paydayFormNew";

    @Autowired
    public EmployeePayForm(EmployeePayFormValidator validator, HrServiceHelper hrServiceHelper, GenericService genericService, PaycheckService paycheckService, TransferService transferService, PensionService pensionService,
                           PayrollService payrollService, DeductionService deductionService, LoanService loanService,
                           SpecAllowService specAllowService, PayrollHelperService payrollHelperService, StatisticsService statisticsService, StoredProcedureService storedProcedureService) {
        this.validator = validator;
        this.hrServiceHelper = hrServiceHelper;
        this.genericService = genericService;
        this.paycheckService = paycheckService;
        this.transferService = transferService;
        this.payrollService = payrollService;
        this.deductionService = deductionService;
        this.specAllowService = specAllowService;
        this.pensionService = pensionService;
        this.loanService = loanService;
        this.payrollHelperService = payrollHelperService;
        this.statisticsService = statisticsService;
        this.storedProcedureService = storedProcedureService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (IppmsUtilsExt.pendingPaychecksExists(genericService, bc)) {
            return "redirect:resolvePendingPaychecksForm.do";
        }

        PayrollFlag pf = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);


        if (this.transferService.getTotalNoOfActiveTransferApprovals(bc.getBusinessClientInstId()) > 0) {
            return "redirect:pendingTransferApprovals.do";
        }
        if (!bc.isPensioner())
            if (this.transferService.getTotalNoOfActiveAllowanceApprovals(bc.getBusinessClientInstId()) > 0)
                return "redirect:pendingPayrollApprovals.do";

        if (this.transferService.getNoOfEmpForPayrollApproval(bc, false) > 0) {
            return "redirect:pendingPayrollApprovals.do";
        }
        PayPeriod pp = genericService.loadObjectWithSingleCondition(PayPeriod.class, CustomPredicate.procurePredicate("defaultInd", 1));

        PayPeriodDaysBean ppB = PayrollBeanUtils.getMonthlyPayPeriod(pf);
        String wPayPeriodName = "Every Month";

        PaginationBean paginationBean = getPaginationInfo(request);

        LocalDate defPayPeriodEnd = PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), false);

        List<AbstractPaycheckEntity> empList = findAllEmployees(bc, ppB, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength);

        if ((empList != null) && (!empList.isEmpty())) {
            this.fNoOfElements = this.payrollService.getTotalNoOfPayableEmployees(bc, PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), true),  PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), false));
            this.mNoOfMda = this.payrollService.getTotalNoOfMdaProcessed(bc);
            this.noOfSpecAllow = this.specAllowService.getTotalNoOfToBePaidSpecAllow(bc, PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), true),  PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), false));
            this.noOfDeduction = this.deductionService.getTotalNoOfToBePaidDeduction(bc, PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), true),  PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), false));
            this.noOfLoan = this.loanService.getTotalNoOfToBePaidLoan(bc);
        }


        PayPeriodDaysMiniBean pPDMB = new PayPeriodDaysMiniBean(empList, paginationBean.getPageNumber(), this.pageLength, this.fNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        DevelopmentLevy wDLI = this.genericService.loadObjectUsingRestriction(DevelopmentLevy.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("year", defPayPeriodEnd.getYear())));

        if (wDLI.isNewEntity()) {
            pPDMB.setShowDeductDevLevy(SHOW_ROW);
        } else {
            pPDMB.setShowDeductDevLevy(HIDE_ROW);
        }

        pPDMB.setPayPeriodName(wPayPeriodName);
        pPDMB.setParentInstId(bc.getBusinessClientInstId());
        pPDMB.setPayPeriodValue(pp.getPayPeriodValue());
        pPDMB.setPayrollFlag(pf);
        pPDMB.setCurrentPayPeriodStart(ppB.getCurrentPayPeriodStart());
        pPDMB.setCurrentPayPeriodEnd(ppB.getCurrentPayPeriodEnd());
        pPDMB.setPayPeriod(ppB.getPayPeriod());
        pPDMB.setPayPeriodDaysBean(ppB);
        pPDMB.setPayDate(defPayPeriodEnd);
        pPDMB.setPayPeriodDaysListInternal(ppB.getPayPeriodList());
        List<Subvention> subventionList = this.genericService.loadAllObjectsUsingRestrictions(Subvention.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("expire", 0),
                CustomPredicate.procurePredicate("employeeBoundInd", 0)), null);
        for(Subvention subvention : subventionList)
                subvention.setAmountStr(subvention.getSubventionAmountStr());

        pPDMB.setSubventionList(subventionList);

        pPDMB.setToBeProcessedStaff(this.fNoOfElements);
        pPDMB.setToBeProcessedMda(this.mNoOfMda);
        pPDMB.setToBeProcessedDeduction(this.noOfDeduction);
        pPDMB.setToBeProcessedLoan(this.noOfLoan);
        pPDMB.setToBeProcessedSpecAllow(this.noOfSpecAllow);


        //get the projected pay

        int pRunMonth = (pPDMB.getPayDate().getMonthValue())-1;
        int pRunYear = pPDMB.getPayDate().getYear();
        if(pRunMonth == 0){
            pRunMonth = 12;
            pRunYear -= 1;
        }

        NamedEntityBean wNamedEntity =  this.statisticsService.loadPaycheckSummaryInfoByMonthAndYear(bc,pRunMonth,pRunYear);
        double lastMonthGrossPay = wNamedEntity.getTotalPay();
        double lastMonthNetPay = wNamedEntity.getNetPay();
        int lastMonthEmp = wNamedEntity.getNoOfActiveEmployees();
        double projectedGrossPay;
        double projectedNetPay;

        if(lastMonthEmp == 0){
            projectedGrossPay = lastMonthGrossPay;
            projectedNetPay = lastMonthNetPay;
        }
        else{
            BigDecimal grossPayBD = new BigDecimal(String.valueOf((lastMonthGrossPay*this.fNoOfElements)/lastMonthEmp)).setScale(2, RoundingMode.FLOOR);
            projectedGrossPay = grossPayBD.doubleValue();
            grossPayBD = new BigDecimal(String.valueOf((lastMonthNetPay*this.fNoOfElements)/lastMonthEmp)).setScale(2, RoundingMode.FLOOR);
            projectedNetPay =  grossPayBD.doubleValue();
        }

        pPDMB.setProjectedGrossPayStr(naira + PayrollHRUtils.getDecimalFormat().format(projectedGrossPay));
        pPDMB.setProjectedNetPayStr(naira + PayrollHRUtils.getDecimalFormat().format(projectedNetPay));
        pPDMB.setLastMonthGrossPayStr(naira + PayrollHRUtils.getDecimalFormat().format(lastMonthGrossPay));

        model.addAttribute("payPeriodsList", ppB.getPayPeriodList());
        model.addAttribute("payPeriods", pPDMB);

        model.addAttribute("roleBean", bc);
        model.addAttribute("payDay", pPDMB.getPayDate().getMonth()+","+pPDMB.getPayDate().getYear());
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@ModelAttribute("payPeriods") PayPeriodDaysMiniBean ppDMB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_CREATE)) {

            ppDMB.setDeleteWarningIssued(true);
            ppDMB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
            result.rejectValue("", "No.Employees", "Please confirm Payroll Run for  "+PayrollHRUtils.getMonthAndYearFromDate(ppDMB.getPayDate()));

            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("payDay", ppDMB.getPayDate().getMonth()+","+ppDMB.getPayDate().getYear());
            model.addAttribute("payPeriods", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;


        }

        if (isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)){

            if(!ppDMB.getGeneratedCaptcha().equalsIgnoreCase(ppDMB.getEnteredCaptcha())) {
                ppDMB.setCaptchaError(true);
                result.rejectValue("", "Warning.Value", "Entered Code does not match Generated Code. Please retry.");
                ppDMB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
                model.addAttribute("pageErrors", result);
                model.addAttribute("payDay", ppDMB.getPayDate().getMonth()+","+ppDMB.getPayDate().getYear());
                model.addAttribute("payPeriods", ppDMB);
                model.addAttribute("roleBean", bc);
                return VIEW_NAME;
            }

            validator.validate(ppDMB, result, bc);
            if (result.hasErrors()) {
                addDisplayErrorsToModel(model, request);
                model.addAttribute("pageErrors", result);
                model.addAttribute("payDay", ppDMB.getPayDate().getMonth()+","+ppDMB.getPayDate().getYear());
                model.addAttribute("payPeriodsList", ((PayPeriodDaysMiniBean) result.getTarget()).getPayPeriodDaysListInternal());
                model.addAttribute("payPeriods", ppDMB);
                model.addAttribute("roleBean", bc);
                return VIEW_NAME;
            }

            if (ppDMB.getFullListSize() < 1) {
                result.rejectValue("", "No.Employees", "No " + bc.getStaffTypeName() + " found for this Pay Period.");
                addDisplayErrorsToModel(model, request);
                model.addAttribute("pageErrors", result);
                model.addAttribute("payDay", ppDMB.getPayDate().getMonth()+","+ppDMB.getPayDate().getYear());
                model.addAttribute("payPeriodsList", ((PayPeriodDaysMiniBean) result.getTarget()).getPayPeriodDaysListInternal());
                model.addAttribute("payPeriods", ppDMB);
                model.addAttribute("roleBean", bc);
                return VIEW_NAME;
            }
            this.fPayPeriodStart =  getDateFromPayPeriod(ppDMB.getPayPeriod(), true);
            this.fPayPeriodEnd =  getDateFromPayPeriod(ppDMB.getPayPeriod(), false);
            ConfigurationBean configurationBean = loadConfigurationBean(request);
            //--Check for Pensioner.
//            if (bc.isPensioner()) {
//
//
//
//                if (configurationBean.isUseIAmAlive()) {
//                    //First check for Pending Approval First....
//                    int count = AmAliveHelperService.noOfPendingAmAliveApprovals(bc,genericService);
//                    if(count > 0){
//                        result.rejectValue("", "No.Employees", "There are  " + count + " " + "Pending 'Am Alive' Approvals pending for this pay period");
//                        result.rejectValue("", "No.Employees", "Click here --> <a href=/ogsg_ippms/viewPendingAmAliveApprovals.do <i>to view pending 'I Am Alive' Approvals</i></a> ");
//                        addDisplayErrorsToModel(model, request);
//                        model.addAttribute("pageErrors", result);
//                        model.addAttribute("payPeriodsList", ((PayPeriodDaysMiniBean) result.getTarget()).getPayPeriodDaysListInternal());
//                        model.addAttribute("payPeriods", ppDMB);
//                        model.addAttribute("payDay", ppDMB.getPayDate().getMonth()+","+ppDMB.getPayDate().getYear());
//                        model.addAttribute("roleBean", bc);
//                        return VIEW_NAME;
//                    }else {
//                        List<HiringInfo> hiringInfoList = this.hrServiceHelper.getEmpByExpRetireDate(bc, this.fPayPeriodStart, this.fPayPeriodEnd, true);
//                        if (IppmsUtils.isNotNullOrEmpty(hiringInfoList)) {
//                            result.rejectValue("", "No.Employees", "There are  " + hiringInfoList.size() + " " + bc.getStaffTypeName() + "(s) found having 'I Am Alive' Indicator set for this Pay Period");
//                            result.rejectValue("", "No.Employees", "Click here --> <a href=/ogsg_ippms/treatIamAlive.do?rm=" + fPayPeriodEnd.getMonthValue() + "&ry=" + fPayPeriodEnd.getYear() + " <i>to treat treat 'I Am Alive' Pensioners</i></a> ");
//                            addDisplayErrorsToModel(model, request);
//                            model.addAttribute("pageErrors", result);
//                            model.addAttribute("payPeriodsList", ((PayPeriodDaysMiniBean) result.getTarget()).getPayPeriodDaysListInternal());
//                            model.addAttribute("payPeriods", ppDMB);
//                            model.addAttribute("payDay", ppDMB.getPayDate().getMonth()+","+ppDMB.getPayDate().getYear());
//                            model.addAttribute("roleBean", bc);
//                            return VIEW_NAME;
//                        }
//                    }
//
//                }
//            }
            this.fPayDate = ppDMB.getPayDate();
            this.fPayPeriod = ppDMB.getPayPeriod();


            this.fCurrentUser = bc.getUserName();

            for (Subvention s : ppDMB.getSubventionList()) {
                SubventionHistory wSH = this.genericService.loadObjectUsingRestriction(SubventionHistory.class, Arrays.asList(CustomPredicate.procurePredicate("subvention.businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("runMonth", this.fPayPeriodEnd.getMonthValue()), CustomPredicate.procurePredicate("runYear", this.fPayPeriodEnd.getYear()), CustomPredicate.procurePredicate("subvention.id", s.getId())));

                if (wSH.isNewEntity()) {
                    wSH.setRunMonth(fPayPeriodEnd.getMonthValue());
                    wSH.setRunYear(fPayPeriodEnd.getYear());
                    wSH.setSubvention(s);
                }

                wSH.setLastModBy(this.fCurrentUser);
                wSH.setLastModTs(LocalDate.now());

                wSH.setAmount(Double.parseDouble(PayrollHRUtils.removeCommas(s.getAmountStr())));

                this.genericService.storeObject(wSH);
            }
            //Set the Bound one separately.
           Subvention subvention = this.genericService.loadObjectUsingRestriction(Subvention.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("employeeBoundInd", 1)));
           if (subvention.isNewEntity()) {
               //Create GNL Subvention
               subvention.setName("Payroll Processing Fee");
               subvention.setDescription("Payroll Processing Fee");
               subvention.setAmount(0.0D);
               subvention.setBaseAmount(47.0D);
               subvention.setEmployeeBoundInd(ON);
               subvention.setCreatedBy(new User(bc.getLoginId()));
               subvention.setBusinessClientId(bc.getBusinessClientInstId());
               subvention.setLastModBy(new User(bc.getLoginId()));
               subvention.setLastModTs(Timestamp.from(Instant.now()));
               subvention.setExpire(0);
               this.genericService.saveObject(subvention);
           }
           SubventionHistory wSH = this.genericService.loadObjectUsingRestriction(SubventionHistory.class, Arrays.asList(CustomPredicate.procurePredicate("subvention.businessClientId", bc.getBusinessClientInstId()),
                   CustomPredicate.procurePredicate("runMonth", this.fPayPeriodEnd.getMonthValue()), CustomPredicate.procurePredicate("runYear", this.fPayPeriodEnd.getYear()), CustomPredicate.procurePredicate("subvention.id", subvention.getId())));
           if (wSH.isNewEntity()) {
               wSH.setRunMonth(fPayPeriodEnd.getMonthValue());
               wSH.setRunYear(fPayPeriodEnd.getYear());
               wSH.setSubvention(subvention);
           }
//
           wSH.setLastModBy(this.fCurrentUser);
           wSH.setLastModTs(LocalDate.now());
           Double amount = EntityUtils.convertDoubleToEpmStandard(subvention.getBaseAmount() * this.fNoOfElements);
           Double taxes = EntityUtils.convertDoubleToEpmStandard(amount * .075);
           wSH.setAmount(amount + taxes);
           this.genericService.storeObject(wSH);


            HashMap<Long, SuspensionLog> wPartPayments = this.payrollService.loadToBePaidSuspendedEmployees(bc);
            List<AbstractDeductionEntity> wEmpListToSplit = this.deductionService.loadToBePaidEmployeeDeductions(bc, this.fPayPeriodStart, this.fPayPeriodEnd, false);
            this.fEmpDedMap = EntityUtils.breakUpDeductionList(wEmpListToSplit, this.fPayPeriodStart, this.fPayPeriodEnd);
            if (!bc.isPensioner()) {
                List<AbstractGarnishmentEntity> wGarnListToSplit = this.loanService.loadToBePaidEmployeeGarnishments(bc, fPayPeriodStart, false);
                this.fGarnMap = EntityUtils.breakUpGarnishmentList(wGarnListToSplit);
            }
            List<AbstractSpecialAllowanceEntity> wAllowListToSplit = this.specAllowService.loadToBePaidEmployeeSpecialAllowances(bc, this.fPayPeriodStart, this.fPayPeriodEnd, false);

            this.fAllowanceMap = EntityUtils.breakUpAllowanceList(wAllowListToSplit, fPayPeriodStart, fPayPeriodEnd);

            List<Long> absorbList = this.payrollHelperService.makeAbsorbReinstateList(bc, true, fPayPeriodStart.getMonthValue(), fPayPeriodStart.getYear());
            List<Long> reinstateList = this.payrollHelperService.makeAbsorbReinstateList(bc, false, fPayPeriodStart.getMonthValue(), fPayPeriodStart.getYear());


            List<SalaryInfo> wList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, getBusinessClientIdPredicate(request), null);
            Map<Long, SalaryInfo> wMap = EntityUtils.breakSalaryInfo(wList);


            //Add Percentage Payment if one exists...
            GlobalPercentConfig globalPercentConfig = IppmsUtilsExt.loadActiveGlobalPercentConfigByClient(genericService, bc);
            if (!globalPercentConfig.isNewEntity()) {
                if (!globalPercentConfig.isGlobalApply())
                    globalPercentConfig.setConfigDetailsList(genericService.loadAllObjectsWithSingleCondition(GlobalPercentConfigDetails.class, CustomPredicate.procurePredicate("parentId", globalPercentConfig.getId()), null));
            }


            if (bc.isPensioner()) {
                this.fCalcPayPerPensioner = new CalculatePayPerPensioner();
                this.fCalcPayPerPensioner.setReinstatements(reinstateList);
                this.fCalcPayPerPensioner.setAbsorptions(absorbList);
                this.fCalcPayPerPensioner.setSixtyYearsAgo(PayrollBeanUtils.calculate60yrsAgo(fPayPeriodEnd, bc.isPensioner(), configurationBean));
                this.fCalcPayPerPensioner.setThirtyFiveYearsAgo(PayrollBeanUtils.calculate35YearsAgo(this.fPayPeriodEnd, configurationBean));
                this.fCalcPayPerPensioner.setPayPeriodEnd(this.fPayPeriodEnd);
                this.fCalcPayPerPensioner.setEmployeeDeductions(this.fEmpDedMap);
                // this.fCalcPayPerPensioner.setEmployeeGarnishments(this.fGarnMap);
                this.fCalcPayPerPensioner.setSpecialAllowances(this.fAllowanceMap);
                this.fCalcPayPerPensioner.setPartPaymentMap(wPartPayments);
                this.fCalcPayPerPensioner.setGenericService(this.genericService);
                // this.fCalcPayPerPensioner.setZeroLastPayEmployees(wZeroedEmployees);
                this.fCalcPayPerPensioner.setBusinessCertificate(bc);
                this.fCalcPayPerPensioner.setUseIamAlive(configurationBean.isUseIAmAlive());
                if (Boolean.valueOf(ppDMB.getDeductDevelopmentLevy()).booleanValue()) {
                    this.fCalcPayPerPensioner.setDeductDevelopmentLevy(true);
                }

                this.fCalcPayPerPensioner.setSalaryInfoMap(wMap);
                this.fCalcPayPerPensioner.setRunMonth(this.fPayPeriodEnd.getMonthValue());
                this.fCalcPayPerPensioner.setRunYear(this.fPayPeriodEnd.getYear());

        } else {
            Map<Long,AllowanceRuleMaster> allowanceRuleMasterMap =  EntityUtils.breakUpPayGroupAllowanceList(
                    this.genericService.loadAllObjectsUsingRestrictions(AllowanceRuleMaster.class,
                            Arrays.asList(CustomPredicate.procurePredicate("activeInd",ON),getBusinessClientIdPredicate(request),
                                    CustomPredicate.procurePredicate("ruleStartDate",fPayPeriodStart,Operation.LESS_OR_EQUAL),CustomPredicate.procurePredicate("ruleEndDate",fPayPeriodEnd, Operation.GREATER_OR_EQUAL)), null));

            this.fCalcPayPerEmployee = new CalculatePayPerEmployee();

            this.fCalcPayPerEmployee.setReinstatements(reinstateList);
            this.fCalcPayPerEmployee.setAbsorptions(absorbList);
            this.fCalcPayPerEmployee.setGlobalPercentConfig(globalPercentConfig);
            this.fCalcPayPerEmployee.setSixtyYearsAgo(PayrollBeanUtils.calculate60yrsAgo(fPayPeriodEnd, bc.isPensioner(), configurationBean));
            this.fCalcPayPerEmployee.setThirtyFiveYearsAgo(PayrollBeanUtils.calculate35YearsAgo(this.fPayPeriodEnd, configurationBean));
            this.fCalcPayPerEmployee.setPayPeriodEnd(this.fPayPeriodEnd);
            this.fCalcPayPerEmployee.setEmployeeDeductions(this.fEmpDedMap);
            this.fCalcPayPerEmployee.setEmployeeGarnishments(this.fGarnMap);
            this.fCalcPayPerEmployee.setSpecialAllowances(this.fAllowanceMap);
            this.fCalcPayPerEmployee.setPartPaymentMap(wPartPayments);
            this.fCalcPayPerEmployee.setGenericService(this.genericService);
            //this.fCalcPayPerEmployee.setZeroLastPayEmployees(wZeroedEmployees);
            this.fCalcPayPerEmployee.setBusinessCertificate(bc);
            this.fCalcPayPerEmployee.setPayGroupAllowanceRule(allowanceRuleMasterMap);

                if (Boolean.valueOf(ppDMB.getDeductDevelopmentLevy()).booleanValue()) {
                    this.fCalcPayPerEmployee.setDeductDevelopmentLevy(true);
                }

                this.fCalcPayPerEmployee.setSalaryInfoMap(wMap);
                this.fCalcPayPerEmployee.setRunMonth(this.fPayPeriodEnd.getMonthValue());
                this.fCalcPayPerEmployee.setRunYear(this.fPayPeriodEnd.getYear());

            }
            PayrollRunMasterBean wPMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(CustomPredicate.procurePredicate("runMonth", fPayPeriodEnd.getMonthValue()),
                    CustomPredicate.procurePredicate("runYear", fPayPeriodEnd.getYear()), getBusinessClientIdPredicate(request)));

            RbaConfigBean rbaConfigBean = genericService.loadObjectWithSingleCondition(RbaConfigBean.class, getBusinessClientIdPredicate(request));

            wPMB.setRunMonth(this.fPayPeriodEnd.getMonthValue());

            if (rbaConfigBean.isNewEntity()) {
                wPMB.setRbaPercentage(0.00D);
            } else {
                wPMB.setRbaPercentage(rbaConfigBean.getRbaPercentage());
            }
            wPMB.setRunYear(this.fPayPeriodEnd.getYear());
            wPMB.setInitiator(new User(bc.getLoginId()));
            wPMB.setPayrollStatus(ON);
            wPMB.setBusinessClientId(bc.getBusinessClientInstId());

        if (!globalPercentConfig.isNewEntity()) {
            wPMB.setGlobalPercentInd(ON);
            wPMB.setGlobalPercentConfig(new GlobalPercentConfig(globalPercentConfig.getId()));
        }
        wPMB.setStartDate(LocalDate.now());
        wPMB.setStartTime(PayrollBeanUtils.getCurrentTime(false));
        this.genericService.saveObject(wPMB);


            PayrollConfigImpact payrollConfigImpact = IppmsUtilsExt.configurePayrollConfigImpact(genericService,configurationBean,wPMB,bc);


            PfaInfo defPfa = genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("defaultInd", 1));
            List<MdaInfo> mdaInfoList = genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), null);

            this.storedProcedureService.callStoredProcedure(IConstants.RESET_IPPMS_PAYCHECK_SEQUENCES,bc.getBusinessClientInstId());
        if (bc.isPensioner()) {
            CalculatePayrollPensioner wCalcPay = new CalculatePayrollPensioner(genericService, payrollService, pensionService, bc, fPayDate, fPayPeriodStart, fPayPeriodEnd, fPayPeriod,
                    fCurrentUser, fCalcPayPerPensioner, ppDMB.getFullListSize(), wPMB, mdaInfoList, defPfa, configurationBean, payrollConfigImpact);

                addSessionAttribute(request, "myCalcPay", wCalcPay);
                addSessionAttribute(request, "ppStartDate", fPayPeriodStart);
                addSessionAttribute(request, "ppEndDate", fPayPeriodEnd);
                addSessionAttribute(request, "payDayDate", fPayDate);
                Thread t = new Thread(wCalcPay);
                t.start();
            } else {
                Map<Long,TerminateReason> terminateReasonMap = genericService.loadObjectAsMapWithConditions(TerminateReason.class, Arrays.asList(CustomPredicate.procurePredicate("finalizedInd",ON)),"id");
                this.fCalcPayPerEmployee.setTerminateReasonMap(terminateReasonMap);
                CalculatePayroll wCalcPay = new CalculatePayroll(this.genericService, this.payrollService, bc, this.fPayDate,
                        this.fPayPeriodStart, this.fPayPeriodEnd, this.fPayPeriod, this.fCurrentUser, this.fCalcPayPerEmployee, ppDMB.getFullListSize(), wPMB,
                        false, null, defPfa, mdaInfoList, null, false, configurationBean,payrollConfigImpact);
            addSessionAttribute(request, "myCalcPay", wCalcPay);
            addSessionAttribute(request, "ppStartDate", fPayPeriodStart);
            addSessionAttribute(request, "ppEndDate", fPayPeriodEnd);
            addSessionAttribute(request, "payDayDate", fPayDate);
            Thread t = new Thread(wCalcPay);
            t.start();
        }

        }

        return "redirect:displayStatus.do";
    }


    private LocalDate getDateFromPayPeriod(String pPayPeriod, boolean pFirst) {
        StringTokenizer str = new StringTokenizer(pPayPeriod, "-", false);
        String strToConvert = "";
        int count = 0;
        while (str.hasMoreElements()) {
            String nextToken = str.nextToken();
            if ((pFirst) && (count == 0)) {
                strToConvert = nextToken;
                break;
            }
            if ((count == 1) && (!pFirst)) {
                strToConvert = nextToken;
                break;
            }

            count++;
        }
        if ((strToConvert != null) && (!strToConvert.equals(""))) {
            LocalDate aDate = getCorrectDate(strToConvert);
            return aDate;
        }

        return null;
    }

    private LocalDate getCorrectDate(String pStrToConvert) {
        String day = pStrToConvert.substring(0, pStrToConvert.indexOf("/")).trim();
        String month = pStrToConvert.substring(pStrToConvert.indexOf("/") + 1, pStrToConvert.lastIndexOf("/")).trim();
        String year = pStrToConvert.substring(pStrToConvert.lastIndexOf("/") + 1).trim();

        int _day = Integer.parseInt(day);
        int _month = Integer.parseInt(month);
        int _year = Integer.parseInt(year);

        LocalDate cal = LocalDate.of(_year, _month, _day);

        return cal;
    }

    private List<AbstractPaycheckEntity> findAllEmployees(BusinessCertificate businessCertificate, PayPeriodDaysBean pPPDB, int pStartRow, int pEndRow) {
        List<AbstractPaycheckEntity> chosenOnes = new ArrayList<>();

        List<HiringInfo> hireInfoList = this.payrollService.loadPayableActiveHiringInfoByBusinessId(businessCertificate, pStartRow, pEndRow);

        boolean add = true;
        for (HiringInfo h : hireInfoList) {
            String empLastPayPeriod = IppmsUtils.treatNull(h.getLastPayPeriod());
            String empCurrentPayPeriod = IppmsUtils.treatNull(h.getCurrentPayPeriod());
            if ((empCurrentPayPeriod.equalsIgnoreCase("")) && (empLastPayPeriod.equalsIgnoreCase(""))) {
                h.setFirstTimePay(true);

            } else {

                h.setNormalPay(true);
            }

            if (add) {
                AbstractPaycheckEntity ePB;
                if (businessCertificate.isPensioner()) {
                    ePB = new EmployeePayBeanPension();
                    ((EmployeePayBeanPension) ePB).setEmployee(h.getPensioner());
                } else {
                    ePB = new EmployeePayBean();
                    ((EmployeePayBean) ePB).setEmployee(h.getEmployee());
                }

                ePB.setPayEmployee("true");

                ePB.setHiringInfo(h);

                if (h.getLastPayDate() != null)
                    ePB.setLastPayDate(PayrollPayUtils.formatDates(h.getLastPayDate()));
                else {
                    ePB.setLastPayDate("");
                }

                if ((ePB.getParentObject().getSalaryInfo() != null) && (!ePB.getParentObject().getSalaryInfo().isNewEntity())) {
                    ePB.setPaymentType("Salaried");
                    ePB.setName(ePB.getParentObject().getDisplayNameWivTitlePrefixed());
                    chosenOnes.add(ePB);
                } else {
                    log.error("Employee " + ePB.getParentObject().getEmployeeId() + " Will never get paid.");
                }

            }

        }

        return chosenOnes;
    }

}