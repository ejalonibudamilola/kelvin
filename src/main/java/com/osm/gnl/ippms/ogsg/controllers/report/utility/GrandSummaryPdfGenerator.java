package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.utils.ThrashOldPdfs;
import org.springframework.stereotype.Controller;

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

@Controller
public class GrandSummaryPdfGenerator extends BaseController {


    public void getPdf(HttpServletResponse response, HttpServletRequest request, ReportGeneratorBean rt) throws IOException {



        int currentDay = LocalDate.now().getDayOfMonth();
        Month currentMonth = LocalDate.now().getMonth();

        List<String> files = new ArrayList<>();

        String currentTime = DateTimeFormatter.ofPattern("hh.mm a").format(LocalTime.now());

        int year = LocalDate.now().getYear();

        Document myPdfReport;
            myPdfReport = new Document(PageSize.A2);
        try {
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            String fileLocation = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";

            File currDir = new File(request.getServletContext().getRealPath(File.separator)+rt.getReportTitle()+".pdf");
            String fileLocation = currDir.getPath();
            //String fileLocation = path.substring(0, path.length() - 1) +rt.getReportTitle()+".pdf";


            PdfWriter pdfwriter = PdfWriter.getInstance(myPdfReport, new FileOutputStream(fileLocation));
            myPdfReport.open();
            PdfFont bold = PdfFontFactory.createFont("Times-Bold");
//            PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, true);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD);
            Font smallBold = new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 15f, Font.NORMAL);


            PdfPTable head = new PdfPTable(1);
            PdfPCell t_cell;

            //adding pdf Header
            //File imgDir = new File("src/main/resources/static/images/"+rt.getBusinessCertificate().getClientReportLogo());
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

            addTableHeaders(myPdfReport, boldFont);
            ungroupedData(rt.getEmployeePayBean(), rt.getDeductionDetailsBean(), myPdfReport, normal, boldFont, rt.getTotalInd());

            ungroupedData2(rt.getEmployeePayBean(), rt.getDeductionDetailsBean(), myPdfReport, normal, boldFont, rt.getTotalInd());

            //Attach report table to PDF /
            onEndPage(pdfwriter, myPdfReport, rt.getWatermark());

            myPdfReport.close();

            FileInputStream baos = new FileInputStream(fileLocation);



            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename="+rt.getReportTitle()+".pdf");

            if(IppmsUtils.isNull(rt.isOutputInd()) || rt.isOutputInd()) {
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
                }finally {
                    ThrashOldPdfs.checkToDeleteOldPdfs(Arrays.asList(fileLocation));
                }
            }

        } catch (IOException | DocumentException ex) {
                ex.printStackTrace();
        }

    }

    private void addMainHeaders(List<String> mainHeaders, Document myPdfReport, Font font) throws DocumentException {

        for (String header : mainHeaders){
            Paragraph value = new Paragraph(header.toUpperCase(), font);
            value.setAlignment(Element.ALIGN_LEFT);
            myPdfReport.add(value);
            myPdfReport.add(new Paragraph("\n"));
        }
    }

    private void addTableHeaders( Document myPdfReport, Font bold) throws DocumentException {

        float[] columnWidth = {1, 3, 2,2};
        PdfPTable myReportTable = new PdfPTable(columnWidth);

        myReportTable.setWidthPercentage(100);
        //create a cell object
        PdfPCell tableCell;

        tableCell = new PdfPCell(new Phrase("S/No", bold));
        tableCell.setBorder(Rectangle.NO_BORDER);
        tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
        myReportTable.addCell(tableCell);
        //add headers
        int i = 0;
                String field = "Earnings";
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setBorder(Rectangle.NO_BORDER);
                myReportTable.addCell(tableCell);

                tableCell = new PdfPCell(new Phrase(""));
                tableCell.setBorder(Rectangle.NO_BORDER);
                myReportTable.addCell(tableCell);

                tableCell = new PdfPCell(new Phrase(""));
                tableCell.setBorder(Rectangle.NO_BORDER);
                myReportTable.addCell(tableCell);


        myPdfReport.add(myReportTable);
    }


    private void ungroupedData(EmployeePayBean data, DeductionDetailsBean miniBean, Document myPdfReport, Font normal, Font bold, int totalInd) throws DocumentException {
        // TODO Auto-generated method stub
        float[] columnWidth = {1, 3, 2,2};
        PdfPTable myReportTable = new PdfPTable(columnWidth);
        myReportTable.setWidthPercentage(100);
//        PdfPCell tableCell = new PdfPCell();
        int i = 1;
            PdfPCell tableCell = new PdfPCell(new Phrase(1 +"", normal));
            tableCell.setBorder(Rectangle.NO_BORDER);
            myReportTable.addCell(tableCell);

            tableCell = new PdfPCell(new Phrase("Monthly Pay", normal));
            tableCell.setBorder(Rectangle.NO_BORDER);
            myReportTable.addCell(tableCell);

            Double temp = data.getMonthlyBasic();
            String newTotal =  PayrollHRUtils.getDecimalFormat().format(temp);
            tableCell = new PdfPCell(new Phrase(newTotal, normal));
            tableCell.setBorder(Rectangle.NO_BORDER);
            myReportTable.addCell(tableCell);

            tableCell = new PdfPCell(new Phrase("", normal));
            tableCell.setBorder(Rectangle.NO_BORDER);
            myReportTable.addCell(tableCell);

        PdfPCell tableCell1 = new PdfPCell(new Phrase(2 +"", normal));
        tableCell1.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell1);

        tableCell1 = new PdfPCell(new Phrase("Arrears", normal));
        tableCell1.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell1);

        Double temp1 = data.getArrears();
        String newTotal1 =  PayrollHRUtils.getDecimalFormat().format(temp1);
        tableCell1 = new PdfPCell(new Phrase(newTotal1, normal));
        tableCell1.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell1);

        tableCell1 = new PdfPCell(new Phrase("", normal));
        tableCell1.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell1);

        PdfPCell tableCell2 = new PdfPCell(new Phrase(3 +"", normal));
        tableCell2.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell2);

        tableCell2 = new PdfPCell(new Phrase("Accrued Arrears", normal));
        tableCell2.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell2);

        Double temp2 = data.getOtherArrears();
        String newTotal2 =  PayrollHRUtils.getDecimalFormat().format(temp2);
        tableCell2 = new PdfPCell(new Phrase(newTotal2, normal));
        tableCell2.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell2);

        tableCell2 = new PdfPCell(new Phrase("", normal));
        tableCell2.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell2);


        PdfPCell tableCell3 = new PdfPCell(new Phrase("", normal));
        tableCell3.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell3);

        tableCell3 = new PdfPCell(new Phrase("Total Gross Pay", bold));
        tableCell3.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell3);

        tableCell3 = new PdfPCell(new Phrase("", normal));
        tableCell3.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell3);

        temp2 = miniBean.getTotalGross();
        newTotal2 =  PayrollHRUtils.getDecimalFormat().format(temp2);
        tableCell3 = new PdfPCell(new Phrase(newTotal2, bold));
        tableCell3.setBorder(Rectangle.TOP);
        myReportTable.addCell(tableCell3);

        tableCell3 = new PdfPCell(new Phrase("", normal));
        tableCell3.setBorder(Rectangle.NO_BORDER);
        myReportTable.addCell(tableCell3);


        myPdfReport.add(myReportTable);
    }


    private void ungroupedData2(EmployeePayBean data, DeductionDetailsBean miniBean, Document myPdfReport, Font normal, Font bold, int totalInd) throws DocumentException {
        // TODO Auto-generated method stub

        float[] columnWidth1 = {1, 3, 2,2};
        PdfPTable myReportTable1 = new PdfPTable(columnWidth1);
        myReportTable1.setWidthPercentage(100);
//        PdfPCell tableCell = new PdfPCell();
        int i = 1;

        PdfPCell tableCell = new PdfPCell(new Phrase("", bold));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        tableCell = new PdfPCell(new Phrase("", bold));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        tableCell = new PdfPCell(new Phrase("", normal));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        tableCell = new PdfPCell(new Phrase("", normal));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        tableCell = new PdfPCell(new Phrase("", bold));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        tableCell = new PdfPCell(new Phrase("Less Following Deductions", bold));
        tableCell.setBorder(Rectangle.BOTTOM);
        myReportTable1.addCell(tableCell);

        tableCell = new PdfPCell(new Phrase("", normal));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        tableCell = new PdfPCell(new Phrase("", normal));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);



        for(DeductGarnMiniBean m : miniBean.getDeductionMiniBean()) {
            tableCell = new PdfPCell(new Phrase(i++ +"", normal));
            tableCell.setBorder(Rectangle.NO_BORDER);
            myReportTable1.addCell(tableCell);

            tableCell = new PdfPCell(new Phrase(m.getName(), normal));
            tableCell.setBorder(Rectangle.NO_BORDER);
            myReportTable1.addCell(tableCell);

            Double temp = m.getAmount();
            String newTotal = PayrollHRUtils.getDecimalFormat().format(temp);
            tableCell = new PdfPCell(new Phrase(newTotal, normal));
            tableCell.setBorder(Rectangle.NO_BORDER);
            myReportTable1.addCell(tableCell);

            tableCell = new PdfPCell(new Phrase("", normal));
            tableCell.setBorder(Rectangle.NO_BORDER);
            myReportTable1.addCell(tableCell);
        }

        tableCell = new PdfPCell(new Phrase("", normal));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        tableCell = new PdfPCell(new Phrase("Total Deduction", bold));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        tableCell = new PdfPCell(new Phrase("", normal));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        Double temp = miniBean.getTotalCurrentDeduction();
        String newTotal = PayrollHRUtils.getDecimalFormat().format(temp);
        tableCell = new PdfPCell(new Phrase(newTotal, bold));
        tableCell.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell);

        PdfPCell tableCell1 = new PdfPCell(new Phrase("", normal));
        tableCell1.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell1);

        tableCell1 = new PdfPCell(new Phrase("Net Pay", bold));
        tableCell1.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell1);

        tableCell1 = new PdfPCell(new Phrase("", normal));
        tableCell1.setBorder(Rectangle.NO_BORDER);
        myReportTable1.addCell(tableCell1);

        temp = miniBean.getNetPay();
        newTotal = PayrollHRUtils.getDecimalFormat().format(temp);
        tableCell1 = new PdfPCell(new Phrase(newTotal, bold));
        tableCell1.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        myReportTable1.addCell(tableCell1);


        myPdfReport.add(myReportTable1);
    }


    private void onEndPage(PdfWriter writer, Document document, String watermark) {
        Font FONT = new Font(Font.FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.85f));
        ColumnText.showTextAligned(writer.getDirectContentUnder(),
                Element.ALIGN_CENTER, new Phrase(watermark, FONT),
                297.5f, 421, 45f);
    }

}
