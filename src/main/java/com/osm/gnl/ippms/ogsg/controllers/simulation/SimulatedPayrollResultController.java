package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.simulation.*;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;


@Controller
@RequestMapping("/viewSimulatedPayrollResult.do")
@SessionAttributes(types = WageBeanContainer.class)
public class SimulatedPayrollResultController extends BaseController {
	
 
 
	private HashMap<Long,HashMap<Integer, SimulationMiniBean>> mdaMap;
	 
	private List<SimulationMiniBean> deductionsTotalList;
	
	private List<SimulationMiniBean> totalsMap;
	
	private List<SimulationMiniBean> grandTotalsMap;
	
	private List<SimulationMiniBean> headerMap;
	private HashMap<Integer,SimulationMiniBean> payeeMap;
	private HashMap<Integer,SimulationMiniBean> nhfMap;
	private HashMap<Integer,SimulationMiniBean> unionMap;
	private HashMap<Integer,SimulationMiniBean> twsMap;
	private HashMap<Integer,SimulationMiniBean> ltgMap;


	private HashMap<Integer,SimulationMiniBean> devLevyMap;


	private  final Integer PAYE = 1;
	private  final Integer NHF = 2;
	private  final Integer UNION = 3;
	private  final Integer TWS = 4;
	private  final Integer LTG = 5;
	private  final Integer DEVLEVY = 6;
	 
	
 	private String lastDisplayStyle;
	private int fStartMonth;
	
	private int totalNumOfMdas;


	 
	public SimulatedPayrollResultController() {}

	//Initialize all Maps...
	private void init(){
	
		this.deductionsTotalList = new ArrayList<>();
		 
		this.grandTotalsMap = new ArrayList<>();
		this.mdaMap = new HashMap<>();
		 
		this.devLevyMap = new HashMap<>();
		
		
		
		this.headerMap = new ArrayList<>();
		
		
		this.totalsMap = new ArrayList<>();
		
		this.payeeMap = new HashMap<>();
		this.nhfMap = new HashMap<>();
		this.unionMap = new HashMap<>();
		this.twsMap = new HashMap<>();
		this.ltgMap = new HashMap<>();
		this.devLevyMap = new HashMap<>();
		
		totalNumOfMdas = 0;
		
	}
	
	@RequestMapping(method = RequestMethod.GET, params = { "pid" })
	public String setupForm(@RequestParam("pid")Long pPid,Model model, HttpServletRequest request) throws Exception {

		SessionManagerService.manageSession(request, model);
 
		init();
		  
		//First load the Master Bean to act as guide...
		PayrollSimulationMasterBean wPSMB = genericService.loadObjectById(PayrollSimulationMasterBean.class,pPid);
		
		if(wPSMB.isNewEntity())
			return "redirect:errorPageWithDesc.do";
		
		
		this.fStartMonth = wPSMB.getSimulationStartMonth();
		
		//this.spillover = wPSMB.isCrossesOver();
		
		ArrayList<String> wGuideInfo = new ArrayList<String>();
		ArrayList<String> wGuideInfo2 = new ArrayList<String>();
		//First determine from when to when to load.....
		if(wPSMB.isCrossesOver()){
			//This means we need to cater for 2 years....
			//int wStartMonth = wPSMB.getSimulationStartMonth();
			int wStartYear =  wPSMB.getSimulationStartYear();
			
			//Now add the rest of the current year...
			for(int wStart = wPSMB.getSimulationStartMonth(); wStart < 12; wStart++){
				wGuideInfo.add(wStart +":"+ wStartYear);
			}
			//Now we need to find out the start and end of the spillover...
			int wEndMonth = (wPSMB.getSimulationStartMonth() + wPSMB.getSimulationPeriodInd()) - 12;
			for(int wStart = 1; wStart <= wEndMonth; wStart++){
				wGuideInfo2.add(wStart +":"+ (wStartYear + 1));
			}
			
		}else{
			//Single Year...
			
			int wEndMonth = (wPSMB.getSimulationStartMonth() + wPSMB.getSimulationPeriodInd());
			for(int wStart = wPSMB.getSimulationStartMonth(); wStart <= wEndMonth; wStart++){
				wGuideInfo.add(wStart +":"+wPSMB.getSimulationStartYear());
			}
			
		}

		SimulationInfoContainer wSIC = new SimulationInfoContainer();
		
		wSIC.setId(pPid);
		//Now iterate through our friends and make a container object.
		//Create the Header Beans...
		int wIndex = 1;
		
		for(String s : wGuideInfo){
			int wStartMonth = Integer.parseInt(s.substring(0,s.indexOf(":")));
			int wStartYear = Integer.parseInt(s.substring(s.indexOf(":")+ 1));
			
			SimulationMiniBean hb = new SimulationMiniBean();
			
			//Now load ALL SimulationInfo for this period...
			List<SimulationInfo> wList = this.genericService.loadAllObjectsUsingRestrictions(SimulationInfo.class,
					Arrays.asList(CustomPredicate.procurePredicate("payrollSimulationMasterBean.id", wPSMB.getId()),
							CustomPredicate.procurePredicate("runMonth",wStartMonth),CustomPredicate.procurePredicate("runYear",wStartYear)), null);
			createSimulationInfoSummaryBean(wList,wStartMonth,wStartYear,wIndex);
			hb.setIntegerId(new Integer(wIndex));
			hb.setName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wStartMonth, wStartYear));
			this.headerMap.add(hb);
			wIndex++;
		}

		for(String s : wGuideInfo2){
			int wStartMonth = Integer.parseInt(s.substring(0,s.indexOf(":")));
			int wStartYear = Integer.parseInt(s.substring(s.indexOf(":")+ 1));
			SimulationMiniBean hb = new SimulationMiniBean();
			//Now load ALL SimulationInfo for this period...
			List<SimulationInfo> wList = this.genericService.loadAllObjectsUsingRestrictions(SimulationInfo.class,
					Arrays.asList(CustomPredicate.procurePredicate("payrollSimulationMasterBean.id", wPSMB.getId()),
							CustomPredicate.procurePredicate("runMonth",wStartMonth),CustomPredicate.procurePredicate("runYear",wStartYear)), null);
			createSimulationInfoSummaryBean(wList,wStartMonth,wStartYear,wIndex);
			hb.setIntegerId(new Integer(wIndex));
			hb.setName(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wStartMonth, wStartYear));
			this.headerMap.add(hb);
			wIndex++;
		}
		
		
		//Now lets create the display beans....
		Collections.sort(headerMap);
		wSIC.setHeaderList(headerMap);
		
		//Now create mdapBeanList...
		List<SimulationMiniBean> wRetList = new ArrayList<SimulationMiniBean>();
		wRetList = getSimulationMiniBeanFromMap(this.mdaMap,wRetList,true);
		 
		//Now before we sort make SimulationInfoSummaryBean....
		List<SimulationInfoSummaryBean> wMasterList = this.makeSimulationInfoSummaryBeanList(wRetList);
		
		/*for(SimulationInfoSummaryBean s : wMasterList){
			System.out.println(s.getAssignedToObject());
			for(SimulationMiniBean sb : s.getMiniBeanList()){
				System.out.println("ID = "+sb.getId()+": Parent Key = "+sb.getParentKey()+": Name = "+sb.getName()+": Value = "+sb.getCurrentValueStr());
			}
		}*/
		
		Collections.sort(wMasterList);
		
		wSIC.setSummaryBean(wMasterList);
		
		Collections.sort(wRetList);
		
		wSIC.setMdapList(wRetList);
		
		Collections.sort(this.totalsMap);
		
		wSIC.setMdapFooterList(this.totalsMap);
		
		Collections.sort(this.deductionsTotalList);
		
		wSIC.setDeductionsTotals(deductionsTotalList);
		
		//Now Set Deductions....
		wRetList = null;
		wRetList = this.getDetailsFromMap(this.payeeMap, new ArrayList<SimulationMiniBean>(), true);
		
		List<SimulationInfoSummaryBean> wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,new ArrayList<SimulationInfoSummaryBean>(),this.PAYE);
	
		wRetList = this.getDetailsFromMap(this.nhfMap, new ArrayList<>(),true);
		
		wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.NHF);
		
		wRetList = this.getDetailsFromMap(this.unionMap, new ArrayList<>(),true);
		
		wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.UNION);
		
		wRetList = this.getDetailsFromMap(this.twsMap, new ArrayList<>(),true);
		
		wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.TWS);
		
		wRetList = this.getDetailsFromMap(this.ltgMap, new ArrayList<>(),true);
		
		wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.LTG);
		
		wRetList = this.getDetailsFromMap(this.devLevyMap, new ArrayList<>(),true);
		
		wPayeeList = this.makeDeductionSimulationInfoSummaryBeanList(wRetList,wPayeeList,this.DEVLEVY);
		
		Collections.sort(wPayeeList);
		
		wSIC.setDeductionsList(wPayeeList);
		
		Collections.sort(this.grandTotalsMap);
		wSIC.setFooterList(grandTotalsMap);
		
		model.addAttribute("miniBean", wSIC);
		addRoleBeanToModel(model, request);


		return "simulation/payrollSimulationResultForm";
	}
	private List<SimulationInfoSummaryBean> makeDeductionSimulationInfoSummaryBeanList(
			List<SimulationMiniBean> pRetList, List<SimulationInfoSummaryBean> pPayeeList, Integer pTypeInd)
	{
	
		
		HashMap<Integer,SimulationInfoSummaryBean> wWorkMap = new HashMap<Integer,SimulationInfoSummaryBean>();
		
		for(SimulationMiniBean child : pRetList){
			
			SimulationInfoSummaryBean parent = wWorkMap.get(pTypeInd);
			if(null == parent){
				parent = new SimulationInfoSummaryBean();
				parent.setName(child.getName());
				parent.setId(Long.valueOf(pTypeInd));
				parent.setSerialNum(++this.totalNumOfMdas);
				List<SimulationMiniBean> wChildList = new ArrayList<SimulationMiniBean>();
				wChildList.add(child);
				Collections.sort(wChildList);
				parent.setMiniBeanList(wChildList);
			}else{
				List<SimulationMiniBean> wChildList = parent.getMiniBeanList();
				wChildList.add(child);
				Collections.sort(wChildList);
				parent.setMiniBeanList(wChildList);
			}
			wWorkMap.put(pTypeInd, parent);
		}
		//Make Entry.Set list outa HashMap...
		List<SimulationInfoSummaryBean> wRetList = this.getEntryMapList(wWorkMap);
		pPayeeList.addAll(wRetList);
		
		return pPayeeList;
	}

	//Now herein lies all the work..
	private List<SimulationInfoSummaryBean> makeSimulationInfoSummaryBeanList(
			List<SimulationMiniBean> pRetList)
	{
		List<SimulationInfoSummaryBean> wRetList = new ArrayList<SimulationInfoSummaryBean>();
		if(pRetList == null || pRetList.isEmpty())
			return wRetList;
		HashMap<Integer,SimulationInfoSummaryBean> wWorkMap = new HashMap<Integer,SimulationInfoSummaryBean>();
		
		for(SimulationMiniBean child : pRetList){
			
			Integer wKey = child.getParentKey().intValue();
			//System.out.println("child.getParentKey() = "+wKey);
			SimulationInfoSummaryBean parent = wWorkMap.get(wKey);
			if(null == parent){
				parent = new SimulationInfoSummaryBean();
				parent.setAssignedToObject(child.getName());
				parent.setId(child.getParentKey());
				List<SimulationMiniBean> wChildList = new ArrayList<SimulationMiniBean>();
				wChildList.add(child);
				Collections.sort(wChildList);
				parent.setMiniBeanList(wChildList);
			}else{
				List<SimulationMiniBean> wChildList = parent.getMiniBeanList();
				wChildList.add(child);
				Collections.sort(wChildList);
				parent.setAssignedToObject(child.getName());
				parent.setId(child.getParentKey());
				parent.setMiniBeanList(wChildList);
			}
			wWorkMap.put(wKey, parent);
			
		}
		//Make Entry.Set list outa HashMap...
		wRetList = this.getEntryMapList(wWorkMap);
		
		return wRetList;
	}

	private List<SimulationInfoSummaryBean> getEntryMapList(
			HashMap<Integer, SimulationInfoSummaryBean> pWorkMap)
	{
		List<SimulationInfoSummaryBean> wRetList = new ArrayList<SimulationInfoSummaryBean>();
		if(pWorkMap == null || pWorkMap.isEmpty())
			return wRetList;
		
		Set<Entry<Integer,SimulationInfoSummaryBean>> set = pWorkMap.entrySet();
		 Iterator<Entry<Integer, SimulationInfoSummaryBean>> i = set.iterator();
		 
		 while(i.hasNext()){
			 Entry<Integer,SimulationInfoSummaryBean> me = i.next();
			 me.getValue().setDisplayStyle("reportOdd");
			 wRetList.add(me.getValue()); 	
		 }
		 		
		return wRetList;
	}

	private void createSimulationInfoSummaryBean(
			List<SimulationInfo> pList, int pStartMonth, int pStartYear, int pIndex)
	{
		
	
        //System.out.println("Start Month = "+pStartMonth+" Start Year = "+pStartYear+" Index = "+pIndex);
		SimulationMiniBean payeeBean = this.getSimulationMiniBean(pStartMonth,this.PAYE,pStartYear,pIndex);
		SimulationMiniBean nhfBean = this.getSimulationMiniBean(pStartMonth, this.NHF, pStartYear,pIndex);
		SimulationMiniBean unionBean = this.getSimulationMiniBean(pStartMonth, this.UNION, pStartYear,pIndex);
		SimulationMiniBean twsBean = this.getSimulationMiniBean(pStartMonth, this.TWS, pStartYear,pIndex);
		SimulationMiniBean ltgBean = this.getSimulationMiniBean(pStartMonth, this.LTG, pStartYear,pIndex);
		SimulationMiniBean devLevyBean = this.getSimulationMiniBean(pStartMonth, this.DEVLEVY, pStartYear,pIndex);
		
		SimulationMiniBean totalDeductions =  new SimulationMiniBean();
		totalDeductions.setIntegerId(pIndex);
		SimulationMiniBean wTotals = new SimulationMiniBean();
		wTotals.setIntegerId(pIndex);
		SimulationMiniBean totalGrossValue =  new SimulationMiniBean();
		
		totalGrossValue.setIntegerId(pIndex);
		
		for(SimulationInfo e : pList){
			
			Long wKey = e.getMdaDeptMap().getMdaInfo().getId();
			
			payeeBean.setCurrentValue(payeeBean.getCurrentValue() + e.getMonthlyTax());
			nhfBean.setCurrentValue(nhfBean.getCurrentValue() + e.getNhf());
			unionBean.setCurrentValue(unionBean.getCurrentValue() + e.getUnionDues());
			twsBean.setCurrentValue(twsBean.getCurrentValue() + e.getTws());
			ltgBean.setCurrentValue(ltgBean.getCurrentValue() + e.getLeaveTransportGrant());
			devLevyBean.setCurrentValue(devLevyBean.getCurrentValue() + e.getDevelopmentLevy());
			
		    totalDeductions.setCurrentValue(totalDeductions.getCurrentValue() +  (e.getMonthlyTax() + e.getNhf() + e.getUnionDues() + e.getTws() + e.getDevelopmentLevy()));
			
		    SimulationMiniBean wWSB =  new SimulationMiniBean();
		    //SimulationInfoSummaryBean wSISB = new SimulationInfoSummaryBean();
		 
				//int _wKey = this.mapAgencyMap.get(wKey);
				if(this.mdaMap.containsKey(wKey)){
					HashMap<Integer,SimulationMiniBean> wAll = this.mdaMap.get(wKey);
					//Now get the SimulationInfoSummary for the Year/Month Simulation....
					if(wAll == null)
						wAll = new HashMap<>();
					
					if(wAll.containsKey(pIndex)){
						wWSB = wAll.get(pIndex);
						wWSB.setCurrentValue(wWSB.getCurrentValue() + e.getNetPay());
						wWSB.setParentKey(wKey);
						wAll.put(pIndex, wWSB);
						
					}else{
						
						wWSB.setName(e.getMdaDeptMap().getMdaInfo().getName());
						wWSB.setIntegerId(pIndex);
						wWSB.setCurrentValue(e.getNetPay());
						wWSB.setParentKey(wKey);
						wAll.put(pIndex, wWSB);
						
					}
					this.mdaMap.put(wKey, wAll);
					
					
				}else{
					HashMap<Integer,SimulationMiniBean> wAll = new HashMap<Integer,SimulationMiniBean>();
					
					wWSB.setName(e.getMdaDeptMap().getMdaInfo().getName());
					wWSB.setParentKey(wKey);
					wWSB.setCurrentValue(e.getNetPay());
					wWSB.setIntegerId(pIndex);
					wAll.put(pIndex, wWSB);
					this.mdaMap.put(wKey, wAll);
				}
		 
			//Now do totals for each Year:Month Combo..
			wTotals.setCurrentValue(wTotals.getCurrentValue() + e.getNetPay());
			totalGrossValue.setCurrentValue(totalGrossValue.getCurrentValue() + (e.getNetPay() + e.getNhf() + e.getTaxesPaid() + e.getTws() + e.getDevelopmentLevy() + e.getUnionDues()));
		}
		
		//Now when all this has been done...
		this.deductionsTotalList.add(totalDeductions);
		this.grandTotalsMap.add(totalGrossValue);
		this.totalsMap.add(wTotals);
		this.payeeMap.put(pStartMonth,payeeBean);
		this.nhfMap.put(pStartMonth, nhfBean);
		this.unionMap.put(pStartMonth, unionBean);
		this.twsMap.put(pStartMonth, twsBean);
		this.ltgMap.put(pStartMonth, ltgBean);
		this.devLevyMap.put(pStartMonth, devLevyBean);
		
	}

	private SimulationMiniBean getSimulationMiniBean(int pStartMonth, int pIndicator, int pYearInd, int pIndex)
	{
		
		//Integer wKey = new Integer(pIndicator);
		
		SimulationMiniBean wRetVal = new SimulationMiniBean();
		switch(pIndicator){
		case 1://PAYE
			wRetVal = this.payeeMap.get(pStartMonth);
			if(wRetVal == null){
				wRetVal = new SimulationMiniBean();
				if(pStartMonth == this.fStartMonth){
					wRetVal.setName("P.A.Y.E");
				}
				wRetVal.setIntegerId(pStartMonth);
				wRetVal.setYearInd(pYearInd);
				wRetVal.setComparator(pIndex);
				this.payeeMap.put(pStartMonth, wRetVal);
			}
			break;
		case 2: //NHF
			wRetVal = this.nhfMap.get(pStartMonth);
			if(wRetVal == null){
				wRetVal = new SimulationMiniBean();
				if(pStartMonth == this.fStartMonth){
					wRetVal.setName("N.H.F");
				}
				wRetVal.setIntegerId(pStartMonth);
				wRetVal.setYearInd(pYearInd);
				wRetVal.setComparator(pIndex);
				this.nhfMap.put(pStartMonth, wRetVal);
			}
			break;
		case 3: //UNION
			wRetVal = this.unionMap.get(pStartMonth);
			if(wRetVal == null){
				wRetVal = new SimulationMiniBean();
				if(pStartMonth == this.fStartMonth){
					wRetVal.setName("Union Dues");
				}
				wRetVal.setIntegerId(pStartMonth);
				wRetVal.setYearInd(pYearInd);
				wRetVal.setComparator(pIndex);
				this.unionMap.put(pStartMonth, wRetVal);
			}
			break;
		case 4://TWS
			wRetVal = this.twsMap.get(pStartMonth);
			if(wRetVal == null){
				wRetVal = new SimulationMiniBean();
				if(pStartMonth == this.fStartMonth){
					wRetVal.setName("Teachers Welfare Scheme");
				}
				wRetVal.setIntegerId(pStartMonth);
				wRetVal.setYearInd(pYearInd);
				wRetVal.setComparator(pIndex);
				this.twsMap.put(pStartMonth, wRetVal);
			}
			break;
		case 5://LTG
			wRetVal = this.ltgMap.get(pStartMonth);
			if(wRetVal == null){
				wRetVal = new SimulationMiniBean();
				if(pStartMonth == this.fStartMonth){
					wRetVal.setName("Leave Transport Grant");
				}
				wRetVal.setIntegerId(pStartMonth);
				wRetVal.setYearInd(pYearInd);
				wRetVal.setComparator(pIndex);
				this.ltgMap.put(pStartMonth, wRetVal);
			}
			break;
		case 6://DEVLEVY
			wRetVal = this.devLevyMap.get(pStartMonth);
			if(wRetVal == null){
				wRetVal = new SimulationMiniBean();
				if(pStartMonth == this.fStartMonth){
					wRetVal.setName("Development Levy");
				}
				wRetVal.setIntegerId(pStartMonth);
				wRetVal.setYearInd(pYearInd);
				wRetVal.setComparator(pIndex);
				this.devLevyMap.put(pStartMonth, wRetVal);
			}
			break;
			
		}
		
		return wRetVal;
	}


	private List<SimulationMiniBean> getSimulationMiniBeanFromMap(
			HashMap<Long,HashMap<Integer,SimulationMiniBean>> pObjectMap, List<SimulationMiniBean> pRetList,boolean pSetDisplay) {
		
		
		Set<Long> wSet = pObjectMap.keySet();
		
	    for(Long wInt : wSet){
	    	
	    	HashMap<Integer,SimulationMiniBean> wInnerMap = pObjectMap.get(wInt);
	    	pRetList = getDetailsFromMap(wInnerMap,pRetList,pSetDisplay);
	    }
		 
		
		 		
		return pRetList;
	}
	private List<SimulationMiniBean> getDetailsFromMap(
			HashMap<Integer, SimulationMiniBean> pObjectMap, List<SimulationMiniBean> pRetList,boolean pSetDisplay) {
		
		Set<Entry<Integer,SimulationMiniBean>> set = pObjectMap.entrySet();
		 Iterator<Entry<Integer, SimulationMiniBean>> i = set.iterator();
		 
		 while(i.hasNext()){
			 Entry<Integer,SimulationMiniBean> me = i.next();
			 
			 if (pSetDisplay) {
				me.getValue().setDisplayStyle("reportOdd");
			 }
			 
				 pRetList.add(me.getValue()); 
			 
			
			
		 }
		 		
		return pRetList;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute ("miniBean") WageBeanContainer pLPB, BindingResult result, SessionStatus status, Model model,HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);

		 // BusinessCertificate bc = this.getBusinessCertificate(request);

		
		return REDIRECT_TO_DASHBOARD;
		
	}
	

}
