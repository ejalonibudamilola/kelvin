package com.osm.gnl.ippms.ogsg.controllers;


import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollExcelUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.*;


public abstract class BaseExcelViewController extends AbstractXlsView {

	protected String imageLocation;
	 
	protected String naira = "\u20A6";
	
	protected String addendum ;
	
	protected HorizontalAlignment LEFT_ALIGN = HorizontalAlignment.LEFT;
	
	protected HorizontalAlignment CENTER = HorizontalAlignment.CENTER;
	
	protected short LAVENDER = IndexedColors.LAVENDER.index;
	
	protected final double PIX_SCALE = 2.5D;
	
	protected  HttpServletResponse fResponse;
	
	protected HttpServletRequest fRequest;
	
	protected Workbook fWorkbook;
	
	protected CreationHelper wCreationHelper;
	
	protected Font fFont;
	
	protected Sheet sheet; 
	
	private CellStyle styleHeader,
	                  footerHeader,
	                  mainBodyStyle,
	                  designMainBodyStyle,
	                  footerBodyStyle;
	private String printDateStr;

	protected final String getPrintDateStr(){
		return "Print Date : "+ PayrollHRUtils.getDisplayDateFormat().format(LocalDate.now());
	}
	protected  final String getEmployeeIdTitle(HttpServletRequest httpServletRequest) throws Exception {
		return IppmsUtilsExt.getStaffIdLabel(this.getBusinessCertificate(httpServletRequest));
	}
	@Override
	protected abstract void buildExcelDocument(Map<String, Object> pModel,
			Workbook pWorkBook, HttpServletRequest pServletRequest,
			HttpServletResponse pServletResponse) throws Exception;
	
	
	protected final CreationHelper setCreationHelper() {
		 
		     return wCreationHelper = fWorkbook.getCreationHelper();
	 
	}
    protected synchronized final BusinessCertificate getBusinessCertificate(HttpServletRequest httpServletRequest) throws Exception{
		try{
			return (BusinessCertificate) httpServletRequest.getSession().getAttribute(IppmsEncoder.getCertificateKey());
		}catch(Exception wEx){
			throw new EpmAuthenticationException(new Throwable("Business Certificate Not Found In Session."));
		}

	}
	protected Font getBoldFont() {
		 
		     fFont = fWorkbook.createFont();
		fFont.setFontHeightInPoints((short)12);
		fFont.setBold(true);
		return fFont;
	}
	protected CellStyle getHeaderStyle() {
		 
		   styleHeader = fWorkbook.createCellStyle();
		styleHeader.setFillPattern(FillPatternType.NO_FILL);
        styleHeader.setFont(getBoldFont());
        styleHeader.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());

        styleHeader.setBorderTop(BorderStyle.MEDIUM);
		styleHeader.setBorderBottom(BorderStyle.MEDIUM);
		styleHeader.setBorderRight(BorderStyle.MEDIUM);
		styleHeader.setBorderLeft(BorderStyle.MEDIUM);
		return styleHeader;
		
	}
	protected CellStyle getFooterStyle() {
		 
		
		footerHeader = fWorkbook.createCellStyle();
		footerHeader.setFillPattern(FillPatternType.NO_FILL);
		footerHeader.setFont(getBoldFont());
		footerHeader.setBorderTop(BorderStyle.MEDIUM);
		footerHeader.setBorderBottom(BorderStyle.MEDIUM);
		footerHeader.setBorderRight(BorderStyle.MEDIUM);
		footerHeader.setBorderLeft(BorderStyle.MEDIUM);
		footerHeader.setAlignment(HorizontalAlignment.RIGHT);
		return footerHeader;
		
	}
	/**
	 * Allows a developer choose the Foreground Color
	 * IndexedColors.LAVENDER.getIndex() as example
	 * @param pHeaderColor
	 * @return
	 */
	protected CellStyle getHeaderStyle(short pHeaderColor) {
		/*
		 * if(styleHeader != null && styleHeader.getFillForegroundColor() ==
		 * pHeaderColor) return styleHeader;
		 */
		  styleHeader = fWorkbook.createCellStyle();
		styleHeader.setFillPattern(FillPatternType.NO_FILL);

        styleHeader.setFillForegroundColor(pHeaderColor);

        styleHeader.setBorderTop(BorderStyle.MEDIUM);
		styleHeader.setBorderBottom(BorderStyle.MEDIUM);
		styleHeader.setBorderRight(BorderStyle.MEDIUM);
		styleHeader.setBorderLeft(BorderStyle.MEDIUM);
		return styleHeader;
		
	}
	protected CellStyle getMainBodyStyle() {
		
		/*
		 * if(mainBodyStyle != null) return mainBodyStyle;
		 */
	    mainBodyStyle = fWorkbook.createCellStyle();
		mainBodyStyle.setFillPattern(FillPatternType.NO_FILL);
 
		mainBodyStyle.setBorderTop(BorderStyle.THIN);
        mainBodyStyle.setBorderBottom(BorderStyle.THIN);
        mainBodyStyle.setBorderRight(BorderStyle.THIN);
        mainBodyStyle.setBorderLeft(BorderStyle.THIN);
	 
		return mainBodyStyle;
	}
	protected CellStyle getMainBodyStyle(boolean pSetColor, short pColor, boolean pSetBoldFont) {
		/*
		 * if(designMainBodyStyle != null) { if(pSetColor &&
		 * designMainBodyStyle.getFillForegroundColor() == pColor) { return
		 * designMainBodyStyle; } }
		 */
		  designMainBodyStyle = fWorkbook.createCellStyle();
		  designMainBodyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		if(pSetColor)
			designMainBodyStyle.setFillForegroundColor(pColor);
		if(pSetBoldFont)
			designMainBodyStyle.setFont(getBoldFont());
		
		designMainBodyStyle.setBorderTop(BorderStyle.THIN);
		designMainBodyStyle.setBorderBottom(BorderStyle.THIN);
		designMainBodyStyle.setBorderRight(BorderStyle.THIN);
		designMainBodyStyle.setBorderLeft(BorderStyle.THIN);
		return designMainBodyStyle;
	}
	protected CellStyle getFooterBodyStyle() {
		//if(footerBodyStyle != null) return footerBodyStyle;
		
		footerBodyStyle = getMainBodyStyle();
		footerBodyStyle.setFillPattern(FillPatternType.NO_FILL);
 
		footerBodyStyle.setBorderTop(BorderStyle.MEDIUM);
		footerBodyStyle.setBorderBottom(BorderStyle.MEDIUM);
		footerBodyStyle.setBorderRight(BorderStyle.MEDIUM);
		footerBodyStyle.setBorderLeft(BorderStyle.MEDIUM);
		return footerBodyStyle;
	}
	protected  Sheet createSheet(String pSheetName, boolean pAddHeader) throws Exception {
		
		 sheet = null;
		
		if(null == pSheetName || StringUtils.trimToEmpty(pSheetName).equals("")) {
			sheet = fWorkbook.createSheet();
		}else {
			sheet = fWorkbook.createSheet(PayrollExcelUtils.makeXlsCompatibleName(StringUtils.trimToEmpty(pSheetName)));
		}
		
		  
		
		if(pAddHeader) {
//			CellRangeAddress headerMergeArea = new CellRangeAddress(1, 4, 4, 10);
//
//	        sheet.addMergedRegion(headerMergeArea);

	        Drawing<?> patriarch = sheet.createDrawingPatriarch();

	        Picture picture = patriarch.createPicture(new HSSFClientAnchor(), loadPicture(getImageLocation(), fWorkbook));
	        picture.resize(5.1, 3.1);
		}
		return sheet;
	}
	 
	protected int loadPicture(String pImageLocation, Workbook pFWorkbook) throws Exception
	{
		int pictureIndex;
	    FileInputStream fis = null;
	    ByteArrayOutputStream bos = null;
	    try {
	        fis = new FileInputStream(pImageLocation);
	       bos = new ByteArrayOutputStream();
	        int c;
	   while ((c = fis.read()) != -1)
	            bos.write(c);
	        pictureIndex = pFWorkbook.addPicture(bos.toByteArray(),
	               Workbook.PICTURE_TYPE_PNG);
	   } finally {
	        if (fis != null)
	            fis.close();
	       if (bos != null)
	           bos.close();
	    }
	    return pictureIndex;
	}
	
	protected String getImageLocation(){
		 if(System.getProperty("os.name").toLowerCase().indexOf("win") != -1){
			 
				imageLocation = System.getProperty("oysg-gov-ippms.root", null)+"\\images\\clientLogo.png";
			}else if(System.getProperty("os.name").toLowerCase().equals("linux")){
				imageLocation = System.getProperty("oysg-gov-ippms.root", null)+"/images/clientLogo.png";
				if(imageLocation.startsWith("null")) {
					//Lets use other kinds of ways...
					imageLocation = this.fRequest.getSession().getServletContext().getRealPath("/")+"images/clientLogo.png";
				}
			}else {
				imageLocation = System.getProperty("oysg-gov-ippms.root", null)+"/images/clientLogo.png";
			}
		 return imageLocation;
	}
	
	protected String getAddendum(int pRunMonth, int pRunYear){
		addendum = PayrollBeanUtils.getMonthNameFromInteger(pRunMonth)+"_"+ pRunYear;
		return addendum;
	}
	protected List<EmpDeductMiniBean> makeDeductionBeanList(
			HashMap<Long, EmpDeductMiniBean> hashMap)
	{
		 
    	Set <Long> wSet = hashMap.keySet();
	      List<EmpDeductMiniBean> wRetList = new ArrayList<EmpDeductMiniBean>();
	      for (Long wInt : wSet)
	      {
	    	  EmpDeductMiniBean wInnerMap = hashMap.get(wInt);

	        wRetList.add(wInnerMap);
	      }

	      return wRetList;
	}

	protected List<BankInfo> getBankInfo(HashMap<Long, BankInfo> pBankInfoMap)
    {
      Set <Long> wSet = pBankInfoMap.keySet();
      List<BankInfo> wRetList = new ArrayList<BankInfo>();
      for (Long wInt : wSet)
      {
        BankInfo wInnerMap = pBankInfoMap.get(wInt);

        wRetList.add(wInnerMap);
      }

      return wRetList;
    }
	protected List<BankBranch> makeBankBranchList(HashMap<Long, BankBranch> pBankBranchMap)
    {
      List<BankBranch> wRetList = new ArrayList<BankBranch>();
      Set <Long>wSet = pBankBranchMap.keySet();
      for (Long wInt : wSet) {
        BankBranch wBranch = pBankBranchMap.get(wInt);
        
          wRetList.add(wBranch);
        }
      
      return wRetList;
    }
	protected HashMap<Long, List<BankBranch>> makeBankBranchMap(HashMap<Long, BankBranch> pBankBranchMap)
    {
      HashMap<Long, List<BankBranch>> wRetList = new HashMap<Long, List<BankBranch>>();
      Set <Long>wSet = pBankBranchMap.keySet();
      for (Long wInt : wSet) {
        BankBranch wBranch = pBankBranchMap.get(wInt);
        if (wRetList.containsKey(wBranch.getBankId())) {
          wRetList.get(wBranch.getBankId()).add(wBranch);
        } else {
          List <BankBranch>wList = new ArrayList<BankBranch>();
          wList.add(wBranch);
          wRetList.put(wBranch.getBankId(), wList);
        }
      }
      return wRetList;
    }
	protected String makeSchoolCodeName(SchoolInfo pB)
    {
      String schoolName = pB.getName();

      char[] wChar = schoolName.toCharArray();
      StringBuffer wStr = new StringBuffer();
      for (char c : wChar) {
        if ((Character.isLetterOrDigit(c)) || (Character.isWhitespace(c))) {
          wStr.append(c);
        }
        else if (c == '/') {
          wStr.append("-");
        }
      }

      String codeName = pB.getCodeName();

      return codeName + "-" + wStr.toString();
    }
	protected String makeXlsCompatibleName(String pBranchName)
    {
      String schoolName = pBranchName;

      char[] wChar = schoolName.toCharArray();
      StringBuffer wStr = new StringBuffer();
      for (char c : wChar) {
        if ((Character.isLetterOrDigit(c)) || (Character.isWhitespace(c))) {
          wStr.append(c);
        }
        else if (c == '/') {
          wStr.append("-");
        }

      }

      return wStr.toString();
    }
	
}
