package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
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
import java.util.Map;

public class StaffPayrollInfoPdfGenerator {

    public void generatePdf(HttpServletResponse response,
                            HttpServletRequest request, ReportGeneratorBean rt) throws IOException {

        LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
        Month currentMonth = currentdate.getMonth();
        int year = currentdate.getYear();
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String currentTime = dateFormat.format(new Date());
        Document myPdfReport = new Document(PageSize.LETTER);
        OutputStream os = null;
        try {
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            String fileLocation = path.substring(0, path.length() - 1) + rt.getReportTitle() + ".pdf";
            File currDir = new File(request.getServletContext().getRealPath(File.separator)+rt.getReportTitle()+".pdf");
            String fileLocation = currDir.getPath();
            //String fileLocation = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";

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
            //File imgDir = new File("src/main/resources/static/images/" + rt.getBusinessCertificate().getClientReportLogo());
//            File imgDir = ResourceUtils.getFile("classpath:static/images/"+rt.getBusinessCertificate().getClientReportLogo());
//            String imgPath = imgDir.getAbsolutePath();
//            Image img2 = Image.getInstance(imgPath);
//            img2.setAlignment(Element.ALIGN_CENTER);
//            img2.setWidthPercentage(120);
//            t_cell = new PdfPCell();
//            t_cell.setBorder(Rectangle.NO_BORDER);
//            t_cell.addElement(img2);
//            head.addCell(t_cell);
//            my_pdf_report.add(head);

            myPdfReport = PdfUtils.makeHeaderFile(rt.getBusinessCertificate(), new PdfPCell(), myPdfReport, head);


            //addding pdf Main Report Title
            Paragraph title = new Paragraph(rt.getReportTitle(), largeBold);
            title.setAlignment(Element.ALIGN_CENTER);
            myPdfReport.add(title);
            myPdfReport.add(new Paragraph("\n"));

            Paragraph time = new Paragraph("Print Date: " + currentDay + " " + currentMonth + ", " + year, smallBold);
            time.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time);

            Paragraph time2 = new Paragraph("Print Time:" + currentTime, smallBold);
            time2.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time2);
            myPdfReport.add(new Paragraph("\n"));

            //adding main headers

            PdfPTable reportTable = new PdfPTable(5);
            reportTable.setWidthPercentage(100);
            //adding table headers
            addMainHeader(rt.getSingleTableData(), myPdfReport, reportTable, normal, boldFont);

            staffPersonalInfoData(rt.getSingleTableData(), myPdfReport, reportTable, normal, boldFont);

            staffHiringAndPayData(rt.getSingleTableData(), myPdfReport, reportTable, normal, boldFont);

            staffOtherCategoriesData(rt.getSingleTableData(), myPdfReport, reportTable, normal, boldFont);

            onEndPage(pdfwriter, myPdfReport, rt.getWatermark());
            myPdfReport.close();


            FileInputStream baos = new FileInputStream(fileLocation);

            ThrashOldPdfs.checkToDeleteOldPdfs(Arrays.asList(fileLocation));

            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=" + rt.getReportTitle() + ".pdf");

            os = response.getOutputStream();

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = baos.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            os.flush();
            os.close();

        } catch (
                BadElementException e) {
            e.printStackTrace();
        } catch (
                DocumentException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                os.close();

            }
        }
    }

    private void onEndPage(PdfWriter pdfwriter, Document my_pdf_report, String watermark) {
        Font FONT = new Font(Font.FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.85f));
        ColumnText.showTextAligned(pdfwriter.getDirectContentUnder(),
                Element.ALIGN_CENTER, new Phrase(watermark, FONT),
                297.5f, 421, 45f);
    }

    private void addMainHeader(Map<String, Object> data, Document my_pdf_report, PdfPTable reportTable, Font normal, Font boldFont) throws DocumentException {

//        Paragraph eName = new Paragraph("Employee:       " + data.get("employeeID") +",   "+data.get("firstName") + ",   "+data.get("LastName"), normal);
//        eName.setAlignment(Element.ALIGN_LEFT);
//        my_pdf_report.add(eName);
//

        PdfPCell table_cell;
        PdfPTable newTable = new PdfPTable(4);
        newTable.setWidthPercentage(100);
        table_cell = new PdfPCell(new Phrase("Employee:       " + data.get("employeeID")+",   ", normal));
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setBackgroundColor(new BaseColor(204, 255, 204));
        newTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.get("firstName") + ",", normal));
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setBackgroundColor(new BaseColor(204, 255, 204));
        newTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.get("LastName") +", "+data.get("initials")+".", normal));
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setBackgroundColor(new BaseColor(204, 255, 204));
        newTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("     ", normal));
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setBackgroundColor(new BaseColor(204, 255, 204));
        newTable.addCell(table_cell);
        my_pdf_report.add(newTable);
        my_pdf_report.add(new Paragraph("\n"));

        Paragraph in1 = new Paragraph("Special Instructions to Follow:", boldFont);
        in1.setAlignment(Element.ALIGN_LEFT);
        my_pdf_report.add(in1);
        my_pdf_report.add(new Paragraph("\n"));

        Paragraph in2 = new Paragraph("1. Please read and confirm the information below.", normal);
        in2.setAlignment(Element.ALIGN_LEFT);
        my_pdf_report.add(in2);

        Paragraph in3 = new Paragraph("2. Please pay particular attention to your name, date of birth, date of employment, grade level step, bank and account number.", normal);
        in3.setAlignment(Element.ALIGN_LEFT);
        my_pdf_report.add(in3);

        Paragraph in4 = new Paragraph("3. Make any necessary correction to your form.", normal);
        in4.setAlignment(Element.ALIGN_LEFT);
        my_pdf_report.add(in4);
    }

    private void staffPersonalInfoData(Map<String, Object> data, Document my_pdf_report, PdfPTable reportTable, Font normalFont, Font boldFont) throws DocumentException {

            Paragraph hd = new Paragraph("PERSONAL INFORMATION", boldFont);
            hd.setAlignment(Element.ALIGN_CENTER);
            my_pdf_report.add(hd);

            PdfPCell table_cell;
            table_cell = new PdfPCell(new Phrase("Surname:", normalFont));
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(data.get("LastName")+"", normalFont));
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("   ", normalFont));
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("Other Names:", normalFont));
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(data.get("firstName")+"", normalFont));
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);


            PdfPCell table_cell2;
            table_cell2 = new PdfPCell(new Phrase("Maiden:", normalFont));
            table_cell2.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell2);

            if(data.get("maiden") != null) {
                table_cell2 = new PdfPCell(new Phrase(data.get("maiden") + "", normalFont));
            }
            else{
                table_cell2 = new PdfPCell(new Phrase( "", normalFont));
            }
            table_cell2.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell2);

            table_cell2 = new PdfPCell(new Phrase("   ", normalFont));
            table_cell2.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell2);

            table_cell2 = new PdfPCell(new Phrase("Title:", normalFont));
            table_cell2.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell2);

            table_cell2 = new PdfPCell(new Phrase(data.get("title")+"", normalFont));
            table_cell2.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell2);


            PdfPCell table_cell4;
            table_cell4 = new PdfPCell(new Phrase("Sex:", normalFont));
            table_cell4.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell4);

            table_cell4 = new PdfPCell(new Phrase(data.get("Sex")+"", normalFont));
            table_cell4.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell4);

            table_cell4 = new PdfPCell(new Phrase("   ", normalFont));
            table_cell4.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell4);


            table_cell4 = new PdfPCell(new Phrase("Marital Status:", normalFont));
            table_cell4.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell4);

            table_cell4 = new PdfPCell(new Phrase(data.get("maritalStatus")+"", normalFont));
            table_cell4.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell4);


            PdfPCell table_cell5;
            table_cell5 = new PdfPCell(new Phrase("Date Of Birth:", normalFont));
            table_cell5.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell5);

            table_cell5 = new PdfPCell(new Phrase(data.get("DateOfBirth")+"", normalFont));
            table_cell5.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell5);

            table_cell5 = new PdfPCell(new Phrase("   ", normalFont));
            table_cell5.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell5);


            table_cell5 = new PdfPCell(new Phrase("Local Govt Area:", normalFont));
            table_cell5.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell5);

            table_cell5 = new PdfPCell(new Phrase(data.get("LGA")+"", normalFont));
            table_cell5.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell5);

            PdfPCell table_cell6;
            table_cell6 = new PdfPCell(new Phrase("Address:", normalFont));
            table_cell6.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell6);

            table_cell6 = new PdfPCell(new Phrase(data.get("Address1")+"", normalFont));
            table_cell6.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell6);

            table_cell6 = new PdfPCell(new Phrase("   ", normalFont));
            table_cell6.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell6);


            table_cell6 = new PdfPCell(new Phrase("Nationality:", normalFont));
            table_cell6.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell6);

            if(data.get("Nationality") != null) {
                table_cell6 = new PdfPCell(new Phrase(data.get("Nationality") + "", normalFont));
            }
            else{
                table_cell6 = new PdfPCell(new Phrase( "", normalFont));
            }
            table_cell6.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell6);


            PdfPCell table_cell7;
            table_cell7 = new PdfPCell(new Phrase("State Of Origin:", normalFont));
            table_cell7.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell7);

            table_cell7 = new PdfPCell(new Phrase(data.get("State")+"", normalFont));
            table_cell7.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell7);

            table_cell7 = new PdfPCell(new Phrase("   ", normalFont));
            table_cell7.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell7);


        table_cell7 = new PdfPCell(new Phrase("Religion:", normalFont));
            table_cell7.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell7);

            table_cell7 = new PdfPCell(new Phrase(data.get("Religion")+"", normalFont));
            table_cell7.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell7);

            PdfPCell table_cell8;
            table_cell8 = new PdfPCell(new Phrase("Mobile:", normalFont));
            table_cell8.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell8);

            table_cell8 = new PdfPCell(new Phrase(data.get("mobile")+"", normalFont));
            table_cell8.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell8);

            table_cell8 = new PdfPCell(new Phrase("   ", normalFont));
            table_cell8.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell8);


        table_cell8 = new PdfPCell(new Phrase("     ", normalFont));
            table_cell8.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell8);

            table_cell8 = new PdfPCell(new Phrase("     ", normalFont));
            table_cell8.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell8);

            PdfPCell table_cell9;
            table_cell9 = new PdfPCell(new Phrase("E-mail:", normalFont));
            table_cell9.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell9);

            table_cell9 = new PdfPCell(new Phrase(data.get("email")+"", normalFont));
            table_cell9.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell9);

            table_cell9 = new PdfPCell(new Phrase("   ", normalFont));
            table_cell9.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell9);


        table_cell9 = new PdfPCell(new Phrase("     ", normalFont));
            table_cell9.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell9);

            table_cell9 = new PdfPCell(new Phrase("     ", normalFont));
            table_cell9.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell9);

        PdfPCell table_cell10;
        table_cell10 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell10.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell10);

        table_cell10 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell10.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell10);

        table_cell10 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell10.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell10);

        table_cell10 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell10.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell10);

        table_cell10 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell10.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell10);

    }

    private void staffHiringAndPayData(Map<String, Object> data, Document my_pdf_report, PdfPTable reportTable, Font normalFont, Font boldFont) throws DocumentException {


        PdfPCell table_cell;
        table_cell = new PdfPCell(new Phrase("   HIRING INFORMATION:", boldFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("     ", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("     ", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("      PAY INFORMATION:", boldFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);


        PdfPCell table_cell2;
        table_cell2 = new PdfPCell(new Phrase("Grade Level:", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase(data.get("GradeLevel")+"", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase("   ", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase("Annual Basic:", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);


        table_cell2 = new PdfPCell(new Phrase(data.get("AnnualBasic")+"", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);


        PdfPCell table_cell3;
        table_cell3 = new PdfPCell(new Phrase("Employment Date:", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase(data.get("EmploymentDate")+"", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase("   ", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase("Payment Method:", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase(data.get("PaymentMethod")+"", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);


        PdfPCell table_cell4;
        table_cell4 = new PdfPCell(new Phrase("Employment Status:", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase(data.get("EmploymentStatus")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase("   ", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase("Bank Branch:", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase(data.get("BankBranch")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);


        PdfPCell table_cell5;
        table_cell5 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase("Account Number:", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase(data.get("AccountNumber")+"", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        PdfPCell table_cell6;
        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

    }


    private void staffOtherCategoriesData(Map<String, Object> data, Document my_pdf_report, PdfPTable reportTable, Font normalFont, Font boldFont) throws DocumentException {


        PdfPCell table_cell;
        table_cell = new PdfPCell(new Phrase("OTHER CATEGORIES:", boldFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("     ", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("     ", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("     ", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("     ", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);


        PdfPCell table_cell2;
        table_cell2 = new PdfPCell(new Phrase("Agency:", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase(data.get("Agency")+"", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);


        PdfPCell table_cell3;
        table_cell3 = new PdfPCell(new Phrase("Section/School:", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        if(data.get("School") != null) {
            table_cell3 = new PdfPCell(new Phrase(data.get("School") + "", normalFont));
        }
        else{
            table_cell3 = new PdfPCell(new Phrase("", normalFont));
        }
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);


        PdfPCell table_cell4;
        table_cell4 = new PdfPCell(new Phrase("Confirmation Month:", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase(data.get("capturedMonth")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);


        PdfPCell table_cell5;
        table_cell5 = new PdfPCell(new Phrase("Employee Type:", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase(data.get("StaffCategory")+"", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);


        PdfPCell table_cell6;
        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        PdfPCell table_cell7;
        table_cell7 = new PdfPCell(new Phrase("PLEASE SIGN:   ", boldFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase("_________________", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

       my_pdf_report.add(reportTable);
    }



    }
