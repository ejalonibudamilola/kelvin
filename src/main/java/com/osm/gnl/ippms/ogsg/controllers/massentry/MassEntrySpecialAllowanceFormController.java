package com.osm.gnl.ippms.ogsg.controllers.massentry;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.MassEntryService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.massentry.PromotionHistory;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.validators.allowance.SpecialAllowanceInfoValidator;
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
@RequestMapping({"/massSpecialAllowance.do"})
@SessionAttributes(types = {PaginatedPaycheckGarnDedBeanHolder.class})
public class MassEntrySpecialAllowanceFormController extends BaseController {

    private final int pageLength = 10;

    private final String VIEW = "massentry/massEntrySpecAllowForm";


    private final SpecialAllowanceInfoValidator validator;
    private final PaycheckService paycheckService;
    private final MassEntryService massEntryService;

    @Autowired
    public MassEntrySpecialAllowanceFormController(SpecialAllowanceInfoValidator validator, PaycheckService paycheckService, MassEntryService massEntryService) {
        this.validator = validator;
        this.paycheckService = paycheckService;
        this.massEntryService = massEntryService;
    }

    
    @ModelAttribute("payType")
    public List<PayTypes> getPayType() throws InstantiationException, IllegalAccessException {

        List<PayTypes> wPTL = this.genericService.loadAllObjectsWithSingleCondition(PayTypes.class,
                CustomPredicate.procurePredicate("selectableInd", IConstants.OFF), null);
        Comparator<PayTypes> wComp = Comparator.comparing(PayTypes::getName);

        Collections.sort(wPTL, wComp);

        return wPTL;

    }

    
    @ModelAttribute("specAllowList")
    public List<SpecialAllowanceType> getSpecAllowList(HttpServletRequest request) {
        BusinessCertificate bc = super.getBusinessCertificate(request);
        List<SpecialAllowanceType> wLoanTypeList = this.genericService.loadAllObjectsWithSingleCondition(SpecialAllowanceType.class,
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), "description");
        return wLoanTypeList;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);
        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_SA_ATTR_NAME);

        if ((wPGBDH != null) && (!wPGBDH.isNewEntity())) {
            removeSessionAttribute(request, MASS_SA_ATTR_NAME);
        }

        List<PromotionHistory> wPromoHist = new ArrayList<>();
        List<Long> wEmpIdList = new ArrayList<Long>();
        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPromoHist, paginationBean.getPageNumber(), this.pageLength, 0, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setEmployeeIdList(wEmpIdList);
        wPGBDH.setPaginationListHolder(wPromoHist);
        wPGBDH.setActiveInd(ON);

        addSessionAttribute(request, MASS_SA_ATTR_NAME, wPGBDH);
        model.addAttribute("massSpecAllowBean", wPGBDH);
        addRoleBeanToModel(model, request);
        return VIEW;
    }

    
    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);
        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_SA_ATTR_NAME);


        List<AbstractSpecialAllowanceEntity> wNewList;
        List<Long> wEmpIdList = new ArrayList<Long>();

        List<AbstractSpecialAllowanceEntity> wAllList = new ArrayList<>();

        for (AbstractSpecialAllowanceEntity p : (List<AbstractSpecialAllowanceEntity>) wPGBDH.getPaginationListHolder()) {
            if (p.getParentId().equals(pEmpId)) {
                continue;
            }
            wAllList.add(p);
            wEmpIdList.add(p.getParentId());
        }

        int pageNumber = paginationBean.getPageNumber();
        if (wAllList.size() > this.pageLength) {
            Double wDouble = new Double(wAllList.size()) / new Double(this.pageLength);
            pageNumber = Integer.parseInt(String.valueOf(wDouble).substring(0, String.valueOf(wDouble).indexOf(".")));
        }
        //Now Paginate.
        wNewList = (List<AbstractSpecialAllowanceEntity>) PayrollUtils.paginateList(pageNumber, this.pageLength, wAllList);


        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, pageNumber, this.pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        wPGBDH.setBeanList(wNewList);
        wPGBDH.setEmployeeIdList(wEmpIdList);
        wPGBDH.setEmptyList(wNewList.size() > 0);
        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setPaginationListHolder(wAllList);
        wPGBDH.setActiveInd(ON);
        addSessionAttribute(request, MASS_SA_ATTR_NAME, wPGBDH);

        model.addAttribute("massSpecAllowBean", wPGBDH);
        addRoleBeanToModel(model, request);
        return VIEW;
    }

    
    @RequestMapping(method = {RequestMethod.GET}, params = {"lid", "act"})
    public String setupForm(@RequestParam("lid") Long pLoginId, @RequestParam("act") String pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_SA_ATTR_NAME);

        List<AbstractSpecialAllowanceEntity> wAllList = (List<AbstractSpecialAllowanceEntity>) wPGBDH.getPaginationListHolder();
        if (wAllList == null)
            wAllList = new ArrayList<>();

        PaginationBean paginationBean = getPaginationInfo(request);
        List<AbstractSpecialAllowanceEntity> wNewList = (List<AbstractSpecialAllowanceEntity>) PayrollUtils.paginateList(paginationBean.getPageNumber(), this.pageLength, wAllList);
        List<Long> wEmpIdList = wPGBDH.getEmployeeIdList();

        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, paginationBean.getPageNumber(), this.pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());


        wPGBDH.setEmptyList(wAllList.size() > 0);
        wPGBDH.setId(pLoginId);
        wPGBDH.setEmployeeIdList(wEmpIdList);
        wPGBDH.setPaginationListHolder(wAllList);
        wPGBDH.setActiveInd(ON);
        addSessionAttribute(request, MASS_SA_ATTR_NAME, wPGBDH);

        addRoleBeanToModel(model, request);
        model.addAttribute("massSpecAllowBean", wPGBDH);
        model.addAttribute("beanList", wPGBDH.getBeanList());
        return VIEW;
    }

    
    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_search", required = false) String search, @RequestParam(value = "_addSpecAllow", required = false) String addSpecAllow, @ModelAttribute("massSpecAllowBean") PaginatedPaycheckGarnDedBeanHolder pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            removeSessionAttribute(request, MASS_SA_ATTR_NAME);
            return "redirect:massEntryMainDashboard.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_SEARCH)) {
            String staffId = pEHB.getStaffId().toUpperCase();

            AbstractEmployeeEntity wEmp = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc),
                    Arrays.asList(getBusinessClientIdPredicate(request),
                            CustomPredicate.procurePredicate("employeeId", staffId)));

            if (wEmp.isNewEntity()) {
                result.rejectValue("", "Global.Change", "No "+bc.getStaffTypeName()+" Found with "+bc.getStaffTitle()+" '" + staffId + "'");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massSpecAllowBean", pEHB);
                return VIEW;
            }else{
                HiringInfo hiringInfo = loadHiringInfoByEmpId(request,bc,wEmp.getId());
                if(!hiringInfo.isNewEntity()){
                    boolean wError = false;
                    if(hiringInfo.isSuspendedEmployee()){
                        wError = true;
                        result.rejectValue("", "Global.Change", bc.getStaffTypeName()+" "+wEmp.getDisplayNameWivTitlePrefixed()+" is Suspended.");

                    }else if(hiringInfo.isTerminatedEmployee()){
                        wError = true;
                        result.rejectValue("", "Global.Change", bc.getStaffTypeName()+" "+wEmp.getDisplayNameWivTitlePrefixed()+" is Terminated.");

                    }
                    if(wError){
                        addDisplayErrorsToModel(model, request);
                        addRoleBeanToModel(model, request);
                        model.addAttribute("status", result);
                        model.addAttribute("massDeductBean", pEHB);
                        return VIEW;
                    }
                }
            }

            for (Object e : pEHB.getPaginationListHolder()) {
                if (((AbstractSpecialAllowanceEntity) e).getParentId().equals(wEmp.getId())) {
                    result.rejectValue("", "Global.Change", bc.getStaffTypeName()+" '" + wEmp.getDisplayName() + "' is already added");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massSpecAllowBean", pEHB);
                    return VIEW;
                }
            }
            pEHB = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_SA_ATTR_NAME);

            List<AbstractSpecialAllowanceEntity> wPromoHist = (List<AbstractSpecialAllowanceEntity>) pEHB.getPaginationListHolder();

            List<Long> wEmpIds = pEHB.getEmployeeIdList();

            if ((wPromoHist == null) || (wPromoHist.isEmpty())) {
                wPromoHist = new ArrayList<>();
            }

            if (wEmpIds == null) {
                wEmpIds = new ArrayList<>();
            }


            AbstractSpecialAllowanceEntity wPromHistBean = IppmsUtils.makeSpecialAllowanceInfoObject(bc);
            wPromHistBean.setEntryIndex(Integer.valueOf(wPromoHist.size() + 1));
            wPromHistBean.setParentObject(wEmp);
            wPromHistBean.setId(wEmp.getId());
            wPromHistBean.setOldSalaryLevelAndStep(wEmp.getSalaryInfo().getSalaryScaleLevelAndStepStr());
            wPromoHist.add(wPromHistBean);
            wEmpIds.add(wEmp.getId());

            pEHB.setPaginationListHolder(wPromoHist);

            pEHB.setEmployeeIdList(wEmpIds);
            addSessionAttribute(request, MASS_SA_ATTR_NAME, pEHB);

            return "redirect:massSpecialAllowance.do?lid=" + bc.getLoginId() + "&act=y";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_ADD_SPEC_ALLOW)) {
            if (IppmsUtils.isNullOrLessThanOne(pEHB.getSalaryTypeId())) {
                result.rejectValue("", "Global.Change", "Please select Special Allowance Type");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massSpecAllowBean", pEHB);
                return VIEW;
            }

            double wOA = 0.0D;
            try {
                wOA = Double.parseDouble(PayrollHRUtils.removeCommas(pEHB.getOwedAmountStr()));

                if (wOA < 0.0D) {
                    result.rejectValue("", "Global.Change", "Please Enter a value for Allowance Amount");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massSpecAllowBean", pEHB);
                    return VIEW;
                }
                pEHB.setOriginalLoanAmount(wOA);
            } catch (NumberFormatException wNFE) {
                result.rejectValue("", "Global.Change", "Please Enter a value for Allowance Amount");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massSpecAllowBean", pEHB);
                return VIEW;
            }

            PaginatedPaycheckGarnDedBeanHolder wPEHB = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_SA_ATTR_NAME);
            if ((wPEHB == null) || (wPEHB.getPaginationListHolder().isEmpty())) {
                result.rejectValue("", "Global.Change", "Please add "+bc.getStaffTypeName()+" to assign Special Allowance.");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massSpecAllowBean", pEHB);
                return VIEW;
            }

            validator.validateForMassEntry(wPEHB, result, this.genericService);
            if (result.hasErrors()) {
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massSpecAllowBean", pEHB);
                return VIEW;
            }

            SpecialAllowanceType wSpecAllowType = this.genericService.loadObjectUsingRestriction(SpecialAllowanceType.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("id", wPEHB.getSalaryTypeId())));
            wPEHB.setName(wSpecAllowType.getDescription());
            wPEHB.setTypeInd(5);
            addSessionAttribute(request, MASS_SA_ATTR_NAME, wPEHB);
            HashMap<Long, Long> wUpdateMap = this.massEntryService.makeDedGarnSpecMap(AbstractSpecialAllowanceEntity.class, "specialAllowanceType.id", wSpecAllowType.getId(), pEHB.getEmployeeIdList(), bc);

            List<AbstractSpecialAllowanceEntity> wPromoHist = (List<AbstractSpecialAllowanceEntity>) wPEHB.getPaginationListHolder();
            List<PromotionHistory> wSuccessList = new ArrayList<PromotionHistory>();
            List<AbstractSpecialAllowanceEntity> wIntegerList = new ArrayList<>();
            int wAddendum = 0;
            PayTypes wPt = this.genericService.loadObjectWithSingleCondition(PayTypes.class, CustomPredicate.procurePredicate("id", wPEHB.getPayTypeInstId()));
            PromotionHistory h;
            AbstractSpecialAllowanceEntity wSpecAllowInfo;
            for (AbstractSpecialAllowanceEntity p : wPromoHist) {
                wAddendum++;
                 wSpecAllowInfo = IppmsUtils.makeSpecialAllowanceInfoObject(bc);
                  h = new PromotionHistory();
                h.setEmployee(p.getParentObject());
                wSuccessList.add(h);
                if (wUpdateMap.containsKey(p.getParentId())) {
                    wSpecAllowInfo.setId(wUpdateMap.get(p.getParentId()));
                }else{
                    wSpecAllowInfo.setCreatedBy(new User(bc.getLoginId()));
                }
                wSpecAllowInfo.setSpecialAllowanceType(wSpecAllowType);
                /*if(bc.isPensioner())
                    wSpecAllowInfo.setPensioner(new Pensioner(p.getParentId()));
                else
                    wSpecAllowInfo.setEmployee(new Employee(p.getParentId()));*/
                wSpecAllowInfo.makeParentObject(p.getParentId());

                wSpecAllowInfo.setPayTypes(wPt);

                wSpecAllowInfo.setAmount(wOA);

                wSpecAllowInfo.setStartDate(pEHB.getStartDate());
                if (pEHB.getEndDate() != null) {
                    wSpecAllowInfo.setEndDate(pEHB.getEndDate());
                }
                wSpecAllowInfo.setDescription(wSpecAllowType.getDescription());
                wSpecAllowInfo.setName(wSpecAllowType.getName());
                wSpecAllowInfo.setLastModBy(new User(bc.getLoginId()));
                wSpecAllowInfo.setLastModTs(Timestamp.from(Instant.now()));
                wSpecAllowInfo.setExpire(0);
                wSpecAllowInfo.setReferenceDate(LocalDate.now());
                wSpecAllowInfo.setReferenceNumber(bc.getUserName() + "/" + PayrollHRUtils.getDateFormat().format(wSpecAllowInfo.getReferenceDate()));
                wSpecAllowInfo.setBusinessClientId(bc.getBusinessClientInstId());
                wIntegerList.add(wSpecAllowInfo);
                if (wIntegerList.size() == 50) {
                    this.genericService.storeObjectBatch(wIntegerList);
                    wIntegerList = new ArrayList<>();
                }
            }
            if (!wIntegerList.isEmpty()) {
                this.genericService.storeObjectBatch(wIntegerList);
            }
            pEHB.setSuccessList(wSuccessList);
            addSessionAttribute(request, MASS_SA_ATTR_NAME, pEHB);
            LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            if (_wCal != null) {
                RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService,bc.getBusinessClientInstId(),_wCal);
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

        return "redirect:displayMassEntryResult.do?lid=" + bc.getLoginId() + "&tn=" + MASS_SA_ATTR_NAME;
    }
}