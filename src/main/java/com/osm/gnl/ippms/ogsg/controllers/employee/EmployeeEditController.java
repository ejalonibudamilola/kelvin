package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.*;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.employee.EmployeeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({ "/editEmployeeForm.do" })
@SessionAttributes({ "employee" })
public class EmployeeEditController extends BaseController {

	private final String VIEW_NAME =  "employee/employeeEditForm";

	private final IMenuService menuService;
    private final EmployeeValidator validator;

	@Autowired
	public EmployeeEditController(IMenuService menuService, EmployeeValidator validator) {

		this.menuService = menuService;
		this.validator = validator;
	}

	@ModelAttribute(value = "emptypes")
	public List<EmployeeType> populateEmployeeType(HttpServletRequest httpServletRequest) {

		return this.genericService.loadAllObjectsWithSingleCondition(EmployeeType.class,
				super.getBusinessClientIdPredicate(httpServletRequest), "name");
	}

	@ModelAttribute(value = "rankTypes")
	public List<Rank> populateRankType(HttpServletRequest httpServletRequest) {

		return this.genericService.loadAllObjectsWithSingleCondition(Rank.class,
				super.getBusinessClientIdPredicate(httpServletRequest), "name");
	}
	@ModelAttribute("religionList")
	public List<Religion> populateReligionList() {
		return this.genericService.loadAllObjectsWithoutRestrictions(Religion.class, "name");
	}


	@ModelAttribute("titleList")
	public List<Title> populateTitleList() {
		return this.genericService.loadAllObjectsWithoutRestrictions(Title.class, "name");
	}


	@ModelAttribute("statesOfOriginList")
	public List<State> populateStateOfOriginList() {
		return this.genericService.loadAllObjectsWithoutRestrictions(State.class, "name");
	}

	@ModelAttribute("cities")
	public List<City> populateCityList() {
		return this.genericService.loadAllObjectsWithoutRestrictions(City.class, "name");
	}
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String setupForm(Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = this.getBusinessCertificate(request);
		// Object userId = super.getSessionId(request);
		NamedEntity wNE = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
		 return "redirect:editEmployeeForm.do?oid="+wNE.getId();
	}

	
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "oid" })
	public String setupForm(@RequestParam("oid") Long eid, Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = this.getBusinessCertificate(request);

		AbstractEmployeeEntity employee = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(
				CustomPredicate.procurePredicate("id",eid), this.getBusinessClientIdPredicate(request)));
		if(employee.isNewEntity())
			return "redirect:employeeForm.do";

		employee.setCanEdit(employee.isTerminated());
		NamedEntity nE = new NamedEntity();
		nE.setId(eid);
		nE.setName(employee.getDisplayNameWivTitlePrefixed());
		addSessionAttribute(request, NAMED_ENTITY, nE);
		if (employee.isTerminated() && !employee.isPensioner()) {
			employee.setCanEdit(true);
			if(menuService.canUserAccessURL(bc, "/createEmployeeContract.do", "/createEmployeeContract.do")) {
				employee = this.setContractStatus(employee);
			}
		}else {
			employee.setCanEdit(false);
		}
		if(employee.isPensioner()){
			//Load the HiringInfo for Display Purposes
			HiringInfo hiringInfo = loadHiringInfoByEmpId(request,bc,eid);
			hiringInfo.setGratuityAmountStr(PayrollHRUtils.getDecimalFormat().format(hiringInfo.getGratuityAmount()));
			employee.setHiringInfo(hiringInfo);
		}
		//--Check if residence ID is set.

		 employee.setOpenResidenceId(IppmsUtils.isNullOrEmpty(employee.getResidenceId()));

	 	employee.setTitleId(employee.getTitle().getId());
		employee.setLgaId(employee.getLgaInfo().getId());
		employee.setRelId(employee.getReligion().getId());
		if ((employee.getSchoolInfo() != null) && (!employee.getSchoolInfo().isNewEntity())) {
			employee.setSchoolInstId(employee.getSchoolInfo().getId());
		}
		//Due to Migration, State Of Origin might be null
		employee.setStateInstId(employee.getCity().getState().getId());
		employee.setCityId(employee.getCity().getId());
		//Due to Migration, State Of Origin might be null
		if(employee.getStateOfOrigin() != null)
		   employee.setStateOfOriginId(employee.getStateOfOrigin().getId());
		List<State> states = new ArrayList<>();
		states.add(employee.getCity().getState());
		List<LGAInfo> wLGAInfoList = this.genericService.loadAllObjectsWithSingleCondition(LGAInfo.class,
				CustomPredicate.procurePredicate("state.id", employee.getStateOfOriginId()), "name");

		model.addAttribute("states",states);
		model.addAttribute("LGAList", wLGAInfoList);
		model.addAttribute("employee",employee);
		model.addAttribute("roleBean", bc);
		return VIEW_NAME;
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "oid", "s" })
	public String setupForm(@RequestParam("oid") Long eid, @RequestParam("s") int pSaved, Model model,
			HttpServletRequest request) throws Exception {

		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = this.getBusinessCertificate(request);
		// Object userId = super.getSessionId(request);

		AbstractEmployeeEntity employee = (AbstractEmployeeEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc), Arrays.asList(
				CustomPredicate.procurePredicate("id",eid), this.getBusinessClientIdPredicate(request)));
		if ((employee.isTerminated())) {
			employee.setCanEdit(true);
		}
		NamedEntity nE = new NamedEntity();
		nE.setId(eid);
		nE.setName(employee.getDisplayNameWivTitlePrefixed());
		addSessionAttribute(request, NAMED_ENTITY, nE);

		employee.setTitleId(employee.getTitle().getId());
		employee.setLgaId(employee.getLgaInfo().getId());
		employee.setRelId(employee.getReligion().getId());
		if ((employee.getSchoolInfo() != null) && (!employee.getSchoolInfo().isNewEntity())) {
			employee.setSchoolInstId(employee.getSchoolInfo().getId());
		}
		employee.setStateInstId(employee.getCity().getState().getId());
		employee.setCityId(employee.getCity().getId());
		List<State> states = new ArrayList<>();
		states.add(employee.getCity().getState());
		employee.setStateOfOriginId(employee.getStateOfOrigin().getId());
		List<LGAInfo> wLGAInfoList = this.genericService.loadAllObjectsWithSingleCondition(LGAInfo.class,
				CustomPredicate.procurePredicate("state.id", employee.getStateOfOrigin().getId()), "name");

		model.addAttribute("LGAList", wLGAInfoList);
		String actionCompleted = "Basic Information for " + employee.getDisplayNameWivTitlePrefixed()
				+ " Updated Successfully.";
		if(!bc.isPensioner()){
			if(pSaved == 2 ) {
				if(menuService.canUserAccessURL(bc, "/createEmployeeContract.do", "/createEmployeeContract.do"))
					employee.setShowContractLink(true);
			}
		}else{
			HiringInfo hiringInfo = loadHiringInfoByEmpId(request,bc,eid);
			hiringInfo.setGratuityAmountStr(PayrollHRUtils.getDecimalFormat().format(hiringInfo.getGratuityAmount()));
			employee.setHiringInfo(hiringInfo);
		}
		model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
		model.addAttribute("saved", Boolean.valueOf(true));
		model.addAttribute("states",states);
		model.addAttribute("employee",employee);
		model.addAttribute("roleBean", bc);
		return VIEW_NAME;

	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@ModelAttribute("employee") AbstractEmployeeEntity employee, BindingResult result, SessionStatus status, Model model,
			HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = this.getBusinessCertificate(request);

		if (isButtonTypeClick(request,IConstants.REQUEST_PARAM_CREATE_CONTRACT)) {

			return "redirect:createEmployeeContract.do?eid=" + employee.getId();
		}


		if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
			return "redirect:employeeOverviewForm.do?eid=" + employee.getId();
		}
		validator.validate(employee, result, bc, loadConfigurationBean(request));
		if (result.hasErrors()) {
			addDisplayErrorsToModel(model, request);
			setSettables(employee,model);
			model.addAttribute("employee",employee);
			model.addAttribute("status", result);
			model.addAttribute("roleBean", bc);
			return VIEW_NAME;
		}


		 
		//Now See if Contract Status Has Changed...
		int wAddendum = 1;
		int wRetVal;
		if(!bc.isPensioner()) {

			HiringInfo wHireInfo = this.genericService.loadObjectWithSingleCondition(HiringInfo.class,CustomPredicate.procurePredicate("employee.id", employee.getId()));

			if(!wHireInfo.getEmployee().getEmployeeType().isContractStaff()) {
				if(wHireInfo.isContractStaff()) {
					wAddendum = 2;
					//Expire The Contract.
					wRetVal = PayrollUtils.expireContract(wHireInfo,this.genericService, bc.getLoginId() );
					if(wRetVal == 1) {
						result.rejectValue("", "Term.Warn", "Error! "+bc.getStaffTypeName()+" Contract NOT FOUND! Contact Your Administrator.");
						addDisplayErrorsToModel(model, request);
						setSettables(employee,model);
						model.addAttribute("employee",employee);
						model.addAttribute("status", result);
						model.addAttribute("roleBean", bc);
						return VIEW_NAME;
					}
				}
			}
			if ((employee.isSchoolEnabled()) && IppmsUtils.isNotNullAndGreaterThanZero(employee.getSchoolInstId())) {
				((Employee)employee).setSchoolInfo(new SchoolInfo(employee.getSchoolInstId()));
			}else{
				((Employee)employee).setSchoolInfo(null);
			}
		}
		employee.setLgaInfo(new LGAInfo(employee.getLgaId()));
		employee.setTitle(new Title(employee.getTitleId()));
		employee.setReligion(new Religion(employee.getRelId()));
        employee.setCity(new City(employee.getCityId(), employee.getStateInstId()));
        employee.setStateOfOrigin(new State(employee.getStateOfOriginId()));
		employee.setLastModBy(new User(bc.getLoginId()));
		employee.setLastModTs(Timestamp.from(Instant.now()));
		this.genericService.saveObject(employee);

		NamedEntity wNE = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
		wNE.setName(employee.getDisplayNameWivTitlePrefixed());
		addSessionAttribute(request, NAMED_ENTITY, wNE);

		return "redirect:editEmployeeForm.do?oid=" +employee.getId() + "&s="+wAddendum;
	}

	private void setSettables(AbstractEmployeeEntity employee, Model model) throws InstantiationException, IllegalAccessException {
		if(IppmsUtils.isNotNullAndGreaterThanZero(employee.getStateInstId())){
			model.addAttribute("states",Arrays.asList(employee.getCity().getState()));
		}
		if(IppmsUtils.isNotNullAndGreaterThanZero(employee.getStateOfOriginId())){
			model.addAttribute("LGAList", genericService.loadAllObjectsWithSingleCondition(LGAInfo.class,CustomPredicate.procurePredicate("state.id", employee.getStateOfOriginId()),"name"));
		}
	}

	private AbstractEmployeeEntity setContractStatus(AbstractEmployeeEntity pEmployee) throws InstantiationException, IllegalAccessException {
		HiringInfo wHireInfo = this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate("employee.id", pEmployee.getId()));
		TerminateReason wTR =  wHireInfo.getTerminateReason();
		if(wTR == null || wTR.isNewEntity() || wTR.isNotReinstateable())
			return pEmployee;
		//--Check if Employee Has an active Contract..
		PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("employee.id", pEmployee.getId()));
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("expiredInd", OFF));
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", pEmployee.getBusinessClientId()));

		if(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, ContractHistory.class) > 0)
			pEmployee.setShowContractLink(true);

		return pEmployee;
	}
}