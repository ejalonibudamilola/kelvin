package com.osm.gnl.ippms.ogsg.controllers.massentry;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.MassEntryService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.massentry.PromotionHistory;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping({"/massDeductionEntry.do"})
@SessionAttributes(types = {PaginatedPaycheckGarnDedBeanHolder.class})
public class MassEntryDeductionFormController extends BaseController {

    private final int pageLength = 10;
    private final String VIEW = "massentry/massEntryDeductionForm";

    private final MassEntryService massEntryService;
    private final PaycheckService paycheckService;

    @Autowired
    public MassEntryDeductionFormController(MassEntryService massEntryService, PaycheckService paycheckService) {
        this.massEntryService = massEntryService;
        this.paycheckService = paycheckService;
    }

    
    @ModelAttribute("deductionList")
    public List<EmpDeductionType> getDeductionsList(HttpServletRequest request) {
        BusinessCertificate bc = super.getBusinessCertificate(request);

        List<EmpDeductionType> wLoanTypeList = this.genericService.loadAllObjectsUsingRestrictions(EmpDeductionType.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("empDeductionCategory.statutoryInd", IConstants.ON)), "description");

        return wLoanTypeList;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_DED_ATTR_NAME);

        if ((wPGBDH != null) && (!wPGBDH.isNewEntity())) {
            removeSessionAttribute(request, MASS_DED_ATTR_NAME);
        }

        List<AbstractDeductionEntity> wDeductInfoList = new ArrayList<>();
        List<Long> wEmpIdList = new ArrayList<>();
        PaginationBean paginationBean = getPaginationInfo(request);
        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wDeductInfoList, paginationBean.getPageNumber(), pageLength, 0, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setEmployeeIdList(wEmpIdList);
        wPGBDH.setPaginationListHolder(wDeductInfoList);
        addSessionAttribute(request, MASS_DED_ATTR_NAME, wPGBDH);
        wPGBDH.setDedGarnOrSpecAllow(false);
        wPGBDH.setActiveInd(ON);
        addRoleBeanToModel(model, request);
        model.addAttribute("massDeductBean", wPGBDH);
        return VIEW;

    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        PaginationBean paginationBean = this.getPaginationInfo(request);


        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_DED_ATTR_NAME);


        List<AbstractDeductionEntity> wNewList;
        List<Long> wEmpIdList = new ArrayList<Long>();


        List<AbstractDeductionEntity> wAllList = new ArrayList<>();

        for (AbstractDeductionEntity p : (List<AbstractDeductionEntity>) wPGBDH.getPaginationListHolder()) {
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
        wNewList = (List<AbstractDeductionEntity>) PayrollUtils.paginateList(pageNumber, this.pageLength, wAllList);

        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, pageNumber, pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        wPGBDH.setEmployeeIdList(wEmpIdList);
        wPGBDH.setBeanList(wNewList);
        wPGBDH.setEmptyList(wNewList.size() > 0);
        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setPaginationListHolder(wAllList);
        wPGBDH.setActiveInd(ON);
        addSessionAttribute(request, MASS_DED_ATTR_NAME, wPGBDH);
        addRoleBeanToModel(model, request);
        model.addAttribute("massDeductBean", wPGBDH);
        return VIEW;
    }

     @RequestMapping(method = {RequestMethod.GET}, params = {"lid", "act"})
    public String setupForm(@RequestParam("lid") Long pLoginId, @RequestParam("act") String pSaved, Model model, HttpServletRequest request) throws Exception {


        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_DED_ATTR_NAME);

        List<AbstractDeductionEntity> wAllList = (List<AbstractDeductionEntity>) wPGBDH.getPaginationListHolder();
        if (wAllList == null)
            wAllList = new ArrayList<>();

        PaginationBean paginationBean = getPaginationInfo(request);
        List<AbstractDeductionEntity> wNewList = (List<AbstractDeductionEntity>) PayrollUtils.paginateList(paginationBean.getPageNumber(), this.pageLength, wAllList);
        List<Long> wEmpIdList = wPGBDH.getEmployeeIdList();


        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, paginationBean.getPageNumber(), pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPGBDH.setEmptyList(wNewList.size() > 0);
        wPGBDH.setId(pLoginId);
        wPGBDH.setEmployeeIdList(wEmpIdList);
        wPGBDH.setPaginationListHolder(wAllList);
        wPGBDH.setActiveInd(ON);
        addSessionAttribute(request, MASS_DED_ATTR_NAME, wPGBDH);
        addRoleBeanToModel(model, request);
        model.addAttribute("massDeductBean", wPGBDH);
        return VIEW;

    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_search", required = false) String search, @RequestParam(value = "_addDeduction", required = false) String addDeduction,
                                @ModelAttribute("massDeductBean") PaginatedPaycheckGarnDedBeanHolder pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            removeSessionAttribute(request, MASS_DED_ATTR_NAME);
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
                model.addAttribute("massDeductBean", pEHB);
                return VIEW;
            }else{
                //Check if this Employee is Retired or Suspended.
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
                if (((AbstractDeductionEntity) e).getParentId().equals(wEmp.getId())) {
                    result.rejectValue("", "Global.Change", bc.getStaffTypeName()+" '" + wEmp.getDisplayName() + "' is already added");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massDeductBean", pEHB);
                    return VIEW;
                }
            }

            pEHB = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_DED_ATTR_NAME);

            List<AbstractDeductionEntity> wPromoHist = (List<AbstractDeductionEntity>) pEHB.getPaginationListHolder();


            List<Long> wEmpIds = pEHB.getEmployeeIdList();

            if (wPromoHist == null) {
                wPromoHist = new ArrayList<>();
            }
            if (wEmpIds == null) {
                wEmpIds = new ArrayList<>();
            }

            AbstractDeductionEntity wPromHistBean = IppmsUtils.makeDeductionInfoObject(bc);
            wPromHistBean.setEntryIndex(wPromoHist.size() + 1);
            wPromHistBean.setParentObject(wEmp);
            wPromHistBean.setId(wEmp.getId());
            wPromoHist.add(wPromHistBean);
            wEmpIds.add(wEmp.getId());

            pEHB.setPaginationListHolder(wPromoHist);
            pEHB.setEmployeeIdList(wEmpIds);


            addSessionAttribute(request, MASS_DED_ATTR_NAME, pEHB);

            return "redirect:massDeductionEntry.do?lid=" + bc.getLoginId() + "&act=y";
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_ADD_DEDUCTION)) {
            if (IppmsUtils.isNullOrLessThanOne(pEHB.getSalaryTypeId())) {
                result.rejectValue("", "Global.Change", "Please select Deduction Type");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massDeductBean", pEHB);

                return VIEW;
            }

            double wOA = 0.00;
            try {
                wOA = Double.parseDouble(PayrollHRUtils.removeCommas(pEHB.getOwedAmountStr()));

                if (wOA < 0.00) {
                    result.rejectValue("", "Global.Change", "Please Enter a value for Deduction Amount");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massDeductBean", pEHB);

                    return VIEW;
                }
                pEHB.setOriginalLoanAmount(wOA);
            } catch (NumberFormatException wNFE) {
                result.rejectValue("", "Global.Change", "Please Enter a value for Deduction Amount");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massDeductBean", pEHB);

                return VIEW;
            }

            PaginatedPaycheckGarnDedBeanHolder wPEHB = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_DED_ATTR_NAME);

            if ((wPEHB == null) || (wPEHB.getList().isEmpty())) {
                result.rejectValue("", "Global.Change", "Please add "+bc.getStaffTypeName()+" to assign deduction.");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massDeductBean", pEHB);

                return VIEW;
            }

            EmpDeductionType wDeductType = this.genericService.loadObjectUsingRestriction(EmpDeductionType.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("id", wPEHB.getSalaryTypeId())));
            ConfigurationBean configurationBean = IppmsUtilsExt.loadConfigurationBean(genericService,bc);
            if ((wDeductType.getPayTypes().isUsingPercentage()) &&
                    (pEHB.getOriginalLoanAmount() > configurationBean.getMaxDeductionValue())) {
                result.rejectValue("", "Global.Change", "Deduction Percentage can not be greater than configured value of "+PayrollHRUtils.getDecimalFormat().format(configurationBean.getMaxDeductionValue())+"%");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massEmailPayslipBean", pEHB);

                return VIEW;
            }
            List<PromotionHistory> wSuccessList = new ArrayList<PromotionHistory>();
            wPEHB.setName(wDeductType.getDescription());
            wPEHB.setTypeInd(3);
            addSessionAttribute(request, MASS_DED_ATTR_NAME, wPEHB);
            HashMap<Long, Long> wUpdateMap = this.massEntryService.makeDedGarnSpecMap(IppmsUtils.getDeductionInfoClass(bc), "empDeductionType.id", wDeductType.getId(), pEHB.getEmployeeIdList(), bc);


            List<AbstractDeductionEntity> wPromoHist = (List<AbstractDeductionEntity>) wPEHB.getPaginationListHolder();

            List<AbstractDeductionEntity> wIntegerList = new ArrayList<>();

            int wAddendum = 0;
            for (AbstractDeductionEntity p : wPromoHist) {
                AbstractDeductionEntity wDeductInfo = IppmsUtils.makeDeductionInfoObject(bc);

                PromotionHistory h = new PromotionHistory();
                h.setEmployee(p.getParentObject());
                wSuccessList.add(h);
                wAddendum++;
                if (wUpdateMap.containsKey(p.getParentId())) {
                    wDeductInfo.setId(wUpdateMap.get(p.getParentId()));
                    wDeductInfo.setCreatedBy(new User(bc.getLoginId()));
                }else{
                    wDeductInfo.setCreatedBy(new User(bc.getLoginId()));
                }
                wDeductInfo.setBusinessClientId(bc.getBusinessClientInstId());
                wDeductInfo.setEmpDeductionType(wDeductType);

                wDeductInfo.setPayTypes(wDeductType.getPayTypes());
                wDeductInfo.setEmpDeductionType(new EmpDeductionType(wDeductType.getId()));
                wDeductInfo.setAmount(wOA);
                wDeductInfo.setAnnualMax(0.00);

                wDeductInfo.setLastModBy(new User(bc.getLoginId()));
                wDeductInfo.setLastModTs(Timestamp.from(Instant.now()));

                wDeductInfo.setDescription(pEHB.getName());
                wDeductInfo.setName(wDeductType.getName());
                wDeductInfo.makeParentObject(p.getParentId());

                wIntegerList.add(wDeductInfo);
                if (wIntegerList.size() == 50) {
                    this.genericService.storeObjectBatch(wIntegerList);
                    wIntegerList = new ArrayList<>();
                }
            }
            if (!wIntegerList.isEmpty()) {
                this.genericService.storeObjectBatch(wIntegerList);
            }
            pEHB.setSuccessList(wSuccessList);
            addSessionAttribute(request, MASS_DED_ATTR_NAME, pEHB);
             LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
            if (_wCal != null) {
                RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService,bc.getBusinessClientInstId(),_wCal);
                wRPB.setNoOfDeductions(wRPB.getNoOfDeductions() + wAddendum);
                if (wRPB.isNewEntity()) {
                    wRPB.setRunMonth(_wCal.getMonthValue());
                    wRPB.setRunYear(_wCal.getYear());
                    wRPB.setRerunInd(IConstants.ON);
                    wRPB.setBusinessClientId(bc.getBusinessClientInstId());
                }
                wRPB.setNoOfDeductions(wRPB.getNoOfDeductions() + 1);
                this.genericService.saveObject(wRPB);
            }
        }

        return "redirect:displayMassEntryResult.do?lid=" + bc.getLoginId() + "&tn=" + MASS_DED_ATTR_NAME;
    }
}