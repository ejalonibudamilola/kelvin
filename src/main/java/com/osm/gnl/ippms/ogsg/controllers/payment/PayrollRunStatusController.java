/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.PayrollConfigImpact;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RbaConfigBean;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.engine.CalculateBulkPaySlip;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayroll;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayrollPensioner;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

import static com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils.getCurrentTime;


@Controller
@RequestMapping({"/displayStatus.do"})
public class PayrollRunStatusController extends BaseController {

    private final String VIEW = "progress/progressBarForm";

    public PayrollRunStatusController() {
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException, IllegalAccessException, InstantiationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        ProgressBean wPB = new ProgressBean();


        Object o = getSessionAttribute(request, "myCalcPay");
        if (o == null) {
            return "redirect:approvePaychecksForm.do?pid=" + bc.getBusinessClientInstId();
        }
        if (o.getClass().isAssignableFrom(CalculatePayroll.class)) {
            CalculatePayroll wCP = (CalculatePayroll) o;
            if (wCP.isRerun()) {
                wPB.setDisplayMessage("Reprocessing Pending Paychecks.....");
                wPB.setPageTitle("Payroll Re-Processing");
            } else if (wCP.isDeleted()) {
                wPB.setDisplayMessage("Rerunning Deleted Paychecks.....");
                wPB.setPageTitle("Deleted Paycheck Payroll Re-Processing");
            } else {
                wPB.setDisplayMessage("Generating Payroll.....");
                wPB.setPageTitle("Payroll Processing");
            }
            if (wCP.isFinished()) {
                if (wCP.isRerun() || wCP.isDeleted())
                    endPayrollRun(wCP.getPayrollRunMasterBean(), wCP.getRerunPayrollBean(), genericService, bc.getBusinessClientInstId(), wCP.getPayrollConfigImpact());
                else
                    endPayrollRun(wCP.getPayrollRunMasterBean(), null, genericService, bc.getBusinessClientInstId(), wCP.getPayrollConfigImpact());

                removeSessionAttribute(request, "myCalcPay");
                savePayrollInformation(getSession(request), bc.getBusinessClientInstId());
                if (wCP.isDeleted())
                    return "redirect:approveRerunPaychecks.do";
                return "redirect:payrollRunStatistics.do?rm=" + wCP.getRunMonth() + "&ry=" + wCP.getRunYear();
            }
            wPB.setCurrentCount(wCP.getCurrentRecord());
            wPB.setTotalElements(wCP.getTotalRecords());
            wPB.setPercentage(wCP.getPercentage());
            wPB.setTimeRemaining(wCP.getTimeToElapse());

        } else if (o.getClass().isAssignableFrom(CalculatePayrollPensioner.class)) {

            CalculatePayrollPensioner wCP = (CalculatePayrollPensioner) o;
            if (wCP.isRerun()) {
                wPB.setDisplayMessage("Reprocessing Pending Paychecks.....");
                wPB.setPageTitle("Pensioner Payroll Re-Processing");
            } else {
                wPB.setDisplayMessage("Generating Pensioner Payroll....");
                wPB.setPageTitle("Pensioner Payroll Processing");
            }

            if (wCP.isFinished()) {

                if (wCP.isRerun() || wCP.isDeletedPaychecks())
                    endPayrollRun(wCP.getPayrollRunMasterBean(), wCP.getRerunPayrollBean(), genericService, bc.getBusinessClientInstId(), wCP.getPayrollConfigImpact());
                else
                    endPayrollRun(wCP.getPayrollRunMasterBean(), null, genericService, bc.getBusinessClientInstId(), wCP.getPayrollConfigImpact());

                removeSessionAttribute(request, "myCalcPay");
                savePayrollInformation(getSession(request), bc.getBusinessClientInstId());
                if (wCP.isDeletedPaychecks())
                    return "redirect:approveRerunPaychecks.do";
                return "redirect:payrollRunStatistics.do?rm=" + wCP.getRunMonth() + "&ry=" + wCP.getRunYear();
            }
            wPB.setCurrentCount(wCP.getCurrentRecord());
            wPB.setTotalElements(wCP.getTotalRecords());
            wPB.setPercentage(wCP.getPercentage());
            wPB.setTimeRemaining(wCP.getTimeToElapse());
        }
        /*else if(o.getClass().isAssignableFrom(NewSimpleExcelGenerator.class)){
            NewSimpleExcelGenerator cERG = (NewSimpleExcelGenerator) o;
            wPB.setDisplayMessage("Generating Excel Report....Please Wait");
            wPB.setPageTitle("Custom Excel Report");
            if(!wPB.isDownload())
                wPB.setDownload(true);
            if(cERG.isFinished()){
                wPB.setDownload(true);
                wPB.setCurrentCount(cERG.getCurrentRecord());
                wPB.setTotalElements(cERG.getTotalRecords());
                wPB.setPercentage(cERG.getPercentage());
                ((NewSimpleExcelGenerator) o).stop(true);
                wPB.setDisplayMessage("Excel Generated Successfully");
                removeSessionAttribute(request, "myCalcPay");


                addSessionAttribute(request,"currentWorkBook", cERG.getWorkbook());
                addSessionAttribute(request,"title", cERG.getTitle());
                return "redirect:exportExcelToView.do";
            }
            wPB.setCurrentCount(cERG.getCurrentRecord());
            wPB.setTotalElements(cERG.getTotalRecords());
            wPB.setPercentage(cERG.getPercentage());
            wPB.setTimeRemaining(cERG.getTimeToElapse());
        }*/

        addRoleBeanToModel(model, request);
        model.addAttribute("progressBean", wPB);
        return VIEW;
    }

    private void endPayrollRun(PayrollRunMasterBean pPayrollRunMasterBean, RerunPayrollBean pRerunPayrollBean, GenericService genericService, Long pBid, PayrollConfigImpact payrollConfigImpact) throws InstantiationException, IllegalAccessException {
        RbaConfigBean wRCB = genericService.loadObjectWithSingleCondition(RbaConfigBean.class, CustomPredicate.procurePredicate("businessClientId", pBid));
        if (!wRCB.isNewEntity())
            pPayrollRunMasterBean.setRbaPercentage(wRCB.getRbaPercentage());
        pPayrollRunMasterBean.setEndDate(LocalDate.now());
        pPayrollRunMasterBean.setEndTime(PayrollBeanUtils.getCurrentTime(false));
        if (!pPayrollRunMasterBean.isPayrollError() && !pPayrollRunMasterBean.isPayrollCancelled())
            pPayrollRunMasterBean.setPayrollStatus(IConstants.AWAITING_APPROVAL_IND);
        genericService.saveObject(pPayrollRunMasterBean);

        if (pRerunPayrollBean != null) {
            genericService.deleteObject(pRerunPayrollBean);
        }
        if (payrollConfigImpact != null)
            genericService.saveOrUpdate(payrollConfigImpact);
    }


    private void savePayrollInformation(HttpSession pSession, Long pBid) {
        Object ppStart = pSession.getAttribute("ppStartDate");
        Object ppEndDate = pSession.getAttribute("ppEndDate");
        Object payDate = pSession.getAttribute("payDayDate");
        if ((ppStart == null) || (ppEndDate == null) || (payDate == null)) {
            return;
        }
        PayrollRun pr = new PayrollRun();

        pr.setBusinessClientId(pBid);
        pr.setPayPeriodStart((LocalDate) ppStart);
        pr.setPayPeriodEnd((LocalDate) ppEndDate);
        pr.setLastPayDate((LocalDate) payDate);

        this.genericService.storeObject(pr);

        pSession.removeAttribute("ppStartDate");
        pSession.removeAttribute("ppEndDate");
        pSession.removeAttribute("payDayDate");

    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);

        Object o = getSessionAttribute(request, "myCalcPay");
        if (o != null) {
            removeSessionAttribute(request, "myCalcPay");
        }

        PayrollRunMasterBean pPayrollRunningBean = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class,
                Arrays.asList(super.getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("payrollStatus", 1)));
        if (!pPayrollRunningBean.isNewEntity()) {
            pPayrollRunningBean.setEndDate(LocalDate.now());
            pPayrollRunningBean.setEndTime(getCurrentTime(false));
            pPayrollRunningBean.setPayrollStatus(IConstants.CANCEL_PAYROLL_IND);
            genericService.storeObject(pPayrollRunningBean);
        }
        Thread.sleep(200L);
        return "redirect:deletePendingPayroll.do";


    }


}