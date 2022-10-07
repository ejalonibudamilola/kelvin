package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.*;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.engine.CalculateFuturePayPerEmployee;
import com.osm.gnl.ippms.ogsg.engine.CalculateFuturePayroll;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckMaster;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import com.osm.gnl.ippms.ogsg.validators.simulation.FuturisticSimulationValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/futuristicPayrollSimulator.do")
@SessionAttributes(types = FuturePaycheckMaster.class)
public class FuturisticSimulationController extends BaseController {


    private ArrayList<Long> payIds;
    private List<FuturePaycheckBean> listToPay;
    private CalculateFuturePayPerEmployee fCalcPayPerEmployee;
    private HashMap<Long, List<AbstractDeductionEntity>> fEmpDedMap;
    private HashMap<Long, List<AbstractGarnishmentEntity>> fGarnMap;
    private HashMap<Long, List<AbstractSpecialAllowanceEntity>> fAllowanceMap;

    private LocalDate fPayPeriodStart;
    private LocalDate fPayPeriodEnd;

    @Autowired
    private FuturisticSimulationValidator validator;
    @Autowired
    private DeductionService deductionService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private SpecAllowService specAllowService;
    @Autowired
    private PayrollService payrollService;
    @Autowired
    private SimulationService simulationService;

    private final String VIEW = "simulation/preFuturisticSimulationForm";
    private LocalDate fPayPeriod;


    public FuturisticSimulationController() {
    }


    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PayrollFlag p = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);

        int pMonth = p.getApprovedMonthInd();

        int pYear = p.getApprovedYearInd();

        LocalDate wRetVal = LocalDate.of(pYear, pMonth, 1);

        LocalDate wCal = PayrollBeanUtils.makeNextPayPeriod(wRetVal, pMonth, pYear);


        FuturePaycheckMaster wFPM = new FuturePaycheckMaster();

        wFPM.setCreatedDate(wCal);
        wFPM.setCompareDate(wRetVal);
        addRoleBeanToModel(model, request);
        model.addAttribute("futureBean", wFPM);


        return VIEW;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"pid"})
    public String setupForm(@RequestParam("pid") Long pPid, Model model, HttpServletRequest request) throws Exception {
        //This is the default subsequent calls will go to the next setup call.
        SessionManagerService.manageSession(request, model);

        FuturePaycheckMaster wFPM = genericService.loadObjectById(FuturePaycheckMaster.class, pPid);

        LocalDate wCal = LocalDate.of(wFPM.getSimulationYear(), wFPM.getSimulationMonth(), 1);
        wFPM.setCreatedDate(wCal);

        wFPM.setCanEdit(true);
        addRoleBeanToModel(model, request);
        model.addAttribute("futureBean", wFPM);

        return VIEW;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,
                                @ModelAttribute("futureBean") FuturePaycheckMaster ppDMB,
                                BindingResult result, SessionStatus status, Model model,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            if (ppDMB.isCanEdit())
                return "redirect:preFutureSimReportForm.do";
            return REDIRECT_TO_DASHBOARD;
        }
        if (!ppDMB.isCanEdit()) {
            validator.validate(ppDMB, result,bc);
            if (result.hasErrors()) {
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("pageErrors", result);
                model.addAttribute("futureBean", ppDMB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }
            //If we get here we need to save the Futuristic Payroll Master Bean...


            ppDMB.setSimulationMonth(ppDMB.getCreatedDate().getMonthValue());
            ppDMB.setSimulationYear(ppDMB.getCreatedDate().getYear());

            ppDMB.setLastModBy(bc.getUserName());
            ppDMB.setLastModTs(LocalDate.now());

            LocalDate wStart = PayrollBeanUtils.getDateFromMonthAndYear(ppDMB.getSimulationMonth(), ppDMB.getSimulationYear(), false);

            LocalDate wEnd = PayrollBeanUtils.getDateFromMonthAndYear(ppDMB.getSimulationMonth(), ppDMB.getSimulationYear(), true);
            ppDMB.setBusinessClientId(bc.getBusinessClientInstId());
            this.fPayPeriodStart = wStart;
            this.fPayPeriodEnd = wEnd;
            this.fPayPeriod = wEnd;
            this.genericService.saveObject(ppDMB);

            ConfigurationBean configurationBean = loadConfigurationBean(request);
            listToPay = findAllEmployees(bc, ppDMB, configurationBean);

            if (listToPay == null || listToPay.isEmpty()) {
                result.rejectValue("", "No.Employees", "No " + bc.getStaffTypeName() + " found for this Pay Period.");
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("pageErrors", result);
                model.addAttribute("futureBean", ppDMB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }

            List<AbstractDeductionEntity> wEmpListToSplit = this.deductionService.loadToBePaidEmployeeDeductions(bc, this.fPayPeriodStart, this.fPayPeriodEnd, false);
            List<AbstractGarnishmentEntity> wGarnListToSplit = this.loanService.loadToBePaidEmployeeGarnishments(bc, wEnd, false);
            List<AbstractSpecialAllowanceEntity> wAllowListToSplit = this.specAllowService
                    .loadToBePaidEmployeeSpecialAllowances(bc, this.fPayPeriodStart, this.fPayPeriodEnd, false);

            HashMap<Long, SuspensionLog> wPartPayments = this.payrollService.loadToBePaidSuspendedEmployees(bc);
            this.fEmpDedMap = EntityUtils.breakUpDeductionList(wEmpListToSplit, this.fPayPeriodStart, this.fPayPeriodEnd);
            this.fGarnMap = EntityUtils.breakUpGarnishmentList(wGarnListToSplit);
            this.fAllowanceMap = EntityUtils.breakUpAllowanceList(wAllowListToSplit, wStart, wEnd);

            PayrollFlag wPF = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);

            LocalDate wLastPay = PayrollBeanUtils.getDateFromMonthAndYear(wPF.getApprovedMonthInd(), wPF.getApprovedYearInd(), true);
            //Map<Long, Long> wZeroedEmployees = this.payrollService.loadEmployeesWithLastPayZero(bc, wLastPay);

            List<SalaryInfo> wList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, getBusinessClientIdPredicate(request), null);
            Map<Long, SalaryInfo> wMap = EntityUtils.breakSalaryInfo(wList);


            this.fCalcPayPerEmployee = new CalculateFuturePayPerEmployee();

            this.fCalcPayPerEmployee.setSalaryInfoMap(wMap);
            this.fCalcPayPerEmployee.setSixtyYearsAgo(PayrollBeanUtils.calculate60yrsAgo(fPayPeriodEnd, false, configurationBean));
            this.fCalcPayPerEmployee.setThirtyFiveYearsAgo(PayrollBeanUtils.calculate35YearsAgo(this.fPayPeriodEnd, configurationBean));
            this.fCalcPayPerEmployee.setPayPeriodEnd(this.fPayPeriod);
            this.fCalcPayPerEmployee.setfEmployeeDeductions(this.fEmpDedMap);
            this.fCalcPayPerEmployee.setfEmployeeGarnishments(this.fGarnMap);
            this.fCalcPayPerEmployee.setfSpecialAllowances(this.fAllowanceMap);
            this.fCalcPayPerEmployee.setPartPaymentMap(wPartPayments);
            //this.fCalcPayPerEmployee.setPayrollService(this.payrollService);
           // this.fCalcPayPerEmployee.setZeroLastPayEmployees(wZeroedEmployees);


            PfaInfo pfaInfo = genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("defaultInd", ON));

            CalculateFuturePayroll wCalcPay = new CalculateFuturePayroll(payrollService, bc, listToPay.size(), ppDMB.getId(),
                    fCalcPayPerEmployee, genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), null),
                    ppDMB.getSimulationMonth(), ppDMB.getSimulationYear(), pfaInfo);


            addSessionAttribute(request, "myFutureCalcPay", wCalcPay);
            Thread t = new Thread(wCalcPay);
            t.start();

//            return "redirect:displayStatus.do?ftr=future";
              return "redirect:displayFtrStatus.do";
        } else {
            if (!ppDMB.isConfirmDeletion()) {
                ppDMB.setConfirmDeletion(true);
                result.rejectValue("", "No.Employees", "You are about to delete all records associated with this future payroll run<br>Press the Cancel button to quit or the 'Delete' button to confirm deletion.");

                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("pageErrors", result);
                model.addAttribute("futureBean", ppDMB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }
            this.genericService.deleteObject(ppDMB);
            return "redirect:preFutureSimReportForm.do";
        }

    }

    private List<FuturePaycheckBean> findAllEmployees(BusinessCertificate fBusinessClientInstId2, FuturePaycheckMaster pFPM, ConfigurationBean configurationBean) {

        List<FuturePaycheckBean> chosenOnes = new ArrayList<FuturePaycheckBean>();
        List<HiringInfo> hireInfoList = simulationService.loadPayableActiveHiringInfoByBusinessId(fBusinessClientInstId2, null);


//        int year = pFPM.getSimulationYear() - 60;
//        int month = pFPM.getSimulationMonth();
//        int day = pFPM.getCreatedDate().getDayOfMonth();
//
//        LocalDate wSixtyYearsAgo =this.makeDate(year,month,day,pFPM.getCreatedDate().isLeapYear());
//
//        LocalDate wThirtyFiveYearsAgo = this.makeDate(pFPM.getSimulationYear() - configurationBean.getServiceLength(),month,pFPM.getCreatedDate().getDayOfMonth(),pFPM.getCreatedDate().isLeapYear());

        for (HiringInfo h : hireInfoList) {

            //This is the ones we want.
           /* Calendar wBirthDate = new GregorianCalendar();
            Calendar wHireDate = new GregorianCalendar();*/
            FuturePaycheckBean ePB = new FuturePaycheckBean();

            ePB.setHiringInfo(h);



                ePB.setName(ePB.getHiringInfo().getEmployee().getFirstName() + " " + ePB.getHiringInfo().getEmployee().getLastName());
                if (this.payIds == null)
                    this.payIds = new ArrayList<Long>();
                this.payIds.add(ePB.getHiringInfo().getEmployee().getId());
               // wBirthDate.setTime(h.getBirthDate());
              //  wHireDate.setTime(h.getHireDate());
                /*
                 * if(wBirthDate.before(wSixtyYearsAgo) ||
                 * wHireDate.before(wThirtyFiveYearsAgo)){ ePB.setDoNotPay(true); }
                 * if(ePB.getHiringInfo().isSuspendedEmployee()) ePB.setDoNotPay(true);
                 */
                //Now check if this employee if he is a contract employee will still have his contract valid by then
                chosenOnes.add(ePB);
            }


        return chosenOnes;
    }


    private LocalDate makeDate(int year, int month, int day, boolean isBaseDateALeapYr) {
        if (month == 2 && day > 28) {
            LocalDate thatYear = LocalDate.of(year, month, 28);
            if (isBaseDateALeapYr) {
                thatYear.isLeapYear();
                day = 29;
            }
        } else {
            switch (month) {
                case 9:
                case 4:
                    if (day > 30)
                        day = 30;
                    break;
                case 6:
                    if (day > 30)
                        day = 30;
                    break;
                case 11:
                     if (day > 30)
                         day = 30;
                    break;

                default:

            }
        }
        return LocalDate.of(year,month,day);
    }


}
