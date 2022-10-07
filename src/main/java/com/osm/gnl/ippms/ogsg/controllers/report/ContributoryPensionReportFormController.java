package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.ContributoryPensionService;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.BusinessCertificateCreator;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.exception.NoBusinessCertificationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/pensionReport.do"})
@SessionAttributes(types = {PaginatedBean.class})
public class ContributoryPensionReportFormController extends BaseController {

    private static final String VIEW_NAME = "report/contributoryPensionForm";

    private final int pageLength = 20;

    @Autowired
    private ContributoryPensionService contributoryPensionService;


    @ModelAttribute("mdaList")
    public List<MdaInfo> populateMDAList(HttpServletRequest request) {
        BusinessCertificate bc = super.getBusinessCertificate(request);

        return this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), "name");
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException, NoBusinessCertificationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
         //TODO
        //Logic will be different for BLGP....
        if(bc.getParentClientId() != null){
            bc = BusinessCertificateCreator.makeBusinessClient(genericService.loadObjectById(BusinessClient.class,bc.getParentClientId()));
        }
        PaginationBean paginationBean = getPaginationInfo(request);

        Long pfId = this.genericService.loadMaxValueByClassClientIdAndColumn(PayrollRun.class, "id", bc.getBusinessClientInstId(), "businessClientId");

        PayrollRun pf = this.genericService.loadObjectById(PayrollRun.class, pfId);

        LocalDate wSDate = LocalDate.now();
        LocalDate wToDate = LocalDate.now();

        if (!pf.isNewEntity()) {
            wSDate = (pf.getPayPeriodStart());
            wToDate = (pf.getPayPeriodEnd());
            int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

            if (noOfEmpWivNegPay > 0) {
                return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + wSDate.getMonthValue() + "&ry=" + wSDate.getYear();
            }

        }

        List<EmpDeductMiniBean> paycheckDeductions;
        try {
            paycheckDeductions = this.contributoryPensionService.loadPensionContributionByRunMonthAndYear(
                    (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), wSDate.getMonthValue(), wSDate.getYear(), bc);
        } catch (NullPointerException e) {
            paycheckDeductions = new ArrayList<>();
        }

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("netPay", 0, Operation.GREATER));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", wSDate.getMonthValue()));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear", wSDate.getYear()));

         double sumContributoryPension = this.genericService.sumFieldUsingPredicateBuilder(IppmsUtils.getPaycheckClass(bc), predicateBuilder, Double.class, "contributoryPension", Arrays.asList("runMonth", "runYear"));


        PredicateBuilder predicateBuilder1 = new PredicateBuilder();
        predicateBuilder1.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        predicateBuilder1.addPredicate(CustomPredicate.procurePredicate("runMonth", wSDate.getMonthValue()));
        predicateBuilder1.addPredicate(CustomPredicate.procurePredicate("runYear", wSDate.getYear()));
        predicateBuilder1.addPredicate(CustomPredicate.procurePredicate("contributoryPension", 0, Operation.GREATER));

         int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder1, IppmsUtils.getPaycheckClass(bc));

        PaginatedBean pCList = new PaginatedBean(paycheckDeductions, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());


        pCList.setName("All PFA");
        pCList.setId(0L);
        pCList.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodStart()));
        pCList.setToDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodEnd()));

        pCList.setFromDate(wSDate);
        pCList.setToDate(wToDate);
        pCList.setShowRow(SHOW_ROW);
        pCList.setTotPensionCont(sumContributoryPension);


        addRoleBeanToModel(model, request);
        model.addAttribute("mdaList", this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), "name"));
//	    model.addAttribute("pfaList", this.payrollServiceExt.loadObjectsByHqlStr("from PfaInfo order by name"));
        model.addAttribute("pfaList", this.genericService.loadAllObjectsWithoutRestrictions(PfaInfo.class, "name"));
        model.addAttribute("miniBean", pCList);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"pid", "sDate", "eDate", "mdaid", "eid"})
    public String setupForm(@RequestParam("pid") Long dedTypeId, @RequestParam("sDate") String pSDate,
                            @RequestParam("eDate") String pToDate, @RequestParam("mdaid") Long pMdaId, @RequestParam("eid") Long pEmpId,
                            Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        if(bc.getParentClientId() != null){
            bc = BusinessCertificateCreator.makeBusinessClient(genericService.loadObjectById(BusinessClient.class,bc.getParentClientId()));
        }

        boolean wUsingMda = false;
        boolean wMustSum = false;

        LocalDate wSDate = null;
        LocalDate wToDate = null;

        if (pSDate == null || pSDate.equals(EMPTY_STR) || pToDate == null || pToDate.equals(EMPTY_STR)) {
            pToDate = null;
            pSDate = null;
            wMustSum = true;
        } else {
            wSDate = PayrollBeanUtils.setDateFromString(pSDate);
            wToDate = PayrollBeanUtils.setDateFromString(pToDate);

            wMustSum = wSDate.getMonthValue() != wToDate.getMonthValue() || wSDate.getYear() != wToDate.getYear();

        }

        if (IppmsUtils.isNullOrLessThanOne(pMdaId)) {

            wUsingMda = !wUsingMda;
        }
        else{
            wUsingMda = true;
        }

        int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

        if (noOfEmpWivNegPay > 0) {
            return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + wSDate.getMonthValue() + "&ry=" + wSDate.getYear();
        }
        PaginationBean paginationBean = getPaginationInfo(request);

        List<EmpDeductMiniBean> paycheckDeductions = this.contributoryPensionService.loadPensionContributions((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength,
                paginationBean.getSortOrder(), paginationBean.getSortCriterion(), wSDate, wToDate, dedTypeId, pMdaId, wMustSum, pEmpId, false, bc);


        int wGLNoOfElements = this.contributoryPensionService.getTotalSumPensionContributions(wSDate, wToDate, dedTypeId, pMdaId, wMustSum, pEmpId, bc);


        PaginatedBean pCList = new PaginatedBean(paycheckDeductions, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        if (dedTypeId > 0) {
//		    	PfaInfo wPfaInfo = (PfaInfo) payrollService.loadObjectByClassAndId(PfaInfo.class, dedTypeId);
            PfaInfo wPfaInfo = this.genericService.loadObjectWithSingleCondition(PfaInfo.class, CustomPredicate.procurePredicate("id", dedTypeId));
            pCList.setName(wPfaInfo.getName());
            pCList.setId(wPfaInfo.getId());
        } else {
            pCList.setName("All PFA");
            pCList.setId(0L);
        }
        if (wUsingMda)
            pCList.setMdaInd(pMdaId);
        if (pEmpId > 0) {
//		    	Employee wEmp = (Employee) this.payrollService.loadObjectByClassAndId(Employee.class, pEmpId);
            AbstractEmployeeEntity wEmp = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("id", pEmpId)));
            pCList.setOgNumber(wEmp.getEmployeeId());
            pCList.setEmpInstId(wEmp.getId());
        }

        pCList.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(wSDate));
        pCList.setToDateStr(PayrollBeanUtils.getJavaDateAsString(wToDate));

        pCList.setFromDate(wSDate);
        pCList.setToDate(wToDate);
        pCList.setShowRow(SHOW_ROW);
        // pCList.setTotPensionCont(sumContributoryPension);

        addRoleBeanToModel(model, request);
//		    model.addAttribute("pfaList", this.payrollServiceExt.loadObjectsByHqlStr("from PfaInfo order by name"));
        model.addAttribute("pfaList", this.genericService.loadAllObjectsWithoutRestrictions(PfaInfo.class, "name"));
        model.addAttribute("miniBean", pCList);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep,
                                @RequestParam(value = "_go", required = false) String go,
                                @ModelAttribute("miniBean") PaginatedBean pDDB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
            boolean wReqDates = true;
            Long empId = null;
            if (!StringUtils.trimToEmpty(pDDB.getOgNumber()).equals(EMPTY_STR)) {
                pDDB.setOgNumber(IppmsUtils.treatOgNumber(getBusinessCertificate(request), pDDB.getOgNumber()));
                Employee wEmp = genericService.loadObjectUsingRestriction(Employee.class, Arrays.asList(getBusinessClientIdPredicate(request),
                        CustomPredicate.procurePredicate("employeeId", pDDB.getOgNumber())));
                empId = wEmp.getId();

                if (empId == null || empId <= 0) {

                    result.rejectValue("", "InvalidValue", "No "+bc.getStaffTypeName()+" found with "+bc.getStaffTitle()+ " "+ pDDB.getOgNumber());
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("miniBean", pDDB);

                    return VIEW_NAME;
                }

                //if we get here...set a flag for
                wReqDates = false;
            }
            String sDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getFromDate());
            String eDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getToDate());
            //All we need do is make sure that the date is not null and fromDate is bigger than toDate..
            if (pDDB.getFromDate() == null && wReqDates) {
                result.rejectValue("fromDate", "InvalidValue", "Please enter a value for 'From' Date");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("pfaList", this.genericService.loadAllObjectsWithoutRestrictions(PfaInfo.class, "name"));
                model.addAttribute("miniBean", pDDB);
                return VIEW_NAME;
            }
            if (pDDB.getToDate() == null && wReqDates) {
                result.rejectValue("fromDate", "InvalidValue", "Please enter a value for 'To' Date");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("pfaList", this.genericService.loadAllObjectsWithoutRestrictions(PfaInfo.class, "name"));
                model.addAttribute("miniBean", pDDB);
                return VIEW_NAME;
            }
            if (wReqDates) {
                if (pDDB.getFromDate().compareTo(pDDB.getToDate()) > 0) {
                    result.rejectValue("fromDate", "InvalidValue", "'From' Date should be before 'To' Date");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("pfaList", this.genericService.loadAllObjectsWithoutRestrictions(PfaInfo.class, "name"));
                    model.addAttribute("miniBean", pDDB);
                    return VIEW_NAME;
                }
            }
            if (pDDB.getId() == null)
                pDDB.setId(0L);
            if (pDDB.getMdaInd() == null)
                pDDB.setMdaInd(0L);

            if (empId == null)
                empId = 0L;
            return "redirect:pensionReport.do?pid=" + pDDB.getId() + "&sDate=" + sDate + "&eDate=" + eDate + "&mdaid=" + pDDB.getMdaInd() + "&eid=" + empId;
        }

        return REDIRECT_TO_DASHBOARD;
    }


}
