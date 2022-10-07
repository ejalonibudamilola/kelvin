package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationMasterBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.MdapLtgAppIndBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.validators.simulation.SimulationStep3Validator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Controller
@RequestMapping("/step3PayrollSimulator.do")
@SessionAttributes(types = SimulationBeanHolder.class)
public class StepThreeSimulationController extends BaseController {
	  

	@Autowired
	private SimulationStep3Validator validator;

	@Autowired
	private HRService hrService;
	@Autowired
	private PayrollService payrollService;
	private final String VIEW = "simulation/simulatePayrollStep3Form";
	public StepThreeSimulationController(){}
	
	@RequestMapping(method = RequestMethod.GET, params={"uid"})
	public String setupForm(@RequestParam("uid") String pUid,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
		
		SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);

		//first check if this is the same user. If not send back.
		if(!bc.getUserName().equalsIgnoreCase(pUid))
			return "redirect:stepTwoPayrollSimulator.do?uid="+bc.getSessionId().toString();
		
		SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);
		
		wLTGH.setId(bc.getBusinessClientInstId());
		
		wLTGH = setRelevantData(wLTGH, bc);
			
		addSessionAttribute(request, SIM_ATTR, wLTGH);
		
		model.addAttribute("simulationBean3", wLTGH);
		
		return VIEW;
	}
	
	//-- This method handles additions...
	@RequestMapping(method = RequestMethod.GET, params={"pid","mta"})
	public String setupForm(@RequestParam("pid") Long pPid,@RequestParam("mta") int pMta,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);

		SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);
		
		if(wLTGH == null)
			return "redirect:stepTwoPayrollSimulator.do?uid="+bc.getSessionId().toString();
		
		wLTGH = setDisplayRelevantData(wLTGH);
	
		boolean found = false;
		if(!wLTGH.getAssignedMBAPList().isEmpty()) {
			for(AbmpBean a : wLTGH.getAssignedMBAPList()){
				if(a.getMdaInfo().getId().equals(pPid)  && a.getMonthToApply() == pMta){
					found = !found;
					break;
				}
			}
			if(found){
				model.addAttribute("simulationBean3", wLTGH);
				
				return VIEW;
			}
		}
		AbmpBean wBean = this.createMBAPBeanForDisplay(pPid,bc.getBusinessClientInstId());
		
		wBean.setMonthToApply(pMta);
		wBean.setMonthYearDisplay(PayrollBeanUtils.getMonthNameFromInteger(pMta)+", "+PayrollBeanUtils.getCurrentYearAsString());
		
		
		
		//first remove from assigned list...
		List<AbmpBean> newList =  new ArrayList<AbmpBean>();
		for(AbmpBean a : wLTGH.getUnAssignedObjects()){
			if(a.getMdaInfo().getId().equals(wBean.getId())){
				//skip
				continue;
			}
				
			newList.add(a);
		}
		
		if(wLTGH.getAssignedMBAPList() == null)
			wLTGH.setAssignedMBAPList(new ArrayList<AbmpBean>());
		
		wLTGH.getAssignedMBAPList().add(wBean);
		wLTGH.setUnAssignedObjects(newList);
		
		Collections.sort(wLTGH.getAssignedMBAPList());
		if(!wLTGH.getUnAssignedObjects().isEmpty())
			Collections.sort(wLTGH.getUnAssignedObjects());
		
		wLTGH.setObjectId(0);
		
		wLTGH.setTotalAssigned(wLTGH.getAssignedMBAPList().size());
		
		wLTGH.setTotalBasicSalary(wLTGH.getTotalBasicSalary() + wBean.getBasicSalary());
		wLTGH.setTotalLtgCost(wLTGH.getTotalLtgCost() + wBean.getLtgCost());
		wLTGH.setTotalNetIncrease(wLTGH.getTotalNetIncrease() + wBean.getNetIncrease());
		wLTGH.setTotalNoOfEmp(wLTGH.getTotalNoOfEmp() + wBean.getNoOfEmp());
		
		addSessionAttribute(request, SIM_ATTR, wLTGH);
		
		model.addAttribute("simulationBean3", wLTGH);
		
		return VIEW;
		
	}
	
	//-- This method handles ALL additions...
	@RequestMapping(method = RequestMethod.GET, params={"mta"})
	public String setupForm(@RequestParam("mta") int pMta,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);

		
		SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);
		
		if(wLTGH == null)
			return "redirect:stepTwoPayrollSimulator.do?uid="+bc.getSessionId().toString();
		
	
		//Now set the all variable to empty.
		wLTGH.setApplyLtgInd("");
		if(wLTGH.getUnAssignedObjects() == null || wLTGH.getUnAssignedObjects().isEmpty()){
			model.addAttribute("simulationBean3", wLTGH);
			
			return VIEW;
		}
		List<AbmpBean> wList = wLTGH.getAssignedMBAPList();
		if(wList == null)
			wList = new ArrayList<AbmpBean>();
		
		for(AbmpBean wBean : wLTGH.getUnAssignedObjects()){
			boolean found = false;
			for(AbmpBean b : wList){
				
				if(b.getMdaInfo().getId().equals(wBean.getMdaInfo().getId())){
					found = !found;
					break;
				}
				
			}
			if(found){
				continue;
			}
			AbmpBean a = this.createMBAPBeanForDisplay(wBean.getMdaInfo().getId(), bc.getBusinessClientInstId());
			
			a.setMonthToApply(pMta);
			a.setMonthYearDisplay(PayrollBeanUtils.getMonthNameFromInteger(pMta)+", "+PayrollBeanUtils.getCurrentYearAsString());
			
			
			wList.add(a);
			
			wLTGH.setTotalBasicSalary(wLTGH.getTotalBasicSalary() + a.getBasicSalary());
			wLTGH.setTotalLtgCost(wLTGH.getTotalLtgCost() + a.getLtgCost());
			wLTGH.setTotalNetIncrease(wLTGH.getTotalNetIncrease() + a.getNetIncrease());
			wLTGH.setTotalNoOfEmp(wLTGH.getTotalNoOfEmp() + a.getNoOfEmp());
			
			
		}
		wLTGH.setAssignedMBAPList(wList);
		wLTGH.setUnAssignedObjects(new ArrayList<AbmpBean>());
	
		Collections.sort(wLTGH.getAssignedMBAPList());
		
		wLTGH.setObjectId(0);
		
		wLTGH.setTotalAssigned(wLTGH.getAssignedMBAPList().size());
		
		wLTGH = setDisplayRelevantData(wLTGH);
		
		addSessionAttribute(request, SIM_ATTR, wLTGH);
		
		model.addAttribute("simulationBean3", wLTGH);
		
		return VIEW;
		
	}
	//This handles removals
	@RequestMapping(method = RequestMethod.GET, params={"pid","act"})
	public String setupForm(@RequestParam("pid") Long pOid,@RequestParam("act") String pAct,Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);

		SimulationBeanHolder wLTGH = (SimulationBeanHolder) getSessionAttribute(request, SIM_ATTR);
		if(wLTGH == null)
			return "redirect:step3PayrollSimulator.do?uid="+bc.getUserName();
		
		
		boolean found = false;
		AbmpBean newBean = new AbmpBean();
		List<AbmpBean> newList =  new ArrayList<AbmpBean>();
		for(AbmpBean a : wLTGH.getAssignedMBAPList()){
			if(a.getMdaInfo().getId().equals(pOid) ){
				found = !found;
				newBean = a;
				newBean.setName(a.getMdaInfo().getName());
				continue;
			}
			newList.add(a);
		}
		if(!found){
			model.addAttribute("simulationBean3", wLTGH);
			
			return VIEW;
		}
		Collections.sort(newList);
		wLTGH.setAssignedMBAPList(newList);
		
		List<AbmpBean> wList = wLTGH.getUnAssignedObjects();
		
		if(wList == null)
			wList = new ArrayList<AbmpBean>();
		
		wList.add(newBean);
		
		wLTGH.setUnAssignedObjects(wList);
		
		
		Collections.sort(wLTGH.getUnAssignedObjects());
		
		
		wLTGH.setTotalAssigned(newList.size());
		
		wLTGH.setTotalBasicSalary(wLTGH.getTotalBasicSalary() - newBean.getBasicSalary());
		wLTGH.setTotalLtgCost(wLTGH.getTotalLtgCost() - newBean.getLtgCost());
		wLTGH.setTotalNetIncrease(wLTGH.getTotalNetIncrease() - newBean.getNetIncrease());
		wLTGH.setTotalNoOfEmp(wLTGH.getTotalNoOfEmp() - newBean.getNoOfEmp());
		wLTGH = setDisplayRelevantData(wLTGH);
		addSessionAttribute(request, SIM_ATTR, wLTGH);
		
		model.addAttribute("simulationBean3", wLTGH);
		
		return VIEW;
		
		
	}

	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(
			@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,
			
			@RequestParam(value = "_add", required = false) String pAdd,
			
			@RequestParam(value = "_finish", required = false) String pFinish,
			
			@ModelAttribute ("simulationBean3") SimulationBeanHolder pHMB, 
			BindingResult result, SessionStatus status,
			Model model,HttpServletRequest request) throws Exception {
		
		SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = this.getBusinessCertificate(request);

		
		//If we get here....for now assume we are adding a value....
		 
		//This is only possible with Super Admin for now....
		if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
			//Now for step 3 -- inform user a PENALTY is attached.
			if(!pHMB.isCancelWarningIssued() && pHMB.getAssignedMBAPList() != null && pHMB.getAssignedMBAPList().size() > 0){
				//Issue a cancel warning...
				pHMB.setCancelWarningIssued(true);
				result.rejectValue("", "Sim.Canc.Warn", "Cancelling of this Page will delete all LTG Mappings already done! Press 'Next' to undo or 'Cancel' to go back to Step 2.");
				 
				model.addAttribute(DISPLAY_ERRORS, BLOCK);
				model.addAttribute("status", result);
				model.addAttribute("simulationBean3", pHMB);
				
				return VIEW;
			}
			
			return "redirect:stepTwoPayrollSimulator.do?uid="+bc.getSessionId().toString()+"&mode=e";
			
		}
		
	 
		if(isButtonTypeClick(request, REQUEST_PARAM_ADD)){
			//First check if the month has been selected.
			validator.validate(pHMB, result,bc);
			if(result.hasErrors()){
				model.addAttribute(DISPLAY_ERRORS, BLOCK);
				model.addAttribute("status", result);
				model.addAttribute("simulationBean3", pHMB);
				addRoleBeanToModel(model, request);
				return VIEW;
			}else{
				if(!StringUtils.trimToEmpty(pHMB.getApplyToAllInd()).equals(EMPTY_STR) && Boolean.valueOf(pHMB.getApplyToAllInd()))
				{
					pHMB.getPayrollSimulationMasterBean().setApplyLtgToAll(ON);
					pHMB.getPayrollSimulationMasterBean().setLastModTs(LocalDate.now());
					this.genericService.storeObject(pHMB.getPayrollSimulationMasterBean());
					addSessionAttribute(request, SIM_ATTR, pHMB);
					//Now Add All and then - return...
					return "redirect:step3PayrollSimulator.do?mta="+pHMB.getAssignToMonthInd();
				}else{
					
					int mta = pHMB.getAssignToMonthInd();
					return "redirect:step3PayrollSimulator.do?pid="+pHMB.getMdaInstId()+"&mta="+mta;
				}
				
			}
		}
		 
		if(isButtonTypeClick(request, REQUEST_PARAM_FINISH)){
			//No need for any validation.
			this.createAndStorePayrollSimulationDetails(pHMB,bc, request);
			
			return "redirect:simulationPreviewForm.do?uid="+bc.getUserName();
			
		}
	
		return "redirect:stepTwoPayrollSimulator.do?uid="+bc.getSessionId().toString();
		
	}
	
	private SimulationBeanHolder setDisplayRelevantData(
			SimulationBeanHolder pHMB)
	{
		PayrollSimulationMasterBean wPSMB = pHMB.getPayrollSimulationMasterBean();
		if(wPSMB.getApplyLtgInd() == ON){
			pHMB.setApplyLtgInd(TRUE);
		}
		if(wPSMB.isIncludePromotionsChecked()){
			pHMB.setIncludePromotionInd(TRUE);
		}
		if(wPSMB.getDeductBaseYearDevLevyInd() == ON){
			pHMB.setDeductBaseYearDevLevy(TRUE);
		}
		if(wPSMB.getDeductSpillOverYearDevLevy() == ON){
			pHMB.setDeductSpillOverYearDevLevy(TRUE);
		}
		if(wPSMB.getStepIncrementInd() == ON){
			pHMB.setStepIncrementInd(TRUE);
		}
		
		return pHMB;
	}

	private AbmpBean createMBAPBeanForDisplay(Long pPid, Long bizId)
	{
	    AbmpBean wRetVal = this.hrService.createMBAPMiniBeanForLTGAllowance(pPid,bizId, true);
		return wRetVal;
	}
	private SimulationBeanHolder setRelevantData(SimulationBeanHolder pLTGH, BusinessCertificate businessCertificate) throws IllegalAccessException, InstantiationException {
		
		//This is just for LTG so prepare ground
		//Now for the Base Year -- find out how many MDA&Ps have had LTG applied this year...
		List<CustomPredicate> predicates = new ArrayList<>();
		predicates.add(CustomPredicate.procurePredicate("ltgApplyYear", LocalDate.now().getYear()));
		predicates.add(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()));
 		List<MdapLtgAppIndBean> wLtgAppList =  this.genericService.loadAllObjectsUsingRestrictions(MdapLtgAppIndBean.class, predicates,null);
		
		if(wLtgAppList != null && !wLtgAppList.isEmpty()){
			pLTGH = separateByDesignation(wLtgAppList,pLTGH);
		}else{
			pLTGH.setExemptLtgMap(new HashMap<>());
		}
			
			
			//Now generate the List of All Agencies, Boards, Ministries, and Parastatals that can be added to the List.
			List<AbmpBean> wAgencyList = this.hrService.getMdapListForLtgByCodeAndFilter(businessCertificate,pLTGH.getExemptLtgMap());
			 
 		    Comparator<AbmpBean> c = Comparator.comparing(AbmpBean::getName);
 		    Collections.sort(wAgencyList,c);
			pLTGH.setUnAssignedObjects(wAgencyList);
		
		//Set the Visuals for the ones coming from Step 2.
		pLTGH.setAssignedMBAPList(new ArrayList<>());
		pLTGH.setHasDataForDisplay(false);
		pLTGH.setTotalAssigned(0);
		pLTGH.setTotalBasicSalary(0);
		pLTGH.setTotalLtgCost(0);
		pLTGH.setTotalNetIncrease(0);
		pLTGH.setTotalNoOfEmp(0);
		
		if(pLTGH.getPayrollSimulationMasterBean().isIncludePromotionsChecked()){
			String includePromotionInd = "Include Promotion";
			pLTGH.setIncludePromotionStr(includePromotionInd);
			pLTGH.setIncludePromotionInd(TRUE);
		}
		if(pLTGH.isDeductDevLevyForBaseYear()){
			pLTGH.setDeductBaseYearDevLevyStr("Deduct Development Levy for "+PayrollBeanUtils.getCurrentYearAsString()+" in "+PayrollBeanUtils.getMonthNameFromInteger(pLTGH.getStartMonthInd()));
		}
		
		if(pLTGH.isDeductDevLevyForSpillOverYear()){
			LocalDate wCal = LocalDate.now();
			wCal = wCal.plusYears(1L);
			pLTGH.setDeductDevLevyForSpillOverYearStr("Deduct Development Levy for "+PayrollBeanUtils.getYearAsString(wCal)+" in "+PayrollBeanUtils.getMonthNameFromInteger(pLTGH.getSpillYearDevLevyInd()));
		}
		
		
		LocalDate wCal = LocalDate.now();
		ArrayList<NamedEntity> wMonthList = new ArrayList<>();

		 if(pLTGH.isApplyLtg()) {

			 int wYear = wCal.getYear();
			 wCal = LocalDate.of(wYear, wCal.getMonthValue(), 1);


			 int wStartMonth = -1;
			 int wEndMonth = -1;

			 wStartMonth = pLTGH.getStartMonthInd();

			 wEndMonth = wStartMonth + pLTGH.getNoOfMonthsInd();
			 if (wEndMonth > 12)
				 wEndMonth = 12;
			 if (wStartMonth <= 12) {
				 //Now what is the end month?
				 for (int wStart = wStartMonth; wStart <= wEndMonth && wCal.getYear() == wYear; wStart++) {
					 NamedEntity n = new NamedEntity();
					 n.setId(new Long(wStart));

					 n.setName(Month.of(wStart).getDisplayName(TextStyle.FULL,Locale.UK));

					 wMonthList.add(n);
				 }
				 pLTGH.setBaseMonthList(wMonthList);
			 }

		 }else{
			 pLTGH.setBaseMonthList(wMonthList);
		 }
		
		return pLTGH;
	}

	private SimulationBeanHolder separateByDesignation(List<MdapLtgAppIndBean> pLtgAppList, SimulationBeanHolder pLTGH)
	{
		
		HashMap<Long,Long> wLtgAppliedMap = new HashMap<Long,Long>();
		
		for(MdapLtgAppIndBean m : pLtgAppList){
			 wLtgAppliedMap.put(m.getMdaInfo().getId(), m.getMdaInfo().getId());
			
		}
		
		pLTGH.setExemptLtgMap(wLtgAppliedMap);
		
		return pLTGH;
	}

	private void createAndStorePayrollSimulationDetails(SimulationBeanHolder pHMB, BusinessCertificate pBc, HttpServletRequest pRequest)
	
	{
		PayrollSimulationMasterBean wPSMB = pHMB.getPayrollSimulationMasterBean();
		
		PayrollSimulationDetailsBean wPSDB = new PayrollSimulationDetailsBean();
		try {
			wPSDB = this.genericService.loadObjectById(PayrollSimulationDetailsBean.class, wPSMB.getId());
			if(wPSDB.isNewEntity())
				wPSDB.setId(wPSMB.getId());
		} catch (Exception wEx) {

			wPSDB = new PayrollSimulationDetailsBean();
			wPSDB.setId(wPSMB.getId());
			wEx.printStackTrace();
		}
		
		if(wPSMB.isIncludePromotionsChecked()){
			wPSDB.setPromotionsInd(ON);
		}
		if(wPSMB.getDeductBaseYearDevLevyInd() == ON){
			wPSDB.setDevLevyInd1(ON);
			wPSDB.setDeductDevLevyMonth1(pHMB.getBaseYearDevLevyInd());
		}
		if(wPSMB.getDeductSpillOverYearDevLevy() == ON){
			wPSDB.setDevLevyInd2(ON);
			wPSDB.setDeductDevLevyMonth2(pHMB.getSpillYearDevLevyInd());
		}
		if(wPSMB.getStepIncrementInd() == ON){
			
			wPSDB.setStepIncrement(ON);
		}
		//Now save the step 3 (LTG) stuffs...
		if(pHMB.getAssignedMBAPList().size() > 0){
			StringBuffer ltgStuffs = new StringBuffer();
			ltgStuffs.append("|");
			int i = 0;
			for(AbmpBean a : pHMB.getAssignedMBAPList()){
				 if(i < pHMB.getTotalAssigned() - 1){
					ltgStuffs.append(a.getId().toString()+":"+a.getMonthToApply()+"|");
				}else{
					ltgStuffs.append(a.getId().toString()+":"+a.getMonthToApply());
				}
				 i++;
			}
			if(ltgStuffs.length() > 0){
				wPSDB.setApplyLtgInd1(ON);
				wPSDB.setApplyLtgDetails1(ltgStuffs.toString());
			}
		}
		 
		
		this.genericService.saveObject(wPSDB);
		addSessionAttribute(pRequest, SIM_ATTR, pHMB);
		
	}
	
	
	

}
