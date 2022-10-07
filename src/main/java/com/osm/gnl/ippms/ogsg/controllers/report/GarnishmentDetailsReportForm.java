package com.osm.gnl.ippms.ogsg.controllers.report;

import java.time.LocalDate;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.GarnishmentService;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
@Controller
@RequestMapping({ "/otherGarnishmentDetailsReport.do" })
@SessionAttributes(types = { DeductionDetailsBean.class })
public class GarnishmentDetailsReportForm extends BaseController {


	private final PaycheckService paycheckService;
	private final GarnishmentService garnishmentService;


	@ModelAttribute("monthList")
	protected List<NamedEntity> getMonthsList() {
		return PayrollBeanUtils.makeAllMonthList();
	}

	@ModelAttribute("yearList")
	protected Collection<NamedEntity> getYearList(HttpServletRequest request) {

		return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
	}
	@Autowired
	public GarnishmentDetailsReportForm(PaycheckService paycheckService, GarnishmentService garnishmentService) {
		this.paycheckService = paycheckService;
		this.garnishmentService = garnishmentService;
	}

	@RequestMapping(method = { RequestMethod.GET }, params = { "did", "rm", "ry", "pid" })
	public String setupForm(@RequestParam("did") Long dedTypeId, @RequestParam("rm") int pRunMonth,
			@RequestParam("ry") int pRunYear, @RequestParam("pid") Long pid, Model model, HttpServletRequest request)
			throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		boolean filter = false;
		if (IppmsUtils.isNotNullAndGreaterThanZero(dedTypeId)) {
			filter = true;
		}
		DeductionDetailsBean deductDetails = new DeductionDetailsBean();
		HashMap<Long, DeductGarnMiniBean> deductionBean = new HashMap<Long, DeductGarnMiniBean>();

		LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
		LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
		String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
		String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);

		List<AbstractPaycheckGarnishmentEntity> paycheckDeductions;


		deductDetails.setFromDate(sDate);
		deductDetails.setToDate(eDate);
		if (filter) {
			deductDetails.setCurrentDeduction(this.genericService.loadObjectUsingRestriction(
					EmpGarnishmentType.class, Arrays.asList(CustomPredicate.procurePredicate("id", dedTypeId),
							CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()))).getDescription());
		}

		deductDetails.setId(pid);
		deductDetails.setFromDateStr(pSDate);
		deductDetails.setToDateStr(pEDate);
		deductDetails.setRunMonth(pRunMonth);
		deductDetails.setRunYear(pRunYear);

		paycheckDeductions = this.garnishmentService.loadEmpGarnishmentsByParentIdAndPayPeriod(dedTypeId, pRunMonth, pRunYear, bc);

		DeductGarnMiniBean d = null;

		for (AbstractPaycheckGarnishmentEntity p : paycheckDeductions) {
			if ((filter) && (!p.getEmpGarnInfo().getEmpGarnishmentType().getId().equals((dedTypeId)))) {
				continue;
			}

			if (deductionBean.containsKey(p.getEmployee().getId())) {
				d = deductionBean.get(p.getEmployee().getId());

			} else {
				d = new DeductGarnMiniBean();
				d.setName(PayrollHRUtils.createDisplayName(p.getEmployee().getLastName(),
						p.getEmployee().getFirstName(), p.getEmployee().getInitials()));
				d.setId(p.getEmployee().getId());
				d.setEmployeeId(p.getEmployee().getEmployeeId());
				d.setMdaName(p.getMdaName());

			}
			d.setDeductionId(p.getEmpGarnInfo().getId());
			d.setAmount(d.getAmount() + p.getAmount());
			d.setBalanceAmount(p.getEmpGarnInfo().getOwedAmount());
			deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
			deductDetails.setTotalBalance(deductDetails.getTotalBalance() + d.getBalanceAmount());
			deductionBean.put(p.getEmployee().getId(), d);
		}
		deductDetails.setNoOfEmployees(this.garnishmentService.getNoOfEmployeesWithLoanDeductions(dedTypeId, pRunMonth, pRunYear, bc));

		deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
		deductDetails.setDeductionId(dedTypeId);

		List <EmpGarnishmentType>garnishmentListFiltered = this.garnishmentService.findEmpGarnishmentsByBusinessClient(bc);

		Collections.sort(garnishmentListFiltered);
		deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
		Collections.sort(deductDetails.getDeductionMiniBean());
		deductDetails.setPageSize(deductDetails.getDeductionMiniBean().size());
		deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(deductDetails.getTotalCurrentDeduction()));
		deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
		 
		model.addAttribute("garnishmentList", garnishmentListFiltered);
		model.addAttribute("garnishmentDetails", deductDetails);
		addRoleBeanToModel(model, request);

		return "garnishmentDetailsForm";
	}

	@RequestMapping(method = { RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep,
			@RequestParam(value = "_go", required = false) String go,
			@RequestParam(value = "_cancel", required = false) String cancel,
			@ModelAttribute("garnishmentDetails") DeductionDetailsBean pDDB, BindingResult result, SessionStatus status,
			Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {

			return "redirect:reportsOverview.do";
		}

		if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
			int rm = pDDB.getRunMonth();
			int ry = pDDB.getRunYear();
			/*
			 * String sDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getFromDate());
			 * String eDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getToDate());
			 */
			if (pDDB.getDeductionId() > 0) {
				return "redirect:otherGarnishmentDetailsReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry="
						+ ry + "&pid=" + pDDB.getId();
			}
			return "redirect:allOtherGarnishmentReport.do?did=" + pDDB.getDeductionId() + "&rm=" + rm + "&ry=" + ry
					+ "&pid=" + pDDB.getId();
		}

		if ("go".equalsIgnoreCase(go)) {
			return "redirect:" + pDDB.getSubLinkSelect() + ".do";
		}

		return "redirect:allOtherGarnishmentReport.do";
	}

}