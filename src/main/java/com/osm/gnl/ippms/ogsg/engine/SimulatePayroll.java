package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.base.services.SimulationService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncreaseBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.PayrollSimulationDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimTaxInfo;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.RuleDetails;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.RetMainBean;
import com.osm.gnl.ippms.ogsg.report.beans.RetMiniBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
@Getter
@Setter
public class SimulatePayroll implements Runnable {
	
	private GenericService genericService;
	private SimulationService simulationService;
	List<SimulationInfo> listToPay;
	private SimulatePayrollEngine fSimulatePayrollEngine;
	private SimulationBeanHolder fSimulationBeanHolder;
	private HashMap<Integer,RuleDetails> ruleDetailsMap;
	private ConfigurationBean configurationBean;
	private PromotionService promotionService;
	private int fListSize;
	private int fCurrentPercentage;
	private BusinessCertificate bc;
	private String displayMessage;
	private String timeToElapse;
    private HashMap<Long, HashMap<Integer, Integer>> salaryInfoMap;
	private boolean fStop;
	private int wBatchSize;
	private List<SimulationInfo> wSaveList;
	private List<SimulationInfo> workingSet;
	private HashMap<Long,SimulationInfo> workingMap;
	private HashMap<Long, SimulationInfo> salaryMap;
	private HashMap<Long,SimulationInfo> holdingMap;
	private List<MdaInfo> fMdaTypeList;
	private RetMainBean wRBM;
	private PayrollFlag payrollFlag;
	private String payPeriodStr;
	private List<SimTaxInfo> wTaxInfo;
	private PayrollSimulationDetailsBean payrollSimulationDetailsBean;
	private HashMap<String,Long> salaryTypeToIdMap;
	private HashMap<Long, List<AbstractDeductionEntity>> fEmpDedMap;
	private HashMap<Long, List<AbstractGarnishmentEntity>> fGarnMap;
	private HashMap<Long, List<AbstractSpecialAllowanceEntity>> fAllowanceMap;
	private HashMap<Long, SuspensionLog> wPartPayments;


	public SimulatePayroll(GenericService genericService1, SimulationService pPayrollService, BusinessCertificate businessCertificate, SimulatePayrollEngine pCalcPayPerEmployee,
						   SimulationBeanHolder pSimBeanHolder, List<MdaInfo> list, PayrollFlag pPayrollFlag, ConfigurationBean configurationBean,
						   HashMap<Long, List<AbstractDeductionEntity>> fEmpDedMap, HashMap<Long, List<AbstractGarnishmentEntity>> fGarnMap, HashMap<Long, List<AbstractSpecialAllowanceEntity>> fAllowanceMap, HashMap<Long, SuspensionLog> wPartPayments){
		
		this.genericService = genericService1;
		this.fSimulatePayrollEngine = pCalcPayPerEmployee;
		this.bc = businessCertificate;
		this.fStop = false;
		this.wSaveList = new ArrayList<>();
		this.fSimulationBeanHolder = pSimBeanHolder;
		this.workingSet = new ArrayList<>();
		this.holdingMap = new HashMap<>();
		this.workingMap = new HashMap<>();
		this.salaryMap = new HashMap<>();
		this.ruleDetailsMap = new HashMap<>();
		this.fMdaTypeList = list;
		this.simulationService = pPayrollService;
		this.payrollFlag = pPayrollFlag;
		this.configurationBean = configurationBean;
		this.fEmpDedMap = fEmpDedMap;
		this.fGarnMap = fGarnMap;
		this.wPartPayments = wPartPayments;
		this.fAllowanceMap = fAllowanceMap;
		
	}


	public void run() {
		
		this.wBatchSize = 500;
		//For this guy we do something different. We actually run each as 1.
		try {
			//First Split the Rules to apply into month:year|Rule Tandem...
			buildRuleDetails();
			 wTaxInfo = this.genericService.loadAllObjectsWithoutRestrictions(SimTaxInfo.class, null);
			 wRBM = PayrollUtils.makeAverageValues(this.simulationService, bc,fSimulationBeanHolder.getPayrollSimulationMasterBean(), payrollFlag);
			salaryMap = this.fSimulatePayrollEngine.calculatePayrollForAllSalaryInfo();
			
			if(this.fSimulationBeanHolder.getPayrollSimulationMasterBean().isCrossesOver()){
				 	for (MdaInfo m : this.fMdaTypeList) {
						
						this.setDisplayMessage("Simulating Payroll for "+bc.getStaffTypeName()+"s in all "+m.getName());
						boolean stepIncreased = false;
						Thread.sleep(200);
						if (getState())
							break;
						List<SimulationInfo> wList = this.findAllEmployees(bc,configurationBean, m,this.fSimulationBeanHolder.getStartMonthInd(),true);
						
						
						if(wList.isEmpty())
							continue;
					    
						fillWorkingMap(wList);
						if(this.payrollSimulationDetailsBean.isDoStepIncrement() && IConstants.AUTO_STEP_INCREASE_MONTH == this.fSimulationBeanHolder.getStartMonthInd()){
							this.increaseStepManually(m.getId());
							stepIncreased = true;
						}
						int wListSize = wList.size();
						this.fListSize = wListSize;
						
						for (int i = 0; i < wList.size(); i++) {
							
							if (getState())
								break;
							
							SimulationInfo wEPB = wList.get(i);
							
							if(stepIncreased){
								
								SimulationInfo wIncreased = this.workingMap.get(wEPB.getEmployee().getId());
								if(wIncreased != null)
									wEPB = wIncreased;
									
								
							}
							wEPB = simulatePayrollManually(wEPB,true,this.fSimulationBeanHolder.getStartMonthInd());

							wEPB.setPayrollSimulationMasterBean(this.fSimulationBeanHolder.getPayrollSimulationMasterBean());
							
							wEPB.setRunMonth(this.fSimulationBeanHolder.getStartMonthInd());
							
							wEPB.setRunYear(Calendar.getInstance().get(Calendar.YEAR));
							
							//Now merge the 2...
							if(!wEPB.isRetired()){
								
								wEPB = mergeSimulationInfo(wEPB,this.fSimulationBeanHolder.getStartMonthInd());
								
							}
							
							
							wSaveList.add(wEPB);
							this.holdingMap.put(wEPB.getEmployee().getId(), wEPB);
							
							if (wSaveList.size() >= wBatchSize || i == wListSize - 1) {
									saveCalculatedPayroll(wSaveList);
									wSaveList = new ArrayList<SimulationInfo>();
							}
							
							fCurrentPercentage++;
							
						}
						
						fCurrentPercentage = 0;
						this.workingMap = new HashMap<>();

					}
					//First lets purget wSaveList...
					if(!wSaveList.isEmpty()){
						//Saves...
						this.saveCalculatedPayroll(wSaveList);
						//purges...
						wSaveList = new ArrayList<>();
					}
					
					//Continue Manual Simulation...what does this mean????
					
					for(int wStartMonth = this.fSimulationBeanHolder.getStartMonthInd() + 1; wStartMonth < 12; wStartMonth++){
						
						this.setDisplayMessage("Simulating Payroll for "+ PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wStartMonth, Calendar.getInstance().get(Calendar.YEAR)));
						Thread.sleep(200);
						boolean stepIncreased = false;
						
						
						if(this.payrollSimulationDetailsBean.isDoStepIncrement() && IConstants.AUTO_STEP_INCREASE_MONTH == wStartMonth){
							this.increaseStepManually();
							stepIncreased = true;
							
						}
						workingSet = this.makeListFromMap();
						this.fListSize = workingSet.size();
						for(int i = 0; i < this.fListSize; i++){
							
							SimulationInfo s = workingSet.get(i);
							
							s.setLeaveTransportGrant(0);
							
							s.setDevelopmentLevy(0);
							
							s.setRetireFlag(0);
							
							if(s.isPromoted() && s.getPromotionMonth() != wStartMonth){
								s.setPromotedInd(0);
							}
								
							if(!stepIncreased){
								s.setStepIncrementInd(0);
							}
							s = simulatePayrollManually(s,true,wStartMonth);
							s.setRunMonth(wStartMonth);
							s.setRunYear(Calendar.getInstance().get(Calendar.YEAR));
								
							if(s.isRetired()){
								s.setRetireFlag(1);
							
							}else{
								s = this.mergeSimulationInfo(s, wStartMonth);
							}
							this.holdingMap.put(s.getEmployee().getId(), s);
							wSaveList.add(s);
							
							if (wSaveList.size() >= wBatchSize || i == workingSet.size() - 1) {
									saveCalculatedPayroll(wSaveList);
									wSaveList = new ArrayList<SimulationInfo>();
							}
							
							fCurrentPercentage++;
						}
						fCurrentPercentage = 0;
						if(!wSaveList.isEmpty()){
							//Saves...
							this.saveCalculatedPayroll(wSaveList);
							//purges...
							wSaveList = new ArrayList<>();
						}
					}
				    
					fCurrentPercentage = 0;
					fListSize = 0;
				//}
				//Now decide how to get the months for the spillover year...
				int wFarIntoYear = (this.fSimulationBeanHolder.getStartMonthInd() + this.fSimulationBeanHolder.getNoOfMonthsInd()) - 12;
				
				for(int wStartMonth = 0; wStartMonth < wFarIntoYear; wStartMonth++){
					
					this.setDisplayMessage("Simulating Payroll for "+PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wStartMonth, Calendar.getInstance().get(Calendar.YEAR) + 1));
					Thread.sleep(200);
					
					boolean stepIncreased = false;
					
					
					if(this.payrollSimulationDetailsBean.isDoStepIncrement() && IConstants.AUTO_STEP_INCREASE_MONTH == wStartMonth){
						this.increaseStepManually();
						stepIncreased = true;
						
					}
					workingSet = this.makeListFromMap();
				    this.fListSize = workingSet.size();
					for(int i = 0; i < fListSize; i++){
						
						SimulationInfo s = workingSet.get(i);
						
						if(!stepIncreased)
							s.setStepIncrementInd(0);
						
						s = simulatePayrollManually(s,true,wStartMonth);
						s.setRunMonth(wStartMonth);
						s.setRunYear(Calendar.getInstance().get(Calendar.YEAR) + 1);
						if(s.isRetired()){
							//Change in original list...
							s.setRetireFlag(1);
							
						}else{
							s = this.mergeSimulationInfo(s, wStartMonth);
						}
						
						wSaveList.add(s);
						this.holdingMap.put(s.getEmployee().getId(), s);
						
						if (wSaveList.size() >= wBatchSize || i == workingSet.size() - 1) {
								saveCalculatedPayroll(wSaveList);
								wSaveList = new ArrayList<SimulationInfo>();
						}
						
						fCurrentPercentage++;
					}
					if(!wSaveList.isEmpty()){
						//Saves...
						this.saveCalculatedPayroll(wSaveList);
						//purges...
						wSaveList = new ArrayList<SimulationInfo>();
					}
				}
				if(!wSaveList.isEmpty()){
					//Saves...
					this.saveCalculatedPayroll(wSaveList);
					//purges...
					wSaveList = new ArrayList<SimulationInfo>();
				}
				fCurrentPercentage = -1; //Signals Stop...
				this.stop(true);
				
				
			}else{
				
				int wEndMonth = (this.fSimulationBeanHolder.getStartMonthInd() + this.fSimulationBeanHolder.getNoOfMonthsInd());
				if(wEndMonth > 11){
					wEndMonth = 11;
				}
				
				for (MdaInfo m : this.fMdaTypeList) {
					if (getState())
						break;
					this.setDisplayMessage("Simulation Payroll for Employees in all "+m.getName());
					Thread.sleep(200);
					
					boolean stepIncreased = false;
					List<SimulationInfo> wList = this.findAllEmployees(bc,configurationBean, m,this.fSimulationBeanHolder.getStartMonthInd(),true);
					
					
					if(wList.isEmpty())
						continue;
				    
					fillWorkingMap(wList);
					if(this.payrollSimulationDetailsBean.isDoStepIncrement() && IConstants.AUTO_STEP_INCREASE_MONTH == this.fSimulationBeanHolder.getStartMonthInd()){
						this.increaseStepManually(m.getId());
						stepIncreased = true;
					}
					this.fListSize = wList.size();
					
					for (int i = 0; i < wList.size(); i++) {
						
						if (getState())
							break;
						

						SimulationInfo wEPB = wList.get(i);
						
						if(stepIncreased){
							
							SimulationInfo wIncreased = this.workingMap.get(wEPB.getEmployee().getId());
							if(wIncreased != null)
								wEPB = wIncreased;
								
							
						}
						 wEPB = simulatePayrollManually(wEPB,true,this.fSimulationBeanHolder.getStartMonthInd());

						wEPB.setPayrollSimulationMasterBean(this.fSimulationBeanHolder.getPayrollSimulationMasterBean());
						
						wEPB.setRunMonth(this.fSimulationBeanHolder.getStartMonthInd());
						
						wEPB.setRunYear(Calendar.getInstance().get(Calendar.YEAR));
						
						//Now merge the 2...
						if(!wEPB.isRetired()){
							
							wEPB = mergeSimulationInfo(wEPB,this.fSimulationBeanHolder.getStartMonthInd());
							
						}
						
						this.holdingMap.put(wEPB.getEmployee().getId(), wEPB);
						
						wSaveList.add(wEPB);
						
						if (wSaveList.size() >= wBatchSize || i == fListSize - 1) {
								saveCalculatedPayroll(wSaveList);
								wSaveList = new ArrayList<SimulationInfo>();
						}
						
						fCurrentPercentage++;
						
					}
					this.workingMap = new HashMap<Long,SimulationInfo>();
					fCurrentPercentage = 0;
					this.fListSize = 0;
				}
				//First lets purget wSaveList...
				if(!wSaveList.isEmpty()){
					//Saves...
					this.saveCalculatedPayroll(wSaveList);
					//purges...
					wSaveList = new ArrayList<SimulationInfo>();
				}
				
				//Start Manual Simulation...what does this mean????
				for(int wStartMonth = this.fSimulationBeanHolder.getStartMonthInd()+ 1; wStartMonth <= wEndMonth; wStartMonth++){
					
					if (getState())
						break;
					
					this.setDisplayMessage("Simulating Payroll for "+PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wStartMonth, Calendar.getInstance().get(Calendar.YEAR)));
					Thread.sleep(200);
					
					boolean stepIncreased = false;
					
					
					if(this.payrollSimulationDetailsBean.isDoStepIncrement() && IConstants.AUTO_STEP_INCREASE_MONTH == wStartMonth){
						this.increaseStepManually();
						stepIncreased = true;
						
					}
					workingSet = this.makeListFromMap();
					
					this.fListSize = workingSet.size();
				
					for(int i = 0; i < fListSize; i++){
						if (getState())
							break;
						
						SimulationInfo s = workingSet.get(i);
						
						if(stepIncreased && !s.isRetired()){
							
							SimulationInfo wIncreased = this.holdingMap.get(s.getEmployee().getId());
							if(wIncreased != null)
								s = wIncreased;
								
							
						}
						
						s = simulatePayrollManually(s,true,wStartMonth);
						s.setId(null);
						s.setRunMonth(wStartMonth);
						s.setRunYear(Calendar.getInstance().get(Calendar.YEAR));
						
						if(!s.isRetired()){
							
							s = mergeSimulationInfo(s,wStartMonth);
							
						}
						if(!stepIncreased)
							s.setStepIncrementInd(0);
						
						wSaveList.add(s);
						this.holdingMap.put(s.getEmployee().getId(), s);
						if (wSaveList.size() >= wBatchSize || i == workingSet.size() - 1) {
								saveCalculatedPayroll(wSaveList);
								wSaveList = new ArrayList<SimulationInfo>();
						}
						
						fCurrentPercentage++;
					}
					if(!wSaveList.isEmpty()){
						//Saves...
						this.saveCalculatedPayroll(wSaveList);
						//purges...
						wSaveList = new ArrayList<SimulationInfo>();
					}
					fCurrentPercentage = 0;
					fListSize = 0;
				}
				
			}
			
			fCurrentPercentage = -1;
			this.fListSize = 0;
			this.holdingMap = null;
			this.workingMap = null;
		} catch (Exception e) {
			fCurrentPercentage = -1;
			log.error("Critical Exception thrown from CalculatePayroll"+e.getMessage());
			e.printStackTrace();
		}
		
	}
	private SimulationInfo mergeSimulationInfo(SimulationInfo pEPB, int pMonth)
	{
		SimulationInfo wFrom = this.salaryMap.get(pEPB.getEmployee().getSalaryInfo().getId());
		
		//--Now Also get the MiniBean values....
		RetMiniBean wRMB = this.wRBM.getRetMiniBeanMap().get(pEPB.getEmployee().getId());
		 boolean wCalculateTax = false;
		if(wRMB == null) {
			wCalculateTax = !wCalculateTax;
		}
		
		
		if(wFrom == null)
			return pEPB;
		
		//Now if we are applying Rules...
		RuleDetails r = ruleDetailsMap.get(pMonth);
		boolean applyLtg = false;
		boolean deductDevelopmentLevy = false;
		if(r == null){
			//No Rules for this month....
		}else{
			
			if(r.getApplicableObjectList() != null && !r.getApplicableObjectList().isEmpty()){
				
				//Now see if this guy needs Ltg Applied....
				for(String s : r.getApplicableObjectList()){
					if(s.equalsIgnoreCase(String.valueOf(pEPB.getMdaDeptMap().getId()))){
						//This means we need to apply LTG.
						applyLtg = true;
						break;
					}
				}
				if(r.isDeductDevLevy()){
					deductDevelopmentLevy = true;
				}
				
			}
			
			
			
		}
		
		
		
		if(applyLtg){
			double payAmtLtg = wFrom.getBasicSalary() * IConstants.LTG_INCREASE;
			pEPB.setLeaveTransportGrant(payAmtLtg);
			 
			pEPB.setTotalPay(wFrom.getTotalPay() + pEPB.getLeaveTransportGrant());

		}else{
		 
			pEPB.setTotalPay(wFrom.getTotalPay());
		}
		
		if(deductDevelopmentLevy){
			pEPB.setNetPay(pEPB.getNetPay() - IConstants.DEVELOPMENT_LEVY);
			pEPB.setDevelopmentLevy(IConstants.DEVELOPMENT_LEVY);
		}
		

		
		pEPB.setTws(wFrom.getTws());
		if(wCalculateTax) {
			for(SimTaxInfo s : wTaxInfo) {
				if(pEPB.getTotalPay() >= s.getLowerBound() && pEPB.getTotalPay() <= s.getUpperBound()) {
					double wTax = EntityUtils.convertDoubleToEpmStandard(pEPB.getTotalPay() * s.getTaxRate());
					pEPB.setTaxesPaid(wTax);
					pEPB.setMonthlyTax(wTax);
					break;
				}
			}
			
		}else {
			
			pEPB.setTaxesPaid(wRMB.getAverageTax());
			pEPB.setMonthlyTax(wRMB.getAverageTax());
			double wTotalPay = pEPB.getTotalPay();
			wTotalPay -= (wRMB.getAverageTax() + wRMB.getAverageDeductions());
			wTotalPay += wRMB.getAverageSpecAllow();
			pEPB.setNetPay(wTotalPay);
			pEPB.setTotalPay(pEPB.getTotalPay() + wRMB.getAverageSpecAllow());
			
			pEPB.setTotalAllowance(pEPB.getTotalAllowance() + wRMB.getAverageSpecAllow());
			pEPB.setTotalDeductions(pEPB.getTotalDeductions() + wRMB.getAverageDeductions());
		}
		//--Remove Deductions...
		pEPB = removeEmployeeDeductions(pEPB, wFrom);

		pEPB.setGrossPay(pEPB.getTotalPay());
		
		pEPB.setNhf(wFrom.getNhf());
		pEPB.setUnionDues(wFrom.getUnionDues());
		
		pEPB.setId(null);
		
		return pEPB;
	}

	private SimulationInfo removeEmployeeDeductions(SimulationInfo pEPB, SimulationInfo wFrom) {
		List<AbstractDeductionEntity> wList = this.fEmpDedMap.get(pEPB.getEmployee().getId());
		if(IppmsUtils.isNotNullOrEmpty(wList)){
			//TODO - Add Deductions and Garnishments to Simulation.
		}

		return pEPB;
	}

	private void fillWorkingMap(
			List<SimulationInfo> pList)
	{
		for(SimulationInfo s : pList){
			this.workingMap.put(s.getEmployee().getId(), s);
		}
	}

	private List<SimulationInfo> makeListFromMap()
	{

		List<SimulationInfo> pRetList = new ArrayList<SimulationInfo>();
		Set<Entry<Long,SimulationInfo>> set = this.holdingMap.entrySet();
		 Iterator<Entry<Long, SimulationInfo>> i = set.iterator();
		 
		 while(i.hasNext()){
			 Entry<Long,SimulationInfo> me = i.next();
			 pRetList.add(me.getValue());
		 }
		 		
		return pRetList;
	}

	private SimulationInfo simulatePayrollManually(SimulationInfo pS,boolean pBaseYear, int pMonth)
	{
		 LocalDate now = LocalDate.now();

		if(pS.isRetired() || this.shouldRetire(pS, pBaseYear, pMonth)){
			pS = makeRetired(pS);
		}else{
			//Determine if we need to promote..
			if(this.payrollSimulationDetailsBean.isApplyPromotions()){
				//is current month = promotion month?
				if(pS.getHiringInfo().getNextPromotionDate() != null){
					//

					if(pS.getHiringInfo().getNextPromotionDate().getMonthValue() == pMonth){
						//Now see if the Year is either this year or next year...
						if(pBaseYear){
							if(pS.getHiringInfo().getNextPromotionDate().getYear() == now.getYear()){
								SalaryInfo s = this.fSimulatePayrollEngine.getSalaryInfoFromMap(pS.getSalaryInfoId());
								//Now promote this guy...
								List<SalaryInfo> wSalaryInfo = this.simulationService.loadSalaryInfoBySalaryScaleAndFilter(s.getSalaryType().getId(),s.getLevel(),s.getStep(),s.getMonthlyBasicSalary());
								if(!wSalaryInfo.isEmpty() && wSalaryInfo.get(0).getLevel() <= IConstants.PROMOTION_LIMIT){
									pS.setSalaryInfoId(wSalaryInfo.get(0).getId());
									pS.setPromotedInd(1);
									pS.setReRunPayRoll(true);
								}
							}
						}else{
							if(pS.getHiringInfo().getNextPromotionDate().getYear() == now.getYear() + 1){
								SalaryInfo s = this.fSimulatePayrollEngine.getSalaryInfoFromMap(pS.getSalaryInfoId());
								//Now promote this guy...
								List<SalaryInfo> wSalaryInfo = this.simulationService.loadSalaryInfoBySalaryScaleAndFilter(s.getSalaryType().getId(),s.getLevel(),s.getStep(),s.getMonthlyBasicSalary());
								if(!wSalaryInfo.isEmpty() && wSalaryInfo.get(0).getLevel() <= IConstants.PROMOTION_LIMIT){
									pS.setSalaryInfoId(wSalaryInfo.get(0).getId());
									pS.setPromotedInd(1);
									pS.setReRunPayRoll(true);
								}
							}
						}
						
					}else{
						pS.setPromotedInd(0);
					}
					
				}else{
					pS.setPromotedInd(0);
				}
				
			}
			
		}
		return pS;
	}
	 
	private void increaseStepManually(Long pMdaInfoId)
	{
		//Set message...
		
		HashMap<Long, HashMap<Integer, Integer>> wSalaryTypeToLevelAndStepMap = getSalaryInfoLevelAndStepMap();
				
		List<StepIncreaseBean> wSIBList1 = this.promotionService.getStepIncreasableEmployees(bc,pMdaInfoId);
				
		List<StepIncreaseBean> wSIBList = findAllEmployeesToBePromoted(wSalaryTypeToLevelAndStepMap,wSIBList1);
				
		HashMap<String,Long> wSLSTSMap = getSalaryTypeToIdMap();
				
	
		this.setDisplayMessage("Performing Auto Step Increment.....");
		try{
			
			for(int i = 0; i < wSIBList.size(); i++) {

				StepIncreaseBean s = wSIBList.get(i);
				SimulationInfo si = this.workingMap.get(s.getId());
				
				if(si ==  null){
					continue;
				}else if(si.isRetired()){
				
					continue;
				}
				//si.setStepIncreased(true);
				if(wSLSTSMap.containsKey(s.getSalaryTypeLevelAndStepStr())){
					//Now determine see if the level is there.
					HashMap<Integer, Integer> sMap = wSalaryTypeToLevelAndStepMap.get(s.getSalaryTypeInstId());
					if(sMap != null && !sMap.isEmpty()){
						Integer wBarValue = sMap.get(s.getLevel());
						if(wBarValue == null){
							//problem...wotodo?? -- this should technically neva happen, but for now, just add..
							//Do nothing!
						}else{
							//Check if the value is equal to this guy's value...
							if(s.getStep() < wBarValue.intValue()){
								s.setNewStep(s.getStep() + 1);
								//Now we need to make a key in the form
								//TYPE:LEVEL:STEP
								String wKey = s.getSalaryTypeInstId() +":"+ s.getLevel() +":"+ s.getNewStep();
								if(!wSLSTSMap.containsKey(wKey)){
									//Big Data Problem.....Should never happen so throw an exception.
									//throw new Exception("Could not get Salary Information for Salary Type "+s.getSalaryTypeInstId()+" Level "+s.getLevel()+" Step "+s.getNewStep());
									//Do NOTHING!
								}else{
									si.setStepIncrementInd(1);
									si.setSalaryInfoId(wSLSTSMap.get(wKey));
								}
								this.workingMap.put(si.getEmployee().getId(),si);
							}
						}
					}
				}

				
				
			}
			
		}catch(Exception wEx){
			
			log.error("Critical Exception thrown from "+this.getClass().getSimpleName()+" "+wEx.getMessage());
		}
		
	}
	private HashMap<Long, HashMap<Integer, Integer>> getSalaryInfoLevelAndStepMap(){
		if(this.salaryInfoMap == null){
			salaryInfoMap = promotionService.makeSalaryTypeLevelStepToSalaryInfoMap(bc.getBusinessClientInstId());
		}
		return salaryInfoMap;
	}
	private HashMap<String,Long> getSalaryTypeToIdMap(){
		if(this.salaryTypeToIdMap == null){
			salaryTypeToIdMap = promotionService.makeSalaryTypeLevelStepToSalaryInfoMap(bc.getBusinessClientInstId());
		}
		return  salaryTypeToIdMap;
	}
 	private void increaseStepManually()
	{
		//Set message...
		
		HashMap<Long, HashMap<Integer, Integer>> wSalaryTypeToLevelAndStepMap = getSalaryInfoLevelAndStepMap();
				
		List<StepIncreaseBean> wSIBList1 = this.promotionService.getStepIncreasableEmployees(bc,null);
				
		List<StepIncreaseBean> wSIBList = findAllEmployeesToBePromoted(wSalaryTypeToLevelAndStepMap,wSIBList1);
				
		HashMap<String,Long> wSLSTSMap = getSalaryTypeToIdMap();
				
	
		this.setDisplayMessage("Performing Auto Step Increment.....");
		try{
			
			for(int i = 0; i < wSIBList.size(); i++) {

				StepIncreaseBean s = wSIBList.get(i);
				SimulationInfo si = this.holdingMap.get(s.getId());
				
				if(si ==  null){
					continue;
				}else if(si.isRetired()){
				
					continue;
				}
				//si.setStepIncreased(true);
				if(wSLSTSMap.containsKey(s.getSalaryTypeInstId())){
					//Now determine see if the level is there.
					HashMap<Integer, Integer> sMap = wSalaryTypeToLevelAndStepMap.get(s.getSalaryTypeInstId());
					if(sMap != null && !sMap.isEmpty()){
						Integer wBarValue = sMap.get(s.getLevel());
						if(wBarValue == null){
							//problem...wotodo?? -- this should technically neva happen, but for now, just add..
							//Do nothing!
						}else{
							//Check if the value is equal to this guy's value...
							if(s.getStep() < wBarValue.intValue()){
								s.setNewStep(s.getStep() + 1);
								//Now we need to make a key in the form
								//TYPE:LEVEL:STEP
								String wKey = s.getSalaryTypeInstId() +":"+ s.getLevel() +":"+ s.getNewStep();
								if(!wSLSTSMap.containsKey(wKey)){
									//Big Data Problem.....Should never happen so throw an exception.
									//throw new Exception("Could not get Salary Information for Salary Type "+s.getSalaryTypeInstId()+" Level "+s.getLevel()+" Step "+s.getNewStep());
									//Do NOTHING!
								}else{
									si.setStepIncrementInd(1);
									si.setSalaryInfoId(wSLSTSMap.get(wKey));
								}
								
								this.holdingMap.put(si.getEmployee().getId(),si);
							}
						}
					}
				}

				
				
			}
			
		}catch(Exception wEx){
			
			log.error("Critical Exception thrown from "+this.getClass().getSimpleName()+" "+wEx.getMessage());
		}
		
	}
	private List<StepIncreaseBean> findAllEmployeesToBePromoted(
			HashMap<Long, HashMap<Integer, Integer>> pSalaryTypeToLevelAndStepMap, List<StepIncreaseBean> pSIBList1)
	{
		List<StepIncreaseBean> wRetVal = new ArrayList<StepIncreaseBean>();
		
		for(StepIncreaseBean s : pSIBList1){
			
			if(pSalaryTypeToLevelAndStepMap.containsKey(s.getSalaryTypeInstId())){
				//Now determine see if the level is there.
				HashMap<Integer, Integer> sMap = pSalaryTypeToLevelAndStepMap.get(s.getSalaryTypeInstId());
				if(sMap != null && !sMap.isEmpty()){
					//Integer wBarValue = ;
					if(sMap.get(s.getLevel()) == null){
						//problem...wotodo?? -- this should technically neva happen, but for now, just add..
						//wRetVal.add(s);
					}else{
						//Check if the value is equal to this guys value...
						if(s.getStep() < sMap.get(s.getLevel()).intValue()){
							wRetVal.add(s);
						}
					}
				}
			}
			
			
		}
		
		
		
		
		
		return wRetVal;
	}
	private SimulationInfo makeRetired(SimulationInfo pS)
	{
		pS.setTotalPay(0);

		pS.setPromotedInd(0);
		
		pS.setNhf(0);
		
		pS.setGrossPay(0);
		
		pS.setTaxesPaid(0);
		
		pS.setTotalAllowance(0);
		
		pS.setMonthlyTax(0);
		
		pS.setLeaveTransportGrant(0);
				
		pS.setNetPay(0);
		
		pS.setDevelopmentLevy(0);
		
		pS.setTws(0);
		
		pS.setUnionDues(0);
		
		pS.setRetireFlag(1);
		
		return pS;
	}

	private boolean shouldRetire(SimulationInfo pS, boolean pBaseYear,int pMonth)
	{


		LocalDate wCompareDate1;

		LocalDate wCompareDate;
		
		LocalDate wToday = LocalDate.now();
		if(!pBaseYear)
			wToday = LocalDate.now().plusYears(1L);
		
		wCompareDate1 = LocalDate.of(wToday.getYear(), pMonth, 1);

		wCompareDate = LocalDate.of(wCompareDate1.getYear(),wCompareDate1.getMonth(),wCompareDate1.lengthOfMonth());


		
		LocalDate wSixtyYearsAgo  = wCompareDate.minusYears(60L);


		LocalDate wThirtyFiveYearsAgo = wCompareDate.minusYears(35L);
		

		if(pS.getBirthDate().isBefore(wSixtyYearsAgo)){
			
			//before we decide not to pay, lets see if the birth month is the same.
			if(pS.getBirthDate().getYear() == wSixtyYearsAgo.getYear()){
				if(pS.getBirthDate().getMonthValue() < wSixtyYearsAgo.getMonthValue()){
					return true;
				}
			}else if(pS.getBirthDate().getYear() < wSixtyYearsAgo.getYear()){
				return true;
			}
			
		}
		
		if(pS.getHireDate().isBefore(wThirtyFiveYearsAgo)){
			if(pS.getHireDate().getYear() == wThirtyFiveYearsAgo.getYear()){

                return pS.getHireDate().getMonthValue() < wThirtyFiveYearsAgo.getMonthValue();
			}else return pS.getHireDate().getYear() < wThirtyFiveYearsAgo.getYear();
			
			
		}
		return false;
		
	}

	 
	private void buildRuleDetails()
	{
		
		try{
			payrollSimulationDetailsBean = genericService.loadObjectById(PayrollSimulationDetailsBean.class,this.fSimulationBeanHolder.getPayrollSimulationMasterBean().getId());
			
			if(payrollSimulationDetailsBean.isApplyLtgForYear1()){
				String wLtgStr = payrollSimulationDetailsBean.getApplyLtgDetails1();
				StringTokenizer wStr = new StringTokenizer(wLtgStr,"|");
				
				while(wStr.hasMoreTokens()){
					String wSubStr = wStr.nextToken();
					String wInstId = wSubStr.substring(0,wSubStr.indexOf(":"));
					String wMonth = wSubStr.substring(wSubStr.lastIndexOf(":") + 1);
					
					if(ruleDetailsMap.containsKey(Integer.valueOf(wMonth))){
						//This means there is a rule to apply for LTG -- so please create a new Rule and Store...
						RuleDetails r = ruleDetailsMap.get(Integer.valueOf(wMonth));
						r.getApplicableObjectList().add(wInstId);
						ruleDetailsMap.put(Integer.valueOf(wMonth), r);
					}else{
						//First time...
						RuleDetails r = new RuleDetails();
						List<String> wList = new ArrayList<String>();
						//r.setApplicableObjectInd(Integer.parseInt(wInd));
						r.setMonthInd(Integer.parseInt(wMonth));
						//Do we check if Development Levy is meant to be taken this month?? ynot?
						if(payrollSimulationDetailsBean.isDeductDevLevyForYear1() && payrollSimulationDetailsBean.getDeductDevLevyMonth1() == r.getMonthInd()){
							//Then we need to set this value...
							r.setDeductDevLevy(true);
						}
						//Check for Development Levy for year 2 too..
						if(payrollSimulationDetailsBean.isDeductDevLevyForYear2() && payrollSimulationDetailsBean.getDeductDevLevyMonth2() == r.getMonthInd()){
							r.setDeductDevLevy(true);
						}
						wList.add(wInstId);
						r.setApplicableObjectList(wList);
						ruleDetailsMap.put(Integer.valueOf(wMonth), r);
					}
					
				}
			}
			if(payrollSimulationDetailsBean.isDeductDevLevyForYear1()){
					if(ruleDetailsMap.containsKey(payrollSimulationDetailsBean.getDeductDevLevyMonth1())){
						//This means there is a rule in existence...
						RuleDetails r = ruleDetailsMap.get(payrollSimulationDetailsBean.getDeductDevLevyMonth1());
						r.setDeductDevLevy(true);
						ruleDetailsMap.put(payrollSimulationDetailsBean.getDeductDevLevyMonth1(),r);
					}else{
						RuleDetails r = new RuleDetails();
						r.setMonthInd(payrollSimulationDetailsBean.getDeductDevLevyMonth1());
						r.setDeductDevLevy(true);
						ruleDetailsMap.put(payrollSimulationDetailsBean.getDeductDevLevyMonth1(), r);
					}
					
			}
			if(payrollSimulationDetailsBean.isDeductDevLevyForYear2()){
				if(ruleDetailsMap.containsKey(payrollSimulationDetailsBean.getDeductDevLevyMonth2())){
					//This means there is a rule in existence...
					RuleDetails r = ruleDetailsMap.get(payrollSimulationDetailsBean.getDeductDevLevyMonth2());
					r.setDeductDevLevy(true);
					ruleDetailsMap.put(payrollSimulationDetailsBean.getDeductDevLevyMonth2(),r);
				}else{
					RuleDetails r = new RuleDetails();
					r.setMonthInd(payrollSimulationDetailsBean.getDeductDevLevyMonth2());
					r.setDeductDevLevy(true);
					ruleDetailsMap.put(payrollSimulationDetailsBean.getDeductDevLevyMonth2(), r);
				}	
			}
			
			
			fSimulatePayrollEngine.setRuleDetailsMap(ruleDetailsMap);
		}catch(Exception wEx){
			log.error(wEx.getMessage());
			this.ruleDetailsMap = new HashMap<Integer,RuleDetails>();
		}
		
	}

	private boolean getState(){
		return this.fStop;
	}

	/**
	 * Gets the current percentage that is done.
	 * @return a percentage or 1111 if something went wrong.
	 */
	public int getPercentage() {
		if(fCurrentPercentage == -1)
			return 100;
		if(fCurrentPercentage == 0)
			return fCurrentPercentage;
		int wRetVal =  (int) Math.round(((new Double(fCurrentPercentage)/new Double(fListSize)) * 100));
		return wRetVal;
	}
	public void stop(boolean pStop){
		fStop = pStop;
	}
	public int getCurrentRecord(){
		return fCurrentPercentage;
	}

	public int getTotalRecords(){
		return fListSize;
	}
	public boolean isFinished(){
        return fCurrentPercentage == -1;
    }

   public String getDisplayMessage(){
	   return this.displayMessage;
   }
   
   private void setDisplayMessage(String pIncoming){
	   this.displayMessage = pIncoming;
   }
 
	private synchronized void saveCalculatedPayroll(List<SimulationInfo> pEmpPayBeanList){
		
		
		if(!pEmpPayBeanList.isEmpty()){
			
			for (SimulationInfo e : pEmpPayBeanList){
				if(this.getState()){
					pEmpPayBeanList = new ArrayList<>();
					break;
				}
				this.genericService.storeObject(e);
				
				
			}
			
		}
	}
	private List<SimulationInfo> findAllEmployees(BusinessCertificate businessCertificate, ConfigurationBean configurationBean, MdaInfo mdaType, int pMonth, boolean pBaseYear) {
		
		
			//This is where we need to get the first list.
			List<HiringInfo> hireInfoList = simulationService.loadPayableActiveHiringInfoByBusinessId(businessCertificate,mdaType);
			
			List<SimulationInfo> chosenOnes = new ArrayList<SimulationInfo>();
			
			LocalDate wToday = LocalDate.now();
			if(!pBaseYear)
				wToday = wToday.plusYears(1L);

		      LocalDate wSixtyYearsAgo = LocalDate.of(wToday.getYear() - configurationBean.getAgeAtRetirement(), pMonth ,wToday.getDayOfMonth());


		    LocalDate wThirtyFiveYearsAgo =  LocalDate.of(wToday.getYear() - configurationBean.getServiceLength(), pMonth ,wToday.getDayOfMonth());
			
			for (HiringInfo h : hireInfoList) {

				// This is the ones we want.

				SimulationInfo ePB = new SimulationInfo();
				ePB.setEmployee(h.getEmployee());
				ePB.setPayEmployee("true");
				ePB.getEmployee().setPayEmployee("true");

				ePB.setHiringInfo(h);
				ePB.setPayEmployeeRef(true);

				

				if (ePB.getEmployee().getSalaryInfo() != null
						&& !ePB.getEmployee().getSalaryInfo().isNewEntity()) {
					ePB.setPaymentType("Salaried");
					ePB.setName(ePB.getEmployee().getFirstName() + " "
							+ ePB.getEmployee().getLastName());

					ePB.setBirthDate(h.getBirthDate());
					ePB.setHireDate(h.getHireDate());
					if (h.getBirthDate().isBefore(wSixtyYearsAgo)) {
						boolean setDoNotPay = true;
						// before we decide not to pay, lets see if the birth
						// month is the same.
						if (h.getBirthDate().getYear() == wSixtyYearsAgo
								.getYear()) {
							if (h.getBirthDate().getMonthValue() == wSixtyYearsAgo
									.getMonthValue()) {
								// set do not pay.
								setDoNotPay = false;
							}
						}
						ePB.setDoNotPay(setDoNotPay);
					} else if (h.getHireDate().isBefore(wThirtyFiveYearsAgo)) {
						boolean setDoNotPay = true;
						if (h.getHireDate().getMonthValue() == wThirtyFiveYearsAgo
								.getMonthValue()) {
							setDoNotPay = false;
						}
						ePB.setDoNotPay(setDoNotPay);
					}
					ePB.setMdaDeptMap(h.getEmployee().getMdaDeptMap());

					 
					if(ePB.isDoNotPay()){
						ePB.setRetireFlag(1);
						//System.out.println(ePB.getHiringInfo().getEmployee().getId());
					}
					chosenOnes.add(ePB);
				} else {
					// log this -- because employee will never get paid.
					log.error(
							"Employee " + ePB.getEmployee().getEmployeeId()
									+ " Will never get paid.");
				}

			}
			
			
			return chosenOnes;
			
			
			
		
			
	}
		
	
	
}