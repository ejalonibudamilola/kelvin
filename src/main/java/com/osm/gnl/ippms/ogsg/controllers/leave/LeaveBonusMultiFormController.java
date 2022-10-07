package com.osm.gnl.ippms.ogsg.controllers.leave;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.ChartService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.base.services.LeaveReportService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.chart.ChartController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.chart.ChartMiniBean;
import com.osm.gnl.ippms.ogsg.domain.chart.SingleChartBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportHeaders;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusBean;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusMasterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author Damilola Ejalonibu
 */
@Controller
public class LeaveBonusMultiFormController extends BaseController {

	@Autowired
	LeaveReportService leaveService;

	@Autowired
	HRService hrService;

	@Autowired
	ChartService chartService;

	public LeaveBonusMultiFormController() {

	}


	@RequestMapping({"/leaveBonusByMdaExcel.do"})
	public ModelAndView generateLeaveBonusMDAExcel(@RequestParam("yr") int pYear,
												   @RequestParam("mdaInd") Long pMdaInd, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionManagerService.manageSession(request, model);


		List<LeaveBonusBean> wList = this.leaveService.loadLeaveBonusDetailsByMdaAndYear(pMdaInd, pYear);
		double wTotalAmount = 0.0D;
		int wTotalStaff = 0;
		for (LeaveBonusBean l : wList) {
			wTotalAmount += l.getLeaveBonusAmount();
			wTotalStaff += 1;
		}
		LeaveBonusMasterBean wLBMB = wList.get(0).getLeaveBonusMasterBean();

		wLBMB.setTotalAmountPaid(wTotalAmount);
		wLBMB.setTotalNoOfEmp(wTotalStaff);
		wLBMB.setLeaveBonusList(wList);
		wLBMB.setRunYear(pYear);
		addRoleBeanToModel(model, request);
		return new ModelAndView("leaveBonusDetailsExcelView", "leaveBonusBean", wLBMB);
	}

	@RequestMapping({"/leaveBonusExcel.do"})
	public ModelAndView generateLeaveBonusExcel(@RequestParam("pid") Long pPid, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);


//	    LeaveBonusMasterBean wLBMB = (LeaveBonusMasterBean) this.payrollService.loadObjectByClassAndId(LeaveBonusMasterBean.class, pPid);
		LeaveBonusMasterBean wLBMB = this.genericService.loadObjectById(LeaveBonusMasterBean.class, pPid);

		if (!wLBMB.isNewEntity()) {
			if (!wLBMB.getLastModBy().equalsIgnoreCase(bc.getUserName()))
				bc.setCanApproveLeaveBonus(true);
		}

		wLBMB.setLeaveBonusList(this.leaveService.loadLeaveBonusByParentId(pPid));
		addRoleBeanToModel(model, request);

		return new ModelAndView("leaveBonusDetailsExcelView", "leaveBonusBean", wLBMB);
	}

	@RequestMapping({"/leaveBonusReportExcel.do"})
	public void generateLeaveBonusReportExcel(@RequestParam("pid") int pYear,
											  Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionManagerService.manageSession(request, model);

		SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

		BusinessCertificate bc = super.getBusinessCertificate(request);
		List<LeaveBonusMasterBean> empList = this.leaveService.loadLeaveBonusMasterBeansForExcelDisplay(pYear);
		List<Map<String, Object>> leaveBonusDataList = new ArrayList<>();
		Map<String, Object> mappedData = new HashMap<>();
		for (LeaveBonusMasterBean data : empList) {
			System.out.println("data .....got here");
			mappedData.put("Approved By", data.getApprovedBy());
			mappedData.put("Created Time", data.getCreatedTime());
			mappedData.put("MDA", bc.getMdaTitle());
			mappedData.put("Approved Date", data.getApprovedDate());
			mappedData.put("Total No Of Emp", data.getTotalNoOfEmp());
			mappedData.put("Total Amount Paid", data.getTotalAmountPaid());
			mappedData.put("Total Leave Bonus", data.getTotalAmountPaid());
			mappedData.put("Run Year", data.getRunYear());
			leaveBonusDataList.add(mappedData);
			System.out.println("data ......." + mappedData.toString());
		}


//	    double wTotalLeaveBonus= 0.0D;
//	    int wTotalEmp = 0;
//	    for(LeaveBonusMasterBean l :empList){
//	    	wTotalLeaveBonus += l.getTotalAmountPaid();
//	    	wTotalEmp += l.getTotalNoOfEmp();
//	    }
//	    BusinessEmpOVBeanInactive wBean = new BusinessEmpOVBeanInactive(empList);
//	    wBean.setTotalGrossPay(wTotalLeaveBonus);
//	    wBean.setToYear(wTotalEmp);
//	    //TODO -- Might be a problem. Check Later
//
//	    wBean.setId(new Long(pYear));
//	    return new ModelAndView("leaveBonusReportExcelView", "leaveBonusMasterListBean", wBean);


//	    return new ModelAndView("leaveBonusReportExcelView", "leaveBonusMasterListBean", empList);


		ArrayList<ReportHeaders> headerList = new ArrayList<ReportHeaders>();
		headerList.add(new ReportHeaders("Approved By", 0));
		headerList.add(new ReportHeaders("Created Time", 0));
		headerList.add(new ReportHeaders(bc.getMdaTitle(), 0));
		headerList.add(new ReportHeaders("Approved Date", 0));
		headerList.add(new ReportHeaders("Total No Of Emp", 1));
		headerList.add(new ReportHeaders("Total Amount Paid", 1));
		headerList.add(new ReportHeaders("Total Leave Bonus", 1));
		headerList.add(new ReportHeaders("Run Year", 0));

		List<Map<String, Object>> leaveBonusHeaders = new ArrayList<>();
		for (ReportHeaders head : headerList) {
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			leaveBonusHeaders.add(mappedHeader);
		}


		List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add("Leave Bonus Summary");
		mainHeaders.add("Period: November 2020");

		ReportGeneratorBean rt = new ReportGeneratorBean();
		rt.setTableHeaders(leaveBonusHeaders);
		rt.setTableData(leaveBonusDataList);
		rt.setBusinessCertificate(bc);
		rt.setMainHeaders(mainHeaders);
		rt.setTableType(0);
		rt.setGroupBy(null);
		rt.setSubGroupBy(null);
		rt.setTotalInd(1);
		rt.setReportTitle("Leave Bonus Report Summary");
		simpleExcelReportGenerator.getExcel(response, request, rt);

	}
//
	@RequestMapping({"/leaveBonusBarChartReport.do"})
	public ModelAndView generateLeaveChartReport(Model model, HttpServletRequest request) throws Exception{
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);
		PaginationBean paginationBean = getPaginationInfo(request);
		List<LeaveBonusMasterBean> empList = this.hrService.loadLeaveBonusMasterBeansForDisplay((paginationBean.getPageNumber() - 1) * this.pageLength,
				this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(),0);
		String url = "/ogsg_ippms/leaveBonusBarChartReport.do";
		List<String> year = new ArrayList<>();
		List<String> tlb = new ArrayList<>();
		for(LeaveBonusMasterBean l: empList){
			int y = l.getRunYear();
			year.add(String.valueOf(y));
		}
		for(LeaveBonusMasterBean l: empList){
			String lb = l.getTotalLeaveBonusStr();
			tlb.add(lb);
		}

        LinkedHashMap<String, String> displayData = new LinkedHashMap<>();
		ChartController chartController = new ChartController();
		ChartMiniBean cMB = new ChartMiniBean();
		SingleChartBean singleChartBean = new SingleChartBean();
		singleChartBean.setUrl(url);
		singleChartBean.setChartTitle("Leave Bonus Chart");
		singleChartBean.setBarXAxis(year);
		singleChartBean.setBarYAxis(tlb);
		return chartController.chartReport(singleChartBean, request, model, displayData, cMB);

	}

}
