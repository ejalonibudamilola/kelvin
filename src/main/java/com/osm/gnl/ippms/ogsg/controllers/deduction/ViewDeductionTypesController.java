package com.osm.gnl.ippms.ogsg.controllers.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/viewDeductionTypes.do"})
@SessionAttributes("dedTypeBean")
public class ViewDeductionTypesController extends BaseController {

    private final int pageLength = 20;


    public ViewDeductionTypesController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

//        PaginationBean paginationBean = this.getPaginationInfo(request);

//        List<EmpDeductionType> empList = this.genericService.loadPaginatedObjects(EmpDeductionType.class, Arrays.asList(getBusinessClientPredicate(request)),
//                (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

//        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(getBusinessClientPredicate(request));
//
//        int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmpDeductionType.class);
//
//        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        List<EmpDeductionType> empList = this.genericService.loadAllObjectsWithSingleCondition(EmpDeductionType.class,CustomPredicate.procurePredicate("businessClientId",this.getBusinessCertificate(request).getBusinessClientInstId()),"name");
        DataTableBean wPELB = new DataTableBean(empList);

        wPELB.setShowRow(SHOW_ROW);

        model.addAttribute("roleBean", super.getBusinessCertificate(request));
        model.addAttribute("dedTypeBean", wPELB);
        model.addAttribute("displayList",wPELB.getObjectList());

        return "deduction/viewEmpDeductionType";
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"dn", "tc"})
    public String setupForm(@RequestParam("dn") String pTypeName,
                            @RequestParam("tc") String pTypeCode, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        PaginationBean paginationBean = this.getPaginationInfo(request);


        List<CustomPredicate> wPredicates = new ArrayList<>();
        if (!IppmsUtils.treatNull(pTypeName).equals(EMPTY_STR)) {

            wPredicates.add(CustomPredicate.procurePredicate("description", IppmsUtils.treatNull(pTypeName), Operation.LIKE));
        }


        if (!IppmsUtils.treatNull(pTypeCode).equals(EMPTY_STR)) {
            wPredicates.add(CustomPredicate.procurePredicate("name", IppmsUtils.treatNull(pTypeCode), Operation.LIKE));

        }

        wPredicates.add(getBusinessClientIdPredicate(request));

        List<EmpDeductionType> empList = this.genericService.loadPaginatedObjects(EmpDeductionType.class, wPredicates,
                (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());


        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(wPredicates);

        int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, EmpDeductionType.class);


        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPELB.setShowRow(SHOW_ROW);
        model.addAttribute("roleBean", super.getBusinessCertificate(request));
        model.addAttribute("dedTypeBean", wPELB);

        return "deduction/viewEmpDeductionType";
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_update", required = false) String cancel,
                                @ModelAttribute("dedTypeBean") PaginatedBean pEHB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        return "redirect:viewDeductionTypes.do?dn=" + IppmsUtils.treatNull(pEHB.getName()) + "&tc=" + IppmsUtils.treatNull(pEHB.getTypeCode());
    }
}