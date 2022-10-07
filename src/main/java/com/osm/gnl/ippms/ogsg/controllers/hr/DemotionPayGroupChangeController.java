package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.ReassignEmployeeLog;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Cadre;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeHrBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.validators.hr.DemotionPayGroupValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequestMapping({"/reassignEmpDeptForm.do"})
@SessionAttributes(types = {EmployeeHrBean.class})
public class DemotionPayGroupChangeController extends BaseController {


    private final DemotionPayGroupValidator validator;
    private final PaycheckService paycheckService;
    private final PayrollService payrollService;
    private final String VIEW_NAME = "hr/demotionPayGroupChangeForm";


    @ModelAttribute("cadreList")
    protected List<Cadre> loadCadreList(HttpServletRequest request) {
        return this.genericService.loadAllObjectsUsingRestrictions(Cadre.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("selectableInd", ON)), "name");
    }

    @ModelAttribute("salaryTypeList")
    protected List<SalaryType> loadPayGroups(HttpServletRequest request) {
        return this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("selectableInd", ON)), "name");
    }

    @Autowired
    public DemotionPayGroupChangeController(DemotionPayGroupValidator validator, PaycheckService paycheckService, PayrollService payrollService) {
        this.validator = validator;
        this.paycheckService = paycheckService;
        this.payrollService = payrollService;
    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEid, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        EmployeeHrBean wEHB = new EmployeeHrBean();
        AbstractEmployeeEntity emp = (AbstractEmployeeEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pEid)));
        wEHB.setEmployee(emp);
        wEHB.setOldRankInstId(emp.getRank().getId());
        if(bc.isSubeb()){
           // HiringInfo wHI = super.loadHiringInfoByEmpId(request,bc,emp.getId());
            wEHB.setOldDesignationId(emp.getRank().getId());
        }
        if (bc.isLocalGovt()) {

            model.addAttribute("rankList", new ArrayList());
        }
        wEHB.setShowForConfirm(HIDE_ROW);

        model.addAttribute("miniBean", wEHB);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"lid", "s"})
    public String setupForm(@RequestParam("lid") Long pRLID,
                            @RequestParam("s") int pSaved,
                            Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        EmployeeHrBean wEHB = new EmployeeHrBean();
        ReassignEmployeeLog wREL = genericService.loadObjectUsingRestriction(ReassignEmployeeLog.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pRLID)));
        wEHB.setEmployee(wREL.getParentObject());

        wEHB.setShowForConfirm(HIDE_ROW);
        String actionCompleted = "";
        if (wREL.isDemotion()) {

                actionCompleted = bc.getStaffTypeName()+" " + wREL.getParentObject().getDisplayNameWivTitlePrefixed() + " has been demoted from "
                        + "" + wREL.getOldSalaryInfo().getLevelStepStr() + " to " + wREL.getSalaryInfo().getLevelStepStr() + " successfully.";

        } else {

                actionCompleted = bc.getStaffTypeName()+" " + wREL.getParentObject().getDisplayNameWivTitlePrefixed() + " has been moved from "
                        + "" + wREL.getOldSalaryInfo().getSalaryScaleLevelAndStepStr() + " to " + wREL.getSalaryInfo().getSalaryScaleLevelAndStepStr() + " successfully.";

        }
        model.addAttribute(SAVED_MSG, actionCompleted);
        model.addAttribute("saved", true);
        model.addAttribute("logBean", wREL);
        model.addAttribute("miniBean", wEHB);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_close", required = false) String pClose,
                                @RequestParam(value = "_cancel", required = false) String cancel, @ModelAttribute("miniBean") EmployeeHrBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return REDIRECT_TO_DASHBOARD;
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_CLOSE)) {
            return "redirect:searchForEmpForm.do";
        }
        PayrollRunMasterBean wPRB = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(super.getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", 1)));
        if (!wPRB.isNewEntity() && wPRB.isRunning()) {
            result.rejectValue("", "No.Employees", "Payroll is currently being run by " + wPRB.getInitiator().getActualUserName());
            result.rejectValue("", "No.Employees", bc.getStaffTypeName() + " Reassignment/Demotion can not be done at this time.");
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            return VIEW_NAME;

        }
        if (bc.isLocalGovt())
            validator.validate(pEHB, result);
        else
            validator.validate(pEHB, result, bc);

        if (result.hasErrors()) {
            addDisplayErrorsToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            model.addAttribute("roleBean", bc);
            model = setSettables(pEHB, model, bc);

            return VIEW_NAME;
        }
        if (!pEHB.isConfirm()) {
            pEHB.setConfirm(true);
            pEHB.setRefNumber(bc.getUserName());
            pEHB.setRefDate(LocalDate.now());
            result.rejectValue("", "Confirm", "Please confirm this Reassignment. Press the 'Cancel' button if you wish to undo");

            addDisplayErrorsToModel(model, request);
            pEHB.setShowForConfirm(SHOW_ROW);
            model.addAttribute("status", result);
            model.addAttribute("miniBean", pEHB);
            model.addAttribute("roleBean", bc);
            model = setSettables(pEHB, model, bc);
            return VIEW_NAME;
        }


        SalaryInfo wSI = genericService.loadObjectById(SalaryInfo.class, pEHB.getSalaryStructureId());
        SalaryInfo wOSI = pEHB.getEmployee().getSalaryInfo();
        pEHB.getEmployee().setSalaryInfo(wSI);


        ReassignEmployeeLog wRDL = new ReassignEmployeeLog();

        wRDL.setUser(new User(bc.getLoginId()));
        if (wSI.getSalaryType().getId().equals(wOSI.getSalaryType().getId())) {
            wRDL.setDemotionInd(IConstants.ON);
        }
        wRDL.setDeptRefNumber(pEHB.getRefNumber());
        wRDL.setRefDate(pEHB.getRefDate());
        wRDL.setLastModTs(LocalDate.now());
        wRDL.setOldSalaryInfo(wOSI);
        wRDL.setSalaryInfo(wSI);
        wRDL.setBusinessClientId(bc.getBusinessClientInstId());
        if(bc.isLocalGovt()) {
            wRDL.setOldRank(new Rank(pEHB.getOldRankInstId()));
            wRDL.setRank(new Rank(pEHB.getRankInstId()));
        }

        if (bc.isPensioner())
            wRDL.setPensioner((Pensioner) pEHB.getEmployee());
        else
            wRDL.setEmployee((Employee) pEHB.getEmployee());

        wRDL.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
        wRDL.setMdaInfo(pEHB.getEmployee().getMdaDeptMap().getMdaInfo());
        // wRDL.setObjectInd(wEmp.getObjectInd());
        if (pEHB.getEmployee().isSchoolStaff()) {
            wRDL.setSchoolInfo(pEHB.getEmployee().getSchoolInfo());
        }
        wRDL.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, bc));

        LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);


        if (_wCal != null) {
            //This means we have a Pending Paycheck. Set for strictly RERUN
            RerunPayrollBean wRPB = this.genericService.loadObjectUsingRestriction(RerunPayrollBean.class, Arrays.asList(
                    CustomPredicate.procurePredicate("runMonth", _wCal.getMonthValue()), CustomPredicate.procurePredicate("runYear", _wCal.getYear()),
                    getBusinessClientIdPredicate(request)));
            wRPB.setNoOfTransfers(wRPB.getNoOfTransfers() + 1);
            if (wRPB.isNewEntity()) {
                wRPB.setRunMonth(_wCal.getMonthValue());
                wRPB.setRunYear(_wCal.getYear());
                wRPB.setRerunInd(IConstants.ON);
                wRPB.setBusinessClientId(bc.getBusinessClientInstId());
            }
            this.genericService.saveObject(wRPB);
        }
        if(bc.isLocalGovt()) {
            if (pEHB.getOldRankInstId() != pEHB.getRankInstId() && pEHB.getRankInstId() != null)
                pEHB.getEmployee().setRank(new Rank(pEHB.getRankInstId()));
            else
                pEHB.getEmployee().setRank(new Rank(pEHB.getOldRankInstId()));

        }

        pEHB.getEmployee().setLastModBy(new User(bc.getLoginId()));
        pEHB.getEmployee().setLastModTs(Timestamp.from(Instant.now()));
         this.genericService.storeObject(pEHB.getEmployee());

        Long rid = this.genericService.storeObject(wRDL);



        return "redirect:reassignEmpDeptForm.do?lid=" + rid + "&s=1";
    }

    private Model setSettables(EmployeeHrBean pEHB, Model pModel, BusinessCertificate businessCertificate) throws Exception {
        if (businessCertificate.isLocalGovt()) {
            if (!IppmsUtils.isNullOrLessThanOne(pEHB.getCadreInstId())) {
                List<Rank> wDeptList = genericService.loadAllObjectsWithSingleCondition(Rank.class, CustomPredicate.procurePredicate("cadre.id", pEHB.getCadreInstId()), "name");
                pModel.addAttribute("rankList", wDeptList);
                if (!IppmsUtils.isNullOrLessThanOne(pEHB.getRankInstId())) {
                    Rank wR = genericService.loadObjectById(Rank.class, pEHB.getRankInstId());
                    pModel.addAttribute("salaryStructureList", this.payrollService.loadSalaryInfoByRankInfo(wR, businessCertificate.getBusinessClientInstId(), true));
                } else {
                    pModel.addAttribute("salaryStructureList", new ArrayList());
                }
            } else {
                pModel.addAttribute("rankList", new ArrayList());
                pModel.addAttribute("salaryStructureList", new ArrayList());
            }
        } else {
            if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getSalaryTypeId())) {
                List<SalaryInfo> wList = genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, CustomPredicate.procurePredicate("salaryType.id", pEHB.getSalaryTypeId()), null);
                Collections.sort(wList, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
                pModel.addAttribute("salaryStructureList", wList);
            }
        }
        return pModel;
    }
}