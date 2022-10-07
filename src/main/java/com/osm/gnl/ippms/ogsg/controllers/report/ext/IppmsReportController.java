package com.osm.gnl.ippms.ogsg.controllers.report.ext;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.ReportService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.domain.beans.SalarySummaryBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.*;

@Controller
public class IppmsReportController extends BaseController {

    private final ReportService reportService;

    @Autowired
    public IppmsReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @RequestMapping({"/salarySummaryByOrg.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("rt") int reportType, Model model,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        PayrollSummaryBean p = new PayrollSummaryBean();
        LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate toDate = LocalDate.of(fromDate.getYear(), fromDate.getMonthValue(), fromDate.lengthOfMonth());


        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();

        ReportGeneratorBean rt = new ReportGeneratorBean();
        p.setAllowanceStartDateStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(toDate.getMonthValue(), toDate.getYear()));
        p.setPayDate(toDate);
        List<SalarySummaryBean> empBeanList = this.reportService.loadSalarySummary(pRunMonth, pRunYear, bc);
        List<Map<String, Object>> newCompExecSummary = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        Map<String, Object> newData;
        Collections.sort(empBeanList, Comparator.comparing(SalarySummaryBean::getMdaName));
        for (SalarySummaryBean data : empBeanList) {

            newData = new HashMap<>();
            newData.put(bc.getMdaTitle(), data.getMdaName());
            newData.put("Staff Count", data.getStaffCount());
            if(bc.isPensioner()){
                newData.put("Gross Pay", data.getGrossPay());
                newData.put("Monthly Pension", data.getMonthlyPension());
                newData.put("Arrears", data.getTotalAllowance());
                newData.put("Net Deduction", data.getTotalDeduction());
                newData.put("Net Pay", data.getPayableAmount());
            }else{
                newData.put("Staff Count", data.getStaffCount());
                newData.put("Basic Pay", data.getBasicPay());
                newData.put("Total Allow.", data.getTotalAllowance());
                newData.put("Gross Pay", data.getGrossPay());
                newData.put("Tax Paid", data.getTaxPaid());
                if (bc.isSubeb())
                    newData.put("Union Dues Emp.", data.getUnionDues());
                newData.put("Pension " + bc.getStaffTypeName(), data.getPenContEmp());
                newData.put("Pension Employer", data.getPenContEmp());
                if (bc.isSubeb() || bc.isCivilService())
                    newData.put("RBA Employer", data.getRbaPaid());
                newData.put("Total Loan Ded.", data.getTotalLoan());
                newData.put("Other Deduction", data.getOtherDeduction());
                newData.put("Total Deduction", data.getTotalDeduction());
                newData.put("Payable Amount", data.getPayableAmount());
            }

            newCompExecSummary.add(newData);
        }

        //adding table headers
        List<ReportGeneratorBean> headerList = new ArrayList<>();
        headerList.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        headerList.add(new ReportGeneratorBean("Staff Count", 1));
        if(bc.isPensioner()){
            headerList.add(new ReportGeneratorBean("Gross Pay", 2));
            headerList.add(new ReportGeneratorBean("Monthly Pension", 2));
            headerList.add(new ReportGeneratorBean("Arrears", 2));
            headerList.add(new ReportGeneratorBean("Net Deduction", 2));
            headerList.add(new ReportGeneratorBean("Net Pay", 2));
        }else{
            headerList.add(new ReportGeneratorBean("Basic Pay", 2));
            headerList.add(new ReportGeneratorBean("Total Allow.", 2));
            headerList.add(new ReportGeneratorBean("Gross Pay", 2));
            headerList.add(new ReportGeneratorBean("Tax Paid", 2));
            if (bc.isSubeb())
                headerList.add(new ReportGeneratorBean("Union Dues Emp.", 2));
            headerList.add(new ReportGeneratorBean("Pension " + bc.getStaffTypeName(), 2));
            headerList.add(new ReportGeneratorBean("Pension Employer", 2));
            if (bc.isSubeb() || bc.isCivilService())
                headerList.add(new ReportGeneratorBean("RBA Employer", 2));
            headerList.add(new ReportGeneratorBean("Total Loan Ded.", 2));
            headerList.add(new ReportGeneratorBean("Other Deduction", 2));
            headerList.add(new ReportGeneratorBean("Total Deduction", 2));
            headerList.add(new ReportGeneratorBean("Payable Amount", 2));
        }


        //translating table headers to meet report generator requirement
        List<Map<String, Object>> mappedHeadersList = new ArrayList<>();
        for (ReportGeneratorBean head : headerList) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            mappedHeadersList.add(mappedHeader);
        }

        //adding report headers
        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("SALARY SUMMARY BY " + bc.getMdaTitle());
//        mainHeaders.add("Pay Period: " + p.getAllowanceStartDateStr());

        List<String> mainHeaders2 = new ArrayList<>();
        mainHeaders2.add("Pay Period: " + p.getAllowanceStartDateStr());

        //setting report bean
        rt.setTableData(newCompExecSummary);
        rt.setTableHeaders(mappedHeadersList);
        rt.setBusinessCertificate(bc);
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setTableType(0);
        rt.setGroupBy(null);
        rt.setSubGroupBy(null);
        rt.setReportTitle("Salary Summary By " + bc.getMdaTitle());
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());

        if (reportType == 1) {
            groupedDataExcelReportGenerator.getExcel(response, request, rt);
        } else {
            rt.setOutputInd(true);
            pdfSimpleGeneratorClass.getPdf(response, request, rt);
        }
    }

}
