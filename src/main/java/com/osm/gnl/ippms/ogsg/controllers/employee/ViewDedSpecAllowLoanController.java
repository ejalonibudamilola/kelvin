package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductContGarnishBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping({ "/viewDedGarnForm.do" })
@SessionAttributes(types = { DeductContGarnishBean.class })
public class ViewDedSpecAllowLoanController extends BaseController {


	private final String VIEW = "deduction/deductionLoanSpecAllowForm";
	public ViewDedSpecAllowLoanController() {
	}

 	@ModelAttribute("payTypeList")
	public Collection<PayTypes> getPayTypesList() {
		return genericService.loadAllObjectsWithoutRestrictions(PayTypes.class,"name");
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "eid", "pid" })
	public String setupForm(@RequestParam("eid") Long empId, @RequestParam("pid") Long pid, Model model,
			HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);
		NamedEntity ne =   new NamedEntity();
			AbstractEmployeeEntity abstractEmployeeEntity = IppmsUtils.loadEmployee(genericService,empId,bc);
			ne.setName(abstractEmployeeEntity.getDisplayNameWivTitlePrefixed());
			ne.setId(abstractEmployeeEntity.getId());
			ne.setStaffId(abstractEmployeeEntity.getEmployeeId());

		DeductContGarnishBean dCGB = buildBean(empId, request);

		dCGB.setId(ne.getId());
		model.addAttribute("namedEntity", ne);
        addRoleBeanToModel(model, request);
		model.addAttribute("dedConGarnBean", dCGB);
		Navigator.getInstance(getSessionId(request)).setFromClass(getClass());
		Navigator.getInstance(getSessionId(request)).setFromForm("redirect:viewDedGarnForm.do?eid=" + dCGB.getId()+"&pid="+bc.getBusinessClientInstId());

		return VIEW;
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@ModelAttribute("dedConGarnBean") DeductContGarnishBean dedGarnBean, BindingResult result,
			SessionStatus status, Model model, HttpServletRequest request)
			throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		return "redirect:employeeOverviewForm.do?eid=" + dedGarnBean.getId() + "&ronly=1";
	}

 	private DeductContGarnishBean buildBean(Long empId, HttpServletRequest request) throws Exception {
		DeductContGarnishBean dCGB = null;
		BusinessCertificate businessCertificate = getBusinessCertificate(request);
		if (IppmsUtils.isNotNullAndGreaterThanZero(empId) && IppmsUtils.isNotNullAndGreaterThanZero(businessCertificate.getBusinessClientInstId())) {
			dCGB = new DeductContGarnishBean();

			List<AbstractDeductionEntity> empDedInfoList = (List<AbstractDeductionEntity>)this.genericService.loadAllObjectsUsingRestrictions( IppmsUtils.getDeductionInfoClass(businessCertificate)
					, Arrays.asList(getBusinessClientIdPredicate(request) ,CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), empId)), "name");

			if (empDedInfoList == null) {
				empDedInfoList = new ArrayList<>();
				dCGB.setMode("c");
			}

			HiringInfo wHInfo =  loadHiringInfoByEmpId(request,businessCertificate,empId);

			List<AbstractGarnishmentEntity> empGarnInfoList = (List<AbstractGarnishmentEntity>)this.genericService.loadAllObjectsUsingRestrictions( IppmsUtils.getGarnishmentInfoClass(businessCertificate)
					, Arrays.asList(getBusinessClientIdPredicate(request) ,CustomPredicate.procurePredicate("employee.id", empId)), "description");

			if (empGarnInfoList == null) {
				empGarnInfoList = new ArrayList<>();
			}

			List<AbstractSpecialAllowanceEntity> wSpecialAllowance = (List<AbstractSpecialAllowanceEntity>) this.genericService.loadAllObjectsUsingRestrictions( IppmsUtils.getSpecialAllowanceInfoClass(businessCertificate)
					, Arrays.asList(getBusinessClientIdPredicate(request) ,CustomPredicate.procurePredicate(businessCertificate.getEmployeeIdJoinStr(), empId),
					CustomPredicate.procurePredicate("expire",OFF), CustomPredicate.procurePredicate("amount", 0, Operation.GREATER)), "name");

			if (wSpecialAllowance == null) {
				wSpecialAllowance = new ArrayList<>();
			} else {
				for (AbstractSpecialAllowanceEntity s : wSpecialAllowance) {
					if (s.isExpired())
						s.setDescription(s.getDescription() + "**");
					s.setDisplayTitle("Expired");
				}
			}
			if(!businessCertificate.isPensioner()) {
				if (wHInfo.isPensionableEmployee() &&
						!wHInfo.isContractStaff()) {
					ConfigurationBean configurationBean = loadConfigurationBean(request);
					if (!PayrollBeanUtils.isTPSEmployee(wHInfo.getBirthDate(),
							wHInfo.getHireDate(), wHInfo.getExpectedDateOfRetirement(),
							PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_HIRE_DATE_STR),
							PayrollBeanUtils.getLocalDateFromString(IConstants.TPS_EXP_RETIRE_DATE_STR
							),configurationBean,businessCertificate)) {
						dCGB.setPensionName("Contributory Pension");
						SalaryInfo wSalInfo =
								wHInfo.getEmployee().getSalaryInfo();
						if
						(wSalInfo.getSalaryType().isBasicRentTransportType()) {
							dCGB.setContributionPayType("% of B,R&T");
							dCGB.setContributionAmountStr("7.5% of (Basic + Rent + Transport)");
							double 	amount = EntityUtils.convertDoubleToEpmStandard(((wSalInfo.getMonthlyBasicSalary() + wSalInfo.getRent() +
									wSalInfo.getTransport()) / 12.0D) * 0.075D);

							dCGB.setContributionAmount(PayrollHRUtils.getDecimalFormat().format(amount));
						} else {
							dCGB.setContributionPayType("% of B,R&MV");
							dCGB.setContributionAmountStr("7.5% of (Basic + Rent + Motor Vehicle)");
							double amount = EntityUtils.convertDoubleToEpmStandard(((wSalInfo.getMonthlyBasicSalary() + wSalInfo.getRent() +
									wSalInfo.getMotorVehicle()) / 12.0D) * 0.075D);
							dCGB.setContributionAmount(PayrollHRUtils.getDecimalFormat().format(amount));

						}
						dCGB.setShowContributionRow(true);
					}
				}
			}
			dCGB.addSpecialAllowanceInfo(wSpecialAllowance);
			dCGB.addEmpDeductionInfo(empDedInfoList);
			dCGB.addEmpGarnishInfo(empGarnInfoList);
			dCGB.setHiringInfo(wHInfo);

		}

		if (dCGB == null) {
			throw new Exception("Deduction Garnishment Bean is null");
		}
		return dCGB;
	}
}