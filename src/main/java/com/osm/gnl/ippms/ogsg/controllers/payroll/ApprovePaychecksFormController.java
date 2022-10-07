package com.osm.gnl.ippms.ogsg.controllers.payroll;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.dao.IApprovePayrollDao;
import com.osm.gnl.ippms.ogsg.base.services.LoanService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.base.services.StatisticsService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RbaConfigBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.engine.ApprovePendingPayroll;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRerun;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.statistics.domain.MdaPayrollStatistics;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/approvePaychecksForm.do"})
@SessionAttributes(types = {PayPeriodDaysMiniBean.class})
public class ApprovePaychecksFormController extends BaseController {


    private final PayrollService payrollService;
    private final StatisticsService statisticsService;
    private final LoanService loanService;
    private final IApprovePayrollDao approvePayrollService;
    private final TransactionTemplate transactionTemplate;
    private final PaycheckService paycheckService;

    private final int pageLength = 20;
    private final String VIEW_NAME = "payment/approvePaychecksForm";

    @Autowired
    public ApprovePaychecksFormController(PayrollService payrollService, StatisticsService statisticsService, LoanService loanService, IApprovePayrollDao approvePayrollService, TransactionTemplate transactionTemplate, PaycheckService paycheckService) {
        this.payrollService = payrollService;
        this.statisticsService = statisticsService;
        this.loanService = loanService;
        this.approvePayrollService = approvePayrollService;
        this.transactionTemplate = transactionTemplate;
        this.paycheckService = paycheckService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);

        List<CustomPredicate> predicates = Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("reRunInd", ON), CustomPredicate.procurePredicate("status", "P"));
        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(predicates);

        if (this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(bc)) > 0) {
            return "redirect:approveRerunPaychecks.do";
        }
        int noOfNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc), genericService, bc);
        if (noOfNegPay > 0) {

            return "redirect:viewNegativePay.do";
        }
        //--We need to make sure there are no Pending Deleted Paychecks.....

        List<AbstractPaycheckEntity> empList = statisticsService.loadPendingPaychecks(bc,(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength);
        int wNoOfElements = statisticsService.countPendingPaychecks(bc);
        PayPeriodDaysMiniBean pPDMB = new PayPeriodDaysMiniBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pPDMB.setAdmin(bc.isSuperAdmin());

        if (!empList.isEmpty()) {
            pPDMB.setPayPeriodName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(empList.get(0).getRunMonth(), empList.get(0).getRunYear()));
            pPDMB.setHasData(true);

        }
        pPDMB.setParentInstId(bc.getBusinessClientInstId());
        predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth",empList.get(0).getRunMonth())).addPredicate(CustomPredicate.procurePredicate("runYear",empList.get(0).getRunYear()));
        predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));
        bc.setRerunPayrollExists(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,PayrollRerun.class) > 0);
        model.addAttribute("approveBean", pPDMB);
        model.addAttribute("roleBean", bc);
        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:approvePaychecksForm.do");
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"pid"})
    public String setupForm(@RequestParam("pid") int val, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        return "redirect:approvePaychecksForm.do";
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"pid", "eid", "ln"})
    public String setupForm(@RequestParam("pid") int val,
                            @RequestParam("eid") Long pEmpId,
                            @RequestParam("ln") String pLastNameStr,
                            Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);
        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(getBusinessClientIdPredicate(request));
        predicates.add(CustomPredicate.procurePredicate("status", "P"));

        if (IppmsUtils.isNotNullAndGreaterThanZero(pEmpId))
            predicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmpId));
        if (IppmsUtils.isNotNullOrEmpty(pLastNameStr)) {
            predicates.add(CustomPredicate.procurePredicate("lastName", pLastNameStr, Operation.LIKE));
        }
        List<AbstractPaycheckEntity> empList1 = (List<AbstractPaycheckEntity>) this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(bc),
                predicates, null);

        if(empList1.isEmpty())
            return "redirect:rerunPayroll.do";

        int wNoOfElements = this.genericService.getTotalPaginatedObjects(IppmsUtils.getPaycheckClass(bc), predicates).intValue();

        PayPeriodDaysMiniBean pPDMB = new PayPeriodDaysMiniBean(empList1, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pPDMB.setAdmin(bc.isSuperAdmin());

        pPDMB.setParentInstId(bc.getBusinessClientInstId());
        pPDMB.setShowCaptchaRow(IConstants.HIDE_ROW);

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth",empList1.get(0).getRunMonth())).addPredicate(CustomPredicate.procurePredicate("runYear",empList1.get(0).getRunYear()));
        predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));
        bc.setRerunPayrollExists(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,PayrollRerun.class) > 0);
        if (pEmpId > 0) {
            pPDMB.setOgNumber(IppmsUtils.loadEmployee(genericService, pEmpId, bc).getEmployeeId());
        }
        if (!StringUtils.trimToEmpty(pLastNameStr).equalsIgnoreCase(EMPTY_STR)) {
            pPDMB.setLastName(pLastNameStr);
        }
        if (!empList1.isEmpty()) {
            pPDMB.setPayPeriodName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(empList1.get(0).getRunMonth(), empList1.get(0).getRunYear()));
            pPDMB.setHasData(true);
        }
        pPDMB.setWholeEntity(true);
        model.addAttribute("approveBean", pPDMB);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"pid", "action"})
    public synchronized String setupForm(@RequestParam("pid") int val, @RequestParam("action") String del, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        addRoleBeanToModel(model, request);
        return "redirect:deletePendingPayroll.do";

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_update", required = false) String pUpd,
                                @RequestParam(value = "_approve", required = false) String pApprove,
                                @RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("approveBean") PayPeriodDaysMiniBean ppDMB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        Object userId = getSessionId(request);
        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }
        PayrollRunMasterBean wPPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", OFF, Operation.NOT_EQUAL)));
        if (!wPPRB.isNewEntity() ) {
            if(wPPRB.isRunning()) {
                ppDMB.setHasErrors(true);
                result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPPRB.getInitiator().getActualUserName());
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("miniBean", ppDMB);

                return VIEW_NAME;
            }else if(wPPRB.isGratuityRunning()){
                result.rejectValue("", "No.Employees", "Gratuity is currently being run by " + wPPRB.getInitiator().getActualUserName());
                ppDMB.setHasErrors(true);
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("miniBean", ppDMB);

                return VIEW_NAME;
            }


        }
        ppDMB.setHasErrors(false);

        if (isButtonTypeClick(request, REQUEST_PARAM_UPD)) {

            Long empId = 0L;
            String wLastName = "";

            if (!StringUtils.trimToEmpty(ppDMB.getOgNumber()).equals(EMPTY_STR)) {

                 AbstractEmployeeEntity abstractEmployeeEntity = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(getBusinessClientIdPredicate(request),
                        CustomPredicate.procurePredicate("employeeId", ppDMB.getOgNumber().toUpperCase())));

                if (abstractEmployeeEntity.isNewEntity()) {
                    ppDMB.setHasErrors(true);
                    result.rejectValue("", "InvalidValue", "No" + bc.getStaffTypeName() + " found with " + bc.getStaffTitle() + " " + ppDMB.getOgNumber());
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("miniBean", ppDMB);

                    return VIEW_NAME;
                } else {
                    empId = abstractEmployeeEntity.getId();
                    PayrollFlag wPF = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);

                    if (!wPF.isNewEntity()) {
                        int wRunMonth = wPF.getApprovedMonthInd();
                        int wRunYear = wPF.getApprovedYearInd();
                        if (wRunMonth == 12) {
                            wRunMonth = 1;
                            wRunYear += 1;
                        } else {
                            wRunMonth += 1;
                        }
                        AbstractPaycheckEntity wEPB = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc),
                                Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("employee.id", empId),
                                        CustomPredicate.procurePredicate("runMonth", wRunMonth), CustomPredicate.procurePredicate("runYear", wRunYear)));

                        if (wEPB == null || wEPB.isNewEntity()) {
                            ppDMB.setHasErrors(true);
                            HiringInfo wEmp = loadHiringInfoByEmpId(request, bc, empId);
                            result.rejectValue("", "InvalidValue", "No Paycheck found for " + wEmp.getAbstractEmployeeEntity().getDisplayName() + " [ " + wEmp.getAbstractEmployeeEntity().getEmployeeId() + " ]");
                            result.rejectValue("", "InvalidValue", "for pay period " + PayrollBeanUtils.createPayPeriodFromInt(wRunMonth, wRunYear));
                            if (wEmp.isTerminatedEmployee()) {
                                result.rejectValue("", "InvalidValue", "Reason : " + bc.getStaffTypeName() + " is terminated.");

                            } else if (wEmp.isSuspendedEmployee()) {
                                result.rejectValue("", "InvalidValue", "Reason : " + bc.getStaffTypeName() + " is on Suspension. Suspension Start Date : " + PayrollHRUtils.getDisplayDateFormat().format(wEmp.getSuspensionDate()));
                            }
                            addDisplayErrorsToModel(model, request);
                            addRoleBeanToModel(model, request);
                            model.addAttribute("status", result);
                            model.addAttribute("miniBean", ppDMB);

                            return VIEW_NAME;
                        }
                    }

                }

            }
            if (!StringUtils.trimToEmpty(ppDMB.getLastName()).equals(EMPTY_STR)) {
                wLastName = ppDMB.getLastName();
            }
            if (StringUtils.trimToEmpty(wLastName).equalsIgnoreCase(EMPTY_STR) && empId.equals(0L)) {
                Navigator.getInstance(userId).setFromForm("redirect:approvePaychecksForm.do");
                return "redirect:approvePaychecksForm.do";
            }
            Navigator.getInstance(userId).setFromForm("redirect:approvePaychecksForm.do?pid=0&eid=" + empId + "&ln=" + wLastName);
            return "redirect:approvePaychecksForm.do?pid=0&eid=" + empId + "&ln=" + wLastName;


        }
        if (!ppDMB.isAdmin()) {
            return REDIRECT_TO_DASHBOARD;
        }

        List<AbstractPaycheckEntity> listToSave = ppDMB.getEmployeePayBean();

        if ((listToSave == null) || (listToSave.isEmpty())) {
            ppDMB.setHasErrors(true);
            result.rejectValue("", "No.Employees", "No Pending Paychecks found for approval.");
            addDisplayErrorsToModel(model, request);

            model.addAttribute("pageErrors", result);
            model.addAttribute("approveBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }

        if (!bc.isSuperAdmin()) {
            ppDMB.setHasErrors(true);
            result.rejectValue("", "No.Employees", "Your profile is not enabled to Approve Payroll.");
            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("approveBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }
        if (bc.isRerunPayrollExists()) {
            ppDMB.setHasErrors(true);
            result.rejectValue("", "No.Employees", "Uncommitted or Pending Payroll Rerun Records Found.");
            result.rejectValue("", "No.Employees", "Payroll Approval Denied.");
            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("approveBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }


        AbstractPaycheckEntity wEPB = listToSave.get(0);

        String wCurrPayPeriod = PayrollBeanUtils.getDateAsString(wEPB.getPayPeriodStart()) + " - " + PayrollBeanUtils.getDateAsString(wEPB.getPayPeriodEnd());

        int runMonth = wEPB.getPayPeriodEnd().getMonthValue();
        int runYear = wEPB.getPayPeriodEnd().getYear();
        int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc), genericService, bc);

        if (noOfEmpWivNegPay > 0) {

            return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + runMonth + "&ry=" + runYear;
        }

        RerunPayrollBean wPRB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(getBusinessClientIdPredicate(request),
                CustomPredicate.procurePredicate("runMonth", runMonth), CustomPredicate.procurePredicate("runYear", runYear)));

            if (!wPRB.isNewEntity()) {
                //Now check if Running IND is set

                    //Show Ignore Button.
                    if (wPRB.getRerunInd() == IConstants.ON) {
                        if(!bc.isIgnorePendingPaychecks()) {
                            bc.setCanApprove( true);
                            ppDMB.setHasErrors(true);
                            result = addRejectValues(wPRB, result, runMonth, runYear);
                            addDisplayErrorsToModel(model, request);
                            model.addAttribute("pageErrors", result);
                            model.addAttribute("approveBean", ppDMB);
                            model.addAttribute("roleBean", bc);
                            model.addAttribute("showRow", true);
                            return VIEW_NAME;
                        }
                    }
                }


        //Now check if this Payroll Had Error while processing....
        PayrollRunMasterBean wPRMB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("runMonth", runMonth), CustomPredicate.procurePredicate("runYear", runYear)));
        if (wPRMB.isPayrollError()) {
            ppDMB.setHasErrors(true);
            result.rejectValue("", "No.Employees", "An Error occurred during Payroll Processing. Payroll Data Might Be Corrupt");
            result.rejectValue("", "No.Employees", "Payroll Approval Denied. Please delete and Re-run Payroll.");
            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("approveBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        } else if (wPRMB.isPayrollCancelled()) {
            ppDMB.setHasErrors(true);
            result.rejectValue("", "No.Employees", "Payroll Processing was cancelled.");
            result.rejectValue("", "No.Employees", "Payroll Approval Denied. Please delete and Re-run Payroll.");
            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("approveBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }
        //Before we proceed...ask for confirmation..
        if (!ppDMB.isConfirmation()) {
            ppDMB.setShowCaptchaRow(IConstants.SHOW_ROW);
            ppDMB.setConfirmation(true);
            ppDMB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
            result.rejectValue("", "No.Employees", "Please Enter the Generated Captcha for confirmation of Payroll Approval..");
            result.rejectValue("", "No.Employees", "Please NOTE Approving payroll is a permanent action.");
            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("approveBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;

        }

        if (!ppDMB.getGeneratedCaptcha().equalsIgnoreCase(ppDMB.getEnteredCaptcha())) {

            result.rejectValue("", "No.Employees", "Entered Captcha " + ppDMB.getEnteredCaptcha() + " did not match Generated Captcha " + ppDMB.getGeneratedCaptcha() + " ");
            result.rejectValue("", "No.Employees", "Please make sure they match.");
            ppDMB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
            ppDMB.setEnteredCaptcha(null);
            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("approveBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;

        }
        //If we get here then do the needful...
        try {
            if (!bc.isPensioner()) {
                RbaConfigBean wRba = this.genericService.loadObjectWithSingleCondition(RbaConfigBean.class, getBusinessClientIdPredicate(request));
                if (!wRba.isNewEntity())
                    wPRMB.setRbaPercentage(wRba.getRbaPercentage());
            }
            List<AbstractGarnishmentEntity> wSaveList = this.loanService.loadAllPendingPaycheckLoans(bc, runMonth, runYear);
            List<MdaPayrollStatistics> wStatList = this.statisticsService.createMdaPayrollStatistics(bc, runMonth, runYear);
            int totDevLevy = 0;

            if (!bc.isPensioner()) {
                totDevLevy = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(Arrays.asList(CustomPredicate.procurePredicate("developmentLevy", 0.0D, Operation.GREATER),
                        CustomPredicate.procurePredicate("status", "P"))), IppmsUtils.getPaycheckClass(bc));
            }
            wPPRB.setIgnorePendingInd(Boolean.compare(bc.isIgnorePendingPaychecks(),true));
            ApprovePendingPayroll wAppPendingPayroll = new ApprovePendingPayroll
                    (approvePayrollService, wSaveList, transactionTemplate, wEPB.getPayDate(), wEPB.getPayPeriodEnd(), wEPB.getPayPeriodStart(), wCurrPayPeriod, runMonth, runYear, totDevLevy, bc, wPRMB, wStatList);
            addSessionAttribute(request, "aprvPay", wAppPendingPayroll);


            Thread t = new Thread(wAppPendingPayroll);
            t.start();

            return "redirect:displayApprovePayroll.do";

        } catch (Exception ex) {
            ppDMB.setHasErrors(true);
            result.rejectValue("", "No.Employees", "An Exception occurred whilst reconciling loans.");
            result.rejectValue("", "No.Employees", "Payroll Approval Denied.");
            addDisplayErrorsToModel(model, request);
            model.addAttribute("pageErrors", result);
            model.addAttribute("approveBean", ppDMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }

    }

    private BindingResult addRejectValues(RerunPayrollBean pPRB,
                                          BindingResult pResult, int pRunMonth, int pRunYear) {
        pResult.rejectValue("", "Rerun.Error", "Payroll for " + PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear) + " cannot be Approved. A 'Payroll Rerun' or 'Undo' must be performed. Reason :");
        if (pPRB.isEmployeeApprovals()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getEmployeeApprovalsStr());
        }
        if (pPRB.isEmployeeRejections()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getEmployeeRejectionsStr());
        }
        if (pPRB.isContracts()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getContractStr());
        }
        if (pPRB.isDeductions()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getDeductionsStr());
        }
        if (pPRB.isLoans()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getLoansStr());
        }
        if (pPRB.isOverPayments()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getOverPaymentsStr());
        }
        if (pPRB.isReabsorptions()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getReabsorbStr());
        }
        if (pPRB.isReinstatements()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getReinstateStr());
        }
        if (pPRB.isSpecialAllowances()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getSpecialAllowancesStr());
        }
        if (pPRB.isSuspensions()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getSuspensionStr());
        }
        if (pPRB.isTerminations()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getTerminationsStr());
        }
        if (pPRB.isTransfers()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getTransferStr());
        }
        if (pPRB.isPromotions()) {
            pResult.rejectValue("", "Rerun.Error", pPRB.getPromotionStr());
        }
        return pResult;
    }


}