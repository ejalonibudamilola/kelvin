package com.osm.gnl.ippms.ogsg.controllers.negativepay;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.NegPayService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.negativepay.domain.NegativePayBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping({"/viewNegativePay.do"})
@SessionAttributes(types = {AbstractPaycheckEntity.class})
public class NegativePayViewFormController extends BaseController {
    private final int pageLength = 20;
    private final PaycheckService paycheckService;
    private final NegPayService negPayService;
    private final String VIEW_NAME = "payment/negativePayAnalysisForm";

    @Autowired
    public NegativePayViewFormController(PaycheckService paycheckService, NegPayService negPayService) {
        this.paycheckService = paycheckService;

        this.negPayService = negPayService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException,
            IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        List<AbstractPaycheckEntity> empBeanList;

        PaginationBean paginationBean = super.getPaginationInfo(request);

        LocalDate wRunDetails = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);

        empBeanList = this.negPayService.loadNegativeEmployeePayBeanByRunMonthAndYear(bc, wRunDetails.getMonthValue(), wRunDetails.getYear()
                , ((paginationBean.getPageNumber() - 1) * this.pageLength), this.pageLength, null, null, true);

        int wNoOfElements = this.negPayService.getNoOfNegativePaychecksByRunMonthAndYear(bc, wRunDetails.getMonthValue(), wRunDetails.getYear(), null, null, true);
        if (wNoOfElements > 0) {
            HashMap<Long, NegativePayBean> wNPBMap = this.genericService.loadObjectAsMapWithConditions(NegativePayBean.class, Arrays.asList(CustomPredicate.procurePredicate("runMonth", wRunDetails.getMonthValue()),
                    CustomPredicate.procurePredicate("runYear", wRunDetails.getYear()), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "id");
            for (AbstractPaycheckEntity e : empBeanList) {
                if (wNPBMap.containsKey(e.getAbstractEmployeeEntity().getId())) {
                    e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName()+"**");
                    e.setDeductionAmount(wNPBMap.get(e.getAbstractEmployeeEntity().getId()).getReductionAmount());
                    if (e.getDeductionAmount() + e.getNetPay() >= 0) {
                        e.setTreatedStatus("Treated.");
                    } else if (e.getDeductionAmount() + e.getNetPay() < 0) {
                        if (e.getDeductionAmount() == 0) {
                            e.setTreatedStatus("In Progress");
                        } else {
                            e.setTreatedStatus("Pending.");
                        }
                    }
                }else{
                    e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName());
                }
            }
        }
        PayrollSummaryBean pBSB = new PayrollSummaryBean(empBeanList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pBSB.setShowRow(SHOW_ROW);
        pBSB.setEmployeeId(0L);
        pBSB.setId(bc.getBusinessClientInstId());
        int month = wRunDetails.getMonthValue();
        int year = wRunDetails.getYear();
        pBSB.setCompanyName(bc.getBusinessName());
        pBSB.setRunMonth(month);
        pBSB.setRunYear(year);

        pBSB.setFromDateAsString(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(month, year));
        addSessionAttribute(request, "negPayMonth", month);
        addSessionAttribute(request, "negPayYr", year);
        model.addAttribute("paystubSummary", pBSB);
        model.addAttribute("roleBean", bc);
        Navigator.getInstance(getSessionId(request)).setFromClass(NegativePayViewFormController.class);
        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewNegativePay.do?rm=" + month + "&ry=" + year);

        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"rm", "ry"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        Long busClientId = bc.getBusinessClientInstId();

        PaginationBean paginationBean = super.getPaginationInfo(request);
        List<AbstractPaycheckEntity> empBeanList = this.negPayService.loadNegativeEmployeePayBeanByRunMonthAndYear(bc, pRunMonth, pRunYear
                , ((paginationBean.getPageNumber() - 1) * this.pageLength), this.pageLength, null, null, true);

        int wNoOfElements = this.negPayService.getNoOfNegativePaychecksByRunMonthAndYear(bc, pRunMonth, pRunYear, null, null, true);
        if (wNoOfElements > 0) {
            HashMap<Long, NegativePayBean> wNPBMap = this.genericService.loadObjectAsMapWithConditions(NegativePayBean.class,
                    Arrays.asList(CustomPredicate.procurePredicate("runMonth", pRunMonth),
                            CustomPredicate.procurePredicate("runYear", pRunYear),
                            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "id");
            NegativePayBean n;
            for (AbstractPaycheckEntity e : empBeanList) {
                n = wNPBMap.get(e.getAbstractEmployeeEntity().getId());
                if (n != null) {
                    if (n.getReductionAmount() > 0) {
                        e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName() + "**");
                        e.setDeductionAmount(n.getReductionAmount());


                    }
                } else {
                    e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName());
                }
            }
        }
        PayrollSummaryBean pBSB = new PayrollSummaryBean(empBeanList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pBSB.setShowRow(SHOW_ROW);
        pBSB.setEmployeeId(0L);
        pBSB.setId(busClientId);
        pBSB.setCompanyName(bc.getBusinessName());
        pBSB.setRunMonth(pRunMonth);
        pBSB.setRunYear(pRunYear);

        pBSB.setFromDateAsString(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
        addSessionAttribute(request, "negPayMonth", pRunMonth);
        addSessionAttribute(request, "negPayYr", pRunYear);
        model.addAttribute("paystubSummary", pBSB);
        model.addAttribute("roleBean", bc);
        Navigator.getInstance(getSessionId(request)).setFromClass(NegativePayViewFormController.class);
        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewNegativePay.do?rm=" + pRunMonth + "&ry=" + pRunYear);

        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"rm", "ry", "eid"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                            @RequestParam("eid") Long pEmpInstId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        this.negPayService.resetNegativePayInd(bc, pEmpInstId, pRunMonth, pRunYear);
        List<AbstractPaycheckEntity> empBeanList = new ArrayList<AbstractPaycheckEntity>();

        PaginationBean paginationBean = super.getPaginationInfo(request);
        empBeanList = this.negPayService.loadNegativeEmployeePayBeanByRunMonthAndYear(bc, pRunMonth, pRunYear
                , ((paginationBean.getPageNumber() - 1) * this.pageLength), this.pageLength, null, null, false);

        int wNoOfElements = this.negPayService.getNoOfNegativePaychecksByRunMonthAndYear(bc, pRunMonth, pRunYear, null, null, false);
        if (wNoOfElements > 0) {
            HashMap<Long, NegativePayBean> wNPBMap = this.genericService.loadObjectAsMapWithConditions(NegativePayBean.class, Arrays.asList(CustomPredicate.procurePredicate("runMonth",pRunMonth),
                    CustomPredicate.procurePredicate("runYear",pRunYear), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "id");
            for (AbstractPaycheckEntity e : empBeanList) {
                if (wNPBMap.containsKey(e.getAbstractEmployeeEntity().getId())) {
                    e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName()+"**");
                    e.setDeductionAmount(wNPBMap.get(e.getAbstractEmployeeEntity().getId()).getReductionAmount());
                    if (e.getDeductionAmount() + e.getNetPay() >= 0) {
                        e.setTreatedStatus("Treated.");
                    } else if (e.getDeductionAmount() + e.getNetPay() < 0) {
                        if (e.getDeductionAmount() == 0) {
                            e.setTreatedStatus("In Progress");
                        } else {
                            e.setTreatedStatus("Pending.");
                        }
                    }
                }else{
                    e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName());
                }
            }
        }
        PayrollSummaryBean pBSB = new PayrollSummaryBean(empBeanList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pBSB.setShowRow(SHOW_ROW);
        pBSB.setEmployeeId(0L);
        pBSB.setId(bc.getBusinessClientInstId());
        pBSB.setCompanyName(bc.getBusinessName());
        pBSB.setRunMonth(pRunMonth);
        pBSB.setRunYear(pRunYear);
        pBSB.setFromDateAsString(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("netPay", 0, Operation.LESS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", pRunMonth, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear", pRunYear, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("negativePayInd", 0, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId(), Operation.EQUALS));
        pBSB.setHasHiddenRecords(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(bc)) > 0);

        model.addAttribute("paystubSummary", pBSB);
        model.addAttribute("roleBean", bc);
        Navigator.getInstance(getSessionId(request)).setFromClass(NegativePayViewFormController.class);
        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewNegativePay.do?rm=" + pRunMonth + "&ry=" + pRunYear);

        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpInstId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        int wRunMonth = (Integer) getSessionAttribute(request, "negPayMonth");
        int wRunYear = (Integer) getSessionAttribute(request, "negPayYr");

        this.negPayService.resetNegativePayInd(bc, pEmpInstId, wRunMonth, wRunYear);
        List<AbstractPaycheckEntity> empBeanList;

        PaginationBean paginationBean = super.getPaginationInfo(request);


        empBeanList = this.negPayService.loadNegativeEmployeePayBeanByRunMonthAndYear(bc, wRunMonth, wRunYear
                , ((paginationBean.getPageNumber() - 1) * this.pageLength), this.pageLength, null, null, false);

        int wNoOfElements = this.negPayService.getNoOfNegativePaychecksByRunMonthAndYear(bc, wRunMonth, wRunYear, null, null, false);
        if (wNoOfElements > 0) {
            HashMap<Long, NegativePayBean> wNPBMap = this.genericService.loadObjectAsMapWithConditions(NegativePayBean.class, Arrays.asList(CustomPredicate.procurePredicate("runMonth",wRunMonth),
                    CustomPredicate.procurePredicate("runYear",wRunYear), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "id");
            for (AbstractPaycheckEntity e : empBeanList) {
                if (wNPBMap.containsKey(e.getAbstractEmployeeEntity().getId())) {
                    e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName()+"**");
                    e.setDeductionAmount(wNPBMap.get(e.getAbstractEmployeeEntity().getId()).getReductionAmount());
                    if (e.getDeductionAmount() + e.getNetPay() >= 0) {
                        e.setTreatedStatus("Treated.");
                    } else if (e.getDeductionAmount() + e.getNetPay() < 0) {
                        if (e.getDeductionAmount() == 0) {
                            e.setTreatedStatus("In Progress");
                        } else {
                            e.setTreatedStatus("Pending.");
                        }
                    }
                }else{
                    e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName());
                }
            }
        }
        PayrollSummaryBean pBSB = new PayrollSummaryBean(empBeanList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pBSB.setShowRow(SHOW_ROW);
        pBSB.setEmployeeId(0L);
        pBSB.setId(bc.getBusinessClientInstId());
        pBSB.setCompanyName(bc.getBusinessName());
        pBSB.setRunMonth(wRunMonth);
        pBSB.setRunYear(wRunYear);
        pBSB.setFromDateAsString(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wRunMonth, wRunYear));
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("netPay", 0, Operation.LESS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", wRunMonth, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear", wRunYear, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("negativePayInd", 0, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId(), Operation.EQUALS));
        pBSB.setHasHiddenRecords(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(bc)) > 0);
        model.addAttribute("paystubSummary", pBSB);
        model.addAttribute("roleBean", bc);
        Navigator.getInstance(getSessionId(request)).setFromClass(NegativePayViewFormController.class);
        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewNegativePay.do?rm=" + wRunMonth + "&ry=" + wRunYear);

        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid", "ln"})
    public String setupForm(@RequestParam("eid") Long pEmpInstId,
                            @RequestParam("ln") String pLastName, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        int wRunMonth = (Integer) getSessionAttribute(request, "negPayMonth");
        int wRunYear = (Integer) getSessionAttribute(request, "negPayYr");


        List<AbstractPaycheckEntity> empBeanList;

        PaginationBean paginationBean = super.getPaginationInfo(request);


        empBeanList = this.negPayService.loadNegativeEmployeePayBeanByRunMonthAndYear(bc, wRunMonth, wRunYear
                , ((paginationBean.getPageNumber() - 1) * this.pageLength), this.pageLength, null, null, false);

        int wNoOfElements = this.negPayService.getNoOfNegativePaychecksByRunMonthAndYear(bc, wRunMonth, wRunYear, null, null, false);

        if (wNoOfElements > 0) {
            HashMap<Long, NegativePayBean> wNPBMap = this.genericService.loadObjectAsMapWithConditions(NegativePayBean.class, Arrays.asList(CustomPredicate.procurePredicate("runMonth",wRunMonth),
                    CustomPredicate.procurePredicate("runYear",wRunYear), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "id");
            for (AbstractPaycheckEntity e : empBeanList) {
                if (wNPBMap.containsKey(e.getAbstractEmployeeEntity().getId())) {
                    e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName()+"**");
                    e.setDeductionAmount(wNPBMap.get(e.getAbstractEmployeeEntity().getId()).getReductionAmount());
                    if (e.getDeductionAmount() + e.getNetPay() >= 0) {
                        e.setTreatedStatus("Treated.");
                    } else if (e.getDeductionAmount() + e.getNetPay() < 0) {
                        if (e.getDeductionAmount() == 0) {
                            e.setTreatedStatus("In Progress");
                        } else {
                            e.setTreatedStatus("Pending.");
                        }
                    }
                }else{
                    e.getAbstractEmployeeEntity().setDisplayNameWivAsterix(e.getAbstractEmployeeEntity().getDisplayName());
                }
            }
        }
        PayrollSummaryBean pBSB = new PayrollSummaryBean(empBeanList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pBSB.setShowRow(SHOW_ROW);
        pBSB.setEmployeeId(0L);
        pBSB.setId(bc.getBusinessClientInstId());
        pBSB.setCompanyName(bc.getBusinessName());
        pBSB.setRunMonth(wRunMonth);
        pBSB.setRunYear(wRunYear);
        pBSB.setFromDateAsString(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wRunMonth, wRunYear));
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("netPay", 0, Operation.LESS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", wRunMonth, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear", wRunYear, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("negativePayInd", 0, Operation.EQUALS));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId(), Operation.EQUALS));
        pBSB.setHasHiddenRecords(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(bc)) > 0);


        model.addAttribute("paystubSummary", pBSB);
        model.addAttribute("roleBean", bc);
        Navigator.getInstance(getSessionId(request)).setFromClass(NegativePayViewFormController.class);
        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewNegativePay.do?rm=" + wRunMonth + "&ry=" + wRunYear);

        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_update", required = false) String pUpd,
                                @RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("paystubSummary") PayrollSummaryBean ppDMB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }


        Long empId = 0L;
        String wLastName = "";

        if (StringUtils.isNotBlank(ppDMB.getOgNumber())) {

            ppDMB.setOgNumber(IppmsUtils.treatOgNumber(bc, ppDMB.getOgNumber()));

            Employee wEmp = this.genericService.loadObjectUsingRestriction(Employee.class, Arrays.asList(CustomPredicate.procurePredicate("employeeId", ppDMB.getOgNumber()),
                    super.getBusinessClientIdPredicate(request)));


            if (wEmp.isNewEntity()) {
                ppDMB.setHasErrors(true);
                result.rejectValue("", "InvalidValue", "No "+bc.getStaffTypeName()+" found with "+bc.getStaffTitle()+" " + ppDMB.getOgNumber());
                model.addAttribute(DISPLAY_ERRORS, BLOCK);

                model.addAttribute("status", result);
                model.addAttribute("paystubSummary", ppDMB);

                return VIEW_NAME;
            } else {
                PayrollFlag wPF = genericService.loadObjectWithSingleCondition(PayrollFlag.class, super.getBusinessClientIdPredicate(request));

                if (!wPF.isNewEntity()) {
                    int wRunMonth = wPF.getApprovedMonthInd();
                    int wRunYear = wPF.getApprovedYearInd();
                    if (wRunMonth == 11) {
                        wRunMonth = 0;
                        wRunYear += 1;
                    } else {
                        wRunMonth += 1;
                    }
                    AbstractPaycheckEntity wEPB = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc),
                            Arrays.asList(CustomPredicate.procurePredicate("employee.id", empId), CustomPredicate.procurePredicate("runMonth", wRunMonth),
                                    CustomPredicate.procurePredicate("runYear", wRunYear)));

                    if (wEPB == null || wEPB.isNewEntity()) {
                        ppDMB.setHasErrors(true);
                        HiringInfo wHI = this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate("employee.id", empId));

                        result.rejectValue("", "InvalidValue", "No Paycheck found for " + wHI.getEmployee().getDisplayName() + " [ " + wHI.getEmployee().getEmployeeId() + " ]");
                        result.rejectValue("", "InvalidValue", "for pay period " + PayrollBeanUtils.createPayPeriodFromInt(wRunMonth, wRunYear));
                        if (wHI.getEmployee().isTerminated()) {
                            result.rejectValue("", "InvalidValue", "Reason : Employee is terminated.");

                        } else if (wHI.isSuspendedEmployee()) {
                            result.rejectValue("", "InvalidValue", "Reason : Employee is on Suspension. Suspension Start Date : " + PayrollHRUtils.getDisplayDateFormat().format(wHI.getSuspensionDate()));
                        }

                        model.addAttribute(DISPLAY_ERRORS, BLOCK);
                        model.addAttribute("status", result);
                        model.addAttribute("paystubSummary", ppDMB);

                        return VIEW_NAME;
                    }
                }

            }

        }
        if (StringUtils.isNotBlank(ppDMB.getLastName())) {
            wLastName = ppDMB.getLastName();
        }
        if (StringUtils.isNotBlank(wLastName) && empId.equals(0L)) {
            return "redirect:viewNegativePay.do?rm=" + ppDMB.getRunMonth() + "&ry=" + ppDMB.getRunYear();
        }
        Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewNegativePay.do?eid=" + empId + "&ln=" + wLastName);
        return "redirect:viewNegativePay.do?eid=" + empId + "&ln=" + wLastName;


    }
}