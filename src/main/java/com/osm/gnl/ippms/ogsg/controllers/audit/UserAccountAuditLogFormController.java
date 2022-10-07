package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.audit.domain.LoginAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping({"/viewUserAccountLogs.do"})
@SessionAttributes(types = {DataTableBean.class})
public class UserAccountAuditLogFormController extends BaseController {

    private final int pageLength = 20;
    private final String VIEW_NAME = "audit/userAccountAuditLogForm";

    public UserAccountAuditLogFormController() {
    }


    @ModelAttribute("userList")
    public List<User> populateUsersList(HttpServletRequest request) {

        return this.genericService.loadAllObjectsWithSingleCondition(User.class, CustomPredicate.procurePredicate("role.businessClient.id", getBusinessCertificate(request).getBusinessClientInstId()), "username");
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
//    PaginatedBean wBEOB = new PaginatedBean(new ArrayList<LoginAudit>(), 1, this.pageLength, 0, null, "asc");
        DataTableBean wBEOB = new DataTableBean(new ArrayList<LoginAudit>());

        wBEOB.setShowRow(HIDE_ROW);
        return makeAndReturnView(model, bc, wBEOB, null);
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"uid"})
    public String setupForm(@RequestParam("uid") Long pUid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        LocalDate fDate = LocalDate.now();
        fDate.withDayOfMonth(1);

        LocalDate tDate = LocalDate.of(fDate.getYear(), fDate.getMonthValue(), fDate.lengthOfMonth());

        return "redirect:viewUserAccountLogs.do?fd=" + PayrollBeanUtils.getDateAsString(fDate) + "&td=" + PayrollBeanUtils.getDateAsString(tDate) + "&uid=" + pUid;

    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"fd", "td", "uid"})
    public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, @RequestParam("uid") Long pUid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);


        boolean useUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUid);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(getBusinessClientIdPredicate(request));
        predicates.add(CustomPredicate.procurePredicate("lastModTs", PayrollBeanUtils.getNextORPreviousDay(fDate, false), Operation.GREATER));
        predicates.add(CustomPredicate.procurePredicate("lastModTs", PayrollBeanUtils.getNextORPreviousDay(tDate, true), Operation.LESS));
        if (useUserId)
            predicates.add(CustomPredicate.procurePredicate("login.id", pUid));

        List<LoginAudit> empList = this.genericService.loadAllObjectsUsingRestrictions(LoginAudit.class, predicates, null);


        DataTableBean wPELB = new DataTableBean(empList);

        wPELB.setShowRow(SHOW_ROW);

        if (empList != null && empList.size() > 0)
            wPELB.setShowLink(true);
        wPELB.setFromDate(fDate);
        wPELB.setToDate(tDate);
        wPELB.setId(pUid);
        wPELB.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(wPELB.getFromDate()));
        wPELB.setToDateStr(PayrollBeanUtils.getJavaDateAsString(wPELB.getToDate()));
        return makeAndReturnView(model, bc, wPELB, null);
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep,
                                @RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("miniBean") DataTableBean pLPB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:auditPageHomeForm.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
            if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null)) {
                result.rejectValue("", "InvalidValue", "Please select valid Dates");
                addDisplayErrorsToModel(model, request);

                return makeAndReturnView(model, bc, pLPB, result);
            }


            if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
                result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
                addDisplayErrorsToModel(model, request);

                return makeAndReturnView(model, bc, pLPB, result);
            }

            if (pLPB.getFromDate().getYear() != pLPB.getToDate().getYear()) {
                result.rejectValue("", "InvalidValue", "Audit Log dates must be in the same year");
                addDisplayErrorsToModel(model, request);

                return makeAndReturnView(model, bc, pLPB, result);
            }

            String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
            String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
            return "redirect:viewUserAccountLogs.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId();
        }

        return "redirect:viewUserAccountLogs.do";
    }

    private String makeAndReturnView(Model model, BusinessCertificate bc, DataTableBean dtb, BindingResult result) {

        model.addAttribute("displayList", dtb.getObjectList());
        model.addAttribute("roleBean", bc);
        if (result != null)
            model.addAttribute("status", result);
        model.addAttribute("miniBean", dtb);

        return this.VIEW_NAME;
    }
}