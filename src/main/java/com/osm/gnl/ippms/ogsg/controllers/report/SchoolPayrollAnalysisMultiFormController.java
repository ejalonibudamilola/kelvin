package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.BankService;
import com.osm.gnl.ippms.ogsg.controllers.report.service.SchoolService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.LgaPayrollSummaryExcelGenerator;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class SchoolPayrollAnalysisMultiFormController extends BaseController {

    /**
     * Kasumu Taiwo
     * 12-2020
     */


    private final BankService bankService;
    private final SchoolService schoolService;
    private final PaycheckService paycheckService;
    private final PayrollService payrollService;

    @Autowired
    public SchoolPayrollAnalysisMultiFormController(BankService bankService, SchoolService schoolService, PaycheckService paycheckService, PayrollService payrollService) {
        this.bankService = bankService;
        this.schoolService = schoolService;
        this.paycheckService = paycheckService;
        this.payrollService = payrollService;
    }



    @RequestMapping({"/TscPayrollAnalysisByMdaExcel.do"})
    public void generateTscPayrollAnalysis(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, Model model,
                                           HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        PayrollSummaryBean p = new PayrollSummaryBean();
        LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate toDate = LocalDate.now();
        LocalDate.of(fromDate.getYear(), fromDate.getMonthValue(), fromDate.lengthOfMonth());

        List<EmployeePayBean> empBeanList;

        empBeanList = this.bankService.loadEmployeePayBeanByParentIdFromDateToDate(fromDate.getMonthValue(),
                toDate.getYear(), bc);

        List<Map<String, Object>> bankSummaryList = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        LinkedHashMap<String,List<LinkedHashMap<String, Object>>> filterMap = new LinkedHashMap<>();

        int i = 1;
        LinkedHashMap<String, Object> newData;
        List<LinkedHashMap<String,Object>> dataMapList;
        Collections.sort(empBeanList,Comparator.comparing(EmployeePayBean::getSchoolName).thenComparing(EmployeePayBean::getEmployeeName));
        for (EmployeePayBean data : empBeanList) {
            newData = new LinkedHashMap<>();

            newData.put(bc.getStaffTypeName(), data.getEmployeeName());
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put("Grade/Step", data.getLevelAndStepStr());

            newData.put("Basic", data.getMonthlyBasic());

            newData.put("Total Allowance", data.getTotalAllowance());
            newData.put("Gross Pay", data.getTotalPay());
            newData.put("Tax Paid", data.getTaxesPaid());
            newData.put("Pension ("+bc.getStaffTypeName()+")", data.getTotalContributions());
            newData.put("Total Loan Deduction", data.getTotalGarnishments());
            newData.put("Total Deduction", data.getTotalDeductions());
            newData.put("Account Number", data.getAccountNumber());
            newData.put("Bank", data.getBranchName());
            newData.put("Payable Amount", data.getNetPay());
            //newData.put("Account No.", data.getAccountNumber());
            newData.put("School", data.getSchoolName());
           // newData.put(bc.getMdaTitle(), data.getMda());
            bankSummaryList.add(newData);
            if(filterMap.containsKey(data.getSchoolName())){
                dataMapList = filterMap.get(data.getSchoolName());
                    for(Map map : dataMapList){
                        map.put("Basic",((Double)map.get("Basic"))+data.getMonthlyBasic());
                        map.put("Total Allowance",((Double)map.get("Total Allowance"))+data.getMonthlyBasic());
                        map.put("Gross Pay",((Double)map.get("Gross Pay"))+data.getMonthlyBasic());
                        map.put("Tax Paid",((Double)map.get("Tax Paid"))+data.getMonthlyBasic());
                        map.put("Pension ("+bc.getStaffTypeName()+")",((Double)map.get("Pension ("+bc.getStaffTypeName()+")"))+data.getMonthlyBasic());
                        map.put("Total Loan Deduction",((Double)map.get("Total Loan Deduction"))+data.getMonthlyBasic());
                        map.put("Total Deduction",((Double)map.get("Total Deduction"))+data.getMonthlyBasic());
                        map.put("Payable Amount",((Double)map.get("Payable Amount"))+data.getMonthlyBasic());
                    }
            }else {
                dataMapList = new ArrayList<>();
                newData = new LinkedHashMap<>();
                newData.put("School",data.getSchoolName());
                newData.put("Basic",data.getMonthlyBasic());
                newData.put("Total Allowance",data.getMonthlyBasic());
                newData.put("Gross Pay",data.getMonthlyBasic());
                newData.put("Tax Paid",data.getMonthlyBasic());
                newData.put("Pension ("+bc.getStaffTypeName()+")",data.getMonthlyBasic());
                newData.put("Total Loan Deduction",data.getMonthlyBasic());
                newData.put("Total Deduction",data.getMonthlyBasic());
                newData.put("Payable Amount",data.getMonthlyBasic());
                dataMapList.add(newData);
            }
            filterMap.put(data.getSchoolName(),dataMapList);
            uniqueSet.put(data.getSchoolName(), i++);

        }

        List<ReportGeneratorBean> groupedHeaders = new ArrayList<>();
        groupedHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        groupedHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        groupedHeaders.add(new ReportGeneratorBean("Grade/Step", 0));
        groupedHeaders.add(new ReportGeneratorBean("Basic", 2));
        groupedHeaders.add(new ReportGeneratorBean("Total Allowance", 2));
        groupedHeaders.add(new ReportGeneratorBean("Gross Pay", 2));
        groupedHeaders.add(new ReportGeneratorBean("Tax Paid", 2));
        groupedHeaders.add(new ReportGeneratorBean("Pension (Employee)", 2));
        groupedHeaders.add(new ReportGeneratorBean("Total Loan Deduction", 2));
        groupedHeaders.add(new ReportGeneratorBean("Total Deduction", 2));
        groupedHeaders.add(new ReportGeneratorBean("Account Number", 0));
        groupedHeaders.add(new ReportGeneratorBean("Bank", 0));
        groupedHeaders.add(new ReportGeneratorBean("Payable Amount", 2));
        groupedHeaders.add(new ReportGeneratorBean("School", 3));

        List<Map<String, Object>> subHeaders = new ArrayList<>();
        Map<String, Object> mappedHeader;
        for (ReportGeneratorBean head : groupedHeaders) {
            mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            subHeaders.add(mappedHeader);
        }
        List<ReportGeneratorBean> ungroupedHeaders;
        List<Map<String, Object>> tableHeaders = new ArrayList<>();
        ungroupedHeaders = new ArrayList<>();
        ungroupedHeaders.add(new ReportGeneratorBean("School", 0));
        ungroupedHeaders.add(new ReportGeneratorBean("Basic", 2));
        ungroupedHeaders.add(new ReportGeneratorBean("Total Allowance", 2));
        ungroupedHeaders.add(new ReportGeneratorBean("Gross Pay", 2));
        ungroupedHeaders.add(new ReportGeneratorBean("Tax Paid", 2));
        ungroupedHeaders.add(new ReportGeneratorBean("Pension (Employee)", 2));
        ungroupedHeaders.add(new ReportGeneratorBean("Total Loan Deduction", 2));
        ungroupedHeaders.add(new ReportGeneratorBean("Total Deduction", 2));
        ungroupedHeaders.add(new ReportGeneratorBean("Payable Amount", 2));
        for (ReportGeneratorBean head : ungroupedHeaders) {
            mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            tableHeaders.add(mappedHeader);
        }

       List<Map<String,Object>> wMainList = new ArrayList<>();
        List<String> stringSet = filterMap.keySet().stream().sorted().collect(Collectors.toList());
        for(String s : stringSet)
            wMainList.addAll(filterMap.get(s));

        String addendum = "TSC";
        if(bc.isSubeb())
            addendum = "Schools";
        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("PAYROLL ANALYSIS ("+addendum+") - DETAILED");
        mainHeaders.add("Pay Period : "+PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth,pRunYear));

        rt.setBusinessCertificate(bc);
        rt.setGroupBy("School");
        rt.setReportTitle(bc.getBusinessName()+"PayrollAnalysisBy"+addendum+"Detailed"+PayrollBeanUtils.getMonthNameAndYearForExcelNaming(pRunMonth,pRunYear,true,false)+ PayrollBeanUtils.getCurrentTimeWidoutAMPM());
        rt.setMainHeaders(mainHeaders);
        rt.setTableSubHeaders(subHeaders);
        rt.setTableSubData(bankSummaryList);
        rt.setSubGroupBy(null);
        rt.setUseTableSubData(true);
        rt.setTableData(wMainList);
        rt.setTableHeaders(tableHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());
        groupedDataExcelReportGenerator.getExcel(response, request, rt);
    }


   /* private HashMap<Long, SalaryInfo> setSalaryInfoHashMap(List<SalaryInfo> list) {
        HashMap<Long, SalaryInfo> wRetMap = new HashMap<>();
        for (SalaryInfo s : list) {
            wRetMap.put(s.getId(), s);
        }
        return wRetMap;
    }*/


    @RequestMapping({"/payrollSummaryByLgaExcel.do"})
    public void payrollSummaryByLgaExcel(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear, @RequestParam("sGL") int fromLevel,
                                         @RequestParam("eGL") int toLevel, @RequestParam("bank") String bank, Model model,
                                         HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException, InstantiationException, IllegalAccessException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        LgaPayrollSummaryExcelGenerator lgaPayrollSummaryExcelGenerator = new LgaPayrollSummaryExcelGenerator();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        LocalDate fromDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate.of(fromDate.getYear(), fromDate.getMonthValue(), fromDate.lengthOfMonth());

        List<EmployeePayBean> empBeanList = this.bankService.loadEmployeePayBeanByLgaFromDateToDate(pRunMonth,
                pRunYear, bc, fromLevel, toLevel, bank);

        List<Map<String, Object>> bankSummaryList = new ArrayList<>();
        Double totalDeduction;
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (EmployeePayBean data : empBeanList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTypeName().toUpperCase(), data.getEmployeeName());
            newData.put("DEPARTMENT", data.getDepartmentName());
            newData.put("RANK", data.getRankName());
            newData.put("GL & STEP", data.getRankName());
            newData.put(bc.getStaffTitle().toUpperCase(), data.getEmployeeId());
            newData.put("INCREMENTAL DATE", data.getIncrementalDate()); //not yet set
            newData.put("SALARY TYPE", data.getSalaryTypeName());
            newData.put("BASIC", data.getMonthlyBasic());
            newData.put("SPECIAL ALLOWANCES 1 (AG)", data.getSpecialAllowance());
            newData.put("SPECIAL ALLOWANCES (OTHERS)", data.getOtherAllowance());
            newData.put("TOTAL ALLOWANCE", data.getTotalAllowance());
            newData.put("SALARY ARREARS", data.getArrears());
            newData.put("GROSS PAY", data.getTotalPay());

            //Deductions
            newData.put("TAX", data.getTaxableIncome());
            newData.put("PENSION", data.getContributoryPension());
            newData.put("NULGE", data.getNulge());
            newData.put("MAHWUN", data.getMahwun());
            newData.put("NAHCP", data.getNachp());
            newData.put("NANNM", data.getNannm());
            newData.put("", "");
            newData.put("TOTAL DEDUCTION 1", data.getTotalDeduction1());
            newData.put("NET PAY", (data.getTotalAllowance() - data.getTotalDeduction1()));


            newData.put("NHF", data.getNhf());
            newData.put("ADV", data.getAdv());
            newData.put("ADV NAME", data.getAdvName());
            newData.put("COOP", data.getCoop());
            newData.put("COOP NAME", data.getCoopName());
            newData.put("DEP", data.getDep());
            newData.put("DEP NAME", data.getDepName());
            newData.put("", "");


             totalDeduction = data.getNhf() + data.getAdv() + data.getCoop() + data.getDep() + data.getTotalDeduction1();
            newData.put("TOTAL DEDUCTION", totalDeduction);
            newData.put("NET PAY", data.getTotalAllowance() - totalDeduction);
            newData.put("BANK NAME", data.getBranchName());
            newData.put("ACCOUNT NUMBER", data.getAccountNumber());
            newData.put("BVN", data.getBvnNo());
            newData.put("PFA", data.getPfaInfo().getName());
            newData.put("PENSION PIN", data.getPensionPinCode());
            newData.put("NARRATION", "");
            newData.put("LGA", data.getLgaName());
            bankSummaryList.add(newData);
            uniqueSet.put(data.getLgaName(), i++);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean(bc.getStaffTypeName().toUpperCase(), 0));
        list.add(new ReportGeneratorBean("DEPARTMENT", 0));
        list.add(new ReportGeneratorBean("RANK", 0));
        list.add(new ReportGeneratorBean("GL & STEP", 0));
        list.add(new ReportGeneratorBean(bc.getStaffTitle().toUpperCase(), 0));
        list.add(new ReportGeneratorBean("INCREMENTAL DATE", 0));
        list.add(new ReportGeneratorBean("SALARY TYPE", 0));
        list.add(new ReportGeneratorBean("BASIC", 0));
        list.add(new ReportGeneratorBean("SPECIAL ALLOWANCES 1 (AG)", 2));
        list.add(new ReportGeneratorBean("SPECIAL ALLOWANCES (OTHERS)", 2));
        list.add(new ReportGeneratorBean("TOTAL ALLOWANCE", 2));
        list.add(new ReportGeneratorBean("SALARY ARREARS", 2));
        list.add(new ReportGeneratorBean("GROSS PAY", 2));

        //Deductions
        list.add(new ReportGeneratorBean("TAX", 2));
        list.add(new ReportGeneratorBean("PENSION", 2));
        list.add(new ReportGeneratorBean("NULGE", 2));
        list.add(new ReportGeneratorBean("MAHWUN", 2));
        list.add(new ReportGeneratorBean("NAHCP", 2));
        list.add(new ReportGeneratorBean("NANNM", 2));
        list.add(new ReportGeneratorBean("", 0));
        list.add(new ReportGeneratorBean("TOTAL DEDUCTION 1", 2));
        list.add(new ReportGeneratorBean("NET PAY", 2));
        list.add(new ReportGeneratorBean("NHF", 2));
        list.add(new ReportGeneratorBean("ADV", 2));
        list.add(new ReportGeneratorBean("ADV NAME", 0));
        list.add(new ReportGeneratorBean("COOP", 2));
        list.add(new ReportGeneratorBean("COOP NAME", 0));
        list.add(new ReportGeneratorBean("DEP", 2));
        list.add(new ReportGeneratorBean("DEP NAME", 0));
        list.add(new ReportGeneratorBean("", 0));
        list.add(new ReportGeneratorBean("TOTAL DEDUCTION", 2));
        list.add(new ReportGeneratorBean("NET PAY", 2));
        list.add(new ReportGeneratorBean("BANK NAME", 0));
        list.add(new ReportGeneratorBean("ACCOUNT NUMBER", 0));
        list.add(new ReportGeneratorBean("BVN", 0));
        list.add(new ReportGeneratorBean("PFA", 0));
        list.add(new ReportGeneratorBean("PENSION PIN", 0));
        list.add(new ReportGeneratorBean("NARRATION", 0));
        list.add(new ReportGeneratorBean("LGA", 3));


        List<Map<String, Object>> BankHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : list) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            BankHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add("PAYROLL ANALYSIS BY LGA");

        rt.setBusinessCertificate(bc);
        rt.setGroupBy("LGA");
        rt.setReportTitle("PAYROLL_ANALYSIS_BY_LGA");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(bankSummaryList);
        rt.setTableHeaders(BankHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());
        lgaPayrollSummaryExcelGenerator.getExcel(response, request, rt);
    }


}