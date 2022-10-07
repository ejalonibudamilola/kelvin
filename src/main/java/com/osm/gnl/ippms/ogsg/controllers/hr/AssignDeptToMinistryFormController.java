package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/assignDeptToMin.do"})
@SessionAttributes(types = {MdaDeptMap.class})
public class AssignDeptToMinistryFormController extends BaseController {

    private final String VIEW_NAME = "assign/assignDeptToMinistryForm";

    @ModelAttribute("mdaList")
    protected List<MdaInfo> loadMdas(HttpServletRequest request) {
        return this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,
                CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()),
                "name");
    }

    public AssignDeptToMinistryFormController() {
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        MdaDeptMap wHMB = new MdaDeptMap();
        Department wDept = new Department();
        MdaInfo wMda = new MdaInfo();

        wHMB.setMdaInfo(wMda);
        wHMB.setDepartment(wDept);

        model.addAttribute("pageTitle", "");
        model.addAttribute("unAssignedDepts", new ArrayList<Department>());
        model.addAttribute("assignedDepts", new ArrayList<Department>());
        model.addAttribute("miniBean", wHMB);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }


    

    @RequestMapping(method = {RequestMethod.GET}, params = {"mid"})
    public String setupForm(@RequestParam("mid") Long pPid, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        MdaDeptMap wHMB = new MdaDeptMap();

        MdaInfo wAgency = this.genericService.loadObjectUsingRestriction(MdaInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("id", pPid), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
        wHMB.setDepartment(new Department());
        wHMB.setMdaInfo(wAgency);
        List<Department> wAssignedDeptList;
        ArrayList<Long> wIdList;
        List<Department> wUADList;

        //thinking it should be loadAllObjectsWithRestriction(Dept Class, mdaInfo.id, businessClientId)
        List<MdaDeptMap> wADMList = this.genericService.loadAllObjectsWithSingleCondition(MdaDeptMap.class,
                CustomPredicate.procurePredicate("mdaInfo.id", wAgency.getId()), null);

        //thinking it should be IConstants.OFF
        wAssignedDeptList = PayrollHRUtils.makeAgencyDeptList(wADMList);
        wIdList = PayrollHRUtils.makeDeptIdListArray(wAssignedDeptList);

        wUADList = this.genericService.loadAllObjectsUsingRestrictions(Department.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "name");
        List<Department> unassignedList = new ArrayList<>();
        for (Department d : wUADList)
            if (!wIdList.contains(d.getId()))
                unassignedList.add(d);


        model.addAttribute("displayTitle", "Add Department for "+bc.getMdaTitle()+" " + wAgency.getName());
        model.addAttribute("unAssignedDepts", unassignedList);
        model.addAttribute("assignedDepts", wAssignedDeptList);
        model.addAttribute("miniBean", wHMB);
        model.addAttribute("roleBean", bc);

        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"pid", "did"})
    public String setupForm(@RequestParam("pid") Long pPid, @RequestParam("did") Long pDid, Model model, HttpServletRequest request) throws Exception {

        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        MdaDeptMap wMDM = this.genericService.loadObjectUsingRestriction(MdaDeptMap.class,
                Arrays.asList(CustomPredicate.procurePredicate("id", pPid),
                        CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));

        MdaInfo wMDA = wMDM.getMdaInfo();

        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(CustomPredicate.procurePredicate("mdaDeptMap.id", wMDM.getId()));

        Integer wInt = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,
                IppmsUtils.getEmployeeClass(bc));



        Integer wInt2 = 0;//this.hrService.checkIfMdaMappingHasPaycheckInfo(pPid, pDid);

        if (wInt.intValue() == 0 && wInt2.intValue() == 0) {
            this.genericService.deleteObject(wMDM);
        } else {

            model.addAttribute("hasMessage", true);

            model.addAttribute("messageString", BLOCK);
            String wMsg;
            if (wInt2 == 0)
                wMsg = "Department can not be removed has  it has " + wInt2
                        + " Paycheck Information attached to it. Deletion Denied.";
            else
                wMsg = "Department can not be removed has  it has " + wInt
                        + " employees assigned. Please reassign employees before removing";

            model.addAttribute("displayMessage", wMsg);
        }

        wMDM = new MdaDeptMap();
        wMDM.setMdaInfo(wMDA);
        wMDM.setDepartment(new Department());

        List<Department> wAssignedDeptList;
        ArrayList<Long> wIdList;
        List<Department> wUADList;


        List<MdaDeptMap> wADMList = this.genericService.loadAllObjectsUsingRestrictions(MdaDeptMap.class, Arrays.asList(
                CustomPredicate.procurePredicate("mdaInfo.id", wMDA.getId()),
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), null);

        wAssignedDeptList = PayrollHRUtils.makeAgencyDeptList(wADMList);
        wIdList = PayrollHRUtils.makeDeptIdListArray(wAssignedDeptList);

        wUADList = this.genericService.loadAllObjectsUsingRestrictions(Department.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("mapable", IConstants.ON)), "name");
        List<Department> unassignedList = new ArrayList<>();
        for (Department d : wUADList)
            if (!wIdList.contains(d.getId()))
                unassignedList.add(d);


        model.addAttribute("displayTitle", "Add Department for "+bc.getMdaTitle()+" " + wMDA.getName());
        model.addAttribute("unAssignedDepts", unassignedList);
        model.addAttribute("assignedDepts", wAssignedDeptList);
        model.addAttribute("miniBean", wMDM);
        model.addAttribute("roleBean", bc);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_add", required = false) String add,
                                 @ModelAttribute("miniBean") MdaDeptMap pHMB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DEPT_FXN_DASHBOARD;
        }

        if (IppmsUtils.isNullOrLessThanOne(pHMB.getDepartment().getId())) {
            result.rejectValue("department.id", "unknown.object", "Please select a 'Department' to assign.");

            model.addAttribute(DISPLAY_ERRORS, BLOCK);


            model.addAttribute("messageString", "");
            model.addAttribute("miniBean", pHMB);
            model.addAttribute("roleBean", bc);
            return VIEW_NAME;
        }

        if (IppmsUtils.isNotNullAndGreaterThanZero(pHMB.getMdaInfo().getId())) {
           /* if (StringUtils.trimToEmpty(pHMB.getDeptDirector()).equals(StringUtils.EMPTY)) {
                MdaInfo mda = this.genericService.loadObjectById(MdaInfo.class, pHMB.getMdaInfo().getId());
                result.rejectValue("deptDirector", "unknown user",
                        "Please enter the 'Head' for this Department in " + mda.getName()
                                + ". Thank you.");

                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                model.addAttribute("messageString", "");
                model.addAttribute("miniBean", pHMB);
                model.addAttribute("roleBean", bc);
                return VIEW_NAME;
            }*/
            MdaDeptMap wADM = new MdaDeptMap(bc.getBusinessClientInstId(), pHMB.getMdaInfo().getId(), pHMB.getDepartment().getId());
            wADM.setLastModBy(new User(bc.getLoginId()));
            wADM.setCreatedBy(new User(bc.getLoginId()));
            wADM.setCreationDate(Timestamp.from(Instant.now()));
            wADM.setLastModTs(Timestamp.from(Instant.now()));
            wADM.setDeptDirector("TBD");
            this.genericService.storeObject(wADM);

        }

        return "redirect:assignDeptToMin.do?mid=" + pHMB.getMdaInfo().getId();
    }
}