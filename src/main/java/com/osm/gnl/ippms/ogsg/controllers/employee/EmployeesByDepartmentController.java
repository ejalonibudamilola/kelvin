package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaginationService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/deptEmpDetails.do"})
@SessionAttributes(types = {PaginatedBean.class})
public class EmployeesByDepartmentController extends BaseController {
    @Autowired
    private PaginationService paginationService;

    private final int pageLength = 20;

    private final String VIEW = "employee/employeeAssignedToDeptForm";

    public EmployeesByDepartmentController() {
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"mid"})
    public String setupForm(@RequestParam("mid") Long pMid, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate businessCertificate = getBusinessCertificate(request);
        String wObjectName = "";
        String wObject = "";
        String wDeptName = "";


        MdaDeptMap wADM = genericService.loadObjectUsingRestriction(MdaDeptMap.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("id", pMid)));

        wObject = businessCertificate.getMdaTitle();
        wObjectName = wADM.getMdaInfo().getName();
        wDeptName = wADM.getDepartment().getName();
        if (wADM.getPreferredName() != null)
            wDeptName = wADM.getPreferredName();

        PaginationBean paginationBean = getPaginationInfo(request);
        CustomPredicate customPredicate;
        if (businessCertificate.isPensioner())
            customPredicate = CustomPredicate.procurePredicate("pensioner.mdaDeptMap.id", pMid);
        else
            customPredicate = CustomPredicate.procurePredicate("employee.mdaDeptMap.id", pMid);
        List<CustomPredicate> predicates = Arrays.asList(customPredicate, getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("terminateInactive", "N"));
//        List<HiringInfo> empList = this.genericService.loadPaginatedObjects(HiringInfo.class, predicates, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder()
//                , paginationBean.getSortCriterion());

        List<HiringInfo> empList = this.genericService.loadAllObjectsUsingRestrictions(HiringInfo.class, predicates,null);

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("mdaDeptMap.id", pMid)).addPredicate(getBusinessClientIdPredicate(request));
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("statusIndicator", 0));
        int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getEmployeeClass(businessCertificate));

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPHDB.setRoleBean(businessCertificate);
        wPHDB.setDepartmentName(wDeptName);
        wPHDB.setMbapName(wObject);
        wPHDB.setMinistryName(wObjectName);
        wPHDB.setNoOfEmployees(wNoOfElements);

        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wPHDB);
        model.addAttribute("displayList", empList);

        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"oid", "tind", "filter"})
    public String setupForm(@RequestParam("oid") Long pMid, @RequestParam("tind") int pOid, @RequestParam("filter") String pFilter, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate businessCertificate = getBusinessCertificate(request);

        String wObjectName = "";
        String wObject = "";


        MdaInfo wADM = genericService.loadObjectUsingRestriction(MdaInfo.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pMid)));

        wObject = businessCertificate.getMdaTitle();
        wObjectName = wADM.getName();

        PaginationBean paginationBean = getPaginationInfo(request);

        List<HiringInfo> empList = this.paginationService.getActiveEmployeesByObjectAndCode(businessCertificate, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), pMid);

        int wNoOfElements = this.paginationService.getTotalNoOfActiveEmployeesByObjectAndCode(businessCertificate, pMid);

        PaginatedBean wPHDB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPHDB.setDepartmentName("All Departments");
        wPHDB.setMbapName(wObject);
        wPHDB.setMinistryName(wObjectName);
        wPHDB.setNoOfEmployees(wNoOfElements);
        wPHDB.setRoleBean(businessCertificate);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", wPHDB);
        model.addAttribute("displayList", empList);

        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("miniBean") PaginatedBean pHADB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        return "redirect:determineDashBoard.do";
    }
}