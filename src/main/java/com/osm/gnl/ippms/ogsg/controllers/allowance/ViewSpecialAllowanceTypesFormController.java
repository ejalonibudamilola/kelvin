package com.osm.gnl.ippms.ogsg.controllers.allowance;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping({"/viewSpecAllowTypes.do"})
@SessionAttributes("specAllowTypeBean")
public class ViewSpecialAllowanceTypesFormController extends BaseController {

    private final int pageLength = 20;
    private final String VIEW_NAME = "employee/viewEmpSpecAllowTypeForm";


    public ViewSpecialAllowanceTypesFormController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);


        List<?> empList = this.genericService.loadAllObjectsWithSingleCondition(SpecialAllowanceType.class, CustomPredicate.procurePredicate("businessClientId", this.getBusinessCertificate(request).getBusinessClientInstId()),"name");
        PaginatedBean wPELB = new PaginatedBean(empList);
        wPELB.setShowRow(SHOW_ROW);
        model.addAttribute("roleBean", super.getBusinessCertificate(request));
        model.addAttribute("specAllowTypeBean", wPELB);
        model.addAttribute("displayList", wPELB.getObjectList());

        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"san", "sat"})
    public String setupForm(@RequestParam("san") String pTypeName,
                            @RequestParam("sat") String pTypeCode, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        PaginationBean paginationBean = this.getPaginationInfo(request);

        BusinessCertificate bc = new BusinessCertificate();


        PredicateBuilder predicateBuilder = new PredicateBuilder();
        if (!IppmsUtils.treatNull(pTypeName).equals(EMPTY_STR)) {

            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("description", IppmsUtils.treatNull(pTypeName), Operation.LIKE));

        }


        if (!IppmsUtils.treatNull(pTypeCode).equals(EMPTY_STR)) {

            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("name", IppmsUtils.treatNull(pTypeCode), Operation.LIKE));
        }

        predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));


        List<?> empList = this.genericService.loadPaginatedObjects(SpecialAllowanceType.class, predicateBuilder.getPredicates(),
                (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());


        int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, SpecialAllowanceType.class);


        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPELB.setShowRow(SHOW_ROW);
        model.addAttribute("roleBean", super.getBusinessCertificate(request));
        model.addAttribute("specAllowTypeBean", wPELB);
        model.addAttribute("displayList", wPELB.getObjectList());

        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_update", required = false) String cancel,
                                @ModelAttribute("specAllowTypeBean") PaginatedBean pEHB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        return "redirect:viewSpecAllowTypes.do?san=" + IppmsUtils.treatNull(pEHB.getName()) + "&sat=" + IppmsUtils.treatNull(pEHB.getTypeCode());
    }
}