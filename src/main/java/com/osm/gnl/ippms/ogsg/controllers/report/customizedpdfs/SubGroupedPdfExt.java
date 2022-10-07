package com.osm.gnl.ippms.ogsg.controllers.report.customizedpdfs;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.PdfHeaderPageEvent;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SubGroupedPdfExt extends PdfBaseClass{



    /**
     * Use this for cases where we need schools and mdas
     * @param response
     * @param request
     * @param rt
     */
    public void getPdf(HttpServletResponse response, HttpServletRequest request, ReportGeneratorBean rt) throws DocumentException {

            super.makePdf(request, rt);

            groupData(rt.getTableData(), rt.getTableHeaders(), myPdfReport, normal, boldFont, rt.getGroupBy(),
                    rt.getGroupedKeySet(), rt.getSubGroupedKeySet(), rt.getSubGroupBy(), rt.getTableSubData(), rt.getMdaSchoolMap(), rt.getDoubleSubGroupBy());

            myPdfReport.close();

            writeOutPdf(response);

     }




    private void groupData(List<Map<String, Object>> data, List<Map<String, Object>> headers,
                           Document myPdfReport, Font normal, Font boldFont, String groupBy, Set<String> groupedKeySet, Set<String> subGroupedKeySet,
                           String subGroupBy, List<Map<String, Object>> tableSubData, Map<String, List<String>> schoolMap, String doubleSubGroupBy) throws DocumentException {

        // This is where the Group by Items are separated in the groupedKeySet....(Deduction or Loan)
        PdfPTable newTable = prepareMyReportTable(headers.size() - rt.getUnUsedHeaders());
        newTable.setWidthPercentage(100);

        List<Map<String, Object>> mapList;
         //This groupedKeySet is for MNDAs
        for (String key : groupedKeySet) {


                mapList = data.stream()
                        .filter(stringObjectMap -> stringObjectMap.get(groupBy).toString().equals(key))
                        .collect(Collectors.toList()); //This contains the Deduction/Loan and all the Staffs and MDA they belong to
                subGroupData(mapList, headers, subGroupBy, myPdfReport, normal, subGroupedKeySet, key, groupBy, boldFont,tableSubData,schoolMap,doubleSubGroupBy);
                //myPdfReport.add(new Paragraph("\n"));
                 sumByGroupParent(mapList, headers, groupBy, myPdfReport, normal, key);


        }

        sumRowCells(data, headers,tableSubData);
    }
    private void subGroupData(List<Map<String, Object>> data, List<Map<String, Object>> headers, String subGroupBy,
                              Document myPdfReport, Font normal, Set<String> subGroupedKeySet, String tableKey, String groupBy,
                              Font boldFont, List<Map<String, Object>> tableSubData, Map<String, List<String>> schoolMap,
                              String doubleSubGroupBy) throws DocumentException {

        List<Map<String, Object>> mapList;
        PdfPTable newTable;
        int i = 0;
        int j = 0;
        for (String key : subGroupedKeySet) {
            //Here we get all the items for the key.
            if (schoolMap.containsKey(key)) {
                //This MDA has subgroups....
                List<String> schoolList = schoolMap.get(key);
                Collections.sort(schoolList);
                for(String string : schoolList) {
                    mapList = tableSubData.stream()
                            .filter(stringObjectMap -> stringObjectMap.get(doubleSubGroupBy).toString().equals(string))
                            .collect(Collectors.toList());
                    if (mapList.size() > 0) {
                        ((PdfHeaderPageEvent) pdfWriter.getPageEvent()).setSubGroupBy(subGroupBy);
                        ((PdfHeaderPageEvent) pdfWriter.getPageEvent()).setGroupByKey(tableKey);
                        ((PdfHeaderPageEvent) pdfWriter.getPageEvent()).setKey(key);
                        ((PdfHeaderPageEvent) pdfWriter.getPageEvent()).setSchoolName(string);
                        if (j > 0 || this.startNewPage)
                            myPdfReport.newPage();
                        else {
                            if (pdfWriter.getCurrentPageNumber() == 1)
                                this.writeSubGroupHeader(tableKey, boldFont, subGroupBy, key,string);
                        }

                        newTable = prepareMyReportTable(headers.size() - rt.getUnUsedHeaders());
                        newTable.setWidthPercentage(100);
                        groupRows(mapList, headers, groupBy, newTable, myPdfReport, normal, key, subGroupBy, tableKey);
                        j++;
                    }
                }
                ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setSubGroupBy(null);
                ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setGroupByKey(null);
                ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setKey(null);
                ((PdfHeaderPageEvent) pdfWriter.getPageEvent()).setSchoolName(null);
            } else{
                mapList = data.stream()
                        .filter(stringObjectMap -> stringObjectMap.get(subGroupBy).toString().equals(key))
                        .collect(Collectors.toList());


                if (mapList.size() > 0) {
                    ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setSubGroupBy(subGroupBy);
                    ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setGroupByKey(tableKey);
                    ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setKey(key);
                    if(i > 0 || this.startNewPage)
                        myPdfReport.newPage();
                    else{
                        if(pdfWriter.getCurrentPageNumber() == 1)
                            this.writeSubGroupHeader(tableKey,boldFont,subGroupBy,key,null);
                    }


                    newTable = prepareMyReportTable(headers.size() - rt.getUnUsedHeaders());
                    newTable.setWidthPercentage(100);
                    groupRows(mapList, headers, groupBy, newTable,myPdfReport,normal, key, subGroupBy, tableKey);
                    i++;
                }


          }



        }
        ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setSubGroupBy(null);
        ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setGroupByKey(null);
        ((PdfHeaderPageEvent)pdfWriter.getPageEvent()).setKey(null);
        ((PdfHeaderPageEvent) pdfWriter.getPageEvent()).setSchoolName(null);
    }


    //adding rows to table by group
    private void groupRows(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy,
                           PdfPTable newTable, Document myPdfReport, Font normal, String tableKey, String subGroupBy, String groupByKey) throws DocumentException {


        int i;
        for (Map<String, Object> row : data) {
            i = 1;

            for (Map<String, Object> str : headers) {
                if (row.get(str.get("headerName").toString()) != null){

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

    private void sumRowCells(List<Map<String, Object>> data, List<Map<String, Object>> headers, @Nullable List<Map<String, Object>> tableSubData) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()- rt.getUnUsedHeaders());
        if(tableSubData != null){
            data.addAll(tableSubData);
        }
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
                else if (count == totalCount - (rt.getUnUsedHeaders() - 1)){
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
        PdfPTable newTable1 = new PdfPTable(headers.size()- rt.getUnUsedHeaders());
        newTable1.setWidthPercentage(100);


        int count = 1;
        for (Map<String, Object> row : headers) {
            if (row.get("totalInd").toString().equals("3")) {
                continue;
            }else {
                sumColumn(data, row, newTable1, normal, count, headers);
            }
            count++;
        }
        myPdfReport.add(newTable1);

    }

    private void sumByGroupParent(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, Document myPdfReport, Font normal, String tableKey, LinkedHashMap<String, List<String>> mdaSchoolMap, List<Map<String, Object>> tableSubData) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()- rt.getUnUsedHeaders());
        newTable1.setWidthPercentage(100);
        if(mdaSchoolMap.containsKey(tableKey)) {
           data.addAll(tableSubData);
        }

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
    private void sumByGroupParent(List<Map<String, Object>> data, List<Map<String, Object>> headers, String groupBy, Document myPdfReport, Font normal, String tableKey) throws DocumentException {
        PdfPTable newTable1 = new PdfPTable(headers.size()- rt.getUnUsedHeaders());
        newTable1.setWidthPercentage(100);

        if(data.size() == 0){

        }
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


    private void sumColumn(List<Map<String, Object>> data, Map<String, Object> header, PdfPTable newTable,
                           Font normal, int count, List<Map<String, Object>> headers) throws DocumentException {
        // TODO Auto-generated method stub
        String no;
        int totalCount = headers.size();
        if(header.get("totalInd").toString().equals("1")) {
            Integer total2 = 0;

            for (Map<String, Object> str : data) {
                if (IppmsUtils.isNotNullOrEmpty(str.get(header.get("headerName")).toString())) {
                    no = str.get(header.get("headerName").toString()).toString();
                    total2 += Integer.valueOf(no);
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
                     no = str.get(header.get("headerName").toString()).toString();
                     total += Double.valueOf(no);
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
        String no;
        if(header.get("totalInd").toString().equals("1")) {
            Integer total2 = 0;
            for (Map<String, Object> str : data) {

                if (IppmsUtils.isNotNullOrEmpty(str.get(header.get("headerName")).toString())) {
                    no = str.get(header.get("headerName").toString()).toString();
                    total2 += Integer.valueOf(no);
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
                     no = str.get(header.get("headerName").toString()).toString();

                    total += Double.valueOf(no);
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


}
