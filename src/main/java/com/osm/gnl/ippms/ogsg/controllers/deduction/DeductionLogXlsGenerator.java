/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.deduction;

import com.osm.gnl.ippms.ogsg.audit.domain.AbstractDeductionAuditEntity;
import com.osm.gnl.ippms.ogsg.controllers.BaseExcelViewController;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;




public class DeductionLogXlsGenerator extends BaseExcelViewController {
	 


	protected void buildExcelDocument(Map pModel, Workbook pWorkbook, HttpServletRequest pRequest, HttpServletResponse pResponse)
	    throws Exception
	  {
		  super.fRequest = pRequest;  
		  PaginatedBean pBSB = (PaginatedBean) pModel.get("dedLogBean");

	    pModel.remove("dedLogBean");
	    
        this.fWorkbook = pWorkbook;
        setCreationHelper();
	    sheet = createSheet("Deduction Audit Log Report", true);

        
		 

	    CellStyle styleHeader = getHeaderStyle(LAVENDER);
	    int startRow = 6;
	    Row rowHeader = sheet.createRow(startRow);
	    RichTextString strToSet = wCreationHelper.createRichTextString("Deduction Logs");
		strToSet.applyFont(getBoldFont());
	    Cell cell0 = rowHeader.createCell(0);
		styleHeader.setAlignment(CENTER);
	    cell0.setCellStyle(styleHeader);
	    cell0.setCellValue(strToSet);
	    
	    if(pBSB.isFilteredByUserId()){
	    	rowHeader = sheet.createRow(++startRow);
		     strToSet = wCreationHelper.createRichTextString("Deduction Changes by "+pBSB.getEmployeeName());
			strToSet.applyFont(getBoldFont());
		     cell0 = rowHeader.createCell(0);
			styleHeader.setAlignment(CENTER);
		    cell0.setCellStyle(styleHeader);
		    cell0.setCellValue(strToSet);
		    
	    }

	    if(pBSB.isFilteredByType()){
	    	rowHeader = sheet.createRow(++startRow);
		     strToSet = wCreationHelper.createRichTextString(pBSB.getTypeName()+ " Deduction Audit Log ");
			strToSet.applyFont(getBoldFont());
		     cell0 = rowHeader.createCell(0);
			styleHeader.setAlignment(CENTER);
		    cell0.setCellStyle(styleHeader);
		    cell0.setCellValue(strToSet);
		    
	    }
	    rowHeader = sheet.createRow(++startRow);
	     strToSet = wCreationHelper.createRichTextString("Audit Period : "+pBSB.getFromDateStr()+" - "+pBSB.getToDateStr());
		strToSet.applyFont(getBoldFont());
	     cell0 = rowHeader.createCell(0);
		 
	    cell0.setCellStyle(styleHeader);
	    cell0.setCellValue(strToSet);
	    
	    rowHeader = sheet.createRow(++startRow);
	     strToSet = wCreationHelper.createRichTextString("Print Date : "+ PayrollHRUtils.getDisplayDateFormat().format(LocalDate.now()));
		strToSet.applyFont(getBoldFont());
	     cell0 = rowHeader.createCell(0);
		 
	    cell0.setCellStyle(styleHeader);
	    cell0.setCellValue(strToSet);
	    
	    ++startRow;

		  Row header = sheet.createRow(++startRow);

	    CellStyle mainHeaderStyle = getMainBodyStyle(true, LAVENDER, true);

	    int headerColumn = 0;
	    
	    
	    Cell cell = null;
	    strToSet = wCreationHelper.createRichTextString("S/No.");
		strToSet.applyFont(getBoldFont());
	     cell = header.createCell(headerColumn++);
	    cell.setCellStyle(mainHeaderStyle);
	    cell.setCellValue(strToSet);
       
    	
    	strToSet = wCreationHelper.createRichTextString("OG Number");
		strToSet.applyFont(getBoldFont());
	     cell = header.createCell(headerColumn++);
	    cell.setCellStyle(mainHeaderStyle);
	    cell.setCellValue(strToSet);

    	strToSet = wCreationHelper.createRichTextString("Employee Name");
		strToSet.applyFont(getBoldFont());
		cell = header.createCell(headerColumn++);
		cell.setCellStyle(mainHeaderStyle);
		cell.setCellValue(strToSet);

    
        if(!pBSB.isFilteredByType()){
        	
		    strToSet = wCreationHelper.createRichTextString("Deduction Type");
			strToSet.applyFont(getBoldFont());
			cell = header.createCell(headerColumn++);
			cell.setCellStyle(mainHeaderStyle);
			cell.setCellValue(strToSet);

        }
       
        
	    strToSet = wCreationHelper.createRichTextString("Old Value");
		strToSet.applyFont(getBoldFont());
	    cell = header.createCell(headerColumn++);
	    cell.setCellStyle(mainHeaderStyle);
	    cell.setCellValue(strToSet);
	    
	    strToSet = wCreationHelper.createRichTextString("New Value");
		strToSet.applyFont(getBoldFont());
	    cell = header.createCell(headerColumn++);
	    cell.setCellStyle(mainHeaderStyle);
	    cell.setCellValue(strToSet);
	    
        if(!pBSB.isFilteredByUserId()){
        	strToSet = wCreationHelper.createRichTextString("Changed By");
        	strToSet.applyFont(getBoldFont());
        	cell = header.createCell(headerColumn++);
        	cell.setCellStyle(mainHeaderStyle);
        	cell.setCellValue(strToSet);
        }
	    

	    strToSet = wCreationHelper.createRichTextString("Changed Date");
		strToSet.applyFont(getBoldFont());
	    cell = header.createCell(headerColumn);
	    cell.setCellStyle(mainHeaderStyle);
	    cell.setCellValue(strToSet);

	   

	    CellStyle mainBodyStyle = getMainBodyStyle();
	    Cell bodyCell = null;

	    
        
		List<AbstractDeductionAuditEntity> wDeductionAuditList = (List<AbstractDeductionAuditEntity>)pBSB.getList();
        Collections.sort(wDeductionAuditList);
        int wSerialNum = 0;
	    for (AbstractDeductionAuditEntity w : wDeductionAuditList) {
	      Row row = sheet.createRow(++startRow);
	      int i = 0;
	      bodyCell = row.createCell(i++);
	      bodyCell.setCellStyle(mainBodyStyle);
	      bodyCell.setCellValue(wCreationHelper.createRichTextString(String.valueOf(++wSerialNum)));
	     
	    	  bodyCell = row.createCell(i++);
		      bodyCell.setCellStyle(mainBodyStyle);
		      bodyCell.setCellValue(wCreationHelper.createRichTextString(w.getPlaceHolder()));
		      bodyCell = row.createCell(i++);
		      bodyCell.setCellStyle(mainBodyStyle);
		      bodyCell.setCellValue(wCreationHelper.createRichTextString(w.getEmployeeName()));
	      
	     
	     
	      if(!pBSB.isFilteredByType()){
	    	  bodyCell = row.createCell(i++);
		      bodyCell.setCellStyle(mainBodyStyle);
		      bodyCell.setCellValue(wCreationHelper.createRichTextString(w.getDeductType()));
	      }
	      
	      bodyCell = row.createCell(i++);
	      bodyCell.setCellStyle(mainBodyStyle);
	      bodyCell.setCellValue(wCreationHelper.createRichTextString(w.getOldValue()));
	      bodyCell = row.createCell(i++);
	      bodyCell.setCellStyle(mainBodyStyle);
	      bodyCell.setCellValue(wCreationHelper.createRichTextString(w.getNewValue()));
	      
	      if(!pBSB.isFilteredByUserId()){
	    	  bodyCell = row.createCell(i++);
		      bodyCell.setCellStyle(mainBodyStyle);
		      bodyCell.setCellValue(wCreationHelper.createRichTextString(w.getChangedBy()));
	      }
	      bodyCell = row.createCell(i++);
	      bodyCell.setCellStyle(mainBodyStyle);
	      bodyCell.setCellValue(wCreationHelper.createRichTextString(w.getAuditTimeStamp()));
	    }


	  
	  }

	 
	  
}
