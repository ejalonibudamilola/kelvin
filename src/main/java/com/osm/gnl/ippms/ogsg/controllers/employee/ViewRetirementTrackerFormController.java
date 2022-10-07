package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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
@RequestMapping({"/viewEmpDueForRetirement.do"})
@SessionAttributes(types = {BusinessEmpOVBeanInactive.class})
public class ViewRetirementTrackerFormController extends BaseController {

    private final int pageLength = 20;

    private final String VIEW = "retirement/retirementTrackerViewForm";

    public ViewRetirementTrackerFormController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        LocalDate wToday = LocalDate.now();

        LocalDate fDate = LocalDate.of(wToday.getYear(), wToday.getMonthValue(), 1);
        LocalDate tDate = LocalDate.of(wToday.getYear(), wToday.getMonthValue(), wToday.lengthOfMonth());
        //Ola Bug
       // tDate = null;

        return "redirect:viewEmpDueForRetirement.do?fd=" + PayrollBeanUtils.getDateAsString(fDate) + "&td=" + PayrollBeanUtils.getDateAsString(tDate);
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"fd", "td"})
    public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

        PaginationBean paginationBean = getPaginationInfo(request);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(getBusinessClientIdPredicate(request));
        if(bc.isPensioner()){
            predicates.addAll(Arrays.asList(CustomPredicate.procurePredicate("amAliveDate", fDate, Operation.GREATER_OR_EQUAL),
                    CustomPredicate.procurePredicate("amAliveDate", tDate, Operation.LESS_OR_EQUAL)));
        }else{
            predicates.addAll(Arrays.asList(
                    CustomPredicate.procurePredicate("terminateInactive", "N", Operation.EQUALS),
                    CustomPredicate.procurePredicate("expectedDateOfRetirement", fDate, Operation.GREATER_OR_EQUAL),
                    CustomPredicate.procurePredicate("expectedDateOfRetirement", tDate, Operation.LESS_OR_EQUAL)));
        }


//        List<HiringInfo> empList = this.genericService.loadPaginatedObjects(HiringInfo.class, predicates,
//                (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());


        List<HiringInfo> empList = this.genericService.loadAllObjectsUsingRestrictions(HiringInfo.class, predicates, null);


        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(predicates);

        int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, HiringInfo.class);

        BusinessEmpOVBeanInactive pCList = new BusinessEmpOVBeanInactive(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        pCList.setFromDate(fDate);
        pCList.setToDate(tDate);
        pCList.setFromDateStr(PayrollHRUtils.getFullDateFormat().format(pCList.getFromDate()));
        pCList.setToDateStr(PayrollHRUtils.getFullDateFormat().format(pCList.getToDate()));
        pCList.setShowRow(SHOW_ROW);
        pCList.setHasData((empList.isEmpty()) || (wGLNoOfElements == 0));

        model.addAttribute("miniBean", pCList);
        addRoleBeanToModel(model, request);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep, @RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("miniBean") BusinessEmpOVBeanInactive pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
            if ((pLPB.getFromDate() == null) || (pLPB.getToDate() == null)) {
                result.rejectValue("", "InvalidValue", "Please select valid Dates");
                addDisplayErrorsToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                addRoleBeanToModel(model, request);
                return VIEW;
            }
            LocalDate date1 = pLPB.getFromDate();
            LocalDate date2 = pLPB.getToDate();

            if (date1.isAfter(date2)) {
                result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
                addDisplayErrorsToModel(model, request);

                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);

                addRoleBeanToModel(model, request);
                return VIEW;
            }

            String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
            String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());
            return "redirect:viewEmpDueForRetirement.do?fd=" + sDate + "&td=" + eDate;
        }

        return "redirect:viewEmpDueForRetirement.do";
    }
}