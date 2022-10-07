package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
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
@RequestMapping({"/viewPromotionLog.do"})
@SessionAttributes(types = {DataTableBean.class})
public class PromotionAuditLogFormController extends BaseController {

    private final int pageLength = 20;

    private final String VIEW_NAME = "promotion/promotionAuditLogForm";


    public PromotionAuditLogFormController() {
    }

    @ModelAttribute("userList")
    public List<User> populateUsersList(HttpServletRequest request) {

        return this.genericService.loadAllObjectsWithSingleCondition(User.class, CustomPredicate.procurePredicate("role.businessClient.id", getBusinessCertificate(request).getBusinessClientInstId()), "username");
    }

    @ModelAttribute("mdaList")
    public List<MdaInfo> populateAllMDAList(HttpServletRequest request) {

        return genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), "name");
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);


        DataTableBean wBEOB = new DataTableBean(new ArrayList<AbstractPromotionAuditEntity>());

        wBEOB.setShowRow(HIDE_ROW);
         return makeAndReturnView(model,getBusinessCertificate(request),wBEOB,null);
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"fd", "td", "uid", "eid", "mdaind"})
    public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd,
                            @RequestParam("uid") Long pUid, @RequestParam("eid") Long pEid,
                            @RequestParam("mdaind") Long pMdaInd, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        LocalDate fDate = null;
        LocalDate tDate = null;

        boolean useDates = (!IppmsUtils.treatNull(pFd).equals(EMPTY_STR)
                && !IppmsUtils.treatNull(pTd).equals(EMPTY_STR));
        if (useDates) {
            fDate = PayrollBeanUtils.setDateFromString(pFd);
            tDate = PayrollBeanUtils.setDateFromString(pTd);

        }

       // PaginationBean paginationBean = super.getPaginationInfo(request);

        List<CustomPredicate> predicates = new ArrayList<>();

        if (IppmsUtils.isNotNullAndGreaterThanZero(pUid)) {
            predicates.add(CustomPredicate.procurePredicate("user.id", pUid));
        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pEid)) {
            predicates.add(CustomPredicate.procurePredicate("employee.id", pEid));
        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pMdaInd)) {
            predicates.add(CustomPredicate.procurePredicate("mdaInfo.id", pMdaInd));
        }
        predicates.add(getBusinessClientIdPredicate(request));
        if (useDates) {
            predicates.add(CustomPredicate.procurePredicate("promotionDate", PayrollBeanUtils.getNextORPreviousDay(fDate, false), Operation.GREATER));
            predicates.add(CustomPredicate.procurePredicate("promotionDate", PayrollBeanUtils.getNextORPreviousDay(tDate, true), Operation.LESS));
        }
        List<AbstractPromotionAuditEntity>  empList = (List<AbstractPromotionAuditEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPromotionAuditClass(bc),predicates,null);



        DataTableBean wPELB = new DataTableBean(empList);

        wPELB.setShowRow(SHOW_ROW);
        if (empList != null && empList.size() > 0)
            wPELB.setShowLink(true);
        if (useDates) {
            wPELB.setFromDate(fDate);
            wPELB.setToDate(tDate);
            wPELB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(fDate));
            wPELB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(tDate));
        }
        if (IppmsUtils.isNullOrLessThanOne(pUid)) pUid = 0L;
        wPELB.setId(pUid);

        if (IppmsUtils.isNotNullAndGreaterThanZero(pEid)) {
            AbstractEmployeeEntity employee = IppmsUtils.loadEmployee(genericService, pEid, bc);
            wPELB.setOgNumber(employee.getEmployeeId());
            wPELB.setEmpInstId(pEid);
        } else {
            wPELB.setEmpInstId(0L);
        }
        if (IppmsUtils.isNotNullAndGreaterThanZero(pMdaInd))
            wPELB.setMdaInstId(pMdaInd);
        wPELB.setFilteredBySchool(IppmsUtils.isNotNullAndGreaterThanOrEqualToZero(pMdaInd));
        return makeAndReturnView(model,bc,wPELB,null);
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep, @RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") DataTableBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:auditPageHomeForm.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {

            boolean filterByEmployee = false;
            Long empId = 0L;
            if (!StringUtils.trimToEmpty(pLPB.getOgNumber()).equals(EMPTY_STR)) {

                pLPB.setOgNumber(IppmsUtils.treatOgNumber(getBusinessCertificate(request), pLPB.getOgNumber()));

                Employee wEmp = this.genericService.loadObjectUsingRestriction(Employee.class, Arrays.asList(CustomPredicate.procurePredicate("employeeId", pLPB.getOgNumber()),
                        getBusinessClientIdPredicate(request)));

                if (IppmsUtils.isNotNull(wEmp)) {
                    empId = wEmp.getId();
                }
                if (!wEmp.isNewEntity()) {

                    filterByEmployee = true;
                } else {
                    result.rejectValue("", "InvalidValue", "No "+bc.getStaffTypeName()+" found with "+bc.getStaffTitle()+" : " + pLPB.getOgNumber());

                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,bc,pLPB,result);
                }

            }
            boolean wUseDates = true;
            if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null)) {

                if (!filterByEmployee) {
                    result.rejectValue("", "InvalidValue", "Please select valid Dates");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,bc,pLPB,result);
                } else {
                    wUseDates = false;
                }
            }
            if (wUseDates) {


                if (pLPB.getFromDate() == null || pLPB.getToDate() == null) {
                    result.rejectValue("", "InvalidValue", "Please select valid Dates");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,bc,pLPB,result);
                }


                if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
                    if (!filterByEmployee) {
                        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
                        model.addAttribute(DISPLAY_ERRORS, BLOCK);
                        return makeAndReturnView(model,bc,pLPB,result);
                    } else {
                        wUseDates = false;
                    }
                }

                if (PayrollBeanUtils.getNoOfMonths(pLPB.getFromDate(), pLPB.getToDate()) > 3 && !filterByEmployee) {
                    result.rejectValue("", "InvalidValue", "Promotion Audit Log dates must be within 3 Months.");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    return makeAndReturnView(model,bc,pLPB,result);

                }


                String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
                String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
                return "redirect:viewPromotionLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId() + "&eid=" + empId + "&mdaind=" + pLPB.getMdaInstId();
            } else {
                return "redirect:viewPromotionLog.do?fd=&td=&uid=" + pLPB.getId() + "&eid=" + empId + "&mdaind=" + pLPB.getMdaInstId();
            }
        }
        return "redirect:viewPromotionLog.do";
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