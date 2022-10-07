package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationInfoContainer;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationInfoSummaryBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationMiniBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
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

public class ExecutivePayrollSummaryPdfGenerator {

    private Document myPdfReport;
    private Font largeBold;
    private Font boldFont;
    private Font smallBold;
    private Font normal;
    private Font headingFont;
    private String fileLocation;
    private String path;


    public void generatePdf(SimulationInfoContainer data, String reportTitle, HttpServletResponse response,
                            HttpServletRequest request, BusinessCertificate bc) throws IOException {

        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        Month currentMonth = currentDate.getMonth();
        int year = currentDate.getYear();
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String currentTime = dateFormat.format(new Date());


        myPdfReport = new Document(PageSize.LETTER);
        init();
        OutputStream os = null;
        try {
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            String fileLocation = path.substring(0, path.length() - 1) + reportTitle + ".pdf";


            File currDir = new File(request.getServletContext().getRealPath(File.separator)+reportTitle+".pdf");
            String fileLocation = currDir.getPath();
            //String fileLocation = path.substring(0, path.length() - 1) +reportTitle+".pdf";

            PdfWriter pdfwriter = PdfWriter.getInstance(myPdfReport, new FileOutputStream(fileLocation));
            myPdfReport.open();


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

            PdfPTable reportTable = new PdfPTable(getFloat(data));
            reportTable.setWidthPercentage(100);
            //adding table headers
            addTableHeaders(data, myPdfReport, boldFont, reportTable);

            organizationDataList(data, myPdfReport, reportTable, normal, boldFont, bc);
            myPdfReport.close();

            FileInputStream baos = new FileInputStream(fileLocation);


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
            ThrashOldPdfs.checkToDeleteOldPdfs(Arrays.asList(fileLocation));
        }
    }

    private void init() {

        //ThrashOldPdfs.checkToDeleteOldPdfs(request);

        largeBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        boldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
        smallBold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
        normal = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
        headingFont = new Font(Font.FontFamily.HELVETICA, 14);
    }


    private void addTableHeaders(SimulationInfoContainer headers, Document myPdfReport, Font boldFont, PdfPTable reportTable) throws DocumentException {

        //create a cell object
        PdfPCell table_cell;

        table_cell = new PdfPCell(new Phrase("S/No", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Organization", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        for (SimulationMiniBean e : headers.getHeaderList()) {
            table_cell = new PdfPCell(new Phrase(e.getName(), boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);
        }

    }


    private void organizationDataList(SimulationInfoContainer data, Document myPdfReport, PdfPTable reportTable,
                                      Font normalFont, Font boldFont, BusinessCertificate bc) throws DocumentException {

        List<SimulationInfoSummaryBean> pList = data.getSummaryBean();
        PdfPCell table_cell;
        BaseColor darkGrey = new BaseColor(169, 169, 169);

        int serialNo = 0;
        for (SimulationInfoSummaryBean e : pList) {
            serialNo++;

            table_cell = new PdfPCell(new Phrase(serialNo+"", normalFont));
            //table_cell.addElement(E);
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase(e.getAssignedToObject(), normalFont));
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorderColor(darkGrey);
            reportTable.addCell(table_cell);


            for(SimulationMiniBean p : e.getMiniBeanList()){
                table_cell = new PdfPCell(new Phrase(p.getCurrentValueStr()+"", normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);
            }
        }

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Sub Totals", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        for(SimulationMiniBean p : data.getMdapFooterList()){
            table_cell = new PdfPCell(new Phrase(p.getCurrentValueStr()+"", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);
        }

        for(int i = 0; i<18; i++){
            table_cell = new PdfPCell(new Phrase("", boldFont));
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);
        }


            contributionsData(data, myPdfReport, reportTable, normalFont, serialNo, boldFont);

    }

    private void contributionsData(SimulationInfoContainer data, Document myPdfReport, PdfPTable reportTable, Font normalFont, int serialNo, Font boldFont) throws DocumentException {
        PdfPCell table_cell;
        BaseColor darkGrey = new BaseColor(169, 169, 169);

        table_cell = new PdfPCell(new Phrase("", normalFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        List<SimulationInfoSummaryBean> pList = data.getContributionsList();
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


            List<SimulationInfoSummaryBean> pContList = data.getContributionsList();
            for (SimulationInfoSummaryBean e : pContList) {
                serialNo++;
                table_cell = new PdfPCell(new Phrase(serialNo + "", normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase(e.getName(), normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                for(SimulationMiniBean p : e.getMiniBeanList()){
                    table_cell = new PdfPCell(new Phrase(p.getCurrentValueStr(), normalFont));
                    table_cell.setBorderColor(darkGrey);
                    reportTable.addCell(table_cell);
                }
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

            for(SimulationMiniBean e : data.getContributionsTotals()){
                table_cell = new PdfPCell(new Phrase(e.getCurrentValueStr(), boldFont));
                table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table_cell.setBorder(Rectangle.NO_BORDER);
                reportTable.addCell(table_cell);
            }

            for (int i = 0; i < 18; i++) {
                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBorder(Rectangle.NO_BORDER);
                reportTable.addCell(table_cell);
            }
        }

        deductionsList(data, myPdfReport, reportTable, normalFont, serialNo, boldFont);
    }

    private void deductionsList(SimulationInfoContainer data, Document myPdfReport, PdfPTable reportTable, Font normalFont, int serialNo, Font boldFont) throws DocumentException {

        PdfPCell table_cell;
        java.util.List<SimulationInfoSummaryBean> pSubList = data.getDeductionsList();
        BaseColor darkGrey = new BaseColor(169, 169, 169);

        if (IppmsUtils.isNotNullAndGreaterThanZero(pSubList.size())) {
            table_cell = new PdfPCell(new Phrase("", normalFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("Deductions", normalFont));
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

            for (SimulationInfoSummaryBean e : pSubList) {
                serialNo++;
                table_cell = new PdfPCell(new Phrase(serialNo + "", normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                table_cell = new PdfPCell(new Phrase(e.getName(), normalFont));
                table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_cell.setBorderColor(darkGrey);
                reportTable.addCell(table_cell);

                for (SimulationMiniBean p : e.getMiniBeanList()){
                    table_cell = new PdfPCell(new Phrase(p.getCurrentValueStr(), normalFont));
                    table_cell.setBorderColor(darkGrey);
                    reportTable.addCell(table_cell);
                }
            }

            table_cell = new PdfPCell(new Phrase("", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            table_cell = new PdfPCell(new Phrase("Sub-Totals", boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);

            for(SimulationMiniBean e : data.getDeductionsTotals()){
                table_cell = new PdfPCell(new Phrase(e.getCurrentValueStr(), boldFont));
                table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table_cell.setBorder(Rectangle.NO_BORDER);
                reportTable.addCell(table_cell);
            }


            for (int i = 0; i < 18; i++) {
                table_cell = new PdfPCell(new Phrase("", normalFont));
                table_cell.setBorder(Rectangle.NO_BORDER);
                reportTable.addCell(table_cell);
            }
        }
        grandTotals(data, myPdfReport, reportTable, normalFont, serialNo, boldFont);
    }

    private void grandTotals(SimulationInfoContainer data, Document myPdfReport, PdfPTable reportTable, Font normalFont, int serialNo,Font boldFont) throws DocumentException {

        BaseColor darkGrey = new BaseColor(169, 169, 169);
        PdfPCell table_cell;

        table_cell = new PdfPCell(new Phrase("", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        table_cell = new PdfPCell(new Phrase("Grand Totals", boldFont));
        table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell.setBorder(Rectangle.NO_BORDER);
        reportTable.addCell(table_cell);

        for(SimulationMiniBean e : data.getFooterList()){
            table_cell = new PdfPCell(new Phrase(e.getCurrentValueStr(), boldFont));
            table_cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table_cell.setBorder(Rectangle.NO_BORDER);
            reportTable.addCell(table_cell);
        }
        myPdfReport.add(reportTable);
    }

    private float[] getFloat(SimulationInfoContainer data){
        int size = data.getHeaderList().size();
        float[] columnWidth = null;
        if(size == 1){
            columnWidth = new float[]{1, 3, 2};
        } else if (size == 2) {
            columnWidth = new float[]{1, 3, 2, 2};
        }
        else if (size == 3) {
            columnWidth = new float[]{1, 3, 2, 2, 2};
        }
        else if (size == 4) {
            columnWidth = new float[]{1, 3, 2, 2, 2, 2};
        }
        else if (size == 5) {
            columnWidth = new float[]{1, 3, 2, 2, 2, 2, 2};
        }
        else if (size == 6) {
            columnWidth = new float[]{1, 3, 2, 2, 2, 2, 2, 2};
        }
        else if (size == 7) {
            columnWidth = new float[]{1, 3, 2, 2, 2, 2, 2, 2, 2};
        }
        else if (size == 8) {
            columnWidth = new float[]{1, 3, 2, 2, 2, 2, 2, 2, 2, 2};
        }
        else if (size == 9) {
            columnWidth = new float[]{1, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        }
        else if (size == 10) {
            columnWidth = new float[]{1, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        }
        else if (size == 11) {
            columnWidth = new float[]{1, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        }
        else if (size == 12) {
            columnWidth = new float[]{1, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        }
        return columnWidth;
    }
}
