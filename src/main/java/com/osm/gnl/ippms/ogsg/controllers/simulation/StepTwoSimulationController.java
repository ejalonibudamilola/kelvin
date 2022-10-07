package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationMasterBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.DevelopmentLevy;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.simulation.SimulationStep2Validator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;


@Controller
@RequestMapping({ "/stepTwoPayrollSimulator.do" })
@SessionAttributes(types = { SimulationBeanHolder.class })
public class StepTwoSimulationController extends BaseController {

	private final String VIEW = "simulation/simulatePayrollStep2Form";

	@Autowired
	private SimulationStep2Validator validator;

	public StepTwoSimulationController() {
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "uid", "mode" })
	public String setupForm(@RequestParam("uid") String pUid, @RequestParam("mode") String pMode, Model model,
			HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		if (!bc.getSessionId().toString().equalsIgnoreCase(pUid)) {
			return "redirect:prePayrollSimulator.do";
		}
		SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);

		wLTGH = createViewVariables(wLTGH);
		wLTGH.setDisplayErrors("none");

		addSessionAttribute(request, SIM_ATTR, wLTGH);

		model.addAttribute("simulationBean2", wLTGH);

		return VIEW;
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "uid" })
	public String setupForm(@RequestParam("uid") String pUid, Model model, HttpServletRequest request)
			throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		if (!bc.getSessionId().toString().equalsIgnoreCase(pUid)) {
			return "redirect:prePayrollSimulator.do";
		}
		SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);

		wLTGH = setDevLevyDeductMonths(wLTGH, bc);

		addSessionAttribute(request, SIM_ATTR, wLTGH);

		model.addAttribute("simulationBean2", wLTGH);

		return VIEW;
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@RequestParam(value = "_next", required = false) String pContinue,
			@ModelAttribute("simulationBean") SimulationBeanHolder pHMB, BindingResult result, SessionStatus status,
			Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

		if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
			return "redirect:prePayrollSimulator.do";
		}

		if (isButtonTypeClick(request, REQUEST_PARAM_NEXT)) {
			validator.validate(pHMB, result);
			if (result.hasErrors()) {
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model.addAttribute("status", result);
				model.addAttribute("simulationBean2", pHMB);

				return VIEW;
			}

			pHMB = setFormBackingData(pHMB);
			if (Boolean.valueOf(pHMB.getApplyLtgInd()).booleanValue()) {
				addSessionAttribute(request, SIM_ATTR, pHMB);
				return "redirect:step3PayrollSimulator.do?uid=" + bc.getUserName();
			}

			createAndStorePayrollSimulationDetails(pHMB, bc, request);
			return "redirect:simulationPreviewForm.do?uid=" + bc.getUserName();
		}

		return "redirect:prePayrollSimulator.do";
	}

	private SimulationBeanHolder createViewVariables(SimulationBeanHolder pLTGH) {
		PayrollSimulationMasterBean wPSMB = pLTGH.getPayrollSimulationMasterBean();

		if (wPSMB.isCanApplyLtg()) {
			pLTGH.setApplyLtgInd("true");
		}
		if (wPSMB.isIncludePromotionsChecked()) {
			pLTGH.setIncludePromotionInd("true");
		}
		if (wPSMB.isCanDeductBasYearDevLevy()) {
			pLTGH.setDeductBaseYearDevLevy("true");
		}
		if (wPSMB.isCanDeductSpillOverYearDevLevy()) {
			pLTGH.setDeductSpillOverYearDevLevy("true");
		}
		if (wPSMB.isCanDoStepIncrement()) {
			pLTGH.setStepIncrementInd("true");
		}

		return pLTGH;
	}

	private SimulationBeanHolder setFormBackingData(SimulationBeanHolder pHMB) {
		PayrollSimulationMasterBean wPSMB = pHMB.getPayrollSimulationMasterBean();
		if (Boolean.valueOf(pHMB.getApplyLtgInd()).booleanValue())
			wPSMB.setApplyLtgInd(1);
		else {
			wPSMB.setApplyLtgInd(0);
		}

		if (Boolean.valueOf(pHMB.getDeductBaseYearDevLevy()).booleanValue())
			wPSMB.setDeductBaseYearDevLevyInd(1);
		else {
			wPSMB.setDeductBaseYearDevLevyInd(0);
		}
		if (Boolean.valueOf(pHMB.getDeductSpillOverYearDevLevy()).booleanValue()) {
			wPSMB.setDeductSpillOverYearDevLevy(1);
		} else {
			wPSMB.setDeductSpillOverYearDevLevy(0);
		}
		if ((StringUtils.trimToEmpty(pHMB.getStepIncrementInd()) != "")
				&& (Boolean.valueOf(pHMB.getStepIncrementInd()).booleanValue()))
			wPSMB.setStepIncrementInd(1);
		else {
			wPSMB.setStepIncrementInd(0);
		}
		pHMB.setPayrollSimulationMasterBean(wPSMB);
		return pHMB;
	}

	private void createAndStorePayrollSimulationDetails(SimulationBeanHolder pHMB, BusinessCertificate pBc,
			HttpServletRequest pRequest) throws Exception {
		PayrollSimulationMasterBean wPSMB = pHMB.getPayrollSimulationMasterBean();

		PayrollSimulationDetailsBean wPSDB = new PayrollSimulationDetailsBean();
		try {
			wPSDB = this.genericService
					.loadObjectById(PayrollSimulationDetailsBean.class, wPSMB.getId());
			if (wPSDB.isNewEntity())
				wPSDB.setId(wPSMB.getId());
		} catch (Exception wEx) {
			wPSDB = new PayrollSimulationDetailsBean();
			wPSDB.setId(wPSMB.getId());
			wEx.printStackTrace();
		}

		if (wPSMB.isIncludePromotionsChecked())
			wPSDB.setPromotionsInd(1);
		else {
			wPSDB.setPromotionsInd(0);
		}
		if (Boolean.valueOf(pHMB.getDeductBaseYearDevLevy()).booleanValue()) {
			wPSDB.setDevLevyInd1(1);
			wPSDB.setDeductDevLevyMonth1(pHMB.getBaseYearDevLevyInd());
			wPSMB.setDeductBaseYearDevLevyInd(1);
		} else {
			wPSDB.setDevLevyInd1(0);
			wPSDB.setDeductDevLevyMonth1(0);
			wPSMB.setDeductBaseYearDevLevyInd(0);
		}
		if (Boolean.valueOf(pHMB.getDeductSpillOverYearDevLevy()).booleanValue()) {
			wPSDB.setDevLevyInd2(1);
			wPSDB.setDeductDevLevyMonth2(pHMB.getSpillYearDevLevyInd());
			wPSMB.setDeductSpillOverYearDevLevy(1);
		} else {
			wPSDB.setDevLevyInd2(0);
			wPSDB.setDeductDevLevyMonth2(0);
			wPSMB.setDeductSpillOverYearDevLevy(0);
		}
		if ((StringUtils.trimToEmpty(pHMB.getStepIncrementInd()) != "")
				&& (Boolean.valueOf(pHMB.getStepIncrementInd()).booleanValue())) {
			wPSDB.setStepIncrement(1);
			wPSMB.setStepIncrementInd(1);
		} else {
			wPSDB.setStepIncrement(0);
			wPSMB.setStepIncrementInd(0);
		}

		this.genericService.saveObject(wPSDB);
		this.genericService.saveObject(wPSMB);
		pHMB.setPayrollSimulationMasterBean(wPSMB);
		addSessionAttribute(pRequest, SIM_ATTR, pHMB);
	}

	private SimulationBeanHolder setDevLevyDeductMonths(SimulationBeanHolder pLTGH, BusinessCertificate bc) {
		pLTGH.setStartMonthName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pLTGH.getStartMonthInd(),
				pLTGH.getEffectiveYear()));
		pLTGH.setSimulationLength(PayrollBeanUtils.getSimulationLengthInMonths(pLTGH.getNoOfMonthsInd()));
		LocalDate localDate = LocalDate.now();
		PredicateBuilder predicateBuilder = new PredicateBuilder();
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("year",localDate.getYear()));

		if (!pLTGH.getPayrollSimulationMasterBean().isCrossesOver()) {
			if (!this.genericService.isObjectExisting(DevelopmentLevy.class,predicateBuilder.getPredicates())) {
				pLTGH.setCanDeductBaseYearDevLevy(true);

				ArrayList<NamedEntity> wMonthList = new ArrayList<>();

				if (localDate.getYear() < pLTGH.getEffectiveYear()) {
					localDate = LocalDate.now().plusYears(1L);
				}
				int wMax = pLTGH.getNoOfMonthsInd();

				if (pLTGH.getStartMonthInd() == 12)
					wMax = 12;
				else if (pLTGH.getStartMonthInd() + pLTGH.getNoOfMonthsInd() >= 12)
					wMax = 12;
				else {
					wMax = pLTGH.getStartMonthInd() + pLTGH.getNoOfMonthsInd();
				}
				for (int wStart = pLTGH.getMonthId(); wStart <= wMax; wStart++) {
					NamedEntity n = new NamedEntity();
					n.setId(new Long(wStart));
					if (wStart == 0)
						pLTGH.setCanDoStepIncrement(true);
					n.setName(localDate.getMonth().getDisplayName(TextStyle.FULL,Locale.UK));
					localDate = localDate.plusMonths(1L);
					wMonthList.add(n);
				}
				pLTGH.setBaseMonthList(wMonthList);
			}

		} else {
			if (!this.genericService.isObjectExisting(DevelopmentLevy.class,predicateBuilder.getPredicates())) {
				pLTGH.setCanDeductBaseYearDevLevy(true);

				ArrayList<NamedEntity> wMonthList = new ArrayList<NamedEntity>();

				int wYear = localDate.getYear();

				for (int wStart = pLTGH.getStartMonthInd(); (wStart <= 12) && (localDate.getYear() == wYear); wStart++) {
					NamedEntity n = new NamedEntity();
					n.setId(new Long(wStart));
					if (wStart == 1)
						pLTGH.setCanDoStepIncrement(true);
					localDate = LocalDate.of(wYear,wStart,1);
					n.setName(localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK));

					wMonthList.add(n);
				}
				pLTGH.setBaseMonthList(wMonthList);
			}

			pLTGH.setCanDeductSpillOverYearDevLevy(true);

			LocalDate wYear2 = LocalDate.now();

			wYear2 = wYear2.plusYears(1L);

			ArrayList<NamedEntity> wMonthList = new ArrayList<>();

			if (!pLTGH.isStepIncreaseInd()) {
				pLTGH.setStepIncreaseInd(true);
			}

			int wEndMonth = pLTGH.getStartMonthInd() + pLTGH.getNoOfMonthsInd() - 12;

			for (int wStart = 1; wStart < wEndMonth; wStart++) {
				NamedEntity n = new NamedEntity();
				n.setId(new Long(wStart));
				wYear2 = LocalDate.of(wYear2.getYear(),wStart,1);
				n.setName(wYear2.getMonth().getDisplayName(TextStyle.FULL,Locale.UK));
				wMonthList.add(n);
			}
			pLTGH.setSpillOverMonthList(wMonthList);
		}

		return pLTGH;
	}
}