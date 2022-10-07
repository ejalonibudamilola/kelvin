package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.RuleDetails;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;

import java.util.*;
import java.util.Map.Entry;

public class SimulatePayrollEngine {

	//private IPayrollExt payrollServiceExt;
	
	private boolean applyLtg;
	
	private Map<Long, SalaryInfo> fSalaryInfoMap;

	private boolean deductDevelopmentLevy;
	
	private Map<Integer,RuleDetails> ruleDetailsMap;
	
	
	/*public void setPayrollServiceExt(IPayrollExt pPayrollServiceExt){
		this.payrollServiceExt = pPayrollServiceExt;
	}*/

	public void setSalaryInfoMap(Map<Long,SalaryInfo> pSalaryInfoMap){
		this.fSalaryInfoMap = pSalaryInfoMap;
	}
	
	public SalaryInfo getSalaryInfoFromMap(Long pSalaryInfoId){
		if(this.fSalaryInfoMap != null && !this.fSalaryInfoMap.isEmpty()){
			if(this.fSalaryInfoMap.containsKey(pSalaryInfoId)){
				return this.fSalaryInfoMap.get(pSalaryInfoId);
			}
		}
		return new SalaryInfo();
	}
	
	public HashMap<Long, SimulationInfo> calculatePayrollForAllSalaryInfo(){

		
			HashMap<Long,SimulationInfo> wRetMap = new HashMap<Long,SimulationInfo>();
			
			List<SalaryInfo> wSS = this.makeSalaryInfoMap();
			
			if(wSS == null || wSS.isEmpty())
				return wRetMap;
			
		for (SalaryInfo s : wSS) {
			// first do what we normally do...calculate payroll normally..
			double payAmt = EntityUtils.convertDoubleToEpmStandard(s.getMonthlyBasicSalary()/12.0D);

			SimulationInfo pEmpPayBean = new SimulationInfo();

			pEmpPayBean.setBasicSalary(payAmt);
			
			pEmpPayBean.setTotalPay(addAllowances(s, payAmt, pEmpPayBean));

			 
			double taxesPaid = s.getPaye();//this.calculateTaxes(s);
			pEmpPayBean.setTaxesPaid(taxesPaid);
			pEmpPayBean.setMonthlyTax(taxesPaid);

			// payAmt = this.removeGarnishments(pEmpPayBean);
			// Now we need to add the Deductions and Allowances...but how?
			payAmt = removeNHFandUnionDues(s, payAmt);
			// Now add all allowances
			//payAmt = addAllowances(s, payAmt, pEmpPayBean);
		    pEmpPayBean.setTotalAllowance(pEmpPayBean.getTotalPay() - payAmt);

			//pEmpPayBean.setNetPay(payAmt);
			pEmpPayBean.setNhf(s.getNhf());
			pEmpPayBean.setUnionDues(s.getUnionDues());

			pEmpPayBean.setGrossPay(pEmpPayBean.getTotalPay());
			
			wRetMap.put(s.getId(),pEmpPayBean);

		}
		
		
		return wRetMap;
	}
	private List<SalaryInfo> makeSalaryInfoMap()
	{
		Set<Entry<Long,SalaryInfo>> set = this.fSalaryInfoMap.entrySet();
		 Iterator<Entry<Long, SalaryInfo>> i = set.iterator();
		 List<SalaryInfo> wRetList = new ArrayList<SalaryInfo>();
		 while(i.hasNext()){
			 Entry<Long,SalaryInfo> me = i.next();
			 
			 wRetList.add(me.getValue());
			
			
		 }
		return wRetList;
	}

	public SimulationInfo calculatePayroll(SimulationInfo pEmpPayBean,int pMonth)
			throws Exception {
	 
		if (!pEmpPayBean.isRetired()) {
			
			
			SalaryInfo wSS = this.fSalaryInfoMap.get(pEmpPayBean.getSalaryInfoId());
			
			if(wSS == null)
				return pEmpPayBean;
			
			//First things first get Rules for this month...
			RuleDetails r = ruleDetailsMap.get(pMonth);
			
			if(r == null){
				//No Rules for this month....
			}else{
				
				if(r.getApplicableObjectList() != null && !r.getApplicableObjectList().isEmpty()){
					
					//Now see if this guy needs Ltg Applied....
					for(String s : r.getApplicableObjectList()){
						if(s.equalsIgnoreCase(String.valueOf(pEmpPayBean.getMdaDeptMap().getId()))){
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
			
			
            //first do what we normally do...calculate payroll normally..
			double payAmt = wSS.getMonthlyBasicSalary();
		
			
			pEmpPayBean.setTotalPay(addAllowances(wSS, payAmt,pEmpPayBean));


			//payAmt = removeBeforeTaxDeductions(pEmpPayBean, payAmt);

			// pEmpPayBean = setEmployeeCompanyContributions(pEmpPayBean);

			//double taxesPaid = wSS.getMonthlyTax();
			double taxesPaid = wSS.getPaye();//this.calculateTaxes(wSS);
			pEmpPayBean.setTaxesPaid(taxesPaid);
			pEmpPayBean.setMonthlyTax(taxesPaid);
			
			//payAmt = this.removeGarnishments(pEmpPayBean);
			// Now we need to add the Deductions and Allowances...but how?
			payAmt = removeNHFandUnionDues(wSS, payAmt);
			// Now add all allowances
			payAmt = addAllowances(wSS, payAmt,pEmpPayBean);
			
			if(applyLtg){
				double payAmtLtg = wSS.getMonthlyBasicSalary() * IConstants.LTG_INCREASE;
				pEmpPayBean.setLeaveTransportGrant(payAmtLtg);
				pEmpPayBean.setNetPay(payAmt + payAmtLtg);
	
			}else{
				pEmpPayBean.setNetPay(payAmt);
			}
			if(deductDevelopmentLevy){
				pEmpPayBean.setNetPay(pEmpPayBean.getNetPay() - 100);
				pEmpPayBean.setDevelopmentLevy(100);
			}
			pEmpPayBean.setNhf(wSS.getNhf());
			pEmpPayBean.setUnionDues(wSS.getUnionDues());
			
			
			
			pEmpPayBean.setGrossPay(pEmpPayBean.getTotalPay());
			
		}else{
			pEmpPayBean.setTotalPay(0);

			
			pEmpPayBean.setNhf(0);
			
			pEmpPayBean.setGrossPay(0);
			
			pEmpPayBean.setTaxesPaid(0);
			
			pEmpPayBean.setTotalAllowance(0);
			
			pEmpPayBean.setMonthlyTax(0);
			
			// Now add all allowances			
			pEmpPayBean.setNetPay(0);
			
		/*	pEmpPayBean.setMapId(0);
			pEmpPayBean.setObjectId(0);*/
		}
		this.applyLtg = false;
		this.deductDevelopmentLevy = false;
		
		return pEmpPayBean;
	}
	

	
	

	private double addAllowances(SalaryInfo pSS, double pPayAmt,SimulationInfo pSPB) {
		double wRetVal = 0;
		wRetVal = pPayAmt + EntityUtils.convertDoubleToEpmStandard(pSS.getConsolidatedAllowance()/12.0D);
		if(pSPB.isLtgEnabled()){
			wRetVal += pSPB.getLeaveTransportGrant();
		}
		return wRetVal;
	}
	private double removeNHFandUnionDues(SalaryInfo pSalaryStructure,double pPayAmt) {
		double wRetVal = 0;
		wRetVal = pPayAmt - (pSalaryStructure.getNhf() + pSalaryStructure.getUnionDues() );
		return wRetVal;
	}

	
	public void setRuleDetailsMap(HashMap<Integer, RuleDetails> pRuleDetailsMap)
	{
		this.ruleDetailsMap = pRuleDetailsMap;
		
	}
	
	
}
