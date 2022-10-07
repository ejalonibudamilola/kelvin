package com.osm.gnl.ippms.ogsg.controllers.report.reconciliation;

import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.ReconciliationService;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfSimpleGeneratorClass;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.ReconciliationReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.report.ReportBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.report.SummaryPage;
import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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
import java.time.format.TextStyle;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class reconciliationReportPdf extends BaseController {

    @Autowired
    ReconciliationService reconciliationService;

    @RequestMapping(value = "/viewPromotions.do", method = RequestMethod.GET, params = {"sDate"})
    public void loadPromotionLog(
            @RequestParam("sDate") String payPeriod,
            HttpServletRequest request, HttpServletResponse response,
            ModelAndView mv) {
        BusinessCertificate bc = this.getBusinessCertificate(request);

        ReconciliationReportGenerator reconciliationReportGenerator = new ReconciliationReportGenerator();


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
                    retVal = this.reconciliationService.loadNewEmployees(sDate, bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Salary Scale", data.getGradeStep());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Changed Date", data.getCreatedDate());
                        newData.put("Created By", data.getUserName());
                        newData.put("Hire Date", data.getChangedDate());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Salary Scale", 0));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
                    tableHeaders.add(new ReportGeneratorBean("Created By", 0));
                    tableHeaders.add(new ReportGeneratorBean("Hire Date", 0));
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
                    rt.setGroupedKeySet(uniqueSet.keySet());
                    rt.setWatermark(bc.getBusinessName()+" New "+bc.getStaffTypeName()+"/s");
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);


                }
                else if(r.isTerminationReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                     retVal = this.reconciliationService.loadTerminatedEmployees(
                             sDate, false, bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Grade/Level", data.getPayGroup());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        newData.put("Reason", data.getReason());
                        newData.put("Separation Date", data.getChangedDate());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade/Level", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean("Reason", 0));
                    tableHeaders.add(new ReportGeneratorBean("Separation Date", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Terminated Employees Report");
                    String filename = "Terminated_Employees_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setGroupedKeySet(uniqueSet.keySet());
                    rt.setWatermark(bc.getBusinessName()+" Terminated Employees");
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
            }
                else if(r.isSuspensionReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = this.reconciliationService.loadSuspensionLog(sDate,
                            bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Grade/Level", data.getGradeStep());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        newData.put("Suspended By", data.getUserName());
                        newData.put("Suspension Date", data.getChangedDate());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade/Level", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean("Suspended By", 0));
                    tableHeaders.add(new ReportGeneratorBean("Suspension Date", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }

                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Suspended Employees Report");
                    String filename = "Suspended_Employees_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setWatermark(bc.getBusinessName()+" Suspended Employees");
                    rt.setGroupedKeySet(uniqueSet.keySet());
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
                }

                else if(r.isReinstatementReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = this.reconciliationService.loadReinstatementLog(
                            sDate, bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Grade/Step", data.getGradeStep());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        newData.put("Reabsorbed By", data.getUserName());
                        newData.put("Date", data.getChangedDate());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade/Step", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean("Reabsorbed By", 0));
                    tableHeaders.add(new ReportGeneratorBean("Date", 0));
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
                    rt.setGroupedKeySet(uniqueSet.keySet());
                    rt.setWatermark(bc.getBusinessName()+" Reinstatement Report");
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);

                }

                else if(r.isReabsorptionReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = this.reconciliationService.loadReabsorptionLog(sDate,
                            bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Level/Step", data.getGradeStep());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        newData.put("Reinstated By", data.getUserName());
                        newData.put("Date", data.getChangedDate());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Level/Step", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean("Reinstated By", 0));
                    tableHeaders.add(new ReportGeneratorBean("Date", 0));
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
                    rt.setGroupedKeySet(uniqueSet.keySet());
                    rt.setWatermark(bc.getBusinessName()+" Reabsorption Report");
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);

                }

                else if(r.isDeductionReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = this.reconciliationService.loadReassignmentLog(sDate,
                            bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Old "+bc.getMdaTitle(), data.getOldAgency());
                        newData.put("New "+bc.getMdaTitle(), data.getAgency());
                        newData.put("Old Scale", data.getOldGradeStep());
                        newData.put("New Scale", data.getNewGradeStep());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        newData.put("Reassigned By", data.getUserName());
                        newData.put("Date", data.getChangedDate());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old "+bc.getMdaTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean("New "+bc.getMdaTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Scale", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Scale", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean("Reassigned By", 0));
                    tableHeaders.add(new ReportGeneratorBean("Date", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }

                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Reassignment Report");
                    String filename = "Reassignment_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setWatermark(bc.getBusinessName()+" Reassignment Report");
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
                }

                else if(r.isPromotionReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = this.reconciliationService.loadPromotionLog(sDate,
                            bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Pay Group", data.getPayGroup());
                        newData.put("Old Grade/Step", data.getOldGradeStep());
                        newData.put("New Grade/Step", data.getNewGradeStep());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        //newData.put("Promoted By", data.getUserName());
                        newData.put("Date", data.getChangedDate());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Grade/Step", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Grade/Step", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean("Date", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }

                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Promotion Report");
                    mainHeaders.add("Pay Period: "+sDate.getMonth().getDisplayName(TextStyle.FULL, Locale.UK)+", "+sDate.getYear());
                    String filename = "Promotion_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setGroupedKeySet(uniqueSet.keySet());
                    rt.setWatermark(bc.getBusinessName()+" Promotion Report");
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
                }

                else if(r.isIncrementReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = this.reconciliationService.loadStepIncrementLog(sDate, bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Pay Group", data.getPayGroup());
                        newData.put("Old Grade/Step", data.getOldGradeStep());
                        newData.put("New Grade/Step", data.getNewGradeStep());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        newData.put("Incremented By", data.getUserName());
                        newData.put("Date", data.getChangedDate());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
                    tableHeaders.add(new ReportGeneratorBean("Old Grade/Step ", 0));
                    tableHeaders.add(new ReportGeneratorBean("New Grade/Step", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean("Incremented By", 0));
                    tableHeaders.add(new ReportGeneratorBean("Date", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }

                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Step Increment Report");
                    String filename = "Step_Increment_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setWatermark(bc.getBusinessName()+" Step Increment");
                    rt.setGroupedKeySet(uniqueSet.keySet());
                    rt.setTableData(hiringList);
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
                }

                else if(r.isSpecAllowanceReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = this.reconciliationService.loadAllowancePaid(sDate, eDate, false, bc.getBusinessClientInstId(), bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }

                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Special Allowance (Increase) Report");
                    String filename = "Special_Allowance_Increase_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setWatermark(bc.getBusinessName()+" Special Allowance");
                    rt.setGroupedKeySet(uniqueSet.keySet());
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setOutputInd(false);

                    beanList.add(rt);
                }
                else if(r.isSpecAllowDecreaseReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = this.reconciliationService.loadAllowancePaid(sDate, eDate, true, bc.getBusinessClientInstId(), bc);

                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }

                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Special Allowance (Decrease) Report");
                    String filename = "Special_Allowance_Decrease_Report";

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
                }

                else if(r.isPrevTerminationReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();
                    retVal = this.reconciliationService.loadTerminatedEmployees(
                            sDate, true, bc);
                    HashMap<String, Integer> uniqueSet = new HashMap<>();
                    int i=0;
                    List<Map<String, Object>> hiringList = new ArrayList<>();
                    for (VariationReportBean data : retVal) {
                        Map<String, Object> newData = new HashMap<>();
                        newData.put(bc.getStaffTitle(), data.getEmployeeId());
                        newData.put(bc.getStaffTypeName(), data.getEmployeeName());
                        newData.put("Grade/Step", data.getEmployeeName());
                        newData.put("Last Period Gross", data.getLastPeriodGross());
                        newData.put("Current Period Gross", data.getCurrentPeriodGross());
                        newData.put("Difference", data.getGrossDifference());
                        newData.put("Reason", data.getGrossDifference());
                        newData.put("Separation Date", data.getGrossDifference());
                        newData.put(bc.getMdaTitle(), data.getAgency());
                        uniqueSet.put(data.getAgency(), i++);
                        hiringList.add(newData);
                    }

                    List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
                    tableHeaders.add(new ReportGeneratorBean("Grade/Step", 0));
                    tableHeaders.add(new ReportGeneratorBean("Last Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Current Period Gross", 2));
                    tableHeaders.add(new ReportGeneratorBean("Difference", 2));
                    tableHeaders.add(new ReportGeneratorBean("Reason", 0));
                    tableHeaders.add(new ReportGeneratorBean("Separation Date", 0));
                    tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

                    List<Map<String, Object>> hirMappedHeaders = new ArrayList<>();
                    for (ReportGeneratorBean head : tableHeaders) {
                        Map<String, Object> mappedHeader = new HashMap<>();
                        mappedHeader.put("headerName", head.getHeaderName());
                        mappedHeader.put("totalInd", head.getTotalInd());
                        hirMappedHeaders.add(mappedHeader);
                    }

                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Previous Retirement/Resignation Report");
                    String filename = "Previous_Retirement_or_Resignation_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setGroupBy(bc.getMdaTitle());
                    rt.setReportTitle(filename);
                    rt.setMainHeaders(mainHeaders);
                    rt.setSubGroupBy(null);
                    rt.setTableData(hiringList);
                    rt.setGroupedKeySet(uniqueSet.keySet());
                    rt.setTableType(1);
                    rt.setTotalInd(0);
                    rt.setTableHeaders(hirMappedHeaders);
                    rt.setWatermark(bc.getBusinessName()+" Termination Report");
                    rt.setOutputInd(false);

                    beanList.add(rt);
                }
                else if(r.isReconciliationReport()){
                    ReportGeneratorBean rt = new ReportGeneratorBean();

                SummaryPage retVal2 = this.reconciliationService.reconciliationReport(sDate, eDate, bc);


                        Map<String, Object> newData = new HashMap<>();
                        newData.put("CurrentStaffCount", retVal2.getCurrStaffCount());
                        newData.put("PrevStaffCount", retVal2.getPrevStaffCount());
                        newData.put("StaffCountDiff", retVal2.getStaffCountDifference());
                        newData.put("CurrentBasicSalary", PayrollHRUtils.getDecimalFormat().format(retVal2.getCurrBasicSalary()));
                        newData.put("PrevBasicSalary", PayrollHRUtils.getDecimalFormat().format(retVal2.getPrevBasicSalary()));
                        newData.put("BasicSalaryDiff", PayrollHRUtils.getDecimalFormat().format(retVal2.getBasicSalaryDifference()));
                        newData.put("CurrTotalAllowance", PayrollHRUtils.getDecimalFormat().format(retVal2.getCurrTotalAllowance()));
                        newData.put("PrevTotalAllowance", PayrollHRUtils.getDecimalFormat().format(retVal2.getPrevTotalAllowance()));
                        newData.put("TotalAllowanceDiff", PayrollHRUtils.getDecimalFormat().format(retVal2.getTotalAllowanceDifference()));
                        newData.put("CurrGrossPay", PayrollHRUtils.getDecimalFormat().format(retVal2.getCurrGrossSum()));
                        newData.put("PrevGrossPay", PayrollHRUtils.getDecimalFormat().format(retVal2.getPrevGrossSum()));
                        newData.put("GrossPayDiff", PayrollHRUtils.getDecimalFormat().format(retVal2.getGrossSumDifference()));
                        newData.put("CurrTax", PayrollHRUtils.getDecimalFormat().format(retVal2.getCurrTaxSum()));
                        newData.put("PrevTax", PayrollHRUtils.getDecimalFormat().format(retVal2.getPrevTaxSum()));
                        newData.put("TaxDiff", PayrollHRUtils.getDecimalFormat().format(retVal2.getTaxSumDifference()));
                        newData.put("CurrOtherDeductions", PayrollHRUtils.getDecimalFormat().format(retVal2.getCurrOtherDeductions()));
                        newData.put("PrevOtherDeductions", PayrollHRUtils.getDecimalFormat().format(retVal2.getPrevOtherDeductions()));
                        newData.put("OtherDeductionsDiff", PayrollHRUtils.getDecimalFormat().format(retVal2.getOtherDeductionsDifference()));
                        newData.put("CurrTotalDeductions", PayrollHRUtils.getDecimalFormat().format(retVal2.getCurrTotalDeductions()));
                        newData.put("PrevTotalDeductions", PayrollHRUtils.getDecimalFormat().format(retVal2.getPrevTotalDeductions()));
                        newData.put("TotalDeductionsDiff", PayrollHRUtils.getDecimalFormat().format(retVal2.getTotalDeductionsDifference()));
                        newData.put("CurrNetPay", PayrollHRUtils.getDecimalFormat().format(retVal2.getCurrNetPay()));
                        newData.put("PrevNetPay", PayrollHRUtils.getDecimalFormat().format(retVal2.getPrevNetPay()));
                        newData.put("NetPayDiff", PayrollHRUtils.getDecimalFormat().format(retVal2.getNetPayDifference()));
                        newData.put("LastPaidGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getPrevGrossSum()));
                        newData.put("NewStaffCount", retVal2.getNewStaffCount());
                        newData.put("NewStaffGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getNewStaffGross()));
                        newData.put("PromotionCount", retVal2.getPromotedEmpCount());
                        newData.put("PromotionGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getPromotedStaffGrossDifference()));
                        newData.put("ReassignmentCount", retVal2.getReassignedEmpCount());
                        newData.put("ReassignmentGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getReassignedStaffGrossDifference()));
                        newData.put("ReinstatedCount", retVal2.getReinstatedEmpCount());
                        newData.put("ReinstatedGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getReinstatedStaffGrossDifference()));
                        newData.put("ReabsorptionCount", retVal2.getReabsorbedEmpCount());
                        newData.put("ReabsorptionGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getReabsorbedStaffGrossDifference()));
                        newData.put("SpecialAllowIncCount", retVal2.getSpecAllowDiffCount());
                        newData.put("SpecialAllowGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getSpecAllowChange()));
                        newData.put("StepIncrementCount", retVal2.getStepIncrementCount());
                        newData.put("StepIncrementGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getStepIncrementGrossDifference()));
                        newData.put("CurrRetirementCount", retVal2.getTerminatedEmpCount());
                        newData.put("CurrRetirementGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getTerminatedStaffGrossDifference()));
                        newData.put("PrevRetirementCount", retVal2.getPrevTerminatedEmpCount());
                        newData.put("PrevRetirementGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getPrevTerminatedStaffGrossDifference()));
                        newData.put("SuspendedCount", retVal2.getSuspendedEmpCount());
                        newData.put("SuspendedGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getSuspendedStaffGrossDifference()));
                        newData.put("SpecialAllowDecCount", retVal2.getSpecAllowDecreaseCount());
                        newData.put("SpecialAllowDecGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getSpecAllowDecrease()));
                        newData.put("SubTotal1", PayrollHRUtils.getDecimalFormat().format(retVal2.getIncrementSubTotal()));
                        newData.put("SubTotal2", PayrollHRUtils.getDecimalFormat().format(retVal2.getDecrementSubTotal()));
                        newData.put("currPeriodGross", PayrollHRUtils.getDecimalFormat().format(retVal2.getCurrGrossSum()));
                        newData.put("Period", payPeriod);


                    List<String> mainHeaders = new ArrayList<>();
                    mainHeaders.add("Reconciliation Report");
                    String filename = "Reconciliation_Report";

                    rt.setBusinessCertificate(bc);
                    rt.setSingleTableData(newData);
                    rt.setReportTitle(filename);

                    beanList.add(rt);
                }
            }
            List<String> files = new ArrayList<>();
            for(int i=0; i<beanList.size(); i++){
                PdfSimpleGeneratorClass pdfSimpleGeneratorClass = new PdfSimpleGeneratorClass();
                if(beanList.get(i).getReportTitle() == "Reconciliation_Report"){
                    reconciliationReportGenerator.generatePdf(response, request, beanList.get(i));
                }
                else {
                    pdfSimpleGeneratorClass.getPdf(response, request, beanList.get(i));
                }

                File currDir = new File(request.getServletContext().getRealPath(File.separator)+beanList.get(i).getReportTitle()+".pdf");

                String fileLocation = currDir.getPath();

                files.add(fileLocation);
            }

            zipPdfReports(files, response,request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zipPdfReports(List<String> files, HttpServletResponse response,HttpServletRequest request) {
        FileOutputStream fos = null;
        ZipOutputStream zipOut;
        FileInputStream fis;
        try {

            File currDir = new File(request.getServletContext().getRealPath(File.separator)+"/reconciliationReport.zip");
            String fileLocation = currDir.getPath();
            //String fileLocation = path.substring(0, path.length() - 1) +"/reconciliationReport.zip";

            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename="+"reconciliationReport.zip");



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
                ex.printStackTrace();
            }
        }
    }
}
