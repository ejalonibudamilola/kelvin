/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.approvals;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.hr.ext.AllowanceRuleControllerService;
import com.osm.gnl.ippms.ogsg.domain.approval.*;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.MasterSalaryTemp;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryTemp;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.payroll.utils.CurrencyWordGenerator;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public abstract class ApprovalNotificationHelper {


    public static String generateAllowanceRuleView(Long pArid, int pSaved ,Model model, GenericService genericService) throws IllegalAccessException, InstantiationException {
        if(pSaved == 2){
            model.addAttribute(IConstants.SAVED_MSG, "Pay Group Allowance Rule Request Rejected Successfully.");
        }else{
            model.addAttribute(IConstants.SAVED_MSG, "Pay Group Allowance Rule Approved Successfully");
        }

        model.addAttribute("saved", true);

        model.addAttribute("miniBean", genericService.loadObjectById(AllowanceRuleApproval.class, pArid));


        return "notification/allowRuleNotifierForm";
    }


    public static String generateAllowanceRuleCreateView(Long pArId, int pSaved, Model model, GenericService genericService,
                                                         BusinessCertificate bc, HttpServletRequest request, IMenuService pMenuService) throws IllegalAccessException, InstantiationException {
        HrMiniBean empHrBean = new HrMiniBean();

        AllowanceRuleApproval  allowanceRuleApproval = genericService.loadObjectById(AllowanceRuleApproval .class, pArId);

        empHrBean.setAllowanceRuleMaster(allowanceRuleApproval.getAllowanceRuleMaster());
        empHrBean = AllowanceRuleControllerService.setRequiredValues(allowanceRuleApproval.getAllowanceRuleMaster().getHiringInfo(), empHrBean,true);

        String actionCompleted = bc.getStaffTypeName() + " " + allowanceRuleApproval.getAllowanceRuleMaster().getHiringInfo().getAbstractEmployeeEntity().getDisplayName();

        if (pSaved == 1) //expired.
            actionCompleted += " Pay Group Allowance Rule expired successfully";
        else if (pSaved == 2)
            actionCompleted += " Pay Group Allowance Rule created successfully";
        else if (pSaved == 3)
            actionCompleted += " Pay Group Allowance Rule edited successfully";
        else {
            actionCompleted += " Pay Group Allowance Rule left unedited (No changes or invalid changes made) ";
        }
        if(pMenuService.canUserAccessURL(bc, "/approveAllowanceRule.do", "/approveAllowanceRule.do")) {
            bc.setCanApprove(true);
            model.addAttribute("canApprove",true);
            bc.setTargetUrl("approveAllowanceRule.do?arid="+allowanceRuleApproval.getId());
        }
        model.addAttribute(IConstants.SAVED_MSG, actionCompleted);

        model.addAttribute("miniBean", empHrBean);
         model.addAttribute("pageTitle", "Pay Group Allowance Rule Details");

        return "notification/allowRuleInitNotificationForm";

    }

    public static String getTransferApprovalView(Long pArid, Model model, int pSaved, GenericService genericService, BusinessCertificate bc, IMenuService menuService, int saved) throws IllegalAccessException, InstantiationException {

        TransferApproval wTA = genericService.loadObjectById(TransferApproval.class, pArid);

        if(wTA.getParentObject().isSchoolStaff())
            wTA.setOldMda(wTA.getParentObject().getSchoolName());
        else
            wTA.setOldMda(wTA.getParentObject().getCurrentMdaName());

        if (wTA.getSchoolInfo() != null && !wTA.getSchoolInfo().isNewEntity()) {

            wTA.setNewMda(wTA.getSchoolInfo().getName());
        } else {
            wTA.setNewMda(wTA.getMdaDeptMap().getMdaInfo().getName());
        }
        String actionCompleted;
        if(wTA.isNewRequest()){

            if (wTA.getSchoolInfo() != null && !wTA.getSchoolInfo().isNewEntity()) {
                actionCompleted = bc.getStaffTypeName() + " " + wTA.getParentObject().getDisplayName() + " Scheduled for Transfer to " + wTA.getSchoolInfo().getName() + " Successfully";


            } else {
                actionCompleted = bc.getStaffTypeName() + " " + wTA.getParentObject().getDisplayName() + " Scheduled for Transfer to " + wTA.getMdaDeptMap().getMdaInfo().getName() + " Successfully";


            }
            if(menuService.canUserAccessURL(bc, "/approveAllowanceRule.do", "/approveAllowanceRule.do")) {
                bc.setCanApprove(true);
                model.addAttribute("canApprove",bc.isCanApprove());
                bc.setTargetUrl("approveTransfer.do?tid="+wTA.getId());
            }
            model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
            model.addAttribute("saved", true);
            model.addAttribute("miniBean", wTA);

            return "notification/transferRequestNotificationForm";
        }else{
            if (pSaved == 2) {
                model.addAttribute(IConstants.SAVED_MSG, "Transfer Request Rejected Successfully.");
            } else {
                model.addAttribute(IConstants.SAVED_MSG, "Transfer Completed and Approved Successfully");
            }
            model.addAttribute("saved", true);
            model.addAttribute("miniBean", wTA);

            return "notification/transferApprovalNotificationForm";
        }


    }

    public static String getStepIncrementInitView(Long pArid, BusinessCertificate bc, GenericService genericService, Model model, HttpServletRequest request,int pSaved,IMenuService pMenuService) throws Exception {
        SessionManagerService.manageSession(request, model);

        StepIncrementApproval stepIncrementApproval = genericService.loadObjectById(StepIncrementApproval.class,pArid);
        HrMiniBean empHrBean = new HrMiniBean();

        String actionCompleted = null;
      
            empHrBean.setName(stepIncrementApproval.getStepIncrementTracker().getEmployee().getDisplayNameWivTitlePrefixed());
            empHrBean.setOldLevelAndStep(stepIncrementApproval.getOldSalaryInfo().getLevelStepStr());
            empHrBean.setNewLevelAndStep(stepIncrementApproval.getSalaryInfo().getLevelAndStepAsStr());
            empHrBean.setOldSalaryStr(stepIncrementApproval.getOldSalaryInfo().getAnnualSalaryStr());
            empHrBean.setNewSalaryStr(stepIncrementApproval.getSalaryInfo().getAnnualSalaryStr());

            if(stepIncrementApproval.isNewRequest())
               actionCompleted = bc.getStaffTypeName() + " " + empHrBean.getName() + " Step Increment Scheduled For Approval Successfully.";
            else if(stepIncrementApproval.isApproved())
                actionCompleted = bc.getStaffTypeName() + " " + empHrBean.getName() + " Step Increment Approved and Effected Successfully.";
            else if(stepIncrementApproval.isRejected())
                actionCompleted = bc.getStaffTypeName() + " " + empHrBean.getName() + " Step Increment Rejected.";

            HiringInfo pWhi = genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate("employee.id", stepIncrementApproval.getParentId()),
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));

        empHrBean.setName(pWhi.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed());

        empHrBean.setGradeLevelAndStep(pWhi.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getName() + ":" + pWhi.getAbstractEmployeeEntity().getSalaryInfo().getLevelAndStepAsStr());

        empHrBean.setFirstName(pWhi.getAbstractEmployeeEntity().getFirstName());
        empHrBean.setLastName(pWhi.getAbstractEmployeeEntity().getLastName());
        empHrBean.setMiddleName(StringUtils.trimToEmpty(pWhi.getAbstractEmployeeEntity().getInitials()));

        empHrBean.setHireDate(PayrollHRUtils.getFullDateFormat().format(pWhi.getHireDate()));

        int noOfYears = LocalDate.now().getYear() - pWhi.getHireDate().getYear();
        empHrBean.setYearsOfService(String.valueOf(noOfYears));
        empHrBean.setAssignedToObject(pWhi.getAbstractEmployeeEntity().getCurrentMdaName());
        empHrBean.setEmployeeId(pWhi.getAbstractEmployeeEntity().getEmployeeId());
        empHrBean.setSalaryScale(pWhi.getAbstractEmployeeEntity().getSalaryScale());
        model.addAttribute(IConstants.SAVED_MSG, actionCompleted);

        model.addAttribute("saved", Boolean.valueOf(true));
        if(stepIncrementApproval.isNewRequest()){
            model.addAttribute("miniBean",empHrBean);
            if(pMenuService.canUserAccessURL(bc, "/approveStepIncrement.do", "/approveStepIncrement.do")) {
                bc.setCanApprove(true);
                model.addAttribute("canApprove",true);
                bc.setTargetUrl("approveStepIncrement.do?aid="+stepIncrementApproval.getId());
            }
            return  "notification/stepIncrementInitNotificationForm";
        }else{
            model.addAttribute("miniBean",stepIncrementApproval);
            return "notification/stepIncrementApprovalNotificationForm";
        }

    }

    public static String getSalaryStructureApprovalView(Long pArid, BusinessCertificate bc, GenericService genericService, Model model, int pSaved, IMenuService menuService) throws IllegalAccessException, InstantiationException {

        MasterSalaryTemp masterSalaryTemp = genericService.loadObjectById(MasterSalaryTemp.class,pArid);
         FileParseBean fileParseBean = new FileParseBean();
        fileParseBean.setSuccessfulRecords(masterSalaryTemp.getSalaryTempList().size());
        fileParseBean.setPayTypeName(masterSalaryTemp.getSalaryType().getName());
        fileParseBean.setPayGroupList(new Vector<>(genericService.loadAllObjectsWithSingleCondition(SalaryTemp.class,CustomPredicate.procurePredicate("masterSalaryTempId", pArid),null)));
        if(pSaved == 1){
            model.addAttribute("mainHeader",fileParseBean.getPayTypeName()+" Approval Request Form");
            model.addAttribute("pageTitle","Salary Structure Request Form");
            if(menuService.canUserAccessURL(bc, "/approveSalaryStructure.do", "/approveSalaryStructure.do")) {
                bc.setCanApprove(true);
                model.addAttribute("canApprove",true);
                bc.setTargetUrl("approveSalaryStructure.do?mId="+pArid);
            }
         }else{
            model.addAttribute("mainHeader",fileParseBean.getPayTypeName()+" Approval Notification Form ");
            model.addAttribute("pageTitle","Salary Structure Approval Notification Form");
        }
        model.addAttribute("fileUploadResult",fileParseBean);
        return "notification/salaryStructureNotificationForm";
    }

    public static String getAmAliveApprovalView(Long pArid, BusinessCertificate bc, GenericService genericService, Model model, int pSaved, IMenuService menuService) throws Exception {

        AmAliveApproval amAliveApproval = genericService.loadObjectById(AmAliveApproval.class,pArid);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        predicates.add(CustomPredicate.procurePredicate("groupTicketId", amAliveApproval.getTicketId()));

        List<AmAliveApproval> empList = genericService.loadAllObjectsUsingRestrictions(AmAliveApproval.class, predicates, null);
        BusinessEmpOVBeanInactive pCList = new BusinessEmpOVBeanInactive(empList);
        for(AmAliveApproval h : empList){
            pCList.setTotalMonthlyBasic(pCList.getTotalMonthlyBasic() + h.getHiringInfo().getMonthlyPensionAmount());
        }
        pCList.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(pCList.getTotalMonthlyBasic()));
        if(pSaved == 1){
            model.addAttribute("mainHeader",bc.getStaffTypeName()+" Am Alive Approval Request Form");
            model.addAttribute("pageTitle","Am Alive Approval Request Form");
            if(menuService.canUserAccessURL(bc, "/viewPendingAmAliveApprovals.do", "/viewPendingAmAliveApprovals.do")) {
                bc.setCanApprove(true);
                model.addAttribute("canApprove",true);
                bc.setTargetUrl("viewPendingAmAliveApprovals.do");
            }
            model.addAttribute(IConstants.SAVED_MSG, "I Am Alive Treatments scheduled for Approval Successfully.");
        }else{
            model.addAttribute(IConstants.SAVED_MSG, "I Am Alive Treatments Approved Successfully.");
        }
        model.addAttribute("saved", pSaved == 1);
        model.addAttribute("miniBean", pCList);

        return "notification/amAliveNotificationForm";


    }

    public static String getStaffCreationInitView(Long pArid, BusinessCertificate bc, GenericService genericService, Model model, int pSaved, IMenuService menuService) throws IllegalAccessException, InstantiationException {

        EmployeeApproval employeeApproval = genericService.loadObjectById(EmployeeApproval.class,pArid);
        HiringInfo hiringInfo  = genericService.loadObjectUsingRestriction(HiringInfo.class,Arrays.asList(CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),employeeApproval.getChildObject().getId()),
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));


        String actionCompleted = bc.getStaffTypeName()+" :" + hiringInfo.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed()
                + " Created Successfully.";

        model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
        model.addAttribute("saved", Boolean.valueOf(true));
        model.addAttribute("miniBean",hiringInfo);
        model.addAttribute("roleBean", bc);
        return "notification/empNotificationForm";
    }
}
