package com.osm.gnl.ippms.ogsg.controllers.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
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
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriod;
import com.osm.gnl.ippms.ogsg.domain.payroll.*;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayPerEmployee;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayPerPensioner;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayroll;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayrollPensioner;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
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
@RequestMapping({"/rerunPayroll.do"})
@SessionAttributes(types = {PayPeriodDaysMiniBean.class, PayPeriodDaysBean.class})
public class RerunPayrollFormController extends BaseController {

    private final EmployeePayFormValidator validator;
    private final DeductionService deductionService;
    private final LoanService loanService;
    private final SpecAllowService specAllowService;
    private final PayrollService payrollService;
    private final PensionService pensionService;
    private final RerunPayrollService rerunPayrollService;
    private final PayrollHelperService payrollHelperService;

    private CalculatePayPerPensioner fCalcPayPerPensioner;
    private CalculatePayPerEmployee fCalcPayPerEmployee;
    private final int pageLength = 20;
    private HashMap<Long, List<AbstractGarnishmentEntity>> fLoanMap;
    private String fCurrentUser;
    private int fNoOfElements;

    private final String VIEW_NAME = "payment/paydayRerunForm";

    @Autowired
    public RerunPayrollFormController(EmployeePayFormValidator validator, DeductionService deductionService, LoanService loanService, SpecAllowService specAllowService, PayrollService payrollService, PensionService pensionService, RerunPayrollService rerunPayrollService, PayrollHelperService payrollHelperService) {
        this.validator = validator;
        this.deductionService = deductionService;
        this.loanService = loanService;
        this.specAllowService = specAllowService;
        this.payrollService = payrollService;
        this.pensionService = pensionService;
        this.rerunPayrollService = rerunPayrollService;
        this.payrollHelperService = payrollHelperService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        PayrollFlag pf = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);


        PayPeriod pp = genericService.loadObjectWithSingleCondition(PayPeriod.class, CustomPredicate.procurePredicate("defaultInd", 1));

        PayPeriodDaysBean ppB = PayrollBeanUtils.getMonthlyPayPeriod(pf);
        String wPayPeriodName = "Every Month";
        PaginationBean paginationBean = getPaginationInfo(request);

        List<AbstractPaycheckEntity> empList = findAllEmployees(bc, ppB, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength);

        if ((empList != null) && (!empList.isEmpty())) {
            this.fNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(getBusinessClientIdPredicate(request)), PayrollRerun.class);
        }

        PayPeriodDaysMiniBean pPDMB = new PayPeriodDaysMiniBean(empList, paginationBean.getPageNumber(), this.pageLength, this.fNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        LocalDate defPayPeriodEnd = PayrollBeanUtils.getDateFromPayPeriod(ppB.getPayPeriod(), false);

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
        pPDMB.setRunMonth(defPayPeriodEnd.getMonthValue());
        pPDMB.setRunYear(defPayPeriodEnd.getYear());
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

        validator.validate(ppDMB, result, bc);
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
        //--Now a potential Bug Fix.
        //Check if this dude has already been run, if it has, send user to approve re-run deleted paychecks first.
        boolean reranPaychecksExists = false;
        for(AbstractPaycheckEntity paycheckEntity : ppDMB.getEmployeePayBean()){
            if(IppmsUtilsExt.paycheckExistsForEmployee(genericService,bc,ppDMB.getRunMonth(),ppDMB.getRunYear(),paycheckEntity.getAbstractEmployeeEntity().getId())){
                reranPaychecksExists = true;
                break;
            }
        }
        if(reranPaychecksExists){
            return "redirect:approveRerunPaychecks.do";
        }

        LocalDate fPayDate = ppDMB.getPayDate();
        //private HashMap<Integer, SalaryDifferenceBean> fSalDiffBean;
        String fPayPeriod = ppDMB.getPayPeriod();

        LocalDate fPayPeriodStart = getDateFromPayPeriod(ppDMB.getPayPeriod(), true);
        LocalDate fPayPeriodEnd = getDateFromPayPeriod(fPayPeriod, false);

        this.fCurrentUser = bc.getUserName();
        List<AbstractGarnishmentEntity> wGarnListToSplit = null;

        List<AbstractDeductionEntity> wEmpListToSplit = this.deductionService.loadToBePaidEmployeeDeductions(bc, fPayPeriodStart, fPayPeriodEnd, true);
        if(!bc.isPensioner())
         wGarnListToSplit = this.loanService.loadToBePaidEmployeeGarnishments(bc, fPayPeriodStart, true);
        List<AbstractSpecialAllowanceEntity> wAllowListToSplit = this.specAllowService.loadToBePaidEmployeeSpecialAllowances(bc, fPayPeriodStart, fPayPeriodEnd, true);

        List<Long> absorbList = this.payrollHelperService.makeAbsorbReinstateList(bc, true, fPayPeriodStart.getMonthValue(), fPayPeriodStart.getYear());
        List<Long> reinstateList = this.payrollHelperService.makeAbsorbReinstateList(bc, false, fPayPeriodStart.getMonthValue(), fPayPeriodStart.getYear());
        HashMap<Long, SuspensionLog> wPartPayments = this.payrollService.loadToBePaidSuspendedEmployees(bc);
        HashMap<Long, List<AbstractDeductionEntity>> fEmpDedMap = EntityUtils.breakUpDeductionList(wEmpListToSplit, fPayPeriodStart, fPayPeriodEnd);
        if(!bc.isPensioner())
        this.fLoanMap = EntityUtils.breakUpGarnishmentList(wGarnListToSplit);
        HashMap<Long, List<AbstractSpecialAllowanceEntity>> fAllowanceMap = EntityUtils.breakUpAllowanceList(wAllowListToSplit, fPayPeriodStart, fPayPeriodEnd);

        List<SalaryInfo> wList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, getBusinessClientIdPredicate(request), null);
        Map<Long, SalaryInfo> wMap = EntityUtils.breakSalaryInfo(wList);

        GlobalPercentConfig globalPercentConfig = IppmsUtilsExt.loadActiveGlobalPercentConfigByClient(genericService,bc);
        if(!globalPercentConfig.isNewEntity()){
            if(!globalPercentConfig.isGlobalApply())
                globalPercentConfig.setConfigDetailsList(genericService.loadAllObjectsWithSingleCondition(GlobalPercentConfigDetails.class, CustomPredicate.procurePredicate("parentId", globalPercentConfig.getId()), null));
        }
        ConfigurationBean configurationBean = loadConfigurationBean(request);

        if (bc.isPensioner()) {
            this.fCalcPayPerPensioner = new CalculatePayPerPensioner();
            this.fCalcPayPerPensioner.setReinstatements(reinstateList);
            this.fCalcPayPerPensioner.setAbsorptions(absorbList);
            this.fCalcPayPerPensioner.setSixtyYearsAgo(PayrollBeanUtils.calculate60yrsAgo(fPayPeriodEnd, bc.isPensioner(), configurationBean));
            this.fCalcPayPerPensioner.setThirtyFiveYearsAgo(PayrollBeanUtils.calculate35YearsAgo(fPayPeriodEnd, configurationBean));
            this.fCalcPayPerPensioner.setPayPeriodEnd(fPayPeriodEnd);
            this.fCalcPayPerPensioner.setEmployeeDeductions(fEmpDedMap);
          //  this.fCalcPayPerPensioner.setEmployeeGarnishments(this.fGarnMap);
            this.fCalcPayPerPensioner.setSpecialAllowances(fAllowanceMap);
            this.fCalcPayPerPensioner.setPartPaymentMap(wPartPayments);
            this.fCalcPayPerPensioner.setGenericService(this.genericService);
           // this.fCalcPayPerPensioner.setZeroLastPayEmployees(wZeroedEmployees);
            this.fCalcPayPerPensioner.setBusinessCertificate(bc);
            this.fCalcPayPerPensioner.setUseIamAlive(configurationBean.isUseIAmAlive());
            if (Boolean.valueOf(ppDMB.getDeductDevelopmentLevy()).booleanValue()) {
                this.fCalcPayPerPensioner.setDeductDevelopmentLevy(true);
            }

            this.fCalcPayPerPensioner.setSalaryInfoMap(wMap);
            this.fCalcPayPerPensioner.setRunMonth(fPayPeriodEnd.getMonthValue());
            this.fCalcPayPerPensioner.setRunYear(fPayPeriodEnd.getYear());
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
            this.fCalcPayPerEmployee.setThirtyFiveYearsAgo(PayrollBeanUtils.calculate35YearsAgo(fPayPeriodEnd, configurationBean));
            this.fCalcPayPerEmployee.setPayPeriodEnd(fPayPeriodEnd);
            this.fCalcPayPerEmployee.setEmployeeDeductions(fEmpDedMap);
            this.fCalcPayPerEmployee.setEmployeeGarnishments(this.fLoanMap);
            this.fCalcPayPerEmployee.setSpecialAllowances(fAllowanceMap);
            this.fCalcPayPerEmployee.setPartPaymentMap(wPartPayments);
            this.fCalcPayPerEmployee.setGenericService(this.genericService);
           // this.fCalcPayPerEmployee.setZeroLastPayEmployees(wZeroedEmployees);
            this.fCalcPayPerEmployee.setPayGroupAllowanceRule(allowanceRuleMasterMap);
            this.fCalcPayPerEmployee.setBusinessCertificate(bc);

            if (Boolean.valueOf(ppDMB.getDeductDevelopmentLevy()).booleanValue()) {
                this.fCalcPayPerEmployee.setDeductDevelopmentLevy(true);
            }

            this.fCalcPayPerEmployee.setSalaryInfoMap(wMap);
            this.fCalcPayPerEmployee.setRunMonth(fPayPeriodEnd.getMonthValue());
            this.fCalcPayPerEmployee.setRunYear(fPayPeriodEnd.getYear());

        }
        PayrollRunMasterBean wPMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(CustomPredicate.procurePredicate("runMonth", fPayPeriodEnd.getMonthValue()),
                CustomPredicate.procurePredicate("runYear", fPayPeriodEnd.getYear()), getBusinessClientIdPredicate(request)));


        if (wPMB.isNewEntity()) {
            wPMB.setRunMonth(fPayPeriodEnd.getMonthValue());
            RbaConfigBean rbaConfigBean = genericService.loadObjectWithSingleCondition(RbaConfigBean.class, getBusinessClientIdPredicate(request));
            if (rbaConfigBean.isNewEntity()) {
                wPMB.setRbaPercentage(0.00D);
            } else {
                wPMB.setRbaPercentage(rbaConfigBean.getRbaPercentage());
            }
            wPMB.setRunYear(fPayPeriodEnd.getYear());

            wPMB.setBusinessClientId(bc.getBusinessClientInstId());
        }
        if(!globalPercentConfig.isNewEntity()){
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
            wRPB.setBusinessClientId(bc.getBusinessClientInstId());
            wRPB.setRunMonth(fPayPeriodEnd.getMonthValue());
            wRPB.setRunYear(fPayPeriodEnd.getYear());
            wRPB.setRerunInd(IConstants.ON);
            this.genericService.saveObject(wRPB);
        }
        PfaInfo defPfa = genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("defaultInd", 1));
        List<MdaInfo> mdaInfoList = genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), null);
        PayrollConfigImpact payrollConfigImpact = IppmsUtilsExt.configurePayrollConfigImpact(genericService,configurationBean,wPMB,bc);
        if (bc.isPensioner()) {
            CalculatePayrollPensioner wCalcPay = new CalculatePayrollPensioner(genericService, payrollService, pensionService, bc, fPayDate, fPayPeriodStart, fPayPeriodEnd, fPayPeriod,
                    fCurrentUser, fCalcPayPerPensioner, ppDMB.getFullListSize(), false, wPMB, wRPB, mdaInfoList, rerunPayrollService,true,defPfa,configurationBean,payrollConfigImpact);

            addSessionAttribute(request, "myCalcPay", wCalcPay);
            addSessionAttribute(request, "ppStartDate", fPayPeriodStart);
            addSessionAttribute(request, "ppEndDate", fPayPeriodEnd);
            addSessionAttribute(request, "payDayDate", fPayDate);
            Thread t = new Thread(wCalcPay);
            t.start();
        } else {
            Map<Long, TerminateReason> terminateReasonMap = genericService.loadObjectAsMapWithConditions(TerminateReason.class, Arrays.asList(CustomPredicate.procurePredicate("finalizedInd",ON)),"id");
            this.fCalcPayPerEmployee.setTerminateReasonMap(terminateReasonMap);
            CalculatePayroll wCalcPay = new CalculatePayroll(this.genericService, this.payrollService, bc, fPayDate,
                    fPayPeriodStart, fPayPeriodEnd, fPayPeriod, this.fCurrentUser, this.fCalcPayPerEmployee, ppDMB.getFullListSize(), wPMB, false, wRPB, defPfa, mdaInfoList,
                    rerunPayrollService,true,configurationBean,payrollConfigImpact);

            addSessionAttribute(request, "myCalcPay", wCalcPay);
            addSessionAttribute(request, "ppStartDate", fPayPeriodStart);
            addSessionAttribute(request, "ppEndDate", fPayPeriodEnd);
            addSessionAttribute(request, "payDayDate", fPayDate);
            Thread t = new Thread(wCalcPay);
            t.start();
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

        List<PayrollRerun> hireInfoList = (List<PayrollRerun>) this.genericService.loadPaginatedObjectsByPredicates(PayrollRerun.class, new PredicateBuilder().
                addPredicate(CustomPredicate.procurePredicate("businessClientId",businessCertificate.getBusinessClientInstId())),pStartRow,pEndRow,null,null);

        for (PayrollRerun h : hireInfoList) {

            AbstractPaycheckEntity ePB = IppmsUtils.makePaycheckObject(businessCertificate);
            ePB.setPayrollRerunInstId(h.getId());
            ePB.setAbstractEmployeeEntity(h.getHiringInfo().getAbstractEmployeeEntity());
            ePB.setPayEmployee("true");
            ePB.getAbstractEmployeeEntity().setPayEmployee("true");

            ePB.setHiringInfo(h.getHiringInfo());

            if (h.getHiringInfo().getLastPayDate() != null)
                ePB.setLastPayDate(PayrollPayUtils.formatDates(h.getHiringInfo().getLastPayDate()));
            else {
                ePB.setLastPayDate("");
            }

            if ((ePB.getAbstractEmployeeEntity().getSalaryInfo() != null) && (!ePB.getAbstractEmployeeEntity().getSalaryInfo().isNewEntity())) {
                ePB.setPaymentType("Salaried");
                ePB.setName(ePB.getAbstractEmployeeEntity().getFirstName() + " " + ePB.getAbstractEmployeeEntity().getLastName());
                chosenOnes.add(ePB);
            } else {
                log.error("Employee " + ePB.getAbstractEmployeeEntity().getEmployeeId() + " Will never get paid.");
            }


        }

        return chosenOnes;
    }


}
