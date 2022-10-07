package com.osm.gnl.ippms.ogsg.controllers.simulation;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.LtgSimulationExcelGenerator;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationPaycheckBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map.Entry;



@Controller
public class SimulatedLtgExcelPreparer extends BaseController {

	 
	private final HashMap<Long, WageSummaryBean> mdaMap = new HashMap<Long, WageSummaryBean>();
	private final HashMap<Long, WageSummaryBean> garnMap = new HashMap<Long,WageSummaryBean>();
	public SimulatedLtgExcelPreparer() {}
 
	
	@RequestMapping("/simulatedLTGPayrollExcel.do")
	public void setupForm(@RequestParam("lid") Long pLid, Model model,
								  HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		LtgSimulationExcelGenerator ltgSimulationExcelGenerator = new LtgSimulationExcelGenerator();
        
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = getBusinessCertificate(request);
 
		List<SimulationPaycheckBean> wEPBList = this.genericService.loadAllObjectsUsingRestrictions(SimulationPaycheckBean.class,
				Arrays.asList(CustomPredicate.procurePredicate("ltgMasterBean.id", pLid), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), null);

		List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();

		WageBeanContainer wBEOB = new WageBeanContainer();

		LtgMasterBean wLMB = this.genericService.loadObjectUsingRestriction(LtgMasterBean.class,
				Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pLid)));

		wBEOB.setName(wLMB.getName());
		wBEOB.setMonthAndYearStr(this.getMonthAndYear(
				wLMB.getSimulationMonth(), wLMB.getSimulationYear()));
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
		for (SimulationPaycheckBean e : wEPBList) {

			
			wUnionDues.setCurrentBalance(wUnionDues.getCurrentBalance()
					+ e.getUnionDues());
			wUnionDues.setPreviousBalance(wUnionDues.getPreviousBalance()
					+ e.getUnionDues());
			wNHFBean.setCurrentBalance(wNHFBean.getCurrentBalance()
					+ e.getNhf());
			wNHFBean.setPreviousBalance(wNHFBean.getPreviousBalance()
					+ e.getNhf());
			wPayee.setCurrentBalance(wPayee.getCurrentBalance()
					+ e.getTaxesPaid());
			wPayee.setPreviousBalance(wPayee.getPreviousBalance()
					+ e.getTaxesPaid());
			wTws.setCurrentBalance(wTws.getCurrentBalance() + e.getTws());
			wTws.setPreviousBalance(wTws.getPreviousBalance() + e.getTws());

			wBEOB.setTotalDedBal(wBEOB.getTotalDedBal() + e.getUnionDues()
					+ e.getNhf() + e.getTaxesPaid() + e.getTws());
			wBEOB.setTotalPrevDedBal(e.getUnionDues() + e.getNhf()
					+ e.getTaxesPaid() + e.getTws());
			wTotalCurrPay += (e.getNetPay());
			wTotalPrevPay += (e.getNetPay() - e.getLeaveTransportGrant());
			WageSummaryBean wWSB = null;
			if (this.mdaMap.containsKey(e.getMdaInfo().getId())) {
				 
				 
					wWSB = this.mdaMap.get(e.getMdaInfo().getId());

				} else {
					wWSB = new WageSummaryBean();
					wWSB.setMdaInstId(e.getMdaInfo().getId());
					wWSB.setObjectInd(e.getMdaInfo().getMdaType().getMdaTypeCode());
					wWSB.setSerialNum(++serialNum);
					wWSB.setAssignedToObject(e.getMdaInfo().getName());

				}
				wWSB.setCurrentBalance(wWSB.getCurrentBalance() + e.getNetPay());
				wWSB.setPreviousBalance(wWSB.getPreviousBalance()
						+ e.getNetPay() - e.getLeaveTransportGrant());
				wWSB.setNoOfEmp(wWSB.getNoOfEmp() + 1);
				this.mdaMap.put(e.getMdaInfo().getId(), wWSB);
			}

		
		wEPBList = null;

		wRetList = getWageSummaryBeanFromMap(this.mdaMap, wRetList, true);
		 

		wRetList = this.setLtgIndicator(wRetList, wBEOB.getId(), bc);

		wBEOB.setTotalCurrBal(wTotalCurrPay);
		wBEOB.setTotalPrevBal(wTotalPrevPay);

		List<WageSummaryBean> wDedList = new ArrayList<WageSummaryBean>();
		wPayee.setSerialNum(++serialNum);
		wNHFBean.setSerialNum(++serialNum);
		wUnionDues.setSerialNum(++serialNum);
		wTws.setSerialNum(++serialNum);
		wDedList = getWageSummaryBeanFromMap(this.garnMap, wDedList, false);
		wDedList.add(wPayee);
		wDedList.add(wNHFBean);
		wDedList.add(wUnionDues);
		wDedList.add(wTws);

		Collections.sort(wDedList);
		wBEOB.setDeductionList(wDedList);

		wBEOB.setGrandTotal(wBEOB.getTotalCurrBal() + wBEOB.getTotalDedBal()
				+ wBEOB.getTotalSubBal());
		wBEOB.setGrandPrevTotal(wBEOB.getTotalPrevBal()
				+ wBEOB.getTotalPrevDedBal() + wBEOB.getTotalPrevSubBal());

		// before sorting this list, find out if user has Materiality level set.
		// wRetList = setMaterialityLevelIndicator(wRetList,bc.getLoginId());
		Collections.sort(wRetList);
		wBEOB.setWageSummaryBeanList(wRetList);


		ltgSimulationExcelGenerator.generateExcel(wBEOB, "ltg_simulation_report", response, request, bc);
	}

	private String getMonthAndYear(int pSimulationMonth, int pSimulationYear)
	{
		String wRetVal = EMPTY_STR;
		try {
			Calendar wGc = Calendar.getInstance();
			wGc.set(pSimulationYear, pSimulationMonth, 1);
			wRetVal = wGc.getDisplayName(Calendar.MONTH, Calendar.LONG,
					Locale.getDefault())+", "+pSimulationYear;
		} catch (Exception wEx) {

		}

		return wRetVal;
	}

	private List<WageSummaryBean> getWageSummaryBeanFromMap(
			HashMap<Long, WageSummaryBean> mdaMap2,
			List<WageSummaryBean> pRetList, boolean pSetDisplay)
	{


		Set<Entry<Long, WageSummaryBean>> set = mdaMap2.entrySet();
		Iterator<Entry<Long, WageSummaryBean>> i = set.iterator();

		while (i.hasNext()) {
			Entry<Long, WageSummaryBean> me = i
					.next();			
			pRetList.add(me.getValue());
			// wSize++;
		}

		return pRetList;
	}

	
	
	private List<WageSummaryBean> setLtgIndicator(
			List<WageSummaryBean> pRetList, Long pLMBid, BusinessCertificate bc) throws InstantiationException, IllegalAccessException
	{


		List<AbmpBean> wLtgAppList = this.genericService.loadAllObjectsUsingRestrictions(AbmpBean.class,
				Arrays.asList(CustomPredicate.procurePredicate("ltgMasterBean.businessClientId", bc.getBusinessClientInstId()),
						CustomPredicate.procurePredicate("ltgMasterBean.id",pLMBid)), null);

		if (wLtgAppList == null || wLtgAppList.isEmpty())
			return pRetList;

		HashMap<Long, Long> wLtgAppMap = this
				.makeLtgDetailsMapFromList(wLtgAppList);

		List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();
		for (WageSummaryBean w : pRetList) {

			Long wKey =  w.getMdaInstId();

            w.setMaterialityThreshold(wLtgAppMap.containsKey(wKey));

			wRetList.add(w);
		}

		return wRetList;
	}

	private HashMap<Long, Long> makeLtgDetailsMapFromList(
			List<AbmpBean> pLtgAppList)
	{
		HashMap<Long, Long> wRetMap = new HashMap<Long, Long>();

		for (AbmpBean a : pLtgAppList) {
             wRetMap.put(a.getMdaInfo().getId(), a.getMdaInfo().getId());

		}

		return wRetMap;
	}

}

