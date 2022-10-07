package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs.SubGroupedPdf;
import com.osm.gnl.ippms.ogsg.controllers.report.service.BankService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranchAsBank;
import com.osm.gnl.ippms.ogsg.domain.report.BankScheduleSummary;
import com.osm.gnl.ippms.ogsg.domain.report.GrossSummary;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
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
public class BankMultiFormController extends BaseController {

    private final PayrollService payrollServiceExt;
    private final BankService bankService;

    @Autowired
    public BankMultiFormController(PayrollService payrollServiceExt, BankService bankService) {
        this.payrollServiceExt = payrollServiceExt;
        this.bankService = bankService;
    }

    @RequestMapping({"/OgunStateBankDetailExcel.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                          @RequestParam("bpv") String pBank, @RequestParam("rt") int reportType, @RequestParam("sGL") int fromLevel,
                          @RequestParam("eGL") int toLevel, @RequestParam("bank") String bank, Model model,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = super.getBusinessCertificate(request);
        ReportGeneratorBean rt = new ReportGeneratorBean();

        List<BankScheduleSummary> bList = bankService.bankScheduleSummary(bc, pRunMonth, pRunYear, fromLevel, toLevel, bank);
        HashMap<Long, BankBranchAsBank> wBankAsMapList = this.genericService.loadObjectAsMapWithConditions(BankBranchAsBank.class, Arrays.asList(getBusinessClientIdPredicate(request)), "branchInstId");

        List<BankScheduleSummary> bankList;
        HashMap<String, BankScheduleSummary> filterMap = new HashMap<>();
        BankScheduleSummary b2;
        for (BankScheduleSummary b : bList) {
            if (wBankAsMapList.containsKey(b.getBankBranchId()))
                b.setBankName(b.getBankBranch());


            if (filterMap.containsKey(b.getBankName())) {
                b2 = filterMap.get(b.getBankName());
                b2.setTotalBankAmount(b2.getTotalBankAmount() + b.getPayableAmount());
                filterMap.put(b.getBankName(), b2);
            } else {
                b.setTotalBankAmount(b.getPayableAmount());
                filterMap.put(b.getBankName(), b);
            }
        }
        bankList = new ArrayList<>(filterMap.values());
        Collections.sort(bankList);
        //we need to split by Banks and then By Branches
        List<Map<String, Object>> bankSummaryList = new ArrayList<>();

        HashMap<String, Integer> uniqueSet = new HashMap<>();

        int i = 1;
        Map<String, Object> newData;
        for (BankScheduleSummary data : bankList) {
            newData = new HashMap<>();
            newData.put("Bank Name", data.getBankName());
            newData.put("NetPay", data.getTotalBankAmount());
            bankSummaryList.add(newData);
            uniqueSet.put(data.getBankName(), i++);
        }


        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean("Bank Name", 0));
        list.add(new ReportGeneratorBean("NetPay", 2));
        list.add(new ReportGeneratorBean("Bank Name", 3));

        List<Map<String, Object>> BankHeaders = new ArrayList<>();
        Map<String, Object> mappedHeader;
        for (ReportGeneratorBean head : list) {
            mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            BankHeaders.add(mappedHeader);
        }
        //Now create a new List for the Bank Branches...
        List<Map<String, Object>> bankBranchSummaryList = new ArrayList<>();
        Collections.sort(bList, Comparator.comparing(BankScheduleSummary::getBankBranch));
        for (BankScheduleSummary data : bList) {
            if (wBankAsMapList.containsKey(data.getBankBranchId()))
                //Special Scenario for the Branch to be Reported as a Bank.
                data.setBankName(data.getBankBranch());

            newData = new HashMap<>();
            newData.put("Branch Name", data.getBankBranch());
            newData.put("NetPay", data.getPayableAmount());
            newData.put("Bank Name", data.getBankName());
            bankBranchSummaryList.add(newData);
            uniqueSet.put(data.getBankName(), i++);
        }

        list = new ArrayList<>();
        list.add(new ReportGeneratorBean("Branch Name", 0));
        list.add(new ReportGeneratorBean("NetPay", 2));
        list.add(new ReportGeneratorBean("Bank Name", 3));

        List<Map<String, Object>> branchHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : list) {
            mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            branchHeaders.add(mappedHeader);
        }
        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getBusinessName() + " - BANK SCHEDULE SUMMARY");
        List<String> mainHeaders2 = new ArrayList<>();
        mainHeaders2.add("Pay Period: " + PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
        rt.setMainHeaders2(mainHeaders2);
        if (reportType == 1) {
            rt.setBusinessCertificate(bc);
            rt.setGroupBy("Bank Name");
            rt.setReportTitle("BANK SCHEDULE SUMMARY_" + bc.getOrgFileNameDiff() + PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth, pRunYear, true, false));
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setUseTableSubData(true);
            rt.setTableData(bankSummaryList);
            rt.setTableSubData(bankBranchSummaryList);
            rt.setTableSubHeaders(branchHeaders);
            rt.setTableHeaders(BankHeaders);
            rt.setTableType(1);
            rt.setTotalInd(1);
            rt.setGroupedKeySet(uniqueSet.keySet());

            new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
        } else {
            rt.setBusinessCertificate(bc);
            rt.setGroupBy("Bank Name");
            rt.setReportTitle("BANK SCHEDULE SUMMARY");
            rt.setMainHeaders2(mainHeaders2);
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(bankSummaryList);
            rt.setTableHeaders(BankHeaders);
            rt.setTableType(1);
            rt.setTotalInd(1);

            rt.setOutputInd(true);

            new PdfSimpleGeneratorClass().getPdf(response, request, rt);
        }


    }

    @RequestMapping({"/bankSummaryReport.do"})
    public void bankSummaryReport(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                                  @RequestParam("rt") int reportType, Model model,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = super.getBusinessCertificate(request);
        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();
        PdfSimpleGeneratorClass simple = new PdfSimpleGeneratorClass();
        List<BankScheduleSummary> bList = bankService.bankSummaryByBanks(bc, pRunMonth, pRunYear);
        List<Map<String, Object>> bankSummaryList = new ArrayList<>();
        for (BankScheduleSummary data : bList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Bank Name", data.getBankName());
            newData.put("Amount", data.getPayableAmount());
            newData.put("Total Staff", data.getTotalStaff());
            bankSummaryList.add(newData);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean("Bank Name", 0));
        list.add(new ReportGeneratorBean("Amount", 2));
        list.add(new ReportGeneratorBean("Total Staff", 1));

        List<Map<String, Object>> BankHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : list) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            BankHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getBusinessName() + " - BANK SUMMARY");
        mainHeaders.add("Summary For: " + PayrollBeanUtils.getMonthNameFromInteger(pRunMonth) + ", " + pRunYear);

        if (reportType == 1) {
            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setReportTitle("BANK SUMMARY");
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
            rt.setReportTitle("BANK SUMMARY");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(bankSummaryList);
            rt.setTableHeaders(BankHeaders);
            rt.setTableType(0);
            rt.setTotalInd(1);
            rt.setOutputInd(true);
            rt.setWatermark(bc.getMdaTitle() + "Bank Summary");

            simple.getPdf(response, request, rt);
        }
    }

    @RequestMapping({"/grossSummaryReport.do"})
    public void grossSummaryReport(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear,
                                   @RequestParam("rt") int reportType, Model model,
                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = super.getBusinessCertificate(request);
        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();
        PdfSimpleGeneratorClass simple = new PdfSimpleGeneratorClass();
        List<GrossSummary> bList = bankService.grossSummaryByLga(bc, pRunMonth, pRunYear);
        List<Map<String, Object>> bankSummaryList = new ArrayList<>();
        for (GrossSummary data : bList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("Local Government Name", data.getLgaName());
            newData.put("Gross Pay", data.getGrossPay());
            newData.put("Total Staff", data.getTotalStaff());
            bankSummaryList.add(newData);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean("Local Government Name", 0));
        list.add(new ReportGeneratorBean("Gross Pay", 2));
        list.add(new ReportGeneratorBean("Total Staff", 1));

        List<Map<String, Object>> BankHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : list) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            BankHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getBusinessName() + " - GROSS SUMMARY (ALL) REPORT");
        mainHeaders.add("Summary For: " + PayrollBeanUtils.getMonthNameFromInteger(pRunMonth) + ", " + pRunYear);

        if (reportType == 1) {
            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setReportTitle("GROSS SUMMARY");
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
            rt.setReportTitle("GROSS SUMMARY");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(bankSummaryList);
            rt.setTableHeaders(BankHeaders);
            rt.setTableType(0);
            rt.setTotalInd(1);
            rt.setOutputInd(true);

            simple.getPdf(response, request, rt);
        }


    }

    @RequestMapping({"/bankDetailedMda.do"})
    public void bankScheduleDetailed(@RequestParam("rm") int wRunMonth,
                                     @RequestParam("ry") int wRunYear, @RequestParam("rt") int reportType, @RequestParam("sGL") int fromLevel,
                                     @RequestParam("eGL") int toLevel, @RequestParam("bank") String bank, @RequestParam("fb") int pForBank, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {

        PdfSimpleGeneratorClass simple = new PdfSimpleGeneratorClass();
        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();
        BusinessCertificate bc = this.getBusinessCertificate(request);

        List<EmployeePayBean> empBeanList;
//            List<BankScheduleDetailed> bList = bankService.bankScheduleDetailedMda(wRunMonth,
//                    wRunYear, bc);

        empBeanList = this.payrollServiceExt.loadEmployeePayBeanByParentIdGlAndBank(wRunMonth, wRunYear, fromLevel, toLevel, bank, bc, false);
        ReportGeneratorBean rt = new ReportGeneratorBean();

        Collections.sort(empBeanList, Comparator.comparing(EmployeePayBean::getBankName).thenComparing(EmployeePayBean::getBranchName).thenComparing(EmployeePayBean::getFullName));

        LinkedHashMap<String, Integer> uniqueSet = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> subUniqueSet = new LinkedHashMap<>();

        List<Map<String, Object>> bankSummaryList = new ArrayList<>();
        int i = 1;
        Map<String, Object> newData;
        for (EmployeePayBean data : empBeanList) {
            newData = new HashMap<>();
            newData.put(bc.getStaffTypeName() + " Name", data.getEmployee().getDisplayName());
            newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());

            newData.put("Account No", data.getAccountNumber());
            if (bc.isPensioner() && pForBank == 0) {
                newData.put("Monthly Pension", data.getMonthlyPension());
                newData.put("Arrears", data.getSpecialAllowance());
                newData.put("Total Deductions", data.getTotalDeductions());
            }
            newData.put("Payable Amount", data.getNetPay());
            newData.put("Bank Name", data.getBankName());
            newData.put("Branch Name", data.getBranchName());
            uniqueSet.put(data.getBankName(), i++);
            subUniqueSet.put(data.getBranchName(), i++);
            bankSummaryList.add(newData);

        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName() + " Name", 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));

        tableHeaders.add(new ReportGeneratorBean("Account No", 0));
        if (bc.isPensioner() && pForBank == 0) {
            tableHeaders.add(new ReportGeneratorBean("Monthly Pension", 2));
            tableHeaders.add(new ReportGeneratorBean("Arrears", 2));
            tableHeaders.add(new ReportGeneratorBean("Total Deductions", 2));
        }
        tableHeaders.add(new ReportGeneratorBean("Payable Amount", 2));
        tableHeaders.add(new ReportGeneratorBean("Bank Name", 3));
        tableHeaders.add(new ReportGeneratorBean("Branch Name", 3));


        List<Map<String, Object>> bankHeaders = new ArrayList<>();
        Map<String, Object> mappedHeader;
        for (ReportGeneratorBean head : tableHeaders) {
            mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            bankHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        if (bc.isPensioner()) {
            if (pForBank == 0) {
                mainHeaders.add(bc.getBusinessName() + " - VOUCHER BY BANKS DETAILED");
                rt.setReportTitle("VOUCHER BY BANKS DETAILED" + bc.getOrgFileNameDiff());
            } else {
                mainHeaders.add(bc.getBusinessName() + " - VOUCHER BY BANKS");
                rt.setReportTitle("VOUCHER BY BANKS" + bc.getOrgFileNameDiff());
            }
        } else {
            mainHeaders.add(bc.getBusinessName() + " - BANK SCHEDULE DETAILED");
            rt.setReportTitle("BANK SCHEDULE DETAILED" + bc.getOrgFileNameDiff());
        }

        List<String> mainHeaders2 = new ArrayList<>();
        mainHeaders2.add("Pay Period: " + PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wRunMonth, wRunYear));

        rt.setBusinessCertificate(bc);
        rt.setGroupBy("Bank Name");
        rt.setMainHeaders(mainHeaders);
        rt.setMainHeaders2(mainHeaders2);
        rt.setTableData(bankSummaryList);
        rt.setTableHeaders(bankHeaders);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());
        rt.setSubGroupedKeySet(subUniqueSet.keySet());
        if (reportType == 2) {
            if (bc.isPensioner()) {
                if (pForBank == 0)
                    rt.setWatermark(bc.getMdaTitle() + " Voucher By Bank Detailed");
                else
                    rt.setWatermark(bc.getMdaTitle() + " Voucher By Bank");
            } else {
                rt.setWatermark(bc.getMdaTitle() + " Bank Schedule Details");
            }
            rt.setSubGroupBy("Branch Name");
            rt.setTableType(2);
            rt.setOutputInd(true);
            rt.setNoWrap(true);

//            simple.getPdf(response, request, rt);
            new SubGroupedPdf().getPdf(response, request, rt);

        } else {
            rt.setTableType(1);

            groupedDataExcelReportGenerator.getExcel(response, request, rt);
        }

    }
}
