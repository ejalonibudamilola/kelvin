package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

@Slf4j
public class ParseLeaveBonus  implements Runnable
{
	  private MultipartFile multipartFile;
	  private String confirmationNumber;
	  private boolean fStop;
	  private boolean finished;
	  private boolean failed;
	  private Integer transactionId;
	  private final int runYear;
	  private XSSFWorkbook fWb;
	  private final FileParseBean fileParseBean;
	  private final HashMap<String, NamedEntity> wEmployeeMap;
	  private HashMap<String, Object> wObjectMap;
	  private final String fileName;
	  private final Vector<NamedEntity> wListToSave;
	  private final Vector<NamedEntity> wErrorList;
	  private final String uniqueUploadId;
      private int objectInd;
      private final Long mdaInstId;
      private final BusinessCertificate bc;


	  public ParseLeaveBonus(XSSFWorkbook workbook, FileParseBean pFileParseBean, Object pSessionId,
							 HashMap<String, NamedEntity> pEmployeeMap, String pUploadUniqueIdentifier, int pNextRunMonth, int pNextRunYear , String pFileName,
							 BusinessCertificate businessCertificate)
	  {
	    this.fWb = workbook;
	    this.fileParseBean = pFileParseBean;
        this.bc = businessCertificate;
	    this.wEmployeeMap = pEmployeeMap;
	    this.wListToSave = new Vector<>();
	    this.wErrorList = new Vector<>();
	    this.uniqueUploadId = pUploadUniqueIdentifier;
	    this.runYear = pNextRunYear;
	    this.mdaInstId = pFileParseBean.getMdaInstId();
	    this.fileName = pFileName;
	  }

	  public void run()
	  {
	    boolean exceptionThrown = false;

	      try
	      {
	        
	        parseLeaveBonusExcelSheet();
	       
	        
	      }
	      catch (IllegalStateException e)
	      {
	        exceptionThrown = true;
	        e.printStackTrace();
	      } catch (IOException e) {
	        exceptionThrown = true;
	        e.printStackTrace();
	      } catch (Exception e) {
	        exceptionThrown = true;
	        e.printStackTrace();
	      } finally {
	        this.fStop = true;
	        this.finished = true;

	          this.fWb = null;


	      }

	  }

	
	private synchronized void parseLeaveBonusExcelSheet()
	    throws Exception
	  {

	    int noOfSheets = this.fWb.getNumberOfSheets();

	    for (int i = 0; i < noOfSheets; i++) {
	      if (isStop())
	      {
	        break;
	      }
	      XSSFSheet wSheet = this.fWb.getSheetAt(i);
	      Iterator<Row> it = wSheet.rowIterator();
	      int x = 0;
	      while (it.hasNext()) {
	        if (isStop()) {
	          break;
	        }
	        XSSFRow row = (XSSFRow)it.next();
	        if (x == 0) {
	          x++;
	          continue;
	        }
	        XSSFCell cell = row.getCell(0);
	        NamedEntity wNamedEntity = new NamedEntity();
	        boolean errorRecord = false;
	        StringBuffer wErrorMsgBuffer = new StringBuffer();
	        
	        if ((cell == null) || (cell.getRichStringCellValue() == null)) break;
	        String ogNumber = cell.getRichStringCellValue().getString();
	        wNamedEntity.setStaffId(ogNumber);
	        if (!this.wEmployeeMap.containsKey(ogNumber.trim().toUpperCase())) {
	        	 wErrorMsgBuffer.append(bc.getStaffTypeName()+" Not Found in "+bc.getMdaTitle());
	        
	          
	          errorRecord = true;
	        } else {
	          wNamedEntity.setName(this.wEmployeeMap.get(ogNumber.trim().toUpperCase()).getName());
	        }

	       
	        String mda = null;
	        String amount = null;

	        cell = row.getCell(1);
	        if (cell == null) {
	          break;
	        }
	        mda = getValueFromCell(cell, this.fWb, "Employee Name");
	        
	        wNamedEntity.setObjectCode(mda);
		     
		   
	         
	        double _amount = 0.0D;
	        cell = row.getCell(2);
	        amount = getValueFromCell(cell, this.fWb, "Amount");
	        wNamedEntity.setAmountInWords(amount);
	        if (amount.indexOf("Error") != -1)
	        {
	          if (!errorRecord)
	            errorRecord = true;
	          else {
	            wErrorMsgBuffer.append("; ");
	          }

	          wNamedEntity.setAllowanceAmount(0.0D);
	          wErrorMsgBuffer.append("Invalid Amount");
	        }
	        else
	        {
	          try
	          {
	            _amount = Double.parseDouble(PayrollHRUtils.removeCommas(amount));
	            BigDecimal wBD = new BigDecimal(String.valueOf(_amount)).setScale(2, RoundingMode.FLOOR);
	            _amount = wBD.doubleValue();
	          }
	          catch (Exception wEx)
	          {
	            if (!errorRecord)
	              errorRecord = !errorRecord;
	            else {
	              wErrorMsgBuffer.append("; ");
	            }

	            wNamedEntity.setAllowanceAmount(0.0D);
	            wErrorMsgBuffer.append("Invalid Amount");
	          }
	        }

	       
	        	int wStartYear = 0;
	        	
	        		cell = row.getCell(3);
	                String wStartMonthStr = getValueFromCell(cell, this.fWb, "Year");
	                wNamedEntity.setLtgYear(wStartMonthStr);
	                if (wStartMonthStr.indexOf("Error") != -1){
	                	if (!errorRecord)
	                        errorRecord = true;
	                      else {
	                        wErrorMsgBuffer.append("; ");
	                      }

	                      
	                      wErrorMsgBuffer.append("Year invalid");
	                     
	                }else{
	                	//Now check if Start Month is actually valid....
	                	if(wStartMonthStr.indexOf(".") != -1){
	                		wStartMonthStr = wStartMonthStr.substring(0, wStartMonthStr.indexOf("."));
	                	}
	                	wStartYear = this.isStartYearValid(wStartMonthStr);
	                	if(wStartYear == -1){
	                		if (!errorRecord)
	                            errorRecord = !errorRecord;
	                          else {
	                            wErrorMsgBuffer.append("; ");
	                          }

	                          //wNamedEntity.setStartDateStr("Start Year invalid");
	                          wErrorMsgBuffer.append(" Year invalid");
	                          
	                	}
	                }
	                
	                
	               
	        if (errorRecord) {
	          wNamedEntity.setErrorMsg(wErrorMsgBuffer.toString());
	          this.wErrorList.add(wNamedEntity);
	        } else {
	          wNamedEntity = new NamedEntity();
	          wNamedEntity.setDeductionAmount(_amount);
	          wNamedEntity.setStaffId(ogNumber.trim().toUpperCase());
	          wNamedEntity.setObjectCode(mda);
	          wNamedEntity.setName(this.wEmployeeMap.get(ogNumber.trim().toUpperCase()).getName());
	          wNamedEntity.setId(this.wEmployeeMap.get(ogNumber.trim().toUpperCase()).getId());
	          wNamedEntity.setMdaInstId(this.mdaInstId);
	          wNamedEntity.setReportType(wStartYear);
	          this.wListToSave.add(wNamedEntity);
	        }

	      }

	      this.fileParseBean.setErrorList(this.wErrorList);
	      this.fileParseBean.setEmployeeMap(this.wEmployeeMap);
	      this.fileParseBean.setListToSave(this.wListToSave);
	      this.fileParseBean.setUniqueUploadId(this.uniqueUploadId);
	      this.fileParseBean.setObjectTypeInd(4);
	      this.fileParseBean.setObjectTypeClass(LeaveBonusBean.class);
	      
	    }
	  }


	  private String getValueFromCell(XSSFCell pCell, XSSFWorkbook pWb, String pFieldName) {
		  String wRetVal = null;
		  switch (pCell.getCellType()) {
			  case NUMERIC:
				  if (DateUtil.isCellDateFormatted(pCell)) {
					  if (pCell.getDateCellValue() == null) break;
					  DataFormatter wFormatter = new DataFormatter(Locale.UK);
					  wRetVal = wFormatter.formatCellValue(pCell);
				  } else if (pCell.getDateCellValue() != null) {
					  wRetVal = Double.toString(pCell.getNumericCellValue());
				  } else {
					  wRetVal = "0";
				  }

				  break;
			  case STRING:
				  if (pCell.getRichStringCellValue() != null) {
					  wRetVal = pCell.getRichStringCellValue().getString();
				  } else {
					  log.error("Invalid value for '" + pFieldName + "' in Excel File " + this.fileName);
					  wRetVal = "Error - Invalid value for '" + pFieldName + "' in Excel File " + this.fileName;
				  }
				  break;
			  default:

				  log.error("Invalid value for '" + pFieldName + "' in Excel File " + this.fileName);
				  wRetVal = "Error - Invalid value for '" + pFieldName + "' in Excel File " + this.fileName;
				  break;


		  }
		  return wRetVal;
	  }
	 
	  private int isStartYearValid(String pStartYearStr)
	  {
		  int wRetVal = -1;
	  	 try{
	  		 wRetVal = Integer.parseInt(pStartYearStr);
	  		 if(wRetVal < this.runYear)
	  			 wRetVal  = -1;
	  	 }catch(Exception wEx){
	  		 return -1;
	  	 }
	  	return wRetVal;
	  }
	  public boolean isFinished()
	  {
	    return this.finished;
	  }
	  public String getConfirmationNumber() {
	    return this.confirmationNumber;
	  }

	  public boolean isStop() {
	    return this.fStop;
	  }

	  public void setStop(boolean pStop) {
	    this.fStop = pStop;
	  }

	  public Integer getTransactionId() {
	    return this.transactionId;
	  }

	  public void setTransactionId(Integer pTransactionId) {
	    this.transactionId = pTransactionId;
	  }

	  public boolean isFailed() {
	    return this.failed;
	  }

	  public void setFailed(boolean pFailed) {
	    this.failed = pFailed;
	  }

	  public HashMap<String, NamedEntity> getwEmployeeMap()
	  {
	    return this.wEmployeeMap;
	  }

	  public HashMap<String, Object> getwObjectMap()
	  {
	    return this.wObjectMap;
	  }

	  public Vector<NamedEntity> getwListToSave()
	  {
	    return this.wListToSave;
	  }

	  public Vector<NamedEntity> getwErrorList()
	  {
	    return this.wErrorList;
	  }

	  public String getUniqueUploadId()
	  {
	    return this.uniqueUploadId;
	  }

	  public FileParseBean getFileParseBean()
	  {
	    return this.fileParseBean;
	  }
	}
