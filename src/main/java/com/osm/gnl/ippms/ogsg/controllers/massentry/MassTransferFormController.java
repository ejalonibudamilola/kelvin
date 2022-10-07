package com.osm.gnl.ippms.ogsg.controllers.massentry;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.MassEntryService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.TransferApproval;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.massentry.PromotionHistory;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequestMapping({"/massTransfer.do"})
@SessionAttributes(types = {PaginatedPaycheckGarnDedBeanHolder.class})
public class MassTransferFormController extends BaseController {


    private final PaycheckService paycheckService;
    private final MassEntryService massEntryService;

    private final int pageLength = 10;
    private final String VIEW = "massentry/massEntryTransferForm";

    @Autowired
    public MassTransferFormController(PaycheckService paycheckService, MassEntryService massEntryService) {
        this.paycheckService = paycheckService;
        this.massEntryService = massEntryService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);

        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_TRANS_ATTR_NAME);

        if ((wPGBDH != null) && (!wPGBDH.isNewEntity())) {
            removeSessionAttribute(request, MASS_TRANS_ATTR_NAME);
        }

        List<PromotionHistory> wPromoHist = new ArrayList<>();
        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPromoHist, paginationBean.getPageNumber(), this.pageLength, 0, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setPaginationListHolder(wPromoHist);
        wPGBDH.setShowArrearsRow(HIDE_ROW);
        wPGBDH.setActiveInd(ON);
        addSessionAttribute(request, MASS_TRANS_ATTR_NAME, wPGBDH);
        addRoleBeanToModel(model, request);
        model.addAttribute("massTransferBean", wPGBDH);

        return VIEW;
    }

    
    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);
        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_TRANS_ATTR_NAME);
        HashMap<Long, TransferApproval> wMap = wPGBDH.getTransferApprovalMap();
        HashMap<Long, TransferLog> wTLog = wPGBDH.getTransferLogMap();

        //Also remove from the PaginatedList...
        List<PromotionHistory> wAllList = new ArrayList<>();

        AbstractEmployeeEntity wEmp = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(
                getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pEmpId)));

        List<MdaInfo> wList = wPGBDH.getMdaList();

        if (wList == null || wList.isEmpty()) {
            //Should never happen really...
            wList = this.loadMdas(request);

        } else {
            wList.add(wEmp.getMdaDeptMap().getMdaInfo());
        }
        wPGBDH.getOldMdaMap().remove(wEmp.getId());
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
        //Now Paginate.
        List<PromotionHistory> wNewList = (List<PromotionHistory>) PayrollUtils.paginateList(pageNumber, this.pageLength, wAllList);

        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, pageNumber, this.pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        wPGBDH.setMdaList(wList);
        wPGBDH.setEmptyList(wNewList.size() > 0);
        wPGBDH.setTransferLogMap(wTLog);
        wPGBDH.setTransferApprovalMap(wMap);
        wPGBDH.setId(bc.getLoginId());
        wPGBDH.setPaginationListHolder(wAllList);
        wPGBDH.setShowArrearsRow(HIDE_ROW);
        wPGBDH.setActiveInd(ON);
        addSessionAttribute(request, MASS_TRANS_ATTR_NAME, wPGBDH);
        addRoleBeanToModel(model, request);
        model.addAttribute("mdaList", wList);
        model.addAttribute("massTransferBean", wPGBDH);

        return VIEW;
    }

    
    @RequestMapping(method = {RequestMethod.GET}, params = {"lid", "act"})
    public String setupForm(@RequestParam("lid") Long pLoginId, @RequestParam("act") String pSaved, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        PaginationBean paginationBean = getPaginationInfo(request);

        PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_TRANS_ATTR_NAME);

        List<MdaInfo> wList = wPGBDH.getMdaList();

        if (wList == null || wList.isEmpty())
            wList = this.loadMdas(request);

        List<PromotionHistory> wAllList = (List<PromotionHistory>) wPGBDH.getPaginationListHolder();
        HashMap<Long, TransferApproval> wMap = wPGBDH.getTransferApprovalMap();
        HashMap<Long, TransferLog> wTLog = wPGBDH.getTransferLogMap();
        HashMap<Long,String> oldMdaMap = wPGBDH.getOldMdaMap();
        if (wAllList == null)
            wAllList = new ArrayList<>();

        //Now Paginate.
        List<PromotionHistory> wNewList = (List<PromotionHistory>) PayrollUtils.paginateList(paginationBean.getPageNumber(), this.pageLength, wAllList);

        wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, paginationBean.getPageNumber(), this.pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        wPGBDH.setOldMdaMap(oldMdaMap);
        wPGBDH.setMdaList(wList);
        wPGBDH.setPaginationListHolder(wAllList);
        wPGBDH.setTransferLogMap(wTLog);
        wPGBDH.setTransferApprovalMap(wMap);
        wPGBDH.setEmptyList(wNewList.size() > 0);
        wPGBDH.setId(pLoginId);
        wPGBDH.setActiveInd(ON);
        addSessionAttribute(request, MASS_TRANS_ATTR_NAME, wPGBDH);

        model.addAttribute("mdaList", wList);
        addRoleBeanToModel(model, request);
        model.addAttribute("massTransferBean", wPGBDH);

        return VIEW;
    }

    
    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @RequestParam(value = "_search", required = false) String search,
                                @RequestParam(value = "_transfer", required = false) String transfer,
                                @ModelAttribute("massTransferBean") PaginatedPaycheckGarnDedBeanHolder pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            removeSessionAttribute(request, MASS_TRANS_ATTR_NAME);
            return "redirect:massEntryMainDashboard.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_SEARCH)) {

            AbstractEmployeeEntity wEmp = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(
                   getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("employeeId", pEHB.getStaffId())));

            if (wEmp.isNewEntity()) {
                result.rejectValue("", "Global.Change", "No "+bc.getStaffTypeName()+" Found with "+bc.getStaffTitle()+" '" + pEHB.getStaffId() + "'");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massTransferBean", pEHB);
                return VIEW;
            }
            for (Object e : pEHB.getPaginationListHolder()) {
                if (((PromotionHistory) e).getId().equals(wEmp.getId())) {
                    result.rejectValue("", "Global.Change", bc.getStaffTypeName()+" '" + wEmp.getDisplayName() + "' is already added");
                    addDisplayErrorsToModel(model, request);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("status", result);
                    model.addAttribute("massTransferBean", pEHB);
                    return VIEW;
                }
            }

            List<MdaInfo> wList = pEHB.getMdaList();
            List<MdaInfo> wNewList = new ArrayList<>();
            HashMap<Long,String> wOldMdaMap = pEHB.getOldMdaMap();
            if(wOldMdaMap == null)
                wOldMdaMap = new HashMap<>();

            wOldMdaMap.put(wEmp.getId(),wEmp.getCurrentMdaName());
            pEHB.setOldMdaMap(wOldMdaMap);

            if (wList == null || wList.isEmpty()) {
                wList = this.loadMdas(request);
            }
            for (MdaInfo m : wList) {
                if (m.getId().equals(wEmp.getMdaDeptMap().getMdaInfo().getId()))
                    continue;
                wNewList.add(m);
            }
            Comparator<MdaInfo> c = Comparator.comparing(MdaInfo::getName);
            Collections.sort(wNewList, c);
            pEHB.setMdaList(wNewList);
            List<PromotionHistory> wPromoHist = (List<PromotionHistory>) pEHB.getPaginationListHolder();

            if (wPromoHist == null) {
                wPromoHist = new ArrayList<>();
            }

            PromotionHistory wPromHistBean = new PromotionHistory();
            wPromHistBean.setEntryIndex(wPromoHist.size() + 1);
            wPromHistBean.setEmployee(wEmp);
            wPromHistBean.setOldSalaryLevelAndStep(wEmp.getSalaryInfo().getSalaryScaleLevelAndStepStr());
            wPromHistBean.setId(wEmp.getId());
            wPromoHist.add(wPromHistBean);

            TransferLog wTL = new TransferLog();
            if(bc.isPensioner())
                wTL.setPensioner(new Pensioner(wEmp.getId()));
            else
                wTL.setEmployee(new Employee(wEmp.getId()));

            wTL.setSalaryInfo(wEmp.getSalaryInfo());


            if (wEmp.isSchoolEnabled()) {
                if (wEmp.getSchoolInfo() != null && !wEmp.getSchoolInfo().isNewEntity()) {
                    wTL.setOldMda(wEmp.getSchoolInfo().getName());
                } else {
                    wTL.setOldMda(wEmp.getAssignedToObject());
                }
            } else {
                wTL.setOldMda(wEmp.getAssignedToObject());
            }
            wTL.setName(wEmp.getDisplayName());
            wTL.setTransferDate(LocalDate.now());
            wTL.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime(false));
            wTL.setUser(new User(bc.getLoginId()));

            pEHB.setPaginationListHolder(wPromoHist);
            pEHB.setSuccessList(wPromoHist);
            pEHB.getTransferLogMap().put(wEmp.getId(), wTL);
      
            if (!bc.isSuperAdmin()) {

                TransferApproval wTA = new TransferApproval();
                if(bc.isPensioner())
                    wTA.setPensioner(new Pensioner(wEmp.getId()));
                else
                   wTA.setEmployee(new Employee(wEmp.getId()));

                wTA.setInitiatedDate(LocalDate.now());
                wTA.setInitiator(new User(bc.getLoginId()));
                wTA.setInitiatedDate(LocalDate.now());

                wTA.setLastModTs(LocalDate.now());

                wTA.setAuditTime(PayrollBeanUtils.getCurrentTime());
                wTA.setBusinessClientId(bc.getBusinessClientInstId());

                wTA.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(bc.getLoginId()));
                HashMap<Long, TransferApproval> wMap = pEHB.getTransferApprovalMap();
                if (wMap == null)
                    wMap = new HashMap<>();
                wMap.put(wEmp.getId(), wTA);
                pEHB.setTransferApprovalMap(wMap);
            }
            addSessionAttribute(request, MASS_TRANS_ATTR_NAME, pEHB);

            return "redirect:massTransfer.do?lid=" + bc.getLoginId() + "&act=y";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_TRANSFER)) {
            if (IppmsUtils.isNullOrLessThanOne(pEHB.getMdaId())) {
                result.rejectValue("", "Global.Change", "Please select a value for New "+bc.getMdaTitle());
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("massTransferBean", pEHB);
                return VIEW;
            }
            if (IppmsUtils.isNullOrLessThanOne(pEHB.getDepartmentId())) {

                result.rejectValue("", "Global.Change", "Please select the Department to transfer "+bc.getStaffTypeName()+" to");
                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                model.addAttribute("departmentList", this.genericService.loadObjectWithSingleCondition(Department.class,
                        CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));

                MdaInfo wMdaInfo = this.genericService.loadObjectUsingRestriction(MdaInfo.class, Arrays.asList(
                        CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id", pEHB.getMdaId())));

                if (wMdaInfo.isSchoolAttached()) {
                    //Try and Load the Schools....
                    model.addAttribute("schoolList", this.genericService.loadObjectUsingRestriction(SchoolInfo.class, Arrays.asList(
                            CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id", wMdaInfo.getId()))));
                    //-- Show the School Row and
                    pEHB.setShowArrearsRow(SHOW_ROW);
                } else {
                    pEHB.setShowArrearsRow(HIDE_ROW);
                }
                model.addAttribute("status", result);
                addRoleBeanToModel(model, request);
                model.addAttribute("massTransferBean", pEHB);
                return VIEW;
            }

        }
        MdaInfo wMdaInfo = this.genericService.loadObjectUsingRestriction(MdaInfo.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id", pEHB.getMdaId())));
        pEHB.setName(wMdaInfo.getName());

        addSessionAttribute(request, MASS_TRANS_ATTR_NAME, pEHB);
        if ((pEHB == null) || (pEHB.getList().isEmpty())) {
            result.rejectValue("", "Global.Change", "Please add "+bc.getStaffTypeName()+" to Transfer.");
            addDisplayErrorsToModel(model, request);
            addRoleBeanToModel(model, request);
            model.addAttribute("status", result);
            model.addAttribute("massTransferBean", pEHB);
            return VIEW;
        }

        List<PromotionHistory> wPromoHist = (List<PromotionHistory>) pEHB.getPaginationListHolder();
        List<Long> wIntegerList = new ArrayList<>();
        List<TransferApproval> wTAList = new ArrayList<>();
        List<TransferLog> wTLList = new ArrayList<>();
        int wAddendum = 0;
        TransferApproval wTA;
        TransferLog wTL;
        MdaDeptMap mdaDeptMap = this.genericService.loadObjectUsingRestriction(MdaDeptMap.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("mdaInfo.id", wMdaInfo.getId()),
                CustomPredicate.procurePredicate("department.id", pEHB.getDepartmentId())));
        SchoolInfo wSchool = this.genericService.loadObjectUsingRestriction(SchoolInfo.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id", pEHB.getSchoolId())));

        boolean wNeedsApproval = !bc.isSuperAdmin();
        String wPayPeriod = PayrollUtils.makePayPeriodForAuditLogs(genericService, bc);
        for (PromotionHistory p : wPromoHist) {
            wAddendum++;
            p.setTransferTo(wMdaInfo.getName());
            if (pEHB.getSchoolTransfer() == IConstants.OFF) {
                p.setTransferFrom(p.getEmployee().getSchoolName());
            } else {
                p.setTransferFrom(p.getEmployee().getParentObjectName());
            }

            if (wNeedsApproval) {
                wTA = pEHB.getTransferApprovalMap().get(p.getId());
                wTA.setMdaDeptMap(mdaDeptMap);
                if (pEHB.getSchoolId() == -1) {
                    wTA.setSchoolInfo(null);
                } else {
                    wTA.setSchoolInfo(new SchoolInfo(pEHB.getSchoolId()));
                }
                wTA.setObjectInd(mdaDeptMap.getMdaInfo().getMdaType().getMdaTypeCode());
                wTA.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
                wTA.setBusinessClientId(bc.getBusinessClientInstId());
                wTA.setEntityId(p.getEmployee().getId());
                wTA.setEmployeeId(p.getEmployee().getEmployeeId());
                wTA.setEntityName(p.getEmployee().getDisplayName());
                this.genericService.saveObject(wTA);
                NotificationService.storeNotification(bc,genericService,wTA,"requestNotification.do?arid="+wTA.getId()+"&s=1&oc="+IConstants.TRANSFER_REQUEST_URL_IND,"Transfer Request", IConstants.TRANSFER_REQUEST_URL_IND);

            } else {
                wIntegerList.add(p.getEmployee().getId());
                wTL = pEHB.getTransferLogMap().get(p.getId());
                wTL.setMdaInfo(wMdaInfo);
                if (pEHB.getSchoolId() == -1) {
                    wTL.setSchoolInfo(null);
                } else {
                    wTL.setSchoolInfo(new SchoolInfo(pEHB.getSchoolId()));
                }
                wTL.setObjectInd(wTL.getMdaInfo().getMdaType().getMdaTypeCode());
                wTL = pEHB.getTransferLogMap().get(p.getId());
                wTL.setNewMda(wMdaInfo.getName());
                wTL.setAuditPayPeriod(wPayPeriod);
                wTL.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
                wTL.setBusinessClientId(bc.getBusinessClientInstId());
                wTLList.add(wTL);
                if (wIntegerList.size() == 50) {
                    this.massEntryService.transferMassEmployee(wIntegerList, mdaDeptMap.getId(), wSchool.getId(), bc.getLoginId());
                    this.genericService.storeObjectBatch(wTLList);
                    wIntegerList = new ArrayList<>();
                    wTLList = new ArrayList<>();
                }
            }


        }
        if (!wIntegerList.isEmpty()) {
            this.massEntryService.transferMassEmployee(wIntegerList, mdaDeptMap.getId(), wSchool.getId(), bc.getLoginId());
            this.genericService.storeObjectBatch(wTLList);

        }
        pEHB.setSuccessList(wPromoHist);
        pEHB.setTypeInd(4);
        addSessionAttribute(request, MASS_TRANS_ATTR_NAME, pEHB);
        LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
        if (_wCal != null) {
            RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService, bc.getBusinessClientInstId(), _wCal);
            wRPB.setNoOfTransfers(wRPB.getNoOfTransfers() + wAddendum);
            if (wRPB.isNewEntity()) {
                wRPB.setRunMonth(_wCal.getMonthValue());
                wRPB.setRunYear(_wCal.getYear());
                wRPB.setRerunInd(IConstants.ON);
                wRPB.setBusinessClientId(bc.getBusinessClientInstId());
            }
            wRPB.setNoOfTransfers(wRPB.getNoOfTransfers() + 1);
            this.genericService.saveObject(wRPB);
        }


        return "redirect:displayMassEntryResult.do?lid=" + bc.getLoginId() + "&tn=" + MASS_TRANS_ATTR_NAME;
    }

    private List<MdaInfo> loadMdas(HttpServletRequest request) throws IllegalAccessException {
        BusinessCertificate bc = super.getBusinessCertificate(request);
        return this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), "name");
    }
}