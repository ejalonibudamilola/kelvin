package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.EmpContMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/viewEmployeeRemunerationReport.do"})
@SessionAttributes(types={BusinessEmpOVBean.class, EmpContMiniBean.class})
public class EmployeeRenumerationFormController extends BaseController {


	private final PaycheckService paycheckService;

	private final EmployeeService employeeService;

	private final int pageLength = 100;

	private final String VIEW = "employee/viewEmployeeRenumerationForm";
	@Autowired
	public EmployeeRenumerationFormController(PaycheckService paycheckService, EmployeeService employeeService) {
		this.paycheckService = paycheckService;
		this.employeeService = employeeService;
	}

	@ModelAttribute("monthList")
	public List<NamedEntity> getMonthList() {
		return PayrollBeanUtils.makeAllMonthList();
	}

	@ModelAttribute("mdaList")
	protected List<MdaInfo> loadAllMdas(HttpServletRequest request) {
		return this.genericService.loadAllObjectsUsingRestrictions(MdaInfo.class,
				Arrays.asList(CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()),
						CustomPredicate.procurePredicate("mappableInd", 0)), "name");
	}

	@ModelAttribute("yearList")
	public List<NamedEntity> makeYearList(HttpServletRequest request) {

		return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
	}

	@RequestMapping(method = {RequestMethod.GET })
	public String setupForm(Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);

		PayrollFlag wPf = this.genericService.loadObjectWithSingleCondition(PayrollFlag.class,getBusinessClientIdPredicate(request));

		PaginationBean paginationBean = getPaginationInfo(request);

		List<Employee> wRetList = this.employeeService.loadActiveEmployeeByRenumeration(
				(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(),
				wPf.getApprovedMonthInd(), wPf.getApprovedYearInd(), new BusinessEmpOVBean(), bc, false);

		EmpContMiniBean wNoOfElements = this.employeeService.getTotalNumberOfEmployeesByRenumeration(
				wPf.getApprovedMonthInd(), wPf.getApprovedYearInd(), new BusinessEmpOVBean(), bc);
		BusinessEmpOVBean wPELB = new BusinessEmpOVBean(wRetList, paginationBean.getPageNumber(), this.pageLength,
				wNoOfElements.getObjectInd(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());

		wPELB.setRunMonth(wPf.getApprovedMonthInd());
		wPELB.setRunYear(wPf.getApprovedYearInd());

		wPELB.setName(PayrollBeanUtils.getMonthNameFromInteger(wPf.getApprovedMonthInd()) + ", "
				+ wPf.getApprovedYearInd());

		addRoleBeanToModel(model, request);
		model.addAttribute("statBean", wNoOfElements);
		model.addAttribute("miniBean", wPELB);

		return VIEW;
	}

	@RequestMapping(method = {RequestMethod.GET }, params = { "rm", "ry" })
	public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model,
			HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);

		Object wParam = getSessionAttribute(request, CONFIG_BEAN);
		BusinessEmpOVBean wConfigBean = new BusinessEmpOVBean();
		if (wParam != null) {
			wConfigBean = (BusinessEmpOVBean) wParam;
		}

		PaginationBean paginationBean = getPaginationInfo(request);

		List<Employee> wRetList = this.employeeService.loadActiveEmployeeByRenumeration(
				(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), pRunMonth, pRunYear,
				wConfigBean, bc,false);

		EmpContMiniBean wNoOfElements = this.employeeService.getTotalNumberOfEmployeesByRenumeration(pRunMonth,
				pRunYear, wConfigBean, bc);
		BusinessEmpOVBean wPELB = new BusinessEmpOVBean(wRetList, paginationBean.getPageNumber(), this.pageLength,
				wNoOfElements.getObjectInd(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
		wPELB.setName(PayrollBeanUtils.getMonthNameFromInteger(pRunMonth) + ", " + pRunYear);
		wPELB.setStatBean(wNoOfElements);
		wPELB.setRunMonth(pRunMonth);
		wPELB.setRunYear(pRunYear);
		wPELB.setMdaInd(wConfigBean.getMdaInd());
		wPELB.setBankTypeInd(wConfigBean.getBankTypeInd());
		/*
		 * GregorianCalendar wCal = new GregorianCalendar(); wCal.set(pRunYear,
		 * pRunMonth, 1); wPELB.setAllowanceStartDate(wCal.getTime());
		 */
		addRoleBeanToModel(model, request);
		model.addAttribute("statBean", wNoOfElements);
		model.addAttribute("miniBean", wPELB);

		return VIEW;
	}

	@RequestMapping(method = {RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@RequestParam(value = "_updateReport", required = false) String updRep,
			@ModelAttribute("miniBean") BusinessEmpOVBean pLPB,
			@ModelAttribute("statBean") EmpContMiniBean pEmpContMiniBean, BindingResult result, SessionStatus status,
			Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
			removeSessionAttribute(request, CONFIG_BEAN);
			return "redirect:hrEmployeeRelatedReports.do";
		}

		if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
			if ((pLPB.getRunMonth() == -1)) {
				result.rejectValue("", "InvalidValue", "Please select Payroll Month");
				((BusinessEmpOVBean) result.getTarget()).setDisplayErrors("block");

				model.addAttribute("status", result);
				model.addAttribute("statBean", pEmpContMiniBean);
				model.addAttribute("miniBean", pLPB);
				addRoleBeanToModel(model, request);

				return VIEW;
			}
			if ((pLPB.getRunYear() == -1)) {
				result.rejectValue("", "InvalidValue", "Please select Year");
				((BusinessEmpOVBean) result.getTarget()).setDisplayErrors("block");

				model.addAttribute("status", result);
				model.addAttribute("statBean", pEmpContMiniBean);
				model.addAttribute("miniBean", pLPB);
				addRoleBeanToModel(model, request);

				return VIEW;
			}

			result = this.filterCriteriaCheck(pLPB, result);
			if (result.hasErrors()) {

				((BusinessEmpOVBean) result.getTarget()).setDisplayErrors("block");

				model.addAttribute("status", result);
				model.addAttribute("statBean", pEmpContMiniBean);
				model.addAttribute("miniBean", pLPB);
				addRoleBeanToModel(model, request);

				return VIEW;
			}
			addSessionAttribute(request, "er_config", pLPB);
			return "redirect:viewEmployeeRemunerationReport.do?rm=" + pLPB.getRunMonth() + "&ry=" + pLPB.getRunYear();

		}
		return "redirect:viewEmployeeRemunerationReport.do";
	}

	private BindingResult filterCriteriaCheck(BusinessEmpOVBean pLPB, BindingResult pResult) {
		if (pLPB.getAllowanceStartDate() != null) {

			if (pLPB.getAllowanceEndDate() != null) {

				if (pLPB.getAllowanceStartDate().compareTo(pLPB.getAllowanceEndDate()) > 0) {
					pResult.rejectValue("", "", "Service Entry Dates are not valid.");
				}
			}

		}

		return pResult;
	}
}
