package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class PdfSimpleGeneratorClass extends PdfPageEventHelper {


    /**
     * Kasumu Taiwo
     * 12-2020
     * Refactored 02/2022, 03/2022
     * Mustola.
     */
     private List<String> tableValues = new ArrayList<>();
    private PdfPCell tableCell;
     public void getPdf(HttpServletResponse response, HttpServletRequest request, ReportGeneratorBean rt) {


        LocalDate currDate = LocalDate.now();
        int currentDay = currDate.getDayOfMonth();
        Month currentMonth = currDate.getMonth();


        String currentTime = DateTimeFormatter.ofPattern("hh.mm a").format(LocalTime.now());
        int year = currDate.getYear();

        Document myPdfReport;

            if (rt.getTableHeaders().size() > 6) {
                myPdfReport = new Document(PageSize.A2.rotate());
            } else {
                myPdfReport = new Document(PageSize.A2);
            }

        try {
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            String fileLocation = path.substring(0, path.length() - 1) + rt.getReportTitle() + ".pdf";

            File currDir = new File(request.getServletContext().getRealPath(File.separator)+rt.getReportTitle()+".pdf");
            String fileLocation = currDir.getPath();
            //String fileLocation = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD);
            Font smallBold = new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 15f, Font.NORMAL);


            PdfWriter pdfwriter = PdfWriter.getInstance(myPdfReport, new FileOutputStream(fileLocation));
            myPdfReport.open();
            PdfTemplate template = pdfwriter.getDirectContent().createTemplate(30, 12);
            pdfwriter.setPageEvent(new PdfHeaderPageEvent(rt, boldFont, smallBold, normal, rt.getTableType(), template));
            PdfPTable head = new PdfPTable(1);

//            File imgDir = ResourceUtils.getFile("classpath:static/images/" + rt.getBusinessCertificate().getClientReportLogo());
//            String imgPath = imgDir.getAbsolutePath();
//            Image img2 = Image.getInstance(imgPath);
//            img2.setAlignment(Element.ALIGN_CENTER);
//            img2.setWidthPercentage(120);
//            PdfPCell cell = new PdfPCell();
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.addElement(img2);
//            PdfPTable head = new PdfPTable(1);
//            head.addCell(cell);
//            myPdfReport.add(head);

            myPdfReport = PdfUtils.makeHeaderFile(rt.getBusinessCertificate(), new PdfPCell(), myPdfReport, head);


            //adding pdf Main Report Title

            //adding main headers
            addMainHeaders(rt.getMainHeaders(), myPdfReport, headerFont);
            addMainHeadersRight(rt.getMainHeaders2(), myPdfReport, smallBold);

            Paragraph time = new Paragraph("Print Date: " + currentDay + " " + currentMonth + ", " + year, smallBold);
            time.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time);

            Paragraph time2 = new Paragraph("Print Time: "+ currentTime, smallBold);
            time2.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time2);
            myPdfReport.add(new Paragraph("\n"));

            //adding table headers


            //check if data should be grouped
            if (rt.getTableType() == 0) {
                PdfPTable myReportTable = new PdfPTable(rt.getTableHeaders().size());
                addTableHeaders(rt.getTableHeaders(), myPdfReport, boldFont, myReportTable);
                ungroupedData(rt.getTableData(), rt.getTableHeaders(), myPdfReport, normal, boldFont, rt.getTotalInd(), myReportTable, rt.isCustomReport());
            } else if (rt.getTableType() == 1) {
                //grouping table data before adding cells
                PdfPTable myReportTable = new PdfPTable(rt.getTableHeaders().size() - 1);
               // PdfPTable myReportTable = new PdfPTable(rt.getTableHeaders().size());
                addTableHeaders(rt.getTableHeaders(), myPdfReport, boldFont, myReportTable);
                groupData(rt.getTableData(), rt.getTableHeaders(), myReportTable, myPdfReport, normal, boldFont, rt.getGroupBy(), rt.getTotalInd(), rt.isCustomReport());
            } else if (rt.getTableType() == 2) {
//                subGroupData(rt.getTableData(), rt.getTableHeaders(), rt.getReportTitle(), rt.getGroupBy(), rt.getSubGroupBy());
            }


            myPdfReport.close();

            FileInputStream baos = new FileInputStream(fileLocation);


            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + rt.getReportTitle() + ".pdf");

            if (IppmsUtils.isNull(rt.isOutputInd()) || rt.isOutputInd()) {
                OutputStream os = response.getOutputStream();
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = baos.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }


                try {
                    os.close();
                } catch (Exception wEx) {
                    wEx.printStackTrace();
                }
            }


        } catch (IOException | DocumentException ex) {
            ex.printStackTrace();
            System.out.println("An exception was caught: " + ex);
        }
//        os.close();
    }


    private void addTableHeaders(List<Map<String, Object>> tableHeaders, Document myPdfReport, Font bold, PdfPTable myReportTable) throws DocumentException {

        myReportTable.setWidthPercentage(100);
        //create a cell object
        //add headers
        int i = 1;
        int totalList = tableHeaders.size();
        for (Map<String, Object> str : tableHeaders) {
            if (str.get("totalInd").toString().equals("3")) {
                continue;
            } else if (str.get("totalInd").toString().equals("1")) {
                String field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                if (i == 1) {
                    //tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                } else if (i == (totalList)) {
                    tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                } else {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                myReportTable.addCell(tableCell);
            } else if (str.get("totalInd").toString().equals("2")) {
                String field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                //tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                if (i == 1) {
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                } else if (i == (totalList)) {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                } else {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                myReportTable.addCell(tableCell);
            } else {
                String field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                if (i == 1) {
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                } else if (i == (totalList)) {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.LEFT);
                } else {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                myReportTable.addCell(tableCell);
            }

            i++;
        }
        myPdfReport.add(myReportTable);
    }


    private void addMainHeaders(List<String> mainHeaders, Document myPdfReport, Font font) throws DocumentException {
        Paragraph value;
        for (String header : mainHeaders) {
            value = new Paragraph(header.toUpperCase(), font);
            value.setAlignment(Element.ALIGN_CENTER);
            myPdfReport.add(value);
            myPdfReport.add(new Paragraph("\n"));
        }
    }

    private void addMainHeadersRight(List<String> mainHeaders, Document myPdfReport, Font font) throws DocumentException {
        if(IppmsUtils.isNotNullOrEmpty(mainHeaders)){
            Paragraph value;
            for (String header : mainHeaders) {
                value = new Paragraph(header, font);
                value.setAlignment(Element.ALIGN_RIGHT);
                myPdfReport.add(value);
            }
        }
    }

    private void groupData(List<Map<String, Object>> data, List<Map<String, Object>> headers, PdfPTable newTable,
                           Document myPdfReport, Font normal, Font boldFont, String groupBy, int totalInd, boolean customReport) throws DocumentException {
        // TODO Auto-generated method stub
        int i = 0;
        String tableValue;
        Paragraph groupHeader;
        for (Map<String, Object> row : data) {
             if(row.containsKey(groupBy))
                tableValue = row.get(groupBy).toString();
             else{

                 continue;
             }

           // boolean testExistence = test(tableValue);
            if (!test(tableValue)) {
                if (i != 0) {
                    myPdfReport.newPage();

                }
                groupHeader = new Paragraph(groupBy + ": " + tableValue, boldFont);
                groupHeader.setAlignment(Element.ALIGN_LEFT);
                myPdfReport.add(groupHeader);

                newTable = prepareMyReportTable(headers.size() - 1);
                newTable.setWidthPercentage(100);
                groupRows(data, headers, groupBy, newTable, tableValue, myPdfReport, normal, customReport);
                i++;
            }
        }

        if (totalInd == 1)
            myPdfReport.add(new Paragraph("\n"));
        sumRowCells(data, headers, newTable, myPdfReport, boldFont, customReport);
    }

    private void ungroupedData(List<Map<String, Object>> data, List<Map<String, Object>> headers, Document myPdfReport,
                               Font normal, Font bold, int totalInd, PdfPTable myReportTable, boolean customReport) throws DocumentException {
        // TODO Auto-generated method stub
        myReportTable = prepareMyReportTable(headers.size());
        myReportTable.setWidthPercentage(100);

        //String num_val;
        PdfPCell tableCell;
        PdfPCell emptyCell = new PdfPCell();
        Object value;
       // Double val;

        for (Map<String, Object> row : data) {

            for (Map<String, Object> str : headers) {
                if (row.get(str.get("headerName").toString()) != null) {
                    if (str.get("totalInd").toString().equals("1")) {

                        tableCell = new PdfPCell(new Phrase(row.get(str.get("headerName").toString()).toString(), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        emptyCell = new PdfPCell(new Phrase(" ", normal));

                    } else if (str.get("totalInd").toString().equals("2")) {

                        tableCell = new PdfPCell(new Paragraph(PayrollHRUtils.getDecimalFormat().format(Double.valueOf(row.get(str.get("headerName").toString()).toString())), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        emptyCell = new PdfPCell(new Phrase(" ", normal));


                    } else {
                        value = row.get(str.get("headerName"));

                        tableCell = new PdfPCell(new Phrase((String) value, normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        emptyCell = new PdfPCell(new Phrase(" ", normal));
                       // tableCell.setBorder(Rectangle.NO_BORDER);
                    }

                } else {
                    tableCell = new PdfPCell(new Phrase("", normal));
                    tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    emptyCell = new PdfPCell(new Phrase(" ", normal));
                }
                tableCell.setBorder(Rectangle.NO_BORDER);
                myReportTable.addCell(tableCell);
            }
            emptyCell.setColspan(headers.size());
            emptyCell.setBorder(Rectangle.NO_BORDER);
            myReportTable.addCell(emptyCell);
        }
        myPdfReport.add(myReportTable);

        if (totalInd == 1) {
            sumRowCells(data, headers, myReportTable, myPdfReport, bold, customReport);
        }
    }

    private PdfPTable prepareMyReportTable(int size) {
        float[] columnWidths;
         switch (size) {
            case 3:
                 columnWidths = new float[]{5, 5, 5};
                break;
             case 4:
                  columnWidths = new float[]{5, 5, 5, 5};
                break;
             case 5:
                  columnWidths = new float[]{5, 5, 5, 5, 5};
                 break;
             case 6:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5};
                 break;
             case 7:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5};
                 break;
             case 8:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5,5};
                 break;
             case 9:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5,5,5};
                 break;
             case 10:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5,5,5,5};
                 break;
             case 11:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5,5,5,5,5};
                 break;
             case 12:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5,5,5,5,5,5};
                 break;
             case 13:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5,5,5,5,5,5,5};
                 break;
             case 14:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5,5,5,5,5,5,5,5};
                 break;
             case 15:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5,5,5,5,5,5,5,5,5};
                 break;
             case 16:
                 columnWidths = new float[]{5, 5, 5, 5, 5,5,5,5,5,5,5,5,5,5,5,5};
                 break;
             default:
                 return new PdfPTable(size);

        }

         return new PdfPTable(columnWidths);
    }

    //adding rows to table by group
    private void groupRows(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, PdfPTable newTable, String tableValue, Document myPdfReport,
                           Font normal, boolean customReport) throws DocumentException {

        String numVal;
        PdfPCell tableCell;
        for (Map<String, Object> row : data) {

            for (Map<String, Object> str : headers) {
                if ((row.get(str.get("headerName").toString()) != null) && (row.get(groupBy).toString().equals(tableValue))) {
                    if (str.get("totalInd").toString().equals("1")) {
                        numVal = row.get(str.get("headerName").toString()).toString();

                        tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(Integer.valueOf(numVal)), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        newTable.addCell(tableCell);
                    } else if (str.get("totalInd").toString().equals("2")) {
                        numVal = row.get(str.get("headerName").toString()).toString();
                        tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(Double.valueOf(numVal)), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        newTable.addCell(tableCell);
                    }else if(str.get("totalInd").toString().equals("3")){

                    }else {
                         tableCell = new PdfPCell(new Phrase(row.get(str.get("headerName").toString()).toString(), normal));
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        newTable.addCell(tableCell);
                    }

                } else {
                    continue;
                }



            }
        }
        myPdfReport.add(newTable);
        sumByGroup(data, headers, groupBy, myPdfReport, tableValue, normal, customReport);
    }


    private void sumByGroup(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, Document myPdfReport, String tableValue, Font normal, boolean customReport) throws DocumentException {
        PdfPTable newTable1;
//        if(customReport)
//          newTable1 = new PdfPTable(headers.size());
//        else
            newTable1 = new PdfPTable(headers.size() - 1);
        newTable1.setWidthPercentage(100);


        int count = 1;
        for (Map<String, Object> row : headers) {
            if (row.get("totalInd").toString().equals("3")) {
                continue;
            } else {
                sumColumn(data, row, groupBy, newTable1, tableValue, myPdfReport, normal, count, headers);
            }
            count++;
        }
        myPdfReport.add(newTable1);

    }

    private void sumColumn(List<Map<String, Object>> data, Map<String, Object> header, String groupBy, PdfPTable newTable,
                           String tableValue1, Document myPdfReport, Font normal, int count, List<Map<String, Object>> headers) throws DocumentException {
        // TODO Auto-generated method stub

        int totalCount = headers.size();

       // Integer val;

        if (header.get("totalInd").toString().equals("1")) {
            Integer total2 = 0;
            String cCompare;
            for (Map<String, Object> str : data) {
                 cCompare = str.get(groupBy).toString();
                if (str.get(header.get("headerName")) != null && cCompare.equalsIgnoreCase(tableValue1)) {

                     total2 += Integer.valueOf(str.get(header.get("headerName").toString()).toString());
                }
            }

            tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(total2), normal));
            tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
            tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            if (count == 1) {
                tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
            } else if ((count != 1) && (count == (totalCount - 1))) {
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
            } else {
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            }
            newTable.addCell(tableCell);
        } else if (header.get("totalInd").toString().equals("2")) {
            Double total = 0.0;
            for (Map<String, Object> str : data) {

                if (str.get(header.get("headerName")) != null) {
                        if(str.get(groupBy).toString().equals(tableValue1))
                           total += Double.valueOf(str.get(header.get("headerName").toString()).toString());
                }
            }
            tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(total), normal));
            tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
            tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            if (count == 1) {
                tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
            } else if ((count != 1) && (count == (totalCount - 1))) {
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
            } else {
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            }
            newTable.addCell(tableCell);
        } else {
            int noOfItems = 0;
            for (Map<String, Object> str : data) {

                if (str.get(header.get("headerName")) != null && (str.get(groupBy).toString().equals(tableValue1)))
                {

                    noOfItems += 1;
                }
            }
            if (count == 1) {
                tableCell = new PdfPCell(new Phrase(tableValue1, normal));
                tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
            } else if (count == 2) {
                if (noOfItems < 2) {
                    tableCell = new PdfPCell(new Phrase(noOfItems + " item", normal));
                } else {
                    tableCell = new PdfPCell(new Phrase(noOfItems + " items", normal));
                }
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            } else if ((count == (totalCount - 1))) {
                tableCell = new PdfPCell();
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
            } else {
                tableCell = new PdfPCell();
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            }
            tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
            newTable.addCell(tableCell);
        }
    }

    //checking if data row has already been grouped
    private boolean test(String tableValue) {
        if (tableValues.contains(tableValue)) {
            return true;
        } else {
            tableValues.add(tableValue);
            return false;
        }
    }


    private void sumRowCells(List<Map<String, Object>> data, List<Map<String, Object>> headers, PdfPTable newTable, Document myPdfReport, Font boldFont, boolean customReport) throws DocumentException {
        PdfPTable newTable1;
//        if(customReport)
//            newTable1 = new PdfPTable(headers.size());
//        else
            newTable1 = new PdfPTable(headers.size());
        newTable1.setWidthPercentage(100);
        int count = 1;
        int totalCount = headers.size();
        Integer total2;
        Double total;
        String no;
        String amount;
        for (Map<String, Object> row : headers) {
            total2 = 0;
            total = 0.0;

            if (row.get("totalInd").toString().equals("1")) {

                for (Map<String, Object> str : data) {
                    if (str.get(row.get("headerName").toString()) != null) {

                        total2 += Integer.valueOf(str.get(row.get("headerName").toString()).toString());
                    }
                }

                tableCell = new PdfPCell(new Phrase(String.valueOf(total2), boldFont));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                if (count == 1) {
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                } else if ((count != 1) && (count == totalCount)) {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                } else {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                newTable1.addCell(tableCell);
            } else if (row.get("totalInd").toString().equals("2")) {
                 for (Map<String, Object> str : data) {
                    if ((str.get(row.get("headerName").toString()) != null)) {
                        no = str.get(row.get("headerName")).toString();
                        total += Double.parseDouble(PayrollHRUtils.removeCommas(no));
                    }
                }
                 amount = PayrollHRUtils.getDecimalFormat().format(total);
                tableCell = new PdfPCell(new Phrase(amount, boldFont));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                if (count == 1) {
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                } else if ((count != 1) && (count == totalCount)) {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                } else {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                newTable1.addCell(tableCell);
            } else {
                if (count == 1) {
                    tableCell = new PdfPCell(new Phrase("Grand Total", boldFont));
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                } else if (count == 2) {
                    if (data.size() < 2) {
                        tableCell = new PdfPCell(new Phrase(data.size() + " item", boldFont));
                    } else {
                        tableCell = new PdfPCell(new Phrase(data.size() + " items", boldFont));
                    }
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                } else if ((count == totalCount)) {
                    tableCell = new PdfPCell();
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                } else {
                    tableCell = new PdfPCell();
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                newTable1.addCell(tableCell);
            }
            count++;
        }
        myPdfReport.add(newTable1);

    }
}
