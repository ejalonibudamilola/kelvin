package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PdfHeaderPageEvent extends PdfPageEventHelper {


    private PdfPCell tableCell;
    private ReportGeneratorBean bean;
    private Font smallBold;
    private  Font boldFont;
    private Font normal;
    private int tableType;
    private Font watermark;
    private PdfTemplate total;
    private String groupByKey;
    private String subGroupBy;
    private String key;
    private String schoolName;

    public PdfHeaderPageEvent(ReportGeneratorBean x, Font bold, Font small, Font normal, int tableType, PdfTemplate template) {
        setBean(x);
        setSmallBold(small);
        setBoldFont(bold);
        setNormal(normal);
        setTableType(tableType);
        setTotal(template);
    }


    @SneakyThrows
    public void onStartPage(PdfWriter writer, Document document) {

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD);

        PdfPTable head = new PdfPTable(1);
        PdfPCell t_cell;
        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();
        int year = currentDate.getYear();
        String currentTime = DateTimeFormatter.ofPattern("hh.mm a").format(LocalTime.now());

        document = PdfUtils.makeHeaderFile(this.getBean().getBusinessCertificate(), new PdfPCell(), document, head);


        addMainHeaders(this.getBean().getMainHeaders(), document, headerFont);
        addMainHeadersRight(this.getBean().getMainHeaders2(), document, smallBold);

        Paragraph time = new Paragraph(currentDay + " " + currentMonth + ", " + year, smallBold);
        time.setAlignment(Element.ALIGN_RIGHT);
        document.add(time);

        Paragraph time2 = new Paragraph(currentTime, smallBold);
        time2.setAlignment(Element.ALIGN_RIGHT);
        document.add(time2);
        document.add(new Paragraph("\n"));


        if(tableType == 0) {
            PdfPTable myReportTable = new PdfPTable(this.getBean().getTableHeaders().size());
            addSimpleTableHeaders(this.getBean().getTableHeaders(), document, boldFont, myReportTable);
        }
        else if(tableType == 1) {
            PdfPTable myReportTable = new PdfPTable(this.getBean().getTableHeaders().size()-1);
            addGroupedTableHeaders(this.getBean().getTableHeaders(), document, boldFont, myReportTable);
        }
        else if(tableType == 2) {
            PdfPTable myReportTable = new PdfPTable(this.getBean().getTableHeaders().size()-2);
            addSubGroupedTableHeaders(this.getBean().getTableHeaders(), document, boldFont, myReportTable);


        }
        else if(tableType == 3) {
            PdfPTable myReportTable = new PdfPTable(this.getBean().getTableHeaders().size()-3);
            addDoubleSubGroupedTableHeaders(this.getBean().getTableHeaders(), document, boldFont, myReportTable);
        }


    }

//    public void onOpenDocument(PdfWriter writer, Document document) {
//        total = writer.getDirectContent().createTemplate(30, 12);
//    }


    public void onEndPage(PdfWriter writer, Document document){
        int y = 120;
        int i;
        for (i = 1; i<20; i++) {
            watermark = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, new GrayColor(0.93f));
            if(i%2 != 1) {
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase(this.getBean().getWatermark(), watermark),
                        120.5f, y, 0f);

                watermark = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, new GrayColor(0.93f));
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase(this.getBean().getWatermark(), watermark),
                        340.5f, y, 0f);

                watermark = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, new GrayColor(0.93f));
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase(this.getBean().getWatermark(), watermark),
                        560.5f, y, 0f);

                watermark = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, new GrayColor(0.93f));
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase(this.getBean().getWatermark(), watermark),
                        780.5f, y, 0f);

                watermark = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, new GrayColor(0.93f));
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase(this.getBean().getWatermark(), watermark),
                        1000.5f, y, 0f);
            }
            else{
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase("Ogun State Government", watermark),
                        120.5f, y, 0f);

                watermark = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, new GrayColor(0.93f));
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase("GNL Systems Ltd", watermark),
                        340.5f, y, 0f);

                watermark = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, new GrayColor(0.93f));
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase("Ogun State Government", watermark),
                        560.5f, y, 0f);

                watermark = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, new GrayColor(0.93f));
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase("GNL Systems Ltd", watermark),
                        780.5f, y, 0f);

                watermark = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, new GrayColor(0.93f));
                ColumnText.showTextAligned(writer.getDirectContentUnder(),
                        Element.ALIGN_CENTER, new Phrase("Ogun State Government", watermark),
                        1000.5f, y, 0f);
            }
            y+=100;
        }


        //set pagination
        PdfPTable table = new PdfPTable(3);
        try {
            table.setWidths(new int[]{24, 24, 2});
            table.getDefaultCell().setFixedHeight(10);
            table.getDefaultCell().setBorder(Rectangle.TOP);
            PdfPCell cell = new PdfPCell();
            cell.setBorder (0);
            cell.setBorderWidthTop (1);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setPhrase(new Phrase("Generated By GNL Systems IPPMS", normal));
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setBorder (0);
            cell.setBorderWidthTop (1);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPhrase(new Phrase(String.format("Page %d of", writer.getPageNumber()), normal));
            table.addCell(cell);

            cell = new PdfPCell(Image.getInstance(total));
            cell.setBorder (0);
            cell.setBorderWidthTop (1);
            table.addCell(cell);
            table.setTotalWidth(document.getPageSize().getWidth()
                    - document.leftMargin() - document.rightMargin());
            table.writeSelectedRows(0, -1, document.leftMargin(),
                    document.bottomMargin() - 20, writer.getDirectContent());
        }
        catch(DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                new Phrase(String.valueOf(writer.getPageNumber() - 1), normal),
                2, 2, 0);
    }


    private void addMainHeaders(List<String> mainHeaders, Document myPdfReport, Font font) throws DocumentException {

        for (String header : mainHeaders){
            Paragraph value = new Paragraph(header.toUpperCase(), font);
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


    private void addSimpleTableHeaders(List<Map<String, Object>> tableHeaders, Document myPdfReport, Font bold, PdfPTable myReportTable) throws DocumentException {


        myReportTable.setWidthPercentage(100);
        if(this.subGroupBy != null && this.groupByKey != null && this.key != null)
            myPdfReport = this.writeSubGroupHeader(groupByKey,boldFont,myPdfReport,subGroupBy,key);

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
                if(i == 1){
                    tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if(i == (totalList)){
                    tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
                else if(i == (totalList)){
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
                tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(i == 1){
                    tableCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else if(i == (totalList)){
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

    private void addGroupedTableHeaders(List<Map<String, Object>> tableHeaders, Document myPdfReport, Font bold, PdfPTable myReportTable) throws DocumentException {

        myReportTable.setWidthPercentage(100);
        if(this.subGroupBy != null && this.groupByKey != null && this.key != null)
            myPdfReport = this.writeSubGroupHeader(groupByKey,boldFont,myPdfReport,subGroupBy,key);
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
                tableCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
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

    private void addSubGroupedTableHeaders(List<Map<String, Object>> tableHeaders, Document myPdfReport, Font bold, PdfPTable myReportTable) throws DocumentException {


        myReportTable.setWidthPercentage(100);
        if(this.subGroupBy != null && this.groupByKey != null && this.key != null)
            myPdfReport = this.writeSubGroupHeader(groupByKey,boldFont,myPdfReport,subGroupBy,key);
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
                else if(i == (totalList - 2)){
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
                else if(i == (totalList - 2)){
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
                else if(i == (totalList - 2)){
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
    private Document writeSubGroupHeader(String groupByKey,Font boldFont, Document myPdfReport,String subGroupBy,String key) throws DocumentException {
        Paragraph newHeader = new Paragraph(groupByKey, boldFont);
        newHeader.setAlignment(Element.ALIGN_LEFT);
        myPdfReport.add(newHeader);

//        Paragraph newSpaces = new Paragraph("      ");
//        myPdfReport.add(newSpaces);

        Paragraph group_header = new Paragraph(subGroupBy+":  " + key, boldFont);
        group_header.setAlignment(Element.ALIGN_LEFT);
        group_header.add(new Paragraph("\n"));
        myPdfReport.add(group_header);

        if(this.getSchoolName() != null){
              group_header = new Paragraph("School :  " + this.getSchoolName(), boldFont);
            group_header.setAlignment(Element.ALIGN_LEFT);
            group_header.add(new Paragraph("\n"));
            myPdfReport.add(group_header);
        }

      /*  Paragraph spaces = new Paragraph("     ");
        myPdfReport.add(spaces);*/
        return myPdfReport;
    }
    private void addDoubleSubGroupedTableHeaders(List<Map<String, Object>> tableHeaders, Document myPdfReport, Font bold, PdfPTable myReportTable) throws DocumentException {


        myReportTable.setWidthPercentage(100);
        if(this.subGroupBy != null && this.groupByKey != null && this.key != null)
            myPdfReport = this.writeSubGroupHeader(groupByKey,boldFont,myPdfReport,subGroupBy,key);
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


}
