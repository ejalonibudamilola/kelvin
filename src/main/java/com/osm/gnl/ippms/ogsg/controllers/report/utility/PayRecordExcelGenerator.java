package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.domain.beans.PRCDeductionsBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.beans.PRCDisplayBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.beans.PayRecordBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;

public class PayRecordExcelGenerator {

    private PayRecordBean fPayRecordBean;
    private XSSFFont arialFont9;
    private XSSFFont arialFont10;
    private XSSFFont  arialNormalFont10;
    private XSSFFont arialFont12;
    private CellStyle totalStyle;
    private CellStyle headerStyle;
    private CellStyle normalStyle;
    private Workbook workbook;

    public void getExcel(HttpServletResponse response, HttpServletRequest request, ReportGeneratorBean rt) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        init(workbook);

        this.fPayRecordBean = (rt.getPayBean());
        List<PRCDisplayBeanHolder> wBaseYearBean = this.fPayRecordBean.getBaseYearBean();
        int pYear = 0;
        if ((this.fPayRecordBean.getUltimateBean() == null) || (this.fPayRecordBean.getUltimateBean().isEmpty())){
            pYear = this.fPayRecordBean.getPenultimateYear();
        }
        wBaseYearBean = this.fPayRecordBean.getUltimateBean();
        if ((wBaseYearBean != null) && (!wBaseYearBean.isEmpty())){
            pYear = this.fPayRecordBean.getUltimateYear();
        }



       // File currDir = new File("src/main/resources/static/images/"+rt.getBusinessCertificate().getClientReportLogo());
        File currDir = ResourceUtils.getFile("classpath:static/images/"+rt.getBusinessCertificate().getClientReportLogo());
        String path = currDir.getAbsolutePath();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" +rt.getReportTitle()+".xls");

        Sheet sheet = workbook.createSheet(rt.getReportTitle());
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        drawPictureHeader(workbook, sheet, path);

        addSheetTitle(workbook, sheet, rt.getReportTitle(), pYear);

        int rowNo = 10;
        headers(workbook, sheet, rowNo);

        ungroupedData(workbook, sheet, wBaseYearBean, this.fPayRecordBean.getUltimateDeductions(), this.fPayRecordBean.getUltimateDeductionsNames());

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void init(Workbook workbook) {
        totalStyle = workbook.createCellStyle();
        normalStyle = workbook.createCellStyle();

        setHeaderStyle(workbook);
        setArialFont9(workbook);
        setArialFont10(workbook);
        setArialNormalFont10(workbook);
        setArialFont12(workbook);
    }

    private void setHeaderStyle(Workbook workbook){
        if(headerStyle == null){
            headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
    }

    private void  setArialFont9(Workbook workbook){
        arialFont9 =  ((XSSFWorkbook) workbook).createFont();
        arialFont9.setFontName("Arial");
        arialFont9.setFontHeightInPoints((short) 9);
        arialFont9.setBold(true);
        headerStyle.setFont(arialFont9);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
    }

    private void  setArialFont10(Workbook workbook){
        arialFont10 =  ((XSSFWorkbook) workbook).createFont();
        arialFont10.setFontName("Arial");
        arialFont10.setFontHeightInPoints((short) 10);
        arialFont10.setBold(true);
        headerStyle.setFont(arialFont10);
        totalStyle.setFont(arialFont10);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
    }

    private void  setArialNormalFont10(Workbook workbook){
        arialNormalFont10 =  ((XSSFWorkbook) workbook).createFont();
        arialNormalFont10.setFontName("Arial");
        arialNormalFont10.setFontHeightInPoints((short) 10);
        arialNormalFont10.setBold(false);
        normalStyle.setFont(arialNormalFont10);
        normalStyle.setBorderBottom(BorderStyle.THIN);
        normalStyle.setBorderLeft(BorderStyle.THIN);
        normalStyle.setBorderRight(BorderStyle.THIN);
        normalStyle.setBorderTop(BorderStyle.THIN);
    }

    private void  setArialFont12(Workbook workbook){
        arialFont12 =  ((XSSFWorkbook) workbook).createFont();
        arialFont12.setFontName("Arial");
        arialFont12.setFontHeightInPoints((short) 12);
        arialFont12.setBold(true);
        headerStyle.setFont(arialFont12);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
    }

    private void ungroupedData(Workbook workbook, Sheet sheet, List<PRCDisplayBeanHolder> pListHolder, List<PRCDeductionsBeanHolder> pDeductionsList,
                               List<String> pDeductionNames) {
        int rowNo = 11;
        Row row;
        int cellNo = 0;
        int serialNo = 1;
        Cell cell;

        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo++);
                cell.setCellValue("Academic Allowance");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo++);
                cell.setCellValue(e.getAcademicAllowanceStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        int cellNo1 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {

                cell = row.createCell(cellNo1++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo1++);
                cell.setCellValue("Contract Allowance");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo1++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo1++);
                cell.setCellValue(e.getContractAllowanceStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        int cellNo2 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo2++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo2++);
                cell.setCellValue("Domestic");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo2++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo2++);
                cell.setCellValue(e.getDomesticServantStrSansNaira());
                cellStyle(workbook, cell);
            }
        }
        int cellNo3 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo3++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo3++);
                cell.setCellValue("Entertainment");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo3++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo3++);
                cell.setCellValue(e.getEntertainmentStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        int cellNo4 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo4++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo4++);
                cell.setCellValue("FCA ALLWS");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo4++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo4++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            }
        }

        int cellNo5 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo5++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo5++);
                cell.setCellValue("Hardship");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo5++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo5++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            }
        }

        int cellNo6 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo6++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo6++);
                cell.setCellValue("LTG");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo6++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo6++);
                cell.setCellValue(e.getLeaveTransportGrantStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        int cellno7 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellno7++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellno7++);
                cell.setCellValue("Meal Subsidy");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellno7++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellno7++);
                cell.setCellValue(e.getMealStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        int cellNo8 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo8++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo8++);
                cell.setCellValue("Motor Maintenance");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo8++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo8++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            }
        }

        int cellNo9 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo9++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo9++);
                cell.setCellValue("Personal Assist");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo9++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo9++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            }
        }

        int cellNo10 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo10++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo10++);
                cell.setCellValue("Reabsorption Arrears");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo10++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo10++);
                cell.setCellValue(e.getOtherArrearsStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        int cellNo11 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo11++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo11++);
                cell.setCellValue("Salary Arrears");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo11++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo11++);
                cell.setCellValue(e.getArrearsStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        int cellNo12 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo12++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo12++);
                cell.setCellValue("Special Allowance");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo12++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo12++);
                cell.setCellValue(e.getSpecialAllowanceStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        int cellNo13 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo13++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo13++);
                cell.setCellValue("Rent Subsidy");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo13++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo13++);
                cell.setCellValue(e.getRentStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

         int cellNo14 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo14++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo14++);
                cell.setCellValue("Responsibility");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo14++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo14++);
                cell.setCellValue(e.getPrincipalAllowanceStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        int cellNo15 = 0;
        row = sheet.createRow(rowNo++);
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo15++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo15++);
                cell.setCellValue("SSC Allws");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo15++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo15++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
         int cellNo16 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo16++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo16++);
                cell.setCellValue("Science Allow");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo16++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo16++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
        int cellNo17 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo17++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo17++);
                cell.setCellValue("Transport Allow");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo17++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo17++);
                cell.setCellValue(e.getTransportStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
        int cellNo18  = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo18++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo18++);
                cell.setCellValue("TSS allowance");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo18++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo18++);
                cell.setCellValue(e.getTssStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
        int cellNo19 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo19++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo19++);
                cell.setCellValue("Utility");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo19++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo19++);
                cell.setCellValue(e.getUtilityStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
        int cellNo20 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo20++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo20++);
                cell.setCellValue("Basic Salary");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo20++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo20++);
                cell.setCellValue(e.getSalaryInfo().getMonthlyBasicSalaryStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
        int cellNo21 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo21++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo21++);
                cell.setCellValue("Overtime");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo21++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo21++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
        int cellNo22 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo22++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo22++);
                cell.setCellValue("Total Gross");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo22++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo22++);
                cell.setCellValue(e.getTotalAllowPlusBasic());
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
        row = sheet.createRow(rowNo++);

        headers(workbook, sheet, rowNo++);


        row = sheet.createRow(rowNo++);
        int cellNo23 = 0;
        for (String s: pDeductionNames) {
            int i = 0;
            for (int j = 1; j <= 12; j++) {
                if (j == 1) {


                    cell = row.createCell(cellNo23++);
                    cell.setCellValue(serialNo++);
                    cellStyle(workbook, cell);

                    cell = row.createCell(cellNo23++);
                    cell.setCellValue("Total Gross");
                    cellStyle(workbook, cell);
                }
                PRCDeductionsBeanHolder e = getDeductionBeanByNameAndMonth(j, s, pDeductionsList);
                if (e.isNew()) {
                    cell = row.createCell(cellNo23++);
                    cell.setCellValue("0.00");
                    cellStyle(workbook, cell);
                } else {
                    cell = row.createCell(cellNo23++);
                    cell.setCellValue(e.getDeductionBean().getAmountStr());
                    cellStyle(workbook, cell);
                }
            }

            row = sheet.createRow(rowNo++);
        }

            int i = 0;
        row = sheet.createRow(rowNo++);
        int cellNo24 = 0;
            for (int j = 1; j <= 12; j++) {
                if (j == 1) {


                    cell = row.createCell(cellNo24++);
                    cell.setCellValue(serialNo++);
                    cellStyle(workbook, cell);

                    cell = row.createCell(cellNo24++);
                    cell.setCellValue("Development Levy");
                    cellStyle(workbook, cell);
                }
                EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
                if (e.isNewEntity()) {
                    cell = row.createCell(cellNo24++);
                    cell.setCellValue("0.00");
                    cellStyle(workbook, cell);
                } else {
                    cell = row.createCell(cellNo24++);
                    cell.setCellValue(e.getDevelopmentLevyStrSansNaira());
                    cellStyle(workbook, cell);
                }
            }

        row = sheet.createRow(rowNo++);
            int cellNo25 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo25++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo25++);
                cell.setCellValue("TWS");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo25++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo25++);
                cell.setCellValue(e.getTwsStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
        int cellNo26 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo26++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo26++);
                cell.setCellValue("Union Dues");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo26++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo26++);
                cell.setCellValue(e.getUnionDuesStrSansNaira());
                cellStyle(workbook, cell);
            }
        }

        row = sheet.createRow(rowNo++);
        int cellNo27 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo27++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo27++);
                cell.setCellValue("N.H.F");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo27++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo27++);
                cell.setCellValue(e.getNhfStrSansNaira());
                cellStyle(workbook, cell);
            }
        }


        row = sheet.createRow(rowNo++);
        int cellNo28 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo28++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo28++);
                cell.setCellValue("P.A.Y.E");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo28++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo28++);
                cell.setCellValue(e.getMonthlyTaxStr());
                cellStyle(workbook, cell);
            }
        }


        row = sheet.createRow(rowNo++);
        int cellNo29 = 0;
        for (int j = 1; j <= 12; j++) {
            if (j == 1) {


                cell = row.createCell(cellNo29++);
                cell.setCellValue(serialNo++);
                cellStyle(workbook, cell);

                cell = row.createCell(cellNo29++);
                cell.setCellValue("Total Net");
                cellStyle(workbook, cell);
            }
            EmployeePayBean e = getEmployeePayBeanByMonth(j, pListHolder);
            if (e.isNewEntity()) {
                cell = row.createCell(cellNo29++);
                cell.setCellValue("0.00");
                cellStyle(workbook, cell);
            } else {
                cell = row.createCell(cellNo29++);
                cell.setCellValue(e.getNetPayStr());
                cellStyle(workbook, cell);
            }
        }


    }

    private void headers(Workbook workbook, Sheet sheet, int rowNo) {
        Row row;
        int cellNo = 0;


        row = sheet.createRow(rowNo);
        Cell cell = row.createCell(cellNo++);
        cell.setCellValue("S/No");
        cellStyle(workbook, cell);

        Cell cell1 = row.createCell(cellNo++);
        cell1.setCellValue("ITEM");
        cellStyle(workbook, cell1);

        Cell cell2 = row.createCell(cellNo++);
        cell2.setCellValue("JAN");
        cellStyle(workbook, cell2);

        Cell cell3 = row.createCell(cellNo++);
        cell3.setCellValue("FEB");
        cellStyle(workbook, cell3);

        Cell cell4 = row.createCell(cellNo++);
        cell4.setCellValue("MAR");
        cellStyle(workbook, cell4);

        Cell cell5 = row.createCell(cellNo++);
        cell5.setCellValue("APR");
        cellStyle(workbook, cell5);

        Cell cell6 = row.createCell(cellNo++);
        cell6.setCellValue("MAY");
        cellStyle(workbook, cell6);

        Cell cell7 = row.createCell(cellNo++);
        cell7.setCellValue("JUN");
        cellStyle(workbook, cell7);

        Cell cell8 = row.createCell(cellNo++);
        cell8.setCellValue("JUL");
        cellStyle(workbook, cell8);

        Cell cell9 = row.createCell(cellNo++);
        cell9.setCellValue("AUG");
        cellStyle(workbook, cell9);

        Cell cell10 = row.createCell(cellNo++);
        cell10.setCellValue("SEP");
        cellStyle(workbook, cell10);

        Cell cell11 = row.createCell(cellNo++);
        cell11.setCellValue("OCT");
        cellStyle(workbook, cell11);

        Cell cell12 = row.createCell(cellNo++);
        cell12.setCellValue("NOV");
        cellStyle(workbook, cell12);

        Cell cell13 = row.createCell(cellNo++);
        cell13.setCellValue("DEC");
        cellStyle(workbook, cell13);
    }

    private void addSheetTitle(Workbook workbook, Sheet sheet, String reportTitle, int pYear) {
        LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
        Month currentMonth = currentdate.getMonth();
        int currentYear = currentdate.getYear();
        int rowNo = 5;
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String currentTime = dateFormat.format(new Date());
        String fullDate = currentMonth.toString() +" "+currentDay+", "+currentYear;

        CellStyle headerStyle = workbook.createCellStyle();

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);



        Row GroupHeaderRow = sheet.createRow(rowNo++);
        Cell headerCell = GroupHeaderRow.createCell(0);
        headerCell.setCellValue("Employee Pay Record Card : " + this.fPayRecordBean.getEmployeeId() + " >> " + this.fPayRecordBean.getEmployeeName() + " : Incremental Month January : " + this.fPayRecordBean.getSalaryScaleLevelAndStep());
        headerCell.setCellStyle(headerStyle);

        Row GroupHeaderRow2 = sheet.createRow(rowNo++);
        Cell headerCell2 = GroupHeaderRow2.createCell(0);
        headerCell2.setCellValue("PRC for Year " + pYear);
        headerCell2.setCellStyle(headerStyle);

        Row GroupHeaderRow3 = sheet.createRow(rowNo++);
        Cell headerCell3 = GroupHeaderRow3.createCell(0);
        headerCell3.setCellValue("Print Date: "+fullDate +", "+currentTime);
        headerCell3.setCellStyle(headerStyle);
    }

    private void drawPictureHeader(Workbook workbook, Sheet sheet, String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        //Get the contents of an InputStream as a byte[].
        byte[] bytes = IOUtils.toByteArray(inputStream);
        //Adds a picture to the workbook
        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        //close the input stream
        inputStream.close();

        //Returns an object that handles instantiating concrete classes
        CreationHelper helper = workbook.getCreationHelper();

        //Creates the top-level drawing patriarch.
        Drawing drawing = sheet.createDrawingPatriarch();

        //Create an anchor that is attached to the worksheet
        ClientAnchor anchor = helper.createClientAnchor();
        //set top-left corner for the image
        anchor.setCol1(0);
        anchor.setRow1(0);

        //Creates a picture
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        //Reset the image to the original size
        pict.resize();
    }

    private void cellStyle(Workbook workbook, Cell cell){

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        CellStyle totalStyle = workbook.createCellStyle();
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalStyle.setFont(font);
        totalStyle.setBorderBottom(BorderStyle.THIN);

        totalStyle.setBorderBottom(BorderStyle.THIN);
        totalStyle.setBorderTop(BorderStyle.THIN);
        totalStyle.setBorderRight(BorderStyle.THIN);
        totalStyle.setBorderLeft(BorderStyle.THIN);

        cell.setCellStyle(totalStyle);
    }

    private PRCDeductionsBeanHolder getDeductionBeanByNameAndMonth(int pJ, String pS, List<PRCDeductionsBeanHolder> pDeductionsList)
    {
        PRCDeductionsBeanHolder wRetVal = new PRCDeductionsBeanHolder();

        for (PRCDeductionsBeanHolder p : pDeductionsList) {
            if ((p.getMonth() == pJ) && (p.getDeductionBean().getName().equalsIgnoreCase(pS))) {
                wRetVal = p;
                break;
            }
        }
        return wRetVal;
    }

    private EmployeePayBean getEmployeePayBeanByMonth(int pMonth, List<PRCDisplayBeanHolder> pListHolder)
    {
        EmployeePayBean wRetVal = new EmployeePayBean();
        for (PRCDisplayBeanHolder p : pListHolder) {
            if (p.getMonth() == pMonth) {
                wRetVal = p.getEmployeePayBean();
                break;
            }
        }

        return wRetVal;
    }
}