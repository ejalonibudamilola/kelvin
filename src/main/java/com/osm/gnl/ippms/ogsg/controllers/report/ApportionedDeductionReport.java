package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.DeductionService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
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
public class ApportionedDeductionReport extends BaseController {

    @Autowired
    PaycheckService paycheckService;

    @Autowired
    DeductionService deductionService;


    @RequestMapping({"/apportionedDeductionReport.do"})
    public void setupForm(@RequestParam("did") Long dedTypeId, @RequestParam("rm") int pRunMonth,
                          @RequestParam("ry") int pRunYear, @RequestParam("pid") Long pid, @RequestParam("rt") Long reportType, Model model, HttpServletRequest request,
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

        paycheckDeductions = this.deductionService.loadEmpDeductionsByParentIdAndPayPeriod(dedTypeId, sDate.getMonthValue(), sDate.getYear(), certificate);
        DeductGarnMiniBean d = null;

        String firstAllot = null;
        String secondAllot = null;
        String thirdAllot = null;
        List<Map<String, Object>> OtherDeductionList = new ArrayList<>();
        for (PaycheckDeduction data : paycheckDeductions) {
            Map<String, Object> newData = new HashMap<>();
            newData.put(certificate.getStaffTitle(), data.getEmployee().getEmployeeId());
            newData.put(certificate.getStaffTypeName(), data.getEmployee().getDisplayName());
            newData.put(data.getEmpDeductionType().getFirstAllotAmtStr()+"("+data.getEmpDeductionType().getFirstAllotAmt()+"%)", (data.getEmpDeductionType().getFirstAllotAmt() / 100.0) * data.getAmount());
            newData.put(data.getEmpDeductionType().getSecAllotAmtStr()+"("+data.getEmpDeductionType().getSecAllotAmt()+"%)", (data.getEmpDeductionType().getSecAllotAmt() / 100.0) * data.getAmount());
            newData.put(data.getEmpDeductionType().getThirdAllotAmtStr()+"("+data.getEmpDeductionType().getThirdAllotAmt()+"%)", (data.getEmpDeductionType().getThirdAllotAmt() / 100.0) * data.getAmount());
            newData.put("Amount Deducted", data.getAmount());
            OtherDeductionList.add(newData);
            firstAllot = data.getEmpDeductionType().getFirstAllotAmtStr()+"("+data.getEmpDeductionType().getFirstAllotAmt()+"%)";
            secondAllot = data.getEmpDeductionType().getSecAllotAmtStr()+"("+data.getEmpDeductionType().getSecAllotAmt()+"%)";
            thirdAllot = data.getEmpDeductionType().getThirdAllotAmtStr()+"("+data.getEmpDeductionType().getThirdAllotAmt()+"%)";
        }

        List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
        tableHeaders.add(new ReportGeneratorBean(certificate.getStaffTitle(), 0));
        tableHeaders.add(new ReportGeneratorBean(certificate.getStaffTypeName(), 0));
        tableHeaders.add(new ReportGeneratorBean(firstAllot, 2));
        tableHeaders.add(new ReportGeneratorBean(secondAllot, 2));
        tableHeaders.add(new ReportGeneratorBean(thirdAllot, 2));
        tableHeaders.add(new ReportGeneratorBean("Amount Deducted", 2));

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
        rt.setGroupBy(null);
        rt.setReportTitle(bc.getName() + "_Apportioned_Deduction");
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(OtherDeductionList);
        rt.setTableHeaders(OtherDeductionsHeaders);
        rt.setTableType(0);
        rt.setTotalInd(1);

        if (reportType == 1) {
            SimpleExcelReportGenerator simpleExcelReportGenerator = new SimpleExcelReportGenerator();
            simpleExcelReportGenerator.getExcel(response, request, rt);
        } else {
            PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();
            rt.setOutputInd(true);
            pdfSimpleGeneratorClass.getPdf(response, request, rt);
        }
    }
}
