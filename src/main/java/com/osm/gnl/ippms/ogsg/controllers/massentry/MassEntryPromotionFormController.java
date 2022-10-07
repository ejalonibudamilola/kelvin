package com.osm.gnl.ippms.ogsg.controllers.massentry;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.MassEntryService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.approval.StepIncrementApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.massentry.PromotionHistory;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncrementTracker;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.hr.MassPromotionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequestMapping({"/massPromotions.do"})
@SessionAttributes(types = {PaginatedPaycheckGarnDedBeanHolder.class})
public class MassEntryPromotionFormController extends BaseController {


    private final MassPromotionValidator validator;
    private final PaycheckService paycheckService;
    private final MassEntryService massEntryService;

    private final int pageLength = 10;
    private String fPayPeriod;
    private final String VIEW = "massentry/massEntryPromotionForm";
    @Autowired
    public MassEntryPromotionFormController(MassPromotionValidator validator, PaycheckService paycheckService, MassEntryService massEntryService) {
        this.validator = validator;

        this.paycheckService = paycheckService;
        this.massEntryService = massEntryService;
    }


    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);

        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_PROMO_ATTR_NAME);

        if ((wPGBDH != null) && (!wPGBDH.isNewEntity())) {
            removeSessionAttribute(request, MASS_PROMO_ATTR_NAME);
        }

        List<PromotionHistory> wPromoHist = new ArrayList<PromotionHistory>();
        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPromoHist, paginationBean.getPageNumber(), this.pageLength, 0, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setPaginationListHolder(wPromoHist);
        addSessionAttribute(request, MASS_PROMO_ATTR_NAME, wPGBDH);
        wPGBDH.setShowArrearsRow(HIDE_ROW);
        wPGBDH.setActiveInd(ON);
        model.addAttribute("salaryTypeList", new ArrayList<SalaryType>());
        model.addAttribute("salaryStructureList", new ArrayList<SalaryInfo>());
        model.addAttribute("massPromoBean", wPGBDH);
        addRoleBeanToModel(model, request);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PaginationBean paginationBean = this.getPaginationInfo(request);

        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_PROMO_ATTR_NAME);
        Long wSalaryTypeId = wPGBDH.getSalaryTypeId();
        List<PromotionHistory> wAllList = new ArrayList<>();
        List<PromotionHistory> wNewList;
        for (PromotionHistory p : (List<PromotionHistory>) wPGBDH.getPaginationListHolder()) {

            if (p.getId().equals(pEmpId)) {
                continue;
            }
            wAllList.add(p);
        }
        int pageNumber = paginationBean.getPageNumber();
        if (wAllList.size() > this.pageLength) {
            Double wDouble = new Double(wAllList.size()) / new Double(this.pageLength);
            pageNumber = Integer.parseInt(String.valueOf(wDouble).substring(0, String.valueOf(wDouble).indexOf(".")));
        }
        wNewList = (List<PromotionHistory>) PayrollUtils.paginateList(pageNumber, this.pageLength, wAllList);
        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, pageNumber, this.pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPGBDH.setEmptyList(wNewList.size() > 0);
        if (!wPGBDH.isEmptyList()) {
            //Now check if the list is set to the last Object...
            if (wNewList.size() == 1) {
                wSalaryTypeId = (wNewList.get(0).getEmployee().getSalaryInfo().getSalaryType().getId());
            }
            wPGBDH.setSalaryTypeId(wSalaryTypeId);
            model.addAttribute("salaryTypeList", Arrays.asList(this.genericService.loadObjectUsingRestriction(SalaryType.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("id", wSalaryTypeId)))));

            List<SalaryInfo> salaryInfoList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("salaryType.id", wSalaryTypeId)), null);
            Collections.sort(salaryInfoList, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
            model.addAttribute("salaryStructureList", salaryInfoList);
        } else {
            model.addAttribute("salaryTypeList", new ArrayList<SalaryType>());
            model.addAttribute("salaryStructureList", new ArrayList<SalaryInfo>());
        }
        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setPaginationListHolder(wAllList);
        addSessionAttribute(request, MASS_PROMO_ATTR_NAME, wPGBDH);
        if (Boolean.valueOf(wPGBDH.getPayArrearsInd()).booleanValue()) {
            wPGBDH.setShowArrearsRow(SHOW_ROW);
        } else {
            wPGBDH.setShowArrearsRow(HIDE_ROW);
        }
        wPGBDH.setActiveInd(ON);
        model.addAttribute("massPromoBean", wPGBDH);
        addRoleBeanToModel(model, request);
        return VIEW;
    }


    @RequestMapping(method = {RequestMethod.GET}, params = {"lid", "act"})
    public String setupForm(@RequestParam("lid") Long pLoginId, @RequestParam("act") String pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        PaginationBean paginationBean = this.getPaginationInfo(request);

        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_PROMO_ATTR_NAME);

        Long wSalaryTypeInstId = wPGBDH.getSalaryTypeId();
        List<PromotionHistory> wAllList = (List<PromotionHistory>) wPGBDH.getPaginationListHolder();

        if (wAllList == null) {
            wAllList = new ArrayList<>();
        }

        List<PromotionHistory> wNewList = (List<PromotionHistory>) PayrollUtils.paginateList(paginationBean.getPageNumber(), this.pageLength, wAllList);
        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, paginationBean.getPageNumber(), this.pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder(), wPGBDH.getHighestLevel(), wPGBDH.getLowestLevel(), wPGBDH.getHighestStep(), wPGBDH.getLowestStep());

        wPGBDH.setEmptyList(wAllList.size() > 0);
        wPGBDH.setPaginationListHolder(wAllList);
        wPGBDH.setId(pLoginId);
        wPGBDH.setSalaryTypeId(wSalaryTypeInstId);
        wPGBDH.setActiveInd(ON);
        if (Boolean.valueOf(wPGBDH.getPayArrearsInd()).booleanValue()) {
            wPGBDH.setShowArrearsRow(SHOW_ROW);
        } else {
            wPGBDH.setShowArrearsRow(HIDE_ROW);
        }
        addSessionAttribute(request, MASS_PROMO_ATTR_NAME, wPGBDH);

        SalaryType salaryTypes = this.genericService.loadObjectUsingRestriction(SalaryType.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("id", wSalaryTypeInstId)));

        model.addAttribute("salaryTypeList", Arrays.asList(salaryTypes));

        List<SalaryInfo> salaryInfoList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("salaryType.id", wSalaryTypeInstId)), null);
        Collections.sort(salaryInfoList, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
        model.addAttribute("salaryStructureList", salaryInfoList);
        model.addAttribute("massPromoBean", wPGBDH);
        addRoleBeanToModel(model, request);
        return VIEW;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_search", required = false) String search,
                                @RequestParam(value = "_promote", required = false) String promote,
                                @ModelAttribute("massPromoBean") PaginatedPaycheckGarnDedBeanHolder pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            removeSessionAttribute(request, MASS_PROMO_ATTR_NAME);
            return "redirect:massEntryMainDashboard.do";
        }
        List<PromotionHistory> wPromoHist = (List<PromotionHistory>) pEHB.getPaginationListHolder();


        if (wPromoHist == null) {
            wPromoHist = new ArrayList<>();
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_SEARCH)) {

            //Here check if there is OG Number there...

            Employee wEmp = this.genericService.loadObjectUsingRestriction(Employee.class, Arrays.asList(
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                    CustomPredicate.procurePredicate("employeeId", pEHB.getStaffId())));

            if (wEmp.isNewEntity()) {
                result.rejectValue("", "Global.Change", "No " + bc.getStaffTypeName() + " Found with  '" + pEHB.getStaffId() + "'");
                addDisplayErrorsToModel(model, request);
                if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                    pEHB.setShowArrearsRow(SHOW_ROW);
                } else {
                    pEHB.setShowArrearsRow(HIDE_ROW);
                }
                model = this.determineSalaryInformation(wPromoHist, wEmp, pEHB, request, model);
                model.addAttribute("status", result);
                model.addAttribute("massPromoBean", pEHB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }else{
                HiringInfo hiringInfo  = this.loadHiringInfoByEmpId(request,bc,wEmp.getId());
                if(wEmp.isTerminated() || hiringInfo.isSuspendedEmployee()){
                    if(wEmp.isTerminated())
                      result.rejectValue("", "Global.Change", "" + bc.getStaffTypeName()+" " + wEmp.getDisplayNameWivTitlePrefixed()+" is Terminated. Promotion Denied.");
                    else
                        result.rejectValue("", "Global.Change", "" + bc.getStaffTypeName()+" " + wEmp.getDisplayNameWivTitlePrefixed()+" is currently Suspended. Promotion Denied.");
                    addDisplayErrorsToModel(model, request);
                    if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                        pEHB.setShowArrearsRow(SHOW_ROW);
                    } else {
                        pEHB.setShowArrearsRow(HIDE_ROW);
                    }
                    model = this.determineSalaryInformation(wPromoHist, wEmp, pEHB, request, model);
                    model.addAttribute("status", result);
                    model.addAttribute("massPromoBean", pEHB);
                    addRoleBeanToModel(model, request);
                    return VIEW;
                }
            }


            for (Object e : pEHB.getPaginationListHolder()) {
                if (((PromotionHistory) e).getId().equals(wEmp.getId())) {
                    result.rejectValue("", "Global.Change", bc.getStaffTypeName() + " '" + wEmp.getDisplayName() + "' is already added");
                    addDisplayErrorsToModel(model, request);
                    if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                        pEHB.setShowArrearsRow(SHOW_ROW);
                    } else {
                        pEHB.setShowArrearsRow(HIDE_ROW);
                    }
                    model = this.determineSalaryInformation(wPromoHist, wEmp, pEHB, request, model);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massPromoBean", pEHB);
                    return VIEW;
                }
            }

            pEHB = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_PROMO_ATTR_NAME);


            if (wPromoHist.isEmpty()) {
                //Set the SalaryType for this Employee as Guide..
                pEHB.setSalaryTypeId(wEmp.getSalaryInfo().getSalaryType().getId());
                pEHB.setHighestLevel(wEmp.getSalaryInfo().getLevel());
                pEHB.setHighestStep(wEmp.getSalaryInfo().getStep());
                pEHB.setFirstRecord(true);

            } else {
                pEHB.setFirstRecord(false);
                if (!wEmp.getSalaryInfo().getSalaryType().getId().equals(pEHB.getSalaryTypeId())) {
                    result.rejectValue("salaryTypeId", "Global.Change", bc.getStaffTypeName() + " '" + wEmp.getDisplayName() + "' Pay Group " +
                            "( '" + wEmp.getSalaryInfo().getSalaryType().getName() + "' )" + " conflicts with pre-added " + bc.getStaffTypeName()
                            + "(s) Pay Group of '" + (this.genericService.loadObjectUsingRestriction(SalaryType.class,
                            Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                                    CustomPredicate.procurePredicate("id", pEHB.getSalaryTypeId())))).getName() + "'");
                    result.rejectValue("salaryTypeId", "Global.Change", "Only " + bc.getStaffTypeName() + " belonging to the same Pay Group can be promoted using Mass Entry for Promotions.");
                    result.rejectValue("salaryTypeId", "Global.Change", bc.getStaffTypeName() + "  '" + wEmp.getDisplayName() + "'s addition denied.");

                    addDisplayErrorsToModel(model, request);
                    if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                        pEHB.setShowArrearsRow(SHOW_ROW);
                    } else {
                        pEHB.setShowArrearsRow(HIDE_ROW);
                    }
                    model = this.determineSalaryInformation(wPromoHist, wEmp, pEHB, request, model);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massPromoBean", pEHB);
                    return VIEW;
                }
                //Now check for Status of Steps and levels
                if (wEmp.getSalaryInfo().getLevel() > pEHB.getHighestLevel()) {
                    pEHB.setLowestLevel(pEHB.getHighestLevel());
                    pEHB.setHighestLevel(wEmp.getSalaryInfo().getLevel());
                    pEHB.setHighestStep(wEmp.getSalaryInfo().getStep());


                } else if (wEmp.getSalaryInfo().getLevel() == pEHB.getHighestLevel()) {
                    if (wEmp.getSalaryInfo().getStep() > pEHB.getHighestStep()) {

                        pEHB.setHighestStep(wEmp.getSalaryInfo().getStep());
                    }
                }
            }

            PromotionHistory wPromHistBean = new PromotionHistory();
            wPromHistBean.setEntryIndex(wPromoHist.size() + 1);
            wPromHistBean.setEmployee(wEmp);
            wPromHistBean.setOldSalaryLevelAndStep(wEmp.getSalaryInfo().getSalaryScaleLevelAndStepStr());
            wPromHistBean.setId(wEmp.getId());
            wPromHistBean.setSalaryInfo(wEmp.getSalaryInfo());
            wPromoHist.add(wPromHistBean);


            pEHB.setPaginationListHolder(wPromoHist);


            addSessionAttribute(request, MASS_PROMO_ATTR_NAME, pEHB);

            return "redirect:massPromotions.do?lid=" + bc.getLoginId() + "&act=y";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_PROMOTE)) {


            if (IppmsUtils.isNullOrLessThanOne(pEHB.getSalaryTypeId())) {
                result.rejectValue("", "Global.Change", "Please select Designation (i.e., Pay Group)");
                addDisplayErrorsToModel(model, request);
                if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                    pEHB.setShowArrearsRow(SHOW_ROW);
                } else {
                    pEHB.setShowArrearsRow(HIDE_ROW);
                }
                if (!IppmsUtils.treatNull(pEHB.getStaffId()).equals(EMPTY_STR))
                    pEHB.setStaffId("");
                model = this.determineSalaryInformation(wPromoHist, null, pEHB, request, model);
                model.addAttribute("status", result);
                model.addAttribute("massPromoBean", pEHB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }

            if (IppmsUtils.isNullOrLessThanOne(pEHB.getSalaryStructureId())) {
                if (!IppmsUtils.treatNull(pEHB.getStaffId()).equals(EMPTY_STR))
                    pEHB.setStaffId("");
                if (IppmsUtils.isNullOrLessThanOne(pEHB.getSalaryTypeId())) {
                    result.rejectValue("", "Global.Change", "Please select Designation (i.e., Pay Group)");
                    addDisplayErrorsToModel(model, request);
                    if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                        pEHB.setShowArrearsRow(SHOW_ROW);
                    } else {
                        pEHB.setShowArrearsRow(HIDE_ROW);
                    }
                    model = this.determineSalaryInformation(wPromoHist, null, pEHB, request, model);
                    model.addAttribute("status", result);
                    model.addAttribute("massPromoBean", pEHB);
                    addRoleBeanToModel(model, request);
                    return VIEW;
                }

                result.rejectValue("", "Global.Change", "Please select Level & Step To promote all to");
                addDisplayErrorsToModel(model, request);
                if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                    pEHB.setShowArrearsRow(SHOW_ROW);
                } else {
                    pEHB.setShowArrearsRow(HIDE_ROW);
                }
                model = this.determineSalaryInformation(wPromoHist, null, pEHB, request, model);
                model.addAttribute("status", result);
                model.addAttribute("massPromoBean", pEHB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }


            if ((pEHB == null) || (pEHB.getPaginationListHolder().isEmpty())) {
                if (!IppmsUtils.treatNull(pEHB.getStaffId()).equals(EMPTY_STR))
                    pEHB.setStaffId("");
                if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                    pEHB.setShowArrearsRow(SHOW_ROW);
                } else {
                    pEHB.setShowArrearsRow(HIDE_ROW);
                }
                result.rejectValue("salaryTypeId", "Global.Change", "Please add " + bc.getStaffTypeName() + " to promote.");
                addDisplayErrorsToModel(model, request);
                model = this.determineSalaryInformation(wPromoHist, null, pEHB, request, model);
                model.addAttribute("status", result);
                model.addAttribute("massPromoBean", pEHB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }
            //--Here add check for Salary Arrears...
//      pEHB.setBusinessCertificate(bc); //Only for validaton.
            validator.validate(pEHB, result, bc);
            if (result.hasErrors()) {
                addDisplayErrorsToModel(model, request);
                if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                    pEHB.setShowArrearsRow(SHOW_ROW);
                } else {
                    pEHB.setShowArrearsRow(HIDE_ROW);
                }
                if (!IppmsUtils.treatNull(pEHB.getStaffId()).equals(EMPTY_STR))
                    pEHB.setStaffId("");
                model = this.determineSalaryInformation(wPromoHist, null, pEHB, request, model);
                model.addAttribute("status", result);
                model.addAttribute("massPromoBean", pEHB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }
            boolean errors = true;
            int counter = 0;
            int ignoreRecords = 0;
            SalaryInfo wSalaryInfo = this.genericService.loadObjectUsingRestriction(SalaryInfo.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("id", pEHB.getSalaryStructureId())));
            if (wSalaryInfo.getLevel() == pEHB.getLowestLevel()) {
                if (wSalaryInfo.getStep() < pEHB.getLowestStep()) {
                    result.rejectValue("salaryTypeId", "Global.Change", "This Module is strictly for Promotions.");
                    result.rejectValue("salaryTypeId", "Global.Change", "Level and Step " + wSalaryInfo.getLevelAndStepAsStr() + " will result in a Demotion for some " + bc.getStaffTypeName() + "(s)");
                } else if (wSalaryInfo.getStep() == pEHB.getLowestStep()) {
                    result.rejectValue("salaryTypeId", "Global.Change", "This Module is strictly for Promotions.");
                    result.rejectValue("salaryTypeId", "Global.Change", "Level and Step " + wSalaryInfo.getLevelAndStepAsStr() + " will not change for some " + bc.getStaffTypeName() + "(s)");

                } else {
                    errors = false;
                }
            }
            if (wSalaryInfo.getLevel() == pEHB.getHighestLevel()) {
                if (wSalaryInfo.getStep() < pEHB.getHighestStep()) {
                    result.rejectValue("salaryTypeId", "Global.Change", "This Module is strictly for Promotions.");
                    result.rejectValue("salaryTypeId", "Global.Change", "Level and Step " + wSalaryInfo.getLevelAndStepAsStr() + " will result in a Demotion for some " + bc.getStaffTypeName() + "(s)");
                } else if (wSalaryInfo.getStep() == pEHB.getHighestStep()) {
                    result.rejectValue("salaryTypeId", "Global.Change", "This Module is strictly for Promotions.");
                    result.rejectValue("salaryTypeId", "Global.Change", "Level and Step " + wSalaryInfo.getLevelAndStepAsStr() + " will not change for some " + bc.getStaffTypeName() + "(s)");

                } else {
                    errors = false;
                }
            } else {
                if(!pEHB.isIncrementWarningIssued() ){
                    Map<Long,StepIncrementTracker> stepIncrementTrackerHashMap = (Map<Long, StepIncrementTracker>) genericService.loadObjectAsMapWithObjectKey(StepIncrementTracker.class,
                            Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("year", LocalDate.now().getYear())),"id");
                    StepIncrementTracker stepIncrementTracker;
                    for (PromotionHistory p : wPromoHist){
                        if(IppmsUtilsExt.isStepIncrementTypePromotion(  p.getEmployee().getSalaryInfo().getId(),wSalaryInfo.getId(),genericService)) {
                            //now check if we can promote or it needs Approval Created for it.
                            p.setStepIncrementType(true);
                            counter++;
                            stepIncrementTracker = stepIncrementTrackerHashMap.get(p.getEmployee().getId());
                            if (stepIncrementTracker != null) {
                                //This means there is already a Step Increment ran for this dude.
                                if (stepIncrementTracker.getNoOfTimes() == 2) {
                                    ignoreRecords++;
                                } else if (stepIncrementTracker.getNoOfTimes() == 1) {
                                    p.setNormalPay(true);
                                    p.setStepIncrementTracker(stepIncrementTracker);
                                }
                            }
                        }

                    }
                    if(counter > 0){
                        result.rejectValue("salaryTypeId", "Global.Change", counter+" "+bc.getStaffTypeName()+ "'s  Found where this promotion is a Step Increment.");
                        result.rejectValue("salaryTypeId", "Global.Change", ignoreRecords+" "+bc.getStaffTypeName()+ "'s  of this have Step Increment done twice already this year. They will be ignored.");
                        result.rejectValue("salaryTypeId", "Global.Change", "This Module is strictly for Promotions.");
                        result.rejectValue("salaryTypeId", "Global.Change", "The Affected "+bc.getStaffTypeName()+" MIGHT NOT be Promoted.");

                    }
                    pEHB.setIncrementWarningIssued(true);
                } else{
                    errors = false;

                }

            }

            if (errors) {

                if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                    pEHB.setShowArrearsRow(SHOW_ROW);
                } else {
                    pEHB.setShowArrearsRow(HIDE_ROW);
                }
                if (!IppmsUtils.treatNull(pEHB.getStaffId()).equals(EMPTY_STR))
                    pEHB.setStaffId("");


                addDisplayErrorsToModel(model, request);
                model = this.determineSalaryInformation(wPromoHist, null, pEHB, request, model);
                model.addAttribute("status", result);
                model.addAttribute("massPromoBean", pEHB);
                addRoleBeanToModel(model, request);
                return VIEW;
            }
            pEHB.setNewSalaryScaleLevelAndStepStr(wSalaryInfo.getSalaryScaleLevelAndStepStr());
            pEHB.setTypeInd(IConstants.PROMOTION);
            if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue()) {
                pEHB.setPayingArrears(true);

            }
            fPayPeriod = PayrollUtils.makePayPeriodForAuditLogs(genericService, bc);
            addSessionAttribute(request, MASS_PROMO_ATTR_NAME, pEHB);


            wPromoHist = (List<PromotionHistory>) pEHB.getPaginationListHolder();
            List<Long> wIntegerList = new ArrayList<Long>();
            List<AbstractSpecialAllowanceEntity> wSAIList = new ArrayList<>();
            List<AbstractPromotionAuditEntity> wPAList = new ArrayList<>();
            List<PromotionTracker> wPTList = new ArrayList<>();
            List<NamedEntity> wSList = new ArrayList<>();

            int wAddendum = 0;
            LocalDate wCurrentDate = LocalDate.now();
            String wRefNum = bc.getUserName() + "/ME_" + PayrollBeanUtils.getDateAsStringWidoutSeparators(wCurrentDate);
            SpecialAllowanceType wSAT = this.genericService.loadObjectUsingRestriction(SpecialAllowanceType.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            (CustomPredicate.procurePredicate("arrearsInd", 1))));


            for (PromotionHistory p : wPromoHist) {

                    if(p.isStepIncrementType()){
                        if(!p.isNormalPay())
                            continue;
                        else {
                            //Create the Approval Process...
                            createStepIncrementApproval(p,bc,pEHB.getSalaryStructureId());
                            continue;
                        }
                    }

                wIntegerList.add(p.getEmployee().getId());
                p.setNewSalaryLevelAndStep(pEHB.getNewSalaryScaleLevelAndStepStr());
                wAddendum++;

                //Now Add The SAI Object ass well...
                if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue() && !wSAT.isNewEntity()) {
                    p.setAmountStr(pEHB.getAmountStr());
                    //-- Now we need to Create a Salary Arrears Special Allowance...
                    AbstractSpecialAllowanceEntity wSAI = (AbstractSpecialAllowanceEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getSpecialAllowanceInfoClass(bc), Arrays.asList(
                            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("id", p.getEmployee().getId()), CustomPredicate.procurePredicate("specialAllowanceType.id", wSAT.getId())));
                    if (wSAI.isNewEntity()) {
                        wSAI = IppmsUtils.makeSpecialAllowanceInfoObject(bc);
                        wSAI.setSpecialAllowanceType(wSAT);
                        PayTypes wPT = new PayTypes(1001L);
                        wSAI.makeParentObject(p.getId());
                        wSAI.setPayTypes(wPT);
                        wSAI.setCreatedBy(new User(bc.getLoginId()));
                        wSAI.setCreationDate(Timestamp.from(Instant.now()));
                    }

                    wSAI.setStartDate(pEHB.getStartDate());
                    wSAI.setEndDate(pEHB.getEndDate());
                    wSAI.setExpire(IConstants.OFF);
                    wSAI.setExpiredBy(null);
                    wSAI.setAmount(Double.parseDouble(PayrollHRUtils.removeCommas(pEHB.getAmountStr())));
                    wSAI.setDescription(wSAT.getName());
                    wSAI.setName(wSAT.getName());
                    wSAI.setLastModBy(new User(bc.getLoginId()));
                    wSAI.setLastModTs(Timestamp.from(Instant.now()));
                    wSAI.setReferenceNumber(wRefNum);
                    wSAI.setReferenceDate(wCurrentDate);
                    wSAI.setBusinessClientId(bc.getBusinessClientInstId());
                    wSAIList.add(wSAI);

                } else {
                    p.setAmountStr("0.00");
                }
                AbstractPromotionAuditEntity wPA = createPromotionAudit(p, bc);
                wPA.setOldSalaryInfo(p.getEmployee().getSalaryInfo());
                wPA.setSalaryInfo(wSalaryInfo);
                wPAList.add(wPA);
                // PromotionLog wPL = createPromotionLog(p, bc, wRefNum, wSalaryInfo);
                // wPLList.add(wPL);
                PromotionTracker wPT = createPromotionTracker(p, bc, wSalaryInfo);
                wPTList.add(wPT);
                wSList.add(this.createSqlForHireInfoUpdate(p, wPT));

                if (wIntegerList.size() == 50) {
                    this.massEntryService.promoteMassEmployee(pEHB.getSalaryStructureId(), wIntegerList, wSAIList);
                    wIntegerList = new ArrayList<>();
                    wSAIList = new ArrayList<>();
                    this.genericService.storeObjectBatch(wPAList);
                    wPAList = new ArrayList<>();

                    this.genericService.storeObjectBatch(wPTList);
                    wPTList = new ArrayList<>();
                    this.massEntryService.updateHiringInfoPromotionDates(wSList);
                    wSList = new ArrayList<>();

                }
            }
            if (!wIntegerList.isEmpty()) {
                this.massEntryService.promoteMassEmployee(pEHB.getSalaryStructureId(), wIntegerList, wSAIList);

                this.genericService.storeObjectBatch(wPAList);

                this.genericService.storeObjectBatch(wPTList);

                this.massEntryService.updateHiringInfoPromotionDates(wSList);

            }
            pEHB.setSuccessList(wPromoHist);
            addSessionAttribute(request, MASS_PROMO_ATTR_NAME, pEHB);
            LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            if (_wCal != null) {
                RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService, bc.getBusinessClientInstId(), _wCal);
                wRPB.setNoOfPromotions(wRPB.getNoOfPromotions() + wAddendum);
                if (Boolean.valueOf(pEHB.getPayArrearsInd()).booleanValue())
                    wRPB.setNoOfSpecialAllowances(wRPB.getNoOfSpecialAllowances() + wAddendum);
                if (wRPB.isNewEntity()) {
                    wRPB.setRunMonth(_wCal.getMonthValue());
                    wRPB.setRunYear(_wCal.getYear());
                    wRPB.setRerunInd(IConstants.ON);
                    wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                }
                this.genericService.saveObject(wRPB);
            }
        }

        return "redirect:displayMassEntryResult.do?lid=" + bc.getLoginId() + "&tn=" + MASS_PROMO_ATTR_NAME;
    }

    private void createStepIncrementApproval(PromotionHistory p, BusinessCertificate bc, Long salaryStructureId) throws InstantiationException, IllegalAccessException {
        StepIncrementApproval stepIncrementApproval = new StepIncrementApproval();
        stepIncrementApproval.setBusinessClientId(bc.getBusinessClientInstId());
        stepIncrementApproval.setParentId(p.getId());
        stepIncrementApproval.setEmployeeId(p.getEmployee().getEmployeeId());
        stepIncrementApproval.setEntityId(p.getEmployee().getId());
        stepIncrementApproval.setEntityName(p.getEmployee().getDisplayName());
        stepIncrementApproval.setLastModTs(LocalDate.now());
        stepIncrementApproval.setInitiatedDate(LocalDate.now());
        stepIncrementApproval.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(bc.getLoginId()));
        stepIncrementApproval.setInitiator(new User(bc.getLoginId()));
        stepIncrementApproval.setStepIncrementTracker(p.getStepIncrementTracker());
        stepIncrementApproval.setSalaryInfo(new SalaryInfo(salaryStructureId));
        stepIncrementApproval.setOldSalaryInfo(new SalaryInfo(p.getSalaryInfo().getId()));
        genericService.storeObject(stepIncrementApproval);
        NotificationService.storeNotification(bc,genericService,stepIncrementApproval,"requestNotification.do?arid=" + stepIncrementApproval.getId() + "&s=1&oc="+IConstants.STEP_INC_INIT_URL_IND,"Step Increment Request",STEP_INC_APPROVAL_CODE);

    }

    private Model determineSalaryInformation(List<PromotionHistory> pPromoHist,
                                             Employee pEmp, PaginatedPaycheckGarnDedBeanHolder pEHB, HttpServletRequest request, Model pModel) throws IllegalAccessException, InstantiationException {

        BusinessCertificate bc = super.getBusinessCertificate(request);
        List<SalaryInfo> salaryInfoList = null;
        if (pEmp == null) {
            pModel.addAttribute("salaryTypeList", Arrays.asList(this.genericService.loadObjectUsingRestriction(SalaryType.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("id", pEHB.getSalaryTypeId())))));

            salaryInfoList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("salaryType.id", pEHB.getSalaryTypeId())), null);


        } else {
            if (pPromoHist.isEmpty()) {
                pModel.addAttribute("salaryTypeList", Arrays.asList(this.genericService.loadObjectUsingRestriction(SalaryType.class,
                        Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                                CustomPredicate.procurePredicate("id", pEmp.getSalaryInfo().getSalaryType().getId())))));
                salaryInfoList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class,
                        Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                                CustomPredicate.procurePredicate("salaryType.id", pEmp.getSalaryInfo().getSalaryType().getId())), null);


            } else {
                pModel.addAttribute("salaryTypeList", Arrays.asList(this.genericService.loadObjectUsingRestriction(SalaryType.class,
                        Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                                CustomPredicate.procurePredicate("id", pEHB.getSalaryTypeId())))));

                salaryInfoList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class,
                        Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                                CustomPredicate.procurePredicate("salaryType.id", pEHB.getSalaryTypeId())), null);
            }
        }
        Collections.sort(salaryInfoList, Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
        pModel.addAttribute("salaryStructureList", salaryInfoList);
        return pModel;
    }

    private AbstractPromotionAuditEntity createPromotionAudit(PromotionHistory pEHB, BusinessCertificate pBc) {
        AbstractPromotionAuditEntity wPA = IppmsUtils.makePromotionAuditObject(pBc);
        wPA.setEmployee(new Employee(pEHB.getEmployee().getId()));
        wPA.setLastModTs(LocalDate.now());
        wPA.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
        wPA.setMdaInfo(pEHB.getEmployee().getMdaDeptMap().getMdaInfo());
        wPA.setUser(new User(pBc.getLoginId()));
        wPA.setAuditPayPeriod(this.fPayPeriod);
        wPA.setOldSalaryInfo(pEHB.getEmployee().getSalaryInfo());
        wPA.setPromotionDate(LocalDate.now());
        if (pEHB.getEmployee().isSchoolStaff())
            wPA.setSchoolInfo(pEHB.getEmployee().getSchoolInfo());
        wPA.setBusinessClientId(pBc.getBusinessClientInstId());
        return wPA;
    }


    private PromotionTracker createPromotionTracker(PromotionHistory pEHB, BusinessCertificate pBc, SalaryInfo pSalInfo) throws InstantiationException, IllegalAccessException {
        PromotionTracker wPT = this.genericService.loadObjectUsingRestriction(PromotionTracker.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", pBc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("employee.id", pEHB.getEmployee().getId())));

        wPT.setEmployee(new Employee(pEHB.getEmployee().getId()));
        wPT.setUser(new User(pBc.getLoginId(), pBc.getUserName()));
        wPT.setBusinessClientId(pBc.getBusinessClientInstId());
        wPT.setLastPromotionDate(LocalDate.now());
        wPT.setNextPromotionDate(PayrollHRUtils.determineNextPromotionDate(LocalDate.now(), pSalInfo.getLevel()));

        return wPT;

    }

    private NamedEntity createSqlForHireInfoUpdate(PromotionHistory pEHB, PromotionTracker pPT) {
        NamedEntity wRetVal = new NamedEntity();
        wRetVal.setName("update HiringInfo set lastModBy.id = :pLMB, lastModTs = :pLMTS, lastPromotionDate = :pLPD, nextPromotionDate = :pNPD where employee.id = :pId");
        wRetVal.setId(pEHB.getId());
        wRetVal.setAllowanceStartDate(pPT.getLastPromotionDate());
        wRetVal.setAllowanceEndDate(pPT.getNextPromotionDate());
        wRetVal.setParentInstId(pPT.getUser().getId());
        wRetVal.setCreationDate(pPT.getLastPromotionDate());

        return wRetVal;

    }
}