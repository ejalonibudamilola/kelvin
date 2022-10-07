package com.osm.gnl.ippms.ogsg.controllers.massentry;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.MassEntryService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.hr.MassPromotionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequestMapping({ "/massPromoByPayGroup.do" })
@SessionAttributes(types = { PayPeriodDaysMiniBean.class })
public class MassPromotionByPaygroupController extends BaseController {
	private final PaycheckService paycheckService;
    private final MassPromotionValidator validator;
	private final MassEntryService massEntryService;
	private final String VIEW = "massentry/massPromoByPayGroupForm";

	@Autowired
	public MassPromotionByPaygroupController(PaycheckService paycheckService, MassPromotionValidator validator, MassEntryService massEntryService) {
		this.paycheckService = paycheckService;
		this.validator = validator;
		this.massEntryService = massEntryService;

	}

	@ModelAttribute("salaryTypeList")
	public List<SalaryType> populateSalaryTypes(HttpServletRequest request) {

		BusinessCertificate bc = getBusinessCertificate(request);

		List<SalaryType> wRetList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(
				CustomPredicate.procurePredicate("selectableInd", IConstants.ON), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "name");

		Collections.sort(wRetList);
		return wRetList;
	}

	@RequestMapping(method = { RequestMethod.GET })
	public String setupForm(@RequestParam(value = "s", required = false) Integer pSaved,
			@RequestParam(value = "fsid", required = false) Long pFromId,
			@RequestParam(value = "tsid", required = false) Long pToId,
			@RequestParam(value = "noe", required = false) Long pNoOfElements, Model model,
			HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);

		PayPeriodDaysMiniBean wPGBDH;

		List<Employee> wPromoHist = new ArrayList<>();
		wPGBDH = new PayPeriodDaysMiniBean();

		if (IppmsUtils.isNotNullAndGreaterThanZero(pSaved)) {
			SalaryInfo oldSalaryInfo = this.genericService.loadObjectById(SalaryInfo.class,pFromId);
			SalaryInfo newSalaryInfo = this.genericService.loadObjectById(SalaryInfo.class, pToId);
			String actionCompleted = pNoOfElements + " "+bc.getStaffTypeName()+"s Scheduled for Promotion from  "
					+ oldSalaryInfo.getSalaryScaleLevelAndStepStr() + " to "
					+ newSalaryInfo.getSalaryScaleLevelAndStepStr() + " Successfully";

			model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
			model.addAttribute("saved", Boolean.valueOf(true));
		}
		super.addSessionAttribute(request, IConstants.SELECTED_IDS_KEY, new HashSet<Integer>());
		super.addSessionAttribute(request, SELECT_ALL_ID_KEY, 0);

		wPGBDH.setEmployeeList(wPromoHist);
		return this.prepareAndReturnView(wPGBDH,model,bc,null,request,false);
	}

	@RequestMapping(method = { RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@RequestParam(value = "_updateReport", required = false) String updateReport,
			@RequestParam(value = "_promote", required = false) String promote,
			@ModelAttribute("miniBean") PayPeriodDaysMiniBean pEHB, BindingResult result, SessionStatus status,
			Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);



		if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
			super.removeSessionAttribute(request, SELECTED_IDS_KEY);
			return "redirect:massEntryMainDashboard.do";
		}


		if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {

			// First Make sure there are values to Search For
			if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getFromSalaryTypeId())) {
				if (IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getFromSalaryStructureId())) {
					// The Only thing left to do is to Find all Employees on this salary
					// structure....
					List<Employee> employeeList = this.massEntryService
							.loadActiveEmployeesForMassPromotion(bc,pEHB.getFromSalaryStructureId());
					// Once Loaded, get the Salary Info and then load the Promotion Level and Steps
					// and Add to the List.
					if (employeeList != null && !employeeList.isEmpty()) {

						pEHB.setEmployeeList(employeeList);
						SalaryInfo salaryInfo = this.genericService.loadObjectById(SalaryInfo.class, pEHB.getFromSalaryStructureId());
						pEHB.setToSalaryTypeId(salaryInfo.getId());
						// Now Load The Other List....
                        return  this.prepareAndReturnView(pEHB,model,bc,null,request, true);

					}

				} else {

					result.rejectValue("", "Global.Change", "Please Select a value for 'From Pay Group'");
					return  this.prepareAndReturnView(pEHB,model,bc,result,request, false);

				}
			} else {
				result.rejectValue("", "Global.Change", "No Level & Step Found for 'Pay Group'");
				return  this.prepareAndReturnView(pEHB,model,bc,result,request, false);
			}
			addRoleBeanToModel(model, request);
			return VIEW;
		}


		if (isButtonTypeClick(request,REQUEST_PARAM_PROMOTE)) {


			SalaryInfo oldSalaryInfo = this.genericService.loadObjectById(SalaryInfo.class, pEHB.getFromSalaryStructureId());
			SalaryInfo newSalaryInfo = this.genericService.loadObjectById(SalaryInfo.class,pEHB.getToSalaryStructureId());
			boolean allChecked = (Integer) request.getSession().getAttribute(IConstants.SELECT_ALL_ID_KEY) > 0;
			validator.validateForPromotion(massEntryService,pEHB,result,bc, oldSalaryInfo,newSalaryInfo,allChecked);
			if(result.hasErrors()){
				if(pEHB.isErrorRecord()){
					super.addSessionAttribute(request,SELECT_ALL_ID_KEY,0);
				}
				return this.prepareAndReturnView(pEHB,model,bc,result,request,true);
			}

			Integer noOfElements = 0;
			List<AbstractPromotionAuditEntity> wPAList = new ArrayList<>();
			List<PromotionTracker> wPTList = new ArrayList<>();
			List<NamedEntity> wSList = new ArrayList<>();
			List<Long> wIntegerList = new ArrayList<>();

			for (Employee p : pEHB.getEmployeeList()) {

				if (allChecked)
					p.setPayEmployee("true");

				if (Boolean.valueOf(p.getPayEmployee()).booleanValue()) {

					//Check if this Guy is already earmarked for Promotion....

					wIntegerList.add(p.getId());
					++noOfElements;

					// Now Add The SAI Object ass well...

					AbstractPromotionAuditEntity wPA = createPromotionAudit(p, bc);
					wPA.setOldSalaryInfo(oldSalaryInfo);
					wPA.setSalaryInfo(newSalaryInfo);
					wPAList.add(wPA);

					PromotionTracker wPT = createPromotionTracker(p, bc, newSalaryInfo);
					wPTList.add(wPT);
					wSList.add(this.createSqlForHireInfoUpdate(p, wPT,bc.getBusinessClientInstId()));

					if (wIntegerList.size() == 50) {
						this.massEntryService.promoteMassEmployee(pEHB.getToSalaryStructureId(), wIntegerList, null);
						wIntegerList = new ArrayList<>();

						this.genericService.storeObjectBatch(wPAList);
						wPAList = new ArrayList<>();

						this.genericService.storeObjectBatch(wPTList);
						wPTList = new ArrayList<>();
						this.massEntryService.updateHiringInfoPromotionDates(wSList);
						wSList = new ArrayList<>();

					}
				}
			}
			if (!wIntegerList.isEmpty()) {
				this.massEntryService.promoteMassEmployee(pEHB.getToSalaryStructureId(), wIntegerList, null);

				this.genericService.storeObjectBatch(wPAList);

				this.genericService.storeObjectBatch(wPTList);

				this.massEntryService.updateHiringInfoPromotionDates(wSList);

			}
			super.removeSessionAttribute(request, SELECTED_IDS_KEY);
			return "redirect:massPromoByPayGroup.do?s=1&fsid=" + oldSalaryInfo.getId() + "&tsid="
					+ newSalaryInfo.getId() + "&noe=" + noOfElements;
		}
		super.removeSessionAttribute(request, SELECTED_IDS_KEY);
		return "redirect:massPromoByPayGroup.do";
	}

	@RequestMapping(method = RequestMethod.POST, params = { "checkboxState", "reqList" })
	public void handleCheckboxSelectionState(@RequestParam("checkboxState") boolean isChecked,
			@RequestParam("reqList") Long reqIdList, HttpServletRequest request) {
		if (reqIdList != null && reqIdList > 0) {
			Set<Long> selectedIds = getSelectedRequests(request);

			if (isChecked) {
				selectedIds.add(reqIdList);
			} else {
				selectedIds.remove(reqIdList);
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST, params = { "checkboxState" })
	public void handleSelectAllCheckboxes(@RequestParam("checkboxState") boolean isChecked,
			HttpServletRequest request) {

		Integer selectAll;
		if (isChecked)
			selectAll = 1;
		else
			selectAll = 0;
		request.getSession().setAttribute(SELECT_ALL_ID_KEY, selectAll);

	}

	private AbstractPromotionAuditEntity createPromotionAudit(Employee pEHB, BusinessCertificate pBc) throws Exception {
		AbstractPromotionAuditEntity wPA =  IppmsUtils.makePromotionAuditObject(pBc);
		wPA.setEmployee(new Employee(pEHB.getId()));
		// wPA.setOldSalaryInfo(new SalaryInfo(pEHB.getOldSalaryInfoInstId()));
		wPA.setUser(new User(pBc.getLoginId()));
		wPA.setLastModTs(LocalDate.now());
		// wPA.setNewSalaryInfo(new SalaryInfo(wSI.getId()));
		wPA.setMdaInfo(new MdaInfo(pEHB.getMdaInstId()));
		wPA.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
		wPA.setBusinessClientId(pBc.getBusinessClientInstId());

		wPA.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, pBc));
		wPA.setPromotionDate(LocalDate.now());
		if (pEHB.getSchoolInstId() > 0)
			wPA.setSchoolInfo(new SchoolInfo(pEHB.getSchoolInstId()));

		return wPA;
	}



	private PromotionTracker createPromotionTracker(Employee pEHB, BusinessCertificate pBc, SalaryInfo pSalInfo)
			throws InstantiationException, IllegalAccessException {
		PromotionTracker wPT = genericService.loadObjectWithSingleCondition(PromotionTracker.class,
				CustomPredicate.procurePredicate("employee.id",pEHB.getId()));
		wPT.setEmployee(new Employee(pEHB.getId()));
		wPT.setUser(new User(pBc.getLoginId()));
		wPT.setBusinessClientId(pBc.getBusinessClientInstId());
		wPT.setLastPromotionDate(LocalDate.now());
		wPT.setNextPromotionDate(PayrollHRUtils.determineNextPromotionDate(LocalDate.now(), pSalInfo.getLevel()));

		return wPT;

	}


	private NamedEntity createSqlForHireInfoUpdate(Employee pEHB, PromotionTracker pPT,Long pUserId) {
		NamedEntity wRetVal = new NamedEntity();
		wRetVal.setName(
				"update HiringInfo set lastModBy.id = :pLMB, lastModTs = :pLMTS, lastPromotionDate = :pLPD, nextPromotionDate = :pNPD where employee.id = :pId");
		wRetVal.setId(pEHB.getId());
		wRetVal.setAllowanceStartDate(pPT.getLastPromotionDate());
		wRetVal.setAllowanceEndDate(pPT.getNextPromotionDate());
		wRetVal.setMdaInstId(pPT.getUser().getId());
		wRetVal.setCreationDate(pPT.getLastPromotionDate());
		wRetVal.setParentInstId(pUserId);

		return wRetVal;

	}

	private Set<Long> getSelectedRequests(HttpServletRequest request) {
		// Check for the collection of selected ID's
 		Set<Long> selectedIds = (Set<Long>) request.getSession().getAttribute(SELECTED_IDS_KEY);
		if (selectedIds == null) {

			selectedIds = new HashSet<>();

			// store it in the session
			request.getSession().setAttribute(SELECTED_IDS_KEY, selectedIds);

		}
		return selectedIds;
	}

	private String prepareAndReturnView(PayPeriodDaysMiniBean pEHB, Model model, BusinessCertificate bc,BindingResult result,
										HttpServletRequest request, boolean addSalaryInfoList) throws IllegalAccessException, InstantiationException {
		if(addSalaryInfoList) {
			SalaryInfo salaryInfo = this.genericService.loadObjectById(SalaryInfo.class, pEHB.getFromSalaryStructureId());
			pEHB.setToSalaryTypeId(salaryInfo.getId());

			model.addAttribute("toSalaryStructureList",
					this.massEntryService.loadSalaryInfoBySalaryTypeLevelAndStep(salaryInfo, true, false));
			model.addAttribute("fromSalaryStructureList",
					this.massEntryService.loadSalaryInfoBySalaryTypeLevelAndStep(salaryInfo, false, false));
		}
		model.addAttribute("miniBean", pEHB);
		addRoleBeanToModel(model, request);
		if(result != null){
			addDisplayErrorsToModel(model,request);
			model.addAttribute("status", result);
		}


		return VIEW;
	}

}
