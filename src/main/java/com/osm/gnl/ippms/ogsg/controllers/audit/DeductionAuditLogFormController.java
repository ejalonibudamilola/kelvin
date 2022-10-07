package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.apache.commons.lang.StringUtils;
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


@Controller
@RequestMapping({"/viewDeductionLog.do"})
@SessionAttributes(types = {DataTableBean.class})
public class DeductionAuditLogFormController extends BaseController {

    private final int pageLength = 20;
    private final String VIEW_NAME = "deduction/deductionAuditForm";


    public DeductionAuditLogFormController() {
    }

    @ModelAttribute("userList")
    public List<User> populateUsersList(HttpServletRequest request) throws InstantiationException, IllegalAccessException {
        return this.genericService.loadAllObjectsWithSingleCondition(User.class,CustomPredicate.procurePredicate("role.businessClient.id",getBusinessCertificate(request).getBusinessClientInstId()),"username");
    }

    
    @ModelAttribute("deductionTypeList")
    public List<EmpDeductionType> populateDeductionTypeList(HttpServletRequest request) {

        return this.genericService.loadAllObjectsWithSingleCondition(EmpDeductionType.class,
                getBusinessClientIdPredicate(request), "description");
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        //--Set default values for start of Month till today and then current user.
        LocalDate tDate = LocalDate.now();
        LocalDate fDate = LocalDate.of(tDate.getYear(),tDate.getMonthValue(),1);

        return this.doWork(model, bc, PayrollBeanUtils.getJavaDateAsString(fDate), PayrollBeanUtils.getJavaDateAsString(tDate), request, bc.getLoginId(), null, null);


    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"fd", "td", "uid", "dtid", "eid"})
    public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, @RequestParam("uid") Long pUid,
                            @RequestParam("dtid") Long pDedTypeId, @RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        return this.doWork(model, bc, pFd, pTd, request, pUid, pDedTypeId, pEmpId);

    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"uid", "dtid", "eid"})
    public String setupForm(@RequestParam("uid") Long pUid,
                            @RequestParam("dtid") Long pDedTypeId, @RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        return this.doWork(model, bc, null, null, request, pUid, pDedTypeId, pEmpId);

    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep,
                                @RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") DataTableBean pLPB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate businessCertificate = getBusinessCertificate(request);
        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:auditPageHomeForm.do";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
            boolean filterByEmployee = false;
            Long empId = 0L;
            if (!StringUtils.trimToEmpty(pLPB.getOgNumber()).equals(EMPTY_STR)) {

                pLPB.setOgNumber(IppmsUtils.treatOgNumber(getBusinessCertificate(request), pLPB.getOgNumber()));

                AbstractEmployeeEntity employee = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(businessCertificate), Arrays.asList(CustomPredicate.procurePredicate("employeeId", pLPB.getOgNumber()),
                        getBusinessClientIdPredicate(request)));

                if (!employee.isNewEntity()) {
                    empId = employee.getId();
                    filterByEmployee = true;
                } else {
                    result.rejectValue("", "InvalidValue", "No "+businessCertificate.getStaffTypeName()+" found with "+businessCertificate.getStaffTitle()+" " + pLPB.getOgNumber());
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,getBusinessCertificate(request),pLPB,result);
                }
            }
            boolean wUseDates = true;
            if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null)) {
                if (!filterByEmployee && pLPB.getId() != null && pLPB.getId().intValue() == 0 && pLPB.getDeductionTypeId() == 0) {

                    result.rejectValue("", "InvalidValue", "Please select valid Dates");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,getBusinessCertificate(request),pLPB,result);
                } else {
                    wUseDates = false;
                }
            }
            if (wUseDates) {

                if (pLPB.getFromDate() == null || pLPB.getToDate() == null) {
                    result.rejectValue("", "InvalidValue", "Please select valid Dates");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,getBusinessCertificate(request),pLPB,result);
                }


                if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
                    result.rejectValue("", "InvalidValue", "'Start Date' can not be greater than 'End Date'");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,getBusinessCertificate(request),pLPB,result);
                }


                String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
                String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
                //Check for Employee ID...

                return "redirect:viewDeductionLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId() + "&dtid=" + pLPB.getDeductionTypeId() + "&eid=" + empId;

            } else {
                return "redirect:viewDeductionLog.do?uid=" + pLPB.getId() + "&dtid=" + pLPB.getDeductionTypeId() + "&eid=" + empId;

            }

        } //End Update Report

        return "redirect:viewDeductionLog.do";
    }

    private String doWork(Model model, BusinessCertificate bc, String pFd, String pTd, HttpServletRequest request,
                          Long pUid, Long pDedTypeId, Long pEmpId) throws InstantiationException, IllegalAccessException {
        LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);
//        PaginationBean paginationBean = super.getPaginationInfo(request);
        String wOgNumber = "";

        List<CustomPredicate> predicates = new ArrayList<>();

       predicates.add(getBusinessClientIdPredicate(request));

        if (fDate != null && tDate != null) {

            predicates.add(CustomPredicate.procurePredicate("lastModTs", PayrollBeanUtils.getNextORPreviousDay(fDate, false), Operation.GREATER));
            predicates.add(CustomPredicate.procurePredicate("lastModTs", PayrollBeanUtils.getNextORPreviousDay(tDate, true), Operation.LESS));

        }

        if (!IppmsUtils.isNullOrLessThanOne(pUid))
            predicates.add(CustomPredicate.procurePredicate("user.id", pUid));


        if (!IppmsUtils.isNullOrLessThanOne(pDedTypeId))
            predicates.add(CustomPredicate.procurePredicate("deductionType.id", pDedTypeId));

        if (!IppmsUtils.isNullOrLessThanOne(pEmpId)) {
            AbstractEmployeeEntity employee = IppmsUtils.loadEmployee(genericService, pEmpId, bc);
            wOgNumber = employee.getEmployeeId();
            predicates.add(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),pEmpId));
        }
        List<?> empList = this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getDeductionAuditEntityClass(bc), predicates, null);

        int wGLNoOfElements = this.genericService.getTotalPaginatedObjects(IppmsUtils.getDeductionAuditEntityClass(bc), predicates).intValue();

        DataTableBean wPELB = new DataTableBean(empList);
        wPELB.setShowRow(SHOW_ROW);

        wPELB.setFromDate(fDate);
        wPELB.setToDate(tDate);
        wPELB.setFromDateStr(PayrollBeanUtils.getDateAsString(fDate));
        wPELB.setToDateStr(PayrollBeanUtils.getDateAsString(tDate));
        wPELB.setId(pUid);
        wPELB.setOgNumber(wOgNumber);
        wPELB.setDeductionTypeId(pDedTypeId);
        wPELB.setEmpInstId(pEmpId);
        wPELB.setShowLink(true);
        return makeAndReturnView(model,bc,wPELB,null);
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