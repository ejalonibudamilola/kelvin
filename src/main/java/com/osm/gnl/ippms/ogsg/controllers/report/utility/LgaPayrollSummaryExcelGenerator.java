package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LgaPayrollSummaryExcelGenerator {


    List<String> table_values = new ArrayList<>();
    List<String> subgroup_values = new ArrayList<>();

    private XSSFFont arialFont9;
    private XSSFFont arialFont10;
    private XSSFFont  arialNormalFont10;
    private XSSFFont arialFont12;
    private CellStyle totalStyle;
    private CellStyle headerStyle;
    private CellStyle normalStyle;
    private Workbook workbook;


    public void getExcel(HttpServletResponse response, HttpServletRequest request, ReportGeneratorBean rt) throws IOException {

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

        Row headerRow;

        int plusFive = 0;
        if((rt.getMainHeaders().size() > 2) && (rt.getMainHeaders().size() < 6)){
            plusFive = 5;
            headerRow = sheet.createRow(8);
        }
        else if(rt.getMainHeaders().size() > 6){
            plusFive = 7;
            headerRow = sheet.createRow(12);
        }
        else {
            headerRow = sheet.createRow(6);
        }

        createHeaderRow(workbook, headerRow, rt.getTableHeaders());

        ungroupedData(rt.getTableHeaders(), rt.getTableData(), workbook, sheet, rt.getTotalInd(), plusFive);

        groupedData(rt.getTableHeaders(), rt.getTableData(), workbook, sheet, rt.getGroupBy(), rt.getMainHeaders(), path, rt.getTotalInd(), plusFive);



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


    private void addNewSheetTitle(Sheet sheet, List<String> mainHeaders, String groupBy) {
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

        Row GroupHeaderRow3 = sheet.createRow(rowNo+1);
        Cell headerCell3 = GroupHeaderRow3.createCell(0);
        headerCell3.setCellValue("Print Date: "+fullDate+" ,"+currentTime+"");
        headerCell3.setCellStyle(headerStyle);

        Row GroupHeaderRow4 = sheet.createRow(rowNo+2);
        Cell headerCell4 = GroupHeaderRow4.createCell(0);
        headerCell4.setCellValue(groupBy);
        headerCell4.setCellStyle(headerStyle);
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


    @SuppressWarnings("unused")
    private void createHeaderRow(Workbook workbook, Row headerRow, List<Map<String, Object>> headers) {
        // TODO Auto-generated method stub
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.ROSE.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setRotation((short)90);


        headerStyle.setFont(arialFont9);

        Cell sn = headerRow.createCell(0);
        sn.setCellValue("S/No");
        sn.setCellStyle(headerStyle);

        int cellNo = 0;
        for (Map<String, Object> str : headers) {
            String field = str.get("headerName").toString();
            cellNo = cellNo + 1;
            if(str.get("totalInd").toString().equals("3")){

            }
            else {
                Cell headerCell = headerRow.createCell(cellNo);
                headerCell.setCellValue(field);
                headerCell.setCellStyle(headerStyle);
            }
        }
    }

    private void ungroupedData(List<Map<String, Object>> headers, List<Map<String, Object>> data, Workbook workbook,
                               Sheet sheet, int totalInd, int plusFive) {
        // TODO Auto-generated method stub
        int rowNo;
        if(plusFive == 5) {
            rowNo = 8;
        }else if(plusFive == 7){
            rowNo = 10;
        }
        else{
            rowNo = 6;
        }
        Row row;
        int cellNo;
        int i = 0;
        for (Map<String, Object> datarow : data) {
            rowNo = rowNo + 1;
            row = sheet.createRow(rowNo);
            cellNo = 0;
            Cell sn = row.createCell(0);
            cellStyle(sn);
            sn.setCellValue(i += 1);

            for (Map<String, Object> str : headers) {
                if ((datarow.get(str.get("headerName").toString()) != null)){
                    cellNo = cellNo + 1;
                    Cell cell = row.createCell(cellNo);
                    if(str.get("totalInd").toString().equals("2")){
                        Double temp = Double.valueOf(PayrollHRUtils.removeCommas(datarow.get(str.get("headerName").toString()).toString()));
                        String newTotal =  PayrollHRUtils.getDecimalFormat().format(temp);
                        cell.setCellValue(IConstants.naira + newTotal);
                        cellStyle(cell);
                    }
                    else if(str.get("totalInd").toString().equals("3")){
                        cell.setCellValue("");
                    }
                    else if(str.get("totalInd").toString().equals("1")){
                        Integer temp = Integer.valueOf(datarow.get(str.get("headerName").toString()).toString());
                        cell.setCellValue(temp);
                        cellStyle(cell);
                    }
                    else {
                        cell.setCellValue(datarow.get(str.get("headerName").toString()).toString());
                        cellStyle(cell);
                    }
                } else {
                    cellNo = cellNo + 1;
                    Cell cell = row.createCell(cellNo);
                    cellStyle(cell);
                }
            }

        }

        if (totalInd == 1)
            sumUngroupedRowCells(data, headers, workbook, sheet, rowNo);
    }

    private void sumUngroupedRowCells(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook, Sheet sheet, int rowNo){
        Row row = null;
        row = sheet.createRow(rowNo+1);
        int cellNo = 0;
        Cell tCell = row.createCell(0);
        tCell.setCellValue("Grand Total");
        cellStyle(tCell);
        for (Map<String, Object> datarow : headers) {
            cellNo = cellNo+1;

            if(datarow.get("totalInd").toString().equals("1")) {
                Integer total = 0;
                for (Map<String, Object> str : data) {
                    if((str.get(datarow.get("headerName").toString()) != null)) {
                        String no = str.get(datarow.get("headerName")).toString();
                        Integer val = Integer.valueOf(no);
                        total +=val;

                    }
                }



                Cell vCell = row.createCell(cellNo);
                vCell.setCellValue(total.toString());
                cellStyleBold(vCell);

            }
            else if(datarow.get("totalInd").toString().equals("2")) {
                Double total = 0.0;
                for (Map<String, Object> str : data) {
                    if ((str.get(datarow.get("headerName").toString()) != null)) {
                        String no = str.get(datarow.get("headerName")).toString();
                        Double val = Double.parseDouble(PayrollHRUtils.removeCommas(no));
                        total += val;

                    }
                }

                Cell vCell = row.createCell(cellNo);
                String newTotal =  PayrollHRUtils.getDecimalFormat().format(total);
                vCell.setCellValue(IConstants.naira + newTotal);
                cellStyleBold(vCell);
            }
            else if(datarow.get("totalInd").toString().equals("3")) {

            }
            else {
                Cell vCell = row.createCell(cellNo);
                cellStyleBold(vCell);
            }

        }
    }


    private void groupedData(List<Map<String, Object>> headers, List<Map<String, Object>> data, Workbook workbook,
                             Sheet sheet, String groupBy, List<String> mainHeaders, String imgPath, int totalInd, int plusFive) throws IOException {
        // TODO Auto-generated method stub
        int rowNo;
        if(plusFive == 5){
            rowNo = 9;
        }
        else if(plusFive == 7){
            rowNo = 13;
        }
        else {
            rowNo = 6;
        }
        for (Map<String, Object> row : data) {
            String dump_value = "";
            rowNo = rowNo + 1;

            String table_value = row.get(groupBy).toString();
            boolean test_existence = test(table_value);
            if(!test_existence) {

                createNewGroupedSheet(data, headers, workbook, groupBy, table_value, mainHeaders, imgPath);
            }
            else {
                dump_value = "no";
            }
        }
    }


    private void createNewGroupedSheet(List<Map<String, Object>> data, List<Map<String, Object>> headers,
                                       Workbook workbook, String groupBy, String table_value, List<String> mainHeaders, String imgPath) throws IOException {
        // TODO Auto-generated method stub
        String str;
        str = table_value.replaceAll("[^a-zA-Z0-9]", " ");
        Sheet newSheet = workbook.createSheet(str);
        newSheet.setColumnWidth(0, 6000);
        newSheet.setColumnWidth(1, 4000);
        drawPictureHeader(workbook, newSheet, imgPath);

        addNewSheetTitle(newSheet, mainHeaders, table_value);
        createNewGroupedSheetHeaders(newSheet, workbook, headers);
        createNewGroupedSheetRows(newSheet, workbook, data, headers, table_value, groupBy);
    }



    private void createNewGroupedSheetHeaders(Sheet newSheet, Workbook workbook, List<Map<String, Object>> headers) {
        // TODO Auto-generated method stub
        Row headerRow = newSheet.createRow(6);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.ROSE.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setRotation((short)90);


        headerStyle.setFont(arialFont10);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);

        Cell sn = headerRow.createCell(0);
        sn.setCellValue("S/No");
        sn.setCellStyle(headerStyle);
        int cellNo = 0;
        for (Map<String, Object> str : headers) {
            String field = str.get("headerName").toString();
            cellNo = cellNo + 1;
            if((str.get("totalInd").toString()).equals("3")){

            }
            else {
                Cell headerCell = headerRow.createCell(cellNo);
                headerCell.setCellValue(field);
                headerCell.setCellStyle(headerStyle);
            }
        }
    }




    private void createNewGroupedSheetRows(Sheet newSheet, Workbook workbook, List<Map<String, Object>> data,
                                           List<Map<String, Object>> headers, String table_value, String groupBy) {
        // TODO Auto-generated method stub
        Row row = null;
        int rowNo = 6;
        int cellNo = 0, i = 0;
        for (Map<String, Object> datarow : data) {
            if(datarow.get(groupBy).toString().equals(table_value)) {
                cellNo = 0;
                rowNo = rowNo + 1;
                row = newSheet.createRow(rowNo);
                Cell sn = row.createCell(0);
//                cellStyle(workbook, sn);
                sn.setCellValue(i += 1);
            }
            else {
                row = newSheet.createRow(5);
            }
            for (Map<String, Object> str : headers) {
                if ((datarow.get(str.get("headerName").toString()) != null) && (datarow.get(groupBy).toString().equals(table_value))){
                    CellStyle style = workbook.createCellStyle();
                    style.setWrapText(true);
                    cellNo = cellNo + 1;

                    Cell cell = row.createCell(cellNo);
                    if(str.get("totalInd").toString().equals("2")){
                        Double temp = Double.valueOf(PayrollHRUtils.removeCommas(datarow.get(str.get("headerName").toString()).toString()));
                        String newTotal =  PayrollHRUtils.getDecimalFormat().format(temp);
                        cell.setCellValue(IConstants.naira + newTotal);
                        cellStyle(cell);
                    }
                    else if(str.get("totalInd").toString().equals("3")){

                    }
                    else{
                        cell.setCellValue(datarow.get(str.get("headerName").toString()).toString());
//                        cellStyle(workbook, cell);
                    }
                } else {
                    cellNo = cellNo + 1;
                    Cell cell = row.createCell(cellNo);
//                    cellStyle(workbook, cell);
                }
            }
        }
        sumByGroup(data, headers, workbook, newSheet, groupBy, table_value, rowNo);
    }




    private int groupRows(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook,
                          Sheet sheet, String groupBy, String table_value, int rowNo) {
        // TODO Auto-generated method stub
        Row row = null;
        int cellNo = 0, i = 0;
        for (Map<String, Object> datarow : data) {
            if(datarow.get(groupBy).toString().equals(table_value)) {
                rowNo = rowNo + 1;
                cellNo = 0;
                row = sheet.createRow(rowNo);
                Cell sn = row.createCell(0);
                cellStyle(sn);
                sn.setCellValue(i += 1);
            }
            else{
                row = sheet.createRow(5);
            }
            for (Map<String, Object> str : headers) {
                if ((datarow.get(str.get("headerName").toString()) != null) && (datarow.get(groupBy).toString().equals(table_value))){
                    CellStyle style = workbook.createCellStyle();
                    style.setWrapText(true);
                    cellNo = cellNo + 1;

                    Cell cell = row.createCell(cellNo);
                    if(str.get("totalInd").toString().equals("2")){
                        Double temp = Double.valueOf(PayrollHRUtils.removeCommas(datarow.get(str.get("headerName").toString()).toString()));
                        String newTotal =  PayrollHRUtils.getDecimalFormat().format(temp);
                        cell.setCellValue(IConstants.naira + newTotal);
                        cellStyle(cell);
                    }
                    else if(str.get("totalInd").toString().equals("3")){

                    }
                    else{
                        cell.setCellValue(datarow.get(str.get("headerName").toString()).toString());
                        cellStyle(cell);
                    }
                } else {
                    cellNo = cellNo + 1;
                    Cell cell = row.createCell(cellNo);
                    cellStyle(cell);
                }
            }
        }
        sumByGroup(data, headers, workbook, sheet, groupBy, table_value, rowNo);
        return rowNo;
    }


    private void sumByGroup(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook,
                            Sheet sheet, String groupBy, String table_value, int rowNo) {
        // TODO Auto-generated method stub
        Row row = null;
        int cellNo = 0;
        row = sheet.createRow(rowNo + 1);
        for (Map<String, Object> datarow : headers) {
            cellNo = cellNo + 1;
            sumColumn(data, datarow, row, groupBy, workbook, table_value, sheet, rowNo, cellNo);

        }

    }


    private void sumColumn(List<Map<String, Object>> data, Map<String, Object> datarow, Row row, String groupBy, Workbook workbook,
                           String table_value, Sheet sheet, int rowNo, int cellNo) {
        // TODO Auto-generated method stub
        Double total = 0.0;
        if (datarow.get("totalInd").toString().equals("1")) {
            Integer val = 0;
            for (Map<String, Object> str : data) {
                String cCompare = str.get(groupBy).toString();
                if ((str.get(datarow.get("headerName").toString()) != null) && (cCompare.equals(table_value))) {
                    String no = str.get(datarow.get("headerName").toString()).toString();
                    val = Integer.valueOf(no);
                    total = total + val;
                } else {

                }
            }

            Cell gCell = row.createCell(0);
            gCell.setCellValue("Total");
            cellStyleBold(gCell);

            Cell cell = row.createCell(cellNo);
            cell.setCellValue(total);
            cellStyleBold(cell);
        }

        else if (datarow.get("totalInd").toString().equals("2")) {
            Double val = 0.0;
            for (Map<String, Object> str : data) {
                String cCompare = str.get(groupBy).toString();
                if ((str.get(datarow.get("headerName").toString()) != null) && (cCompare.equals(table_value))) {
                    String no = str.get(datarow.get("headerName").toString()).toString();
                    val = Double.parseDouble(PayrollHRUtils.removeCommas(no));
                    total = total + val;
                } else {
                    Cell cell = row.createCell(cellNo);
                    cellStyleBold(cell);
                }
            }

//            Cell tCell = row.createCell(0);
//            tCell.setCellValue(table_value);
//            cellStyle(workbook, tCell);

            Cell gCell = row.createCell(0);
            gCell.setCellValue("Total");
            cellStyleBold(gCell);

            Cell cell = row.createCell(cellNo);
            String newTotal =  PayrollHRUtils.getDecimalFormat().format(total);
            cell.setCellValue(IConstants.naira + newTotal);
            cellStyleBold(cell);
        }

        else if(datarow.get("totalInd").toString().equals("3")){

        }




    }


    private boolean test(String table_value) {
        // TODO Auto-generated method stub
        if(table_values.contains(table_value)) {
            return true;
        }
        else {
            table_values.add(table_value);
            return false;
        }
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
