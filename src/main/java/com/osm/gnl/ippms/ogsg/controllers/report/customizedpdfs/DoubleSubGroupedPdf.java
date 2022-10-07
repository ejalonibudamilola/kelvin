package com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfHeaderPageEvent;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfUtils;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
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
import java.util.Set;
import java.util.stream.Collectors;

public class DoubleSubGroupedPdf {

    List<String> tableValues = new ArrayList<>();
    private PdfPCell tableCell;
    private String fiveSpaces = "     ";

    public void getPdf(HttpServletResponse response, HttpServletRequest request, ReportGeneratorBean rt) {


        LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
        Month currentMonth = currentdate.getMonth();


        String currentTime = DateTimeFormatter.ofPattern("hh.mm a").format(LocalTime.now());
        int year = currentdate.getYear();

        Document myPdfReport;
        if(rt.getTableHeaders().size() > 5){
            myPdfReport = new Document(PageSize.A2);
        }
        else {
            myPdfReport = new Document(PageSize.LETTER);
        }
        try {
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            String fileLocation = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";

            File currDir = new File(request.getServletContext().getRealPath(File.separator)+rt.getReportTitle()+".pdf");
            String fileLocation = currDir.getPath();
            //String  = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD);
            Font smallBold = new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 15f, Font.NORMAL);


            PdfWriter pdfwriter = PdfWriter.getInstance(myPdfReport, new FileOutputStream(fileLocation));
            myPdfReport.open();
            PdfTemplate template = pdfwriter.getDirectContent().createTemplate(30, 12);
            pdfwriter.setPageEvent(new PdfHeaderPageEvent(rt, boldFont, smallBold, normal, rt.getTableType(), template));


            PdfPTable head = new PdfPTable(1);
            PdfPCell t_cell;

            //adding pdf Header


//            File imgDir = ResourceUtils.getFile("classpath:static/images/"+rt.getBusinessCertificate().getClientReportLogo());
//            String imgPath = imgDir.getAbsolutePath();
//            Image img2 = Image.getInstance(imgPath);
//            img2.setAlignment(Element.ALIGN_CENTER);
//            img2.setWidthPercentage(120);
//            t_cell = new PdfPCell();
//            t_cell.setBorder(Rectangle.NO_BORDER);
//            t_cell.addElement(img2);
//            head.addCell(t_cell);
//            myPdfReport.add(head);
            myPdfReport = PdfUtils.makeHeaderFile(rt.getBusinessCertificate(), new PdfPCell(), myPdfReport, head);
            //addding pdf Main Report Title

            //adding main headers
            addMainHeaders(rt.getMainHeaders(), myPdfReport, headerFont);

            Paragraph time = new Paragraph(currentDay + " " + currentMonth + ", " + year, smallBold);
            time.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time);

            Paragraph time2 = new Paragraph(currentTime, smallBold);
            time2.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time2);
            myPdfReport.add(new Paragraph("\n"));

            //adding table headers




            PdfPTable newTable = null;
            Boolean addOne;
            //check if data should be grouped
            if (rt.getTableType() == 3){
                //grouping table data before adding cells
                PdfPTable myReportTable = new PdfPTable(rt.getTableHeaders().size()-3);
                addTableHeaders(rt.getTableHeaders(), myPdfReport, boldFont, myReportTable);
                groupData(rt.getTableData(), rt.getTableHeaders(), newTable, myPdfReport, normal, boldFont, rt.getGroupBy(), rt.getTotalInd(),
                        rt.getGroupedKeySet(), rt.getSubGroupedKeySet(), rt.getSubGroupBy(), rt.getDoubleSubGroupedKeySet(), rt.getDoubleSubGroupBy());
            }


            myPdfReport.close();

            FileInputStream baos = new FileInputStream(fileLocation);

//            ThrashOldPdfs.checkToDeleteOldPdfs();

            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename="+rt.getReportTitle()+".pdf");

            if(IppmsUtils.isNull(rt.isOutputInd()) || rt.isOutputInd()) {
                OutputStream os = response.getOutputStream();

                os = response.getOutputStream();
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = baos.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }


                try {
                    os.close();
                } catch (Exception wEx) {

                }
            }



        } catch (IOException | DocumentException ex) {
            ex.printStackTrace();
            System.out.println("An exception was caught: "+ex);
        }
//        os.close();
    }

    private void addTableHeaders(List<Map<String, Object>> tableHeaders, Document myPdfReport, Font bold, PdfPTable myReportTable) throws DocumentException {

        PdfPTable outerTable = new PdfPTable(1);



        myReportTable.setWidthPercentage(100);
        //create a cell object
        //add headers
        int i = 1;
        int totalList = tableHeaders.size();
        for (Map<String, Object> str : tableHeaders) {
            if(str.get("totalInd").toString().equals("3")){
                continue;
            }
            else if(str.get("totalInd").toString().equals("1")){
                String field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(i == 1){
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if(i == (totalList - 3)){
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                }
                else{
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                myReportTable.addCell(tableCell);
            }
            else if(str.get("totalInd").toString().equals("2")){
                String field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(i == 1){
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if(i == (totalList - 3)){
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                }
                else{
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                myReportTable.addCell(tableCell);
            }
            else {
                String field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                if(i == 1){
                    tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if(i == (totalList - 3)){
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                }
                else{
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                myReportTable.addCell(tableCell);
            }

            i++;
        }
        myPdfReport.add(myReportTable);
    }

    private void addMainHeaders(List<String> mainHeaders, Document myPdfReport, Font font) throws DocumentException {

        for (String header : mainHeaders){
            Paragraph value = new Paragraph(header.toUpperCase(), font);
            value.setAlignment(Element.ALIGN_CENTER);
            myPdfReport.add(value);
            myPdfReport.add(new Paragraph("\n"));
        }
    }


    private void groupData(List<Map<String, Object>> data, List<Map<String, Object>> headers, PdfPTable newTable,
                           Document myPdfReport, Font normal, Font boldFont, String groupBy, int totalInd, Set<String> groupedKeySet,
                           Set<String> subGroupedKeySet, String subGroupBy, Set<String> doubleSubGroupedKeySet, String doubleSubGroupBy) throws DocumentException {
        // TODO Auto-generated method stub
        int i = 0;
        List<Map<String, Object>> mapList;
        for (String key : groupedKeySet) {

            if(i != 0) {
                myPdfReport.newPage();
            }

//                addTableHeaders(headers, myPdfReport, boldFont, newTable);

            mapList = data.stream()
                    .filter(stringObjectMap -> stringObjectMap.get(groupBy).toString().equals(key))
                    .collect(Collectors.toList());
            if(mapList.size() > 0) {
                Paragraph group_header = new Paragraph(groupBy + ":  " + key, boldFont);
                group_header.setAlignment(Element.ALIGN_LEFT);
                myPdfReport.add(group_header);

                Paragraph spaces = new Paragraph(fiveSpaces);
                myPdfReport.add(spaces);


                newTable = new PdfPTable(headers.size() - 3);
                newTable.setWidthPercentage(100);

                subGroupData(mapList, headers, subGroupBy, newTable, myPdfReport, normal, subGroupedKeySet, boldFont, key, doubleSubGroupBy, doubleSubGroupedKeySet);
                myPdfReport.add(new Paragraph("\n"));
                sumByGroup(mapList, headers, groupBy, myPdfReport, normal, key);
                i++;
            }
        }

        if(totalInd == 1)
            myPdfReport.add(new Paragraph("\n"));
        sumRowCells(data, headers, newTable, myPdfReport, boldFont);
    }

    private void subGroupData(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy,
                              PdfPTable newTable, Document myPdfReport, Font normal, Set<String> subGroupedKeySet,
                              Font boldFont, String tableKey, String doubleSubGroupBy, Set<String> doubleSubGroupedKeySet) throws DocumentException {

        List<Map<String, Object>> mapList;
        for (String key : subGroupedKeySet) {


            mapList = data.stream()
                    .filter(stringObjectMap -> stringObjectMap.get(groupBy).toString().equals(key))
                    .collect(Collectors.toList());

            if(mapList.size() > 0) {
                Paragraph group_header = new Paragraph(groupBy + ":  " + key, boldFont);
                group_header.setAlignment(Element.ALIGN_LEFT);
                myPdfReport.add(group_header);

                Paragraph spaces = new Paragraph(fiveSpaces);
                myPdfReport.add(spaces);


                newTable = new PdfPTable(headers.size() - 3);
                newTable.setWidthPercentage(100);


                doubleSubGroupData(mapList, headers, newTable, myPdfReport, normal, subGroupedKeySet, boldFont, key, doubleSubGroupBy, doubleSubGroupedKeySet);
            }
        }


    }

    private void doubleSubGroupData(List<Map<String, Object>> data, List<Map<String, Object>> headers,
                              PdfPTable newTable, Document myPdfReport, Font normal, Set<String> subGroupedKeySet,
                              Font boldFont, String tableKey, String groupBy, Set<String> doubleSubGroupedKeySet) throws DocumentException {

        List<Map<String, Object>> mapList;
        for (String key : doubleSubGroupedKeySet) {
            mapList = data.stream()
                    .filter(stringObjectMap -> stringObjectMap.get(groupBy).toString().equals(key))
                    .collect(Collectors.toList());

            if (mapList.size() > 0) {
                Paragraph group_header = new Paragraph(groupBy+":  " + key, boldFont);
                group_header.setAlignment(Element.ALIGN_LEFT);
                myPdfReport.add(group_header);

                Paragraph spaces = new Paragraph(fiveSpaces);
                myPdfReport.add(spaces);

                newTable = new PdfPTable(headers.size() - 3);
                newTable.setWidthPercentage(100);


                groupRows(mapList, headers, groupBy, newTable, myPdfReport, normal, tableKey);
            }

        }


    }


    //adding rows to table by group
    private void groupRows(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, PdfPTable newTable, Document myPdfReport, Font normal, String tableKey) throws DocumentException {

        for (Map<String, Object> row : data) {
            int i = 1;
            for (Map<String, Object> str : headers) {
                if (row.get(str.get("headerName").toString()) != null){
                    if(str.get("totalInd").toString().equals("1")) {
                        String num_val = row.get(str.get("headerName").toString()).toString();
                        Integer val = Integer.valueOf(num_val);
                        PdfPCell tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(val), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        newTable.addCell(tableCell);
                    }
                    else if (str.get("totalInd").toString().equals("2")) {
                        String num_val = row.get(str.get("headerName").toString()).toString();
                        Double val = Double.valueOf(num_val);
                        PdfPCell tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(val), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        newTable.addCell(tableCell);
                    }
                    else if (str.get("totalInd").toString().equals("3")) {


                    }
                    else {
                        PdfPCell tableCell = new PdfPCell(new Phrase(row.get(str.get("headerName").toString()).toString(), normal));
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        newTable.addCell(tableCell);
                        if(i==1)
                            tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                }
                else {
                    continue;
                }
                i++;
            }
        }
        myPdfReport.add(newTable);
        sumByGroup(data, headers, groupBy, myPdfReport, normal, tableKey);
    }

    private void sumRowCells(List<Map<String, Object>> data, List<Map<String, Object>> headers, PdfPTable newTable, Document myPdfReport, Font boldFont) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()-3);
        newTable1.setWidthPercentage(100);
        int count = 1;
        int totalCount = headers.size();
        for (Map<String, Object> row : headers) {

            if(row.get("totalInd").toString().equals("1")) {
                Integer total2 = 0;
                for (Map<String, Object> str : data) {
                    if((str.get(row.get("headerName").toString()) != null)) {
                        String no = str.get(row.get("headerName").toString()).toString();
                        Integer val = Integer.valueOf(no);
                        total2+=val;
                    }
                }

                tableCell = new PdfPCell(new Phrase(String.valueOf(total2), boldFont));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(count == 1){
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if ((count != 1) && (count == totalCount-3)){
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                }
                else {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                newTable1.addCell(tableCell);
            }
            else if(row.get("totalInd").toString().equals("2")) {
                Double total = 0.0;
                for (Map<String, Object> str : data) {
                    if((str.get(row.get("headerName").toString()) != null)) {
                        String no = str.get(row.get("headerName")).toString();
                        Double val = Double.parseDouble(PayrollHRUtils.removeCommas(no));
                        total+=val;
                    }
                }
                String amount = PayrollHRUtils.getDecimalFormat().format(total);
                tableCell = new PdfPCell(new Phrase(amount, boldFont));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(count == 1){
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if ((count != 1) && (count == totalCount-3)){
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                }
                else {
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                newTable1.addCell(tableCell);
            }
            else {
                if(count == 1){
                    tableCell = new PdfPCell(new Phrase("Grand Total", boldFont));
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if (count == 2){
                    if(data.size() < 2){
                        tableCell = new PdfPCell(new Phrase(data.size()+" item", boldFont));
                    }else{
                        tableCell = new PdfPCell(new Phrase(data.size()+" items", boldFont));
                    }
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if ((count == totalCount-3)){
                    tableCell = new PdfPCell();
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                }
                else{
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

    private void sumByGroup(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, Document myPdfReport, Font normal, String tableKey) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()-3);
        newTable1.setWidthPercentage(100);


        int count = 1;
        for (Map<String, Object> row : headers) {
            if (row.get("totalInd").toString().equals("3")) {
                continue;
            }else {
                sumColumn(data, row, groupBy, newTable1, myPdfReport, normal, count, headers, tableKey);
            }
            count++;
        }
        myPdfReport.add(newTable1);

    }

    private void sumColumn(List<Map<String, Object>> data, Map<String, Object> header, String groupBy, PdfPTable newTable,
                           Document myPdfReport, Font normal, int count, List<Map<String, Object>> headers, String tableKey) throws DocumentException {
        // TODO Auto-generated method stub

        int totalCount = headers.size();

        if(header.get("totalInd").toString().equals("1")) {
            Integer total2 = 0;
            for (Map<String, Object> str : data) {
                if (IppmsUtils.isNotNullOrEmpty(str.get(header.get("headerName")).toString())) {
                    String no = str.get(header.get("headerName").toString()).toString();
                    Integer val = Integer.valueOf(no);
                    total2 += val;
                }
            }

            tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(total2), normal));
            tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
            tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            if(count == 1){
                tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
            }
            else if ((count != 1) && (count == (totalCount - 3))){
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
            }
            else {
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            }
            newTable.addCell(tableCell);
        }
        else if(header.get("totalInd").toString().equals("2")) {
            Double total = 0.0;
            for (Map<String, Object> str : data) {
                if (IppmsUtils.isNotNullOrEmpty(str.get(header.get("headerName")).toString())) {
                    String no = str.get(header.get("headerName").toString()).toString();
                    Double val = Double.valueOf(no);
                    total += val;
                }
            }
            tableCell = new PdfPCell(new Phrase( PayrollHRUtils.getDecimalFormat().format(total), normal));
            tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
            tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            if(count == 1){
                tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
            }
            else if ((count != 1) && (count == (totalCount - 3))){
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
            }
            else {
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            }
            newTable.addCell(tableCell);
        }
        else{
            if(count == 1){
                tableCell = new PdfPCell(new Phrase(tableKey, normal));
                tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
            }
            else if (count == 2){
                if(data.size() < 2){
                    tableCell = new PdfPCell(new Phrase(data.size()+" item", normal));
                }else{
                    tableCell = new PdfPCell(new Phrase(data.size()+" items", normal));
                }
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            }
            else if ((count == (totalCount - 3))){
                tableCell = new PdfPCell();
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
            }
            else{
                tableCell = new PdfPCell();
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            }
            tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
            newTable.addCell(tableCell);
        }
    }

    private boolean test(String tableValue) {
        if(tableValues.contains(tableValue)) {
            return true;
        }
        else {
            tableValues.add(tableValue);
            return false;
        }
    }
}
