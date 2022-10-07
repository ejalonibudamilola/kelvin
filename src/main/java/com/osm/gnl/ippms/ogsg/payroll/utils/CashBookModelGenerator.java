package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class CashBookModelGenerator {


    @Autowired
	private GenericService genericService;
	@Autowired
    private PaycheckService paycheckService;

	private final String salary;
	private final String tax;
	private final int month;
	private final int year;
	private final BusinessCertificate businessCertificate;

	private HashMap<Long, WageSummaryBean> agencyMap;

	private HashMap<Long, SalaryInfo> salaryInfoMap;

	// private String lastDisplayStyle;

	public CashBookModelGenerator( String pSalary, String pTax, int pMonth, int pYear, BusinessCertificate businessCertificate) {
		this.month = pMonth;
		this.salary = pSalary;
		this.tax = pTax;
		this.year = pYear;
		this.businessCertificate = businessCertificate;

	}

	// Initialize all Maps...
	private void init() {

		this.agencyMap = new HashMap<Long, WageSummaryBean>();

		salaryInfoMap = new HashMap<Long, SalaryInfo>();

	}

	private void setControlMaps() throws Exception{
		CustomPredicate predicate = CustomPredicate.procurePredicate("salaryType.businessClientId",this.businessCertificate.getBusinessClientInstId());
		List<CustomPredicate> list = new ArrayList<CustomPredicate>();
		list.add(predicate);
        List<SalaryInfo> wList = genericService.loadAllObjectsUsingRestrictions(SalaryInfo.class,list,"salaryType.name");

		salaryInfoMap = IppmsUtils.makeHasMapFromList(wList);
	}

	public synchronized WageBeanContainer generateModelData() throws Exception {

		WageBeanContainer wBEOB = new WageBeanContainer();

		// Set this so we have a handle to the begin dates as string.
		wBEOB.setPoliticoSalary(this.salary);
		wBEOB.setPoliticoTax(this.tax);
		wBEOB.setMonth(this.month);
		wBEOB.setYear(this.year);
		init();
		// Set control Maps.
		setControlMaps();

		List<AbstractPaycheckEntity> wEPBList = (List<AbstractPaycheckEntity>) this.paycheckService.loadEmployeePayBeanByRunMonthAndRunYear(this.businessCertificate,this.month, this.year);

		List<WageSummaryBean> wRetList = new ArrayList<WageSummaryBean>();

		wBEOB.setMonthAndYearStr(
				PayrollBeanUtils.getMonthNameFromInteger(this.month) + "," + this.year);

		int serialNum = 0;
		for (AbstractPaycheckEntity e : wEPBList) {

			// First things first -- Get the SalaryInfo
			SalaryInfo s = this.salaryInfoMap.get(e.getSalaryInfo().getId());
			if (s == null || s.isNewEntity())
				throw new Exception("Salary Information not found for Paycheck ID " + e.getId());

			WageSummaryBean wWSB = null;

			if (this.agencyMap.containsKey(e.getMdaDeptMap().getMdaInfo().getId())) {
				wWSB = this.agencyMap.get(e.getMdaDeptMap().getMdaInfo().getId());

			} else {
				wWSB = new WageSummaryBean();

				wWSB.setSerialNum(++serialNum);
				wWSB.setAssignedToObject(e.getMdaDeptMap().getMdaInfo().getName());

			}
			wWSB = this.makePaymentInformation(wWSB, s, e);

			this.agencyMap.put(e.getMdaDeptMap().getMdaInfo().getId(), wWSB);

		}
		WageSummaryBean wPoliticos = new WageSummaryBean();
		wPoliticos.setSerialNum(++serialNum);
		wPoliticos.setAssignedToObject("Public Office Holders");
		wPoliticos.setBasicSalary(Double.parseDouble(PayrollHRUtils.removeCommas(this.salary)));
		wPoliticos.setGrossAmount(wPoliticos.getBasicSalary());

		wPoliticos.setPaye(Double.parseDouble(PayrollHRUtils.removeCommas(this.tax)));

		wPoliticos.setNetPay(wPoliticos.getBasicSalary() - wPoliticos.getPaye());

		wRetList = getWageSummaryBeanFromMap(this.agencyMap, wRetList, true);

		wBEOB = makeTotals(wRetList, wBEOB);

		wBEOB.setTotalBasicSalary(wBEOB.getTotalBasicSalary() + wPoliticos.getBasicSalary());
		wBEOB.setTotalNetPay(wBEOB.getTotalNetPay() + wPoliticos.getNetPay());
		wBEOB.setTotalPaye(wBEOB.getTotalPaye() + wPoliticos.getPaye());
		Collections.sort(wRetList);
		// Now Make this guys spit out their totals.

		wBEOB.setWageSummaryBeanList(wRetList);
		wBEOB.getWageSummaryBeanList().add(wPoliticos);

		return wBEOB;

	}

	private WageBeanContainer makeTotals(List<WageSummaryBean> pRetList, WageBeanContainer pBEOB) {
		for (WageSummaryBean w : pRetList) {
			w.setDisplayStyle("reportOdd");
			pBEOB.setTotalBasicSalary(pBEOB.getTotalBasicSalary() + w.getBasicSalary());
			pBEOB.setTotalRent(pBEOB.getTotalRent() + w.getRent());
			pBEOB.setTotalTransport(pBEOB.getTotalTransport() + w.getTransport());
			pBEOB.setTotalMeal(pBEOB.getTotalMeal() + w.getMeal());
			pBEOB.setTotalUtility(pBEOB.getTotalUtility() + w.getUtility());
			pBEOB.setTotalInducement(pBEOB.getTotalInducement() + w.getInducement());
			pBEOB.setTotalRuralPosting(pBEOB.getTotalRuralPosting() + w.getRuralPosting());
			pBEOB.setTotalMedicals(pBEOB.getTotalMedicals() + w.getMedicals());
			pBEOB.setTotalHardship(pBEOB.getTotalHardship() + w.getHardship());
			pBEOB.setTotalHazard(pBEOB.getTotalHazard() + w.getHazard());
			pBEOB.setTotalCallDuty(pBEOB.getTotalCallDuty() + w.getCallDuty());
			pBEOB.setTotalJournal(pBEOB.getTotalJournal() + w.getJournal());
			pBEOB.setTotalDomServ(pBEOB.getTotalDomServ() + w.getDomesticServant());
			pBEOB.setTotalEntAllow(pBEOB.getTotalEntAllow() + w.getEntertainmentAllowance());
			pBEOB.setTotalAccAllow(pBEOB.getTotalAccAllow() + w.getAcademicAllowance());
			pBEOB.setTotalOutfit(pBEOB.getTotalOutfit() + w.getOutfit());
			pBEOB.setTotalTorchLight(pBEOB.getTotalTorchLight() + w.getTorchLight());
			pBEOB.setTotalTss(pBEOB.getTotalTss() + w.getTss());
			pBEOB.setTotalAdminAllowance(pBEOB.getTotalAdminAllowance() + w.getAdminAllowance());
			pBEOB.setTotalDriversAllowance(pBEOB.getTotalDriversAllowance() + w.getDriversAllowance());
			pBEOB.setTotalFurniture(pBEOB.getTotalFurniture() + w.getFurniture());
			pBEOB.setTotalConsolidated(pBEOB.getTotalConsolidated() + w.getConsolidated());
			pBEOB.setTotalSecurity(pBEOB.getTotalSecurity() + w.getSecurityAllowance());
			pBEOB.setTotalOtherAllowances(pBEOB.getTotalOtherAllowances() + w.getOtherAllowances());
			pBEOB.setTotalGrossSalary(pBEOB.getTotalGrossSalary() + w.getGrossAmount());
			pBEOB.setTotalTws(pBEOB.getTotalTws() + w.getTws());
			pBEOB.setTotalUnionDues(pBEOB.getTotalUnionDues() + w.getUnionDues());
			pBEOB.setTotalNhf(pBEOB.getTotalNhf() + w.getNhf());
			pBEOB.setTotalPaye(pBEOB.getTotalPaye() + w.getPaye());
			pBEOB.setTotalDeductions(pBEOB.getTotalDeductions() + w.getTotalDeductions());
			pBEOB.setTotalNetPay(pBEOB.getTotalNetPay() + w.getNetPay());
		}

		return pBEOB;
	}

	private WageSummaryBean makePaymentInformation(WageSummaryBean pWSB, SalaryInfo pS, AbstractPaycheckEntity pEPB) {
		pWSB.setBasicSalary(pWSB.getBasicSalary() + pS.getMonthlyBasicSalary());
		pWSB.setRent(pWSB.getRent() + pS.getRent());
		pWSB.setTransport(pWSB.getTransport() + pS.getTransport());
		pWSB.setMeal(pWSB.getMeal() + pS.getMeal());
		pWSB.setUtility(pWSB.getUtility() + pS.getUtility());
		pWSB.setInducement(pWSB.getInducement() + pS.getInducement());
		pWSB.setRuralPosting(pWSB.getRuralPosting() + pS.getRuralPosting());
		pWSB.setMedicals(pWSB.getMedicals() + pS.getMedicals());
		pWSB.setHardship(pWSB.getHardship() + pS.getHardship());
		pWSB.setHazard(pWSB.getHazard() + pS.getHazard());
		pWSB.setCallDuty(pWSB.getCallDuty() + pS.getCallDuty());
		pWSB.setJournal(pWSB.getJournal() + pS.getJournal());
		pWSB.setDomesticServant(pWSB.getDomesticServant() + pS.getDomesticServant());
		pWSB.setEntertainmentAllowance(pWSB.getEntertainmentAllowance() + pS.getEntertainment());
		//pWSB.setAcademicAllowance(pWSB.getAcademicAllowance());
		pWSB.setOutfit(pWSB.getOutfit() + pS.getOutfit());
		pWSB.setTorchLight(pWSB.getTorchLight() + pS.getToolsTorchLightAllowance());
		
		pWSB.setGrossAmount(pWSB.getGrossAmount() + pS.getTotalGrossPay());
		//pWSB.setTws(pWSB.getTws() + pS.getTws());
		pWSB.setUnionDues(pWSB.getUnionDues() + pEPB.getUnionDues());
		pWSB.setNhf(pWSB.getNhf() + pS.getNhf());
		pWSB.setPaye(pWSB.getPaye() + pEPB.getTaxesPaid());
		pWSB.setTotalDeductions(
				pWSB.getTotalDeductions() + (pS.getNhf() + pEPB.getTaxesPaid() + pEPB.getUnionDues()));
		pWSB.setAdminAllowance(pWSB.getAdminAllowance() + pS.getAdminAllowance());
		pWSB.setDriversAllowance(pWSB.getDriversAllowance() + pS.getDriversAllowance());
		pWSB.setFurniture(pWSB.getFurniture() + pS.getFurniture());
		pWSB.setConsolidated(pWSB.getConsolidated() + pS.getConsolidated());
		pWSB.setSecurityAllowance(pWSB.getSecurityAllowance() + pS.getSecurityAllowance());
		pWSB.setOtherAllowances(pWSB.getOtherAllowances()
				+ (pEPB.getSpecialAllowance() + pEPB.getLeaveTransportGrant() + pEPB.getContractAllowance()));
		pWSB.setNetPay(pWSB.getNetPay() + pEPB.getNetPay());
		return pWSB;
	}

	private List<WageSummaryBean> getWageSummaryBeanFromMap(HashMap<Long, WageSummaryBean> agencyMap2,
			List<WageSummaryBean> pRetList, boolean pSetDisplay) {

		Set<Entry<Long, WageSummaryBean>> set = agencyMap2.entrySet();
		Iterator<Entry<Long, WageSummaryBean>> i = set.iterator();

		while (i.hasNext()) {
			Entry<Long, WageSummaryBean> me = i.next();

			me.getValue().setDisplayStyle("reportOdd");

			pRetList.add(me.getValue());

			// wSize++;
		}

		return pRetList;
	}

}