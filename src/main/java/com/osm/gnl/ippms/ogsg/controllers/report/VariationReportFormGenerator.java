package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.services.EmployeeService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.report.ReportBean;
import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.osm.gnl.ippms.ogsg.web.ui.WebHelper.getBusinessCertificate;

/**
 * @author Damilola Ejalonibu
 */
public class VariationReportFormGenerator {


	public Object generateReports(HttpServletRequest request, EmployeeService employeeService, GenericService genericService, LocalDate startDate, Model model, List<ReportBean> repList) throws Exception	{

		int runMonth =  startDate.getMonthValue();
		int runYear = startDate.getYear();

		BusinessCertificate bc = getBusinessCertificate(request);

		LocalDate wCal;
		wCal = startDate;
		LocalDate prevMonthStart = PayrollBeanUtils.getPreviousMonthDate(wCal, false);

		int prevRunMonth =  prevMonthStart.getMonthValue();
		int prevRunYear =  prevMonthStart.getYear();
		

	    VariationReportBean wLTGH = new VariationReportBean();
	    
	    wLTGH.setReportsList(repList);
	    wLTGH.setMonthList(PayrollBeanUtils.makeAllMonthList());
		List<Integer> runYears = new ArrayList<>();
	    try {
			runYears = employeeService.getAllRunYears(bc);
		} catch (NullPointerException e) {
			runYears = employeeService.getAllRunYears(bc);
		}
		wLTGH.setYearList(runYears);

		PredicateBuilder predicateBuilder = new PredicateBuilder();
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runMonth", runMonth));
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear", runYear));

		double tGross;
		try {
			tGross = genericService.sumFieldUsingPredicateBuilder(IppmsUtils.getPaycheckClass(bc), predicateBuilder, Double.class, "totalPay", Arrays.asList("runMonth", "runYear"));
	} catch (NullPointerException e) {
			tGross = 0.0D;
		}
		double tNet;
		try {
			tNet = genericService.sumFieldUsingPredicateBuilder(IppmsUtils.getPaycheckClass(bc), predicateBuilder, Double.class, "netPay", Arrays.asList("runMonth", "runYear"));
		} catch (NullPointerException e) {
			tNet = 0.0D;
		}
		wLTGH.setThisMonthGross(tGross);
	    wLTGH.setThisMonthNet(tNet);

	    PredicateBuilder predicateBuilder1 = new PredicateBuilder();
	    predicateBuilder1.addPredicate(CustomPredicate.procurePredicate("runMonth", prevRunMonth));
	    predicateBuilder1.addPredicate(CustomPredicate.procurePredicate("runYear", prevRunYear));

	    double pGross = 0.0D;
		try {
			pGross = genericService.sumFieldUsingPredicateBuilder(IppmsUtils.getPaycheckClass(bc), predicateBuilder1, Double.class, "totalPay", Arrays.asList("runMonth", "runYear"));
		} catch (NullPointerException e) {
			pGross = 0.0D;
		}

		double pNet = 0.0D;
		try {
			pNet = genericService.sumFieldUsingPredicateBuilder(IppmsUtils.getPaycheckClass(bc), predicateBuilder1, Double.class, "netPay", Arrays.asList("runMonth", "runYear"));
		}catch(NullPointerException e)  {
			pNet = 0.0D;
		}
	    wLTGH.setPrevMonthGross(pGross);
	    wLTGH.setPrevMonthNet(pNet);


	    double grossDifference = tGross - pGross;
	    double netDifference = tNet - pNet;
	    
	    wLTGH.setNetDifference(netDifference);
	    wLTGH.setGrossDifference(grossDifference);
    
	    wLTGH.setCurrDateStr(PayrollHRUtils.getMonthYearDateFormat().format(startDate));
	    wLTGH.setPrevDateStr(PayrollHRUtils.getMonthYearDateFormat().format(prevMonthStart));
	    
	    wLTGH.setFromDate(startDate);
	  //  wLTGH.setToDate(endDate);
	    wLTGH.setRunMonth(runMonth);
	    wLTGH.setRunYear(runYear);
	    
	    model.addAttribute("profileBean", wLTGH);
	    
		return model;
	}
	
}
