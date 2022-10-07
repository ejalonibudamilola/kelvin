package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
import com.osm.gnl.ippms.ogsg.utils.ThrashOldPdfs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ExecutiveSummaryPdfGenerator {


    public void generatePdf(WageBeanContainer data, String reportTitle, HttpServletResponse response,
                              HttpServletRequest request, BusinessCertificate bc) throws IOException {

        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();
        int year = currentDate.getYear();
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String currentTime = dateFormat.format(new Date());
       // Document myPdfReport = new Document(PageSize.A4.rotate());
        Document myPdfReport = new Document(PageSize.LETTER);
        OutputStream os = null;
        try {
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            String fileLocation = path.substring(0, path.length() - 1) + reportTitle + ".pdf";
            File currDir = new File(request.getServletContext().getRealPath(File.separator)+reportTitle+".pdf");
            String fileLocation = currDir.getPath();
            //String fileLocation = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";

            //File currDir = new File(this.getClass().getClassLoader().getResource(".").getFile()+"/"+reportTitle+".pdf");
           // String fileLocation = currDir.getAbsolutePath();


            PdfWriter pdfwriter = PdfWriter.getInstance(myPdfReport, new FileOutputStream(fileLocation));
            myPdfReport.open();
            PdfFont bold = PdfFontFactory.createFont("Times-Bold");
//            PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, true);
            Font largeBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD);
            Font smallBold = new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 15f, Font.NORMAL);
            Font headingFont = new Font(Font.FontFamily.HELVETICA, 14);


            PdfPTable head = new PdfPTable(1);
            PdfPCell t_cell;

            //adding pdf Header
            //File imgDir = new File("src/main/resources/static/images/" + bc.getClientReportLogo());
//            File imgDir = ResourceUtils.getFile("classpath:static/images/"+bc.getClientReportLogo());
//            String imgPath = imgDir.getAbsolutePath();
//            Image img2 = Image.getInstance(imgPath);
//            img2.setAlignment(Element.ALIGN_CENTER);
//            img2.setWidthPercentage(120);
//            t_cell = new PdfPCell();
//            t_cell.setBorder(Rectangle.NO_BORDER);
//            t_cell.addElement(img2);
//            head.addCell(t_cell);
//            myPdfReport.add(head);

            myPdfReport = PdfUtils.makeHeaderFile(bc, new PdfPCell(), myPdfReport, head);


            //addding pdf Main Report Title
            Paragraph title = new Paragraph(reportTitle, largeBold);
            title.setAlignment(Element.ALIGN_CENTER);
            myPdfReport.add(title);
            myPdfReport.add(new Paragraph("\n"));

            Paragraph time = new Paragraph(currentDay + " " + currentMonth + ", " + year, smallBold);
            time.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time);

            Paragraph time2 = new Paragraph(currentTime, smallBold);
            time2.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time2);
            myPdfReport.add(new Paragraph("\n"));

            //adding main headers

          //  PdfPTable reportTable = new PdfPTable(6);
            float[] columnWidth = {1, 8, 2,2,4,4};
            PdfPTable reportTable = new PdfPTable(columnWidth);
            reportTable.setWidthPercentage(100);
            //adding table headers
            addTableHeaders(data, myPdfReport, boldFont, reportTable);

            organizationDataList(data, myPdfReport, reportTable, normal, boldFont, bc);
            myPdfReport.close();

            FileInputStream baos = new FileInputStream(fileLocation);

            ThrashOldPdfs.checkToDeleteOldPdfs(Arrays.asList(fileLocation));

            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename="+reportTitle+".pdf");

             os = response.getOutputStream();

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = baos.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            os.flush();
            os.close();

        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }finally{
            if(os != null){
                os.close();

            }
        }
    }


    private void addTableHeaders(WageBeanContainer headers, Document myPdfReport, Font boldFont, PdfPTable reportTable) throws DocumentException {

        //create a cell object
        PdfPCell table_cell;

        table_cell = new PdfPCell(new Phrase("S/No", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Organization", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(headers.getMonthAndYearStr() + " Staff", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(headers.getPrevMonthAndYearStr() + " Staff", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(headers.getMonthAndYearStr(), boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(headers.getPrevMonthAndYearStr(), boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);
    }


    private void organizationDataList(WageBeanContainer data, Document myPdfReport, PdfPTable reportTable,
                                      Font normalFont, Font boldFont, BusinessCertificate bc) throws DocumentException {

        List<WageSummaryBean> pList = data.getWageSummaryBeanList();
        PdfPCell table_cell;
        BaseColor darkGrey = new BaseColor(169, 169, 169);

        int serialNo = 0;
        for (WageSummaryBean e : pList) {
            int i = 0;
            serialNo++;

            table_cell = new PdfPCell(new Phrase(serialNo+"", normalFont));
            //table_cell.addElement(E);
            table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(e.getAssignedToObject(), normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(e.getNoOfEmp()+"", normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(e.getPreviousNoOfEmp()+"", normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(e.getCurrentBalanceStr()+"", normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(e.getPreviousBalanceStr()+"", normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

        }

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Gross Pay", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalNoOfEmp()+"", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalNoOfPrevMonthEmp()+"", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalCurrBalStr()+"", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalPrevBalStr()+"", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        for(int i = 0; i<18; i++){
            table_cell = new PdfPCell(new Phrase("", boldFont));
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);
        }

        if(bc.isPensioner()) {
            paymentsDataList(data, myPdfReport, reportTable, normalFont, serialNo, boldFont);
        }
        else{
            contributionsData(data, myPdfReport, reportTable, normalFont, serialNo, boldFont);
        }
    }

    private void contributionsData(WageBeanContainer data, Document myPdfReport, PdfPTable reportTable, Font normalFont, int serialNo, Font boldFont) throws DocumentException {
        PdfPCell table_cell;
        BaseColor darkGrey = new BaseColor(169, 169, 169);

        table_cell = new PdfPCell(new Phrase("", normalFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        List<WageSummaryBean> pList = data.getContributionList();
        if(pList.size() > 0) {
            table_cell = new PdfPCell(new Phrase("Contributions", normalFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);


            for (int i = 0; i < 4; i++) {
                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table_cell.setBorder(Rectangle.NO_BORDER);
                reportTable.addCell(table_cell);
            }


            for (WageSummaryBean e : pList) {
                serialNo++;
                table_cell = new PdfPCell(new Phrase(serialNo + "", normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase(e.getName(), normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase(e.getCurrentBalanceStr(), normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase(e.getPreviousBalanceStr(), normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);
            }

            table_cell = new PdfPCell(new Phrase("", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("Total Contributions", boldFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(data.getTotalCurrContStr(), boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(data.getTotalPrevContStr(), boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            for (int i = 0; i < 18; i++) {
                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBorder(Rectangle.NO_BORDER);
                reportTable.addCell(table_cell);
            }
        }

        subventionsDataList(data, myPdfReport, reportTable, normalFont, serialNo, boldFont);
    }

    private void subventionsDataList(WageBeanContainer data, Document myPdfReport, PdfPTable reportTable, Font normalFont, int serialNo, Font boldFont) throws DocumentException {

        PdfPCell table_cell;
        List<WageSummaryBean> pSubList = data.getSubventionList();
        BaseColor darkGrey = new BaseColor(169, 169, 169);

        if (IppmsUtils.isNotNullAndGreaterThanZero(pSubList.size())) {
            table_cell = new PdfPCell(new Phrase("", normalFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("Recurrent Subventions", normalFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);


            for (int i = 0; i < 4; i++) {
                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table_cell.setBorder(Rectangle.NO_BORDER);
                reportTable.addCell(table_cell);
            }

            for (WageSummaryBean e : pSubList) {
                serialNo++;
                table_cell = new PdfPCell(new Phrase(serialNo + "", normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase(e.getName(), normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase(e.getCurrentBalanceStr(), normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase(e.getPreviousBalanceStr(), normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);
            }

            table_cell = new PdfPCell(new Phrase("", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("Total Subventions", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(data.getTotalSubBalStr(), boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(data.getTotalPrevSubBalStr(), boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            for (int i = 0; i < 18; i++) {
                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBorder(Rectangle.NO_BORDER);
                reportTable.addCell(table_cell);
            }
        }
        paymentsDataList(data, myPdfReport, reportTable, normalFont, serialNo, boldFont);
    }

    private void paymentsDataList(WageBeanContainer data, Document myPdfReport, PdfPTable reportTable, Font normalFont, int serialNo,Font boldFont) throws DocumentException {

        BaseColor darkGrey = new BaseColor(169, 169, 169);
        PdfPCell table_cell;

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Total Payments", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalCurrOutGoingStr(), boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalPrevOutGoingStr(), boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        for(int i = 0; i<18; i++){
            table_cell = new PdfPCell(new Phrase("", normalFont));
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);
        }

        deductionsDataList(data, myPdfReport, reportTable, normalFont, serialNo, boldFont);
    }

    private void deductionsDataList(WageBeanContainer data, Document myPdfReport, PdfPTable reportTable, Font normalFont, int serialNo, Font boldFont) throws DocumentException {
        PdfPCell table_cell;
        BaseColor darkGrey = new BaseColor(169, 169, 169);

        table_cell = new PdfPCell(new Phrase("", normalFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Deductions", normalFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        for (int i = 0; i<4; i++){
            table_cell = new PdfPCell(new Phrase("", normalFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);
        }

        List<WageSummaryBean> pGarnList = data.getDeductionList();
        for (WageSummaryBean e : pGarnList) {
            serialNo++;
            table_cell = new PdfPCell(new Phrase(serialNo+"", normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(e.getName(), normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("", normalFont));
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("", normalFont));
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(e.getCurrentBalanceStr(), normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(e.getPreviousBalanceStr(), normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);
        }

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Total Deductions", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalDedBalStr(), boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalPrevDedBalStr(), boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        for(int i = 0; i<18; i++){
            table_cell = new PdfPCell(new Phrase("", normalFont));
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);
        }

        netPayDataList(data, myPdfReport, reportTable, normalFont, serialNo, boldFont);
    }

    private void netPayDataList(WageBeanContainer data, Document myPdfReport, PdfPTable reportTable, Font normalFont, int serialNo, Font boldFont) throws DocumentException {

        PdfPCell table_cell;

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Net Pay (Gross Pay - Total Deductions)", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalNoOfEmp()+"", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getTotalNoOfPrevMonthEmp()+"", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getGrandTotalStr()+"", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.getGrandPrevTotalStr()+"", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        myPdfReport.add(reportTable);
    }



}
