package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.utils.ThrashOldPdfs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class CustomPdfReportGenerator extends BaseController implements Runnable {

    List<String> tableValues = new ArrayList<>();
    List<String> tableValues_sum = new ArrayList<>();

    private int fCurrentPercentage;
    private Long parentId;
    int wListSize;
    boolean fStop;
    int wBatchSize;
    private String fileLocation;
    private final List<FuturePaycheckBean> wSaveList;
    private long startTime;
    private long timePerBatch;
    private int fRemainingRecords;
    private int fListSize;
    private final ReportGeneratorBean reportGeneratorBean;
    private HttpServletResponse response;
    private HttpServletRequest request;

    public CustomPdfReportGenerator(ReportGeneratorBean rGB, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, int totalListSize) {
        this.request = httpServletRequest;
        this.response = httpServletResponse;
        this.reportGeneratorBean = rGB;
        this.wBatchSize = 200;
        this.fStop = false;
        this.wSaveList = new ArrayList();
        this.wListSize = totalListSize;
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();

        LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
        Month currentMonth = currentdate.getMonth();

        List<String> files = new ArrayList<>();


        String currentTime = DateTimeFormatter.ofPattern("hh.mm a").format(LocalTime.now());
        int year = currentdate.getYear();

        Document myPdfReport;
        if(reportGeneratorBean.getTableHeaders().size() > 5){
            myPdfReport = new Document(PageSize.A2);
        }
        else {
            myPdfReport = new Document(PageSize.LETTER);
        }
        try {
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            fileLocation = path.substring(0, path.length() - 1) +reportGeneratorBean.getReportTitle()+".pdf";

            File currDir = new File(request.getServletContext().getRealPath(File.separator)+reportGeneratorBean.getReportTitle()+".pdf");
            String fileLocation = currDir.getPath();
           // String fileLocation = path.substring(0, path.length() - 1) +reportGeneratorBean.getReportTitle()+".pdf";


            PdfWriter pdfwriter = PdfWriter.getInstance(myPdfReport, new FileOutputStream(fileLocation));
            myPdfReport.open();

            Font boldFont = new Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD);
            Font smallBold = new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 15f, Font.NORMAL);


            PdfPTable head = new PdfPTable(1);
            PdfPCell t_cell;

            //adding pdf Header
//            File imgDir = new File("src/main/resources/static/images/"+reportGeneratorBean.getBusinessCertificate().getClientReportLogo());
//            String imgPath = imgDir.getAbsolutePath();
//            Image img2 = Image.getInstance(imgPath);
//            img2.setAlignment(Element.ALIGN_CENTER);
//            img2.setWidthPercentage(120);
//            t_cell = new PdfPCell();
//            t_cell.setBorder(Rectangle.NO_BORDER);
//            t_cell.addElement(img2);
//            head.addCell(t_cell);
//            myPdfReport.add(head);

            myPdfReport = PdfUtils.makeHeaderFile(reportGeneratorBean.getBusinessCertificate(), new PdfPCell(), myPdfReport, head);


            //addding pdf Main Report Title

            //adding main headers
            addMainHeaders(reportGeneratorBean.getMainHeaders(), myPdfReport, boldFont);

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
            if(reportGeneratorBean.getTableType() == 0) {
                addTableHeaders(reportGeneratorBean.getTableHeaders(), myPdfReport, boldFont, true);
                ungroupedData(reportGeneratorBean.getTableData(), reportGeneratorBean.getTableHeaders(), myPdfReport, normal, boldFont, reportGeneratorBean.getTotalInd());
            }
            else if (reportGeneratorBean.getTableType() == 1){
                //grouping table data before adding cells
                addTableHeaders(reportGeneratorBean.getTableHeaders(), myPdfReport, boldFont, false);
                groupData(reportGeneratorBean.getTableData(), reportGeneratorBean.getTableHeaders(), newTable, myPdfReport, normal, boldFont, reportGeneratorBean.getGroupBy(), reportGeneratorBean.getTotalInd());
            }
            else if (reportGeneratorBean.getTableType() == 2){
//                subGroupData(rt.getTableData(), rt.getTableHeaders(), rt.getReportTitle(), rt.getGroupBy(), rt.getSubGroupBy());
            }
            //Attach report table to PDF /
            onEndPage(pdfwriter, myPdfReport, reportGeneratorBean.getWatermark());

            myPdfReport.close();


            ThrashOldPdfs.checkToDeleteOldPdfs(Arrays.asList(fileLocation));





        } catch (IOException | DocumentException ex) {
            ex.printStackTrace();
        }
//        os.close();
    }



    /**
     * Gets the remaining time for payroll to run fully.
     *
     * @return Time in years, months, days, hours, minutes, seconds
     */
    public String getTimeToElapse() {
        String wRetVal = "";
        if (fCurrentPercentage == -1)
            return wRetVal;
        if (fCurrentPercentage == 0)
            return wRetVal;

        Date currDate = new Date();
        long timeRemainingMillis = (this.getfRemainingRecords() / this.wBatchSize) * this.getTimePerBatch();
        //System.out.println("Time Per Batch: " + this.getTimePerBatch() + " Remaining Records: " + this.getfRemainingRecords() + " Time Remaining Millis: " + timeRemainingMillis);
        Date endDate = new Date(currDate.getTime() + timeRemainingMillis);

        wRetVal = PayrollBeanUtils.getMeasuredTimeFromDates(currDate, endDate);


        return wRetVal;


    }


    private boolean getState() {
        return this.fStop;
    }

    public String getFileLocation(){
        return  this.fileLocation;
    }

    public String getTitle(){
        return this.reportGeneratorBean.getReportTitle()+".pdf";
    }

    public int getPercentage() {
        if (fCurrentPercentage == -1)
            return 100;
        if (fCurrentPercentage == 0)
            return fCurrentPercentage;
        double wPercent = (((double) fCurrentPercentage / this.getTotalRecords())) * 100;
        int wRetVal = EntityUtils.convertDoubleToEpmStandardZeroDecimal(wPercent);
        return wRetVal;
    }

    public int getfRemainingRecords() {
        fRemainingRecords = this.getTotalRecords() - this.getCurrentRecord();
        return fRemainingRecords;
    }

    public void stop(boolean pStop) {
        this.fStop = pStop;
    }

    public int getCurrentRecord() {
        return this.fCurrentPercentage;
    }

    public int getTotalRecords() {
        return this.wListSize;
    }

    public boolean isFinished() {
        return (this.fCurrentPercentage == this.wListSize) || (this.fCurrentPercentage == -1);
    }






    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long pParentId) {
        this.parentId = pParentId;
    }

    public long getTimePerBatch() {
        return timePerBatch;
    }

    public void setTimePerBatch(long timePerBatch) {
        this.timePerBatch = timePerBatch;
    }

    public void setfRemainingRecords(int fRemainingRecords) {
        this.fRemainingRecords = fRemainingRecords;
    }





    private void addTableHeaders(List<Map<String, Object>> tableHeaders, Document myPdfReport, Font bold, Boolean addOne) throws DocumentException {
        PdfPTable myReportTable;

        PdfPTable outerTable = new PdfPTable(1);


        if (addOne == true) {
            myReportTable = new PdfPTable(tableHeaders.size() + 1);
        }
        else{
            myReportTable = new PdfPTable(tableHeaders.size());
        }
        myReportTable.setWidthPercentage(100);
        //create a cell object
        PdfPCell tableCell;

        tableCell = new PdfPCell(new Phrase("S/No", bold));
        tableCell.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
        tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
        myReportTable.addCell(tableCell);
        //add headers
        int i = 0;
        for (Map<String, Object> str : tableHeaders) {
            i++;
            int totalList = tableHeaders.size();
            if(str.get("totalInd").toString().equals("3")){
//                tableCell = new PdfPCell(new Phrase(""));
//                tableCell.setBorder(Rectangle.NO_BORDER);
//                myReportTable.addCell(tableCell);
            }
            else if(str.get("totalInd").toString().equals("1")){
                String field = str.get("headerName").toString();
                tableCell = new PdfPCell(new Phrase(field, bold));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(i == totalList){
                    tableCell.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP);
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
                if(i == totalList){
                    tableCell.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP);
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
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(i == totalList){
                    tableCell.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP);
                }
                else{
                    tableCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                }
                myReportTable.addCell(tableCell);
            }

        }
//        PdfPCell cell = new PdfPCell();
//        cell.addElement(myReportTable);
//        outerTable.addCell(cell);

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
        boolean firstBatch = false;
        int j=0;
        for (Map<String, Object> row : data) {
            j++;
            String dump_value = "", fiveSpaces = "     ";
            String tableValue = row.get(groupBy).toString();
            boolean test_existence = test(tableValue);
            if(!test_existence) {
                dump_value = "yes";
                String text = "Grouping By ";
                Paragraph group_header = new Paragraph(tableValue, boldFont);
                group_header.setAlignment(Element.ALIGN_LEFT);
                myPdfReport.add(group_header);

                Paragraph spaces = new Paragraph(fiveSpaces);
                myPdfReport.add(spaces);


                newTable = new PdfPTable(headers.size());
                newTable.setWidthPercentage(100);
                groupRows(data, headers, groupBy, newTable, tableValue, myPdfReport, normal);
            }
            else {
                dump_value = "no";
            }
            fListSize = j;

            if ((j >= this.wBatchSize) || (j == this.wListSize - 1)) {
                if (!firstBatch)
                    this.timePerBatch = System.currentTimeMillis() - this.startTime;
                this.startTime = System.currentTimeMillis();
                firstBatch = true;
            }
            this.fCurrentPercentage += 1;
        }

        if(totalInd == 1)
            sumRowCells(data, headers, newTable, myPdfReport, boldFont);
    }

    private void ungroupedData(List<Map<String, Object>> data, List<Map<String, Object>> headers, Document myPdfReport, Font normal, Font bold, int totalInd) throws DocumentException {
        // TODO Auto-generated method stub
        boolean firstBatch = false;
        PdfPTable myReportTable = new PdfPTable(headers.size()+1);
        myReportTable.setWidthPercentage(100);
//        PdfPCell tableCell = new PdfPCell();
        int i = 1;
        int j = 0;
        for (Map<String, Object> row : data) {
            j++;
            PdfPCell tableCell1 = new PdfPCell(new Phrase(i++ +"", normal));
            myReportTable.addCell(tableCell1);

            for (Map<String, Object> str : headers) {
                if(row.get(str.get("headerName").toString()) != null) {
                    if (str.get("totalInd").toString().equals("1")) {
                        String num_val = row.get(str.get("headerName").toString()).toString();
                        PdfPCell tableCell = new PdfPCell(new Phrase(num_val, normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        myReportTable.addCell(tableCell);
                    } else if (str.get("totalInd").toString().equals("2")) {
                        String num_val = row.get(str.get("headerName").toString()).toString();
                        Double val = Double.valueOf(num_val);
                        PdfPCell tableCell = new PdfPCell(new Paragraph(PayrollHRUtils.getDecimalFormat().format(val), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        myReportTable.addCell(tableCell);

                    } else {
                        PdfPCell tableCell = new PdfPCell(new Phrase(row.get(str.get("headerName").toString()).toString(), normal));
                        myReportTable.addCell(tableCell);
                    }
//                    PdfPCell tableCell = new PdfPCell(new Phrase("theyey", boldFont));
//                    tableCell.setBorder(Rectangle.NO_BORDER);
//                    myReportTable.addCell(tableCell);

                }
                else{
                    PdfPCell tableCell = new PdfPCell(new Phrase("", normal));
                    myReportTable.addCell(tableCell);
                }
            }
            fListSize = j;

            if ((j >= this.wBatchSize) || (j == this.wListSize - 1)) {
                if (!firstBatch)
                    this.timePerBatch = System.currentTimeMillis() - this.startTime;
                this.startTime = System.currentTimeMillis();
                firstBatch = true;
            }
            this.fCurrentPercentage += 1;
        }

        myPdfReport.add(myReportTable);

        if(totalInd == 1) {
            sumRowCells(data, headers, myReportTable, myPdfReport, bold);
        }
    }

    //adding rows to table by group
    private void groupRows(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, PdfPTable newTable, String tableValue, Document myPdfReport, Font normal) throws DocumentException {
        boolean firstBatch = false;
        int i = 1;
        for (Map<String, Object> row : data) {
            if(row.get(groupBy).toString().equals(tableValue)) {
                PdfPCell tableCell1 = new PdfPCell(new Phrase(i++ + "", normal));
                newTable.addCell(tableCell1);
            }
            else{

            }

            for (Map<String, Object> str : headers) {
                if ((row.get(str.get("headerName").toString()) != null) && (row.get(groupBy).toString().equals(tableValue))){
                    if(str.get("totalInd").toString().equals("1")) {
                        String num_val = row.get(str.get("headerName").toString()).toString();
                        Integer val = Integer.valueOf(num_val);
                        PdfPCell tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(val), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        newTable.addCell(tableCell);
                    }
                    else if (str.get("totalInd").toString().equals("2")) {
                        String num_val = row.get(str.get("headerName").toString()).toString();
                        Double val = Double.valueOf(num_val);
                        PdfPCell tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(val), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        newTable.addCell(tableCell);
                    }
                    else if (str.get("totalInd").toString().equals("3")) {


                    }
                    else {
                        PdfPCell tableCell = new PdfPCell(new Phrase(row.get(str.get("headerName").toString()).toString(), normal));
                        tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        newTable.addCell(tableCell);
                    }
                }
                else {

                }
            }
        }
        myPdfReport.add(newTable);
        sumByGroup(data, headers, groupBy, myPdfReport, tableValue, normal);
    }


    //test method
    private void sumByGroup(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, Document myPdfReport, String tableValue, Font normal) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()+1);
        newTable1.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(new Phrase("Total", normal));
        newTable1.addCell(cell);
        cell.setBackgroundColor(new BaseColor(204, 255, 204));

        for (Map<String, Object> row : headers) {
            if (row.get("totalInd").toString().equals("3")) {

            }else {
                sumColumn(data, row, groupBy, newTable1, tableValue, myPdfReport, normal);
            }
        }

    }

    private void sumColumn(List<Map<String, Object>> data, Map<String, Object> header, String groupBy, PdfPTable newTable,
                           String tableValue1, Document myPdfReport, Font normal) throws DocumentException {
        // TODO Auto-generated method stub

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

            PdfPCell tableCell = new PdfPCell(new Phrase(PayrollHRUtils.getDecimalFormat().format(total2), normal));
            tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
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
            PdfPCell tableCell = new PdfPCell(new Phrase( PayrollHRUtils.getDecimalFormat().format(total), normal));
            tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
            newTable.addCell(tableCell);
        }
        else{
            PdfPCell tableCell = new PdfPCell();
            tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
            newTable.addCell(tableCell);
        }
        myPdfReport.add(newTable);
    }

    //checking if data row has already been grouped
    private boolean test(String tableValue) {
        if(tableValues.contains(tableValue)) {
            return true;
        }
        else {
            tableValues.add(tableValue);
            return false;
        }
    }


    //adding watermark to page
    private void onEndPage(PdfWriter writer, Document document, String watermark) {
        Font FONT = new Font(Font.FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.85f));
        ColumnText.showTextAligned(writer.getDirectContentUnder(),
                Element.ALIGN_CENTER, new Phrase(watermark, FONT),
                297.5f, 421, 45f);
    }

    private void sumRowCells(List<Map<String, Object>> data, List<Map<String, Object>> headers, PdfPTable newTable, Document myPdfReport, Font boldFont) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()+1);
        newTable1.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(String.format("Grand Total"), boldFont));
        cell.setBackgroundColor(new BaseColor(204, 255, 204));
        newTable1.addCell(cell);

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

                PdfPCell tableCell = new PdfPCell(new Phrase(String.valueOf(total2), boldFont));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
                PdfPCell tableCell = new PdfPCell(new Phrase(amount, boldFont));
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                tableCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                newTable1.addCell(tableCell);
            }
            else if(row.get("totalInd").toString().equals("3")) {

            }
            else {
                PdfPCell tableCell = new PdfPCell();
                tableCell.setBackgroundColor(new BaseColor(204, 255, 204));
                newTable1.addCell(tableCell);
            }

        }
        myPdfReport.add(newTable1);

    }
}
