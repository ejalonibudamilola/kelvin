package com.osm.gnl.ippms.ogsg.controllers.configcontrol;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.paygroup.*;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping({"/approveSalaryStructure.do"})
@SessionAttributes(types={MasterSalaryTemp.class})
public class ApproveSalaryStructureController extends BaseController {


    private static String VIEW_NAME = "approval/approveSalaryStructureForm";




    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(@RequestParam(value = "mId", required = false) Long mId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);

        List<SalaryTemp> sMainList;

        if(IppmsUtils.isNull(mId)){
            List<MasterSalaryTemp> masterSalaryTemps = this.genericService.loadAllObjectsUsingRestrictions(MasterSalaryTemp.class,
                    Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                            CustomPredicate.procurePredicate("approvalStatusInd", IConstants.OFF)), null);

            if(IppmsUtils.isNullOrEmpty(masterSalaryTemps))
                return REDIRECT_TO_DASHBOARD;

            model.addAttribute("miniBean", masterSalaryTemps);
            model.addAttribute("sBean", masterSalaryTemps.get(0));
            addRoleBeanToModel(model, request);
            return  "approval/approveSalaryStructureMasterForm";
        }
        else {
            PredicateBuilder predicateBuilder = new PredicateBuilder();
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("masterSalaryTemp.businessClientId",
                    bc.getBusinessClientInstId()));
            predicateBuilder.addPredicate(CustomPredicate.procurePredicate("masterSalaryTemp.id", mId));

            //sTemp = (List<SalaryTemp>) this.genericService.loadPaginatedObjectsByPredicates(SalaryTemp.class, predicateBuilder, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
            sMainList = this.genericService.loadAllObjectsUsingRestrictions(SalaryTemp.class, Arrays.asList(CustomPredicate.procurePredicate(
                    "parentId", mId)), "");

            Collections.sort(sMainList, Comparator.comparing(AbstractSalaryTemp::getLevel).thenComparing(AbstractSalaryTemp::getStep));

           // if (IppmsUtils.isNullOrEmpty(sTemp))
            //    return "redirect:approveSalaryStructure.do";

            MasterSalaryTemp masterSalaryTemp = this.genericService.loadObjectById(MasterSalaryTemp.class, mId);

            int wNoOfElements = this.genericService.getTotalPaginatedObjects(SalaryTemp.class, Arrays.asList(CustomPredicate.procurePredicate("parentId", mId))).intValue();

            PaginatedBean wBEOB = new PaginatedBean(sMainList, paginationBean.getPageNumber(), this.pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());


            wBEOB.setSalaryTypeId(masterSalaryTemp.getSalaryType().getId());

            model.addAttribute("approveBean", wBEOB);
            model.addAttribute("sBean", sMainList);
            model.addAttribute("miniBean", masterSalaryTemp);
            addRoleBeanToModel(model, request);
        }


        return VIEW_NAME;

    }


    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_update", required = false) String pUpd,
                                @RequestParam(value = "_approve", required = false) String pApprove,
                                @RequestParam(value = "_confirm", required = false) String pConfirm,
                                @RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") MasterSalaryTemp ppDMB,
                                BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        List<SalaryTemp> sTemp, sMainList;
        PaginatedBean wBEOB;

        BusinessCertificate bc = this.getBusinessCertificate(request);

        PaginationBean paginationBean = getPaginationInfo(request);

        List<SalaryInfo> salInfo = new ArrayList<>();

        PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("parentId", ppDMB.getId()));

        sTemp = (List<SalaryTemp>) this.genericService.loadPaginatedObjectsByPredicates(SalaryTemp.class, predicateBuilder,(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
        wBEOB = new PaginatedBean(sTemp, paginationBean.getPageNumber(), this.pageLength, sTemp.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
        sMainList = this.genericService.loadAllObjectsUsingRestrictions(SalaryTemp.class, predicateBuilder.getPredicates(),null);


        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:approveSalaryStructure.do";
        }

        if (isButtonTypeClick(request,REQUEST_PARAM_APPROVE)) {
            ConfigurationBean configurationBean = IppmsUtilsExt.loadConfigurationBean(genericService,bc);
            if(!configurationBean.isPayGroupCreatorCanApprove()) {
                if (ppDMB.getInitiator().getId().equals(bc.getLoginId())) {
                    wBEOB.setHasErrors(true);
                    result.rejectValue("", "Invalid.True", "You can not approve Salary Structure you created.");
                    model.addAttribute(DISPLAY_ERRORS, BLOCK);
                    addDisplayErrorsToModel(model, request);
                    model.addAttribute("sBean", sMainList);
                    model.addAttribute("approveBean", wBEOB);
                    model.addAttribute("status", result);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("miniBean", ppDMB);
                    return VIEW_NAME;
                }
            }
            if(!wBEOB.isShowForConfirm()){
                    wBEOB.setShowForConfirm(true);
                    ppDMB.setApprovalStatusInd(IConstants.ON);
                    model.addAttribute("sBean", sMainList);
                    model.addAttribute("approveBean", wBEOB);
                    addRoleBeanToModel(model, request);
                    model.addAttribute("miniBean", ppDMB);
                return "approval/confirmSalaryStructureApprovalForm";
                }
            }

        if (isButtonTypeClick(request,REQUEST_PARAM_REJECT)) {

            ConfigurationBean configurationBean = IppmsUtilsExt.loadConfigurationBean(genericService,bc);
            if(!configurationBean.isPayGroupCreatorCanApprove())
            if(ppDMB.getInitiator().getId().equals(bc.getLoginId())){
                wBEOB.setHasErrors(true);
                result.rejectValue("", "Invalid.True","You can not approve Salary Structure you created.");
                model.addAttribute(DISPLAY_ERRORS, BLOCK);
                addDisplayErrorsToModel(model, request);
                model.addAttribute("sBean", sMainList);
                model.addAttribute("approveBean", wBEOB);
                model.addAttribute("status", result);
                addRoleBeanToModel(model, request);
                model.addAttribute("miniBean", ppDMB);
                return VIEW_NAME;
            }
            if(!wBEOB.isShowForConfirm()){
                wBEOB.setShowForConfirm(true);
                ppDMB.setApprovalStatusInd(2);
                model.addAttribute("sBean", sMainList);
                model.addAttribute("approveBean", wBEOB);
                addRoleBeanToModel(model, request);
                model.addAttribute("miniBean", ppDMB);
                return "approval/confirmSalaryStructureApprovalForm";
            }
        }

        if(isButtonTypeClick(request, REQUEST_PARAM_CONFIRM)){
            SalaryType salaryType = new SalaryType(ppDMB.getSalaryType().getId());
            if(ppDMB.getApprovalStatusInd() == IConstants.ON){
                for (SalaryTemp s : sMainList) {
                    SalaryInfo si = new SalaryInfo();
                    si.setAdminAllowance(s.getAdminAllowance());
                    si.setBusinessClientId(ppDMB.getBusinessClientId());
                    si.setCallDuty(s.getCallDuty());
                    si.setConsAllowance(s.getConsAllowance());
                    si.setCreatedDate(s.getCreatedDate());
                    si.setDomesticServant(s.getDomesticServant());
                    si.setDriversAllowance(s.getDriversAllowance());
                    si.setEnhancedHealthAllowance(s.getEnhancedHealthAllowance());
                    si.setEntertainment(s.getEntertainment());
                    si.setExamAllowance(s.getExamAllowance());
                    si.setFurniture(s.getFurniture());
                    si.setHazard(s.getHazard());
                    si.setInducement(s.getInducement());
                    si.setJournal(s.getJournal());
                    si.setLastModBy(s.getLastModBy());
                    si.setLastModTs(s.getLastModTs());
                    si.setLcosAllowance(s.getLcosAllowance());
                    si.setLevel(s.getLevel());
                    si.setMeal(s.getMeal());
                    si.setMedicalAllowance(s.getMedicalAllowance());
                    si.setBasicMonthlySalary(s.getBasicMonthlySalary());
                    si.setMotorVehicle(s.getMotorVehicle());
                    si.setNhf(s.getNhf());
                    si.setNurseOtherAllowance(s.getNurseOtherAllowance());
                    si.setOutfitAllowance(s.getOutfitAllowance());
                    si.setOvertimeAllowance(s.getOvertimeAllowance());
                    si.setPayGroupCode(s.getPayGroupCode());
                    si.setPaye(s.getPaye());
                    si.setPersonalAssistant(s.getPersonalAssistant());
                    si.setQuatersAllowance(s.getQuatersAllowance());
                    si.setRent(s.getRent());
                    si.setResearchAllowance(s.getResearchAllowance());
                    si.setResponsibilityAllowance(s.getResponsibilityAllowance());
                    si.setRuralPosting(s.getRuralPosting());
                    si.setSecurityAllowance(s.getSecurityAllowance());
                    si.setShiftDuty(s.getShiftDuty());
                    si.setSittingAllowance(s.getSittingAllowance());
                    si.setSpaAllowance(s.getSpaAllowance());
                    si.setSpecialHealthAllowance(s.getSpecialHealthAllowance());
                    si.setSpecialistAllowance(s.getSpecialistAllowance());
                    si.setStep(s.getStep());
                    si.setSwesAllowance(s.getSwesAllowance());
                    si.setTeachingAllowance(s.getTeachingAllowance());
                    si.setToolsTorchLightAllowance(s.getToolsTorchLightAllowance());
                    si.setTotorAllowance(s.getTotorAllowance());
                    si.setTransport(s.getTransport());
                    si.setTss(s.getTss());
                    si.setTws(s.getTws());
                    si.setUniformAllowance(s.getUniformAllowance());
                    si.setUnionDues(s.getUnionDues());
                    si.setUtility(s.getUtility());
                    si.setSalaryType(salaryType);
                    salInfo.add(si);
//                    s.setApprovedInd(IConstants.ON);
//                    this.genericService.saveOrUpdate(s);

                    //Update Master Salary Structure Temp

                    ppDMB.setApprovalStatusInd(ON);
                    ppDMB.setApprover(new User(bc.getLoginId()));
                    ppDMB.setApprovedDate(LocalDate.now());
                    ppDMB.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime());
                    this.genericService.saveObject(ppDMB);

                    //Create Notification For Object

                }
                String redirectUrl = "requestNotification.do?arid=" + ppDMB.getId() + "&s=1&oc="+IConstants.PAY_GROUP_INIT_URL_IND;
                NotificationService.storeNotification(bc,genericService,ppDMB,redirectUrl,"Pay Group Approval Notification", IConstants.PAY_GROUP_APPROVAL_CODE);
                this.genericService.storeObjectBatch(salInfo);
                model.addAttribute(IConstants.SAVED_MSG, "Salary Structure Approved successfully.");
            }
            else if(ppDMB.getApprovalStatusInd() == 2){
                //Update Master Salary Structure Temp
                ppDMB.setApprovalStatusInd(2);
                ppDMB.setApprover(new User(bc.getLoginId()));
                ppDMB.setApprovedDate(LocalDate.now());
                ppDMB.setApprovalAuditTime(PayrollBeanUtils.getCurrentTime());
                this.genericService.saveObject(ppDMB);

                //Create Notification For Object
                String redirectUrl = "requestNotification.do?arid=" + ppDMB.getId() + "&s=2&oc="+IConstants.PAY_GROUP_INIT_URL_IND;
                NotificationService.storeNotification(bc,genericService,ppDMB,redirectUrl,"Pay Group Approval Notification", IConstants.PAY_GROUP_APPROVAL_CODE);
                model.addAttribute(IConstants.SAVED_MSG, "Salary Structure Rejected successfully.");
            }
        }

        wBEOB.setShowLink(true);
        model.addAttribute("approveBean", wBEOB);
        model.addAttribute("sBean", sMainList);
        model.addAttribute("saved", true);
        addRoleBeanToModel(model, request);
        model.addAttribute("miniBean", ppDMB);
        return "approval/confirmSalaryStructureApprovalForm";
    }
}
