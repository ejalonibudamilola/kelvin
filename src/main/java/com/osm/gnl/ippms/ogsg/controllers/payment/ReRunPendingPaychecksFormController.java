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
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.BusinessPaySchedule;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriod;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriodDays;
import com.osm.gnl.ippms.ogsg.domain.payroll.*;
import com.osm.gnl.ippms.ogsg.domain.report.SalaryDifferenceBean;
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
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Controller
@RequestMapping({"/reRunAllPaychecks.do"})
@SessionAttributes(types = {PayPeriodDaysMiniBean.class, PayPeriodDaysBean.class})
public class ReRunPendingPaychecksFormController extends BaseController {

    //private IPayroll payrollService;
    // private IPayrollExt payrollServiceExt;
    
    private final PayrollService payrollService;
    private final  TransferService transferService;
    private final  EmployeePayFormValidator validator;
    private final  DeductionService deductionService;
    private final  LoanService loanService;
    private final  SpecAllowService specAllowService;
    private final  PensionService pensionService;
    private final  RerunPayrollService rerunPayrollService;
    private final  PayrollHelperService payrollHelperService;

    private List<BusinessPaySchedule> busPaySchedule;
    private Collection<PayPeriod> payPeriodList;
    private Collection<PayPeriodDays> payPeriodDaysList;
    private final int pageLength = 20;
    private HashMap<Long, List<AbstractDeductionEntity>> fEmpDedMap;
    private HashMap<Long, List<AbstractGarnishmentEntity>> fGarnMap;
    private HashMap<Long, List<AbstractSpecialAllowanceEntity>> fAllowanceMap;
    private HashMap<Integer, SalaryDifferenceBean> fSalDiffBean;

    private String fPayPeriod;
    private LocalDate fPayDate;
    private LocalDate fPayPeriodStart;
    private LocalDate fPayPeriodEnd;
    private String fCurrentUser;
    private int fNoOfElements;
    private final String VIEW_NAME = "payment/paydayForm";
    private CalculatePayPerPensioner fCalcPayPerPensioner;
    private CalculatePayPerEmployee fCalcPayPerEmployee;

    @Autowired
    public ReRunPendingPaychecksFormController(PayrollService payrollService, TransferService transferService, EmployeePayFormValidator validator, DeductionService deductionService, LoanService loanService, SpecAllowService specAllowService, PensionService pensionService, RerunPayrollService rerunPayrollService, PayrollHelperService payrollHelperService) {
        this.payrollService = payrollService;
        this.transferService = transferService;
        this.validator = validator;
        this.deductionService = deductionService;
        this.loanService = loanService;
        this.specAllowService = specAllowService;
        this.pensionService = pensionService;
        this.rerunPayrollService = rerunPayrollService;
        this.payrollHelperService = payrollHelperService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (this.genericService.isObjectExisting(PayrollRerun.class, Arrays.asList(getBusinessClientIdPredicate(request)))) {
            return "redirect:approveRerunPaychecks.do";
        }

        PayrollFlag pf = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);


        if (this.transferService.getTotalNoOfActiveTransferApprovals(bc.getBusinessClientInstId()) > 0) {
            return "redirect:pendingTransferApprovals.do";
        }
        if (this.transferService.getNoOfEmpForPayrollApproval(bc, false) > 0) {
            return "redirect:pendingPayrollApprovals.do";
        }
        if(!bc.isPensioner()){
            if (this.transferService.getTotalNoOfActiveAllowanceApprovals(bc.getBusinessClientInstId()) > 0) {
                return "redirect:pendingAllowanceRuleApprovals.do";
            }
        }
        PayPeriod pp = genericService.loadObjectWithSingleCondition(PayPeriod.class, CustomPredicate.procurePredicate("defaultInd", 1));
        PayPeriodDaysBean ppB = PayrollBeanUtils.getMonthlyPayPeriod(pf);
        String wPayPeriodName = "Every Month";


        PaginationBean paginationBean = getPaginationInfo(request);

        LocalDate defPayPeriodEnd = PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), false);

        List<AbstractPaycheckEntity> empList = findAllEmployees(bc, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength);

        if ((empList != null) && (!empList.isEmpty())) {
            this.fNoOfElements = this.payrollService.getTotalNoOfPayableEmployees(bc, PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), true), PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), false));
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
        pPDMB.setReRun(true);

        model.addAttribute("payPeriodsList", ppB.getPayPeriodList());
        model.addAttribute("payPeriods", pPDMB);

        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@ModelAttribute("payPeriods") PayPeriodDaysMiniBean ppDMB, BindingResult result, SessionStatus status,
                                Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        validator.validateForRerun(ppDMB, result, genericService, bc);
        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("payPeriodsList", ((PayPeriodDaysMiniBean) result.getTarget()).getPayPeriodDaysListInternal());
            model.addAttribute("payPeriods", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }

        if (ppDMB.getFullListSize() < 1) {
            result.rejectValue("", "No.Employees", "No " + bc.getStaffTypeName() + " found for this Pay Period.");
            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("payPeriodsList", ((PayPeriodDaysMiniBean) result.getTarget()).getPayPeriodDaysListInternal());
            model.addAttribute("payPeriods", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }

        this.fPayDate = ppDMB.getPayDate();
        this.fPayPeriod = ppDMB.getPayPeriod();

        this.fPayPeriodStart = PayrollBeanUtils.getDateFromPayPeriod(this.fPayPeriod, true);
        this.fPayPeriodEnd = PayrollBeanUtils.getDateFromPayPeriod(this.fPayPeriod, false);


        this.fCurrentUser = bc.getUserName();

        List<AbstractGarnishmentEntity> wGarnListToSplit = null;
        List<AbstractDeductionEntity> wEmpListToSplit = this.deductionService.loadToBePaidEmployeeDeductions(bc, this.fPayPeriodStart, this.fPayPeriodEnd, false);
        if(!bc.isPensioner())
         wGarnListToSplit = this.loanService.loadToBePaidEmployeeGarnishments(bc, fPayPeriodStart, false);
        List<AbstractSpecialAllowanceEntity> wAllowListToSplit = this.specAllowService.loadToBePaidEmployeeSpecialAllowances(bc, this.fPayPeriodStart, this.fPayPeriodEnd, false);

        HashMap<Long, SuspensionLog> wPartPayments = this.payrollService.loadToBePaidSuspendedEmployees(bc);
        this.fEmpDedMap = EntityUtils.breakUpDeductionList(wEmpListToSplit, this.fPayPeriodStart, this.fPayPeriodEnd);
        if(!bc.isPensioner())
        this.fGarnMap = EntityUtils.breakUpGarnishmentList(wGarnListToSplit);
        this.fAllowanceMap = EntityUtils.breakUpAllowanceList(wAllowListToSplit, fPayPeriodStart, fPayPeriodEnd);


        List<SalaryInfo> wList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, getBusinessClientIdPredicate(request), null);
        Map<Long, SalaryInfo> wMap = EntityUtils.breakSalaryInfo(wList);

        List<Long> absorbList = this.payrollHelperService.makeAbsorbReinstateList(bc, true, fPayPeriodStart.getMonthValue(), fPayPeriodStart.getYear());
        List<Long> reinstateList = this.payrollHelperService.makeAbsorbReinstateList(bc, false, fPayPeriodStart.getMonthValue(), fPayPeriodStart.getYear());

        GlobalPercentConfig globalPercentConfig = IppmsUtilsExt.loadActiveGlobalPercentConfigByClient(genericService, bc);
        if (!globalPercentConfig.isNewEntity()) {
            if (!globalPercentConfig.isGlobalApply())
                globalPercentConfig.setConfigDetailsList(genericService.loadAllObjectsWithSingleCondition(GlobalPercentConfigDetails.class, CustomPredicate.procurePredicate("parentId", globalPercentConfig.getId()), null));
        }
        ConfigurationBean configurationBean = loadConfigurationBean(request);

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
            Map<Long, AllowanceRuleMaster> allowanceRuleMasterMap =  EntityUtils.breakUpPayGroupAllowanceList(
                    this.genericService.loadAllObjectsUsingRestrictions(AllowanceRuleMaster.class,
                            Arrays.asList(CustomPredicate.procurePredicate("activeInd",ON),getBusinessClientIdPredicate(request),
                                    CustomPredicate.procurePredicate("ruleStartDate",fPayPeriodStart, Operation.LESS_OR_EQUAL),CustomPredicate.procurePredicate("ruleEndDate",fPayPeriodEnd, Operation.GREATER_OR_EQUAL)), null));

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
            // this.fCalcPayPerEmployee.setZeroLastPayEmployees(wZeroedEmployees);
            this.fCalcPayPerEmployee.setPayGroupAllowanceRule(allowanceRuleMasterMap);
            this.fCalcPayPerEmployee.setBusinessCertificate(bc);

            if (Boolean.valueOf(ppDMB.getDeductDevelopmentLevy()).booleanValue()) {
                this.fCalcPayPerEmployee.setDeductDevelopmentLevy(true);
            }

            this.fCalcPayPerEmployee.setSalaryInfoMap(wMap);
            this.fCalcPayPerEmployee.setRunMonth(this.fPayPeriodEnd.getMonthValue());
            this.fCalcPayPerEmployee.setRunYear(this.fPayPeriodEnd.getYear());

        }
        PayrollRunMasterBean wPMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(CustomPredicate.procurePredicate("runMonth", fPayPeriodEnd.getMonthValue()),
                CustomPredicate.procurePredicate("runYear", fPayPeriodEnd.getYear()), getBusinessClientIdPredicate(request)));


        if (wPMB.isNewEntity()) {
            wPMB.setRunMonth(this.fPayPeriodEnd.getMonthValue());
            RbaConfigBean rbaConfigBean = genericService.loadObjectWithSingleCondition(RbaConfigBean.class, getBusinessClientIdPredicate(request));
            if (rbaConfigBean.isNewEntity()) {
                wPMB.setRbaPercentage(0.00D);
            } else {
                wPMB.setRbaPercentage(rbaConfigBean.getRbaPercentage());
            }
            wPMB.setRunYear(this.fPayPeriodEnd.getYear());
            wPMB.setInitiator(new User(bc.getLoginId()));
            wPMB.setPayrollStatus(ON);
            wPMB.setBusinessClientId(bc.getBusinessClientInstId());

        }
        if (!globalPercentConfig.isNewEntity()) {
            wPMB.setGlobalPercentConfig(globalPercentConfig);
            wPMB.setGlobalPercentInd(ON);
        }
        wPMB.setInitiator(new User(bc.getLoginId()));
        wPMB.setPayrollStatus(ON);
        wPMB.setStartDate(LocalDate.now());
        wPMB.setStartTime(PayrollBeanUtils.getCurrentTime(false));
        this.genericService.saveObject(wPMB);

        RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("runMonth", fPayPeriodEnd.getMonthValue()),
                CustomPredicate.procurePredicate("runYear", fPayPeriodEnd.getYear())));
        if (wRPB.isNewEntity()) {

            wRPB.setRunMonth(fPayPeriodEnd.getMonthValue());
            wRPB.setRunYear(fPayPeriodEnd.getYear());
            wRPB.setRerunInd(IConstants.ON);
            wRPB.setBusinessClientId(bc.getBusinessClientInstId());
            this.genericService.saveObject(wRPB);
        }
        PfaInfo defPfa = genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("defaultInd", 1));
        List<MdaInfo> mdaInfoList = genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), null);
        PayrollConfigImpact payrollConfigImpact = IppmsUtilsExt.configurePayrollConfigImpact(genericService,configurationBean,wPMB,bc);
        if (bc.isPensioner()) {
            CalculatePayrollPensioner wCalcPay = new CalculatePayrollPensioner(genericService, payrollService, pensionService, bc, fPayDate, fPayPeriodStart, fPayPeriodEnd, fPayPeriod,
                    fCurrentUser, fCalcPayPerPensioner, ppDMB.getFullListSize(), true, wPMB, wRPB, mdaInfoList, rerunPayrollService, false, defPfa, configurationBean,payrollConfigImpact);

            addSessionAttribute(request, "myCalcPay", wCalcPay);
            addSessionAttribute(request, "ppStartDate", fPayPeriodStart);
            addSessionAttribute(request, "ppEndDate", fPayPeriodEnd);
            addSessionAttribute(request, "payDayDate", fPayDate);
            Thread t = new Thread(wCalcPay);
            t.start();
        } else {
            Map<Long, TerminateReason> terminateReasonMap = genericService.loadObjectAsMapWithConditions(TerminateReason.class, Arrays.asList(CustomPredicate.procurePredicate("finalizedInd",ON)),"id");
            this.fCalcPayPerEmployee.setTerminateReasonMap(terminateReasonMap);
            CalculatePayroll wCalcPay = new CalculatePayroll(this.genericService, this.payrollService, bc, this.fPayDate,
                    this.fPayPeriodStart, fPayPeriodEnd, this.fPayPeriod, this.fCurrentUser, this.fCalcPayPerEmployee, ppDMB.getFullListSize(), wPMB, true, wRPB, defPfa, mdaInfoList,
                    rerunPayrollService, false,configurationBean,payrollConfigImpact);

            addSessionAttribute(request, "myCalcPay", wCalcPay);
            addSessionAttribute(request, "ppStartDate", this.fPayPeriodStart);
            addSessionAttribute(request, "ppEndDate", this.fPayPeriodEnd);
            addSessionAttribute(request, "payDayDate", this.fPayDate);
            Thread t = new Thread(wCalcPay);
            t.start();
        }


        return "redirect:displayStatus.do";
    }


    private List<AbstractPaycheckEntity> findAllEmployees(BusinessCertificate businessCertificate, int pStartRow, int pEndRow) {
        List<AbstractPaycheckEntity> chosenOnes = new ArrayList<>();

        List<HiringInfo> hireInfoList = this.payrollService.loadPayableActiveHiringInfoByBusinessId(businessCertificate, pStartRow, pEndRow);

        //PayrollPayUtils pt = new PayrollPayUtils();
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
                AbstractPaycheckEntity ePB = null;
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
