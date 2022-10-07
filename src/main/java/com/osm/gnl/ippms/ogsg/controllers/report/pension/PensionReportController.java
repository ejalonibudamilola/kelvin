package com.osm.gnl.ippms.ogsg.controllers.report.pension;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.PensionReportService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.report.PensionBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class PensionReportController extends BaseController {

    @Autowired
    PensionReportService pensionReportService;

    @RequestMapping({"/pensionListingReport.do"})
    public void generateContributoryPensionByMDA(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                                                 @RequestParam("rt") int reportType, Model model,
                                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = super.getBusinessCertificate(request);
        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();
        PdfSimpleGeneratorClass simple = new PdfSimpleGeneratorClass();
        List<PensionBean> bList = pensionReportService.pensionListing(bc, pRunMonth, pRunYear);
        Collections.sort(bList, Comparator.comparing(PensionBean::getId));
        List<Map<String, Object>> bankSummaryList = new ArrayList<>();
        for (PensionBean data : bList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Category", data.getCategory());
            newData.put("Number Of "+bc.getStaffTypeName()+"s", data.getTotalPensioners());
            newData.put("Cumulative "+bc.getStaffTypeName()+"s", data.getCumulativeTotalPensioners());
            newData.put("Amount", data.getAmount());
            newData.put("Cumulative Amount", data.getCumulativeTotalAmount());
            bankSummaryList.add(newData);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean("Category", 0));
        list.add(new ReportGeneratorBean("Number Of "+bc.getStaffTypeName()+"s", 1));
        list.add(new ReportGeneratorBean("Cumulative "+bc.getStaffTypeName()+"s", 1));
        list.add(new ReportGeneratorBean("Amount", 2));
        list.add(new ReportGeneratorBean("Cumulative Amount", 2));

        List<Map<String, Object>> BankHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : list) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            BankHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getBusinessName() + " - Pension Listing");
        mainHeaders.add("Summary For: " + PayrollBeanUtils.getMonthNameFromInteger(pRunMonth) + ", " + pRunYear);

        if (reportType == 1) {
            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setReportTitle("Pension Listing");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(bankSummaryList);
            rt.setTableHeaders(BankHeaders);
            rt.setTableType(0);
            rt.setTotalInd(1);
            simpleExcelReportGenerator.getExcel(response, request, rt);
        } else {
            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setReportTitle("Pension Listing");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(bankSummaryList);
            rt.setTableHeaders(BankHeaders);
            rt.setTableType(0);
            rt.setTotalInd(1);
            rt.setOutputInd(true);
            rt.setWatermark(bc.getBusinessName()+" Pension Listing");

            simple.getPdf(response, request, rt);
        }
    }

    @RequestMapping({"/deceasedPensionersReport.do"})
    public void deceasedPensionersReport(@RequestParam ("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("rt") int reportType,
                                         Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BusinessCertificate bc = super.getBusinessCertificate(request);
        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();
        PdfSimpleGeneratorClass simple = new PdfSimpleGeneratorClass();
        List<PensionBean> bList = pensionReportService.deceasedPensioners(bc, pRunMonth, pRunYear);
        Collections.sort(bList, Comparator.comparing(PensionBean::getId));
        List<Map<String, Object>> bankSummaryList = new ArrayList<>();
        for (PensionBean data : bList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
            newData.put("Annual Rate Of Pay", (data.getAmount() * 12));
            newData.put("Month Rate Of Pay", data.getAmount());
            newData.put("Local Government", data.getLgaName());
            bankSummaryList.add(newData);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        list.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        list.add(new ReportGeneratorBean("Annual Rate Of Pay", 2));
        list.add(new ReportGeneratorBean("Month Rate Of Pay", 2));
        list.add(new ReportGeneratorBean("Local Government", 0));

        List<Map<String, Object>> BankHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : list) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            BankHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getBusinessName() + " - Deceased Pensioners");
        mainHeaders.add("Summary For: " + PayrollBeanUtils.getMonthNameFromInteger(pRunMonth) + ", " + pRunYear);

        if (reportType == 1) {
            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setReportTitle("Deceased Pensioners");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(bankSummaryList);
            rt.setTableHeaders(BankHeaders);
            rt.setTableType(0);
            rt.setTotalInd(1);
            simpleExcelReportGenerator.getExcel(response, request, rt);
        } else {
            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setReportTitle("Deceased Pensioners");
            rt.setWatermark(bc.getBusinessName()+" Deceased Pensioners");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setReportTitle(bc.getBusinessName()+" Deceased Pensioners");
            rt.setTableData(bankSummaryList);
            rt.setTableHeaders(BankHeaders);
            rt.setTableType(0);
            rt.setTotalInd(1);
            rt.setOutputInd(true);

            simple.getPdf(response, request, rt);
        }

    }

}
