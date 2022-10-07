package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.report.ReportBean;
import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping({"/payDifference.do"})
@SessionAttributes(types={VariationReportBean.class})
public class VariationReportFormController extends BaseController
{


   private final  EmployeeService employeeService;

  private final String VIEW = "report/reportSelectionForm";

    @Autowired
  public VariationReportFormController(EmployeeService employeeService)
  {
      this.employeeService = employeeService;
  }

  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	 // BusinessCertificate bc = this.getBusinessCertificate(request);

    PayrollFlag pf  = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));
	LocalDate startDate = pf.getPayPeriodStart();

    List<ReportBean> repList = new ArrayList<>();
    HashMap<Integer, ReportBean> wRepMap = new HashMap<>();

    ReportBean newStaffReport = new ReportBean(1L, "New Staff Report", "/viewNewStaff.do");
    newStaffReport.setNewStaffReport(true);
    newStaffReport.setTemplateName("NewStaffReport");
    newStaffReport.setUsingReconsiliationReport(true);
    wRepMap.put(newStaffReport.getId().intValue(), newStaffReport);

    ReportBean terminatedStaff = new ReportBean(2L, "Terminated Employees Report", "/viewTerminatedEmps.do");
    terminatedStaff.setTemplateName("Termination");
    terminatedStaff.setTerminationReport(true);
    terminatedStaff.setUsingReconsiliationReport(true);
    wRepMap.put(terminatedStaff.getId().intValue(), terminatedStaff);

    ReportBean suspensionReport = new ReportBean(3L, "Suspension Report", "/viewSuspensionLog.do");
    suspensionReport.setTemplateName("Suspension");
    suspensionReport.setSuspensionReport(true);
    suspensionReport.setUsingReconsiliationReport(true);
    wRepMap.put(suspensionReport.getId().intValue(), suspensionReport);

    ReportBean reinstatementReport = new ReportBean(4L, "Reinstatement Report", "/viewReinstatementLog.do");
    reinstatementReport.setTemplateName("Reinstatement");
    reinstatementReport.setReinstatementReport(true);
    reinstatementReport.setUsingReconsiliationReport(true);
    wRepMap.put(reinstatementReport.getId().intValue(), reinstatementReport);

    ReportBean reabsorptionReport = new ReportBean(5L, "Reabsorption Report", "/viewAbsorptionLog.do");
    reabsorptionReport.setTemplateName("Reabsorption");
    reabsorptionReport.setReabsorptionReport(true);
    reabsorptionReport.setUsingReconsiliationReport(true);
    wRepMap.put(reabsorptionReport.getId().intValue(), reabsorptionReport);

    ReportBean reassignmentReport = new ReportBean(6L, "Reassignment Report", "/viewReassignments.do");
    reassignmentReport.setTemplateName("Reassignment");
    reassignmentReport.setDeductionReport(true);
    reassignmentReport.setUsingReconsiliationReport(true);
    wRepMap.put(reassignmentReport.getId().intValue(), reassignmentReport);

    ReportBean promotionReport = new ReportBean(7L, "Promotion Report", "/viewPromotions.do");
    promotionReport.setTemplateName("Promotion");
    promotionReport.setPromotionReport(true);
    promotionReport.setUsingReconsiliationReport(true);
    wRepMap.put(promotionReport.getId().intValue(), promotionReport);

    ReportBean incrementReport = new ReportBean(8L, "Step Increment Report", "/viewStepIncrementLog.do");
    incrementReport.setTemplateName("StepIncrement");
    incrementReport.setIncrementReport(true);
    incrementReport.setUsingReconsiliationReport(true);
    wRepMap.put(incrementReport.getId().intValue(), incrementReport);

    ReportBean specialAllowanceReport = new ReportBean(9L, "Special Allowance Increase", "/specAllowInfoLog.do");
    specialAllowanceReport.setTemplateName("TotalAllowance");
    specialAllowanceReport.setSpecAllowanceReport(true);
    specialAllowanceReport.setUsingReconsiliationReport(true);
    wRepMap.put(specialAllowanceReport.getId().intValue(), specialAllowanceReport);

    ReportBean specialAllowanceDecrease = new ReportBean(11L, "Special Allowance Decrease", "/specAllowInfoDecreaseLog.do");
    specialAllowanceDecrease.setTemplateName("TotalAllowanceDecrease");
    specialAllowanceDecrease.setSpecAllowDecreaseReport(true);
    specialAllowanceDecrease.setUsingReconsiliationReport(true);
    wRepMap.put(specialAllowanceDecrease.getId().intValue(), specialAllowanceDecrease);

    ReportBean prevTerminatedStaff = new ReportBean(12L, "Previous Terminated Employees", "/viewPrevTerminatedEmps.do");
    prevTerminatedStaff.setTemplateName("PrevTermination");
    prevTerminatedStaff.setPrevTerminationReport(true);
    prevTerminatedStaff.setUsingReconsiliationReport(true);
    wRepMap.put(prevTerminatedStaff.getId().intValue(), prevTerminatedStaff);

    ReportBean summary = new ReportBean(10L, "Reconciliation Report", "/viewSummary.do");
    summary.setTemplateName("SummaryPage");
    summary.setReconciliationReport(true);
    summary.setUsingReconsiliationReport(true);

    wRepMap.put(summary.getId().intValue(), summary);



    repList.add(promotionReport);
    repList.add(summary);
    repList.add(reabsorptionReport);
    repList.add(newStaffReport);
    repList.add(reassignmentReport);
    repList.add(suspensionReport);
    repList.add(reinstatementReport);
    repList.add(specialAllowanceReport);
    repList.add(specialAllowanceDecrease);
    repList.add(incrementReport);
    repList.add(terminatedStaff);
    repList.add(prevTerminatedStaff);

    Collections.sort(repList);

    addSessionAttribute(request, "reportList", repList);
    addSessionAttribute(request, "reportMap", wRepMap);

    model = (Model) new VariationReportFormGenerator().generateReports(request, employeeService, genericService, startDate, model, repList);
    addRoleBeanToModel(model, request);
    model.addAttribute("title", "Reconciliation");
    return VIEW;
  }

  
@RequestMapping(method={RequestMethod.GET}, params={"sDate"})
  public String setupForm(@RequestParam("sDate")String payPeriod, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);


    LocalDate startDate;
   // Date endDate = null;

    if(payPeriod == null)
    {
	    PayrollFlag pf = new PayrollFlag();
        pf = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));
        startDate = pf.getPayPeriodStart();
	 //  	 endDate = pf.getPayPeriodEnd();
    }
    else {

    	LocalDate gCal = PayrollBeanUtils.setDateFromString(payPeriod);
    	startDate = gCal;
    	//endDate = pf.getPayPeriodEnd();
    }

    List<ReportBean> repList = (List<ReportBean>) getSessionAttribute(request, "reportList");

    model = (Model) new VariationReportFormGenerator().generateReports(request, employeeService, this.genericService, startDate, model, repList);

    addRoleBeanToModel(model, request);
    model.addAttribute("title", "Reconciliation");
    return VIEW;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_update", required=false) String update,
		  @RequestParam(value="_cancel", required=false) String cancel,
		  @RequestParam(value="_load", required=false) String load,
		  @ModelAttribute("profileBean") VariationReportBean pHMB,
		  BindingResult result, SessionStatus status, Model model, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);



    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
    {
    	removeSessionAttribute(request, "reportList");
    	removeSessionAttribute(request, "reportMap");
      return "redirect:reportsOverview.do";
    }



    if(isButtonTypeClick(request,REQUEST_PARAM_LOAD))
    {

    	ReportBean toUse = new ReportBean();
    	List<ReportBean> selected = new ArrayList<>();
    	List<ReportBean> rBList = pHMB.getReportsList();
    	for(ReportBean r : rBList)
    	{
    		if(r.isChecked()) {
				selected.add(r);
			}
    	}

    	if(selected.size() > 1)
    	{
//	    	RunVariationReport wRVP = new RunVariationReport(pdfService, selected, pHMB.getFromDateStr(), super.getSession(request), response, bc.getBusinessClientInstId(),false,false);
//	    	super.addSessionAttribute(request, "rvp", wRVP);
//	    	super.addSessionAttribute(request, "sRepMap", selected);
//	    	Thread t = new Thread(wRVP);
//	    	t.start();
            addSessionAttribute(request,
                    SCHOOL_SELECTED_MONTHLY_VARIATION, pHMB);
            addSessionAttribute(request,
                    SELECTED_MONTHLY_VARAIATION_OBJECT, selected);
            LocalDate date = pHMB.getFromDate();
            String dateStr = PayrollBeanUtils.getDateAsString(date);
            return "redirect:viewPromotions.do?sDate="+dateStr;
    	}
    	else if(selected.size() == 1)
    	{
            addSessionAttribute(request,
                    SCHOOL_SELECTED_MONTHLY_VARIATION, pHMB);
            addSessionAttribute(request,
                    SELECTED_MONTHLY_VARAIATION_OBJECT, selected);
            LocalDate date = pHMB.getFromDate();
            String dateStr = PayrollBeanUtils.getDateAsString(date);
            return "redirect:viewPromotions.do?sDate="+dateStr;
    	}
    }

    if(isButtonTypeClick(request, REQUEST_PARAM_UPD))
    {
    	int rm = pHMB.getRunMonth();
    	int ry = pHMB.getRunYear();

    	LocalDate wCal = PayrollBeanUtils.getDateFromMonthAndYear(rm, ry);
    	pHMB.setFromDate(wCal);

        LocalDate date = pHMB.getFromDate();
        String dateStr = PayrollBeanUtils.getDateAsString(date);
    	return "redirect:payDifference.do?sDate="+dateStr;
    }

    return "redirect:reportsOverview.do";
  }
}