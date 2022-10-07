package com.osm.gnl.ippms.ogsg.controllers.audit;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.PaymentMethodInfoLog;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.AuditService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
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
import java.util.List;

@Controller
@RequestMapping({"/viewBankLog.do"})
@SessionAttributes(types = {DataTableBean.class})
public class BankInfoAuditLogsFormController extends BaseController {

    @Autowired
    private AuditService auditService;
    private final int pageLength = 20;
    private final String VIEW_NAME = "audit/paymentMethodAuditLogForm";

    public BankInfoAuditLogsFormController() {
    }

    @ModelAttribute("userList")
    public List<User> populateUsersList(HttpServletRequest request) {
        return this.genericService.loadAllObjectsWithSingleCondition(User.class,
                CustomPredicate.procurePredicate("role.businessClient.id",getBusinessCertificate(request).getBusinessClientInstId()),"username");
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        DataTableBean wBEOB = new DataTableBean(new ArrayList<PaymentMethodInfoLog>());

        wBEOB.setShowRow(HIDE_ROW);
        return makeAndReturnView(model,getBusinessCertificate(request),wBEOB,null);
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"fd", "td", "uid", "eid"})
    public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd,
                            @RequestParam("uid") Long pUid,
                            @RequestParam("eid") Long pEmpId,
                            Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        LocalDate fDate = null;
        LocalDate tDate = null;
        boolean usingDates = false;
        if (IppmsUtils.isNotNullOrEmpty(pFd)
                && IppmsUtils.isNotNullOrEmpty(pTd)) {
            usingDates = true;
            fDate = PayrollBeanUtils.setDateFromString(pFd);
            tDate = PayrollBeanUtils.setDateFromString(pTd);
        }

//       PaginationBean paginationBean = super.getPaginationInfo(request);
        boolean useUserId = pUid > 0;

        boolean useEmpId = pEmpId > 0;

        List<PaymentMethodInfoLog> empList;
        int wGLNoOfElements = 0;
        if (usingDates) {
            empList = this.auditService.getBankInfoAuditLogByDateAndUserId(bc, PayrollBeanUtils.getNextORPreviousDay(fDate, false),
                    PayrollBeanUtils.getNextORPreviousDay(tDate, true), pUid, pEmpId, useUserId, useEmpId);
            wGLNoOfElements = this.auditService.getTotalNoOfBankInfoAuditLogByDateAndUserId(bc,
                    PayrollBeanUtils.getNextORPreviousDay(fDate, false),
                    PayrollBeanUtils.getNextORPreviousDay(tDate, true), pUid, pEmpId, useUserId, useEmpId);
        } else {
            empList = this.auditService.getBankInfoAuditLogByDateAndUserId(bc, null, null, pUid, pEmpId, useUserId, useEmpId);
            wGLNoOfElements = this.auditService.getTotalNoOfBankInfoAuditLogByDateAndUserId(bc,
                    null, null, pUid, pEmpId, useUserId, useEmpId);

        }

        DataTableBean wPELB = new DataTableBean(empList);

//        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPELB.setShowRow(SHOW_ROW);
        wPELB.setShowLink(empList != null && !empList.isEmpty() && empList.size() > 0);
        if (usingDates) {
            wPELB.setFromDate(fDate );
            wPELB.setToDate(tDate);
            wPELB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(wPELB.getFromDate()));
            wPELB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(wPELB.getToDate()));

        } else {
            wPELB.setNotUsingDates(true);
        }

        wPELB.setId(pUid);
        if (useEmpId) {

            wPELB.setOgNumber(IppmsUtils.loadEmployee(genericService, pEmpId, bc).getEmployeeId());
            wPELB.setEmpInstId(pEmpId);
        }
        return makeAndReturnView(model,bc,wPELB,null);
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep,
                                @RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") DataTableBean pLPB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:auditPageHomeForm.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
            boolean filterByEmployee = false;
            Long empId = 0L;
            if (!StringUtils.trimToEmpty(pLPB.getOgNumber()).equals(EMPTY_STR)) {

                empId =  ((AbstractEmployeeEntity)this.genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(bc),CustomPredicate.procurePredicate( "employeeId",pLPB.getOgNumber().toUpperCase()))).getId();
                if (empId != null && empId > 0) {
                    filterByEmployee = true;
                } else {
                    result.rejectValue("", "InvalidValue", "No "+bc.getStaffTypeName()+" found with ID " + pLPB.getOgNumber());
                   addDisplayErrorsToModel(model, request);
                    return makeAndReturnView(model,bc,pLPB,result);

                }

            }
            boolean wUseDates = false;
            if ((pLPB.getFromDate() == null || pLPB.getToDate() == null) && !filterByEmployee) {
                result.rejectValue("", "InvalidValue", "Please select valid Dates OR Filter by Employee");
                addDisplayErrorsToModel(model, request);
                return makeAndReturnView(model,bc,pLPB,result);

            }

            if ((pLPB.getFromDate() != null) && (pLPB.getToDate() != null)) {
                wUseDates = true;
            }
            if (wUseDates) {

                if ( pLPB.getFromDate().isAfter(pLPB.getToDate())) {
                    result.rejectValue("", "InvalidValue",
                            "'Between' Date can not be greater than 'And' Date ");
                    addDisplayErrorsToModel(model, request);
                    return makeAndReturnView(model,bc,pLPB,result);

                }

                if(PayrollBeanUtils.getNoOfMonths(pLPB.getFromDate(),pLPB.getToDate()) > 3){

                        result.rejectValue("", "InvalidValue",
                                "Payment Method Audit Log dates must within 3 Months.");
                        addDisplayErrorsToModel(model, request);
                    return makeAndReturnView(model,bc,pLPB,result);


                }

                String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB
                        .getFromDate());
                String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB
                        .getToDate());
                return "redirect:viewBankLog.do?fd=" + sDate + "&td=" + eDate
                        + "&uid=" + pLPB.getId() + "&eid=" + empId;
            } else {

                return "redirect:viewBankLog.do?fd=&td="
                        + "&uid=" + pLPB.getId() + "&eid=" + empId;
            }
        }

        return "redirect:viewBankLog.do";
    }

    private String makeAndReturnView(Model model,BusinessCertificate bc,DataTableBean dtb, BindingResult result){

        model.addAttribute("displayList", dtb.getObjectList());
        model.addAttribute("roleBean", bc);
        if(result != null)
            model.addAttribute("status", result);
        model.addAttribute("miniBean", dtb);

        return this.VIEW_NAME;
    }
}