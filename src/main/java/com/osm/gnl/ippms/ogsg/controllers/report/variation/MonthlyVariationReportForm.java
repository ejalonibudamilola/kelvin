package com.osm.gnl.ippms.ogsg.controllers.report.variation;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.VariationReportFormGenerator;
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

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({ "/monthlyVariation.do" })
@SessionAttributes(types = { VariationReportBean.class })
public class MonthlyVariationReportForm extends BaseController {


	/*
	 * private static final String REQUEST_PARAM_CANCEL = "_cancel"; private
	 * static final String REQUEST_PARAM_CANCEL_VALUE = "cancel";
	 */

	@Autowired
	EmployeeService employeeService;


	private final String VIEW = "report/reportSelectionForm";
	private boolean usingReconciliation;
	private boolean usingVariationReport;

	public MonthlyVariationReportForm( ) {}

	@RequestMapping(method = { RequestMethod.GET })
	public String setupForm(Model model, HttpServletRequest request)
			throws Exception
	{
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		PayrollFlag pf = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class,getBusinessClientIdPredicate(request));
		LocalDate startDate = pf.getPayPeriodStart();

		List<ReportBean> repList = new ArrayList<>();
		HashMap<Integer, ReportBean> wRepMap = new HashMap<>();

		ReportBean newStaffReport = new ReportBean(1L, "New Staff Report",
				"/monthlyVariationviewNewStaff.do");
		newStaffReport.setNewStaffReport(true);
		newStaffReport.setTemplateName("NewStaffReport");
		newStaffReport
				.setSubReportTemplateName("MonthlyVariationNewStaffsubreport");
		newStaffReport
				.setSubReportDataSource("monthlyVariationNewstaffDataSource");
		newStaffReport
				.setMVariationTemplateName("MonthlyVariationNewStaffReport");
		newStaffReport.setUsingMonthlyVariation(true);
		wRepMap.put(newStaffReport.getId().intValue(), newStaffReport);

		ReportBean terminatedStaff = new ReportBean(2L,
				"Terminated Employees Report",
				"/monthlyVariationviewTerminatedEmps.do");
		terminatedStaff.setTemplateName("Termination");
		terminatedStaff
				.setSubReportTemplateName("MonthlyVariationTerminationsubreport");
		terminatedStaff
				.setSubReportDataSource("terminationSubreportDataSource");
		terminatedStaff
				.setMVariationTemplateName("MonthlyVariationTermination");
		terminatedStaff.setTerminationReport(true);
		terminatedStaff.setUsingMonthlyVariation(true);

		wRepMap.put(terminatedStaff.getId().intValue(), terminatedStaff);

		ReportBean suspensionReport = new ReportBean(3L, "Suspension Report",
				"/monthlyVariationviewSuspensionLog.do");
		suspensionReport.setTemplateName("Suspension");
		suspensionReport
				.setSubReportDataSource("monthlyVariationSuspensionDataSource");
		suspensionReport
				.setSubReportTemplateName("MonthlyVariationSuspensionsubreport");
		suspensionReport
				.setMVariationTemplateName("MonthlyVariationSuspension");
		suspensionReport.setSuspensionReport(true);
		suspensionReport.setUsingMonthlyVariation(true);

		wRepMap.put(suspensionReport.getId().intValue(), suspensionReport);

		ReportBean reinstatementReport = new ReportBean(4L,
				"Reinstatement Report",
				"/monthlyVariationviewReinstatementLog.do");
		reinstatementReport.setTemplateName("Reinstatement");
		reinstatementReport
				.setSubReportDataSource("monthlyVariationReinstatementDataSource");
		reinstatementReport
				.setSubReportTemplateName("MonthlyVariationReinstatementsubreport");
		reinstatementReport
				.setMVariationTemplateName("MonthlyVariationReinstatement");
		reinstatementReport.setReinstatementReport(true);
		reinstatementReport.setUsingMonthlyVariation(true);
		wRepMap.put(reinstatementReport.getId().intValue(), reinstatementReport);

		ReportBean reabsorptionReport = new ReportBean(5L,
				"Reabsorption Report", "/monthlyVariationviewAbsorptionLog.do");
		reabsorptionReport.setTemplateName("Reabsorption");
		reabsorptionReport
				.setSubReportDataSource("monthlyVariationReabsorptionDataSource");
		reabsorptionReport
				.setSubReportTemplateName("MonthlyVariationReabsorptionsubreport");
		reabsorptionReport
				.setMVariationTemplateName("MonthlyVariationReabsorption");
		reabsorptionReport.setReabsorptionReport(true);
		reabsorptionReport.setUsingMonthlyVariation(true);
		wRepMap.put(reabsorptionReport.getId().intValue(), reabsorptionReport);

		ReportBean deductionReport = new ReportBean(6L,
				"Deductions Report", "/monthlyVariationDeductionsReport.do");
		deductionReport.setTemplateName("DeductionsReport");
		deductionReport
				.setSubReportDataSource("monthlyVariationDeductionsDatasource");
		deductionReport
				.setSubReportTemplateName("MonthlyVariationDeductionssubreport");
		deductionReport
				.setMVariationTemplateName("MonthlyVariationDeductions");
		deductionReport.setDeductionReport(true);
		deductionReport.setUsingMonthlyVariation(true);
		wRepMap.put(deductionReport.getId().intValue(), deductionReport);

		ReportBean promotionReport = new ReportBean(7L, "Promotion Report",
				"/monthlyVariationViewPromotions.do");
		promotionReport.setTemplateName("Promotion");
		promotionReport
				.setSubReportDataSource("monthlyVariationPromotionDatasource");
		promotionReport
				.setSubReportTemplateName("MonthlyVariationPromotionsubreport");
		promotionReport.setMVariationTemplateName("MonthlyVariationPromotion");
		promotionReport.setUsingMonthlyVariation(true);
		promotionReport.setPromotionReport(true);
		wRepMap.put(promotionReport.getId().intValue(), promotionReport);


		ReportBean specialAllowanceReport = new ReportBean(9L,
				"Special Allowance Report",
				"/monthVariationSpecAllowInfoLog.do");
		specialAllowanceReport.setTemplateName("TotalAllowance");
		specialAllowanceReport
				.setSubReportDataSource("MonthlyVariationSpecialAllowanceSubreporttDataSource");
		specialAllowanceReport
				.setSubReportTemplateName("MonthlyVariationSpecialAllowanceSubreport");
		specialAllowanceReport.setMVariationTemplateName("TotalAllowance");
		specialAllowanceReport.setSpecAllowanceReport(true);
		specialAllowanceReport.setUsingMonthlyVariation(true);

		wRepMap.put(specialAllowanceReport.getId().intValue(), specialAllowanceReport);

		ReportBean demotionReport = new ReportBean(10L, "Demotion Report",
				"/monthVariationDemotionReport.do");
		demotionReport.setTemplateName("Demotion");
		demotionReport.setSubReportDataSource("");
		demotionReport.setSubReportTemplateName("");
		demotionReport.setMVariationTemplateName("");
		demotionReport.setDemotionReport(true);
		demotionReport.setUsingMonthlyVariation(true);
		wRepMap.put(demotionReport.getId().intValue(), demotionReport);

		ReportBean payGroupChange = new ReportBean(11L, "PayGroup Change",
				"/monthVariationPayGroupReport.do");
		payGroupChange.setTemplateName("PayGroup");
		payGroupChange.setSubReportDataSource("");
		payGroupChange.setSubReportTemplateName("");
		payGroupChange.setMVariationTemplateName("");
		payGroupChange.setPaygroupReport(true);
		payGroupChange.setUsingMonthlyVariation(true);
		wRepMap.put(payGroupChange.getId().intValue(), payGroupChange);

		ReportBean loansReport = new ReportBean(12L, "Loans Report",
				"/monthVariationloanReport.do");
		loansReport.setTemplateName("Loans");
		loansReport.setSubReportDataSource("");
		loansReport.setSubReportTemplateName("");
		loansReport.setMVariationTemplateName("");
		loansReport.setLoansReport(true);
		loansReport.setUsingMonthlyVariation(true);
		wRepMap.put(loansReport.getId().intValue(), loansReport);

		ReportBean employeeChange = new ReportBean(12L, bc.getStaffTypeName()+" Change",
				"/monthVariationEmployeeChangeReport.do");
		employeeChange.setTemplateName("EmployeeChange");
		employeeChange.setSubReportDataSource("");
		employeeChange.setSubReportTemplateName("");
		employeeChange.setMVariationTemplateName("");
		employeeChange.setEmployeeChangeReport(true);
		employeeChange.setUsingMonthlyVariation(true);
		wRepMap.put(employeeChange.getId().intValue(), employeeChange);

		ReportBean transferReport = new ReportBean(12L, "Transfer Report",
				"/monthVariationTransferReport.do");
		transferReport.setTemplateName("TransferReport");
		transferReport.setSubReportDataSource("");
		transferReport.setSubReportTemplateName("");
		transferReport.setMVariationTemplateName("");
		transferReport.setTransferReport(true);
		transferReport.setUsingMonthlyVariation(true);
		wRepMap.put(transferReport.getId().intValue(), transferReport);

		ReportBean bankChangeReport = new ReportBean(12L, "Bank Change Report",
				"/monthVariationBankChangeReport.do");
		bankChangeReport.setTemplateName("BankChangeReport");
		bankChangeReport.setSubReportDataSource("");
		bankChangeReport.setSubReportTemplateName("");
		bankChangeReport.setMVariationTemplateName("");
		bankChangeReport.setBankChangeReport(true);
		bankChangeReport.setUsingMonthlyVariation(true);
		wRepMap.put(bankChangeReport.getId().intValue(), bankChangeReport);

		repList.add(promotionReport);
		// repList.add(summary);
		repList.add(reabsorptionReport);
		repList.add(newStaffReport);
		repList.add(deductionReport);
		repList.add(suspensionReport);
		repList.add(reinstatementReport);
		repList.add(specialAllowanceReport);
		repList.add(terminatedStaff);
		repList.add(demotionReport);
		repList.add(payGroupChange);
		repList.add(loansReport);
		repList.add(employeeChange);
		repList.add(transferReport);
		repList.add(bankChangeReport);

		Collections.sort(repList);

		addSessionAttribute(request, "reportList", repList);
		addSessionAttribute(request, "reportMap", wRepMap);
		setUsingVariationReport(true);
//		model = (Model) new VariationReportFormGenerator().generateReports(
//				super.getSession(request), this.payrollService, startDate,
//				model, repList);
		model = (Model) new VariationReportFormGenerator().generateReports(
				request,  employeeService, genericService, startDate,
				model, repList);
		addRoleBeanToModel(model, request);
		model.addAttribute("monthlyVariation", true);
		model.addAttribute("title", "Variation");
		return VIEW;
	}

	
	@RequestMapping(method = { RequestMethod.GET }, params={"sDate"})
	public String setupForm(@RequestParam("sDate") String payPeriod,
			Model model, HttpServletRequest request) throws Exception
	{
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		LocalDate startDate;
		// Date endDate = null;

		if (payPeriod == null) {
			PayrollFlag pf = new PayrollFlag();

			pf = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class,getBusinessClientIdPredicate(request));
			startDate = pf.getPayPeriodStart();
			// endDate = pf.getPayPeriodEnd();
		} else {

			LocalDate gCal = PayrollBeanUtils.setDateFromString(payPeriod);
			startDate = gCal;
			// endDate = pf.getPayPeriodEnd();
		}

		List<ReportBean> repList = (List<ReportBean>) getSessionAttribute(
				request, "reportList");

		model = (Model) new VariationReportFormGenerator().generateReports(
				request, employeeService, this.genericService, startDate,
				model, repList);

		addRoleBeanToModel(model, request);
		model.addAttribute("title", "Variation");
		return VIEW;
	}

	@RequestMapping(method = { RequestMethod.POST })
	public String processSubmit(
			@RequestParam(value = "_update", required = false) String update,
			@RequestParam(value = "_cancel", required = false) String cancel,
			@RequestParam(value = "_load", required = false) String load,
			@ModelAttribute("profileBean") VariationReportBean pHMB,
			BindingResult result, SessionStatus status, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		SessionManagerService.manageSession(request, model);


		if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
			removeSessionAttribute(request, "reportList");
			removeSessionAttribute(request, "reportMap");
			return "redirect:reportsOverview.do";
		}


		if (isButtonTypeClick(request,REQUEST_PARAM_LOAD)) {

			ReportBean toUse = new ReportBean();
			List<ReportBean> selected = new ArrayList<ReportBean>();
			List<ReportBean> rBList = pHMB.getReportsList();
			for (ReportBean r : rBList) {
				if (r.isChecked()) {
					selected.add(r);
				}
			}

			if (selected.size() > 1)

			{

				addSessionAttribute(request,
						SCHOOL_SELECTED_MONTHLY_VARIATION, pHMB);
				addSessionAttribute(request,
						SELECTED_MONTHLY_VARAIATION_OBJECT, selected);
				LocalDate date = pHMB.getFromDate();
				String dateStr = PayrollBeanUtils.getDateAsString(date);
				return "redirect:collatedMonthlyVariationReport.do?sDate="+dateStr;

			} else if (selected.size() == 1) {
				addSessionAttribute(request,
						SCHOOL_SELECTED_MONTHLY_VARIATION, pHMB);
				addSessionAttribute(request,
						SELECTED_MONTHLY_VARAIATION_OBJECT, selected);
//				toUse = selected.get(0);
//				return "redirect:" + toUse.getLink() + "?sDate="
//						+ pHMB.getFromDateStr();
				LocalDate date = pHMB.getFromDate();
				String dateStr = PayrollBeanUtils.getDateAsString(date);
				return "redirect:collatedMonthlyVariationReport.do?sDate="+dateStr;
			}
		}



		if (isButtonTypeClick(request, REQUEST_PARAM_UPD)) {
			int rm = pHMB.getRunMonth();
			int ry = pHMB.getRunYear();

			LocalDate wCal = PayrollBeanUtils.getDateFromMonthAndYear(rm, ry);
			pHMB.setFromDate(wCal);
			LocalDate date = pHMB.getFromDate();
			String dateStr = PayrollBeanUtils.getDateAsString(date);
			return "redirect:monthlyVariation.do?sDate="+dateStr;
		}

		return "redirect:reportsOverview.do";
	}

	public void setUsingReconciliation(boolean usingReconciliation)
	{
		this.usingReconciliation = usingReconciliation;
	}

	public boolean isUsingReconciliation()
	{
		return usingReconciliation;
	}

	public void setUsingVariationReport(boolean usingVariationReport)
	{
		this.usingVariationReport = usingVariationReport;
	}

	public boolean isUsingVariationReport()
	{
		return usingVariationReport;
	}
}