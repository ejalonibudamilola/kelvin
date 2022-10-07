package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
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


public class SimpleExcelReportGenerator {

    /**
     * Kasumu Taiwo
     * 12-2020
     */

    private List<String> tableValues = new ArrayList<>();
    private List<String> subGroupValues = new ArrayList<>();


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
        //File currDir = new File("src/main/resources/static/images/" + rt.getBusinessCertificate().getClientReportLogo());
        File currDir = ResourceUtils.getFile("classpath:static/images/"+rt.getBusinessCertificate().getClientReportLogo());
        String path = currDir.getAbsolutePath();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + rt.getReportTitle()+"_"+ PayrollBeanUtils.getDateAsStringWidoutSeparators(LocalDate.now())+"_"+PayrollBeanUtils.getCurrentTime(true) + ".xls");

        Sheet sheet = workbook.createSheet(rt.getReportTitle());
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        drawPictureHeader(workbook, sheet, path);

        addSheetTitle(workbook, sheet, rt.getReportTitle(), rt.getMainHeaders());

        Row headerRow;

        int headerSize = rt.getMainHeaders().size();

        int startRow = headerSize + 4;
        headerRow = sheet.createRow(++startRow);



        createHeaderRow(workbook, headerRow, rt.getTableHeaders());

        if (rt.getTableType() == 0) {
            ungroupedData(rt.getTableHeaders(), rt.getTableData(), workbook, sheet, rt.getTotalInd(), startRow);
        } else if (rt.getTableType() == 1) {
            groupedData(rt.getTableHeaders(), rt.getTableData(), workbook, sheet, rt.getGroupBy(), rt.getMainHeaders(), path, rt.getTotalInd(), startRow);
        } else if (rt.getTableType() == 2) {
            subGroupedData(rt.getTableHeaders(), rt.getTableData(), workbook, sheet, rt.getGroupBy(), rt.getSubGroupBy(), rt.getMainHeaders(), path, rt.getTotalInd());
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


    private void subGroupedData(List<Map<String, Object>> headers, List<Map<String, Object>> data, Workbook workbook,
                                Sheet sheet, String groupBy, String subGroupBy, List<String> mainHeaders, String imgPath, int totalInd) throws IOException {
        // TODO Auto-generated method stub
        int rowNo = 6;
        int subGroupRowNo = 8;
        int subRowNo = 0;
        Sheet subGroupSheet = null;
        Row groupHeaderRow;
        Cell headerCell;
        String groupByValue;
        String subGroupByValue;
        Cell subHeaderCell;
        Row subHeaderRow;
        for (Map<String, Object> row : data) {
            String dumpValue = "";
            rowNo = rowNo + 1;
              groupByValue = row.get(groupBy).toString();
              subGroupByValue = row.get(subGroupBy).toString();
            boolean testExistence = test(groupByValue);
            if (!testExistence) {
                subGroupRowNo = 9;

                subGroupValues.clear();
                groupHeaderRow = sheet.createRow(rowNo);
                headerCell = groupHeaderRow.createCell(0);
                headerStyle = workbook.createCellStyle();

                XSSFFont font = ((XSSFWorkbook) workbook).createFont();
                font.setFontName("Arial");
                font.setFontHeightInPoints((short) 12);
                font.setBold(true);
                headerStyle.setFont(font);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);

                headerCell.setCellValue(groupByValue);
                headerCell.setCellStyle(headerStyle);

                subGroupSheet = workbook.createSheet(groupByValue);
                subGroupSheet.setColumnWidth(0, 6000);
                subGroupSheet.setColumnWidth(1, 4000);
                drawPictureHeader(workbook, subGroupSheet, imgPath);
                addSheetTitle(workbook, subGroupSheet, groupByValue, mainHeaders);
                createNewGroupedSheetHeaders(subGroupSheet, workbook, headers);
            }
            boolean test_subgroup_existing = doSubGroupExist(subGroupByValue);
            if (!test_subgroup_existing) {
                rowNo = rowNo + 1;
                  subHeaderRow = sheet.createRow(rowNo);
                  subHeaderCell = subHeaderRow.createCell(0);

                subHeaderCell.setCellValue(subGroupBy + " - " + subGroupByValue);
                subHeaderCell.setCellStyle(headerStyle);
                int rowFilledCount = subGroupedRows(data, headers, workbook, sheet, groupBy, groupByValue, subGroupBy, subGroupByValue, rowNo, totalInd);
                rowNo = rowFilledCount + 2;
                subRowNo = createNewSubgroupedSheet(data, headers, workbook, subGroupSheet, groupBy, groupByValue, subGroupBy, subGroupByValue, subGroupRowNo, totalInd);
                subGroupRowNo = subRowNo + 2;
            }
        }

        if (totalInd == 1)
            sumUngroupedRowCells(data, headers, workbook, sheet, rowNo);
    }



    private void sumGroupedTotal(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook,
                                 Sheet subGroupSheet, String groupBy, String groupByValue, int rowNo) {
        // TODO Auto-generated method stub
        Row row = null;
        row = subGroupSheet.createRow(rowNo + 1);
        int cellNo = 0;
        Cell cell = null;

        Cell tCell = null;
        CellStyle totalStyle = workbook.createCellStyle();
        for (Map<String, Object> datarow : headers) {
            cellNo = cellNo + 1;
            Double total = 0.0;
            if (datarow.get("totalInd").toString().equals(1)) {
                for (Map<String, Object> str : data) {
                    if ((str.get(datarow.get("headerName").toString()) != null) && (str.get(groupBy).toString().equals(groupByValue))) {
                        String no = str.get(datarow.get("headerName").toString()).toString();
                        Double val = Double.valueOf(no);
                        total += val;
                    } else {

                    }
                }


                tCell = row.createCell(1);
                tCell.setCellValue("Grand Total");
                tCell.setCellStyle(totalStyle);

                cell = row.createCell(cellNo);
                totalStyle.setFont(arialFont10);
                totalStyle.setWrapText(true);
                cell.setCellStyle(totalStyle);
                String newTotal = PayrollHRUtils.getDecimalFormat().format(total);
                cell.setCellValue(IConstants.naira + newTotal);

            } else {
                // @Taiwo for what??
            }

        }

    }


    private int subGroupedRows(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook,
                               Sheet sheet, String groupBy, String groupByValue, String subGroupBy, String subGroupByValue, int rowNo, int totalInd) {
        // TODO Auto-generated method stub
        Row row = null;
        int cellNo = 0;
        int total = 0;
        Cell cell = null;
        CellStyle style = workbook.createCellStyle();
        for (Map<String, Object> datarow : data) {
            if (datarow.get(groupBy).toString().equals(groupByValue)) {
                rowNo = rowNo + 1;
                cellNo = 0;
                row = sheet.createRow(rowNo);
            }
            for (Map<String, Object> str : headers) {
                if ((datarow.get(str.get("headerName").toString()) != null) && (datarow.get(groupBy).toString().equals(groupByValue)) && (datarow.get(subGroupBy).toString().equals(subGroupByValue))) {

                    style.setWrapText(true);
                    cellNo = cellNo + 1;

                    assert row != null;
                    cell = row.createCell(cellNo);
                    cell.setCellValue(datarow.get(str.get("headerName").toString()).toString());
                    cellStyle(cell);
                }
            }
        }

        if (totalInd == 1) {
            sumBySubGroup(data, headers, workbook, sheet, groupBy, groupByValue, subGroupBy, subGroupByValue, rowNo);
        }
        return rowNo;
    }


    private void sumBySubGroup(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook,
                               Sheet sheet, String groupBy, String groupByValue, String subgroupBy, String subGroupByValue, int rowNo) {
        // TODO Auto-generated method stub
        Row row = null;
        int cellNo = 0;
        row = sheet.createRow(rowNo + 1);
        for (Map<String, Object> datarow : headers) {
            cellNo = cellNo + 1;
            sumSubColumn(data, datarow, row, subgroupBy, workbook, subGroupByValue, groupBy,
                    groupByValue, sheet, rowNo, cellNo);
        }
    }


    private void sumSubColumn(List<Map<String, Object>> data, Map<String, Object> datarow, Row row, String subgroupBy, Workbook workbook,
                              String subGroupByValue, String groupBy, String groupByValue, Sheet sheet, int rowNo, int cellNo) {
        // TODO Auto-generated method stub
        Double total = 0.0D;
        Double val = 0.0D;
        String cCompare;
        String dCompare;
        String no;
        if ((datarow.get("totalInd").toString().equals(1)) || (datarow.get("totalInd").toString().equals(2))) {
            for (Map<String, Object> str : data) {
                cCompare = str.get(groupBy).toString();
                dCompare = str.get(subgroupBy).toString();
                if ((str.get(datarow.get("headerName").toString()) != null) && (cCompare.equals(groupByValue))
                        && (dCompare.equals(subGroupByValue))) {
                    no = str.get(datarow.get("headerName").toString()).toString();
                    val = Double.valueOf(no);
                    total = total += val;
                } else {

                }
            }
            Cell tCell = row.createCell(1);
            tCell.setCellValue(subGroupByValue);
            cellStyle(tCell);

            Cell gCell = row.createCell(2);
            gCell.setCellValue("Total");
            cellStyle(gCell);

        }

        if (datarow.get("totalInd").toString().equals(1)) {
            Cell cell = row.createCell(cellNo);
            cell.setCellValue(total);
            cellStyle(cell);
        } else if (datarow.get("totalInd").toString().equals(2)) {
            Cell cell = row.createCell(cellNo);
            cell.setCellValue(IConstants.naira + total);
            cellStyle(cell);
        }

        if (datarow.get("totalInd").toString().equals("1")) {
            for (Map<String, Object> str : data) {
                if ((str.get(datarow.get("headerName").toString()) != null)) {
                    no = str.get(datarow.get("headerName")).toString();
                    val = Double.parseDouble(no);
                    total += val;

                }
            }


            Cell vCell = row.createCell(cellNo);
            vCell.setCellValue(total.toString());
            cellStyle(vCell);

        } else if (datarow.get("totalInd").toString().equals("2")) {
            for (Map<String, Object> str : data) {
                if ((str.get(datarow.get("headerName").toString()) != null)) {
                    no = str.get(datarow.get("headerName")).toString();
                    val = Double.parseDouble(PayrollHRUtils.removeCommas(no));
                    total += val;

                }
            }

            Cell vCell = row.createCell(cellNo);
            String newTotal = PayrollHRUtils.getDecimalFormat().format(total);
            vCell.setCellValue(IConstants.naira + total.toString());
            cellStyle(vCell);
        } else if (datarow.get("totalInd").toString().equals("3")) {

        } else {
            Cell vCell = row.createCell(cellNo);
            cellStyle(vCell);
        }
    }

    private int createNewSubgroupedSheet(List<Map<String, Object>> data, List<Map<String, Object>> headers,
                                         Workbook workbook, Sheet subGroupSheet, String groupBy, String groupByValue, String subGroupBy,
                                         String subGroupByValue, int newRow, int totalInd) {
        // TODO Auto-generated method stub

        newRow = newRow + 1;
        Row groupHeaderRow = subGroupSheet.createRow(newRow);
        Cell SubheaderCell = groupHeaderRow.createCell(0);
        CellStyle headerStyle = workbook.createCellStyle();

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        headerStyle.setFont(font);

        SubheaderCell.setCellValue(subGroupByValue);
        SubheaderCell.setCellStyle(headerStyle);
        int rowNo = createNewSubGroupedSheetRows(subGroupSheet, workbook, data, headers, subGroupBy,
                subGroupByValue, groupBy, groupByValue, newRow, totalInd);
        return rowNo;
    }

    private int createNewSubGroupedSheetRows(Sheet newSheet, Workbook workbook, List<Map<String, Object>> data,
                                             List<Map<String, Object>> headers, String subgroupBy, String subGroupByValue,
                                             String groupBy, String groupByValue, int rowNo, int totalInd) {
        // TODO Auto-generated method stub
        Row row = null;
        int cellNo = 0, i = 0;
        Cell sn;
        CellStyle style = workbook.createCellStyle();
        for (Map<String, Object> dataRow : data) {
            if (dataRow.get(subgroupBy).toString().equals(subGroupByValue)) {
                cellNo = 0;
                rowNo = rowNo + 1;
                row = newSheet.createRow(rowNo);
                sn = row.createCell(0);
                sn.setCellValue(i += 1);
                cellStyleNoBold(sn);
            }
            for (Map<String, Object> str : headers) {
                if ((dataRow.get(str.get("headerName").toString()) != null) && (dataRow.get(subgroupBy).toString().equals(subGroupByValue))
                        && (dataRow.get(groupBy).toString().equals(groupByValue))) {

                    style.setWrapText(true);
                    cellNo = cellNo + 1;

                    Cell cell = row.createCell(cellNo);
                    cell.setCellValue(dataRow.get(str.get("headerName").toString()).toString());
                    cellStyleNoBold(cell);

                } else {

                }
            }
        }

        if (totalInd == 1) {
            sumBySubGroup(data, headers, workbook, newSheet, groupBy, groupByValue, subgroupBy, subGroupByValue, rowNo);
        }
        return rowNo;
    }


    private void addSheetTitle(Workbook workbook, Sheet sheet, String reportTitle, List<String> mainHeaders) {
        // TODO Auto-generated method stub

        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();
        int rowNo = 2;
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String currentTime = dateFormat.format(new Date());
        String fullDate = currentMonth.toString() + " " + currentDay + ", " + currentYear;

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

        Cell headerCell2;
        Row groupHeaderRow2;
        for (String cn : mainHeaders) {
            rowNo = rowNo + 1;
            groupHeaderRow2 = sheet.createRow(rowNo);
            headerCell2 = groupHeaderRow2.createCell(0);
            headerCell2.setCellValue(cn);
            headerCell2.setCellStyle(headerStyle);
        }

        Row groupHeaderRow3 = sheet.createRow(rowNo + 1);
        Cell headerCell3 = groupHeaderRow3.createCell(0);
        headerCell3.setCellValue("Print Date: " + fullDate + " ," + currentTime + "");
        headerCell3.setCellStyle(headerStyle);

    }


    private void addNewSheetTitle(Workbook workbook, Sheet sheet, String reportTitle, List<String> mainHeaders, String groupBy) {
        // TODO Auto-generated method stub

        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();
        int rowNo = 2;
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String currentTime = dateFormat.format(new Date());
        String fullDate = currentMonth.toString() + " " + currentDay + ", " + currentYear;


        headerStyle.setFont(arialFont10);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        Row groupHeaderRow2;
        Cell headerCell2;

        for (String cn : mainHeaders) {
            rowNo = rowNo + 1;
            groupHeaderRow2 = sheet.createRow(rowNo);
            headerCell2 = groupHeaderRow2.createCell(0);
            headerCell2.setCellValue(cn);
            headerCell2.setCellStyle(headerStyle);
        }

        Row groupHeaderRow3 = sheet.createRow(rowNo + 1);
        Cell headerCell3 = groupHeaderRow3.createCell(0);
        headerCell3.setCellValue("Print Date: " + fullDate + " ," + currentTime + "");
        headerCell3.setCellStyle(headerStyle);

//        Row GroupHeaderRow5 = sheet.createRow(rowNo+2);
//        Cell headerCell5 = GroupHeaderRow5.createCell(0);
//        headerCell5.setCellValue("Print Time: "+currentTime+"");
//        headerCell5.setCellStyle(headerStyle);

        Row groupHeaderRow4 = sheet.createRow(rowNo + 2);
        Cell headerCell4 = groupHeaderRow4.createCell(0);
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



    private void createHeaderRow(Workbook workbook, Row headerRow, List<Map<String, Object>> headers) {
        // TODO Auto-generated method stub
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        headerStyle.setFont(arialFont9);

        Cell sn = headerRow.createCell(0);
        sn.setCellValue("S/No");
        sn.setCellStyle(headerStyle);
        String field;
        Cell headerCell;
        int cellNo = 0;
        for (Map<String, Object> str : headers) {
            field = str.get("headerName").toString();

            if (str.get("totalInd").toString().equals("3")) {

            } else {
                headerCell = headerRow.createCell(++cellNo);
                headerCell.setCellValue(field);
                headerCell.setCellStyle(headerStyle);
            }
        }
    }

    private void ungroupedData(List<Map<String, Object>> headers, List<Map<String, Object>> data, Workbook workbook,
                               Sheet sheet, int totalInd, int plusFive) {
        // TODO Auto-generated method stub
        int rowNo = plusFive + 1;
//        if (plusFive == 5) {
//            rowNo = 8;
//        } else if (plusFive == 7) {
//            rowNo = 10;
//        }
//        else if (plusFive == 9) {
//            rowNo = 13;
//        }
//        else {
//            rowNo = 15;
//        }
        Row row;
        int cellNo;
        int i = 0;
        for (Map<String, Object> dataRow : data) {

            row = sheet.createRow(rowNo++);
            cellNo = 0;
            Cell sn = row.createCell(0);
            cellStyleNoBold(sn);
            sn.setCellValue(i += 1);
            Cell cell;
            Double temp;
            String newTotal;
            Integer tempInt;
            for (Map<String, Object> str : headers) {
                if ((dataRow.get(str.get("headerName").toString()) != null)) {


                    cell = row.createCell(++cellNo);
                    if (str.get("totalInd").toString().equals("2")) {
                        temp = Double.valueOf(PayrollHRUtils.removeCommas(dataRow.get(str.get("headerName").toString()).toString()));
                        newTotal = PayrollHRUtils.getDecimalFormat().format(temp);
                        cell.setCellValue(IConstants.naira + newTotal);
                        cellStyleNoBold(cell);
                    } else if (str.get("totalInd").toString().equals("3")) {
                        cell.setCellValue("");
                    } else if (str.get("totalInd").toString().equals("1")) {
                        tempInt = Integer.valueOf(dataRow.get(str.get("headerName").toString()).toString());
                        cell.setCellValue(tempInt);
                        cellStyleNoBold(cell);
                    } else {
                        cell.setCellValue(dataRow.get(str.get("headerName").toString()).toString());
                        cellStyleNoBold(cell);
                    }
                } else {

                    cell = row.createCell(++cellNo);
                    cellStyleNoBold(cell);
                }
            }

        }

        if (totalInd == 1)
            sumUngroupedRowCells(data, headers, workbook, sheet, rowNo);
    }

    private void sumUngroupedRowCells(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook, Sheet sheet, int rowNo) {
        Row row;
        row = sheet.createRow(rowNo + 1);
        int cellNo = 0;
        Cell tCell = row.createCell(0);
        tCell.setCellValue("Grand Total");
        cellStyle(tCell);
        String no;
        Integer val;
        Integer total;
        Double valDbl;
        String newTotal;
        Double totalDbl;
        Cell vCell;
        for (Map<String, Object> datarow : headers) {
            cellNo = cellNo + 1;

            if (datarow.get("totalInd").toString().equals("1")) {
                 total = 0;
                for (Map<String, Object> str : data) {
                    if ((str.get(datarow.get("headerName").toString()) != null)) {
                        no = str.get(datarow.get("headerName")).toString();
                        val = Integer.valueOf(no);
                        total += val;

                    }
                }


                vCell = row.createCell(cellNo);
                vCell.setCellValue(total.toString());
                cellStyle(vCell);

            } else if (datarow.get("totalInd").toString().equals("2")) {
                totalDbl = 0.0;
                for (Map<String, Object> str : data) {
                    if ((str.get(datarow.get("headerName").toString()) != null)) {
                        no = str.get(datarow.get("headerName")).toString();
                        valDbl = Double.parseDouble(PayrollHRUtils.removeCommas(no));
                        totalDbl += valDbl;
                    }
                }

                vCell = row.createCell(cellNo);
                newTotal = PayrollHRUtils.getDecimalFormat().format(totalDbl);
                vCell.setCellValue(IConstants.naira + newTotal);
                cellStyle(vCell);
            } else if (datarow.get("totalInd").toString().equals("3")) {

            } else {
                vCell = row.createCell(cellNo);
                cellStyle(vCell);
            }

        }
    }


    private void groupedData(List<Map<String, Object>> headers, List<Map<String, Object>> data, Workbook workbook,
                             Sheet sheet, String groupBy, List<String> mainHeaders, String imgPath, int totalInd, int plusFive) throws IOException {
        int rowNo = plusFive + 1;
//        int rowNo;
//        if (plusFive == 5) {
//            rowNo = 10;
//        } else if (plusFive == 7) {
//            rowNo = 14;
//        } else {
//            rowNo = 7;
//        }
        String dumpValue = "";
        String text;
        Row groupHeaderRow;
        Cell headerCell;



        headerStyle.setFont(arialFont10);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        String tableValue;
        int rowFilledCount;

        for (Map<String, Object> row : data) {

            tableValue = row.get(groupBy).toString();
             if (!test(tableValue)) {
                groupHeaderRow = sheet.createRow(rowNo);
                headerCell = groupHeaderRow.createCell(0);


                headerCell.setCellValue(tableValue);
                headerCell.setCellStyle(headerStyle);

                 rowFilledCount = groupRows(data, headers, workbook, sheet, groupBy, tableValue, rowNo);
                rowNo = rowFilledCount + 2;
                createNewGroupedSheet(data, headers, workbook, groupBy, tableValue, mainHeaders, imgPath);
            } else {
                dumpValue = "no";
            }
        }
        if (totalInd == 1)
            sumUngroupedRowCells(data, headers, workbook, sheet, rowNo);
    }


    private void createNewGroupedSheet(List<Map<String, Object>> data, List<Map<String, Object>> headers,
                                       Workbook workbook, String groupBy, String tableValue, List<String> mainHeaders, String imgPath) throws IOException {
        // TODO Auto-generated method stub
        String str;
        str = tableValue.replaceAll("[^a-zA-Z0-9]", " ");
        Sheet newSheet = workbook.createSheet(str);
        newSheet.setColumnWidth(0, 6000);
        newSheet.setColumnWidth(1, 4000);
        drawPictureHeader(workbook, newSheet, imgPath);

        addNewSheetTitle(workbook, newSheet, tableValue, mainHeaders, tableValue);
        createNewGroupedSheetHeaders(newSheet, workbook, headers);
        createNewGroupedSheetRows(newSheet, workbook, data, headers, tableValue, groupBy);
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
    private void setHeaderStyle(Workbook workbook){
        if(headerStyle == null){
            headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

    }
    private void createNewGroupedSheetHeaders(Sheet newSheet, Workbook workbook, List<Map<String, Object>> headers) {
        // TODO Auto-generated method stub
        Row headerRow = newSheet.createRow(6);

        Cell sn = headerRow.createCell(0);
        sn.setCellValue("S/No");
        sn.setCellStyle(headerStyle);
        String field;
        Cell headerCell;
        int cellNo = 0;
        for (Map<String, Object> str : headers) {
            field = str.get("headerName").toString();
            cellNo = cellNo + 1;
            if ((str.get("totalInd").toString()).equals("3")) {

            } else {
                headerCell = headerRow.createCell(cellNo);
                headerCell.setCellValue(field);
                headerCell.setCellStyle(headerStyle);
            }
        }
    }


    private void createNewGroupedSheetRows(Sheet newSheet, Workbook workbook, List<Map<String, Object>> data,
                                           List<Map<String, Object>> headers, String tableValue, String groupBy) {
        // TODO Auto-generated method stub
        Row row = null;
        int rowNo = 6;
        int cellNo = 0, i = 0;
        Cell sn;
        Cell cell;
        CellStyle style = workbook.createCellStyle();
        Double temp;
        String newTotal;
        for (Map<String, Object> datarow : data) {
            if (datarow.get(groupBy).toString().equals(tableValue)) {

                rowNo = rowNo + 1;
                row = newSheet.createRow(rowNo);
                sn = row.createCell(0);
                sn.setCellValue(i += 1);
            } else {
                row = newSheet.createRow(0);
            }
            for (Map<String, Object> str : headers) {
                if ((datarow.get(str.get("headerName").toString()) != null) && (datarow.get(groupBy).toString().equals(tableValue))) {

                    style.setWrapText(true);
                    cellNo = cellNo + 1;

                    cell = row.createCell(cellNo);
                    if (str.get("totalInd").toString().equals("2")) {
                        temp = Double.valueOf(PayrollHRUtils.removeCommas(datarow.get(str.get("headerName").toString()).toString()));
                        newTotal = PayrollHRUtils.getDecimalFormat().format(temp);
                        cell.setCellValue(IConstants.naira + newTotal);
                        cellStyleNoBold(cell);
                    } else if (str.get("totalInd").toString().equals("3")) {

                    } else {
                        cell.setCellValue(datarow.get(str.get("headerName").toString()).toString());
                        cellStyleNoBold(cell);
                    }
                }
            }
            cellNo = 0;
        }
        sumByGroup(data, headers, workbook, newSheet, groupBy, tableValue, rowNo);
    }


    private int groupRows(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook,
                          Sheet sheet, String groupBy, String tableValue, int rowNo) {
        // TODO Auto-generated method stub
        Row row;
        int cellNo = 0, i = 0;
        Cell sn;
        Cell cell;
        CellStyle style = workbook.createCellStyle();
        Double temp;
        int headerSize = headers.size();
        String newTotal;
        for (Map<String, Object> dataRow : data) {
            if (dataRow.get(groupBy).toString().equals(tableValue)) {
                rowNo = rowNo + 1;

                row = sheet.createRow(rowNo);
                sn = row.createCell(0);
                cellStyleNoBold(sn);
                sn.setCellValue(i += 1);
            } else {
                row = sheet.createRow(5);
            }
            for (Map<String, Object> str : headers) {
                if ((dataRow.get(str.get("headerName").toString()) != null) && (dataRow.get(groupBy).toString().equals(tableValue))) {

                    style.setWrapText(true);
                    //cellNo = cellNo + 1;

                    cell = row.createCell(++cellNo);
                    if (str.get("totalInd").toString().equals("2")) {
                        temp = Double.valueOf(PayrollHRUtils.removeCommas(dataRow.get(str.get("headerName").toString()).toString()));
                        newTotal = PayrollHRUtils.getDecimalFormat().format(temp);
                        cell.setCellValue(IConstants.naira + newTotal);
                        cellStyleNoBold(cell);
                    } else if (str.get("totalInd").toString().equals("3")) {

                    } else {
                        cell.setCellValue(dataRow.get(str.get("headerName").toString()).toString());
                        cellStyleNoBold(cell);
                    }
                } else {
                   // cellNo = cellNo + 1;
                    cell = row.createCell(++cellNo);
                    cellStyleNoBold(cell);
                }
            }
            cellNo = 0;
        }
        sumByGroup(data, headers, workbook, sheet, groupBy, tableValue, rowNo);
        return rowNo;
    }


    private void sumByGroup(List<Map<String, Object>> data, List<Map<String, Object>> headers, Workbook workbook,
                            Sheet sheet, String groupBy, String tableValue, int rowNo) {
        // TODO Auto-generated method stub
        Row row;
        int cellNo = 0;
        row = sheet.createRow(rowNo + 1);
        for (Map<String, Object> dataRow : headers) {
            sumColumn(data, dataRow, row, groupBy, workbook, tableValue, ++cellNo);

        }

    }


    private void sumColumn(List<Map<String, Object>> data, Map<String, Object> dataRow, Row row, String groupBy, Workbook workbook,
                           String tableValue, int cellNo) {
        // TODO Auto-generated method stub
        Double total = 0.0;
        String cCompare;
        String no;
        if (dataRow.get("totalInd").toString().equals("1")) {
            Integer val = 0;
            for (Map<String, Object> str : data) {
                cCompare = str.get(groupBy).toString();
                if ((str.get(dataRow.get("headerName").toString()) != null) && (cCompare.equals(tableValue))) {
                    no = str.get(dataRow.get("headerName").toString()).toString();
                    val = Integer.valueOf(no);
                    total = total + val;
                } else {

                }
            }


            Cell gCell = row.createCell(0);
            gCell.setCellValue("Total");
            cellStyle(gCell);

            Cell cell = row.createCell(cellNo);
            cell.setCellValue(total);
            cellStyle(cell);
        } else if (dataRow.get("totalInd").toString().equals("2")) {
            Double val = 0.0;
            Cell cell;
            for (Map<String, Object> str : data) {
                cCompare = str.get(groupBy).toString();
                if ((str.get(dataRow.get("headerName").toString()) != null) && (cCompare.equals(tableValue))) {
                    no = str.get(dataRow.get("headerName").toString()).toString();
                    val = Double.parseDouble(PayrollHRUtils.removeCommas(no));
                    total = total + val;
                } else {
                    cell = row.createCell(cellNo);
                    cellStyle(cell);
                }
            }

            Cell gCell = row.createCell(0);
            gCell.setCellValue("Total");
            cellStyle(gCell);

            cell = row.createCell(cellNo);
            String newTotal = PayrollHRUtils.getDecimalFormat().format(total);
            cell.setCellValue(IConstants.naira + newTotal);
            cellStyle(cell);
        } else if (dataRow.get("totalInd").toString().equals("3")) {

        }


    }


    private boolean test(String tableValue) {
        // TODO Auto-generated method stub
        if (tableValues.contains(tableValue)) {
            return true;
        } else {
            tableValues.add(tableValue);
            return false;
        }
    }

    private boolean doSubGroupExist(String subGroupByValue) {
        // TODO Auto-generated method stub
        if (subGroupValues.contains(subGroupByValue)) {
            return true;
        } else {
            subGroupValues.add(subGroupByValue);
            return false;
        }
    }

    private void cellStyle(Cell cell) {

        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalStyle.setFont(arialFont10);
        totalStyle.setBorderBottom(BorderStyle.THIN);

        totalStyle.setBorderBottom(BorderStyle.THIN);
        totalStyle.setBorderTop(BorderStyle.THIN);
        totalStyle.setBorderRight(BorderStyle.THIN);
        totalStyle.setBorderLeft(BorderStyle.THIN);

        cell.setCellStyle(totalStyle);
    }

    private void cellStyleNoBold(Cell cell) {

        normalStyle.setAlignment(HorizontalAlignment.RIGHT);
        normalStyle.setFont(arialNormalFont10);
        normalStyle.setBorderBottom(BorderStyle.THIN);

        normalStyle.setBorderBottom(BorderStyle.THIN);
        normalStyle.setBorderTop(BorderStyle.THIN);
        normalStyle.setBorderRight(BorderStyle.THIN);
        normalStyle.setBorderLeft(BorderStyle.THIN);

        cell.setCellStyle(normalStyle);
    }


}
