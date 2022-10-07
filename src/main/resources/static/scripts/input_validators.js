function checkStateSelected(_state)
{
	// if the return value is not a two character state name
	if (_state.value.length == 1)
	{
		alert("Please select a state.");
		return false;
	}
	return true;
}

function checkEmail(theForm, thefield, theValue)
{
	if (theValue != "") {
		atIndex = theValue.indexOf('@', 0);
		at2Index = theValue.indexOf('@', atIndex+1);
		dotIndex = theValue.indexOf('.', atIndex + 2);
		spaceIndex = theValue.indexOf(' ', 0);
		commaIndex = theValue.indexOf(',', 0);
		semIndex = theValue.indexOf(';', 0);
		if (atIndex < 1 || dotIndex == -1 || spaceIndex != -1
			|| commaIndex >= 0 || semIndex >= 0 || at2Index >= 0
			|| theValue.charAt(theValue.length - 1) == '.') {
				return false;
		}
		
		// We know tld isn't empty because of above tests
		// This list of TLD's is accurate as of 3/1/07
		// See http://www.icann.org/registries/top-level-domains.htm
		tld = theValue.substring(theValue.lastIndexOf('.') + 1).toUpperCase()
		if ((tld.length != 2) &&
		    (   (tld != "AERO") && (tld != "ARPA") && (tld != "BIZ") && (tld != "CAT") &&
				(tld != "COM") && (tld != "COOP") && (tld != "EDU") && (tld != "GOV") &&
				(tld != "INFO") && (tld != "INT") && (tld != "JOBS") && (tld != "MIL") &&
				(tld != "MOBI") && (tld != "MUSEUM") && (tld != "NAME") && (tld != "NET") &&
				(tld != "ORG") && (tld != "PRO") && (tld != "TRAVEL")   )) {
			return false;
		}
	}
	return true;
}

function checkBoaEmail(theForm, thefield, theValue)
{	 
	
	var badEmails = new Array('none@bac.com', 'none@none.com', 'none@myBank.com', 'none@somebank.com', 'none@usi.com');	
       var new2="" + theValue;
    var newval=new2.toLowerCase()
    if(newval.indexOf("none@")==0){
    	return false;
    }
    if(newval.indexOf("@none.")!= -1){
    	return false;
    }
	if (theValue != "")
		{
			for (j in badEmails)
			{
			if (newval == badEmails[j]) 
				{
					return false;
				}			
			}
		}
	return checkEmail(theForm, thefield, theValue);
}

function checkMatchingPasswords(theForm, thefield, theValue)
{
	if (theValue != theForm.Password.value)
	{
		return false;
	}

	return true;
}

function checkMatchingEmails(theForm, thefield, theValue)
{
	// Some forms use "emailAddr" and some use "EmailAddress"
	// as the pc:input name.  This validation func handles both.
	var compareEmail;
	if (theForm.emailAddr) {
		compareEmail = theForm.emailAddr;
	} else if (theForm.EmailAddress) {
		compareEmail = theForm.EmailAddress;
	} else {
		compareEmail = null;
	}
	
	if (theValue != compareEmail.value) {
		return false;
	}
	return true;
}

// isSensitized - return true if the value is in the form of a sensitized data
// otherwise return false.  Sensitized data looks like "....3453".
function isSensitized(theValue)
{
	return ((theValue.substring(0, 4) == "...."));
}

// checkAllDigits - return true if theValue is all digits and nothing else, otherwise return false
function checkAllDigits(theValue)
{
	for (var i = 0; i < theValue.length; i++)
	{
		check_char = theValue.charAt(i);
		if (check_char < '0' || check_char > '9')
			return false;
	}
	return true;
}

// checkNumberRange -- check to see if a number is with a range
// This function was taken from CFUSION/scripts/cfform.js
function checkNumberRange(object_value, min_value, max_value)
{
	// check minimum
	if (min_value != null)
	{
		if (object_value < min_value)
			return false;
	}

	// check maximum
	if (max_value != null)
	{
		if (object_value > max_value)
			return false;
	}

	//All tests passed, so...
	return true;
}

// checkCreditCard - check to see if a credit card number is valid
// This function was taken from CFUSION/scripts/cfform.js and modified
// to handle "sensitized" numbers.
function checkCreditCard(theForm, thefield, object_value)
{
	var white_space = " -";
	var creditcard_string="";
	var check_char;

	if (object_value.length == 0)
		return true;

	// check for sensitized data - four dots followed by 4 characters eg "....3403"
	if (isSensitized(object_value))
		return true;

	// squish out the white space
	for (var i = 0; i < object_value.length; i++)
	{
		check_char = white_space.indexOf(object_value.charAt(i))
		if (check_char < 0)
			creditcard_string += object_value.substring(i, (i + 1));
	}

	// if all white space return error
	if (creditcard_string.length == 0)
		return false;

	// make sure number is a valid integer
	if (creditcard_string.charAt(0) == "+")
		return false;

	if (!checkAllDigits(creditcard_string))
		return false;

	// now check mod10

	var doubledigit = creditcard_string.length % 2 == 1 ? false : true;
	var checkdigit = 0;
	var tempdigit;

	for (var i = 0; i < creditcard_string.length; i++)
	{
		tempdigit = eval(creditcard_string.charAt(i))

		if (doubledigit)
		{
			tempdigit *= 2;
			checkdigit += (tempdigit % 10);

			if ((tempdigit / 10) >= 1.0)
			{
				checkdigit++;
			}

			doubledigit = false;
		}
		else
		{
			checkdigit += tempdigit;
			doubledigit = true;
		}
	}
	return (checkdigit % 10) == 0 ? true : false;

}

function checkCreditCardCID(theForm, thefield, object_value){
	if (object_value.length == 0)
		return true;

	//cid value must be either a 3 or 4 digit number
	return /^[0-9]{3,4}$/.test(object_value);
}

function checkRoutingNumber(theForm, thefield, object_value)
{
	var white_space = " -+.";
	var routingString="";
	var check_char;

	// check for sensitized data - four dots followed by 4 characters eg "....3403"
	if (isSensitized(object_value))
		return true;

	// squish out the white space
	for (var i = 0; i < object_value.length; i++)
	{
		check_char = white_space.indexOf(object_value.charAt(i))
		if (check_char < 0)
			routingString += object_value.substring(i, (i + 1));
	}

	// if not nine characters return error
	if (routingString.length != 9)
		return false;

	// make sure number is only digits
	return checkAllDigits(routingString);
}

// checkFractionalRoutingNumber - return true if theValue contains only digits and hyphens.
function checkFractionalRoutingNumber(object_value)
{
        var check_char;

	for (var i = 0; i < object_value.length; i++)
	{
		check_char = object_value.charAt(i);
		if ((check_char < '0' || check_char > '9') && check_char != '-' && check_char != ' ')
			return false;
	}
	return true;
}

// checkAccountName - return true if the first character is a digit, otherwise return false
function checkAccountName(theValue)
{ 
		var check_char = theValue.charAt(0);
		if (check_char == '0' || check_char == '1' || check_char == '2' || check_char == '3' || check_char == '4' || check_char == '5' || check_char == '6' || check_char == '7' || check_char == '8' || check_char == '9')
			return false;
	return true;
}

function checkAccountNumber(theForm, thefield, object_value)
{
	// check for sensitized data - four dots followed by 4 characters eg "....3403"
	if (isSensitized(object_value))
		return true;

	// make sure number is a valid integer
	return checkAllDigits(object_value);
}

// checkSSN - check to see if a Social Security number is valid
// This function was taken from CFUSION/scripts/cfform.js and modified
// to handle "sensitized" numbers.
function checkSSN(theForm, thefield, object_value)
{
	var white_space = " -+.";
	var ssc_string="";
	var check_char;

	if (object_value.length == 0)
		return true;

	// check for sensitized data - four dots followed by 4 characters eg "....3403"
	if (isSensitized(object_value))
		return true;

	// if SSN in xxx-xx-xxxx format
	if (object_value.length == 11)
	{
		// make sure white space is valid
		if (object_value.charAt(3) != "-" && object_value.charAt(3) != " ")
			return false;
	
		if (object_value.charAt(6) != "-" && object_value.charAt(6) != " ")
		return false;

		// squish out the white space
		for (var i = 0; i < object_value.length; i++)
		{
			check_char = white_space.indexOf(object_value.charAt(i))
			if (check_char < 0)
				ssc_string += object_value.substring(i, (i + 1));
		}
	
		// if all white space return error
		if (ssc_string.length != 9)
			return false;
	}
	// if SSN in xxxxxxxxx format
	else if (object_value.length == 9)
		ssc_string = object_value;
	// Does not support any other format
	else
		return false;


	// make sure number is a valid integer
	if (!checkAllDigits(ssc_string))
		return false;

	return true;
}

function checkLast6DigitsOfSSN(object_value)
{
	var white_space = " -+.";
	var ssc_string="";
	var check_char;

	if (object_value.length == 0)
		return true;

	// if SSN in xx-xxxx format
	if (object_value.length == 7)
	{
		if (object_value.charAt(2) != "-" && object_value.charAt(2) != " ")
			return false;

		// squish out the white space
		for (var i = 0; i < object_value.length; i++)
		{
			check_char = white_space.indexOf(object_value.charAt(i))
			if (check_char < 0)
				ssc_string += object_value.substring(i, (i + 1));
		}
	
		// if all white space return error
		if (ssc_string.length != 6)
			return false;
	}
	// if SSN in xxxxxx format
	else if (object_value.length == 6)
		ssc_string = object_value;
	// Does not support any other format
	else
		return false;

	// make sure number is a valid integer
	if (!checkAllDigits(ssc_string))
		return false;

	return true;
}

// checkPhone - check to see if a phone number is valid
// This function was taken from CFUSION/scripts/cfform.js and modified
// to be more generous.
function checkPhone(theForm, thefield, object_value)
{
	if (object_value.length == 0)
		return true;

	if (object_value.length == 10)
	{
		if (!checkAllDigits(object_value))
			return false;

		// check if first 3 characters represent a valid area code
		if (!checkNumberRange((eval(object_value.substring(0,3))), 100, 1000))
			return false;

		// check if  characters 4 - 6 represent a valid exchange
		if (!checkAllDigits((eval(object_value.substring(3,6))), 100, 1000))
			return false;

        
		return true;
	}
	else if (object_value.length != 12)
		return false;

	// now we know the number is 12 characters so we allow '-' ' ' '.' as separators

    // characters 4 and 8 must be "-"
    if (object_value.charAt(3) != "-" || object_value.charAt(7) != "-")
        return false;

	// check if first 3 characters represent a valid area code
	if (!checkAllDigits(object_value.substring(0,3)))
		return false;
	else if (!checkNumberRange((eval(object_value.substring(0,3))), 100, 1000))
		return false;

	// check if area code/exchange separator is either a'-' or ' ' or '.'
	if (object_value.charAt(3) != "-" && object_value.charAt(3) != " " && object_value.charAt(3) != ".")
		return false

	// check if  characters 5 - 7 represent a valid exchange
	if (!checkAllDigits(object_value.substring(4,7)))
		return false;
	else if (!checkAllDigits((eval(object_value.substring(4,7))), 100, 1000))
			return false;

	// check if exchange/number separator is either a'-' or ' ' or '.'
	if (object_value.charAt(7) != "-" && object_value.charAt(7) != " "  && object_value.charAt(7) != ".")
		return false;

	return (checkAllDigits(object_value.substring(8,12)));
}

// checkFedEIN - check to see if a federal EIN number is valid
function checkFedEIN(theForm, thefield, object_value)
{
	var white_space = " -";
	var ein_string="";
	var check_char;

	if (object_value.length == 0)
		return true;

	if (object_value.length != 10)
		return false;

	// make sure white space is valid
	if (object_value.charAt(2) != "-" && object_value.charAt(2) != " ")
		return false;

	// squish out the white space
	for (var i = 0; i < object_value.length; i++)
	{
		check_char = white_space.indexOf(object_value.charAt(i))
		if (check_char < 0)
			ein_string += object_value.substring(i, (i + 1));
	}

	// if all white space return error
	if (ein_string.length != 9)
		return false;

	// make sure number is a valid integer
	if (!checkAllDigits(ein_string))
		return false;

	return true;
}

// checkFedTIN - check to see if a federal TIN number is valid
function checkFedTIN(theForm, thefield, object_value)
{
	var white_space = " -+.";
	var tin_string="";
	var check_char;

	if (object_value.length != 11 && object_value.length != 10) {
    	return false;
    }

	// make sure white space is valid
	if ((object_value.charAt(3) != "-" && object_value.charAt(3) != " " ||
		object_value.charAt(6) != "-" && object_value.charAt(6) != " ") &&
		object_value.charAt(2) != "-" && object_value.charAt(2) != " ") {
    	return false;
    }
 
 	// squish out the white space
	for (var i = 0; i < object_value.length; i++)
	{
		check_char = white_space.indexOf(object_value.charAt(i))
		if (check_char < 0)
			tin_string += object_value.substring(i, (i + 1));
	}	

	if (tin_string.length != 9 || !checkAllDigits(tin_string)) {
		return false;
 	}
 
	return true;
}

// checkIAWPN - check to see if a IA Withhodling Permit Number is valid
function checkIAWPN(object_value)
{
	// empty string and 'applied for' are allowed:
	if (object_value.length == 0 || object_value.toLowerCase() == "applied for")
		return true;
	// has to be a 12 digit number but not the example number '123456789001'
	if (object_value.length != 12 || object_value == "123456789001" || !checkAllDigits(object_value)) 
		return false;

 	return true;
}

function checkTime(object_value)
{
	var white_space = " ";
	var ampm_string = "";
	var check_char;

	// Returns true if value is a number or is NULL
	if (object_value.length == 0)
		return true;

	// the colon must have an index of 1 or 2
	var colon = object_value.indexOf(":", 0);
	if (colon < 1 || colon > 2)
		return false;

	// get the hour and make sure it's between 1 and 12
	var hour = object_value.substring(0, colon);
	if (!checkAllDigits(hour) || parseInt(hour,10) < 1 || parseInt(hour,10) > 12)
		return false;
 
	// get the minutes and make sure it's between 0 and 59
	var minutes = object_value.substring(colon+1, colon+3);
	if (!checkAllDigits(minutes) || parseInt(minutes,10) < 0 || parseInt(minutes,10) > 59)
		return false;

	// get the rest and it has to be either AM or PM
	var rest = object_value.substring(colon+3, object_value.length);

	// squish out the white space, maximum of 2 white spaces
	for (var i = 0; i < rest.length; i++)
	{
		check_char = white_space.indexOf(rest.charAt(i))
		if (check_char < 0)
			ampm_string += rest.substring(i, (i + 1));
	}
	
	// if all white space return error
	if (ampm_string.length != 2)
		return false;

	// if not AM or PM, return error
	if (ampm_string.charAt(1) != "M" && ampm_string.charAt(1) != "m")
		return false;

	if (ampm_string.charAt(0) != "A" && ampm_string.charAt(0) != "a" && ampm_string.charAt(0) != "P" && ampm_string.charAt(0) != "p")
		return false;

	// if AM or PM not consecutive characters, return error
	if (object_value.indexOf(ampm_string) < 0)
		return false;

	return true;
}

function checkDate(object_value)
{
	// Returns true if value is a number or is NULL
	if (object_value.length == 0)
		return true;

	object_value = object_value.replace(/[-.]/g, "/");

	// the first dash must have an index of 1 or 2
	var firstDash = object_value.indexOf("/", 0);
	if (firstDash < 1 || firstDash > 2)
		return false;

	// get the month and make sure it's between 1 and 12
	var month = object_value.substring(0, firstDash);
	if (!checkAllDigits(month) || parseInt(month,10) < 1 || parseInt(month,10) > 12)
		return false;

	// the second dash must have an index 3-5
	var secondDash = object_value.indexOf("/", firstDash+1);
	if (secondDash < 3 || secondDash > 5)
		return false;

	// get the day and make sure it's between 1 and 31.
	// we won't check anything more complicated, such as 4/31 or 2/30
	var day = object_value.substring(firstDash+1, secondDash);
	if (!checkAllDigits(day) || parseInt(day,10) < 1 || parseInt(day,10) > 31)
		return false;

	// make sure the year is in either yy or yyyy format
	var year = object_value.substring(secondDash+1);
	if (year.length != 2 && year.length != 4)
		return false;

	// make sure the year is all digits
	if (!checkAllDigits(year))
		return false;

	return true;
}

// Note that this function does NOT do any validation.  That is up to you.
// Call checkDate() if you need it. Returns 1 if date1>date2, -1 if date1<date2
// or 0 if they are equal.
function compareDate(date1,date2)
{
	var firstDash1=date1.indexOf("/",0);
	var firstDash2=date2.indexOf("/",0);
	var secondDash1=date1.indexOf("/",firstDash1+1);
	var secondDash2=date2.indexOf("/",firstDash2+1);

	var month1 = eval(date1.substring(0,firstDash1));
	var day1 = eval(date1.substring(firstDash1+1,secondDash1));
	var month2 = eval(date2.substring(0,firstDash2));
	var day2 = eval(date2.substring(firstDash2+1,secondDash2));

	var year1 = date1.substring(secondDash1+1);
	var year2 = date2.substring(secondDash2+1);
	if( year1.length==2 && year2.length==4 ) year1 = year2.substring(0,2)+year1;
	if( year2.length==2 && year1.length==4 ) year2 = year1.substring(0,2)+year2;

	year1 = eval(year1);
	year2 = eval(year2);

	if( year1<year2 ) return -1;
	if( year1>year2 ) return 1;
	if( month1<month2 ) return -1;
	if( month1>month2 ) return 1;
	if( day1<day2 ) return -1;
	if( day1>day2 ) return 1;

	return 0;
}

function checkPositiveNumber(object_value)
{
	if (object_value.length == 0)
		return true;

	var minusSign = "-";
	if( object_value.indexOf(minusSign)>=0 ) {
		return false;
	}
	return checkNumber(object_value);
}

function checkPositiveInteger(object_value)
{
	if (object_value.length == 0)
		return true;

	var minusSign = "-";
	if( object_value.indexOf(minusSign)>=0 ) {
		return false;
	}
	return checkInteger(object_value);
}

function checkInteger(object_value)
{
	//Returns true if value is a number or is NULL
	//otherwise returns false

	if (object_value.length == 0)
		return true;

	//Returns true if value is an integer defined as
	//   having an optional leading + or -.
	//   otherwise containing only the characters 0-9.
	var decimal_format = ".";
	var check_char;

	//The first character can be + -  blank or a digit.
	check_char = object_value.indexOf(decimal_format)
	//Was it a decimal?
	if (check_char < 1)
		return checkNumber(object_value);
	else
		return false;
}

function checkPercentage(object_value)
{
	return checkNumber(object_value) && checkNumberRange(object_value, 0.0, 100.0);
}


function checkWholePositiveDollar (object_value)
{
		//Returns true if value is a number or is NULL
	//otherwise returns false

	while( object_value.charAt(0)==" " ) {
		object_value = object_value.substring(1);
	}
	if (object_value.length == 0)
		return true;

	//Returns true if value is a number defined as
	//   having an optional leading + or -.
	//   having at most 1 decimal point.
	//   otherwise containing only the characters 0-9.
	var start_format = "0123456789";
	var number_format = " .0123456789";
	var check_char;
	var decimal = false;
	var trailing_blank = false;
	var digits = false;

	//The first character can be a digit.
	check_char = start_format.indexOf(object_value.charAt(0))

	if (check_char < 1)
		return false;

	//Remaining characters can be only . or a digit, but only one decimal.
	for (var i = 1; i < object_value.length; i++)
	{
		check_char = number_format.indexOf(object_value.charAt(i))
					 if (check_char < 0)
			return false;
					 else if (check_char == 1)
					 {
						 if (decimal)		// Second decimal.
							 return false;
						 else
							 decimal = true;
					 }
					 else if (check_char == 0)
					 {
						 if (decimal || digits)
							 trailing_blank = true;
		// ignore leading blanks

					 }
					 else if (trailing_blank)
						 return false;
					 else if (decimal && check_char != 2) // don't allow any non 0's after the decimal
						 return false;
					 else
						 digits = true;
	}

	//All tests passed, so...
	return true

}

function checkNumber(object_value)
{
	//Returns true if value is a number or is NULL
	//otherwise returns false

	while( object_value.charAt(0)==" " ) {
		object_value = object_value.substring(1);
	}
	if (object_value.length == 0)
		return true;

	//Returns true if value is a number defined as
	//   having an optional leading + or -.
	//   having at most 1 decimal point.
	//   otherwise containing only the characters 0-9.
	var start_format = " .+-0123456789";
	var number_format = " .0123456789";
	var check_char;
	var decimal = false;
	var trailing_blank = false;
	var digits = false;

	//The first character can be + - .  blank or a digit.
	check_char = start_format.indexOf(object_value.charAt(0))

	// if there are only +, - or . char in the value object than return false
	if (object_value.length == 1 && (check_char == 1 || check_char == 2 || check_char == 3))
		return false;

	//Was it a decimal?
	if (check_char == 1)
		decimal = true;
	else if (check_char < 1)
		return false;

	//Remaining characters can be only . or a digit, but only one decimal.
	for (var i = 1; i < object_value.length; i++)
	{
		check_char = number_format.indexOf(object_value.charAt(i))
		if (check_char < 0)
			return false;
		else if (check_char == 1)
		{
			if (decimal)		// Second decimal.
				return false;
			else
				decimal = true;
		}
		else if (check_char == 0)
		{
			if (decimal || digits)
				trailing_blank = true;
        // ignore leading blanks

		}
	        else if (trailing_blank)
			return false;
		else
			digits = true;
	}

	//All tests passed, so...
	return true
}

// If provider login is optional.
function checkEmailIfLoginChecked(theForm, thefield, theValue)
{
	if(theForm.ClientLogin.checked)
		return checkEmail(theForm, thefield, theValue);

	return true;
}

function checkZip(object_value)
{
	if (object_value.length == 0)
		return true;

	if (object_value.length != 5 && object_value.length != 10)
		return false;

	// make sure first 5 digits are a valid integer
	if (object_value.charAt(0) == "-" || object_value.charAt(0) == "+")
		return false;

	if (!checkInteger(object_value.substring(0,5)))
		return false;

	if (object_value.length == 5)
		return true;

	// make sure

	// check if separator is either a'-' or ' '
	if (object_value.charAt(5) != "-" && object_value.charAt(5) != " ")
		return false;

	// check if last 4 digits are a valid integer
	if (object_value.charAt(6) == "-" || object_value.charAt(6) == "+")
		return false;

	return (checkInteger(object_value.substring(6,10)));
}

//check if field is empty.
function hasValue(object,value)
{
	value = trim(value);
	if (value.length == 0)
		return false;
	else
		return true;
}

// prevent double click
var form_submitted = false;
function oneClick()
{
	if (form_submitted)
		return false;

	form_submitted = true;
	return true;
}

// allow only one click from the page
// This is different from oneClick(), since that gets reset by pc:form - See taglib code.
var oneForm_submitted = false;
function oneClickOnly()
{
	if (oneForm_submitted)
		return false;

	oneForm_submitted = true;
		return true;
}

//Set the form submitted variable. Return the parameter.
function setFormSubmitted(val)
{
	form_submitted = val;
	return val;
}

function checkIrsPin(object_value)
{
	if (object_value.length == 0)
		return true;

	if (object_value.length != 10)
		return false;

	if (!checkInteger(object_value))
		return false;

	return true;
}

function checkDesigneePin(object_value)
{
	if (object_value.length != 5)
		return false;

	if (!checkInteger(object_value))
		return false;

	return true;
}

function confirmInteger(objValue,objMin,objMax)
{
	if( objValue.length == 0 )
		return true;

	var value;

	if( !checkInteger(objValue) ) {
		value=0;
	}

	if( objMin!=null && objMin!="null" && objMin>objValue )
		return false;

	if( objMax!=null && objMax!="null" && objMax<objValue )
		return false;

	return true;
}

function confirmNumber(objValue,objMin,objMax)
{
	if( objValue.length == 0 )
		return true;

	var value;

	if( !checkNumber(objValue) ) {
		value=0;
	}

	if( objMin!=null && objMin!="null" && objMin>objValue )
		return false;

	if( objMax!=null && objMax!="null" && objMax<objValue )
		return false;

	return true;
}

function confirmDate(objValue,objMin,objMax)
{
	if( objValue.length == 0 )
		return true;

	var value;

	if( !checkDate(objValue) ) {
		return true;
	}

	if( objMin!=null && objMin!="null" && compareDate(objValue,objMin)<0 )
		return false;

	if( objMax!=null && objMax!="null" && compareDate(objValue,objMax)>0 )
		return false;

	return true;
}

// Trims a string to get rid of leading or trailing spaces..
function trim(value) {
	var temp = value;
	var obj = /^(\s*)([\W\w]*)(\b\s*$)/;
	if (obj.test(temp)) { temp = temp.replace(obj, '$2'); }
	var obj = / +/g;
	temp = temp.replace(obj, " ");
	if (temp == " ") { temp = ""; }
	return temp;
}

function checkHours(input_object)
{
	if (input_object.value == "hours")
	{
		input_object.value = "";
	}
	// Shouldn't this be checkPositiveNumber?  Not sure why we allow negative hours
	return checkNumber(input_object.value);
}

function checkQuarterStartDate(object_value)
{
	// must be a date:
	if (!checkDate(object_value))
		return false;
	
	// get the first dash and the second dash:
	var firstDash = object_value.indexOf("/", 0); 		
	var secondDash = object_value.indexOf("/", firstDash+1);
	var MonthDay = object_value.substring(0, secondDash);	
	if (MonthDay=="1/1" || MonthDay=="01/01" || MonthDay=="01/1" || MonthDay=="1/01" ||
		MonthDay=="4/1" || MonthDay=="04/01" || MonthDay=="04/1" || MonthDay=="4/01" ||
		MonthDay=="7/1" || MonthDay=="07/01" || MonthDay=="07/1" || MonthDay=="7/01" ||
		MonthDay=="10/1"|| MonthDay=="10/01")
		return true;
	return false;
}

function checkAlphaNumeric(object_value)
{
	//Returns true if value is alphanumeric or is NULL
	//otherwise returns false

	object_value=trim(object_value);

	if (object_value.length == 0)
		return true;

	//Returns true if value is a number defined as
	//   having  containing only the characters a-z and 0-9.
	var name_format = "abcdefghijklmnopqrstuvwxyzABCEDFGHIJKLMNOPQRSTUVWXYZ0123456789";
	var check_char;

	//Remaining characters can be only . or a digit, but only one decimal.
	for (var i = 0; i < object_value.length; i++)
	{
		check_char = name_format.indexOf(object_value.charAt(i))
					 if (check_char < 0) return false;
	}

	//All tests passed, so...
	return true
}	
