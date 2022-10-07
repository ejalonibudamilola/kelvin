package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
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

public class FuturePayrollExcelGenerator {

    private XSSFFont arialFont9;
    private XSSFFont arialFont10;
    private XSSFFont  arialNormalFont10;
    private XSSFFont arialFont12;
    private CellStyle totalStyle;
    private CellStyle headerStyle;
    private CellStyle normalStyle;
    private Workbook workbook;

    public String generateExcel(WageBeanContainer data, String reportTitle, HttpServletResponse response,
                                BusinessCertificate bc) throws IOException {

        workbook = new XSSFWorkbook();

        init(workbook);

       // File currDir = new File("src/main/resources/static/images/"+bc.getClientReportLogo());
        File currDir = ResourceUtils.getFile("classpath:static/images/"+bc.getClientReportLogo());
        String path = currDir.getAbsolutePath();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" +reportTitle+".xls");


        Sheet sheet = workbook.createSheet(reportTitle);
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        drawPictureHeader(workbook, sheet, bc);

        addSheetTitle(workbook, sheet, reportTitle, bc, data);

        Row headerRow = sheet.createRow(6);

        createHeaderRow(workbook, headerRow, data, bc);


        organizationDataList(data, workbook, sheet, bc);


        workbook.write(response.getOutputStream());
        workbook.close();

        return path;
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

    private void organizationDataList(WageBeanContainer data, Workbook workbook, Sheet sheet, BusinessCertificate bc) {
        // TODO Auto-generated method stub
        int rowNum = 7;
        Row row;
        int serialNo = 0;
        int x = 0;
        Cell bodyCell = null;
        List<WageSummaryBean> pList = data.getWageSummaryBeanList();
        for (WageSummaryBean e : pList) {
            row = sheet.createRow(rowNum++);
//			this.p += 1;
            int i = 0;
            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            serialNo++; bodyCell.setCellValue(serialNo);

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getAssignedToObject());

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getPreviousNoOfEmp());

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getNoOfEmp());

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getEmpDiff());

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getPreviousBalanceStr());

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getCurrentBalanceStr());
        }

        Row footer = sheet.createRow(rowNum++);
        Cell cellFooter = null;

        cellFooter = footer.createCell(1);
        cellStyle(cellFooter);
        cellFooter.setCellValue("Sub Totals");

        cellFooter = footer.createCell(2);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalNoOfPrevMonthEmp());

        cellFooter = footer.createCell(3);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalNoOfEmp());

        cellFooter = footer.createCell(4);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalEmpDiff());

        cellFooter = footer.createCell(5);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalPrevBalStr());

        cellFooter = footer.createCell(6);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalCurrBalStr());

        rowNum += 2;


            Deductions(data, workbook, sheet, rowNum, serialNo);
    }

    private void Deductions(WageBeanContainer data, Workbook workbook, Sheet sheet, int startRow, int wSerialNo) {

        Cell bodyCell = null;
        startRow++; Row row = sheet.createRow(startRow);
        bodyCell = row.createCell(1);
        cellStyle(bodyCell);
        bodyCell.setCellValue("Deductions");

        List<WageSummaryBean> pGarnList = data.getDeductionList();
        for (WageSummaryBean e : pGarnList) {
            startRow++; row = sheet.createRow(startRow);
//			this.p += 1;
            int i = 0;
            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            wSerialNo++; bodyCell.setCellValue(wSerialNo);

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getName());

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue("");

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue("");

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue("");

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getPreviousBalanceStr());

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getCurrentBalanceStr());
        }

        startRow++;
        Row footer = sheet.createRow(startRow);
        Cell cellFooter = null;

        cellFooter = footer.createCell(1);
        cellStyle(cellFooter);
        cellFooter.setCellValue("Sub Totals");

        cellFooter = footer.createCell(2);
        cellStyle(cellFooter);
        cellFooter.setCellValue("");

        cellFooter = footer.createCell(3);
        cellStyle(cellFooter);
        cellFooter.setCellValue("");

        cellFooter = footer.createCell(4);
        cellStyle(cellFooter);
        cellFooter.setCellValue("");

        cellFooter = footer.createCell(5);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalPrevDedBalStr());

        cellFooter = footer.createCell(6);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalDedBalStr());

        startRow++;

        grandTotals(data, workbook, sheet, startRow, wSerialNo);
    }

    private void grandTotals(WageBeanContainer data, Workbook workbook, Sheet sheet, int startRow, int wSerialNo) {
        Row footer = sheet.createRow(startRow);
        Cell cellFooter = null;

        cellFooter = footer.createCell(1);
        cellStyle(cellFooter);
        cellFooter.setCellValue("Grand Totals");

        cellFooter = footer.createCell(2);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalNoOfPrevMonthEmp());

        cellFooter = footer.createCell(3);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalNoOfEmp());

        cellFooter = footer.createCell(4);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getTotalEmpDiff());

        cellFooter = footer.createCell(5);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getGrandPrevTotalStr());

        cellFooter = footer.createCell(6);
        cellStyle(cellFooter);
        cellFooter.setCellValue(data.getGrandTotalStr());
    }

    private void createHeaderRow(Workbook workbook, Row headerRow, WageBeanContainer headers, BusinessCertificate bc) {
        // TODO Auto-generated method stub
        
        
        headerStyle.setAlignment(HorizontalAlignment.RIGHT);
        headerStyle.setFont(arialFont10);

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("S/No");
        headerCell.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue(bc.getMdaTitle());
        headerCell2.setCellStyle(headerStyle);

        Cell headerCell3= headerRow.createCell(2);
        headerCell3.setCellValue("Future Staff Strength");
        headerCell3.setCellStyle(headerStyle);

        Cell headerCell4= headerRow.createCell(3);
        headerCell4.setCellValue("Current Staff Strength");
        headerCell4.setCellStyle(headerStyle);

        Cell headerCell5= headerRow.createCell(4);
        headerCell5.setCellValue("No. Of Retirees");
        headerCell5.setCellStyle(headerStyle);

        Cell headerCell6= headerRow.createCell(5);
        headerCell6.setCellValue(headers.getPrevMonthAndYearStr());
        headerCell6.setCellStyle(headerStyle);

        Cell headerCell7= headerRow.createCell(6);
        headerCell7.setCellValue(headers.getMonthAndYearStr());
        headerCell7.setCellStyle(headerStyle);
    }


    private void drawPictureHeader(Workbook workbook, Sheet sheet, BusinessCertificate bc) throws IOException {
        // TODO Auto-generated method stub
        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:static/images/"+bc.getClientReportLogo()));
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

    private void addSheetTitle(Workbook workbook, Sheet sheet, String reportTitle, BusinessCertificate bc, WageBeanContainer data) {
        // TODO Auto-generated method stub
        LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
        Month currentMonth = currentdate.getMonth();
        int currentYear = currentdate.getYear();

        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String currentTime = dateFormat.format(new Date());
        String fullDate = currentMonth.toString() +" "+currentDay+", "+currentYear;
        
        headerStyle.setFont(arialFont10);

        Row GroupHeaderRow1 = sheet.createRow(3);
        Cell headerCell1 = GroupHeaderRow1.createCell(0);
        headerCell1.setCellValue(bc.getBusinessName()+ " Futuristic Payroll Simulation Report for "+ data.getMonthAndYearStr() );
        headerCell1.setCellStyle(headerStyle);


        Row GroupHeaderRow3 = sheet.createRow(4);
        Cell headerCell3 = GroupHeaderRow3.createCell(0);
        headerCell3.setCellValue("Print Date: "+fullDate);
        headerCell3.setCellStyle(headerStyle);

        Row GroupHeaderRow4 = sheet.createRow(5);
        Cell headerCell4 = GroupHeaderRow4.createCell(0);
        headerCell4.setCellValue("Time: "+currentTime);
        headerCell4.setCellStyle(headerStyle);
    }

    private void cellStyle(Cell cell){
        
        totalStyle.setFont(arialFont10);
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalStyle.setBorderBottom(BorderStyle.MEDIUM);

        totalStyle.setBorderBottom(BorderStyle.MEDIUM);
        totalStyle.setBorderTop(BorderStyle.MEDIUM);
        totalStyle.setBorderRight(BorderStyle.MEDIUM);
        totalStyle.setBorderLeft(BorderStyle.MEDIUM);

        cell.setCellStyle(totalStyle);
    }

    private void cellSimpleStyle(Cell cell){

        
        normalStyle.setFont(arialNormalFont10);
        normalStyle.setAlignment(HorizontalAlignment.RIGHT);
        normalStyle.setBorderBottom(BorderStyle.THIN);

        normalStyle.setBorderBottom(BorderStyle.THIN);
        normalStyle.setBorderTop(BorderStyle.THIN);
        normalStyle.setBorderRight(BorderStyle.THIN);
        normalStyle.setBorderLeft(BorderStyle.THIN);

        cell.setCellStyle(normalStyle);
    }

}
