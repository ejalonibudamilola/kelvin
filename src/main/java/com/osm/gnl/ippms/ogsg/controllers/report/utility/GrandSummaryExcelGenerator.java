package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;

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

public class GrandSummaryExcelGenerator {

    private XSSFFont arialFont9;
    private XSSFFont arialFont10;
    private XSSFFont  arialNormalFont10;
    private XSSFFont arialFont12;
    private CellStyle totalStyle;
    private CellStyle headerStyle;
    private CellStyle normalStyle;
    private Workbook workbook;

    public void getExcel(HttpServletResponse response, ReportGeneratorBean rt) throws IOException {

        workbook = new XSSFWorkbook();

        init(workbook);

        //File currDir = new File("src/main/resources/static/images/"+rt.getBusinessCertificate().getClientReportLogo());
        File currDir = ResourceUtils.getFile("classpath:static/images/"+rt.getBusinessCertificate().getClientReportLogo());
        String path = currDir.getAbsolutePath();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" +rt.getReportTitle()+".xls");

        Sheet sheet = workbook.createSheet(rt.getReportTitle());
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        drawPictureHeader(workbook, sheet, path);

        addSheetTitle(sheet, rt.getMainHeaders());

        Row headerRow, headerRow2;

        headerRow = sheet.createRow(6);

        createHeaderRow(workbook, headerRow);

        ungroupedData(rt.getEmployeePayBean(), rt.getDeductionDetailsBean(), sheet);

        headerRow2 = sheet.createRow(12);
        createHeaderRow2(workbook, headerRow2);

        ungroupedData2(rt.getDeductionDetailsBean(), sheet, rt.getTotalInd());


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

    private void drawPictureHeader(Workbook workbook, Sheet sheet, String imgPath) throws IOException {
        // TODO Auto-generated method stub
        InputStream inputStream = new FileInputStream(imgPath);
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
        pict.resize(2, 2);
    }


    private void addSheetTitle(Sheet sheet, List<String> mainHeaders) {
        // TODO Auto-generated method stub

        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();
        int rowNo = 2;
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String currentTime = dateFormat.format(new Date());
        String fullDate = currentMonth.toString() +" "+currentDay+", "+currentYear;

        
        headerStyle.setFont(arialFont10);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);


        for(String cn : mainHeaders) {
            rowNo = rowNo + 1;
            Row GroupHeaderRow2 = sheet.createRow(rowNo);
            Cell headerCell2 = GroupHeaderRow2.createCell(0);
            headerCell2.setCellValue(cn);
            headerCell2.setCellStyle(headerStyle);
        }

        Row GroupHeaderRow3 = sheet.createRow(rowNo+ 1);
        Cell headerCell3 = GroupHeaderRow3.createCell(0);
        headerCell3.setCellValue("Print Date: "+fullDate+" ,"+currentTime+"");
        headerCell3.setCellStyle(headerStyle);

    }


    private void createHeaderRow(Workbook workbook, Row headerRow) {
        // TODO Auto-generated method stub
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        headerStyle.setAlignment(HorizontalAlignment.LEFT);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        
        headerStyle.setFont(arialFont9);

        Cell sn = headerRow.createCell(0);
        sn.setCellValue("S/No");
        sn.setCellStyle(headerStyle);


        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Earnings");
        headerCell.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(2);
        headerCell2.setCellValue("Amount");
        headerCell2.setCellStyle(headerStyle);
    }



    private void createHeaderRow2(Workbook workbook, Row headerRow) {
        // TODO Auto-generated method stub
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        
        headerStyle.setFont(arialFont9);


        int cellNo = 0;

        Cell headerCell = headerRow.createCell(cellNo+1);
        headerCell.setCellValue("Less Following Deductions");
        headerCell.setCellStyle(headerStyle);
    }

    private void ungroupedData(EmployeePayBean data, DeductionDetailsBean miniBean,
                               Sheet sheet) {
        // TODO Auto-generated method stub
        int rowNo = 6;

        Row row;
        int cellNo;
        int i = 0;

            rowNo = rowNo + 1;
            row = sheet.createRow(rowNo);
            cellNo = 0;
            Cell sn = row.createCell(0);
            cellStyle(sn);
            sn.setCellValue(i + 1);

                    cellNo = cellNo + 1;
                    Cell cell = row.createCell(cellNo);
                    cell.setCellValue("Monthly Pay");
                    cellStyle(cell);

                    cellNo = cellNo + 1;
                    cell = row.createCell(cellNo);
                        Double temp = data.getMonthlyBasic();
                        String newTotal =  PayrollHRUtils.getDecimalFormat().format(temp);
                        cell.setCellValue(IConstants.naira + newTotal);
                        cellStyle(cell);

        rowNo = rowNo + 1;
        row = sheet.createRow(rowNo);
        cellNo = 0;
        sn = row.createCell(0);
        cellStyle(sn);
        sn.setCellValue(i + 2);



        cellNo = cellNo + 1;
        cell = row.createCell(cellNo);
        cell.setCellValue("Arrears");
        cellStyle(cell);

        cellNo = cellNo + 1;
        cell = row.createCell(cellNo);
        temp = data.getArrears();
        newTotal =  PayrollHRUtils.getDecimalFormat().format(temp);
        cell.setCellValue(IConstants.naira + newTotal);
        cellStyle(cell);


        rowNo = rowNo + 1;
        row = sheet.createRow(rowNo);
        cellNo = 0;
        sn = row.createCell(0);
        cellStyle(sn);
        sn.setCellValue(i + 3);

        cellNo = cellNo + 1;
        cell = row.createCell(cellNo);
        cell.setCellValue("Accrued Arrears");
        cellStyle(cell);

        cellNo = cellNo + 1;
        cell = row.createCell(cellNo);
        temp = data.getOtherArrears();
        newTotal =  PayrollHRUtils.getDecimalFormat().format(temp);
        cell.setCellValue(IConstants.naira + newTotal);
        cellStyle(cell);


        rowNo = rowNo + 1;
        row = sheet.createRow(rowNo);
        cellNo = 0;
        sn = row.createCell(0);
        cellStyle(sn);
        sn.setCellValue(i + 4);

        cellNo = cellNo + 1;
        cell = row.createCell(cellNo);
        cell.setCellValue("Total Gross Pay");
        cellStyle(cell);

        cellNo = cellNo + 2;
        cell = row.createCell(cellNo);
        temp = miniBean.getTotalGross();
        newTotal =  PayrollHRUtils.getDecimalFormat().format(temp);
        cell.setCellValue(IConstants.naira + newTotal);
        cellStyle(cell);

    }


    private void ungroupedData2(DeductionDetailsBean miniBean,
                                Sheet sheet, int totalInd) {
        // TODO Auto-generated method stub
        int rowNo = 12;

        Row row;
        int cellNo;
        int i = 4;



        for(DeductGarnMiniBean m : miniBean.getDeductionMiniBean()){
            rowNo = rowNo + 1;
            row = sheet.createRow(rowNo);
            cellNo = 0;
            Cell sn = row.createCell(0);
            cellStyle(sn);
            sn.setCellValue(i += 1);

            cellNo = cellNo + 1;
            Cell cell = row.createCell(cellNo);
                cell.setCellValue(m.getName());
            cellStyle(cell);

            cellNo = cellNo + 1;
            cell = row.createCell(cellNo);
            Double temp = m.getAmount();
            String newTotal = PayrollHRUtils.getDecimalFormat().format(temp);
            cell.setCellValue(IConstants.naira + newTotal);
            cellStyle(cell);

        }

        rowNo = rowNo+1;
        row = sheet.createRow(rowNo);
        Cell totalCell = row.createCell(1);
        totalCell.setCellValue("Total Deduction");
        cellStyle(totalCell);

        totalCell = row.createCell(3);
        totalCell.setCellValue(IConstants.naira + PayrollHRUtils.getDecimalFormat().format(miniBean.getTotalCurrentDeduction()));
        cellStyle(totalCell);


        rowNo = rowNo+1;
        row = sheet.createRow(rowNo);
        Cell totalCell2 = row.createCell(1);
        totalCell2.setCellValue("Net Pay");
        cellStyle(totalCell2);


        totalCell2 = row.createCell(3);
        totalCell2.setCellValue(IConstants.naira + PayrollHRUtils.getDecimalFormat().format(miniBean.getNetPay()));
        cellStyle(totalCell2);


    }

    private void cellStyle(Cell cell){

        normalStyle.setAlignment(HorizontalAlignment.RIGHT);
        normalStyle.setFont(arialNormalFont10);
        normalStyle.setBorderBottom(BorderStyle.THIN);

        normalStyle.setBorderBottom(BorderStyle.THIN);
        normalStyle.setBorderTop(BorderStyle.THIN);
        normalStyle.setBorderRight(BorderStyle.THIN);
        normalStyle.setBorderLeft(BorderStyle.THIN);

        cell.setCellStyle(normalStyle);
    }

    private void cellStyleBold(Cell cell){

        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalStyle.setFont(arialFont10);
        totalStyle.setBorderBottom(BorderStyle.THIN);

        totalStyle.setBorderBottom(BorderStyle.THIN);
        totalStyle.setBorderTop(BorderStyle.THIN);
        totalStyle.setBorderRight(BorderStyle.THIN);
        totalStyle.setBorderLeft(BorderStyle.THIN);

        cell.setCellStyle(totalStyle);
    }

}
