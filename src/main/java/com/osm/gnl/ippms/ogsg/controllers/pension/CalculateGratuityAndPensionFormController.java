/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.pension;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.employee.beans.NewPensionerBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollGratuityUtil;
import com.osm.gnl.ippms.ogsg.validators.pension.CreateGratuityAndPensionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/calcPensionAndGratuity.do")
@SessionAttributes(types = NewPensionerBean.class)
public class CalculateGratuityAndPensionFormController extends BaseController {


	private final CreateGratuityAndPensionValidator validator;
	private final PensionService pensionService;
    private final String VIEW = "gratuity/createGratuityForm";
	@Autowired
	public CalculateGratuityAndPensionFormController(CreateGratuityAndPensionValidator validator, PensionService pensionService){
		this.validator = validator;
		this.pensionService = pensionService;
	}



	@RequestMapping(method = RequestMethod.GET, params = { "eid" })
	public String setupForm(@RequestParam("eid") Long pHid, Model model,
			HttpServletRequest request) throws Exception {

		SessionManagerService.manageSession(request);
		BusinessCertificate bc = getBusinessCertificate(request);
		if(!bc.isPensioner()){
			throw new Exception ("Access Denied");
		}
		NewPensionerBean wNPB = new NewPensionerBean();
		// First we need to get the HiringInfo object...
		HiringInfo wHI =  genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pHid)));

		if(wHI.getGratuityAmount() > 0 || wHI.getYearlyPensionAmount() > 0)
			return "redirect:calcPensionAndGratuity.do?hid="+wHI.getId()+"&rind=1";
		//Per adventure the parent client is missing from the Business Client.
		if(IppmsUtils.isNullOrLessThanOne(bc.getParentClientId())){
			bc.setParentClientId(wHI.getPensioner().getParentBusinessClientId());
			//--For Posterity, add it to the Business Client.
			addSessionAttribute(request, IppmsEncoder.getCertificateKey(),bc);
		}

		String string = "select s.id, s.name from SalaryType s where s.businessClientId = :pBizIdVar order by s.name";
		//List<NamedEntity> wSTList = genericService.loadControlObjectByHqlStr(bc,string,true);

		wNPB.setCalculateGratuityInd(IConstants.ON);
		wNPB.setHiringInfo(wHI);
		wNPB.setSalaryTypeId(wHI.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getId());
		wNPB.setSalaryInfoId(wHI.getAbstractEmployeeEntity().getSalaryInfo().getId());
		//wNPB.setSalaryTypeList(wSTList);
		//wNPB.setLevelAndStepList(new ArrayList<>());

		wNPB.setRoleBean(bc);
		model = setSalaryInfoList(model,wNPB);
		addRoleBeanToModel(model,request);
		model.addAttribute("gratBean", wNPB);

		return VIEW;
	}

	@RequestMapping(method = RequestMethod.GET, params = { "hid", "rind" })
	public String setupForm(@RequestParam("hid") Long pHid,
			@RequestParam("rind") int pRecalcInd, Model model,
			HttpServletRequest request) throws Exception {

		SessionManagerService.manageSession(request);
		NewPensionerBean wNPB = new NewPensionerBean();
		BusinessCertificate bc = getBusinessCertificate(request);
		if(!bc.isPensioner()){
			throw new Exception ("Access Denied");
		}
		// First we need to get the HiringInfo object...
		HiringInfo wHI =  genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pHid)));
		if(IppmsUtils.isNullOrLessThanOne(bc.getParentClientId())){
			bc.setParentClientId(wHI.getPensioner().getParentBusinessClientId());
			//--For Posterity, add it to the Business Client.
			addSessionAttribute(request, IppmsEncoder.getCertificateKey(),bc);
		}
		//List<NamedEntity> wSTList = genericService.loadControlObjectByHqlStr(bc,string,true);

		//wNPB.setSalaryTypeList(wSTList);
		//wNPB.setLevelAndStepList(new ArrayList<>());
		wNPB.setCalculateGratuityInd(IConstants.OFF);
		wNPB.setCalculatePensionInd(IConstants.OFF);
		wNPB.setRecalculation(true);
		wNPB.setHiringInfo(wHI);
		wNPB.setSalaryTypeId(wHI.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getId());
		wNPB.setSalaryInfoId(wHI.getAbstractEmployeeEntity().getSalaryInfo().getId());
		//wNPB.setSalaryTypeList(wSTList);
		//wNPB.setLevelAndStepList(new ArrayList<>());
		wNPB.setRoleBean(bc);
		model = setSalaryInfoList(model,wNPB);
		addRoleBeanToModel(model,request);
		model.addAttribute("gratBean", wNPB);

		return VIEW;
	}


	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(
			@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,

			@ModelAttribute("gratBean") NewPensionerBean pHMB,
			BindingResult result, SessionStatus status, Model model,
			HttpServletRequest request) throws Exception {

		SessionManagerService.manageSession(request);
		BusinessCertificate bc = super.getBusinessCertificate(request);

		// This is only possible with Super Admin for now....
		if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
			// Go to the home Page.
			if (!pHMB.isRecalculation() && pHMB.getHiringInfo().isNewEntity())
				return "redirect:penPaymentInfoForm.do?oid="+pHMB.getHiringInfo().getParentId();
			else
				return "redirect:pensionerOverviewForm.do?eid="
						+ pHMB.getHiringInfo().getPensioner().getId();

		}
		PredicateBuilder predicateBuilder = new PredicateBuilder();
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("status", "P"));
		predicateBuilder.addPredicate(getBusinessClientIdPredicate(request));
		if (this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(bc)) > 0) {

			result.rejectValue(
					"",
					"Pending.Paychecks",
					"Pending Paychecks found. Pension and Gratuity Recalculation can only be effected after a Payroll Run has been approved.");


//			if (pHMB.getSalaryTypeId() > 0) {
//				pHMB.setLevelAndStepList(getSalaryInfoList(pHMB.getSalaryTypeId(), bc));
//			}
			addRoleBeanToModel(model, request);
			addDisplayErrorsToModel(model, request);
			model = setSalaryInfoList(model,pHMB);
			model.addAttribute("status", result);
			model.addAttribute("gratBean", pHMB);
			return VIEW;
		}
		// Check for Errors....
		validator.validate(pHMB, result);
		if (result.hasErrors()) {

//			if (pHMB.getSalaryTypeId() > 0) {
//				pHMB.setLevelAndStepList(getSalaryInfoList(pHMB.getSalaryTypeId(), bc));
//
//			}
			addRoleBeanToModel(model, request);
			addDisplayErrorsToModel(model, request);

			model = setSalaryInfoList(model,pHMB);
			model.addAttribute("status", result);
			model.addAttribute("gratBean", pHMB);
			return VIEW;
		}
		if(pHMB.isConsolidatedIndBind()){
			pHMB.setConsolidatedIndStr("true");
		}else{
			pHMB.setConsolidatedIndStr("false");
		}
		// Other wise...check if we have confirmation...
		if (!pHMB.isConfirmation()) {
			pHMB.setConfirmation(!pHMB.isConfirmation());
			pHMB.setLockAll(true);
			pHMB.setSalaryInfo(genericService.loadObjectById(SalaryInfo.class, pHMB.getSalaryInfoId()));
			pHMB.setTotalLengthOfService(pHMB.getHiringInfo()
					.getTotalLenOfServInMonths());
			pHMB = PayrollGratuityUtil.generateGatuityAndPension(pHMB, this.pensionService, bc);
			if (pHMB.isCalculateGratuity() && pHMB.isCalculatePensions())
				result.rejectValue("", "Confirmation.Required",
						"Please Confirm this Gratuity and Pension Calculation.");
			else if (pHMB.isCalculateGratuity() && !pHMB.isCalculatePensions())
				result.rejectValue("", "Confirmation.Required",
						"Please Confirm this Gratuity Calculation.");
			else if (!pHMB.isCalculateGratuity() && pHMB.isCalculatePensions())
				result.rejectValue("", "Confirmation.Required",
						"Please Confirm this Pension Calculation.");

			addRoleBeanToModel(model, request);
//			if (pHMB.getSalaryTypeId() > 0) {
//				pHMB.setLevelAndStepList(getSalaryInfoList(pHMB.getSalaryTypeId(),bc));
//
//			}
			addDisplayErrorsToModel(model, request);
			model = setSalaryInfoList(model,pHMB);
			model.addAttribute("status", result);
			model.addAttribute("gratBean", pHMB);
			return VIEW;
		}
		// If Confirmed. Now Save the Values....
		if (pHMB.isCalculateGratuity()
				&& pHMB.getHiringInfo().getGratuityAmount() > 0.0D) {
			// Make sure a date is put in there...
			if (pHMB.getGratuityEffectiveDate() == null) {
				result.rejectValue("", "Confirmation.Required",
						"Please Enter a value for Gratuity Effective Date.");

//				if (pHMB.getSalaryTypeId() > 0) {
//					pHMB.setLevelAndStepList(getSalaryInfoList(pHMB
//									.getSalaryTypeId(), bc));
//				}
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model = setSalaryInfoList(model,pHMB);
				model.addAttribute("status", result);
				model.addAttribute("gratBean", pHMB);
				return VIEW;
			} else {
				// TODO Add - Logic here to test Recalculation Logic.
			}
		}
		HiringInfo wHireInfo = pHMB.getHiringInfo();

		wHireInfo.setLastModBy(new User(bc.getLoginId()));
		wHireInfo.setLastModTs(Timestamp.from(Instant.now()));
		this.genericService.saveObject(wHireInfo);

		// Now Create the Gratuity Object if Gratuity is greater than 0.
		if (pHMB.isCalculateGratuity() && wHireInfo.getGratuityAmount() > 0) {
			// Now See if this dude has an existing Gratuity Info....
			GratuityInfo wGI = this.genericService.loadObjectUsingRestriction(GratuityInfo.class,Arrays.asList(CustomPredicate.procurePredicate("pensioner.id", wHireInfo.getPensioner().getId()),
					getBusinessClientIdPredicate(request)));
			if (wGI.isNewEntity()) {
				wGI.setPensioner(wHireInfo.getPensioner());
				wGI.setMonth( pHMB
						.getGratuityEffectiveDate().getMonthValue());
				wGI.setYear(pHMB.getGratuityEffectiveDate().getYear());
			}
			wGI.setBusinessClientId(bc.getBusinessClientInstId());
			wGI.setGratuityAmount(wHireInfo.getGratuityAmount());
			wGI.setOutstandingAmount(wHireInfo.getGratuityAmount());
			wGI.setLastModBy(new User(bc.getLoginId()));
			wGI.setLastModTs(Timestamp.from(Instant.now()));
			wGI.setCreatedBy(new User(bc.getLoginId()));
			wGI.setCreationDate(Timestamp.from(Instant.now()));
			this.genericService.saveObject(wGI);

		}

		if (!pHMB.isRecalculation())
			return "redirect:penPaymentInfoForm.do?oid="+pHMB.getHiringInfo().getParentId();
		else
			return "redirect:pensionerOverviewForm.do?eid="
					+ pHMB.getHiringInfo().getParentId();

	}
	private Model setSalaryInfoList(Model model,NewPensionerBean bean) {
		if(bean.isConsolidatedIndBind()){
			bean.setConsolidatedIndStr("true");
		}else{
			bean.setConsolidatedIndStr("false");
		}
		ArrayList<SalaryInfo> salaryInfos = new ArrayList<>();
		salaryInfos.add(bean.getHiringInfo().getPensioner().getSalaryInfo());
		ArrayList<SalaryType> salaryTypes = new ArrayList<>();
		salaryTypes.add(bean.getHiringInfo().getPensioner().getSalaryInfo().getSalaryType());
		model.addAttribute("salaryTypes", salaryTypes);
		model.addAttribute("levelAndSteps", salaryInfos);
		return model;

	}

	private List<SalaryInfo> getSalaryInfoList(Long pSalaryTypeId, BusinessCertificate businessCertificate){
		List<SalaryInfo> wSalaryInfoList = this.genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class,
				Arrays.asList(CustomPredicate.procurePredicate("salaryType.id", pSalaryTypeId),CustomPredicate.procurePredicate("businessClientId", businessCertificate.getParentClientId())), null);
		Comparator<SalaryInfo> comparator = Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep);
		wSalaryInfoList.stream().sorted(comparator);
		return wSalaryInfoList;
	}
}
