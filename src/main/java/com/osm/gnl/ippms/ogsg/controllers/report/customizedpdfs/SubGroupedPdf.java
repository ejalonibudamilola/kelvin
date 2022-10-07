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
import com.osm.gnl.ippms.ogsg.utils.ThrashOldPdfs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SubGroupedPdf {

    List<String> tableValues = new ArrayList<>();
    private PdfPCell tableCell;
    private String fiveSpaces = "     ";
    PdfWriter  pdfWriter;
    Map<String, Map<String, String>> lastPageNoticeMap;
    private boolean startNewPage;
    private boolean wrapStrings;
    public void getPdf(HttpServletResponse response, HttpServletRequest request, ReportGeneratorBean rt) {

        wrapStrings = rt.isNoWrap();
        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();

        String currentTime = DateTimeFormatter.ofPattern("hh.mm a").format(LocalTime.now());
        int year = currentDate.getYear();

        Document myPdfReport;
        if(rt.getTableHeaders().size() > 5){
            if(rt.isCheckRotate()){
                myPdfReport = new Document(PageSize.A2.rotate());
            }
            else{
                myPdfReport = new Document(PageSize.A2);
            }
        }
        else {
            myPdfReport = new Document(PageSize.LETTER);
        }
        String fileLocation = null;
        try {

            File currDir = new File(request.getServletContext().getRealPath(File.separator)+rt.getReportTitle()+".pdf");
              fileLocation = currDir.getPath();
            //String fileLocation = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 11f, Font.BOLD);
            Font smallBold = new Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 11f, Font.NORMAL);


            pdfWriter = PdfWriter.getInstance(myPdfReport, new FileOutputStream(fileLocation));
            myPdfReport.open();
            PdfTemplate template = pdfWriter.getDirectContent().createTemplate(30, 12);
            pdfWriter.setPageEvent(new PdfHeaderPageEvent(rt, boldFont, smallBold, normal, rt.getTableType(), template));


            PdfPTable head = new PdfPTable(1);

            myPdfReport = PdfUtils.makeHeaderFile(rt.getBusinessCertificate(), new PdfPCell(), myPdfReport, head);

            //addding pdf Main Report Title

            //adding main headers
            addMainHeaders(rt.getMainHeaders(),myPdfReport,headerFont);
            addMainHeadersRight(rt.getMainHeaders2(), myPdfReport, smallBold);


            Paragraph time = new Paragraph("Print Date: "+currentDay + " " + currentMonth + ", " + year, smallBold);
            time.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time);

            Paragraph time2 = new Paragraph("Print Time: "+currentTime, smallBold);
            time2.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time2);
            myPdfReport.add(new Paragraph("\n"));


            //PdfPTable newTable = null;
            //check if data should be grouped
            //if (rt.getTableType() == 2){
                //grouping table data before adding cells

            //PdfPTable myReportTable = new PdfPTable(rt.getTableHeaders().size()-2);
            PdfPTable myReportTable =  prepareMyReportTable(rt.getTableHeaders().size() - 2);
            myReportTable.setWidthPercentage(100f);
            addTableHeaders(rt.getTableHeaders(), myPdfReport, boldFont, myReportTable);
               groupData(rt.getTableData(), rt.getTableHeaders(), myPdfReport, normal, boldFont, rt.getGroupBy(), rt.getTotalInd(),
                        rt.getGroupedKeySet(), rt.getSubGroupedKeySet(), rt.getSubGroupBy());

          //  }


            myPdfReport.close();

            FileInputStream baos = new FileInputStream(fileLocation);

//            ThrashOldPdfs.checkToDeleteOldPdfs();

            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename="+rt.getReportTitle()+".pdf");

            if(IppmsUtils.isNull(rt.isOutputInd()) || rt.isOutputInd()) {
                OutputStream os  = response.getOutputStream();
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
        }finally{
            if(fileLocation != null)
            try {
                Thread.sleep(2500);
                ThrashOldPdfs.checkToDeleteOldPdfs(Arrays.asList(fileLocation));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        os.close();
    }

    private void addTableHeaders(List<Map<String, Object>> tableHeaders, Document myPdfReport, Font bold, PdfPTable myReportTable) throws DocumentException {

        String field;
        //create a cell object
        //add headers
        int i = 1;
        int totalList = tableHeaders.size();
        for (Map<String, Object> str : tableHeaders) {
            if(str.get("totalInd").toString().equals("3")){
                continue;
            }
            else if(str.get("totalInd").toString().equals("1")){
                 field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(i == 1){
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if(i == (totalList - 2)){
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                }
                else{
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                myReportTable.addCell(tableCell);
            }
            else if(str.get("totalInd").toString().equals("2")){
                 field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(i == 1){
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if(i == (totalList - 2)){
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                }
                else{
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                myReportTable.addCell(tableCell);
            }
            else {
                 field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                if(i == 1){
                    tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if(i == (totalList - 2)){
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

    private void addMainHeaders(List<String> mainHeaders,Document myPdfReport, Font headerFont) throws DocumentException {

        for (String header : mainHeaders){
            Paragraph value = new Paragraph(header.toUpperCase(), headerFont);
            value.setAlignment(Element.ALIGN_CENTER);
            myPdfReport.add(value);
            myPdfReport.add(new Paragraph("\n"));
        }
    }

    private void addMainHeadersRight(List<String> mainHeaders, Document myPdfReport, Font font) throws DocumentException {
        if(IppmsUtils.isNotNullOrEmpty(mainHeaders)){
            for (String header : mainHeaders){
                Paragraph value = new Paragraph(header, font);
                value.setAlignment(Element.ALIGN_RIGHT);
                myPdfReport.add(value);
            }
        }
    }


   private void groupData(List<Map<String, Object>> data, List<Map<String, Object>> headers,
                          Document myPdfReport, Font normal, Font boldFont, String groupBy, int totalInd, Set<String> groupedKeySet, Set<String> subGroupedKeySet,
                          String subGroupBy) throws DocumentException {

        // This is where the Group by Items are separated in the groupedKeySet....(Deduction or Loan)
       PdfPTable newTable = prepareMyReportTable(headers.size() - 2);
       newTable.setWidthPercentage(100);

        List<Map<String, Object>> mapList;
        int i = 0;
        for (String key : groupedKeySet) {

          /*  if(i != 0) {
                myPdfReport.newPage();
                newPage = true;
            }else{
                newPage = false;
            }
*/
           // newTable = new PdfPTable(headers.size()-2);

            if(i != 0){
                myPdfReport.newPage();
            }



            mapList = data.stream()
                    .filter(stringObjectMap -> stringObjectMap.get(groupBy).toString().equals(key))
                    .collect(Collectors.toList()); //This contains the Deduction/Loan and all the Staffs and MDA they belong to
            subGroupData(mapList, headers, subGroupBy, myPdfReport, normal, subGroupedKeySet, key,  groupBy,boldFont);
            //myPdfReport.add(new Paragraph("\n"));
            sumByGroupParent(mapList,headers, groupBy, myPdfReport, normal, key);
            i++;
        }

//        if(totalInd == 1)
//            myPdfReport.add(new Paragraph("\n"));
        sumRowCells(data, headers, myPdfReport, boldFont);
    }
    private void subGroupData(List<Map<String, Object>> data, List<Map<String, Object>> headers, String subGroupBy,
                              Document myPdfReport, Font normal, Set<String> subGroupedKeySet, String tableKey, String groupBy, Font boldFont) throws DocumentException {

        List<Map<String, Object>> mapList;
        PdfPTable newTable;
        int i = 0;
        for (String key : subGroupedKeySet) {
            //Here we get all the items for the key.

            mapList = data.stream()
                    .filter(stringObjectMap -> stringObjectMap.get(subGroupBy).toString().equals(key))
                    .collect(Collectors.toList());

            if (mapList.size() > 0) {
                ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setSubGroupBy(subGroupBy);
                ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setGroupByKey(tableKey);
                ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setKey(key);
//                if(i > 0 || this.startNewPage)
//                   myPdfReport.newPage();
//                else{
//                    if(pdfWriter.getCurrentPageNumber() == 1)
//                        this.writeSubGroupHeader(tableKey,boldFont,myPdfReport,subGroupBy,key);
//                }

                this.writeSubGroupHeader(tableKey,boldFont,myPdfReport,subGroupBy,key);
//                if(!newPage)
//                   this.writeSubGroupHeader();
                  newTable = prepareMyReportTable(headers.size() - 2);
                newTable.setWidthPercentage(100);
                groupRows(mapList, headers, groupBy, newTable,myPdfReport,normal, key, subGroupBy, tableKey);
                i++;
            }

        }
        ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setSubGroupBy(null);
        ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setGroupByKey(null);
        ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setKey(null);

    }

    private Document writeSubGroupHeader(String groupByKey,Font boldFont, Document myPdfReport,String subGroupBy,String key) throws DocumentException {
        Paragraph newHeader = new Paragraph(groupByKey, boldFont);
        newHeader.setAlignment(Element.ALIGN_LEFT);
        myPdfReport.add(newHeader);

      /*  Paragraph newSpaces = new Paragraph(fiveSpaces);
        myPdfReport.add(newSpaces);
*/
        Paragraph group_header = new Paragraph(subGroupBy+":  " + key, boldFont);
        group_header.setAlignment(Element.ALIGN_LEFT);
        myPdfReport.add(group_header);

       /* Paragraph spaces = new Paragraph(fiveSpaces);
        myPdfReport.add(spaces);*/
        return myPdfReport;
    }
    //adding rows to table by group
    private void groupRows(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy,
                           PdfPTable newTable, Document myPdfReport, Font normal, String tableKey, String subGroupBy, String groupByKey) throws DocumentException {


        int i;
        for (Map<String, Object> row : data) {
           i = 1;

            for (Map<String, Object> str : headers) {
                if (row.get(str.get("headerName").toString()) != null){

                    /*if(pdfWriter.getCurrentPageNumber() > currentPageNumber){
                        this.writeSubGroupHeader(groupByKey,groupBy,boldFont,myPdfReport,subGroupBy,tableKey);
                        currentPageNumber = pdfWriter.getCurrentPageNumber();
                    }*/
                    //Check if the writer is writing to a new page here

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
                        if(wrapStrings)
                            tableCell.setNoWrap(true);
                        tableCell.setBorder(Rectangle.NO_BORDER);
                        newTable.addCell(tableCell);
                        if(i==1)
                            tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                }else {

                    continue;
                }
                i++;

            }
        }
        myPdfReport.add(newTable);
        sumByGroup(data, headers, groupBy , myPdfReport, normal, tableKey, subGroupBy, groupByKey);
    }

    private void sumRowCells(List<Map<String, Object>> data, List<Map<String, Object>> headers, Document myPdfReport, Font boldFont) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()-2);
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
                else if ((count != 1) && (count == totalCount-2)){
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
                else if ((count != 1) && (count == totalCount-2)){
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
                else if ((count == totalCount-2)){
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

    private void sumByGroup(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, Document myPdfReport, Font normal, String tableKey, String subGroupBy, String groupByKey) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()-2);
        newTable1.setWidthPercentage(100);


        int count = 1;
        for (Map<String, Object> row : headers) {
            if (row.get("totalInd").toString().equals("3")) {
                continue;
            }else {
                sumColumn(data, row, groupBy , newTable1, myPdfReport, normal, count, headers, tableKey, subGroupBy, groupByKey);
            }
            count++;
        }
        myPdfReport.add(newTable1);

    }

    private void sumByGroupParent(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, Document myPdfReport, Font normal, String tableKey) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()-2);
        newTable1.setWidthPercentage(100);


        int count = 1;
        for (Map<String, Object> row : headers) {
            if (row.get("totalInd").toString().equals("3")) {
                continue;
            }else {
                sumColumnParent(data, row, groupBy, newTable1, myPdfReport, normal, count, headers, tableKey);
            }
            count++;
        }
        myPdfReport.add(newTable1);
        if(!this.startNewPage)
            this.startNewPage = true;

    }

    private void sumColumn(List<Map<String, Object>> data, Map<String, Object> header, String groupBy , PdfPTable newTable,
                           Document myPdfReport, Font normal, int count, List<Map<String, Object>> headers, String tableKey, String subGroupBy , String groupByKey) throws DocumentException {
        // TODO Auto-generated method stub

        int totalCount = headers.size();
         if(header.get("totalInd").toString().equals("1")) {
            Integer total2 = 0;
            for (Map<String, Object> str : data) {
               // String cCompare = str.get(subGroupBy).toString();
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
            else if ((count != 1) && (count == (totalCount - 2))){
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
               // String cCompare = str.get(subGroupBy).toString();
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
            else if ((count != 1) && (count == (totalCount - 2))){
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
            }
            else {
                tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
            }
            newTable.addCell(tableCell);
        }
        else{
            if(count == 1){

                tableCell = new PdfPCell(new Phrase("Totals", normal));
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
            else if ((count == (totalCount - 2))){
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

    private void sumColumnParent(List<Map<String, Object>> data, Map<String, Object> header, String groupBy, PdfPTable newTable,
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
            else if ((count != 1) && (count == (totalCount - 2))){
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
            else if ((count != 1) && (count == (totalCount - 2))){
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
            else if ((count == (totalCount - 2))){
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
    private PdfPTable prepareMyReportTable(int size) {
        float[] columnWidths;
        switch (size) {
            case 3:
                columnWidths = new float[]{5, 5, 5};
                break;
            case 4:
                columnWidths = new float[]{7, 5, 5, 5};
                break;
            case 5:
                columnWidths = new float[]{7, 5, 5, 5, 5};
                break;
            case 6:
                columnWidths = new float[]{7, 5, 5, 5, 5,5};
                break;
            case 7:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5};
                break;
            case 8:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5,5};
                break;
            case 9:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5,5,5};
                break;
            case 10:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5,5,5,5};
                break;
            case 11:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5,5,5,5,5};
                break;
            case 12:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5,5,5,5,5,5};
                break;
            case 13:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5,5,5,5,5,5,5};
                break;
            case 14:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5,5,5,5,5,5,5,5};
                break;
            case 15:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5,5,5,5,5,5,5,5,5};
                break;
            case 16:
                columnWidths = new float[]{7, 5, 5, 5, 5,5,5,5,5,5,5,5,5,5,5,5};
                break;
            default:
                return new PdfPTable(size);

        }

        return new PdfPTable(columnWidths);
    }
}
