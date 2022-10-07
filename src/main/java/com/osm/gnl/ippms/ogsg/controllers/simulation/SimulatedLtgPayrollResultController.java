package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationPaycheckBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;


@Slf4j
@Controller
@RequestMapping("/viewSimulatedPayroll.do")
@SessionAttributes(types = WageBeanContainer.class)
public class SimulatedLtgPayrollResultController extends BaseController {
	
 
	private HashMap<Long, WageSummaryBean> mdaMap;
	 
	private HashMap<Long,WageSummaryBean> garnMap;
	
	private final String VIEW = "LTG/casp/simulatedPayrollForm";
	
 	private String lastDisplayStyle;

	
	public SimulatedLtgPayrollResultController() {}

	//Initialize all Maps...
	private void init(){
		this.mdaMap = new HashMap<Long,WageSummaryBean>();
		this.garnMap = new HashMap<Long,WageSummaryBean>();
		//this.subventionMap = new HashMap<Integer,WageSummaryBean>();
		
	}
	 
	
	@RequestMapping(method = RequestMethod.GET, params = { "lid" })
	public String setupForm(@RequestParam("lid")Long pLid,Model model, HttpServletRequest request) throws Exception {

		SessionManagerService.manageSession(request, model);
 
		//LtgMasterBean wLMB = (LtgMasterBean)this.payrollService.loadObjectByClassAndId(LtgMasterBean.class,pLid);
		
		init();
		
		 
		
		List<SimulationPaycheckBean> wEPBList =   this.genericService.loadAllObjectsWithSingleCondition(SimulationPaycheckBean.class, CustomPredicate.procurePredicate("ltgMasterBean.id",pLid), null);
								
		List<WageSummaryBean> wRetList = new ArrayList<>();
		
		WageBeanContainer wBEOB =  new WageBeanContainer();
		
		LtgMasterBean wLMB = this.genericService.loadObjectById(LtgMasterBean.class,pLid);
		
		wBEOB.setName(wLMB.getName());
		wBEOB.setMonthAndYearStr(this.getMonthAndYear(wLMB.getSimulationMonth(),wLMB.getSimulationYear()));
		wBEOB.setId(pLid);

		double wTotalCurrPay = 0.0;
		double wTotalPrevPay = 0.0;
		int serialNum = 0;
		WageSummaryBean wNHFBean = new WageSummaryBean();
		wNHFBean.setName("N.H.F Deductions");
		WageSummaryBean wUnionDues = new WageSummaryBean();
		wUnionDues.setName("Pooled Union Dues");
		WageSummaryBean wPayee = new WageSummaryBean();
		wPayee.setName("PAYE");
		WageSummaryBean wTws = new WageSummaryBean();
		wTws.setName("TWS Deductions");
		wBEOB.setTotalNoOfEmp(wEPBList.size());
		for(SimulationPaycheckBean e : wEPBList){
			
			Long wKey = e.getMdaInfo().getId();
			
			 
			wUnionDues.setCurrentBalance(wUnionDues.getCurrentBalance() + e.getUnionDues());
			wUnionDues.setPreviousBalance(wUnionDues.getPreviousBalance() + e.getUnionDues());
			wNHFBean.setCurrentBalance(wNHFBean.getCurrentBalance() + e.getNhf());
			wNHFBean.setPreviousBalance(wNHFBean.getPreviousBalance() + e.getNhf());
			wPayee.setCurrentBalance(wPayee.getCurrentBalance() + e.getTaxesPaid());
			wPayee.setPreviousBalance(wPayee.getPreviousBalance() + e.getTaxesPaid());
			wTws.setCurrentBalance(wTws.getCurrentBalance() + e.getTws());
			wTws.setPreviousBalance(wTws.getPreviousBalance() + e.getTws());
			
			wBEOB.setTotalDedBal(wBEOB.getTotalDedBal() + e.getUnionDues() + e.getNhf() + e.getTaxesPaid() + e.getTws());
			wBEOB.setTotalPrevDedBal(e.getUnionDues() + e.getNhf() + e.getTaxesPaid() + e.getTws());
			wTotalCurrPay += (e.getNetPay());
			wTotalPrevPay += (e.getNetPay() - e.getLeaveTransportGrant());
			WageSummaryBean wWSB = null;
			  
			if(this.mdaMap.containsKey(wKey)){
					wWSB = this.mdaMap.get(wKey);
					
				}else{
					wWSB = new WageSummaryBean();
					wWSB.setMdaInstId(wKey);
					wWSB.setObjectInd(e.getMdaInfo().getMdaType().getMdaTypeCode());
					wWSB.setSerialNum(++serialNum);
					wWSB.setAssignedToObject(e.getMdaInfo().getName());
					 
				}
				wWSB.setCurrentBalance(wWSB.getCurrentBalance() + e.getNetPay());
				wWSB.setPreviousBalance(wWSB.getPreviousBalance() + e.getNetPay() - e.getLeaveTransportGrant());
				wWSB.setNoOfEmp(wWSB.getNoOfEmp() + 1);
				this.mdaMap.put(wKey, wWSB);
			 
			
		}
		wEPBList = null;
		
		wRetList = getWageSummaryBeanFromMap(this.mdaMap,wRetList,true);
		 
		
		wRetList = this.setLtgIndicator(wRetList,wBEOB.getId());
		
		wBEOB.setTotalCurrBal(wTotalCurrPay);
		wBEOB.setTotalPrevBal(wTotalPrevPay);
		
		List<WageSummaryBean> wDedList = new ArrayList<WageSummaryBean>();
		wPayee.setSerialNum(++serialNum);
		wNHFBean.setSerialNum(++serialNum);		
		wUnionDues.setSerialNum(++serialNum);
		wTws.setSerialNum(++serialNum);
		wDedList = getWageSummaryBeanFromMap(this.garnMap,wDedList,false);
		wDedList.add(wPayee);
		wDedList.add(wNHFBean);
		wDedList.add(wUnionDues);
		wDedList.add(wTws);
		
		Collections.sort(wDedList);
		wBEOB.setDeductionList(wDedList);
		
		wBEOB.setGrandTotal(wBEOB.getTotalCurrBal() + wBEOB.getTotalDedBal() +wBEOB.getTotalSubBal());
		wBEOB.setGrandPrevTotal(wBEOB.getTotalPrevBal()+wBEOB.getTotalPrevDedBal()+wBEOB.getTotalPrevSubBal());
		
		//before sorting this list, find out if user has Materiality level set.
		//wRetList = setMaterialityLevelIndicator(wRetList,bc.getLoginId());
		Collections.sort(wRetList);
		wBEOB.setWageSummaryBeanList(wRetList);

		model.addAttribute("miniBean", wBEOB);
		addRoleBeanToModel(model, request);


		return "LTG/casp/simulatedPayrollForm";
	}

	private String getMonthAndYear(int pSimulationMonth, int pSimulationYear)
	{
		String wRetVal = IConstants.EMPTY_STR;
		try{
			Calendar wGc = Calendar.getInstance();
			wGc.set(pSimulationYear,pSimulationMonth, 1);
			wRetVal = wGc.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())+", "+pSimulationYear;
		}catch(Exception wEx){
			log.error(this.getClass().getSimpleName()+" "+wEx.getMessage());
			 wEx.printStackTrace();
		}
		
		
		return wRetVal;
	}

	private List<WageSummaryBean> getWageSummaryBeanFromMap(
			HashMap<Long, WageSummaryBean> mdaMap2, List<WageSummaryBean> pRetList,boolean pSetDisplay) {
		
		 
		Set<Entry<Long,WageSummaryBean>> set = mdaMap2.entrySet();
		 Iterator<Entry<Long, WageSummaryBean>> i = set.iterator();
		 
		 while(i.hasNext()){
			 Entry<Long,WageSummaryBean> me = i.next();
			 
			 if (pSetDisplay) {
				//if ((wSize % 2) == 1) {
					me.getValue().setDisplayStyle("reportEven");
					lastDisplayStyle = "reportEven";
				} else {
					me.getValue().setDisplayStyle("reportOdd");
					lastDisplayStyle = "reportOdd";
				}
			
			 pRetList.add(me.getValue());
			// wSize++;
		 }
		 		
		return pRetList;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute ("miniBean") WageBeanContainer pLPB, BindingResult result, SessionStatus status, Model model,HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		  //BusinessCertificate bc = this.getBusinessCertificate(request);

		
		return REDIRECT_TO_DASHBOARD;
		
	}
	private List<WageSummaryBean> setLtgIndicator(
			List<WageSummaryBean> pRetList,Long long1) throws InstantiationException, IllegalAccessException {
		
 		List<AbmpBean> wLtgAppList = genericService.loadAllObjectsWithSingleCondition(AbmpBean.class, CustomPredicate.procurePredicate("ltgMasterBean.id",long1), null);
		
		if(wLtgAppList == null || wLtgAppList.isEmpty())
			return pRetList;
		
		HashMap<Long,Long> wLtgAppMap = this.makeLtgDetailsMapFromList(wLtgAppList);
		
		List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();
		for(WageSummaryBean w : pRetList){
			
			Long wKey = w.getMdaInstId();

            w.setMaterialityThreshold(wLtgAppMap.containsKey(wKey));
			
			wRetList.add(w);
		}
		
		return wRetList;
	}

	private HashMap<Long, Long> makeLtgDetailsMapFromList(
			List<AbmpBean> pLtgAppList)
	{
		HashMap<Long,Long> wRetMap = new HashMap<Long,Long>();
		
		for(AbmpBean a : pLtgAppList){
			
			Long wKey = a.getMdaInfo().getId();
			
			wRetMap.put(wKey, wKey);
			
			
		}
		 		
		return wRetMap;
	}

}
