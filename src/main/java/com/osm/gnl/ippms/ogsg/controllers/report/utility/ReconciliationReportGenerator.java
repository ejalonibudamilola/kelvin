package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.Map;

public class ReconciliationReportGenerator {

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
            //String  = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";


            PdfWriter pdfwriter = PdfWriter.getInstance(myPdfReport, new FileOutputStream(fileLocation));
            myPdfReport.open();
            PdfFont bold = PdfFontFactory.createFont("Times-Bold");
//            PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, true);
            Font largeBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD);
            Font smallBold = new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 15f, Font.NORMAL);


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
            Paragraph title = new Paragraph("Reconciliation Report", largeBold);
            title.setAlignment(Element.ALIGN_CENTER);
            myPdfReport.add(title);
            myPdfReport.add(new Paragraph("\n"));

            Paragraph period = new Paragraph("Period: " + rt.getSingleTableData().get("Period"), boldFont);
            period.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(period);

            Paragraph time = new Paragraph("Print Date: " + currentDay + " " + currentMonth + ", " + year, boldFont);
            time.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time);

            Paragraph time2 = new Paragraph("Print Time:" + currentTime, boldFont);
            time2.setAlignment(Element.ALIGN_RIGHT);
            myPdfReport.add(time2);
            myPdfReport.add(new Paragraph("\n"));

            //adding main headers

            PdfPTable reportTable = new PdfPTable(4);
            reportTable.setWidthPercentage(100);
            //adding table headers
            addMainHeader(rt.getSingleTableData(), myPdfReport, reportTable, normal, boldFont);

            staffPersonalInfoData(rt.getSingleTableData(), myPdfReport, reportTable, normal, boldFont);

            staffHiringAndPayData(rt.getSingleTableData(), myPdfReport, reportTable, normal, boldFont);

            staffOtherCategoriesData(rt.getSingleTableData(), myPdfReport, reportTable, normal, boldFont);

            onEndPage(pdfwriter, myPdfReport, rt.getWatermark());
            myPdfReport.close();


            FileInputStream baos = new FileInputStream(fileLocation);

            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=" + rt.getReportTitle() + ".pdf");

//            if (IppmsUtils.isNull(rt.isOutputInd()) || rt.isOutputInd()) {
//                os = response.getOutputStream();
//
//                byte buffer[] = new byte[8192];
//                int bytesRead;
//
//                while ((bytesRead = baos.read(buffer)) != -1) {
//                    os.write(buffer, 0, bytesRead);
//                }
//
//                os.flush();
//                os.close();
//            }

            } catch(
                    DocumentException e){
                e.printStackTrace();
            } finally{
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
        table_cell = new PdfPCell(new Phrase("",   normal));
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setBackgroundColor(new BaseColor(204, 255, 204));
        newTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Last Period", normal));
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setBackgroundColor(new BaseColor(204, 255, 204));
        newTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Current Period", normal));
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setBackgroundColor(new BaseColor(204, 255, 204));
        newTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Difference", normal));
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setBackgroundColor(new BaseColor(204, 255, 204));
        newTable.addCell(table_cell);

        my_pdf_report.add(newTable);
        my_pdf_report.add(new Paragraph("\n"));
    }

    private void staffPersonalInfoData(Map<String, Object> data, Document my_pdf_report, PdfPTable reportTable, Font normalFont, Font boldFont) throws DocumentException {



        PdfPCell table_cell;
        table_cell = new PdfPCell(new Phrase("Staff Count:", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.get("PrevStaffCount")+"", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.get("CurrentStaffCount")+"", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.get("StaffCountDiff")+"", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);


        PdfPCell table_cell2;
        table_cell2 = new PdfPCell(new Phrase("Basic Salary:", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);


        table_cell2 = new PdfPCell(new Phrase(data.get("PrevBasicSalary")+"", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase(data.get("CurrentBasicSalary")+"", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase(data.get("BasicSalaryDiff")+"", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);


        PdfPCell table_cell4;
        table_cell4 = new PdfPCell(new Phrase("Total Allowance:", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase(data.get("PrevTotalAllowance")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase(data.get("CurrTotalAllowance")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);


        table_cell4 = new PdfPCell(new Phrase(data.get("TotalAllowanceDiff")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);


        PdfPCell table_cell5;
        table_cell5 = new PdfPCell(new Phrase("Gross Pay:", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase(data.get("PrevGrossPay")+"", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase(data.get("CurrGrossPay")+"", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);


        table_cell5 = new PdfPCell(new Phrase(data.get("GrossPayDiff")+"", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);


        PdfPCell table_cell6;
        table_cell6 = new PdfPCell(new Phrase("Tax:", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase(data.get("PrevTax")+"", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase(data.get("CurrTax")+"", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase(data.get("TaxDiff")+"", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);



        PdfPCell table_cell7;
        table_cell7 = new PdfPCell(new Phrase("Other Deductions:", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase(data.get("PrevOtherDeductions")+"", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase(data.get("CurrOtherDeductions")+"", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);


        table_cell7 = new PdfPCell(new Phrase(data.get("OtherDeductionsDiff")+"", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);


        PdfPCell table_cell8;
        table_cell8 = new PdfPCell(new Phrase("Total Deductions:", normalFont));
        table_cell8.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell8);

        table_cell8 = new PdfPCell(new Phrase(data.get("PrevTotalDeductions")+"", normalFont));
        table_cell8.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell8);

        table_cell8 = new PdfPCell(new Phrase(data.get("CurrTotalDeductions")+"", normalFont));
        table_cell8.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell8);


        table_cell8 = new PdfPCell(new Phrase(data.get("TotalDeductionsDiff")+"", normalFont));
        table_cell8.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell8);

        PdfPCell table_cell9;
        table_cell9 = new PdfPCell(new Phrase("Net Pay:", normalFont));
        table_cell9.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell9);

        table_cell9 = new PdfPCell(new Phrase(data.get("PrevNetPay")+"", normalFont));
        table_cell9.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell9);

        table_cell9 = new PdfPCell(new Phrase(data.get("CurrNetPay")+"", normalFont));
        table_cell9.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell9);


        table_cell9 = new PdfPCell(new Phrase(data.get("NetPayDiff")+"", normalFont));
        table_cell9.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell9);


    }

    private void staffHiringAndPayData(Map<String, Object> data, Document my_pdf_report, PdfPTable reportTable, Font normalFont, Font boldFont) throws DocumentException {


//        Paragraph period = new Paragraph("Reconciliation From Last Period", boldFont);
//        period.setAlignment(Element.ALIGN_RIGHT);
//        my_pdf_report.add(period);

        PdfPCell table_cell;
        table_cell = new PdfPCell(new Phrase("Last Paid Gross:", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("     ", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("     ", normalFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase(data.get("LastPaidGross")+"", boldFont));
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);


        PdfPCell table_cell2;
        table_cell2 = new PdfPCell(new Phrase("Increments(+):", boldFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase("Staff Count", boldFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase("Implication", boldFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase("      ", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);



        PdfPCell table_cell3;
        table_cell3 = new PdfPCell(new Phrase("New Staff:", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase(data.get("NewStaffCount")+"", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase(data.get("NewStaffGross")+"", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase("       ", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);



        PdfPCell table_cell4;
        table_cell4 = new PdfPCell(new Phrase("Promotion:", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase(data.get("PromotionCount")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase(data.get("PromotionGross")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase("      ", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);



        PdfPCell table_cell5;
        table_cell5 = new PdfPCell(new Phrase("Re-assignment:", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase(data.get("ReassignmentCount")+"", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase(data.get("ReassignmentGross")+"", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase("      ", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);


        PdfPCell table_cell6;
        table_cell6 = new PdfPCell(new Phrase("Re-instatement:", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase(data.get("ReinstatedCount")+"", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase(data.get("ReinstatedGross")+"", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        PdfPCell table_cell7;
        table_cell7 = new PdfPCell(new Phrase("Reabsorption:", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase(data.get("ReabsorptionCount")+"", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase(data.get("ReabsorptionGross")+"", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);


        PdfPCell table_cell8;
        table_cell8 = new PdfPCell(new Phrase("Special Allowance Increase:", normalFont));
        table_cell8.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell8);

        table_cell8 = new PdfPCell(new Phrase(data.get("SpecialAllowIncCount")+"", normalFont));
        table_cell8.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell8);

        table_cell8 = new PdfPCell(new Phrase(data.get("SpecialAllowGross")+"", normalFont));
        table_cell8.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell8);

        table_cell8 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell8.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell8);


        PdfPCell table_cell9;
        table_cell9 = new PdfPCell(new Phrase("Step Increment:", normalFont));
        table_cell9.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell9);

        table_cell9 = new PdfPCell(new Phrase(data.get("StepIncrementCount")+"", normalFont));
        table_cell9.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell9);

        table_cell9 = new PdfPCell(new Phrase(data.get("StepIncrementGross")+"", normalFont));
        table_cell9.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell9);

        table_cell9 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell9.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell9);


        PdfPCell table_cell10;
        table_cell10 = new PdfPCell(new Phrase("Sub Total:", normalFont));
        table_cell10.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell10);

        table_cell10 = new PdfPCell(new Phrase("       ", normalFont));
        table_cell10.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell10);

        table_cell10 = new PdfPCell(new Phrase("        ", normalFont));
        table_cell10.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell10);

        table_cell10 = new PdfPCell(new Phrase(data.get("SubTotal1")+"", normalFont));
        table_cell10.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell10);
    }


    private void staffOtherCategoriesData(Map<String, Object> data, Document my_pdf_report, PdfPTable reportTable, Font normalFont, Font boldFont) throws DocumentException {


        PdfPCell table_cell;
        table_cell = new PdfPCell(new Phrase("Decrements(-)", boldFont));
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
        table_cell2 = new PdfPCell(new Phrase("Current Month Retirement/Resignation:", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase(data.get("CurrRetirementCount")+"", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase(data.get("CurrRetirementGross")+"", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        table_cell2 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell2.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell2);

        PdfPCell table_cell3;
        table_cell3 = new PdfPCell(new Phrase("Previous Month Retirement/Resignation:", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);


        table_cell3 = new PdfPCell(new Phrase(data.get("PrevRetirementCount") + "", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase(data.get("PrevRetirementGross") + "", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);

        table_cell3 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell3.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell3);


        PdfPCell table_cell4;
        table_cell4 = new PdfPCell(new Phrase("Suspension/Interdiction:", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase(data.get("SuspendedCount")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase(data.get("SuspendedGross")+"", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);

        table_cell4 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell4.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell4);


        PdfPCell table_cell5;
        table_cell5 = new PdfPCell(new Phrase("Special Allowance Decrease:", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase(data.get("SpecialAllowDecCount")+"", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase(data.get("SpecialAllowDecGross")+"", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell5 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell5.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);


        PdfPCell table_cell6;
        table_cell6 = new PdfPCell(new Phrase("Sub Total:", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell5);

        table_cell6 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        table_cell6 = new PdfPCell(new Phrase(data.get("SubTotal2")+"", normalFont));
        table_cell6.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell6);

        PdfPCell table_cell7;
        table_cell7 = new PdfPCell(new Phrase("Current Period Gross:", boldFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);


        table_cell7 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase("     ", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        table_cell7 = new PdfPCell(new Phrase(data.get("currPeriodGross")+"", normalFont));
        table_cell7.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell7);

        my_pdf_report.add(reportTable);
    }


}
