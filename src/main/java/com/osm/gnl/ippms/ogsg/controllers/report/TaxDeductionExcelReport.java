package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs.UngroupedPdf;
import com.osm.gnl.ippms.ogsg.controllers.report.service.PaycheckDeductionService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.beans.EmployeeTaxDeductionBean;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
public class TaxDeductionExcelReport extends BaseController {

    @Autowired
    PaycheckDeductionService paycheckDeductionService;

    @RequestMapping({"/taxByAgencyExcel.do"})
    public void generateTaxByAgencyByExcel(@RequestParam("sDate") String fD, @RequestParam("eDate") String tD,
                                           @RequestParam("mid") Long pMdaId,
                                           @RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception
    {
        SessionManagerService.manageSession(request, model);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        BusinessCertificate bc = getBusinessCertificate(request);

        PayrollSummaryBean p = new PayrollSummaryBean();
        LocalDate fromDate;
        LocalDate toDate;
        fromDate = PayrollBeanUtils.getLocalDateFromString(fD);
        toDate = PayrollBeanUtils.getLocalDateFromString(tD);

        List<EmpDeductMiniBean> empBeanList;

        empBeanList = this.paycheckDeductionService.loadEmpTaxDeductionsByRunMonthAndYear(fromDate,toDate,pMdaId,pEmpId, bc);

        p.setPayDate(toDate);
        p.setId(pEmpId);
        if(pEmpId > 0){
            HiringInfo wHinfo = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(CustomPredicate.procurePredicate("employee.id", pEmpId),
                    getBusinessClientIdPredicate(request)));
            p.setOgNumber(wHinfo.getEmployee().getEmployeeId());
            p.setName(wHinfo.getEmployee().getDisplayNameWivTitlePrefixed());
            p.setTerminatedEmployee(wHinfo.getEmployee().isTerminated());
            p.setTitleField(wHinfo.getEmployee().getSalaryInfo().getSalaryScaleLevelAndStepStr());
            p.setCompanyName(String.valueOf(wHinfo.getNoOfYearsInService()));
            p.setMdaCode(wHinfo.getEmployee().getAssignedToObject());
        }

        p.setMdaInstId(pMdaId);

        if(pMdaId > 0){
            MdaInfo wMdaInfo = this.genericService.loadObjectUsingRestriction(MdaInfo.class, Arrays.asList(CustomPredicate.procurePredicate("id", pMdaId),
                    CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
            p.setMode(wMdaInfo.getName());
            p.setMdaCode(wMdaInfo.getCodeName());
        }
        p.setFromDate(fromDate);
        if(fromDate.getMonthValue() != toDate.getMonthValue()
                || fromDate.getYear() != toDate.getYear()){
            p.setDisplayName("Tax Deductions for "+ PayrollBeanUtils.makePayPeriod(fromDate, toDate));

        }else{
            p.setDisplayName("Tax Deductions for "+PayrollBeanUtils.makePayPeriod(toDate));
        }

        p.setExcelFileName(PayrollBeanUtils.createPayPeriod(fromDate.getMonthValue(), fromDate.getYear(), toDate.getMonthValue(), toDate.getYear(), true, false));
        if(pEmpId > 0){
            p.setDoNotShowEmpName(true);
        }

        p.setDeductionList(empBeanList);

        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        List<Map<String, Object>> hiringList = new ArrayList<>();
        for (EmpDeductMiniBean data : empBeanList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getStaffTypeName(),data.getName());
            newData.put("Taxes Paid",data.getCurrentDeduction());
            newData.put(bc.getMdaTitle(), data.getMdaName());
            uniqueSet.put(data.getMdaName(), i++);
            hiringList.add(newData);
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean("Taxes Paid", 2));
        tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

        List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : tableHeaders){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            hirMappedHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(p.getDisplayName());

        rt.setBusinessCertificate(bc);
        rt.setGroupBy(bc.getMdaTitle());
        rt.setReportTitle(bc.getBusinessName()+" Tax Deductions Summary");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(hiringList);
        rt.setGroupedKeySet(uniqueSet.keySet());
        rt.setTableHeaders(hirMappedHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);

        new GroupedDataExcelReportGenerator().getExcel(response, request, rt);
    }


    @RequestMapping({"/taxYTDSummaryExcel.do"})
    public void generateTaxReturnsSummary(@RequestParam("ry") int pRunYear,
                                           @RequestParam("rt") int reportType, Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        ReportGeneratorBean rt = new ReportGeneratorBean();

        SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();

        PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();

        PayrollSummaryBean p = new PayrollSummaryBean();
        int pStartMonth = 1;
        int pEndMonth = 12;
        LocalDate wPeriodStart = PayrollBeanUtils.getDateFromMonthAndYear(pStartMonth, pRunYear,false);
        LocalDate wPeriodEnd = PayrollBeanUtils.getDateFromMonthAndYear(pEndMonth, pRunYear,true);
        List<EmployeePayBean> empBeanList = new ArrayList<>();

        empBeanList = this.paycheckDeductionService.loadEmployeeTaxImplications(pRunYear, bc);

        p.setPayDate(wPeriodEnd);

        p.setFromDate(wPeriodStart);

        List<Map<String, Object>> taxSummaryList = new ArrayList<>();
        for (EmployeePayBean data : empBeanList) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(bc.getStaffTypeName()+" Name", data.getEmployee().getDisplayName());
            newData.put(bc.getStaffTitle(), data.getEmployeeId());
            newData.put(bc.getMdaTitle(), data.getMda());
            newData.put("Gross Pay YTD", data.getTotalPay());
            newData.put("Taxable Income YTD", data.getTaxableIncome());
            newData.put("Tax Paid YTD", data.getTaxesPaid());
            taxSummaryList.add(newData);
        }

        List<ReportGeneratorBean> list = new ArrayList<>();
        list.add(new ReportGeneratorBean(bc.getStaffTypeName()+" Name", 0));
        list.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
        list.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
        list.add(new ReportGeneratorBean("Gross Pay YTD", 2));
        list.add(new ReportGeneratorBean("Taxable Income YTD", 2));
        list.add(new ReportGeneratorBean("Tax Paid YTD", 2));

        List<Map<String, Object>> taxHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : list){
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            taxHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getBusinessName()+" - Civil Service Tax Deductions");

            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setReportTitle(bc.getBusinessName()+" Tax Deductions");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(taxSummaryList);
            rt.setTableHeaders(taxHeaders);
            rt.setTableType(0);
            rt.setTotalInd(1);

            if(reportType == 1) {
                simpleExcelReportGenerator.getExcel(response, request, rt);
            }
            else{
                rt.setWatermark(bc.getBusinessName()+" Tax Deductions");
                rt.setOutputInd(true);
                new UngroupedPdf().getPdf(response, request, rt);
            }

    }

    @RequestMapping(value = "/empTaxDed.do", method = RequestMethod.GET)
    public void empTaxDeduction(
            @RequestParam("sDate") String pPayPeriod,
            @RequestParam("eid") String empId, HttpServletRequest request,
            HttpServletResponse response, ModelAndView mv) throws Exception {
        BusinessCertificate bc = getBusinessCertificate(request);

        PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();

        ReportGeneratorBean rt = new ReportGeneratorBean();

            List<EmployeeTaxDeductionBean> jeds = this.paycheckDeductionService.taxDeduction(pPayPeriod, empId, bc);

            EmployeeTaxDeductionBean emp = new EmployeeTaxDeductionBean();

            List<Map<String, Object>> taxSummaryList = new ArrayList<>();
            for (EmployeeTaxDeductionBean data : jeds) {
                Map<String, Object> newData = new HashMap<>();
                newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                newData.put(bc.getStaffTitle(), data.getEmployeeId());
                newData.put(bc.getMdaTitle(), data.getAgency());
                newData.put("Pay Period", data.getPayPeriod());
                newData.put("Pay Date", data.getPayPeriod());
                newData.put("Amount", data.getAmount());
                taxSummaryList.add(newData);
                emp.setEmployeeId(data.getEmployeeId());
                emp.setEmployeeName(data.getEmployeeName());
                emp.setAgency(data.getAgency());
            }

            List<ReportGeneratorBean> list = new ArrayList<>();
            list.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
            list.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
            list.add(new ReportGeneratorBean(bc.getMdaTitle(), 0));
            list.add(new ReportGeneratorBean("Pay Period", 0));
            list.add(new ReportGeneratorBean("Pay Date", 0));
            list.add(new ReportGeneratorBean("Amount", 2));

            List<Map<String, Object>> taxHeaders = new ArrayList<>();
            for(ReportGeneratorBean head : list){
                Map<String, Object> mappedHeader = new HashMap<>();
                mappedHeader.put("headerName", head.getHeaderName());
                mappedHeader.put("totalInd", head.getTotalInd());
                taxHeaders.add(mappedHeader);
            }

            List<String> mainHeaders = new ArrayList<>();
            mainHeaders.add("Tax History Report");
            mainHeaders.add(bc.getStaffTypeName() +": "+emp.getEmployeeName());
            mainHeaders.add(bc.getStaffTitle() +": "+emp.getEmployeeId());
            mainHeaders.add(bc.getMdaTitle() +": "+emp.getAgency());

            rt.setBusinessCertificate(bc);
            rt.setGroupBy(null);
            rt.setReportTitle("Tax_Deductions");
            rt.setMainHeaders(mainHeaders);
            rt.setSubGroupBy(null);
            rt.setTableData(taxSummaryList);
            rt.setTableHeaders(taxHeaders);
            rt.setTableType(0);
            rt.setTotalInd(1);
            rt.setOutputInd(true);
            rt.setWatermark(bc.getBusinessName()+" Tax Deduction");


            pdfSimpleGeneratorClass.getPdf(response, request, rt);
    }
}
