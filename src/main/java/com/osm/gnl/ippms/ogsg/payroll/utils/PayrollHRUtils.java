package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;




public class PayrollHRUtils implements IConstants {

	private static DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

	private PayrollHRUtils(){}
	
	


	public static String treatNull(String pStr){
				
		if(pStr == null)
			return "";
		
		return pStr.trim();
	}
	public static String removeNonSqlXters(String pStr){
		StringBuffer retVal = new StringBuffer();
		
		if(pStr == null || pStr.trim().equals(""))
			return "";
		for(char c : pStr.toCharArray()){
			if(Character.isJavaIdentifierPart(c)){
				retVal.append(c);
			}
		}		
		
		return retVal.toString().trim();
	}
	
	public static String treatSqlStringParam(String pStr, boolean removeSpaceSeparator){
		StringBuffer retVal = new StringBuffer();
		
		if(pStr == null || pStr.trim().equals(""))
			return "";
		for(char c : pStr.toCharArray()){
			if(String.valueOf(c).equals("'")){
				continue;
			}
			if(c == ' ' && removeSpaceSeparator){
				continue;
			}
			retVal.append(c);

		}		
		
		return retVal.toString().trim();
	}
	
	public static DecimalFormat getDecimalFormat(){
		return decimalFormat;
		 
	}
	/**
	 * 
	 * @return A date format of 'day/month'<br> for example 02/09
	 */
	public static DateTimeFormatter getMiniDateFormat(){
		return DateTimeFormatter.ofPattern("dd/MM");
	}
	/**
	 * 
	 * @return A date format of 'day/month/yy' <br> for example&nbsp 02/09/09
	 */
	public static DateTimeFormatter getDateFormat(){
		return DateTimeFormatter.ofPattern("dd/MM/yy");
	}
	/**
	 * 
	 * @return A date format of 'day/month/yyyy' <br> 
	 * for example&nbsp 02/09/2009
	 */
	public static DateTimeFormatter getFullDateFormat(){
		return DateTimeFormatter.ofPattern("dd/MM/yyyy");
	}
	public static DateTimeFormatter getFullDateFormat(boolean pMonthFirst){
		return DateTimeFormatter.ofPattern("MM/dd/yyyy");
	}
	/**
	 * 
	 * @return a date format of Month day and year<br>
	 * for example&nbsp; Aug 10, 2009
	 */
	public static DateTimeFormatter getDisplayDateFormat(){
		return DateTimeFormatter.ofPattern("MMM dd, yyyy");
	}
	/**
	 *
	 * @return a date format of Month day and year<br>
	 * for example&nbsp; Aug 10, 2009
	 */
	public static DateTimeFormatter getTimeStampDisplayDateFormat(){

		return DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss");
	}
	/**
	 * 
	 * @return a date format of Month and Year<br>
	 * for example&nbsp; August, 2009
	 */
	public static DateTimeFormatter getMonthYearDateFormat(){
		return DateTimeFormatter.ofPattern("MMMM, yyyy");
	}
	/**
	 * 
	 * @return a date format of Month and Year
	 * for example&nbsp; Aug, 2008
	 */
	public static DateTimeFormatter getMiniMonthYearDateFormat(){
		return DateTimeFormatter.ofPattern("MMM, yyyy");
	}
	
	public static String getMonthAndYearFromDate(Object pDateRep){
		String wRetVal = "";
		if(pDateRep == null)
			return wRetVal;
		if(pDateRep.getClass().isAssignableFrom(String.class)){
			LocalDate wDate = PayrollBeanUtils.setDateFromString((String)pDateRep);
			wRetVal = getMonthYearDateFormat().format(wDate);
			//wRetVal = wRetVal.replaceFirst(",", "");
			//wRetVal = wRetVal.replaceAll(" ", "");
		}else if(pDateRep.getClass().isAssignableFrom(LocalDate.class)){
			wRetVal = getMonthYearDateFormat().format((LocalDate)pDateRep);
			//wRetVal = wRetVal.replaceFirst(",", "");
			//wRetVal = wRetVal.replaceAll(" ", "");
		}
		return wRetVal;
	}
	/**
	 * This method removes commas for a Double value
	 * but leaves the '.' (Dot)
	 * @param pStr
	 * @return
	 */
	public static String removeCommas(String pStr) {
		String retVal = "";
		for(Character c : pStr.toCharArray()){
				if(!Character.isLetterOrDigit(c)){
					if(!c.equals('.'))
					continue;
				}					
				retVal = retVal + c.toString();
			}
		
		return retVal;
	}



	public static ArrayList<Long> makeDeptIdListArray(List<Department> pAssignedDeptList) {
		ArrayList<Long> wRetList = new ArrayList<Long>();
		
		if(pAssignedDeptList == null || pAssignedDeptList.isEmpty())
			return wRetList;
		
		for(Department d : pAssignedDeptList){
			wRetList.add(d.getId());
		}
		return wRetList;
	}


	public static List<Department> makeAgencyDeptList(List<MdaDeptMap> pList) {
		List<Department> wRetList = new ArrayList<Department>();
		
		if(pList != null && !pList.isEmpty()){
			for(MdaDeptMap m : pList){
				if(m.getPreferredName() != null){
					m.getDepartment().setName(m.getPreferredName());
				}
				if(m.getPreferredDesc() != null){
					m.getDepartment().setDescription(m.getPreferredDesc());
				}
				m.getDepartment().setMdaDeptMapId(m.getId());
				wRetList.add(m.getDepartment());
			}
		}
		
		return wRetList;
	}


	 
    /**
     * This method returns the next promotion date using the following algorithm<br>
     * <ul>
     * <li>If Level &lt; 8, next promotion should be 3 years</li>
     * <li>If 7 &lt; Level &lt; 16, next promotion should be 4 years</li>
     * <li>else it should be 5 years</li>
     * 
     * @param pLastPromotionDate
     * @param pLevel
     * @return
     */
	public static LocalDate determineNextPromotionDate(LocalDate pLastPromotionDate, int pLevel) {

		LocalDate wRetVal = LocalDate.now();
		LocalDate wWorking = pLastPromotionDate;

		int wYear = wRetVal.getYear();
		int wMonth = wRetVal.getMonthValue();
		int wDay = wRetVal.getDayOfMonth();
		
		//This method can be wrong...
		if(pLevel < 7){
			wYear += 2;
		}else if(pLevel >= 7 && pLevel < 14){
			wYear += 3;
			//wWorking.add(Calendar.YEAR, 3);
		}else if(pLevel >= 14 && pLevel < 16){
			wYear += 4;
			//wWorking.add(Calendar.YEAR, 4);
		}else{
			wYear += 6;
			//wWorking.add(Calendar.YEAR, 6);
		}
		switch(wMonth){
		
		case 4:
		case 6:
		case 9:
		case 11:
			if(wDay > 30){
				wDay = 30;
			}
			break;
		case 2:
			if(PayrollBeanUtils.isLeapYear(wYear)){
				if(wDay > 29){
					wDay = 29;
				}
			}else{
				if(wDay > 28){
					wDay = 28;
				}
			}
			break;
		}
		 wRetVal = LocalDate.of(wYear, wMonth, wDay);
		return wRetVal;
	}

	public static boolean canBeXlsSheetName(String pStringName)
	{
		 
		if(pStringName == null)
			return false;
		char[] wChar = pStringName.toCharArray();
		for(char c : wChar){
			if(!Character.isLetterOrDigit(c) && !Character.isWhitespace(c))
				return false;
		}
		
		
		return true;
	} 
	/**
	 * 
	 * @param pLastName
	 * @param pFirstName
	 * @param pInitials
	 * @return
	 */
	public static String createDisplayName(String pLastName, String pFirstName, String pInitials){
		if(pLastName == null && pFirstName == null)
			return "";
		String wRetVal = pLastName.toUpperCase()+", "+changeToNormalCase(pFirstName);
		if(pInitials != null && pInitials.trim().length() > 0){
			wRetVal += " "+changeToNormalCase(pInitials)/*.substring(0, 1).toUpperCase()*/+".";
		}
					
		return wRetVal;
		
	}
	public static String createDisplayNameWivShortInitials(String pLastName, String pFirstName, String pInitials){
		String wRetVal = pLastName.toUpperCase()+", "+changeToNormalCase(pFirstName);
		if(pInitials != null && pInitials.trim().length() > 0){
			wRetVal += " "+changeToNormalCase(pInitials).substring(0, 1).toUpperCase()+".";
		}

		return wRetVal;

	}
	/**
	 * 
	 * @param pLastName
	 * @param pFirstName
	 * @param pInitials
	 * @return
	 */
	public static String createDisplayName(String pLastName, String pFirstName, Object pInitials){
		String wRetVal = "";
		wRetVal = pLastName.toUpperCase()+", "+changeToNormalCase(pFirstName);
		
		
		if(pInitials != null ){
			wRetVal += " "+changeToNormalCase((String) pInitials)/*.substring(0, 1).toUpperCase()*/+".";
		}
					
		return wRetVal;
		
	}
	public static String changeToNormalCase(String pStr){
    	StringBuffer wStr = new StringBuffer();
    	
    	char[] wCharArray = pStr.toCharArray();
    	int i = 0;
    	for(char c : wCharArray){
    		if(i == 0){
    			if(Character.isLowerCase(c))
    				Character.toUpperCase(c);
    			wStr.append(c);
    			i++;
    		}else{
    			wStr.append(Character.toLowerCase(c));
    		}
    	}
    	
    	return wStr.toString();
    }


	public static DecimalFormat getDecimalFormat(boolean pRoundUp){
		return new DecimalFormat("#,##0.00");
	}
	


	public static Object getNumberFromString(String pAmountStr)
{
	 Object wRetVal;
	 try{
		 wRetVal = Double.parseDouble(removeCommas(pAmountStr));
	 }catch(Exception wEx){
		 //Eat the Exception
		 wRetVal = null;
	 }
	return wRetVal;
}


	public static String dateWidoutDelimeters(){
		return "_"+StringUtils.remove(PayrollHRUtils.getDisplayDateFormat().format(LocalDate.now()), "/");
	}




	public static boolean isRecordBeforeSept2019(int pRunMonth, int pRunYear) {
		return pRunYear <= 2019 && (pRunMonth < 8 || pRunYear < 2019);
	}

	
}
