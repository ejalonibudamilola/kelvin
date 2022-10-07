package com.osm.gnl.ippms.ogsg.controllers.report.variation;

import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.VariationService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.domain.report.ReportBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class variationReportPdf extends BaseController {

    @Autowired
    VariationService variationService;

    @RequestMapping(value = "/collatedMonthlyVariationReport.do", method = RequestMethod.GET, params = { "sDate" })
    public void loadCollatedMonthlyVariationReport(
            @RequestParam("sDate") String payPeriod,
            HttpServletRequest request, HttpServletResponse response,
            ModelAndView mv)
    {
        BusinessCertificate bc = this.getBusinessCertificate(request);

        LocalDate sDate = PayrollBeanUtils.setDateFromString(payPeriod);
        LocalDate currMonthStart = sDate;
        LocalDate eDate = LocalDate.now();
        LocalDate.of(sDate.getYear(), sDate.getMonthValue(),
                sDate.lengthOfMonth());
        boolean wSchoolSelected = false;
        try {
            List<ReportBean> rBean = (List<ReportBean>) getSessionAttribute(request,
                            SELECTED_MONTHLY_VARAIATION_OBJECT);
            Map<String, Object> paramMap = new HashMap<String, Object>();

            List<VariationReportBean> collatedReportList = new ArrayList<VariationReportBean>();
            List<VariationReportBean> totalCollatedReportList = new ArrayList<VariationReportBean>();
            List<VariationReportBean> wRetVal = new ArrayList<VariationReportBean>();
            List<String> mdaAll = new ArrayList<String>();
            List<String> distinctMda = new ArrayList<String>();

            VariationReportBean vs = (VariationReportBean) getSessionAttribute(request,
                            SCHOOL_SELECTED_MONTHLY_VARIATION);
            if (vs.isSchoolSelected()) {
                wSchoolSelected = true;
            }
            // For Subreports

            List<ReportGeneratorBean> beanList = new ArrayList();
            for (ReportBean r : rBean) {

                List<VariationReportBean> retVal = null;


                if (r.isNewStaffReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService
                            .loadNewEmployeesForMonthlyVariationReturnList(
                                    sDate, wSchoolSelected, bc);


                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Changed Date", data.getCreatedDateAndAuditTime());
                        newData.put("Grade & Step", data.getGradeStep());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade & Step", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("New " + bc.getStaffTypeName() + " Report");
                    String filename = "New_" + bc.getStaffTypeName() + "_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);


                } else if (r.isPromotionReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService
                            .loadPromotionLogForMonthlyVariationReturnList(
                                    sDate, wSchoolSelected, bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old Pay Group", data.getOldValue());
                        newData.put("New Pay Group", data.getNewValue());
                        newData.put("Last Mod. By", data.getUserName());
                        newData.put("Changed Date", data.getCreatedDateAndAuditTime());
                        newData.put("Pay Group", data.getPayGroup());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Pay Group", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Pay Group", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Mod. By", 0));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Promoted " + bc.getStaffTypeName() + " Log");
                    String filename = "Promoted_" + bc.getStaffTypeName() + "_Log";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);



                } else if (r.isDeductionReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService
                            .loadDeductionLogForMonthlyVariationReturnList(
                                    sDate, wSchoolSelected, bc);


                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old Value", data.getOldValue());
                        newData.put("New Value", data.getNewValue());
                        newData.put("Deduction Name", data.getLoanCode());
                        newData.put("Last Mod. By", data.getUserName());
                        newData.put("Changed Date", data.getCreatedDateAndAuditTime());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("Deduction Name", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Mod. By", 0));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add(bc.getStaffTypeName() + " Deductions Log");
                    String filename = bc.getStaffTypeName() + "_Deductions_Log";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);


                } else if (r.isReabsorptionReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService
                            .loadReabsorptionLogForMothlyVariationReturnList(bc,
                                    sDate, wSchoolSelected);


                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Grade Level", data.getGradeStep());
                        newData.put("Absorbed Date", data.getAbsorbedDate());
                        newData.put("Suspended Date", data.getSuspendedDate());
                        newData.put("Action By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade Level", 0));
                    tableHeaders.add(new ReportGeneratorBean("Absorbed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Suspended Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Action By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Reabsorption Report");
                    String filename = "Reabsorption_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);


                } else if (r.isReinstatementReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService
                            .loadReinstatementLogForMonthlyVariationReturnList(
                                    sDate, wSchoolSelected, bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Grade Level", data.getGradeStep());
                        newData.put("Reinstatement Date", data.getAbsorbedDate());
                        newData.put("Termination Date", data.getSuspendedDate());
                        newData.put("Action By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade Level", 0));
                    tableHeaders.add(new ReportGeneratorBean("Reinstatement Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Termination Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Action By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Reinstatement Report");
                    String filename = "Reinstatement_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
//                    pdfSimpleGeneratorClass.getPdf(response, request, rt);
                } else if (r.isSpecAllowanceReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService
                            .loadAllowancePaidForMonthlyVariationReturnList(
                                    sDate, wSchoolSelected, bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old Value", data.getOldValue());
                        newData.put("New Value", data.getNewValue());
                        newData.put("Allowance Name", data.getLoanCode());
                        newData.put("Changed Date", data.getCreatedDateAndAuditTime());
                        newData.put("Last Mod. By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("Allowance Name", 0));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Mod. By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Special Allowance Report");
                    String filename = "Spec_Allow_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);

                } else if (r.isTerminationReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService
                            .loadTerminatedEmployeesForMonthlyVariationReturnList(
                                    sDate, wSchoolSelected, bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Termination Date", data.getTerminateDate());
                        newData.put("Grade/Level", data.getPayGroup());
                        newData.put("Reason", data.getPayGroup());
                        newData.put("Action By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Termination Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade/Level", 0));
                    tableHeaders.add(new ReportGeneratorBean("Reason", 0));
                    tableHeaders.add(new ReportGeneratorBean("Action By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Terminated "+bc.getStaffTypeName()+" Report");
                    String filename = "Terminated_"+bc.getStaffTypeName()+"_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
//                    pdfSimpleGeneratorClass.getPdf(response, request, rt);
                } else if (r.isSuspensionReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService
                            .loadSuspensionLogForMonthlyVariationReturnList(
                                    sDate, wSchoolSelected, bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Suspended Date", data.getSuspendedDate());
                        newData.put("Grade/Level", data.getPayGroup());
                        newData.put("Reason", data.getReason());
                        newData.put("Action By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Suspended Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade/Level", 0));
                    tableHeaders.add(new ReportGeneratorBean("Reason", 0));
                    tableHeaders.add(new ReportGeneratorBean("Action By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add(bc.getStaffTypeName() + " Suspension Log Report");
                    String filename = bc.getStaffTypeName() + "_Suspension_Log_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
//                    pdfSimpleGeneratorClass.getPdf(response, request, rt);

                } else if (r.isBankChangeReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService.loadBankChangesVariationReturnList(
                            sDate, wSchoolSelected, bc);


                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old Value", data.getOldValue());
                        newData.put("New Value", data.getNewValue());
                        newData.put("Changed Date", data.getCreatedDateAndAuditTime());
                        newData.put("Last Mod. By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Mod. By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add(bc.getStaffTypeName() + " Account Changes Report");
                    String filename = bc.getStaffTypeName() + "_Account_Changes_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
//                    pdfSimpleGeneratorClass.getPdf(response, request, rt);

                } else if (r.isTransferReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService.loadTransferVariationReturnList(
                            sDate, wSchoolSelected, bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old "+bc.getMdaTitle(), data.getOldValue());
                        newData.put("New "+bc.getMdaTitle(), data.getNewValue());
                        newData.put("Grade/Level", data.getGradeStep());
                        newData.put("Transfer Date", data.getCreatedDateAndAuditTime());
                        newData.put("Last Mod. By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old "+bc.getMdaTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean("New "+bc.getMdaTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade/Level", 0));
                    tableHeaders.add(new ReportGeneratorBean("Transfer Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Mod. By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add(bc.getStaffTypeName() + " Transfer Report");
                    String filename = bc.getStaffTypeName() + "_Transfer_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
//                    pdfSimpleGeneratorClass.getPdf(response, request, rt);

                } else if (r.isPaygroupReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService.loadPayGroupMonthlyVariationReturnList(
                            sDate, wSchoolSelected, bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old Value", data.getOldValue());
                        newData.put("New Value", data.getNewValue());
                        newData.put("Changed Date", data.getCreatedDateAndAuditTime());
                        newData.put("Last Mod. By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Mod. By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add(bc.getStaffTypeName() + " PayGroup Report");
                    String filename = bc.getStaffTypeName() + "_PayGroup_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
//                    pdfSimpleGeneratorClass.getPdf(response, request, rt);

                } else if (r.isLoansReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService.loadLoansVariationReturnList(
                            sDate, wSchoolSelected, bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old Value", data.getOldValue());
                        newData.put("New Value", data.getNewValue());
                        newData.put("Column Changed", data.getColumnChanged());
                        newData.put("Loan Name", data.getLoanCode());
                        newData.put("Changed Date", data.getCreatedDateAndAuditTime());
                        newData.put("Last Mod. By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("Column Changed", 0));
                    tableHeaders.add(new ReportGeneratorBean("Loan Name", 0));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Mod. By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add(bc.getStaffTypeName() + " Loan Variation Report");
                    String filename = bc.getStaffTypeName() + "_Loan_Variation_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
//                    pdfSimpleGeneratorClass.getPdf(response, request, rt);

                } else if (r.isEmployeeChangeReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService.loadEmployeeChangeVariationReturnList(
                            bc.getBusinessClientInstId(), sDate, wSchoolSelected, bc);


                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old Value", data.getOldValue());
                        newData.put("New Value", data.getNewValue());
                        newData.put("Column Changed", data.getColumnChanged());
                        newData.put("Changed Date", data.getCreatedDateAndAuditTime());
                        newData.put("Last Mod. By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Value", 0));
                    tableHeaders.add(new ReportGeneratorBean("Column Changed", 0));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Mod. By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add(bc.getStaffTypeName() + " Changes Report");
                    String filename = bc.getStaffTypeName() + "_Changes_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
//                    pdfSimpleGeneratorClass.getPdf(response, request, rt);
                } else if (r.isDemotionReport()) {
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = variationService
                            .loadDemotionForMonthlyVariationReturnList(
                                    sDate, wSchoolSelected, bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old Pay Group", data.getOldValue());
                        newData.put("New Pay Group", data.getNewValue());
                        newData.put("Changed Date", data.getCreatedDateAndAuditTime());
                        newData.put("Last Mod. By", data.getUserName());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Pay Group", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Pay Group", 0));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Mod. By", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add(bc.getStaffTypeName() + " Demotions Report");
                    String filename = bc.getStaffTypeName() + "_Demotions_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
//                    pdfSimpleGeneratorClass.getPdf(response, request, rt);
                }
            }
            List<String> files = new ArrayList<>();
            for(int i=0; i<beanList.size(); i++){
                PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();
                pdfSimpleGeneratorClass.getPdf(response, request, beanList.get(i));

//                File currDir = new File(".");
//                String path = currDir.getAbsolutePath();
//                String fileLocation = path.substring(0, path.length() - 1) +beanList.get(i).getReportTitle()+".pdf";

              //  File currDir = new File(this.getClass().getClassLoader().getResource(".").getFile()+"/"+beanList.get(i).getReportTitle()+".pdf");
              //  String fileLocation = currDir.getAbsolutePath();
                File currDir = new File(request.getServletContext().getRealPath(File.separator)+beanList.get(i).getReportTitle()+".pdf");
                String fileLocation = currDir.getPath();
                //String fileLocation = path.substring(0, path.length() - 1) +beanList.get(i).getReportTitle()+".pdf";

                files.add(fileLocation);
            }

            zipPdfReports(files, response,request);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zipPdfReports(List<String> files, HttpServletResponse response, HttpServletRequest request) {
        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;
        try {
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            String fileLocation = path.substring(0, path.length() - 1) +"monthlyVariationReport.zip";

           // File currDir = new File(this.getClass().getClassLoader().getResource(".").getFile()+"/monthlyVariationReport.zip");
           // String fileLocation = currDir.getAbsolutePath();
            File currDir = new File(request.getServletContext().getRealPath(File.separator)+"/monthlyVariationReport.zip");
            String fileLocation = currDir.getPath();
           // String fileLocation = path.substring(0, path.length() - 1) +"/monthlyVariationReport.zip";
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename="+"monthlyVariationReport.zip");



            fos = new FileOutputStream(fileLocation);
            zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
            for(String filePath:files){
                File input = new File(filePath);
                fis = new FileInputStream(input);
                ZipEntry ze = new ZipEntry(input.getName());
                zipOut.putNextEntry(ze);
                byte[] tmp = new byte[4*1024];
                int size = 0;
                while((size = fis.read(tmp)) != -1){
                    zipOut.write(tmp, 0, size);
                }
                zipOut.flush();
                fis.close();
            }
            zipOut.close();
            FileInputStream baos = new FileInputStream(fileLocation);
            OutputStream os = response.getOutputStream();
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = baos.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            try{
                if(fos != null) fos.close();
            } catch(Exception ex){

            }
        }
    }
}
