package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUploadTemplateExcelGenerator {


    /**
     * Kasumu Taiwo
     * 12-2020
     */

    List<String> tableValues = new ArrayList<>();
    List<String> subGroupValues = new ArrayList<>();

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

       // File currDir = new File("src/main/resources/static/images/"+rt.getBusinessCertificate().getClientReportLogo());
        File currDir = ResourceUtils.getFile("classpath:static/images/"+rt.getBusinessCertificate().getClientReportLogo());
        String path = currDir.getAbsolutePath();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" +rt.getReportTitle()+".xlsx");

        Sheet sheet = workbook.createSheet(rt.getReportTitle());
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);


        Row headerRow;

       headerRow = sheet.createRow(0);


        createHeaderRow(workbook, headerRow, rt.getTableHeaders());

        if(rt.getTableType() == 0){
            ungroupedData(rt.getTableHeaders(), rt.getTableData(), workbook, sheet, rt.getTotalInd());
        }


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




    private void sumBySubGroup(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook,
                               Sheet sheet, String groupBy, String groupby_value, String subgroupBy, String subgroupby_value, int rowNo) {
        // TODO Auto-generated method stub
        Row row = null;
        int cellNo = 0;
        row = sheet.createRow(rowNo + 1);
        for (Map<String, Object> datarow : headers) {
            cellNo = cellNo + 1;
            sumSubColumn(data, datarow, row, subgroupBy, workbook, subgroupby_value, groupBy,
                    groupby_value, sheet, rowNo, cellNo);
        }
    }


    private void sumSubColumn(List<Map<String, Object>> data, Map<String, Object> datarow, Row row, String subgroupBy, Workbook workbook,
                              String subgroupby_value, String groupBy, String groupby_value, Sheet sheet, int rowNo, int cellNo) {
        // TODO Auto-generated method stub
        Double total = 0.0;
        if ((datarow.get("totalInd").toString().equals(1)) ||  (datarow.get("totalInd").toString().equals(2))){
            for (Map<String, Object> str : data) {
                String cCompare = str.get(groupBy).toString();
                String dCompare = str.get(subgroupBy).toString();
                if ((str.get(datarow.get("headerName").toString()) != null) && (cCompare.equals(groupby_value))
                        && (dCompare.equals(subgroupby_value))) {
                    String no = str.get(datarow.get("headerName").toString()).toString();
                    Double val = Double.valueOf(no);
                    total = total += val;
                } else {

                }
            }
            Cell tCell = row.createCell(1);
            tCell.setCellValue(subgroupby_value);
            cellStyle(tCell);

            Cell gCell = row.createCell(2);
            gCell.setCellValue("Total");
            cellStyle(gCell);

        }

        if (datarow.get("totalInd").toString().equals(1)){
            Cell cell = row.createCell(cellNo);
            cell.setCellValue(total);
            cellStyle(cell);
        }

        else if (datarow.get("totalInd").toString().equals(2)){
            Cell cell = row.createCell(cellNo);
            cell.setCellValue(IConstants.naira + total);
            cellStyle(cell);
        }

        if(datarow.get("totalInd").toString().equals("1")) {
            for (Map<String, Object> str : data) {
                if((str.get(datarow.get("headerName").toString()) != null)) {
                    String no = str.get(datarow.get("headerName")).toString();
                    Double val = Double.parseDouble(no);
                    total +=val;

                }
            }



            Cell vCell = row.createCell(cellNo);
            vCell.setCellValue(total.toString());
            cellStyle(vCell);

        }
        else if(datarow.get("totalInd").toString().equals("2")) {
            for (Map<String, Object> str : data) {
                if ((str.get(datarow.get("headerName").toString()) != null)) {
                    String no = str.get(datarow.get("headerName")).toString();
                    Double val = Double.parseDouble(PayrollHRUtils.removeCommas(no));
                    total += val;

                }
            }

            Cell vCell = row.createCell(cellNo);
            String newTotal =  PayrollHRUtils.getDecimalFormat().format(total);
            vCell.setCellValue(IConstants.naira + total.toString());
            cellStyle(vCell);
        }
        else if(datarow.get("totalInd").toString().equals("3")) {

        }
        else {
            Cell vCell = row.createCell(cellNo);
            cellStyle(vCell);
        }
    }


    private void createHeaderRow(Workbook workbook, Row headerRow, List<Map<String, Object>> headers) {
        // TODO Auto-generated method stub

        headerStyle.setFont(arialFont9);


        int cellNo = 0;
        for (Map<String, Object> str : headers) {
            String field = str.get("headerName").toString();
            if(str.get("totalInd").toString().equals("3")){

            }
            else {
                Cell headerCell = headerRow.createCell(cellNo++);
                headerCell.setCellValue(field);
                headerCell.setCellStyle(headerStyle);
            }
        }
    }

    private void ungroupedData(List<Map<String, Object>> headers, List<Map<String, Object>> data, Workbook workbook,
                               Sheet sheet, int totalInd) {
        // TODO Auto-generated method stub
        int rowNo = 0;


        Row row;
        int cellNo;
        int i = 0;
        for (Map<String, Object> datarow : data) {
            rowNo = rowNo + 1;
            row = sheet.createRow(rowNo);
            cellNo = 0;

            for (Map<String, Object> str : headers) {
                if ((datarow.get(str.get("headerName").toString()) != null)){

                    Cell cell = row.createCell(cellNo++);
                    if(str.get("totalInd").toString().equals("2")){
                        Double temp = Double.valueOf(datarow.get(str.get("headerName").toString()).toString());
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
                    Cell cell = row.createCell(cellNo++);
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
                cellStyle(vCell);

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
                cellStyle(vCell);
            }
            else if(datarow.get("totalInd").toString().equals("3")) {

            }
            else {
                Cell vCell = row.createCell(cellNo);
                cellStyle(vCell);
            }

        }
    }



    private void cellStyle(Cell cell){


        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalStyle.setFont(arialNormalFont10);
        totalStyle.setBorderBottom(BorderStyle.THIN);

        totalStyle.setBorderBottom(BorderStyle.THIN);
        totalStyle.setBorderTop(BorderStyle.THIN);
        totalStyle.setBorderRight(BorderStyle.THIN);
        totalStyle.setBorderLeft(BorderStyle.THIN);

        cell.setCellStyle(totalStyle);
    }


}
