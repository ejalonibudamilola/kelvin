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
import com.osm.gnl.ippms.ogsg.utils.ThrashOldPdfs;
import org.springframework.lang.Nullable;

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


public abstract class PdfBaseClass {

    List<String> tableValues = new ArrayList<>();
    protected PdfPCell tableCell;
    protected String fiveSpaces = "     ";
    protected PdfWriter pdfWriter;
    protected boolean startNewPage;
    protected boolean wrapStrings;
    protected Document myPdfReport;
    protected ReportGeneratorBean rt;
    protected LocalDate currentDate;
    protected  Font headerFont ;
    protected Font boldFont;
    protected Font smallBold ;
    protected Font normal;
    protected String fileLocation;
    protected PdfPTable myReportTable;

    public void makePdf(HttpServletRequest request, ReportGeneratorBean reportGeneratorBean) {
        rt = reportGeneratorBean;
        wrapStrings = rt.isNoWrap();
        currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();

        String currentTime = DateTimeFormatter.ofPattern("hh.mm a").format(LocalTime.now());
        int year = currentDate.getYear();


        if(rt.getTableHeaders().size() > 5){
            myPdfReport = new Document(PageSize.A2);
        }
        else {
            myPdfReport = new Document(PageSize.LETTER);
        }

        try {

            File currDir = new File(request.getServletContext().getRealPath(File.separator)+rt.getReportTitle()+".pdf");
            fileLocation = currDir.getPath();
            //String fileLocation = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";

              headerFont = new Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD);
              boldFont = new Font(Font.FontFamily.HELVETICA, 13f, Font.BOLD);
              smallBold = new Font(Font.FontFamily.HELVETICA, 11f, Font.BOLD);
              normal = new Font(Font.FontFamily.HELVETICA, 13f, Font.NORMAL);


            pdfWriter = PdfWriter.getInstance(myPdfReport, new FileOutputStream(fileLocation));
            myPdfReport.open();
            PdfTemplate template = pdfWriter.getDirectContent().createTemplate(30, 12);
            pdfWriter.setPageEvent(new PdfHeaderPageEvent(rt, boldFont, smallBold, normal, rt.getTableType(), template));


            PdfPTable head = new PdfPTable(1);

            myPdfReport = PdfUtils.makeHeaderFile(rt.getBusinessCertificate(), new PdfPCell(), myPdfReport, head);

            //addding pdf Main Report Title

            //adding main headers
            addMainHeaders(rt.getMainHeaders());
            addMainHeadersRight(rt.getMainHeaders2());


            Paragraph time = new Paragraph("Print Date: "+currentDay + " " + currentMonth + ", " + year, smallBold);
            time.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time);

            Paragraph time2 = new Paragraph("Print Time: "+currentTime, smallBold);
            time2.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time2);
            myPdfReport.add(new Paragraph("\n"));

            PdfPTable myReportTable =  prepareMyReportTable(rt.getTableHeaders().size() - 3);
            myReportTable.setWidthPercentage(100f);
            addTableHeaders(myReportTable);


        } catch (IOException | DocumentException ex) {
            ex.printStackTrace();
        }
//        os.close();
    }
    protected void thrashPdf(){
        if(fileLocation != null)
            try {
                Thread.sleep(2500);
                ThrashOldPdfs.checkToDeleteOldPdfs(Arrays.asList(fileLocation));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
    protected void addMainHeaders(List<String> mainHeaders) throws DocumentException {

        for (String header : mainHeaders){
            Paragraph value = new Paragraph(header.toUpperCase(), headerFont);
            value.setAlignment(Element.ALIGN_CENTER);
            myPdfReport.add(value);
            myPdfReport.add(new Paragraph("\n"));
        }
    }

    protected void addMainHeadersRight(List<String> mainHeaders) throws DocumentException {
        if(IppmsUtils.isNotNullOrEmpty(mainHeaders)){
            for (String header : mainHeaders){
                Paragraph value = new Paragraph(header, smallBold);
                value.setAlignment(Element.ALIGN_RIGHT);
                myPdfReport.add(value);
            }
        }
    }
    protected PdfPTable prepareMyReportTable(int size) {
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
    private void addTableHeaders(PdfPTable myReportTable) throws DocumentException {

        String field;
        //create a cell object
        //add headers
        int i = 1;
        int totalList = rt.getTableHeaders().size();
        for (Map<String, Object> str :  rt.getTableHeaders()) {
            if(str.get("totalInd").toString().equals("3")){
                continue;
            }
            else if(str.get("totalInd").toString().equals("1")){
                field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, boldFont));
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
                tableCell = new PdfPCell(new Phrase(field, boldFont));
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
                tableCell = new PdfPCell(new Phrase(field, boldFont));
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
    protected void writeSubGroupHeader(String groupByKey,Font boldFont, String subGroupBy,String key,@Nullable String schoolName) throws DocumentException {
        Paragraph newHeader = new Paragraph(groupByKey, boldFont);
        newHeader.setAlignment(Element.ALIGN_LEFT);
        myPdfReport.add(newHeader);

      /*  Paragraph newSpaces = new Paragraph(fiveSpaces);
        myPdfReport.add(newSpaces);
*/
        Paragraph group_header = new Paragraph(subGroupBy+":  " + key, boldFont);
        group_header.setAlignment(Element.ALIGN_LEFT);
        myPdfReport.add(group_header);

        if(schoolName != null){
            group_header = new Paragraph("School :  " + schoolName, boldFont);
            group_header.setAlignment(Element.ALIGN_LEFT);
            myPdfReport.add(group_header);
        }

    }
    protected void writeOutPdf(HttpServletResponse response){
        try{
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



    } catch (IOException ex) {
        ex.printStackTrace();
    }finally{
         thrashPdf();
    }
    }
}
