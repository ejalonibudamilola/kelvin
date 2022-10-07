package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.HiringInfoAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.AuditService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
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
@RequestMapping({"/viewHireInfoLog.do"})
@SessionAttributes(types = {DataTableBean.class})
public class HiringInfoAuditLogsFormController extends BaseController {

    private final int pageLength = 20;
    private final String VIEW_NAME = "audit/hireInfoAuditLogForm";
    private final AuditService auditService;

    @Autowired
    public HiringInfoAuditLogsFormController(AuditService auditService) {
        this.auditService = auditService;
    }

    @ModelAttribute("userList")
    public List<User> populateUsersList(HttpServletRequest request) {

        return this.genericService.loadAllObjectsWithSingleCondition(User.class, CustomPredicate.procurePredicate("role.businessClient.id", getBusinessCertificate(request).getBusinessClientInstId()), "username");
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        DataTableBean wBEOB = new DataTableBean(new ArrayList<HiringInfoAudit>());

        wBEOB.setShowRow(HIDE_ROW);
        return makeAndReturnView(model,getBusinessCertificate(request),wBEOB,null);
    }

    @RequestMapping(method = {RequestMethod.GET},
            params = {"fd", "td", "uid", "eid"})
    public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd,
                            @RequestParam("uid") Long pUid, @RequestParam("eid") Long pEid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        LocalDate fDate = null;
        LocalDate tDate = null;
        boolean useDates = false;
        boolean useUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUid);
        boolean useEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEid);


        if (pFd != null && !IppmsUtils.treatNull(pFd).equals(EMPTY_STR)
                && (pFd != null && !IppmsUtils.treatNull(pFd).equals(EMPTY_STR))) {
            useDates = true;
            fDate = PayrollBeanUtils.setDateFromString(pFd);
            tDate = PayrollBeanUtils.setDateFromString(pTd);
        }
//        PaginationBean paginationBean = getPaginationInfo(request);

        List<HiringInfoAudit> empList = null;
        int wGLNoOfElements = 0;

        if (useDates) {
            empList = this.auditService.getHireInfoAuditLogByDateAndUserId(bc,
                    PayrollBeanUtils.getNextORPreviousDay(fDate, false), PayrollBeanUtils.getNextORPreviousDay(tDate, true), pUid, pEid, useUserId, useEmpId);
            wGLNoOfElements = this.auditService.getTotalNoOfHireInfoAuditLogByDateAndUserId(bc, PayrollBeanUtils.getNextORPreviousDay(fDate, false), PayrollBeanUtils.getNextORPreviousDay(tDate, true),
                    pUid, pEid, useUserId, useEmpId);

        } else {
            empList = this.auditService.getHireInfoAuditLogByDateAndUserId(bc, null, null, pUid, pEid, useUserId, useEmpId);

            wGLNoOfElements = this.auditService.getTotalNoOfHireInfoAuditLogByDateAndUserId(bc, null, null,
                    pUid, pEid, useUserId, useEmpId);

        }


//        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        DataTableBean wPELB = new DataTableBean(empList);
        wPELB.setShowRow(SHOW_ROW);
        if (useDates) {
            wPELB.setFromDate(fDate);
            wPELB.setToDate(tDate);
            wPELB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(fDate));
            wPELB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(tDate));
        }
        if (empList.size() > 0)
            wPELB.setShowLink(true);
        wPELB.setId(pUid);
        if (useEmpId) {
            AbstractEmployeeEntity entity = IppmsUtils.loadEmployee(genericService, pEid, bc);
            wPELB.setOgNumber(entity.getEmployeeId());
            wPELB.setEmpInstId(pEid);
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
                pLPB.setOgNumber(IppmsUtils.treatOgNumber(bc, pLPB.getOgNumber()));


                AbstractEmployeeEntity entity = (AbstractEmployeeEntity) this.genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(bc), CustomPredicate.procurePredicate("employeeId", pLPB.getOgNumber()));
                if(IppmsUtils.isNotNull(entity)) {
                    empId = entity.getId();
                }
                    if( !entity.isNewEntity()){

                        filterByEmployee = true;
                    }else {
                    result.rejectValue("", "InvalidValue", "No " + employeeText(request) + " found with using " + pLPB.getOgNumber());
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                        return makeAndReturnView(model,bc,pLPB,result);
                }

            }
            boolean wUseDates = true;
            if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null)) {
                if (!filterByEmployee && pLPB.getId() != null && pLPB.getId() == 0) {
                    result.rejectValue("", "InvalidValue", "Please select valid Dates");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,bc,pLPB,result);
                } else {
                    wUseDates = false;
                }
            }
            if (wUseDates) {

                if ((pLPB.getFromDate() == null) || (pLPB.getToDate() == null)) {
                    result.rejectValue("", "InvalidValue", "Please select valid Dates");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,bc,pLPB,result);
                }

                if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
                    result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,bc,pLPB,result);
                }

                if (pLPB.getFromDate().getYear() != pLPB.getToDate().getYear()) {
                    result.rejectValue("", "InvalidValue", "Audit Log dates must be in the same year");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,bc,pLPB,result);
                }

                String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
                String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
                return "redirect:viewHireInfoLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId() + "&eid=" + empId;
            } else {
                return "redirect:viewHireInfoLog.do?fd=&td=&uid=" + pLPB.getId() + "&eid=" + empId;

            }

        }

        return "redirect:viewHireInfoLog.do";
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