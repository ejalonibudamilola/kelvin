package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.DeductionService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.GroupedDataExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SimpleExcelReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ApportionedDeductionSummaryReport extends BaseController {

    @Autowired
    PaycheckService paycheckService;

    @Autowired
    DeductionService deductionService;


    @RequestMapping({"/apportionedDeductionSummaryReport.do"})
    public void setupForm(@RequestParam("rm") int pRunMonth,
                          @RequestParam("ry") int pRunYear, @RequestParam("rt") Long reportType, Model model, HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        SessionManagerService.manageSession(request, model);

        ReportGeneratorBean rt = new ReportGeneratorBean();


        BusinessCertificate certificate = getBusinessCertificate(request);

        LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
        String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
        String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);

        DeductionDetailsBean deductDetails = new DeductionDetailsBean();
        HashMap<Long, DeductGarnMiniBean> deductionBean = new HashMap<Long, DeductGarnMiniBean>();

        List<PaycheckDeduction> paycheckDeductions;

        deductDetails.setFromDate(sDate);
        deductDetails.setToDate(eDate);
        deductDetails.setRunMonth(pRunMonth);
        deductDetails.setRunYear(pRunYear);

        deductDetails.setCurrentDeduction("All Deductions");

        deductDetails.setId(certificate.getBusinessClientInstId());
        deductDetails.setFromDateStr(pSDate);
        deductDetails.setToDateStr(pEDate);
        BusinessClient bc = this.genericService.loadObjectById(BusinessClient.class, certificate.getBusinessClientInstId());
        deductDetails.setCompanyName(bc.getName());

        paycheckDeductions = this.deductionService.loadEmpDeductionsByParentIdAndPayPeriod(null, sDate.getMonthValue(),sDate.getYear(), certificate);
        DeductGarnMiniBean d = null;


        List<Map<String, Object>> OtherDeductionList = new ArrayList<>();
        HashMap<String, Integer> uniqueSet = new HashMap<>();
        int i = 1;
        for (PaycheckDeduction data : paycheckDeductions) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(certificate.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put(certificate.getStaffTypeName(), data.getEmployee().getDisplayName());
            newData.put(data.getEmpDeductionType().getFirstAllotAmtStr(), data.getEmpDeductionType().getFirstAllotAmt());
            newData.put(data.getEmpDeductionType().getSecAllotAmtStr(), data.getEmpDeductionType().getSecAllotAmt());
            newData.put(data.getEmpDeductionType().getThirdAllotAmtStr(), data.getEmpDeductionType().getThirdAllotAmt());
            newData.put("Amount Deducted", data.getAmount());
            newData.put("Deduction Type", data.getEmpDeductionType().getDescription());
            OtherDeductionList.add(newData);
            uniqueSet.put(data.getEmpDeductionType().getDescription(), i++);
        }

        HashMap<String, String> check = new HashMap<>();
        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(certificate.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(certificate.getStaffTypeName(), 0));
        for (PaycheckDeduction data : paycheckDeductions) {
            if(check.containsKey(data.getEmpDeductionType().getFirstAllotAmtStr())){
                continue;
            }
            else {
                tableHeaders.add(new ReportGeneratorBean(data.getEmpDeductionType().getFirstAllotAmtStr(), 2));
                check.put(data.getEmpDeductionType().getFirstAllotAmtStr(), data.getEmpDeductionType().getFirstAllotAmtStr());
            }
            if(check.containsKey(data.getEmpDeductionType().getSecAllotAmtStr())){
                continue;
            }
            else{
                tableHeaders.add(new ReportGeneratorBean(data.getEmpDeductionType().getSecAllotAmtStr(), 2));
                check.put(data.getEmpDeductionType().getSecAllotAmtStr(), data.getEmpDeductionType().getSecAllotAmtStr());
            }
            if(check.containsKey(data.getEmpDeductionType().getThirdAllotAmtStr())){
                continue;
            }
            else{
                tableHeaders.add(new ReportGeneratorBean(data.getEmpDeductionType().getThirdAllotAmtStr(), 2));
                check.put(data.getEmpDeductionType().getThirdAllotAmtStr(), data.getEmpDeductionType().getThirdAllotAmtStr());
            }

        }
        tableHeaders.add(new ReportGeneratorBean("Amount Deducted", 2));
        tableHeaders.add(new ReportGeneratorBean("Deduction Type", 3));

        List<Map<String, Object>> OtherDeductionsHeaders = new ArrayList<>();
        for (ReportGeneratorBean head : tableHeaders) {
            Map<String, Object> mappedHeader = new HashMap<>();
            mappedHeader.put("headerName", head.getHeaderName());
            mappedHeader.put("totalInd", head.getTotalInd());
            OtherDeductionsHeaders.add(mappedHeader);
        }

        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(bc.getName() + " Apportioned Deduction Report");
        mainHeaders.add("Pay Period : " + pSDate + " - " + eDate);


        rt.setBusinessCertificate(certificate);
        rt.setGroupBy("Deduction Type");
        rt.setReportTitle(bc.getName() + "_Apportioned_Deduction");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(OtherDeductionList);
        rt.setTableHeaders(OtherDeductionsHeaders);
        rt.setTableType(1);
        rt.setTotalInd(1);
        rt.setGroupedKeySet(uniqueSet.keySet());

        if (reportType == 1) {
            GroupedDataExcelReportGenerator groupedDataExcelReportGenerator = new GroupedDataExcelReportGenerator();
            groupedDataExcelReportGenerator.getExcel(response, request, rt);
        } else {
            rt.setWatermark(certificate.getBusinessName()+" Apportioned Deductions");
            PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();
            rt.setOutputInd(true);
            pdfSimpleGeneratorClass.getPdf(response, request, rt);
        }
    }
}
