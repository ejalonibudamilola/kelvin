package com.osm.gnl.ippms.ogsg.controllers.report.utility;


import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationInfoContainer;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationInfoSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationMiniBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
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

public class PayrollSimulationExcelGenerator {

    private XSSFFont arialFont9;
    private XSSFFont arialFont10;
    private XSSFFont  arialNormalFont10;
    private XSSFFont arialFont12;
    private CellStyle totalStyle;
    private CellStyle headerStyle;
    private CellStyle normalStyle;
    private Workbook workbook;

    public String generateExcel(SimulationInfoContainer data, String reportTitle, HttpServletResponse response,
                                HttpServletRequest request, BusinessCertificate bc) throws IOException {

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

        createHeaderRow(workbook, headerRow, data);


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

    private void organizationDataList(SimulationInfoContainer data, Workbook workbook, Sheet sheet, BusinessCertificate bc) {
        int rowNum = 7;
        Row row;
        int serialNo = 0;
        int x = 0;
        Cell bodyCell = null;
        List <SimulationInfoSummaryBean> wMainList = data.getSummaryBean();

        for (SimulationInfoSummaryBean s : wMainList) {
            row = sheet.createRow(rowNum++);
//			this.p += 1;
            int i = 0;
            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            serialNo++; bodyCell.setCellValue(serialNo);

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(s.getAssignedToObject());

            for (SimulationMiniBean t : s.getMiniBeanList())
            {
                bodyCell = row.createCell(i++);
                cellSimpleStyle(bodyCell);
                bodyCell.setCellValue(t.getCurrentValueStr());
            }
        }


        Row footer = sheet.createRow(rowNum++);
        Cell cellFooter = null;

        cellFooter = footer.createCell(1);
        cellStyle(workbook, cellFooter);
        cellFooter.setCellValue("Sub Totals");

        int cellno = 2;

        List <SimulationMiniBean> wFooterList = data.getMdapFooterList();
        for (SimulationMiniBean s : wFooterList){
            cellFooter = footer.createCell(cellno++);
            cellStyle(workbook, cellFooter);
            cellFooter.setCellValue(s.getCurrentValueStr());
        }

        rowNum += 2;

        Deductions(data, workbook, sheet, rowNum, serialNo);
    }

    private void Deductions(SimulationInfoContainer data, Workbook workbook, Sheet sheet, int startRow, int wSerialNo) {

        Cell bodyCell = null;
        startRow++; Row row = sheet.createRow(startRow);
        bodyCell = row.createCell(1);
        cellStyle(workbook, bodyCell);
        bodyCell.setCellValue("Deductions");


        List<SimulationInfoSummaryBean> pGarnList = data.getDeductionsList();
        for (SimulationInfoSummaryBean e : pGarnList) {
            startRow++; row = sheet.createRow(startRow);
//			this.p += 1;
            int i = 0;
            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            wSerialNo++; bodyCell.setCellValue(wSerialNo);

            bodyCell = row.createCell(i++);
            cellSimpleStyle(bodyCell);
            bodyCell.setCellValue(e.getName());

            for (SimulationMiniBean t : e.getMiniBeanList()) {
                bodyCell = row.createCell(i++);
                cellSimpleStyle(bodyCell);
                bodyCell.setCellValue(t.getCurrentValueStr());
            }

        }

        startRow++;
        Row footer = sheet.createRow(startRow);
        Cell cellFooter = null;

        cellFooter = footer.createCell(1);
        cellStyle(workbook, cellFooter);
        cellFooter.setCellValue("Sub Totals");

        List <SimulationMiniBean> wFooterList = data.getDeductionsTotals();
        int cellno = 2;
        for (SimulationMiniBean s : wFooterList) {
            cellFooter = footer.createCell(cellno++);
            cellStyle(workbook, cellFooter);
            cellFooter.setCellValue(s.getCurrentValueStr());
        }

        startRow++;

        grandTotals(data, workbook, sheet, startRow, wSerialNo);
    }

    private void grandTotals(SimulationInfoContainer data, Workbook workbook, Sheet sheet, int startRow, int wSerialNo) {

        Row footer = sheet.createRow(startRow);
        Cell cellFooter = null;

        cellFooter = footer.createCell(1);
        cellStyle(workbook, cellFooter);
        cellFooter.setCellValue("Grand Totals");

        List <SimulationMiniBean> wFooterList = data.getFooterList();
        int cellno = 2;
        for (SimulationMiniBean s : wFooterList) {
            cellFooter = footer.createCell(cellno++);
            cellStyle(workbook, cellFooter);
            cellFooter.setCellValue(s.getCurrentValueStr());
        }
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

    private void addSheetTitle(Workbook workbook, Sheet sheet, String reportTitle, BusinessCertificate bc, SimulationInfoContainer data) {
        // TODO Auto-generated method stub
        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();

        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String currentTime = dateFormat.format(new Date());
        String fullDate = currentMonth.toString() +" "+currentDay+", "+currentYear;

        headerStyle.setFont(arialFont10);

        Row GroupHeaderRow1 = sheet.createRow(3);
        Cell headerCell1 = GroupHeaderRow1.createCell(0);
        headerCell1.setCellValue(bc.getBusinessName()+ " Payroll Simulation Results");
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

    private void createHeaderRow(Workbook workbook, Row headerRow, SimulationInfoContainer headers) {
        // TODO Auto-generated method stub
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        headerStyle.setFont(arialFont10);

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("S/No");
        headerCell.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Organization");
        headerCell2.setCellStyle(headerStyle);

        int cellno = 2;
        List<SimulationMiniBean> wHeaderList = headers.getHeaderList();
        for (SimulationMiniBean s : wHeaderList){
            headerCell2 = headerRow.createCell(cellno++);
            headerCell2.setCellValue(s.getName());
            headerCell2.setCellStyle(headerStyle);
        }
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

    private void cellStyle(Workbook workbook, Cell cell){

        totalStyle.setFont(arialFont10);
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalStyle.setBorderBottom(BorderStyle.MEDIUM);

        totalStyle.setBorderBottom(BorderStyle.MEDIUM);
        totalStyle.setBorderTop(BorderStyle.MEDIUM);
        totalStyle.setBorderRight(BorderStyle.MEDIUM);
        totalStyle.setBorderLeft(BorderStyle.MEDIUM);

        cell.setCellStyle(totalStyle);
    }
}
