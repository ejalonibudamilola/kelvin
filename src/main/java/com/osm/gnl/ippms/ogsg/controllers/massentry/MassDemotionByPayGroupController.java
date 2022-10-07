package com.osm.gnl.ippms.ogsg.controllers.massentry;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.ReassignEmployeeLog;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.MassEntryService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
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
@RequestMapping({ "/massDemotionByPayGroup.do" })
@SessionAttributes(types = { PayPeriodDaysMiniBean.class })
public class MassDemotionByPayGroupController extends BaseController {


	private final PaycheckService paycheckService;

	private final MassEntryService massEntryService;
	 
	private final String VIEW = "massentry/massDemotionByPayGroupForm";

	@Autowired
	public MassDemotionByPayGroupController(PaycheckService paycheckService, MassEntryService massEntryService) {
		this.paycheckService = paycheckService;
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
	public String setupForm(@RequestParam( value = "s", required = false )Integer pSaved,
			@RequestParam( value = "fsid", required = false )Long pFromId,
			@RequestParam( value = "tsid", required = false )Long pToId,
			@RequestParam( value = "noe", required = false )Integer pNoOfElements,
			Model model, HttpServletRequest request)
			throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);

		PayPeriodDaysMiniBean wPGBDH;

		List<Employee> wPromoHist = new ArrayList<>();
		wPGBDH = new PayPeriodDaysMiniBean();
	 
		
		if(IppmsUtils.isNotNullAndGreaterThanZero(pSaved)) {
			SalaryInfo oldSalaryInfo = this.genericService.loadObjectById(SalaryInfo.class,pFromId);
			SalaryInfo newSalaryInfo = this.genericService.loadObjectById(SalaryInfo.class, pToId);
			String actionCompleted = pNoOfElements +" "+ bc.getStaffTypeName()+"  Demoted from  " +oldSalaryInfo.getSalaryScaleLevelAndStepStr() + " to "+newSalaryInfo.getSalaryScaleLevelAndStepStr()+" Successfully";

		    model.addAttribute(IConstants.SAVED_MSG, actionCompleted);
		    model.addAttribute("saved", Boolean.valueOf(true));
		} 
		super.addSessionAttribute(request, IConstants.SELECTED_DEM_IDS_KEY, new HashSet<Integer>());
		super.addSessionAttribute(request, IConstants.SELECT_ALL_DEM_ID_KEY,0);
			
		 
		wPGBDH.setEmployeeList(wPromoHist);
		model.addAttribute("miniBean", wPGBDH);
		addRoleBeanToModel(model, request);
		return VIEW;
	}

  
	@RequestMapping(method = { RequestMethod.POST })
	public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
			@RequestParam(value = "_updateReport", required = false) String updateReport,
			@RequestParam(value = "_promote", required = false) String promote,
			@ModelAttribute("miniBean") PayPeriodDaysMiniBean pEHB, BindingResult result,
			SessionStatus status, Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);


		if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
			super.removeSessionAttribute(request, SELECT_ALL_ID_KEY);
			super.removeSessionAttribute(request, SELECT_ALL_DEM_ID_KEY);
			return "redirect:massEntryMainDashboard.do";
		}

		if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {

			//First Make sure there are values to Search For
			if(IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getFromSalaryTypeId())) {
				if(IppmsUtils.isNotNullAndGreaterThanZero(pEHB.getFromSalaryStructureId())) {
					//The Only thing left to do is to Find all Employees on this salary structure....
					List<Employee> employeeList = this.massEntryService.loadActiveEmployeesForMassPromotion(bc,pEHB.getFromSalaryStructureId());
					//Once Loaded, get the Salary Info and then load the Promotion Level and Steps and Add to the List.
					if(employeeList != null && !employeeList.isEmpty()) {

						pEHB.setEmployeeList(employeeList);
						SalaryInfo salaryInfo = this.genericService.loadObjectById(SalaryInfo.class, pEHB.getFromSalaryStructureId());
						pEHB.setToSalaryTypeId(salaryInfo.getId());
						//Now Load The Other List....
						 
  						model.addAttribute("toSalaryStructureList", this.massEntryService.loadSalaryInfoBySalaryTypeLevelAndStep(salaryInfo, true,true));
						model.addAttribute("fromSalaryStructureList", this.massEntryService.loadSalaryInfoBySalaryTypeLevelAndStep(salaryInfo,false,true));
						model.addAttribute("miniBean", pEHB);
						model.addAttribute("roleBean", bc);
						return VIEW;
					}
					
				}else {
					result.rejectValue("", "Global.Change", "Please Select a value for 'From Pay Group'");
					addDisplayErrorsToModel(model,request);
					model.addAttribute("status", result);
					model.addAttribute("miniBean", pEHB);
					model.addAttribute("roleBean", bc);
					return VIEW;
				}
			}else {
				result.rejectValue("", "Global.Change", "No Level & Step Found for 'Pay Group'");
				addDisplayErrorsToModel(model,request);
				model.addAttribute("status", result);
				model.addAttribute("miniBean", pEHB);
				model.addAttribute("roleBean", bc);
				return VIEW;
			}

			model.addAttribute("roleBean", bc);
 
			return VIEW;
		}

		if (isButtonTypeClick(request,REQUEST_PARAM_PROMOTE)) {
		 
			if (pEHB.getToSalaryStructureId()  == 0) {
				 
			 
					result.rejectValue("", "Global.Change", "Please select 'To Level/Step'");
					addDisplayErrorsToModel(model, request);
					SalaryInfo salaryInfo = genericService.loadObjectById(SalaryInfo.class, pEHB.getFromSalaryStructureId());
					pEHB.setToSalaryTypeId(salaryInfo.getId());
					//Now Load The Other List....
					 
				    model.addAttribute("toSalaryStructureList", this.massEntryService.loadSalaryInfoBySalaryTypeLevelAndStep(salaryInfo, true,true));
					model.addAttribute("fromSalaryStructureList", this.massEntryService.loadSalaryInfoBySalaryTypeLevelAndStep(salaryInfo,false,true));
					model.addAttribute("miniBean", pEHB);
					model.addAttribute("roleBean", bc);
 					model.addAttribute("status", result);
					 
					return VIEW;
			}
			 

			//First things first...all papa...
			 
			boolean allChecked = (Integer) request.getSession().getAttribute(SELECT_ALL_DEM_ID_KEY) > 0;
			
			if (!allChecked) {

				boolean atLeastOneChecked = false;

				for (Employee e : pEHB.getEmployeeList()) {
					if (Boolean.valueOf(e.getPayEmployee()).booleanValue()) {
						atLeastOneChecked = true;
						break;
					}
				}
				if (!atLeastOneChecked) {
					result.rejectValue("", "Global.Change", "Please select at least 1 "+bc.getStaffTypeName()+" to be Demoted.");
					addDisplayErrorsToModel(model, request);
					SalaryInfo salaryInfo = genericService.loadObjectById(SalaryInfo.class, pEHB.getFromSalaryStructureId());
					pEHB.setToSalaryTypeId(salaryInfo.getId());
					// Now Load The Other List....

					model.addAttribute("toSalaryStructureList", this.massEntryService.loadSalaryInfoBySalaryTypeLevelAndStep(salaryInfo, true,true));
					model.addAttribute("fromSalaryStructureList", this.massEntryService.loadSalaryInfoBySalaryTypeLevelAndStep(salaryInfo,false,true));
					model.addAttribute("miniBean", pEHB);
					model.addAttribute("roleBean", bc);
					model.addAttribute("status", result);

					return VIEW;
				}
			}
		

			String wRefNum = bc.getUserName() + "/MDem_" + PayrollBeanUtils.getDateAsStringWidoutSeparators(LocalDate.now());
			SalaryInfo oldSalaryInfo = genericService.loadObjectById(SalaryInfo.class, pEHB.getFromSalaryStructureId());
			SalaryInfo newSalaryInfo = genericService.loadObjectById(SalaryInfo.class, pEHB.getToSalaryStructureId());
			//Rank oldRank = this.genericService.loadObjectUsingRestriction(Rank.class,Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("from")))
			Integer noOfElements = 0;
			List<ReassignEmployeeLog> wPAList = new ArrayList<>();
			 
			List<NamedEntity> wSList = new ArrayList<>();
			List<Long> wIntegerList = new ArrayList<>();
			for (Employee p : pEHB.getEmployeeList()) {
				
				if(allChecked)
					p.setPayEmployee("true");
				
			if (Boolean.valueOf(p.getPayEmployee()).booleanValue()) {

				wIntegerList.add(p.getId());
				++noOfElements;

				    ReassignEmployeeLog wPA = createPromotionAudit(p, bc,wRefNum, oldSalaryInfo.getId(), newSalaryInfo.getId());
					wPA.setOldSalaryInfo(oldSalaryInfo);
					wPA.setSalaryInfo(newSalaryInfo);
					wPAList.add(wPA);
				 
				 
					if (wIntegerList.size() == 50) {
						this.massEntryService.promoteMassEmployee(pEHB.getToSalaryStructureId(), wIntegerList, null);
						wIntegerList = new ArrayList<>();

						this.genericService.storeObjectBatch(wPAList);
						wPAList = new ArrayList<>();
						 
						this.massEntryService.updateHiringInfoPromotionDates(wSList);
						wSList = new ArrayList<>();

					}
				}
			}
			if (!wIntegerList.isEmpty()) {
				this.massEntryService.promoteMassEmployee(pEHB.getToSalaryStructureId(), wIntegerList, null);

				this.genericService.storeObjectBatch(wPAList);

			}
			super.removeSessionAttribute(request,  SELECT_ALL_DEM_ID_KEY);
			super.removeSessionAttribute(request,  SELECTED_DEM_IDS_KEY);
			return "redirect:massDemotionByPayGroup.do?s=1&fsid="+oldSalaryInfo.getId()+ "&tsid=" + newSalaryInfo.getId()+"&noe="+noOfElements;
		}
		super.removeSessionAttribute(request, SELECTED_IDS_KEY);
		return "redirect:massDemotionByPayGroup.do";
	}
 
	 @RequestMapping(method = RequestMethod.POST, params = {"checkboxState", "reqList"})
	    public void handleCheckboxSelectionState(
	            @RequestParam("checkboxState") boolean isChecked,
	            @RequestParam("reqList")  Long  reqIdList,
	            HttpServletRequest request) {
	        if (reqIdList != null && reqIdList  > 0) {
	            Set<Long> selectedIds = getSelectedRequests(request);

	            if (isChecked) {
	                selectedIds.add(reqIdList);
	            } else {
	                selectedIds.remove(reqIdList);
	            }
	        }
	    }
	 @RequestMapping(method = RequestMethod.POST, params = {"checkboxState"})
	    public void handleSelectAllCheckboxes(
	            @RequestParam("checkboxState") boolean isChecked,
	            HttpServletRequest request) {
		        
	            Integer selectAll;
	            if(isChecked)
	            	selectAll = 1;
	            else
	            	selectAll = 0;
	            request.getSession().setAttribute(SELECT_ALL_DEM_ID_KEY, selectAll);
	            
	        
	    }
	  
	private ReassignEmployeeLog createPromotionAudit(Employee pEHB, BusinessCertificate bc, String pWRefNum, Long oldSalaryId, Long nuSalaryId) throws Exception {
		  
	    ReassignEmployeeLog wRDL = new ReassignEmployeeLog();
	    User wLogin = new User(bc.getLoginId());
	    wRDL.setUser(wLogin);
	    wRDL.setDemotionInd(IConstants.ON);
	    //For now set Old Rank as new Rank.
	    wRDL.setOldRank(new Rank(pEHB.getBiometricId()));
		wRDL.setRank(new Rank(pEHB.getBiometricId()));
	    wRDL.setDeptRefNumber(pWRefNum);
	    wRDL.setRefDate(LocalDate.now());
	    wRDL.setLastModTs(wRDL.getRefDate());
	    wRDL.setOldSalaryInfo(new SalaryInfo(oldSalaryId));
	    wRDL.setSalaryInfo(new SalaryInfo(nuSalaryId));
	    wRDL.setEmployee(pEHB);
	    wRDL.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
	    wRDL.setMdaInfo(new MdaInfo(pEHB.getMdaInstId()));
	    wRDL.setBusinessClientId(bc.getBusinessClientInstId());

	    if(pEHB.getSchoolInstId() > 0){
	    	wRDL.setSchoolInfo(new SchoolInfo(pEHB.getSchoolInstId()));
	    }
	    wRDL.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, bc));

		return wRDL;
	}


	 private Set<Long> getSelectedRequests(HttpServletRequest request) {

			Set<Long> selectedIds = (Set<Long>) request.getSession().getAttribute(SELECTED_DEM_IDS_KEY);
	        if (selectedIds == null) {

	            selectedIds = new HashSet<>();

	            //store it in the session
	            request.getSession().setAttribute(SELECTED_DEM_IDS_KEY, selectedIds);

	        }
	        return selectedIds;
	    }

	  


}
