package com.osm.gnl.ippms.ogsg.payroll.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * @author Dapo Akinyade
 * @version 1.0
 * Sep 8, 2014
 *
 */

public class CurrencyWordGenerator {
   
	
	private static CurrencyWordGenerator fInstance;
	
	 
	 public static CurrencyWordGenerator getInstance()
	   {
	     if (fInstance == null)
	    	 fInstance = new CurrencyWordGenerator();
	      
	     return fInstance;
	   }

	
	private String handleDecimal(final Object pValue) throws Exception{		 
   	  int x = 0;
   	  int wDecimals = 0;
   	  String value = "0";
   	  
   	  if(pValue != null){
   		  if(pValue.getClass().isAssignableFrom(String.class)){
   			value = String.valueOf(pValue);
   		
   		  }else if(pValue.getClass().isAssignableFrom(Double.class)){
   			  BigDecimal wBd = new BigDecimal((Double)pValue).setScale(2, RoundingMode.HALF_EVEN);
   			  value = wBd.toPlainString();
   		  }else if(pValue.getClass().isAssignableFrom(Integer.class)){
   			  value = String.valueOf(((Integer)pValue).intValue());
   		  }else if (pValue.getClass().isAssignableFrom(Long.class)){
   			  value = String.valueOf(((Long)pValue).longValue());
   		  }else{
   			  throw new Exception("Exception : Exception thrown from NumberWordConverter - Invalid Object Type Supplied");
   		  }
   	  }
   	  
   	  boolean decimal = false;
   	  if(value.contains(".")){
   		  decimal = true;
   	  }

   	  
   	 if(decimal == true){
   		 
   		 //handles decimal points
   		int decimals = value.indexOf("."); 
      String k =   value.substring(decimals);
      String normalize = k.replace(".", "");
     
      //converts string to int value of decimal number 
      x = Integer.parseInt(normalize);
   
      //handles the whole numbers
      int index = value.indexOf(".");
      String g = value.substring(0, index);
      wDecimals = Integer.parseInt(g);

      return convert(wDecimals)+ " Naira and "+ convert(x) + " Kobo only"; 
   	 }
   
   	 if (decimal !=true){
   		 String g = value;
   		 wDecimals = Integer.parseInt(g);

   	// System.out.println(convert(wDecimals));
	return convert(wDecimals);}
   	 
   	 return "";
 }
	
	
	private final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen",
            "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

	private final String[] tens = {
            "",        // 0
            "",        // 1
            "Twenty",  // 2
            "Thirty",  // 3
            "Forty",   // 4
            "Fifty",   // 5
            "Sixty",   // 6
            "Seventy", // 7
            "Eighty",  // 8
            "Ninety"   // 9
    };
    
  
     

      
	private String convert(final int n) {
        
    	
    	 
    	if (n < 0) {
            return "minus " + convert(-n);
        }

        if (n < 20) {
            return units[n];
        }

        if (n < 100) {
            return tens[ (n / 10)] + ((n % 10 != 0) ? " " : "") + units[(n % 10)];
        }

        if (n < 1000) {
            return units[ (n / 100)] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        }

        if (n < 1000000) {
            return convert(n / 1000) + " Thousand" + ((n % 1000 != 0) ? " " : "") + convert(n % 1000);
        }

        if (n < 1000000000) {
            return convert(n / 1000000) + " Million" + ((n % 1000000 != 0) ? " " : "") + convert(n % 1000000);
        }

        return convert(n / 1000000000) + " Billion"  + ((n % 1000000000 != 0) ? " " : "") + convert(n % 1000000000);
    }
    
   /**
    * 
    * @param pObjectToDecode
    * @return String representation of Monetary Value...
    * @throws Exception
    * @Note pObjectToDecode - must be one of <br>Integer.class <br>Decimal.class <br>String.class<br> Double.class
    * <br>else Exception will be thrown....
    */
   public String convertToWords(Object pObjectToDecode) throws Exception{
     	
     	return handleDecimal(pObjectToDecode);
     	 
    }
   
   
 

}