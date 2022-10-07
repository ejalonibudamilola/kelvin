package com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfHeaderPageEvent;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfUtils;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.utils.ThrashOldPdfs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GroupedPdf extends PdfPageEventHelper {

    List<String> tableValues = new ArrayList<>();
    private PdfPCell tableCell;

    public void getPdf(HttpServletResponse response, HttpServletRequest request, ReportGeneratorBean rt) {


        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();


        String currentTime = DateTimeFormatter.ofPattern("hh.mm a").format(LocalTime.now());
//        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
//        String currentTime = dateFormat.format(new Date());
        int year = currentDate.getYear();

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
            //String fileLocation = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";


            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD);
            Font smallBold = new Font(Font.FontFamily.HELVETICA, 11f, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL);


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
            addMainHeaders(rt.getMainHeaders(), myPdfReport, boldFont);

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
            if (rt.getTableType() == 1){
                //grouping table data before adding cells
                PdfPTable myReportTable = new PdfPTable(rt.getTableHeaders().size()-1);
                addTableHeaders(rt.getTableHeaders(), myPdfReport, boldFont, myReportTable);
                groupData(rt.getTableData(), rt.getTableHeaders(), newTable, myPdfReport, normal, boldFont, rt.getGroupBy(), rt.getTotalInd());
            }
            else if (rt.getTableType() == 2){
//                subGroupData(rt.getTableData(), rt.getTableHeaders(), rt.getReportTitle(), rt.getGroupBy(), rt.getSubGroupBy());
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
                finally {
                    ThrashOldPdfs.checkToDeleteOldPdfs(Arrays.asList(fileLocation));
                }
            }



        } catch (IOException | DocumentException ex) {
            ex.printStackTrace();
            System.out.println("An exception was caught: "+ex);
        }
//        os.close();
    }

    private void addTableHeaders(List<Map<String, Object>> tableHeaders, Document myPdfReport, Font bold, PdfPTable myReportTable) throws DocumentException {

     //   PdfPTable outerTable = new PdfPTable(1);



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
                else if(i == (totalList - 1)){
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
                else if(i == (totalList - 1)){
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
                else if(i == (totalList - 1)){
                    tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
            value.setAlignment(Element.ALIGN_LEFT);
            myPdfReport.add(value);
            myPdfReport.add(new Paragraph("\n"));
        }
    }


    private void groupData(List<Map<String, Object>> data, List<Map<String, Object>> headers, PdfPTable newTable,
                           Document myPdfReport, Font normal, Font boldFont, String groupBy, int totalInd) throws DocumentException {
        // TODO Auto-generated method stub
        int i = 0;
        String dump_value = "", fiveSpaces = "     ";
        boolean test_existence;
        String tableValue;
        Paragraph group_header;
        Paragraph spaces;
        for (Map<String, Object> row : data) {

              tableValue = row.get(groupBy).toString();
              test_existence = test(tableValue);
            if(!test_existence) {
                if(i != 0) {
                    myPdfReport.newPage();
                }

//                addTableHeaders(headers, myPdfReport, boldFont, newTable);
                  group_header = new Paragraph(groupBy+": "+tableValue, boldFont);
                group_header.setAlignment(Element.ALIGN_LEFT);
                myPdfReport.add(group_header);

                 spaces = new Paragraph(fiveSpaces);
                myPdfReport.add(spaces);


                newTable = new PdfPTable(headers.size()-1);
                newTable.setWidthPercentage(100);
                groupRows(data, headers, groupBy, newTable, tableValue, myPdfReport, normal);
                i++;
            }
            else {
            }
        }

        if(totalInd == 1)
            myPdfReport.add(new Paragraph("\n"));
            sumRowCells(data, headers, newTable, myPdfReport, boldFont);
    }


    //adding rows to table by group
    private void groupRows(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, PdfPTable newTable, String tableValue, Document myPdfReport, Font normal) throws DocumentException {
        int i;
        String num_val;
        Integer valInt;
        PdfPCell tableCell;
        Double valD;
        for (Map<String, Object> row : data) {
              i = 1;
            for (Map<String, Object> str : headers) {
                if ((row.get(str.get("headerName").toString()) != null) && (row.get(groupBy).toString().equals(tableValue))){
                    if(str.get("totalInd").toString().equals("1")) {
                          num_val = row.get(str.get("headerName").toString()).toString();
                          //valInt = Integer.valueOf(num_val);
                          tableCell = new PdfPCell(new Phrase(num_val, normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        newTable.addCell(tableCell);
                    }
                    else if (str.get("totalInd").toString().equals("2")) {
                          num_val = row.get(str.get("headerName").toString()).toString();
                          valD = Double.valueOf(num_val);
                          tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(valD), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        newTable.addCell(tableCell);
                    }
                    else if (str.get("totalInd").toString().equals("3")) {


                    }
                    else {
                          tableCell = new PdfPCell(new Phrase(row.get(str.get("headerName").toString()).toString(), normal));
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        if(i==1){
                            tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        }
                        else if(i == (headers.size() -1)){
                            tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        }
                        newTable.addCell(tableCell);
                    }
                }
                else {
                        continue;
                }
                i++;
            }
        }
        myPdfReport.add(newTable);
        sumByGroup(data, headers, groupBy, myPdfReport, tableValue, normal);
    }

    private void sumRowCells(List<Map<String, Object>> data, List<Map<String, Object>> headers, PdfPTable newTable, Document myPdfReport, Font boldFont) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()-1);
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
                else if ((count != 1) && (count == totalCount)){
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
                else if ((count != 1) && (count == totalCount)){
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
                else if ((count == totalCount)){
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

    private void sumByGroup(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, Document myPdfReport, String tableValue, Font normal) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()-1);
        newTable1.setWidthPercentage(100);


        int count = 1;
        for (Map<String, Object> row : headers) {
            if (row.get("totalInd").toString().equals("3")) {
                continue;
            }else {
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

        if(header.get("totalInd").toString().equals("1")) {
            Integer total2 = 0;
            for (Map<String, Object> str : data) {
                String cCompare = str.get(groupBy).toString();
                if ((IppmsUtils.isNotNullOrEmpty(str.get(header.get("headerName")).toString())) && (cCompare.equals(tableValue1))) {
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
            else if ((count != 1) && (count == (totalCount - 1))){
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
                String cCompare = str.get(groupBy).toString();
                if ((IppmsUtils.isNotNullOrEmpty(str.get(header.get("headerName")).toString())) && (cCompare.equals(tableValue1))) {
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
            else if ((count != 1) && (count == (totalCount - 1))){
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
            }
            else {
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            }
            newTable.addCell(tableCell);
        }
        else{
            if(count == 1){
                tableCell = new PdfPCell(new Phrase(tableValue1, normal));
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
            else if ((count == (totalCount - 1))){
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
