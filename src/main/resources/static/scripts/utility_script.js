

errorPage = '<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">' +
			'<html><head><meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">' + 
	        '<title>Ogun State IPPMS</title>' +
			'<link href="css/newLogin_tms.css" rel="stylesheet" type="text/css"></head> ' +
			'<body><div class="register1">&nbsp;</div><form name="f" action="j_spring_security_check" method="POST" id="login">' +
			'<div class="placeRivBorder"> <h2 class="headerImage"></h2><div class="tableForm"><table width="100%"> <tr>' + 
			'<td width="23%"><label for="login_username">Username:</label></td>' +
			'<td width="77%"><input type="text" id="login_username" class="field required"  autocomplete="off"'+ 
			'title="Please provide your username" name="j_username" value="" /></td></tr></table>'+
			'</div><div class="tableForm"><table width="100%"><tr><td width="23%"><label for="login_password">Password:</label></td>' +
			'<td width="77%"><input type="password" name="j_password"  id="login_password" class="field required" title="Password is required" /></td>'+
			'</tr></table></div><div class="submit"><button type="submit"></button></div><p class="back">Please login with your RIV-TMS Account details</p>'+
			'<h4>&copy; 2012 GNL Systems Ltd</h4></div></form></body></html>';


/**
 * This function displays a success message after creation/manipulation of data...
 */
$(document).ready(function(){
	/*
     * Slide in the .success div
     */
    jQuery(".success").hide();
    jQuery(".success").slideDown('slow');

    /*
     * Register a timeout for the success div
     */
     setTimeout(function(){
         jQuery(".success").slideUp('slow', function(){
             jQuery(this).remove();
         });
         
     }, 7000);
});


/**
 * 
 * @param pData
 * @returns
 */
function clearTopErrorMessage(){
	$("div#topOfPageBoxedErrorMessage ul").remove();
	$("div#topOfPageBoxedErrorMessage").hide();
}


/* 
 * 
 * This script is a utility for performing various tasks.
 */
// check is number
function regIsNumber(pData)
{
    var reg = new RegExp('^[-]?[0-9]+[\.]?[0-9]+$');
    return reg.test(pData);
}

//another method for determining if value is a number
function isNumber(n) {
  return !isNaN(parseFloat(n)) && isFinite(n);
}


var dates = {
    convert:function(d) {
        // Converts the date in d to a date-object. The input can be:
        //   a date object: returned without modification
        //  an array      : Interpreted as [year,month,day]. NOTE: month is 0-11.
        //   a number     : Interpreted as number of milliseconds
        //                  since 1 Jan 1970 (a timestamp) 
        //   a string     : Any format supported by the javascript engine, like
        //                  "YYYY/MM/DD", "MM/DD/YYYY", "Jan 31 2009" etc.
        //  an object     : Interpreted as an object with year, month and date
        //                  attributes.  **NOTE** month is 0-11.
        return (
            d.constructor === Date ? d :
            d.constructor === Array ? new Date(d[0],d[1],d[2]) :
            d.constructor === Number ? new Date(d) :
            d.constructor === String ? new Date(d) :
            typeof d === "object" ? new Date(d.year,d.month,d.date) :
            NaN
            );
    },
    compare:function(a,b) {
        // Compare two dates (could be of any type supported by the convert
        // function above) and returns:
        //  -1 : if a < b
        //   0 : if a = b
        //   1 : if a > b
        // NaN : if a or b is an illegal date
        // NOTE: The code inside isFinite does an assignment (=).
        return (
            isFinite(a=this.convert(a).valueOf()) &&
            isFinite(b=this.convert(b).valueOf()) ?
            (a>b)-(a<b) :
            NaN
            );
    },
    inRange:function(d,start,end) {
        // Checks if date in d is between dates in start and end.
        // Returns a boolean or NaN:
        //    true  : if d is between start and end (inclusive)
        //    false : if d is before start or after end
        //    NaN   : if one or more of the dates is illegal.
        // NOTE: The code inside isFinite does an assignment (=).
        return (
            isFinite(d=this.convert(d).valueOf()) &&
            isFinite(start=this.convert(start).valueOf()) &&
            isFinite(end=this.convert(end).valueOf()) ?
            start <= d && d <= end :
            NaN
            );
    }
};


// Check if string is non-blank
var isNonblank_re  = /\S/;
function isNonblank (s) {
    return String (s).search (isNonblank_re) != -1;
}


//check if character(s) is numeric
//this is for key presses..
function checkNumeric(pElem, evt)
{
    
    var charCode = (evt.which) ? evt.which : event.keyCode;

    if (charCode > 31 && (charCode < 48 || charCode > 57))
        pElem.value = '';
    
    
    
   // Get ASCII value of key that user pressed
//   var key = window.event.keyCode;
//
//   // Was key that was pressed a numeric character (0-9)?
//   if ( key > 47 && key < 58 )
//      return; // if so, do nothing
//   else
//      window.event.returnValue = null; // otherwise, 
	                               // discard character
}

//this is for cases like pasting onto a field directly....
function checkNumericForFocus(pElem){
    if(pElem != '' && isNaN(pElem.value)){
        pElem.value = '';
    }
}