package com.osm.gnl.ippms.ogsg.controllers.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionCategory;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.validators.deduction.CreateDeductionTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Controller
@RequestMapping({ "/createDeductionType.do" })
@SessionAttributes(types = { EmpDeductionType.class })
public class CreateDeductionTypeController extends BaseController {


    private final CreateDeductionTypeValidator validator;

	private final String VIEW_NAME = "deduction/createDeductionTypeForm";
	@Autowired
	public CreateDeductionTypeController(CreateDeductionTypeValidator validator) {
		this.validator = validator;
	}

	@ModelAttribute("deductionCategory")
	public Collection<EmpDeductionCategory> getDeductionCategories() {
		return this.genericService.loadAllObjectsWithoutRestrictions(EmpDeductionCategory.class, "name");

	}


	@ModelAttribute("bankList")
	public Collection<BankInfo> getBankInfo() {
		List<BankInfo> wBankList =  this.genericService.loadAllObjectsWithoutRestrictions(BankInfo.class, "name");
		Collections.sort(wBankList);
		return wBankList;
	}


	@ModelAttribute("payType")
	public Collection<PayTypes> getPayTypesList() {
		return this.genericService.loadAllObjectsWithSingleCondition(PayTypes.class, CustomPredicate.procurePredicate("selectableInd",IConstants.OFF),"name");
	}

	@RequestMapping(method = { RequestMethod.GET })
	public String setupForm(Model model, HttpServletRequest request)
			throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		EmpDeductionType wEDT = new EmpDeductionType();

		wEDT.setTaxable("Y");
		wEDT.setSubTypeEnable("N");
		wEDT.setInherited(false);
		wEDT.setCompanyTypeEnable("N");
		wEDT.setShowForConfirm(HIDE_ROW);
		wEDT.setBankBranches(new BankBranch());
		model.addAttribute("deductionTypeBean", wEDT);
		addRoleBeanToModel(model, request);
		return VIEW_NAME;
	}

	@RequestMapping(method = { RequestMethod.GET }, params = { "cid" })
	public String setupForm(@RequestParam("cid") Long pCatId, Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		EmpDeductionCategory wEDC = this.genericService.loadObjectById(EmpDeductionCategory.class,pCatId);
		EmpDeductionType wEDT = new EmpDeductionType();
		wEDT.setEmpDeductCatRef(wEDC.getId() );
		if(wEDC.isApportionedDeduction()){
            wEDT.setShowApportionsRows(true);
			wEDT.setGroupDeduction(true);
		}else if (wEDC.isStatutoryDeduction()) {
			wEDT.setGroupDeduction(true);
			wEDT.setShowForConfirm(SHOW_ROW);
		} else {
			wEDT.setShowForConfirm(HIDE_ROW);
			wEDT.setGroupDeduction(false);
		}

		wEDT.setEmpDeductionCategory(wEDC);

		wEDT.setTaxable("Y");
		wEDT.setSubTypeEnable("N");
		wEDT.setInherited(false);
		wEDT.setCompanyTypeEnable("N");
		wEDT.setBankBranches(new BankBranch());
		addRoleBeanToModel(model, request);
		model.addAttribute("deductionTypeBean", wEDT);
		return VIEW_NAME;
	}

	@RequestMapping(method = { RequestMethod.GET }, params = { "dtid", "s" })
	public String setupForm(@RequestParam("dtid") Long pDtid, @RequestParam("s") int pSaved, Model model,
			HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		EmpDeductionType wEDT = this.genericService.loadObjectById(EmpDeductionType.class, pDtid);
		wEDT.setEmpDeductCatRef(wEDT.getEmpDeductionCategory().getId());
		if (wEDT.getEmpDeductionCategory().isStatutoryDeduction()) {
			wEDT.setShowForConfirm(SHOW_ROW);
		}else {
			wEDT.setShowForConfirm(HIDE_ROW);
			if(wEDT.getEmpDeductionCategory().isApportionedDeduction())
				wEDT.setShowApportionsRows(true);
		}

		String actionCompleted = "Deduction Type " + wEDT.getDescription() + " Created Successfully";

		model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
		model.addAttribute("saved", Boolean.valueOf(true));
		model.addAttribute("deductionTypeBean", wEDT);
		addRoleBeanToModel(model, request);
		return VIEW_NAME;
	}

	@RequestMapping(method = { RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@ModelAttribute("deductionTypeBean") EmpDeductionType pEHB, BindingResult result, SessionStatus status,
			Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = this.getBusinessCertificate(request);

	 

		if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
			return CONFIG_HOME_URL;
		}


		validator.validate(pEHB, result, bc);
		if (result.hasErrors()) {
			addDisplayErrorsToModel(model, request);
			model.addAttribute("status", result);
			model.addAttribute("bankBranchList", this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id",
					pEHB.getBankInstId()),"name"));
			addRoleBeanToModel(model, request);
			model.addAttribute("deductionTypeBean", pEHB);
			addDisplayErrorsToModel(model, request);
			return VIEW_NAME;
		}


		pEHB.setLastModBy(new User(bc.getLoginId()));
		pEHB.setCreatedBy(new User(bc.getLoginId()));
		pEHB.setLastModTs(Timestamp.from(Instant.now()));
		pEHB.setBusinessClientId(bc.getBusinessClientInstId());
		if (!pEHB.isGroupDeduction()) {
			// Now Add Default PayTypes and Default Bank Branch..
			pEHB.setBankBranches(this.genericService.loadDefaultObject(BankBranch.class, Arrays.asList(CustomPredicate.procurePredicate("defaultInd",IConstants.ON))));
			pEHB.setPayTypes(this.genericService.loadDefaultObject(PayTypes.class, Arrays.asList(CustomPredicate.procurePredicate("defaultInd", IConstants.ON))));
		}else{
			pEHB.setBankBranches(new BankBranch(pEHB.getBranchInstId()));
			pEHB.setPayTypes(new PayTypes(pEHB.getEmpDeductPayTypeRef()));
		}
		this.genericService.saveObject(pEHB);

		return "redirect:createDeductionType.do?dtid=" + pEHB.getId() + "&s=1";

	}
}