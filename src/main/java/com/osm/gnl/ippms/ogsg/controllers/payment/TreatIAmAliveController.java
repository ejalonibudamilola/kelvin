/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.HrServiceHelper;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.approval.AmAliveApproval;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.CurrencyWordGenerator;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/treatIamAlive.do"})
@SessionAttributes(types = {BusinessEmpOVBeanInactive.class})
public class TreatIAmAliveController extends BaseController {


    private final HrServiceHelper hrServiceHelper;

    private final String VIEW_NAME = "employee/treatIamAliveForm";

    @Autowired
    public TreatIAmAliveController(final HrServiceHelper hrServiceHelper) {
        this.hrServiceHelper = hrServiceHelper;
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"rm", "ry"})
    public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if(IppmsUtilsExt.countNoOfIamAliveApprovals(this.genericService,bc,pRunMonth,pRunYear) > 0){
            return "redirect:viewApproveRejectAmAlive.do?rm="+pRunMonth+"&ry="+pRunYear;
        }

        LocalDate fDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear, false);
        LocalDate tDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear, true);


        List<HiringInfo> empList = this.hrServiceHelper.getEmpByExpRetireDate(bc, fDate, tDate, true);

        ConfigurationBean configurationBean = this.loadConfigurationBean(request);
        BusinessEmpOVBeanInactive pCList = new BusinessEmpOVBeanInactive(empList);
        for (HiringInfo h : empList) {
            h.setResetIAmAliveDate(h.getExpectedDateOfRetirement().plusMonths(configurationBean.getIamAliveExt()));
            h.setOldPensionAmount(h.getMonthlyPensionAmount());
            pCList.setTotalMonthlyBasic(pCList.getTotalMonthlyBasic() + h.getMonthlyPensionAmount());

        }
        pCList.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(pCList.getTotalMonthlyBasic()));
        pCList.setNoOfYearsInService(empList.size());
        pCList.setRunMonth(pRunMonth);
        pCList.setRunYear(pRunYear);
        pCList.setShowRow(SHOW_ROW);
        pCList.setEmpDiff(configurationBean.getIamAliveExt());


        model.addAttribute("saved", false);
        model.addAttribute("miniBean", pCList);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }
    @RequestMapping(method = {RequestMethod.GET}, params = {"tid", "s"})
    public String setupForm(@RequestParam("tid") Long pTicketId, @RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        predicates.add(CustomPredicate.procurePredicate("ticketId", pTicketId));

        List<AmAliveApproval> empList = this.genericService.loadAllObjectsUsingRestrictions(AmAliveApproval.class, predicates, null);
        BusinessEmpOVBeanInactive pCList = new BusinessEmpOVBeanInactive(empList);
        for(AmAliveApproval h : empList){
            pCList.setTotalMonthlyBasic(pCList.getTotalMonthlyBasic() + h.getHiringInfo().getMonthlyPensionAmount());
        }
        pCList.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(pCList.getTotalMonthlyBasic()));
        pCList.setShowRow(SHOW_ROW);

        model.addAttribute("saved", pSaved == 1);
        model.addAttribute("miniBean", pCList);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }
    @RequestMapping(method = {RequestMethod.GET}, params = {"s", "rm", "ry"})
    public String setupForm(@RequestParam("s") int pSaved,@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        predicates.add(CustomPredicate.procurePredicate("approvalStatusInd", 0));
        predicates.add(CustomPredicate.procurePredicate("runMonth", pRunMonth));
        predicates.add(CustomPredicate.procurePredicate("runYear", pRunYear));

        List<AmAliveApproval> empList = this.genericService.loadAllObjectsUsingRestrictions(AmAliveApproval.class, predicates, null);
        BusinessEmpOVBeanInactive pCList = new BusinessEmpOVBeanInactive(empList);
        for(AmAliveApproval h : empList){
            pCList.setTotalMonthlyBasic(pCList.getTotalMonthlyBasic() + h.getHiringInfo().getMonthlyPensionAmount());
        }
        pCList.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(pCList.getTotalMonthlyBasic()));
        pCList.setShowRow(SHOW_ROW);
        String actionCompleted = "I Am Alive Treatments scheduled for Approval Successfully.";
        model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
        model.addAttribute("saved", pSaved == 1);
        model.addAttribute("miniBean", pCList);
        addRoleBeanToModel(model, request);
        return VIEW_NAME;
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_update", required = false) String updRep, @RequestParam(value = "_close", required = false) String cancel, @ModelAttribute("miniBean") BusinessEmpOVBeanInactive pLPB, BindingResult
            result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        Long ticketId = PayrollBeanUtils.getCurrentDateTimeAsLong(bc.getLoginId());

        if (isButtonTypeClick(request, REQUEST_PARAM_CLOSE)) {
            return "redirect:paydayForm.do";
        }

        HiringInfo helperHireInfo = null;
        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE)) {
            List<AmAliveApproval> approveAmAliveList = new ArrayList<>();
            boolean foo;
            for (HiringInfo h : (List<HiringInfo>) pLPB.getList()) {
                    foo = Boolean.valueOf(h.getSuspendBind()).booleanValue();
                    //This might mean an extension
                    if (h.getResetIAmAliveDate() != null) {
                        if (Period.between(h.getExpectedDateOfRetirement(), h.getResetIAmAliveDate()).getMonths() > pLPB.getEmpDiff()) {
                            result.rejectValue("", "InvalidValue", "I Am Alive Extension is more than the approved " + pLPB.getEmpDiff() + " Months for " + h.getEmployee().getDisplayName() + " [ " + h.getEmployee().getEmployeeId() + " ]");
                            continue;
                        } else {
                            approveAmAliveList.add(createApproval(h, foo, bc,pLPB.getRunMonth(),pLPB.getRunYear(),ticketId,null));
                        }
                    } else {
                        if(false == foo){
                          result.rejectValue("", "InvalidValue", "Either enter a valid value for 'I am Alive Extension' OR Suspend " +  h.getEmployee().getDisplayName() + " [ " + h.getEmployee().getEmployeeId() + " ]");
                          continue;
                        }else{
                            approveAmAliveList.add(createApproval(h, true, bc,pLPB.getRunMonth(),pLPB.getRunYear(),ticketId,null));

                        }
                    }


               if(helperHireInfo == null)
                   helperHireInfo = h;

            }
            if (result.hasErrors()) {
                addDisplayErrorsToModel(model, request);
                model.addAttribute("status", result);
                model.addAttribute("miniBean", pLPB);
                addRoleBeanToModel(model, request);

                return VIEW_NAME;
            } else {
                if (IppmsUtils.isNotNullOrEmpty(approveAmAliveList)) {
                    this.genericService.storeObjectBatch(approveAmAliveList);
                    AmAliveApproval amAliveApproval = new AmAliveApproval();
                    amAliveApproval = createApproval(helperHireInfo,false,bc,pLPB.getRunMonth(),pLPB.getRunYear(),ticketId,amAliveApproval);
                    genericService.saveObject(amAliveApproval);

                    NotificationService.storeNotification(bc,genericService,amAliveApproval,"requestNotification.do?arid="+amAliveApproval.getId()+"&s=1&oc="+IConstants.AM_ALIVE_INIT_URL_IND,"Am Alive Approval Request",AM_ALIVE_APPROVAL_CODE);
                    return "redirect:treatIamAlive.do?s=1&rm="+pLPB.getRunMonth()+"&ry="+pLPB.getRunYear();
                }

            }

        }
        return "redirect:treatIamAlive.do?rm=" + pLPB.getRunMonth() + "&ry=" + pLPB.getRunYear();


    }


    private AmAliveApproval createApproval(HiringInfo h, boolean pSuspendStaff, BusinessCertificate bc, int pRunMonth, int pRunYear, Long ticketId,AmAliveApproval amAliveApproval) throws InterruptedException {
        AmAliveApproval approveAmAlive;
        if(amAliveApproval != null){
            approveAmAlive = amAliveApproval;
            approveAmAlive.setTicketId(ticketId);
            approveAmAlive.setHiringInfo(new HiringInfo(h.getId()));
            approveAmAlive.setEntityId(h.getAbstractEmployeeEntity().getId());
            approveAmAlive.setEntityName(PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth,pRunYear,true,false));
            approveAmAlive.setEmployeeId(pRunMonth+""+pRunYear);
            approveAmAlive.setBusinessClientId(bc.getBusinessClientInstId());
            approveAmAlive.setInitiator(new User(bc.getLoginId()));
            approveAmAlive.setLastModTs(LocalDate.now());
            approveAmAlive.setRunMonth(pRunMonth);
            approveAmAlive.setRunYear(pRunYear);
            approveAmAlive.setGroupLeaderInd(ON);
            approveAmAlive.setInitiatedDate(LocalDate.now());

        }else {
             approveAmAlive = new AmAliveApproval();
            approveAmAlive.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(bc.getLoginId()));
            approveAmAlive.setHiringInfo(new HiringInfo(h.getId()));
            approveAmAlive.setEntityId(h.getAbstractEmployeeEntity().getId());
            approveAmAlive.setEntityName(h.getAbstractEmployeeEntity().getDisplayName());
            approveAmAlive.setEmployeeId(h.getAbstractEmployeeEntity().getEmployeeId());
            approveAmAlive.setGroupTicketId(ticketId);
            approveAmAlive.setBusinessClientId(bc.getBusinessClientInstId());
            approveAmAlive.setInitiator(new User(bc.getLoginId()));
            approveAmAlive.setInitiatedDate(LocalDate.now());
            approveAmAlive.setLastModTs(LocalDate.now());
            approveAmAlive.setRunMonth(pRunMonth);
            approveAmAlive.setRunYear(pRunYear);
            approveAmAlive.setInitiatedDate(LocalDate.now());

            Thread.sleep(10L);

            if (pSuspendStaff) {
                approveAmAlive.setSuspendInd(ON);
                approveAmAlive.setAmAliveDate(h.getExpectedDateOfRetirement());
            } else {
                approveAmAlive.setAmAliveDate(h.getResetIAmAliveDate());
            }
        }
        return approveAmAlive;
    }
}
